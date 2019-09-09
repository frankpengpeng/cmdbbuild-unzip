/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.shark;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Properties;
import javax.xml.namespace.QName;
import org.apache.axis.client.Stub;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmReflectionUtils.wrapProxy;
import org.cmdbuild.utils.lang.ProxyWrapper;
import org.enhydra.shark.api.client.wfmc.wapi.WAPI;
import org.enhydra.shark.api.client.wfmc.wapi.WMConnectInfo;
import org.enhydra.shark.api.client.wfservice.SharkInterface;
import org.enhydra.shark.client.utilities.SharkInterfaceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.xml.rpc.Service;
import javax.xml.rpc.encoding.TypeMapping;
import org.apache.axis.encoding.ser.ArrayDeserializerFactory;
import org.apache.axis.encoding.ser.ArraySerializerFactory;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;
import static org.cmdbuild.cache.CacheConfig.SYSTEM_OBJECTS;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.services.PostStartup;
import org.cmdbuild.workflow.SharkRemoteServiceConfiguration;
import static org.cmdbuild.workflow.WorkflowCommonConst.SHARK;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.model.WorkflowException;

@Component
public class SharkWebserviceClientImpl implements SharkWebserviceClient {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final WorkflowConfiguration wfConfig;
	private final SharkRemoteServiceConfiguration config;
	private final Holder<WAPI> wapiCache;
	private final Holder<SharkInterface> sharkInterfaceCache;

	public SharkWebserviceClientImpl(WorkflowConfiguration wfConfig, SharkRemoteServiceConfiguration config, CacheService cacheService) {
		this.wfConfig = checkNotNull(wfConfig);
		this.config = checkNotNull(config);
		wapiCache = cacheService.newHolder("shark_wapi", SYSTEM_OBJECTS);
		sharkInterfaceCache = cacheService.newHolder("shark_interface", SYSTEM_OBJECTS);
	}

	@PostStartup
	public void startSharkWsClient() {
		reconfigureSharkSafe();
	}

	@ConfigListener(SharkRemoteServiceConfiguration.class)
	public void handleConfigUpdateEvent() {
		reconfigureSharkSafe();
	}

	@Override
	public WAPI getWapi() {
		checkEnabled();
		return wapiCache.get(this::createWAPI);
	}

	@Override
	public SharkInterface getSharkInterface() {
		checkEnabled();
		return sharkInterfaceCache.get(this::createSharkInterface);
	}

	@Override
	public WMConnectInfo getConnectionInfo() {
		return new WMConnectInfo(config.getSharkUsername(), config.getSharkPassword(), "", "");
	}

	private boolean isEnabled() {
		return wfConfig.isWorkflowProviderEnabled(SHARK);
	}

	private void checkEnabled() {
		checkArgument(isEnabled(), "shark is not enabled");
	}

	private void reconfigureSharkSafe() {
		try {
			reconfigureShark();
		} catch (Exception ex) {
			logger.error(marker(), "error reconfiguring shark", ex);
		}
	}

	private void reconfigureShark() {
		try {
			if (isEnabled()) {
				Properties props = config.getClientProperties();
				logger.info("reconfigure shark with properties = {}", map(props));
				SharkInterfaceWrapper.setProperties(props, true);
			}
			SharkInterfaceWrapper.killShark();
		} catch (Exception ex) {
			throw new WorkflowException(ex);
		}
	}

	private SharkInterface createSharkInterface() {
		try {
			checkEnabled();
			SharkInterface shark = SharkInterfaceWrapper.getShark();
			shark = wrapProxy(SharkInterface.class, shark, new ProxyWrapper() {
				@Override
				public void beforeMethodInvocation(Method method, Object[] params) {
					logger.debug("invoke SHARK client method = {} with params = {}", method.getName(), params);
				}

			});
			return shark;
		} catch (Exception ex) {
			throw new WorkflowException(ex, "error creating shark interface");
		}
	}

	private WAPI createWAPI() {
		try {
			checkEnabled();
			WAPI wapi = getSharkInterface().getWAPIConnection();
			configureWAPI(wapi);
			wapi = wrapProxy(WAPI.class, wapi, new ProxyWrapper() {
				@Override
				public void beforeMethodInvocation(Method method, Object[] params) {
					logger.debug("invoke WAPI client method = {} with params = {}", method.getName(), params);
				}

			});
			return wapi;
		} catch (Exception ex) {
			throw new WorkflowException(ex, "error creating shark WAPI");
		}
	}

	/**
	 * It can be overridden to add something to the WAPI interface ({
	 *
	 * @param wapi
	 * @see RemoteSharkClientImpl}).
	 */
	private void configureWAPI(WAPI wapi) {
		if (wapi instanceof Stub) {
			Stub axisClientStub = (Stub) wapi;
			registerCustomTypes(axisClientStub);
		}
	}

	private void registerCustomTypes(Stub axisClientStub) {
		Service rpcService = axisClientStub._getService();
		TypeMapping tm = rpcService.getTypeMappingRegistry().getTypeMapping(org.apache.axis.Constants.URI_SOAP11_ENC);

		// TODO register if needed, and take care of concurrency
		registerType(tm, org.cmdbuild.workflow.type.LookupType.class);
		registerType(tm, org.cmdbuild.workflow.type.ReferenceType.class);
	}

	private static final String CMDBUILD_TYPE_NS = "http://type.workflow.cmdbuild.org";

	/**
	 * Only God knows why it is different.
	 */
	private static final String CMDBUILD_EJB_NS = "http://ebj.workflow.cmdbuild.org";

	private static final String ARRAY_NAME_PREFIX = "ArrayOf_tns1_";

	private void registerType(TypeMapping tm, Class<?> javaType) {
		QName typeQname = new QName(CMDBUILD_TYPE_NS, javaType.getSimpleName());
		tm.register(javaType, typeQname, new BeanSerializerFactory(javaType, typeQname), new BeanDeserializerFactory(javaType, typeQname));

		Class<?> javaArrayType = Array.newInstance(javaType, 0).getClass();
		QName arrayQname = new QName(CMDBUILD_EJB_NS, ARRAY_NAME_PREFIX + javaType.getSimpleName());
		tm.register(javaArrayType, arrayQname, new ArraySerializerFactory(typeQname, null), // why
				// null?!
				new ArrayDeserializerFactory());
	}

}

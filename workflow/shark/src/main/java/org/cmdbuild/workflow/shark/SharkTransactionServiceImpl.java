/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.shark;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.workflow.model.WorkflowException;
import org.enhydra.shark.api.client.wfmc.wapi.WMConnectInfo;
import org.enhydra.shark.api.client.wfmc.wapi.WMSessionHandle;
import org.enhydra.shark.client.utilities.SharkInterfaceWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SharkTransactionServiceImpl implements SharkTransactionService {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final SharkWebserviceClient webserviceClient;
	private final SharkEventService eventService;

	public SharkTransactionServiceImpl(SharkWebserviceClient webserviceClient, SharkEventService eventService) {
		this.webserviceClient = checkNotNull(webserviceClient);
		this.eventService = checkNotNull(eventService);
	}

	@Override
	public SharkTransactionContext beginTransaction() {
		logger.trace("beginTransaction BEGIN");
		try {
			SharkInterfaceWrapper.getUserTransaction().begin();
			WMConnectInfo wmci = webserviceClient.getConnectionInfo();
			WMSessionHandle handle = webserviceClient.getWapi().connect(wmci);
			SharkEventCollector wokflowEventCollector = new SharkEventCollector(handle.getId());
			eventService.getEventBus().register(wokflowEventCollector);
			SharkTransactionContext context = new SharkTransactionContext(handle, wokflowEventCollector);
			logger.trace("beginTransaction END");
			return context;
		} catch (Exception e) {
			throw new WorkflowException(e, "error creating shark transaction");
		}
	}

	@Override
	public void commitTransaction(SharkTransactionContext context) {
		logger.trace("commitTransaction BEGIN");
		try {
			SharkInterfaceWrapper.getUserTransaction().commit();
		} catch (Exception e) {
			throw new WorkflowException(e);
		} finally {
			closeTransaction(context);
			logger.trace("commitTransaction END");
		}
	}

	@Override
	public void rollbackTransaction(SharkTransactionContext context) {
		logger.trace("rollbackTransaction BEGIN");
		try {
			SharkInterfaceWrapper.getUserTransaction().rollback();
		} catch (Exception e) {
			logger.warn("error during shark transaction rollback", e);
		} finally {
			closeTransaction(context);
			logger.trace("rollbackTransaction END");
		}
	}

	private void closeTransaction(SharkTransactionContext context) {
		logger.trace("closeTransaction BEGIN");
		eventService.getEventBus().unregister(context.getEventCollector());
		try {
			webserviceClient.getWapi().disconnect(context.getHandle());
		} catch (Exception e) {
			logger.warn("error during shark transaction disconnection", e);
		}
		logger.trace("closeTransaction END");
	}

}

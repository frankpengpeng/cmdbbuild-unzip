/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.shark.jndi;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.StringRefAddr;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import org.enhydra.jndi.DataSourceFactory;

/**
 *
 * @author davide
 */
public class ExtendedDataSourceFactory extends DataSourceFactory {

	@Override
	public Object getObjectInstance(Object o, Name name, Context cntxt, Hashtable hshtbl) throws Exception {
		// BEGIN try to decrypt values inside resourceRef (o) via cryptUtils
		{ 
			if (o instanceof org.apache.naming.ResourceRef) {
				org.apache.naming.ResourceRef resourceRef = (org.apache.naming.ResourceRef) o;
				List<RefAddr> refAddrList = Collections.list(resourceRef.getAll());
				for (int i = 0; i < refAddrList.size(); i++) {
					RefAddr refAddr = refAddrList.get(i);
					if (refAddr instanceof StringRefAddr) {
						StringRefAddr stringRefAddr = (StringRefAddr) refAddr;
						String value = (String) stringRefAddr.getContent();
						if (Cm3EasyCryptoUtils.isEncrypted(value)) {
							StringRefAddr decryptedRefAddr = new StringRefAddr(stringRefAddr.getType(), Cm3EasyCryptoUtils.decryptValue(value));
							resourceRef.remove(i);
							resourceRef.add(i, decryptedRefAddr);
						}
					}
				}
			}
		}
		// END try to decrypt values inside resourceRef (o) via cryptUtils

		return super.getObjectInstance(o, name, cntxt, hshtbl);
	}

}

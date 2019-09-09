/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import java.util.List;
import org.cmdbuild.client.rest.core.RestServiceClient;

public interface LookupApi extends RestServiceClient {

	List<LookupValue> getValues(String lookupTypeId);

	interface LookupValue {
		
		String getId();

		String getType();

		String getCode();

		String getDescription();
	}
}

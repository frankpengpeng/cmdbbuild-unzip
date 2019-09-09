/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import java.io.InputStream;
import org.cmdbuild.client.rest.model.CustomPageInfo;

public interface CustomPageApi {

	CustomPageApiResponse upload(InputStream data);

	interface CustomPageApiResponse {

		CustomPageInfo getCustomPageInfo();

		CustomPageApi then();
	}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import org.cmdbuild.data.filter.*;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EcqlFilterImpl implements EcqlFilter {

	private final String ecql, jsContext;

	public EcqlFilterImpl(String ecql, String jsContext) {
		this.ecql = checkNotBlank(ecql);
		this.jsContext = checkNotBlank(jsContext);
	}

	@Override
	public String getEcqlId() {
		return ecql;
	}

	@Override
	public String getJsContext() {
		return jsContext;
	}

}

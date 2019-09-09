/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data2.api;

import org.cmdbuild.data2.api.DataAccessService.AttrQueryParams;

public class SimpleAttrQueryParams implements AttrQueryParams {

	private final Integer limit, offset;

	public SimpleAttrQueryParams(Integer limit, Integer offset) {
		this.limit = limit;
		this.offset = offset;
	}

	@Override
	public Integer limit() {
		return limit;
	}

	@Override
	public Integer offset() {
		return offset;
	}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.inner;

import org.cmdbuild.requestcontext.RequestContextHolder;
import org.cmdbuild.requestcontext.RequestContextService;
import org.springframework.stereotype.Component;

@Component
public class CurrentSessionHolderImpl implements CurrentSessionHolder {

	private final RequestContextHolder<String> inner;

	public CurrentSessionHolderImpl(RequestContextService requestContextService) {
		inner = requestContextService.createRequestContextHolder();
	}

	@Override
	public String getOrNull() {
		return inner.getOrNull();
	}

	@Override
	public void set(String value) {
		inner.set(value);
	}

}

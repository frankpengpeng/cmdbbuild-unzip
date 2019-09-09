/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import static com.google.common.base.Strings.emptyToNull;
import javax.annotation.Nullable;
import org.cmdbuild.requestcontext.RequestContextHolder;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class RequestLanguageHolderImpl implements RequestLanguageHolder {

	private final RequestContextHolder<String> holder;

	public RequestLanguageHolderImpl(RequestContextService requestContextService) {
		holder = requestContextService.createRequestContextHolder();
	}

	@Override
	public boolean hasRequestLanguage() {
		return holder.hasContent();
	}

	@Override
	public void setRequestLanguage(String lang) {
		holder.set(checkNotBlank(lang));
	}

	@Override
	public String getRequestLanguage() {
		return checkNotBlank(holder.get());
	}

	@Override
	@Nullable
	public String getRequestLanguageOrNull() {
		return emptyToNull(holder.getOrNull());
	}
}

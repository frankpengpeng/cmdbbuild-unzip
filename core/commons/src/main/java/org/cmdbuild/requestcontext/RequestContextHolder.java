/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.requestcontext;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;

public interface RequestContextHolder<T> {

	default T get() {
		return checkNotNull(getOrNull(), "missing value in request context holder");
	}

	@Nullable
	T getOrNull();

	void set(T value);

	default boolean hasContent() {
		return getOrNull() != null;
	}

}

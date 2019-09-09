/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.requestcontext;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import javax.annotation.Nullable;

public interface RequestContext {

	default <T> T get(String key, Supplier<T> loader) {
		checkNotNull(loader);
		T value = getOrNull(key);
		if (value == null) {
			value = loader.get();
			set(key, value);
		}
		return value;
	}

	default <T> T get(String key) {
		return checkNotNull(getOrNull(key), "request context value not found for key = %s", key);
	}

	@Nullable
	<T> T getOrNull(String key);

	void set(String key, Object value);

	default boolean has(String key) {
		return getOrNull(key) != null;
	}

	String getId();
}

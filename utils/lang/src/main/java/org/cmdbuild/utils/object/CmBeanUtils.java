/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.object;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.onlyElement;
import java.lang.reflect.Method;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CmBeanUtils {

	public static @Nullable
	Object getBeanPropertyValue(Object bean, String key) {
		checkNotNull(bean);
		checkNotBlank(key);
		try {
			Method method = list(bean.getClass().getMethods()).stream().filter((m) -> m.getParameterCount() == 0).filter((m) -> m.getName().toLowerCase().matches("^(get|is)" + Pattern.quote(key.toLowerCase()))).collect(onlyElement());
			return method.invoke(bean);
		} catch (Exception ex) {
			throw runtime(ex, "error extracting bean value for key = %s from bean = %s", key, bean);
		}
	}

	public static @Nullable
	<T> T getBeanPropertyValue(Object bean, String key, Class<T> classe) {
		return convert(getBeanPropertyValue(bean, key), classe);
	}

}

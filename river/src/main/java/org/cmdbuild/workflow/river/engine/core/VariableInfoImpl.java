/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.core;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.Serializable;
import java.util.Optional;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;

public class VariableInfoImpl<V extends Serializable> implements RiverVariableInfo<V> {

	private final Optional<V> defaultValue;
	private final String key;
	private final Class<V> javaType;

	public VariableInfoImpl(String key, Class<V> javaType, Optional<V> defaultValue) {
		this.key = checkNotBlank(key);
		this.javaType = checkNotNull(javaType);
		this.defaultValue = defaultValue.map(v -> convert(v, javaType));
	}

	public static <V extends Serializable> RiverVariableInfo<V> variableInfo(String key, Class<V> javaType) {
		return variableInfo(key, javaType, null);
	}

	public static <V extends Serializable> RiverVariableInfo<V> variableInfo(String key, Class<V> javaType, @Nullable V defaultValue) {
		return new VariableInfoImpl<>(key, javaType, Optional.ofNullable(defaultValue));
	}

	@Override
	public Optional<V> getDefaultValue() {
		return defaultValue;
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public boolean isBasicType() {
		return true;
	}

	@Override
	public Class<V> getJavaType() {
		return javaType;
	}

	@Override
	public String toString() {
		return "VariableInfoImpl{" + "defaultValue=" + defaultValue + ", key=" + key + ", javaType=" + javaType + '}';
	}

}

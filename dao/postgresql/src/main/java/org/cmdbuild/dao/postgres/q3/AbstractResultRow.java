/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.function.StoredFunctionOutputParameter;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;

public abstract class AbstractResultRow implements ResultRow {

	@Override
	@Nullable
	public <T> T get(StoredFunctionOutputParameter outputParameter) {
		Map<String, Object> map = asMap();
		checkArgument(map.containsKey(outputParameter.getName()), "output value not found for key = %s", outputParameter.getName());
		return (T) rawToSystem(outputParameter.getType(), map.get(outputParameter.getName()));
	}
}

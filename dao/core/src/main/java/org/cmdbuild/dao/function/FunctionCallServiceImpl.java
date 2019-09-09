/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.function;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.ResultRow;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;

@Component
public class FunctionCallServiceImpl implements FunctionCallService {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final DaoService dao;

	public FunctionCallServiceImpl(DaoService dao) {
		this.dao = checkNotNull(dao);
	}

	@Override
	public StoredFunction getFunction(String functionId) {
		return dao.getFunctionByName(functionId);
	}

	@Override
	public Map<String, Object> callFunction(String functionId, Map<String, Object> params) {
		return callFunction(getFunction(functionId), params);
	}

	@Override
	public Map<String, Object> callFunction(StoredFunction function, Map<String, Object> params) {
		logger.debug("call function = {} with params = {}", function, params);

		List<Object> input = function.getInputParameters().stream().map((p) -> rawToSystem(p.getType(), params.get(p.getName()))).collect(toList());

		List<ResultRow> result = dao.selectFunction(function, input).run();

		Map<String, Object> res;

		if (result.isEmpty()) {
			res = emptyMap();
		} else {
			Map<String, Object> valueSet = getOnlyElement(result).asMap();
			res = function.getOutputParameters().stream().collect(toMap(StoredFunctionOutputParameter::getName, (p) -> valueSet.get(p.getName())));
//			{
//				Object rawValue = valueSet.get(p.getName());
//				return rawToSystem(p.getType(), rawValue);
////				Object rawValue = valueSet.get(p.getName());
////				return functionOutputValueToServiceOutputValue(p.getType(), rawValue);
//			}));
		}

		if (logger.isTraceEnabled()) {
			logger.trace("parsed function output = \n\n{}\n", mapToLoggableString(res));
		}

		return res;
	}

//	private static Object functionOutputValueToServiceOutputValue(CardAttributeType<?> type, Object value) {
//		return value == null ? "" : new WsAttributeValueConvertingVisitor(type, value).convertValue();
//	}
}

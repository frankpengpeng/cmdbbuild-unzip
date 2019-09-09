/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.function;

import java.util.Map;

public interface FunctionCallService {

	StoredFunction getFunction(String functionId);

	Map<String, Object> callFunction(String functionId, Map<String, Object> functionParams);

	Map<String, Object> callFunction(StoredFunction function, Map<String, Object> functionParams);

}

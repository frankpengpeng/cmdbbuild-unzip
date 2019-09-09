/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sql;

public interface SqlScriptFunctionToken {

	String getUnparsedTextBeforeFunctionToken();

	String getFunctionName();

	String getFunctionSignature();

	String getFunctionDefinition();

}

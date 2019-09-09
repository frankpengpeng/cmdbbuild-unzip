/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli;

public interface CliCommandRunner {

	String getName();

	String getDescription();

	void exec(String[] args) throws Exception;
}

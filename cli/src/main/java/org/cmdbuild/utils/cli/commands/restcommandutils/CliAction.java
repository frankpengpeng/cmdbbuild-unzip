/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands.restcommandutils;

import java.util.List;

public interface CliAction {

	void execute(List<String> params);

	String getHelpAliases();

	String getHelpParameters();

	String getName();

}

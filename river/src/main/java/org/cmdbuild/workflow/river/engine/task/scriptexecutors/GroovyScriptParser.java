/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.task.scriptexecutors;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.StringReader;
import static java.lang.String.format;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class GroovyScriptParser {

	private final String scriptContent;
	private final Iterable<String> paramNames;

	private List<String> contentLines, importLines;

	public GroovyScriptParser(String scriptContent, Iterable<String> paramNames) {
		this.scriptContent = checkNotNull(scriptContent);
		this.paramNames = checkNotNull(paramNames);
	}

	public String buildGroovyClassScript() throws Exception {
		String groovyClassName = createClassname(scriptContent);

		contentLines = IOUtils.readLines(new StringReader(scriptContent));

		separateImportLines();

		StringBuilder groovyScript = new StringBuilder();

		importLines.forEach((importLine) -> {
			groovyScript.append(importLine).append("\n");
		});

		groovyScript.append(format("class %s implements %s {\n\n\tpublic void execute(Map<String, Object> dataIn,Map<String, Object> dataOut){\n\n", groovyClassName, GroovyScriptExecutor.MyGroovyScript.class.getCanonicalName()));

		paramNames.forEach((param) -> {
			groovyScript.append(format("def %s = dataIn.get(\"%s\");\n", param, param));
		});

		groovyScript.append("\n");

		contentLines.forEach((contentLine) -> {
			groovyScript.append(contentLine).append("\n");
		});

		groovyScript.append("\n\n");

		paramNames.forEach((param) -> {
			groovyScript.append(format("dataOut.put(\"%s\",%s);\n", param, param));
		});

		groovyScript.append("\n\t}\n\n}\n");

		return groovyScript.toString();
	}

	private static String createClassname(String scriptContent) {
		return StringUtils.capitalize(hash(scriptContent).toLowerCase().replaceAll("[0-9]+", ""));
	}

	private void separateImportLines() {
		List<String> allScriptContentLines = contentLines;

		importLines = list();
		contentLines = list();

		allScriptContentLines.forEach((line) -> {
			if (line.matches("^\\s*import\\s+.*")) {
				importLines.add(line);
			} else {
				contentLines.add(line);
			}
		});

	}

}

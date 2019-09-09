/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.framework;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import static java.lang.String.format;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import static org.cmdbuild.test.framework.TestContextUtils.createTestDatabase;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.io.CmIoUtils.readLines;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.sql.SqlScriptFunctionToken;
import static org.cmdbuild.utils.sql.SqlScriptUtils.parseSqlFunctionTokensFromScript;
import static org.junit.Assert.fail;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.EMPTY_DUMP;

public class SqlTestRunner extends Runner {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Class testClass;
	private final Multimap<String, TestElement> testsMap;

	private DatabaseCreator databaseCreator;
	private Connection connection;

	public SqlTestRunner(Class testClass) {
		Reflections reflections = new Reflections(testClass.getPackage().getName(), new ResourcesScanner());
		this.testClass = testClass;
		Multimap<String, TestElement> map = LinkedHashMultimap.create();
		reflections.getResources(Pattern.compile(".*[.]sql")).forEach((rawFileName) -> {
			List<SqlScriptFunctionToken> functionTokenList = parseSqlFunctionTokensFromScript(readToString(getClass().getResourceAsStream("/" + rawFileName)));
			String fileName = FilenameUtils.getBaseName(rawFileName);
			List<TestElement> valueSet = functionTokenList.stream().map((token) -> new TestElement(fileName, token)).collect(toImmutableList());
			map.putAll(fileName, valueSet);
		});
		testsMap = ImmutableListMultimap.copyOf(map);
	}

	@Override
	public Description getDescription() {
		Description description = Description.createSuiteDescription(testClass);
		testsMap.values().forEach((testElement) -> description.addChild(Description.createTestDescription(testClass, testElement.getName())));
		return description;
	}

	@Override
	public void run(RunNotifier runNotifier) {
		setupTestRun();
		try {
			Description description = getDescription();
			runNotifier.fireTestRunStarted(description);
			testsMap.values().stream().filter(t -> !(t.isSetup() || t.isTeardown())).forEach((t) -> runTest(runNotifier, t));
			runNotifier.fireTestRunFinished(new Result());
		} finally {
			teardownTestRun();
		}
	}

	private void runTest(RunNotifier runNotifier, TestElement test) {
		runNotifier.fireTestStarted(test.getDescription());
		try {
			setupTest(test);
			try {
				logger.info("test {} BEGIN", test.getName());
				try (Statement statement = connection.createStatement()) {
					statement.execute(format("SELECT %s();", test.getName()));
				}
				if (test.hasExpected()) {
					fireFailure(runNotifier, test, new AssertionError(format("expected error = '%s'", test.getExpected())));
				} else {
					fireSuccess(runNotifier, test);
				}
			} finally {
				teardownTest(test);
			}
		} catch (Exception ex) {
			if (test.hasExpected() && Pattern.compile(test.getExpected()).matcher(ex.toString()).find()) {
				fireSuccess(runNotifier, test);
			} else {
				fireFailure(runNotifier, test, ex);
			}
		}
	}

	private void fireSuccess(RunNotifier runNotifier, TestElement test) {
		logger.info("test {} COMPLETE", test.getName());
		runNotifier.fireTestFinished(test.getDescription());
	}

	private void fireFailure(RunNotifier runNotifier, TestElement test, Throwable ex) {
		logger.warn("test {} ERROR", test.getName());
		runNotifier.fireTestFailure(new Failure(test.getDescription(), ex));
	}

	private void setupTest(TestElement test) throws SQLException {
		prepareTuid();
		logger.debug("prepare tuid function");
		try (Statement statement = connection.createStatement()) {
			statement.execute(format("CREATE OR REPLACE FUNCTION tuid() RETURNS VARCHAR AS $$ SELECT '%s'::varchar; $$ LANGUAGE SQL IMMUTABLE;", tuid()));
		}
		try (Statement statement = connection.createStatement()) {
			statement.execute("SELECT _cm3_system_login();");
		}
		testsMap.get(test.getFileName()).stream().filter(TestElement::isSetup).forEach(this::executeSupportFunction);
		try (Statement statement = connection.createStatement()) {
			statement.execute(test.getDefinition());
		}
	}

	private void teardownTest(TestElement test) {
		try {
			try (Statement statement = connection.createStatement()) {
				statement.execute(format("DROP FUNCTION IF EXISTS %s();", test.getName()));
			}
			testsMap.get(test.getFileName()).stream().filter(TestElement::isTeardown).forEach(this::executeSupportFunction);
		} catch (Exception ex) {
			logger.warn("error running test teardown for test = {}", test, ex);
		}
	}

	private void executeSupportFunction(TestElement supportElement) {
		checkArgument(supportElement.isSetup() || supportElement.isTeardown());
		logger.debug("execute test support function = {}", supportElement);
		try (Statement statement = connection.createStatement()) {
			statement.execute(format("%s\nSELECT %s();\nDROP FUNCTION IF EXISTS %s()", supportElement.getDefinition(), supportElement.getName(), supportElement.getName()));
		} catch (SQLException ex) {
			throw runtime(ex);
		}
	}

	private void setupTestRun() {
		databaseCreator = createTestDatabase((c) -> c.withSource(EMPTY_DUMP));
		try {
			connection = databaseCreator.getCmdbuildDataSource().getConnection();
		} catch (SQLException ex) {
			throw runtime(ex);
		}
	}

	private void teardownTestRun() {
		if (connection != null) {
			try {
				connection.close();
			} catch (Exception ex) {
				logger.warn("error closing connection", ex);
			}
			connection = null;
		}
		if (databaseCreator != null && !databaseCreator.useExistingDatabase()) {
			try {
				databaseCreator.dropDatabase();
			} catch (Exception ex) {
				logger.warn("error dropping database", ex);
			}
			databaseCreator = null;
		}
	}

	public static Map<String, String> parseElementMetadata(String text) {
		return readLines(text).stream().filter(l -> l.matches(" *--+ *[^ :]+ *: *[^ ].*")).map(l -> {
			Matcher matcher = Pattern.compile(" *--+ *([^ :]+) *: *(.+?) *").matcher(l);
			checkArgument(matcher.matches());
			return Pair.of(checkNotBlank(matcher.group(1)), checkNotBlank(matcher.group(2)));
		}).collect(toMap(Pair::getKey, Pair::getValue)).immutable();
	}

	private class TestElement {

		private final String name, fileName;
		private final String definition;
		private final Description description;
		private final Map<String, String> metadata;

		public TestElement(String fileName, SqlScriptFunctionToken token) {
			this.fileName = checkNotBlank(fileName);
			this.name = checkNotBlank(token.getFunctionName());
			this.definition = checkNotBlank(token.getFunctionDefinition());
			description = Description.createTestDescription(testClass, name);
			metadata = parseElementMetadata(token.getUnparsedTextBeforeFunctionToken());
			logger.debug("loaded metadata = {} for element = {}", metadata, this);
		}

		public String getFileName() {
			return fileName;
		}

		public Description getDescription() {
			return description;
		}

		public String getName() {
			return name;
		}

		public String getDefinition() {
			return definition;
		}

		public boolean isSetup() {
			return name.startsWith("test_setup");
		}

		public boolean isTeardown() {
			return name.startsWith("test_teardown");
		}

		@Nullable
		public String getExpected() {
			return metadata.get("EXPECTED");
		}

		public boolean hasExpected() {
			return isNotBlank(getExpected());
		}

		@Override
		public String toString() {
			return "TestElement{" + "fileName=" + fileName + ", name=" + name + '}';
		}

	}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.framework;

import java.util.Map;
import static org.cmdbuild.test.framework.SqlTestRunner.parseElementMetadata;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SqlMetadataParsingTest {

	@Test
	public void testSqlMetadataParsing() {
		Map<String, String> map = parseElementMetadata("\n\n -- EXPECTED : some expected stuff \n--\n");
		assertEquals(1, map.size());
		assertEquals("some expected stuff", map.get("EXPECTED"));
	}
}

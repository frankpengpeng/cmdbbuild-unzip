/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.json.test;

import static org.cmdbuild.utils.json.CmJsonUtils.prettifyIfJsonLazy;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class JsonProcessingTest {

	@Test
	public void testPrettyPrintLazy1() {
		String value = "{\"one\":[\"asd\",{\"two\":2}]}";
		Object out = prettifyIfJsonLazy(value);
		assertEquals("{\n"
				+ "  \"one\" : [ \"asd\", {\n"
				+ "    \"two\" : 2\n"
				+ "  } ]\n"
				+ "}", out.toString());
	}

	@Test
	public void testPrettyPrintLazy2() {
		String value = "ASD";
		Object out = prettifyIfJsonLazy(value);
		assertEquals(value, out.toString());
	}

	@Test
	public void testPrettyPrintLazy3() {
		String value = null;
		Object out = prettifyIfJsonLazy(value);
		assertEquals(String.valueOf(value), out.toString());
	}

}

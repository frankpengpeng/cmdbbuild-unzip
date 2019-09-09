package org.cmdbuild.utils.crypto.test;

import static org.junit.Assert.assertEquals;

import org.cmdbuild.utils.crypto.CmLegacyPasswordUtils;
import org.junit.Test;

public class LegacyPasswordUtilsTest {

	@Test
	public void test1() {
		String enc = CmLegacyPasswordUtils.encrypt("something");
		String dec = CmLegacyPasswordUtils.decrypt(enc);
		assertEquals("something", dec);
	}

	@Test
	public void test2() {
		String dec = CmLegacyPasswordUtils.decrypt("Sh7JgSrZ4JwCsVBZBZrAsQ==");
		assertEquals("chiara01", dec);
	}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql;

import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import org.cmdbuild.ecql.utils.EcqlUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.junit.Test;

public class EcqlUtilsTest {

	@Test
	public void testResolveEcqlXa1() {
		String ecql1 = "from Hardware where Id in (/(select \"IdObj2\" from \"Map_AssetMgtCI\" where \"IdObj1\"={xa:process} and \"Status\"='A')/";
		String ecql2 = EcqlUtils.resolveEcqlXa(ecql1, map("process", 12345678l));
		assertEquals("from Hardware where Id in (/(select \"IdObj2\" from \"Map_AssetMgtCI\" where \"IdObj1\"=12345678 and \"Status\"='A')/", ecql2);
	}

	@Test
	public void testResolveEcqlXa2() {
		String ecql1 = "from Hardware where Id in (/(select \"IdObj2\" from \"Map_AssetMgtCI\" where \"IdObj1\"='{xa:process}' and \"Status\"='A')/";
		String ecql2 = EcqlUtils.resolveEcqlXa(ecql1, map("process", "test"));
		assertEquals("from Hardware where Id in (/(select \"IdObj2\" from \"Map_AssetMgtCI\" where \"IdObj1\"='test' and \"Status\"='A')/", ecql2);
	}

	@Test
	public void testBuildEcqlId() {
		String encodedId = EcqlUtils.buildEcqlId(EcqlSource.CLASS_ATTRIBUTE, 1234);
		assertEquals("ekfufsvssidcxn", encodedId);
		EcqlId<List<String>> ecqlId = EcqlUtils.parseEcqlId(encodedId);
		assertEquals(EcqlSource.CLASS_ATTRIBUTE, ecqlId.getSource());
		assertEquals(String.valueOf(1234), getOnlyElement(ecqlId.getId()));
	}

	@Test
	public void testBuildEcqlId2() {
		String encodedId = EcqlUtils.buildEcqlId(EcqlSource.CLASS_ATTRIBUTE, 1234, "Something");
		assertEquals("33wdpdl29k27lu2bo2wvk073u7jy5bt17", encodedId);
		EcqlId<List<String>> ecqlId = EcqlUtils.parseEcqlId(encodedId);
		assertEquals(EcqlSource.CLASS_ATTRIBUTE, ecqlId.getSource());
		assertEquals(String.valueOf(1234), ecqlId.getId().get(0));
		assertEquals("Something", ecqlId.getId().get(1));
	}

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CmdbConvertUtilsTest {

	@Test
	public void testBeanConversion() {
		Something input = new Else();
		Something output = CmConvertUtils.convert(input, Something.class);
		assertEquals(input, output);
	}

	@Test
	public void testBooleanConversion() {
		Boolean res = CmConvertUtils.convert("true", Boolean.class);
		assertEquals(true, res);
	}

	@Test
	public void testBooleanPrimitiveConversion() {
		boolean res = CmConvertUtils.convert("true", boolean.class);
		assertEquals(true, res);
	}

	@Test
	public void testIntegerConversion() {
		Integer res = CmConvertUtils.convert("123", Integer.class);
		assertEquals(Integer.valueOf(123), res);
	}

	@Test
	public void testStringArrayConversion1() {
		String[] res = CmConvertUtils.convert(list("a", "b"), String[].class);
		assertEquals(res[0], "a");
		assertEquals(res[1], "b");
	}

	@Test
	public void testStringArrayConversion2() {
		String[] res = CmConvertUtils.convert(new String[]{"a", "b"}, String[].class);
		assertEquals(res[0], "a");
		assertEquals(res[1], "b");
	}

	@Test
	public void testStringArrayConversion3() {
		String[] res = CmConvertUtils.convert(new Integer[]{2, 3}, String[].class);
		assertEquals(res[0], "2");
		assertEquals(res[1], "3");
	}

	@Test
	public void testStringArrayConversion4() {
		Integer[] res = CmConvertUtils.convert(new String[]{"2", "3"}, Integer[].class);
		assertEquals(res[0], (Integer) 2);
		assertEquals(res[1], (Integer) 3);
	}

	@Test
	public void testIntegerPrimitiveConversion() {
		int res = CmConvertUtils.convert("123", int.class);
		assertEquals(123, res);
	}

	@Test
	public void testLongConversion() {
		Long res = CmConvertUtils.convert("123123123123", Long.class);
		assertEquals(Long.valueOf(123123123123l), res);
	}

	@Test
	public void testLongPrimitiveConversion() {
		long res = CmConvertUtils.convert("123123123123", long.class);
		assertEquals(123123123123l, res);
	}

	@Test
	public void testCollectionConversion() {
		List<String> res = CmConvertUtils.convert(asList("1", "2", "3"), List.class);
		assertEquals(asList("1", "2", "3"), res);
	}

	@Test
	public void testArrayToSetConversion() {
		Set<String> res = CmConvertUtils.convert(new String[]{"1", "2", "3"}, Set.class);
		assertEquals(set("1", "2", "3"), res);
	}

	@Test
	@Ignore //TODO
	public void testCollectionConversion2() {
		List<String> res = CmConvertUtils.convert("1,2,3", List.class);
		assertEquals(asList("1", "2", "3"), res);
	}

	@Test
	public void testDateTimeConversion() {
		ZonedDateTime res = CmConvertUtils.convert("2018-05-30T08:39:59.420Z", ZonedDateTime.class);
		assertEquals(1527669599420l, res.toInstant().toEpochMilli());
	}

	@Test
	public void testParametrizedConversion() {
		List<byte[]> list = convert(new Object[]{new byte[]{1, 2, 3}, new byte[]{4, 5, 6}}, new TypeToken<List<byte[]>>() {
		}.getType());

		assertEquals(2, list.size());
		assertTrue(list.get(0) instanceof byte[]);
		assertTrue(list.get(1) instanceof byte[]);
		assertArrayEquals(new byte[]{1, 2, 3}, list.get(0));
		assertArrayEquals(new byte[]{4, 5, 6}, list.get(1));
	}

	@Test
	public void testParametrizedConversion2() {
		List<Integer> list = convert(new Object[]{"1", 2l, new BigInteger("3")}, new TypeToken<List<Integer>>() {
		}.getType());

		assertEquals(3, list.size());
		assertEquals(asList(1, 2, 3), list);
	}

	@Test
	public void testConvertToMap1() {
		Map<String, String> map = convert(ImmutableMap.of("a", "b", "c", "d"), Map.class);

		assertNotNull(map);
		assertEquals(2, map.size());
		assertEquals("b", map.get("a"));
	}

	@Test
	public void testConvertToMap2() {
		Map<String, String> map = convert(ImmutableMap.of("a", "b", "c", "d"), new TypeToken<Map<String, String>>() {
		}.getType());

		assertNotNull(map);
		assertEquals(2, map.size());
		assertEquals("b", map.get("a"));
	}

	@Test
	@Ignore("TODO")
	public void testConvertToMap3() {
		Map<String, String> map = convert("{\"a\":\"b\",\"c\":\"d\"}", Map.class);

		assertNotNull(map);
		assertEquals(2, map.size());
		assertEquals("b", map.get("a"));
	}

	@Test
	public void testConvertToMap4() {
		Map<String, String> map = convert("{\"a\":\"b\",\"c\":\"d\"}", new TypeToken<Map<String, String>>() {
		}.getType());

		assertNotNull(map);
		assertEquals(2, map.size());
		assertEquals("b", map.get("a"));
	}

	@Test
	public void testConvertToMap5() {
		Map<String, Boolean> map = convert("{\"a\":true,\"c\":false}", new TypeToken<Map<String, Boolean>>() {
		}.getType());

		assertNotNull(map);
		assertEquals(2, map.size());
		assertEquals(true, map.get("a"));
		assertEquals(false, map.get("c"));
	}

	@Test
	public void testEnumConversion1() {
		assertEquals(EnumOne.ONE, convert("one", EnumOne.class));
	}

	@Test
	public void testEnumConversion2() {
		assertEquals(EnumOne.ONE, convert("ONE", EnumOne.class));
	}

	@Test
	public void testEnumConversion3() {
		assertEquals(EnumTwo.ET_TWO, convert("two", EnumTwo.class));
	}

	@Test
	public void testEnumConversion4() {
		assertEquals(EnumTwo.ET_TWO, convert("two", EnumTwo.class));
	}

	@Test
	public void testEnumConversion5() {
		assertEquals(EnumTwo.ET_TWO, convert("et_two", EnumTwo.class));
	}

	@Test
	public void testEnumConversion6() {
		assertEquals(EnumThree.ANY_THREE, convert("three", EnumThree.class));
	}

	private static class Something {

	}

	private static class Else extends Something {

	}

	private static enum EnumOne {
		ONE, TWO, THREE
	}

	private static enum EnumTwo {
		ET_ONE, ET_TWO, ET_THREE
	}

	private static enum EnumThree {
		ANY_ONE, ANY_TWO, ANY_THREE
	}

}

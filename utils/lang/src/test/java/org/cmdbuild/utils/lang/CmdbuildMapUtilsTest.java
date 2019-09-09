package org.cmdbuild.utils.lang;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import java.util.Map;
import static java.util.function.Function.identity;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMultimap;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmMapUtils.toMultimap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdbuildMapUtilsTest {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Test
	public void testMultimapCollector1() throws Exception {
		FluentMultimap<String, String> multimap = asList("a", "b", "c").stream().collect(toMultimap(identity(), identity()));
		assertNotNull(multimap);
		assertEquals(3, multimap.size());
		assertEquals(3, multimap.asMap().size());
		assertEquals(newArrayList("a", "b", "c"), newArrayList(multimap.keySet()));
		assertEquals(newArrayList("a", "b", "c"), newArrayList(multimap.keys()));
		assertEquals(newArrayList("a", "b", "c"), newArrayList(multimap.values()));
	}

	@Test
	public void testMultimapCollector2() throws Exception {
		FluentMultimap<String, String> multimap = asList("a", "b", "c").stream().collect(toMultimap((v) -> "1", identity()));
		assertNotNull(multimap);
		assertEquals(3, multimap.size());
		assertEquals(1, multimap.asMap().size());
		assertEquals(newArrayList("1"), newArrayList(multimap.keySet()));
		assertEquals(newArrayList("1", "1", "1"), newArrayList(multimap.keys()));
		assertEquals(newArrayList("a", "b", "c"), newArrayList(multimap.values()));
		assertEquals(newArrayList("a", "b", "c"), newArrayList(multimap.get("1")));
	}

	@Test
	public void testMapCollector1() throws Exception {
		Map<String, String> map = asList("a", "b", "c").stream().collect(toMap(identity(), (v) -> "1"));
		assertEquals(3, map.size());
		assertTrue(map.containsKey("a"));
		assertTrue(map.containsKey("b"));
		assertTrue(map.containsKey("c"));
		assertEquals("1", map.get("a"));
	}

	@Test
	public void testMapCollector2() throws Exception {
		Map<String, String> map = asList("a", "b", "c").stream().collect(toMap(identity(), (v) -> null));
		assertEquals(3, map.size());
		assertTrue(map.containsKey("a"));
		assertTrue(map.containsKey("b"));
		assertTrue(map.containsKey("c"));
		assertEquals(null, map.get("a"));
	}

	@Test(expected = RuntimeException.class)
	public void testMapCollector3() throws Exception {
		Map<String, String> map = asList("a", "b", "c", "b").stream().collect(toMap(identity(), (v) -> "1"));
		fail();
	}

}

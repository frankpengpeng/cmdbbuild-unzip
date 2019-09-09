/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.requestcontext.test;

import org.cmdbuild.requestcontext.RequestContextHolder;
import org.cmdbuild.requestcontext.RequestContextService;
import org.cmdbuild.requestcontext.impl.RequestContextServiceImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class RequestContextTest {

	private RequestContextService requestContextService;

	@Before
	public void setUp() {
		requestContextService = new RequestContextServiceImpl();
		requestContextService.initCurrentRequestContext("test");
	}

	@Test
	public void testHolder() {
		RequestContextHolder<String> holder = requestContextService.createRequestContextHolder();

		assertEquals(null, holder.getOrNull());

		holder.set("test");

		assertEquals("test", holder.getOrNull());
		assertEquals("test", holder.get());

		holder.set(null);

		assertEquals(null, holder.getOrNull());
	}

	@Test(expected = RuntimeException.class)
	public void testHolderFailOnNull() {
		RequestContextHolder<String> holder = requestContextService.createRequestContextHolder();

		holder.get();

		fail();
	}

	@Test
	public void testHolderWithDefault1() {
		RequestContextHolder<String> holder = requestContextService.createRequestContextHolder(() -> "something");

		assertEquals("something", holder.get());

		holder.set("test");

		assertEquals("test", holder.get());
	}

	@Test
	public void testHolderWithDefault2() {
		RequestContextHolder<String> holder = requestContextService.createRequestContextHolder(() -> "something");

		assertEquals("something", holder.getOrNull());

		holder.set("test");

		assertEquals("test", holder.getOrNull());
	}

}

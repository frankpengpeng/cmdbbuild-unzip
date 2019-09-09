/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.test;

import static java.util.Arrays.asList;
import org.cmdbuild.dao.orm.CardMapperRepository;
import org.cmdbuild.dao.orm.services.CardMapperLoader;
import org.cmdbuild.dao.orm.services.CardMapperRepositoryImpl;
import org.cmdbuild.dao.orm.test.beans.RequestData;
import org.cmdbuild.dao.orm.test.beans.SimpleRequestData;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;

public class CardMapperLoaderTest {

	@Test
	public void testCardMapperLoader() {
		CardMapperRepository mapperRepository = new CardMapperRepositoryImpl();
		CardMapperLoader loader = new CardMapperLoader(mapperRepository);

		loader.scanClassesForHandlers(asList(SimpleRequestData.class));

		assertNotNull(mapperRepository.get(RequestData.class));
		assertNotNull(mapperRepository.get(SimpleRequestData.class));
		assertEquals("Request", mapperRepository.get(RequestData.class).getClassId());
		assertEquals(SimpleRequestData.class, mapperRepository.get(RequestData.class).getTargetClass());
	}
}

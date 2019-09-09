/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import org.cmdbuild.dao.beans.AttributeMetadataImpl;
import org.cmdbuild.dao.beans.CardDefinitionImpl;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.orm.CardMapperRepository;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.dao.orm.services.CardMapperLoader;
import org.cmdbuild.dao.orm.services.CardMapperRepositoryImpl;
import org.cmdbuild.dao.orm.services.CardMapperServiceImpl;
import org.cmdbuild.dao.orm.test.beans.RequestData;
import org.cmdbuild.dao.orm.test.beans.SimpleRequestData;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.beans.CardDefinition;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.PostgresService;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.ClassType;

public class CardMapperTest {

	private final ClasseRepository classeRepository = mock(ClasseRepository.class);
	private final Classe classe = mock(Classe.class);

	private final CardMapperRepository mapperRepository = new CardMapperRepositoryImpl();
	private final CardMapperLoader loader = new CardMapperLoader(mapperRepository);
	private final CardMapperService service = new CardMapperServiceImpl(mapperRepository, classeRepository);

	@Before
	public void init() {
		loader.scanClassesForHandlers(asList(SimpleRequestData.class));

		Attribute actionId = attr("ActionId", new StringAttributeType(), classe);
		Attribute payload = attr("Payla", new StringAttributeType(), classe);
		Attribute payloadSize = attr("PayloadSize", new IntegerAttributeType(), classe);
		Attribute response = attr("Response", new StringAttributeType(), classe);

		when(classe.getAttributeOrNull("ActionId")).thenReturn(actionId);
		when(classe.getAttributeOrNull("Payla")).thenReturn(payload);
		when(classe.getAttributeOrNull("PayloadSize")).thenReturn(payloadSize);
		when(classe.getAttributeOrNull("Response")).thenReturn(response);

		when(classe.getAttribute("ActionId")).thenReturn(actionId);
		when(classe.getAttribute("Payla")).thenReturn(payload);
		when(classe.getAttribute("PayloadSize")).thenReturn(payloadSize);
		when(classe.getAttribute("Response")).thenReturn(response);

		when(classe.hasAttribute("ActionId")).thenReturn(true);
		when(classe.hasAttribute("Payla")).thenReturn(true);
		when(classe.hasAttribute("PayloadSize")).thenReturn(true);
		when(classe.hasAttribute("Response")).thenReturn(true);

		when(classe.getServiceAttributes()).thenReturn(asList(actionId, payload, payloadSize, response));

		when(classe.getName()).thenReturn("Request");
		when(classe.getClassType()).thenReturn(ClassType.SIMPLE);

		when(classeRepository.getClasse("Request")).thenReturn(classe);
	}

	@Test
	public void testObjectToCard() {

		RequestData object = SimpleRequestData.builder()
				.withActionId("myActionId")
				.withPayload("myPayload")
				.withPayloadSize(123)
				.withResponse(null)
				.build();

		Card card = service.objectToCard(object);

		assertEquals("myActionId", card.get("ActionId", String.class));
		assertEquals("myPayload", card.get("Payla", String.class));
		assertEquals(new Integer(123), card.get("PayloadSize", Integer.class));
		assertEquals(null, card.get("Response", String.class));
	}

	@Test
	public void testCardToObject() {
		CardDefinition cardDefinition = CardDefinitionImpl.newInstance(mock(PostgresService.class), classe);

		cardDefinition.set("ActionId", "myActionId");
		cardDefinition.set("Payla", "myPayload");
		cardDefinition.set("PayloadSize", 123);
		cardDefinition.set("Response", null);

		Card card = (Card) cardDefinition;

		RequestData object = service.cardToObject(card);

		assertEquals("myActionId", object.getActionId());
		assertEquals("myPayload", object.getPayload());
		assertEquals(new Integer(123), object.getPayloadSize());
		assertEquals(null, object.getResponse());
	}

	private static Attribute attr(String name, CardAttributeType type, Classe classe) {
		return AttributeImpl.builder().withName(name).withType(type).withMeta(new AttributeMetadataImpl(emptyMap())).withOwner(classe).build();
	}
}

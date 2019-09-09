/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.EnumSet;
import java.util.List;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.ClassType;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.formtrigger.FormTrigger;
import org.cmdbuild.formtrigger.FormTriggerBinding;
import org.cmdbuild.formtrigger.FormTriggerImpl;
import org.cmdbuild.formtrigger.FormTriggerService;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@RunWith(CmTestRunner.class)
public class FormTriggersIT {

	private final FormTriggerService service;
	private final ClasseRepository classeRepository;

	private Classe classe;

	public FormTriggersIT(FormTriggerService service, ClasseRepository classeRepository) {
		this.service = checkNotNull(service);
		this.classeRepository = checkNotNull(classeRepository);
	}

	@Before
	public void setUp() {
		prepareTuid();
		classe = classeRepository.createClass(ClassDefinitionImpl.builder()
				.withName(format("Test%s", tuid()))
				.withParent(classeRepository.getRootClass())
				.withMetadata(ClassMetadataImpl.builder()
						.withType(ClassType.STANDARD)
						.build())
				.build());
	}

	@Test
	public void testGet() {
		List<FormTrigger> formTriggers = service.getFormTriggersForClass(classe);
		assertNotNull(formTriggers);
		assertEquals(0, formTriggers.size());
	}

	@Test
	public void testUpdate() {
		List<FormTrigger> formTriggers = list(
				FormTriggerImpl.builder().withActive(true).withJsScript("something=\"js\";").withBindings(EnumSet.of(FormTriggerBinding.beforeInsert, FormTriggerBinding.beforeView)).build(),
				FormTriggerImpl.builder().withActive(false).withJsScript("something=\"else\";").withBindings(EnumSet.of(FormTriggerBinding.afterClone)).build());
		service.updateFormTriggersForClass(classe, formTriggers);

		List<FormTrigger> res = service.getFormTriggersForClass(classe);
		assertNotNull(res);
		assertEquals(2, res.size());

		FormTrigger t1 = res.get(0);
		assertEquals(true, t1.isActive());
		assertEquals("something=\"js\";", t1.getJsScript());
		assertEquals(EnumSet.of(FormTriggerBinding.beforeInsert, FormTriggerBinding.beforeView), t1.getBindings());

		FormTrigger t2 = res.get(1);
		assertEquals(false, t2.isActive());
		assertEquals("something=\"else\";", t2.getJsScript());
		assertEquals(EnumSet.of(FormTriggerBinding.afterClone), t2.getBindings());
	}

}

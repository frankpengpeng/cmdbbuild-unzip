/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.lookup.LookupImpl;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class EmailTemplateIT {

    private final DaoService dao;
    private final CacheService cacheService;
    private final LookupService lookupService;
    private final EmailTemplateProcessorService emailTemplateProcessorService;

    public EmailTemplateIT(DaoService dao, CacheService cacheService, LookupService lookupService, EmailTemplateProcessorService emailTemplateProcessorService) {
        this.dao = checkNotNull(dao);
        this.cacheService = checkNotNull(cacheService);
        this.lookupService = checkNotNull(lookupService);
        this.emailTemplateProcessorService = checkNotNull(emailTemplateProcessorService);
    }

    @Before
    public void init() {
        prepareTuid();
    }

    @Test
    public void testFtlTemplate() {
        EmailTemplate emailTemplate = EmailTemplateImpl.builder()
                .withName(tuid("MyTemplate"))
                .withTextPlainContentType()
                .withBody("[#ftl]this is a ftl template with ${data.myKey} interpolation")
                .withSubject("[#ftl]sub[#if data.myBool]jet[/#if]")
                .build();

        Email email = emailTemplateProcessorService.createEmailFromTemplate(emailTemplate, map("myKey", "My Data", "myBool", true));

        assertEquals("this is a ftl template with My Data interpolation", email.getContentPlaintext());
        assertEquals("subjet", email.getSubject());
    }

    @Test
    public void testFtlTemplateApiCall() {
        Classe myClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());
        Card myCard = dao.create(CardImpl.buildCard(myClass, ATTR_CODE, "my_card", ATTR_DESCRIPTION, "My Card"));

        String myFunctionName = tuid("_test_function").toLowerCase();
        dao.getJdbcTemplate().execute(format("CREATE OR REPLACE FUNCTION %s(_param varchar, OUT _out varchar) RETURNS varchar AS $$ BEGIN _out = 'hello ' || _param || '!'; END $$ LANGUAGE PLPGSQL;"
                + "COMMENT ON FUNCTION %s(_param varchar, OUT _out varchar) IS 'TYPE: function';", myFunctionName, myFunctionName));
        cacheService.invalidateAll();
        assertNotNull(dao.getFunctionByName(myFunctionName));

        EmailTemplate emailTemplate = EmailTemplateImpl.builder()
                .withName(tuid("MyTemplate"))
                .withTextPlainContentType()
                .withBody("[#ftl]this is a ftl template with code from api call = ${cmdb.existingCard(data.myClassId,data.myCardId).fetch().getCode()} :)")
                .withSubject("[#ftl]my ${cmdb.callFunction(data.myFunctionName).with('_param','subject').execute()._out}")
                .build();

        Email email = emailTemplateProcessorService.createEmailFromTemplate(emailTemplate, map("myClassId", myClass.getName(), "myCardId", myCard.getId(), "myFunctionName", myFunctionName));

        assertEquals("this is a ftl template with code from api call = my_card :)", email.getContentPlaintext());
        assertEquals("my hello subject!", email.getSubject());
    }

    @Test
    public void testEmailEasytemplate() {
        EmailTemplate emailTemplate = EmailTemplateImpl.builder()
                .withName(tuid("MyTemplate"))
                .withTextPlainContentType()
                .withBody("this is an easy template with {server:Code} interpolation")
                .build();

        Classe classe = dao.createClass(ClassDefinitionImpl.build(tuid("MyClass")));
        Card card = dao.create(CardImpl.buildCard(classe, ATTR_CODE, "MyCode"));
        card = dao.getCard(classe, card.getId());

        Email email = emailTemplateProcessorService.createEmailFromTemplate(emailTemplate, card);

        assertEquals("this is an easy template with MyCode interpolation", email.getContentPlaintext());
    }

    @Test
    public void testEmailEasytemplateWithIdAndDescSyntax() {
        EmailTemplate emailTemplate = EmailTemplateImpl.builder()
                .withName(tuid("MyTemplate"))
                .withTextPlainContentType()
                .withBody("this is an easy template with lookup value = {server:MyLookupAttr}, description = {server:MyLookupAttr.Description}, id = {server:MyLookupAttr.Id} and code = {server:MyLookupAttr.Code}")
                .build();

        LookupType lookupType = lookupService.createLookupType(tuid("MyLookupType"));
        Lookup lookupValue = lookupService.createOrUpdateLookup(LookupImpl.builder().withType(lookupType).withCode("mylookupCode").withDescription("My Lookup Description").build());

        Classe classe = dao.createClass(ClassDefinitionImpl.build(tuid("MyClass")));
        dao.createAttribute(AttributeImpl.builder().withName("MyLookupAttr").withType(new LookupAttributeType(lookupType)).withOwner(classe).build());
        classe = dao.getClasse(classe);
        Card card = dao.create(CardImpl.buildCard(classe, ATTR_CODE, "MyCode", "MyLookupAttr", lookupValue));
        card = dao.getCard(classe, card.getId());

        Email email = emailTemplateProcessorService.createEmailFromTemplate(emailTemplate, card);

        assertEquals(format("this is an easy template with lookup value = %s, description = My Lookup Description, id = %s and code = mylookupCode", lookupValue.getId(), lookupValue.getId()), email.getContentPlaintext());
    }

}

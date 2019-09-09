/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_ONE;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailTemplateService;
import org.cmdbuild.etl.ImportExportColumnConfigImpl;
import org.cmdbuild.etl.ImportExportColumnMode;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_CSV;
import org.cmdbuild.etl.ImportExportOperationResult;
import org.cmdbuild.etl.ImportExportService;
import org.cmdbuild.etl.ImportExportTemplate;
import org.cmdbuild.etl.ImportExportTemplateImpl;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_IMPORT_EXPORT;
import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class ImportErrorMessagesIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final ImportExportService service;
    private final EmailTemplateService emailTemplateService;
    private final EmailAccountService emailAccountService;
    private final LookupService lookupService;

    private Classe myClass, myClass1, myClass2;
    private ImportExportTemplate importTemplate;
    private LookupType myLookupType;
    private Lookup lookup;
    private Domain domain;
    private Attribute attribute;
    private Card one, two;
    private CMRelation relation;

    public ImportErrorMessagesIT(DaoService dao, ImportExportService service, EmailTemplateService emailTemplateService, EmailAccountService emailAccountService, LookupService lookupService, SessionService sessionService, UserRepository userRepository, RoleRepository roleRepository, UserConfigService userConfigService) {
        this.dao = checkNotNull(dao);
        this.service = checkNotNull(service);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.emailAccountService = checkNotNull(emailAccountService);
        this.lookupService = checkNotNull(lookupService);
    }

    @Before
    public void init() {
        prepareTuid();

        myClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());
        myClass1 = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MySecondClass")).build());
        myClass2 = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyThirdClass")).build());
        domain = dao.createDomain(DomainDefinitionImpl.builder().withSourceClass(myClass1).withTargetClass(myClass2).withCardinality(ONE_TO_ONE).withName(tuid("MyDomain")).build());
        domain = dao.getDomain(domain.getName());
        attribute = dao.createAttribute(AttributeImpl.builder().withOwner(domain).withName("ReferenceAttr").withType(new StringAttributeType()).build());

        one = dao.create(CardImpl.buildCard(myClass1, map(ATTR_CODE, "ally")));
        two = dao.create(CardImpl.buildCard(myClass2, map(ATTR_CODE, "bob")));

        relation = dao.create(RelationImpl.builder().withType(domain).withSourceCard(one).withTargetCard(two).addAttribute(attribute.getName(), "myAttrValue").build());
        myLookupType = lookupService.createLookupType(tuid("MyLookupType"));
        lookup = lookupService.createOrUpdateLookup(myLookupType, "one");

        dao.updateAttribute(AttributeImpl.copyOf(myClass.getAttribute(ATTR_CODE)).withClassOrderInMeta(1).build());
        dao.createAttribute(AttributeImpl.builder().withType(new StringAttributeType()).withName("StringAttr").withMeta((m) -> m.withRequired(true)).withOwner(myClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new LookupAttributeType(myLookupType)).withName("LookupAttr").withOwner(myClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new ReferenceAttributeType(domain, RD_DIRECT)).withName("ReferenceAttr").withOwner(myClass).build());
        myClass = dao.getClasse(myClass.getName());
        myClass1 = dao.getClasse(myClass1.getName());
        myClass2 = dao.getClasse(myClass2.getName());

        importTemplate = ImportExportTemplateImpl.builder()
                .withCode("myCsvTemplate")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("StringAttr").withColumnName("STRINGATTR").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("LookupAttr").withColumnName("LOOKUPATTR").withMode(ImportExportColumnMode.IECM_ID).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("ReferenceAttr").withColumnName("REFERENCEATTR").withMode(ImportExportColumnMode.IECM_CODE).build()))
                .withTarget(myClass)
                .withType(IETT_IMPORT_EXPORT)
                .withFileFormat(IEFF_CSV)
                .withImportKeyAttribute("Code")
                .withCsvSeparator(";")
                .build();
    }

    @Test
    public void testLookupErrorMessage() {
        String csvImportData = "CODE;STRINGATTR;LOOKUPATTR;REFERENCEATTR\nfour;five;123;" + relation.getTargetCode();
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), importTemplate);

        assertEquals("lookup not found for id =< 123 >", getOnlyElement(result.getErrors()).getUserErrorMessage());
    }

    @Test
    public void testReferenceErrorMessage() {
        String csvImportData = "CODE;STRINGATTR;LOOKUPATTR;REFERENCEATTR\nfour;five;" + lookup.getId() + ";wrong";
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), importTemplate);

        assertEquals("card not found for code =< wrong >", getOnlyElement(result.getErrors()).getUserErrorMessage());
    }

    @Test
    public void testMandatoryNullFieldErrorMessage() {
        String csvImportData = "CODE;STRINGATTR;LOOKUPATTR;REFERENCEATTR\nfour;;" + lookup.getId() + ";" + relation.getTargetCode();
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), importTemplate);

        assertEquals("missing value for required attr = StringAttr", getOnlyElement(result.getErrors()).getUserErrorMessage());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.transform;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import java.util.List;
import javax.activation.DataSource;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.IdAndDescription;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailTemplate;
import org.cmdbuild.email.EmailTemplateService;
import org.cmdbuild.email.beans.EmailAccountImpl;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import org.cmdbuild.etl.ImportExportColumnConfigImpl;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_CODE;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_DESCRIPTION;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_ID;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_IGNORE;
import org.cmdbuild.etl.ImportExportFileFormat;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_CSV;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_XLS;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_XLSX;
import static org.cmdbuild.etl.ImportExportMergeMode.IEM_DELETE_MISSING;
import static org.cmdbuild.etl.ImportExportMergeMode.IEM_UPDATE_ATTR_ON_MISSING;
import org.cmdbuild.etl.ImportExportOperationResult;
import org.cmdbuild.etl.ImportExportService;
import org.cmdbuild.etl.ImportExportTemplate;
import org.cmdbuild.etl.ImportExportTemplateImpl;
import static org.cmdbuild.etl.ImportExportTemplateTarget.IET_CLASS;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_EXPORT;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_IMPORT_EXPORT;
import org.cmdbuild.lookup.LookupImpl;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_DATE_FORMAT_EXTJS;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_TIMEZONE;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_TIME_FORMAT_EXTJS;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class ImportExportIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final ImportExportService service;
    private final EmailTemplateService emailTemplateService;
    private final EmailAccountService emailAccountService;
    private final LookupService lookupService;
    private final SessionService sessionService;
    private final UserRepository userRepository;//TODO improve user creation
    private final RoleRepository roleRepository;//TODO improve user creation
    private final UserConfigService userConfigService;

    private EmailTemplate emailTemplate;
    private EmailAccount emailAccount;
    private Classe myClass;
    private ImportExportTemplate simpleTemplate;

    public ImportExportIT(DaoService dao, ImportExportService service, EmailTemplateService emailTemplateService, EmailAccountService emailAccountService, LookupService lookupService, SessionService sessionService, UserRepository userRepository, RoleRepository roleRepository, UserConfigService userConfigService) {
        this.dao = checkNotNull(dao);
        this.service = checkNotNull(service);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.emailAccountService = checkNotNull(emailAccountService);
        this.lookupService = checkNotNull(lookupService);
        this.sessionService = checkNotNull(sessionService);
        this.userRepository = checkNotNull(userRepository);
        this.roleRepository = checkNotNull(roleRepository);
        this.userConfigService = checkNotNull(userConfigService);
    }

    @Before
    public void init() {
        prepareTuid();

        emailTemplate = emailTemplateService.createEmailTemplate(EmailTemplateImpl.builder().withName(tuid("myEmailTemplate")).withTextPlainContentType().build());
        emailAccount = emailAccountService.create(EmailAccountImpl.builder().withName(tuid("myEmailAccount")).withAddress("my.email@account.com").build());

        myClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());
        dao.updateAttribute(AttributeImpl.copyOf(myClass.getAttribute(ATTR_CODE)).withClassOrderInMeta(1).build());
        dao.createAttribute(AttributeImpl.builder().withType(new StringAttributeType()).withName("MyAttr").withOwner(myClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new DateTimeAttributeType()).withName("DateTimeAttr").withOwner(myClass).build());
        myClass = dao.getClasse(myClass.getName());

        dao.create(CardImpl.buildCard(myClass, map("Code", "one", "Description", "unooo", "MyAttr", "1", "DateTimeAttr", "2018-02-23T12:24Z")));
        dao.create(CardImpl.buildCard(myClass, map("Code", "two", "Description", "due2322", "MyAttr", "2", "DateTimeAttr", "2018-05-23T14:25Z")));
        dao.create(CardImpl.buildCard(myClass, map("Code", "three", "Description", "333tre", "MyAttr", "3", "DateTimeAttr", "2018-08-23T16:27Z")));

        simpleTemplate = ImportExportTemplateImpl.builder()
                .withCode("myCsvTemplate")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("MyAttr").withColumnName("ATTR").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("DateTimeAttr").withColumnName("DATE").build()))
                .withTarget(myClass)
                .withType(IETT_IMPORT_EXPORT)
                .withFileFormat(IEFF_CSV)
                .withImportKeyAttribute("MyAttr")
                .build();

        UserData user = userRepository.create(UserDataImpl.builder().withUsername(tuid("my_username")).build());//TODO improve user creation
        roleRepository.setUserGroupsByName(user.getId(), list("SuperUser"), null);//TODO improve user creation

        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(user.getUsername()));

        userConfigService.setForCurrent(USER_CONFIG_TIMEZONE, "Europe/Rome");
        userConfigService.setForCurrent(USER_CONFIG_DATE_FORMAT_EXTJS, "d/m/Y");
        userConfigService.setForCurrent(USER_CONFIG_TIME_FORMAT_EXTJS, "H:i:s");
    }

    @Test
    public void testTemplateCreation() {

        ImportExportTemplate template = ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode("myTemplate")
                .withAttributeNameForUpdateAttrOnMissing("MyAttr")
                .withAttributeValueForUpdateAttrOnMissing("myValue")
                //                .withExportFilter("{\"filter\":\"meh\"}") TODO
                .withMergeMode(IEM_UPDATE_ATTR_ON_MISSING)
                .withTarget(myClass)
                .withType(IETT_EXPORT)
                .withFileFormat(IEFF_XLSX)
                .withErrorEmailTemplateId((long) emailTemplate.getId())
                .withErrorEmailAccountId((long) emailAccount.getId())
                .build();

        ImportExportTemplate template1 = service.create(template);
        assertEquals(template.getCode(), template1.getCode());
        assertEquals(emailTemplate.getId(), template1.getErrorEmailTemplateId());
        assertEquals(emailAccount.getId(), template1.getErrorEmailAccountId());

        ImportExportTemplate template2 = getOnlyElement(service.getAllForTarget(IET_CLASS, myClass.getName()));
        assertEquals(template1.getId(), template2.getId());
        assertEquals(template.getCode(), template2.getCode());
        assertEquals(template.getFileFormat(), template2.getFileFormat());

    }

    @Test
    public void testCsvTemplate() {
        ImportExportTemplate csvTemplate = service.create(ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode("myCsvTemplate")
                .withFileFormat(IEFF_CSV)
                .withCsvSeparator(";")
                .build());
        assertTrue(csvTemplate.getUseHeader());

        DataSource export = service.exportDataWithTemplate(csvTemplate.getId());
        assertEquals("text/csv", export.getContentType());
        assertThat(export.getName(), matchesPattern(".*.csv"));

        String content = readToString(export);
        assertThat(content, matchesPattern("(?s)CODE;DESCRIPTION;ATTR;DATE\r\n.*"));
    }

    @Test
    public void testXlsTemplate() {
        ImportExportTemplate xlsTemplate = service.create(ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode("myXlsTemplate")
                .withFileFormat(IEFF_XLS)
                .build());

        DataSource export = service.exportDataWithTemplate(xlsTemplate.getId());
        assertEquals("application/vnd.ms-excel", export.getContentType());
        assertThat(export.getName(), matchesPattern(".*.xls"));

        byte[] content = toByteArray(export);
        assertEquals("application/x-tika-msoffice", getContentType(content));
    }

    @Test
    public void testXlsxTemplate() {
        ImportExportTemplate xlsxTemplate = service.create(ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode("myXlsxTemplate")
                .withFileFormat(IEFF_XLSX)
                .build());

        DataSource export = service.exportDataWithTemplate(xlsxTemplate.getId());
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", export.getContentType());
        assertThat(export.getName(), matchesPattern(".*.xlsx"));

        byte[] content = toByteArray(export);
        assertEquals("application/x-tika-ooxml", getContentType(content));
    }

    @Test
    public void testCsvImport() {
        Classe classe = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyOtherClass")).build());
        ImportExportTemplate template = ImportExportTemplateImpl.builder()
                .withCode("myCsvTemplate")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build()))
                .withTarget(classe)
                .withType(IETT_IMPORT_EXPORT)
                .withMergeMode(IEM_DELETE_MISSING)
                .withFileFormat(IEFF_CSV)
                .withImportKeyAttribute("Code")
                .build();
        String csvImportData = "CODE,DESCRIPTION\r\none,OnE\r\ntwo,t2222o\r\n";
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), template);
        assertFalse(result.hasErrors());

        List<Card> cards = dao.selectAll().from(classe).getCards();
        assertEquals(2, cards.size());
        assertEquals(set("one", "two"), set(transform(cards, Card::getCode)));

        String csvExportData = readToString(service.exportDataWithTemplate(template));
        assertEquals(csvImportData, csvExportData);

        String csvImportData2 = "CODE,DESCRIPTION\r\none,ONEEEEEE\r\n";
        result = service.importDataWithTemplate(newDataSource(csvImportData2, "text/csv", "file.csv"), template);
        assertFalse(result.hasErrors());

        List<Card> cards2 = dao.selectAll().from(classe).getCards();
        assertEquals(1, cards2.size());
        assertEquals(set("one"), set(transform(cards2, Card::getCode)));
        assertEquals("ONEEEEEE", getOnlyElement(cards2).getDescription());

        String csvExportData2 = readToString(service.exportDataWithTemplate(template));
        assertEquals(csvImportData2, csvExportData2);
    }

    @Test
    public void testXlsImportWithIgnoredColumn() {
        Classe classe = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyOtherClass")).build());
        ImportExportTemplate xlsTemplate = ImportExportTemplateImpl.builder()
                .withCode("myXlsTemplateIgnore")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withColumnName("IGNORED").withMode(IECM_IGNORE).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build()))
                .withTarget(classe)
                .withType(IETT_IMPORT_EXPORT)
                .withMergeMode(IEM_DELETE_MISSING)
                .withFileFormat(IEFF_XLS)
                .withImportKeyAttribute("Code")
                .build();
        DataSource source = newDataSource(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/testImportIgnoreColumn.xls")), "application/octet-stream", "file.xls");
        ImportExportOperationResult result = service.importDataWithTemplate(source, xlsTemplate);
        assertFalse(result.hasErrors());

        List<Card> cards = dao.selectAll().from(classe).getCards();
        assertEquals(2, cards.size());
        assertEquals(set("test1", "test2"), set(transform(cards, Card::getCode)));
    }

    @Test
    public void testCsvImportWithIgnoredColumn() {
        Classe classe = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyOtherClass")).build());
        ImportExportTemplate template = ImportExportTemplateImpl.builder()
                .withCode("myCsvTemplate")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withColumnName("IGNORED").withMode(IECM_IGNORE).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build()))
                .withTarget(classe)
                .withType(IETT_IMPORT_EXPORT)
                .withMergeMode(IEM_DELETE_MISSING)
                .withFileFormat(IEFF_CSV)
                .withImportKeyAttribute("Code")
                .build();
        String csvImportData = "CODE,IGNORED,DESCRIPTION\r\none,a,OnE\r\ntwo,b,t2222o\r\n";
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), template);
        assertFalse(result.hasErrors());

        List<Card> cards = dao.selectAll().from(classe).getCards();
        assertEquals(2, cards.size());
        assertEquals(set("one", "two"), set(transform(cards, Card::getCode)));

        String csvExportData = readToString(service.exportDataWithTemplate(template));
        assertEquals("CODE,IGNORED,DESCRIPTION\r\none,,OnE\r\ntwo,,t2222o\r\n", csvExportData);

        String csvImportData2 = "CODE,IGNORED,DESCRIPTION\r\none,a,ONEEEEEE\r\n";
        result = service.importDataWithTemplate(newDataSource(csvImportData2, "text/csv", "file.csv"), template);
        assertFalse(result.hasErrors());

        List<Card> cards2 = dao.selectAll().from(classe).getCards();
        assertEquals(1, cards2.size());
        assertEquals(set("one"), set(transform(cards2, Card::getCode)));
        assertEquals("ONEEEEEE", getOnlyElement(cards2).getDescription());

        String csvExportData2 = readToString(service.exportDataWithTemplate(template));
        assertEquals("CODE,IGNORED,DESCRIPTION\r\none,,ONEEEEEE\r\n", csvExportData2);
    }

    @Test
    public void testCsvImportWithAutoIgnoredColumn() {
        Classe classe = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyOtherClass")).build());
        ImportExportTemplate template = ImportExportTemplateImpl.builder()
                .withCode("myCsvTemplate")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build()))
                .withTarget(classe)
                .withType(IETT_IMPORT_EXPORT)
                .withMergeMode(IEM_DELETE_MISSING)
                .withFileFormat(IEFF_CSV)
                .withImportKeyAttribute("Code")
                .withIgnoreColumnOrder(true)
                .build();
        String csvImportData = "CODE,IGNORED,DESCRIPTION,IGNORED,OTHER\r\none,a,OnE,b,c\r\ntwo,b,t2222o,f,g\r\n";
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), template);
        assertFalse(result.hasErrors());

        List<Card> cards = dao.selectAll().from(classe).getCards();
        assertEquals(2, cards.size());
        assertEquals(set("one", "two"), set(transform(cards, Card::getCode)));

        String csvExportData = readToString(service.exportDataWithTemplate(template));
        assertEquals("CODE,DESCRIPTION\r\none,OnE\r\ntwo,t2222o\r\n", csvExportData);

        String csvImportData2 = "DESCRIPTION,OTHER,CODE\r\nONEEEEEE,a,one\r\n";
        result = service.importDataWithTemplate(newDataSource(csvImportData2, "text/csv", "file.csv"), template);
        assertFalse(result.hasErrors());

        List<Card> cards2 = dao.selectAll().from(classe).getCards();
        assertEquals(1, cards2.size());
        assertEquals(set("one"), set(transform(cards2, Card::getCode)));
        assertEquals("ONEEEEEE", getOnlyElement(cards2).getDescription());

        String csvExportData2 = readToString(service.exportDataWithTemplate(template));
        assertEquals("CODE,DESCRIPTION\r\none,ONEEEEEE\r\n", csvExportData2);
    }

    @Test
    public void testXlsImportWithAutoIgnoreColumn() {
        Classe classe = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyOtherClass")).build());
        ImportExportTemplate xlsTemplate = ImportExportTemplateImpl.builder()
                .withCode("myXlsTemplateIgnore")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build()))
                .withTarget(classe)
                .withType(IETT_IMPORT_EXPORT)
                .withMergeMode(IEM_DELETE_MISSING)
                .withFileFormat(IEFF_XLS)
                .withImportKeyAttribute("Code")
                .withIgnoreColumnOrder(true)
                .build();
        DataSource source = newDataSource(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/testImportIgnoreColumn.xls")), "application/octet-stream", "file.xls");
        ImportExportOperationResult result = service.importDataWithTemplate(source, xlsTemplate);
        assertFalse(result.hasErrors());

        List<Card> cards = dao.selectAll().from(classe).getCards();
        assertEquals(2, cards.size());
        assertEquals(set("test1", "test2"), set(transform(cards, Card::getCode)));
        assertEquals(set("test11", "test22"), set(transform(cards, Card::getDescription)));
    }

    @Test
    public void testXlsImportWithAutoIgnoreColumn2() {
        Classe classe = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyOtherClass")).build());
        ImportExportTemplate xlsTemplate = ImportExportTemplateImpl.builder()
                .withCode("myXlsTemplateIgnore")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build()))
                .withTarget(classe)
                .withType(IETT_IMPORT_EXPORT)
                .withMergeMode(IEM_DELETE_MISSING)
                .withFileFormat(IEFF_XLS)
                .withImportKeyAttribute("Code")
                .withIgnoreColumnOrder(true)
                .build();
        DataSource source = newDataSource(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/testImportIgnoreColumn2.xls")), "application/octet-stream", "file.xls");
        ImportExportOperationResult result = service.importDataWithTemplate(source, xlsTemplate);
        assertFalse(result.hasErrors());

        List<Card> cards = dao.selectAll().from(classe).getCards();
        assertEquals(2, cards.size());
        assertEquals(set("test1", "test2"), set(transform(cards, Card::getCode)));
        assertEquals(set("test11", ""), set(transform(cards, Card::getDescription)));
    }

    @Test
    public void testCsvImportWithoutHeader() {
        Classe classe = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyOtherClass")).build());
        ImportExportTemplate template = ImportExportTemplateImpl.builder()
                .withCode("myCsvTemplate")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build()))
                .withTarget(classe)
                .withType(IETT_IMPORT_EXPORT)
                .withMergeMode(IEM_DELETE_MISSING)
                .withFileFormat(IEFF_CSV)
                .withImportKeyAttribute("Code")
                .withUseHeader(false)
                .build();
        String csvImportData = "one,OnE\r\ntwo,t2222o\r\n";
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), template);
        assertFalse(result.hasErrors());

        List<Card> cards = dao.selectAll().from(classe).getCards();
        assertEquals(2, cards.size());
        assertEquals(set("one", "two"), set(transform(cards, Card::getCode)));

        String csvExportData = readToString(service.exportDataWithTemplate(template));
        assertEquals(csvImportData, csvExportData);

        String csvImportData2 = "one,ONEEEEEE\r\n";
        result = service.importDataWithTemplate(newDataSource(csvImportData2, "text/csv", "file.csv"), template);
        assertFalse(result.hasErrors());

        List<Card> cards2 = dao.selectAll().from(classe).getCards();
        assertEquals(1, cards2.size());
        assertEquals(set("one"), set(transform(cards2, Card::getCode)));
        assertEquals("ONEEEEEE", getOnlyElement(cards2).getDescription());

        String csvExportData2 = readToString(service.exportDataWithTemplate(template));
        assertEquals(csvImportData2, csvExportData2);
    }

    @Test
    public void testCsvImportExportAttrConversion() {

        LookupType myLookupType = lookupService.createLookupType(tuid("MyLookupType"));
        lookupService.createOrUpdateLookup(LookupImpl.builder().withType(myLookupType).withCode("something").withDescription("SO ME THING").build());
        lookupService.createOrUpdateLookup(myLookupType, "else");
        lookupService.createOrUpdateLookup(myLookupType, "other");

        Classe myOtherClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyOtherClass")).build());
        Domain domain = dao.createDomain(DomainDefinitionImpl.builder().withSourceClass(myOtherClass).withTargetClass(myClass).withCardinality(MANY_TO_ONE).build());
        dao.updateAttribute(AttributeImpl.copyOf(myOtherClass.getAttribute(ATTR_CODE)).withClassOrderInMeta(1).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(myOtherClass).withName("BooleanAttr").withType(new BooleanAttributeType()).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(myOtherClass).withName("DateTimeAttr").withType(new DateTimeAttributeType()).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(myOtherClass).withName("ReferenceAttr").withType(new ReferenceAttributeType(domain, RD_DIRECT)).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(myOtherClass).withName("FkAttr").withType(new ForeignKeyAttributeType(myClass)).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(myOtherClass).withName("OtherFkAttr").withType(new ForeignKeyAttributeType(myClass)).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(myOtherClass).withName("LookupAttr").withType(new LookupAttributeType(myLookupType)).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(myOtherClass).withName("OtherLookupAttr").withType(new LookupAttributeType(myLookupType)).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(myOtherClass).withName("DoubleAttr").withType(new DoubleAttributeType()).build());
        myOtherClass = dao.getClasse(myOtherClass.getName());

        ImportExportTemplate template = ImportExportTemplateImpl.builder()
                .withCode("myCsvTemplate")
                .withColumns(list(ImportExportColumnConfigImpl.build("Code"),
                        ImportExportColumnConfigImpl.build("Description"),
                        ImportExportColumnConfigImpl.build("BooleanAttr"),
                        ImportExportColumnConfigImpl.build("DateTimeAttr"),
                        ImportExportColumnConfigImpl.builder().withAttributeName("ReferenceAttr").withMode(IECM_ID).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("FkAttr").withMode(IECM_CODE).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("OtherFkAttr").withMode(IECM_DESCRIPTION).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("LookupAttr").withMode(IECM_CODE).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("OtherLookupAttr").withMode(IECM_DESCRIPTION).build(),
                        ImportExportColumnConfigImpl.build("DoubleAttr")
                ))
                .withTarget(myOtherClass)
                .withType(IETT_IMPORT_EXPORT)
                .withFileFormat(IEFF_CSV)
                .withImportKeyAttribute("Code")
                .withUseHeader(false)
                .build();

        Card refTarget1 = dao.selectAll().from(myClass).where(ATTR_CODE, EQ, "one").getCard(),
                refTarget2 = dao.selectAll().from(myClass).where(ATTR_CODE, EQ, "two").getCard();
        assertTrue(isNotBlank(refTarget1.getCode()));
        assertTrue(isNotBlank(refTarget1.getDescription()));
        assertTrue(isNotBlank(refTarget2.getCode()));
        assertTrue(isNotBlank(refTarget2.getDescription()));

        String csvImportData = format("ally,AlllY,true,23/02/2019 10:13:34,%s,%s,,other,,12.43\r\nbob,BOB,false,10/12/1988 00:00:00,%s,,,,,0.33\r\rcharlie,CCCC,,,,%s,%s,something,SO ME THING,1.144\r\n",
                refTarget1.getId(), refTarget1.getCode(), refTarget2.getId(), refTarget2.getCode(), refTarget1.getDescription());

        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), template);
        assertFalse(result.getErrorsDescription(), result.hasErrors());
        assertEquals(3, result.getProcessedRecordCount());
        assertEquals(3, result.getCreatedRecordCount());

        List<Card> cards = dao.selectAll().from(myOtherClass).getCards();
        assertEquals(3, cards.size());
        assertEquals(set("ally", "bob", "charlie"), set(transform(cards, Card::getCode)));

        Card ally = dao.selectAll().from(myOtherClass).where(ATTR_CODE, EQ, "ally").getCard();
        assertEquals("AlllY", ally.getDescription());
        assertEquals(true, ally.get("BooleanAttr", Boolean.class));
        assertEquals("2019-02-23T09:13:34Z", toIsoDateTimeUtc(ally.get("DateTimeAttr", ZonedDateTime.class)));
        assertEquals(refTarget1.getId(), ally.getNotNull("ReferenceAttr", IdAndDescription.class).getId());
        assertEquals(refTarget1.getId(), ally.getNotNull("FkAttr", IdAndDescription.class).getId());
        assertNull(ally.get("OtherFkAttr", IdAndDescription.class));
        assertEquals("other", ally.getNotNull("LookupAttr", IdAndDescription.class).getCode());
        assertNull(ally.get("OtherLookupAttr", IdAndDescription.class));
        assertEquals((Double) 12.43d, ally.get("DoubleAttr", Double.class));

        Card bob = dao.selectAll().from(myOtherClass).where(ATTR_CODE, EQ, "bob").getCard();
        assertEquals("BOB", bob.getDescription());
        assertEquals(false, bob.get("BooleanAttr", Boolean.class));
        assertEquals("1988-12-09T23:00:00Z", toIsoDateTimeUtc(bob.get("DateTimeAttr", ZonedDateTime.class)));
        assertEquals(refTarget2.getId(), bob.getNotNull("ReferenceAttr", IdAndDescription.class).getId());
        assertNull(bob.get("FkAttr", IdAndDescription.class));
        assertNull(bob.get("OtherFkAttr", IdAndDescription.class));
        assertNull(bob.get("LookupAttr", IdAndDescription.class));
        assertNull(bob.get("OtherLookupAttr", IdAndDescription.class));
        assertEquals((Double) 0.33d, bob.get("DoubleAttr", Double.class));

        Card charlie = dao.selectAll().from(myOtherClass).where(ATTR_CODE, EQ, "charlie").getCard();
        assertEquals("CCCC", charlie.getDescription());
        assertNull(charlie.get("BooleanAttr", Boolean.class));
        assertNull(charlie.get("DateTimeAttr", ZonedDateTime.class));
        assertNull(charlie.get("ReferenceAttr", IdAndDescription.class));
        assertEquals(refTarget2.getId(), charlie.getNotNull("FkAttr", IdAndDescription.class).getId());
        assertEquals(refTarget1.getId(), charlie.getNotNull("OtherFkAttr", IdAndDescription.class).getId());
        assertEquals("something", charlie.getNotNull("LookupAttr", IdAndDescription.class).getCode());
        assertEquals("something", charlie.getNotNull("OtherLookupAttr", IdAndDescription.class).getCode());
        assertEquals((Double) 1.144d, charlie.get("DoubleAttr", Double.class));

        String csvExportData = readToString(service.exportDataWithTemplate(template));

        assertEquals(format("ally,AlllY,true,23/02/2019 10:13:34,%s,one,,other,,12.43\r\n"
                + "bob,BOB,false,10/12/1988 00:00:00,%s,,,,,0.33\r\n"
                + "charlie,CCCC,,,,two,unooo,something,SO ME THING,1.144\r\n", refTarget1.getId(), refTarget2.getId()), csvExportData);

        for (ImportExportFileFormat format : list(IEFF_XLSX, IEFF_XLS)) {
            ImportExportTemplateImpl xtemplate = ImportExportTemplateImpl.copyOf(template).withFileFormat(format).build();
            byte[] data = toByteArray(service.exportDataWithTemplate(xtemplate));
            assertTrue(data.length > 0);
            result = service.importDataWithTemplate(newDataSource(data, "application/octet-stream"), xtemplate);
            assertFalse(result.hasErrors());
            assertEquals(3, result.getProcessedRecordCount());
            assertEquals(0, result.getCreatedRecordCount());
            assertEquals(0, result.getDeletedRecordCount());
            assertEquals(0, result.getModifiedRecordCount());
            assertEquals(3, result.getUnmodifiedRecordCount());
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.math.BigDecimal;
import javax.activation.DataSource;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_ONE;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CharAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailTemplateService;
import org.cmdbuild.etl.ImportExportColumnConfigImpl;
import org.cmdbuild.etl.ImportExportColumnMode;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_CODE;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_DESCRIPTION;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_ID;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_CSV;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_XLS;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_XLSX;
import org.cmdbuild.etl.ImportExportMergeMode;
import org.cmdbuild.etl.ImportExportOperationResult;
import org.cmdbuild.etl.ImportExportService;
import org.cmdbuild.etl.ImportExportTemplate;
import org.cmdbuild.etl.ImportExportTemplateImpl;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_EXPORT;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_IMPORT;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_IMPORT_EXPORT;
import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_DATE_FORMAT_EXTJS;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_TIMEZONE;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_TIME_FORMAT_EXTJS;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class ImportExport2IT {

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

    private Classe myClass, myClass2;
    private ImportExportTemplate simpleTemplate;
    private LookupType myLookupType;
    private Domain domain;
    private Attribute attribute;
    private Card one, two;
    private CMRelation relation;

    public ImportExport2IT(DaoService dao, ImportExportService service, EmailTemplateService emailTemplateService, EmailAccountService emailAccountService, LookupService lookupService, SessionService sessionService, UserRepository userRepository, RoleRepository roleRepository, UserConfigService userConfigService) {
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

        myClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());
        myClass2 = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyThirdClass")).build());
        domain = dao.createDomain(DomainDefinitionImpl.builder().withSourceClass(myClass).withTargetClass(myClass2).withCardinality(ONE_TO_ONE).withName(tuid("MyDomain")).build());
        attribute = dao.createAttribute(AttributeImpl.builder().withOwner(domain).withName("ReferenceAttr").withType(new StringAttributeType()).build());
        domain = dao.getDomain(domain.getName());

        one = dao.create(CardImpl.buildCard(myClass, map(ATTR_CODE, "ally")));
        two = dao.create(CardImpl.buildCard(myClass2, map(ATTR_CODE, "bob")));

        relation = dao.create(RelationImpl.builder().withType(domain).withSourceCard(one).withTargetCard(two).addAttribute(attribute.getName(), "myAttrValue").build());
        myLookupType = lookupService.createLookupType(tuid("MyLookupType"));
        lookupService.createOrUpdateLookup(myLookupType, "one");
        lookupService.createOrUpdateLookup(myLookupType, "two");
        lookupService.createOrUpdateLookup(myLookupType, "three");

        dao.updateAttribute(AttributeImpl.copyOf(myClass.getAttribute(ATTR_CODE)).withClassOrderInMeta(1).build());
        dao.createAttribute(AttributeImpl.builder().withType(new StringAttributeType()).withName("StringAttr").withOwner(myClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new CharAttributeType()).withName("CharAttr").withOwner(myClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new IntegerAttributeType()).withName("IntegerAttr").withOwner(myClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new DoubleAttributeType()).withName("DoubleAttr").withOwner(myClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new DecimalAttributeType()).withName("DecimalAttr").withOwner(myClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new BooleanAttributeType()).withName("BooleanAttr").withOwner(myClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new LookupAttributeType(myLookupType)).withName("LookupAttr").withOwner(myClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new ReferenceAttributeType(domain, RD_DIRECT)).withName("ReferenceAttr").withOwner(myClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new StringAttributeType()).withName("ExtraValue").withOwner(myClass).build());
        myClass = dao.getClasse(myClass.getName());
        myClass2 = dao.getClasse(myClass2.getName());

        simpleTemplate = ImportExportTemplateImpl.builder()
                .withCode(tuid("myCsvTemplate"))
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("StringAttr").withColumnName("STRINGATTR").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("CharAttr").withColumnName("CHARATTR").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("IntegerAttr").withColumnName("INTEGERATTR").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("DoubleAttr").withColumnName("DOUBLEATTR").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("DecimalAttr").withColumnName("DECIMALATTR").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("BooleanAttr").withColumnName("BOOLEANATTR").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("LookupAttr").withColumnName("LOOKUPATTR").withMode(ImportExportColumnMode.IECM_CODE).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("ReferenceAttr").withColumnName("REFERENCEATTR").withMode(ImportExportColumnMode.IECM_ID).build()))
                .withTarget(myClass)
                .withType(IETT_IMPORT_EXPORT)
                .withFileFormat(IEFF_CSV)
                .withImportKeyAttribute("Code")
                .build();

        UserData user = userRepository.create(UserDataImpl.builder().withUsername(tuid("my_username")).build());//TODO improve user creation
        roleRepository.setUserGroupsByName(user.getId(), list("SuperUser"), null);//TODO improve user creation

        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired(user.getUsername()));

        userConfigService.setForCurrent(USER_CONFIG_TIMEZONE, "Europe/Rome");
        userConfigService.setForCurrent(USER_CONFIG_DATE_FORMAT_EXTJS, "d/m/Y");
        userConfigService.setForCurrent(USER_CONFIG_TIME_FORMAT_EXTJS, "H:i:s");
    }

    @Test
    public void testDomainTemplate() {
        ImportExportTemplate template = ImportExportTemplateImpl.builder()
                .withCode(tuid("myDomainTemplate"))
                .withColumns(list(
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdObj1").withColumnName("IDO1").withMode(IECM_CODE).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdObj2").withColumnName("IDO2").withMode(IECM_DESCRIPTION).build()))
                .withTarget(domain)
                .withType(IETT_IMPORT_EXPORT)
                .withFileFormat(IEFF_CSV)
                .build();
        service.create(template);
    }

    @Test
    public void testImportDomain() {
        ImportExportTemplate csvTemplate = ImportExportTemplateImpl.builder()
                .withCode(tuid("myCsvDomainTemplate"))
                .withColumns(list(
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdObj1").withColumnName("IDO1").withMode(IECM_ID).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdObj2").withColumnName("IDO2").withMode(IECM_ID).build()))
                .withTarget(domain)
                .withType(IETT_IMPORT_EXPORT)
                .withFileFormat(IEFF_CSV)
                .withImportKeyAttribute("IdDomain")
                .withCsvSeparator(";")
                .build();
        csvTemplate = service.create(csvTemplate);

        Card card1 = dao.create(CardImpl.buildCard(myClass, map(ATTR_CODE, "test1")));
        Card card2 = dao.create(CardImpl.buildCard(myClass2, map(ATTR_CODE, "test2")));

        String csvImportData = "IDO1;IDO2\n"
                + card1.getId() + ";" + card2.getId();
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), csvTemplate);
        assertFalse(result.hasErrors());
        CMRelation rel = dao.selectAll().from(domain).where("IdObj1", EQ, card1.getId()).getRelation();

        assertEquals(card1.getId(), rel.getSourceId());
        assertEquals(card2.getId(), rel.getTargetId());
        dao.delete(rel);
        dao.delete(myClass, card1.getId());
    }

    @Test
    @Ignore
    public void testTemplateCreation() {
        ImportExportTemplate csvTemplate = service.create(ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode(tuid("myCsvTemplate"))
                .withFileFormat(IEFF_CSV)
                .withCsvSeparator(";")
                .build());
        assertTrue(csvTemplate.getUseHeader());

        DataSource export = service.exportDataWithTemplate(csvTemplate.getId());
        assertEquals("text/csv", export.getContentType());
        assertThat(export.getName(), matchesPattern(".*.txt"));

        String content = readToString(export);
        assertThat(content, matchesPattern("(?s)CODE;DESCRIPTION;STRINGATTR;CHARATTR;INTEGERATTR;DOUBLEATTR;DECIMALATTR;BOOLEANATTR;LOOKUPATTR;REFERENCEATTR\r\n.*"));
    }

    @Test
    public void testImportFirstColumnEmpty() {
        importXlsSimple();
        Card card = dao.selectAll().from(myClass).where("Code", EQ, "test1").getCard();

        assertTrue(isNotBlank(card.getCode()));

        assertEquals("test111", card.get("StringAttr"));
        assertEquals("test11", card.get("Description"));
    }

    @Test
    public void testExportFirstColumnEmpty() throws IOException {
        importXlsSimple();
        ImportExportTemplate xlsExportTemplate = ImportExportTemplateImpl.builder()
                .withCode(tuid("myXlsTemplateEmptyColumnExport"))
                .withColumns(list(
                        ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("StringAttr").withColumnName("STRINGATTR").build()))
                .withTarget(myClass)
                .withType(IETT_EXPORT)
                .withFileFormat(IEFF_XLS)
                .withImportKeyAttribute("Code")
                .withFirstCol(2)
                .build();

        DataSource result = service.exportDataWithTemplate(xlsExportTemplate);

        HSSFWorkbook workbook = new HSSFWorkbook(result.getInputStream());
        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("", sheet.getRow(1).getCell(0, CREATE_NULL_AS_BLANK).getStringCellValue());
        assertEquals("ally", sheet.getRow(1).getCell(1).getStringCellValue());
    }

    @Test
    public void testExportXls() throws IOException {
        importXlsComplete();
        ImportExportTemplate xlsExportTemplate = ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode(tuid("myXlsTemplateEmptyColumnExport"))
                .withTarget(myClass)
                .withType(IETT_EXPORT)
                .withFileFormat(IEFF_XLS)
                .withImportKeyAttribute("Code")
                .build();

        DataSource result = service.exportDataWithTemplate(xlsExportTemplate);
        HSSFWorkbook workbook = new HSSFWorkbook(result.getInputStream());
        HSSFSheet sheet = workbook.getSheetAt(0);

        assertEquals("CODE", sheet.getRow(0).getCell(0).getStringCellValue());
        assertEquals("DESCRIPTION", sheet.getRow(0).getCell(1).getStringCellValue());
        assertEquals("STRINGATTR", sheet.getRow(0).getCell(2).getStringCellValue());
        assertEquals("CHARATTR", sheet.getRow(0).getCell(3).getStringCellValue());
        assertEquals("INTEGERATTR", sheet.getRow(0).getCell(4).getStringCellValue());
        assertEquals("DOUBLEATTR", sheet.getRow(0).getCell(5).getStringCellValue());
        assertEquals("DECIMALATTR", sheet.getRow(0).getCell(6).getStringCellValue());
        assertEquals("BOOLEANATTR", sheet.getRow(0).getCell(7).getStringCellValue());
        assertEquals("LOOKUPATTR", sheet.getRow(0).getCell(8).getStringCellValue());
        assertEquals("REFERENCEATTR", sheet.getRow(0).getCell(9).getStringCellValue());

        assertEquals("ally", sheet.getRow(1).getCell(0).getStringCellValue());
    }
    
    @Test
    public void testExportXls2() throws IOException {
        one = dao.create(CardImpl.buildCard(myClass, map(ATTR_CODE, "test", "StringAttr", "test","CharAttr","a","IntegerAttr",1)));
        myClass = dao.getClasse(myClass);
        importXlsComplete();
        ImportExportTemplate xlsExportTemplate = ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode(tuid("myXlsTemplateEmptyColumnExport"))
                .withTarget(myClass)
                .withType(IETT_EXPORT)
                .withFileFormat(IEFF_XLS)
                .withImportKeyAttribute("Code")
                .build();

        DataSource result = service.exportDataWithTemplate(xlsExportTemplate);
        HSSFWorkbook workbook = new HSSFWorkbook(result.getInputStream());
        HSSFSheet sheet = workbook.getSheetAt(0);

        assertEquals("CODE", sheet.getRow(0).getCell(0).getStringCellValue());
        assertEquals("DESCRIPTION", sheet.getRow(0).getCell(1).getStringCellValue());
        assertEquals("STRINGATTR", sheet.getRow(0).getCell(2).getStringCellValue());
        assertEquals("CHARATTR", sheet.getRow(0).getCell(3).getStringCellValue());
        assertEquals("INTEGERATTR", sheet.getRow(0).getCell(4).getStringCellValue());
        assertEquals("DOUBLEATTR", sheet.getRow(0).getCell(5).getStringCellValue());
        assertEquals("DECIMALATTR", sheet.getRow(0).getCell(6).getStringCellValue());
        assertEquals("BOOLEANATTR", sheet.getRow(0).getCell(7).getStringCellValue());
        assertEquals("LOOKUPATTR", sheet.getRow(0).getCell(8).getStringCellValue());
        assertEquals("REFERENCEATTR", sheet.getRow(0).getCell(9).getStringCellValue());

        assertEquals("ally", sheet.getRow(1).getCell(0).getStringCellValue());
        assertEquals("test", sheet.getRow(2).getCell(0).getStringCellValue());
        assertEquals("test", sheet.getRow(2).getCell(2).getStringCellValue());
        assertEquals("a", sheet.getRow(2).getCell(3).getStringCellValue());
        //assertEquals(1, sheet.getRow(2).getCell(4).getNumericCellValue()); TODO
    }

    @Test
    public void testImportCsvLookupCodeReferenceId() {
        Lookup lookup = lookupService.createOrUpdateLookup(myLookupType, "five");

        ImportExportTemplate csvTemplate = service.create(ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode(tuid("myCsvTemplate"))
                .withFileFormat(IEFF_CSV)
                .withCsvSeparator(";")
                .build());

        String csvImportData = "CODE;DESCRIPTION;STRINGATTR;CHARATTR;INTEGERATTR;DOUBLEATTR;DECIMALATTR;BOOLEANATTR;LOOKUPATTR;REFERENCEATTR\n"
                + "five;cinque5;test5;f;555;55.5;0.05;true;" + lookup.getCode() + ";" + relation.getTargetId();
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), csvTemplate);
        assertFalse(result.hasErrors());
        Card card = dao.selectAll().from(myClass).where("Code", EQ, "five").getCard();

        assertTrue(isNotBlank(card.getCode()));
        assertTrue(isNotBlank(card.getDescription()));

        assertEquals("five", card.getCode());
        assertEquals("cinque5", card.getDescription());
        assertEquals("test5", card.get("StringAttr"));
        assertEquals("f", card.get("CharAttr"));
        assertEquals(555, card.get("IntegerAttr"));
        assertEquals(55.5, card.get("DoubleAttr"));
        assertEquals(new BigDecimal("0.05"), card.get("DecimalAttr"));
        assertEquals(true, card.get("BooleanAttr"));
        assertEquals(lookup.getId(), ((IdAndDescriptionImpl) card.get("LookupAttr")).getId());
        assertEquals(relation.getTargetId(), ((IdAndDescriptionImpl) card.get("ReferenceAttr")).getId());
    }

    @Test
    public void testImportCsvLookupIdReferenceCode() {
        Lookup lookup = lookupService.createOrUpdateLookup(myLookupType, "four");

        ImportExportTemplate csvTemplate = ImportExportTemplateImpl.builder()
                .withCode(tuid("myCsvTemplate"))
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("LookupAttr").withColumnName("LOOKUPATTR").withMode(ImportExportColumnMode.IECM_ID).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("ReferenceAttr").withColumnName("REFERENCEATTR").withMode(ImportExportColumnMode.IECM_CODE).build()))
                .withTarget(myClass)
                .withType(IETT_IMPORT_EXPORT)
                .withFileFormat(IEFF_CSV)
                .withImportKeyAttribute("Code")
                .withCsvSeparator(";")
                .build();

        String csvImportData = "CODE;LOOKUPATTR;REFERENCEATTR\n"
                + "four;" + lookup.getId() + ";" + relation.getTargetCode();
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), csvTemplate);
        assertFalse(result.hasErrors());
        Card card = dao.selectAll().from(myClass).where("Code", EQ, "four").getCard();

        assertTrue(isNotBlank(card.getCode()));

        assertEquals("four", card.getCode());
        assertEquals(lookup.getId(), ((IdAndDescriptionImpl) card.get("LookupAttr")).getId());
        assertEquals(relation.getTargetId(), ((IdAndDescriptionImpl) card.get("ReferenceAttr")).getId());
    }

    @Test
    public void testMergeUpdateOnMissing() {
        ImportExportTemplate csvTemplate = service.create(ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode(tuid("myCsvMergeTemplate"))
                .withFileFormat(IEFF_CSV)
                .withCsvSeparator(";")
                .withMergeMode(ImportExportMergeMode.IEM_UPDATE_ATTR_ON_MISSING)
                .withAttributeNameForUpdateAttrOnMissing("ExtraValue")
                .withAttributeValueForUpdateAttrOnMissing("old_field")
                .build());

        dao.create(CardImpl.buildCard(myClass,
                map("Code", "one", "Description", "uno1", "StringAttr", "test 1", "CharAttr", "u", "IntegerAttr", "111", "DoubleAttr", "11.1",
                        "DecimalAttr", "1", "BooleanAttr", "true", "LookupAttr", "one", "ReferenceAttr", relation.getTargetId())));

        dao.create(CardImpl.buildCard(myClass,
                map("Code", "two", "Description", "uno1", "StringAttr", "test 1", "CharAttr", "u", "IntegerAttr", "111", "DoubleAttr", "11.1",
                        "DecimalAttr", "1", "BooleanAttr", "true", "LookupAttr", "one", "ReferenceAttr", relation.getTargetId())));

        String csvImportData = "CODE;DESCRIPTION;STRINGATTR;CHARATTR;INTEGERATTR;DOUBLEATTR;DECIMALATTR;BOOLEANATTR;LOOKUPATTR;REFERENCEATTR\n"
                + "two;asasd;test5;f;555;55.5;0.05;true;one;" + relation.getTargetId();

        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), csvTemplate);
        assertFalse(result.hasErrors());

        Card card = dao.selectAll().from(myClass).where("Code", EQ, "one").getCard();
        Card card2 = dao.selectAll().from(myClass).where("Code", EQ, "two").getCard();

        assertEquals("old_field", card.get("ExtraValue"));
        assertEquals("old_field", card.get("ExtraValue"));
    }

    @Test
    public void testMergeDeleteMissing() {
        ImportExportTemplate csvTemplate = service.create(ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode(tuid("myCsvMergeTemplate"))
                .withFileFormat(IEFF_CSV)
                .withCsvSeparator(";")
                .withMergeMode(ImportExportMergeMode.IEM_DELETE_MISSING)
                .build());

        dao.create(CardImpl.buildCard(myClass,
                map("Code", "one", "Description", "uno1", "StringAttr", "test 1", "CharAttr", "u", "IntegerAttr", "111", "DoubleAttr", "11.1",
                        "DecimalAttr", "0.01", "BooleanAttr", "true", "LookupAttr", "one", "ReferenceAttr", relation.getTargetId())));

        dao.create(CardImpl.buildCard(myClass,
                map("Code", "one1", "Description", "uno1", "StringAttr", "test 1", "CharAttr", "u", "IntegerAttr", "111", "DoubleAttr", "11.1",
                        "DecimalAttr", "0.02", "BooleanAttr", "true", "LookupAttr", "one", "ReferenceAttr", relation.getTargetId())));

        String csvImportData = "CODE;DESCRIPTION;STRINGATTR;CHARATTR;INTEGERATTR;DOUBLEATTR;DECIMALATTR;BOOLEANATTR;LOOKUPATTR;REFERENCEATTR\n"
                + "one1;asasd;test5;f;555;55.5;0.05;true;one;" + relation.getTargetId() + "\n"
                + "two;wutwut;test5;f;555;55.5;0.05;true;one;" + relation.getTargetId() + "\n";

        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), csvTemplate);

        assertFalse(result.hasErrors());
        assertEquals(2, dao.selectCount().from(myClass).getCount());

        Card card = dao.selectAll().from(myClass).where("Code", EQ, "one1").getCard();
        Card card2 = dao.selectAll().from(myClass).where("Code", EQ, "two").getCard();

        assertEquals("asasd", card.getDescription());
        assertEquals("wutwut", card2.getDescription());
    }

    @Test
    public void importXlsxSimple() {
        Classe xlsxClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassXlsx")).build());
        dao.createAttribute(AttributeImpl.builder().withType(new IntegerAttributeType()).withName("Attr1").withOwner(xlsxClass).build());
        dao.createAttribute(AttributeImpl.builder().withType(new IntegerAttributeType()).withName("Attr2").withOwner(xlsxClass).build());
        xlsxClass = dao.getClasse(xlsxClass);
        ImportExportTemplate xlsxTemplate = ImportExportTemplateImpl.builder()
                .withCode(tuid("myXlsxImportTemplate"))
                .withColumns(list(
                        ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Attr1").withColumnName("FIELD1").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Attr2").withColumnName("FIELD2").build()))
                .withTarget(xlsxClass)
                .withType(IETT_IMPORT)
                .withFileFormat(IEFF_XLSX)
                .withImportKeyAttribute("Code")
                .build();

        DataSource source = newDataSource(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/test_numeric.xlsx")), "application/octet-stream", "file.xlsx");
        ImportExportOperationResult result = service.importDataWithTemplate(source, xlsxTemplate);
        assertFalse(result.hasErrors());
        Card card = dao.selectAll().from(xlsxClass).where("Code", EQ, "Test1").getCard();
        assertEquals(10, card.get("Attr1"));
        assertEquals(100, card.get("Attr2"));
    }

    @Test
    @Ignore
    public void importXlsxSimple2() {
        Classe xlsxClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClassXlsx")).build());
        dao.createAttribute(AttributeImpl.builder().withType(new ReferenceAttributeType(domain, RD_DIRECT)).withName("ReferenceAttr").withOwner(xlsxClass).build());
        xlsxClass = dao.getClasse(xlsxClass);
        ImportExportTemplate xlsxTemplate = ImportExportTemplateImpl.builder()
                .withCode(tuid("myXlsxImportTemplate"))
                .withColumns(list(
                        ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("ReferenceAttr").withColumnName("FIELD").withMode(IECM_CODE).build()))
                .withTarget(xlsxClass)
                .withType(IETT_IMPORT)
                .withFileFormat(IEFF_XLSX)
                .withImportKeyAttribute("Code")
                .build();

        DataSource source = newDataSource(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/test_numeric.xlsx")), "application/octet-stream", "file.xlsx");
        ImportExportOperationResult result = service.importDataWithTemplate(source, xlsxTemplate);
        assertFalse(result.hasErrors());
        Card card = dao.selectAll().from(xlsxClass).where("Code", EQ, "Test1").getCard();
    }

    private void importXlsSimple() {
        ImportExportTemplate xlsTemplate = ImportExportTemplateImpl.builder()
                .withCode(tuid("myXlsTemplateEmptyColumnImport"))
                .withColumns(list(
                        ImportExportColumnConfigImpl.builder().withAttributeName("Code").withColumnName("CODE").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("Description").withColumnName("DESCRIPTION").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("StringAttr").withColumnName("STRINGATTR").build()))
                .withTarget(myClass)
                .withType(IETT_IMPORT)
                .withFileFormat(IEFF_XLS)
                .withImportKeyAttribute("Code")
                .withFirstCol(2)
                .build();

        DataSource source = newDataSource(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/testImport.xls")), "application/octet-stream", "file.xls");
        ImportExportOperationResult result = service.importDataWithTemplate(source, xlsTemplate);
        assertFalse(result.hasErrors());
    }

    private void importXlsComplete() {
        ImportExportTemplate xlsTemplate = ImportExportTemplateImpl.copyOf(simpleTemplate)
                .withCode(tuid("myXlsTemplateEmptyColumnImport"))
                .withTarget(myClass)
                .withType(IETT_IMPORT)
                .withFileFormat(IEFF_XLS)
                .withImportKeyAttribute("Code")
                .build();

        //If testImport_1.xls is modified care that the boolean values are not automatically converted to 1/0
        DataSource source = newDataSource(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/test/core/testImport_1.xls")), "application/octet-stream", "file.xls");
        ImportExportOperationResult result = service.importDataWithTemplate(source, xlsTemplate);
        assertFalse(result.hasErrors());
    }

}

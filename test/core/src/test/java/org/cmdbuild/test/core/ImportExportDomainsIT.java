/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.util.List;
import javax.activation.DataSource;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_MANY;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.etl.ImportExportColumnConfigImpl;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_CODE;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_DESCRIPTION;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_CSV;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_XLS;
import static org.cmdbuild.etl.ImportExportMergeMode.IEM_DELETE_MISSING;
import org.cmdbuild.etl.ImportExportOperationResult;
import org.cmdbuild.etl.ImportExportService;
import org.cmdbuild.etl.ImportExportTemplate;
import org.cmdbuild.etl.ImportExportTemplateImpl;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_EXPORT;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_IMPORT;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class ImportExportDomainsIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final ImportExportService service;

    private Classe one, two;
    private Card card1, card2;
    private Domain domain;
    private ImportExportTemplate template, template2;

    public ImportExportDomainsIT(DaoService dao, ImportExportService service) {
        this.dao = checkNotNull(dao);
        this.service = checkNotNull(service);
    }

    @Before
    public void init() {
        prepareTuid();

        one = dao.createClass(ClassDefinitionImpl.build(tuid("One")));
        two = dao.createClass(ClassDefinitionImpl.build(tuid("Two")));

        domain = dao.createDomain(DomainDefinitionImpl.builder()
                .withName(tuid("MyDomain1_"))
                .withSourceClass(one)
                .withTargetClass(two)
                .withCardinality(MANY_TO_MANY).build());

        card1 = dao.create(CardImpl.buildCard(one, map("Code", "one", "Description", "test1")));
        card2 = dao.create(CardImpl.buildCard(two, map("Code", "two", "Description", "test2")));

        template = ImportExportTemplateImpl.builder()
                .withCode("myCsvImportTemplate")
                .withColumns(list(
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdClass1").withColumnName("Class1").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdClass2").withColumnName("Class2").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdObj1").withColumnName("Obj1").withMode(IECM_DESCRIPTION).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdObj2").withColumnName("Obj2").withMode(IECM_CODE).build()))
                .withTarget(domain)
                .withCsvSeparator(",")
                .withType(IETT_IMPORT)
                .withMergeMode(IEM_DELETE_MISSING)
                .withFileFormat(IEFF_CSV)
                .build();

    }

    @Test
    public void testImportCsvDomainDescriptionCode() {

        String csvImportData = "Class1,Class2,Obj1,Obj2\r\n" + one.getName() + "," + two.getName() + "," + card1.getDescription() + "," + card2.getCode() + "\r\n";
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), template);

        List<CMRelation> relationList = dao.selectAll().from(domain).getRelations();
        assertEquals(1, relationList.size());
        assertEquals(one.getName(), relationList.get(0).getSourceClassName());
        assertEquals(two.getName(), relationList.get(0).getTargetClassName());
        assertEquals(card1.getId(), relationList.get(0).getSourceCard().getId());
        assertEquals(card2.getId(), relationList.get(0).getTargetCard().getId());
    }

    @Test
    public void testExportCsvDomainDescriptionCode() throws IOException {
        template2 = ImportExportTemplateImpl.builder()
                .withCode("myCsvExportTemplate")
                .withColumns(list(
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdClass1").withColumnName("Class1").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdClass2").withColumnName("Class2").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdObj1").withColumnName("Obj1").withMode(IECM_DESCRIPTION).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdObj2").withColumnName("Obj2").withMode(IECM_CODE).build()))
                .withTarget(domain)
                .withCsvSeparator(",")
                .withType(IETT_EXPORT)
                .withMergeMode(IEM_DELETE_MISSING)
                .withFileFormat(IEFF_CSV)
                .build();

        String csvImportData = "Class1,Class2,Obj1,Obj2\r\n" + one.getName() + "," + two.getName() + ",test1,two\r\n";
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), template);
        assertFalse(result.hasErrors());

        DataSource export = service.exportDataWithTemplate(template2);

        assertEquals("text/csv", export.getContentType());
        assertThat(export.getName(), matchesPattern(".*.csv"));

        String content = readToString(export);
        assertEquals("Class1,Class2,Obj1,Obj2\r\n" + one.getName() + "," + two.getName() + ",test1,two\r\n", content);
    }

    @Test
    public void testExportXlsDomainDescriptionCode() throws IOException {
        template2 = ImportExportTemplateImpl.builder()
                .withCode("myXlsExportTemplate")
                .withColumns(list(
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdClass1").withColumnName("Class1").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdClass2").withColumnName("Class2").build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdObj1").withColumnName("Obj1").withMode(IECM_DESCRIPTION).build(),
                        ImportExportColumnConfigImpl.builder().withAttributeName("IdObj2").withColumnName("Obj2").withMode(IECM_CODE).build()))
                .withTarget(domain)
                .withType(IETT_EXPORT)
                .withMergeMode(IEM_DELETE_MISSING)
                .withFileFormat(IEFF_XLS)
                .build();

        String csvImportData = "Class1,Class2,Obj1,Obj2\r\n" + one.getName() + "," + two.getName() + ",test1,two\r\n";
        ImportExportOperationResult result = service.importDataWithTemplate(newDataSource(csvImportData, "text/csv", "file.csv"), template);
        assertFalse(result.hasErrors());

        DataSource export = service.exportDataWithTemplate(template2);

        HSSFWorkbook workbook = new HSSFWorkbook(export.getInputStream());
        HSSFSheet sheet = workbook.getSheetAt(0);

        assertEquals("Class1", sheet.getRow(0).getCell(0).getStringCellValue());
        assertEquals("Class2", sheet.getRow(0).getCell(1).getStringCellValue());
        assertEquals("Obj1", sheet.getRow(0).getCell(2).getStringCellValue());
        assertEquals("Obj2", sheet.getRow(0).getCell(3).getStringCellValue());

        assertEquals(one.getName(), sheet.getRow(1).getCell(0).getStringCellValue());
        assertEquals(two.getName(), sheet.getRow(1).getCell(1).getStringCellValue());
        assertEquals("test1", sheet.getRow(1).getCell(2).getStringCellValue());
        assertEquals("two", sheet.getRow(1).getCell(3).getStringCellValue());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import javax.activation.DataHandler;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.data.filter.SorterElement.SorterElementDirection.DESC;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.SysReportService;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewImpl;
import org.cmdbuild.view.ViewService;
import org.cmdbuild.view.ViewType;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class PrintViewIT {

    private final DaoService dao;
    private final ViewService viewService;
    private final SysReportService reportService;
    private final CacheService cacheService;

    private Classe myClass, myClass2;

    public PrintViewIT(DaoService dao, ViewService viewService, SysReportService reportService, CacheService cacheService) {
        this.dao = checkNotNull(dao);
        this.viewService = checkNotNull(viewService);
        this.reportService = checkNotNull(reportService);
        this.cacheService = checkNotNull(cacheService);
    }

    @Before
    public void init() {
        prepareTuid();

        myClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());
        myClass2 = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MySuperClass")).build());
        myClass = dao.getClasse(myClass.getName());

        dao.create(CardImpl.buildCard(myClass, ATTR_CODE, "one", ATTR_DESCRIPTION, "uno"));
        dao.create(CardImpl.buildCard(myClass, ATTR_CODE, "two", ATTR_DESCRIPTION, "due"));
        dao.create(CardImpl.buildCard(myClass, ATTR_CODE, "three", ATTR_DESCRIPTION, "tre"));

        dao.create(CardImpl.buildCard(myClass2, ATTR_CODE, "oneS", ATTR_DESCRIPTION, "unoS"));
        dao.create(CardImpl.buildCard(myClass2, ATTR_CODE, "twoS", ATTR_DESCRIPTION, "dueS"));
        dao.create(CardImpl.buildCard(myClass2, ATTR_CODE, "threeS", ATTR_DESCRIPTION, "treS"));
    }

    @Test
    public void testViewPrintCSVWithFilter() {
        View view = viewService.create(ViewImpl.builder().withName(tuid("MyView")).withSourceClass(myClass.getName())
                .withType(ViewType.FILTER)
                .withFilter(CmdbFilterUtils.serializeFilter(AttributeFilterConditionImpl.in(ATTR_CODE, "three", "two").toAttributeFilter().toCmdbFilters())).build());

        DataHandler printData = reportService.executeViewReport(view, ReportFormat.CSV, DaoQueryOptionsImpl.builder().orderBy(ATTR_CODE, DESC).build());
        assertEquals("text/plain", printData.getContentType());

        String printStr = readToString(printData);
        assertEquals("Code;Description\nthree;tre\ntwo;due\n", printStr);
    }

    @Test
    public void testViewPrintPDFWithFilter() {
        View view = viewService.create(ViewImpl.builder().withName(tuid("MyView")).withSourceClass(myClass.getName())
                .withType(ViewType.FILTER)
                .withFilter(CmdbFilterUtils.serializeFilter(AttributeFilterConditionImpl.in(ATTR_CODE, "three", "two").toAttributeFilter().toCmdbFilters())).build());

        DataHandler printData = reportService.executeViewReport(view, ReportFormat.PDF, DaoQueryOptionsImpl.builder().orderBy(ATTR_CODE, DESC).build());
        assertEquals("application/pdf", printData.getContentType());
    }

    @Test
    public void testViewPrintSuperClassCSVWithFilter() {
        View view = viewService.create(ViewImpl.builder().withName(tuid("MyView")).withSourceClass(myClass2.getName())
                .withType(ViewType.FILTER)
                .withFilter(CmdbFilterUtils.serializeFilter(AttributeFilterConditionImpl.in(ATTR_CODE, "threeS", "twoS").toAttributeFilter().toCmdbFilters())).build());

        DataHandler printData = reportService.executeViewReport(view, ReportFormat.CSV, DaoQueryOptionsImpl.builder().orderBy(ATTR_CODE, DESC).build());
        assertEquals("text/plain", printData.getContentType());

        String printStr = readToString(printData);
        assertEquals("Code;Description\nthreeS;treS\ntwoS;dueS\n", printStr);
    }

    @Test
    public void testViewPrintSuperClassPDFWithFilter() {
        View view = viewService.create(ViewImpl.builder().withName(tuid("MyView")).withSourceClass(myClass2.getName())
                .withType(ViewType.FILTER)
                .withFilter(CmdbFilterUtils.serializeFilter(AttributeFilterConditionImpl.in(ATTR_CODE, "threeS", "twoS").toAttributeFilter().toCmdbFilters())).build());

        DataHandler printData = reportService.executeViewReport(view, ReportFormat.PDF, DaoQueryOptionsImpl.builder().orderBy(ATTR_CODE, DESC).build());
        assertEquals("application/pdf", printData.getContentType());
    }

    @Test
    public void testFunctionViewPrintCSV() {
        dao.getJdbcTemplate().execute(format(
                "CREATE OR REPLACE FUNCTION %s(\n"
                + "	OUT \"Description\" character varying,\n"
                + "	OUT \"Code\" character varying\n"
                + ")\n"
                + "RETURNS SETOF record AS\n"
                + "$BODY$\n"
                + "BEGIN\n"
                + "RETURN QUERY EXECUTE 'SELECT \"Code\",\"Description\" FROM \"%s\"';\n"
                + "END\n"
                + "$BODY$\n"
                + "LANGUAGE plpgsql VOLATILE;\n"
                + "\n"
                + "COMMENT ON FUNCTION %s() IS 'TYPE: function';",
                "test_function_view", myClass2.getName(), "test_function_view"));
        cacheService.invalidateAll();
        View view = viewService.create(ViewImpl.builder().withName(tuid("MyView")).withSourceFunction("test_function_view").withType(ViewType.SQL).build());

        DataHandler printData = reportService.executeViewReport(view, ReportFormat.CSV, DaoQueryOptionsImpl.builder().build());
        assertEquals("text/plain", printData.getContentType());

        String printStr = readToString(printData);
        assertEquals("Description;Code\noneS;unoS\ntwoS;dueS\nthreeS;treS\n", printStr);
    }

    @Test
    public void testFunctionViewPrintPDF() {
        dao.getJdbcTemplate().execute(format(
                "CREATE OR REPLACE FUNCTION %s(\n"
                + "	OUT \"Description\" character varying,\n"
                + "	OUT \"Code\" character varying\n"
                + ")\n"
                + "RETURNS SETOF record AS\n"
                + "$BODY$\n"
                + "BEGIN\n"
                + "RETURN QUERY EXECUTE 'SELECT \"Code\",\"Description\" FROM \"%s\"';\n"
                + "END\n"
                + "$BODY$\n"
                + "LANGUAGE plpgsql VOLATILE;\n"
                + "\n"
                + "COMMENT ON FUNCTION %s() IS 'TYPE: function';",
                "test_function_view", myClass2.getName(), "test_function_view"));
        cacheService.invalidateAll();
        View view = viewService.create(ViewImpl.builder().withName(tuid("MyView")).withSourceFunction("test_function_view").withType(ViewType.SQL).build());

        DataHandler printData = reportService.executeViewReport(view, ReportFormat.PDF, DaoQueryOptionsImpl.builder().build());
        assertEquals("application/pdf", printData.getContentType());
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.activation.DataHandler;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import static org.cmdbuild.data.filter.SorterElement.SorterElementDirection.DESC;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
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
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class PrintIT {

    private final DaoService dao;
    private final ViewService viewService;
    private final SysReportService reportService;

    private Classe myClass;

    public PrintIT(DaoService dao, ViewService viewService, SysReportService reportService) {
        this.dao = checkNotNull(dao);
        this.viewService = checkNotNull(viewService);
        this.reportService = checkNotNull(reportService);
    }

    @Before
    public void init() {
        prepareTuid();

        myClass = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyClass")).build());
        myClass = dao.getClasse(myClass.getName());

        dao.create(CardImpl.buildCard(myClass, ATTR_CODE, "one", ATTR_DESCRIPTION, "uno"));
        dao.create(CardImpl.buildCard(myClass, ATTR_CODE, "two", ATTR_DESCRIPTION, "due"));
        dao.create(CardImpl.buildCard(myClass, ATTR_CODE, "three", ATTR_DESCRIPTION, "tre"));
    }

    @Test
    public void testClassPrint() {
        DataHandler printData = reportService.executeClassReport(myClass, ReportFormat.CSV, DaoQueryOptionsImpl.builder().orderBy(ATTR_CODE, DESC).build());
        assertEquals("text/plain", printData.getContentType());

        String printStr = readToString(printData);
        assertEquals("Code;Description\nthree;tre\ntwo;due\none;uno\n", printStr);
    }

    @Test
    public void testCardPrint() {
        Card card = dao.create(CardImpl.buildCard(myClass, ATTR_CODE, tuid("myCard")));

        DataHandler printData = reportService.executeCardReport(card, ReportFormat.CSV);
        assertEquals("text/plain", printData.getContentType());

        String printStr = readToString(printData);
        assertThat(printStr, matchesPattern("(?s).*Code\\s:\\s" + card.getCode() + ".*"));
        assertThat(printStr, matchesPattern("(?s).*" + myClass.getName() + ".*"));
    }

    @Test
    public void testCardPrint2() {
        Classe classe = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("MyOtherClass")).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(classe)
                .withName("MyAttr").withDescription("Attribute \"virgolette\"").withType(new StringAttributeType()).build());
        classe = dao.getClasse(classe.getName());
        Card card = dao.create(CardImpl.buildCard(classe, ATTR_CODE, tuid("myCard")));

        DataHandler printData = reportService.executeCardReport(card, ReportFormat.PDF);
        assertEquals("application/pdf", printData.getContentType());
    }

    @Test
    public void testViewCardPrint() {
        View view = viewService.create(ViewImpl.builder().withName(tuid("MyView")).withSourceClass(myClass.getName())
                .withType(ViewType.FILTER)
                .withFilter(CmdbFilterUtils.serializeFilter(AttributeFilterConditionImpl.in(ATTR_CODE, "three", "two").toAttributeFilter().toCmdbFilters())).build());

        DataHandler printData = reportService.executeViewReport(view, ReportFormat.CSV, DaoQueryOptionsImpl.builder().orderBy(ATTR_CODE, DESC).build());
        assertEquals("text/plain", printData.getContentType());

        String printStr = readToString(printData);
        assertEquals("Code;Description\nthree;tre\ntwo;due\n", printStr);
    }

}

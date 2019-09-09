/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner.sys;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Lists.transform;
import java.awt.Color;
import org.cmdbuild.dao.function.StoredFunction;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import static java.lang.String.format;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRCommonText;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRSection;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignStaticText;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import static org.apache.commons.lang3.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.common.localization.LanguageService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecordValues;
import org.cmdbuild.dao.beans.DatabaseRecordValuesImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.STRING;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.report.ReportException;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.SysReportService;
import org.cmdbuild.report.inner.ReportHelper;
import static org.cmdbuild.report.inner.utils.ReportUtils.compileAndFillReport;
import static org.cmdbuild.report.inner.utils.ReportUtils.getBands;
import static org.cmdbuild.report.inner.utils.ReportUtils.loadReportFromResources;
import static org.cmdbuild.report.inner.utils.ReportUtils.loadReportImageParamsFromResourcesAndFixReport;
import org.cmdbuild.userconfig.UserPrefHelper;
import org.cmdbuild.userconfig.UserPreferencesService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewAccessService;
import org.cmdbuild.view.ViewType;

@Component
public class SysReportServiceImpl implements SysReportService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final UserClassService classService;
    private final LookupService lookupService;
    private final LanguageService languageService;
    private final UserPreferencesService userPreferencesService;
    private final ReportHelper reportHelper;
    private final ViewAccessService viewAccessService;
    private final UserDomainService domainService;

    public SysReportServiceImpl(UserClassService classService, DaoService dao, LanguageService languageService, UserPreferencesService userPreferencesService, ReportHelper reportHelper, ViewAccessService viewAccessService, UserDomainService domainService, LookupService lookupService) {
        this.dao = checkNotNull(dao);
        this.languageService = checkNotNull(languageService);
        this.userPreferencesService = checkNotNull(userPreferencesService);
        this.reportHelper = checkNotNull(reportHelper);
        this.viewAccessService = checkNotNull(viewAccessService);
        this.domainService = checkNotNull(domainService);
        this.classService = checkNotNull(classService);
        this.lookupService = checkNotNull(lookupService);
    }

    @Override
    public DataHandler executeClassSchemaReport(Classe classe, ReportFormat format) {
        try {
            logger.debug("execute class report for class = {} format = {}", classe, format);
            JasperPrint jasperPrint = new ClassSchemaReportHelper(classe).runReport();
            logger.trace("export report to format = {}", format);
            return reportHelper.exportReport(jasperPrint, classe.getName(), format);
        } catch (JRException ex) {
            throw new ReportException(ex, "error building report structure for class = %s", classe);
        }
    }

    @Override
    public DataHandler executeSchemaReport(ReportFormat format) {
        try {
            JasperPrint jasperPrint = new SchemaReportHelper().runReport();
            logger.trace("export report to format = {}", format);
            return reportHelper.exportReport(jasperPrint, "schema_report", format);
        } catch (JRException ex) {
            throw new ReportException(ex, "error building report structure for the schema");
        }
    }

    @Override
    public DataHandler executeClassReport(Classe classe, ReportFormat format, DaoQueryOptions queryOptions) {
        try {
            logger.debug("execute class report for class = {} format = {}", classe, format);
            JasperPrint jasperPrint = new ClassOrViewReportHelper(
                    classe,
                    classe.getName(),
                    classe.getAllAttributes(),
                    queryOptions,
                    (attrs) -> dao.select(list(transform(attrs, Attribute::getName))).from(classe).withOptions(queryOptions).getCards())
                    .runReport(equal(format, ReportFormat.CSV));
            logger.trace("export report to format = {}", format);
            return reportHelper.exportReport(jasperPrint, classe.getName(), format);
        } catch (JRException ex) {
            throw new ReportException(ex, "error building report structure for class = %s", classe);
        }
    }

    @Override
    public DataHandler executeCardReport(Card card, ReportFormat format) {
        try {
            logger.debug("execute card report for card = {} format = {}", card, format);
            JasperPrint jasperPrint = new CardReportHelper(card).runReport();
            logger.trace("export report to format = {}", format);
            return reportHelper.exportReport(jasperPrint, format("%s_%s", card.getClassName(), card.getId()), format);
        } catch (JRException ex) {
            throw new ReportException(ex, "error building report structure for card = %s", card);
        }
    }

    @Override
    public DataHandler executeViewReport(View view, ReportFormat format, DaoQueryOptions queryOptions) {
        try {
            logger.debug("execute view report for view = {} format = {}", view, format);
            JasperPrint jasperPrint = new JasperPrint();
            if (view.isOfType(ViewType.SQL)) {
                StoredFunction function = getFunction(view.getSourceFunction());
                List<DatabaseRecordValues> values = list();
                viewAccessService.getRecords(view, queryOptions).elements().forEach((e) -> values.add(new DatabaseRecordValuesImpl(e)));
                jasperPrint = new ClassOrViewReportHelper(
                        view,
                        view.getName(),
                        function.getAllAttributes(),
                        queryOptions,
                        (attrs) -> values)
                        .runReport(equal(format, ReportFormat.CSV));
            } else if (view.isOfType(ViewType.FILTER)) {
                jasperPrint = new ClassOrViewReportHelper(
                        view,
                        view.getName(),
                        dao.getClasse(view.getSourceClass()).getAllAttributes(),
                        queryOptions,
                        (attrs) -> viewAccessService.getCards(view, queryOptions).elements())//TODO query only required attrs (?)
                        //                            dao.select(list(transform(attrs, Attribute::getName))).from(classe).withOptions(queryOptions).getCards())
                        .runReport(equal(format, ReportFormat.CSV));
            }
            logger.trace("export report to format = {}", format);
            return reportHelper.exportReport(jasperPrint, view.getName(), format);
        } catch (JRException ex) {
            throw new ReportException(ex, "error building report structure for view = %s", view);
        }
    }

    private StoredFunction getFunction(String idOrName) {
        checkNotBlank(idOrName);
        if (isNumber(idOrName)) {
            return dao.getFunctionById(Long.valueOf(idOrName));
        } else {
            return dao.getFunctionByName(idOrName);
        }
    }

    private Map<String, Object> getCommonParams() {
        return map(
                JRParameter.REPORT_LOCALE, languageService.getRequestLocale()
        );
    }

    private static int getSizeFromAttribute(Attribute attr) {
        switch (attr.getType().getName()) {
            case BOOLEAN:
                return 4;
            case REGCLASS:
            case REFERENCE:
            case INET:
            case LOOKUP:
            case FOREIGNKEY:
            case TIME:
            case STRINGARRAY:
                return 20;
            case DECIMAL:
            case DOUBLE:
            case INTEGER:
            case LONG:
                return 8;
            case TEXT:
                return 50;
            case TIMESTAMP:
                return 16;
            case DATE:
                return 10;
            case STRING:
                if (attr.getType().as(StringAttributeType.class).hasLength()) {
                    return min(max(4, attr.getType().as(StringAttributeType.class).getLength()), 40);
                } else {
                    return 40;
                }
            default:
                return 0;
        }

    }

    private class ClassOrViewReportHelper extends GenericReportHelper {

        private final Object subject;
        private final String subjectName;
        private final List<Attribute> attrs;
        private final Function<List<Attribute>, List<? extends DatabaseRecordValues>> dataSupplier;

        private int x, y;

        public ClassOrViewReportHelper(Object subject, String subjectName, Collection<Attribute> attributes, DaoQueryOptions queryOptions, Function<List<Attribute>, List<? extends DatabaseRecordValues>> dataSupplier) {
            this.subject = checkNotNull(subject);
            this.subjectName = checkNotBlank(subjectName);
            this.dataSupplier = checkNotNull(dataSupplier);

            if (queryOptions.hasAttrs()) {
                Map<String, Attribute> attrsByName = map(attributes, Attribute::getName);
                attrs = queryOptions.getAttrs().stream().map(n -> checkNotNull(attrsByName.get(n), "attribute not found for name =< %s >", n)).collect(toImmutableList());
                attrs.forEach(a -> checkArgument(a.hasServiceReadPermission(), "permission denied for attribute = %s", a));
            } else {
                attrs = attributes.stream().filter(Attribute::showInGrid).filter(Attribute::hasUiReadPermission).collect(toImmutableList());
            }
        }

        public JasperPrint runReport(boolean csvReport) throws JRException {
            logger.debug("run report for subject = {}, selected attributes = {}", subject, transform(attrs, Attribute::getName));
            JasperDesign jasperDesign = loadReportFromResources(csvReport ? "CMDBuild_list_csv.jrxml" : "CMDBuild_list.jrxml");
            Map<String, Object> imageParams = loadReportImageParamsFromResourcesAndFixReport(jasperDesign);

            attrs.forEach(rethrowConsumer(a -> jasperDesign.addField(buildFieldForAttr(a))));

            int sumOfWeights = attrs.stream().mapToInt(a -> getSizeFromAttribute(a)).sum();
            int height = 17;
            int pageWidth = jasperDesign.getPageWidth();

            JRBand detailBand = jasperDesign.getDetailSection().getBands()[0];

            x = 0;
            y = 2;
            attrs.forEach(a -> {
                int width = getSizeFromAttribute(a) * pageWidth * 95 / sumOfWeights / 100;
                logger.trace("add col for attr = {} with width = {}", a.getName(), width);
                JRDesignTextField textField = createTextFieldForAttribute(a, false);
                textField.setX(x);
                textField.setY(y);
                textField.setWidth(width);
                textField.setHeight(height);
                textField.setBlankWhenNull(true);
                textField.setStretchWithOverflow(true);
                detailBand.getChildren().add(textField);
                x += width;
            });

            JRBand columnHeader = jasperDesign.getColumnHeader();

            x = 0;
            attrs.forEach(a -> {
                int width = getSizeFromAttribute(a) * pageWidth * 95 / sumOfWeights / 100;
                logger.trace("add col header for attr = {} with width = {}", a.getName(), width);
                JRDesignStaticText staticText = new JRDesignStaticText();
                staticText.setText(a.getDescriptionOrName());
                staticText.setForecolor(Color.WHITE);
                staticText.setY(0);
                staticText.setX(x);
                staticText.setHeight(height);
                staticText.setWidth(width);
                columnHeader.getChildren().add(staticText);
                x += width;
            });

            List data
                    = //                    dao.select(list(transform(attrs, Attribute::getName))).from(classe).withOptions(queryOptions).getCards()
                    dataSupplier.apply(attrs).stream().map(c -> map().accept(m -> {
                attrs.forEach(a -> m.put(getFieldNameForAttr(a), getReportValueFromCard(c, a)));
                logger.trace("loaded card = {} with data = \n\n{}\n", c, mapToLoggableStringLazy((Map) m));
            })).collect(toList()); //TODO filter param, sorter param

            Map<String, Object> params = map(getCommonParams()).with(imageParams).with(
                    "Card_List_Title", subjectName
            );

            return compileAndFillReport(jasperDesign, params, new JRMapCollectionDataSource(data));
        }
    }

    private class ClassSchemaReportHelper extends GenericReportHelper {

        private final Classe classe;
        private final List<Attribute> attrs;

        public ClassSchemaReportHelper(Classe classe) {
            this.classe = checkNotNull(classe);
            attrs = classe.getAllAttributes().stream().filter(Attribute::showInGrid).filter(Attribute::hasUiReadPermission).collect(toImmutableList());
        }

        public JasperPrint runReport() throws JRException {
            List<Domain> domains = domainService.getUserDomains().stream().filter((d) -> d.isDomainForClasse(classe)).collect(toList());
            JasperDesign jasperDesign = loadReportFromResources("CMDBuild_class_schema.jrxml");
            Map<String, Object> imageParams = loadReportImageParamsFromResourcesAndFixReport(jasperDesign);

            List domainList = domains.stream().map(a -> map(
                    "domainname", a.getName(),
                    "domainclass1", a.getSourceClass().getName(),
                    "domainclass2", a.getTargetClass().getName(),
                    "domaincardinality", a.getCardinality()
            )).collect(toList());

            List attributeList = attrs.stream().filter(Attribute::hasUiReadPermission).map(a -> map(
                    "attributename", a.getName(),
                    "attributetype", a.getType().getName().toString(),
                    "attributelength", a.isOfType(STRING) ? a.getType().as(StringAttributeType.class).getLength() : 0,
                    "attributenotnull", a.isMandatory(),
                    "attributeunique", a.isUnique(),
                    "attributelookup", a.isOfType(LOOKUP) ? a.getType().as(LookupAttributeType.class).getLookupTypeName() : "",
                    "attributereferencedomain", a.isOfType(REFERENCE) ? a.getType().as(ReferenceAttributeType.class).getDomainName() : ""
            )).collect(toList());
            Map<String, Object> d = map(
                    "classData", map(
                            "classname", classe.getName(),
                            "classdescription", classe.getDescription(),
                            "classisprocess", classe.isProcess(),
                            "classsuperclass", classe.getParentOrNull(),
                            "classissuperclass", classe.isSuperclass()
                    ),
                    "attributeList", new JRBeanCollectionDataSource(attributeList, false),
                    "domainList", new JRBeanCollectionDataSource(domainList, false)
            );

            return compileAndFillReport(jasperDesign, map(getCommonParams()).with(imageParams), new JRMapCollectionDataSource(list(d)));
        }
    }

    private class SchemaReportHelper extends GenericReportHelper {

        public JasperPrint runReport() throws JRException {
            JasperDesign jasperDesign = loadReportFromResources("CMDBuild_database_schema.jrxml");
            Map<String, Object> imageParams = loadReportImageParamsFromResourcesAndFixReport(jasperDesign);

            List<Classe> classes = classService.getAllUserClasses();

            List classList = classes.stream().map(a -> map(
                    "classname", a.getName(),
                    "classdescription", a.getDescription(),
                    "isprocess", a.isProcess(),
                    "issuperclass", a.isSuperclass(),
                    "superclass", a.getParentOrNull(),
                    "attributes", new JRBeanCollectionDataSource(a.getAllAttributes().stream().map(
                            f -> map(
                                    "name", f.getName(),
                                    "type", f.getType().getName().toString(),
                                    "length", f.isOfType(STRING) ? f.getType().as(StringAttributeType.class).getLength() : 0,
                                    "notnull", f.isMandatory(),
                                    "unique", f.isUnique(),
                                    "lookup", f.isOfType(LOOKUP) ? f.getType().as(LookupAttributeType.class).getLookupTypeName() : "",
                                    "reference", f.isOfType(REFERENCE) ? f.getType().as(ReferenceAttributeType.class).getDomainName() : ""
                            )).collect(toList()), false))).collect(toList());

            List lookupklist = lookupService.getAllTypes().stream().map(a -> map(
                    "lookuptype", a.getName(),
                    "values", new JRBeanCollectionDataSource(lookupService.getAllLookup(a.getName()).stream().map(
                            f -> map(
                                    "lookupname", f.getCode(),
                                    "lookupdesc", f.getDescription()
                            )).collect(toList()), false)
            )).collect(toList());

            Map<String, Object> data = map(
                    "classList", new JRBeanCollectionDataSource(classList, false),
                    "classList2", new JRBeanCollectionDataSource(classList, false),
                    "lookuplist", new JRBeanCollectionDataSource(lookupklist, false)
            );

            return compileAndFillReport(jasperDesign, map(getCommonParams()).with(imageParams), new JRMapCollectionDataSource(list(data)));
        }

    }

    private class CardReportHelper extends GenericReportHelper {

        private final Card card;

        private int width, height, x, y;

        public CardReportHelper(Card card) {
            this.card = checkNotNull(card);
        }

        public JasperPrint runReport() throws JRException {
            JasperDesign jasperDesign = loadReportFromResources("CMDBuild_card_detail.jrxml");
            Map<String, Object> imageParams = loadReportImageParamsFromResourcesAndFixReport(jasperDesign);
            Classe classe = card.getType();
            Map<String, Object> data = map();

            JRSection detailSection = jasperDesign.getDetailSection();
            JRDesignBand detailBand = (JRDesignBand) detailSection.getBands()[0];
            x = 0;
            y = 0;
            width = jasperDesign.getPageWidth() - 30 * 2;
            height = 20;

            classe.getAllAttributes().stream().filter(Attribute::hasUiReadPermission).forEach(rethrowConsumer(a -> {
                Object value = getReportValueFromCard(card, a);
                data.put(getFieldNameForAttr(a), value);
                jasperDesign.addField(buildFieldForAttr(a));

                JRDesignTextField textField = createTextFieldForAttribute(a, true);
                textField.setHeight(height);
                textField.setWidth(width);
                textField.setX(x);
                textField.setY(y);
                detailBand.getChildren().add(textField);

                y += 20;
            }));

            int detailHeight = y + 5;
            detailBand.setHeight(detailHeight);

            int totalBandsHeight = getBands(jasperDesign).stream().mapToInt(JRBand::getHeight).sum();
            if (totalBandsHeight > jasperDesign.getPageHeight()) {
                jasperDesign.setPageHeight(totalBandsHeight);
            }
            logger.trace("report data = \n\n{}\n", mapToLoggableStringLazy(data));

            Map<String, Object> params = map(getCommonParams()).with(imageParams).with(
                    "Card_Detail_Title", format("%s - %s", card.getClassName(), card.getDescription())
            );

            logger.trace("report params = \n\n{}\n", mapToLoggableStringLazy(params));

            return compileAndFillReport(jasperDesign, params, new JRMapCollectionDataSource(list(data)));
        }

    }

    private JRDesignTextField createTextFieldForAttribute(Attribute attribute, boolean addLabel) {
        String expr = format("$F{%s#%s}", attribute.getOwner().getName(), attribute.getName());
        if (addLabel) {
            expr = format("msg(\"%s : {0}\", %s == null ? \"\" : %s)", escapeJava(attribute.getDescriptionOrName()), expr, expr);
        }
        JRDesignExpression varExpr = new JRDesignExpression();
        varExpr.setText(expr);
        JRDesignTextField field = new JRDesignTextField();
        field.setExpression(varExpr);
        field.setBlankWhenNull(true);
        field.setStretchWithOverflow(true);
        field.setForecolor(Color.BLACK);
        field.setBackcolor(Color.GRAY);
        field.setPositionType(PositionTypeEnum.FLOAT);
        field.setMarkup(JRCommonText.MARKUP_HTML);
        field.setX(0);
        field.setY(0);
        return field;
    }

    private static Class getJavaClassForReportFromAttribute(Attribute attr) {
        switch (attr.getType().getName()) {
            case BOOLEAN:
                return Boolean.class;
            case CHAR:
                return Character.class;
            case REFERENCE:
            case FOREIGNKEY:
            case LOOKUP:
            case INET:
            case STRING:
            case TEXT:
            case TIME:
            case TIMESTAMP:
            case DATE:
            case DECIMAL:
            case DOUBLE:
                return String.class;
            case INTEGER:
                return Integer.class;
            case STRINGARRAY:
                return String[].class;
            default:
                return Object.class;//TODO check this
        }
    }

    private abstract class GenericReportHelper {

        private final UserPrefHelper userPrefHelper = userPreferencesService.getUserPreferencesHelper();

        @Nullable
        protected final Object getReportValueFromCard(DatabaseRecordValues card, Attribute attr) {
            Class type = getJavaClassForReportFromAttribute(attr);
            switch (attr.getType().getName()) {
                case LOOKUP:
                case REFERENCE:
                case FOREIGNKEY:
                    return card.getDescriptionOf(attr.getName());
                case DATE:
                    return userPrefHelper.serializeDate(card.get(attr.getName(), LocalDate.class));
                case TIMESTAMP:
                    return userPrefHelper.serializeDateTime(card.get(attr.getName(), ZonedDateTime.class));
                case TIME:
                    return userPrefHelper.serializeTime(card.get(attr.getName(), LocalTime.class));
                case DECIMAL:
                case DOUBLE:
                    return userPrefHelper.serializeNumber(card.get(attr.getName(), Number.class));
                default:
                    return card.get(attr.getName(), type);
            }
        }
    }

    private static String getFieldNameForAttr(Attribute attr) {
        return format("%s#%s", attr.getOwner().getName(), attr.getName());
    }

    private static JRDesignField buildFieldForAttr(Attribute attr) {
        JRDesignField field = new JRDesignField();
        field.setName(getFieldNameForAttr(attr));
        field.setDescription(attr.getDescriptionOrName());
        field.setValueClass(getJavaClassForReportFromAttribute(attr));
        return field;
    }
}

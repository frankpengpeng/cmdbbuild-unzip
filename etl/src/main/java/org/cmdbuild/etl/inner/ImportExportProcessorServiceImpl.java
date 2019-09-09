/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.inner;

import org.cmdbuild.userconfig.UserPrefHelper;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Maps.transformEntries;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import static java.util.stream.Collectors.toList;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import static org.apache.commons.codec.binary.Base64.encodeBase64;
import static org.apache.commons.codec.binary.StringUtils.newStringUsAscii;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import static org.apache.poi.ss.usermodel.CellType.BLANK;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.cmdbuild.classe.access.UserCardAccess;
import org.cmdbuild.classe.access.UserCardService;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardIdAndClassName;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.IdAndDescription;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.LOOKUP;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.TIMESTAMP;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.ImportExportColumnConfig;
import org.cmdbuild.etl.ImportExportColumnConfigImpl;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_CODE;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_DESCRIPTION;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_ID;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_IGNORE;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_CSV;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_XLS;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_XLSX;
import static org.cmdbuild.etl.ImportExportMergeMode.IEM_LEAVE_MISSING;
import org.cmdbuild.etl.ImportExportOperationResult;
import org.cmdbuild.etl.ImportExportOperationResultError;
import org.cmdbuild.etl.ImportExportProcessorService;
import org.cmdbuild.etl.ImportExportTemplate;
import static org.cmdbuild.etl.utils.XlsProcessingUtils.getRecordsFromXlsFile;
import static org.cmdbuild.etl.utils.XlsProcessingUtils.lazyRecordToString;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoTime;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import static org.cmdbuild.utils.io.CmIoUtils.countBytes;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmConvertUtils.toDouble;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.mapDifferencesToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.normalize;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;
import org.cmdbuild.userconfig.UserPreferencesService;
import static org.cmdbuild.utils.date.CmDateUtils.toDate;
import static org.cmdbuild.utils.date.CmDateUtils.toTime;
import static org.cmdbuild.utils.io.CmIoUtils.byteCountToDisplaySize;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmExceptionUtils.exceptionToMessage;
import static org.cmdbuild.utils.lang.CmExceptionUtils.exceptionToUserMessage;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.utils.lang.CmNullableUtils;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

@Component
@Primary
public class ImportExportProcessorServiceImpl implements ImportExportProcessorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String IMPORT_RECORD_LINE_NUMBER = "CM_IMPORT_RECORD_LINE_NUMBER";

    private final LookupService lookupService;
    private final UserCardService cardService;
    private final UserPreferencesService userPreferencesService;
    private final DaoService dao;

    public ImportExportProcessorServiceImpl(LookupService lookupService, UserCardService cardService, UserPreferencesService userPreferences, DaoService dao) {
        this.lookupService = checkNotNull(lookupService);
        this.cardService = checkNotNull(cardService);
        this.userPreferencesService = checkNotNull(userPreferences);
        this.dao = checkNotNull(dao);
    }

    @Override
    public DataSource exportDataWithTemplate(ImportExportTemplate template) {
        try {
            logger.info("start data export for template = {}", template);
            checkNotNull(template, "invalid template: template is null");
            DataSource dataSource = buildExportProcessor(template).exportData();
            logger.info("completed export with template = {} output file = {} ({} {})", template, dataSource.getName(), byteCountToDisplaySize(countBytes(dataSource)), dataSource.getContentType());
            return dataSource;
        } catch (Exception ex) {
            throw new EtlException(ex, "export error with template = %s", template);
        }
    }

    @Override
    public ImportExportOperationResult importDataWithTemplate(DataSource data, ImportExportTemplate template) {
        try {
            logger.info("start data import for template = {} from file = {}", template, data.getName());
            checkNotNull(template, "invalid template: template is null");
            ImportExportOperationResult result = buildImportProcessor(template).importData(data);
            logger.info("completed import with template = {} result = {}", template, result);
            return result;
        } catch (Exception ex) {
            throw new EtlException(ex, "import error with template = %s source = %s %s", template, data.getName(), data.getContentType());
        }
    }

    private ExportProcessor buildExportProcessor(ImportExportTemplate template) throws Exception {
        switch (template.getFileFormat()) {
            case IEFF_CSV:
                return new CsvExportProcessor(template);
            case IEFF_XLS:
            case IEFF_XLSX:
                return new XlsExportProcessor(template);
            default:
                throw new EtlException("unsupported template file format = %s", template.getFileFormat());
        }
    }

    private ImportProcessor buildImportProcessor(ImportExportTemplate template) throws Exception {
        switch (template.getFileFormat()) {
            case IEFF_CSV:
                return new CsvImportProcessor(template);
            case IEFF_XLS:
            case IEFF_XLSX:
                return new XlsImportProcessor(template);
            default:
                throw new EtlException("unsupported template file format = %s", template.getFileFormat());
        }
    }

    private CsvPreference getCsvPreference(ImportExportTemplate template) {
        String separator = firstNotBlank(template.getCsvSeparator(), ",");
        CsvPreference csvPreference = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE.getQuoteChar(), separator.charAt(0), CsvPreference.STANDARD_PREFERENCE.getEndOfLineSymbols()).build();
        return csvPreference;
    }

    private class CsvImportProcessor extends ImportProcessor {

        public CsvImportProcessor(ImportExportTemplate template) {
            super(template);
            checkArgument(equal(template.getFileFormat(), IEFF_CSV));
        }

        @Override
        protected List<Map<String, Object>> getRecords(DataSource data) throws Exception {
            List<Map<String, Object>> list = list();
            try (CsvListReader csvReader = new CsvListReader(new InputStreamReader(data.getInputStream()), getCsvPreference(template))) {
                List<String> line;
                while ((line = csvReader.read()) != null) {
                    if (csvReader.getLineNumber() == 1 && template.getUseHeader()) {
                        checkHeader(line);
                    } else {
                        try {
                            list.add(parseLine(line).with(IMPORT_RECORD_LINE_NUMBER, csvReader.getLineNumber()));
                        } catch (Exception ex) {
                            throw new EtlException(ex, "error while parsing line = %s", csvReader.getLineNumber());
                        }
                    }
                }
            }
            logger.debug("loaded {} records from file", list.size());
            return list;
        }

        private FluentMap<String, Object> parseLine(List<String> line) {
            checkArgument(line.size() >= columns.size(), "invalid line size = %s (expected size = %s)", line.size(), columns.size());
            Iterator<String> iterator = line.iterator();
            return (FluentMap) map().accept(m -> columns.stream().forEach((c) -> {
                Object value = iterator.next();
                if (c.doNotIgnoreColumn()) {
                    m.put(c.getAttributeName(), value);
                }
            }));
        }

    }

    private class XlsImportProcessor extends ImportProcessor {

        private final int headerRow, dataRow, columnOffset;

        public XlsImportProcessor(ImportExportTemplate template) {
            super(template);
            checkArgument(set(IEFF_XLS, IEFF_XLSX).contains(template.getFileFormat()));
            headerRow = isNullOrLtEqZero(template.getHeaderRow()) ? 0 : template.getHeaderRow() - 1;
            dataRow = isNullOrLtEqZero(template.getDataRow()) ? (template.getUseHeader() ? 1 : 0) : template.getDataRow() - 1;
            columnOffset = isNullOrLtEqZero(template.getFirstCol()) ? 0 : template.getFirstCol() - 1;
            logger.debug("start import with header row = {} data row = {} column offset = {}", headerRow, dataRow, columnOffset);
        }

        @Override
        protected List<Map<String, Object>> getRecords(DataSource data) throws Exception {
            List<List<Object>> rawRecords = getRecordsFromXlsFile(data, template.getFileFormat(), template.getSkipUnknownColumns() ? null : columns.size(), columnOffset);
            List<Map<String, Object>> list = list();
            for (int rowIndex = 0; rowIndex < rawRecords.size(); rowIndex++) {
                List<Object> rawRecord = rawRecords.get(rowIndex);
                if (rowIndex == headerRow && template.getUseHeader()) {
                    logger.debug("check header row = {}", rowIndex);
                    checkHeader(rawRecord);
                } else if (rowIndex >= dataRow) {
                    try {
                        logger.trace("parse data row = {}", rowIndex);
                        list.add(parseRow(rawRecord).with(IMPORT_RECORD_LINE_NUMBER, rowIndex + 1));
                    } catch (Exception ex) {
                        throw new EtlException(ex, "error while parsing line = %s", rowIndex);
                    }
                } else {
                    logger.debug("skipping row = {}", rowIndex);
                }
            }
            logger.debug("loaded {} records from file", list.size());
            return list;
        }

        private FluentMap<String, Object> parseRow(List<Object> row) {
            return (FluentMap) map().accept(m -> {
                for (int i = 0; i < columns.size(); i++) {
                    Object value = i < row.size() ? row.get(i) : null;
                    if (columns.get(i).doNotIgnoreColumn()) {
                        m.put(columns.get(i).getAttributeName(), value);
                    }
                }
            });
        }

    }

    private abstract class ImportProcessor {

        protected final UserPrefHelper helper = userPreferencesService.getUserPreferencesHelper();
        protected final ImportExportTemplate template;
        protected final EntryTypeHelper entryTypeHelper;
        protected long createdRecordCount = 0,
                modifiedRecordCount = 0,
                unmodifiedRecordCount = 0,
                deletedRecordCount = 0;
        protected final List<ImportExportOperationResultError> errors = list();
        protected final Set<Long> processedRecordIdsFromFile = set();

        protected final List<ImportExportColumnConfig> columns;

        public ImportProcessor(ImportExportTemplate template) {
            this.template = checkNotNull(template);
            checkArgument(template.isImportTemplate(), "invalid template: this is not an import template");
            entryTypeHelper = getTarget(template);
            columns = list(template.getColumns());
        }

        public ImportExportOperationResult importData(DataSource data) throws Exception {
            List<Map<String, Object>> records = list(getRecords(data)).without(r -> template.getColumns().stream().filter(ImportExportColumnConfig::doNotIgnoreColumn).map(ImportExportColumnConfig::getAttributeName).map(r::get).allMatch(CmNullableUtils::isNullOrEmpty));
            for (int recordNumber = 0; recordNumber < records.size(); recordNumber++) {
                Map<String, Object> fileRecord = records.get(recordNumber);
                int recordLineNumber = toInt(fileRecord.get(IMPORT_RECORD_LINE_NUMBER));
                fileRecord = map(fileRecord).withoutKey(IMPORT_RECORD_LINE_NUMBER);
                try {
                    DatabaseRecord dbRecord = entryTypeHelper.importRecord(fileRecord);
                    processedRecordIdsFromFile.add(dbRecord.getId());
                } catch (Exception ex) {
                    logger.warn(marker(), "error loading record = {} line = {} ({})", recordNumber, recordLineNumber, fileRecord, ex);
                    errors.add(new ImportExportOperationResultErrorImpl(recordNumber, recordLineNumber, fileRecord, exceptionToUserMessage(ex), exceptionToMessage(ex)));
                }
            }
            if (!template.hasMergeMode(IEM_LEAVE_MISSING)) {
                if (!errors.isEmpty()) {
                    logger.warn(marker(), "import has errors, skipping handling of missing records");
                } else {
                    logger.debug("handle missing records");
                    entryTypeHelper.handleMissingRecords();
                }
            }
            return new ImportExportOperationResultImpl(createdRecordCount, modifiedRecordCount, unmodifiedRecordCount, deletedRecordCount, records.size(), errors);
        }

        protected void checkHeader(List<? extends Object> row) {
            logger.trace("check header row data = {}", lazyRecordToString(row));//TODO improve row debug dump
            List<String> rawLine = list(transform(row, CmStringUtils::toStringOrEmpty)),
                    expected = list(transform(columns, ImportExportColumnConfig::getColumnName)),
                    lineToCheck = rawLine;
            if (template.getSkipUnknownColumns()) {
                lineToCheck = list(lineToCheck).without(not(expected::contains));
            }
            if (lineToCheck.size() > columns.size()) {
                lineToCheck = lineToCheck.subList(0, columns.size());
            }
            if (template.getIgnoreColumnOrder()) {
                checkArgument(lineToCheck.size() == columns.size() && equal(set(lineToCheck), set(expected)), "invalid header row: expected (in any order) = %s but found = %s", new TreeSet(expected), rawLine);
                reorderColumnConfigs(rawLine);
                logger.debug("actual column order = %s", list(transform(columns, ImportExportColumnConfig::getColumnName)));
            } else {
                checkArgument(equal(lineToCheck, expected), "invalid header row: expected (in this order) = %s but found = %s", expected, rawLine);
            }
            logger.debug("header is ok");
        }

        protected void reorderColumnConfigs(List<String> colHeaders) {
            Map<String, ImportExportColumnConfig> configsByColumnHeader = uniqueIndex(template.getColumns(), ImportExportColumnConfig::getColumnName);
            columns.clear();
            colHeaders.stream().map(c -> {
                ImportExportColumnConfig col = configsByColumnHeader.get(c);
                if (template.getSkipUnknownColumns() && col == null) {
                    col = ImportExportColumnConfigImpl.builder().withMode(IECM_IGNORE).build();
                }
                return checkNotNull(col, "config not found for column header =< %s >", c);
            }).forEach(columns::add);
        }

        @Nullable
        private <T> T convertValueToSystem(String attributeName, @Nullable Object value) {
            String columnName = "<undefined>";
            try {
                Attribute attribute = entryTypeHelper.getEntryType().getAttribute(attributeName);
                ImportExportColumnConfig columnConfig = template.getColumnByAttrName(attributeName);
                columnName = columnConfig.getColumnName();
                T converted = convertValueToSystem(attribute, columnConfig, value);
                if (attribute.isMandatory()) {
                    checkArgument(isNotBlank(converted), "CM: missing value for required attr = %s", attribute.getName());
                }
                return converted;
            } catch (Exception ex) {
                throw new EtlException(ex, "error importing value for attribute =< %s > column =< %s > value =< %s >", attributeName, columnName, value);
            }
        }

        @Nullable
        private <T> T convertValueToSystem(Attribute attribute, ImportExportColumnConfig columnConfig, @Nullable Object value) {
            if (attribute.getOwner().isDomain() && set(ATTR_IDOBJ1, ATTR_IDOBJ2).contains(attribute.getName())) {
                Classe target;
                switch (attribute.getName()) {
                    case ATTR_IDOBJ1:
                        target = ((Domain) attribute.getOwner()).getSourceClass();
                        break;
                    case ATTR_IDOBJ2:
                        target = ((Domain) attribute.getOwner()).getTargetClass();
                        break;
                    default:
                        throw new IllegalArgumentException("unsupported attribute = " + attribute);
                }
                CardIdAndClassName card = processRefValue(target, columnConfig, value);
                value = card == null ? null : card.getId();
            } else {
                switch (attribute.getType().getName()) {
                    case REFERENCE:
                    case FOREIGNKEY:
                        if (isNullOrBlank(value)) {
                            return null;
                        } else {
                            Classe target;
                            if (attribute.isOfType(REFERENCE)) {
                                target = dao.getDomain(attribute.getType().as(ReferenceAttributeType.class).getDomainName()).getReferencedClass(attribute);
                            } else {
                                target = dao.getClasse(attribute.getForeignKeyDestinationClassName());
                            }
                            value = processRefValue(target, columnConfig, value);
                        }
                        break;
                    case LOOKUP:
                        if (isNullOrBlank(value)) {
                            return null;
                        } else {
                            String lookupType = attribute.getType().as(LookupAttributeType.class).getLookupTypeName();
                            switch (columnConfig.getMode()) {
                                case IECM_CODE:
                                    value = checkNotNull(lookupService.getLookupByTypeAndCodeOrNull(lookupType, toStringNotBlank(value)), "CM: lookup not found for code =< %s >", value);
                                    break;
                                case IECM_DESCRIPTION:
                                    value = checkNotNull(lookupService.getLookupByTypeAndDescriptionOrNull(lookupType, toStringNotBlank(value)), "CM: lookup not found for description =< %s >", value);
                                    break;
                                case IECM_ID:
                                    value = checkNotNull(lookupService.getLookupOrNull(toLong(value)), "CM: lookup not found for id =< %s >", value);
                                    break;
                                default:
                                    throw new EtlException("invalid column mode = %s for attr = %s", columnConfig.getMode(), attribute);
                            }
                        }
                        break;
                    case TIMESTAMP:
                        value = helper.parseDateTime(value);
                        break;
                    case DATE:
                        value = helper.parseDate(value);
                        break;
                    case TIME:
                        value = helper.parseTime(value);
                        break;
                    case DECIMAL:
                    case DOUBLE:
                    case INTEGER:
                    case LONG:
                        if (value instanceof String) {
                            value = helper.parseNumber((String) value);
                        }
                }
            }
            value = rawToSystem(attribute, value);
            return (T) value;
        }

        @Nullable
        private CardIdAndClassName processRefValue(Classe target, ImportExportColumnConfig columnConfig, @Nullable Object value) {
            switch (columnConfig.getMode()) {
                case IECM_CODE:
                    return checkNotNull(dao.select(ATTR_ID).from(target).where(ATTR_CODE, EQ, toStringNotBlank(value)).getCardOrNull(), "CM: card not found for code =< %s >", value);
                case IECM_DESCRIPTION:
                    return checkNotNull(dao.select(ATTR_ID).from(target).where(ATTR_DESCRIPTION, EQ, toStringOrEmpty(value)).getCardOrNull(), "CM: card not found for description =< %s >", value);
                case IECM_ID:
                    return checkNotNull(dao.select(ATTR_ID).from(target).where(ATTR_ID, EQ, toLong(value)).getCardOrNull(), "CM: card not found for id = %s", value);
                default:
                    throw new EtlException("invalid column mode = %s", columnConfig.getMode());
            }
        }

        protected abstract List<Map<String, Object>> getRecords(DataSource data) throws Exception;

        private EntryTypeHelper getTarget(ImportExportTemplate template) {
            switch (template.getTargetType()) {
                case IET_CLASS:
                    return new ClassHelper();
                case IET_DOMAIN:
                    return new DomainHelper();
                default:
                    throw new EtlException("unsupported template target type = %s", template.getTargetType());
            }
        }

        private abstract class EntryTypeHelper {

            public abstract EntryType getEntryType();

            public abstract DatabaseRecord importRecord(Map<String, Object> record);

            public abstract void handleMissingRecords();

            protected void handleMissingRecord(DatabaseRecord dbRecord) {
                switch (template.getMergeMode()) {
                    case IEM_DELETE_MISSING:
                        logger.debug("delete missing record = {}", dbRecord);
                        dao.delete(dbRecord);
                        deletedRecordCount++;
                        break;
                    case IEM_UPDATE_ATTR_ON_MISSING:
                        String attributeName = checkNotBlank(template.getAttributeNameForUpdateAttrOnMissing());
                        Attribute attribute = getEntryType().getAttribute(attributeName);
                        ImportExportColumnConfig colConfig = ImportExportColumnConfigImpl.builder().withAttributeName(attributeName).withColumnName("DUMMY").accept(c -> {
                            if (attribute.isOfType(REFERENCE, FOREIGNKEY, LOOKUP)) {
                                if (isNumber(template.getAttributeValueForUpdateAttrOnMissing())) {
                                    c.withMode(IECM_ID);
                                } else {
                                    c.withMode(IECM_CODE);
                                }
                            }
                        }).build();
                        Object value = convertValueToSystem(attribute, colConfig, template.getAttributeValueForUpdateAttrOnMissing());
                        updateRecord(dbRecord, attributeName, value);
                        deletedRecordCount++;//TODO mark deleted only if not already deleted (add filter on query for missing records)
                        break;

                    case IEM_LEAVE_MISSING:
                        break;//do nothing
                    default:
                        throw new EtlException("unsupported merge mode = %s", template.getMergeMode());
                }
            }

            protected abstract void updateRecord(DatabaseRecord dbRecord, String attributeName, Object value);
        }

        private class ClassHelper extends EntryTypeHelper {

            private final Classe classe;

            public ClassHelper() {
                classe = dao.getClasse(template.getTargetName());
                logger.debug("import to class = {}", classe);
            }

            @Override
            public EntryType getEntryType() {
                return classe;
            }

            @Override
            public DatabaseRecord importRecord(Map<String, Object> record) {
                logger.trace("import class record, raw data = \n\n{}\n", mapToLoggableStringLazy(record));
                Map<String, Object> attrs = map(transformEntries(record, ImportProcessor.this::convertValueToSystem));
                logger.trace("import class record, processed data = \n\n{}\n", mapToLoggableStringLazy(attrs));
                Object keyValue = checkNotNull(attrs.get(template.getImportKeyAttribute()), "key attribute value is null");
                Card card, currentCard = dao.selectAll().from(classe).where(template.getImportKeyAttribute(), EQ, keyValue).getCardOrNull();
                if (currentCard == null) {
                    card = CardImpl.buildCard(classe, attrs);
                    logger.debug("create new card = {}", card);
                    card = dao.create(card);
                    createdRecordCount++;
                } else {
                    Card newCard = CardImpl.copyOf(currentCard).addAttributes(attrs).build();
                    if (!currentCard.allValuesEqualTo(newCard)) {
                        logger.trace("detected changes in these attributes = \n\n{}\n", lazyString(()
                                -> mapDifferencesToLoggableString(filterKeys(currentCard.toMap(), currentCard.getAttrsChangedFrom(newCard)::contains), filterKeys(newCard.toMap(), currentCard.getAttrsChangedFrom(newCard)::contains))));
                        logger.debug("update card = {}", newCard);
                        card = dao.update(newCard);
                        modifiedRecordCount++;
                    } else {
                        card = currentCard;
                        logger.debug("skipping unmodified card = {}", card);
                        unmodifiedRecordCount++;
                    }
                }
                return card;
            }

            @Override
            public void handleMissingRecords() {
                UserCardAccess cardAccess = cardService.getUserCardAccess(classe.getName());
                CmdbFilter cardAccessFilter = cardAccess.getWholeClassFilter();
                dao.selectAll().from(classe).where(cardAccessFilter).getCards().stream().filter(c -> !processedRecordIdsFromFile.contains(c.getId())).forEach(this::handleMissingRecord);
            }

            @Override
            protected void updateRecord(DatabaseRecord dbRecord, String attributeName, Object value) {
                dao.update(CardImpl.copyOf((Card) dbRecord).addAttribute(attributeName, value).build());
            }

        }

        private class DomainHelper extends EntryTypeHelper {

            private final Domain domain;

            public DomainHelper() {
                domain = dao.getDomain(template.getTargetName());
                logger.debug("import to domain = {}", domain);
            }

            @Override
            public EntryType getEntryType() {
                return domain;
            }

            @Override
            public DatabaseRecord importRecord(Map<String, Object> record) {
                Map<String, Object> attrs = map(transformEntries(filterKeys(record, not(set(ATTR_IDOBJ1, ATTR_IDOBJ2)::contains)), ImportProcessor.this::convertValueToSystem));
                long sourceId = convertValueToSystem(ATTR_IDOBJ1, record.get(ATTR_IDOBJ1)),
                        targetId = convertValueToSystem(ATTR_IDOBJ2, record.get(ATTR_IDOBJ2));
                CMRelation relation = dao.getRelationOrNull(domain, sourceId, targetId);
                if (relation == null) {
                    Card sourceCard = dao.getCard(domain.getSourceClass(), sourceId),
                            targetCard = dao.getCard(domain.getTargetClass(), targetId);
                    relation = RelationImpl.builder()
                            .withType(domain)
                            .withSourceCard(sourceCard)
                            .withTargetCard(targetCard)
                            .withAttributes(attrs).build();
                    logger.debug("create new relation = {}", relation);
                    relation = dao.create(relation);
                    createdRecordCount++;
                } else {
                    CMRelation newRelation = RelationImpl.copyOf(relation).addAttributes(attrs).build();
                    if (!relation.allValuesEqualTo(newRelation)) {
                        logger.debug("update relation = {}", newRelation);
                        relation = dao.update(newRelation);
                        modifiedRecordCount++;
                    } else {
                        logger.debug("skipping unmodified relation = {}", relation);
                        unmodifiedRecordCount++;
                    }
                }
                return relation;
            }

            @Override
            public void handleMissingRecords() {
                dao.selectAll().from(domain).getRelations() //TODO access control
                        .stream().filter(c -> !processedRecordIdsFromFile.contains(c.getId())).forEach(this::handleMissingRecord);
            }

            @Override
            protected void updateRecord(DatabaseRecord dbRecord, String attributeName, Object value) {
                dao.update(RelationImpl.copyOf((CMRelation) dbRecord).addAttribute(attributeName, value).build());
            }

        }
    }

    private static class ImportExportOperationResultErrorImpl implements ImportExportOperationResultError {

        private final long recordIndex, recordLineNumber;
        private final Map<String, String> recordData;
        private final String userErrorMessage, techErrorMessage;

        public ImportExportOperationResultErrorImpl(long recordIndex, long recordLineNumber, Map<String, Object> recordData, String userErrorMessage, String techErrorMessage) {
            this.recordIndex = recordIndex;
            this.recordLineNumber = recordLineNumber;
            this.recordData = map(transformValues(recordData, CmStringUtils::toStringOrEmpty)).immutable();
            this.userErrorMessage = checkNotBlank(userErrorMessage);
            this.techErrorMessage = checkNotBlank(techErrorMessage);
        }

        @Override
        public long getRecordIndex() {
            return recordIndex;
        }

        @Override
        public long getRecordLineNumber() {
            return recordLineNumber;
        }

        @Override
        public List<Map.Entry<String, String>> getRecordData() {
            return list(recordData.entrySet());
        }

        @Override
        public String getUserErrorMessage() {
            return userErrorMessage;
        }

        @Override
        public String getTechErrorMessage() {
            return techErrorMessage;
        }

    }

    private static class ImportExportOperationResultImpl implements ImportExportOperationResult {

        private final long createdRecordCount, modifiedRecordCount, unmodifiedRecordCount, deletedRecordCount, processedRecordCount;
        private final List<ImportExportOperationResultError> errors;

        public ImportExportOperationResultImpl(long createdRecordCount, long modifiedRecordCount, long unmodifiedRecordCount, long deletedRecordCount, long processedRecordCount, List<ImportExportOperationResultError> errors) {
            this.createdRecordCount = createdRecordCount;
            this.modifiedRecordCount = modifiedRecordCount;
            this.unmodifiedRecordCount = unmodifiedRecordCount;
            this.deletedRecordCount = deletedRecordCount;
            this.processedRecordCount = processedRecordCount;
            this.errors = ImmutableList.copyOf(errors);
        }

        @Override
        public long getCreatedRecordCount() {
            return createdRecordCount;
        }

        @Override
        public long getModifiedRecordCount() {
            return modifiedRecordCount;
        }

        @Override
        public long getUnmodifiedRecordCount() {
            return unmodifiedRecordCount;
        }

        @Override
        public long getDeletedRecordCount() {
            return deletedRecordCount;
        }

        @Override
        public long getProcessedRecordCount() {
            return processedRecordCount;
        }

        @Override
        public List<ImportExportOperationResultError> getErrors() {
            return errors;
        }

        @Override
        public String toString() {
            return "ImportExportOperationResult{" + "processed=" + processedRecordCount + ", errors=" + errors.size() + '}';
        }

    }

    private class XlsExportProcessor extends ExportProcessor {

        private final Workbook workbook;
        private final Sheet sheet;
        private final String contentType, fileExt;
        private final CellStyle dateCellStyle, dateTimeCellStyle;
        private int rowIndex;
        private final int headerRow, dataRow, columnOffset;

        public XlsExportProcessor(ImportExportTemplate template) {
            super(template);
            checkArgument(set(IEFF_XLS, IEFF_XLSX).contains(template.getFileFormat()));

            switch (template.getFileFormat()) {
                case IEFF_XLS:
                    workbook = new HSSFWorkbook();
                    sheet = workbook.createSheet("export");
                    fileExt = "xls";
                    contentType = "application/vnd.ms-excel";
                    break;
                case IEFF_XLSX:
                    workbook = new XSSFWorkbook();
                    sheet = workbook.createSheet("export");
                    fileExt = "xlsx";
                    contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    break;
                default:
                    throw new EtlException("unsupported template file format = %s", template.getFileFormat());
            }

            dateCellStyle = workbook.createCellStyle();
            dateCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("d/m/yy h:mm")); //TODO 
            dateTimeCellStyle = workbook.createCellStyle();
            dateTimeCellStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("d/m/yy")); //TODO 

            headerRow = isNullOrLtEqZero(template.getHeaderRow()) ? 0 : template.getHeaderRow() - 1;
            dataRow = isNullOrLtEqZero(template.getDataRow()) ? (template.getUseHeader() ? 1 : 0) : template.getDataRow() - 1;
            columnOffset = isNullOrLtEqZero(template.getFirstCol()) ? 0 : template.getFirstCol() - 1;

            if (template.getUseHeader()) {
                Row row = sheet.createRow(headerRow);
                for (int i = 0; i < template.getColumns().size(); i++) {
                    Cell cell = row.createCell(i + columnOffset, STRING);
                    cell.setCellValue(template.getColumns().get(i).getColumnName());
                }
            }

            rowIndex = dataRow - 1;
        }

        @Override
        protected void addRecordToResponse(DatabaseRecord record) throws Exception {
            Row row = sheet.createRow(++rowIndex);
            for (int i = 0; i < template.getColumns().size(); i++) {
                ImportExportColumnConfig config = template.getColumns().get(i);
                Pair<Attribute, Object> pair = getAttributeAndValue(record, config);
                serializeAttributeValue(row, i + columnOffset, config, pair.getLeft(), pair.getRight());
            }
        }

        @Override
        protected DataSource doExportData() throws Exception {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            byte[] data = out.toByteArray();
            logger.trace("export {} data = \n\n{}\n", fileExt, lazyString(() -> newStringUsAscii(encodeBase64(data, true))));
            return newDataSource(data, contentType, format("export_%s_%s.%s", normalize(template.getCode()), CmDateUtils.dateTimeFileSuffix(), fileExt));
        }

        private void serializeAttributeValue(Row row, int columnIndex, ImportExportColumnConfig columnConfig, @Nullable Attribute attribute, @Nullable Object value) {
            if (attribute == null || isNullOrBlank(value)) {
                row.createCell(columnIndex, BLANK);
            } else {
                switch (attribute.getType().getName()) {
                    case BOOLEAN:
                        row.createCell(columnIndex, BOOLEAN).setCellValue(toBoolean(value));
                        break;
                    case CHAR:
                    case INET:
                    case JSON:
                    case REGCLASS:
                    case STRING:
                    case TEXT:
                        row.createCell(columnIndex, STRING).setCellValue(convert(value, String.class));
                        break;
                    case DECIMAL:
                    case DOUBLE:
                        row.createCell(columnIndex, NUMERIC).setCellValue(toDouble(value));
                        break;
                    case INTEGER:
                    case LONG:
                        row.createCell(columnIndex, NUMERIC).setCellValue(toLong(value));
                        break;
                    case DATE:
                        Cell dateCell = row.createCell(columnIndex, NUMERIC);
                        dateCell.setCellValue(toJavaDate(value));
                        dateCell.setCellStyle(dateCellStyle);
                        break;
                    case TIMESTAMP:
                        Cell dateTimeCell = row.createCell(columnIndex, NUMERIC);
//                        dateTimeCell.setCellValue(helper.zonedDateTimeToUserLocalJavaDate(toDateTime(value)));
                        dateTimeCell.setCellValue(toJavaDate(value));
                        dateTimeCell.setCellStyle(dateTimeCellStyle);
                        break;
                    case TIME:
                        row.createCell(columnIndex, STRING).setCellValue(toIsoTime(value));
                        break;
                    case REFERENCE:
                    case FOREIGNKEY:
                    case LOOKUP:
                        IdAndDescription idAndDescription = (IdAndDescription) value;
                        if (isNullOrLtEqZero(idAndDescription.getId())) {
                            row.createCell(columnIndex, BLANK);
                        } else {
                            switch (columnConfig.getMode()) {
                                case IECM_CODE:
                                    row.createCell(columnIndex, STRING).setCellValue(checkNotBlank(idAndDescription.getCode(), "invalid code export for value = %s : code is null", idAndDescription));
                                    break;
                                case IECM_DESCRIPTION:
                                    row.createCell(columnIndex, STRING).setCellValue(checkNotBlank(idAndDescription.getDescription(), "invalid description export for value = %s : description is null", idAndDescription));
                                    break;
                                case IECM_ID:
                                    row.createCell(columnIndex, NUMERIC).setCellValue(idAndDescription.getId());
                                    break;
                                default:
                                    throw new EtlException("unsupported column export mode = %s for attribute = %s", columnConfig.getMode(), attribute);
                            }
                        }
                        break;
                    default:
                        throw new EtlException("unable to export attribute = %s: unsupported attribute type", attribute);
                }
            }
            logger.trace("export column = {} with value = {} ({}) to cell = {}", columnConfig.getColumnName(), value, getClassOfNullable(value).getName(), row.getCell(rowIndex));
        }

    }

    private class CsvExportProcessor extends ExportProcessor {

        private final StringWriter writer;
        private final CsvListWriter csv;

        public CsvExportProcessor(ImportExportTemplate template) throws Exception {
            super(template);
            checkArgument(equal(template.getFileFormat(), IEFF_CSV));

            CsvPreference csvPreference = getCsvPreference(template);

            writer = new StringWriter();
            csv = new CsvListWriter(writer, csvPreference);
            if (template.getUseHeader()) {
                csv.write(list(transform(template.getColumns(), ImportExportColumnConfig::getColumnName)));
            }
        }

        @Override
        protected void addRecordToResponse(DatabaseRecord record) throws Exception {
            logger.info("export row = {}", record);
            csv.write(list(transform(template.getColumns(), (c) -> {
                Pair<Attribute, Object> pair = getAttributeAndValue(record, c);
                return nullToEmpty(serializeAttributeValue(c, pair.getLeft(), pair.getValue()));
            })));
        }

        @Override
        protected DataSource doExportData() throws Exception {
            csv.close();
            String csvString = writer.toString();
            logger.trace("export csv data = \n\n{}\n", csvString);
            return newDataSource(csvString.getBytes(StandardCharsets.UTF_8), "text/csv", format("export_%s_%s.csv", normalize(template.getCode()), CmDateUtils.dateTimeFileSuffix()));
        }

        private String serializeAttributeValue(ImportExportColumnConfig columnConfig, @Nullable Attribute attribute, @Nullable Object value) {
            if (attribute == null || isNullOrBlank(value)) {
                return "";
            } else {
                switch (attribute.getType().getName()) {
                    case DECIMAL:
                    case DOUBLE:
                    case INTEGER:
                    case LONG:
                        return helper.serializeNumber(convert(value, Number.class));
                    case BOOLEAN:
                    case CHAR:
                    case INET:
                    case JSON:
                    case REGCLASS:
                    case STRING:
                    case TEXT:
                        return convert(value, String.class);
                    case DATE:
                        return helper.serializeDate(toDate(value));
                    case TIME:
                        return helper.serializeTime(toTime(value));
                    case TIMESTAMP:
                        return helper.serializeDateTime(toDateTime(value));
                    case REFERENCE:
                    case FOREIGNKEY:
                    case LOOKUP:
                        IdAndDescription idAndDescription = (IdAndDescription) value;
                        if (isNullOrLtEqZero(idAndDescription.getId())) {
                            return "";
                        } else {
                            switch (columnConfig.getMode()) {
                                case IECM_CODE:
                                    return checkNotBlank(idAndDescription.getCode(), "invalid code export for value = %s : code is null", idAndDescription);
                                case IECM_DESCRIPTION:
                                    return checkNotBlank(idAndDescription.getDescription(), "invalid description export for value = %s : description is null", idAndDescription);
                                case IECM_ID:
                                    return idAndDescription.getId().toString();
                                default:
                                    throw new EtlException("unsupported column export mode = %s for attribute = %s", columnConfig.getMode(), attribute);
                            }
                        }
                    default:
                        throw new EtlException("unable to export attribute = %s: unsupported attribute type", attribute);
                }
            }
        }

    }

    private abstract class ExportProcessor {

        protected final UserPrefHelper helper = userPreferencesService.getUserPreferencesHelper();
        protected final ImportExportTemplate template;

        public ExportProcessor(ImportExportTemplate template) {
            this.template = checkNotNull(template);
            checkArgument(template.isExportTemplate(), "invalid template: this is not an export template");
        }

        public DataSource exportData() throws Exception {
            CmdbFilter filter = CmdbFilterUtils.parseFilter(template.getExportFilter());
            switch (template.getTargetType()) {
                case IET_CLASS:
                    Classe classe = dao.getClasse(template.getTargetName());
                    template.getColumns().stream().filter(ImportExportColumnConfig::doNotIgnoreColumn).forEach(c -> checkArgument(classe.hasAttribute(c.getAttributeName()), "invalid template: attribute not found in class = %s for name = %s", classe, c.getAttributeName()));
                    UserCardAccess cardAccess = cardService.getUserCardAccess(template.getTargetName());
                    CmdbFilter cardAccessFilter = cardAccess.getWholeClassFilter();
                    filter = filter.and(cardAccessFilter);
                    List<Card> cards = dao.selectAll()
                            .from(template.getTargetName())
                            //                .orderBy(sorter)TODO check order
                            .where(filter)
                            .accept(cardAccess.addSubsetFilterMarkersToQueryVisitor())
                            .getCards().stream()
                            .map(cardAccess::addCardAccessPermissionsFromSubfilterMark)
                            .collect(toList());
                    logger.info("building export with {} card rows", cards.size());
                    cards.forEach(rethrowConsumer(this::addRecordToResponse));
                    return doExportData();
                case IET_DOMAIN:
                    Domain domain = dao.getDomain(template.getTargetName());
                    template.getColumns().stream().filter(ImportExportColumnConfig::doNotIgnoreColumn).forEach(c -> checkArgument(domain.hasAttribute(c.getAttributeName()), "invalid template: attribute not found in domain = %s for name = %s", domain, c.getAttributeName()));
                    List<CMRelation> relations = dao.selectAll().from(domain).where(filter).getRelations(); //TODO access control
                    logger.info("building export with {} relation rows", relations.size());
                    relations.forEach(rethrowConsumer(this::addRecordToResponse));
                    return doExportData();
                default:
                    throw new EtlException("unsupported target type = %s", template.getTargetType());
            }
        }

        protected Pair<Attribute, Object> getAttributeAndValue(DatabaseRecord record, ImportExportColumnConfig c) {
            Attribute attribute;
            Object value;
            if (c.ignoreColumn()) {
                attribute = null;
                value = null;
            } else {
                if (record.getType().isDomain() && set(ATTR_IDOBJ1, ATTR_IDOBJ2).contains(c.getAttributeName())) {
                    attribute = record.getType().asDomain().getIdObjAttrAsFkAttr(c.getAttributeName());
                    value = ((CMRelation) record).getIdObjAttrValueAsFkAttrValue(c.getAttributeName());
                } else {
                    attribute = record.getType().getAttributeOrNull(c.getAttributeName());
                    value = record.get(c.getAttributeName());
                }
            }
            logger.trace("export column = {} with value = {} ({})", c, value, getClassOfNullable(value).getName());
            return Pair.of(attribute, value);
        }

        protected abstract void addRecordToResponse(DatabaseRecord record) throws Exception;

        protected abstract DataSource doExportData() throws Exception;

    }

}

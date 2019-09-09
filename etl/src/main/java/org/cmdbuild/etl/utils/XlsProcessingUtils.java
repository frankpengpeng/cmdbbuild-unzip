/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.utils;

import com.google.common.base.Joiner;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.IOException;
import java.util.List;
import static java.util.stream.Collectors.joining;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.ImportExportFileFormat;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import org.cmdbuild.utils.lang.CmStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XlsProcessingUtils {

    public static List<List<Object>> getRecordsFromXlsFile(DataSource data) {
        return getRecordsFromXlsFile(data, parseEnum(FilenameUtils.getExtension(data.getName()), ImportExportFileFormat.class), null, 0);
    }

    public static List<List<Object>> getRecordsFromXlsFile(DataSource data, ImportExportFileFormat fileFormat, @Nullable Integer columnNumber, int columnOffset) {
        return new XlsProcessor(columnNumber, columnOffset).getRecordsFromXlsFile(data, fileFormat);
    }

    public static Object lazyRecordToString(List<? extends Object> record) {
        return lazyString(() -> record.stream().map(CmStringUtils::toStringOrEmpty).collect(joining(" | ")));
    }

    private static class XlsProcessor {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Integer columnNumber;
        private final int columnOffset;

        public XlsProcessor(@Nullable Integer columnNumber, int columnOffset) {
            this.columnNumber = columnNumber;
            this.columnOffset = columnOffset;
        }

        public List<List<Object>> getRecordsFromXlsFile(DataSource data, ImportExportFileFormat fileFormat) {
            try {
                Workbook workbook;
                switch (fileFormat) {
                    case IEFF_XLS:
                        workbook = new HSSFWorkbook(data.getInputStream());
                        break;
                    case IEFF_XLSX:
                        workbook = new XSSFWorkbook(data.getInputStream());
                        break;
                    default:
                        throw new EtlException("unsupported file format = %s", fileFormat);
                }
                List<List<Object>> list = list();
                int activeSheetIndex = workbook.getActiveSheetIndex();
                Sheet sheet = workbook.getSheetAt(activeSheetIndex);
                if (workbook.getNumberOfSheets() > 1) {
                    logger.warn(marker(), "expected file with a single sheet, but found {}; will load data from active sheet with index = {} and name =< {} > ", workbook.getNumberOfSheets(), activeSheetIndex, sheet.getSheetName());
                } else {
                    logger.debug("load from sheet with index = {} and name =< {} >", activeSheetIndex, sheet.getSheetName());
                }
                for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    try {
                        List<Object> parsedRow = parseRow(row);
                        logger.trace("load row index = {} data = {}", rowIndex, lazyRecordToString(parsedRow));
                        list.add(parsedRow);
                    } catch (Exception ex) {
                        throw new EtlException(ex, "error while parsing line = %s", rowIndex);
                    }
                }
                logger.debug("loaded {} records from file", list.size());
                return list;
            } catch (IOException ex) {
                throw new EtlException(ex);
            }
        }

        private List<Object> parseRow(Row row) {
            return (List) list().accept(m -> {
                int count;
                if (isNotNullAndGtZero(columnNumber)) {
                    count = columnNumber;
                } else if (row == null) {
                    return;
                } else {
                    count = row.getLastCellNum();
                }
                for (int i = 0; i < count; i++) {
                    int columnIndex = i + columnOffset;
                    try {
                        Object value;
                        if (row == null) {
                            value = null;
                        } else {
                            Cell cell = row.getCell(columnIndex, CREATE_NULL_AS_BLANK);
                            value = getCellValue(cell);
                        }
                        m.add(value);
                    } catch (Exception ex) {
                        throw new EtlException(ex, "error while parsing column = %s", columnIndex);
                    }
                }
            });
        }

        @Nullable
        private Object getCellValue(Cell cell) {
            switch (cell.getCellTypeEnum()) {
                case BLANK:
                case STRING:
                    return nullToEmpty(cell.getStringCellValue());
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue();
                    } else {
                        double value = cell.getNumericCellValue();
                        if (value % 1 == 0) {
                            return (long) value;
                        } else {
                            return value;
                        }
                    }
                case BOOLEAN:
                    return cell.getBooleanCellValue();
                default:
                    throw new EtlException("unsupported cell type = %s", cell.getCellTypeEnum());
            }
        }
    }
}

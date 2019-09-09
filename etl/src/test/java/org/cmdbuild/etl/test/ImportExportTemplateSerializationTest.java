/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.test;

import com.fasterxml.jackson.databind.JsonNode;
import org.cmdbuild.etl.ImportExportColumnConfigImpl;
import static org.cmdbuild.etl.ImportExportMergeMode.IEM_UPDATE_ATTR_ON_MISSING;
import static org.cmdbuild.etl.ImportExportTemplateTarget.IET_CLASS;
import org.cmdbuild.etl.data.ImportExportTemplateConfig;
import org.cmdbuild.etl.data.ImportExportTemplateConfigImpl;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.etl.ImportExportColumnMode.IECM_CODE;
import static org.cmdbuild.etl.ImportExportFileFormat.IEFF_XLSX;
import static org.cmdbuild.etl.ImportExportTemplateType.IETT_EXPORT;
import static org.junit.Assert.assertTrue;

public class ImportExportTemplateSerializationTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testTemplateToJson() {
        ImportExportTemplateConfig config = ImportExportTemplateConfigImpl.builder()
                .withAttributeNameForUpdateAttrOnMissing("myAttr")
                .withAttributeValueForUpdateAttrOnMissing("myValue")
                .withColumns(list(ImportExportColumnConfigImpl.builder().withAttributeName("MyOtherAttr").withColumnName("MyColAttr").withMode(IECM_CODE).build()))
                .withExportFilter("{\"filter\":\"meh\"}")
                .withMergeMode(IEM_UPDATE_ATTR_ON_MISSING)
                .withTargetName("MyClass")
                .withTargetType(IET_CLASS)
                .withType(IETT_EXPORT)
                .withFileFormat(IEFF_XLSX)
                .build();

        String asString = toJson(config);

        logger.debug("as string = {}", asString);
        assertEquals(fromJson("{\"targetName\":\"MyClass\",\"columns\":[{\"attributeName\":\"MyOtherAttr\",\"columnName\":\"MyColAttr\",\"default\":null,\"mode\":\"code\"}],"
                + "\"attributeNameForUpdateAttrOnMissing\":null,\"attributeValueForUpdateAttrOnMissing\":null,\"exportFilter\":\"{\\\"filter\\\":\\\"meh\\\"}\",\"csvSeparator\":null,"
                + "\"importKeyAttribute\":null,\"useHeader\":true,\"ignoreColumnOrder\":false,\"headerRow\":null,\"dataRow\":null,\"firstCol\":null,"
                + "\"mergeMode\":\"leave_missing\",\"type\":\"export\",\"targetType\":\"class\",\"format\":\"xlsx\"}", JsonNode.class),
                fromJson(asString, JsonNode.class));

        ImportExportTemplateConfig config2 = fromJson(asString, ImportExportTemplateConfigImpl.class);

        assertEquals(config.getTargetName(), config2.getTargetName());
        assertEquals(config.getTargetType(), config2.getTargetType());
        assertEquals(IECM_CODE, config.getColumns().get(0).getMode());
        assertEquals(config.getMergeMode(), config2.getMergeMode());
        assertEquals(config.getFileFormat(), config2.getFileFormat());
        assertTrue(config.getUseHeader());
    }

}

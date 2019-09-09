/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.String.format;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.entrytype.AttributeGroupInfo;
import org.cmdbuild.dao.entrytype.EntryType;

public interface ObjectTranslationService {

    @Nullable
    String translateByCode(String code);

    String translateAttributeDescription(Attribute attribute);

    String translateAttributeGroupDescription(EntryType entryType, AttributeGroupInfo attributeGroup);

    default String translateClassDescription(Classe classe) {
        return translateByCode(format("class.%s.description", classe.getName()), classe.getDescription());
    }

    default String translateDomainDirectDescription(String domainName, String defaultValue) {
        return translateByCode(format("domain.%s.directdescription", checkNotBlank(domainName)), defaultValue);
    }

    default String translateDomainInverseDescription(String domainName, String defaultValue) {
        return translateByCode(format("domain.%s.inversedescription", checkNotBlank(domainName)), defaultValue);
    }

    default String translateDomainMasterDetailDescription(String domainName, String defaultValue) {
        return translateByCode(format("domain.%s.masterdetaillabel", checkNotBlank(domainName)), defaultValue);
    }

    default String translateLookupDescriptionSafe(String lookupType, String lookupCode, String defaultValue) {
        try {
            return translateLookupDescription(lookupType, lookupCode, defaultValue);
        } catch (Exception ex) {
            LoggerFactory.getLogger(getClass()).warn(marker(), "unable to translate lookup description for lookup type = %s code = %s", lookupType, lookupCode, ex);
            return defaultValue;
        }
    }

    default String translateLookupDescription(String lookupType, String lookupCode, String defaultValue) {
        return translateByCode(format("lookup.%s.%s.description", checkNotBlank(lookupType, "lookup type cannot be null"), checkNotBlank(lookupCode, "lookup code cannot be null")), defaultValue);
    }

    default String translateMenuitemDescription(String code, String defaultValue) {
        return translateByCode(format("menuitem.%s.description", checkNotBlank(code)), defaultValue);
    }

    default String translateViewDesciption(String viewCode, String defaultValue) {
        return translateByCode(format("view.%s.description", checkNotBlank(viewCode)), defaultValue);
    }

    default String translateReportDesciption(String reportCode, String defaultValue) {
        return translateByCode(format("report.%s.description", checkNotBlank(reportCode)), defaultValue);
    }

    default String translateByCode(String code, String defaultValue) {
        return firstNonNull(translateByCode(code), nullToEmpty(defaultValue));
    }

}

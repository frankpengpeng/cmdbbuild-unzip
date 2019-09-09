/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.serializationhelpers;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.stereotype.Component;

@Component
public class DomainSerializationHelper {

    private final ObjectTranslationService translationService;

    public DomainSerializationHelper(ObjectTranslationService translationService) {
        this.translationService = checkNotNull(translationService);
    }

    public CmMapUtils.FluentMap<String, Object> serializeBasicDomain(Domain input) {
        return map("_id", input.getName(),
                "name", input.getName(),
                "description", input.getDescription());
//				"_description_translated",translationService.t) TODO domain description translation ???
    }

    public CmMapUtils.FluentMap<String, Object> serializeDetailedDomain(Domain input) {
        return serializeBasicDomain(input).with("source", input.getSourceClass().getName(),
                "sourceProcess", input.getSourceClass().isProcess(),
                "destination", input.getTargetClass().getName(),
                "destinationProcess", input.getTargetClass().isProcess(),
                "cardinality", input.getCardinality(),
                "descriptionDirect", input.getDirectDescription(),
                "_descriptionDirect_translation", translationService.translateDomainDirectDescription(input.getName(), input.getDirectDescription()),
                "descriptionInverse", input.getInverseDescription(),
                "_descriptionInverse_translation", translationService.translateDomainInverseDescription(input.getName(), input.getInverseDescription()),
                "indexDirect", input.getIndexForSource(),
                "indexInverse", input.getIndexForTarget(),
                "descriptionMasterDetail", input.getMasterDetailDescription(),
                "_descriptionMasterDetail_translation", translationService.translateDomainMasterDetailDescription(input.getName(), input.getMasterDetailDescription()),
                "filterMasterDetail", input.getMasterDetailFilter(),
                "isMasterDetail", input.isMasterDetail(),
                "inline", input.getMetadata().isInline(),
                "defaultClosed", input.getMetadata().isDefaultClosed(),
                "active", input.isActive(),
                "disabledSourceDescendants", CmCollectionUtils.toList(input.getDisabledSourceDescendants()),
                "disabledDestinationDescendants", CmCollectionUtils.toList(input.getDisabledTargetDescendants()));
    }
}

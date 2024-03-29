package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_CREATE;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_UPDATE;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.translation.TranslationService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("processes/{processId}/attributes/")
@Produces(APPLICATION_JSON)
public class ProcessAttributesWsV2 {

    private final UserClassService classService;
    private final DaoService dao;
    private final TranslationService translationService;

    public ProcessAttributesWsV2(UserClassService classService, DaoService dao, TranslationService translationService) {
        this.classService = checkNotNull(classService);
        this.dao = checkNotNull(dao);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("processId") String processId) {
        List list = classService.getUserAttributes(processId).stream().map(this::serializeAttributeType).collect(toList());
        return map("data", list, "meta", map("total", list.size()));
    }

    public Object serializeAttributeType(Attribute attribute) {
        return map(
                //"old meta", attribute.getMetadata(),
                "type", attributeTypeConversion(attribute.getType().getName().name().toLowerCase()),
                "name", attribute.getName(),
                "description", translationService.translateAttributeDescription(attribute),
                "displayableInList", attribute.showInGrid(),
                "domainName", "", //TODO 
                "unique", attribute.isUnique(),
                "mandatory", attribute.isMandatory(),
                "inherited", attribute.isInherited(),
                "active", attribute.isActive(),
                "index", attribute.getIndex(),
                "defaultValue", attribute.getDefaultValue(),
                "group", attribute.getGroupNameOrNull(),
                "editorType", attribute.getEditorType(),
                "filter", map("text", attribute.getFilter(), "params", attribute.getMetadata().getCustomMetadata()),
                "values", "",
                "writable", attribute.hasUiPermission(AP_CREATE) || attribute.hasUiPermission(AP_UPDATE),
                "hidden", !attribute.hasUiReadPermission(),
                "metadata", attribute.getMetadata().getCustomMetadata(),
                "lookupType", attribute.getMetadata().getLookupType(),
                "_id", attribute.getName(),
                "immutable", attribute.hasUiPermission(AP_CREATE) && !attribute.hasUiPermission(AP_UPDATE)
        ).accept((m) -> {
            attribute.getType().accept(new NullAttributeTypeVisitor() {
                @Override
                public void visit(StringAttributeType attributeType) {
                    m.put("lenght", attributeType.getLength()
                    );
                }

                @Override
                public void visit(ReferenceAttributeType attributeType) {
                    Classe referencedClass = dao.getDomain(attributeType.getDomainName()).getReferencedClass(attribute);
                    m.put("domainName", attributeType.getDomainName(),
                            "targetClass", referencedClass.getName(),
                            "targetType", referencedClass.isProcess() ? "process" : referencedClass.isStandardClass() ? "class" : null //TODO 

                    );
                }

                @Override
                public void visit(DecimalAttributeType attributeType) {
                    m.put("precision", attributeType.getPrecision(),
                            "scale", attributeType.getScale()
                    );
                }

                @Override
                public void visit(LookupAttributeType attributeType) {
                    m.put("lookup", attributeType.getLookupTypeName());
                }

            });
        });
    }

    private String attributeTypeConversion(String attrType) {
        if (attrType.equals("timestamp")) {
            return "dateTime";
        } else {
            return attrType;
        }
    }
}

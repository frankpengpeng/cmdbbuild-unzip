/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.filterKeys;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import org.cmdbuild.bim.BimService;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.ExtendedClassDefinition;
import org.cmdbuild.classe.ExtendedClassDefinitionImpl;
import org.cmdbuild.contextmenu.ContextMenuItem;
import org.cmdbuild.contextmenu.ContextMenuItemImpl;
import org.cmdbuild.contextmenu.ContextMenuType;
import org.cmdbuild.contextmenu.ContextMenuVisibility;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.entrytype.AttachmentDescriptionMode;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.ClassType;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.formtrigger.FormTrigger;
import org.cmdbuild.formtrigger.FormTriggerBinding;
import org.cmdbuild.formtrigger.FormTriggerImpl;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ASCENDING;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTRIBUTE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DESCENDING;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DIRECTION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationUtils.serializeWidget;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.widget.model.WidgetData;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_BUTTON_LABEL_KEY;
import static org.cmdbuild.widget.utils.WidgetUtils.serializeWidgetDataToString;
import static org.cmdbuild.widget.utils.WidgetUtils.toWidgetData;
import org.springframework.stereotype.Component;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.beans.ClassMetadataImpl.ClassMetadataImplBuilder;
import static org.cmdbuild.dao.entrytype.AttachmentDescriptionModeUtils.serializeAttachmentDescriptionMode;
import org.cmdbuild.dao.entrytype.AttributeGroupInfoImpl;
import org.cmdbuild.dao.entrytype.ClassMultitenantMode;
import static org.cmdbuild.dao.entrytype.ClassMultitenantModeUtils.serializeClassMultitenantMode;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CLONE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_CREATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_MODIFY;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import static org.cmdbuild.dao.entrytype.ClassMultitenantModeUtils.parseClassMultitenantMode;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

@Component
public class ClassSerializationHelper {

    private final ObjectTranslationService translationService;
    private final BimService bimService;
    private final EasyuploadService easyuploadService;
    private final UserClassService classService;
    private final MultitenantConfiguration multitenantConfiguration;

    public ClassSerializationHelper(ObjectTranslationService translationService, BimService bimService, EasyuploadService easyuploadService, UserClassService classService, MultitenantConfiguration multitenantConfiguration) {
        this.translationService = checkNotNull(translationService);
        this.bimService = checkNotNull(bimService);
        this.easyuploadService = checkNotNull(easyuploadService);
        this.classService = checkNotNull(classService);
        this.multitenantConfiguration = checkNotNull(multitenantConfiguration);
    }

    public CmMapUtils.FluentMap<String, Object> buildBasicResponse(Classe input) {
        return CmMapUtils.<String, Object>map(
                "_id", input.getName(),
                "name", input.getName(),
                "description", input.getDescription(),
                "_description_translation", translationService.translateClassDescription(input),
                "prototype", input.isSuperclass(),
                "parent", input.getParentOrNull(),
                "active", input.isActive(),
                "type", input.isSimpleClass() ? "simple" : "standard", //TODO enum
                "_can_read", input.hasUiPermission(CP_READ),
                "_can_create", input.hasUiPermission(CP_CREATE),
                "_can_update", input.hasUiPermission(CP_UPDATE),
                "_can_clone", input.hasUiPermission(CP_CLONE),
                "_can_delete", input.hasUiPermission(CP_DELETE),
                "_can_modify", input.hasUiPermission(CP_MODIFY),
                "defaultFilter", input.getMetadata().getDefaultFilterOrNull(),
                "defaultImportTemplate", input.getMetadata().getDefaultImportTemplateOrNull(),
                "defaultExportTemplate", input.getMetadata().getDefaultExportTemplateOrNull(),
                "description_attribute_name", ATTR_DESCRIPTION,
                "metadata", input.getMetadata().getCustomMetadata(),
                "_icon", input.getMetadata().hasIcon() ? Optional.ofNullable(easyuploadService.getByPathOrNull(input.getMetadata().getIcon())).map(EasyuploadItem::getId).orElse(null) : null
        ).accept((m) -> {
            if (multitenantConfiguration.isMultitenantEnabled()) {
                m.put("multitenantMode", serializeClassMultitenantMode(input.getMultitenantMode()));
            }
        });
    }

    public CmMapUtils.FluentMap<String, Object> buildFullDetailResponse(Classe input) {
        return buildBasicResponse(input).with(
                "attachmentTypeLookup", input.hasAttachmentTypeLookupType() ? input.getAttachmentTypeLookupType() : null,
                "attachmentDescriptionMode", input.getAttachmentDescriptionMode() == null ? null : serializeAttachmentDescriptionMode(input.getAttachmentDescriptionMode()),
                "noteInline", input.getMetadata().getNoteInline(),
                "noteInlineClosed", input.getMetadata().getNoteInlineClosed(),
                "attachmentsInline", input.getMetadata().getAttachmentsInline(),
                "attachmentsInlineClosed", input.getMetadata().getAttachmentsInlineClosed(),
                "validationRule", input.getMetadata().getValidationRuleOrNull(),
                "stoppableByUser", input.getMetadata().isUserStoppable(),
                "defaultOrder", input.getAttributesForDefaultOrder().stream().map((a) -> map(ATTRIBUTE, a.getName(), DIRECTION, a.getClassOrder() > 0 ? ASCENDING : DESCENDING)).collect(toList()),
                "domainOrder", input.getMetadata().getDomainOrder()
        ).accept((m) -> {
            if (bimService.isEnabled()) {
                m.put("hasBimLayer", bimService.hasBim(input));
            }
        });
    }

    public CmMapUtils.FluentMap<String, Object> buildFullDetailExtendedResponse(ExtendedClass extendedClass) {
        Classe input = extendedClass.getClasse();
        List<FormTrigger> triggers = extendedClass.getFormTriggers();
        List<ContextMenuItem> contextMenuItems = extendedClass.getContextMenuItems();
        AtomicInteger attrGroupIndex = new AtomicInteger(0);
        return buildFullDetailResponse(input).with(
                "widgets", widgetsToResponse(extendedClass.getWidgets())
        ).skipNullValues().with("formTriggers", triggers == null ? null : triggers.stream().map((t) -> {
            Map map = map("script", t.getJsScript(), "active", t.isActive());
            for (FormTriggerBinding binding : FormTriggerBinding.values()) {
                map.put(binding.name(), t.getBindings().contains(binding));
            }
            return map;
        }).collect(toList())).with("contextMenuItems", contextMenuItems == null ? null : contextMenuItems.stream().map((item) -> {
            return map("label", item.getLabel(),
                    "type", item.getType().name().toLowerCase(),
                    "active", item.isActive(),
                    "visibility", item.getVisibility().name().toLowerCase())
                    .skipNullValues()
                    .with(
                            "componentId", item.getComponentId(),
                            "script", item.getJsScript(),
                            "config", item.getConfig()
                    );
        }).collect(toList())).with("attributeGroups", extendedClass.getAttributeGroups().stream().map(g -> map(
                "_id", g.getName(),
                "name", g.getName(),
                "description", g.getDescription(),
                "_description_translation", translationService.translateAttributeGroupDescription(input, g),
                "index", attrGroupIndex.incrementAndGet()
        )).collect(toList())).then();
    }

    private Object widgetsToResponse(Collection<WidgetData> widgets) {
        return widgets.stream().map((widgetData) -> {
            Map<String, Object> widgetDataAsMapWithoutLabel = filterKeys(widgetData.getData(), not(equalTo(WIDGET_BUTTON_LABEL_KEY)));
            return serializeWidget(widgetData).with("_config", serializeWidgetDataToString(widgetDataAsMapWithoutLabel));
        }).collect(toList());
    }

    public ExtendedClassDefinition extendedClassDefinitionForNewClass(WsClassData data) {
        return addExtendedClassData(classDefinitionForNewClass(data), data);
    }

    public ExtendedClassDefinition extendedClassDefinitionForExistingClass(String classId, WsClassData data) {
        return addExtendedClassData(classDefinitionForExistingClass(classId, data), data);
    }

    private ClassDefinition classDefinitionForNewClass(WsClassData data) {
        Classe parent = Optional.ofNullable(trimToNull(data.parentId)).map(classService::getUserClass).orElse(null);
        return ClassDefinitionImpl.builder()
                .withParent(parent == null ? null : parent.getName())
                .withName(data.name)
                .withMetadata(ClassMetadataImpl.builder().accept(data.metadataFillerForClassDataCreate()).accept(addIcon(data)).build())
                .build();
    }

    private ClassDefinition classDefinitionForExistingClass(String classId, WsClassData data) {
        Classe currentClass = classService.getUserClass(classId);
        return ClassDefinitionImpl.copyOf(currentClass)
                .withMetadata(ClassMetadataImpl.copyOf(currentClass.getMetadata()).accept(data.metadataFillerForClassDataUpdate()).accept(addIcon(data)).build())
                .build();
    }

    private Consumer<ClassMetadataImplBuilder> addIcon(WsClassData data) {
        return b -> {
            if (isNotNullAndGtZero(data.iconId)) {
                b.withIconPath(easyuploadService.getById(data.iconId).getPath());
            } else {
                b.withIconPath(null);
            }
        };
    }

    private ExtendedClassDefinition addExtendedClassData(ClassDefinition classDefinition, WsClassData data) {
        return ExtendedClassDefinitionImpl.builder()
                .withClassDefinition(classDefinition)
                .withContextMenuItems(toContextMenuItems(data.contextMenuItems))
                .withFormTriggers(toFormTriggers(data.formTriggers))
                .withDefaultClassOrdering(data.defaultOrder == null ? emptyList() : data.defaultOrder.stream()
                        .map((o) -> Pair.of(o.attribute, parseDirection(o.direction)))
                        .collect(toList()))
                .withWidgets(data.widgets.stream().map((w) -> toWidgetData(w.type, w.active, w.config, w.label)).collect(toList()))
                .withAttributeGroups(data.attributeGroups.stream().map(g -> new AttributeGroupInfoImpl(g.name, g.description)).collect(toList()))
                .build();
    }

    private ExtendedClassDefinition.Direction parseDirection(String direction) {
        switch (checkNotBlank(direction).toLowerCase()) {
            case ASCENDING:
                return ExtendedClassDefinition.Direction.ASC;
            case DESCENDING:
                return ExtendedClassDefinition.Direction.DESC;
            default:
                throw new UnsupportedOperationException("unsupported order direction = " + direction);
        }
    }

    private List<ContextMenuItem> toContextMenuItems(List<WsClassData.WsClassDataContextMenuItem> contextMenuItems) {
        return contextMenuItems.stream().map((i) -> ContextMenuItemImpl.builder()
                .withActive(i.active)
                .withComponentId(i.componentId)
                .withConfig(i.config)
                .withJsScript(i.script)
                .withLabel(i.label)
                .withType(ContextMenuType.valueOf(i.type.toUpperCase()))
                .withVisibility(ContextMenuVisibility.valueOf(i.visibility.toUpperCase()))
                .build()).collect(toList());
    }

    private List<FormTrigger> toFormTriggers(List<WsClassData.WsClassDataFormTrigger> formTriggers) {
        return formTriggers.stream().map((t) -> {
            List<FormTriggerBinding> bindings = list();
            if (t.beforeView) {
                bindings.add(FormTriggerBinding.beforeView);
            }
            if (t.beforeInsert) {
                bindings.add(FormTriggerBinding.beforeInsert);
            }
            if (t.beforeEdit) {
                bindings.add(FormTriggerBinding.beforeEdit);
            }
            if (t.beforeClone) {
                bindings.add(FormTriggerBinding.beforeClone);
            }
            if (t.afterInsert) {
                bindings.add(FormTriggerBinding.afterInsert);
            }
            if (t.afterEdit) {
                bindings.add(FormTriggerBinding.afterEdit);
            }
            if (t.afterClone) {
                bindings.add(FormTriggerBinding.afterClone);
            }
            if (t.afterDelete) {
                bindings.add(FormTriggerBinding.afterDelete);
            }
            return FormTriggerImpl.builder()
                    .withActive(t.active)
                    .withJsScript(t.script)
                    .withBindings(bindings)
                    .build();
        }).collect(toList());
    }

    public static class WsClassData {

        public final String name;
        public final String description;
        public final String validationRule, flowStatusAttr, messageAttr, flowProvider;
        public final ClassType type;
        public final String parentId;
        public final ClassMultitenantMode multitenantMode;
        public final boolean isActive;
        public final boolean isSuperclass;
        public final Boolean noteInline;
        public final Boolean noteInlineClosed, attachmentsInlineClosed, attachmentsInline;
        public final Boolean stoppableByUser;
        public final String attachmentTypeLookup, attachmentDescriptionMode;
        public final List<WsClassDataDefaultOrder> defaultOrder;
        public final List<WsClassDataFormTrigger> formTriggers;
        public final List<WsClassDataContextMenuItem> contextMenuItems;
        public final List<WsClassDataWidget> widgets;
        public final List<String> domainOrder;
        public final List<WsClassDataAttributeGroup> attributeGroups;
        public final Long defaultFilter, defaultImportTemplate, defaultExportTemplate, iconId;

        public WsClassData(@JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("defaultFilter") Long defaultFilter,
                @JsonProperty("defaultImportTemplate") Long defaultImportTemplate,
                @JsonProperty("defaultExportTemplate") Long defaultExportTemplate,
                @JsonProperty("_icon") Long iconId,
                @JsonProperty("validationRule") String validationRule,
                @JsonProperty("type") String type,
                @JsonProperty("messageAttr") String messageAttr,
                @JsonProperty("flowStatusAttr") String flowStatusAttr,
                @JsonProperty("engine") String engine,
                @JsonProperty("parent") String parentId,
                @JsonProperty("active") Boolean isActive,
                @JsonProperty("prototype") Boolean isSuperclass,
                @JsonProperty("noteInline") Boolean noteInline,
                @JsonProperty("noteInlineClosed") Boolean noteInlineClosed,
                @JsonProperty("attachmentsInline") Boolean attachmentsInline,
                @JsonProperty("attachmentsInlineClosed") Boolean attachmentsInlineClosed,
                @JsonProperty("attachmentTypeLookup") String attachmentTypeLookup,
                @JsonProperty("attachmentDescriptionMode") String attachmentDescriptionMode,
                @JsonProperty("multitenantMode") String multitenantMode,
                @JsonProperty("stoppableByUser") Boolean stoppableByUser,
                @JsonProperty("defaultOrder") List<WsClassDataDefaultOrder> defaultOrder,
                @JsonProperty("formTriggers") List<WsClassDataFormTrigger> formTriggers,
                @JsonProperty("contextMenuItems") List<WsClassDataContextMenuItem> contextMenuItems,
                @JsonProperty("widgets") List<WsClassDataWidget> widgets,
                @JsonProperty("attributeGroups") List<WsClassDataAttributeGroup> attributeGroups,
                @JsonProperty("domainOrder") List<String> domainOrder) {
            this.name = checkNotBlank(name, "class name cannot be blank");
            this.description = description;
            this.defaultFilter = defaultFilter;
            this.defaultImportTemplate = defaultImportTemplate;
            this.defaultExportTemplate = defaultExportTemplate;
            this.validationRule = validationRule;
            this.type = parseEnum(checkNotBlank(type, "missing 'type' param"), ClassType.class);
            this.messageAttr = messageAttr;
            this.flowStatusAttr = flowStatusAttr;
            this.flowProvider = engine;
            this.parentId = parentId;
            this.isActive = firstNonNull(isActive, true);
            this.isSuperclass = firstNonNull(isSuperclass, false);
            this.noteInline = noteInline;
            this.stoppableByUser = stoppableByUser;
            this.noteInlineClosed = noteInlineClosed;
            this.attachmentsInline = attachmentsInline;
            this.attachmentsInlineClosed = attachmentsInlineClosed;
            this.attachmentTypeLookup = attachmentTypeLookup;
            this.attachmentDescriptionMode = attachmentDescriptionMode;
            this.defaultOrder = nullToEmpty(defaultOrder);
            this.formTriggers = nullToEmpty(formTriggers);
            this.contextMenuItems = nullToEmpty(contextMenuItems);
            this.widgets = nullToEmpty(widgets);
            this.multitenantMode = isBlank(multitenantMode) ? null : parseClassMultitenantMode(multitenantMode);
            this.attributeGroups = nullToEmpty(attributeGroups);
            this.domainOrder = nullToEmpty(domainOrder);
            this.iconId = iconId;
        }

        public Consumer<ClassMetadataImpl.ClassMetadataImplBuilder> metadataFillerForClassDataCreate() {
            return (b) -> b
                    .withSuperclass(isSuperclass)
                    .withType(type)
                    .accept(metadataFillerForClassDataUpdate());
        }

        public Consumer<ClassMetadataImpl.ClassMetadataImplBuilder> metadataFillerForClassDataUpdate() {
            return (b) -> b.withActive(isActive)
                    .withDescription(description)
                    .withIsUserStoppable(stoppableByUser)
                    .withAttachmentTypeLookup(attachmentTypeLookup)
                    .withAttachmentDescriptionMode(parseEnumOrNull(attachmentDescriptionMode, AttachmentDescriptionMode.class))
                    .withDefaultFilter(defaultFilter)
                    .withDefaultImportTemplate(defaultImportTemplate)
                    .withDefaultExportTemplate(defaultExportTemplate)
                    .withNoteInline(noteInline)
                    .withNoteInlineClosed(noteInlineClosed)
                    .withAttachmentsInline(attachmentsInline)
                    .withAttachmentsInlineClosed(attachmentsInlineClosed)
                    .withValidationRule(validationRule)
                    .withMultitenantMode(multitenantMode)
                    .withFlowStatusAttr(flowStatusAttr)
                    .withFlowProvider(flowProvider)
                    .withMessageAttr(messageAttr)
                    .withDomainOrder(domainOrder);
        }

        @Override
        public String toString() {
            return "WsClassData{" + "name=" + name + ", description=" + description + ", type=" + type + ", parentId=" + parentId + ", isActive=" + isActive + ", isSuperclass=" + isSuperclass + '}';
        }

        public static class WsClassDataDefaultOrder {

            public final String attribute;
            public final String direction;

            public WsClassDataDefaultOrder(@JsonProperty("attribute") String attribute, @JsonProperty("direction") String direction) {
                this.attribute = attribute;
                this.direction = direction;
            }

        }

        public static class WsClassDataFormTrigger {

            public final String script;
            public final boolean active;
            public final boolean beforeView;
            public final boolean beforeInsert;
            public final boolean beforeEdit;
            public final boolean beforeClone;
            public final boolean afterInsert;
            public final boolean afterEdit;
            public final boolean afterClone;
            public final boolean afterDelete;

            public WsClassDataFormTrigger(
                    @JsonProperty("script") String script,
                    @JsonProperty("active") Boolean active,
                    @JsonProperty("beforeView") Boolean beforeView,
                    @JsonProperty("beforeInsert") Boolean beforeInsert,
                    @JsonProperty("beforeEdit") Boolean beforeEdit,
                    @JsonProperty("beforeClone") Boolean beforeClone,
                    @JsonProperty("afterInsert") Boolean afterInsert,
                    @JsonProperty("afterEdit") Boolean afterEdit,
                    @JsonProperty("afterClone") Boolean afterClone,
                    @JsonProperty("afterDelete") Boolean afterDelete) {
                this.script = script;
                this.active = active;
                this.beforeView = beforeView;
                this.beforeInsert = beforeInsert;
                this.beforeEdit = beforeEdit;
                this.beforeClone = beforeClone;
                this.afterInsert = afterInsert;
                this.afterEdit = afterEdit;
                this.afterClone = afterClone;
                this.afterDelete = afterDelete;
            }

        }

        public static class WsClassDataContextMenuItem {

            private final boolean active;
            private final String label, type, componentId, script, config, visibility;

            public WsClassDataContextMenuItem(
                    @JsonProperty("active") Boolean active,
                    @JsonProperty("label") String label,
                    @JsonProperty("type") String type,
                    @JsonProperty("componentId") String componentId,
                    @JsonProperty("script") String script,
                    @JsonProperty("config") String config,
                    @JsonProperty("visibility") String visibility) {
                this.active = active;
                this.label = label;
                this.type = type;
                this.componentId = componentId;
                this.script = script;
                this.config = config;
                this.visibility = visibility;
            }

        }

        public static class WsClassDataWidget {

            private final String label, type, config;
            private final boolean active;

            public WsClassDataWidget(
                    @JsonProperty("_label") String label,
                    @JsonProperty("_type") String type,
                    @JsonProperty("_config") String config,
                    @JsonProperty("_active") Boolean active) {
                this.label = label;
                this.type = type;
                this.config = config;
                this.active = active;
            }

        }

        public static class WsClassDataAttributeGroup {

            private final String name, description;

            public WsClassDataAttributeGroup(
                    @JsonProperty("name") String name,
                    @JsonProperty("description") String description) {
                this.name = name;
                this.description = description;
            }

        }
    }
}

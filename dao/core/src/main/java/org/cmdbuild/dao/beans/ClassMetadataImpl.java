/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Maps.filterKeys;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.dao.entrytype.AttachmentDescriptionMode;
import static org.cmdbuild.dao.entrytype.AttachmentDescriptionModeUtils.parseAttachmentDescriptionMode;
import org.cmdbuild.dao.entrytype.ClassMetadata;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ATTACHMENT_DESCRIPTION_MODE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ATTACHMENT_TYPE_LOOKUP_TYPE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.CLASS_ICON;
import static org.cmdbuild.dao.entrytype.ClassMetadata.DEFAULT_FILTER;
import static org.cmdbuild.dao.entrytype.ClassMetadata.NOTE_INLINE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.NOTE_INLINE_CLOSED;
import static org.cmdbuild.dao.entrytype.ClassMetadata.PROCESS_ENGINE;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.workflow.WorkflowCommonConst.WORKFLOW_ENGINES;

import static org.cmdbuild.dao.entrytype.ClassMetadata.SUPERCLASS;
import static org.cmdbuild.dao.entrytype.ClassMetadata.USER_STOPPABLE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.VALIDATION_RULE;
import static org.cmdbuild.dao.entrytype.ClassMetadata.WORKFLOW_ENABLE_SAVE_BUTTON;
import static org.cmdbuild.dao.entrytype.ClassMetadata.WORKFLOW_PROVIDER;
import static org.cmdbuild.dao.entrytype.ClassMetadata.WORKFLOW_STATUS_ATTR;
import org.cmdbuild.dao.entrytype.ClassMultitenantMode;
import static org.cmdbuild.dao.entrytype.ClassMultitenantMode.CMM_NEVER;
import org.cmdbuild.dao.entrytype.ClassMultitenantModeUtils;
import static org.cmdbuild.dao.entrytype.ClassMultitenantModeUtils.serializeClassMultitenantMode;
import org.cmdbuild.dao.entrytype.ClassPermissionMode;
import org.cmdbuild.dao.entrytype.ClassType;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class ClassMetadataImpl extends EntryTypeMetadataImpl implements ClassMetadata {

    private static final Set<String> CLASS_METADATA_KEYS = set(WORKFLOW_PROVIDER, SUPERCLASS, CLASS_TYPE, USER_STOPPABLE, WORKFLOW_STATUS_ATTR,
            WORKFLOW_ENABLE_SAVE_BUTTON, ATTACHMENT_TYPE_LOOKUP_TYPE, ATTACHMENT_DESCRIPTION_MODE,
            DEFAULT_FILTER, NOTE_INLINE, NOTE_INLINE_CLOSED, ATTACHMENTS_INLINE, ATTACHMENTS_INLINE_CLOSED, VALIDATION_RULE, PROCESS_ENGINE, MULTITENANT_MODE, IS_PROCESS,
            DEFAULT_IMPORT_TEMPLATE, DEFAULT_EXPORT_TEMPLATE, DOMAIN_ORDER).immutable();

    private final boolean isSuperclass, isUserStoppable, isFlowSaveButtonEnabled, noteInline, noteInlineClosed, isProcess, attachmentsInline, attachmentsInlineClosed;
    private final String flowStatusAttr, messageAttr, attachmentTypeLookup, validationRule, flowProvider;
    private final Long defaultFilter, defaultImportTemplate, defaultExportTemplate;
    private final AttachmentDescriptionMode attachmentDescriptionMode;
    private final ClassType classType;
    private final ClassMultitenantMode multitenantMode;
    private final List<String> domainOrder;

    public ClassMetadataImpl(Map<String, String> map) {
        super(map, filterKeys(map, not(CLASS_METADATA_KEYS::contains)));
        isSuperclass = toBooleanOrDefault(map.get(SUPERCLASS), false);

        String classTypeStr = firstNotBlank(map.get(CLASS_TYPE), CLASS_TYPE_STANDARD);
        switch (classTypeStr.toLowerCase()) {
            case CLASS_TYPE_SIMPLE:
                classType = ClassType.SIMPLE;
                break;
            case CLASS_TYPE_STANDARD:
                classType = ClassType.STANDARD;
                break;
            default:
                throw unsupported("unsupported class type = %s", classTypeStr);
        }

        isProcess = toBooleanOrDefault(map.get(IS_PROCESS), false);
        isUserStoppable = toBooleanOrDefault(map.get(USER_STOPPABLE), false);
        flowStatusAttr = emptyToNull(map.get(WORKFLOW_STATUS_ATTR));
        messageAttr = emptyToNull(map.get(WORKFLOW_MESSAGE_ATTR));
        isFlowSaveButtonEnabled = toBooleanOrDefault(map.get(WORKFLOW_ENABLE_SAVE_BUTTON), true);
        attachmentTypeLookup = emptyToNull(map.get(ATTACHMENT_TYPE_LOOKUP_TYPE));
        attachmentDescriptionMode = parseAttachmentDescriptionMode(map.get(ATTACHMENT_DESCRIPTION_MODE));
        noteInline = toBooleanOrDefault(map.get(NOTE_INLINE), false);
        noteInlineClosed = toBooleanOrDefault(map.get(NOTE_INLINE_CLOSED), false);
        attachmentsInline = toBooleanOrDefault(map.get(ATTACHMENTS_INLINE), false);
        attachmentsInlineClosed = toBooleanOrDefault(map.get(ATTACHMENTS_INLINE_CLOSED), true);
        defaultFilter = toLongOrNull(map.get(DEFAULT_FILTER));
        defaultImportTemplate = toLongOrNull(map.get(DEFAULT_IMPORT_TEMPLATE));
        defaultExportTemplate = toLongOrNull(map.get(DEFAULT_EXPORT_TEMPLATE));
        validationRule = emptyToNull(map.get(VALIDATION_RULE));
        flowProvider = trimToNull(map.get(WORKFLOW_PROVIDER));
        multitenantMode = Optional.ofNullable(trimToNull(map.get(MULTITENANT_MODE))).map(ClassMultitenantModeUtils::parseClassMultitenantMode).orElse(CMM_NEVER);
        checkArgument(flowProvider == null || WORKFLOW_ENGINES.contains(flowProvider), "invalid process engine, must be one of %s, found %s instead", WORKFLOW_ENGINES, flowProvider);
        domainOrder = isBlank(map.get(DOMAIN_ORDER)) ? emptyList() : Splitter.on(",").trimResults().omitEmptyStrings().splitToList(map.get(DOMAIN_ORDER)).stream().distinct().collect(toImmutableList());
    }

    public ClassMetadataImpl() {
        this(emptyMap());
    }

    @Override
    public List<String> getDomainOrder() {
        return domainOrder;
    }

    @Override
    public boolean isProcess() {
        return isProcess;
    }

    @Override
    public boolean isSuperclass() {
        return isSuperclass;
    }

    @Override
    public ClassType getClassType() {
        return classType;
    }

    @Override
    public boolean isUserStoppable() {
        return isUserStoppable;
    }

    @Override
    @Nullable
    public String getFlowStatusAttr() {
        return flowStatusAttr;
    }

    @Override
    @Nullable
    public String getMessageAttr() {
        return messageAttr;
    }

    @Override
    public boolean isFlowSaveButtonEnabled() {
        return isFlowSaveButtonEnabled;
    }

    @Override
    @Nullable
    public String getAttachmentTypeLookupTypeOrNull() {
        return attachmentTypeLookup;
    }

    @Override
    @Nullable
    public AttachmentDescriptionMode getAttachmentDescriptionMode() {
        return attachmentDescriptionMode;
    }

    @Override
    public boolean getNoteInline() {
        return noteInline;
    }

    @Override
    public boolean getNoteInlineClosed() {
        return noteInlineClosed;
    }

    @Override
    public boolean getAttachmentsInline() {
        return attachmentsInline;
    }

    @Override
    public boolean getAttachmentsInlineClosed() {
        return attachmentsInlineClosed;
    }

    @Override
    @Nullable
    public Long getDefaultFilterOrNull() {
        return defaultFilter;
    }

    @Override
    @Nullable
    public Long getDefaultImportTemplateOrNull() {
        return defaultImportTemplate;
    }

    @Override
    @Nullable
    public Long getDefaultExportTemplateOrNull() {
        return defaultExportTemplate;
    }

    @Override
    @Nullable
    public String getValidationRuleOrNull() {
        return validationRule;
    }

    @Override
    @Nullable
    public String getFlowProviderOrNull() {
        return flowProvider;
    }

    @Override
    public ClassMultitenantMode getMultitenantMode() {
        return multitenantMode;
    }

    public static ClassMetadataImplBuilder builder() {
        return new ClassMetadataImplBuilder();
    }

    public static ClassMetadataImplBuilder copyOf(ClassMetadata source) {
        return new ClassMetadataImplBuilder(source.getAll());
    }

    public static class ClassMetadataImplBuilder implements Builder<ClassMetadataImpl, ClassMetadataImplBuilder> {

        private final Map<String, String> metadata = map();

        public ClassMetadataImplBuilder() {
        }

        public ClassMetadataImplBuilder(Map map) {
            this.metadata.putAll(map);
        }

        private ClassMetadataImplBuilder with(String key, @Nullable Object value) {
            metadata.put(key, toStringOrNull(value));
            return this;
        }

        public ClassMetadataImplBuilder withDescription(String description) {
            return this.with(DESCRIPTION, description);
        }

        public ClassMetadataImplBuilder withActive(Boolean isActive) {
            return this.with(ACTIVE, isActive);
        }

        public ClassMetadataImplBuilder withMode(ClassPermissionMode mode) {
            return this.with(ENTRY_TYPE_MODE, mode.name().toLowerCase());
        }

        public ClassMetadataImplBuilder withSuperclass(Boolean isSuperclass) {
            return this.with(SUPERCLASS, isSuperclass);
        }

        public ClassMetadataImplBuilder withType(ClassType classType) {
            switch (classType) {
                case SIMPLE:
                    return this.with(CLASS_TYPE, CLASS_TYPE_SIMPLE);
                case STANDARD:
                    return this.with(CLASS_TYPE, CLASS_TYPE_STANDARD);
                default:
                    throw unsupported("unsupported class type = %s", classType);
            }
        }

        public ClassMetadataImplBuilder withIsUserStoppable(Boolean isUserStoppable) {
            return this.with(USER_STOPPABLE, isUserStoppable);
        }

        public ClassMetadataImplBuilder withIsFlowSaveButtonEnabled(Boolean isFlowSaveButtonEnabled) {
            return this.with(WORKFLOW_ENABLE_SAVE_BUTTON, isFlowSaveButtonEnabled);
        }

        public ClassMetadataImplBuilder withNoteInline(Boolean noteInline) {
            return this.with(NOTE_INLINE, noteInline);
        }

        public ClassMetadataImplBuilder withNoteInlineClosed(Boolean noteInlineClosed) {
            return this.with(NOTE_INLINE_CLOSED, noteInlineClosed);
        }

        public ClassMetadataImplBuilder withAttachmentsInline(Boolean attachmentsInline) {
            return this.with(ATTACHMENTS_INLINE, attachmentsInline);
        }

        public ClassMetadataImplBuilder withAttachmentsInlineClosed(Boolean attachmentsInlineClosed) {
            return this.with(ATTACHMENTS_INLINE_CLOSED, attachmentsInlineClosed);
        }

        public ClassMetadataImplBuilder withFlowStatusAttr(String flowStatusAttr) {
            return this.with(WORKFLOW_STATUS_ATTR, flowStatusAttr);
        }

        public ClassMetadataImplBuilder withMessageAttr(String messageAttr) {
            return this.with(WORKFLOW_MESSAGE_ATTR, messageAttr);
        }

        public ClassMetadataImplBuilder withAttachmentTypeLookup(String attachmentTypeLookup) {
            return this.with(ATTACHMENT_TYPE_LOOKUP_TYPE, attachmentTypeLookup);
        }

        public ClassMetadataImplBuilder withDefaultFilter(Long defaultFilter) {
            return this.with(DEFAULT_FILTER, defaultFilter);
        }

        public ClassMetadataImplBuilder withIconPath(String iconPath) {
            return this.with(CLASS_ICON, iconPath);
        }

        public ClassMetadataImplBuilder withDefaultImportTemplate(Long defaultImportTemplate) {
            return this.with(DEFAULT_IMPORT_TEMPLATE, defaultImportTemplate);
        }

        public ClassMetadataImplBuilder withDefaultExportTemplate(Long defaultExportTemplate) {
            return this.with(DEFAULT_EXPORT_TEMPLATE, defaultExportTemplate);
        }

        public ClassMetadataImplBuilder withValidationRule(String validationRule) {
            return this.with(VALIDATION_RULE, validationRule);
        }

        public ClassMetadataImplBuilder withMultitenantMode(@Nullable ClassMultitenantMode multitenantMode) {
            return this.with(MULTITENANT_MODE, multitenantMode == null ? null : serializeClassMultitenantMode(multitenantMode));
        }

        public ClassMetadataImplBuilder withFlowProvider(String flowProvider) {
            return this.with(WORKFLOW_PROVIDER, flowProvider);
        }

        public ClassMetadataImplBuilder withAttachmentDescriptionMode(AttachmentDescriptionMode attachmentDescriptionMode) {
            return this.with(ATTACHMENT_DESCRIPTION_MODE, attachmentDescriptionMode);
        }

        public ClassMetadataImplBuilder withDomainOrder(@Nullable List<String> domainOrder) {
            return this.with(DOMAIN_ORDER, domainOrder == null || domainOrder.isEmpty() ? null : Joiner.on(",").join(domainOrder));
        }

        public ClassMetadataImplBuilder withOther(Map other) {
            metadata.putAll(map(other).withoutKeys(CLASS_METADATA_KEYS::contains));
            return this;
        }

        @Override
        public ClassMetadataImpl build() {
            return new ClassMetadataImpl(metadata);
        }

    }
}

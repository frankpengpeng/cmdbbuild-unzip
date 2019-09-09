/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Objects.equal;
import java.util.List;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface ClassMetadata extends EntryTypeMetadata {

    static final String SUPERCLASS = "cm_superclass";
    static final String CLASS_TYPE = "cm_class_type";
    static final String CLASS_TYPE_SIMPLE = "simpleclass";
    static final String CLASS_TYPE_STANDARD = "class";
    static final String USER_STOPPABLE = "cm_stoppable";
    static final String WORKFLOW_STATUS_ATTR = "cm_workflow_status_attr";
    static final String WORKFLOW_MESSAGE_ATTR = "cm_workflow_message_attr";
    static final String WORKFLOW_ENABLE_SAVE_BUTTON = "cm_workflow_enable_save_button";
    static final String WORKFLOW_PROVIDER = "cm_workflow_provider";
    static final String ATTACHMENT_TYPE_LOOKUP_TYPE = "cm_attachment_type_lookup";
    static final String ATTACHMENT_DESCRIPTION_MODE = "cm_attachment_description_mode";
    static final String DEFAULT_FILTER = "cm_default_filter",
            DEFAULT_IMPORT_TEMPLATE = "cm_default_import_template",
            DEFAULT_EXPORT_TEMPLATE = "cm_default_export_template",
            NOTE_INLINE = "cm_note_inline",
            NOTE_INLINE_CLOSED = "cm_note_inline_closed",
            ATTACHMENTS_INLINE = "cm_attachments_inline",
            ATTACHMENTS_INLINE_CLOSED = "cm_attachments_inline_closed";
    static final String VALIDATION_RULE = "cm_validation_rule";
    static final String CLASS_ICON = "cm_class_icon";
    static final String PROCESS_ENGINE = "cm_process_engine";
    static final String IS_PROCESS = "cm_is_process";
    static final String MULTITENANT_MODE = "cm_multitenant_mode",
            DOMAIN_ORDER = "cm_domain_order";

    boolean isSuperclass();

    ClassType getClassType();

    default boolean holdsHistory() {
        return ClassType.STANDARD.equals(getClassType());
    }

    boolean isUserStoppable();

    boolean isProcess();

    @Nullable
    String getFlowStatusAttr();

    @Nullable
    String getFlowProviderOrNull();

    @Nullable
    String getMessageAttr();

    boolean isFlowSaveButtonEnabled();

    @Nullable
    String getAttachmentTypeLookupTypeOrNull();

    @Nullable
    AttachmentDescriptionMode getAttachmentDescriptionMode();

    @Nullable
    Long getDefaultFilterOrNull();

    @Nullable
    Long getDefaultImportTemplateOrNull();

    @Nullable
    Long getDefaultExportTemplateOrNull();

    @Nullable
    String getValidationRuleOrNull();

    boolean getNoteInline();

    boolean getNoteInlineClosed();

    boolean getAttachmentsInline();

    boolean getAttachmentsInlineClosed();

    ClassMultitenantMode getMultitenantMode();

    List<String> getDomainOrder();

    default boolean hasIcon() {
        return isNotBlank(getAll().get(CLASS_ICON));
    }

    default String getIcon() {
        return checkNotBlank(getAll().get(CLASS_ICON));
    }

    default boolean isSimpleClass() {
        return equal(getClassType(), ClassType.SIMPLE);
    }

    default boolean isStandardClass() {
        return equal(getClassType(), ClassType.STANDARD);
    }

}

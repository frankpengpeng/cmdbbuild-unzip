Ext.define('CMDBuildUI.model.tasks.ReadEmailConfig', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    fields: [{
        name: 'filter_type',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'account_name',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'filter_reject',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'folder_incoming',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'folder_rejected',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'folder_processed',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'filter_regex_from',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'filter_regex_subject',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'cronExpression',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'action_workflow_active',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'action_workflow_advance',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'action_attachments_active',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'action_notification_active',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'action_workflow_class_name',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'action_attachments_category',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'action_notification_template',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'action_workflow_fields_mapping',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'action_workflow_attachmentssave',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'action_workflow_attachmentscategory',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'parsing_active',
        type: 'boolean',
        persist: false,
        critical: false,
        calculate: function(data){
            if(data.parsing_key_end && data.parsing_key_init && data.parsing_value_init && data.parsing_value_end){
                return true;
            }
            return false;
        }
    }, {
        name: 'parsing_key_end',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'parsing_key_init',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'parsing_value_init',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'parsing_value_end',
        type: 'string',
        persist: true,
        critical: true
    }],



    proxy: {
        type: 'memory'
    },

    clone: function () {
        var newTask = this.copy();
        newTask.set('_id', undefined);
        newTask.crudState = "C";
        newTask.phantom = true;
        delete newTask.crudStateWas;
        delete newTask.previousValues;
        delete newTask.modified;
        return newTask;
    }
});
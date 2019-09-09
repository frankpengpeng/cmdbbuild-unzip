
Ext.define('CMDBuildUI.view.attachments.Form',{
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.attachments.FormController',
        'CMDBuildUI.view.attachments.FormModel'
    ],

    alias: 'widget.attachments-form',
    controller: 'attachments-form',
    viewModel: {
        type: 'attachments-form'
    },

    fieldDefaults: {
        labelAlign: 'top'
    },

    layout: 'column',
    items: [{
        xtype: 'fieldcontainer',
        columnWidth: 0.5,
        padding: '0 15 0 15',
        layout: 'anchor',
        items : [{
            xtype: 'combobox',
            reference: 'category',
            fieldLabel: CMDBuildUI.locales.Locales.attachments.category,
            displayField: 'description',
            valueField: '_id',
            forceSelection: true,
            allowBlank: false,
            editable: false,
            anchor: '100%',
            autoEl: {
                'data-testid': 'attachmentform-category'
            },
            bind: {
                value: '{theAttachment.category}',
                store: '{categories}'
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.attachments.category'
            }
        }, {
            xtype: 'textareafield',
            reference: 'description',
            fieldLabel: CMDBuildUI.locales.Locales.attachments.description,
            validateOnChange: false,
            hidden: true,
            anchor: '100%',
            autoEl: {
                'data-testid': 'attachmentform-description'
            },
            bind: {
                fieldLabel: '{description.label}',
                hidden: '{description.hidden}',
                value: '{theAttachment.description}'
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.attachments.description'
            },
            validator: function(value) {
                var vm = this.lookupViewModel();
                return !vm.get("description.required") || value ? true : 'Err';
            }
        }, {
            xtype: 'filefield',
            reference: 'file',
            fieldLabel: CMDBuildUI.locales.Locales.attachments.file,
            // allowBlank: false,
            anchor: '100%',
            // buttonText: CMDBuildUI.locales.Locales.attachments.uploadfile,
            autoEl: {
                'data-testid': 'attachmentform-file'
            },
            bind: {
                value: '{theAttachment.file}',
                disabled: '{!canEditFile}' // TODO: check
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.attachments.file'
            },
            validator: function(value) {
                var vm = this.lookupViewModel();
                return !vm.get("newAttachment") || value ? true : 'Err';
            }
        }, {
            xtype: 'checkboxfield',
            reference: 'majorversion',
            fieldLabel: CMDBuildUI.locales.Locales.attachments.majorversion,
            autoEl: {
                'data-testid': 'attachmentform-majorversion'
            },
            bind: {
                hidden: '{hideMajorVersion}',
                value: '{majorVersionValue}'
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.attachments.majorversion'
            }
        }]
    }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        formBind: true, // enable once the form is valid
        disabled: true,
        reference: 'saveBtn',
        itemId: 'saveBtn',
        ui: 'management-action',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        reference: 'cancelBtn',
        itemId: 'cancelBtn',
        ui: 'secondary-action',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }]

});

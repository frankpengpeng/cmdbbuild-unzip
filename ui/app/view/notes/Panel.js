
Ext.define('CMDBuildUI.view.notes.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.notes.PanelController',
        'CMDBuildUI.view.notes.PanelModel'
    ],

    alias: 'widget.notes-panel',
    controller: 'notes-panel',
    viewModel: {
        type: 'notes-panel'
    },

    modelValidation: true,

    config: {
        /**
         * @cfg {Boolean} editMode
         */
        editMode: false
    },

    publishes: ['editMode'],
    bind: {
        editMode: '{editmode}'
    },
    layout: 'fit',

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.notes.edit,
        reference: 'editbtn',
        itemId: 'editbtn',
        iconCls: 'x-fa fa-pencil',
        ui: 'management-action-small',
        hidden: true,
        disabled: true,
        bind: {
            hidden: '{hiddenbtns.edit}',
            disabled: '{!basepermissions.edit}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.notes.edit'
        },
        autoEl: {
            'data-testid': 'notes-panel-editbtn'
        }
    }],

    items: [
        CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
            flex: 1,
            bind: {
                value: '{theObject.Notes}',
                hidden: '{!editmode}'
            }
        }), {
            xtype: 'panel',
            cls: 'x-selectable',
            bind: {
                html: '{theObject.Notes}',
                hidden: '{editmode}'
            }
        }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        reference: 'savebtn',
        itemId: 'savebtn',
        ui: 'management-action-small',
        formBind: true, //only enabled once the form is valid
        disabled: true,
        bind: {
            hidden: '{hiddenbtns.save}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        autoEl: {
            'data-testid': 'notes-panel-savebtn'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        reference: 'cancelbtn',
        itemId: 'cancelbtn',
        ui: 'secondary-action-small',
        bind: {
            hidden: '{hiddenbtns.cancel}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        autoEl: {
            'data-testid': 'notes-panel-cancelbtn'
        }
    }]
});

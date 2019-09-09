
Ext.define('CMDBuildUI.view.importexport.Export',{
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.importexport.ExportController',
        'CMDBuildUI.view.importexport.ExportModel'
    ],

    alias: 'widget.importexport-export',
    controller: 'importexport-export',
    viewModel: {
        type: 'importexport-export'
    },

    config: {
        /**
         * @cfg {CMDBuildUI.model.importexports.Template []}
         * Allowed templates for data import.
         */
        templates: [],

        /**
         * @cfg {CMDBuildUI.model.classes.Class}
         * Class instance
         */
        object: null
    },

    publish: [
        'templates'
    ],

    twoWayBindable: [
        'templates'
    ],

    bind: {
        'templates': '{templatesList}'
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,

    items: [{
        xtype: 'combobox',
        itemId: 'tplcombo',
        fieldLabel: CMDBuildUI.locales.Locales.importexport.template,
        allowNull: false,
        valueField: '_id',
        displayField: 'description',
        bind: {
            store: '{templates}',
            value: '{values.template}'
        }
    }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.importexport.export,
        formBind: true,
        itemId: 'exportbtn',
        ui: 'management-action',
        bind: {
            href: '{exporturl}'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.close,
        ui: 'secondary-action',
        itemId: 'closebtn'
    }]
});

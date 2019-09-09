Ext.define('CMDBuildUI.view.administration.content.importexportdata.templates.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.importexportdata.templates.GridController',
        'CMDBuildUI.view.administration.content.importexportdata.templates.GridModel',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],

    alias: 'widget.administration-content-importexportdata-templates-grid',
    controller: 'administration-content-importexportdata-templates-grid',
    viewModel: {
        type: 'administration-content-importexportdata-templates-grid'
    },
    bind: {
        store: '{allImportExportTemplates}',
        selection: '{selected}'
    },
    
    reserveScrollbar: true,

    columns: [{
        text: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.classdomain,
        localized:{
            text: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.classdomain'
        },
        dataIndex: 'targetName',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
        },
        dataIndex: 'description',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.type,
        localized:{
            text: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.type'
        },
        dataIndex: 'type',
        align: 'left',
        renderer: function (value) {
            try {
                var vm = this.lookupViewModel();
                // var store = CMDBuildUI.model.importexports.Template.templateTypes;
                var store = vm.getStore('templateTypesStore');
                if (store) {
                    var record = store.findRecord('value', value);
                    return record && record.get('label');
                }
                return value;
            } catch (e) {
                return value;
            }
        }
    }, {
       text: CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.fileformat,
       localized:{
           text: 'CMDBuildUI.locales.Locales.administration.importexport.fieldlabels.fileformat'
       },
        dataIndex: 'fileFormat',
        align: 'left',
        renderer: function (value) {
            try {
                var vm = this.lookupViewModel();
                var store = vm.getStore('fileTypesStore');
                // var store = CMDBuildUI.model.importexports.Template.fileTypes;
                if (store) {
                    var record = store.findRecord('value', value);
                    return record && record.get('label');
                }
                return value;
            } catch (e) {
                return value;
            }
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.active,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
        },
        dataIndex: 'active',
        align: 'center',
        xtype: 'checkcolumn',
        disabled: true,
        disabledCls: '' // or don't add this config if you want the field to look disabled
    }],

    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',

        expandOnDblClick: false,
        widget: {
            xtype: 'administration-content-importexportdata-templates-card-viewinrow',
            ui: 'administration-tabandtools',
            viewModel: {
                data: {
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            },
            bind: {
                theImportExportTemplate: '{selected}'
            }

        }
    }],

    autoEl: {
        'data-testid': 'administration-content-importexportdata-templates-grid'
    },

    forceFit: true,
    loadMask: true,

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    labelWidth: "auto"
});
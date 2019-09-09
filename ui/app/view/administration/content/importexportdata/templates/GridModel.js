Ext.define('CMDBuildUI.view.administration.content.importexportdata.templates.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-importexportdata-templates-grid',
    data: {
        theImportExportTemplate: null,
        search: {
            value: null
        },
        selected: null,
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },
    formulas: {
        templateTypes: {
            get: function (data) {
                return CMDBuildUI.model.importexports.Template.getTemplateTypes();
            }
        },
        targetTypes: {
            get: function () {
                return CMDBuildUI.model.importexports.Template.getTargetTypes();
            }
        },
        fileTypes: {
            get: function(){
                return CMDBuildUI.model.importexports.Template.getFileTypes();
            }
        }
    },
    stores: {
        templateTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{templateTypes}'
        },
        fileTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{fileTypes}'
        }
    }
});

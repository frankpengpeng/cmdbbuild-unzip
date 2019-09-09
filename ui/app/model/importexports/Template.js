Ext.define('CMDBuildUI.model.importexports.Template', {
    require: ['CMDBuildUI.model.menu.MenuItem'],
    extend: 'CMDBuildUI.model.base.Base',
    statics: {
        types: {
            import: 'import',
            export: 'export',
            importexport: 'import_export'
        },

        getTemplateTypes: function () {
            return [{
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.import,
                value: 'import'
            }, {
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.export,
                value: 'export'
            }, {
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.importexport,
                value: 'import_export'
            }];
        },

        getTargetTypes: function () {
            return [{
                label: CMDBuildUI.locales.Locales.administration.localizations.class,
                value: CMDBuildUI.model.administration.MenuItem.types.klass
            }, {
                label: CMDBuildUI.locales.Locales.administration.localizations.domain,
                value: CMDBuildUI.model.administration.MenuItem.types.domain
            }];
        },

        getFileTypes: function () {
            return [{
                label: 'CSV',
                value: 'csv'
            }, {
                label: 'XLSX',
                value: 'xlsx'
            }, {
                label: 'XLS',
                value: 'xls'
            }];
        },
        getMergeModes: function () {
            return [{
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.nodelete, // 'No delete'
                value: 'leave_missing'
            }, {
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.delete, // 'Delete'
                value: 'delete_missing'
            }, {
                label: CMDBuildUI.locales.Locales.administration.importexport.texts.modifycard, // 'Modify card'
                value: 'update_attr_on_missing'
            }];
        },

        getCsvSeparators: function () {
            return [{
                value: ',',
                label: ','
            }, {
                value: ';',
                label: ';'
            }, {
                value: '|',
                label: '|'
            }];
        },

        missingRecords: {
            nodelete: 'leave_missing',
            delete: 'delete_missing',
            modifycard: 'update_attr_on_missing'
        }       
    },
    /**
     * This field is not returned by the servers but used for internal purpose
     */
    hasMany: [{
        name: 'columns',
        model: 'CMDBuildUI.model.importexports.Attribute',
        persist: true,
        critical: true,
        field: 'columns'
    }],
    fields: [{
        name: 'code', // the name of template
        type: 'string',
        validators: ['presence'],
        persist: true,
        critical: true
    }, {
        name: 'description', // the description of template
        type: 'string',
        validators: ['presence'],
        persist: true,
        critical: true
    }, {
        name: 'targetType', // it can be class|domain
        validators: ['presence'],
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'targetName', // the name of class/domain
        validators: ['presence'],
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'type', // import/export/both
        type: 'string',
        validators: ['presence'],
        persist: true,
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true,
        persist: true,
        critical: true
    }, {
        name: 'fileFormat', // csv/xlsx/xls
        validators: ['presence'],
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'ignoreColumnOrder',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: true
    }, {
        name: 'useHeader',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: true
    }, {
        name: 'headerRow', // only if fileFormat  is xlsx/xls
        type: 'number',
        defaultValue: 1,
        persist: true,
        critical: true
    }, {
        name: 'dataRow', // only if fileFormat  is xlsx/xls
        type: 'number',
        defaultValue: 2,
        persist: true,
        critical: true
    }, {
        name: 'firstCol', // only if fileFormat  is xlsx/xls
        type: 'number'
        // defaultValue: 1, // unused from 26/06/19
        // persist: true, // unused from 26/06/19
        // critical: true // unused from 26/06/19
    }, {
        // input type is combo with all attributes in attributes grid
        // visible only if
        name: 'importKeyAttribute',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // input type is combo with values: nodelete|delete|modifycard
        // visible only if this.type === inport|importexport
        name: 'mergeMode',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // input type is combo with all attributes in grid
        // visible only if this.mergeMode === modifycard
        // mandatory if field is visible
        name: 'mergeMode_when_missing_update_attr',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // input type is textfield
        // it can be active only if attibute is set
        name: 'mergeMode_when_missing_update_value',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // it can be active only if template type is import || importexport
        name: 'exportFilter',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // mandatory
        // input type is combo with all email templates
        name: 'errorEmailTemplate',
        type: 'number',
        validators: ['presence'],
        persist: true,
        critical: true
    }, {
        // mandatory
        // input type is combo with all email accounts
        name: 'errorEmailAccount',
        type: 'number',
        persist: true,
        critical: true
    }, {
        // mandatory && visible only if fileFormat is csv
        name: 'csv_separator',
        type: 'string',
        persist: true,
        critical: true
    }, {

        name: 'columns',
        type: 'auto',
        defaultValue: [],
        persist: true,
        critical: true
    }],

    proxy: {
        type: 'baseproxy',
        url: '/etl/templates',
        extraParams: {
            detailed: true
        }
    },

    copyForClone: function () {
        var newRecord = this.clone();
        newRecord.set('_id', undefined);
        newRecord.columns().add(this.getAssociatedData().columns);
        newRecord.set('columns', newRecord.columns().getRange());
        newRecord.crudState = "C";
        newRecord.phantom = true;
        delete newRecord.crudStateWas;
        delete newRecord.previousValues;
        delete newRecord.modified;

        return newRecord;
    }
});
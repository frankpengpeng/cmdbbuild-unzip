Ext.define('CMDBuildUI.model.importexports.Attribute', {
    extend: 'Ext.data.Model',

    statics: {
        getAttributeModes: function () {
            return [{
                value: 'id',
                label: 'Id'
            }, {
                value: 'code',
                label: CMDBuildUI.locales.Locales.administration.common.labels.code
            }, {
                value: 'description',
                label: CMDBuildUI.locales.Locales.administration.common.labels.description
            }, {
                value: 'default',
                label: CMDBuildUI.locales.Locales.administration.common.labels.default
            }];
        },
        getDefaultDomainsAttributes: function () {
            return [{
                attribute: 'IdObj1',
                columnName: '', // will be set next by domain source class description
                default: '',
                mode: '',
                index: 0
            }, {
                attribute: 'IdObj2',
                columnName: '', // will be set next by domain destination class description
                default: '',
                mode: '',
                index: 1
            }];
        }
    },
    fields: [{
        // the Attribute.name 
        name: 'attribute',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // theAttribute.description
        name: 'columnName',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // values can be default|description|code|id
        // editable only if theAttribute.type is lookup|reference
        name: 'mode',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // if theAttribute.type is lookup|reference, input type should be "combo" 
        // otherwise input type should be "textfield"
        // it can be active only if template type is import or importexport
        name: 'default',
        type: 'string',
        persist: true,
        critical: true
    }, {
        // it is needed by reorder grig
        name: 'index',
        type: 'number',
        persist: false,
        critical: false
    }],
    proxy: {
        type: 'memory'
    }
});
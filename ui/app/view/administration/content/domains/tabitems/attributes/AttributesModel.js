Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.attributes.AttributesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-domains-tabitems-attributes-attributes',
    data: {
        selected: {},
        isOtherPropertiesHidden: true
    },
    formulas: {

        allAttributeProxy: {
            bind: '{theDomain.name}',
            get: function (objectTypeName) {
                if (objectTypeName) {
                    return {
                        url: Ext.String.format("/domains/{0}/attributes", objectTypeName),
                        type: 'baseproxy',
                        extraParams: {
                            limit: 0
                        }
                    };
                }
            }
        }
    },
    stores: {
        allAttributes: {
            model: "CMDBuildUI.model.Attribute",
            proxy: '{allAttributeProxy}',
            autoLoad: true,
            autoDestroy: true,
            remoteSort: false, //true for server sorting
            sorters: [{
                property: 'index',
                direction: 'ASC' // or 'ASC'
            }],
            filters: [
                function (item) {
                    return item.data.name !== 'Notes' && item.data.name !== 'IdTenant';
                }
            ]
        }

    }
});
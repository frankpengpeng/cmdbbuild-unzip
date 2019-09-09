Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.attributes.AttributesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-classes-tabitems-attributes-attributes',
    data: {
        selected: {},
        isOtherPropertiesHidden: false
    },
    formulas: {

        allAttributeProxy: {
            bind: '{theObject.name}',
            get: function (objectTypeName) {
                if (objectTypeName) {
                    return {
                        url: Ext.String.format("/classes/{0}/attributes", objectTypeName),
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
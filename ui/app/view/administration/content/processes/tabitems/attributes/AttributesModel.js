Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.attributes.AttributesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-processes-tabitems-attributes-attributes',
    data: {
        selected: {}
    },
    formulas: {

        allAttributeProxy: {
            bind: '{theProcess.name}',
            get: function (objectTypeName) {
                if (objectTypeName) {
                    return {
                        url: Ext.String.format("/processes/{0}/attributes", objectTypeName),
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
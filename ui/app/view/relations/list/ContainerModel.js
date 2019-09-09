Ext.define('CMDBuildUI.view.relations.list.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-list-container',

    data: {
        storedata: {},
        hiddenbtns: {
            relgraph: true
        },
        addbtn: {
            disabled: true
        }
    },

    formulas: {
        updateStoreData: {
            bind: {
                objectId: '{objectId}',
                objectType: '{objectType}',
                objectTypeName: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objectTypeName && data.objectId) {
                    var url;
                    switch (data.objectType) {
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                            url = CMDBuildUI.util.api.Classes.getCardRelations(data.objectTypeName, data.objectId);
                            break;
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                            url = CMDBuildUI.util.api.Processes.getProcessInstanceRelations(data.objectTypeName, data.objectId);
                            break;
                    }
                    this.set("storedata.proxyurl", url);
                }

                this.set("hiddenbtns.relgraph", !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.enabled));
            }
        }
    },

    stores: {
        allRelations: {
            model: "CMDBuildUI.model.domains.Relation",
            proxy: {
                type: 'baseproxy',
                url: '{storedata.proxyurl}',
                extraParams: {
                    detailed: true
                }
            },
            groupField: '_type',
            autoLoad: false,
            autoDestroy: true,
            pageSize: 0 // disable pagination
        }
    }

});
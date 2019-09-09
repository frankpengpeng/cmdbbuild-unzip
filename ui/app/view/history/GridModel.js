Ext.define('CMDBuildUI.view.history.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.history-grid',

    data: {
        storedata: {
            autoload: false
        }
    },

    formulas: {
        /**
         * Update store data
         */
        updateStoreData: {
            bind: {
                objecttype: '{objectType}',
                objecttypename: '{objectTypeName}',
                objectid: '{objectId}'
            },
            get: function (data) {
                if (data.objecttype && data.objecttypename && data.objectid) {
                    var vm = this;
                    CMDBuildUI.util.helper.ModelHelper.getModel(data.objecttype, data.objecttypename).then(function (model) {
                        // set store model name
                        var historymodel = CMDBuildUI.util.helper.ModelHelper.getHistoryModel(data.objecttype, data.objecttypename);
                        vm.set("storedata.modelname", historymodel.getName());
                        // set store proxy url
                        vm.set("storedata.proxyurl", Ext.String.format("{0}/{1}/history", model.getProxy().getUrl(), data.objectid));
                        // set store auto load
                        vm.set("storedata.autoload", true);
                    });
                    // set isProcess variable
                    vm.set("isProcess", data.objecttype === CMDBuildUI.util.helper.ModelHelper.objecttypes.process);
                }
            }
        }
    },

    stores: {
        objects: {
            type: 'history',
            model: '{storedata.modelname}',
            proxy: {
                url: '{storedata.proxyurl}',
                type: 'baseproxy'
            },
            autoLoad: '{storedata.autoload}',
            autoDestroy: true
        }
    }
});

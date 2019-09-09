Ext.define('CMDBuildUI.view.views.items.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.views-items-grid',

    data: {
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
        objectTypeName: null,
        storeinfo: {
            autoLoad: false
        }
    },

    formulas: {

        updateData: {
            bind: {
                objectTypeName: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objectTypeName) {
                    // view data
                    var viewdata = CMDBuildUI.util.helper.ModelHelper.getViewFromName(data.objectTypeName);
                    this.set("title", viewdata.get("description"));

                    // model name
                    var modelName = CMDBuildUI.util.helper.ModelHelper.getModelName(
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
                        data.objectTypeName
                    );
                    this.set("storeinfo.modelname", modelName);

                    var model = Ext.ClassManager.get(modelName);
                    this.set("storeinfo.proxytype", model.getProxy().type);
                    this.set("storeinfo.url", model.getProxy().getUrl());

                    // auto load
                    this.set("storeinfo.autoload", true);
                }
            }
        }
    },

    stores: {
        items: {
            type: 'views',
            model: '{storeinfo.modelname}',
            autoLoad: '{storeinfo.autoload}',
            proxy: {
                type: '{storeinfo.proxytype}',
                url: '{storeinfo.url}'
            }
        }
    }

});
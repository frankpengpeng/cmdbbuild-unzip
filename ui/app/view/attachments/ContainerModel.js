Ext.define('CMDBuildUI.view.attachments.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.attachments-container',

    data: {
        // store variables
        storeAutoLoad: false,
        storeProxyUrl: '',
        disableActions: {
            add: false,
            edit: false,
            delete: false,
            download: false,
            viewHistory: false
        }
    },

    formulas: {
        /**
         * Update store variables
         */
        updateStoreVariables: {
            bind: {
                type: '{objectType}',
                typename: '{objectTypeName}',
                id: '{objectId}'
            },
            get: function (data) {
                if (data.type && data.typename && data.id) {
                    // set proxy url
                    var parentModel = Ext.ClassManager.get(CMDBuildUI.util.helper.ModelHelper.getModelName(data.type, data.typename));
                    this.set(
                        "storeProxyUrl",
                        Ext.String.format(
                            "{0}/{1}/attachments",
                            parentModel.getProxy().getUrl(),
                            data.id
                        )
                    );
                    // set auto load
                    this.set("storeAutoLoad", true);
                }
            }
        }
    },

    stores: {
        attachments: {
            type: 'attachments',
            autoLoad: '{storeAutoLoad}',
            proxy: {
                url: '{storeProxyUrl}',
                type: 'baseproxy'
            }
        }
    }
});

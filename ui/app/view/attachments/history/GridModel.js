Ext.define('CMDBuildUI.view.attachments.history.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.attachments-history-grid',
    data: {
        // store variables
        storeAutoLoad: false,
        storeProxyUrl: ''
    },

    formulas: {
        /**
         * Update store variables
         */
        updateStoreVariables: {
            bind: {
                type: '{objectType}',
                typename: '{objectTypeName}',
                id: '{objectId}',
                attachmentId: '{attachmentId}'
            },
            get: function (data) {
                if (data.type && data.typename && data.id) {
                    // set proxy url
                    var parentModel = Ext.ClassManager.get(CMDBuildUI.util.helper.ModelHelper.getModelName(data.type, data.typename));
                    this.set(
                        "storeProxyUrl",
                        Ext.String.format(
                            "{0}/{1}/attachments/{2}/history",
                            parentModel.getProxy().getUrl(),
                            data.id,
                            data.attachmentId
                        )
                    );
                    // set auto load
                    this.set("storeAutoLoad", true);
                }
            }
        }
    },

    stores: {
        attachmentshistory: {
            type: 'attachments',
            autoLoad: '{storeAutoLoad}',
            proxy: {
                url: '{storeProxyUrl}',
                type: 'baseproxy'
            }
        }
    }

});
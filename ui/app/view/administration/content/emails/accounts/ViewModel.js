Ext.define('CMDBuildUI.view.administration.content.emails.accounts.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-accounts-view',
    data: {
        storeAutoLoad: false,
        storeProxyUrl: ''
    },
    formulas: {

        updateStoreVariables: {
            get: function (data) {
                this.set(
                    "storeProxyUrl",
                    Ext.String.format(
                        '{0}/email/accounts/?detailed=true',
                        CMDBuildUI.util.Config.baseUrl
                    )
                );
                // set auto load
                this.set("storeAutoLoad", true);

            }
        }
    },

    stores: {
        accounts: {
            type: 'accounts',
            autoLoad: '{storeAutoLoad}',
            autoDestroy: true,
            proxy: {
                url: '{storeProxyUrl}',
                type: 'baseproxy'
            },
            sorters: 'name'
        }
    }
});
Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-templates-card-view',
    data: {
        accountDescription: null
    },

    formulas: {
        updateAccount: {
            get: function (data) {
                this.set(
                    "storeProxyUrl",
                    Ext.String.format(
                        '{0}/email/accounts',
                        CMDBuildUI.util.Config.baseUrl
                    )
                );
                this.set("storeAutoLoad", true);
            }
        }
    },

    stores: {
        account: {
            model: 'CMDBuildUI.model.emails.Account',
            proxy: {
                type: "baseproxy",
                url: '{storeProxyUrl}'
            },
            autoLoad: '{storeAutoLoad}',
            autoDestroy: true,
            listeners: {
                load: 'onStoreLoad'
            }
        }
    }

});
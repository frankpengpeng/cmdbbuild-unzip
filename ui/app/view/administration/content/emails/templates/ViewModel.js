Ext.define('CMDBuildUI.view.administration.content.emails.templates.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-templates-view',
    data: {
        storeAutoLoad: false,
        storeProxyUrl: '',
        actions: {
            add: false,
            edit: false,
            view: true
        }
    },
    formulas: {

        updateStoreVariables: {
            get: function (data) {
                this.set(
                    "storeProxyUrl",
                    Ext.String.format(
                        '{0}/email/templates/?detailed=true',
                        CMDBuildUI.util.Config.baseUrl
                    )
                );
                // set auto load
                this.set("storeAutoLoad", true);

            }
        }
    },

    stores: {
        templates: {
            type: 'templates',
            autoLoad: '{storeAutoLoad}',
            autoDestroy: true,
            proxy: {
                url: '{storeProxyUrl}',
                type: 'baseproxy'
            }
        }
    }
});
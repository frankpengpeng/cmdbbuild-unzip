Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-templates-card-edit',
    data: {
        actions: {
            add: false,
            edit: true,
            view: false
        }
    },

    formulas: {
        delaylistdata: {
            get: function () {
                var delaylist = [{
                    value: 0,
                    label: CMDBuildUI.locales.Locales.administration.emails.delays.none
                }, {
                    value: 3600000, // 1 hour in milliseconds
                    label: CMDBuildUI.locales.Locales.administration.emails.delays.hour1
                }, {
                    value: 7200000, // 2 hours in milliseconds
                    label: CMDBuildUI.locales.Locales.administration.emails.delays.hour2
                }, {
                    value: 14400000, // 4 hours in milliseconds
                    label: CMDBuildUI.locales.Locales.administration.emails.delays.hours4
                }, {
                    value: 86400000, // 1 day in milliseconds
                    label: CMDBuildUI.locales.Locales.administration.emails.delays.day1
                }, {
                    value: 172800000, // 2 days in milliseconds
                    label: CMDBuildUI.locales.Locales.administration.emails.delays.days2
                }, {
                    value: 345600000, // 4 days in milliseconds
                    label: CMDBuildUI.locales.Locales.administration.emails.delays.days4
                }, {
                    value: 604800000, // 1 week in milliseconds
                    label: CMDBuildUI.locales.Locales.administration.emails.delays.week1
                }, {
                    value: 1209600000, // 2 weeks in milliseconds
                    label: CMDBuildUI.locales.Locales.administration.emails.delays.weeks2
                }, {
                    value: 2629746000, // 1 month in milliseconds
                    label: CMDBuildUI.locales.Locales.administration.emails.delays.month1
                }];
                return delaylist;
            }
        },

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
        delaylist: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            data: '{delaylistdata}',
            autoDestroy: true
        },

        account: {
            model: 'CMDBuildUI.model.emails.Account',
            proxy: {
                type: "baseproxy",
                url: '{storeProxyUrl}'
            },
            autoLoad: '{storeAutoLoad}',
            autoDestroy: true
        }
    }
});
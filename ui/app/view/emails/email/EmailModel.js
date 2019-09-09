Ext.define('CMDBuildUI.view.emails.email.EmailModel', {
    extend: 'Ext.app.ViewModel',

    alias: 'viewmodel.emails-email',

    data: {
        attachmentsstore: {
            autoload: false
        },
        disabled: {
            templatechoice: true,
            keepsync: true
        }
    },

    formulas: {
        /**
         * Update attachemnts store configuration
         */
        updateAttachmentsStore: {
            bind: {
                email: '{theEmail}'
            },
            get: function (data) {
                var url;
                if (data.email.store) {
                    url = data.email.store.getProxy().getUrl() + Ext.String.format("/{0}/attachments", data.email.getId());
                } else {
                    url = this.get('storeurl');
                }
                this.set("attachmentsstore.proxyurl", url);
                // load attachments only for saved emails
                this.set("attachmentsstore.autoload", data.email.crudState !== "C");
            }
        },


        /**
         * Update keep syncronization field configuration
         */
        updateKeepSync: {
            bind: {
                template: '{theEmail.template}'
            },
            get: function (data) {
                this.set("disabled.keepsync", Ext.isEmpty(data.template));
            }
        }
    },

    stores: {
        /**
         * Templates list
         */
        templates: {
            model: 'CMDBuildUI.model.emails.Template',
            proxy: {
                type: "baseproxy",
                url: CMDBuildUI.util.api.Emails.getTemplatesUrl()
            },
            autoLoad: true,
            pageSize: 0, // disable pagination
            autoDestroy: true
        },

        /**
         * Delays list
         */
        delays: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },
            autoDestroy: true,
            data: [{
                value: 0,
                label: CMDBuildUI.locales.Locales.emails.delays.none
            }, {
                value: 3600, // 1 hour in milliseconds
                label: CMDBuildUI.locales.Locales.emails.delays.hour1
            }, {
                value: 7200, // 2 hours in milliseconds
                label: CMDBuildUI.locales.Locales.emails.delays.hour2
            }, {
                value: 14400, // 4 hours in milliseconds
                label: CMDBuildUI.locales.Locales.emails.delays.hours4
            }, {
                value: 86400, // 1 day in milliseconds
                label: CMDBuildUI.locales.Locales.emails.delays.day1
            }, {
                value: 172800, // 2 days in milliseconds
                label: CMDBuildUI.locales.Locales.emails.delays.days2
            }, {
                value: 345600, // 4 days in milliseconds
                label: CMDBuildUI.locales.Locales.emails.delays.days4
            }, {
                value: 604800, // 1 week in milliseconds
                label: CMDBuildUI.locales.Locales.emails.delays.week1
            }, {
                value: 1209600, // 2 weeks in milliseconds
                label: CMDBuildUI.locales.Locales.emails.delays.weeks2
            }, {
                value: 2629746, // 1 month in milliseconds
                label: CMDBuildUI.locales.Locales.emails.delays.month1
            }]
        },

        /**
         * Attachments list
         */
        attachments: {
            type: 'attachments',
            proxy: {
                type: 'baseproxy',
                url: '{attachmentsstore.proxyurl}'
            },
            autoLoad: '{attachmentsstore.autoload}',
            autoDestroy: true
        }
    }
});
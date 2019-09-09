Ext.define('CMDBuildUI.view.emails.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.emails-grid',

    data: {
        lastcheckdata: {},
        storedata: {
            autoload: false
        },
        templatesstoredata: {
            autoload: false,
            advancedfilter: null
        }
    },

    formulas: {
        updateButtonUsability: {
            get: function (data) {
                var tabpanel = this.getView().getController().getParentTabPanel();
                if (tabpanel && tabpanel.getFormMode && tabpanel.getFormMode() === CMDBuildUI.mixins.DetailsTabPanel.actions.edit) {
                    this.set('disableButtonOnView', false);
                } else {
                    this.set('disableButtonOnView', true);
                }
            }
        },
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
                    var url;
                    if (data.type === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass) {
                        url = CMDBuildUI.util.api.Emails.getCardEmailsUrl(data.typename, data.id);
                    } else if (data.type === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                        url = CMDBuildUI.util.api.Emails.getProcessInstanceEmailsUrl(data.typename, data.id);
                    }
                    this.set("storedata.proxyurl", url);
                    this.set("storedata.extraparams", {
                        detailed: true
                    });
                    // set auto load
                    this.set("storedata.autoload", true);
                }
            }
        },

        /**
         * Update templates store variables
         */
        updateTemplatesStoreData: {
            bind: {
                enabledtemplates: '{emailtemplatestoevaluate}'
            },
            get: function (data) {
                if (data.enabledtemplates.length) {
                    var names = [];
                    data.enabledtemplates.forEach(function (t) {
                        names.push(t.name);
                    });
                    this.set("templatesstoredata.extraparams", {
                        detailed: true,
                        includeBindings: true
                    });
                    this.set("templatesstoredata.advancedfilter", {
                        attributes: {
                            name: {
                                operator: "in",
                                value: names,
                                sort: 'description'
                            }
                        }
                    });
                    this.set("templatesstoredata.autoload", true);
                }
            }
        }
    },

    stores: {
        emails: {
            type: 'emails',
            autoLoad: '{storedata.autoload}',
            autoDestroy: true,
            proxy: {
                url: '{storedata.proxyurl}',
                type: 'baseproxy',
                extraParams: '{storedata.extraparams}'
            }
        },

        templates: {
            model: 'CMDBuildUI.model.emails.Template',
            proxy: {
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Emails.getTemplatesUrl(),
                extraParams: '{templatesstoredata.extraparams}'
            },
            advancedFilter: '{templatesstoredata.advancedfilter}',
            autoLoad: '{templatesstoredata.autoload}',
            autoDestroy: true,
            pageSize: 0, // disable pagination
            listeners: {
                load: 'onTemplatesStoreLoaded'
            }
        }
    }
});
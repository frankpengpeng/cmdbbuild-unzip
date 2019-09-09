Ext.define('CMDBuildUI.view.relations.masterdetail.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-masterdetail-tabpanel',

    data: {
        mddomains: [],
        foreignkeys: []
    },
    formulas: {
        updateStoreInfofilter: {
            get: function () {
                var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(this.get("objectTypeName"), this.get("objectType"));
                this.set('storeinfo.advancedFilter', {
                    attribute: {
                        and: [{
                            simple: {
                                attribute: "isMasterDetail",
                                operator: "equal",
                                value: true
                            }
                        }, {
                            simple: {
                                attribute: "destination",
                                operator: "in",
                                value: item.getHierarchy()
                            }
                        }]
                    }
                });
                this.set('storeinfo.autoload', true);
            }
        }
    },

    stores: {
        fkdomains: {
            autoLoad: '{storeinfo.autoload}',
            autoDestroy: true,
            advancedFilter: '{storeinfo.advancedFilter}',
            proxy: {
                type: 'baseproxy',
                url: Ext.String.format('{0}/fkdomains', CMDBuildUI.util.Config.baseUrl)
            },
            listeners: {
                load: 'onStoreLoaded'
            }
        }
    }
});
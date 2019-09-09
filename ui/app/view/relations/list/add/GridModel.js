Ext.define('CMDBuildUI.view.relations.list.add.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-list-add-grid',

    data: {
        searchvalue: null
    },

    formulas: {
        /**
         * Update store info
         */
        updateStoreInfo: {
            bind: {
                objecttypename: '{objectTypeName}',
                theDomain: '{theDomain}',
                direction: '{relationDirection}'
            },
            get: function (data) {
                if (data.objecttypename) {
                    // store type
                    var object = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.objecttypename);
                    this.set("storeinfo.proxyurl", CMDBuildUI.util.api.Classes.getCardsUrl(data.objecttypename));

                    // add ecql filter
                    var filters = null;
                    if (this.get("storeinfo.ecqlfilter") && !Ext.Object.isEmpty(this.get("storeinfo.ecqlfilter"))) {
                        filters = {
                            ecql: this.get("storeinfo.ecqlfilter")
                        };
                    }
                    this.set("storeinfo.advancedfilter", filters);

                    this.set("storeinfo.extraparams", {
                        forDomain_name: data.theDomain.get("name"),
                        forDomain_direction: data.direction,
                        forDomain_originId: this.get("originId")
                    });

                    // sorters
                    var sorters = [];
                    if (object && object.defaultOrder().getCount()) {
                        object.defaultOrder().getRange().forEach(function (o) {
                            sorters.push({
                                property: o.get("attribute"),
                                direction: o.get("direction") === "descending" ? "DESC" : 'ASC'
                            });
                        });
                    } else {
                        sorters.push({
                            property: 'Description'
                        });
                    }
                    this.set("storeinfo.sorters", sorters);
                }
            }
        }
    },

    stores: {
        records: {
            type: 'classes-cards',
            model: '{storeinfo.modelname}',
            proxy: {
                type: 'baseproxy',
                url: '{storeinfo.proxyurl}',
                extraParams: '{storeinfo.extraparams}'
            },
            advancedFilter: '{storeinfo.advancedfilter}',
            sorters: '{storeinfo.sorters}',
            autoLoad: '{storeinfo.autoload}'
        }
    }

});

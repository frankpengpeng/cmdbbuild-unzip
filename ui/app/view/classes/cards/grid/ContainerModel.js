Ext.define('CMDBuildUI.view.classes.cards.grid.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-grid-container',

    data: {
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
        objectTypeName: null,
        addbtn: {},
        storeinfo: {
            autoload: false,
            advancedfilter: null
        },
        search: {
            value: null
        }
    },

    formulas: {
        updateData: {
            bind: {
                objectTypeName: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objectTypeName) {
                    // class description
                    var desc = CMDBuildUI.util.helper.ModelHelper.getClassDescription(data.objectTypeName);
                    this.set("objectTypeDescription", desc);
                    this.set("title", Ext.String.format("{0} {1}", CMDBuildUI.locales.Locales.classes.cards.label, desc));

                    // model name
                    var modelName = CMDBuildUI.util.helper.ModelHelper.getModelName(
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                        data.objectTypeName
                    );
                    this.set("storeinfo.modelname", modelName);

                    var model = Ext.ClassManager.get(modelName);
                    this.set("storeinfo.proxytype", model.getProxy().type);
                    this.set("storeinfo.url", model.getProxy().getUrl());

                    if (this.getView().getFilter()) {
                        this.set("storeinfo.advancedfilter", {
                            baseFilter: this.getView().getFilter()
                        });
                    }

                    // sorters
                    var klass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.objectTypeName);
                    var sorters = [];
                    if (klass && klass.defaultOrder().getCount()) {
                        klass.defaultOrder().getRange().forEach(function (o) {
                            sorters.push({
                                property: o.get("attribute"),
                                direction: o.get("direction") === "descending" ? "DESC" : 'ASC'
                            });
                        });
                    } else if (!klass.isSimpleClass()) {
                        sorters.push({
                            property: 'Description'
                        });
                    }
                    this.set("storeinfo.sorters", sorters);

                    // auto load
                    this.set("storeinfo.autoload", true);

                    // add button
                    this.set("addbtn.text", CMDBuildUI.locales.Locales.classes.cards.addcard + ' ' + desc);
                }
            }
        },
        btnMapHidden: function () {
            return !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.enabled);
        },
        btnMapText: {
            bind: '{activeView}',
            get: function (activeView) {
                if (activeView === "map") {
                    return CMDBuildUI.locales.Locales.gis.list;
                } else {
                    return CMDBuildUI.locales.Locales.gis.map;
                }
            }
        },

        btnIconCls: {
            bind: '{activeView}',
            get: function (activeView) {
                if (activeView === "map") {
                    return 'x-fa fa-list';
                } else {
                    return 'x-fa fa-globe';
                }
            }
        },

        btnHide: {
            bind: '{activeView}',
            get: function (activeView) {
                if (activeView === "map") {
                    return true;
                } else {
                    return false;
                }
            }
        },

        canFilter: {
            bind: {
                activeView: '{activeView}'
            },
            get: function (data) {
                var activeView = data.activeView;
                if (activeView === "map") {
                    return false;
                } else {
                    return true;
                }
            }
        },

        /**
         * added in revisionedCode
         */
        selectedChange: {
            bind: {
                selected: '{selected}'
            },
            get: function (data) {
                var selected = data.selected;
                if (!selected || selected.id == null || selected.type == null) return;

                selected.conf = selected.conf || {};
                Ext.applyIf(selected.conf, {
                    center: true,
                    zoom: true
                });

                CMDBuildUI.map.util.Util.getGeoValues(selected.id, selected.type,
                    function (records, operation, success) {
                        if (success) {
                            this.getView().fireEvent('selectedchangeevent', selected, CMDBuildUI.map.util.Util.getSelectionGeoValue());
                        } else {
                            CMDBuildUI.util.Notifier.showErrorMessage(Ext.String.format("cant't get geovalues of card. \n id: {0} \n type: {1} ", selected.id, selected.type));
                        }
                    }, this);

            }
        }
    },

    stores: {
        cards: {
            type: 'classes-cards',
            storeId: 'cards',
            model: '{storeinfo.modelname}',
            sorters: '{storeinfo.sorters}',
            proxy: {
                type: '{storeinfo.proxytype}',
                url: '{storeinfo.url}'
            },
            advancedFilter: '{storeinfo.advancedfilter}',
            autoLoad: '{storeinfo.autoload}',
            autoDestroy: true
        }
    }

});
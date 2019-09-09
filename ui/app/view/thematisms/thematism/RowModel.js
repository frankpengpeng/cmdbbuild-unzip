Ext.define('CMDBuildUI.view.thematisms.thematism.RowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.thematisms-thematism-row',
    data: {
        hiddenfields: {
            attributecombo: true,
            functioncombo: true
        },
        values: {
            geoattribute: null,
            attributecombo: null,
            name: null
        }
    },

    formulas: {
        updateAttributesData: {
            bind: {
                objecttype: '{objectType}',
                objecttypename: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objecttype && data.objecttypename) {
                    var me = this;
                    CMDBuildUI.util.helper.ModelHelper.getModel(data.objecttype, data.objecttypename).then(function (model) {
                        var d = [];
                        model.getFields().forEach(function (field) {
                            if (!Ext.String.startsWith(field.name, "_")) {
                                d.push({
                                    value: field.name,
                                    label: field.attributeconf.description_localized
                                });
                            }
                        });
                        me.set("attributesstoredata", d);
                    });
                }
            }
        },
        updateGeoAttributesData: {
            bind: {
                objecttype: '{objectType}',
                objecttypename: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objecttype && data.objecttypename) {
                    var geoAttributesStore = CMDBuildUI.map.util.Util.getGeoAttributesStore();
                    var d = [];

                    geoAttributesStore.getRange().forEach(function (record) {
                        if (record.get('owner_type') == data.objecttypename) {
                            d.push({
                                label: record.get('name'),
                                value: record.get('name')
                            });
                        }
                    }, this);
                    this.set('geoattributestoredata', d);
                }
            }
        },
        updateFunctionData: {
            bind: {
                objecttype: '{objectType}',
                objecttypename: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objecttype && data.objecttypename) {

                    var me = this;
                    var d = [{
                        label: 'function 1',
                        value: 'id Function'
                    }];
                    //ther should be a server call
                    me.set('functionstoredata', d);
                }
            }
        },

        updateSourceDeps: {
            bind: '{values.source}',
            get: function (source) {
                if (source === CMDBuildUI.model.thematisms.Thematism.sources.function) {
                    this.set("hiddenfields.attributecombo", true);
                    this.set("hiddenfields.functioncombo", false);
                } else if (source === CMDBuildUI.model.thematisms.Thematism.sources.table) {
                    this.set("hiddenfields.attributecombo", false);
                    this.set("hiddenfields.functioncombo", true);
                }
            }
        },

        sincronyzeTheThematism: {
            bind: {
                theThematism: '{theThematism}',
                attribute: '{attributesstoredata}',
                geoattribute: '{geoattributestoredata}',
                function: '{functionstoredata}'
            }, get: function (data) {
                if (data.theThematism && data.attribute && data.geoattribute && data.function) {
                    var analisysType = data.theThematism.getAnalysisType();
                    var geoattribute = data.theThematism.get('attribute');
                    var source = data.theThematism.get('type');
                    var attribute = data.theThematism.getTargetClassAttribute();
                    var name = data.theThematism.get('name');

                    this.set('values.name', name);
                    this.set('values.analysistype', analisysType);
                    this.set('values.geoattribute', geoattribute);
                    this.set('values.source', source);
                    this.set('values.attributecombo', attribute);
                }
            }
        },

        buttonsDisabled: {
            bind: {
                name: '{values.name}',
                analisysType: '{values.analysistype}',
                source: '{values.source}',
                geoAttribute: '{values.geoattribute}',
                attributeCombo: '{values.attributecombo}'
            },
            get: function (data) {
                if (data.analisysType && data.source && data.geoAttribute && data.attributeCombo && data.name) {
                    this.getView().lookupViewModel('thematisms-panel').set('buttonsDisabled', false);
                    return;
                }
                this.getView().lookupViewModel('thematisms-panel').set('buttonsDisabled', true);
            }

        }
    },

    stores: {
        analysistypes: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: [/* {
                label: CMDBuildUI.locales.Locales.thematism.intervals, // 'intervals'
                value: CMDBuildUI.model.thematisms.Thematism.analysistypes.intervals
            }, */ {
                    label: CMDBuildUI.locales.Locales.thematism.punctual, // 'punctual',
                    value: CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual
                }/* , {
                label: CMDBuildUI.locales.Locales.thematism.graduated, // 'graduated',
                value: CMDBuildUI.model.thematisms.Thematism.analysistypes.graduated
            } */]
        },

        sources: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: [{
                label: CMDBuildUI.locales.Locales.thematism.table, // 'Table'
                value: CMDBuildUI.model.thematisms.Thematism.sources.table
            }/* , {
                label: CMDBuildUI.locales.Locales.thematism.function, // 'Function',
                value: CMDBuildUI.model.thematisms.Thematism.sources.function
            } */]
        },

        attributesstore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{attributesstoredata}'
        },

        geoAttributes: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{geoattributestoredata}'
        },
        functionstore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{functionstoredata}'
        }
    }

});

Ext.define('CMDBuildUI.view.processes.instances.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.processes-instances-grid',

    data: {
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
        objectTypeName: null,
        isModelLoaded: false,
        search: {
            value: null
        },
        selected: null,
        addbtn: {
            disabled: true,
            hidden: true
        },
        storedata: {
            autoload: false
        },
        statuscombo: {
            hidden: true,
            disabled: true,
            value: null,
            store: {
                autoLoad: false
            }
        }
    },

    formulas: {

        /**
         * Updata view model data
         */
        updateData: {
            bind: {
                typename: '{objectTypeName}'
            },
            get: function (data) {
                // update translations
                if (data.typename) {
                    var process = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(data.typename);

                    // set model name
                    this.set("storedata.modelname", CMDBuildUI.util.helper.ModelHelper.getModelName(
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                        data.typename
                    ));

                    this.set("title", CMDBuildUI.locales.Locales.processes.workflow + ' ' + process.getTranslatedDescription());

                    // set proxy url
                    this.set("storedata.proxyurl", CMDBuildUI.util.api.Processes.getAllInstancesActivitiesUrl(data.typename));

                    // set sorters
                    var sorters = [];
                    if (process && process.defaultOrder().getCount()) {
                        process.defaultOrder().getRange().forEach(function (o) {
                            sorters.push({
                                property: o.get("attribute"),
                                direction: o.get("direction") === "descending" ? "DESC" : 'ASC'
                            });
                        });
                    }
                    this.set("storedata.sorters", sorters);

                    // set auto load to true
                    this.set("storedata.autoload", true);

                    // add button
                    this.set("addbtn.text", CMDBuildUI.locales.Locales.processes.startworkflow + ' ' + process.getTranslatedDescription());
                    this.set("addbtn.hidden", !this.getView().getShowAddButton());

                    // filters
                    this.set("allowfilter", this.getView().getAllowFilter());
                }
            }
        },

        updateStatusCombo: {
            bind: {
                allowfilter: '{allowfilter}',
                ismodelloaded: '{isModelLoaded}',
                typename: '{objectTypeName}'
            },
            get: function (data) {
                this.set("statuscombo.hidden", !data.allowfilter);

                if (data.ismodelloaded) {
                    var fieldname = CMDBuildUI.model.processes.Process.flowstatus.field;
                    var lt = CMDBuildUI.model.processes.Process.flowstatus.lookuptype;

                    // check if process has custom status attribute
                    var process = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(data.typename);
                    if (process.get("flowStatusAttr")) {
                        var flowStatusAttr = process.get("flowStatusAttr");
                        // get model
                        var modelName = CMDBuildUI.util.helper.ModelHelper.getModelName(
                            CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
                            data.typename
                        );
                        var model = Ext.ClassManager.get(modelName);
                        var field = model.getField(flowStatusAttr);
                        if (field && field.cmdbuildtype === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup) {
                            lt = field.attributeconf.lookupType;
                            fieldname = field.name;
                            //TODO: manage filters
                        }
                    } else {
                        var record = this.getView().getOpenRunningStatusValue();
                        if (record) {
                            this.set("statuscombo.value", record.getId());
                        }
                    }
                    this.set("statuscombo.store.proxyurl", CMDBuildUI.util.api.Lookups.getLookupValues(lt));
                    this.set("statuscombo.store.autoload", true);
                    this.set("statuscombo.disabled", false);
                    this.set("statuscombo.field", fieldname);
                    this.set("statuscombo.lookuptype", lt);
                }
            }
        }
    },

    stores: {
        instances: {
            type: 'processes-instances',
            model: '{storedata.modelname}',
            proxy: {
                type: 'baseproxy',
                url: '{storedata.proxyurl}'
            },
            sorters: '{storedata.sorters}',
            autoLoad: '{storedata.autoload}',
            autoDestroy: true
        },

        /**
         * Status combo store definition
         */
        statuscombostore: {
            model: 'CMDBuildUI.model.lookups.Lookup',
            proxy: {
                type: 'baseproxy',
                url: '{statuscombo.store.proxyurl}'
            },
            autoLoad: '{statuscombo.store.autoload}',
            remoteFilter: false,
            autoDestroy: true,
            listeners: {
                load: function (store, records, successful, operation, eOpts) {
                    store.add({
                        _id: '__ALL__',
                        _type: '_FAKELOOKUP_',
                        code: '__ALL__',
                        description: CMDBuildUI.locales.Locales.processes.allstatuses
                    });
                }
            }
        }
    }

});

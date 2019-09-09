Ext.define('CMDBuildUI.view.relations.fieldset.FieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-fieldset',

    data: {
        // title: null,
        basetitle: null,
        isprocess: false,
        domain: null,
        direction: null,
        current: {
            objectType: null,
            objectTypeName: null,
            objectId: null
        },
        targetmodel: null,
        targetitem: null,
        storeinfo: {
            autoLoad: false
        },
        recordscount: 0
    },

    formulas: {
        updatedata: {
            bind: {
                domain: '{domain}',
                direction: '{direction}',
                current: '{current}'
            },
            get: function (data) {
                if (data.domain) {
                    var me = this;
                    var destinationtype, destinationtypename, sourcetypename;
                    if (data.direction === "_1") {
                        this.set("basetitle", data.domain.get("_descriptionInverse_translation"));
                        destinationtype = data.domain.get("sourceProcess") ?
                            CMDBuildUI.util.helper.ModelHelper.objecttypes.process :
                            CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                        destinationtypename = data.domain.get("source");
                        sourcetypename = data.domain.get("destination");
                    } else if (data.direction === "_2") {
                        this.set("basetitle", data.domain.get("_descriptionDirect_translation"));
                        destinationtype = data.domain.get("destinationProcess") ?
                            CMDBuildUI.util.helper.ModelHelper.objecttypes.process :
                            CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                        destinationtypename = data.domain.get("destination");
                        sourcetypename = data.domain.get("source");
                    }

                    // load model
                    CMDBuildUI.util.helper.ModelHelper.getModel(destinationtype, destinationtypename).then(function (model) {
                        me.set("targettype", destinationtype);
                        me.set("targettypename", destinationtypename);
                        me.set("targetmodel", model);
                        // set model name
                        me.set("storeinfo.model", model.getName());

                        // set store type and proxy
                        var storetype = 'classes-cards';
                        var proxyurl = model.getProxy().getUrl();
                        if (destinationtype === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                            storetype = 'processes-instances';
                            proxyurl = proxyurl.replace("/instances", "/instance_activities");
                            me.set("isprocess", true);
                        }
                        me.set("storeinfo.type", storetype);
                        me.set("storeinfo.proxyurl", proxyurl);

                        // set advanced filter
                        me.set("storeinfo.advancedfilter", {
                            relation: [{
                                domain: data.domain.getId(),
                                type: "oneof",
                                destination: destinationtypename,
                                source: sourcetypename,
                                direction: data.direction,
                                cards: [{
                                    className: data.current.objectTypeName,
                                    id: data.current.objectId
                                }]
                            }]
                        });

                        var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(destinationtypename, destinationtype);
                        me.set("targetitem", item);
                        // set sorters
                        var sorters = [];
                        if (item && item.defaultOrder().getCount()) {
                            item.defaultOrder().getRange().forEach(function (o) {
                                sorters.push({
                                    property: o.get("attribute"),
                                    direction: o.get("direction") === "descending" ? "DESC" : 'ASC'
                                });
                            });
                        } else {
                            sorters.push({
                                property: 'Description',
                                direction: 'ASC'
                            });
                        }
                        me.set("storeinfo.sorters", sorters);

                        // set autoload
                        me.set("storeinfo.autoload", true);

                        // add button
                        me.set("addrelationbtn.disabled", false);
                    });
                }
            }
        },
        title: {
            bind: {
                basetitle: '{basetitle}',
                count: '{recordscount}'
            },
            get: function(data) {
                var title = data.basetitle;
                if (data.count !== undefined) {
                    title += " (" + data.count + ")";
                }
                return title;
            }
        }
    },

    stores: {
        records: {
            type: '{storeinfo.type}',
            model: '{storeinfo.model}',
            autoLoad: '{storeinfo.autoload}',
            autoDestroy: true,
            proxy: {
                type: 'baseproxy',
                url: '{storeinfo.proxyurl}'
            },
            advancedFilter: '{storeinfo.advancedfilter}',
            sorters: '{storeinfo.sorters}'
        }
    }
});

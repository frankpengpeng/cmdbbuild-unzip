Ext.define('CMDBuildUI.view.relations.masterdetail.TabModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-masterdetail-tab',

    data: {
        domain: null,
        targetType: null,
        targetTypeName: null,
        storeInfo: {
            autoLoad: false,
            advancedfilter: null
        },
        addbutton: {
            hidden: true,
            text: null,
            disabled: true
        },
        search: {
            value: null
        }
    },

    formulas: {
        /**
         * Updates store configuration depending on type name and domain.
         */
        updateStoreInfo: {
            bind: {
                targettype: '{targetType}',
                targettypename: '{targetTypeName}',
                domain: '{domain}'
            },
            get: function (data) {
                if (data.targettype && data.targettypename && data.domain) {
                    var me = this;

                    // update tab title
                    this.set("mddescription", data.domain.getTranslatedDescriptionMasterDetail());
                    this.set("mditems", 0);

                    CMDBuildUI.util.helper.ModelHelper.getModel(data.targettype, data.targettypename).then(function (model) {
                        // set model name
                        me.set("storeInfo.model", model.getName());

                        // set store type
                        var proxytype = 'classes-cards';
                        var proxyurl = model.getProxy().getUrl();
                        if (data.targettype === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                            proxytype = 'processes-instances';
                            proxyurl = proxyurl.replace("/instances", "/instance_activities");
                        } else {
                            me.set("addbutton.hidden", false);
                        }
                        me.set("storeInfo.type", proxytype);
                        me.set("storeInfo.proxyurl", proxyurl);

                        // set store proxy
                        var source, destination, direction;
                        if (data.domain.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.onetomany) {
                            source = data.domain.get("destination");
                            destination = data.domain.get("source");
                            direction = "_2";
                        } else {
                            source = data.domain.get("source");
                            destination = data.domain.get("destination");
                            direction = "_1";
                        }

                        var sorters = [];
                        if (!data.domain.get('fkDomain')) {
                            me.set("storeInfo.advancedfilter", {
                                relation: [{
                                    domain: data.domain.get('name'),
                                    type: "oneof",
                                    destination: destination,
                                    source: source,
                                    direction: direction,
                                    cards: [{
                                        className: me.get("objectTypeName"),
                                        id: me.get("objectId")
                                    }]
                                }]
                            });
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
                        } else {
                            me.set("storeInfo.advancedfilter", {
                                attribute: {
                                    simple: {
                                        attribute: data.domain.get('fk_attribute_name'),
                                        operator: "equal",
                                        value: me.get("objectId")
                                    }
                                }
                            });
                        }

                        // set autoload
                        me.set("storeInfo.autoload", true);

                        var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.targettypename, data.targettype);
                        // update target type description
                        var addbuttontext = Ext.String.format(
                            '{0} {1}',
                            CMDBuildUI.locales.Locales.relations.adddetail,
                            item.getTranslatedDescription()
                        );
                        me.set("addbutton.text", addbuttontext);
                        me.set("addbutton.disabled", !(me.get("basepermissions.edit") && item.get(CMDBuildUI.model.base.Base.permissions.edit)));

                        // set sorters
                        me.set("storeInfo.sorters", sorters);

                        // fire data changed event
                        var view = me.getView();
                        view.fireEvent("targetdataupdated", view, view.getTargetTypeObject(), model, data.domain);
                    });
                }
            }
        },
        title: {
            bind: {
                mddescription: '{mddescription}',
                items: '{mditems}'
            },
            get: function (data) {
                return Ext.String.format(
                    "{0} <br /> <small>{1} {2}</small>",
                    data.mddescription,
                    data.items || 0,
                    CMDBuildUI.locales.Locales.relations.mditems
                );
            }
        }
    },

    stores: {
        records: {
            type: '{storeInfo.type}',
            model: '{storeInfo.model}',
            autoLoad: '{storeInfo.autoload}',
            autoDestroy: true,
            proxy: {
                type: 'baseproxy',
                url: '{storeInfo.proxyurl}'
            },
            advancedFilter: '{storeInfo.advancedfilter}',
            sorters: '{storeInfo.sorters}'
        }
    }

});
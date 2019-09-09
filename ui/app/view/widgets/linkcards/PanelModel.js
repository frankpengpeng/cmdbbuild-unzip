Ext.define('CMDBuildUI.view.widgets.linkcards.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.widgets-linkcards-panel',

    data: {
        objectType: null,
        objectTypeName: null,
        model: null,
        disablegridfilter: false,
        selection: null,
        storeinfo: {
            advancedfilter: null
        }
    },

    formulas: {
        /**
         * Get model name.
         */
        modelName: {
            bind: '{model}',
            get: function (model) {
                return model ? model.getName() : null;
            }
        },

        /**
         * Disable button view card/process instace.
         */
        disableViewAction: {
            bind: '{theSession}',
            get: function () {
                return false;
            }
        },

        /**
         * Disable action edit card/process instace.
         */
        disableEditAction: {
            bind: {
                canedit: '{theWidget.AllowCardEditing}',
                objecttype: '{objectType}',
                objecttypename: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objecttypename && data.objecttype) {
                    var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objecttypename, data.objecttype);
                    return !obj.get(CMDBuildUI.model.base.Base.permissions.edit) || (data.canedit === undefined || data.canedit === null || data.canedit === "0" || data.canedit === "false" || data.canedit === false);
                }
                return true;
            }
        },

        textTogglefilter: {
            bind: {
                disablegridfilter: '{disablegridfilter}'
            },
            get: function (data) {
                if (data.disablegridfilter) {
                    return CMDBuildUI.locales.Locales.widgets.linkcards.togglefilterenabled;
                }
                return CMDBuildUI.locales.Locales.widgets.linkcards.togglefilterdisabled;
            }
        },

        /**
         * Disable toggle filter button.
         */
        disableTogglefilter: {
            bind: {
                disable: '{theWidget.DisableGridFilterToggler}',
                filter: '{theWidget._Filter_ecql}'
            },
            get: function (data) {
                return data.disable || !data.filter ? true : false;
            }
        },

        /**
         * Text refresh selection
         */
        textRefreshselection: {
            bind: {},
            get: function (data) {
                return CMDBuildUI.locales.Locales.widgets.linkcards.refreshselection;
            }
        },
        // textClosebtn
        /**
         * Disable refresh selection button.
         */
        disableRefreshselection: {
            bind: {
                defaultselection: '{theWidget._DefaultSelection_ecql}',
                noselect: '{theWidget.NoSelect}'
            },
            get: function (data) {
                return !data.defaultselection || data.noselect ? true : false;
            }
        },

        /**
         * Text close button
         */
        textClosebtn: {
            bind: {},
            get: function (data) {
                return CMDBuildUI.locales.Locales.common.actions.close;
            }
        },

        /**
         * Calculate store type.
         */
        storeType: {
            bind: {
                objecttype: '{objectType}',
                modelname: '{modelName}'
            },
            get: function (data) {
                if (!data.modelname) {
                    return;
                }
                switch (data.objecttype) {
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                        return 'classes-cards';
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                        return 'processes-instances';
                    default:
                        return null;
                }
            }
        },

        /**
         * Get store proxy definition.
         */
        storeProxy: {
            bind: {
                model: '{model}',
                filter: '{theWidget._Filter_ecql}',
                target: '{theTarget}'
            },
            get: function (data) {
                // return empty proxy id model is not yet defined
                if (!data.model) {
                    return {};
                }

                // get proxy configuration
                var proxyconfig = Ext.clone(data.model.proxyConfig);

                if (data.filter) {
                    // calculate ecql
                    var ecql = CMDBuildUI.util.ecql.Resolver.resolve(
                        data.filter,
                        data.target
                    );
                    if (ecql) {
                        this.set("storeinfo.advancedfilter", {
                            ecql: ecql
                        });
                    }
                }

                // return proxy
                return proxyconfig;
            }
        },

        /**
         * Get store autoLoad value.
         */
        storeAutoLoad: {
            bind: '{storeProxy}',
            get: function (proxy) {
                return proxy ? true : false;
            }
        },

        /**
         * Update selected data from target output variable.
         */
        updateSelectedFromTarget: {
            bind: {
                target: '{theTarget}',
                rows: '{gridrows.totalCount}'
            },
            get: function (data) {
                var view = this.getView();
                if (data.rows && data.target && view.getOutput()) {
                    var output = data.target.get(view.getOutput());
                    if (!Ext.isEmpty(output)) {
                        var selection = [];
                        var gridstore = this.get("gridrows");
                        output.forEach(function (item) {
                            var r = gridstore.getById(item._id);
                            if (r) {
                                selection.push(r);
                            }
                        });
                        this.set("selection", selection);
                    }
                }
            }
        }
    },

    stores: {
        // grid data
        gridrows: {
            type: '{storeType}',
            model: '{modelName}',
            autoLoad: '{storeAutoLoad}',
            proxy: '{storeProxy}',
            advancedFilter: '{storeinfo.advancedfilter}',
            autoDestroy: true
        }
    }

});
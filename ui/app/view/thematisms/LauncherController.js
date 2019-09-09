Ext.define('CMDBuildUI.view.thematisms.LauncherController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.thematisms-launcher',
    listen: {
        component: {
            '#mainbtnThematism': {
                beforerender: 'onMainBtnBeforeRender'
            },
            '#clearthematismtool': {
                click: 'onClearThematismToolClick'
            },
            '#thematismdesc': {
                click: 'onOpenMenuClick'
            }
        }
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onMainBtnBeforeRender: function (button, eOpts) {
        var vm = this.getViewModel();
        var me = this;

        function initMenu(thematisms) {
            if (thematisms && thematisms.length) {
                button.on("click", me.onOpenMenuClick, me);
            } else {
                button.on("click", me.onAddNewThematismClick, me);
            }
        }

        vm.bind({
            bindTo: {
                objectTypeName: '{objectTypeName}',
                objectType: '{objectType}'
            }
        }, function (data) {
            if (data.objectTypeName && data.objectType) {
                var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objectTypeName, data.objectType);
                vm.set("item", item);
                item.getThematisms().then(function (thematisms) {
                    initMenu(thematisms.getRange());
                });
            }
        });
    },

    /**
     * Open a popup to create a new filter
     */
    onAddNewThematismClick: function () {
        var vm = this.getViewModel();
        var thematism = Ext.create('CMDBuildUI.model.thematisms.Thematism', {
            name: CMDBuildUI.locales.Locales.thematism.newThematism,
            description: CMDBuildUI.locales.Locales.thematism.newThematism,
            owner: vm.get('objectTypeName'),
            analysistype: CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual,
            type: CMDBuildUI.model.thematisms.Thematism.sources.table
        });
        this.editThematism(thematism);
    },

    onOpenMenuClick: function () {
        var view = this.getView();
        if (!view.isMenuExpanded) {
            view.getMenu().getViewModel().setStores({ thematisms: this.getViewModel().get("item").thematisms() });
            view.expandMenu();
        }
    },

    /**
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism
     */
    editThematism: function (thematism) {
        var me = this;
        var vm = this.getViewModel();
        var viewmodel = {
            data: {
                objectType: vm.get("objectType"),
                objectTypeName: vm.get("objectTypeName"),
                theThematism: thematism
            }
        };

        // popup definition
        var popup = CMDBuildUI.util.Utilities.openPopup(null, thematism.get("name"), {
            xtype: 'thematisms-panel',
            viewModel: viewmodel,
            listeners: {
                /**
                 * 
                 * @param {CMDBuildUI.view.thematisms.Panel} panel 
                 * @param {CMDBuildUI.model.themtism.Thematism} thematism 
                 * @param {Object} eOpts 
                 */
                applythematism: function (panel, thematism, eOpts) {
                    applyThematismHandler = function () {
                        me.updateRules(thematism, panel.down('thematisms-thematism-rules').getViewModel().get('legendstore'));
                        thematism.set('tryRules', true);
                        me.onApplyThematism(thematism, true);
                        popup.close();
                    }

                    if (thematism.hasRules()) {
                        applyThematismHandler();
                    } else {
                        me.calculateRulesHandler(panel, thematism, eOpts, applyThematismHandler, me);
                    }
                },
                /**
                 * 
                 * @param {CMDBuildUI.view.thematisms.Panel} panel 
                 * @param {CMDBuildUI.model.themtism.Thematism} thematism 
                 * @param {Object} eOpts 
                 */
                saveandapplythematism: function (panel, thematism, eOpts) {
                    saveAndApplyHandler = function () {
                        me.updateRules(thematism, panel.down('thematisms-thematism-rules').getViewModel().get('legendstore'));
                        thematism.set('tryRules', false);
                        me.onSaveAndApplyThematism(thematism);
                        popup.close();
                    }

                    if (thematism.hasRules()) {
                        saveAndApplyHandler();
                    } else {
                        me.calculateRulesHandler(panel, thematism, eOpts, saveAndApplyHandler, me);
                    }
                },

                /**
                 * 
                 * @param {CMDBuildUI.view.thematisms.Panel} panel 
                 * @param {CMDBuildUI.model.themtism.Thematism} thematism 
                 * @param {Object} eOpts 
                 */
                calculaterules: function (panel, thematism, eOpts) {
                    me.calculateRulesHandler(panel, thematism, eOpts);
                },

                /**
                * Custom event to close popup directly from popup
                * @param {Object} eOpts 
                */
                popupclose: function (panel, thematism, eOpts) {
                    thematism.reject(true);
                    // thematism.rules().rejectChanges();
                    popup.close();
                }
            }
        });
    },

    /**
     * 
     * @param {CMDBuildUI.view.thematisms.Panel} panel 
     * @param {CMDBuildUI.model.themtism.Thematism} thematism 
     * @param {Object} eOpts 
     * @param {Function} callback
     * @param {Object} scope
     */
    calculateRulesHandler: function (panel, thematism, eOpts, callback, scope) {
        var me = this;
        var lm = CMDBuildUI.util.Utilities.addLoadMask(panel.lookupReference('rules'));

        me.getView().fillThematism(panel.lookupReference('thematisms-thematism-row').getFields(), thematism);
        me.onCalculateRules(thematism, function () {
            CMDBuildUI.util.Utilities.removeLoadMask(lm);
            if (callback) {
                callback.call(scope);
            }
        }, this);
    },

    updateRules: function (thematism, legendStore) {
        var baseRecord = thematism.rules().getRange()[0];
        var newRules = [];

        legendStore.getRange().forEach(function (record) {
            var value = record.get('value');
            var color = record.get('color');
            var ruleRecord = thematism.rules().findRecord('value', value);

            if (ruleRecord) {
                ruleRecord.get('style').color = color;
            } else {
                var copyRecord = {
                    attribute: baseRecord.get('attribute'),
                    operator: baseRecord.get('operator'),
                    value: [value],
                    style: {
                        color: color
                    }
                }
                newRules.push(copyRecord);
            }
        });

        if (newRules.length) {
            thematism.rules().insert(0,newRules);
        }
    },

    /**
     * 
     * @param {CMDBuildUI.model.thematism.Thematism} thematism 
     */
    deleteThematism: function (thematism) {
        if (this.getViewModel().get("appliedthematism.id") === thematism.getId()) {
            this.onClearThematismToolClick();
        }
        thematism.erase();
    },
    /**
     * 
     * @param {CMDBuildUI.model.thematism.Thematism} thematism 
     * @param {Boolean} calltype tells if the apply comes from an apply or saveandApply event
     */
    onApplyThematism: function (thematism, calltype) {
        var me = this;
        me.applyThematism(thematism, calltype);

        var button = me.lookup("mainbtn");
        button.un("click", me.onAddNewThematismClick, me);
        button.on("click", me.onOpenMenuClick, me);
    },

    /**
     * 
     * @param {CMDBuildUI.model.themtism.Thematism} thematism 
     */
    onSaveAndApplyThematism: function (thematism) {
        thematism.save({
            success: function (record, operation) {
                this.onApplyThematism(thematism);
            }, scope: this
        });
    },

    /**
     * 
     * @param {Ext.panel.Tool} tool 
     * @param {Ext.event.Event} e 
     * @param {CMDBuildUI.view.thematisms.Launcher} owner 
     * @param {Object} eOpts 
     */
    onClearThematismToolClick: function (tool, e, owner, eOpts) {
        this.getView().clearThematism();
        CMDBuildUI.map.util.Util.clearThematism();
    },

    /**
     * 
     * @param {Ext.view.Table} grid 
     * @param {HTMLElement} td 
     * @param {Number} cellIndex 
     * @param {CMDBuildUI.model.thematisms.Thematism} record 
     * @param {HTMLElement} tr 
     * @param {Number} rowIndex 
     * @param {Ext.event.Event} e 
     * @param {Object} eOpts 
     */
    onThematismGridCellClick: function (grid, td, cellIndex, record, tr, rowIndex, e, eOpts) {
        if (cellIndex === 0) {
            this.onClearThematismToolClick();
            this.applyThematism(record, record.get('tryRules'));
            this.getView().collapseMenu();
        }
    },

    /**
     * 
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism 
     * @param {Function} callback
     * @param {Object} scope
     */
    onCalculateRules: function (thematism, callback, scope) {
        CMDBuildUI.thematisms.util.Util.calculateRules(thematism,
            /**
             * 
             * @param {CMDBuildUI.model.thematisms.Thematism} thematism 
             * @param {[CMDBuildUI.model.thematisms.Rules]} rules 
             */
            function (thematism, rules) {
                thematism.rules().clearData();
                thematism.rules().insert(0, rules);
                // thematism.stringifyThematism();
                if (callback) {
                    callback.call(scope || this);
                }
            }, this);
    },

    privates: {

        /**
         * @param {CMDBuildUI.model.thematisms.Thematism} thematism 
         * @param {Boolean} calltype tells if the apply comes from an apply or saveandApply event
         */
        applyThematism: function (thematism, calltype) {
            var me = this,
                view = me.getView();
            var vm = this.getViewModel();

            function applyThematism() {
                //TODO: apply the thematism in the map
                CMDBuildUI.map.util.Util.applyThematism(thematism, calltype);

                //have to add a new record in the thematisms store of the class
                var thematisms = vm.get("item").thematisms();
                var thematismposition = thematisms.findRecord('_id', thematism.getId());
                if (!thematismposition || thematismposition === -1) {
                    // thematismposition.add(thematism)
                    thematisms.add(thematism);
                }

                vm.set('appliedthematism.id', thematism.getId());
                vm.set('appliedthematism.description', thematism.get('description'));
            }

            applyThematism();
        }
    }
});

Ext.define('CMDBuildUI.view.processes.instances.instance.Mixin', {
    mixinId: 'processinstance-mixin',

    showForm: Ext.emptyFn,

    config: {
        buttons: [{
            reference: 'saveBtn',
            itemId: 'saveBtn',
            text: CMDBuildUI.locales.Locales.common.actions.save,
            ui: 'management-action-small',
            bind: {
                hidden: '{!showSaveButton}'
            },
            autoEl: {
                'data-testid': 'processinstance-save'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.save'
            }
        }, {
            reference: 'executeBtn',
            itemId: 'executeBtn',
            text: CMDBuildUI.locales.Locales.common.actions.execute,
            ui: 'management-action-small',
            formBind: true, //only enabled once the form is valid
            disabled: true,
            bind: {
                hidden: '{!showExecuteButton}'
            },
            autoEl: {
                'data-testid': 'processinstance-execute'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.execute'
            }
        }, {
            reference: 'cancelBtn',
            itemId: 'cancelBtn',
            ui: 'secondary-action-small',
            text: CMDBuildUI.locales.Locales.common.actions.cancel,
            autoEl: {
                'data-testid': 'processinstance-cancel'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
            }
        }]
    },

    /**
     * Load activity data and display form
     */
    loadActivity: function () {
        var vm = this.getViewModel();
        var me = this;

        // get model and set proxy url
        var activityModel = Ext.ClassManager.get("CMDBuildUI.model.processes.Activity");
        activityModel.getProxy().setUrl(
            CMDBuildUI.util.api.Processes.getInstanceActivitiesUrl(
                vm.get("objectTypeName"),
                vm.get("objectId")
            )
        );

        function loadActivity() {
            activityModel.load(vm.get("activityId"), {
                success: function (record, operation) {
                    vm.set("theActivity", record);
    
                    // get the process definition
                    vm.set("theProcess", CMDBuildUI.util.helper.ModelHelper.getProcessFromName(vm.get("objectTypeName")));
    
                    // render form
                    me.showForm();
                },
                scope: me
            });
        }

        if (vm.get("activityId")) {
            loadActivity();
        } else {
            var activities = Ext.create("Ext.data.Store", {
                model: activityModel.getName(),
                proxy: activityModel.getProxy(),
                autoLoad: false,
                autoDestroy: true
            });
            activities.load({
                callback: function(records) {
                    if (records && records.length === 1) {
                        vm.set("activityId", records[0].getId());
                        loadActivity();
                    }
                }
            });
        }
    },

    /**
     * 
     * @return {Object} 
     */
    getProcessStatusBar: function () {
        var config;
        var vm = this.getViewModel();
        var model = vm.get("objectModel");

        // get flow status attribute configuration
        var flowStatusAttr = vm.get("theProcess").get("flowStatusAttr");
        if (flowStatusAttr) {
            var attr = model.getField(flowStatusAttr);
            config = {
                type: attr.attributeconf.lookupType,
                field: flowStatusAttr
            };
        } else {
            // return default values
            config = {
                type: 'FlowStatus',
                field: 'status'
            };
        }
        return {
            xtype: 'statuses-progress-bar',
            lookuptype: config.type,
            bind: {
                lookupvalue: '{theObject.' + config.field + '}'
            }
        };
    },

    /**
     * Get attributes configuration from activity
     * @return {Object} containing visibleAttributes and overrides
     */
    getAttributesConfigFromActivity: function () {
        var visibleAttributes = [],
            overrides = {};
        var vm = this.getViewModel();

        var messageAttr = this.getMessageAttribute();
        // iterate on activity attributes
        Ext.Array.each(vm.get("theActivity").attributes().getRange(), function (attr, index) {
            if (attr.getId() !== messageAttr) {
                // TODO: check permissions
                visibleAttributes.push(attr.getId());
                overrides[attr.getId()] = attr.getData();
            }
        });
        // hide save and execute buttons if there are not visible attributes
        if (!visibleAttributes.length) {
            var savebtn = this.lookupReference("saveBtn");
            if (savebtn) {
                savebtn.setHidden(true);
            }
            var executebtn = this.lookupReference("executeBtn");
            if (executebtn) {
                executebtn.setHidden(true);
            }
        }
        return {
            visibleAttributes: visibleAttributes,
            overrides: overrides
        };
    },

    /**
     * Get action field
     * 
     * @return {Ext.form.field.ComboBox}
     */
    getActionField: function () {
        var vm = this.getViewModel();
        var bindvalue;

        var model = vm.get("objectModel");

        // iterate on activity attributes
        var action = Ext.Array.findBy(
            vm.get("theActivity").attributes().getRange(),
            function (attr, index) {
                return attr.get("action");
            });

        // create store and add it to viewmodel
        var store = {
            model: 'CMDBuildUI.model.lookups.Lookup'
        };
        if (action) {
            var fieldname = action.getId();
            vm.set("activity_action.fieldname", fieldname);

            // get action attribute lookup definition
            var attr = model.getField(fieldname);
            if (attr.cmdbuildtype.toLowerCase() === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase()) {
                // override attribute
                var attrclone = Ext.merge({}, attr, {
                    mandatory: true,
                    description: CMDBuildUI.locales.Locales.processes.action.label,
                    metadata: {
                        description_localized: CMDBuildUI.locales.Locales.processes.action.label
                    }
                });
                var formfield = CMDBuildUI.util.helper.FormHelper.getFormField(
                    attrclone, {
                        mode: this.formmode
                    });
                return {
                    layout: 'column',
                    items: [Ext.apply(formfield, {
                        cls: Ext.baseCSSPrefix + 'process-action-field',
                        padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                        fieldsDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                        labelAlign: "left",
                        columnWidth: 0.5,
                        flex: '0.5',
                        layout: 'anchor'
                    }), {
                        columnWidth: 0.5,
                        flex: '0.5',
                        margin: "10px 0 0", 
                        align: "right",
                        layout: 'hbox',
                        items: [{flex: 1}, this.getCurrentActivityInfo()]
                    }]
                };
            }
        }
        return {
            xtype: "displayfield",
            labelAlign: "left",
            cls: Ext.baseCSSPrefix + 'process-action-field',
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            fieldLabel: CMDBuildUI.locales.Locales.processes.action.label,
            value: CMDBuildUI.locales.Locales.processes.action.advance
        };
    },

    /**
     * 
     * @param {Object[]} items 
     * @return {Object}
     */
    getMainPanelForm: function (items) {
        return {
            flex: 1,
            layout: {
                type: 'hbox',
                align: 'stretch' //stretch vertically to parent
            },
            height: "100%",
            padding: "0 10 0 0",
            items: [{
                flex: 1,
                scrollable: "y",
                items: [{
                    items: items
                }]
            }, {
                xtype: 'widgets-launchers',
                formMode: this.formmode,
                bind: {
                    widgets: '{theActivity.widgets}'
                }
            }]
        };
    },

    /**
     * Get message box
     * 
     * @return {Object} Message box configuration
     */
    getMessageBox: function () {
        var vm = this.getViewModel();
        var conf;
        var model = vm.get("objectModel");
        var messageAttr = this.getMessageAttribute();
        if (messageAttr) {
            var hasmessage = vm.get("theActivity").attributes().find("_id", messageAttr) !== -1;
            if (hasmessage) {
                var field = model.getField(messageAttr);
                if (field.cmdbuildtype.toLowerCase() === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase()) {
                    messageAttr = Ext.String.format("_{0}_description_translation", messageAttr);
                } else if (field.cmdbuildtype.toLowerCase() === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase()) {
                    messageAttr = Ext.String.format("_{0}_description", messageAttr);
                }
                return {
                    xtype: 'container',
                    ui: 'messageinfo',
                    messageAttr: field.name,
                    hidden: true,
                    bind: {
                        html: Ext.String.format("{theObject.{0}}", messageAttr),
                        hidden: Ext.String.format("{!theObject.{0}}", field.name)
                    }
                };
            }
        }
        return conf;
    },

    /**
     * @return The string to use as activity info tooltip.
     */
    getCurrentActivityInfo: function () {
        var vm = this.lookupViewModel();
        var group = Ext.getStore("groups.Groups").findRecord("name", vm.get("theActivity").get("performer"));
        var html = Ext.String.format(
            "<small class=\"x-selectable\">{0} | {1}</small>",
            vm.get("theActivity").get("description"),
            group ? group.get("description") : ''
        );
        return {
            xtype: 'tbtext',
            itemId: 'infotool',
            reference: 'infotool',
            cls: Ext.baseCSSPrefix + 'process-infotool',
            iconCls: 'x-fa fa-info-circle',
            html: html,
            autoEl: {
                'data-testid': 'processes-instance-infotool'
            }
        };
    },

    /**
     * @param {Object} callback
     * @param {Function} callback.success
     * @param {Function} callback.failure
     * @param {Function} callback.callback
     */
    executeProcess: function (callback) {
        var vm = this.getViewModel();

        if (this.isValid()) {
            var theObject = vm.get("theObject");
            // set advance attribute
            theObject.set("_advance", true);
            theObject.set("_activity", vm.get("activityId"));
            // set type if empty
            if (!theObject.get("_type")) {
                theObject.set("_type", vm.get("objectTypeName"));
            }

            CMDBuildUI.util.Ajax.setActionId("proc.inst.execute");
            this.loadMask = CMDBuildUI.util.Utilities.addLoadMask(this);

            // save and advance
            function saveData(success) {
                if (success) {
                    theObject.save(callback);
                } else {
                    if (Ext.isFunction(callback)) {
                        Ext.callback(callback, null, [theObject, {}, false]);
                    } else if (Ext.isObject(callback)) {
                        if (Ext.isFunction(callback.failure)) {
                            Ext.callback(callback.failure, null, [theObject, {}]);
                        }
                        if (Ext.isFunction(callback.callback)) {
                            Ext.callback(callback.callback, null, [theObject, {}, false]);
                        }
                    }
                }
            }

            // update emails
            var panel = this.getParentTabPanel();
            if (panel) {
                panel.updateEmails(saveData);
            } else {
                saveData(true);
            }
        }
    },

    /**
     * @param {Object} callback
     * @param {Function} callback.success
     * @param {Function} callback.failure
     * @param {Function} callback.callback
     */
    saveProcess: function (callback) {
        var vm = this.getViewModel();

        var theObject = vm.get("theObject");
        // set advance attribute
        theObject.set("_advance", false);
        theObject.set("_activity", vm.get("activityId"));
        // set type if empty
        if (!theObject.get("_type")) {
            theObject.set("_type", vm.get("objectTypeName"));
        }

        CMDBuildUI.util.Ajax.setActionId("proc.inst.save");
        this.loadMask = CMDBuildUI.util.Utilities.addLoadMask(this);

        // save and advance
        function saveData() {
            theObject.save(callback);
        }

        // update emails
        var panel = this.getParentTabPanel();
        if (panel) {
            panel.updateEmails(saveData);
        } else {
            saveData();
        }
    },

    /**
     * Close detail window
     */
    closeDetailWindow: function () {
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
    },

    /**
     * Add rules for fields visibility
     */
    addConditionalVisibilityRules: function () {
        var fields = [];
        this.getForm().getFields().getRange().forEach(function (f) {
            if (f.updateFieldVisibility !== undefined) {
                // add field to list
                fields.push(f);
            }
        });

        this.getViewModel().bind({
            bindTo: '{theObject}',
            deep: true
        }, function (theObject) {
            fields.forEach(function (f) {
                // apply visibility function
                Ext.callback(f.updateFieldVisibility, f, [theObject]);
            });
        });
    },

    /**
     * Add rules for fields visibility
     */
    addAutoValueRules: function () {
        var vm = this.lookupViewModel();
        this.getForm().getFields().getRange().forEach(function (f) {
            if (f.setValueFromAutoValue !== undefined) {
                vm.bind(f.getAutoValueBind(), function (data) {
                    f.setValueFromAutoValue();
                });
            }
        });
    },

    /**
     * Return the Id of open running lookup state
     */
    getOpenRunningStatusId: function () {
        var type = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(CMDBuildUI.model.processes.Process.flowstatus.lookuptype);
        if (type) {
            return type.values().findRecord("code", "open.running").getId();
        }
        return;
    },

    privates: {
        /**
         * Return message attribute name
         */
        getMessageAttribute: function () {
            return this.getViewModel().get("theProcess").get("messageAttr");
        },

        /**
         * @return {Ext.tab.Panel}
         */
        getParentTabPanel: function () {
            return this.up("processes-instances-tabpanel");
        }
    }
});
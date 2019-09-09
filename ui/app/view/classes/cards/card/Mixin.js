Ext.define('CMDBuildUI.view.classes.cards.card.Mixin', {
    mixinId: 'card-mixin',

    config: {
        /**
         * @cfg {String} [objectType] 
         * 
         * The object type.
         */
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,

        /**
         * @cfg {String} [objectTypeName]
         * 
         * Class name
         */
        objectTypeName: null,

        /**
         * @cfg {Numeric} objectTypeId
         */
        objectId: null,

        /**
         * @cfg {Boolean} redirectAfterSave
         * 
         * `true` to open the created card after save action. 
         * Defaults to true.
         */
        redirectAfterSave: true,

        /**
         * @cfg {Boolean} fireGlobalEventsAfterSave
         * 
         * `true` to fire global events after save action. 
         * Defaults to true.
         */
        fireGlobalEventsAfterSave: true,

        /**
         * @cfg {Boolean} hideWidgets
         * Set to `true` to hide card widgets.
         */
        hideWidgets: false,

        /**
         * @cfg {Boolean} hideInlineElements
         * Set to `true` to hide inline elements (notes, attachments, domains).
         */
        hideInlineElements: true,

        /**
         * @cfg {String[]} overrideReadOnlyFields
         * 
         * An array of read-only attributes
         */
        overrideReadOnlyFields: []
    },

    publish: [
        'objectType',
        'objectTypeName',
        'objectId'
    ],

    twoWayBindable: [
        'objectType',
        'objectTypeName',
        'objectId'
    ],

    bind: {
        objectType: '{objectType}',
        objectTypeName: '{objectTypeName}',
        objectId: '{objectId}'
    },

    formmode: null,

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
     * Return form fields
     * 
     * @return {Ext.Component[]}
     */
    getDynFormFields: function () {
        var vm = this.getViewModel();
        var defaultValues, overrides = {};

        // get default values
        if (this.getDefaultValues) {
            defaultValues = this.getDefaultValues();
        }

        // get object overrides
        var obj = vm.get("theObject");
        if (obj) {
            overrides = obj.getOverridesFromPermissions();
        }
        var visibleAttributes;
        if (Ext.isObject(overrides)) {
            visibleAttributes = Object.keys(overrides);
        }

        // Override read-only property
        this.getOverrideReadOnlyFields().forEach(function (attr) {
            if (!overrides[attr]) {
                overrides[attr] = {};
            }
            overrides[attr].writable = false;
        });

        // return dynamic fields
        return CMDBuildUI.util.helper.FormHelper.renderForm(vm.get("objectModel"), {
            mode: this.formmode,
            defaultValues: defaultValues,
            attributesOverrides: overrides,
            visibleAttributes: visibleAttributes,
            showAsFieldsets: true
        });
    },

    privates: {
        /**
         * 
         * @param {Object[]} items 
         * @return {Object}
         */
        getMainPanelForm: function (items) {
            var me = this;
            var classObject = CMDBuildUI.util.helper.ModelHelper.getClassFromName(me.lookupViewModel().get("objectTypeName"));
            var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
            var showInlineElements = !this.getHideInlineElements() && !classObject.isSimpleClass();
            if (showInlineElements) {

                // add inline notes
                if (classObject.get("noteInline") && privileges.card_tab_note_access) {
                    items.push(me.getInlineNotesConfig(classObject.get("noteInlineClosed")));
                }

                // add inline attachments
                var configAttachments = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled);
                if (classObject.get("attachmentsInline") && privileges.card_tab_attachment_access && configAttachments && this.formmode !== CMDBuildUI.util.helper.FormHelper.formmodes.create) {
                    items.push(me.getInlineAttachmentsConfig(classObject.get("attachmentsInlineClosed")));
                }
            }

            // create panel
            var panelitems = [{
                flex: 1,
                scrollable: 'y',
                referece: 'fieldspanel',
                items: [{
                    items: items
                }],
                listeners: [{
                    added: function (panel, container, position, eOpts) {
                        if (showInlineElements) {
                            me.addInlineDomains(panel, classObject);
                        }
                    }
                }]
            }];

            if (!this.getHideWidgets()) {
                panelitems.push({
                    xtype: 'widgets-launchers',
                    formMode: this.formmode,
                    bind: {
                        widgets: '{classObject.widgets}'
                    }
                });
            }
            return {
                flex: 1,
                layout: {
                    type: 'hbox',
                    align: 'stretch'  //stretch vertically to parent
                },
                height: "100%",
                items: panelitems
            };
        },

        /**
         * 
         * @param {String[]} triggers 
         * @param {Object} api 
         */
        executeFormTriggers: function (triggers, api) {
            // execute form triggers
            triggers.forEach(function (triggerfunction) {
                var executeFormTrigger;
                var jsfn = Ext.String.format(
                    'executeFormTrigger = function(api) {{0}}',
                    triggerfunction
                );
                try {
                    eval(jsfn);
                } catch (e) {
                    CMDBuildUI.util.Logger.log(
                        "Error on trigger function.",
                        CMDBuildUI.util.Logger.levels.error,
                        null,
                        e
                    );
                    executeFormTrigger = Ext.emptyFn;
                }
                // use try / catch to manage errors
                try {
                    executeFormTrigger(api);
                } catch (e) {
                    CMDBuildUI.util.Logger.log(
                        "Error on execution of form trigger.",
                        CMDBuildUI.util.Logger.levels.error,
                        null,
                        {
                            fn: triggerfunction
                        }
                    );
                }
            });
        },

        /**
         * Initialize before action form triggers.
         * 
         * @param {String} action 
         * @param {Object} base_api 
         */
        initBeforeActionFormTriggers: function (action, base_api) {
            var me = this;
            var vm = this.getViewModel();
            var item = CMDBuildUI.util.helper.ModelHelper.getClassFromName(vm.get("objectTypeName"));
            if (item) {
                // get form triggers
                var triggers = item.getFormTriggersForAction(action);
                if (triggers && triggers.length) {
                    // bind object creation
                    vm.bind({
                        bindTo: {
                            theObject: '{theObject}'
                        }
                    }, function (data) {
                        var api = Ext.apply({
                            record: data.theObject
                        }, base_api);
                        me.executeFormTriggers(triggers, api);
                    });
                }
            }
        },

        /**
         * Execute after action form triggers.
         * 
         * @param {String} action 
         * @param {CMDBuildUI.model.classes.Card} record
         * @param {Object} base_api 
         */
        executeAfterActionFormTriggers: function (action, record, base_api) {
            var me = this;
            var vm = this.getViewModel();
            var item = CMDBuildUI.util.helper.ModelHelper.getClassFromName(vm.get("objectTypeName"));
            if (item) {
                // get form triggers
                var triggers = item.getFormTriggersForAction(action);
                if (triggers && triggers.length) {
                    var api = Ext.apply({
                        record: record
                    }, base_api);
                    me.executeFormTriggers(triggers, api);
                }
            }
        },

        /**
         * 
         * @param {Boolean} closed 
         */
        getInlineNotesConfig: function (closed) {
            var field;
            var bindvalue = "{theObject.Notes}";
            var containerbind = {};
            var containerhidden = false;
            if (this.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.read) {
                field = {
                    xtype: 'displayfield',
                    bind: {
                        value: bindvalue
                    }
                };
                containerbind = {
                    hidden: '{!theObject.Notes}'
                };
                containerhidden = true;
            } else {
                field = CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                    bind: {
                        value: bindvalue
                    }
                });
            }
            return {
                xtype: 'formpaginationfieldset',
                title: CMDBuildUI.locales.Locales.common.tabs.notes,
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                collapsed: closed,
                collapsible: true,
                hidden: containerhidden,
                items: [field],
                bind: containerbind
            };
        },

        /**
         * 
         * @param {Boolean} closed 
         */
        getInlineAttachmentsConfig: function (closed) {
            var vm = this.getViewModel();
            return {
                xtype: 'formpaginationfieldset',
                title: CMDBuildUI.locales.Locales.common.tabs.attachments,
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                collapsed: closed,
                collapsible: true,
                items: [{
                    xtype: "attachments-container",
                    viewModel: {
                        data: {
                            targetType: vm.get("objectType"),
                            targetTypeName: vm.get("objectTypeName"),
                            targetId: vm.get("objectId"),
                            disableActions: {
                                add: this.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.read,
                                edit: this.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.read,
                                delete: this.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.read
                            }
                        }
                    }
                }]
            };
        },

        addInlineDomains: function (panel, classObject) {
            var me = this;
            var vm = this.lookupViewModel();
            classObject.getDomains().then(function (domains) {
                domains.getRange().forEach(function (domain) {
                    if (domain.get("inline") &&
                        ((domain.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.manytomany) ||
                            (domain.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.onetomany && Ext.Array.contains(classObject.getHierarchy(), domain.get("source"))) ||
                            (domain.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.manytoone && Ext.Array.contains(classObject.getHierarchy(), domain.get("destination")))
                        )
                    ) {
                        panel.add(CMDBuildUI.view.relations.fieldset.Fieldset.getFieldsetConfig(
                            domain, 
                            {
                                objectType: vm.get("objectType"),
                                objectTypeName: vm.get("objectTypeName"),
                                objectId: vm.get("objectId")
                            }, 
                            {
                                collapsed: domain.get("defaultClosed"),
                                formmode: me.formmode
                            }
                        ));
                    }
                });
            });
        }

    }

});
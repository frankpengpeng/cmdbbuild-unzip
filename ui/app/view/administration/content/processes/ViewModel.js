Ext.define('CMDBuildUI.view.administration.content.processes.ViewModel', {
    extend: 'Ext.app.ViewModel',
    requires: [
        'CMDBuildUI.model.processes.Process'
    ],
    alias: 'viewmodel.administration-content-processes-view',
    data: {
        activeTab: 0,
        objectTypeName: null,
        theProcess: null,
        attributeGroups: [],
        action: null,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        disabledTabs: {
            properties: false,
            attributes: false,
            domains: false,
            tasks: false,
            layers: true,
            geoattributes: true
        },
        toolbarHiddenButtons: {
            edit: true, // action !== view
            delete: true, // action !== view
            enable: true, //action !== view && theProcess.active
            disable: true, // action !== view && !theProcess.active
            version: true // action !== view
        },
        checkboxNoteInlineClosed: {
            disabled: true
        },
        isMultitenantActive: false
    },

    formulas: {
        precessLabel: {
            bind: '{theProcess.description}',
            get: function () {                
                return CMDBuildUI.locales.Locales.administration.processes.toolbar.processLabel;
            }
        },
        tenantModes: {
            get: function () {
                return CMDBuildUI.model.users.Tenant.getTenantModes();
            }
        },
        attachmentsDescriptionModes: {
            get: function () {
                return [{
                    "value": "hidden",
                    "label": CMDBuildUI.locales.Locales.administration.common.strings.hidden // Hidden
                }, {
                    "value": "optional",
                    "label": CMDBuildUI.locales.Locales.administration.common.strings.visibleoptional // Visible optional
                }, {
                    "value": "mandatory",
                    "label": CMDBuildUI.locales.Locales.administration.common.strings.visiblemandatory // Visible madatory
                }];
            }
        },
        contexMenuTypes: {
            get: function () {
                return [{
                    'value': 'component',
                    'label': CMDBuildUI.locales.Locales.administration.classes.texts.component // Component
                }, {
                    'value': 'custom',
                    'label': CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.contextMenus.inputs.typeOrGuiCustom.values.custom.label // Custom
                }, {
                    'value': 'separator',
                    'label': CMDBuildUI.locales.Locales.administration.classes.texts.separator // Separator
                }];
            }
        },
        contextMenuApplicabilities: {
            get: function () {
                return [{
                    'value': 'one',
                    'label': CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.contextMenus.inputs.applicability.values.one.label
                }, {
                    'value': 'many',
                    'label': CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.contextMenus.inputs.applicability.values.many.label
                }, {
                    'value': 'all',
                    'label': CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.contextMenus.inputs.applicability.values.all.label
                }];
            }
        },
        updateCheckboxState: {
            bind: '{theProcess.noteInline}',
            get: function (data) {
                if (data) {
                    this.set('checkboxNoteInlineClosed.disabled', false);
                } else {
                    this.set('checkboxNoteInlineClosed.disabled', true);
                    this.set('theProcess.noteInlineClosed', false);
                }
            }
        },

        action: {
            bind: '{theProcess}',
            get: function (get) {
                this.set('activeTab', this.getView().up('administration-content').getViewModel().get('activeTabs.processes') || 0);
                if (this.get('actions.edit')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (this.get('actions.add')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
                this.configToolbarButtons();
                this.configDisabledTabs();
                var form = this.getView().down('administration-content-processes-tabitems-properties-fieldsets-generaldatafieldset').up('form').getForm();
                var nameField = form.findField('processnamefieldadd');
                nameField.maxLength = value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit ? Infinity : 20;

            }
        },
        getToolbarButtons: {
            bind: {
                actions: '{actions}',
                active: '{theProcess.active}'
            },
            get: function (get) {
                this.configDisabledTabs();

                this.set('toolbarHiddenButtons.edit', !get.actions.view);
                this.set('toolbarHiddenButtons.delete', !get.actions.view);
                this.set('toolbarHiddenButtons.enable', !get.actions.view || (get.actions.view && get.active));
                this.set('toolbarHiddenButtons.disable', !get.actions.view || (get.actions.view && !get.active));
                this.set('toolbarHiddenButtons.version', !get.actions.view);
            }
        },
        isSuperProcess: {
            bind: '{theProcess.prototype}',
            get: function (prototype) {
                if (prototype) {
                    return prototype;
                }
            }
        },

        isSimpleProcess: {
            bind: '{theProcess.type}',
            get: function (type) {
                if (type) {
                    return type === 'simple';
                }
            }
        },

        hideParentCombobox: {
            bind: {
                type: '{theProcess}',
                view: '{actions.view}'
            },
            get: function (data) {
                if (data.type) {
                    return (data.type === 'simple' || data.view === true) ? true : false;
                }
            }
        },

        hideParentDisplayfield: {
            bind: {
                type: '{theProcess.type}',
                view: '{actions.view}'
            },
            get: function (data) {
                if (data.type) {
                    return (data.type === 'simple' ||
                        (data.view === false && data.type === 'standard')
                    ) ? true : false;
                }
            }
        },

        getDefaultOrderData: {
            bind: '{theProcess.defaultOrder}',
            get: function (defaultOrder) {
                if (defaultOrder && defaultOrder.length) {
                    return defaultOrder;
                }
                return [];
            }
        },
        attributeGroupingModelName: {
            bind: '{theProcess}',
            get: function (theObject) {
                if (theObject) {
                    return 'CMDBuildUI.model.AttributeGrouping';
                }
            }
        },
        processModelName: {
            bind: '{theProcess}',
            get: function (theProcess) {
                if (theProcess) {
                    return 'CMDBuildUI.model.processes.Process';
                }
            }
        },

        lookupTypeModelName: {
            bind: '{theProcess}',
            get: function (theProcess) {
                if (theProcess) {
                    return 'CMDBuildUI.model.lookups.LookupType';
                }
            }
        },

        contextMenuItemModelName: {
            bind: '{theProcess}',
            get: function (theObject) {
                if (theObject) {
                    return 'CMDBuildUI.model.ContextMenuItem';
                }
            }
        },


        attributeOrderModelName: {
            bind: '{theProcess}',
            get: function (theProcess) {
                if (theProcess) {
                    return 'CMDBuildUI.model.AttributeOrder';
                }
            }
        },
        attributeProxy: {
            bind: '{theProcess.name}',
            get: function (objectTypeName) {
                if (objectTypeName && !this.get('theProcess').phantom) {
                    return {
                        url: Ext.String.format("/processes/{0}/attributes", objectTypeName),
                        type: 'baseproxy'
                    };
                }
            }
        },

        defaultFilterData: {
            bind: '{theProcess}',
            get: function (theProcess) {
                if (theProcess) {
                    this.set('defaultFilterData._id', theProcess.get('_id'));
                    this.set('defaultFilterData.name', theProcess.get('name'));
                }
            }
        },
        getContextMenuItemData: {
            bind: '{theProcess}',
            get: function (theProcess) {
                return (theProcess) ? theProcess.getAssociatedData().contextMenuItems : [];
            }
        },
        unorderedAttributes: {
            bind: '{theProcess}',
            get: function (theProcess) {
                if (theProcess) {
                    return [function (item) {
                        if (item.get('name') === 'tenantId' || item.get('name') === 'Notes') {
                            return false;
                        } else {
                            var defaultOrder = theProcess.getAssociatedData().defaultOrder;
                            for (var field in defaultOrder) {
                                if (defaultOrder[field].attribute === item.get('name')) {
                                    return false;
                                }
                            }
                            return true;
                        }

                    }];
                }
            }
        },

        ////////////////////////////////////// UNUSED

        // processParentStore: {
        //     bind: '{theProcess}',
        //     get: function (action) {
        //         return Ext.getStore('processes.Processes').filterBy(function (process) {
        //             return process.get('prototype') === true;
        //         });
        //     }
        // },

        attributeModelName: {
            bind: '{theProcess}',
            get: function (theProcess) {
                if (theProcess) {
                    return "CMDBuildUI.model.Attribute";
                }
            }
        },


        allAttributeProxy: {
            bind: '{theProcess}',
            get: function (theProcess) {
                if (theProcess.get('name')) {
                    return {
                        url: Ext.String.format("/processes/{0}/attributes", theProcess.get('name')),
                        type: 'baseproxy',
                        extraParams: {
                            limit: 0
                        }
                    };
                } else {

                    return {
                        type: 'memory'
                    };
                }
            }
        },
        contextMenuCount: {
            bind: '{theProcess.contextMenuItems}',
            get: function (contextMenuItems) {
                if (contextMenuItems) {
                    return contextMenuItems.data.items.length;
                }
                return 0;
            }
        },
        multitenantMode: {
            get: function (get) {
                var me = this;
                CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(
                    function (configs) {
                        var multitenantMode = Ext.Array.findBy(configs, function (item, index) {
                            return item._key === 'org__DOT__cmdbuild__DOT__multitenant__DOT__mode';
                        });
                        me.set('isMultitenantActive', multitenantMode.value !== 'DISABLED');
                    }
                );
            }
        },
        isMultitenant: {
            bind: {
                theProcess: '{theProcess}'
            },
            get: function (data) {

                return CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled) && !data.theProcess.get('prototype');
            }
        },
        isMultitenatModeHiddenCombo: {
            bind: {
                isMultitenant: '{isMultitenant}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isPrototype: '{theProcess.prototype}'
            },
            get: function (data) {

                return !(data.isMultitenant && (data.isEdit || data.isAdd) && !data.isPrototype);
            }
        },
        isMultitenatModeHiddenDisplay: {
            bind: {
                isMultitenant: '{isMultitenant}',
                isView: '{actions.view}',
                isPrototype: '{theProcess.prototype}'
            },
            get: function (data) {

                return !(data.isMultitenant && data.isView && !data.isPrototype);
            }
        }
    },


    stores: {
        processVersionStore: {
            alias: 'store.processVersion',
            proxy: {
                url: '/processes/{theProcess.name}/versions',
                type: 'baseproxy'
            },
            autoDestroy: true,
            pageSize: 0
        },

        defaultOrderStoreNew: {
            model: '{attributeOrderModelName}',
            alias: 'store.attribute-default-order',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: Ext.create('CMDBuildUI.model.AttributeOrder')
        },

        defaultOrderStore: {
            model: 'CMDBuildUI.model.AttributeOrder',
            alias: 'store.attribute-default-order',
            proxy: {
                type: 'memory'
            },
            data: '{getDefaultOrderData}',
            autoDestroy: true
        },
        defaultOrderDirectionsStore: {
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            data: [{
                'value': 'ascending',
                'label': 'Ascending' // TODO: translate
            },
            {
                'value': 'descending',
                'label': 'Descending' // TODO: translate
            }]            
        },
        attributesStore: {
            model: "CMDBuildUI.model.Attribute",
            proxy: '{attributeProxy}',
            fields: ['name', 'description'],
            pageSize: 0,
            autoLoad: true,
            autoDestroy: true,
            remoteFilter: false
        },

        unorderedAttributesStore: {
            source: '{attributesStore}',
            filters: '{unorderedAttributes}',
            sorters: ['description'],
            autoDestroy: true            
        },
        superprocessesStore: {
            model: '{processModelName}',
            source: 'processes.Processes',
            autoDestroy: true,
            autoLoad: true,
            sorters: ['description'],
            filters: [
                function (item) {
                    return item.get('prototype') === true;
                }
            ],
            pageSize: 0
        },

        attachmentTypeLookupStore: {
            model: '{lookupTypeModelName}',
            type: 'attachments-categories',
            fields: [{
                name: '_id',
                type: 'string'
            }, {
                name: 'name',
                type: 'string'
            }],

            proxy: {
                url: '/lookup_types/',
                type: 'baseproxy'
            },
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0
        },

        //////////////////////////////////////////////////////

        allAttributesStore: {
            model: '{attributeOrderModelName}',
            proxy: '{allAttributeProxy}',
            alias: 'store.allAttributesStore',
            autoLoad: true,
            //autoDestroy: true,
            pageSize: 0,
            sorters: [{
                property: 'description',
                direction: 'ASC'
            }]
        },

        allLookupAttributesStore: {
            source: '{attributesStore}',
            type: 'chained',
            filters: [
                function (item) {
                    return item.get('type') === 'lookup';
                }
            ],
            sorters: [{
                property: 'description',
                direction: 'ASC'
            }],
            autoDestroy: true
        },

        multitenantModeStore: {
            type: 'multitenant-multitenantmode',
            data: '{tenantModes}'
        },
        attachmentDescriptionModeStore: {
            type: 'common-attachmentdescriptionmode',
            data: '{attachmentsDescriptionModes}'
        },
        attributeGroupsStoreNew: {
            model: '{attributeGroupingModelName}',
            alias: 'store.attribute-groupings-new',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: Ext.create('CMDBuildUI.model.AttributeGrouping')
        },
        attributeGroupsStore: {
            source: '{theProcess.attributeGroups}'
        },
        contextMenuComponentStore: {
            model: 'CMDBuildUI.model.base.Base',
            source: 'customcomponents.ContextMenus'
        },
        contextMenuItemsStoreNew: {
            model: '{contextMenuItemModelName}',
            proxy: {
                type: 'memory'
            },
            data: CMDBuildUI.model.ContextMenuItem.create({}),
            autoDestroy: true
        },
        contextMenuItemsStore: {
            model: '{contextMenuItemModelName}',
            proxy: {
                type: 'memory'
            },
            data: '{getContextMenuItemData}',
            autoDestroy: true
        },
        contextMenuItemTypeStore: {
            autoLoad: true,
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            autoDestroy: true,
            data: '{contexMenuTypes}'
        },

        contextMenuApplicabilityStore: {
            type: 'common-applicability',
            data: '{contextMenuApplicabilities}'
        }
    },

    configToolbarButtons: function () {
        this.set('disabledTabs.properties', false);
        this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
        this.set('toolbarHiddenButtons.delete', !this.get('actions.view'));
        this.set('toolbarHiddenButtons.enable', !this.get('actions.view') || (this.get('actions.view') && this.data.theProcess.data.active /*this.get('theProcess.active')*/));
        this.set('toolbarHiddenButtons.disable', !this.get('actions.view') || (this.get('actions.view') && !this.data.theProcess.data.active /*!this.get('theProcess.active')*/));
        this.set('toolbarHiddenButtons.version', !this.get('actions.view'));

        return true;
    },
    configDisabledTabs: function () {
        this.set('disabledTabs.properties', false);
        this.set('disabledTabs.attributes', !this.get('actions.view'));
        this.set('disabledTabs.domains', !this.get('actions.view'));
        this.set('disabledTabs.tasks', !this.get('actions.view'));
        var gisEnabled = CMDBuildUI.util.helper.Configurations.get('cm_system_gis_enabled');
        this.set('disabledTabs.layers', !gisEnabled || !this.get('actions.view'));
        this.set('disabledTabs.geoattributes', !gisEnabled || !this.get('actions.view'));
    }
});
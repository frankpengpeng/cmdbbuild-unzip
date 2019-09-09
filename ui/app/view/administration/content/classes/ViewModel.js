Ext.define('CMDBuildUI.view.administration.content.classes.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-classes-view',
    data: {
        isClass: true,
        activeTab: 0,
        objectTypeName: null,
        theObject: null,
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
            layers: false,
            geoattributes: false,
            import_export: true
        },
        formWidgetCount: 0,
        contextMenuCount: 0,
        attributeGroups: [],
        toolbarHiddenButtons: {
            edit: true, // action !== view
            delete: true, // action !== view
            enable: true, //action !== view && theObject.active
            disable: true, // action !== view && !theObject.active
            print: true // action !== view
        },
        checkboxNoteInlineClosed: {
            disabled: true
        },
        checkboxAttachmentsInlineClosed: {
            disabled: true
        },
        isMultitenantActive: false

    },

    formulas: {
        classLabel: {
            bind: '{theObject.description}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.classes.toolbar.classLabel;
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
        classTypes: {
            get: function () {
                return [{
                    'value': 'standard',
                    'label': CMDBuildUI.locales.Locales.administration.classes.texts.standard // Standard
                }, {
                    'value': 'simple',
                    'label': CMDBuildUI.locales.Locales.administration.classes.texts.simple // Simple'
                }];
            }
        },
        defaultOrders: {
            get: function () {
                return [{
                    'value': 'ascending',
                    'label': CMDBuildUI.locales.Locales.administration.common.strings.ascending // Ascending
                }, {
                    'value': 'descending',
                    'label': CMDBuildUI.locales.Locales.administration.common.strings.descending // Descending
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
        updateNoteInlineClosedCheckboxState: {
            bind: '{theObject.noteInline}',
            get: function (data) {
                if (data) {
                    this.set('checkboxNoteInlineClosed.disabled', false);
                } else {
                    this.set('checkboxNoteInlineClosed.disabled', true);
                    this.set('theObject.noteInlineClosed', false);
                }
            }
        },

        updateAttachmnentsInlineClosedCheckboxState: {
            bind: '{theObject.attachmentsInline}',
            get: function (data) {
                if (data) {
                    this.set('checkboxAttachmentsInlineClosed.disabled', false);
                } else {
                    this.set('checkboxAttachmentsInlineClosed.disabled', true);
                    this.set('theObject.attachmentsInlineClosed', false);
                }
            }
        },

        isMultitenant: {
            bind: {
                theObject: '{theObject}'
            },
            get: function (data) {
                var activeTab = this.getView().up('administration-content').getViewModel().get('activeTabs.classes');
                this.set('activeTab', activeTab);
                return CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled) && !data.theObject.get('prototype');
            }
        },
        isMultitenatModeHiddenCombo: {
            bind: {
                isMultitenant: '{isMultitenant}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isPrototype: '{theObject.prototype}'
            },
            get: function (data) {
                var showMultiTenant = (data.isMultitenant && (data.isEdit || data.isAdd) && !data.isPrototype);
                return !showMultiTenant;
            }
        },
        isMultitenatModeHiddenDisplay: {
            bind: {
                isMultitenant: '{isMultitenant}',
                isView: '{actions.view}',
                isPrototype: '{theObject.prototype}'
            },
            get: function (data) {
                return !(data.isMultitenant && data.isView && !data.isPrototype);
            }
        },
        action: {
            bind: {
                theObject: '{theObject}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isView: '{actions.view}'
            },
            get: function (data) {
                if (data.theObject) {
                    this.configToolbarButtons();
                    this.configDisabledTabs(data.theObject);
                }
                if (data.isEdit) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
                var form = this.getView().down('administration-content-classes-tabitems-properties-fieldsets-generaldatafieldset').up('form').getForm();
                var nameField = form.findField('classnamefieldadd');
                nameField.maxLength = value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit ? Infinity : 20;


            }
        },
        getToolbarButtons: {
            bind: {
                actions: '{actions}',
                active: '{theObject.active}'
            },
            get: function (get) {
                this.configToolbarButtons();
            }
        },
        isSuperClassManager: {
            bind: '{theObject.prototype}',
            get: function (prototype) {
                return prototype;
            }
        },
        formTriggerCount: {
            bind: {
                formTriggers: '{theObject.formTriggers}',
                triggerStore: '{formTriggersStore}'
            },
            get: function (data) {

                if (data.formTriggers) {
                    return data.formTriggers.data.items.length;
                }
                return 0;
            }
        },
        formWidgetCountManager: {
            bind: '{theObject.widgets}',
            get: function (widgets) {
                if (widgets) {
                    this.set('formWidgetCount', widgets.data.items.length);
                    return widgets.data.items.length;
                }
                this.set('formWidgetCount', 0);
                return 0;
            },
            set: function (value) {
                this.set('formWidgetCount', value);
            }
        },
        contextMenuCountManager: {
            bind: '{theObject.contextMenuItems}',
            get: function (contextMenuItems) {
                if (contextMenuItems) {
                    this.set('contextMenuCount', contextMenuItems.data.items.length);
                }
                this.set('contextMenuCount', 0);
            },
            set: function (value) {
                this.set('contextMenuCount', value);
            }
        },
        isSimpleClass: {
            bind: {
                type: '{theObject.type}',
                prototype: '{theObject.prototype}'
            },
            get: function (data) {
                if (data.type) {
                    var isSimple = data.type === 'simple';
                    return isSimple;
                }
            }
        },

        isStandardClassAndIsViewAction: {
            bind: '{theObject.type}',
            get: function (type) {
                if (type) {
                    return (type === 'standard' && this.get('actions.view') === true) ? true : false;
                }
            }
        },

        hideParentCombobox: {
            bind: '{theObject.type}',
            get: function (type) {
                if (type) {
                    return (type === 'simple' || this.get('actions.view') === true) ? true : false;
                }
            }
        },
        hideParentDisplayfield: {
            bind: {
                type: '{theObject.type}',
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
        hideParentDisabledfield: {
            bind: {
                type: '{theObject.type}',
                edit: '{actions.edit}'
            },
            get: function (data) {
                if (data.type) {
                    return (data.type === 'simple' ||
                        (data.edit === false && data.type === 'standard')
                    ) ? true : false;
                }
            }
        },

        getFormTriggersData: {
            bind: '{theObject}',
            get: function (theObject) {
                return (theObject) ? theObject.getAssociatedData().formTriggers : [];
            }
        },
        getContextMenuItemData: {
            bind: '{theObject}',
            get: function (theObject) {
                return (theObject) ? theObject.getAssociatedData().contextMenuItems : [];
            }
        },
        getFormWidgetData: {
            bind: '{theObject}',
            get: function (theObject) {
                if (theObject) {
                    return theObject.getAssociatedData().widgets;
                }
                return [];
            }
        },
        getDefaultOrderData: {
            bind: '{theObject.defaultOrder}',
            get: function (defaultOrder) {
                return (defaultOrder) ? defaultOrder.getData().items : [];
            }
        },
        getClassType: {
            bind: '{theObject.type}',
            get: function (type) {
                if (type) {
                    return Ext.util.Format.capitalize(type);
                }
            }
        },
        storeClassName: {
            bind: '{theObject}',
            get: function (theObject) {
                if (theObject) {
                    return 'CMDBuildUI.model.classes.Class';
                }
            }
        },
        lookupTypeModelName: {
            bind: '{theObject}',
            get: function (theObject) {
                if (theObject) {
                    return 'CMDBuildUI.model.lookups.LookupType';
                }
            }
        },
        contextMenuItemModelName: {
            bind: '{theObject}',
            get: function (theObject) {
                if (theObject) {
                    return 'CMDBuildUI.model.ContextMenuItem';
                }
            }
        },

        formTriggerModelName: {
            bind: '{theObject}',
            get: function (theObject) {
                if (theObject) {
                    return 'CMDBuildUI.model.FormTrigger';
                }
            }
        },
        formWidgetModelName: {
            bind: '{theObject}',
            get: function (theObject) {
                if (theObject) {
                    return 'CMDBuildUI.model.WidgetDefinition';
                }
            }
        },
        attributeOrderModelName: {
            bind: '{theObject}',
            get: function (theObject) {
                if (theObject) {
                    return 'CMDBuildUI.model.AttributeOrder';
                }
            }
        },
        attributeGroupingModelName: {
            bind: '{theObject}',
            get: function (theObject) {
                if (theObject) {
                    return 'CMDBuildUI.model.AttributeGrouping';
                }
            }
        },
        attributeProxy: {
            bind: '{theObject.name}',
            get: function (objectTypeName) {
                if (objectTypeName && !this.get('theObject').phantom) {
                    return {
                        url: Ext.String.format("/classes/{0}/attributes", objectTypeName),
                        type: 'baseproxy'
                    };
                }
            }
        },

        defaultFilterData: {
            bind: '{theObject}',
            get: function (objectTypeName) {
                if (objectTypeName) {
                    var datas = objectTypeName.getData();

                    this.set('defaultFilterData._id', datas._id);
                    this.set('defaultFilterData.name', datas.name);
                    this.set('defaultFilterProxy', {
                        url: Ext.String.format("/classes/{0}/filters", datas.name),
                        type: 'baseproxy'
                    });
                }
            }
        },

        unorderedAttributes: function (get) {
            var theObject = get('theObject');
            var defaultOrder = theObject.getAssociatedData().defaultOrder;
            return [function (item) {

                var found = false;
                for (var field in defaultOrder) {
                    if (item.get('name') === 'tenantId' || item.get('name') === 'Notes' || defaultOrder[field].attribute === item.get('name')) {
                        break;
                    }
                }
                return !found;
            }];
        },

        getParentDescription: {
            bind: {
                theObject: '{theObject}'
            },
            get: function (data) {

                var me = this;
                var theObject = data.theObject;
                var superclassesStore = Ext.getStore('classes.PrototypeClasses'); // data.superclassesStore;
                if (theObject && superclassesStore) {
                    var parent = superclassesStore.findRecord('name', me.get('theObject.parent'));
                    if (parent) {
                        me.set('parentDescription', parent.get('description'));
                        return parent.get('description');
                    }
                }
            }
        },

        filterImportExportTemplates: {
            bind: {
                theObjectName: '{theObject.name}'
            },
            get: function (data) {
                if (!this.get('actions.add')) {
                    var allDomains = Ext.getStore('domains.Domains').getRange().filter(
                        function (item) {
                            return item.get('source') === data.theObjectName || item.get('destination') === data.theObjectName;
                        });
                    var filterImportTemplates = [function (item) {
                        if (item.get('type') === CMDBuildUI.model.importexports.Template.types.import || item.get('type') === CMDBuildUI.model.importexports.Template.types.importexport) {
                            var res = false;
                            switch (item.get('targetType')) {
                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                    res = item.get('targetName') === data.theObjectName;
                                    break;
                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.domain:
                                    var domains = Ext.Array.filter(allDomains, function (domain) {
                                        return domain.get('name') === item.get('targetName');
                                    });
                                    res = domains.length;
                                    break;
                                default:
                                    break;
                            }
                            return res;
                        }
                        return false;
                    }];

                    var filterExportTemplates = [function (item) {

                        if (CMDBuildUI.model.importexports.Template.types.export === item.get('type') || item.get('type') === CMDBuildUI.model.importexports.Template.types.importexport) {

                            var res = false;
                            switch (item.get('targetType')) {
                                case CMDBuildUI.model.administration.MenuItem.types.klass:
                                    res = item.get('targetName') === data.theObjectName;
                                    break;
                                case CMDBuildUI.model.administration.MenuItem.types.domain:
                                    var domains = Ext.Array.filter(allDomains, function (domain) {
                                        return domain.get('name') === item.get('targetName');
                                    });
                                    res = domains.length;
                                    break;
                                default:
                                    break;
                            }
                            return res;
                        }
                        return false;
                    }];
                    this.set('filterImportTemplates', filterImportTemplates);
                    this.set('filterExportTemplates', filterExportTemplates);
                }

            }
        }
    },

    stores: {
        defaultOrderStore: {
            model: 'CMDBuildUI.model.AttributeOrder',
            alias: 'store.attribute-default-order',
            proxy: {
                type: 'memory'
            },
            data: '{getDefaultOrderData}',
            autoDestroy: true
        },
        defaultOrderStoreNew: {
            model: '{attributeOrderModelName}',
            alias: 'store.attribute-default-order-new',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: Ext.create('CMDBuildUI.model.AttributeOrder')
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
        defaultOrderDirectionsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            data: '{defaultOrders}'
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
            autoDestroy: true
        },
        superclassesStore: {
            type: 'classes.PrototypeClasses',
            pageSize: 0
        },

        formTriggersStore: {
            storeId: 'formTriggersStore',
            model: '{formTriggerModelName}',
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            autoDestroy: true,
            data: '{getFormTriggersData}'
        },
        formTriggersStoreNew: {
            model: '{formTriggerModelName}',
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            autoDestroy: true,
            data: Ext.create('CMDBuildUI.model.FormTrigger', {})
        },
        contextMenuComponentStore: {
            model: 'CMDBuildUI.model.base.Base',
            source: 'customcomponents.ContextMenus',
            pageSize: 0
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
        },
        formWidgetsStore: {
            model: '{formWidgetModelName}',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{getFormWidgetData}'
        },
        formWidgetsStoreNew: {
            model: '{formWidgetModelName}',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: Ext.create('CMDBuildUI.model.WidgetDefinition')
        },
        contextMenuItemsStoreNew: {
            model: '{contextMenuItemModelName}',
            proxy: {
                type: 'memory'
            },
            data: CMDBuildUI.model.ContextMenuItem.create({}),
            autoDestroy: true
        },
        attachmentDescriptionModeStore: {
            type: 'common-attachmentdescriptionmode',
            data: '{attachmentsDescriptionModes}'
        },
        attachmentTypeLookupStore: {
            model: '{lookupTypeModelName}',
            type: 'attachments-categories',
            fields: [{
                _name: '_id',
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
        defaultFilterStore: {
            proxy: '{defaultFilterProxy}',
            data: '{defaultFilterData}',
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0
        },
        classTypeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            data: '{classTypes}',
            autoDestroy: true

        },

        defaultImportTemplateStore: {
            source: 'importexports.Templates',
            filters: '{filterImportTemplates}'
        },

        defaultExportTemplateStore: {
            source: 'importexports.Templates',
            filters: '{filterExportTemplates}'
        },

        multitenantModeStore: {
            type: 'multitenant-multitenantmode',
            data: '{tenantModes}'
        },

        attributeGroupsStore: {
            source: '{theObject.attributeGroups}'
        }
    },

    configToolbarButtons: function () {
        this.set('disabledTabs.properties', false);
        this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
        this.set('toolbarHiddenButtons.delete', !this.get('actions.view'));
        this.set('toolbarHiddenButtons.enable', !this.get('actions.view') || (this.get('actions.view') && this.data.theObject.data.active /*this.get('theObject.active')*/));
        this.set('toolbarHiddenButtons.disable', !this.get('actions.view') || (this.get('actions.view') && !this.data.theObject.data.active /*!this.get('theObject.active')*/));
        this.set('toolbarHiddenButtons.print', !this.get('actions.view'));

        return true;
    },
    configDisabledTabs: function () {
        var me = this;
        var theObject = this.get('theObject') || this.getData().theObject;
        me.set('disabledTabs.properties', false);
        me.set('disabledTabs.attributes', !me.get('actions.view'));
        me.set('disabledTabs.domains', !me.get('actions.view'));
        var gisEnabled = CMDBuildUI.util.helper.Configurations.get('cm_system_gis_enabled');

        me.set('disabledTabs.layers', !gisEnabled || !me.get('actions.view'));
        me.set('disabledTabs.geoattributes', !gisEnabled || !me.get('actions.view'));
        var importExportDisabled = (me.get('isSimpleClass') || (theObject && theObject.get('prototype'))) || !me.get('actions.view');
        me.set('disabledTabs.import_export', importExportDisabled);
    }
});
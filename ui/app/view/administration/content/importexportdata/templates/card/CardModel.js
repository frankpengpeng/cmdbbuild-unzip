Ext.define('CMDBuildUI.view.administration.content.importexportdata.templates.card.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.view-administration-content-importexportdata-templates-card',
    data: {
        // attributeGridHeight: null,
        gridAttributeDataNew: CMDBuildUI.model.importexports.Attribute.create(),
        actions: {
            view: true,
            edit: false,
            add: false
        },
        isExport: false,
        isImport: false,
        isModifyCard: false,
        isCsv: false,
        isExcell: false,
        isClass: false,
        isDomain: false,
        isAttributeGridHidden: true,
        isAttributeGridNewHidden: true,
        allClassOrDomainsAtributesData: []
    },

    formulas: {
        isAttributeGridHiddenManager: {
            bind: {
                targetName: '{theImportExportTemplate.targetName}',
                type: '{theImportExportTemplate.type}'
            },
            get: function (data) {
                if (data.type && data.targetName) {
                    this.set('isAttributeGridHidden', false);
                    if (!this.get('actions.view')) {
                        this.set('isAttributeGridNewHidden', false);
                    }
                } else {
                    this.set('isAttributeGridHidden', true);
                    if (!this.get('actions.view')) {
                        this.set('isAttributeGridNewHidden', true);
                    }
                }
                if (this.get('actions.view')) {
                    this.set('isAttributeGridNewHidden', true);
                }
            }
        },
        isTargetNameDisabled: {
            bind: '{targetName}',
            get: function (targetName) {
                if (this.get('disabledTargetTypeName') || (typeof targetName === 'string' && targetName && !this.get('actions.view'))) {
                    return true;
                }
                return false;
            }
        },
        isTargetTypeDisabled: {
            bind: '{targetType}',
            get: function (targetType) {
                if (this.get('disabledTargetTypeName') || (typeof targetType === 'string' && targetType && !this.get('actions.view'))) {
                    return true;
                }
                return false;
            }
        },
        objectTypeNameManager: {
            bind: {
                targetName: '{targetName}',
                targetType: '{targetType}'
            },
            get: function (data) {
                if ((typeof data.targetName === 'string' && data.targetName) && (typeof data.targetType === 'string' && data.targetType)) {
                    this.set('isTargetNameDisabled', true);
                    this.set('isTargetTypeDisabled', true);
                }
            }
        },
        gridAttributesData: {
            bind: '{theImportExportTemplate.columns}',
            get: function (columns) {
                if (columns) {
                    Ext.Array.forEach(columns.getRange(), function (item, index) {
                        item.set('index', index);
                    });
                    return columns.getRange();
                }
                return [];
            }
        },
        allClassOrDomainsAtributesDataManager: {
            bind: {
                targetType: '{theImportExportTemplate.targetType}',
                targetName: '{theImportExportTemplate.targetName}'
            },
            get: function (data) {
                var me = this;
                if (data.targetType && data.targetName) {
                    var store;
                    me.set('isClass', data.targetType === CMDBuildUI.model.administration.MenuItem.types.klass);
                    me.set('isDomain', data.targetType === CMDBuildUI.model.administration.MenuItem.types.domain);
                    switch (data.targetType) {
                        case CMDBuildUI.model.administration.MenuItem.types.klass:
                            store = Ext.getStore('classes.Classes');
                            me.set('isClass', true);
                            me.set('isDomain', false);
                            break;
                        case CMDBuildUI.model.administration.MenuItem.types.domain:
                            store = Ext.getStore('domains.Domains');
                            me.set('isClass', false);
                            me.set('isDomain', true);
                            break;
                    }
                    if (store) {
                        var record = store.findRecord('name', data.targetName);

                        if (me.get('theImportExportTemplate').crudState === 'C') {
                            me.resetAllAttributesStores(record);
                        }
                        if (record) {
                            record.getAttributes().then(
                                function (attributeStore) {
                                    me.set('allClassOrDomainAttributes', attributeStore.getRange());
                                });
                        }
                    }
                }
            }
        },
        allClassOrDomainsAttributesFilter: {
            bind: {
                attributesGridStore: '{allSelectedAttributesStore}'
            },
            get: function (data) {
                var allAttrStore = this.get('theImportExportTemplate.columns');
                if (allAttrStore) {
                    return [function (item) {
                        try {
                            return !allAttrStore.findRecord('attribute', item.get('name'));
                        } catch (e) {
                            return true;
                        }
                    }];
                } else {
                    return [];
                }
            }
        },
        mergeModeManager: {
            bind: '{theImportExportTemplate.mergeMode}',
            get: function (mergeMode) {
                if (mergeMode === CMDBuildUI.model.importexports.Template.missingRecords.modifycard) {
                    this.set('isModifyCard', true);
                } else {
                    this.set('isModifyCard', false);
                }
            }
        },
        typeManager: {
            bind: '{theImportExportTemplate.type}',
            get: function (type) {
                switch (type) {
                    case CMDBuildUI.model.importexports.Template.types.import:
                        this.set('isImport', true);
                        this.set('isExport', false);
                        break;
                    case CMDBuildUI.model.importexports.Template.types.export:
                        this.set('isImport', false);
                        this.set('isExport', true);
                        break;
                    case CMDBuildUI.model.importexports.Template.types.importexport:
                        this.set('isImport', true);
                        this.set('isExport', true);
                        break;
                    default:
                        break;
                }
            }
        },

        fileFormatManager: {
            bind: '{theImportExportTemplate.fileFormat}',
            get: function (fileFormat) {
                switch (fileFormat) {
                    case 'csv':
                        this.set('isCsv', true);
                        this.set('isExcell', false);
                        break;
                    case 'xls':
                    case 'xlsx':
                        this.set('isCsv', false);
                        this.set('isExcell', true);
                        break;
                    default:
                        this.set('isCsv', false);
                        this.set('isExcell', false);
                        break;
                }
            }
        },

        isClone: {
            bind: '{theImportExportTemplate}',
            get: function (theImportExportTemplate) {
                return (theImportExportTemplate && theImportExportTemplate.phantom) || false;
            }
        },

        panelTitle: {
            bind: '{theImportExportTemplate.description}',
            get: function (description) {
                var title = Ext.String.format(
                    '{0} - {1}',
                    'Import/Export template',
                    description
                );
                this.getParent().set('title', title);
            }
        },

        allClassesOrDomainsData: {
            bind: '{theImportExportTemplate.targetType}',
            get: function (targetType) {
                switch (targetType) {
                    case CMDBuildUI.model.administration.MenuItem.types.klass:
                        return Ext.getStore('classes.Classes').getData().items.filter(function (item) {
                            return !item.get('prototype') && item.get('type') === 'standard';
                        });
                    case CMDBuildUI.model.administration.MenuItem.types.domain:
                        return Ext.getStore('domains.Domains').getData().items;
                    default:
                        return [];
                }
            }
        },
        templateTypes: {
            get: function () {
                return CMDBuildUI.model.importexports.Template.getTemplateTypes();
            }
        },
        targetTypes: {
            get: function () {
                return CMDBuildUI.model.importexports.Template.getTargetTypes();
            }
        },
        fileTypes: {
            get: function () {
                return CMDBuildUI.model.importexports.Template.getFileTypes();
            }
        },
        mergeModes: {
            get: function () {
                return CMDBuildUI.model.importexports.Template.getMergeModes();
            }
        },
        csvSeparators: {
            get: function () {
                return CMDBuildUI.model.importexports.Template.getCsvSeparators();
            }
        },
        attributeModes: {
            get: function () {
                return CMDBuildUI.model.importexports.Attribute.getAttributeModes();
            }
        },
        defaultDomainsAttributes: {
            get: function(){
                return CMDBuildUI.model.importexports.Attribute.getDefaultDomainsAttributes();
            }
        }
    },

    stores: {
        attributeModesReferenceStore: {
                model: 'CMDBuildUI.model.base.ComboItem',
                proxy: {
                    type: 'memory'
                },
                data: '{attributeModes}'  
        },
        
        allClassesOrDomains: {
            fields: ['name', 'description'],
            proxy: {
                type: 'memory'
            },
            data: '{allClassesOrDomainsData}'
        },
        allClassOrDomainsAtributes: {
            data: '{allClassOrDomainAttributes}',
            sorters: 'description'
        },
        allClassOrDomainsAtributesFiltered: {
            source: '{allClassOrDomainsAtributes}',
            sorters: 'description',
            filters: '{allClassOrDomainsAttributesFilter}'
        },
        allSelectedAttributesStore: {
            proxy: {
                type: 'memory'
            },
            model: 'CMDBuildUI.model.importexports.Attribute',
            data: '{gridAttributesData}',
            sorters: 'index'
        },
        newSelectedAttributesStore: {
            proxy: {
                type: 'memory'
            },
            model: 'CMDBuildUI.model.importexports.Attribute',
            data: [CMDBuildUI.model.importexports.Attribute.create()]
        },

        allEmailAccounts: {
            type: 'chained',
            source: 'emails.Accounts',
            autoLoad: true
        },

        allEmailTemplates: {
            type: 'chained',
            source: 'emails.Templates',
            autoLoad: true
        },
        templateTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{templateTypes}'
        },
        targetTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{targetTypes}'
        },
        fileTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{fileTypes}'
        },
        mergeModesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{mergeModes}'
        },
        csvSeparatorsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{csvSeparators}'
        },
        defaultDomainsAttributesStore: {
            proxy: 'memory',
            data: '{defaultDomainsAttributes}'
        }
    },

    resetAllAttributesStores: function (classOrDomain) {
        var me = this;
        if (me.get('allClassOrDomainsAtributes')) {
            me.get('allClassOrDomainsAtributes').removeAll();
        }
        if (me.get('allClassOrDomainsAtributes')) {
            me.get('allClassOrDomainsAtributesFiltered').removeAll();
        }
        if (me.get('allSelectedAttributesStore')) {
            me.get('allSelectedAttributesStore').removeAll();
        }
        if (me.get('theImportExportTemplate.columns') && me.get('theImportExportTemplate.columns').isStore && me.get('actions.add')) {
            me.get('theImportExportTemplate.columns').removeAll();
            if (classOrDomain && classOrDomain.entityName === 'CMDBuildUI.model.domains.Domain') {                
                Ext.Array.forEach(me.get('defaultDomainsAttributesStore').getRange(), function (item) {
                    switch (item.get('attribute')) {
                        case 'IdObj1':
                            item.set('columnName', classOrDomain.get('source'));
                            break;
                        case 'IdObj2':
                            item.set('columnName', classOrDomain.get('destination'));
                            break;
                        default:
                            break;
                    }
                    me.get('theImportExportTemplate.columns').add(item);
                    if (me.get('allSelectedAttributesStore')) {
                        me.get('allSelectedAttributesStore').add(item);
                    }
                });
            }
        }
    }
});
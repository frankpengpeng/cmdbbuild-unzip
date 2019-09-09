Ext.define('CMDBuildUI.model.classes.Class', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        classtypes: {
            simple: 'simple',
            standard: 'standard'
        },
        formtriggeractions: {
            afterInsert: 'afterInsert',
            beforeInsert: 'beforeInsert',
            afterEdit: 'afterEdit',
            beforeEdit: 'beforeEdit',
            afterClone: 'afterClone',
            beforeClone: 'beforeClone'
        }
    },

    requires: [
        'Ext.data.validator.Format',
        'Ext.data.validator.Length',
        'Ext.data.validator.Presence'
    ],
    
    fields: [{
        name: 'name',
        type: 'string',
        critical: true
    }, {
        name: 'description',
        type: 'string',
        critical: true
    }, {
        name: 'parent',
        type: 'string',
        critical: true,
        defaultValue: 'Class'
    }, {
        name: 'prototype',
        type: 'boolean',
        critical: true
    }, {
        name: 'type',
        type: 'string',
        defaultValue: 'standard',
        critical: true
    }, {
        name: 'system',
        type: 'boolean'
    }, {
        name: 'attachmentTypeLookup',
        type: 'string',
        critical: true,
        persist: true
    }, {
        name: 'attachmentDescriptionMode',
        type: 'string',
        critical: true,
        persist: true
    }, {
        name: 'active',
        type: 'boolean',
        critical: true,
        defaultValue: true
    }, {
        name: 'defaultFilter',
        type: 'string',
        critical: true,
        persist: true
    }, {
        name: 'defaultImportTemplate',
        type: 'string',
        critical: true,
        persist: true
    }, {
        name: 'defaultExportTemplate',
        type: 'string',
        critical: true,
        persist: true
    }, {
        name: 'defaultOrder',
        type: 'auto',
        critical: true
    }, {
        name: 'formTriggers',
        type: 'auto',
        critical: true
    }, {
        name: 'contextMenuItems',
        type: 'auto',
        critical: true
    }, {
        name: 'widgets',
        type: 'auto',
        critical: true
    }, {
        name: 'attributeGroups',
        type: 'auto',
        critical: true,
        defaultValue: []
    }, {
        name: 'isMasterDetail',
        type: 'boolean',
        critical: true,
        defaultValue: false
    }, {
        name: 'multitenantMode',
        type: 'string',
        critical: true,
        defaultValue: 'never' // values ca be: never, always, mixed
    }, {
        name: '_icon',
        type: 'number',
        critical: true,
        persistent: true
    }, {
        name: '_iconPath',
        type: 'string',
        calculate: function (data) {
            if (data._icon) {
                return Ext.String.format('{0}/uploads/{1}/image.png', CMDBuildUI.util.Config.baseUrl, data._icon);
            } else {
                return null;
            }
        }
    }, {
        name: 'attachmentsInline',
        type: 'boolean',
        defaultValue: false,
        critical: true
    }, {
        name: 'attachmentsInlineClosed',
        type: 'boolean',
        defaultValue: true,
        critical: true
    }, {
        name: 'noteInline',
        type: 'boolean',
        defaultValue: false,
        critical: true
    }, {
        name: 'noteInlineClosed',
        type: 'boolean',
        defaultValue: false,
        critical: true
    }, {
        dame: 'domainOrder',
        type: 'auto',
        dafaultValue: [],
        critical: true
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.Attribute',
        name: 'attributes'
    }, {
        model: 'CMDBuildUI.model.domains.Domain',
        name: 'domains'
    }, {
        model: 'CMDBuildUI.model.AttributeOrder',
        name: 'defaultOrder',
        associationKey: 'defaultOrder'
    }, {
        model: 'CMDBuildUI.model.FormTrigger',
        name: 'formTriggers',
        associationKey: 'formTriggers'
    }, {
        model: 'CMDBuildUI.model.ContextMenuItem',
        name: 'contextMenuItems',
        associationKey: 'contextMenuItems'
    }, {
        model: 'CMDBuildUI.model.WidgetDefinition',
        name: 'widgets',
        associationKey: 'widgets'
    }, {
        model: 'CMDBuildUI.model.base.Filter',
        name: 'filters'
    }, {
        model: 'CMDBuildUI.model.AttributeGrouping',
        name: 'attributeGroups'
    }, {
        model: 'CMDBuildUI.model.importexports.Template',
        name: 'importExportTemplates'
    }, {
        model: 'CMDBuildUI.model.thematisms.Thematism',
        name: 'thematisms'
    }],

    validators: {
        name: [
            'presence'
            // {
            //     type: 'length',
            //     max: 20,
            //     message: Ext.String.format(CMDBuildUI.locales.Locales.administration.common.messages.greaterthen, 20)
            // },
            // {
            //     type: 'format',
            //     matcher:  /^(?![_0-9])[a-zA-Z0-9-_]+$/,
            //     message: Ext.String.format(CMDBuildUI.locales.Locales.administration.common.messages.cantcontainchar, '_ (underscore)')
            // }
        ],
        description: ['presence']
    },

    proxy: {
        url: '/classes/',
        type: 'baseproxy',
        reader: {
            type: 'json'
        }
    },

    /**
     * Get translated description
     * @return {String}
     */
    getTranslatedDescription: function () {
        return this.get("_description_translation") || this.get("description");
    },

    /**
     * Get CMDBuild hierarchy
     * @return {String[]} A list of class names
     */
    getHierarchy: function () {
        var hierarchy = [];
        var parentName = this.get("parent");
        if (parentName) {
            var parent = Ext.getStore("classes.Classes").getById(parentName);
            if (parent) {
                hierarchy = parent.getHierarchy();
            }
            hierarchy.push(this.getId());
        }
        return hierarchy;
    },

    /**
     * Get all children
     * @param {Boolean} leafs Get only leafs
     * @return {CMDBuild.model.Class[]} A list of children classes
     */
    getChildren: function (leafs) {
        var children = [];
        var store = Ext.getStore("classes.Classes");

        store.filter({
            property: "parent",
            value: this.get("name"),
            exactMatch: true
        });

        var allitems = store.getRange();
        store.clearFilter();

        allitems.forEach(function (p) {
            if (!leafs || (leafs && !p.get("prototype"))) {
                children.push(p);
            }
            children = Ext.Array.merge(children, p.getChildren(leafs));
        });

        return children;
    },

    /**
     * Get all children as tree
     * @param {Boolean} leafs Get only leafs
     * @param {Function} [itemCorrection] Manage single record
     * @return {Ext.list.TreeItem[]} A list of children classes
     */
    getChildrenAsTree: function (leafs, itemCorrection) {
        var me = this;
        var children = [];
        var filters = [function (item) {
            return item.get('parent') === me.get('name');
        }];
        var store = Ext.create('Ext.data.ChainedStore', {
            source: 'classes.Classes',
            filters: filters
        });
        var allitems = store.getRange();

        if (!allitems.length) {
            store.clearFilter();
            store.filter(function (item) {
                return item.get('name') === me.get('name');
            });
        }
        
        allitems = store.getRange();
        store.clearFilter();

        allitems.forEach(function (item) {
            if (itemCorrection) {
                item = itemCorrection(item);
            }
            var treeItem = {
                objecttype: 'Class',
                enabled: item.get('enabled'),
                text: item.get("description"),
                name: item.get('name')
            };
            if ((leafs && item.get("prototype"))) {
                treeItem.leaf = false;
                treeItem.menutype = 'folder';
                treeItem.expanded = true;
                treeItem.enabled = true;
                var childrens = item.getChildrenAsTree(leafs, itemCorrection);
                treeItem.children = childrens;
            } else {
                treeItem.children = [];
                treeItem.leaf = true;
            }
            children.push(treeItem);
        });
        return children;
    },

    /**
     * @return {Boolean} 
     */
    isSimpleClass: function () {
        return this.get("type") === CMDBuildUI.model.classes.Class.classtypes.simple;
    },

    /**
     * Get object for menu
     * @return {String}
     */
    getObjectTypeForMenu: function () {
        return this.get('name');
    },

    /**
     * Load attributes relation
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as parameters the attributes store and a boolean field.
     */
    getAttributes: function (force) {
        var deferred = new Ext.Deferred();
        var attributes = this.attributes();
        var className = this.get('name');
        // If is new record
        if(this.crudState === 'C'){
                deferred.resolve([], true);
        } else if (!attributes.isLoaded() || force) {

            attributes.setProxy({
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Classes.getAttributes(className)
            });

            attributes.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(attributes, true);
                    } else {
                        deferred.reject(operation);
                    }
                }
            });
        } else {
            // return promise
            deferred.resolve(attributes, false);
        }
        return deferred.promise;

    },

    /**
     * Load domains relation
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as paramenters the domains store and a boolean field.
     */
    getDomains: function (force) {
        var deferred = new Ext.Deferred();
        var domains = this.domains();

        if (!domains.isLoaded() || force) {
            // configure proxy
            domains.getProxy().setUrl(CMDBuildUI.util.api.Classes.getDomains(this.get("name")));
            domains.getProxy().setExtraParams({
                detailed: true
            });
            // load store
            domains.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(domains, true);
                    }
                }
            });
        } else {
            // return promise
            deferred.resolve(domains, false);
        }
        return deferred.promise;
    },

    /**
     * 
     * @param {*} action 
     */
    getFormTriggersForAction: function (action) {
        var triggers = this.formTriggers();
        var filtered = [];
        triggers.each(function (t) {
            if (t.get("active") && t.get(action)) {
                filtered.push(t.get("script"));
            }
        });
        return filtered;
    },

    /**
     * Load domains relation
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as paramenters the domains store and a boolean field.
     */
    getFilters: function (force) {
        var deferred = new Ext.Deferred();
        var filters = this.filters();

        if (!filters.isLoaded() || force) {
            filters.getProxy().setUrl(CMDBuildUI.util.api.Common.getFiltersUrl(CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, this.get("name")));
            // load store
            filters.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(filters, true);
                    }
                }
            });
        } else {
            // return promise
            deferred.resolve(filters, false);
        }
        return deferred.promise;
    },

    /**
     * Load import/export templates
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as paramenters the domains store and a boolean field.
     */
    getImportExportTemplates: function (force) {
        var deferred = new Ext.Deferred();
        var templates = this.importExportTemplates();

        if (!templates.isLoaded() || force) {
            templates.getProxy().setUrl(CMDBuildUI.util.api.Classes.getImportExportTemplatesUrl(this.get("name")));
            // load store
            templates.load({
                params: {
                    include_related_domains: true
                },
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(templates, true);
                    }
                }
            });
        } else {
            // return promise
            deferred.resolve(templates, false);
        }
        return deferred.promise;
    },

    /**
     * Load thematisms
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as paramenters the thematism store and a boolean field.
     */
    getThematisms: function (force) {
        var deferred = new Ext.Deferred();
        var thematisms = this.thematisms();


        if (!thematisms.isLoaded() || force) {
            thematisms.getProxy().setUrl(CMDBuildUI.util.api.Classes.getThematismsUrl(this.get("name")));
            thematisms.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(thematisms, true);
                    }
                }
            });
        } else {
            deferred.resolve(thematisms, false);
        }
        return deferred.promise;
    }
});
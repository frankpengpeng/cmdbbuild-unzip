Ext.define('CMDBuildUI.model.processes.Process', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        flowstatus: {
            field: "FlowStatus",
            lookuptype: "FlowStatus"
        }
    },

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
        defaultValue: 'Activity'
    }, {
        name: 'prototype',
        type: 'boolean',
        critical: true
    }, {
        name: 'flowStatusAttr',
        type: 'string',
        critical: true
    }, {
        name: 'attributeGroups',
        type: 'auto',
        critical: true,
        defaultValue: []
    }, {
        name: 'engine',
        type: 'string',
        critical: true
    }, {
        name: 'messageAttr',
        type: 'string',
        critical: true
    }, {
        name: 'enableSaveButton',
        type: 'boolean',
        critical: true
    }, {
        name: 'hideSaveButton',
        type: 'boolean',
        calculated: function (value, record) {
            return !record.get('enableSaveButton');
        }
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
        critical: true,
        defaultValue: []
    }, {
        name: 'multitenantMode',
        type: 'string',
        critical: true,
        defaultValue: 'never' // values ca be: never, always, mixed
    }, {
        name: 'active',
        type: 'boolean',
        critical: true,
        defaultValue: true
    }, {
        name: 'attachmentDescriptionMode',
        type: 'string',
        critical: true
    }, {
        name: 'attachmentTypeLookup',
        type: 'string',
        critical: true
    }, {
        name: 'noteInline',
        type: 'boolean',
        critical: true
    }, {
        name: 'noteInlineClosed',
        type: 'boolean',
        critical: true
    }, {
        name: 'stoppableByUser',
        type: 'boolean',
        critical: true
    }, {
        name: 'type',
        type: 'string',
        defaultValue: 'standard',
        critical: true
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
        model: 'CMDBuildUI.model.process.ProcessVersion',
        name: 'versions'
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
        url: '/processes/',
        type: 'baseproxy'
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
     * @return {String[]} A list of process names
     */
    getHierarchy: function () {
        var hierarchy = [];
        if (this.get("parent")) {
            var parent = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(this.get("parent"));
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
     * @return {CMDBuildUI.model.processes.Process[]} A list of children processes
     */
    getChildren: function (leafs) {
        var children = [];
        var store = Ext.getStore("processes.Processes");

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
     * @return {CMDBuild.model.Process[]} A list of children processes
     */
    getChildrenAsTree: function (leafs, itemCorrection) {
        var me = this;
        var children = [];
        var filters = [function (item) {
            return item.get('parent') === me.get('name');
        }];
        var store = Ext.create('Ext.data.ChainedStore', {
            source: 'processes.Processes',
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
                objecttype: 'Process',
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
        var processName = this.get('name');

        if (!attributes.isLoaded() || force) {

            attributes.setProxy({
                type: 'baseproxy',
                url: Ext.String.format("{0}/processes/{1}/attributes", CMDBuildUI.util.Config.baseUrl, processName)
            });

            attributes.load({
                callback: function (records, operation, success) {
                    if (success) {
                        deferred.resolve(attributes, true);
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
            domains.getProxy().setUrl(CMDBuildUI.util.api.Processes.getDomains(this.get("name")));
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
     * Load domains relation
     * @param {Boolean} force If `true` load the store also if it is already loaded.
     * @return {Ext.Deferred} The promise has as paramenters the domains store and a boolean field.
     */
    getFilters: function (force) {
        var deferred = new Ext.Deferred();
        var filters = this.filters();

        if (!filters.isLoaded() || force) {
            filters.getProxy().setUrl(CMDBuildUI.util.api.Common.getFiltersUrl(CMDBuildUI.util.helper.ModelHelper.objecttypes.process, this.get("name")));
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
    }
});
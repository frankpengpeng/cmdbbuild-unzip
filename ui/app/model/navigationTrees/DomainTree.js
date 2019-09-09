Ext.define('CMDBuildUI.model.navigationTrees.DomainTree', {
    extend: 'CMDBuildUI.model.base.Base',
    fields: [{
        name: '_id',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'name',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'description',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true,
        persist: true,
        critical: true,
        default: true
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.navigationTrees.TreeNode',
        name: 'nodes',
        associationKey: 'nodes'
    }],

    proxy: {
        type: 'baseproxy',
        url: CMDBuildUI.util.api.DomainTrees.getDomainTrees()
    },

    /**
     * @returns {Ext.data.Model} Ther root of the tree model: CMDBuildUI.model.navigationTrees.TreeNode
     */
    getRoot: function () {
        var index = this.nodes().findBy(function (record, id) {
            if (!record.parent) {
                return true;
            }
        });

        return this.nodes().getAt(index);
    },

    /**
     * This function finds the records wich have parent = id
     * @param {String} id
     * @returns {[Ext.data.Model]} model: CMDBuildUI.model.navigationTrees.TreeNode
     */
    getChild: function (id) {
        var childs = [];

        this.nodes().getRange().forEach(function (record) {
            if (record.get('parent') === id) {
                childs.push(record);
            }
        }, this);

        return childs;
    },

    /**
     * This function return the parent record of the one passed by id
     * @param {String} id
     * @returns {Ext.data.Model} model: CMDBuildUI.model.navigationTrees.TreeNode
     */
    getParent: function (id) {
        var record = this.nodes().findRecord('_id', id);
        var parentId = record.get('parent');

        return this.nodes().findRecord('_id', parentId);
    },

    /**
     * @param {String} id 
     */
    getNode: function (id) {
        return this.nodes().findRecord('_id', id);
    }
});
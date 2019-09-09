Ext.define('CMDBuildUI.view.administration.content.bimnavigationtrees.Tree', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.bimnavigationtrees.TreeController',
        'CMDBuildUI.view.administration.content.bimnavigationtrees.TreeModel'
    ],
    alias: 'widget.administration-content-bimnavigationtrees-tree',
    controller: 'administration-content-bimnavigationtrees-tree',
    itemId: 'domainsClassTree',
    viewModel: 'administration-content-bimnavigationtrees-tree',
    viewConfig: {
        markDirty: false,
        animate: false
    },

    bind: {
        store: '{treeStore}'
    },
    ui: 'administration-navigation-tree',
    plugins: CMDBuildUI.util.administration.helper.NavTreeHelper.getViewPlugins(),


    listeners: {
        beforecheckchange: function () {
            return !this.getView().lookupViewModel().get('actions.view');
        },
        checkchange: function (node, checked) {
            function checkParent(node, checked) {
                var parent = node.parentNode;
                if (parent) {
                    parent.set('checked', checked);
                    if (parent.parentNode) {
                        checkParent(parent, checked);
                    }
                }
            }

            function uncheckChild(node, checked) {
                var childrens = node.childNodes;
                Ext.Array.forEach(childrens, function (childNode, i) {
                    childNode.set('checked', checked);
                    childNode.set('showOnlyOne', false);
                    childNode.set('recursionEnabled', false);
                    uncheckChild(childNode, checked);
                });
            }

            if (checked) {
                checkParent(node, checked);
            } else {
                node.set('showOnlyOne', false);
                node.set('recursionEnabled', false);
                uncheckChild(node, checked);
            }
        }
    },
    columns: [{
        xtype: 'treecolumn',
        text: CMDBuildUI.locales.Locales.administration.classes.toolbar.classLabel,
        localized:{
            text: 'CMDBuildUI.locales.Locales.administration.classes.toolbar.classLabel'
        },
        dataIndex: 'text',
        flex: 1
    }]
});
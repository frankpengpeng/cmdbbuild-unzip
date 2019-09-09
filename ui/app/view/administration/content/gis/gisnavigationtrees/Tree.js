Ext.define('CMDBuildUI.view.administration.content.gisnavigationtrees.Tree', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gisnavigationtrees.TreeController',
        'CMDBuildUI.view.administration.content.gisnavigationtrees.TreeModel'
    ],
    alias: 'widget.administration-content-gisnavigationtrees-tree',
    controller: 'administration-content-gisnavigationtrees-tree',
    itemId: 'domainsClassTree',
    viewModel: 'administration-content-gisnavigationtrees-tree',
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
        xtype: 'checkcolumn',
        text: CMDBuildUI.locales.Locales.administration.bim.multilevel,
        localized:{
            text: 'CMDBuildUI.locales.Locales.administration.bim.multilevel'
        },
        dataIndex: 'showOnlyOne',
        width: 100,
        align: 'center',
        listeners: {
            beforecheckchange: function (check, rowIndex, checked, record, event, eOpts) {
                var isEdit = this.getView().lookupViewModel().get('actions.edit');
                var isRecordChecked = record.get('checked');
                return isEdit && isRecordChecked;
            }
        }
    }, {
        xtype: 'treecolumn',
        text: CMDBuildUI.locales.Locales.administration.classes.toolbar.classLabel,
        localized:{
            text: 'CMDBuildUI.locales.Locales.administration.classes.toolbar.classLabel'
        },
        dataIndex: 'text',
        flex: 1
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.strings.filtercql,
        localized:{
            text: 'CMDBuildUI.locales.Locales.administration.common.strings.filtercql'
        },
        dataIndex: 'filter',
        align: 'left',
        flex: 1,
        editor: 'textfield'
    }]
});
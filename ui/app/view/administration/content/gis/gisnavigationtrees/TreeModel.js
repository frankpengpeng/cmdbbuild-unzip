Ext.define('CMDBuildUI.view.administration.content.gisnavigationtrees.TreeModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gisnavigationtrees-tree',

    formulas: {
        treeRoot: {
            bind: {
                targetClass: '{theNavigationtree.targetClass}',
                theNavigationtree: '{theNavigationtree}'
            },
            get: function (data) {
                var root = data.theNavigationtree.get('nodes').length ? data.theNavigationtree.get('nodes')[0] : [];
            
                var tree = {};
                tree._id = root._id;
                tree.text = data.targetClass;
                tree.targetClass = data.targetClass;
                tree.targetIsProcess = false;
                tree.children = [];
                tree.expanded = false;
                tree.multilevel = false;
                tree.checked = true;
                tree.filter = '';
                tree.domain = root.domain;
                tree.filter = (typeof root.filter === 'string')? root.filter: '';
                tree.recursionEnabled = root.recursionEnabled;
                tree.showOnlyOne = root.showOnlyOne;
                
                return tree;
            }
        }
    },

    stores: {
        treeStore: {
            type: 'tree',
            proxy: {
                type: 'memory'
            },
            root: '{treeRoot}',
            listeners: {
                nodecollapse: function (node) {
                    if (node.childNodes.length) {
                        node.removeAll();
                    }
                },
                rootchange : function(newRoot, oldRoot, eOpts){
                    Ext.asap(function(newRoot){
                        if(newRoot.get('text')){
                            newRoot.expand();
                        }
                    }, this, [newRoot]);
                }
            }
        }
    }
});
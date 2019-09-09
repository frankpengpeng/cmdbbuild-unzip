Ext.define('CMDBuildUI.view.graph.tab.cards.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-tab-cards-card',
    data: {
        mode: null //Activity or Class
    },
    formulas: {
        updateTabCard: {
            bind: {
                selectedNode: '{selectedNode}'
            },
            get: function (data) {
                var selectedNode = data.selectedNode;
                if (!selectedNode || selectedNode.length === 0) return;

                var type = data.selectedNode[0].type;
                var view = this.getView();

                if (CMDBuildUI.util.helper.ModelHelper.getClassFromName(type)) { //the type is a class
                    if (this.get('mode') !== CMDBuildUI.locales.Locales.relationGraph.class) {
                        this.set('mode', CMDBuildUI.locales.Locales.relationGraph.class);
                        var cmp = view.lookupReference('cardView');
                        view.remove(cmp);
                        view.add({
                            xtype: 'classes-cards-card-view',
                            reference: 'cardView', //processes-instances-instance-view
                            shownInPopup: true,
                            hideTools: true,
                            hideWidgets: true
                        });
                    }
                } else if (CMDBuildUI.util.helper.ModelHelper.getProcessFromName(type)) { //the type is a process
                    if (this.get('mode') !== CMDBuildUI.locales.Locales.relationGraph.activity) {
                        this.set('mode', CMDBuildUI.locales.Locales.relationGraph.activity);
                        var cmp = view.lookupReference('cardView');
                        view.remove(cmp);
                        // view.add({
                        //     xtype: 'processes-instances-instance-view',
                        //     shownInPopup: true,
                        //     autoScroll: true
                        // });
                        view.add({
                            xtype: 'panel',
                            // html: 'TODO: Handle Process View',
                            reference: 'cardView'
                        });
                    }
                } else if (type.includes('compound')) {
                    if (this.get('mode') !== CMDBuildUI.locales.Locales.relationGraph.compoundnode) {
                        this.set('mode', CMDBuildUI.locales.Locales.relationGraph.compoundnode);
                        var cmp = view.lookupReference('cardView');
                        view.remove(cmp);
                        view.add({
                            xtype: 'panel',
                            // html: CMDBuildUI.locales.Locales.relationGraph.compoundnode,
                            reference: 'cardView'
                        });
                    }
                } else {
                    console.error('Shoul never be Here');
                }
            }
        },
        updateCardBind: {
            bind: {
                selectedNode: '{selectedNode}'
            },
            get: function (data) {
                var selectedNode = data.selectedNode;
                if (!selectedNode || selectedNode.length === 0) return;

                this.set('objectTypeName', selectedNode[0].type);
                this.set('objectId', selectedNode[0].id);

            }
        },

        valueLabel: {
            bind: {
                selectedNode: '{selectedNode}'
            },
            get: function (data) {
                var selectedNode = data.selectedNode;
                if (!selectedNode || selectedNode.length === 0) return;
                
                if (data.selectedNode[0].type.includes('compound_')){
                    return data.selectedNode[0].type.replace('compound_','');
                }
                return data.selectedNode[0].type;
            }
        }
    }

});

Ext.define('CMDBuildUI.view.relations.list.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-container',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            activate: 'onActivate'
        },
        '#addrelationbtn': {
            beforerender: 'onAddRelationBtnBeforeRender'
        }, 
        '#openrelgraphbtn': {
            click : 'onOpenRelgrapBtnClick'
        }
    },

    /**
     * Refresh data on tab activate event
     * 
     * @param {CMDBuildUI.view.relations.list.Container} view 
     * @param {Object} eOpts 
     */
    onActivate: function(view, eOpts) {
        var vm = view.lookupViewModel();
        if (vm.get("allRelations") && !vm.get("allRelations").isLoading() && vm.get("allRelations").isLoaded()) {
            vm.get("allRelations").load();
        }
    },

    /**
     * @param {CMDBuildUI.view.relations.list.Container} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        this.getViewModel().bind({
            bindTo: '{domains}'
        }, function(data) {
            me.addRelationsGrid();
        });
    },

    /**
     * @param {Ext.button.Button} button Add relation button
     * @param {Object} eOpts
     */
    onAddRelationBtnBeforeRender: function (button) {
        var vm = this.getViewModel();
        
        var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(vm.get("objectTypeName"), vm.get("objectType"));
        var objectHierarchy = object.getHierarchy();

        /**
         * 
         * @param {String} description Relation description
         * @param {String} type The name of the target type
         * @param {Object} domain Domain definition
         * @param {String} direction forward|backward
         */
        function createMenuItem(description, type, domain, direction) {
            var item = CMDBuildUI.util.helper.ModelHelper.getClassFromName(type);
            return {
                text: Ext.String.format('{0} ({1})', description, item.getTranslatedDescription()),
                iconCls: 'x-fa fa-file-text-o',
                listeners: {
                    click: 'onAddRelationMenuItemClick'
                },
                type: type,
                domain: domain,
                disabled: !item.get(CMDBuildUI.model.base.Base.permissions.edit),
                direction: direction
            };
        }

        object.getDomains().then(function(domains) {
            var menu = [];

            vm.bind({
                bindTo: {
                    canedit: '{basepermissions.edit}'
                }
            }, function (data) {
                // disable add relation button
                if (data.canedit && domains.getTotalCount() > 0) {
                    vm.set("addbtn.disabled", false);
                }
            });

            vm.setStores({
                domains: domains
            });

            domains.each(function(domain) {
                if (Ext.Array.contains(objectHierarchy, domain.get("source")) &&
                    !domain.get("destinationProcess")) {
                    menu.push(createMenuItem(
                        domain.getTranslatedDescriptionDirect(),
                        domain.get("destination"),
                        domain,
                        'direct'
                    ));
                }
                if (Ext.Array.contains(objectHierarchy, domain.get("destination")) &&
                    !domain.get("sourceProcess")) {
                    menu.push(createMenuItem(
                        domain.getTranslatedDescriptionInverse(),
                        domain.get("source"),
                        domain,
                        'inverse'
                    ));
                }
            });

            menu.sort(function (a, b) {
                if (a.text < b.text) {
                    return -1;
                }
                if (a.text > b.text) {
                    return 1;
                }
                return 0;
            });
            button.setMenu(menu);
        });
    },

    /**
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onAddRelationMenuItemClick: function (item, e, eOpts) {
        var vm = this.getViewModel();
        var view = this.getView();
        var multiselect = item.domain.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.manytomany || 
            (item.direction == 'inverse' && item.domain.get("cardinality") == CMDBuildUI.model.domains.Domain.cardinalities.manytoone) ||
            (item.direction == 'direct' && item.domain.get("cardinality") == CMDBuildUI.model.domains.Domain.cardinalities.onetomany);
        CMDBuildUI.util.helper.ModelHelper.getModel('class', item.type).then(function (model) {
            var popup;
            var title = item.text;
            var config = {
                xtype: 'relations-list-add-gridcontainer',
                originTypeName: vm.get("objectTypeName"),
                originId: vm.get("objectId"),
                multiSelect: multiselect,
                viewModel: {
                    data: {
                        objectTypeName: item.type,
                        relationDirection: item.direction,
                        theDomain: item.domain
                    }
                },
                listeners: {
                    popupclose: function () {
                        popup.removeAll(true);
                        popup.close();
                    }
                },
                onSaveSuccess: function() {
                    view.down('relations-list-grid').getStore().reload();
                }
            };

            popup = CMDBuildUI.util.Utilities.openPopup('popup-add-relation', title, config, null);

        });
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Ext.event.Event} event 
     * @param {Object} e 
     */
    onOpenRelgrapBtnClick: function(button, event, e) {
        var vm = button.lookupViewModel();
        CMDBuildUI.util.Ajax.setActionId("class.card.relgraph.open");
        CMDBuildUI.util.Utilities.openPopup('graphPopup', CMDBuildUI.locales.Locales.relationGraph.relationGraph, {
            xtype: 'graph-graphcontainer',
            _id: vm.get("objectId"),
            _type: vm.get("objectTypeName")
        });
    },

    privates: {
        addRelationsGrid: function () {
            this.getView().add({
                xtype: 'relations-list-grid'
            });
        }
    }

});

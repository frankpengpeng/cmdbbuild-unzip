Ext.define('CMDBuildUI.view.relations.list.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            itemdblclick: 'onItemDblClick'
        },
        'tableview': {
            actionopencard: 'onActionOpenCard',
            actioneditrelation: 'onActionEditRelation',
            actiondeleterelation: 'onActionDeleteRelation',
            actioneditcard: 'onActionEditCard'
        }
    },

    onBeforeRender: function(view) {
        view.lookupViewModel().bind({
            bindTo: '{allRelations}'
        }, function(store) {
            store.load();
        });
    },

    /**
    * @param {CMDBuildUI.view.attachments.Grid} grid
    * @param {Ext.data.Model} record
    * @param {Number} rowIndex
    * @param {Number} colIndex
    * 
    */
    onActionOpenCard: function (grid, record, rowIndex, colIndex) {
        var path;
        switch (CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(record.get("_destinationType"))) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                path = Ext.String.format('classes/{0}/cards/{1}/view', record.get("_destinationType"), record.get("_destinationId"));
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                path = Ext.String.format('processes/{0}/instances/{1}', record.get("_destinationType"), record.get("_destinationId"));
                break;
        }
        this.redirectTo(path);
    },

    /**
    * @param {CMDBuildUI.view.attachments.Grid} grid
    * @param {Ext.data.Model} record
    * @param {Number} rowIndex
    * @param {Number} colIndex
    * 
    */
    onActionEditRelation: function (grid, record, rowIndex, colIndex) {
        var vm = this.getViewModel();

        var domains = vm.get("domains").getRange();
        var objectTypeName = vm.get("objectTypeName");
        var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName);
        var objectHierarchy = object.getHierarchy();

        /**
        * @param {Ext.data.Model} record
        * @param {Object} item
        */
        var openEditRelationPopup = function (record, item) {
            CMDBuildUI.util.helper.ModelHelper.getModel('class', item.type).then(function (model) {
                var popup;
                var title = Ext.String.format("{0} {1} ({2})", CMDBuildUI.locales.Locales.relations.editrelation, item.title, item.type);
                record.getProxy().setUrl(grid.getStore().getProxy().getUrl());
                var config = {
                    xtype: 'relations-list-add-gridcontainer',
                    originTypeName: vm.get("objectTypeName"),
                    originId: vm.get("objectId"),
                    viewModel: {
                        data: {
                            objectTypeName: item.type,
                            relationDirection: item.direction,
                            // selection: record,
                            theRelation: record,
                            theDomain: item.domain
                        }
                    },

                    listeners: {
                        /**
                         * Custom event to close popup directly from popup
                         */
                        popupclose: function (eOpts) {
                            popup.close();
                        }
                    },
                    onSaveSuccess: function() {
                        grid.getStore().reload();
                    }
                };
                popup = CMDBuildUI.util.Utilities.openPopup('popup-edit-relation', title, config);

            }, function () {
            });
        };

        for (var i = 0; i < domains.length; i++) {
            var domain = domains[i];
            if (domain.get('_id') === record.get('_type')) {
                var item;
                if (Ext.Array.contains(objectHierarchy, domain.get("source")) &&
                    !domain.get("destinationProcess")) {

                    item = {
                        id: record.get('_id'),
                        type: domain.get("destination"),
                        title: domain.get("descriptionDirect"),
                        source: domain.get('descriptionMasterDetail'),
                        domain: domain,
                        direction: 'direct'//'forward'
                    };

                }
                if (Ext.Array.contains(objectHierarchy, domain.get("destination")) &&
                    !domain.get("sourceProcess")) {

                    item = {
                        id: record.get('_id'),
                        type: domain.get("source"),
                        title: domain.get("descriptionInverse"),
                        source: domain.get("source"),
                        domain: domain,
                        direction: 'inverse'//'backward'

                    };

                }
                openEditRelationPopup(record, item);
            }
        }
    },

    /**
    * @param {CMDBuildUI.view.attachments.Grid} grid
    * @param {Ext.data.Model} record
    * @param {Number} rowIndex
    * @param {Number} colIndex
    * 
    */
    onActionDeleteRelation: function (grid, record, rowIndex, colIndex) {
        var data = record.getData();

        var currentId = record.getId();
        var vm = this.getView().getViewModel();

        Ext.Msg.confirm(
            "Attention",    //TODO:translate
            "Are you sure you want to delete this relation?",   //TODO:translate
            function (btnText) {
                if (btnText === "yes") {

                    var fullUrl = Ext.String.format('/domains/{0}/relations', record.get('_type'));

                    record.getProxy().setUrl(fullUrl);
                    CMDBuildUI.util.Ajax.setActionId('relation.delete');
                    record.erase();
                }
            }, this);
    },

    /**
    * @param {CMDBuildUI.view.attachments.Grid} grid
    * @param {Ext.data.Model} record
    * @param {Number} rowIndex
    * @param {Number} colIndex
    * 
    */
    onActionEditCard: function (grid, record, rowIndex, colIndex) {
        var popup;
        var me = this;
        // open popup
        var config = {
            xtype: 'classes-cards-card-edit',
            objectTypeName: record.get("_destinationType"),
            objectId: record.get("_destinationId"),

            buttons: [{
                ui: 'management-action',
                reference: 'detailsavebtn',
                itemId: 'detailsavebtn',
                text: CMDBuildUI.locales.Locales.common.actions.save,
                autoEl: {
                    'data-testid': 'relations-list-grid-editcard-save'
                },
                formBind: true,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                },
                handler: function(btn, event) {
                    popup.down("classes-cards-card-edit").getController().saveForm(function(record) {
                        grid.getStore().load();
                        popup.destroy();
                    });
                }
            }, {
                ui: 'secondary-action',
                reference: 'detailclosebtn',
                itemId: 'detailclosebtn',
                text: CMDBuildUI.locales.Locales.common.actions.close,
                autoEl: {
                    'data-testid': 'relations-list-grid-editcard-cancel'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.close'
                },
                handler: function(btn, event) {
                    popup.destroy();
                }
            }],

            listeners: {
                itemupdated: function () {
                    popup.close();
                    grid.getStore().load();
                },
                cancelupdating: function () {
                    popup.close();
                }
            }
        };
        popup = CMDBuildUI.util.Utilities.openPopup('popup-edit-card', record.get("_destinationDescription"), config);
    },

    /**
     * @param {CMDBuildUI.view.relations.list.Grid} grid
     * @param {Ext.data.Model} record
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onItemDblClick: function (grid, record, item, index, e, eOpts) {
        this.onActionOpenCard(grid, record);
    }
});

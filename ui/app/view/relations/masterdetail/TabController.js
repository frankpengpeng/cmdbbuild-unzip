Ext.define('CMDBuildUI.view.relations.masterdetail.TabController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-masterdetail-tab',

    control: {
        '#': {
            targetdataupdated: 'onTargetDataUpdated'
        },
        'grid': {
            rowdblclick: 'onGridRowDblclick'
        },
        '#adddetailbtn': {
            click: 'onAddDetailButtonClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.relations.masterdetail.Tab} view
     * @param {CMDBuildUI.model.classes.Class|CMDBuildUI.model.processes.Process} targetTypeObject
     * @param {CMDBuildUI.model.classes.Card} targetTypeModel
     * @param {CMDBuildUI.model.domains.Domain} domain
     * @param {Object} eOpts
     */
    onTargetDataUpdated: function (view, targetTypeObject, targetTypeModel, domain, eOpts) {
        var me = this;
        var vm = view.lookupViewModel();
        // get reference field name for target model
        targetTypeModel.getFields().forEach(function (field) {
            if (field.attributeconf && field.attributeconf.domain === domain.get("name")) {
                me._targetreferencefield = field.name;
            }
        });

        var detailxtype, btnHidden;
        if (vm.get("targetType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
            detailxtype = 'processes-instances-instance-view';
            btnHidden = true;
        } else {
            detailxtype = 'classes-cards-card-view';
            btnHidden = false;
        }

        // add grid
        var columns = CMDBuildUI.util.helper.GridHelper.getColumns(targetTypeModel.getFields(), {
            allowFilter: false,
            addTypeColumn: targetTypeObject.get("prototype")
        });
        view.add({
            xtype: 'relations-masterdetail-grid',
            reference: 'mdgrid',
            columns: columns,
            plugins: [{
                ptype: 'forminrowwidget',
                pluginId: 'forminrowwidget',
                expandOnDblClick: true,
                removeWidgetOnCollapse: true,
                widget: {
                    xtype: detailxtype,
                    viewModel: {
                        data: {
                            basepermissions: view.lookupViewModel().get("basepermissions")
                        }
                    }, // do not remove otherwise the viewmodel will not be initialized
                    tabpaneltools: [{
                        xtype: 'tool',
                        itemId: 'detaileditbtn',
                        iconCls: 'x-fa fa-pencil',
                        cls: 'management-tool',
                        disabled: true,
                        hidden: btnHidden,
                        tooltip: CMDBuildUI.locales.Locales.relations.editdetail,
                        callback: function (panel, tool, event) {
                            me.onItemEditButtonClick(panel.up());
                        },
                        autoEl: {
                            'data-testid': 'relations-masterdetail-tab-editBtn'
                        },
                        bind: {
                            disabled: '{!basepermissions.edit && !permissions.edit}'
                        }
                    }, {
                        xtype: 'tool',
                        itemId: 'detailopenbtn',
                        iconCls: 'x-fa fa-external-link',
                        cls: 'management-tool',
                        hidden: false,
                        tooltip: CMDBuildUI.locales.Locales.relations.opendetail,
                        callback: function (panel, tool, event) {
                            me.onItemViewButtonClick(panel.up());
                        },
                        autoEl: {
                            'data-testid': 'relations-masterdetail-tab-openBtn'
                        }
                    }, {
                        xtype: 'tool',
                        itemId: 'detaildeletebtn',
                        iconCls: 'x-fa fa-trash',
                        cls: 'management-tool',
                        disabled: true,
                        hidden: btnHidden,
                        tooltip: CMDBuildUI.locales.Locales.relations.deletedetail,
                        callback: function (panel, tool, event) {
                            me.onItemDeleteButtonClick(panel.up());
                        },
                        autoEl: {
                            'data-testid': 'relations-masterdetail-tab-deleteBtn'
                        },
                        bind: {
                            disabled: '{!basepermissions.delete && !permissions.delete}'
                        }
                    }]
                }
            }],
            viewModel: {
                data: {
                    objectTypeName: view.getTargetTypeName()
                }
            }
        });

        // update button
        if (targetTypeObject.get("prototype")) {
            // if is superclass add menu with all children leafs
            var children = targetTypeObject.getChildren(true);
            var menu = [];
            for (var i = 0; i < children.length; i++) {
                var child = children[i];
                menu.push({
                    text: child.getTranslatedDescription(),
                    iconCls: 'x-fa fa-file-text-o',
                    listeners: {
                        click: 'onAddDetailMenuItemClick'
                    },
                    disabled: !child.get(CMDBuildUI.model.base.Base.permissions.edit),
                    type: child.get("name")
                });
            }
            this.lookupReference("adddetailbtn").setMenu(Ext.Array.sort(menu, function (a, b) {
                return a.text === b.text ? 0 : (a.text < b.text ? -1 : 1);
            }));
        }

        vm.bind("{records}", function(records) {
            records.addListener("load", function() {
                vm.set("mditems", records.getTotalCount());
            });
        });
    },

    /**
     * @param {Ext.button.Button} button Add detail botton
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddDetailButtonClick: function (button, e, eOpts) {
        var view = this.getView();
        var targetTypeName = view.getTargetTypeName();
        var targetTypeObject = view.getTargetTypeObject();
        if (!targetTypeObject.get("prototype")) {
            // if element is not prototype
            this.showAddDetailForm(targetTypeName, targetTypeObject.get("description"));
        }
    },

    /**
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onAddDetailMenuItemClick: function (item, event, eOpts) {
        this.showAddDetailForm(item.type, item.text);
    },

    /**
     * Show add detail form
     * @param {String} targetTypeName
     * @param {String} targetTypeDescription
     */
    showAddDetailForm: function (targetTypeName, targetTypeDescription) {
        var vm = this.getViewModel();
        var view = this.getView();
        CMDBuildUI.util.helper.ModelHelper.getModel(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            targetTypeName
        ).then(function (model) {
            var panel;
            var title = Ext.String.format("New {0}", targetTypeDescription);
            var config = {
                xtype: 'classes-cards-card-create',
                fireGlobalEventsAfterSave: false,
                viewModel: {
                    data: {
                        objectTypeName: targetTypeName
                    }
                },
                defaultValues: [{
                    domain: view.getDomain().get("name"),
                    value: vm.get("objectId"),
                    valuedescription: vm.get("objectDescription"),
                    editable: false
                }],
                redirectAfterSave: false,
                buttons: [{
                    ui: 'management-action',
                    reference: 'detailsavebtn',
                    itemId: 'detailsavebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    autoEl: {
                        'data-testid': 'widgets-createmodifycard-save'
                    },
                    formBind: true,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.save'
                    },
                    handler: function (btn, event) {
                        panel.down("classes-cards-card-create").getController().saveForm(function (record) {
                            vm.get("records").load();
                            panel.destroy();
                        });
                    }
                }, {
                    ui: 'secondary-action',
                    reference: 'detailclosebtn',
                    itemId: 'detailclosebtn',
                    text: CMDBuildUI.locales.Locales.common.actions.close,
                    autoEl: {
                        'data-testid': 'widgets-createmodifycard-close'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.close'
                    },
                    handler: function (btn, event) {
                        panel.destroy();
                    }
                }]
            };
            panel = CMDBuildUI.util.Utilities.openPopup('popup-add-detail', title, config, null);
        }, function () {
        });
    },

    /**
     * Called when card is shown
     * @param {CMDBuildUI.view.classes.cards.card.View} cardpanel 
     */
    onItemViewButtonClick: function (cardpanel) {
        var cvm = cardpanel.getViewModel();
        this.redirectToItem(cvm.get("objectTypeName"), cvm.get("objectId"), cvm.get("activityId"));
    },

    /**
     * Called when card is shown
     * @param {CMDBuildUI.view.classes.cards.card.View} cardpanel 
     */
    onItemEditButtonClick: function (cardpanel) {
        var popup;
        var vm = this.getViewModel();
        var grid = this.lookupReference("mdgrid");
        // open popup
        var title = cardpanel.getTitle();
        var cvm = cardpanel.getViewModel();
        var config = {
            xtype: 'classes-cards-card-edit',
            objectTypeName: cvm.get("objectTypeName"),
            objectId: cvm.get("objectId"),
            redirectAfterSave: false,
            fireGlobalEventsAfterSave: false,
            overrideReadOnlyFields: this._targetreferencefield ? [this._targetreferencefield] : [],
            buttons: [{
                ui: 'management-action',
                reference: 'detailsavebtn',
                itemId: 'detailsavebtn',
                text: CMDBuildUI.locales.Locales.common.actions.save,
                autoEl: {
                    'data-testid': 'widgets-createmodifycard-save'
                },
                formBind: true,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                },
                handler: function (btn, event) {
                    popup.down("classes-cards-card-edit").getController().saveForm(function (record) {
                        grid.updateRowWithExpader(record);
                        popup.destroy();
                    });
                }
            }, {
                ui: 'secondary-action',
                reference: 'detailclosebtn',
                itemId: 'detailclosebtn',
                text: CMDBuildUI.locales.Locales.common.actions.close,
                autoEl: {
                    'data-testid': 'widgets-createmodifycard-close'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.close'
                },
                handler: function (btn, event) {
                    popup.destroy();
                }
            }]
        };
        popup = CMDBuildUI.util.Utilities.openPopup('popup-edit-card', title, config);
    },

    /**
     * Called when card is shown
     * @param {CMDBuildUI.view.classes.cards.card.View} cardpanel 
     */
    onItemDeleteButtonClick: function (cardpanel) {
        var me = this;
        Ext.Msg.confirm(
            "Attention",    //TODO:translate
            "Are you sure you want to delete this card?",   //TODO:translate
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('relation.delete');
                    cardpanel.getViewModel().get("theObject").erase({
                        success: function (record, operation) {
                            me.getViewModel().get("records").load();
                        }
                    });
                }
            },
            this
        );
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // get value
        var searchTerm = vm.get("search.value");
        if (searchTerm) {
            CMDBuildUI.util.Ajax.setActionId("card.details.search");
            // add filter
            var store = vm.get("records");
            store.getAdvancedFilter().addQueryFilter(searchTerm);
            store.load();
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        CMDBuildUI.util.Ajax.setActionId("card.details.clearfilter");
        // clear store filter
        var store = vm.get("records");
        store.getAdvancedFilter().clearQueryFilter();
        store.load();
        // reset input
        field.reset();
    },

    /**
    * @param {Ext.form.field.Base} field
    * @param {Ext.event.Event} event
    */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.attachments.Grid} view 
     * @param {Ext.data.Model} record 
     * @param {HTMLElement} element 
     * @param {Number} rowIndex 
     * @param {Ext.event.Event} event 
     * @param {Object} eOpts 
     */
    onGridRowDblclick: function (view, record, element, rowIndex, event, eOpts) {
        this.redirectToItem(record.get("_type"), record.getId(), record.get("_activity_id"));
    },

    privates: {
        /**
         * @property {String} _targetreferencefield
         * The name of the field in target model which has as domain current domain.
         */
        _targetreferencefield: null,

        /**
         * 
         * @param {Ext.data.Model} record 
         */
        redirectToItem: function (objecttypename, objectid, activityid) {
            var vm = this.getViewModel();
            if (vm.get("targetType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                url = Ext.String.format(
                    "processes/{0}/instances/{1}/activities/{2}/view",
                    objecttypename,
                    objectid,
                    activityid
                );
            } else {
                url = Ext.String.format(
                    "classes/{0}/cards/{1}/view",
                    objecttypename,
                    objectid
                );
            }
            this.redirectTo(url);
        }
    }
});


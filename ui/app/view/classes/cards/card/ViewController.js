Ext.define('CMDBuildUI.view.classes.cards.card.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-card-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#cloneMenuBtn': {
            click: 'onCloneMenuBtnClick'
        },
        '#printBtn': {
            click: 'onPrintBtnClick'
        },
        '#bimBtn': {
            click: 'onBimButtonClick'
        },
        '#relgraphBtn': {
            click: 'onRelationGraphBtnClick'
        },
        '#openTabsBtn': {
            click: 'onOpenTabsBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.card.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        if (!view.getObjectTypeName() && !view.getObjectId()) {
            var config = view.getInitialConfig();
            if (!Ext.isEmpty(config._rowContext)) {
                var record = config._rowContext.record; // get widget record
                if (record && record.getData()) {
                    // view.setObjectTypeName(record.getRecordType());
                    // view.setObjectId(record.getRecordId());
                    vm.set("objectTypeName", record.getRecordType());
                    vm.set("objectId", record.getRecordId());
                }
            }
        }

        // bind object type name and object id
        // to get model and load card data
        this.getViewModel().bind({
            bindTo: {
                objectTypeName: '{objectTypeName}',
                objectId: '{objectId}'
            }
        }, this.onObjectTypeNameAndIdChanged, this);

        // bind card data load to show the form
        vm.bind({
            bindTo: {
                theobjecttype: '{theObject._type}',
                theobjectid: '{theObject._id}',
                objectmodel: '{objectModel}'
            }
        }, this.onObjectLoaded, this);
    },

    /**
     * Triggered on edit tool click.
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (tool, event, eOpts) {
        CMDBuildUI.util.Ajax.setActionId("class.card.edit");
        var url = this.getBasePath() + '/edit';
        this.redirectTo(url, true);
    },

    /**
     * Triggered on delete tool click.
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (tool, event, eOpts) {
        var view = this.getView();
        var vm = view.lookupViewModel();

        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.classes.cards.deleteconfirmation,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('class.card.delete');
                    // get the object
                    vm.get("theObject").erase({
                        success: function (record, operation) {
                            // fire global card deleted event
                            Ext.GlobalEvents.fireEventArgs("carddeleted");
                            // close detail window
                            if (view.getShownInPopup()) {
                                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
                            }
                        }
                    });
                }
            }, this);
    },

    /**
     * Triggered on open tool click.
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onOpenBtnClick: function (tool, event, eOpts) {
        CMDBuildUI.util.Ajax.setActionId("class.card.view");
        var url = this.getBasePath() + '/view';
        this.redirectTo(url, true);
    },

    /**
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onBimButtonClick: function (tool, event, eOpts) {
        CMDBuildUI.util.Ajax.setActionId("class.card.bim.open");
        CMDBuildUI.util.Utilities.openPopup('bimPopup', CMDBuildUI.locales.Locales.bim.bimViewer, { //FUTURE: create a configuration for passing the poid and ifctype
            xtype: 'bim-container',
            projectId: this.getViewModel().get('bim.projectid')
        });
    },

    /**
     * triggered on the relation graph btn click
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onRelationGraphBtnClick: function (tool, event, eOpts) {
        CMDBuildUI.util.Ajax.setActionId("class.card.relgraph.open");
        var me = this;
        CMDBuildUI.util.Utilities.openPopup('graphPopup', CMDBuildUI.locales.Locales.relationGraph.relationGraph, {
            xtype: 'graph-graphcontainer',
            _id: me.getViewModel().get('theObject').get('_id'),
            _type: me.getViewModel().get('theObject').get('_type'),
            _code: me.getViewModel().get('theObject').get('Code'),
            _description: me.getViewModel().get('theObject').get('Description')

        });
    },

    /**
     * Triggered on open tabs button click.
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onOpenTabsBtnClick: function (tool, event, eOpts) {
        var me = this;
        var configAttachments = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled);
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        var items = [];

        // details action
        if (privileges.card_tab_detail_access) {
            items.push({
                tooltip: CMDBuildUI.locales.Locales.common.tabs.details,
                iconCls: 'x-fa fa-th-list',
                cls: 'management-tool',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        CMDBuildUI.util.Ajax.setActionId("class.card.details.open");
                        me.redirectTo(me.getBasePath() + '/details', true);
                    }
                }
            });
        }

        // notes action
        if (privileges.card_tab_note_access) {
            items.push({
                tooltip: CMDBuildUI.locales.Locales.common.tabs.notes,
                iconCls: 'x-fa fa-sticky-note',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        CMDBuildUI.util.Ajax.setActionId("class.card.notes.open");
                        me.redirectTo(me.getBasePath() + '/notes', true);
                    }
                }
            });
        }

        // relations action
        if (privileges.card_tab_relation_access) {
            items.push({
                tooltip: CMDBuildUI.locales.Locales.common.tabs.relations,
                iconCls: 'x-fa fa-link',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        CMDBuildUI.util.Ajax.setActionId("class.card.relations.open");
                        me.redirectTo(me.getBasePath() + '/relations', true);
                    }
                }
            });
        }

        // history action
        if (privileges.card_tab_history_access) {
            items.push({
                tooltip: CMDBuildUI.locales.Locales.common.tabs.history,
                iconCls: 'x-fa fa-history',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        CMDBuildUI.util.Ajax.setActionId("class.card.history.open");
                        me.redirectTo(me.getBasePath() + '/history', true);
                    }
                }
            });
        }

        // email action
        if (privileges.card_tab_email_access) {
            items.push({
                tooltip: CMDBuildUI.locales.Locales.common.tabs.emails,
                iconCls: 'x-fa fa-envelope',
                height: 32,
                listeners: {
                    click: function (menuitem, eOpts) {
                        CMDBuildUI.util.Ajax.setActionId("class.card.emails.open");
                        me.redirectTo(me.getBasePath() + '/emails', true);
                    }
                }
            });
        }

        // attachments action
        if (configAttachments && privileges.card_tab_attachment_access) {
            items.push({
                tooltip: CMDBuildUI.locales.Locales.common.tabs.attachments,
                iconCls: 'x-fa fa-paperclip',
                height: 32,
                hidden: !configAttachments,
                listeners: {
                    click: function (menuitem, eOpts) {
                        CMDBuildUI.util.Ajax.setActionId("class.card.attachments.open");
                        me.redirectTo(me.getBasePath() + '/attachments', true);
                    }
                }
            });
        }

        if (items.length) {
            var menu = Ext.create('Ext.menu.Menu', {
                autoShow: true,
                items: items,
                ui: 'actionmenu'
            });
            menu.setMinWidth(35);
            menu.setWidth(35);
            menu.alignTo(tool.el.id, 't-b?');
        }
    },

    /**
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onCloneMenuBtnClick: function (tool, event, eOpts) {
        var me = this;
        var menu = Ext.create('Ext.menu.Menu', {
            autoShow: true,
            ui: 'actionmenu',
            items: [{
                tooltip: CMDBuildUI.locales.Locales.classes.cards.clone,
                iconCls: 'x-fa fa-file-o',
                height: 32,
                handler: function () {
                    me.onCloneMenuItemClick();
                }
            }, {
                tooltip: CMDBuildUI.locales.Locales.classes.cards.clonewithrelations,
                iconCls: 'x-fa fa-file-text-o',
                height: 32,
                handler: function () {
                    me.onCloneWithRelationsMenuItemClick();
                }
            }]
        });
        menu.setMinWidth(35);
        menu.setWidth(35);
        menu.alignTo(tool.el.id, 't-b?');
    },

    onCloneMenuItemClick: function () {
        CMDBuildUI.util.Ajax.setActionId("class.card.clone");
        var url = this.getBasePath() + '/clone';
        this.redirectTo(url, true);
    },

    onCloneWithRelationsMenuItemClick: function () {
        CMDBuildUI.util.Ajax.setActionId("class.card.clonewithrelations");
        var vm = this.getViewModel();
        var title = CMDBuildUI.locales.Locales.classes.cards.clonewithrelations;
        var popupId = 'popup-clone-card-and-relations';

        var config = {
            xtype: 'classes-cards-relations-panel',
            viewModel: {
                data: vm.getData()
            }
        };
        CMDBuildUI.util.Utilities.openPopup(
            popupId,
            title,
            config,
            {}
        );
    },

    /**
     * 
     * @param {Ext.panel.Tool} tool
     * @param {Ext.Event} event
     * @param {Object} eOpts
     */
    onPrintBtnClick: function (tool, event, eOpts) {
        function printCard(format) {
            var vm = tool.lookupViewModel();

            // url and format
            var url = CMDBuildUI.util.api.Classes.getPrintCardUrl(
                vm.get("objectTypeName"),
                vm.get("objectId"),
                format
            );
            url += "?extension=" + format;

            // open file in popup
            CMDBuildUI.util.Utilities.openPrintPopup(url);
        }

        var menu = Ext.create('Ext.menu.Menu', {
            autoShow: true,
            ui: 'actionmenu',
            items: [{
                iconCls: 'x-fa fa-file-pdf-o',
                itemId: 'printPdfBtn',
                tooltip: CMDBuildUI.locales.Locales.common.grid.printpdf,
                text: CMDBuildUI.locales.Locales.common.grid.printpdf,
                printformat: 'pdf',
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.common.grid.printpdf',
                    text: 'CMDBuildUI.locales.Locales.common.grid.printpdf'
                },
                handler: function () {
                    printCard("pdf");
                }
            }, {
                iconCls: 'x-fa fa-file-word-o',
                itemId: 'printOdtBtn',
                tooltip: CMDBuildUI.locales.Locales.common.grid.printodt,
                text: CMDBuildUI.locales.Locales.common.grid.printodt,
                printformat: 'odt',
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.common.grid.printodt',
                    text: 'CMDBuildUI.locales.Locales.common.grid.printodt'
                },
                handler: function () {
                    printCard("odt");
                }
            }]
        });
        menu.setMinWidth(35);
        menu.setWidth(35);
        menu.alignTo(tool.el.id, 't-b?');
    },

    privates: {
        /**
         * Get resource base path for routing.
         * @return {String}
         */
        getBasePath: function () {
            var vm = this.getViewModel();
            return 'classes/' + vm.get("objectTypeName") + '/cards/' + vm.get("objectId");
        },

        /**
         * 
         * @param {Object} data 
         * @param {String} data.objectTypeName
         * @param {Number|String} data.objectId
         */
        onObjectTypeNameAndIdChanged: function (data) {
            var vm = this.getViewModel();
            var me = this;
            if (data.objectTypeName && data.objectId) {
                CMDBuildUI.util.helper.ModelHelper.getModel(
                    CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                    data.objectTypeName
                ).then(function (model) {
                    vm.set("objectModel", model);

                    vm.linkTo("theObject", {
                        type: model.getName(),
                        id: data.objectId
                    });
                });
            }
        },

        /**
         * 
         * @param {Object} data 
         * @param {String} data.theobjecttype
         * @param {CMDBuildUI.model.classes.Card} data.objectmodel
         */
        onObjectLoaded: function (data) {
            var view = this.getView();
            var vm = this.getViewModel();
            if (data.theobjecttype && data.objectmodel) {
                var items = [];
                if (view.getShownInPopup()) {
                    // get form fields as fieldsets
                    items = view.getDynFormFields();

                    if (!view.getHideTools()) {
                        // add toolbar
                        var toolbar = {
                            xtype: 'toolbar',
                            cls: 'fieldset-toolbar',
                            items: Ext.Array.merge([{ xtype: 'tbfill' }], view.tabpaneltools)
                        };
                        Ext.Array.insert(items, 0, [toolbar]);
                    }
                    items = view.getMainPanelForm(items);
                } else {
                    // get form fields as tab panel
                    var panel = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get("objectModel"), {
                        mode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
                        showAsFieldsets: false
                    });

                    if (!view.getHideTools()) {
                        // add toolbar
                        Ext.apply(panel, {
                            tools: view.tabpaneltools
                        });
                    }

                    items.push(panel);
                }
                view.removeAll(true);
                view.add(items);

                // add conditional visibility rules
                view.addConditionalVisibilityRules();
            }
        }
    }

});

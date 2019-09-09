Ext.define('CMDBuildUI.view.emails.DMSAttachments.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.emails-dmsattachments-panel',
    control: {
        '#': {
            beforeRender: 'onBeforeRender'
        },
        '#comboclass': {
            change: 'onComboClassChange'
        },
        '#saveBtn': {
            click: 'onSaveBtn'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }

    },

    /**
     * @param {CMDBuildUI.view.emails.Edit.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        this.getViewModel().set('firstload', true);
        var objectTypeName = view.getViewModel().get('objectTypeName');
        var combobox = view.lookupReference('comboclass');
        combobox.setValue(objectTypeName);
    },

    /**
     * @param {Ext.form.field.ComboBox} combos
     * @param {String} newValue
     * @param {String} oldValue
     * @param {Object} eOpts
     * 
     */
    onComboClassChange: function (combo, newValue, oldValue, eOpts) {
        var me = this;
        var vm = this.getView().getViewModel();
        if (vm.getData().comboclass.selection) {
            var typeSelected = vm.getData().comboclass.selection.get('type');
            if (newValue) {
                me.setContainerGrid(typeSelected, newValue);
            }
        }
    },

    /**
     * @param {CMDBuildUI.view.emails.Create} view
     * @param {Object} eOpts
     */
    onSaveBtn: function (view, eOpts) {
        var me = this;
        var selected = this.getView().lookupReference('attachmentgrid').getSelection();
        var attachmentStore = this.getView().config.store;
        var objectTypeName = this.getViewModel().get('selected').type;
        var objectId = this.getViewModel().get('selected').id;
        selected.forEach(function (selatt) {
            if (me.checkAlreadyExists(selatt, attachmentStore)) {
                var w = Ext.create('Ext.window.Toast', {
                    title: CMDBuildUI.locales.Locales.notifier.warning,
                    html: CMDBuildUI.locales.Locales.emails.alredyexistfile,
                    iconCls: 'x-fa fa-exclamation-circle',
                    align: 'br'
                });
                w.show();
            } else {
                selatt.set('objectTypeName', objectTypeName);
                selatt.set('objectId', objectId);
                selatt.set('DMSAttachment', true);
                selatt.set('newAttachment', true);
                attachmentStore.add(selatt.getData());
            }
        });

        var popup = this.getView().up("panel");
        popup.close();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var popup = this.getView().up("panel");
        popup.close();
    },
    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Numeric|String} newid
     * @param {Numeric|String} oldid
     */

    checkAlreadyExists: function (selatt, store) {
        var presence = false;
        var filename = selatt.get('name');
        var items = store.getRange();
        items.forEach(function (item) {
            if (item.get('name') == filename) {
                presence = true;
                return;
            }
        });
        return presence;
    },


    privates: {
        setAttachmentsGrid: function () {
            var attachmentContainer = this.lookupReference('attachmentcontainer');
            attachmentContainer.add({
                xtype: 'attachments-grid',
                reference: 'attachmentgrid',
                selModel: {
                    type: 'checkboxmodel'
                },
                columns: [{
                    text: CMDBuildUI.locales.Locales.attachments.filename,
                    dataIndex: 'name',
                    align: 'left',
                    hidden: false,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.attachments.filename'
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.attachments.description,
                    dataIndex: 'description',
                    align: 'left',
                    hidden: false,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.attachments.description'
                    }
                }]
            });
        },

        setContainerGrid: function (typeSelected, newValue) {
            var me = this;
            var xtype;
            var type;
            if (CMDBuildUI.util.helper.ModelHelper.objecttypes.klass == typeSelected) {
                xtype = 'classes-cards-grid-grid';
                type = 'classes-cards';
            } else {
                if (CMDBuildUI.util.helper.ModelHelper.objecttypes.process == typeSelected) {
                    xtype = 'processes-instances-grid';
                    type = 'processes-instances';
                }
            }
            var classContainer = me.lookupReference('classcontainer');
            var attachmentContainer = me.lookupReference('attachmentcontainer');
            CMDBuildUI.util.helper.ModelHelper.getModel(
                typeSelected,
                newValue
            ).then(function (model) {
                classContainer.removeAll(true);
                attachmentContainer.removeAll(true);
                me.setAttachmentsGrid();
                classContainer.add({
                    xtype: xtype,
                    reference: 'classGrid',
                    plugins: null,
                    scrollable: true,
                    maxHeight: 250,
                    bind: {
                        selection: '{selection}'
                    },
                    viewModel: {
                        stores: {
                            cards: {
                                type: type,
                                model: model.getName(),
                                autoLoad: true,
                                autoDestroy: true,
                                listeners: {
                                    beforeload: function (store, operation, eOpts) {
                                        var selId = me.getViewModel().get('objectId');
                                        var extraparams = store.getProxy().getExtraParams();
                                        extraparams.positionOf = selId;
                                        extraparams.positionOf_goToPage = false;
                                    },

                                    load: function (store, record) {
                                        var firstload = me.getViewModel().get('firstload');
                                        if (firstload) {
                                            me.getViewModel().set('firstload', false);
                                            var selId = me.getViewModel().get('objectId');
                                            var metadata = store.getProxy().getReader().metaData;
                                            var posinfo = metadata.positions[selId];
                                            var selected = [];
                                            selected.push(store.getById(selId));
                                            if (!posinfo.pageOffset) {
                                                me.lookup('classGrid').setSelection(selected);
                                            } else {
                                                view.ensureVisible(posinfo.positionInTable, {
                                                    callback: function () {
                                                        expandSelection(posinfo.positionInTable, store);
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    listeners: {
                        selectionChange: function (selection, record, eOpts) {
                            attachmentContainer.removeAll(true);
                            var proxytype = record[0].store.getProxy().type;
                            var cardId = record[0].getId();
                            var proxyurl = record[0].store.getProxy().getUrl() + "/" + cardId + "/attachments";
                            attachmentContainer.add({
                                xtype: 'attachments-grid',
                                reference: 'attachmentgrid',
                                scrollable: true,
                                selModel: {
                                    type: 'checkboxmodel'
                                },
                                columns: [{
                                    text: CMDBuildUI.locales.Locales.attachments.filename,
                                    dataIndex: 'name',
                                    align: 'left',
                                    hidden: false,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.attachments.filename'
                                    }
                                }, {
                                    text: CMDBuildUI.locales.Locales.attachments.description,
                                    dataIndex: 'description',
                                    align: 'left',
                                    hidden: false,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.attachments.description'
                                    }
                                }],
                                viewModel: {
                                    stores: {
                                        attachments: {
                                            type: 'attachments',
                                            autoLoad: true,
                                            autoDestroy: true,
                                            proxy: {
                                                url: proxyurl,
                                                type: proxytype
                                            }
                                        }

                                    }
                                }
                            });
                        }
                    }
                });
            });
        }
    }
});
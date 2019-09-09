Ext.define('CMDBuildUI.view.emails.email.Mixin', {
    mixinId: 'email-mixin',

    config: {
        modelValidation: true,
        // create/edit form
        items: [{
            xtype: 'fieldcontainer',
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            forceFit: true,
            layout: 'fit',
            fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
            items: [{
                layout: 'column',
                items: [{
                    xtype: 'combobox',
                    itemId: 'templatecombo',
                    fieldLabel: CMDBuildUI.locales.Locales.emails.composefromtemplate,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.emails.composefromtemplate'
                    },
                    queryMode: 'local',
                    forceSelection: true,
                    displayField: 'description',
                    valueField: '_id',
                    bind: {
                        store: '{templates}',
                        value: '{theEmail.template}',
                        disabled: '{disabled.templatechoice}'
                    },
                    columnWidth: 0.4
                }, {
                    xtype: 'checkboxfield',
                    fieldLabel: CMDBuildUI.locales.Locales.emails.keepsynchronization,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.emails.keepsynchronization'
                    },
                    bind: {
                        value: '{theEmail.keepSynchronization}',
                        disabled: '{disabled.keepsync}'
                    },
                    columnWidth: 0.2
                }, {
                    xtype: 'combobox',
                    reference: 'delay',
                    fieldLabel: CMDBuildUI.locales.Locales.emails.delay,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.emails.delay'
                    },
                    displayField: 'label',
                    valueField: 'value',
                    bind: {
                        store: '{delays}',
                        value: '{theEmail.delay}'
                    },
                    columnWidth: 0.4
                }]
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.emails.from,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.emails.from'
                },
                bind: {
                    value: '{theEmail.from}'
                }
            }, {
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.emails.to,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.emails.to'
                },
                bind: {
                    value: '{theEmail.to}'
                }
            }, {
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.emails.cc,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.emails.cc'
                },
                bind: {
                    value: '{theEmail.cc}'
                }
            }, {
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.emails.bcc,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.emails.bcc'
                },
                bind: {
                    value: '{theEmail.bcc}'
                }
            }, {
                xtype: 'textfield',
                allowBlank: false,
                fieldLabel: CMDBuildUI.locales.Locales.emails.subject,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.emails.subject'
                },
                bind: {
                    value: '{theEmail.subject}'
                }
            }, CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                reference: 'body',
                allowBlank: false,
                fieldLabel: CMDBuildUI.locales.Locales.emails.message,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.emails.message'
                },
                bind: {
                    value: '{theEmail._content_html}'
                }
            }), {
                padding: '15 0 15 0',
                layout: 'column',
                items: [{
                    xtype: 'filefield',
                    buttonOnly: true,
                    itemId: 'addfileattachment',
                    buttonText: CMDBuildUI.locales.Locales.emails.attachfile,
                    ui: 'secondary-action',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.emails.attachfile'
                    }
                }, {
                    xtype: 'button',
                    margin: '0 0 0 15',
                    itemId: 'addattachmentsfromdms',
                    reference: 'addattachmentsfromdms',
                    text: CMDBuildUI.locales.Locales.emails.addattachmentsfromdms,
                    ui: 'secondary-action',
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.emails.addattachmentsfromdms'
                    }
                }]
            }]
        }, {
            xtype: 'grid',
            reference: 'attachmentsgrid',
            itemId: 'attachmentsgrid',
            hideHeaders: true,
            columns: [{
                text: CMDBuildUI.locales.Locales.attachments.filename,
                dataIndex: 'name',
                align: 'left',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.attachments.filename'
                },
                width: '45%'
            }, {
                text: CMDBuildUI.locales.Locales.attachments.description,
                dataIndex: 'description',
                align: 'left',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.attachments.description'
                },
                width: '45%'
            }, {
                xtype: 'actioncolumn',
                minWidth: '10%', // width property not works. Use minWidth.
                align: 'left',
                items: [{
                        iconCls: 'attachments-grid-action x-fa fa-download',
                        getTip: function () {
                            return CMDBuildUI.locales.Locales.attachments.download;
                        },
                        handler: function (grid, rowIndex, colIndex) {
                            var record = grid.getStore().getAt(rowIndex);
                            var attachmentStore = grid.getStore();
                            var url = Ext.String.format(
                                "{0}/{1}/{2}?CMDBuild-Authorization={3}",
                                attachmentStore.getProxy().getUrl(), // base url 
                                record.getId(), // attachment id
                                record.get("name"), // file name 
                                CMDBuildUI.util.helper.SessionHelper.getToken() // session tocken
                            );
                            // open the url in new tab
                            window.open(url, "_blank");
                        },
                        isDisabled: function (view, rowIndex, colIndex, item, record) {
                            return record.get('newAttachment');
                        }
                    },
                    {
                        iconCls: 'attachments-grid-action x-fa fa-trash',
                        getTip: function () {
                            return CMDBuildUI.locales.Locales.attachments.deleteattachment;
                        },
                        handler: function (grid, rowIndex, colIndex) {
                            var attachmentStore = grid.getStore();
                            attachmentStore.removeAt(rowIndex);
                        }
                    }
                ]
            }],
            bind: {
                store: '{attachments}'
            }
        }],

        buttons: [{
            text: CMDBuildUI.locales.Locales.common.actions.save,
            reference: 'saveBtn',
            itemId: 'saveBtn',
            formBind: true,
            disabled: true,
            ui: 'management-action',
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.save'
            }
        }, {
            text: CMDBuildUI.locales.Locales.common.actions.cancel,
            reference: 'cancelBtn',
            itemId: 'cancelBtn',
            ui: 'secondary-action',
            localized: {
                text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
            }
        }]
    },

    /**
     * Add attachment from file
     * 
     * @param {Ext.form.field.File} filefield 
     */
    addFileAttachment: function (filefield) {
        var vm = this.lookupViewModel();
        var input = filefield.fileInputEl.dom.files[0];
        var store = vm.getStore('attachments');
        if (input) {
            if (this.checkAlreadyExists(input, store)) {
                var w = Ext.create('Ext.window.Toast', {
                    title: CMDBuildUI.locales.Locales.notifier.warning,
                    html: CMDBuildUI.locales.Locales.emails.alredyexistfile,
                    iconCls: 'x-fa fa-exclamation-circle',
                    align: 'br',
                    alwaysOnTop: 2
                });
                w.show();
            } else {
                store.add([{
                    name: input.name,
                    _modified: input.lastModifiedDate,
                    _file: input,
                    DMSAttachment: false,
                    newAttachment: true
                }]);
            }
        }
    },

    /**
     * Add attacmhent from database
     */
    addDmsAttachment: function () {
        var vm = this.lookupViewModel();
        var title = CMDBuildUI.locales.Locales.emails.dmspaneltitle;
        var config = {
            xtype: 'emails-dmsattachments-panel',
            store: vm.getStore('attachments'),
            viewModel: {
                data: {
                    objectTypeName: vm.get('objectTypeName'),
                    objectId: vm.get('objectId'),
                    objectType: vm.get('objectType')
                }
            }
        };
        CMDBuildUI.util.Utilities.openPopup('popup-add-attachmentfromdms-panel', title, config, null);
    },

    /**
     * 
     * @param {CMDBuildUI.model.base.ComboItem} template 
     */
    updateEmailFromTemplate: function (template) {
        var vm = this.lookupViewModel();
        if (template) {
            var theEmail = vm.get("theEmail");

            // set proxy url for new email
            if (theEmail.crudState === "C") {
                theEmail.getProxy().setUrl(vm.get("storeurl"));
            }

            // update card data
            theEmail.set("_card", vm.get("objectdata"));

            // update and save email from template
            theEmail.save({
                params: {
                    apply_template: true,
                    template_only: false
                }
            });
        }
    },

    checkAlreadyExists: function (filename, store) {
        var presence = false;
        var items = store.getRange();
        items.forEach(function (item) {
            if (item.get('name') == filename.name) {
                presence = true;
                return;
            }
        });
        return presence;
    }
});
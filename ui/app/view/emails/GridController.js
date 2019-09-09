Ext.define('CMDBuildUI.view.emails.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.emails-grid',

    control: {
        '#': {
            show: 'onShow'
        },
        '#composeemail': {
            click: 'onComposeEmail'
        },
        '#regenerateallemails': {
            click: 'onRegenerateAllEmails'
        },
        '#gridrefresh': {
            click: 'onGridRefresh'
        },
        'tableview': {
            actionview: 'onActionView',
            actiondelete: 'onActionDelete',
            actionedit: 'onActionEdit',
            actionsend: 'onActionSend',
            actionreply: 'onActionReply',
            actionregenerate: 'onActionRegenerate'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.emails.Grid} view 
     * @param {Object} eOpts 
     */
    onShow: function (view, eOpts) {
        if (this.getParentTabPanel().getFormMode() === CMDBuildUI.mixins.DetailsTabPanel.actions.edit) {
            this.updateEmailsFromTemplates();
        }
    },

    /**
     * @param {CMDBuildUI.view..emails.Container.button} view
     * @param {Object} eOpts
     */
    onComposeEmail: function (view, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        var email = Ext.create("CMDBuildUI.model.emails.Email");
        var attachment = Ext.create('CMDBuildUI.model.attachments.Attachment');

        var object = this.getParentTabPanel().getFormObject();
        var objectdata = object && object.getCleanData() || {};

        var config = {
            xtype: 'emails-create',
            viewModel: {
                data: {
                    objectId: vm.get('objectId'),
                    objectType: vm.get('objectType'),
                    objectTypeName: vm.get('objectTypeName'),
                    objectdata: objectdata,
                    storeurl: vm.get("emails").getProxy().getUrl(),
                    theEmail: email,
                    theAttachment: attachment
                }
            },
            listeners: {
                itemcreated: function () {
                    me.getStore('emails').reload();
                }
            }
        };

        CMDBuildUI.util.Utilities.openPopup('popup-compose-email', CMDBuildUI.locales.Locales.emails.composeemail, config, null);
    },

    onRegenerateAllEmails: function () {
        // TODO: regenearte all emails
    },

    onGridRefresh: function (view, epts) {
        this.getStore('emails').reload();
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionView: function (grid, record, rowIndex, colIndex) {
        var vm = this.getViewModel();
        var listeners = {};
        vm.set("theEmail", record);
        var config = {
            xtype: 'emails-view',
            viewModel: {
                data: {
                    objectId: vm.get('objectId'),
                    objectType: vm.get('objectType'),
                    objectTypeName: vm.get('objectTypeName'),
                    theEmail: vm.get('theEmail')
                }
            }
        };

        CMDBuildUI.util.Utilities.openPopup(
            'popup-view-email',
            CMDBuildUI.locales.Locales.emails.view,
            config,
            listeners
        );
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionDelete: function (grid, record, rowIndex, colIndex) {
        var vm = this.getViewModel();
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.emails.remove,
            CMDBuildUI.locales.Locales.emails.remove_confirmation,
            function (action) {
                if (action === "yes") {
                    record.getProxy().setUrl(vm.get("emails").getProxy().getUrl());
                    CMDBuildUI.util.Ajax.setActionId('emails.delete');
                    record.erase();
                }
            }
        );
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionEdit: function (grid, record, rowIndex, colIndex) {
        var vm = this.getViewModel();
        var listeners = {};
        var attachment = Ext.create('CMDBuildUI.model.attachments.Attachment');

        var object = this.getParentTabPanel().getFormObject();
        var objectdata = object && object.getCleanData() || {};

        var config = {
            xtype: 'emails-edit',
            viewModel: {
                data: {
                    objectId: vm.get('objectId'),
                    objectType: vm.get('objectType'),
                    objectTypeName: vm.get('objectTypeName'),
                    objectdata: objectdata,
                    storeurl: vm.get("emails").getProxy().getUrl(),
                    theEmail: record,
                    theAttachment: attachment
                }
            }
        };

        CMDBuildUI.util.Utilities.openPopup(
            'popup-edit-email',
            CMDBuildUI.locales.Locales.emails.view,
            config,
            listeners
        );
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionSend: function (grid, record, rowIndex, colIndex) {
        var vm = this.getViewModel();
        if (record && record.getData()) {
            var theEmail = record;
            theEmail.set('status', 'outgoing');
            var url = Ext.String.format(
                '{0}/classes/{1}/cards/{2}/emails',
                CMDBuildUI.util.Config.baseUrl,
                vm.get('objectTypeName'),
                vm.get('objectId')
            );
            theEmail.getProxy().setUrl(url);
            theEmail.save();
        }
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionReply: function (grid, record, rowIndex, colIndex) {
        var me = this;
        var vm = this.getViewModel();

        // email configuration
        var emailconf = {
            cc: record.get('cc'),
            bcc: record.get('bcc'),
            account: record.get("account")
        };

        // calculate receiver address
        if (record.get("status") === CMDBuildUI.model.emails.Email.statuses.received) {
            emailconf.to = record.get("from");
        } else if (record.get("status") === CMDBuildUI.model.emails.Email.statuses.sent) {
            emailconf.to = record.get("to");
        }

        // calculate prefix
        var subjectprefix = 'Re: ';
        if (Ext.String.startsWith(record.get('subject'), subjectprefix)) {
            emailconf.subject = record.get('subject');
        } else {
            emailconf.subject = subjectprefix + record.get('subject');
        }

        // calculate body
        var bodyprefix = Ext.String.format(
            CMDBuildUI.locales.Locales.emails.replyprefix,
            CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(record.get("date")),
            record.get("from")
        );

        // TODO: workaround - check if correct 
        emailconf.body = emailconf._content_html = Ext.String.format(
            "<p>&nbsp;</p><p>&nbsp;</p><p>{0}</p><blockquote>{1}</blockquote>",
            bodyprefix,
            record.get("_content_html")
        );

        // generate email
        var email = Ext.create("CMDBuildUI.model.emails.Email", emailconf);

        var attachment = Ext.create('CMDBuildUI.model.attachments.Attachment');
        var title = CMDBuildUI.locales.Locales.emails.composeemail;
        var config = {
            xtype: 'emails-create',
            viewModel: {
                data: {
                    objectId: vm.get('objectId'),
                    objectType: vm.get('objectType'),
                    objectTypeName: vm.get('objectTypeName'),
                    storeurl: vm.get("emails").getProxy().getUrl(),
                    theEmail: email,
                    theAttachment: attachment
                }
            },
            listeners: {
                itemcreated: function () {
                    me.getView().getStore('emails').reload();
                }
            }
        };

        var popup = CMDBuildUI.util.Utilities.openPopup('popup-compose-email', title, config, null);
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionRegenerate: function (grid, record, rowIndex, colIndex) {
        // TODO: regenearte single emails
    },

    /**
     * 
     * @param {Ext.data.Store} store 
     * @param {CMDBuildUI.model.emails.Template[]} records 
     * @param {Boolean} successful 
     * @param {Ext.data.operation.Read} operation 
     * @param {Object} eOpts 
     */
    onTemplatesStoreLoaded: function (store, records, successful, operation, eOpts) {
        var vm = this.getViewModel();
        var templates = vm.get("emailtemplatestoevaluate");
        store.each(function (template) {
            var tpl = Ext.Array.findBy(templates, function (t) {
                return template.get("name") === t.name;
            });
            if (tpl) {
                template.set("_condition", tpl.condition);
                template.set("notifyWith", tpl.notifywith);
            }
        });
    },

    privates: {
        /**
         * @return {Ext.tab.Panel}
         */
        getParentTabPanel: function () {
            return this.getView().up("tabpanel");
        },

        /**
         * @param {function} callback
         * @param {function} syncstore
         */
        updateEmailsFromTemplates: function (callback, syncstore) {
            var vm = this.getView().lookupViewModel();

            // get form data
            var tabpanel = this.getParentTabPanel();
            var object = tabpanel.getFormObject();
            if (!object) {
                return;
            }
            var objectdata = object.getCleanData();

            // get changes from last update
            var changed = CMDBuildUI.util.Utilities.getObjectChanges(objectdata, vm.get("lastcheckdata"));

            // update last check data
            vm.set("lastcheckdata", objectdata);

            // get drafts emails
            var emails = vm.get("emails");
            if (!emails) {
                if (callback) {
                    Ext.callback(callback, null, [true]);
                }
                return;
            }
            emails.filter({
                property: "status",
                value: CMDBuildUI.model.emails.Email.statuses.draft,
                exactMatch: true
            });
            var drafts = emails.getRange();
            emails.clearFilter();
            var templates = vm.get("templates") && vm.get("templates").getRange() || [];
            var porcessestemplates = 0;

            if (templates.length === 0) {
                Ext.callback(callback, null, [true]);
            }

            function finishEmailUpdating(success) {
                if (success && syncstore) {
                    if (!emails.needsSync && callback) {
                        Ext.callback(callback, null, [success]);
                    } else {
                        if (emails.getModifiedRecords().length || emails.getNewRecords().length || emails.getRemovedRecords().length) {
                            emails.sync({
                                success: function () {
                                    if (callback) {
                                        Ext.callback(callback, null, [true]);
                                    }
                                },
                                failure: function () {
                                    if (callback) {
                                        Ext.callback(callback, null, [false]);
                                    }
                                }
                            });
                        } else {
                            if (callback) {
                                Ext.callback(callback, null, [false]);
                            }
                        }
                    }
                } else {
                    if (callback) {
                        Ext.callback(callback, null, [success]);
                    }
                }
            }

            // get enabled templates
            var wasSuccessful = true;
            templates.forEach(function (template) {
                // get bindings
                var tplbindings = template.get("_bindings") && template.get("_bindings").client || [];
                var bindings = [];
                tplbindings.forEach(function (b) {
                    var sb = b && b.split(".") || "";
                    if (sb && sb.length) {
                        bindings.push(sb[0]);
                    }
                });

                // check bindings changes
                var haschanges = false;
                bindings.forEach(function (b) {
                    if (Ext.Array.contains(Object.keys(changed), b)) {
                        haschanges = true;
                    }
                });

                // get email for this template
                var email = Ext.Array.findBy(drafts, function (draft) {
                    return draft.get("template") == template.getId();
                });

                // delete email
                if (haschanges && !Ext.isEmpty(email) && email.get("keepSynchronization")) {
                    email.erase();
                }

                // create email
                if (
                    (Ext.isEmpty(bindings) && Ext.isEmpty(email)) || // no bindings and email not exists
                    (haschanges && Ext.isEmpty(email)) || // has changes on binding fields and email not exists
                    (haschanges && !Ext.isEmpty(email) && email.get("keepSynchronization")) // has changes on binding fields and email and keep synk is active
                ) {
                    // create temporary email to generate email from template
                    var newemail = Ext.create("CMDBuildUI.model.emails.Email", {
                        template: template.getId(),
                        notifyWith: template.get("notifyWith"),
                        _card: objectdata
                    });
                    if (template.get("_condition")) {
                        var condition = template.get("_condition");
                        if (!/^{\w+:\S+}$/.test(condition)) {
                            condition = Ext.String.format("{js:{0}}", template.get("_condition"));
                        }
                        newemail.set("_expr", condition);
                    }
                    newemail.getProxy().setUrl(emails.getProxy().getUrl());

                    // generate email from template
                    newemail.save({
                        params: {
                            apply_template: true,
                            template_only: true
                        },
                        callback: function (record, operation, success) {
                            porcessestemplates++;
                            if (success) {
                                // create email new email with given data
                                if (!template.get("_condition") || record.get("_expr") === "true" || record.get("_expr") === true) {
                                    emails.add([record.getCleanData()]);
                                }
                            } else {
                                // mark as failure
                                wasSuccessful = false;
                            }
                            if (porcessestemplates === templates.length) {
                                finishEmailUpdating(wasSuccessful);
                            }
                        }
                    });
                } else {
                    porcessestemplates++;
                    if (porcessestemplates === templates.length) {
                        finishEmailUpdating(wasSuccessful);
                    }
                }
            });
        }
    }

});
Ext.define('CMDBuildUI.view.emails.email.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.emails-edit',

    control: {
        '#saveBtn': {
            click: 'onSaveBtn'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#addfileattachment': {
            change: 'onAddFileAttachmentChange'
        },
        '#addattachmentsfromdms': {
            click: 'onAddAttachmentsFromDMS'
        },
        '#templatecombo': {
            select: 'onTemplateComboChange'
        }
    },

    /**
     * @param {Ext.form.field.File} filefield
     * @param {Object} value
     * @param {Object} eOpts
     */
    onAddFileAttachmentChange: function (filefield, value, eOpts) {
        this.getView().addFileAttachment(filefield);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddAttachmentsFromDMS: function (button, e, eOpts) {
        this.getView().addDmsAttachment();
    },

    /**
     * @param {Ext.form.field.Combobox} combo
     * @param {xt.data.Model/Ext.data.Model[]} record
     * @param {Object} eOpts
     */
    onTemplateComboChange: function (combo, record, eOpts) {
        this.getView().updateEmailFromTemplate(record);
    },

    /**
     * @param {CMDBuildUI.view.emails.Create} view
     * @param {Object} eOpts
     */
    onSaveBtn: function (view, eOpts) {
        var me = this;
        var createView = this.getView();
        var vm = createView.getViewModel();
        var attachmentStore = me.getView().getViewModel().getStore('attachments');
        var allItems = attachmentStore.getRange();
        var removedItems = attachmentStore.removed;
        var className = vm.get('objectTypeName');
        var objectId = vm.get('objectId');
        var url = Ext.String.format(
            '{0}/classes/{1}/cards/{2}/emails',
            CMDBuildUI.util.Config.baseUrl,
            className,
            objectId
        );
        var theEmail = vm.get('theEmail');
        theEmail.set("body", theEmail.get("_content_html"));
        theEmail.getProxy().setUrl(url);
        theEmail.save({
            callback: function (email, response, success) {
                if (createView) {
                    var emailId = email.get('_id');
                    removedItems.forEach(function (item) {
                        var url = Ext.String.format(
                            '{0}/classes/{1}/cards/{2}/emails/{3}/attachments',
                            CMDBuildUI.util.Config.baseUrl,
                            className,
                            objectId,
                            emailId
                        );
                        item.getProxy().setUrl(url);
                        item.erase();
                    });
                    allItems.forEach(function (item) {
                        if (item.get('newAttachment')) {
                            if (!item.get('DMSAttachment')) {
                                me.sendAttachment(item, emailId);
                            } else {
                                if (item.get('DMSAttachment')) {
                                    me.sendDMSAttachment(item, emailId);
                                }
                            }
                        }
                    });
                }
                createView.up().close();
            }
        });
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

    privates: {
        sendAttachment: function (attachment, emailId) {
            var vm = this.getViewModel();
            CMDBuildUI.util.Ajax.setActionId('attachment.upload');
            var input = attachment.get('_file');
            var formData = new FormData();

            // append attachment json data
            var jsonData = Ext.encode(vm.get("theAttachment").getData());
            var fieldName = 'attachment';
            try {
                formData.append(fieldName, new Blob([jsonData], {
                    type: "application/json"
                }));
            } catch (err) {
                CMDBuildUI.util.Logger.log(
                    "Unable to create attachment Blob FormData entry with type 'application/json', fallback to 'text/plain': " + err,
                    CMDBuildUI.util.Logger.levels.error
                );
                // metadata as 'text/plain' (format compatible with older webviews)
                formData.append(fieldName, jsonData);
            }

            var url = Ext.String.format(
                '{0}/classes/{1}/cards/{2}/emails/{3}/attachments',
                CMDBuildUI.util.Config.baseUrl,
                vm.get('objectTypeName'),
                vm.get('objectId'),
                emailId
            );

            // define method
            var method = "POST";

            CMDBuildUI.util.File.upload(method, formData, input, url, {
                failure: function (error) {
                    CMDBuildUI.util.Notifier.showErrorMessage(error);
                }
            });
        },

        sendDMSAttachment: function (attachment, emailId) {

            var vm = this.getViewModel();
            var objectTypeName = attachment.get('objectTypeName');
            var objectId = attachment.get('objectId');
            var attachmentId = attachment.get('_id');
            var extraParams = {
                copyFrom_class: objectTypeName,
                copyFrom_card: objectId,
                copyFrom_id: attachmentId
            };
            var url = Ext.String.format(
                '{0}/classes/{1}/cards/{2}/emails/{3}/attachments',
                CMDBuildUI.util.Config.baseUrl,
                vm.get('objectTypeName'),
                vm.get('objectId'),
                emailId
            );
            Ext.Ajax.request({
                url: url,
                method: 'POST',
                jsonData: {},
                params: extraParams
            });
        }
    }
});
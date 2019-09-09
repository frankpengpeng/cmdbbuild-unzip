Ext.define('CMDBuildUI.view.attachments.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.attachments-container',

    control: {
        '#addattachment': {
            click: 'onAddAttachmentClick'
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddAttachmentClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var attachment = Ext.create("CMDBuildUI.model.attachments.Attachment");

        // atachments form definition
        var config = {
            xtype: 'attachments-form',
            viewModel: {
                data: {
                    newAttachment: true,
                    url: this.getViewModel().get("attachments").getProxy().getUrl(),
                    theAttachment: attachment,
                    targetTypeObject: CMDBuildUI.util.helper.ModelHelper.getObjectFromName(vm.get("objectTypeName"), vm.get("objectType"))
                }
            }
        };

        // custom panel listeners
        var listeners = {
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                vm.get("attachments").load();
            }
        };
        // create panel
        CMDBuildUI.util.Utilities.openPopup(
            'popup-add-attachment',
            CMDBuildUI.locales.Locales.attachments.add,
            config, 
            listeners
        );
    }
});

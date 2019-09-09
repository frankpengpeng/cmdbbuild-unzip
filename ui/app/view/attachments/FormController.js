Ext.define('CMDBuildUI.view.attachments.FormController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.attachments-form',

    control: {
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var form = this.getView();
        if (form.isValid()) {
            var vm = this.getViewModel();
            CMDBuildUI.util.Ajax.setActionId('attachment.upload');
            var input = this.lookupReference("file").extractFileInput().files[0];

            // init formData
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

            // get url
            var url = this.getViewModel().get("url");

            // define method
            var method = vm.get("newAttachment") ? "POST" : "PUT";

            CMDBuildUI.util.File.upload(method, formData, input, url, {
                success: function () {
                    form.up("panel").close();
                },
                failure: function (error) {
                    CMDBuildUI.util.Notifier.showErrorMessage(error);
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var popup = this.getView().up("panel");
        popup.close();
    }
});
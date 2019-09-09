Ext.define('CMDBuildUI.view.importexport.ImportController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.importexport-import',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        "#importbtn": {
            click: 'onImportBtnClick'
        },
        '#downloadreportbtn': {
            afterrender: 'onDownloadReportBtnAfterRender'
        },
        '#sendreportbtn': {
            click: 'onSendReportBtnClick'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.importexport.Import} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel();
        CMDBuildUI.util.helper.ModelHelper.getModel(CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, view.getObject().get("name")).then(function (model) {
            vm.set("classmodel", model);
        });
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Object} eOpts 
     */
    onImportBtnClick: function (btn, eOpts) {
        var view = this.getView();
        var vm = this.getViewModel();

        // disable fields
        vm.set("disabled.template", true);
        vm.set("disabled.file", true);
        vm.set("disabled.importbtn", true);
        vm.set("disabled.closebtn", true);

        // prepare form data for file upload
        var formData = new FormData();
        var input = this.lookupReference("filefield").extractFileInput().files[0];
        var url = Ext.String.format("{0}/etl/templates/{1}/import", CMDBuildUI.util.Config.baseUrl, vm.get("values.template"));

        var lm = CMDBuildUI.util.Utilities.addLoadMask(view);
        CMDBuildUI.util.File.upload("POST", formData, input, url, {
            success: function (responseText, seOpts) {
                view.lookupReference("templatedefinition").collapse();
                vm.set("responsetext", responseText);
                var response = Ext.JSON.decode(responseText);
                vm.set("response", response.data);
                vm.set("hidden.importbtn", true);
                vm.set("hidden.response", false);
                vm.set("hidden.downloadreportbtn", false);
                vm.set("hidden.sendreportbtn", false);
                vm.set("disabled.closebtn", false);
                // refresh grid
                view.refreshGrid();

                CMDBuildUI.util.Utilities.removeLoadMask(lm);
            },
            failure: function (error, seOpts) {
                vm.set("disabled.template", false);
                vm.set("disabled.file", false);

                CMDBuildUI.util.Ajax.showMessages({ responseText: error }, {});

                CMDBuildUI.util.Utilities.removeLoadMask(lm);
            }
        });
    },

    onDownloadReportBtnAfterRender: function (btn) {
        btn.el.set({ "download": "report.txt" });
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Object} eOpts 
     */
    onCloseBtnClick: function (btn, eOpts) {
        var view = this.getView();
        if (view.closePopup) {
            view.closePopup();
        }
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Object} eOpts 
     */
    onSendReportBtnClick: function (btn, eOpts) {
        var email = Ext.create("CMDBuildUI.model.emails.Email", {
            template: "cm_send_to_current_user",
            subject: CMDBuildUI.locales.Locales.importexport.emailsubject,
            body: btn.lookupViewModel().get("responsetext"),
            status: "outgoing"
        });
        email.getProxy().setUrl(CMDBuildUI.util.api.Emails.getCardEmailsUrl("_ANY", "_ANY"));
        email.save({
            extraParams: {
                apply_template: true
            },
            success: function () {
                CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.importexport.emailsuccess);
            },
            failure: function () {
                CMDBuildUI.util.Notifier.showErrorMessage(CMDBuildUI.locales.Locales.importexport.emailfailure);
            }
        });
    }
});

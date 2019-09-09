Ext.define('CMDBuildUI.view.main.header.ChangePasswordController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.main-header-changepassword',

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
        var newpsw = this.lookupReference('password').getValue();
        var oldpsw = this.lookupReference('oldpassword').getValue();
        var me = this;
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + '/users/current/password',
            method: 'PUT',
            jsonData: {
                password: newpsw,
                oldpassword: oldpsw
            },
            success: function () {
                me.getView().up("panel").close();
            },
            failure: function (error) {
                CMDBuildUI.util.Notifier.showErrorMessage(error);
            }

        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.Utilities.closePopup('popup-change-password');
    }

});
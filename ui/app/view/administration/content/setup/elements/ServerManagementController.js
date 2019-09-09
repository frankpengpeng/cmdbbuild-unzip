Ext.define('CMDBuildUI.view.administration.content.setup.elements.ServerManagementController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-servermanagement',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        view.up('administration-content').getViewModel().set('title', 'Server management');
    },

    onDropCacheBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + '/system/cache/drop',
            method: 'POST',
            success: function (transport) {
                button.setDisabled(false);
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                CMDBuildUI.util.Notifier.showSuccessMessage('The server cache memory has been emptied', null, 'administration');
            }
        });
    },

    onSyncServicesBtnClick: function (button, e, eOpts) {
        
        setTimeout(function () {
            CMDBuildUI.util.Notifier.showSuccessMessage('The services are now synchronized', null, 'administration');
        }, 1000);
    },

    onUnlockAllCardsBtnClick: function (button, e, eOpts) {
       
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);

        Ext.Ajax.request({
            url: Ext.String.format(
                '{0}/locks/_ANY',
                CMDBuildUI.util.Config.baseUrl
            ),
            method: 'DELETE',
            success: function (response) {
                if(button.el.dom){
                    button.setDisabled(false);
                }
                Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);

                CMDBuildUI.util.Notifier.showSuccessMessage('All the cards have been unlocked', null, 'administration');
            },
            callback: function(){
                if(button.el.dom){
                    button.setDisabled(false);
                }
            }
        });
    }

});
Ext.define('CMDBuildUI.view.administration.content.setup.elements.GeneralOptionsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-generaloptions',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function(view){
        view.up('administration-content').getViewModel().set('title', 'General options');
        view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    }
    

});

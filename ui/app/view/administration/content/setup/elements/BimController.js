Ext.define('CMDBuildUI.view.administration.content.setup.elements.BimController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-bim',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function(view){
        view.up('administration-content').getViewModel().set('title', 'BIM');
        view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    }
    
    
});

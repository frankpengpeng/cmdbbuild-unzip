Ext.define('CMDBuildUI.view.administration.content.setup.RelationChartController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-relationchart',
    
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function(view){
        view.up('administration-content').getViewModel().set('title', 'Ralation chart');
        view.up('administration-content-setup-view').getViewModel().setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    }
    
    
});

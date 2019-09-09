Ext.define('CMDBuildUI.view.administration.content.importexportdata.templates.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-importexportdata-templates-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function(view){
        var vm = this.getViewModel();
        vm.set('title', 'Import / Export templates');
    }
});

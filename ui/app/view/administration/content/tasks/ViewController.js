Ext.define('CMDBuildUI.view.administration.content.tasks.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-tasks-view',

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

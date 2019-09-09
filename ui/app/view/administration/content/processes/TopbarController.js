Ext.define('CMDBuildUI.view.administration.content.processes.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-topbar',

    control:{
        '#addprocess':{
            click: 'onAddProcessClick'
        }
    },
    

    onAddProcessClick: function(){
        this.redirectTo('administration/processes',true);
        var vm = Ext.getCmp('administrationNavigationTree').getViewModel();
        vm.set('selected', null);
    }
});

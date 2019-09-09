Ext.define('CMDBuildUI.view.administration.content.classes.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-topbar',

    control:{
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addclass':{
            click: 'onAddClassClick'
        }
    },

    onBeforeRender: function(view){
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.classes);
    },
    onAddClassClick: function() {      
        this.redirectTo('administration/classes',true);
        var vm = Ext.getCmp('administrationNavigationTree').getViewModel();
        vm.set('selected', null);
    }
});

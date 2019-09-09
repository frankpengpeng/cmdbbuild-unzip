
Ext.define('CMDBuildUI.view.login.Container',{
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.login.ContainerController',
        'CMDBuildUI.view.login.ContainerModel',

        'CMDBuildUI.view.login.FormPanel'
    ],

    xtype: 'login-container',
    controller: 'login-container',
    viewModel: {
        type: 'login-container'
    },

    scrollable: true,

    // add data-testid attribute to element
    autoEl: {
        'data-testid' : 'login-container'
    },

    layout: {
        type: 'vbox',
        align: 'center',
        pack: 'center'
    },
    padding: 15,

    items : [{
        xtype: 'login-formpanel'
    }]
});

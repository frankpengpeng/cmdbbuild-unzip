Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-topbar',

    control: {
        '#addgroup': {
            click: 'onAddGroupClick'
        }
    },

    /**
     * 
     * @param {Ext.button} button 
     * @param {*} event 
     * @param {*} eOpts 
     */
    onAddGroupClick: function (button, event, eOpt) {
        this.redirectTo('administration/groupsandpermissions_empty', true);

        
        Ext.getCmp('administrationNavigationTree').getViewModel().set('selected', null);
        Ext.asap(function () {
            
            Ext.ComponentQuery.query('viewport')[0].getViewModel().set('isFormHidden', false);
        });
    }
});
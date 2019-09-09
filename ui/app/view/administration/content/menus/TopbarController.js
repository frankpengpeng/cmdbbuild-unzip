Ext.define('CMDBuildUI.view.administration.content.menus.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-menus-topbar',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
        }
    },
    /**
     * Before render
     * @param {CMDBuildUI.view.administration.content.menus.Topbar} view
     */
    onBeforeRender: function(view){
        view.up('administration-content').getViewModel().set('title', 'Menus');
    },
    /**
     * On add menu button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, e, eOpts) {   
        this.redirectTo('administration/menus', true);
        var vm = Ext.getCmp('administrationNavigationTree').getViewModel();
        vm.set('selected', null);
    }
});

Ext.define('CMDBuildUI.view.administration.content.users.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-users-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        // setTimeout(function () {
        //     var url = 'administration/users';
        //     var treeComponent = Ext.getCmp('administrationNavigationTree');
        //     var treeComponentStore = treeComponent.getStore();
        //     var selected = treeComponentStore.findNode("href", url);
        //     if (selected) {
        //         treeComponent.setSelection(selected);
        //     }
        // }, 250);
    }
});

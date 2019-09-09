Ext.define('CMDBuildUI.view.administration.content.classes.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-view',

    control: {
        '#': {
            bererender: 'onBeforeRender',
            afterlayout: 'onAfterLayout'
        }
    },

    onBeforeRender: function (view) {
        Ext.getStore('importexports.Template').load();
        // view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.classes);
    },
    onAfterLayout: function (panel) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
    }
});

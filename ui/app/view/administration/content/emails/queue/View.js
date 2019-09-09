Ext.define('CMDBuildUI.view.administration.content.emails.queue.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.queue.ViewController',
        'CMDBuildUI.view.administration.content.emails.queue.ViewModel'
    ],
    alias: 'widget.administration-content-emails-queue-view',
    controller: 'administration-content-emails-queue-view',
    viewModel: {
        type: 'administration-content-emails-queue-view'
    },

    items: [{
        xtype: 'administration-content-emails-queue-grid'
    }],
    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var vm = this.getViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.emails.queue);
        this.callParent(arguments);
    }
});
Ext.define('CMDBuildUI.view.administration.content.menus.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.menus.TopbarController'
    ],

    alias: 'widget.administration-content-menus-topbar',
    controller: 'administration-content-menus-topbar',
    viewModel: {},

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.menus.texts.add,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.menus.texts.add'
        },
        ui: 'administration-action-small',
        reference: 'addBtn',
        itemId: 'addBtn',
        iconCls: 'x-fa fa-plus',
        autoEl: {
            'data-testid': 'administration-menu-toolbar-addMenuBtn'
        }
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            hidden: '{!theMenuName}',
            html: CMDBuildUI.locales.Locales.administration.menus.singular + ': <b data-testid="administration-menus-toolbar-menuName">{theMenuName}</b>'
        }
    }]
});
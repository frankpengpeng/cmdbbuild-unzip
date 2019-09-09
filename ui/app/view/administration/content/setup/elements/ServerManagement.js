Ext.define('CMDBuildUI.view.administration.content.setup.elements.ServerManagement', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.ServerManagementController',
        'CMDBuildUI.view.administration.content.setup.elements.ServerManagementModel'
    ],
    alias: 'widget.administration-content-setup-elements-servermanagement',
    controller: 'administration-content-setup-elements-servermanagement',
    viewModel: {
        type: 'administration-content-setup-elements-servermanagement'
    },
    margin: 10,
    items: [{
        xtype: 'container',
        ui: 'administration-formpagination',
        scrollable: true,
        forceFit: true,
        items: [{
            style: 'margin-top:15px',
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    style : 'width : 170px;',
                    xtype: 'button',
                    ui: 'administration-action-small',
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.dropcache,
                    localized:{
                        text: 'CMDBuildUI.locales.Locales.administration.systemconfig.dropcache'
                    },
                    iconCls: 'x-fa fa-wrench',
                    handler: 'onDropCacheBtnClick'
                }]
            }]
        }, {
            style: 'margin-top:15px',
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'button',
                    style : 'width : 170px;',
                    ui: 'administration-action-small',
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.synkservices,
                    localized:{
                        text: 'CMDBuildUI.locales.Locales.administration.systemconfig.synkservices'
                    },
                    iconCls: 'x-fa fa-wrench',
                    handler: 'onSyncServicesBtnClick',
                    disabled: true,
                    bind: {
                        disabled: '{!theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__shark__DOT__enabled}'
                    }
                }]
            }]
        }, {
            style: 'margin-top:15px',
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'button',
                    style : 'width : 170px;',
                    ui: 'administration-action-small',
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.unlockallcards,
                    localized:{
                        text: 'CMDBuildUI.locales.Locales.administration.systemconfig.unlockallcards'
                    },
                    iconCls: 'x-fa fa-wrench',
                    handler: 'onUnlockAllCardsBtnClick'
                }]
            }]
        }]
    }]
});
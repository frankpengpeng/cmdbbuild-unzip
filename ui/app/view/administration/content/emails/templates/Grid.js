Ext.define('CMDBuildUI.view.administration.content.emails.templates.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.GridController',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],

    alias: 'widget.administration-content-emails-templates-grid',
    controller: 'administration-content-emails-templates-grid',
    viewModel: {},

    forceFit: true,
    columns: [{
        text: CMDBuildUI.locales.Locales.administration.emails.name,
        dataIndex: 'name',
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.name'
        },
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.emails.description,
        dataIndex: 'description',
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.description'
        },
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.emails.subject,
        dataIndex: 'subject',
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.emails.subject'
        },
        align: 'left'
    }],
    bind: {
        store: '{templates}'
    },

    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',
        expandOnDblClick: true,
        removeWidgetOnCollapse: true,
        widget: {
            xtype: 'administration-content-emails-templates-card-viewinrow',
            autoHeight: true,
            ui: 'administration-tabandtools',
            bind: {},
            viewModel: {}
        }
    }]

});
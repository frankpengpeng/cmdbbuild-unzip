
Ext.define('CMDBuildUI.view.reports.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.reports.ContainerController',
        'CMDBuildUI.view.reports.ContainerModel'
    ],

    alias: 'widget.reports-container',
    controller: 'reports-container',
    viewModel: {
        type: 'reports-container'
    },

    cls: 'reports-container',

    config: {
        /**
         * @cfg {String} objectTypeName
         * The report name.
         */
        objectTypeName: null,

        /**
         * @cfg {String} extension
         */
        extension: null
    },

    /**
     * @cf {Boolean} hideTitle
     * `true` to hide title bar.
     */
    hideTitle: false,

    publish: [
        'objectTypeName',
        'extension'
    ],

    bind: {
        title: '{title}',
        objectTypeName: '{objectTypeName}',
        extension: '{extension}'
    },

    tbar: [{
        xtype: 'tbfill'
    }, {
        xtype: 'button',
        ui: 'management-action',
        iconCls: 'x-fa fa-refresh',
        itemId: 'refreshbtn',
        tooltip: CMDBuildUI.locales.Locales.reports.reload,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.reports.reload'
        }
    }, {
        xtype: 'button',
        ui: 'management-action',
        iconCls: 'x-fa fa-download',
        itemId: 'downloadbtn',
        tooltip: CMDBuildUI.locales.Locales.reports.download,
        disabled: true,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.reports.download'
        },
        bind: {
            disabled: '{!downloadbtn.href}',
            href: '{downloadbtn.href}'
        }
    }],

    items: [{
        xtype: 'uxiframe',
        width: '100%',
        height: '100%',
        reference: 'reportiframe',
        ariaAttributes: {
            role: 'document'
        }
    }]
});

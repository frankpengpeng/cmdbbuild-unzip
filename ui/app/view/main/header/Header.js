Ext.define('CMDBuildUI.view.main.header.Header', {
    extend: 'Ext.toolbar.Toolbar',

    requires: [
        'CMDBuildUI.view.main.header.HeaderController',
        'CMDBuildUI.view.main.header.HeaderModel'
    ],

    xtype: 'main-header-header',
    controller: 'main-header-header',
    viewModel: {
        type: 'main-header-header'
    },

    padding: "10px 15px",

    // add data-testid attribute to element
    autoEl: {
        'data-testid': 'main-header-header'
    },

    items: [{
        xtype: 'main-header-logo',
        height: 30,
        cls: 'logo',
        reference: 'cmdbuildLogo',
        itemId: 'cmdbuildLogo',
        autoEl: {
            'data-testid': 'header-cmdbuildlogo'
        }
    }, {
        xtype: 'tbspacer'
    }, {
        xtype: 'image',
        alt: CMDBuildUI.locales.Locales.main.logo.companylogo,
        localized:{
            alt: 'CMDBuildUI.locales.Locales.main.logo.companylogo'
        },
        height: 30,
        cls: 'logo',
        reference: 'companyLogo',
        itemId: 'companyLogo',
        hidden: true,
        autoEl: {
            'data-testid': 'header-companylogo'
        },
        bind: {
            hidden: '{companylogoinfo.hidden}',
            src: '{companylogoinfo.url}'
        }
    }, {
        xtype: 'tbspacer',
        hidden: true,
        bind: {
            hidden: '{companylogoinfo.hidden}'
        }
    }, {
        xtype: 'tbtext',
        reference: 'instanceName',
        itemId: 'instanceName',
        cls: 'instancename',
        autoEl: {
            'data-testid': 'header-instancename'
        },
        bind: {
            text: '{instancename}'
        }
    }, {
        xtype: 'tbfill'
        // }, {
        //     iconCls: 'x-fa fa-search',
        //     ui: 'header',
        //     tooltip: CMDBuildUI.locales.Locales.main.searchinallitems,
        //     reference: 'globalsearch',
        //     itemId: 'globalsearch',
        //     hidden: true,
        //     autoEl: {
        //         'data-testid': 'header-globalsearch'
        //     },
        //     bind: {
        //         hidden: '{!isAuthenticated}'
        //     },
        //     localized: {
        //         tooltip: 'CMDBuildUI.locales.Locales.main.searchinallitems'
        //     }
        // }, {
        //     xtype: 'tbseparator',
        //     hidden: true,
        //     bind: {
        //         hidden: '{!isAuthenticated}'
        //     }
    }, {
        xtype: 'main-header-usermenu',
        iconCls: 'x-fa fa-users',
        hidden: true,
        autoEl: {
            'data-testid': 'header-usermenu'
        },
        bind: {
            hidden: '{!isAuthenticated}'
        }
    }, {
        iconCls: 'x-fa fa-cog',
        ui: 'header',
        tooltip: CMDBuildUI.locales.Locales.main.administrationmodule,
        reference: 'administrationbtn',
        itemId: 'administrationbtn',
        href: '#administration',
        hidden: true,
        autoEl: {
            'data-testid': 'header-administration'
        },
        bind: {
            hidden: '{!isAdministrator}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.main.administrationmodule'
        }
    }, {
        iconCls: 'x-fa fa-table',
        ui: 'header',
        tooltip: CMDBuildUI.locales.Locales.main.managementmodule,
        reference: 'managementbtn',
        itemId: 'managementbtn',
        href: '#management',
        hidden: true,
        autoEl: {
            'data-testid': 'header-management'
        },
        bind: {
            hidden: '{!isAdministrationModule}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.main.managementmodule'
        }
    }, {
        iconCls: 'x-fa fa-sign-out',
        ui: 'header',
        tooltip: CMDBuildUI.locales.Locales.main.logout,
        reference: 'logoutbtn',
        itemId: 'logoutbtn',
        hidden: true,
        autoEl: {
            'data-testid': 'header-logout'
        },
        bind: {
            hidden: '{!isAuthenticated}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.main.logout'
        }
    }]
});
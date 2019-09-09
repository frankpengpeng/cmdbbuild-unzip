Ext.define('CMDBuildUI.view.login.FormPanel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.login.FormPanelController',
        'CMDBuildUI.view.login.FormPanelModel',
        'Ext.ux.form.MultiSelect'
    ],

    xtype: 'login-formpanel',
    controller: 'login-formpanel',
    viewModel: {
        type: 'login-formpanel'
    },

    title: CMDBuildUI.locales.Locales.login.title, // default
    border: true,
    bodyPadding: 15,
    width: 300,
    layout: 'anchor',

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    localized: {
        title: 'CMDBuildUI.locales.Locales.login.title'
    },

    modelValidation: true,

    items: [{
            html: CMDBuildUI.locales.Locales.main.pleasecorrecterrors,
            localized: {
                html: 'CMDBuildUI.locales.Locales.main.pleasecorrecterrors'
            },
            cls: 'error',
            hidden: true,
            autoEl: {
                'data-testid': 'login-errormessage'
            },
            bind: {
                hidden: '{!showErrorMessage}'
            }
        }, {
            xtype: 'textfield',
            reference: 'usernameField',
            fieldLabel: CMDBuildUI.locales.Locales.login.fields.username,
            allowBlank: false,
            autoEl: {
                'data-testid': 'login-inputusername'
            },
            bind: {
                value: '{theSession.username}',
                disabled: '{disabledfields.username}'
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.login.fields.username'
            },
            listeners: {
                afterrender: function (field) {
                    field.focus(false, 200);
                }
            }
        }, {
            xtype: 'textfield',
            reference: 'passwordField',
            fieldLabel: CMDBuildUI.locales.Locales.login.fields.password,
            allowBlank: false,
            inputType: 'password',
            hidden: false,
            autoEl: {
                'data-testid': 'login-inputpassword'
            },
            bind: {
                value: '{password}',
                disabled: '{disabledfields.password}',
                hidden: '{hiddenfields.password}'
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.login.fields.password'
            }
        }, {
            xtype: 'combobox',
            reference: 'roleField',
            fieldLabel: CMDBuildUI.locales.Locales.login.fields.group,
            displayField: 'label',
            valueField: 'value',
            queryMode: 'local',
            forceSelection: true,
            editable: false,
            hidden: true,
            autoEl: {
                'data-testid': 'login-inputrole'
            },
            bind: {
                hidden: '{hiddenfields.role}',
                value: '{theSession.role}',
                store: '{groups}'
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.login.fields.group'
            }
        },
        {
            xtype: 'combobox',
            reference: 'activeTenantsFieldone',
            fieldLabel: CMDBuildUI.locales.Locales.login.fields.tenants,
            displayField: 'label',
            valueField: 'value',
            queryMode: 'local',
            forceSelection: true,
            editable: false,
            hidden: true,
            autoEl: {
                'data-testid': 'login-inputtenant-one'
            },
            bind: {
                hidden: '{hiddenfields.tenantsone}',
                value: '{theSession.activeTenants}',
                store: '{tenants}'
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.login.fields.tenants'
            }
        },
        {
            xtype: 'multiselectfield',
            reference: 'activeTenantsField',
            fieldLabel: CMDBuildUI.locales.Locales.login.fields.tenants,
            displayField: 'label',
            valueField: 'value',
            hidden: true,
            autoEl: {
                'data-testid': 'login-inputtenant'
            },
            bind: {
                hidden: '{hiddenfields.tenants}',
                store: '{tenants}'
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.login.fields.tenants'
            }
        },
        {
            xtype: 'combobox',
            reference: 'languageField',
            fieldLabel: CMDBuildUI.locales.Locales.login.fields.language,
            editable: false,
            forceSelection: true,
            // allowBlank: false,
            displayField: 'description',
            valueField: 'code',
            hidden: true,
            autoEl: {
                'data-testid': 'login-inputlanguage'
            },
            bind: {
                store: '{languages}',
                value: '{language}',
                hidden: '{hiddenfields.language}'
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.login.fields.language'
            }
        }
    ],

    buttons: [{
        text: CMDBuildUI.locales.Locales.login.buttons.login,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        reference: 'loginbtn',
        itemId: 'loginbtn',
        ui: 'management-action',
        autoEl: {
            'data-testid': 'login-btnlogin'
        },
        bind: {
            text: '{locales.buttons.login}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.login.buttons.login'
        }
    }, {
        text: CMDBuildUI.locales.Locales.login.buttons.logout,
        reference: 'logoutbtn',
        itemId: 'logoutbtn',
        ui: 'secondary-action',
        autoEl: {
            'data-testid': 'login-btnlogout'
        },
        bind: {
            text: '{locales.buttons.logout}',
            hidden: '{hiddenfields.cancelbtn}'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.login.buttons.logout'
        }
    }]
});
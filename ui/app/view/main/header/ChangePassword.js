Ext.define('CMDBuildUI.view.main.header.ChangePassword', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.main.header.ChangePasswordController',
        'CMDBuildUI.view.main.header.ChangePasswordModel'
    ],

    alias: 'widget.main-header-changepassword',
    controller: 'main-header-changepassword',
    viewModel: {
        type: 'main-header-changepassword'
    },

    bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    items: [{

        xtype: 'textfield',
        fieldLabel: CMDBuildUI.locales.Locales.main.oldpassword,
        allowBlank: false,
        localized: {
            text: 'CMDBuildUI.locales.Locales.main.oldpassword'
        },
        autoEl: {
            'data-testid': 'main-header-changepassword-oldpassword'
        },
        itemId: 'oldpassword',
        reference: 'oldpassword',
        name: 'oldpassword',
        inputType: 'password',
        triggers: {
            showPassword: {
                cls: 'x-fa fa-eye',
                hideTrigger: false,
                scope: this,
                handler: function (field, button, e) {
                    field.lookupReferenceHolder().lookupReference('oldpassword').inputEl.el.set({
                        type: 'text'
                    });
                    field.getTrigger('showPassword').setVisible(false);
                    field.getTrigger('hidePassword').setVisible(true);
                }
            },
            hidePassword: {
                cls: 'x-fa fa-eye-slash',
                hidden: true,
                scope: this,
                handler: function (field, button, e) {
                    // set the element type to text
                    field.lookupReferenceHolder().lookupReference('oldpassword').inputEl.el.set({
                        type: 'password'
                    });
                    field.getTrigger('showPassword').setVisible(true);
                    field.getTrigger('hidePassword').setVisible(false);
                }
            }
        }
    }, {
        xtype: 'textfield',
        fieldLabel: CMDBuildUI.locales.Locales.main.newpassword,
        allowBlank: false,
        localized: {
            text: 'CMDBuildUI.locales.Locales.main.newpassword'
        },
        autoEl: {
            'data-testid': 'main-header-changepassword-newpassword'
        },
        itemId: 'newpassword',
        reference: 'password',
        name: 'newpassword',
        inputType: 'password',
        triggers: {
            showPassword: {
                cls: 'x-fa fa-eye',
                hideTrigger: false,
                scope: this,
                handler: function (field, button, e) {
                    field.lookupReferenceHolder().lookupReference('newpassword').inputEl.el.set({
                        type: 'text'
                    });
                    field.getTrigger('showPassword').setVisible(false);
                    field.getTrigger('hidePassword').setVisible(true);
                }
            },
            hidePassword: {
                cls: 'x-fa fa-eye-slash',
                hidden: true,
                scope: this,
                handler: function (field, button, e) {
                    // set the element type to text
                    field.lookupReferenceHolder().lookupReference('newpassword').inputEl.el.set({
                        type: 'password'
                    });
                    field.getTrigger('showPassword').setVisible(true);
                    field.getTrigger('hidePassword').setVisible(false);
                }
            }
        }
    }, {
        xtype: 'textfield',
        fieldLabel: CMDBuildUI.locales.Locales.main.confirmpassword,
        allowBlank: false,
        localized: {
            text: 'CMDBuildUI.locales.Locales.main.confirmpassword'
        },
        autoEl: {
            'data-testid': 'main-header-changepassword-confirmpassword'
        },
        itemId: 'confirmpassword',
        reference: 'confirmpassword',
        vtype: 'passwordMatch',
        name: 'confirmpassword',
        inputType: 'password',
        triggers: {
            showPassword: {
                cls: 'x-fa fa-eye',
                hideTrigger: false,
                scope: this,
                handler: function (field, button, e) {
                    field.lookupReferenceHolder().lookupReference('confirmpassword').inputEl.el.set({
                        type: 'text'
                    });
                    field.getTrigger('showPassword').setVisible(false);
                    field.getTrigger('hidePassword').setVisible(true);
                }
            },
            hidePassword: {
                cls: 'x-fa fa-eye-slash',
                hidden: true,
                scope: this,
                handler: function (field, button, e) {
                    // set the element type to text
                    field.lookupReferenceHolder().lookupReference('confirmpassword').inputEl.el.set({
                        type: 'password'
                    });
                    field.getTrigger('showPassword').setVisible(true);
                    field.getTrigger('hidePassword').setVisible(false);
                }
            }
        }
    }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        formBind: true, // enable once the form is valid
        disabled: true,
        reference: 'saveBtn',
        itemId: 'saveBtn',
        ui: 'management-action',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        autoEl: {
            'data-testid': 'main-header-changepassword-saveBtn'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        reference: 'cancelBtn',
        itemId: 'cancelBtn',
        ui: 'secondary-action',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        autoEl: {
            'data-testid': 'main-header-changepassword-cancelBtn'
        }
    }]

});
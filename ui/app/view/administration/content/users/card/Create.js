Ext.define('CMDBuildUI.view.administration.content.users.card.Create', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.users.card.CreateController',
        'CMDBuildUI.view.administration.content.users.card.EditModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],

    alias: 'widget.administration-content-users-card-create',
    controller: 'view-administration-content-users-card-create',
    viewModel: {
        type: 'view-administration-content-users-card-edit'
    },

    config: {
        objectTypeName: null,
        /**
         * @cfg {Object[]}
         * 
         * Can set default values for any of the attributes. An object can be:
         * `{attribute: 'attribute name', value: 'default value', editable: true|false}` 
         * used for all attributes or
         * `{domain: 'domain name', value: 'default value', editable: true|false}` 
         * used to set default values for references fields.
         */
        defaultValues: null
    },
    bind: {
        data: {
            theUser: '{theUser}'
        }

    },
    modelValidation: true,
    autoScroll: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    bubbleEvents: [
        'itemcreated',
        'cancelcreation'
    ],
    items: [{
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        items: [{
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.username,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.username'
                },
                name: 'username',
                vtype: 'usernameValidation',
                enforceMaxLength: true,
                allowBlank: false,
                maxLength: 40,
                bind: {
                    value: '{theUser.username}'
                },
                listeners: {
                    afterrender: function (cmp) {
                        cmp.inputEl.set({
                            autocomplete: 'new-password'
                        });
                    },
                    change: function (input, newVal, oldVal) {
                        CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(input, newVal, oldVal, '[name="description"]');
                    }
                }
            }, {
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.texts.description,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.texts.description'
                },
                name: 'description',
                bind: {
                    value: '{theUser.description}'
                }
            }]
        }, {
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.navigation.email,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.navigation.email'
                },
                name: 'email',
                enforceMaxLength: true,
                maxLength: 320,
                bind: {
                    value: '{theUser.email}'
                }
            }]
        }, {
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.language,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.language'
                },
                labelAlign: 'top',
                name: 'language',
                valueField: 'code',
                displayField: 'description',
                queryMode: 'local',
                typeAhead: true,
                bind: {
                    store: '{languagesStore}',
                    value: '{theUser.language}'
                }
            }, {
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.initialpage,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.initialpage'
                },
                name: 'initialPage',
                valueField: '_id',
                displayField: 'label',
                queryMode: 'local',
                typeAhead: true,
                bind: {
                    value: '{theUser.initialPage}',
                    store: '{getAllPagesStore}'
                },
                triggers: {
                    foo: {
                        cls: 'x-form-clear-trigger',
                        handler: function () {
                            this.clearValue();
                        }
                    }
                },
                tpl: new Ext.XTemplate(
                    '<tpl for=".">',
                    '<tpl for="group" if="this.shouldShowHeader(group)"><div class="group-header">{[this.showHeader(values.group)]}</div></tpl>',
                    '<div class="x-boundlist-item">{label}</div>',
                    '</tpl>', {
                        shouldShowHeader: function (group) {
                            return this.currentGroup !== group;
                        },
                        showHeader: function (group) {
                            this.currentGroup = group;
                            return group;
                        }
                    })
            }]
        }, {
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.service,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.service'
                },
                name: 'service',
                bind: {
                    value: '{theUser.service}'
                }
            }, {
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.privileged,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.privileged'
                },
                name: 'privileged',
                bind: {
                    value: '{theUser.privileged}'
                }
            }]
        }, {
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                },
                name: 'active',
                bind: {
                    value: '{theUser.active}'
                }
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.emails.password,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.password'
        },
        items: [{
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },

            items: [{
                // TODO: Need validation after ws creation
                xtype: 'textfield',
                autoEl: {
                    'data-testid': 'administration-user-password'
                },
                listeners: {
                    afterrender: function (cmp) {
                        cmp.inputEl.set({
                            autocomplete: 'new-password'
                        });
                    }
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                },
                name: 'password',
                inputType: 'password',
                reference: 'password',
                vtype: 'passwordValidation',
                enforceMaxLength: true,
                allowBlank: false,
                maxLength: 40,
                triggers: {
                    showPassword: {
                        cls: 'x-fa fa-eye',
                        hideTrigger: false,
                        scope: this,
                        handler: function (field, button, e) {
                            field.lookupReferenceHolder().lookupReference('password').inputEl.el.set({
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
                            field.lookupReferenceHolder().lookupReference('password').inputEl.el.set({
                                type: 'password'
                            });
                            field.getTrigger('showPassword').setVisible(true);
                            field.getTrigger('hidePassword').setVisible(false);
                        }
                    }
                },
                bind: {
                    value: '{theUser.password}'
                }
            }, {
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.confirmpassword,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.confirmpassword'
                },
                name: 'confirmPassword',
                inputType: 'password',
                reference: 'confirmPasswordField',
                vtype: 'passwordMatch',
                enforceMaxLength: true,
                allowBlank: false,
                maxLength: 40,
                autoEl: {
                    'data-testid': 'administration-user-confirmpassword'
                },
                listeners: {
                    afterrender: function (cmp) {
                        cmp.inputEl.set({
                            autocomplete: 'new-password'
                        });
                    }
                },
                triggers: {
                    showPassword: {
                        cls: 'x-fa fa-eye',
                        hideTrigger: false,
                        scope: this,
                        handler: function (field, button, e) {
                            // set the element type to text
                            field.lookupReferenceHolder().lookupReference('confirmPasswordField').inputEl.el.set({
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
                            // set the element type to password
                            field.lookupReferenceHolder().lookupReference('confirmPasswordField').inputEl.el.set({
                                type: 'password'
                            });
                            field.getTrigger('showPassword').setVisible(true);
                            field.getTrigger('hidePassword').setVisible(false);
                        }
                    }
                },
                bind: {
                    value: '{theUser.confirmPassword}'
                }
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups'
        },
        items: [{
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('defaultUserGroup', {
                defaultUserGroup: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup'
                        },
                        allowBlank: true
                    },
                    hidden: false,
                    disableKeyFilter: true,
                    displayField: 'description',
                    valueField: '_id',
                    bind: {
                        store: '{activeUserRolesStore}',
                        value: '{theUser.defaultUserGroup}'
                    },
                    triggers: {
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: function (field, button, e) {
                                field.reset();
                            },
                            autoEl: {
                                'data-testid': 'administration-user-form-defaultgroup-clear-trigger'
                            }
                        }
                    }
                }
            }, false, true)]
        }, {
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            items: [{
                xtype: 'checkbox',
                labelAlign: 'top',
                flex: '0.5',

                layout: 'anchor',
                fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multigroup,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multigroup'
                },
                bind: {
                    value: '{theUser.multiGroup}'
                },
                listeners: {
                    change: function (check, newValue, oldValue, eOpts) {
                        var field = check.up('fieldset').down('[name="defaultUserGroup"]');
                        var form = check.up('form');
                        field.allowBlank = !newValue;
                        field.up('fieldcontainer').allowBlank = !newValue;
                        if (form && form.form) {
                            form.form.checkValidity();
                        }

                        if (!newValue) {
                            field.clearInvalid();
                            field.up('fieldcontainer').labelEl.dom.innerHTML = field.up('fieldcontainer').labelEl.dom.innerHTML.replace(' *', '');
                        } else {
                            field.up('fieldcontainer').labelEl.dom.innerHTML = field.up('fieldcontainer').labelEl.dom.innerHTML.replace('</span></span>', '</span> *</span>');
                        }

                    }
                }
            }]
        }, {
            xtype: 'grid',
            bind: {
                store: '{rolesStore}'
            },
            viewConfig: {
                markDirty: false
            },
            sortable: false,
            sealedColumns: false,
            sortableColumns: false,
            enableColumnHide: false,
            enableColumnMove: false,
            enableColumnResize: false,
            menuDisabled: true,
            stopSelect: true,
            columns: [{
                text: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group'
                },
                dataIndex: 'description',
                flex: 1,
                align: 'left'
            }, {
                text: CMDBuildUI.locales.Locales.administration.attributes.texts.active,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.active'
                },
                xtype: 'checkcolumn',
                dataIndex: 'active',
                align: 'center',
                listeners: {
                    checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                        var vm = check.up('form').getViewModel();
                        var currentGroups = vm.get('theUser.userGroups');

                        switch (checked) {
                            case true:
                                currentGroups.push({
                                    id: record.get('_id'),
                                    _id: record.get('_id'),
                                    name: record.get('name'),
                                    description: record.get('description')
                                });
                                break;
                            case false:
                                var index = currentGroups.map(function (group) {
                                    return group.get('_id');
                                }).indexOf(record.get('_id'));
                                currentGroups.splice(index, 1);
                                break;
                        }
                        vm.set('userGroups', currentGroups);

                    }
                }
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.users.fieldLabels.tenants,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.tenants'
        },
        bind: {
            hidden: '{!tenantModeIsClass}'
        },
        items: [
            /**
             * currently not supported by server
             */
            // {
            //     layout: 'column',
            //     defaults: {
            //         columnWidth: 0.5
            //     },
            //     items: [{
            //         xtype: 'combo',
            //         labelAlign: 'top',
            //         flex: '0.5',

            //         layout: 'anchor',

            //         fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaulttenant,      
            //         localized: {
            //             fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaulttenant'
            //         },
            //         displayField: 'description',
            //         valueField: '_id',
            //         bind: {
            //             store: '{getSelectedTenants}',
            //             value: '{theUser.defaultTenant}'
            //         }
            //     }]
            // }, 
            {
                layout: 'column',
                defaults: {
                    columnWidth: 0.5
                },
                items: [{
                    xtype: 'combo',
                    labelAlign: 'top',
                    flex: '0.5',

                    layout: 'anchor',
                    queryMode: 'local',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenantactivationprivileges,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenantactivationprivileges'
                    },
                    displayField: 'label',
                    valueField: 'value',
                    bind: {
                        store: '{multiTenantActivationPrivilegesStore}',
                        value: '{theUser.multiTenantActivationPrivileges}'
                    }
                }]
            },
            /**
             * currently not supported by server
             */
            // {
            //     layout: 'column',
            //     defaults: {
            //         columnWidth: 0.5
            //     },
            //     bind: {
            //         hidden: '{!tenantModeIsClass}'
            //     },
            //     items: [{
            //         xtype: 'checkbox',
            //         labelAlign: 'top',
            //         flex: '0.5',

            //         layout: 'anchor',
            //         fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenant,      
            //         localized: {
            //             fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenant'
            //         },
            //         bind: {
            //             value: '{theUser.multitenant}'
            //         }
            //     }]
            // },
            {
                /**
                 * currently is possible to edit tenants relation only in management
                 */
                marginTop: 10,
                xtype: 'grid',
                bind: {
                    store: '{tenantsStore}',
                    hidden: '{tenantModeIsDbFunction}'
                },
                sortable: false,
                sealedColumns: false,
                sortableColumns: false,
                enableColumnHide: false,
                enableColumnMove: false,
                enableColumnResize: false,
                menuDisabled: true,
                stopSelect: true,
                columns: [{
                    text: CMDBuildUI.locales.Locales.administration.users.fieldLabels.tenant,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.tenant'
                    },
                    dataIndex: 'description',
                    flex: 1,
                    align: 'left'
                }, {
                    text: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    xtype: 'checkcolumn',
                    dataIndex: 'active',
                    align: 'center',
                    listeners: {
                        checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                            var vm = check.up('form').getViewModel();
                            var currentTenants = vm.get('theUser.userTenants');
                            switch (checked) {
                                case true:
                                    currentTenants.push({
                                        _id: record.get('_id'),
                                        name: record.get('name'),
                                        description: record.get('description'),
                                        active: true
                                    });
                                    break;
                                case false:
                                    var index = currentTenants.map(function (tenant) {
                                        if (tenant.isModel) {
                                            return tenant.get('_id');
                                        }
                                        return tenant._id;
                                    }).indexOf(record.get('_id'));
                                    currentTenants.splice(index, 1);
                                    break;
                            }

                            vm.set('userTenants', currentTenants);

                        }
                    }
                }]
            }

        ]
    }],

    buttons: CMDBuildUI.util.administration.helper.FormHelper.getSaveAndAddCancelButtons(true)
});
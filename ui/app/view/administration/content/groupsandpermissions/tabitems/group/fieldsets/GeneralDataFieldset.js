Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.fieldsets.GeneralDataFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-groupsandpermissions-tabitems-group-fieldsets-generaldatafieldset',
    viewModel: {},

    ui: 'administration-formpagination',
    items: [{
        xtype: 'fieldset',
        layout: 'column',
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        items: [{
            columnWidth: 0.5,
            items: [{
                /********************* Name **********************/
                // create / edit
                xtype: 'textfield',
                tabIndex: 1,
                vtype: 'alphanum',
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.name,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.name'
                },
                name: 'name',
                enforceMaxLength: true,
                maxLength: 100,
                hidden: true,
                bind: {
                    value: '{theGroup.name}',
                    hidden: '{actions.view}',
                    disabled: '{actions.edit}'
                },
                listeners: {
                    change:  function (input, newVal, oldVal) {
                        CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(input, newVal, oldVal,'[name="description"]');
                    }
                }
            }, {
                // view
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.name,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.name'
                },
                name: 'name',
                hidden: true,
                bind: {
                    value: '{theGroup.name}',
                    hidden: '{!actions.view}'
                }
            }, {
                /********************* Type **********************/
                // create / edit
                xtype: 'combobox',
                tabIndex: 3,
                editable: false,
                reference: 'type',
                displayField: 'label',
                valueField: 'value',
                name: 'type',
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.type,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.type'
                },
                hidden: true,
                queryMode: 'local',
                anchor: '100%',
                forceSelection: true,
                bind: {
                    value: '{theGroup.type}',
                    hidden: '{actions.view}',
                    store: '{typesStore}'
                }
            }, {
                // view
                xtype: 'displayfield',
                name: 'type',
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.type,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.type'
                },
                hidden: true,
                bind: {
                    value: '{theGroup.type}',
                    hidden: '{!actions.view}'
                },
                renderer: function (value) {
                    switch (value) {
                        case 'default':
                            return CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.normal;
                        case 'admin_readolnly':
                            return CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.readonlyadmin;
                        case 'admin_limited':
                            return CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.limitedadmin;
                        case 'admin':
                            return CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.admin;
                        default:
                            return value;
                    }
                }
            }, {
                xtype: 'displayfield',
                tabIndex: 5,
                name: 'initialPage',
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.defaultpage,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.defaultpage'
                },
                bind: {
                    value: '{theGroup.startingClass}',
                    hidden: '{!actions.view}'
                }
            }, {
                xtype: 'combo',
                tabIndex: 7,
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.defaultpage,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.defaultpage'
                },
                name: 'initialPage',
                valueField: '_id',
                displayField: 'label',
                queryMode: 'local',
                typeAhead: true,
                bind: {
                    value: '{theGroup.startingClass}',
                    store: '{getAllPagesStore}',
                    hidden: '{actions.view}'
                },
                triggers: {
                    clearField: {
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
                            return group.replace(/\b\w/g, function (l) {
                                return l.toUpperCase();
                            });
                        }
                    }
                )
            }, {
                /********************* Active **********************/
                // create / edit / view
                xtype: 'checkbox',
                tabIndex: 9,
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.active,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.active'
                },
                name: 'active',
                hidden: true,
                bind: {
                    value: '{theGroup.active}',
                    readOnly: '{actions.view}',
                    hidden: '{!theGroup}'
                }
            }]
        }, {
            columnWidth: 0.5,
            style: {
                paddingLeft: '15px'
            },
            items: [{
                /********************* Description **********************/
                // create / edit
                xtype: 'textfield',
                tabIndex: 2,
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.description,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.description'
                },
                name: 'description',
                hidden: true,
                bind: {
                    value: '{theGroup.description}',
                    hidden: '{actions.view}'
                }
            }, {
                // view
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.description,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.description'
                },
                name: 'description',
                hidden: true,
                bind: {
                    value: '{theGroup.description}',
                    hidden: '{!actions.view}'
                }
            }, {
                /********************* Type **********************/
                // create / edit
                xtype: 'textfield',
                tabIndex: 4,
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email'
                },
                name: 'email',

                hidden: true,
                bind: {
                    value: '{theGroup.email}',
                    hidden: '{actions.view}'
                }
            }, {
                // view
                xtype: 'displayfield',
                name: 'email',
                fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email'
                },
                hidden: true,
                bind: {
                    value: '{theGroup.email}',
                    hidden: '{!actions.view}'
                }
            }]
        }]
    }]
});
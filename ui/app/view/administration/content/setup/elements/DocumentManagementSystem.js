Ext.define('CMDBuildUI.view.administration.content.setup.elements.DocumentManagementSystem', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.DocumentManagementSystemController',
        'CMDBuildUI.view.administration.content.setup.elements.DocumentManagementSystemModel'
    ],

    alias: 'widget.administration-content-setup-elements-documentmanagementsystem',
    controller: 'administration-content-setup-elements-documentmanagementsystem',
    viewModel: {
        type: 'administration-content-setup-elements-documentmanagementsystem'
    },

    items: [{
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'isEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__enabled}',
                        readOnly: '{actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.categorylookup,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.categorylookup'
                    },
                    name: 'attachmentTypeLookup',
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__category__DOT__lookup}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    /********************* Category Lookup **********************/
                    xtype: 'combobox',
                    queryMode: 'local',
                    forceSelection: true,
                    displayField: 'name',
                    valueField: '_id',
                    fieldLabel: 'CMDBuild category (default lookup)',
                    name: 'attachmentTypeLookup',
                    hidden: true,
                    bind: {
                        store: '{attachmentTypeLookupStore}',
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__category__DOT__lookup}',
                        hidden: '{actions.view}'
                    }
                }]
            }, {
                cmdbuildtype: 'column',
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    /********************* Description Mode **********************/
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.descriptionmode,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.descriptionmode'
                    },
                    name: 'attachmentDescriptionMode',
                    editable: false,
                    store: Ext.create('Ext.data.Store', {
                        fields: ['value', 'label'],
                        data: [{
                            "value": "hidden",
                            "label": CMDBuildUI.locales.Locales.administration.common.strings.hidden
                        }, {
                            "value": "optional",
                            "label": CMDBuildUI.locales.Locales.administration.common.strings.visibleoptional
                        }, {
                            "value": "mandatory",
                            "label": CMDBuildUI.locales.Locales.administration.common.strings.visiblemandatory
                        }]
                    }),
                    queryMode: 'local',
                    displayField: 'label',
                    valueField: 'value',
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__category__DOT__lookupDescriptionMode}',
                        hidden: '{actions.view}'
                    }
                }, {
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.descriptionmode,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.descriptionmode'
                    },
                    name: 'attachmentDescriptionMode',
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__category__DOT__lookupDescriptionMode}',
                        hidden: '{!actions.view}'
                    },
                    renderer: CMDBuildUI.util.administration.helper.RendererHelper.getAttachmentDescriptionMode

                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    /********************* Service Type **********************/
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.gis.servicetype,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.servicetype'
                    },
                    name: 'dmsServiceType',
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type}',
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value) {
                        switch (value) {
                            case 'cmis':
                                return CMDBuildUI.locales.Locales.administration.systemconfig.cmis;
                            case 'alfresco':
                                return ""; // not supported
                            case 'postgres':
                                return CMDBuildUI.locales.Locales.administration.systemconfig.postgres; // not supported
                            default:
                                return value;
                        }
                    }
                }, {
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.gis.servicetype,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.servicetype'
                    },
                    name: 'dmsServiceType',
                    itemId: 'dmsServiceType',

                    editable: false,
                    store: Ext.create('Ext.data.Store', {
                        fields: ['value', 'label'],
                        data: [
                            // {
                            //     "value": "alfresco",
                            //     "label": "Afresco (v. 3.4 or lower)"
                            // }, 
                            {
                                "value": "cmis",
                                "label": CMDBuildUI.locales.Locales.administration.systemconfig.cmis // TODO: translate
                            }, {
                                "value": "postgres",
                                "label": CMDBuildUI.locales.Locales.administration.systemconfig.postgres // sperimental
                            }
                        ]
                    }),
                    queryMode: 'local',
                    displayField: 'label',
                    valueField: 'value',
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type}',
                        hidden: '{actions.view}'
                    }
                }]
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: "CMIS",
        hidden: true,
        bind: {
            hidden: '{!isCmis}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    name: 'cmisHost',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.host,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.host'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__url}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'textfield',
                    name: 'cmisHost',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.host,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.host'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__url}',
                        hidden: '{actions.view}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'displayfield',
                    name: 'webServicePath',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.webservicepath,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.webservicepath'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__path}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'textfield',
                    name: 'webServicePath',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.webservicepath,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.webservicepath'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__path}',
                        hidden: '{actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    name: 'username',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__user}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'textfield',
                    name: 'username',
                    listeners: {
                        afterrender: function (cmp) {
                            cmp.inputEl.set({
                                autocomplete: 'new-username'
                            });
                        }
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.username,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.username'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__user}',
                        hidden: '{actions.view}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'displayfield',
                    name: 'password',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                    },
                    value: '****',
                    bind: {
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'textfield',
                    inputType: 'password',
                    name: 'password',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                    },
                    listeners: {
                        afterrender: function (cmp) {
                            cmp.inputEl.set({
                                autocomplete: 'off'
                            });
                        }
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__password}',
                        hidden: '{actions.view}'
                    },
                    triggers: {
                        showPassword: {
                            cls: 'x-fa fa-eye',
                            hideTrigger: false,
                            scope: this,
                            handler: function (field, button, e) {
                                // set the element type to text
                                field.inputEl.el.set({
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
                                field.inputEl.el.set({
                                    type: 'password'
                                });
                                field.getTrigger('showPassword').setVisible(true);
                                field.getTrigger('hidePassword').setVisible(false);
                            }
                        }
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    name: 'cmisPresets',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.preset,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.preset'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__model}',
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value) {
                        switch (value) {
                            case 'alfresco':
                                return CMDBuildUI.locales.Locales.administration.systemconfig.alfresco;
                            default:
                                return value;
                        }
                    }
                }, {
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.preset,
                    localized:{
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.preset'
                    },
                    name: 'cmisPresets',
                    editable: false,
                    store: Ext.create('Ext.data.Store', { // TODO: remove static data after #683 issue resolution
                        fields: ['value', 'label'],
                        data: [{
                            "value": "alfresco",
                            "label": CMDBuildUI.locales.Locales.administration.systemconfig.alfresco
                        }]
                    }),
                    queryMode: 'local',
                    displayField: 'label',
                    valueField: 'value',
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__cmis__DOT__model}',
                        hidden: '{actions.view}'
                    }
                }]
            }]
        }]
    }]
});
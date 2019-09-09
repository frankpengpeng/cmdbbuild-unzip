// locazation: ok

Ext.define('CMDBuildUI.view.administration.content.views.card.fieldscontainers.GeneralProperties', {
    extend: 'Ext.form.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.views.card.FieldsHelper'
    ],
    alias: 'widget.administration-content-views-card-fieldscontainers-generalproperties',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    viewModel: {},
    layout: 'column',
    items: [{
        columnWidth: 0.5,
        items: [CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput({
            name: {
                allowBlank: false,
                bind: {
                    value: '{theViewFilter.name}'
                }
            }
        }, true, '[name="description"]'),

        CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
            description: {
                allowBlank: false,
                bind: {
                    value: '{theViewFilter.description}'
                },
                fieldcontainer: {
                    userCls: 'with-tool',
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClick'
                }
            }
        }),
        {
            xtype: 'fieldcontainer',
            bind: {
                hidden: '{!isSqlType}'
            },
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getFunctionsInput({
                sourceFunction: {
                    fieldcontainer: {
                        allowBlank: false
                    },
                    allowBlank: false,
                    disabledCls: '',
                    displayField: 'description',
                    valueField: 'name',
                    bind: {
                        value: '{theViewFilter.sourceFunction}',
                        store: '{getFunctionsStore}'
                    }
                }
            }, 'sourceFunction')]
        },

        {
            xtype: 'fieldcontainer',
            bind: {
                hidden: '{isSqlType}'
            },
            items: [CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesInput({
                sourceClassName: {
                    fieldcontainer: {
                        fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.targetclass,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.targetclass'
                        }
                    },
                    bind: {
                        value: '{theViewFilter.sourceClassName}',
                        store: '{getAllPagesStore}'
                    }
                }
            }, 'sourceClassName')]
        },
        {
            // this field is hidden and used only for form validation!!
            xtype: 'fieldcontainer',
            hidden: true,
            allowBlank: false,
            items: [{
                xtype: 'textareafield',
                name: 'filter_input',
                bind: {
                    value: '{theViewFilter.filter}'
                }
            }]
        },
        {
            xtype: 'fieldcontainer',
            fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters,
            localized:{
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.searchfilters.fieldlabels.filters'
            },
            columnWidth: 1,
            bind: {
                hidden: '{isSqlType}'
            },
            items: [{
                xtype: 'components-administration-toolbars-formtoolbar',
                style: 'border:none; margin-top: 5px',
                items: [{
                    xtype: 'tbfill'
                }, {
                    xtype: 'tool',
                    align: 'right',
                    itemId: 'editFilterBtn',
                    cls: 'administration-tool margin-right5',
                    iconCls: 'cmdbuildicon-filter',
                    tooltip: CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters,
                    autoEl: {
                        'data-testid': 'administration-searchfilter-tool-removefilterbtn'
                    },
                    bind: {
                        disabled: '{!theViewFilter.sourceClassName}'
                    }
                }, {

                    xtype: 'tool',
                    align: 'right',
                    itemId: 'removeFilterBtn',
                    cls: 'administration-tool margin-right5',
                    iconCls: 'cmdbuildicon-filter-remove',
                    tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.deleteBtn.tooltip,
                    autoEl: {
                        'data-testid': 'administration-searchfilter-tool-removefilterbtn'
                    },
                    bind: {
                        disabled: '{!theViewFilter.filter}'
                    }
                }]
            }]
        },
        CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
            active: {
                disabledCls: '',
                bind: {
                    value: '{theViewFilter.active}'
                }
            }
        })

        ]
    }]
});
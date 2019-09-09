Ext.define('CMDBuildUI.view.administration.content.reports.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.reports.ViewController',
        'CMDBuildUI.view.administration.content.reports.ViewModel'
    ],
    alias: 'widget.administration-content-reports-view',
    controller: 'administration-content-reports-view',
    layout: 'border',
    viewModel: {
        type: 'administration-content-reports-view'
    },
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [ {
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        hidden: true,
        bind: {
            hidden: '{hideForm}'
        },
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
                items: [{
                    columnWidth: 0.5,
                    items: [{
                        /********************* theReport.code **********************/
                        xtype: 'displayfield',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.reports.fieldlabels.name, // Name
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.reports.fieldlabels.name'
                        },
                        hidden: true,
                        name: 'code',
                        bind: {
                            value: '{theReport.code}',
                            hidden: '{!actions.view}'
                        }
                    }, {
                        /********************* theReport.code **********************/
                        xtype: 'textfield',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.reports.fieldlabels.name, // Name
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.reports.fieldlabels.name'
                        },
                        vtype: 'nameInputValidation',
                        hidden: true,
                        allowBlank: false,
                        maxLength: 20,
                        name: 'code',
                        bind: {
                            value: '{theReport.code}',
                            hidden: '{actions.view}',
                            disabled: '{actions.edit}'
                        },
                        listeners: {
                            change: function (input, newVal, oldVal) {
                                CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(input, newVal, oldVal, '[name="description"]');
                            }
                        }
                    }]
                }]
            }, {
                layout: 'column',
                items: [{
                    columnWidth: 0.5,
                    items: [{
                        /********************* theReport.description **********************/
                        xtype: 'displayfield',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.reports.fieldlabels.description, // Description
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.reports.fieldlabels.description'
                        },
                        hidden: true,
                        name: 'description',
                        bind: {
                            value: '{theReport.description}',
                            hidden: '{!actions.view}'
                        }
                    }, {
                        /********************* theReport.description **********************/
                        xtype: 'textfield',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.reports.fieldlabels.description, // Description
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.reports.fieldlabels.description'
                        },
                        hidden: true,
                        allowBlank: false,
                        name: 'description',
                        bind: {
                            value: '{theReport.description}',
                            hidden: '{!actions.add}'
                        },
                        labelToolIconCls: 'fa-flag',
                        labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                        labelToolIconClick: 'onTranslateClick'
                    }, {
                        columnWidth: 0.5,
                        xtype: 'textfield',
                        name: 'description',
                        bind: {
                            value: '{theReport.description}',
                            hidden: '{!actions.edit}'
                        },
                        fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.reports.fieldlabels.description',
                            labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                        },
                        labelToolIconCls: 'fa-flag',
                        labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                        labelToolIconClick: 'onTranslateClick'
                    }]
                }]
            }, {
                layout: 'column',
                items: [{
                    columnWidth: 0.5,
                    items: [{
                        /********************* theReport.active **********************/
                        xtype: 'checkbox',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                        },
                        hidden: true,
                        name: 'enabled',
                        bind: {
                            value: '{theReport.active}',
                            readOnly: '{actions.view}',
                            hidden: '{!theReport}'
                        }
                    }]
                }]
            }]
        }, {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            bind: {
                hidden: '{actions.view}'
            },
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.reports.titles.file, // File
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.reports.titles.file'
            },
            items: [{
                layout: 'column',
                columnWidth: 0.5,
                items: [{
                    columnWidth: 0.5,
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.custompages.fieldlabels.zipfile, // Zip file
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.custompages.fieldlabels.zipfile'
                    },
                    allowBlank: false,
                    items: [{
                        flex: 1,
                        xtype: 'filefield',
                        name: 'fileReport',
                        msgTarget: 'side',
                        emptyText: CMDBuildUI.locales.Locales.administration.custompages.texts.selectfile,
                        localized: {
                            emptyText: 'CMDBuildUI.locales.Locales.administration.custompages.texts.selectfile'
                        },
                        accept: '.zip',
                        buttonConfig: {
                            ui: 'administration-secondary-action-small'
                        },
                        bind: {
                            hidden: '{actions.view}'
                        }
                    }]
                }]
            }]
        }]
    }],

    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        dock: 'top',
        padding: '6 0 6 8',
        borderBottom: 0,
        itemId: 'toolbarscontainer',
        style: 'border-bottom-width:0!important',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({},
            'report',
            'theReport',
            [{
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.administration.reports.texts.addreport, // Add report
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.reports.texts.addreport'
                },
                ui: 'administration-action-small',
                itemId: 'addBtn',
                iconCls: 'x-fa fa-plus',
                autoEl: {
                    'data-testid': 'administration-reports-addBtn'
                }
            }, {
                xtype: 'textfield',
                name: 'search',
                width: 250,
                emptyText: CMDBuildUI.locales.Locales.administration.reports.emptytexts.searchreports, // Search reports...
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.administration.reports.emptytexts.searchreports'
                },
                cls: 'administration-input',
                reference: 'searchtext',
                itemId: 'searchtext',
                bind: {
                    value: '{search.value}',
                    hidden: '{!canFilter}'
                },
                listeners: {
                    specialkey: 'onSearchSpecialKey'
                },
                triggers: {
                    search: {
                        cls: Ext.baseCSSPrefix + 'form-search-trigger',
                        handler: 'onSearchSubmit',
                        autoEl: {
                            'data-testid': 'administration-lookuptypes-toolbar-form-search-trigger'
                        }
                    },
                    clear: {
                        cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                        handler: 'onSearchClear',
                        autoEl: {
                            'data-testid': 'administration-lookuptypes-toolbar-form-clear-trigger'
                        }
                    }
                },
                autoEl: {
                    'data-testid': 'administration-lookuptypes-toolbar-search-form'
                }
            }, {
                xtype: 'tbfill'
            }],
            null,
            [{
                xtype: 'tbtext',
                hidden: true,
                bind: {
                    hidden: '{!theReport.description}',
                    html: '{componentTypeName}: <b data-testid="administration-report-description">{theReport.description}</b>'
                }
            }])
    }, {
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'top',
        borderBottom: 0,
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: true, // #editBtn set true for show the button
            sql: true,
            'delete': true, // #deleteBtn set true for show the button
            activeToggle: true, // #enableBtn and #disableBtn set true for show the buttons
            download: true // #downloadBtn set true for show the buttons
        },

            /* testId */
            'report',

            /* viewModel object needed only for activeTogle */
            'theReport',

            /* add custom tools[] on the left of the bar */
            [],

            /* add custom tools[] before #editBtn*/
            [],

            /* add custom tools[] after at the end of the bar*/
            []
        ),
        bind: {
            hidden: '{formtoolbarHidden}'
        }
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
    }]


});
Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.domains.Domains', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.classes.tabitems.domains.DomainsController',
        'CMDBuildUI.view.administration.content.classes.tabitems.domains.DomainsModel'
    ],

    alias: 'widget.administration-content-classes-tabitems-domains-domains',
    controller: 'administration-content-classes-tabitems-domains-domains',
    viewModel: {
        type: 'administration-content-classes-tabitems-domains-domains'
    },
    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true,
        selected: null
    },

    forceFit: true,
    loadMask: true,
    viewConfig: {
        plugins: [{
            ptype: 'gridviewdragdrop',
            dragText: CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop,
            // TODO: localized not work as expected
            localized: {
                dragText: 'CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop'
            },
            containerScroll: true,
            pluginId: 'gridviewdragdrop'
        }]
    },
    plugins: [{
        ptype: 'administration-forminrowwidget',
        pluginId: 'administration-forminrowwidget',
        expandOnDblClick: true,
        selectRowOnExpand: true,
        widget: {
            xtype: 'administration-content-domains-tabitems-properties-properties',
            ui: 'administration-tabandtools',
            layout: 'fit',
            paddingBottom: 10,
            scrollable: false, 
            viewModel: {
                type: 'administration-content-domains-view'
            },
            bind: {
                theDomain: '{record}'
            }
        }
    }],

    autoEl: {
        'data-testid': 'administration-content-classes-tabitems-domains-grid'
    },

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    labelWidth: "auto",
    tbar: [{
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.domains.texts.adddomain,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.texts.adddomain'
            },
            ui: 'administration-action-small',
            reference: 'adddomain',
            itemId: 'adddomain',
            iconCls: 'x-fa fa-plus',
            autoEl: {
                'data-testid': 'administration-class-toolbar-addDomainBtn'
            }
        }, {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.domains.texts.addlink,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.domains.texts.addlink'
            },
            ui: 'administration-action-small',
            reference: 'addlink',
            itemId: 'addlink',
            iconCls: 'x-fa fa-link',
            viewModel: {},
            // TODO: comment hidden for activate linkbutton
            hidden: true,
            bind: {

            },
            autoEl: {
                'data-testid': 'administration-class-toolbar-addLinkBtn'
            }
        }, {
            xtype: 'textfield',
            name: 'search',
            width: 250,
            viewModel: {},
            emptyText: CMDBuildUI.locales.Locales.administration.domains.texts.emptyText,
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.administration.domains.texts.emptyText'
            },
            cls: 'administration-input',
            reference: 'searchdomaintext',
            itemId: 'searchdomaintext',
            bind: {
                value: '{searchdomain.value}',
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
                        'data-testid': 'administration-class-toolbar-form-search-trigger'
                    }
                },
                clear: {
                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                    handler: 'onSearchClear',
                    autoEl: {
                        'data-testid': 'administration-class-toolbar-form-clear-trigger'
                    }
                }
            },
            autoEl: {
                'data-testid': 'administration-class-toolbar-search-form'
            }
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'tool',
            align: 'right',
            itemId: 'editBtn',
            cls: 'administration-tool',
            iconCls: 'x-fa fa-pencil',
            viewModel: {},
            tooltip: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.editBtn.tooltip,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.editBtn.tooltip'
            },
            autoEl: {
                'data-testid': 'administration-class-domains-tool-editbtn'
            },
            // TODO: comment hidden for activate editbutton form link column in grid
            hidden: true,
            bind: {
                hidden: '{actions.edit}'
            }
        }
    ],


    columns: [{
        text: CMDBuildUI.locales.Locales.administration.common.labels.name,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
        },
        dataIndex: 'name',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.common.labels.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
        },
        dataIndex: 'description',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin'
        },
        dataIndex: 'source',
        align: 'left',
        renderer: function (value, cell, record) {
            if (value) {
                var storeId = record.get('sourceProcess') ? 'processes.Processes' : 'classes.Classes';
                var sourceRecord = Ext.getStore(storeId).getById(record.get('source'));
                return sourceRecord && sourceRecord.get('description');
            }
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
        },
        dataIndex: 'destination',
        align: 'left',
        renderer: function (value, cell, record) {
            if (value) {
                var storeId = record.get('destinationProcess') ? 'processes.Processes' : 'classes.Classes';
                var sourceRecord = Ext.getStore(storeId).getById(record.get('destination'));
                return sourceRecord && sourceRecord.get('description');
            }
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription'
        },
        dataIndex: 'descriptionDirect',
        align: 'left'
    }, {
        text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription',
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription'
        },
        dataIndex: 'descriptionInverse',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality'
        },
        dataIndex: 'cardinality',
        align: 'left'
    }, {
        xtype: 'checkcolumn',
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetailshort,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetailshort'
        },
        hidden: true,
        disabled: true,
        disabledCls: '',
        dataIndex: 'isMasterDetail',
        align: 'center'
    }, {
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdetail,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdetail'
        },
        hidden: true,
        dataIndex: 'descriptionMasterDetail',
        align: 'left'
    }, {
        xtype: 'checkcolumn',
        text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inline,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inline'
        },
        hidden: true,
        disabled: true,
        disabledCls: '',
        dataIndex: 'inline',
        align: 'center'
    },
    //  {
    //     xtype: 'checkcolumn',
    //     text: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.link,
    //     localized: {
    //         text: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.link'
    //     },
    //     dataIndex: 'link',
    //     align: 'center',
    //     hidden: true,
    //     bind: {
    //         disabled: '{actions.view}'
    //     }
    // },
     {
        xtype: 'checkcolumn',
        text: CMDBuildUI.locales.Locales.administration.common.labels.active,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
        },
        dataIndex: 'active',
        align: 'center',
        readOnly: true
       
    }],


    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        // TODO: comment hidden for activate form buttons form link column in grid
        hidden: true,
        bind: {
            // hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
    }]
});
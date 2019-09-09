Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldset', {
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldsetController',
        'CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldsetModel'
    ],

    alias: 'widget.administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset',

    controller: 'administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset',
    viewModel: {
        type: 'administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset'
    },
    ui: 'administration-formpagination',

    items: [{
        xtype: 'fieldset',
        title: CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.generalattributes,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.generalattributes'
        },
        layout: 'anchor',
        itemId: 'domain-generaldatafieldset',
        ui: 'administration-formpagination',
        items: [{
            layout: 'column',

            items: [{
                columnWidth: 0.5,
                /********************* Name **********************/
                items: [{
                    // create / edit
                    xtype: 'textfield',
                    vtype: 'alphanum',
                    reference: 'domainname',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                    },
                    name: 'name',
                    enforceMaxLength: true,
                    hidden: true,
                    allowBlank: false,
                    bind: {
                        value: '{theDomain.name}',
                        hidden: '{!actions.add}'
                    },
                    listeners: {
                        change: function (input, newVal, oldVal) {
                            CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(input, newVal, oldVal, '[name="description"]');
                        }
                    }
                }, {
                    // view
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                    },
                    name: 'name',
                    hidden: true,
                    bind: {
                        value: '{theDomain.name}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    // edit
                    xtype: 'textfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                    },
                    name: 'name',
                    hidden: true,
                    disabled: true,
                    allowBlank: false,
                    bind: {
                        value: '{theDomain.name}',
                        hidden: '{!actions.edit}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                /********************* description **********************/
                items: [{
                    // edit
                    columnWidth: 0.5,
                    xtype: 'textfield',
                    name: 'description',
                    bind: {
                        value: '{theDomain.description}',
                        hidden: '{!actions.edit}'
                    },
                    allowBlank: false,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description',
                        labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                    },
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClickDescription'
                }, {
                    // view
                    xtype: 'displayfield',
                    name: 'description',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                    },
                    hidden: true,
                    bind: {
                        value: '{theDomain.description}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    // add
                    xtype: 'textfield',
                    name: 'description',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                    },
                    hidden: true,
                    allowBlank: false,
                    bind: {
                        value: '{theDomain.description}',
                        hidden: '{!actions.add}'
                    },
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClickDescription'
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                /********************* Source **********************/
                items: [{
                    // create
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin'
                    },
                    valueField: '_id',
                    displayField: 'label',
                    queryMode: 'local',
                    forceSelection: true,
                    typeAhead: true,
                    allowBlank: false,
                    name: 'source',
                    hidden: true,
                    bind: {
                        value: '{theDomain.source}',
                        hidden: '{actions.view}',
                        disabled: '{actions.edit}',
                        store: '{sourceClassStore}'
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
                }, {
                    // view
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.origin'
                    },
                    hidden: true,
                    bind: {
                        value: '{theDomain.source}',
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value, input) {
                        if (value) {
                            var vm = input.lookupViewModel();
                            var storeId = vm.get('theDomain.sourceProcess') ? 'processes.Processes' : 'classes.Classes';
                            var record = Ext.getStore(storeId).getById(vm.get('theDomain.source'));
                            return record && record.get('description');
                        }

                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                /********************* destination **********************/
                items: [{
                    xtype: 'combobox',
                    valueField: '_id',
                    displayField: 'label',
                    queryMode: 'local',
                    forceSelection: true,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
                    },
                    typeAhead: true,
                    allowBlank: false,
                    name: 'destination',
                    hidden: true,
                    bind: {
                        value: '{theDomain.destination}',
                        hidden: '{actions.view}',
                        disabled: '{actions.edit}',
                        store: '{destinationClassStore}'
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
                }, {
                    // view
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
                    },
                    name: 'destination',
                    hidden: true,
                    bind: {
                        value: '{theDomain.destination}',
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value, input) {
                        if (value) {
                            var vm = input.lookupViewModel();
                            var storeId = vm.get('theDomain.destinationProcess') ? 'processes.Processes' : 'classes.Classes';
                            var record = Ext.getStore(storeId).getById(vm.get('theDomain.destination'));
                            return record && record.get('description');
                        }

                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                /********************* direct description **********************/
                items: [{
                    // create
                    xtype: 'textfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription'
                    },
                    name: 'descriptionDirect',
                    allowBlank: false,
                    hidden: true,
                    bind: {
                        value: '{theDomain.descriptionDirect}',
                        hidden: '{!actions.add}'
                    },
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClickDirect'
                }, {
                    // edit
                    columnWidth: 0.5,
                    xtype: 'textfield',
                    name: 'description',
                    allowBlank: false,
                    bind: {
                        value: '{theDomain.descriptionDirect}',
                        hidden: '{!actions.edit}'
                    },
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription',
                        labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                    },
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClickDirect'
                }, {
                    // view
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.directdescription'
                    },
                    name: 'descriptionDirect',
                    hidden: true,
                    bind: {
                        value: '{theDomain.descriptionDirect}',
                        hidden: '{!actions.view}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                /********************* inverse description **********************/
                items: [{
                    // create 
                    xtype: 'textfield',
                    name: 'descriptionInverse',
                    allowBlank: false,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription'
                    },
                    hidden: true,
                    bind: {
                        value: '{theDomain.descriptionInverse}',
                        hidden: '{!actions.add}'
                    },
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClickInverse'
                }, {
                    // edit
                    columnWidth: 0.5,
                    xtype: 'textfield',
                    name: 'descriptionInverse',
                    bind: {
                        value: '{theDomain.descriptionInverse}',
                        hidden: '{!actions.edit}'
                    },
                    allowBlank: false,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription',
                        labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                    },
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClickInverse'
                }, {
                    // view
                    xtype: 'displayfield',
                    name: 'descriptionInverse',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inversedescription'
                    },
                    hidden: true,
                    bind: {
                        value: '{theDomain.descriptionInverse}',
                        hidden: '{!actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                /********************* cardinality **********************/
                items: [{
                    // create
                    xtype: 'combobox',
                    queryMode: 'local',
                    forceSelection: true,
                    displayField: 'label',
                    valueField: 'value',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality'
                    },
                    name: 'cardinality',
                    allowBlank: false,
                    hidden: true,
                    bind: {
                        store: '{cardinalityStore}',
                        value: '{theDomain.cardinality}',
                        hidden: '{actions.view}',
                        disabled: '{actions.edit}'
                    }
                }, {
                    // view
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality'
                    },
                    name: 'cardinality',
                    hidden: true,
                    bind: {
                        value: '{theDomain.cardinality}',
                        hidden: '{!actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                /********************* Master detail **********************/
                items: [{
                    // create / edit / view
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetail,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.masterdetail'
                    },
                    name: 'masterDetail',
                    hidden: true,
                    bind: {
                        value: '{theDomain.isMasterDetail}',
                        readOnly: '{actions.view}',
                        hidden: '{!theDomain}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                /********************* Master detail label **********************/
                items: [{
                    // add / edit
                    xtype: 'textfield',
                    name: 'descriptionMasterDetail',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdataillong,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdataillong'
                    },
                    hidden: true,
                    bind: {
                        value: '{theDomain.descriptionMasterDetail}',
                        hidden: '{descriptionMasterDetailInput.hidden}'
                    },
                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClickMasterDetail'
                }, {
                    // view
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdataillong,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.labelmasterdataillong'
                    },
                    name: 'descriptionMasterDetail',
                    hidden: true,
                    bind: {
                        value: '{theDomain.descriptionMasterDetail}',
                        hidden: '{descriptionMasterDetailDisplay.hidden}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                /********************* Inline **********************/
                items: [{
                    // create / edit / view
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inline,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.inline'
                    },
                    name: 'inline',
                    hidden: true,
                    bind: {
                        value: '{theDomain.inline}',
                        readOnly: '{actions.view}',
                        hidden: '{!theDomain}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                /********************* Default closed **********************/
                items: [{
                    // create / edit / view
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.defaultclosed,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.defaultclosed'
                    },
                    name: 'defaultClosed',
                    hidden: true,
                    bind: {
                        value: '{theDomain.defaultClosed}',
                        readOnly: '{actions.view}',
                        hidden: '{!theDomain}',
                        disabled: '{!theDomain.inline}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                /********************* View condition CQL **********************/
                items: [{
                    // create / edit
                    xtype: 'textfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.viewconditioncql,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.viewconditioncql'
                    },
                    name: 'viewConditionCQL',
                    hidden: true,
                    bind: {
                        value: '{theDomain.viewConditionCQL}',
                        hidden: '{actions.view}'
                    }
                }, {
                    // view
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.viewconditioncql,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.viewconditioncql'
                    },
                    name: 'viewConditionCQL',
                    hidden: true,
                    bind: {
                        value: '{theDomain.viewConditionCQL}',
                        hidden: '{!actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                /********************* Active **********************/
                items: [{
                    // create / edit / view
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'active',
                    hidden: true,
                    bind: {
                        value: '{theDomain.active}',
                        readOnly: '{actions.view}',
                        hidden: '{!theDomain}'
                    }
                }]
            }]
        }]
    }]
});
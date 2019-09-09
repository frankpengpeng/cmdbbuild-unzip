Ext.define('CMDBuildUI.view.administration.content.setup.elements.GeneralOptions', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.GeneralOptionsController',
        'CMDBuildUI.view.administration.content.setup.elements.GeneralOptionsModel'
    ],

    alias: 'widget.administration-content-setup-elements-generaloptions',
    controller: 'administration-content-setup-elements-generaloptions',
    viewModel: {
        type: 'administration-content-setup-elements-generaloptions'
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
            layout: 'hbox',
            items: [{
                flex: 1,
                marginRight: 20,
                items: [{
                    xtype: 'displayfield',
                    name: 'instanceName',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.instancename,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.instancename'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__instance_name}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    flex: 5,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.instancename,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.instancename',
                        labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                    },
                    xtype: 'textfield',
                    name: 'instanceName',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__instance_name}',
                        hidden: '{actions.view}'
                    },

                    labelToolIconCls: 'fa-flag',
                    labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                    labelToolIconClick: 'onTranslateClick'
                }]
            }, {
                //columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                flex: 1,
                items: [{
                    xtype: 'displayfield',
                    name: 'initialPage',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.defaultpage,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.defaultpage'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__startingclass}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'combo',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.defaultpage,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.defaultpage'
                    },
                    name: 'initialPage',
                    valueField: '_id',
                    displayField: 'label',
                    queryMode: 'local',
                    typeAhead: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__startingclass}',
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
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    name: 'relationLimit',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.relationlimit,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.relationlimit'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__relationlimit}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'numberfield',
                    name: 'relationLimit',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.relationlimit,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.relationlimit'
                    },
                    minValue: 0, //prevents negative numbers
                    step: 10,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__relationlimit}',
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
                    name: 'referenceComboLimit',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.referencecombolimit,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.referencecombolimit'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__referencecombolimit}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'numberfield',
                    name: 'referenceComboLimit',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.referencecombolimit,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.referencecombolimit'
                    },
                    minValue: 0, //prevents negative numbers
                    step: 100,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__referencecombolimit}',
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
                    name: 'sessionTimeout',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.sessiontimeout,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.sessiontimeout'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__session__DOT__timeout}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'numberfield',
                    name: 'sessionTimeout',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.sessiontimeout,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.sessiontimeout'
                    },
                    minValue: 0, //prevents negative numbers
                    step: 60,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__session__DOT__timeout}',
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
                    name: 'ajaxTimeout',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.ajaxtimeout,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.ajaxtimeout'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__timeout}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'numberfield',
                    name: 'ajaxTimeout',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.ajaxtimeout,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.ajaxtimeout'
                    },
                    minValue: 0, //prevents negative numbers
                    step: 60,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__timeout}',
                        hidden: '{actions.view}'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.noteinline,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.noteinline'
                    },
                    name: 'noteInline',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__noteInline}',
                        readOnly: '{actions.view}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.noteinlinedefaultclosed,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.noteinlinedefaultclosed'
                    },
                    name: 'noteInlineClosed',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__noteInlineClosed}',
                        readOnly: '{actions.view}'
                    }
                }]
            }]
        }, {
            xtype: 'container',
            layout: 'column',
            items: [{
                // Date format
                xtype: 'fieldcontainer',
                columnWidth: 1,
                layout: 'column',
                items: [{
                    xtype: 'fieldcontainer',
                    columnWidth: 0.5,
                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldateformat,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeldateformat'
                    },
                    items: [{
                        xtype: 'displayfield',
                        bind: {
                            value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__dateFormat}',
                            hidden: '{!actions.view}'
                        },
                        renderer: function (value) {
                            var store = this.up('fieldcontainer').down('combo').getStore();
                            var func = store.findRecord('value', value);
                            if (func) {
                                return func.get('label');
                            }
                            return value;
                        }
                    }, {
                        xtype: 'combobox',
                        displayField: 'label',
                        valueField: 'value',
                        forceSelection: true,
                        editable: false,
                        autoSelect: false,
                        triggers: {
                            clear: {
                                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                handler: function () {
                                    this.clearValue();
                                }
                            }
                        },
                        bind: {
                            store: '{dateFormats}',
                            value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__dateFormat}',
                            hidden: '{actions.view}'
                        }
                    }]
                },
                {
                    xtype: 'fieldcontainer',
                    columnWidth: 0.5,
                    flex: '0.5',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    layout: 'anchor',
                    items: [{
                        xtype: 'fieldcontainer',
                        fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeltimeformat,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeltimeformat'
                        },
                        items: [{
                            xtype: 'displayfield',
                            bind: {
                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__timeFormat}',
                                hidden: '{!actions.view}'
                            },
                            renderer: function (value) {
                                var store = this.up('fieldcontainer').down('combo').getStore();
                                var func = store.findRecord('value', value);
                                if (func) {
                                    return func.get('label');
                                }
                                return value;
                            }
                        }, {
                            // Time format
                            xtype: 'combobox',

                            // padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                            displayField: 'label',
                            valueField: 'value',
                            forceSelection: true,
                            editable: false,
                            autoSelect: false,
                            triggers: {
                                clear: {
                                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                    handler: function () {
                                        this.clearValue();
                                    }
                                }
                            },
                            bind: {
                                store: '{timeFormats}',
                                value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__timeFormat}',
                                hidden: '{actions.view}'
                            }

                        }]
                    }]
                }]
            }]
        }, {
            xtype: 'container',
            layout: 'column',
            items: [{
                // Date format
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                flex: '0.5',
                layout: 'anchor',
                items: [{
                    xtype: 'fieldcontainer',
                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labeldecimalsseparator'
                    },
                    items: [{
                        xtype: 'displayfield',
                        bind: {
                            value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__decimalsSeparator}',
                            hidden: '{!actions.view}'
                        },
                        renderer: function (value) {
                            var store = this.up('fieldcontainer').down('combo').getStore();
                            var func = store.findRecord('value', value);
                            if (func) {
                                return func.get('label');
                            }
                            return value;
                        }
                    }, {
                        // Time format
                        xtype: 'combobox',
                        displayField: 'label',
                        valueField: 'value',
                        forceSelection: true,
                        editable: false,
                        autoSelect: false,
                        triggers: {
                            clear: {
                                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                handler: function () {
                                    this.clearValue();
                                }
                            }
                        },
                        bind: {
                            store: '{decimalsSeparators}',
                            validation: '{validations.decimalsSeparator}',
                            value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__decimalsSeparator}',
                            hidden: '{actions.view}'
                        },
                        validator: function () {
                            this.lookupViewModel().set("theSetup.org__DOT__cmdbuild__DOT__ui__DOT__decimalsSeparator", this.getValue());
                            return true;
                        }
                    }]
                }]
            },
            {
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                flex: '0.5',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                layout: 'anchor',
                items: [{
                    xtype: 'fieldcontainer',
                    fieldLabel: CMDBuildUI.locales.Locales.main.preferences.labelthousandsseparator,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.labelthousandsseparator'
                    },
                    items: [{
                        xtype: 'displayfield',
                        bind: {
                            value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__thousandsSeparator}',
                            hidden: '{!actions.view}'
                        },
                        renderer: function (value) {
                            var store = this.up('fieldcontainer').down('combo').getStore();                            
                            var func = store.findRecord('value', value);
                            if (func) {
                                return func.get('label');
                            }
                            return value;
                        }
                    }, {
                        xtype: 'combobox',

                        displayField: 'label',
                        valueField: 'value',
                        forceSelection: true,
                        editable: false,
                        autoSelect: false,
                        triggers: {
                            clear: {
                                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                handler: function () {
                                    this.clearValue();
                                }
                            }
                        },
                        bind: {
                            store: '{thousandsSeparators}',
                            value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__thousandsSeparator}',
                            hidden: '{actions.view}',
                            validation: '{validations.thousandsSeparator}'
                        },

                        validator: function () {
                            this.lookupViewModel().set("theSetup.org__DOT__cmdbuild__DOT__ui__DOT__thousandsSeparator", this.getValue());
                            return true;
                        }
                    }]
                }]
            }
            ]
        },
        {
            xtype: 'container',
            layout: 'column',
           
            items: [{
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                flex: '0.5',
                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.preferredofficesuite,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.preferredofficesuite'
                },
                items: [{
                    xtype: 'displayfield',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__preferences__DOT__preferredOfficeSuite}',
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value) {
                        if (value) {
                            var store = this.lookupViewModel().get('getPreferredOfficeSuitesStore');
                            return store.findRecord('value', value).get('label');
                        }
                        return value;
                    }
                }, {
                    xtype: 'combo',
                    name: 'preferredOfficeSuite',
                    valueField: 'value',
                    displayField: 'label',
                    queryMode: 'local',
                    typeAhead: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__preferences__DOT__preferredOfficeSuite}',
                        store: '{getPreferredOfficeSuitesStore}',
                        hidden: '{actions.view}'
                    }
                }]
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.lockmanagement,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.lockmanagement'
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
                    name: 'lockcardenabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__lockcardenabled}',
                        readOnly: '{actions.view}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.showcardlockerusername,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.showcardlockerusername'
                    },
                    name: 'lockcarduservisible',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__lockcarduservisible}',
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
                    name: 'lockcardtimeout',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxlocktime,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxlocktime'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__lockcardtimeout}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'numberfield',
                    name: 'lockcardtimeout',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxlocktime,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxlocktime'
                    },
                    minValue: 0, //prevents negative numbers
                    step: 10,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__lockcardtimeout}',
                        hidden: '{actions.view}'
                    }
                }]
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.gridautorefresh,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.gridautorefresh'
        },
        hidden: true, // TODO: Temporarily disabled (Fabio 5/11/18) 
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
                    name: 'lockcardenabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__gridAutorefresh}',
                        readOnly: '{actions.view}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'displayfield',
                    name: 'lockcardtimeout',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.frequency,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.frequency'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__gridAutorefreshFrequency}',
                        hidden: '{!actions.view}'
                    }
                }, {
                    xtype: 'numberfield',
                    name: 'lockcardtimeout',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.frequency,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.frequency'
                    },
                    minValue: 0, //prevents negative numbers
                    step: 10,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__gridAutorefreshFrequency}',
                        hidden: '{actions.view}'
                    }
                }]
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.companylogo,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.companylogo'
        },
        hidden: false, // TODO: Temporarily disabled (Fabio 5/11/18) 
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.logo,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.logo'
                },
                layout: 'column',
                items: [{
                    columnWidth: 1,
                    xtype: 'filefield',
                    itemId: 'iconFile',
                    emptyText: CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile,
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile'
                    },
                    accept: '.png',
                    buttonConfig: {
                        ui: 'administration-secondary-action-small'
                    },
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}'
                    }
                }, {
                    xtype: 'image',
                    height: 30,
                    alt: CMDBuildUI.locales.Locales.administration.systemconfig.logo,
                    localized: {
                        alt: 'CMDBuildUI.locales.Locales.administration.systemconfig.logo'
                    },
                    itemId: 'classIconPreview',
                    bind: {
                        src: '{logo}'
                    }
                }]
            }]
        }]
    }]
});
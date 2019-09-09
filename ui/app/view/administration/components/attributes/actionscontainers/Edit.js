Ext.define('CMDBuildUI.view.administration.components.attributes.actionscontainers.Edit', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-components-attributes-actionscontainers-edit',
    requires: [
        'CMDBuildUI.view.administration.components.attributes.actionscontainers.CardController',
        'CMDBuildUI.view.administration.components.attributes.actionscontainers.CardModel'
    ],
    controller: 'administration-components-attributes-actionscontainers-card',
    viewModel: {
        type: 'administration-components-attributes-actionscontainers-card'
    },
    bubbleEvents: [
        'itemupdated',
        'cancelupdating'
    ],
    modelValidation: true,
    config: {
        objectTypeName: null,
        objectType: null,
        objectId: null,
        shownInPopup: false,
        theAttribute: null
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,
    ui: 'administration-formpagination',
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
                xtype: 'textfield',
                disabled: true,
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.name,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.name'
                },
                name: 'name',
                bind: {
                    value: '{theAttribute.name}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textfield',
                name: 'description',
                bind: {
                    value: '{theAttribute.description}'
                },
                allowBlank: false,
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.description,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.description',
                    labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                },
                labelToolIconCls: 'fa-flag',
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                labelToolIconClick: 'onTranslateClick'
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'combo',
                itemId: 'groupfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group'
                },
                name: 'group',
                typeAhead: true,
                forceSelection: true,
                queryMode: 'local',
                allowBlank: true,
                displayField: 'description',
                valueField: 'name',
                hidden: true,
                bind: {
                    value: '{theAttribute.group}',
                    store: '{attributeGroupStore}',
                    hidden: '{isGroupHidden}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mode,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mode'
                },
                name: 'mode',
                clearFilterOnBlur: false,
                anyMatch: true,
                autoSelect: true,
                forceSelection: true,
                allowBlank: false,
                typeAhead: true,
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                msgTarget: 'qtip',
                bind: {
                    value: '{theAttribute.mode}',
                    store: '{attributeModeStore}'

                },
                listeners: {
                    change: function () {
                        var me = this;
                        var fields = this.up('form').getForm().getFields().items;
                        fields.forEach(function (field, index) {                            
                            field.clearInvalid();
                            field.validate();                            
                        });
                    }
                },
                renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                    switch (value) {
                        case 'write':
                            return CMDBuildUI.locales.Locales.administration.attributes.strings.editable;
                        case 'read':
                            return CMDBuildUI.locales.Locales.administration.attributes.strings.readonly;
                        case 'hidden':
                            return CMDBuildUI.locales.Locales.administration.attributes.strings.hidden;
                        case 'immutable':
                            return CMDBuildUI.locales.Locales.administration.attributes.strings.immutable;
                    }
                },
                /**
                 * Returns whether or not the widget value is currently valid by {@link #getErrors validating} the
                 * {@link #processRawValue processed raw value} of the widget. **Note**: {@link #disabled} buttons are
                 * always treated as valid.
                 *
                 * @return {Boolean} True if the value is valid, else false
                 */
                isValid: function () {
                    return this.validateValue(this.getValue());
                },

                /**
                 * Uses {@link #getErrors} to build an array of validation errors. If any errors are found, they are passed to
                 * {@link #markInvalid} and false is returned, otherwise true is returned.
                 * 
                 * @param {Object} value The value to validate
                 * @return {Boolean} True if all validations passed, false if one or more failed
                 */
                validateValue: function (value) {
                    var errors = this.getErrors(value),
                        isValid = Ext.isEmpty(errors);
                    if (isValid) {
                        this.clearInvalid();
                    } else {
                        this.markInvalid(errors);
                    }
                    return isValid;
                },

                /**
                 * @param {Object} value The value to validate. The processed raw value will be used if nothing is passed.
                 * @return {String[]} Array of any validation errors
                 */
                getErrors: function (value) {
                    var messages = [];

                    // we have to validate only if value is 'hidden' 
                    var form = this.up('form');
                    if (value === 'hidden') {
                        // get theAttribute from viewModel
                        var theAttribute = form.getViewModel().get('theAttribute');
                        // if field showInGrid === true return error
                        if (theAttribute.get('showInGrid')) {
                            messages.push(CMDBuildUI.locales.Locales.administration.attributes.strings.thefieldshowingridcantbechecked);
                        }
                        // if field showInReducedGrid === true return error
                        if (theAttribute.get('showInReducedGrid')) {
                            messages.push(CMDBuildUI.locales.Locales.administration.attributes.strings.thefieldshowinreducedgridcantbechecked);
                        }
                        // if field mandatory === true return error
                        // if (theAttribute.get('mandatory')) {
                        //     messages.push(CMDBuildUI.locales.Locales.administration.attributes.strings.thefieldmandatorycantbechecked);
                        // }
                    }
                    return messages;
                },

                /**
                 * Returns whether or not the widget value is currently valid by {@link #getErrors validating} the field's current
                 * value, and fires the {@link #validitychange} event if the field's validity has changed since the last validation.
                 * **Note**: {@link #disabled} fields are always treated as valid.
                 *
                 * Custom implementations of this method are allowed to have side-effects such as triggering error message display.
                 * To validate without side-effects, use {@link #isValid}.
                 *
                 * @return {Boolean} True if the value is valid, else false
                 */
                validate: function () {
                    return this.checkValidityChange(this.isValid());
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showingrid,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showingrid'
                },
                name: 'showInGrid',
                bind: {
                    value: '{theAttribute.showInGrid}'
                },
                listeners: {
                    change: function () {
                        var me = this;
                        var fields = this.up('form').getForm().getFields().items;
                        fields.forEach(function (field, index) {
                            if (me.id !== field.id) {
                                field.clearInvalid();
                                field.validate();
                            }
                        });
                    }
                },
                 /**
                 * @param {Object} value The value to validate. The processed raw value will be used if nothing is passed.
                 * @return {String[]} Array of any validation errors
                 */
                getErrors: function (value) {
                    var messages = [];
                    var form = this.up('form');
                    // get theAttribute from viewModel
                    var theAttribute = form.getViewModel().get('theAttribute');
                    // we have to validate only if the value is true and the "mode" field is "hidden"
                    if (value === true && theAttribute.get('mode') === 'hidden') {
                        messages.push(CMDBuildUI.locales.Locales.administration.attributes.strings.thefieldmodeishidden);
                    } else {
                        var modeField = form.down('[name="mode"]');
                        modeField.clearInvalid();
                        modeField.validate();
                    }

                    return messages;
                }
            }, {
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showinreducedgrid'
                },
                name: 'showInReducedGrid',
                bind: {
                    value: '{theAttribute.showInReducedGrid}'
                },
                listeners: {
                    change: function () {
                        var me = this;
                        var fields = this.up('form').getForm().getFields().items;
                        fields.forEach(function (field, index) {
                            if (me.id !== field.id) {
                                field.clearInvalid();
                                field.validate();
                            }
                        });
                    }
                },
                /**
                 * @param {Object} value The value to validate. The processed raw value will be used if nothing is passed.
                 * @return {String[]} Array of any validation errors
                 */
                getErrors: function (value) {
                    var messages = [];
                    var form = this.up('form');
                    // get theAttribute from viewModel
                    var theAttribute = form.getViewModel().get('theAttribute');
                    // we have to validate only if the value is true and the "mode" field is "hidden"
                    if (value === true && theAttribute.get('mode') === 'hidden') {
                        messages.push(CMDBuildUI.locales.Locales.administration.attributes.strings.thefieldmodeishidden);
                    } else {
                        var modeField = form.down('[name="mode"]');
                        modeField.clearInvalid();
                        modeField.validate();
                    }

                    return messages;
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unique,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unique'
                },
                name: 'unique',
                bind: {
                    value: '{theAttribute.unique}'
                }
            }, {
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mandatory,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.mandatory'
                },
                name: 'mandatory',
                bind: {
                    value: '{theAttribute.mandatory}'
                },
                /**
                 * @param {Object} value The value to validate. The processed raw value will be used if nothing is passed.
                 * @return {String[]} Array of any validation errors
                 */
                getErrors: function (value) {
                    var messages = [];
                    var form = this.up('form');
                    // get theAttribute from viewModel
                    var theAttribute = form.getViewModel().get('theAttribute');
                    // we have to validate only if the value is true and the "mode" field is "hidden"
                    if (value === true && theAttribute.get('mode') === 'hidden') {
                        messages.push(CMDBuildUI.locales.Locales.administration.attributes.strings.thefieldmodeishidden);
                    }

                    return messages;
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.active,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.active'
                },
                name: 'active',
                bind: {
                    value: '{theAttribute.active}'
                }
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.attributes.titles.typeproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.attributes.titles.typeproperties'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.type,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.type'
                },
                name: 'type',
                disabled: true,
                allowBlank: false,
                displayField: 'label',
                valueField: 'value',
                store: {
                    type: 'attributes-attributetypes'
                },
                bind: {
                    value: '{theAttribute.type}'
                }
            }]
        }, {
            // If type is date
            bind: {
                hidden: '{!types.isDate}'
            },
            hidden: true,
            xtype: 'administration-attribute-datefields'
        }, {
            // If type is datetime
            bind: {
                hidden: '{!types.isDatetime}'
            },
            hidden: true,
            xtype: 'administration-attribute-datetimefields'
        }, {
            // If type is decimal
            bind: {
                hidden: '{!types.isDecimal}'
            },
            hidden: true,
            xtype: 'administration-attribute-decimalfields'
        }, {
            // If type is double
            bind: {
                hidden: '{!types.isDouble}'
            },
            hidden: true,
            xtype: 'administration-attribute-doublefields'
        }, {
            // If type is foreignKey
            bind: {
                hidden: '{!types.isForeignkey}'
            },
            hidden: true,
            xtype: 'administration-attribute-foreignkeyfields'
        }, {
            // If type is integer
            bind: {
                hidden: '{!types.isInteger}'
            },
            hidden: true,
            xtype: 'administration-attribute-integerfields'
        }, {
            // If type is ip address
            bind: {
                hidden: '{!types.isIpAddress}'
            },
            hidden: true,
            xtype: 'administration-attribute-ipaddressfields'
        }, {
            // If type is lookup
            bind: {
                hidden: '{!types.isLookup}'
            },
            hidden: true,
            xtype: 'administration-attribute-lookupfields'
        }, {
            // If type is reference
            bind: {
                hidden: '{!types.isReference}'
            },
            hidden: true,
            xtype: 'administration-attribute-referencefields'
        }, {
            // If type is string
            bind: {
                hidden: '{!types.isString}',
                theAttribute: '{theAttribute}'
            },
            hidden: true,
            xtype: 'administration-attribute-stringfields'
        }, {
            // If type is text
            bind: {
                hidden: '{!types.isText}'
            },
            hidden: true,
            xtype: 'administration-attribute-textfields'
        }, {
            // If type is time
            bind: {
                hidden: '{!types.isTime}'
            },
            hidden: true,
            xtype: 'administration-attribute-timefields'
        }, {
            // If type is timestamp
            bind: {
                hidden: '{!types.isTimestamp}'
            },
            hidden: true,
            xtype: 'administration-attribute-timestampfields'
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.attributes.titles.otherproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.attributes.titles.otherproperties'
        },
        bind: {
            hidden: '{isOtherPropertiesHidden}'
        },
        items: [{
            hidden: true,
            bind: {
                hidden: '{!theAttribute}'
            },
            items: [{
                layout: 'column',
                items: [{
                    columnWidth: 0.5,
                    xtype: 'textarea',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.help,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.help'
                    },
                    name: 'help',
                    bind: {
                        value: '{theAttribute.help}'
                    },
                    resizable: {
                        handles : "s"
                    }
                }, {
                    columnWidth: 0.5,
                    xtype: 'textarea',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showif,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showif'
                    },
                    name: 'showIf',
                    bind: {
                        value: '{theAttribute.showIf}'
                    },
                    resizable: {
                        handles : "s"
                    }
                }]
            }]
        }, {

            hidden: true,
            bind: {
                hidden: '{!theAttribute}'
            },
            items: [{
                layout: 'column',
                items: [{
                    columnWidth: 0.5,
                    xtype: 'textarea',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.validationrules,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.validationrules'
                    },
                    name: 'validadationRules',
                    bind: {
                        value: '{theAttribute.validationRules}'
                    },
                    resizable: {
                        handles : "s"
                    }
                }, {
                    columnWidth: 0.5,
                    xtype: 'textarea',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.autovalue,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.autovalue'
                    },
                    name: 'autoValue',
                    bind: {
                        value: '{theAttribute.autoValue}'
                    },
                    resizable: {
                        handles : "s"
                    }
                }]
            }]
        }, {

            hidden: true,
            bind: {
                hidden: '{!theAttribute}'
            },
            items: [{
                layout: 'column',
                items: [{
                    columnWidth: 0.5,
                    xtype: 'textarea',
                    hidden: true, // TODO: activate on 3.x milestone
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.actionpostvalidation,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.actionpostvalidation'
                    },
                    name: 'actionsPostValidation',
                    bind: {
                        value: '{theAttribute.actionPostValidation}'
                    }
                }]
            }]
        }]
    }],

    buttons: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
});
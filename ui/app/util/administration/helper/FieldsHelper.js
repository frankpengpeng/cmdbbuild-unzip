Ext.define('CMDBuildUI.util.administration.helper.FieldsHelper', {
    singleton: true,

    getFillOpacityInput: function (config) {
        return {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',
            layout: 'hbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillopacity,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillopacity'
            },
            items: [CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField(
                Ext.merge({},
                    config.fillOpacity, {
                        // columnWidth: 1,
                        flex: 1,
                        padding: '0 15 0 0',
                        name: 'fillOpacity',
                        increment: 0.01,
                        minValue: 0,
                        maxValue: 1,
                        multiplier: 100,
                        inputDecimalPrecision: 0,
                        sliderDecimalPrecision: 2,
                        showPercentage: true
                    })
            )]
        };
    },

    getFillColorInput: function (config) {

        return {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',
            layout: 'column',
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor'
            },
            items: [CMDBuildUI.util.helper.FieldsHelper.getColorpickerField(Ext.merge({}, config.fillColor, {
                columnWidth: 1,
                alt: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor,
                localized: {
                    alt: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor'
                },
                bind: {
                    hidden: '{actions.view}'
                }
            }))]
        };
    },

    getPointRadiusInput: function (config) {

        return {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',
            layout: 'column',
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.pointradius,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.pointradius'
            },
            items: [Ext.merge({}, config.pointRadius, {
                columnWidth: 1,
                xtype: 'numberfield',
                minValue: 0,
                step: 1,
                decimalPrecision: 0,
                name: 'pointRadius',
                bind: {
                    hidden: '{actions.view}'
                }
            }), Ext.merge({}, config.pointRadius, {
                xtype: 'displayfield',
                bind: {
                    hidden: '{!actions.view}'
                }
            })]
        };
    },

    getIconInput: function (config) {
        return {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',
            layout: 'column',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
            },
            items: [Ext.merge({}, {
                flex: 1,
                xtype: 'filefield',
                columnWidth: 0.8,
                emptyText: CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile,
                accept: '.png',
                buttonConfig: {
                    ui: 'administration-secondary-action-small'
                },
                bind: {
                    hidden: '{actions.view}'
                }
            }, config.icon.input), Ext.merge({}, {
                xtype: 'image',
                columnWidth: 0.2,
                height: 32,
                width: 32,
                alt: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                reference: 'currentIconPreview',
                tooltip: CMDBuildUI.locales.Locales.administration.common.strings.currenticon,
                config: {
                    theValue: null
                }
            }, config.icon.preview)]
        };
    },

    getIconComboInput: function (config) {
        return {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',
            layout: 'column',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
            items: [Ext.merge({}, config.icon.input, {
                flex: 1,
                xtype: 'combo',
                columnWidth: 1,
                emptyText: CMDBuildUI.locales.Locales.administration.common.strings.selectimage,
                valueField: '_id',
                displayField: '_description',

                store: {
                    model: 'CMDBuildUI.model.icons.Icon',
                    autoLoad: true,
                    autoDestroy: true,
                    proxy: {
                        url: Ext.String.format(
                            '{0}/uploads/?path=images/gis',
                            CMDBuildUI.util.Config.baseUrl
                        ),
                        type: 'baseproxy'
                    },
                    sorters: ['description'],
                    pageSize: 0
                },
                bind: {
                    hidden: '{actions.view}'
                }
            }), Ext.merge({}, config.icon.preview, {
                xtype: 'image',
                width: 32,
                maxWidth: 32,
                maxHeight: 32,
                height: 'auto',
                alt: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                reference: 'currentIconPreview',
                tooltip: CMDBuildUI.locales.Locales.administration.common.strings.currenticon,
                config: {
                    theValue: null
                }
            })]
        };
    },

    getStrokeDashStyleInput: function (config) {
        var display = this._getDisplayfield(config.strokeDashstyle, {
            displayField: 'label',
            valueField: 'value'
        });

        var combo = this._getCombofield(Ext.merge({}, config.strokeDashstyle, {
            columnWidth: 1,
            xtype: 'combobox',
            clearFilterOnBlur: true,
            queryMode: 'local',
            displayField: 'label',
            valueField: 'value',
            name: 'strokeDashstyle',
            bind: {
                hidden: '{actions.view}'
            }
        }), 'strokeDashstyle');

        return {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            layout: 'column',
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokedashstyle,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokedashstyle'
            },
            items: [combo, display]
        };
    },

    getStrokeColorInput: function (config) {
        return {
            columnWidth: 0.5,
            xtype: 'fieldcontainer',

            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor'
            },
            layout: 'column',
            items: [CMDBuildUI.util.helper.FieldsHelper.getColorpickerField(Ext.merge({}, config.strokeColor.input, {
                columnWidth: 1,
                alt: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor,
                localized: {
                    alt: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokecolor'
                },
                bind: {
                    hidden: '{actions.view}'
                }
            }))]
        };
    },

    getStrokeOpacityInput: function (config) {
        return {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            layout: 'hbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokeopacity,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokeopacity'
            },
            items: [CMDBuildUI.util.helper.FieldsHelper.getSliderWithInputField(Ext.merge({}, config.strokeOpacity, {
                flex: 1,
                padding: '0 15 0 0',
                name: 'strokeOpacity',
                increment: 0.01,
                minValue: 0,
                maxValue: 1,
                multiplier: 100,
                inputDecimalPrecision: 0,
                sliderDecimalPrecision: 2,
                showPercentage: true
            }))]
        };
    },

    getStrokeWidthInput: function (config) {
        var display = this._getDisplayfield(config.strokeWidth);
        var input = this._getNumberfield(config.strokeWidth, 'strokeWidth');
        var obj = {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokewidth,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.strokewidth'
            },
            items: [display, input]
        };
        return obj;
    },

    getFunctionsInput: function (config, propertyName) {

        propertyName = propertyName || 'function';

        var input = this._getCombofield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName], {
            fieldLabel: 'description',
            fieldValue: 'name'
        });
        return Ext.merge({}, {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
            allowBlank: config[propertyName].allowBlank,
            items: [input, display]
        }, (config[propertyName] && config[propertyName].fieldcontainer) || {});
    },

    getSubTypeInput: function (config) {
        var propertyName = 'subtype';
        var input = this._getCombofield(config.subtype, propertyName, true);
        var display = this._getDisplayfield(config.subtype, {
            fieldLabel: 'label',
            fieldValue: 'value'
        });

        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.type,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.type'
            },
            items: [input, display]
        };
    },

    getNameInput: function (config, disabledOnEdit, copyToInput) {
        var propertyName = 'name';
        var input = this._getTextfield(config[propertyName], propertyName, false, disabledOnEdit, copyToInput);
        var display = this._getDisplayfield(config[propertyName]);

        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            allowBlank: config[propertyName].allowBlank,
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
            },
            items: [input, display]
        };
    },

    getCodeInput: function (config, disabledOnEdit, copyToInput) {
        var propertyName = 'code';
        var input = this._getTextfield(config[propertyName], propertyName, false, disabledOnEdit, copyToInput);
        var display = this._getDisplayfield(config[propertyName]);

        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            allowBlank: config[propertyName].allowBlank,
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.code,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.code'
            },
            items: [input, display]
        };
    },

    getActiveOnSaveInput: function (config, name, disabledOnEdit) {
        var propertyName = name || 'activeonsave';
        var input = this._getCheckboxfield(config[propertyName], propertyName);

        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.tesks.labels.activeonsave,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tesks.labels.activeonsave'
            },
            items: [input]
        };

        return fieldcontainer;
    },

    getEmailAccountsInput: function (config) {
        var propertyName = 'emailaccounts';
        var input = this._getCombofield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName], {
            fieldLabel: 'name',
            fieldValue: '_id'
        });

        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.tesks.labels.emailaccount,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tesks.labels.emailaccount'
            },
            items: [input, display]
        };
    },

    getIncomingFolderInput: function (config) {
        var propertyName = 'incomingfolder';
        var input = this._getTextfield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            allowBlank: config[propertyName].allowBlank,
            fieldLabel: CMDBuildUI.locales.Locales.administration.tesks.labels.incomingfolder,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tesks.labels.incomingfolder'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },


    getFilterTypeInput: function (config) {
        var propertyName = 'filtertype';
        var input = this._getCombofield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName], {
            fieldLabel: 'label',
            fieldValue: 'value'
        });

        return {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.tesks.labels.filtertype,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.tesks.labels.filtertype'
            },
            items: [input, display]
        };
    },
    getDescriptionInput: function (config) {
        var propertyName = 'description';
        var input = this._getTextfield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        var fieldcontainer = Ext.merge({}, {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            allowBlank: config[propertyName].allowBlank,
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            items: [input, display]
        }, config.description.fieldcontainer || {});

        return fieldcontainer;

    },

    getActiveInput: function (config, name, disabledOnEdit) {
        var propertyName = name || 'active';
        var input = this._getCheckboxfield(config[propertyName], propertyName);

        var fieldcontainer = Ext.merge({}, {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            items: [input]
        }, config[propertyName].fieldcontainer ? config[propertyName].fieldcontainer : {});

        return fieldcontainer;
    },

    getServiceTypeInput: function (config) {
        var propertyName = 'servicetype';
        var input = this._getCombofield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.servicetype,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.servicetype'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getTypeInput: function (config) {
        var propertyName = 'type';
        var input = this._getCombofield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.type,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.type'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getAssociatedClass: function (config) {
        var propertyName = 'associatedClass';
        var input = this._getCombofield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedclass,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.associatedclass'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getAssociatedCard: function (config) {
        var propertyName = 'associatedCard';
        var input = this._getReferenceField(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.associatedcard'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getLastCheckin: function (config) {
        var propertyName = 'lastCheckin';
        var input = this._getTextfield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: 'CMDBuildUI.locales.Locales.administration.bim.lastcheckin',
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.bim.lastcheckin'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getGeoServerEnabledInput: function (config, name, disabledOnEdit) {
        var propertyName = name || 'active';
        var input = this._getCheckboxfield(config[propertyName], propertyName);

        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 1,
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.messages.enabled,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.messages.enabled'
            },
            items: [input]
        };

        return fieldcontainer;
    },

    getGeoServerUrlInput: function (config) {
        var propertyName = 'geoserverurl';
        var input = this._getTextfield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.url',
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.url'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getGeoServerWorkspaceInput: function (config) {
        var propertyName = 'geoserverworkspace';
        var input = this._getTextfield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.workspace,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.workspace'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getGeoServerAdminUserInput: function (config) {
        var propertyName = 'geoserveradminuser';
        var input = this._getTextfield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.adminuser,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.adminuser'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getGeoServerAdminPasswordInput: function (config) {
        var propertyName = 'geoserveradminpassword';
        var input = this._getTextfield(config[propertyName], propertyName, true);
        var display = this._getDisplayfield(config[propertyName]);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            reference: 'geoserveradminpassword',
            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.adminpassword,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.adminpassword'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    getParentProject: function (config) {
        var propertyName = 'parentId';
        var input = this._getCombofield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);
        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.bim.parentproject,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.bim.parentproject'
            },
            items: [input, display]
        };

        return fieldcontainer;
    },

    privates: {
        _getDisplayfield: function (config, storeKeys) {
            var displayfield = Ext.merge({}, {
                columnWidth: 1,
                // hidden: true,
                minHeight: 40,
                xtype: 'displayfield',
                bind: {
                    hidden: '{!actions.view}'
                }
            }, config);

            var bindedStore = displayfield.store || displayfield.bind.store;
            if (displayfield.store || (displayfield.bind && displayfield.bind.store)) {

                displayfield.renderer = function (value, input) {
                    var store;

                    if (typeof bindedStore === "object") {
                        store = bindedStore;
                    } else if (typeof bindedStore === "string") {
                        store = input.lookupViewModel().getStore(bindedStore.slice(1, -1)) || input.lookupViewModel().get(bindedStore.slice(1, -1));

                    }
                    if (storeKeys && store && value) {
                        var record = store.findRecord(storeKeys.valueField, value);
                        if (record) {
                            return record.get(storeKeys.displayField);
                        }
                    }
                    return value;
                };

                delete displayfield.bind.store;
            }

            return displayfield;
        },
        _getTextfield: function (config, name, password, disabledOnEdit, copyToInput) {
            var inputtype = password ? 'password' : 'text';
            if (name === 'name' || name === 'code') {
                config.vtype = 'nameInputValidation';
            }
            var textfield = Ext.merge({}, {
                columnWidth: 1,
                xtype: 'textfield',
                itemId: Ext.String.format('{0}_input', name),
                name: name,
                inputType: inputtype,
                hidden: true,
                bind: {
                    hidden: '{actions.view}'
                },
                listeners: {}
            }, config);
            if (disabledOnEdit) {
                textfield = this._setDisabledOnEdit(textfield);
            }
            if (copyToInput) {
                this._afterRenderCopyToInput(textfield, copyToInput);
            }
            return textfield;
        },
        _getTextarea: function (config, name, disabledOnEdit) {

            var textarea = Ext.merge({}, {
                columnWidth: 1,
                xtype: 'textarea',
                itemId: Ext.String.format('{0}_input', name),
                name: name,
                bind: {
                    readOnly: '{actions.view}'
                },
                listeners: {}
            }, config);

            if (disabledOnEdit) {
                textarea = this._setDisabledOnEdit(textarea);
            }

            return textarea;
        },
        _afterRenderCopyToInput: function (input, copyToInput) {
            var me = this;
            input.listeners.afterrender = function (_input) {
                me._copyToInputOnChange(_input, copyToInput);
            };
        },

        _copyToInputOnChange: function (input, copyToInput) {
            var me = this;
            input.on('change', function (_input, newVal, oldVal) {
                me.copyTo(_input, newVal, oldVal, copyToInput);
            });
        },

        _getReferenceField: function (config, name, disabledOnEdit) {
            var combo = Ext.merge({}, {
                xtype: 'referencecombofield',
                reference: 'maincombo',
                metadata: 'Class',
                itemId: Ext.String.format('{0}_input', name),
                columnWidth: 1,
                bind: {
                    hidden: '{actions.view}'
                },
                name: name,
                margin: 0
            }, config);
            if (disabledOnEdit) {
                combo = this._setDisabledOnEdit(combo);
            }
            return combo;

        },

        _getCombofield: function (config, name, disabledOnEdit) {
            var combo = Ext.merge({}, {
                columnWidth: 1,
                xtype: 'combobox',
                clearFilterOnBlur: true,
                itemId: Ext.String.format('{0}_input', name),
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                reference: name,
                forceSelection: true,
                name: name,
                hidden: true,
                bind: {
                    hidden: '{actions.view}'
                }
            }, config);
            if (disabledOnEdit) {
                combo = this._setDisabledOnEdit(combo);
            }
            return combo;
        },
        _getNumberfield: function (config, name, disabledOnEdit) {
            var numberfield = Ext.merge({}, {
                xtype: 'numberfield',
                minValue: 0,
                itemId: Ext.String.format('{0}_input', name),
                step: 1,
                decimalPrecision: 0,
                name: name,
                hidden: true,
                bind: {
                    hidden: '{actions.view}'
                }
            }, config);

            if (disabledOnEdit) {
                numberfield = this._setDisabledOnEdit(numberfield);
            }
            return numberfield;
        },

        _setDisabledOnEdit: function (input) {
            input.listeners = input.listeners || {};
            if (!input.listeners.beforerender) {
                input.listeners.beforerender = function (_input) {
                    var isAdd = _input.lookupViewModel().get('actions.add');
                    if (!isAdd) {
                        _input.vtype = undefined;
                    }
                    _input.setDisabled(!isAdd);
                };
            }
            return input;
        },

        _getCheckboxfield: function (config, name, readOnlyOnView) {

            var checkbox = Ext.merge({}, {
                xtype: 'checkbox',
                name: name,
                itemId: Ext.String.format('{0}_input', name),
                bind: {
                    disabled: '{actions.view}'
                }
            }, config);

            return checkbox;
        }
    },
    getAllClassesInput: function (config, name, disabledOnEdit) {
        var propertyName = name || 'class';
        var fieldcontainer = Ext.merge({}, {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            items: []
        }, config[propertyName].fieldcontainer || {});

        delete config[propertyName].fieldLabel;

        var display = this._getDisplayfield(config[propertyName], {
            displayField: 'label',
            valueField: '_id'
        });
        config = Ext.merge({}, {
            name: name,
            valueField: '_id',
            displayField: 'label',
            queryMode: 'local',
            forceSelection: true,
            typeAhead: true,
            bind: {
                hidden: '{actions.view}'
            },

            triggers: {
                clear: {
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
        }, config[propertyName]);
        var input = this._getCombofield(config, name, disabledOnEdit);
        fieldcontainer.items.push(input);
        fieldcontainer.items.push(display);
        return fieldcontainer;
    },

    getTargetClassesInput: function (config, name, disabledOnEdit) {
        var propertyName = 'targetClass';
        var input = this._getCombofield(config[propertyName], propertyName);
        var display = this._getDisplayfield(config[propertyName]);

        var fieldcontainer = {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.administration.navigationtrees.strings.sourceclass,
            items: [input, display]
        };

        return fieldcontainer;
    },

    copyTo: function (input, newVal, oldVal, copyToInput) {
        if (input.lookupViewModel().get('actions.add')) {
            var copyTo = input.up('form').down(copyToInput);
            if (copyTo && oldVal === copyTo.getValue()) {
                copyTo.setValue(newVal);
            }
        }
    },

    getDeafultGroupInput: function (config) {
        var propertName = 'defaultGroup';
        var combo = this._getCombofield(Ext.merge({}, config[propertName], {
            columnWidth: 1,
            xtype: 'combobox',
            clearFilterOnBlur: true,
            queryMode: 'local',
            displayField: 'description',
            valueField: '_id',
            name: propertName,
            bind: {
                hidden: '{actions.view}'
            }
        }), propertName);

        return {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            layout: 'column',
            fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup'
            },
            items: [combo]
        };
    },
    getDisplayField: function (propertyName, config) {
        return this._getDisplayfield(config[propertyName], propertyName);
    },
    getCommonTextfieldInput: function (propertyName, config) {
        var fieldcontainer = Ext.merge({}, {
            xtype: 'fieldcontainer',
            layout: 'column',
            itemId: Ext.String.format('{0}_fieldcontainer', propertyName),
            columnWidth: config[propertyName].columnWidth || 0.5,
            fieldLabel: config[propertyName].fieldLabel,
            allowBlank: config[propertyName].allowBlank,
            items: []
        }, config[propertyName].fieldcontainer || {});
        delete config[propertyName].fieldLabel;
        delete config[propertyName].fieldcontainer;
        if (config[propertyName].localized) {
            delete config[propertyName].localized.fieldLabel;
        }
        fieldcontainer.items.push(this._getTextfield(config[propertyName], propertyName));
        if (config[propertyName].noDisplayField === true) {
            return fieldcontainer;
        }
        fieldcontainer.items.push(this._getDisplayfield(config[propertyName]));

        return fieldcontainer;
    },

    getCommonTextareaInput: function (propertyName, config, disabledOnEdit) {
        var fieldcontainer = Ext.merge({}, {
            xtype: 'fieldcontainer',
            layout: 'column',
            itemId: Ext.String.format('{0}_fieldcontainer', propertyName),
            columnWidth: config[propertyName].columnWidth || 0.5,
            fieldLabel: config[propertyName].fieldLabel,
            allowBlank: config[propertyName].allowBlank,
            items: []
        }, config[propertyName].fieldcontainer || {});
        delete config[propertyName].fieldLabel;
        delete config[propertyName].fieldcontainer;
        if (config[propertyName].localized) {
            delete config[propertyName].localized.fieldLabel;
        }
        var textarea = this._getTextarea(config[propertyName], propertyName, disabledOnEdit);

        fieldcontainer.items.push(textarea);
        return fieldcontainer;
    },

    getCommonNumberfieldInput: function (propertyName, config) {
        var fieldcontainer = Ext.merge({}, {
            xtype: 'fieldcontainer',
            layout: 'column',
            itemId: Ext.String.format('{0}_fieldcontainer', propertyName),
            columnWidth: config[propertyName].columnWidth || 0.5,
            fieldLabel: config[propertyName].fieldLabel,
            allowBlank: config[propertyName].allowBlank,
            items: []
        }, config[propertyName].fieldcontainer || {});
        delete config[propertyName].fieldLabel;
        delete config[propertyName].fieldcontainer;
        if (config[propertyName].localized) {
            delete config[propertyName].localized.fieldLabel;
        }
        fieldcontainer.items.push(this._getNumberfield(config[propertyName], propertyName));
        fieldcontainer.items.push(this._getDisplayfield(config[propertyName]));

        return fieldcontainer;
    },

    getCommonComboInput: function (propertyName, config, disabledOnEdit, onlyCombo) {
        var fieldcontainer = Ext.merge({}, {
            xtype: 'fieldcontainer',
            layout: 'column',
            columnWidth: config[propertyName].columnWidth || 0.5,
            fieldLabel: config[propertyName].fieldLabel,
            items: [],
            itemId: Ext.String.format('{0}_fieldcontainer', propertyName),
            allowBlank: config[propertyName].allowBlank
        }, config[propertyName].fieldcontainer || {});

        delete config[propertyName].fieldcontainer;
        delete config[propertyName].fieldLabel;
        delete config[propertyName].disabled;
        if (config[propertyName].localized) {
            delete config[propertyName].localized.fieldLabel;
        }

        fieldcontainer.items.push(this._getCombofield(config[propertyName], propertyName, disabledOnEdit));
        if (!onlyCombo) {
            fieldcontainer.items.push(this._getDisplayfield(config[propertyName], {
                displayField: config[propertyName].displayField || 'label',
                valueField: config[propertyName].valueField || 'value'
            }));
        }

        return fieldcontainer;
    },
    setAllowBlank: function (field, value, form) {
        field.allowBlank = value;
        field.up('fieldcontainer').allowBlank = value;
        if (form && form.form) {
            form.form.checkValidity();
        }

        if (value) {
            field.clearInvalid();
            if (field.up('fieldcontainer').labelEl) {
                field.up('fieldcontainer').labelEl.dom.innerHTML = field.up('fieldcontainer').labelEl.dom.innerHTML.replace('<span class="required-field-placeholder"> *</span>', '<span class="required-field-placeholder"></span>');
            }
        } else {
            if (field.up('fieldcontainer').labelEl) {
                field.up('fieldcontainer').labelEl.dom.innerHTML = field.up('fieldcontainer').labelEl.dom.innerHTML.replace('<span class="required-field-placeholder"></span>', '<span class="required-field-placeholder"> *</span>');
            }
        }
    }
});
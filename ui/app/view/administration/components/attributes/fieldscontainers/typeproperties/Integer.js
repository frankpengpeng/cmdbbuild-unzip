Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Integer', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-attribute-integerfields',
    items: [{
        xtype: 'container',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: [{

            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'textfield',
                itemId: 'unitOfMeasureFieldInteger',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure'
                },
                name: 'unitOfMeasure',
                maxLength: 5,
                bind: {
                    value: '{theAttribute.unitOfMeasure}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var form = this.up('form');
                        var precisionAttributeLocationField = form.down('#unitOfMeasureLocationFieldInteger');
                        if (!newValue) {
                            precisionAttributeLocationField.setValue(null);
                        }
                        precisionAttributeLocationField.validate();
                    }
                }
            }, {
                columnWidth: 0.5,
                xtype: 'combo',
                itemId: 'unitOfMeasureLocationFieldInteger',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.positioningofum,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.positioningofum'
                },
                name: 'unitOfMeasureLocationInteger',
                clearFilterOnBlur: false,
                anyMatch: true,
                autoSelect: true,
                forceSelection: true,
                typeAhead: true,
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                store: {
                    type: 'attributes-unitofmeasurelocation'
                },
                bind: {
                    value: '{theAttribute.unitOfMeasureLocation}',
                    disabled: '{!theAttribute.unitOfMeasure}'
                },
                validator: function (field) {
                    var form = this.up('form');
                    var precisionAttributeField = form.down('#unitOfMeasureFieldInteger');
                    if (precisionAttributeField.getValue() && !this.getValue()) {
                        return CMDBuildUI.locales.Locales.administration.attributes.strings.positioningofumrequired;
                    }
                    return true;
                }
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showseparator,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showseparator'
                },
                itemId: 'attribute-showseparator',
                name: 'showSeparator',
                bind: {
                    value: '{theAttribute.showThousandsSeparator}'
                }
            }]
        }]
    }, {
        xtype: 'fieldcontainer',
        hidden: true,
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            layout: 'column',
            items: [{
                layout: 'column',
                items: [{
                    columnWidth: 0.5,
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.unitofmeasure'
                    },
                    bind: {
                        value: '{theAttribute.unitOfMeasure}'
                    }
                }, {
                    columnWidth: 0.5,
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.positioningofum,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.positioningofum'
                    },
                    bind: {
                        value: '{theAttribute.unitOfMeasureLocation}'
                        // disabled: '{!theAttribute.unitOfMeasure}'
                    },
                    renderer: function (value) {
                        if (value) {
                            var store = Ext.getStore('attributes.UnifOfMeasureLocations');
                            if (store) {
                                var record = store.findRecord('value', value);
                                return record && record.get('label');
                            }
                        }
                        return value;
                    }
                }]
            }, {
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showseparator,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showseparator'
                },
                itemId: 'attribute-showseparator',
                name: 'showSeparator',
                disabled: true,
                disabledCls: '',
                bind: {
                    value: '{theAttribute.showThousandsSeparator}'
                }
            }]
        }]
    }]
});
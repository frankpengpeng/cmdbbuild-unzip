Ext.define('CMDBuildUI.util.helper.FieldsHelper', {
    singleton: true,

    /**
     * Generate fielcontainer with slider and numbierfield
     * 
     * @param {Object} config
     * @param {Number} config.multiplier the maxValue of slider, the input 
     * minValue will be set equal to minValue * multiplier
     * defaultValue = 1
     * 
     * @param {Number} config.minValue the maxValue of slider, the input 
     * minValue will be set  equal to minValue * multiplier
     * defaultValue = 0
     * 
     * @param {Number} config.maxValue the maxValue of slider, the input 
     * maxValue will be set equal to maxValue * multiplier
     * defaultValue = 100
     * 
     * @param {Boolean} config.showPercentage if true show percentage "%" symbol
     * in slider qtip
     * defaultValue = false
     * 
     * @param {Number} config.sliderDecimalPrecision use 0 if showPercentage 
     * otherwise use any number, value is used to format the slider qtip text
     * defaultValue = 0
     * 
     * @param {Number} config.inputDecimalPrecision use 0 if showPercentage 
     * otherwise use any number, value is used to format the input field value
     * defaultValue = 0
     * 
     * @param {String} config.name name used on input field
     * defaultValue = undefined
     * 
     * @param {String} config.columnWidth use this is the onwner of 
     * fieldcontainer is layout type "column"
     * defaultValue = undefined
     * 
     * @param {String|Number} config.padding the padding of field conainer
     * defaultValue = 0
     * 
     * @param {String} config.fieldLabel the label of fieldcontainer
     * defaultValue = undefined
     * 
     * @param {Object} config.bind the bind of slider
     * defaultValue = undefined
     * 
     * @param {Object} config.localized the locale object key
     * @param {String} config.loacalized.fieldLabel the localized key as string 
     * of fieldLabel
     * defaultValue = {}
     * 
     * @return {Ext.field.FieldContainer} 
     * 
     */
    getSliderWithInputField: function (config) {
        config = Ext.merge({
            multiplier: 1,
            minValue: 0,
            maxValue: 100,
            showPercentage: false,
            sliderDecimalPrecision: 0,
            inputDecimalPrecision: 0,
            name: undefined,
            columnWidth: undefined,
            padding: 0,
            fieldLabel: undefined,
            bind: {},
            localized: {}
        }, config);

        var fieldcontainer = {
            columnWidth: config.columnWidth,
            xtype: 'fieldcontainer',
            padding: config.padding,
            fieldLabel: config.fieldLabel,
            localized: config.localized,
            layout: 'column',
            items: [{
                xtype: 'fieldcontainer',
                layout: 'hbox',
                columnWidth: 1,
                bind: {
                    hidden: '{actions.view}'
                },
                items: [{
                    flex: 1,
                    xtype: 'slider',
                    increment: config.increment,
                    minValue: config.minValue,
                    maxValue: config.maxValue,
                    decimalPrecision: config.sliderDecimalPrecision,
                    padding: '0 15 0 0',
                    name: config.name,
                    bind: Ext.merge({
                        hidden: '{actions.view}'
                    }, config.bind),
                    tipText: function (thumb) {
                        if (config.showPercentage) {
                            return Ext.util.Format.percent(thumb.value);
                        } else {
                            return parseFloat(thumb.value * config.multiplier).toFixed(config.sliderDecimalPrecision);
                        }
                    },
                    listeners: {
                        change: function (slider, newValue) {
                            slider.up('fieldcontainer').down('numberfield').setValue(String(Ext.util.Format.number(newValue * config.multiplier, config.inputDecimalPrecision)));
                        }
                    }
                }, {
                    xtype: 'numberfield',
                    width: 50,
                    step: config.increment * config.multiplier,
                    minValue: config.minValue * config.multiplier,
                    maxValue: config.maxValue * config.multiplier,
                    decimalPrecision: config.inputDecimalPrecision,
                    // Remove spinner buttons, and arrow key and mouse wheel listeners
                    hideTrigger: true,
                    value: 0,
                    keyNavEnabled: false,
                    mouseWheelEnabled: false,
                    selectOnFocus: true,
                    fieldStyle: 'text-align: center;padding: 5px 5px 4px',
                    listeners: {
                        blur: function (numberfield, event, eOpts) {
                            var slider = numberfield.up('fieldcontainer').down('slider');
                            numberfield.validate();
                            numberfield.lookupViewModel().set(slider.getConfig().bind.value.stub.path, parseFloat(Number(numberfield.getValue()) / config.multiplier).toFixed(config.sliderDecimalPrecision));
                        }
                    }
                }]
            }, {
                xtype: 'displayfield',
                columnWidth: 1,
                bind: Ext.merge(config.bind,{
                    hidden: '{!actions.view}'
                }),
                hidden: true,
                renderer: function (value) {
                    if (config.showPercentage) {
                        return Ext.util.Format.percent(value);
                    } else {
                        return parseFloat(value * config.multiplier).toFixed(config.sliderDecimalPrecision);
                    }
                }
            }]
        };
        return fieldcontainer;

    },

    /**
     * Generate fielcontainer with slider and numbierfield
     * 
     * @param {Object} config
     * 
     * @param {String} config.name name used on input field
     * defaultValue = undefined
     * 
     * @param {String} config.columnWidth use this is the onwner of 
     * fieldcontainer is layout type "column"
     * defaultValue = undefined
     * 
     * @param {String|Number} config.padding the padding of field conainer
     * defaultValue = 0
     * 
     * @param {String} config.fieldLabel the label of fieldcontainer
     * defaultValue = undefined
     * 
     * @param {Object} config.bind the bind of slider
     * defaultValue = undefined
     * 
     * @param {Object} config.alt img alt tag attribute
     * [W] For WAI-ARIA compliance, IMG elements SHOULD have an alt attribute.
     * defaultValue = '-'
     * 
     * @param {Object} config.localized the localized object used in 
     * fieldconatiner and image
     * @param {String} config.localized.fieldLabel the localized key as string 
     * of fieldLabel
     * @param {String} config.localized.alt the localized key as string of alt
     * defaultValue = {}
     * 
     * @return {Ext.field.FieldContainer}
     */
    getColorpickerField: function (config) {
        config = Ext.applyIf(config, {
            name: undefined,
            columnWidth: undefined,
            padding: 0,
            fieldLabel: undefined,
            bind: {},
            localized: {},
            alt: '-'
        });

        var fieldcontainer = {
            columnWidth: config.columnWidth,
            xtype: 'fieldcontainer',
            fieldLabel: config.fieldLabel,
            localized: config.localized,
            layout: 'column',
            padding: config.padding,
            items: [{
                name: config.name,
                columnWidth: 1,
                xtype: 'cmdbuild-colorpicker',
                bind: config.bind,
                triggers: {
                    clear: {
                        cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                        handler: function (input) {
                            input.lookupViewModel().set(input.getConfig().bind.value.stub.path, null);
                            input.up('fieldcontainer').down('image').setStyle('color', 'initial');
                            input.reset();
                        }
                    }
                },
                listeners: {
                    change: function (input, newValue) {
                        input.up('fieldcontainer').down('image').setStyle('color', newValue);
                    }
                }
            }, {
                xtype: 'image',
                autoEl: 'div',
                alt: config.alt || config.fieldLabel,
                localized: config.localized,
                width: 32,
                cls: 'fa-2x x-fa fa-square',
                style: {
                    lineHeight: '32px'
                }
            }]
        };
        return fieldcontainer;
    },

    /**
     * 
     * @param {Boolean} value 
     * @return {String}
     */
    renderBooleanField: function(value) {
        if (Ext.isEmpty(value)) {
            return "";
        }
        var klass = '';
        if (value) {
            klass = 'x-form-cb-checked';
        }
        return Ext.String.format("<span class=\"{0}\"><span class=\"x-form-checkbox-default\"></span></span>", klass);
    },

    /**
     * 
     * @param {Numeric} value 
     * @param {Object} config 
     * @param {Object} config.showThousandsSeparator
     * @param {Object} config.unitOfMeasure
     * @param {Object} config.unitOfMeasureLocation
     * @return {String}
     */
    renderIntegerField: function (value, config) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        config = config || {};

        // show thousands separator
        if (config.showThousandsSeparator) {
            value = this.formatNumber(value, null, config.showThousandsSeparator);
        }

        // show unit of measure
        if (!Ext.isEmpty(config.unitOfMeasure)) {
            var format;
            if (config.unitOfMeasureLocation === CMDBuildUI.model.Attribute.unitOfMeasureLocations.before) {
                format = "{1} {0}";
            } else {
                format = "{0} {1}";
            }
            value = Ext.String.format(format, value, config.unitOfMeasure);
        }

        return value;
    },


    /**
     * 
     * @param {Numeric} value 
     * @param {Object} config 
     * @param {Object} config.scale
     * @param {Object} config.showThousandsSeparator
     * @param {Object} config.unitOfMeasure
     * @param {Object} config.unitOfMeasureLocation
     * @return {String}
     */
    renderDecimalField: function (value, config) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        config = config || {};

        // show thousands separator
        value = this.formatNumber(value, config.scale, config.showThousandsSeparator);

        // show unit of measure
        if (!Ext.isEmpty(config.unitOfMeasure)) {
            var format;
            if (config.unitOfMeasureLocation === CMDBuildUI.model.Attribute.unitOfMeasureLocations.before) {
                format = "{1} {0}";
            } else {
                format = "{0} {1}";
            }
            value = Ext.String.format(format, value, config.unitOfMeasure);
        }

        return value;
    },


    /**
     * 
     * @param {Numeric} value 
     * @param {Object} config 
     * @param {Object} config.visibleDecimals
     * @param {Object} config.showThousandsSeparator
     * @param {Object} config.unitOfMeasure
     * @param {Object} config.unitOfMeasureLocation
     * @return {String}
     */
    renderDoubleField: function (value, config) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        config = config || {};

        // show thousands separator
        value = this.formatNumber(value, config.visibleDecimals, config.showThousandsSeparator);

        // show unit of measure
        if (!Ext.isEmpty(config.unitOfMeasure)) {
            var format;
            if (config.unitOfMeasureLocation === CMDBuildUI.model.Attribute.unitOfMeasureLocations.before) {
                format = "{1} {0}";
            } else {
                format = "{0} {1}";
            }
            value = Ext.String.format(format, value, config.unitOfMeasure);
        }

        return value;
    },

    /**
     * 
     * @param {Date} value 
     * @return {String}
     */
    renderDateField: function(value) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        return Ext.util.Format.date(value, CMDBuildUI.util.helper.UserPreferences.getDateFormat());
    },

    /**
     * 
     * @param {Date} value 
     * @param {Object} config
     * @param {Boolean} config.hideSeconds
     * @return {String}
     */
    renderTimeField: function(value, config) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        config = config || {};
        var format;
        // convert to date
        if (typeof value === "string") {
            value = new Date('1970-01-01T' + value);
        }
        // get format
        if (config.hideSeconds) {
            format = CMDBuildUI.util.helper.UserPreferences.getTimeWithoutSecondsFormat();
        } else {
            format = CMDBuildUI.util.helper.UserPreferences.getTimeWithSecondsFormat();
        }
        return Ext.util.Format.date(value, format);
    },

    /**
     * 
     * @param {Date} value 
     * @param {Object} config
     * @param {Boolean} config.hideSeconds
     * @return {String}
     */
    renderTimestampField: function(value, config) {
        if (Ext.isEmpty(value)) {
            return value;
        }
        config = config || {};
        var format;
        if (config.hideSeconds) {
            format = CMDBuildUI.util.helper.UserPreferences.getTimestampWithoutSecondsFormat();
        } else {
            format = CMDBuildUI.util.helper.UserPreferences.getTimestampWithSecondsFormat();
        }
        return Ext.util.Format.date(value, format);
    },

    /**
     * Return base configuration for HTML editor
     * @param {Object} config 
     */
    getHTMLEditor: function (config) {
        return Ext.applyIf(config, {
            xtype: 'cmdbuildhtmleditor',
            enableAlignments: true,
            enableColors: true,
            enableFont: true,
            enableFontSize: true,
            enableFormat: true,
            enableLinks: true,
            enableLists: true,
            enableSourceEdit: true
        });
    },

    privates: {
        /**
         * 
         * @param {Number} number 
         * @param {Number} decimalsToShow 
         * @param {Boolean} showThousandsSeparator 
         */
        formatNumber: function (number, decimalsToShow, showThousandsSeparator) {
            if (typeof number !== "number" && isNaN(number)) {
                return number;
            } else if (typeof number !== "number") {
                number = parseFloat(number);
            }
            var strnumber = number.toString();
            if (!Ext.isEmpty(decimalsToShow)) {
                strnumber = number.toFixed(decimalsToShow);
            }
            if (CMDBuildUI.util.helper.UserPreferences.getDecimalsSeparator() !== '.') {
                strnumber.replace(".", CMDBuildUI.util.helper.UserPreferences.getDecimalsSeparator());
            }
            if (showThousandsSeparator) {
                strnumber = strnumber.replace(/(\d)(?=(\d{3})+(?!\d))/g, '$1' + CMDBuildUI.util.helper.UserPreferences.getThousandsSeparator());
            }
            return strnumber;
        }
    }

});
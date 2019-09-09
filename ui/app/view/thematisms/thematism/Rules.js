
Ext.define('CMDBuildUI.view.thematisms.thematism.Rules', {
    extend: 'CMDBuildUI.components.tab.FieldSet',

    requires: [
        'CMDBuildUI.view.thematisms.thematism.RulesController',
        'CMDBuildUI.view.thematisms.thematism.RulesModel'
    ],

    alias: 'widget.thematisms-thematism-rules',
    controller: 'thematisms-thematism-rules',
    viewModel: {
        type: 'thematisms-thematism-rules'
    },

    title: CMDBuildUI.locales.Locales.thematism.defineLegend,
    collapsible: true,
    scrollable: true,
    items: [{
        xtype: 'button',
        reference: 'calculaterules',
        itemId: 'calculaterules',
        text: CMDBuildUI.locales.Locales.thematism.calculateRules,
        ui: 'management-action-small',
        localized: {
            text: 'CMDBuildUI.locales.Locales.thematism.calculateRules'
        },
        margin: '0 0 15 15',
        hidden: true,
        disabled: true,
        bind: {
            disabled: '{buttonsDisabled}'
        }
    }, {
        xtype: 'grid',
        markDirty: false,
        layout: 'vbox',
        bind: {
            store: '{legendstore}'
        },
        columns: [{
            text: CMDBuildUI.locales.Locales.thematism.value,
            localized: {
                text: 'CMDBuildUI.locales.Locales.thematism.value'
            },
            dataIndex: 'viewValue'
        }, {
            text: CMDBuildUI.locales.Locales.thematism.quantity,
            localized: {
                text: 'CMDBuildUI.locales.Locales.thematism.quantity'
            },
            dataIndex: 'count'
        }, {
            text: CMDBuildUI.locales.Locales.thematism.color,
            localized: {
                text: 'CMDBuildUI.locales.Locales.thematism.color'
            },
            dataIndex: 'color',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                return Ext.String.format('<div style="background-color: {0}; width: inherit; height: 32px;"> </div>', value);
            }
        }]
    }],

    //this configuration when true, enables some extra behavours: 
    //color picker expantion, and datachanged event handle in the view model
    config: {
        needListener: false
    },

    /**
     * @param {string}
     */
    getColorPicker: function () {
        var me = this;

        if (!me.colorpicker) {
            me.colorpicker = new Ext.picker.Color({
                xtype: 'colorpicker',
                floating: true,
                alwaysOnTop: true,
                shadow: false,
                focusable: true,
                resizable: false,
                draggable: false,
                minWidth: 300,
                layout: 'fit',
                value: null,
                viewModel: {
                    theLegendRecord: null,
                    formulas: {
                        updateSelected: {
                            bind: '{theLegendRecord}',
                            get: function (record) {
                                if (record) {
                                    var color = record.get('color').substr(1).toUpperCase();
                                    foundColor = this.getView().colors.find(function (col) {
                                        if (col == color) { return true };
                                    });
                                    if (foundColor) {
                                        this.getView().select(foundColor);
                                    }
                                }
                            }
                        }
                    }
                },
                listeners: {
                    select: function (colorpicker, color, eopts) {
                        var legendRecord = this.getViewModel().get('theLegendRecord');
                        legendRecord.set('color', '#' + color);
                    }
                },
                controller: {
                    listen: {
                        component: {
                            '#': {
                                beforerender: function () {
                                    me.colorpicker.mon(me, 'destroy', function () {
                                        me.colorpicker.destroy()
                                    }, me);
                                }
                            }
                        }
                    }
                }
            });
        }
        return me.colorpicker;
    },

    /**
     * 
     * @param {CMDBuildUI.model.thematisms.LegendModel} record 
     */
    setColorPickerRecord: function (record) {
        var me = this;
        var colorpicker = me.colorpicker;
        var vm = colorpicker.getViewModel();
        vm.set('theLegendRecord', record);
    },

    /**
     * 
     * @param {Object} e 
     */
    onWindowClick: function (e) {
        this.collapseIf(e);
    },

    /**
     * @param {HTMLElement} tr the cell clicked
     */
    expandColorPicker: function (tr) {
        var me = this;
        //Ext.suspendLayouts();

        var colorpicker = me.getColorPicker();
        colorpicker.show();
        colorpicker.setZIndex(190000);

        me.isColorPickerExpanded = true;
        me.alignColorPicker(tr);

        Ext.asap(function () {
            me.colorpicker.mon(Ext.getWin(), 'click', me.onWindowClick, me);
        });

        //Ext.resumeLayouts(true);
    },

    /**
     * 
     * @param {*} e 
     */
    collapseIf: function (e) {
        var me = this;
        //if the click is on the color picker
        if (e.within(me.colorpicker.el, false, true)) {
            return;
        } else {
            var record = e.record;
            if (!record || !e.record.entityName.includes('LegendModel')) {
                me.collapseColorPicker()
            }
        }
    },

    /**
     * 
     */
    collapseColorPicker: function () {
        var me = this;

        me.colorpicker.hide();
        me.isColorPickerExpanded = false;

        me.colorpicker.mun(Ext.getWin(), 'click', me.onWindowClick, me);
        // me.colorpicker.destroy();
        // me.colorpicker = null;
    },

    /**
     * @param {HTMLElement} tr the cell clicked
     */
    alignColorPicker: function (tr) {
        var me = this,
            colorpicker;

        if (me.rendered && !me.destroyed) {
            colorpicker = me.getColorPicker();

            if (colorpicker.isVisible() && colorpicker.isFloating()) {
                me.doAlignColorPicker(tr);
            }
        }
    },

    privates: {

        /**
         * @param {HTMLElement} tr the cell clicked
         */
        doAlignColorPicker: function (tr) {
            var me = this,
                colorpicker = me.colorpicker;

            colorpicker.el.alignTo(tr, 'tr');
        }
    }
});
Ext.define('CMDBuildUI.view.widgets.customform.Panel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.widgets.customform.PanelController',
        'CMDBuildUI.view.widgets.customform.PanelModel',
        'CMDBuildUI.view.widgets.customform.Utilities'
    ],

    mixins: [
        'CMDBuildUI.view.widgets.Mixin'
    ],

    alias: 'widget.widgets-customform-panel',
    controller: 'widgets-customform-panel',
    viewModel: {
        type: 'widgets-customform-panel'
    },

    layout: "fit",
    /**
     * @cfg {String} theWidget.Layout
     * One of `grid` or `form`.
     */

    /**
     * @cfg {String} theWidget.RefreshBehaviour
     * One of `everyTime` or `firstTime`.
     * If value is `everyTime` the content is refreshed every time the widget is opened 
     * following the modification of a configuration parameter.
     * If value is `firstTime` the content is refreshed only when the widget is opened 
     * the first time.
     * Default value is `everyTime`.
     */

    /**
     * @cfg {String} theWidget.ReadOnly
     * If `true` disable all functionalities and makes the data only readable. 
     * Default value is `false`.
     */

    /**
     * @cfg {String} theWidget.AddDisabled
     * If `true` disable add functionality. 
     * Default value is `false`.
     */

    /**
     * @cfg {String} theWidget.CloneDisabled
     * If `true` disable clone functionality. 
     * Default value is `false`.
     */

    /**
     * @cfg {String} theWidget.DeleteDisabled
     * If `true` disable delete functionality. 
     * Default value is `false`.
     */

    /**
     * @cfg {String} theWidget.ExportDisabled
     * If `true` disable export functionality. 
     * Default value is `false`.
     */

    /**
     * @cfg {String} theWidget.ImportDisabled
     * If `true` disable import functionality. 
     * Default value is `false`.
     */

    /**
     * @cfg {String} theWidget.ModifyDisabled
     * If `true` disable modify functionality. 
     * Default value is `false`.
     */

    /**
     * @cfg {String} theWidget.ModelType
     * One of `form`, `class` or `function`.
     */

    /**
     * @cfg {String} theWidget.ClassModel
     * Name of the class from which take the attributes.
     */

    /**
     * @cfg {String} theWidget.ClassAttributes
     * List of attributes to be considered separated by commas.
     * Empty or `null` indicates all attributes.
     */

    /**
     * @cfg {String} theWidget.FormModel
     * A list of attributes definition.
     */

    /**
     * @cfg {String} theWidget.DataType
     * One of `raw`, `raw_json`, `raw_text` or `function`.
     */

    /**
     * @cfg {String} theWidget.FunctionData
     * The function name from wich get the data.
     */

    /**
     * @cfg {String} theWidget.SerializationType
     * One of `json` or `text`.
     * Default value is `text`.
     */

    /**
     * @cfg {String} theWidget.KeyValueSeparator
     * The string to use to separate key from value.
     * Default value is `=`.
     */

    /**
     * @cfg {String} theWidget.AttributesSeparator
     * The string to use to separate attributes.
     * Default value is `,`.
     */

    /**
     * @cfg {String} theWidget.RowsSeparator
     * The string to use to separate rows.
     * Default value is `\n`.
     */

    fbar: [{
        xtype: 'button',
        ui: 'secondary-action',
        reference: 'closebtn',
        itemId: 'closebtn',
        text: CMDBuildUI.locales.Locales.common.actions.close,
        iconCls: 'x-fa fa-check',
        bind: {
            text: '{translations.close}'
        },
        autoEl: {
            'data-testid': 'widgets-customform-close'
        }
    }],

    /**
     * Return the name of the model used by the widget.
     * @return {String}
     */
    getModelName: function () {
        return 'CMDBuildUI.model.customform.' + this.getWidgetId();
    }

});
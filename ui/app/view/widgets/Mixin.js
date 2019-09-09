Ext.define('CMDBuildUI.view.widgets.Mixin', {
    mixinId: 'cmdbuildwidgets-mixin',

    mixins: ['Ext.mixin.Bindable'],

    config: {
        /**
         * @cfg {String} [widgetId] The id of the widget.
         */
        widgetId: null,

        /**
         * @cfg {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} [target] 
         * The target object on which the widget is called.
         */
        target: null,

        /**
         * @cfg {String} [output] 
         * The target attribute where output will be saved.
         */
        output: null
    },

    publishes: [
        "target"
    ],

    twoWayBindable: [
        "target"
    ]

    /**
     * @cfg {CMDBuildUI.model.WidgetDefinition} viewModel.theWidget 
     * Widget definition.
     */

    /**
     * @cfg {CMDBuildUI.model.base.Base} viewModel.theTarget 
     * The target object on which the widget is called.
     */

    /**
     * @property {Ext.Component} _widgetOwner
     * The owner component of this widget.
     */

    /**
     * @event popupclose
     * Fired to close popup.
     * 
     * @param {Object} eOpts
     */

});
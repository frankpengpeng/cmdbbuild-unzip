
Ext.define('CMDBuildUI.view.widgets.Launchers', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.widgets.LaunchersController',
        'CMDBuildUI.view.widgets.LaunchersModel'
    ],

    alias: 'widget.widgets-launchers',
    controller: 'widgets-launchers',
    viewModel: {
        type: 'widgets-launchers'
    },

    cls: 'widgets-launchers-container',

    config: {
        /**
         * @cfg {Ext.data.Store} [widgets] 
         * A store of {CMDBuildUI.model.WidgetDefinition} instances.
         */
        widgets: null,

        /**
         * @cfg {String} [formmode] 
         * Possible values are `read`, `update` and `create`.
         * Default value is `read`.
         */
        formMode: 'read'
    },
    publishes: [
        'widgets',
        'formMode'
    ],

    layout: {
        type: 'vbox',
        align: 'stretch'  //stretch vertically to parent
    },

    hidden: true,

    bind: {
        hidden: '{hideLaunchersPanel}'
    },

    /**
     * @event widgetschanged
     * Fires when widgets property is changed.
     *
     * @param {Ext.Component} this
     * @param {Ext.data.Store} newvalue
     * @param {Ext.data.Store} oldvalue
     * @param {Object} eOpts
     */

    /**
     * @event widgetbuttonclick
     * Fires when widget button is clicked.
     *
     * @param {Ext.Component} this
     * @param {Ext.button.Button} button
     * @param {CMDBuildUI.model.WidgetDefinition} widget
     * @param {Event} e
     * @param {Object} eOpts
     */

    /**
     * @param {Ext.data.Store} newvalue
     * @param {Ext.data.Store} oldvalue
     */
    updateWidgets: function (newvalue, oldvalue) {
        this.fireEvent("widgetschanged", this, newvalue, oldvalue);
    }

});

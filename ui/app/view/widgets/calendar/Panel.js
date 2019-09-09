
Ext.define('CMDBuildUI.view.widgets.calendar.Panel',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.widgets.calendar.PanelController',
        'CMDBuildUI.view.widgets.calendar.PanelModel'
    ],

    mixins: [
        'CMDBuildUI.view.widgets.Mixin'
    ],

    alias: 'widget.widgets-calendar-panel',
    controller: 'widgets-calendar-panel',
    viewModel: {
        type: 'widgets-calendar-panel'
    },
    layout: "fit"

    /**
     * @cfg {String} theWidget.EventClass 
     * Class name or process name to get data.
     */

    /**
     * @cfg {String} theWidget.Filter 
     * The filter to apply to the list request.
     */

    /**
     * @cfg {String} theWidget.StartDate
     * The field to use as event start date. 
     */

    /**
     * @cfg {String} theWidget.EndDate
     * The field to use as event end date. 
     */

    /**
     * @cfg {String} theWidget.EventTitle
     * The field to use as event title. 
     */

    /**
     * @cfg {String} theWidget.EventType
     * The field to use as event type. 
     */

    /**
     * @cfg {String} theWidget.EventTypeLookup
     * The LookUp type to use as event type info. 
     */

    /**
     * @cfg {String} theWidget.DefaultDate
     * The field in current class/process to use as opening date in calendar. 
     */
});

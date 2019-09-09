Ext.define('CMDBuildUI.view.widgets.calendar.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-calendar-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.widgets.calendar.Panel} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel();
        var widget = vm.get("theWidget");
        var sourceTypeName = widget.get("ClassName");
        sourceTypeName = sourceTypeName.replace(/"/g,'');
        var sourceType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(sourceTypeName);
        var target = vm.get("theTarget");

        view.add({
            xtype: 'ux-calendar',
            dataSourceType: sourceType,
            dataSourceTypeName: sourceTypeName,
            dataSourceFilter: widget.get("Filter") ? widget.get("_Filter_ecql") : null,
            targetObject: target,
            eventStartDateAttribute: widget.get("EventStartDate"),
            eventEndDateAttribute: widget.get("EventEndDate"),
            eventTitleAttribute: widget.get("EventTitle"),
            eventTypeAttribute: widget.get("EventType"),
            eventTypeLookup: widget.get("EventTypeLookup"),
            openingDate: widget.get("DefaultDate") ? target.get(widget.get("DefaultDate")) : null
        });
    }
});

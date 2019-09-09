Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.DefaultOrdersFieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabitems-properties-fieldsets-defaultordersfieldset',

    mixins: ['CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin'],
    /**
     * 
     * @param {*} view 
     * @param {*} rowIndex 
     * @param {*} colIndex 
     */
    onAddNewDefaultOrderBtn: function (view, rowIndex, colIndex) {
        var attribute = view.lookupReferenceHolder().lookupReference("defaultOrderAttribute");
        var direction = view.lookupReferenceHolder().lookupReference("defaultOrderDirection");
        var orderGrid = view.lookupReferenceHolder().lookupReference("defaultOrderGrid");
        var orderStore = orderGrid.getStore();
        var newRecordStore = view.getStore();
        if (attribute.getValue() && direction.getValue()) {
            Ext.suspendLayouts();
            orderStore.add(CMDBuildUI.model.AttributeOrder.create({
                attribute: attribute.getValue(),
                direction: direction.getValue()
            }));
            attribute.getStore().remove(attribute.selection);
            newRecordStore.removeAll();
            newRecordStore.add(CMDBuildUI.model.AttributeOrder.create());
            orderGrid.getView().refresh();
            Ext.resumeLayouts();
        }
    }

});
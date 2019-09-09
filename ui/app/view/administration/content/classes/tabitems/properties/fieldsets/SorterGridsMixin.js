Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin', {
   
    mixinId: 'administrationroutes-sortergrids',

    moveUp: function (grid, rowIndex, colIndex) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        var record = store.getAt(rowIndex);
        rowIndex--;
        if (!record || rowIndex < 0) {
            return;
        }

        store.remove(record);
        store.insert(rowIndex, record);

        grid.refresh();
        Ext.resumeLayouts();
    } ,
    moveDown: function (grid, rowIndex, colIndex) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        var record = store.getAt(rowIndex);
        rowIndex++;
        if (!record || rowIndex >= store.getCount()) {
            return;
        }
        store.remove(record);
        store.insert(rowIndex, record);

        grid.refresh();
        Ext.resumeLayouts();
    },
    deleteRow: function (grid, rowIndex, colIndex) {
        var store = grid.getStore();
        var record = store.getAt(rowIndex);
        store.remove(record);
        grid.updateLayout();
    }
});
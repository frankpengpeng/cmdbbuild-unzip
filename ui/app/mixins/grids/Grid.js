Ext.define('CMDBuildUI.mixins.grids.Grid', {
    mixinId: 'grids-grid-mixin',

    /**
     * 
     * @param {CMDBuildUI.store.classes.Cards} store 
     * @param {Ext.model.Model} record
     */
    expandRowAfterLoadWithPosition: function (store, newid) {
        var me = this;
        var metadata = store.getProxy().getReader().metaData;
        var posinfo = (metadata && metadata.positions) && metadata.positions[newid] || { positionInPage: 0 };
        if (!posinfo.pageOffset) {
            Ext.asap(function () {
                me.expandSelection(posinfo.positionInPage, store, newid);
            });
        } else {
            me.ensureVisible(posinfo.positionInTable, {
                callback: function () {
                    me.expandSelection(posinfo.positionInTable, store, newid);
                }
            });
        }
    },

    /**
     * Expands selected row, if selection is present
     * 
     * @param {Integer} index Row index
     * @param {Ext.model.Model} record
     * 
     */
    expandSelection: function (index, store, newid) {
        var expander = this.getPlugin("forminrowwidget");
        var record = store.getById(newid);
        if (expander) {
            if (index !== -1) {
                expander.toggleRow(index, record);
            } else {
                CMDBuildUI.util.Notifier.showWarningMessage(
                    "Card not found"
                );
            }
        }
    },

    /**
     * @return {Boolean}
     */
    isMultiSelectionEnabled: function () {
        return this.getSelectionModel().getSelectionMode() === 'MULTI';
    },

    /**
     * 
     * @param {CMDBuild.model.Base} record 
     */
    updateRowWithExpader: function(record) {
        var store = this.getStore();
        var storerecord = store.getById(record.getId());
        if (storerecord) {
            // get expander
            var expander = this.getPlugin("forminrowwidget");
            // get row
            var tableview = this.getView();
            var rownode = tableview.getNode(storerecord);
            var normalRow = Ext.fly(rownode);
            // get index
            var storeindex = store.indexOf(storerecord);

            // collapse row if expanded
            if (expander && !normalRow.hasCls(expander.rowCollapsedCls) && storeindex !== -1) {
                expander.toggleRow(storeindex, storerecord);
            }

            // update data
            var newdata = record.getData();
            storerecord.updateDataFromObject(newdata);
            tableview.refreshNode(storerecord);

            // expand
            if (expander && storeindex !== -1) {
                expander.toggleRow(storeindex, storerecord);
            }
        }
    }
});
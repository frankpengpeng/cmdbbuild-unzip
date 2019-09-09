Ext.define('CMDBuildUI.view.attachments.history.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.attachments-history-grid',
    control: {
        '#': {
            rowdblclick: 'onRowDblclick'
        },
        'tableview': {
            actiondownload: 'onActionDownload'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.attachments.Grid} view 
     * @param {Ext.data.Model} record 
     * @param {HTMLElement} element 
     * @param {Number} rowIndex 
     * @param {Ext.event.Event} event 
     * @param {Object} eOpts 
     */
    onRowDblclick: function (view, record, element, rowIndex, event, eOpts) {
        this.onActionDownload(view, record, rowIndex);
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionDownload: function (grid, record, rowIndex, colIndex) {

        var url = Ext.String.format(
            "{0}/{1}/{2}?CMDBuild-Authorization={3}",
            grid.up().getViewModel().get('storeUrl'), // base url 
            record.getId(), // attachment id
            record.get("name"), // file name 
            CMDBuildUI.util.helper.SessionHelper.getToken() // session token
        );
        // open the url in new tab
        window.open(url, "_blank");
    }

});
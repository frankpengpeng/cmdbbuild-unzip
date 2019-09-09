Ext.define('CMDBuildUI.view.attachments.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.attachments-grid',

    control: {
        '#': {
            rowdblclick: 'onRowDblclick'
        },
        'tableview': {
            actiondownload: 'onActionDownload',
            actionedit: 'onActionEdit',
            actiondelete: 'onActionDelete',
            actionhistory: 'onActionHistory'
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
            grid.getStore().getProxy().getUrl(), // base url 
            record.getId(), // attachment id
            record.get("name"), // file name 
            CMDBuildUI.util.helper.SessionHelper.getToken() // session tocken
        );
        // open the url in new tab
        window.open(url, "_blank");
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionEdit: function (grid, record, rowIndex, colIndex) {
        var vm = this.getViewModel();
        var url = Ext.String.format('{0}/{1}', this.getViewModel().get("attachments").getProxy().getUrl(), record.getId());

        // atachments form definition
        var config = {
            xtype: 'attachments-form',
            viewModel: {
                data: {
                    newAttachment: false,
                    url: url,
                    theAttachment: record,
                    targetTypeObject: CMDBuildUI.util.helper.ModelHelper.getObjectFromName(vm.get("objectTypeName"), vm.get("objectType"))
                }
            }
        };

        // custom panel listeners
        var listeners = {
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                vm.get("attachments").load();
            }
        };
        // create panel
        CMDBuildUI.util.Utilities.openPopup(
            'popup-edit-attachment',
            CMDBuildUI.locales.Locales.attachments.editattachment,
            config,
            listeners
        );
    },
    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionDelete: function (grid, record, rowIndex, colIndex) {
        var vm = this.getViewModel();
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.attachments.deleteattachment,
            CMDBuildUI.locales.Locales.attachments.deleteattachment_confirmation,
            function (action) {
                if (action === "yes") {
                    record.getProxy().setUrl(vm.get("attachments").getProxy().getUrl());
                    CMDBuildUI.util.Ajax.setActionId('attachment.delete');
                    record.erase();
                }
            }
        );
    },

    /**
     * @param {CMDBuildUI.view.attachments.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionHistory: function (grid, record, rowIndex, colIndex) {
        var content = {
            xtype: 'attachments-history-grid',
            scrollable: 'y',
            viewModel: {
                data: {
                    objectTypeName: grid.up('grid').getViewModel().get('objectTypeName'),
                    objectType: grid.up('grid').getViewModel().get('objectType'),
                    objectId: grid.up('grid').getViewModel().get('objectId'),
                    attachmentId: record.get('_id'),
                    storeUrl: grid.getStore().getProxy().getUrl()
                }

            }
        };
        // custom panel listeners
        var title = CMDBuildUI.locales.Locales.attachments.attachmenthistory;
        var popUp = CMDBuildUI.util.Utilities.openPopup(null, title, content, {});
    }

});
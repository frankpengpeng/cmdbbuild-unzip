Ext.define('CMDBuildUI.view.administration.content.tasks.GridController', {
    extend: 'Ext.app.ViewController',
    mixins: ['CMDBuildUI.view.administration.content.tasks.card.CardMixin'],
    alias: 'controller.administration-content-tasks-grid',

    control: {
        '#': {
            sortchange: 'onSortChange',
            // afterrender: 'onAfterRender'
            afterrender: 'onAfterRender',
            deselect: 'onDeselect',
            select: 'onSelect',
            rowdblclick: 'onRowDblclick'

        }
    },

    onAfterRender: function (view) {
        var vm = view.up('administration-content-tasks-view').getViewModel();
        vm.getStore("allImportExportTemplates").load();
        Ext.getStore('emails.Accounts').load();
        Ext.getStore('emails.Templates').load();
        view.getSelectionModel().excludeToggleOnColumn = 5;
    },
    onRunBtnClick: function (grid, rowIndex, colIndex, tool, event, record) {
        CMDBuildUI.util.administration.helper.AjaxHelper.runJob(record).then(function (response) {
            CMDBuildUI.util.Notifier.showMessage(
                Ext.String.format('Task {0} executed.', record.get('name')), {
                    ui: 'administration',
                    icon: CMDBuildUI.util.Notifier.icons.success
                }
            );

        });

    },
    onStartStopBtnClick: function (grid, rowIndex, colIndex, tool, event, record) {

        
        var formInRow = grid.grid.getPlugin('administration-forminrowwidget').view;
        
        record.set('enabled', !record.get('enabled'));

        record.set('config', record._config.getData());
        record.save({
            success: function (record, operation) {
                grid.refresh();
                formInRow.fireEventArgs('itemupdated', [grid.grid, record, this]);
            }
        });
        event.preventDefault();
        return false;
    },
    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onDeselect: function (row, record, index, eOpts) {

    },

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onSelect: function (row, record, index, eOpts) {
        this.getView().setSelection(record);
        Ext.GlobalEvents.fireEventArgs('selecteduser', [record]);
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    // onNewBtnClick: function (item, event, eOpts) {
    //     var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
    //     container.removeAll();
    //     container.add({
    //         xtype: 'administration-content-tasks-card',
    //         viewModel: {
    //             links: {
    //                 theTask: {
    //                     type: 'CMDBuildUI.model.importexports.Template',
    //                     create: true
    //                 }
    //             }
    //         }
    //     });
    // },

    // onImportExportTemplateUpdated: function (form, record) {
    //     var view = this.getView();
    //     view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [this.getView(), record, this]);
    // },

    // onImportExportTemplateCreated: function (record) {
    //     var view = this.getView();

    //     var store = view.getStore();
    //     store.load();

    //     store.on('load', function (records, operation, success) {
    //         view.getView().refresh();
    //         Ext.asap(function () {
    //             var store = view.getStore();
    //             var index = store.findExact("_id", record.getId());
    //             var storeItem = store.getAt(index);

    //             view.getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, storeItem, index]);
    //         });
    //     });
    // },

    onSortChange: function () {
        var currentSelected = this.view.getSelection() && this.view.getSelection()[0];
        this.view.getPlugin('administration-forminrowwidget').removeAllExpanded();

        if (currentSelected) {
            var store = this.view.getStore();
            var index = store.findExact("_id", currentSelected.get('_id'));
            var record = store.getAt(index);
            this.view.getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [this.getView(), record, index]);
        }
    }

});
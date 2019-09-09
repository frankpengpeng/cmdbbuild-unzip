Ext.define('CMDBuildUI.view.administration.content.tasks.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-tasks-topbar',

    control: {
        '#adduser': {
            click: 'onNewBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (item, event, eOpts) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        var type = this.getViewModel().get('type');

        switch (type) {
            case 'import_export':
                type = 'import_file';
                break;

            default:
                break;
        }        
        var cardVm = {
            links: {
                theTask: {
                    type: item.lookupViewModel().get('taskModelName'),
                    create: {
                        type: type,
                        config: {
                            type: type
                        }
                    }
                }
            },
            data: {
                workflowClassName: item.lookupViewModel().get('objectTypeName'),
                taskType: this.getViewModel().get('type'),
                grid: item.up('administration-content-tasks-view').down('administration-content-tasks-grid'),
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                actions: {
                    view: false,
                    edit: false,
                    add: true
                }
            }
        };
        if (item.lookupViewModel().get('objectTypeName')) {
            cardVm.links.theTask.create.config.classname = item.lookupViewModel().get('objectTypeName');
            cardVm.data.comeFromClass = item.lookupViewModel().get('objectTypeName');
        }
        container.add({
            xtype: 'administration-content-tasks-card',
            viewModel: cardVm
        });
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.event.Event} event
     */
    onSearchSubmit: function (field, event) {
        var grid = this.getView().up().down('grid');
        var store = grid.getStore();

        var formInRow = grid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded();

        // removeAllExpanded
        store.clearFilter();
        CMDBuildUI.util.administration.helper.GridHelper.searchMoreFields(store, field.getValue());
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        // clear store filter
        var store = this.getView().up().down('grid').getStore();
        if (store) {
            store.clearFilter();
        }
        // reset input
        field.reset();
    }
});

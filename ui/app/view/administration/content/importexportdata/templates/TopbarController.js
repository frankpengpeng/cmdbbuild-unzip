Ext.define('CMDBuildUI.view.administration.content.importexportdata.templates.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-importexportdata-templates-topbar',

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
        var targetName = item.up('administration-content-importexportdata-templates-view').getViewModel().get('targetName');
        container.removeAll();        
        var disabledTargetType, disabledTargetTypeName;
        if (this.getView().up('tabpanel')) {
            disabledTargetType = true;
            disabledTargetTypeName = true;
        }
        container.add({
            xtype: 'administration-content-importexportdata-templates-card',
            viewModel: {
                links: {
                    theImportExportTemplate: {
                        type: 'CMDBuildUI.model.importexports.Template',
                        create: {
                            targetType: CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(targetName),
                            targetName: targetName
                        }
                    }
                },
                data: {
                    disabledTargetType: disabledTargetType,
                    disabledTargetTypeName: disabledTargetTypeName,
                    grid: item.up('administration-content-importexportdata-templates-view').down('administration-content-importexportdata-templates-grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    targetType: CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(targetName),
                    targetName: targetName,
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                }
            }
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

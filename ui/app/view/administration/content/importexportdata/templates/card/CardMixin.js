Ext.define('CMDBuildUI.view.administration.content.importexportdata.templates.card.CardMixin', {

    mixinId: 'administration-importexportmixin',

    onEditBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = view.getViewModel();
        var viewModel = {
            data: {
                theImportExportTemplate: view.getViewModel().get('selected') || view.getViewModel().get('theImportExportTemplate'),
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                actions: {
                    edit: true,
                    view: false,
                    add: false
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-importexportdata-templates-card',
            viewModel: viewModel
        });
    },

    onDeleteBtnClick: function (button) {
        var me = this;
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    var grid = button.up('administration-content-importexportdata-templates-grid');
                    CMDBuildUI.util.Ajax.setActionId('delete-importexporttemplate');
                    grid.getStore().remove(me.getViewModel().getData().theImportExportTemplate);
                    grid.getStore().sync();
                }
            }, this);
    },


    onViewBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = view.getViewModel();
        var viewModel = {
            data: {
                theImportExportTemplate: view.getViewModel().get('selected') || view.getViewModel().get('theImportExportTemplate'),
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                actions: {
                    edit: false,
                    view: true,
                    add: false
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-importexportdata-templates-card',
            viewModel: viewModel
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onActiveToggleBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theImportExportTemplate = vm.get('theImportExportTemplate');
        theImportExportTemplate.set('active', !theImportExportTemplate.get('active'));
        this.setColumnsData();
        theImportExportTemplate.save({
            success: function (record, operation) {
                view.up('administration-content-importexportdata-templates-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [view.up('administration-content-importexportdata-templates-grid'), record, this]);

            }
        });

    },

    onCloneBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var newImportExportTemplate = vm.get('theImportExportTemplate').copyForClone();
        var viewModel = {
            data: {
                theImportExportTemplate: newImportExportTemplate,
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                actions: {
                    edit: false,
                    view: false,
                    add: true
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-importexportdata-templates-card',
            viewModel: viewModel
        });
    },

    /**
     * 
     * @param {*} row 
     * @param {*} record 
     * @param {*} element 
     * @param {*} rowIndex 
     * @param {*} e 
     * @param {*} eOpts 
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var view = this.getView(),
            container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var formInRow = view.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        view.setSelection(record);

        container.removeAll();
        container.add({
            xtype: 'administration-content-importexportdata-templates-card',
            viewModel: {
                data: {
                    theImportExportTemplate: record,
                    grid: view.ownerGrid,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                }
            }
        });
    },
    privates: {
        setColumnsData: function () {
            var vm = this.getViewModel();
            var theImportExportTemplate = vm.get('theImportExportTemplate');
            var columns = [];

            Ext.Array.forEach(vm.get('allSelectedAttributesStore').getRange(), function (item) {
                columns.push(item.getData());
            });
            theImportExportTemplate.set('columns', columns);
        }
    }
});
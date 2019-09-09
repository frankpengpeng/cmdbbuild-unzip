Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ToolsMixin', {
    mixinId: 'administration-lookupvalue-tools-mixin',


    onEditBtnClick: function () {
        var view = this.getView();
        var vm = this.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var objecttype = vm.get('theValue._type');
        var grid = vm.get('grid') || view.up();
        var valueName = vm.get('theValue.description');
        var valueId = vm.get('theValue._id');
        container.removeAll();
        container.add({
            xtype: 'administration-content-lookuptypes-tabitems-values-card-edit',
            viewModel: {
                data: {
                    lookupTypeName: objecttype,
                    valueId: valueId,
                    values: grid.getStore().getRange(),
                    title: objecttype + ' - ' + 'Values' + ' - ' + valueName,
                    grid: grid
                }
            }
        });
    },

    onDeleteBtnClick: function () {
        var vm = this.getViewModel();
        var detailsWindow = this.getView().up('#CMDBuildAdministrationDetailsWindow');

        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {

                    CMDBuildUI.util.Ajax.setActionId('delete-lookupvalue-card');

                    var theObject = vm.getData().theValue;
                    theObject.erase({
                        failure: function () {
                            vm.get('theValue').reject();
                        },
                        success: function (record, operation) {
                            Ext.ComponentQuery.query('administration-content-lookuptypes-tabitems-values-grid-grid')[0].getStore().reload();
                            if(detailsWindow){
                                detailsWindow.fireEvent("closed");
                            }
                        }
                    });
                }
            },
            this
        );
    },

    /**
     * 
     */
    onOpenBtnClick: function () {
        var view = this.getView();
        var vm = this.getViewModel();

        var objecttype = vm.get('theValue').get('_type');
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-lookuptypes-tabitems-values-card-view',
            viewModel: {
                data: {
                    lookupTypeName: objecttype,
                    valueId: vm.get('theValue').getId(),
                    values: view.up().getStore().getRange(),
                    title: objecttype + ' - ' + 'Values' + ' - ' + vm.get('theValue').get('name'),
                    grid: view.up()
                }
            }
        });
    },

    onActiveToggleBtnClick: function () {
        var me = this;
        var view = me.getView();
        var vm = view.getViewModel();
        var theValue = vm.get('theValue');
        var grid = Ext.ComponentQuery.query('administration-content-lookuptypes-tabitems-values-grid-grid')[0];
        CMDBuildUI.util.Ajax.setActionId('toggleactive-lookupvalue');
        theValue.set('active', !theValue.get('active'));
        theValue.save({
            success: function (record, operation) {
                grid.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [grid, record, me]);
            }
        });
    }


});
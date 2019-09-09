Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.ContextMenusFieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabitems-properties-fieldsets-contextmenusfieldset',

    mixins: ['CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin'],
    /**
     * 
     * @param {*} view 
     * @param {*} rowIndex 
     * @param {*} colIndex 
     */
    onAddNewContextMenuBtn: function (view, rowIndex, colIndex) {

        var vm = this.getViewModel();
        var contexstMenus = vm.get('theObject.contextMenuItems');
        var newContextMenuStore = vm.get('contextMenuItemsStoreNew');
        var newContextMenu = newContextMenuStore.getData().items[0];

        Ext.suspendLayouts();
        contexstMenus.add(newContextMenu);        
        vm.set('contextMenuCountManager', contexstMenus.getData().length);
        newContextMenuStore.removeAll();
        newContextMenuStore.add(CMDBuildUI.model.ContextMenuItem.create());
        view.refresh();
        this.lookupReference('contextMenuGrid').view.grid.getView().refresh();
        window.newContextMenuScriptField.getSession().setValue('');
        Ext.resumeLayouts();

    },
    onEditBtn: function (view, rowIndex, colIndex) {
        var vm = this.getViewModel();
        var grid = this.lookupReference('contextMenuGrid');
        var theContext = grid.getStore().getAt(rowIndex);
        vm.set('theContext', theContext);


        var formFields = [{
            xtype: 'container',
            padding: '0 10 0 10',
            viewModel: {},

            items: [{
                xtype: 'combobox',
                inputField: 'visibility',                
                labelAlign: 'top',
                editable: false,
                forceSelection: true,
                allowBlank: false,
                fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.applicability, // Applicability
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.applicability'
                },
                displayField: 'label',
                valueField: 'value',
                value: view.grid.getStore().getAt(rowIndex).get('applicability'),
                store: 'administration.common.Applicability',
                bind: {
                    value: '{theContext.visibility}'
                }
            }, {
                /********************* Active **********************/
                xtype: 'checkbox',
                labelAlign: 'top',
                bind: '{theContext.active}',
                value: view.grid.getStore().getAt(rowIndex).get('active'),
                boxLabel: CMDBuildUI.locales.Locales.administration.common.labels.active, // Active
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                }
            }]
        }];

        var popup = CMDBuildUI.util.administration.helper.AcePopupHelper.getPopup('theContext', theContext, 'script', formFields, 'popup-edit-contextmenu', CMDBuildUI.locales.Locales.administration.classes.strings.editcontextmenu);
    }

});
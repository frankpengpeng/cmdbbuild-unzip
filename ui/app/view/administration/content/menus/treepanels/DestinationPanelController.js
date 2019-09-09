Ext.define('CMDBuildUI.view.administration.content.menus.treepanels.DestinationPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-menus-treepanels-destinationpanel',

    controls: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },
    /**
     * 
     * @param {*} view 
     */
    onBeforeRender: function (view) {

    },

    /**
     * 
     * @param {*} node 
     * @param {*} data 
     * @param {*} overModel 
     * @param {*} dropPosition 
     * @param {*} dropHandlers 
     */
    onBeforeDrop: function (node, data, overModel, dropPosition, dropHandlers) {
        dropHandlers.wait = true;
        if (this.getView().up('administration-content-menu-view').getViewModel().get('actions.view')) {
            dropHandlers.cancelDrop();
        } else {
            dropHandlers.processDrop();
        }
    },
    /**
     * 
     * @param {*} store 
     * @param {*} records 
     * @param {*} successful 
     * @param {*} operation 
     * @param {*} model 
     * @param {*} eOpts 
     */
    onGetCurrentMenuStoreLoad: function (store, records, successful, operation, model, eOpts) {

    },
    /**
     * 
     * @param {*} node 
     * @param {*} oldParent 
     * @param {*} newParent 
     * @param {*} index 
     * @param {*} eOpts 
     */
    onGetCurrentMenuStoreNodeMove: function (node, oldParent, newParent, index, eOpts) {
        var children = oldParent.get('children');
        if (children) {
            var oldIndex = children
                .findIndex(function (item) {
                    return item.id === node.getData().id;
                });
            oldParent.get('children').splice(oldIndex, 1);
            if (newParent) {
                var data = node.getData();
                if (data.menutype === 'report') {
                    data.menutype = 'reportpdf';
                    data.objecttype = '_Report';
                } else if (data.menutype === 'dashboard') {
                    data.objecttype = '_Dashboards';
                }
                if (newParent.get('children')) {
                    newParent.get('children')[data.index] = CMDBuildUI.model.menu.MenuItem.create(data);
                }
            }
        }
    },

    /**
     * On translate button click
     * @param {*} view 
     * @param {*} rowIndex 
     * @param {*} colIndex 
     * @param {*} item 
     * @param {*} e 
     */
    onTranslateClick: function (button, rowIndex, colIndex, item, e) {
        var menuId = button.getStore().getAt(rowIndex).getId();
        var content = {
            xtype: 'administration-localization-localizecontent',
            scrollable: 'y',
            viewModel: {
                data: {
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    translationCode: CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfMenuItemDescription(menuId)
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
                CMDBuildUI.util.Utilities.closePopup('popup-edit-menu-item-localization');
            }
        };
        // create panel
        var popUp = CMDBuildUI.util.Utilities.openPopup(
            'popup-edit-menu-item-localization',
            CMDBuildUI.locales.Locales.administration.common.strings.localization,
            content,
            listeners, {
                ui: 'administration-actionpanel',
                width: '450px',
                height: '450px'
            }
        );
    }
});
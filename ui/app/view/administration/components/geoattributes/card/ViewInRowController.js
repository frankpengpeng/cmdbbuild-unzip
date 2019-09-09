Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-geoattributes-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }

    },

    onBeforeRender: function (view) {
        var vm = view.getViewModel();
        if (view && view._rowContext) {
            vm.set('theGeoAttribute', view._rowContext.record);
        }
    },

    onEditBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        CMDBuildUI.model.map.GeoAttribute.setProxy({
            url: view.up().grid.getViewModel().get('storedata.url'),
            type: 'baseproxy'
        });
        container.add({
            xtype: 'administration-components-geoattributes-card-form',
            viewModel: {
                links: {
                    theGeoAttribute: {
                        type: 'CMDBuildUI.model.map.GeoAttribute',
                        id: vm.get('theGeoAttribute._id')
                    }
                },

                data: {
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    },
                    grid: this.getView().up()
                }
            }
        });
    },
    /**
     * this is needed but unused
     * @param {*} store 
     */
    onTreeStoreDataChanged: function (store) { },

    onCloneBtnClick: function (button, event, eOpts) {
        CMDBuildUI.util.Logger.log("Not implemented", CMDBuildUI.util.Logger.levels.log);
    },

    onOpenBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        CMDBuildUI.model.map.GeoAttribute.setProxy({
            url: view.up().grid.getViewModel().get('storedata.url'),
            type: 'baseproxy'
        });

        container.add({
            xtype: 'administration-components-geoattributes-card-form',
            viewModel: {
                links: {
                    theGeoAttribute: {
                        type: 'CMDBuildUI.model.map.GeoAttribute',
                        id: vm.get('theGeoAttribute._id')
                    }
                },

                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    },
                    grid: this.getView().up()
                }
            }
        });
    },

    onDeleteBtnClick: function () {
        var me = this;
        var view = me.getView();
        var vm = me.getViewModel();
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText.toLowerCase() === 'yes') {
                    CMDBuildUI.util.Ajax.setActionId('delete-geoattribute');
                    CMDBuildUI.model.map.GeoAttribute.setProxy({
                        url: view.up().grid.getViewModel().get('storedata.url'),
                        type: 'baseproxy'
                    });
                    vm.get('theGeoAttribute').erase({
                        success: function (record, operation) {
                        },
                        failure: function () {
                            vm.get('theGeoAttribute').reject();                            
                        }
                    });
                }
            }, this);
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theGeoAttribute = vm.get('theGeoAttribute');
        theGeoAttribute.set('active', !theGeoAttribute.get('active'));

        theGeoAttribute.save({
            success: function (record, operation) {
                view.up('administration-components-geoattributes-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [null, record, this]);

                // view.up('administration-components-geoattributes-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, record.getStore(), vm.get('recordIndex')]);  
            },
            failure: function (record, reason) {
                record.reject();
                view.up('administration-components-geoattributes-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, record, vm.get('recordIndex')]);
                view.up('administration-components-geoattributes-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, record, vm.get('recordIndex')]);
            }
        });

    }

});
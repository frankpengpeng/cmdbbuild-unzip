
Ext.define('CMDBuildUI.view.bim.ContainerBim', {
    extend: 'Ext.container.Container',
    requires: [
        'CMDBuildUI.view.bim.ContainerBimController',
        'CMDBuildUI.view.bim.ContainerBimModel'
    ],

    alias: 'widget.bim-container',
    controller: 'bim-containerbim',
    viewModel: {
        type: 'bim-containerbim'
    },

    layout: 'border',
    items: [{ //TODO: Set a maxWidth
        region: 'west',
        width: '33%',
        split: true,
        collapsible: false,
        xtype: 'tabpanel',
        layout: 'fit',
        ui: 'managementlighttabpanel',
        deferredRender: false,
        items: [{
            xtype: 'bim-tab-cards-tree',
            title: CMDBuildUI.locales.Locales.bim.tree.label,
            localized: {
                title: 'CMDBuildUI.locales.Locales.bim.tree.label'
            }
        }, {
            xtype: 'bim-tab-cards-layers',
            title: CMDBuildUI.locales.Locales.bim.layers.label,
            localized: {
                title: 'CMDBuildUI.locales.Locales.bim.layers.label'
            },
            reference: 'bim-tab-cards-layers'
        }, {
            xtype: 'classes-cards-card-view',
            itemId: 'bim-tab-cards-card',
            title: CMDBuildUI.locales.Locales.bim.card.label,
            localized: {
                title: 'CMDBuildUI.locales.Locales.bim.card.label'
            },
            hideTools: true,
            hideWidgets: true,
            shownInPopup: true,
            disabled: true,
            bind: null
        }]
    }, {
        reference: 'rightPanel',
        xtype: 'panel',
        region: 'center',
        layout: 'fit',
        html: '<div style="height: inherit;width: inherit;"><canvas id="divBim3DView" style="height: inherit;width: inherit;"></canvas></div>', //divBim3DView
        fbar: [{
            xtype: 'toolbar',
            flex: 1,
            items: [{
                xtype: 'tbtext',
                html: CMDBuildUI.locales.Locales.bim.menu.camera + ":"
            }, {
                iconCls: 'cmdbuildicon-default-bim',
                tooltip: CMDBuildUI.locales.Locales.bim.menu.resetView,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.menu.resetView'
                },
                cls: 'management-tool',
                xtype: 'tool',
                handler: function () {
                    CMDBuildUI.util.bim.Viewer.defaultView();
                }
            }, {
                iconCls: 'cmdbuildicon-front-bim',
                tooltip: CMDBuildUI.locales.Locales.bim.menu.frontView,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.menu.frontView'
                },
                cls: 'management-tool',
                xtype: 'tool',
                handler: function () {
                    CMDBuildUI.util.bim.Viewer.frontView();
                }
            }, {
                iconCls: 'cmdbuildicon-side-bim',
                tooltip: CMDBuildUI.locales.Locales.bim.menu.sideView,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.menu.sideView'
                },
                cls: 'management-tool',
                xtype: 'tool',
                handler: function () {
                    CMDBuildUI.util.bim.Viewer.sideView();
                }
            }, {
                iconCls: 'cmdbuildicon-top-bim',
                tooltip: CMDBuildUI.locales.Locales.bim.menu.topView,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.menu.topView'
                },
                cls: 'management-tool',
                xtype: 'tool',
                handler: function () {
                    CMDBuildUI.util.bim.Viewer.topView();
                }
            }, {
                xtype: 'tbseparator'
            }, {
                xtype: 'tbtext',
                html: CMDBuildUI.locales.Locales.bim.menu.mod + ":",
                localized: {
                    html: 'CMDBuildUI.locales.Locales.bim.menu.mod'
                }
            }, {
                iconCls: 'x-fa fa-arrows',
                reference: 'mode',
                xtype: 'tool',
                itemId: 'bim-containerbim-menu-mode',
                mode: 'rotate',
                tooltip: CMDBuildUI.locales.Locales.bim.menu.pan,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.menu.pan'
                },
                cls: 'management-tool'
            }, {
                xtype: 'tbseparator'
            }, {
                xtype: 'tool',
                iconCls: 'cmdbuildicon-orthographic',
                itemId: 'bim-containerbim-menu-camera',
                cameraType: 'perspective',
                tooltip: CMDBuildUI.locales.Locales.bim.menu.orthographic,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.bim.menu.orthographic'
                },
                cls: 'management-tool'
            }, {
                xtype: 'tbfill'
            }]
        }],
        controller: {
            control: {
                '#': {
                    afterrender: 'onAfterRender',
                    resize: 'onResize'
                }
            },
            onAfterRender: function () {
                var pId = this.getView().getBubbleParent().projectId;
                this.getView().getBubbleParent().getController().onDivRendered(pId, 'ifc2x3tc1'); //TODO: make dinamic the ifcType
            },
            /**
             * This function handles the resize of the canvas
             * @param panel the panel
             * @param height the height of the panel
             * @param width the width of the panel 
             */
            onResize: function (panel, height, width) {
                // CMDBuildUI.util.bim.SceneTree.resize(height, width);
            }
        }
    }]
});

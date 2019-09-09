Ext.define('CMDBuildUI.view.bim.ContainerBimController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-containerbim',
    listen: {
        global: {
            highlitedifcobject: 'onHiglitedObject'
        },
        component: {
            '#': {
                resize: function () {
                    CMDBuildUI.util.bimsurfer.util.resize();
                }
            },

            'bim-container panel #bim-containerbim-menu-camera': {
                click: 'cameraHandler'
            },
            'bim-container panel #bim-containerbim-menu-mode': {
                click: 'modeHandler'
            }
        }
    },

    /**
     * @param {Object} highlited
     */
    onHiglitedObject: function (highlighted) {
        var objectId = highlighted.objectId;

        var node = this.getView().down('bim-tab-cards-tree').getStore().findNode('oid', objectId);
        var globalId = node.get('globalId');

        CMDBuildUI.util.bim.Util.getRelatedCard(globalId, function (data) {

            var view = this.getView().down('#bim-tab-cards-card');
            if (data.exists == true) {

                if (view.isDisabled() == true) {
                    view.setDisabled(false);
                }

                this.getViewModel().set('objectId', data.ownerId);
                this.getViewModel().set('objectTypeName', data.ownerType)

            } else {
                var tabpanel = view.up('tabpanel')
                var activeTab = tabpanel.getActiveTab();

                if (activeTab.getItemId() == 'bim-tab-cards-card') {
                    tabpanel.setActiveTab(0);
                    view.setDisabled(true);
                }
            }



        }, this);
    },
    /**
     * @param  {Number} poid the identifier for the bim project
     * @param  {String} type Ifc4 or Ifc2x3tc1
     * @param  {} observer
     */
    onDivRendered: function (poid, type, observer) {
        CMDBuildUI.util.bim.Viewer.show(poid, type);
    },

    /**
     * 
     * @param {Ext.component} tool 
     * @param {Object} e 
     */
    cameraHandler: function (tool, e) {
        var cameraType = tool.cameraType

        switch (cameraType) {
            case 'perspective':
                tool.cameraType = 'orthographic';
                //TODO: use function for changing the camera
                tool.setIconCls('cmdbuildicon-perspective');
                tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.perspective);
                break;
            case 'orthographic':
                tool.cameraType = 'perspective';
                tool.setIconCls('cmdbuildicon-orthographic');
                tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.orthographic);

                //TODO: use the function for changing the camera
                break;
        }

        CMDBuildUI.util.bim.Viewer.setCamera(tool.cameraType);
    },

    /**
     * 
     * @param {Ext.component} tool 
     * @param {Object} e 
     */
    modeHandler: function (tool, e) {
        var mode = tool.mode;

        switch (mode) {
            case 'pan':
                tool.mode = 'rotate';
                tool.setIconCls('x-fa fa-arrows');
                tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.pan)
                break;
            case 'rotate':
                tool.mode = 'pan'
                tool.setIconCls('x-fa fa-repeat');
                tool.setTooltip(CMDBuildUI.locales.Locales.bim.menu.rotate);
                break;
        }
    }
});

Ext.define('CMDBuildUI.view.graph.canvas.bottomMenu.canvasMenuController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.graph-canvas-bottommenu-canvasmenu',
    listen: {
        component: {
            '#': {
                beforerender: 'onBeforeRender',
                destroy: 'onDestroy'
            },
            "#enableTooltip": {
                toggle: 'onEnableDisableTooltipToggle',
                beforerender: 'onEnableToggleBeforeRender'
            }
        }
    },

    /**
     * @param {Ext.Component} toolbar
     * @param {Object} eOpts
     */
    onBeforeRender: function (toolbar, eOpts) {
        CMDBuildUI.graph.util.canvasMenu._init();
    },

    /**
     * @param {Ext.Component} toolbar
     * @param {Object} eOpts
     */
    onDestroy: function () {
        CMDBuildUI.graph.util.canvasMenu._reset();
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Boolean} pressed
     * @param {Object} eOpts
     */
    onEnableDisableTooltipToggle: function (button, pressed, eOpts) {
        console.log(pressed);
        CMDBuildUI.graph.threejs.SceneUtils.tooltip.setEnable(pressed);
    },

    /**
     * @param {Ext.Component} button
     * @param {Object} eOpts
     */
    onEnableToggleBeforeRender: function (button, eOpts) {
        //gets the configuration
        var enabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.node.tooltipEnabled);
        button.toggle(enabled, true);
    }
});
Ext.define('CMDBuildUI.view.administration.content.tasks.card.StepBar', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-tasks-stepbar',

    requires: [],
    config: {
        steps: null,
        currentStep: null,
        advanceable: null
    },

    layout: {
        type: 'fit',
        align: 'middle'
    },
    scrollable: false,

    ui: 'administration-stepbar',
    items: [
        { xtype: 'container', width: 10},
        {
            xtype: 'segmentedbutton',
            cls: 'stepbar',
            width: '80%',
            margin: 15,
            items: [{
                text: 'Option One',
                userCls: 'stepbar-item',
                ui: 'custom'
            }, {
                text: 'Option Two',
                pressed: true,
                userCls: 'stepbar-item'
            }, {
                text: 'Option Three',
                userCls: 'stepbar-item'
            }]
        },
            { xtype: 'container', width: 10}
    ],
    initComponent: function () {
        var me = this;
        if (me.getSteps()) {
            // Ext.Array.forEach(me.getSteps(), function (step, index) {
            //     me.down('segmentedbutton').add({
            //         text: step,
            //         index: index,
            //         listeners: {
            //             click: function (button, event) {
            //                 debugger;
            //             }
            //         }
            //     })
            // });
        }

        this.callParent(arguments);
    }
});
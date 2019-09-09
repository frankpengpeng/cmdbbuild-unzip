Ext.define('CMDBuildUI.view.administration.content.tasks.View', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.administration-content-tasks-view',

    requires: [
        'CMDBuildUI.view.administration.content.tasks.ViewController',
        'CMDBuildUI.view.administration.content.tasks.ViewModel'
    ],

    controller: 'administration-content-tasks-view',
    viewModel: {
        type: 'administration-content-tasks-view'
    },
    config: {
        type: null,
        workflowClassName: null
    },
    bind: {
        type: '{type}',
        workflowClassName: '{workflowClassName}'
    },
    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    items: [
        { xtype: 'administration-content-tasks-topbar', region: 'north' },
        { xtype: 'administration-content-tasks-grid', region: 'center', bind: { hidden: '{isGridHidden}' } }
    ],

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var vm = this.getViewModel();
        if (this.getType()) {
            vm.set('type', this.getType());
        } else {
            CMDBuildUI.util.Logger.log("type is not declared", CMDBuildUI.util.Logger.levels.error);
        }
        var type = CMDBuildUI.model.tasks.Task.types.findRecord('group', this.getType());
        
        vm.getParent().set('title', Ext.String.format('Tasks {0}', (type && type.get('groupLabel')?type.get('groupLabel'):'')));
        this.callParent(arguments);
    }
});
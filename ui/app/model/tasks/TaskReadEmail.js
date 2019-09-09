Ext.define('CMDBuildUI.model.tasks.TaskReadEmail', {
    extend: 'CMDBuildUI.model.tasks.Task',

    requires: [
        'Ext.data.validator.Presence'
    ],
    statics: {
        filterTypes: Ext.create('Ext.data.Store', {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: [{
                label: 'None',
                value: 'none'
            },{
                label: 'Regex',
                value: 'regex'
            }, {
                label: 'Function',
                value: 'function'
            }, {
                label: 'Is replay',
                value: 'isreplay'
            }, {
                label: 'Is not replay',
                value: 'isnotreplay'
            }]
        })
    },
    fields: [{
        name: 'config',
        critical: true,
        //type: 'auto'
        reference: {
            type: 'CMDBuildUI.model.tasks.ReadEmailConfig',
            unique: true
        }
    }],



    proxy: {
        url: '/jobs/',
        type: 'baseproxy',
        extraParams: {
            detailed: true
        }
    },

    clone: function () {
        var newTask = this.copy();
        newTask.set('_id', undefined);
        newTask.set('code', '');
        newTask.set('description', '');
        newTask.crudState = "C";
        newTask.phantom = true;
        delete newTask.crudStateWas;
        delete newTask.previousValues;
        delete newTask.modified;
        return newTask;
    }
});



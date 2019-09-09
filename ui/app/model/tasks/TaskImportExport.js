Ext.define('CMDBuildUI.model.tasks.TaskImportExport', {
    extend: 'CMDBuildUI.model.tasks.Task',

    requires: [
        'Ext.data.validator.Presence'
    ],

    statics: {
        postImportAsctions: Ext.create('Ext.data.Store', {

            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: [{
                label: 'Delete files',
                value: 'delete_files'
            }, {
                label: 'Disable files',
                value: 'disable_files'
            }, {
                label: 'Move files',
                value: 'move_files'
            }, {
                label: 'Do nothing',
                value: 'do_nothing'
            }]
        })
    },
    
    fields: [{
        name: 'config',
        critical: true,
        persist: true,
        reference: {
            type: 'CMDBuildUI.model.tasks.TaskImportExportConfig',
            unique: true
        }
    }],

    proxy: {
        url: '/jobs/',
        type: 'baseproxy',
        extraParams: {
            detailed: true
        },
        writer: {
            type: 'json',
            allDataOptions: {
                associated: true,
                persist: true
            }

        }
    },

    copyForClone: function () {
        var newTask = this.copy();
        newTask.set('_id', undefined);
        newTask.set('code', '');
        newTask.set('description', '');
        // newTask._config().add(this.getAssociatedData()._config);
        newTask.set('_config', this.getAssociatedData().config);
        newTask.crudState = "C";
        newTask.phantom = true;
        delete newTask.crudStateWas;
        delete newTask.previousValues;
        delete newTask.modified;
        return newTask;
    }
});



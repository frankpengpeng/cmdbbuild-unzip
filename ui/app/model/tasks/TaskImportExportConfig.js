Ext.define('CMDBuildUI.model.tasks.TaskImportExportConfig', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    statics: {
       
        sourceTypes: Ext.create('Ext.data.Store', {
            proxy: 'memory',
            data: [{
                label: 'File on server',
                value: 'file',
                group: 'importexport'
            }, {
                label: 'URL',
                value: 'url',
                group: 'importexport'
            }]
        }),
        postImportActions: Ext.create('Ext.data.Store', {
            proxy: 'memory',
            data: [{
                label: 'Delete files',
                value: 'delete_files',
                group: 'importexport'
            }, {
                label: 'Disable files',
                value: 'disable_files',
                group: 'importexport'
            }, {
                label: 'Move files',
                value: 'move_files',
                group: 'importexport'
            }, {
                label: 'Disable files',
                value: 'disable_files',
                group: 'importexport'
            }, {
                label: 'Do nothing',
                value: 'do_nothing',
                group: 'importexport'
            }]
        }),
        notificationModes: Ext.create('Ext.data.Store', {
            proxy: 'memory',
            data: [{
                label: 'On errors',
                value: 'on_errors',
                group: 'importexport'
            }, {
                label: 'Always',
                value: 'always',
                group: 'importexport'
            }, {
                label: 'Attach file',
                value: 'attach_file',
                group: 'export'
            }, {
                label: 'Never',
                value: 'never',
                group: 'importexport'
            }]
        })
    },
    fields: [{
        name: 'fileName', // export
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'template', // import/export
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'directory', // import/export
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'emailAccount', // export
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'emailTemplate', // export
        type: 'string',
        persist: true,
        critical: true
    }, {
        // on_errors, always, attach_file, never
        name: 'notificationMode', // export
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'cronExpression', // import/export
        type: 'string',
        persist: true,
        critical: true
    }, {
        // for values see statics.sourceTypes
        name: 'source', // import
        type: 'string',
        persist: true,
        critical: true
    }, {
        // ".*[.]csv" string|REGEX
        name: 'filePattern', // import
        type: 'string',
        persist: true,
        critical: true
    }, {
        // required if source === file
        // values delete_files, disable_files, move_files, do_nothing
        name: 'postImportAction', // import
        type: 'string',
        persist: true,
        critical: true
    }, {
        // required field if the source = file and postImportAction = move_files, this will be the target directory of the move_files action
        name: 'targetDirectory', // import
        type: 'string',
        persist: true,
        critical: true
    }],
    

    proxy: {
        type: 'memory'
    },

    clone: function () {
        var newTask = this.copy();
        newTask.set('_id', undefined);
        newTask.crudState = "C";
        newTask.phantom = true;
        delete newTask.crudStateWas;
        delete newTask.previousValues;
        delete newTask.modified;
        return newTask;
    }
});
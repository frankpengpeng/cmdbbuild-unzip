Ext.define('CMDBuildUI.model.tasks.Task', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],
    statics: {
        typelabel: {
            import_export: 'Import / Export',
            emailservice: 'Read emails'
        },
        types: Ext.create('Ext.data.Store', {
            proxy: 'memory',
            data: [{
                label: 'Import',
                value: 'import_file',
                group: 'import_export',
                groupLabel: 'Import / Export'
            }, {
                label: 'Export',
                value: 'export_file',
                group: 'import_export',
                groupLabel: 'Import / Export'
            }, {
                label: 'Read emails',
                value: 'emailService',
                group: 'emailService',
                groupLabel: 'Read emails'
            }, {
                label: 'Process',
                value: 'workflow',
                group: 'workflow',
                groupLabel: 'Start workflow'
            }]
        }),
        cronSettings: Ext.create('Ext.data.Store', {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: [{
                label: 'Every hours',
                value: '0 * * * ?'
            }, {
                label: 'Every day',
                value: '0 4 * * ?'
            }, {
                label: 'Every month',
                value: '0 4 1 * ?'
            }, {
                label: 'Every year',
                value: '0 4 1 1 ?'
            }, {
                label: 'Advanced',
                value: 'advanced'
            }]
        })
    },
    fields: [{
        name: 'code',
        type: 'string',
        persist: true,
        critical: true,
        validators: ['presence']
    }, {
        name: 'description',
        type: 'string',
        persist: true,
        critical: true,
        validators: ['presence']
    }, {
        name: 'type',
        type: 'string',
        persist: true,
        critical: true,
        validators: ['presence']
    }, {
        name: 'cronExpression',
        type: 'string',
        persist: true,
        critical: true,
        validators: ['presence']
    }, {
        name: 'enabled',
        type: 'boolean',
        persist: true,
        critical: true
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



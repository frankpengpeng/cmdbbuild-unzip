Ext.define('CMDBuildUI.view.administration.content.tasks.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-tasks-view',
    data: {
        type: null,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        toolbarHiddenButtons: {
            edit: true, // action !== view
            print: true, // action !== view
            disable: true,
            enable: true
        }
    },

    formulas: {

        typeManager: {
            bind: {
                type:  '{type}'      
            },
            get: function (data) {
                var me = this;
                if (data.type) {
                    switch (data.type) {
                        case 'import_export':
                            this.set('gridFilters', [function (item) {
                                return item.get('type') === 'import_file' || item.get('type') === 'export_file';
                            }]);
                            this.set('taskModelName', 'CMDBuildUI.model.tasks.TaskImportExport');
                            break;
                        case 'emailService':
                            this.set('gridFilters', [function (item) {
                                return data.type === item.get('type');
                            }]);
                            this.set('taskModelName', 'CMDBuildUI.model.tasks.TaskReadEmail');
                            break;
                        case 'workflow':                            
                            this.set('gridFilters', [function (item) {
                                if(!me.getView().getWorkflowClassName()){
                                    return data.type === item.get('type');
                                }else{
                                    return data.type === item.get('type') && me.getView().getWorkflowClassName() === item._config.get('classname');
                                }
                            }]);
                            this.set('taskModelName', 'CMDBuildUI.model.tasks.TaskStartWorkflow');
                            break;
                        default:
                            this.set('gridFilters', [function (item) {
                                return data.type === item.get('type');
                            }]);
                            break;

                    }


                    return data.type;

                }
            }
        },
        actionManager: {
            bind: '{action}',
            get: function (action) {
                if (this.get('actions.edit')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (this.get('actions.add')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        }
    },

    stores: {
        gridDataStore: {
            type: 'tasks',
            model: '{taskModelName}',
            proxy: {
                type: 'baseproxy',
                url: '/jobs',
                extraParams: {
                    detailed: true,
                    type: '{type}'
                }
            },
            filters: '{gridFilters}',
            autoLoad: true,
            autoDestroy: true
        },
        allImportExportTemplates: {
            type: 'importexports-templates',
            autoload: true,
            autoDestroy: true
        },

        allEmailAccountTemplates: {
            type: 'importexports-templates',
            autoload: true,
            autoDestroy: true
        }
    }
});

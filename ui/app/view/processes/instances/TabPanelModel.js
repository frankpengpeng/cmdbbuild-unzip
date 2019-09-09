Ext.define('CMDBuildUI.view.processes.instances.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.processes-instances-tabpanel',

    data: {
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.process,
        objectTypeName: null,
        objectId: null,
        activityId: null,
        activeTab: 0,
        storeinfo: {
            autoload: false
        },
        disabled: {
            activity: false,
            attachments: true,
            emails: true,
            history: true,
            notes: true,
            relations: true
        },
        basepermissions: {
            delete: false,
            edit: false
        },
        emailtemplatestoevaluate: []
    },

    formulas: {
        //update permissions to able/disable icons
        updatePermissions: {
            bind: {
                action: '{action}'
            },
            get: function (data) {
                if (
                    data.action !== CMDBuildUI.mixins.DetailsTabPanel.actions.create
                ) {
                    this.set("disabled.attachments", false);
                    this.set("disabled.emails", false);
                    this.set("disabled.history", false);
                    this.set("disabled.notes", false);
                    this.set("disabled.relations", false);
                }
            }
        },

        updateWindowTitle: {
            bind: {
                objecttypename: '{objectTypeName}'
            },
            get: function(data) {
                if (data.objecttypename) {
                    this.getParent().set(
                        "typeDescription",
                        CMDBuildUI.util.helper.ModelHelper.getProcessDescription(data.objecttypename)
                    );
                }
            }
        }
    }
});

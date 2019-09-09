Ext.define('CMDBuildUI.view.processes.instances.instance.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.processes-instances-instance-edit',

    data: {
        activity_action: {
            fieldname: null,
            value: null
        }
    },

    formulas: {
        showSaveButton: {
            bind: '{theProcess}',
            get: function (theProcess) {
                return theProcess && theProcess.get("enableSaveButton");
            }
        },
        popupTitle: {
            bind: {
                instance: '{theObject.Description}',
                activity: '{theActivity.description}'
            },
            get: function (data) {
                this.getParent().getParent().set(
                    "itemDescription",
                    Ext.String.format(
                        "{0} &mdash; {1}",
                        data.instance,
                        data.activity
                    )
                );
            }
        }
    }

});
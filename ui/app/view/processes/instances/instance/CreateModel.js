Ext.define('CMDBuildUI.view.processes.instances.instance.CreateModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.processes-instances-instance-create',

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
                process: '{theProcess}',
                activity: '{theActivity.description}'
            },
            get: function (data) {
                this.getParent().getParent().set(
                    "itemDescription",
                    Ext.String.format(
                        "&mdash; {0}",
                        data.activity
                    )
                );
            }
        }
    }

});
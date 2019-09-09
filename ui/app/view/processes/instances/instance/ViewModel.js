Ext.define('CMDBuildUI.view.processes.instances.instance.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.processes-instances-instance-view',

    data: {
        hiddenbtns: {
            open: true,
            relgraph: true,
            opentabs: true
        }
    },

    formulas: {
        title: function (get) {
            return null; // return null to hide header
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
        },
        updatePermissions: {
            bind: {
                activity: '{theActivity}'
            },
            get: function (data) {
                if (data.activity && data.activity.get("writable")) {
                    this.getParent().set("basepermissions.delete", true);
                    this.getParent().set("basepermissions.edit", true);
                } else {
                    this.getParent().set("basepermissions.delete", false);
                    this.getParent().set("basepermissions.edit", false);
                }
                this.set("hiddenbtns.open", this.getView().getShownInPopup());

                // open tabs button
                var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
                var configAttachments = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled);
                if (
                    privileges.flow_tab_note_access ||
                    privileges.flow_tab_relation_access ||
                    privileges.flow_tab_history_access ||
                    privileges.flow_tab_email_access ||
                    (configAttachments && privileges.flow_tab_attachment_access)
                ) {
                    this.set("hiddenbtns.opentabs", this.getView().getShownInPopup());
                }
            }
        }
    }

});
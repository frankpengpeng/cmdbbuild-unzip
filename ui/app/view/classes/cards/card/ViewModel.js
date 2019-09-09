Ext.define('CMDBuildUI.view.classes.cards.card.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-card-view',

    data: {
        permissions: {
            clone: false,
            delete: false,
            edit: false
        },
        hiddenbtns: {
            bim: true,
            open: false,
            relgraph: true,
            opentabs: true
        },
        bim: {
            projectid: null
        }
    },

    formulas: {
        title: function (get) {
            return null; // return null to hide header
        },
        /**
         * Update description in parent
         */
        updateDescription: {
            bind: {
                description: '{theObject.Description}'
            },
            get: function (data) {
                if (this.getView().getShownInPopup()) {
                    this.getParent().set("objectDescription", data.description);
                }
            }
        },

        /**
         * class object by type name
         */
        classObject: {
            bind: {
                typename: '{objectTypeName}'
            },
            get: function(data) {
                return CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.typename);
            }
        },

        updatePermissions: {
            bind: {
                typename: '{objectTypeName}',
                objectid: '{objectId}'
            },
            get: function (data) {
                var me = this;
                if (data.typename) {
                    var configs = CMDBuildUI.util.helper.Configurations;
                    var item = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.typename);
                    var isSimpleClass = item.isSimpleClass();
                    // hidden buttons
                    this.set("hiddenbtns.open", this.getView().getShownInPopup());
                    this.set("hiddenbtns.relgraph", (isSimpleClass || !configs.get(CMDBuildUI.model.Configuration.relgraph.enabled)));

                    // bim options
                    if (!isSimpleClass && data.objectid && configs.get(CMDBuildUI.model.Configuration.bim.enabled)) {
                        Ext.Ajax.request({
                            url: CMDBuildUI.util.api.Classes.getCardBimUrl(data.typename, data.objectid),
                            success: function (response) {
                                var jsonResponse = JSON.parse(response.responseText);
                                if (jsonResponse.data.exists) {
                                    me.set('hiddenbtns.bim', false);
                                    me.set('bim.projectid', jsonResponse.data.projectId);
                                } else {
                                    me.set('hiddenbtns.bim', true);
                                    me.set('bim.projectid', null);
                                }
                            }
                        });
                    }

                    // open tabs button
                    var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
                    var configAttachments = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled);
                    if (!isSimpleClass & (
                        privileges.card_tab_detail_access ||
                        privileges.card_tab_note_access ||
                        privileges.card_tab_relation_access ||
                        privileges.card_tab_history_access ||
                        privileges.card_tab_email_access ||
                        (configAttachments && privileges.card_tab_attachment_access))
                    ) {
                        this.set("hiddenbtns.opentabs", this.getView().getShownInPopup());
                    }
                }
            }
        },

        updatePermissionsFromInstance: {
            bind: {
                model: '{theObject._model}'
            },
            get: function (data) {
                if (data.model) {
                    this.set("permissions.clone", data.model[CMDBuildUI.model.base.Base.permissions.clone]);
                    this.set("permissions.delete", data.model[CMDBuildUI.model.base.Base.permissions.delete]);
                    this.set("permissions.edit", data.model[CMDBuildUI.model.base.Base.permissions.edit]);
                }
            }
        }
    }
});

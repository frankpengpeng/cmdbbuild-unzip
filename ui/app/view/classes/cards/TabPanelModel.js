Ext.define('CMDBuildUI.view.classes.cards.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-tabpanel',

    data: {
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
        objectTypeName: null,
        objectId: null,
        objectDescription: null,
        storeinfo: {
            autoload: false
        },
        basepermissions: {
            clone: false,
            delete: false,
            edit: false
        },
        disabled: {
            attachments: true,
            card: false,
            email: true,
            history: true,
            masterdetail: true,
            notes: true,
            relations: true
        },
        emailtemplatestoevaluate: []
    },

    formulas: {
        //update permissions to able/disable icons
        updatePermissions: {
            bind: {
                action: '{action}',
                objectTypeName: '{objectTypeName}'
            },
            get: function (data) {
                if (
                    data.action !== CMDBuildUI.mixins.DetailsTabPanel.actions.create &&
                    data.action !== CMDBuildUI.mixins.DetailsTabPanel.actions.clone
                ) {
                    this.set("disabled.attachments", false);
                    this.set("disabled.email", false);
                    this.set("disabled.history", false);
                    this.set("disabled.masterdetail", false);
                    this.set("disabled.notes", false);
                    this.set("disabled.relations", false);
                }

                var item = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.objectTypeName);
                this.set("basepermissions", {
                    clone: item.get(CMDBuildUI.model.base.Base.permissions.clone),
                    delete: item.get(CMDBuildUI.model.base.Base.permissions.delete),
                    edit: item.get(CMDBuildUI.model.base.Base.permissions.edit)
                });
            }
        },

        updateWindowTitle: {
            bind: {
                objecttypename: '{objectTypeName}',
                objectid: '{objectId}'
            },
            get: function (data) {
                if (data.objecttypename && data.objectid) {
                    var me = this;
                    // set description for parent view model
                    me.getParent().set(
                        "typeDescription",
                        CMDBuildUI.util.helper.ModelHelper.getClassDescription(data.objecttypename)
                    );
                    // set item description in parent view model
                    CMDBuildUI.util.helper.ModelHelper.getModel(CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, data.objecttypename).then(function (model) {
                        model.load(data.objectid, {
                            callback: function (record, operation, success) {
                                if (success) {
                                    me.getParent().set(
                                        "itemDescription",
                                        record.get("Description")
                                    );
                                }
                            }
                        });
                    });
                }
            }
        }
    }
});

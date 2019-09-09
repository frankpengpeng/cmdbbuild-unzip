Ext.define('CMDBuildUI.view.attachments.FormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.attachments-form',

    data: {
        majorVersionValue: false,

        // store params
        storedata: {
            autoload: false
        },

        // fields properties
        description: {
            hidden: true,
            required: false
        },
        majorversion: {
            hidden: true
        }
    },

    formulas: {

        /**
         * hide/show major version field
         */
        hideMajorVersion: {
            bind: '{newAttachment}',
            get: function (isNewAttachment) {
                return isNewAttachment;
            }
        },

        /**
         * update categories store paramaters
         */
        updateData: {
            bind: {
                targettypeobject : '{targetTypeObject}'
            },
            get: function (data) {
                // get store url
                var ltype = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.categorylookup);
                if (data.targettypeobject.get("attachmentTypeLookup")) {
                    ltype = data.targettypeobject.get("attachmentTypeLookup");
                }
                this.set("storedata.proxyurl", CMDBuildUI.util.api.Lookups.getLookupValues(ltype));
                this.set("storedata.autoload", true);

                // set descripion properties
                var mode = data.targettypeobject.get("attachmentDescriptionMode") || CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.descriptionmode);
                if (mode === CMDBuildUI.model.attachments.Attachment.descriptionmodes.optional) {
                    this.set("description.hidden", false);
                } else if (mode === CMDBuildUI.model.attachments.Attachment.descriptionmodes.mandatory) {
                    this.set("description.hidden", false);
                    this.set("description.label", CMDBuildUI.locales.Locales.attachments.description + " *");
                    this.set("description.required", true);
                }
            }
        }
    },

    stores: {
        categories: {
            model: 'CMDBuildUI.model.lookups.Lookup',
            proxy: {
                url: '{storedata.proxyurl}',
                type: 'baseproxy'
            },
            autoLoad: '{storedata.autoload}'
        }
    }
});

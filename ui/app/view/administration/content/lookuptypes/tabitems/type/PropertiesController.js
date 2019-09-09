Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.type.PropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabitems-type-properties',
    require: [
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],

    control: {
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onDeleteBtnClick: function (button) {
        button.setDisabled();
        var me = this;
        var vm = me.getViewModel();

        Ext.Msg.confirm(
            "Delete lookup", // TODO: translate
            "Are you sure you want to delete this lookup?", // TODO: translate
            function (action) {
                if (action === "yes") {
                    var theObject = vm.get('theLookupType');
                    CMDBuildUI.util.Ajax.setActionId('delete-lookuptype');

                    theObject.erase({
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheLookupTypeUrl();
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', Ext.util.History.getToken(), nextUrl, me);
                        }
                    });
                }
            }
        );
    },



    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        button.setDisabled(true);
        var vm = me.getViewModel();
        var theLookupType = vm.get('theLookupType');
        if (!theLookupType.isValid()) {
            var validatorResult = theLookupType.validate();
            var errors = validatorResult.items;
            for (var i = 0; i < errors.length; i++) {
            }
        } else {

            theLookupType.save({
                success: function (record, operation) {
                    theLookupType.commit();
                    var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheLookupTypeUrl(record.getId());

                    CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                        function () {
                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                        });

                },
                callback: function (record, operation, success) {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                }
            });
        }
    },

    onCancelBtnClick: function (button, e, eOpts) {
        var me = this;
        button.setDisabled(true);
        var nextUrl =  CMDBuildUI.util.administration.helper.ApiHelper.client.getTheLookupTypeUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
    }
});
Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.view-administration-content-lookuptypes-tabitems-values-card-create',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * On translate button click
     * @param {Event} event
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theValue = vm.get('theValue');
        var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfLookupValueDescription( theValue.get('_type'), theValue.get('code'));
        var popup = CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(key, CMDBuildUI.util.administration.helper.FormHelper.formActions.add, 'theTranslation', vm);
        popup.setPagePosition(event.getX() - 450, event.getY() + 20);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.EditController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        // set vm varibles
        var vm = this.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
        vm.set('actions.add', true );
        vm.set('actions.view', false );
        vm.set('actions.edit', false );
        vm.linkTo("theValue", {
            type: 'CMDBuildUI.model.lookups.Lookup',
            create: true
        });
        var typeViewModel = Ext.ComponentQuery.query('viewport')[0].down('administration-content-lookuptypes-tabitems-type-properties').lookupViewModel();
        if (typeViewModel) {
            vm.set('parentTypeName', typeViewModel.get('theLookupType.parent'));
        }
    },
    /**
     * Save function
     */
    onSaveBtnClick: function () {
        var me = this;
        var vm = this.getViewModel();
        var form = this.getView();

        if (form.isValid()) {
            var theValue = vm.get('theValue');
            theValue.getProxy().setUrl(Ext.String.format('/lookup_types/{0}/values', vm.get('lookupTypeName')));
            delete theValue.data.inherited;
            delete theValue.data.writable;
            delete theValue.data.hidden;

            theValue.save({
                success: function (record, operation) {
                    var theValue = vm.get('theValue');    
                    var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfLookupValueDescription(theValue.get('_type'), theValue.get('code'));
                    if(vm.get(('theTranslation'))){
                        vm.get('theTranslation').crudState = 'U';
                        vm.get('theTranslation').crudStateWas = 'U';
                        vm.get('theTranslation').phantom = false;
                        vm.get('theTranslation').set('_id', key);
                        vm.get('theTranslation').save({
                            success: function (record, operation) {
                                Ext.GlobalEvents.fireEventArgs("lookupvaluecreated", [record]);
                                CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                            }
                        });
                    }else{
                        Ext.GlobalEvents.fireEventArgs("lookupvaluecreated", [record]);
                        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                    }
                }
            });
        } else {
            var w = Ext.create('Ext.window.Toast', {
                ui: 'administration',
                html: CMDBuildUI.locales.Locales.administration.common.messages.correctformerrors,
                title: CMDBuildUI.locales.Locales.administration.common.messages.error,
                iconCls: 'x-fa fa-check-circle',
                align: 'br'
            });
            w.show();
        }
    },
    /**
     * Reset form function
     */
    onCancelBtnClick: function () {
        this.getViewModel().get("theValue").reject();
        this.getView().fireEventArgs("cancelcreation");
    },

    /**
     * @param {Ext.form.field.File} input
     * @param {Object} value
     * @param {Object} eOpts
     */
    onFileChange: function (input, value, eOpt) {
        var vm = this.getViewModel();
        var file = input.fileInputEl.dom.files[0];
        var reader = new FileReader();

        reader.addEventListener("load", function () {
            vm.getData().theValue.set('icon_image', reader.result);
        }, false);

        if (file) {
            reader.readAsDataURL(file);
        }
    }
});

Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.EditController', {
    imports: [
        'CMDBuildUI.util.Utilities'
    ],

    extend: 'Ext.app.ViewController',
    alias: 'controller.view-administration-content-lookuptypes-tabitems-values-card-edit',

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
     * @param {CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.EditController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        var lookupTypeName = vm.get('lookupTypeName');
        var valueId = vm.get('valueId');
        if (lookupTypeName && valueId) {
            vm.linkTo("theValue", {
                type: 'CMDBuildUI.model.lookups.Lookup',
                id: valueId
            });
        }
        var typeViewModel = Ext.ComponentQuery.query('viewport')[0].down('administration-content-lookuptypes-tabitems-type-properties').lookupViewModel();
        if (typeViewModel) {
            vm.set('parentTypeName', typeViewModel.get('theLookupType.parent'));
        }

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
        var translationCode = Ext.String.format('lookup.{0}.{1}.description', theValue.get('_type'), theValue.get('code'));
        var popup = CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, CMDBuildUI.util.administration.helper.FormHelper.formActions.edit, 'theTranslation', vm);
        popup.setPagePosition(event.getX() - 450, event.getY() + 20);
    },

    /**
     * On save button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        button.setDisabled(true);
        var me = this;
        var vm = me.getViewModel();

        vm.get('theValue').save({
            success: function (record, operation) {
                Ext.GlobalEvents.fireEventArgs("lookupvalueupdated", [record]);
                me.getView().up().fireEvent("closed");
            },
            callback: function () {
                if (button.el.dom) {
                    button.setDisabled(false);
                }

            }
        });
    },
    /**
     * On cancel button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        vm.get("theValue").reject(); // discard changes
        this.getView().up().fireEvent("closed");
    }

});
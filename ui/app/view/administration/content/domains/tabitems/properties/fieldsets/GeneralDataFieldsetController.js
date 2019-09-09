Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset',

    control:{
        '#': {
            afterrender: 'onAfterRender'
        }
    },
    onAfterRender: function () {
        if (this.getViewModel().get('actions.add')) {
            this.lookupReference('domainname').maxLength = 20;
        }
    },

    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickDescription: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theDomain = vm.get('theDomain');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDomainDescription(vm.get('actions.edit') ? theDomain.get('name') : '.');    
        var popup = CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theDomainDescriptionTranslation', vm.getParent());
        popup.setPagePosition(event.getX() - 450, event.getY() - 250);
    },

    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickDirect: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theDomain = vm.get('theDomain');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDomainDirectDescription(vm.get('actions.edit') ? theDomain.get('name') : '.');
        var popup = CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theDirectDescriptionTranslation', vm.getParent());
        popup.setPagePosition(event.getX() - 450, event.getY() - 250);
    },


    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickInverse: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theDomain = vm.get('theDomain');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDomainInverseDescription(vm.get('actions.edit') ? theDomain.get('name') : '.');
        var popup = CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theInverseDescriptionTranslation', vm.getParent());
        popup.setPagePosition(event.getX() - 450, event.getY() - 250);
    },


    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickMasterDetail: function (event, button, eOpts) {
        var vm = this.getViewModel();
        var theDomain = vm.get('theDomain');
        var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDomainMasterDetail(vm.get('actions.edit') ? theDomain.get('name') : '.');
        var popup = CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theMasterDetailTranslation', vm.getParent());
        popup.setPagePosition(event.getX() - 450, event.getY() - 250);
    }
});
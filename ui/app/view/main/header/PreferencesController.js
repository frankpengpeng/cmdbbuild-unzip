Ext.define('CMDBuildUI.view.main.header.PreferencesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.main-header-preferences',
    control: {
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Event} e 
     * @param {Object} eOpts 
     */
    onSaveBtnClick: function (btn, e, eOpts) {
        var vm = btn.lookupViewModel();
        var preferences = CMDBuildUI.util.helper.UserPreferences.getPreferences();
        preferences.set(CMDBuildUI.model.users.Preference.decimalsSeparator, vm.get("values.decimalsSeparator"));
        preferences.set(CMDBuildUI.model.users.Preference.thousandsSeparator, vm.get("values.thousandsSeparator"));
        preferences.set(CMDBuildUI.model.users.Preference.dateFormat, vm.get("values.dateFormat"));
        preferences.set(CMDBuildUI.model.users.Preference.timeFormat, vm.get("values.timeFormat"));
        preferences.set(CMDBuildUI.model.users.Preference.language, vm.get("values.language"));
        preferences.set(CMDBuildUI.model.users.Preference.timezone, vm.get("values.timezone"));
        preferences.set(CMDBuildUI.model.users.Preference.preferredOfficeSuite, vm.get("values.preferredOfficeSuite"));
        // make post request
        var newprefs = preferences.clone();
        newprefs.getProxy().setUrl("/sessions/current/preferences");
        newprefs.phantom = true;
        newprefs.save({
            callback: function () {
                CMDBuildUI.util.helper.UserPreferences.formats = {};
                CMDBuildUI.util.Utilities.closePopup('UserPreferences');
            }
        });
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Event} e 
     * @param {Object} eOpts 
     */
    onCancelBtnClick: function (btn, e, eOpts) {
        CMDBuildUI.util.Utilities.closePopup('UserPreferences');
    }
});
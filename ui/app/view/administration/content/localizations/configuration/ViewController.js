Ext.define('CMDBuildUI.view.administration.content.localizations.configuration.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-configuration-view',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.localizations.view} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        var languagesStore = vm.getStore('languages');
        languagesStore.load({
            callback: function (languagesStoreRecords) {
                var activeLanguagesStore = vm.getStore('activelanguages');
                activeLanguagesStore.load({
                    callback: function (activeLanguagesStoreRecords) {
                        var languages = [];
                        var languagescheckboxGroup = view.lookup('languagescheckboxGroup');
                        var activelanguages = me.createLanguagesArray(activeLanguagesStoreRecords);
                        languagesStoreRecords.forEach(function (record) {
                            var lang = record.get('description');
                            var code = record.get('code');
                            if (activelanguages.includes(code)) {
                                record.set('active', true);
                            }
                            var flag = '<img width="20px" style="vertical-align:middle;margin-right:5px" src="resources/images/flags/' + code + '.png" />';
                            languages.push({
                                boxLabel: flag + lang,
                                value: record.get('active'),
                                disabled: true,
                                disabledCls: '',
                                config: {
                                    record: record
                                },
                                listeners: {
                                    change: function (checkbox, newValue, oldValue, eOpts) {
                                        var language = checkbox.config.record;
                                        if (newValue) {
                                            activeLanguagesStore.add(language);
                                        } else {
                                            activeLanguagesStore.remove(language);
                                        }
                                    }
                                }
                            });
                        });

                        me._items = languagescheckboxGroup.add(languages);
                        me.onstoreActiveLoaded();
                    }
                });
            }
        });

    },

    /**
     * @param {CMDBuildUI.view.administration.content.localizations.button} view
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, eOpts) {
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        this.settingDisabled(false);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.localizations.button} view
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, eOpts) {
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        this.settingDisabled(true);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.localizations.button} view
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, eOpts) {
        var vm = this.getViewModel();
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var activelanguagesArray = [];

        vm.get('activelanguages').getRange().forEach(function(item){
            activelanguagesArray.push(item.get('code'));
        });
        
        var config = {
            'org__DOT__cmdbuild__DOT__core__DOT__language':vm.get('defaultlanguage'),
            'org__DOT__cmdbuild__DOT__core__DOT__languageprompt': vm.get('languageprompt'),
            'org__DOT__cmdbuild__DOT__core__DOT__enabled_languages':activelanguagesArray.join(',')
        };
        CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs(config, null, null, this).then(function(){
            button.setDisabled(false);
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        });

        this.settingDisabled(true);        
    },

    /**
     * @param {boolean} view
     */
    settingDisabled: function (view) {
        this._items.forEach(function (item) {
            item.setDisabled(view);            
        });
    },

    onstoreActiveLoaded: function () {
        var view = this.getView();
        var vm = this.getViewModel();
        var activeLanguagesStore = vm.getStore('activelanguages');
        var defaultLanguageCombo = view.lookup('defaultLanguageCombo');
        defaultLanguageCombo.bindStore(activeLanguagesStore);
    },

    /**
     * @param {array} storeRecords
     */
    createLanguagesArray: function (storeRecords) {
        var activelanguages = [];
        storeRecords.forEach(function (language) {
            activelanguages.push(language.get('code'));
        });
        return activelanguages;
    }

});
Ext.define('CMDBuildUI.view.administration.content.localizations.configuration.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-localizations-configuration-view',
    data: {
        actions: {
            view: false,
            edit: false,
            add: false
        },
        languageprompt: false,
        defaultlanguage:''
    },

    formulas: {  
        configManager: function(){
            var me = this;
            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(function(configs){
                var language = configs.filter(function(config){
                    return config._key === 'org__DOT__cmdbuild__DOT__core__DOT__language';
                })[0];
                var languageprompt = configs.filter(function(config){
                    return config._key === 'org__DOT__cmdbuild__DOT__core__DOT__languageprompt';
                })[0];
                me.set('defaultlanguage', language.hasValue ? language.value : language.default);
                me.set('languageprompt', languageprompt.hasValue ? Boolean(languageprompt.value) : Boolean(languageprompt.default));
            });
        },
        action: {
            bind: {
                isView: '{actions.view}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}'
            },
            get: function (data) {
                if (data.isView) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        }
    },

    stores: {
        languages: {
            type: 'translatable-languages',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                url: Ext.String.format(
                    '{0}/languages',
                    CMDBuildUI.util.Config.baseUrl
                ),
                type: 'baseproxy'
            },
            pageSize: 0
        },
        activelanguages: {
            type: 'translatable-languages',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                url: Ext.String.format(
                    '{0}/languages?active=true',
                    CMDBuildUI.util.Config.baseUrl
                ),
                type: 'baseproxy'
            },
            pageSize: 0
        }

    }
});
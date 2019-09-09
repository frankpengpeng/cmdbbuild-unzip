Ext.define('CMDBuildUI.view.administration.content.setup.elements.GeneralOptionsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-generaloptions',
    data: {
        values: {
            language: null,
            dateFormat: null,
            timeFormat: null,
            decimalsSeparator: null,
            thousandsSeparator: null
        },
        validations: {
            decimalsSeparator: true,
            thousandsSeparator: true
        }
    },

    formulas: {
        updateData: {
            get: function () {
                this.set("values.decimalsSeparator", CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.decimalsSeparator));
                this.set("values.thousandsSeparator", CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.thousandsSeparator));
                this.set("values.dateFormat", CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.dateFormat));
                this.set("values.timeFormat", CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.timeFormat));
                this.set("values.language", CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.language));
                this.set("values.timezone", CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.timezone));
                this.set("values.preferredOfficeSuite", CMDBuildUI.util.helper.UserPreferences.get(CMDBuildUI.model.users.Preference.preferredOfficeSuite));
            }
        },

        /**
         * Update separators validations
         */
        updateSeparatorsValidations: {
            bind: {
                decimals: '{values.decimalsSeparator}',
                thousands: '{values.thousandsSeparator}'
            },
            get: function (data) {
                var decimals = true,
                    thousands = true;
                if (data.decimals && data.thousands && data.decimals === data.thousands) {
                    thousands = decimals = CMDBuildUI.locales.Locales.main.preferences.decimalstousandserror;
                } else if (data.decimals && !data.thousands) {
                    thousands = CMDBuildUI.locales.Locales.main.preferences.thousandserror;
                } else if (!data.decimals && data.thousands) {
                    decimals = CMDBuildUI.locales.Locales.main.preferences.decimalserror;
                }
                this.set("validations.decimalsSeparator", decimals);
                this.set("validations.thousandsSeparator", thousands);
            }
        },

        dateFormatsData: function () {
            return [{
                label: 'dd/mm/yyyy',
                value: 'd/m/Y'
            }, {
                label: 'dd-mm-yyyy',
                value: 'd-m-Y'
            }, {
                label: 'dd.mm.yyyy',
                value: 'd.m.Y'
            }, {
                label: 'mm/dd/yyyy',
                value: 'm/d/Y'
            }, {
                label: 'yyyy/mm/dd',
                value: 'Y/m/d'
            }, {
                label: 'yyyy-mm-dd',
                value: 'Y-m-d'
            }];
        },

        timeFormatsData: function () {
            return [{
                value: 'H:i:s',
                label: CMDBuildUI.locales.Locales.main.preferences.twentyfourhourformat
            }, {
                value: 'h:i:s A',
                label: CMDBuildUI.locales.Locales.main.preferences.twelvehourformat
            }];
        },

        decimalsSeparatorsData: function () {
            return [{
                value: ',',
                label: CMDBuildUI.locales.Locales.main.preferences.comma
            }, {
                value: '.',
                label: CMDBuildUI.locales.Locales.main.preferences.period
            }];
        },

        thousandsSeparatorsData: function () {
            return [{
                value: ',',
                label: CMDBuildUI.locales.Locales.main.preferences.comma
            }, {
                value: '.',
                label: CMDBuildUI.locales.Locales.main.preferences.period
            }, {
                value: ' ',
                label: CMDBuildUI.locales.Locales.main.preferences.space
            }];
        },

        preferredOfficeSuiteData: function () {
            return [{
                value: 'default',
                label: CMDBuildUI.locales.Locales.main.preferences.default
            }, {
                value: 'msoffice',
                label: CMDBuildUI.locales.Locales.main.preferences.msoffice
            }];
        },

        updateTimezones: {
            get: function (data) {
                this.set(
                    "storeProxyUrl",
                    Ext.String.format(
                        '{0}/timezones',
                        CMDBuildUI.util.Config.baseUrl
                    )
                );
                this.set("storeAutoLoad", true);
            }
        }
    },

    stores: {
        languages: {
            model: 'CMDBuildUI.model.Language',
            sorters: 'description',
            autoLoad: true,
            autoDestroy: true
        },
        dateFormats: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{dateFormatsData}',
            autoLoad: true,
            autoDestroy: true
        },
        timeFormats: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{timeFormatsData}',
            autoLoad: true,
            autoDestroy: true
        },
        decimalsSeparators: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{decimalsSeparatorsData}',
            autoLoad: true,
            autoDestroy: true
        },
        thousandsSeparators: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{thousandsSeparatorsData}',
            autoLoad: true,
            autoDestroy: true
        },
        timezones: {
            autoDestroy: true,
            proxy: {
                type: 'baseproxy',
                url: '{storeProxyUrl}'
            },
            pageSize: 0,
            autoLoad: '{storeAutoLoad}'

        },
        preferredOfficeSuite: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{preferredOfficeSuiteData}',
            autoDestroy: true
        }
    }

});

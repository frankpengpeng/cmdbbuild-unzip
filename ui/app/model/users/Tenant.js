Ext.define('CMDBuildUI.model.users.Tenant', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        tenantmodes: {
            never: 'never',
            mixed: 'mixed',
            always: 'always'
        },
        getTenantModes: function(){
            return [{
                value: 'never',
                label: CMDBuildUI.locales.Locales.administration.common.strings.never // Never
            }, {
                value: 'always',
                label: CMDBuildUI.locales.Locales.administration.common.strings.always // Always
            }, {
                value: 'mixed',
                label: CMDBuildUI.locales.Locales.administration.common.strings.mixed // Mixed
            }];
        }
    },

    fields: [{
        name: 'description',
        type: 'string'
    }]
});
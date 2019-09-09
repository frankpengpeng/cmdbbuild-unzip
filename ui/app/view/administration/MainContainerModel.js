Ext.define('CMDBuildUI.view.administration.MainContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-maincontainer',

    formulas: {
        configManager: function(){
            var me = this;

            CMDBuildUI.util.administration.helper.ConfigHelper.getConfigs().then(
                function (configs) {
                    configs.forEach(function (key) {
                        me.set(Ext.String.format('theSetup.{0}', key._key), (key.hasValue) ? key.value : key.default);
                    });
                }
            );
        }
    }
});

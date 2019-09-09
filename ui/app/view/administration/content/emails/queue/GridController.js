Ext.define('CMDBuildUI.view.administration.content.emails.queue.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-queue-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.administration.content.emails.queue.Grid} view 
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        vm.set('storeConfig.autoLoad', true);
        CMDBuildUI.util.administration.helper.ConfigHelper.getConfig('org__DOT__cmdbuild__DOT__email__DOT__queue__DOT__enabled').then(function (configValue) {            
            vm.set('queueEnabled', configValue === 'true');
        });
    },
    /**
     * Change queue config status
     * 
     * @param {Ext.form.field.Checkbox} checkbox 
     * @param {Boolean} newValue 
     * @param {Boolean} oldValue 
     */
    onActiveStop: function (button, newValue, oldValue) {
        button.setDisabled(true);
        var vm = button.lookupViewModel();
        CMDBuildUI.util.Ajax.setActionId('email-queue-stop');
        CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs({
            'org__DOT__cmdbuild__DOT__email__DOT__queue__DOT__enabled': false
        },null,null, this).then(function (success) {
            if (success && success.status === 200) {
                vm.set('queueEnabled', false);
                vm.getStore('gridDataStore').load();
            } else {
                vm.set('queueEnabled', true);
            }
            button.setDisabled(false);
        });
    },
     /**
     * Change queue config status
     * 
     * @param {Ext.form.field.Checkbox} checkbox 
     * @param {Boolean} newValue 
     * @param {Boolean} oldValue 
     */
    onActiveStart: function (button, newValue, oldValue) {
        button.setDisabled(true);
        var vm = button.lookupViewModel();
        CMDBuildUI.util.Ajax.setActionId('email-queue-start');
        CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs({
            'org__DOT__cmdbuild__DOT__email__DOT__queue__DOT__enabled': true
        }, null, null, this).then(function (success) {
            if (success && success.status === 200) {
                vm.set('queueEnabled', true);
                vm.getStore('gridDataStore').load();
            } else {
                vm.set('queueEnabled', false);
            }
            button.setDisabled(false);
        });
    },

    /**
     * send mail
     * 
     * @param {CMDBuildUI.view.administration.content.emails.queue.Grid} grid 
     * @param {Number} rowIndex 
     * @param {Number} colIndex 
     * @param {Ext.panel.Tool} button 
     * @param {Ext.event.Event} event 
     * @param {CMDBuildUI.model.emails.Email} record 
     * @param {*} row 
     */
    onItemSendClick: function (grid, rowIndex, colIndex, button, event, record, row) {
        button.disable();
        CMDBuildUI.util.Ajax.setActionId('email-queue-send');
        Ext.Ajax.request({
            url: Ext.String.format('{0}/email/queue/outgoing/{1}/trigger', CMDBuildUI.util.Config.baseUrl, record.get('_id')),
            method: 'POST',
            callback: function () {
                button.enable();
                grid.getStore().load();
            }
        });
    }
});
Ext.define('CMDBuildUI.view.administration.content.emails.accounts.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-accounts-topbar',

    control: {
        '#addaccount': {
            click: 'onNewAccountBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewAccountBtnClick: function (item, event, eOpts) {
        var view = this.getView();

        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var account = Ext.create("CMDBuildUI.model.emails.Account");
        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-accounts-card-create',
            viewModel: {
                data: {
                    theAccount: account
                }
            }
        });

    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onKeyUp: function (field, event) {
        var vm = this.getViewModel();
        var searchTerm = '';
        if (vm.getData().search){
            searchTerm = vm.getData().search.value;
        }
                   
        var store = vm.get('accounts');
        store.clearFilter();
        if (searchTerm) {
            CMDBuildUI.util.administration.helper.GridHelper.searchMoreFields(store, searchTerm);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // clear store filter

        var store = vm.get('accounts');
        store.clearFilter();

        // reset input
        field.reset();
    }

});
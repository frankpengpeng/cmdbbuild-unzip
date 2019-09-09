Ext.define('CMDBuildUI.view.administration.content.users.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-users-topbar',

    control:{
        '#adduser':{
            click: 'onNewBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (item, event, eOpts) {
        var view = this.getView();
        view.up('administration-content-users-view').getViewModel().set('isGridHidden', false);
        
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-users-card-create',
            viewModel: {
                links: {
                    theUser: {
                        type: 'CMDBuildUI.model.users.User',
                        create: true
                    }
                }
            }
        });
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.onSearchSubmit(field);
        }
    },
    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        var searchValue = vm.getData().search.value;
        var allUserStore = vm.get("allUsers");
        if (searchValue) {
            var filter = {
                "query": searchValue
            };
            allUserStore.getProxy().setExtraParam('filter', Ext.JSON.encode(filter));
            allUserStore.load();
        } else {
            this.onSearchClear(field);
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
        var allUserStore = vm.get("allUsers");
        allUserStore.getProxy().setExtraParam('filter', Ext.JSON.encode([]));
        allUserStore.load();
        // reset input
        field.reset();
    }
});

Ext.define('CMDBuildUI.view.administration.content.emails.templates.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-templates-topbar',

    control: {
        '#addtemplate': {
            click: 'onNewTemplateBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewTemplateBtnClick: function (item, event, eOpts) {
        var view = this.getView();

        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var template = Ext.create("CMDBuildUI.model.emails.Template");
        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-templates-card-create',
            viewModel: {
                data: {
                    theTemplate: template
                }
            }
        });

    },
    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onKeyUp: function (field, event) {
        // get vm value
        var vm = this.getViewModel();
        var searchTerm = vm.getData().search.value;
        var store = vm.get('templates');
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

        var store = vm.get('templates');
        store.clearFilter();

        // reset input
        field.reset();
    }

});
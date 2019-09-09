Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-templates-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.tabitems.users.card.ViewInRow} view
     * @param {Object} eOpts
     */

    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var record = view.getInitialConfig()._rowContext.record;
        vm.set("theTemplate", record);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-templates-card-edit',
            viewModel: {
                data: {
                    theTemplate: this.getViewModel().get('theTemplate')
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onOpenBtnClick: function (button, e, eOpts) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-templates-card-view',
            viewModel: {
                data: {
                    theTemplate: this.getViewModel().get('theTemplate')
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloneBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var newTemplate = vm.get('theTemplate').clone();

        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-templates-card-create',

            viewModel: {
                data: {
                    theTemplate: newTemplate
                }
            }
        });
    },


    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this;

        var callback = function (btnText) {
            if (btnText === "yes") {
                CMDBuildUI.util.Ajax.setActionId('delete-template');
                me.getViewModel().get('theTemplate').erase({
                    success: function (record, operation) {
                        Ext.ComponentQuery.query('administration-content-emails-templates-grid')[0].fireEventArgs('reload', [record, 'delete']);
                    }
                });
            }
        };

        CMDBuildUI.util.administration.helper.ConfirmMessageHelper.showDeleteItemMessage(null, null, callback, this);
    },
    
    /**
     * @param {Ext.data.Store} data
     */
    onStoreLoad: function (data) {
        var theTemplate = this.getViewModel().get('theTemplate');
        var idAccount = theTemplate.get('account');
        var accountName = '';
        if (data.getById(idAccount)) {
            accountName = data.getById(idAccount).get('name');
        }
        this.getViewModel().set('accountDescription', accountName);
    }

});
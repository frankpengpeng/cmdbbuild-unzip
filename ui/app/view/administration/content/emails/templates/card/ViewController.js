Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-templates-card-view',

    control: {
        '#': {
            beforeRender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.emails.templates.card.CreateController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var title = view.getViewModel().get('theTemplate').get('name');
        view.up('administration-detailswindow').getViewModel().set('title', title);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var template = this.getViewModel().get('theTemplate');
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-templates-card-edit',
            viewModel: {
                data: {
                    theTemplate: template
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
        vm.set('newTemplate', newTemplate);
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
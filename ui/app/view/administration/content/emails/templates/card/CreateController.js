Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-templates-card-create',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
        // '#editValuesBtn': {
        //     click: 'onEditValueBtnClick'
        // }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.emails.templates.card.CreateController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.up('administration-detailswindow').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.emails.newtemplate);
        var vm = this.getViewModel();
        if (!vm.get('theTemplate') || !vm.get('theTemplate').phantom) {
            vm.linkTo("theTemplate", {
                type: 'CMDBuildUI.model.emails.Template',
                create: true
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var vm = this.getViewModel();
        var form = this.getView();
        if (form.isValid()) {
            var theTemplate = vm.get('theTemplate');
            theTemplate.save({
                success: function (record, operation) {
                    var w = Ext.create('Ext.window.Toast', {
                        ui: 'administration',
                        title: CMDBuildUI.locales.Locales.administration.common.messages.success,
                        html: CMDBuildUI.locales.Locales.administration.emails.templatesavedcorrectly,
                        iconCls: 'x-fa fa-check-circle',
                        align: 'br'
                    });
                    w.show();

                    Ext.GlobalEvents.fireEventArgs("templatecreated", [record]);
                    var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
                    container.fireEvent('closed');
                },
                callback: function () {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getViewModel().get("theTemplate").reject();
        var popup = this.getView().up("panel");
        popup.close();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditValueBtnClick: function (button, e, eOpts) {
        var title = CMDBuildUI.locales.Locales.administration.emails.editvalues;

        var config = {
            xtype: 'administration-components-keyvaluegrid-grid',
            viewModel: {
                data: {
                    theKeyvaluedata: this.getViewModel().get('theTemplate').get('data'),
                    theOwnerObject: this.getViewModel().get('theTemplate'),
                    theOwnerObjectKey: 'data',
                    actions: {
                        view: false
                    }
                }
            }
        };

        CMDBuildUI.util.Utilities.openPopup('popup-add-attachmentfromdms-panel', title, config, null, {
            ui: 'administration-actionpanel'
        });
    }

});
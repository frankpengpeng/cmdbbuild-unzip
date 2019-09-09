Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-templates-card-edit',

    control: {
        '#': {
            beforeRender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
        // ,
        // '#editValuesBtn': {
        //     click: 'onEditValueBtnClick'
        // }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.emails.templates.card.EditController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var title = view.getViewModel().get('theTemplate').get('name');
        view.up('administration-detailswindow').getViewModel().set('title', title);
        var vm = this.getViewModel();
        if (!vm.get('theTemplate').phantom) {
            vm.linkTo("theTemplate", {
                type: 'CMDBuildUI.model.emails.Template',
                id: vm.get('theTemplate').get('_id')
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
                    Ext.GlobalEvents.fireEventArgs("templateupdated", [record]);
                    form.up().fireEvent("closed");
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
        var vm = this.getViewModel();
        vm.get("theTemplate").reject(); // discard changes
        this.getView().up().fireEvent("closed");
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditValueBtnClick: function () {
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
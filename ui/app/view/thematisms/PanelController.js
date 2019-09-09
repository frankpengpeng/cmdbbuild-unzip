Ext.define('CMDBuildUI.view.thematisms.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.thematisms-panel',
    listen: {
        component: {
            '#': {
                beforerender: 'onBeforeRender'
            },
            '#cancelbutton': {
                click: 'onCancelButtonClick'
            },
            '#calculaterules': {
                click: 'onClaculateButtonClick'
            },
            '#applybutton': {
                click: 'onApplyButtonClick'
            },
            '#savebutton': {
                click: 'onSaveButtonClick'
            }
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.filters.attributes.Panel} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        view.add([{
            xtype: 'thematisms-thematism-row',
            reference: 'thematisms-thematism-row'
        }, {
            xtype: 'thematisms-thematism-rules',
            reference: 'rules',
            needListener: true
        }])
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onCancelButtonClick: function (button, eOpts) {
        this.getView().fireEvent('popupclose', this.getView(), this.getThematism());
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onClaculateButtonClick: function (button, eOpts) {
        this.getView().fireEvent('calculaterules', this.getView(), this.getThematism());
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onApplyButtonClick: function () {
        this.getView().fireEvent('applythematism', this.getView(), this.getThematism());
    },

    /**
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onSaveButtonClick: function () {
        //TODO: implement the new popup asking for the name
        // this.getView().fireEvent('saveandapplythematism', this.getView(), this.getThematism());
        var me = this;
        var thematism = this.getThematism();
        var w = Ext.create('Ext.window.Window', {
            title: thematism.get('description'),
            width: 400,
            layout: 'fit',
            alwaysOnTop: true,
            modal: true,
            ui: "management",

            viewModel: {
                data: {
                    theThematism: thematism
                }
            },

            items: {
                xtype: 'form',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                items: [{
                    xtype: 'textfield',
                    name: 'name',
                    fieldLabel: CMDBuildUI.locales.Locales.thematism.name,
                    bind: '{theThematism.name}',
                    allowBlank: false  // requires a non-empty value
                }]
            },

            buttons: [{
                text: CMDBuildUI.locales.Locales.common.actions.save,
                ui: 'management-action-small',
                handler: function () {
                    thematism.set("description", thematism.get("name"));
                    me.getView().fireEvent('saveandapplythematism', me.getView(), thematism);
                    w.destroy();
                }
            }, {
                text: CMDBuildUI.locales.Locales.common.actions.cancel,
                ui: 'secondary-action-small',
                handler: function () {
                    w.destroy();
                }
            }]
        });

        w.show();
    },

    privates: {

        /**
         * @returns {CMDBuildUI.model.thematisms.Thematism}
         */
        getThematism: function () {
            var thematism = this.getViewModel().get("theThematism");
            //here are set some configurations
            return thematism;
        }
    }
});

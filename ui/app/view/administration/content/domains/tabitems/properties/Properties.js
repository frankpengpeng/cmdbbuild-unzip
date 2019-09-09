Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.Properties', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.domains.tabitems.properties.PropertiesController'
    ],

    alias: 'widget.administration-content-domains-tabitems-properties-properties',
    controller: 'administration-content-domains-tabitems-properties-properties',
    viewModel: {

    },
    config: {
        theDomain: null
    },
    autoScroll: true,
    modelValidation: true,


    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    items: [{
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: [{
            xtype: 'administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset'
        }]
    }],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }],

    listeners: {
        beforerender: function(panel){
            this.addDocked({
                xtype: 'components-administration-toolbars-formtoolbar',
                dock: 'top',
                items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                    edit: true,
                    delete: true,
                    view: this._rowContext && this._rowContext.record,
                    activeToggle: true
                }, 'domains', 'theDomain'),
                bind: {
                    hidden: '{!actions.view}'
                }
            },0);
        },
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },

    initComponent: function () {
        var vm = this.getViewModel();
        var cardView = this.up('administration-detailswindow');
        if (cardView) {
            switch (vm.get('action')) {
                case CMDBuildUI.util.administration.helper.FormHelper.formActions.add:
                    vm.getParent().set('title', 'New Domain');
                    break;
                case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
                        vm.getParent().set('title', 'Domain - ' + vm.get('objectTypeName'));
                    break;
                case CMDBuildUI.util.administration.helper.FormHelper.formActions.view:                    
                        vm.getParent().set('title', 'Domain - ' + vm.get('objectTypeName'));
                    break;
            }
        }

        this.callParent(arguments);
    }

});
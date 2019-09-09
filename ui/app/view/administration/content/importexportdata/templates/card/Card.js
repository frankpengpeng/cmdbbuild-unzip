Ext.define('CMDBuildUI.view.administration.content.importexportdata.templates.card.Card', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-content-importexportdata-templates-card',
    
    requires: [
        'CMDBuildUI.view.administration.content.importexportdata.templates.card.CardController',
        'CMDBuildUI.view.administration.content.importexportdata.templates.card.CardModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],
    controller: 'view-administration-content-importexportdata-templates-card',
    viewModel: {
        type: 'view-administration-content-importexportdata-templates-card'
    },
    bubbleEvents: [
        'itemupdated',
        'cancelupdating'
    ],
    modelValidation: true,
    config: {
        theImportExportTemplate: null
    },

    bind: {
        theImportExportTemplate: '{theImportExportTemplate}'
    },
    hidden: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    scrollable: true,
    ui: 'administration-formpagination',
    items: [

    ],

    initComponent: function () {
        Ext.asap(function () {
            try {
                this.up().mask('loading');
            } catch (error) {

            }
        }, this);
        this.callParent(arguments);
    }
});
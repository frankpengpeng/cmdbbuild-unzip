Ext.define('CMDBuildUI.view.administration.content.importexportdata.templates.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    requires: [
        'CMDBuildUI.view.administration.content.importexportdata.templates.card.helpers.FieldsetsHelper',
        'CMDBuildUI.view.administration.content.importexportdata.templates.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.importexportdata.templates.card.ViewInRowModel',
        'Ext.layout.*'
    ],
    autoDestroy: true,
    alias: 'widget.administration-content-importexportdata-templates-card-viewinrow',
    controller: 'administration-content-importexportdata-templates-card-viewinrow',
    viewModel: {
        type: 'view-administration-content-importexportdata-templates-card'
    },

    cls: 'administration',
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    config: {
        objectTypeName: null,
        objectId: null,
        shownInPopup: false,
        theImportExportTemplate: null
    },
    minHeight: 200,
    items: [

    ],

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true, // #editBtn set true for show the button
        view: true, // #viewBtn set true for show the button
        clone: true, // #cloneBtn set true for show the button
        'delete': true, // #deleteBtn set true for show the button
        activeToggle: true // #enableBtn and #disableBtn set true for show the buttons       
    },

        /* testId */
        'importexporttemplates',

        /* viewModel object needed only for activeTogle */
        'theImportExportTemplate',

        /* add custom tools[] on the left of the bar */
        [],

        /* add custom tools[] before #editBtn*/
        [],

        /* add custom tools[] after at the end of the bar*/
        []
    ),

    listeners: {
        afterlayout: function (panel) {
            try {
                panel.unmask();
            } catch (error) {

            }
        }
    },

    initComponent: function () {
        Ext.asap(function () {
            try {
                this.mask('loading');
            } catch (error) {

            }
        }, this);

        this.callParent(arguments);
    }

});
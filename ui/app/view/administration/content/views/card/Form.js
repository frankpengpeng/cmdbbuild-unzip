Ext.define('CMDBuildUI.view.administration.content.views.card.Form', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.views.card.FormController',
        'CMDBuildUI.view.administration.content.views.card.FormModel',
        'CMDBuildUI.view.administration.content.views.card.FieldsHelper'
    ],
    alias: 'widget.administration-content-views-card-form',
    controller: 'administration-content-views-card-form',
    viewModel: {
        type: 'administration-content-views-card-form'
    },

    config: {
        theViewFilter: null
    },
    bind: {
        theViewFilter: '{theViewFilter}',
        userCls: '{formModeCls}' // this is used for hide label localzation icon in `view` mode
    },

    layout: {
        type: 'vbox',
        align: 'stretch',
        vertical: true
    },
    modelValidation: true,
    autoScroll: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [{
        region: 'center',
        xtype: 'form',
        hidden: true,
        bind: {
            hidden: '{isFormHidden}'
        },
        items: [{
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            scrollable: 'y',
            autoScroll: true,
            padding: '5 0 0 15',
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            items: [{
                xtype: 'administration-content-views-card-fieldscontainers-generalproperties'
            }]
        }]
    }],
    dockedItems: [{
        dock: 'top',
        borderBottom: 0,
        xtype: 'components-administration-toolbars-formtoolbar',
        itemId: 'toolbarscontainer',
        style: 'border-bottom-width:0!important',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({},
            'searchfilter',
            'theViewFilter',
            [{
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.administration.views.addview,
                localized:{
                    text: 'CMDBuildUI.locales.Locales.administration.views.addview'
                },
                ui: 'administration-action-small',
                reference: 'addBtn',
                itemId: 'addBtn',
                iconCls: 'x-fa fa-plus',
                margin: '7 0 5 9',
                autoEl: {
                    'data-testid': 'administration-searchfilter-toolbar-addBtn'
                }
            }])

    }, {
        xtype: 'components-administration-toolbars-formtoolbar',
        dock: 'top',
        bind: {
            hidden: '{!actions.view}'
        },
        listeners: {},
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: true,
            delete: true,
            activeToggle: true
        }, 'searchfilter', 'theViewFilter')
    }, {
        xtype: 'toolbar',
        itemId: 'bottomtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{isFormButtonsBarHidden}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }],

    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        this.callParent(arguments);
    },
    listeners: {
        afterlayout: function (panel) {

            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    }
});
Ext.define('CMDBuildUI.view.administration.content.gisnavigationtrees.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gisnavigationtrees.ViewController',
        'CMDBuildUI.view.administration.content.gisnavigationtrees.ViewModel'
    ],
    alias: 'widget.administration-content-gisnavigationtrees-view',
    controller: 'administration-content-gisnavigationtrees-view',
    layout: 'border',
    viewModel: {
        type: 'administration-content-gisnavigationtrees-view'
    },
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    items: [{
        bind: {
            hidden: '{formtoolbarHidden}'
        },
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        borderBottom: 0,
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: true,
            delete: true,
            activeToggle: true
        },
        'gisnavigation',
        'theNavigationtree')
    }, {
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        hidden: true,
        bind: {
            hidden: '{hideForm}'
        },
        items: [{
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            items: [{
                layout: 'column',
                items: [{
                    columnWidth: 0.5,
                    items: [
                        // is targetClass OR sourceClass???
                        CMDBuildUI.util.administration.helper.FieldsHelper.getTargetClassesInput({
                            targetClass: {
                                bind: {
                                    value: '{theNavigationtree.targetClass}',
                                    store: '{getAllStandardClassStore}'
                                },
                                listeners: {
                                    change: function (store, newValue, oldValue) {
                                        this.lookupViewModel().set('theNavigationtree.targetClass', newValue);
                                    }
                                }
                            }
                        })
                    ]
                }]
            }, {
                layout: 'column',
                items: [{
                    columnWidth: 0.5,
                    items: [CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                        active: {
                            disabledCls: '',
                            bind: {
                                value: '{theNavigationtree.active}'
                            }
                        }
                    })]
                }]
            }]
        }, {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            padding: 0,
            title: CMDBuildUI.locales.Locales.administration.common.labels.tree,
            localized:{
                title: 'CMDBuildUI.locales.Locales.administration.common.labels.tree'
            },
            items: [{
                xtype: 'administration-content-gisnavigationtrees-tree'
            }],
            bind: {
                hidden: '{!theNavigationtree.targetClass}'
            }
        }
        ]
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
    }]
});
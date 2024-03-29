Ext.define('CMDBuildUI.view.administration.content.navigationtrees.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.navigationtrees.ViewController',
        'CMDBuildUI.view.administration.content.navigationtrees.ViewModel'
    ],
    alias: 'widget.administration-content-navigationtrees-view',
    controller: 'administration-content-navigationtrees-view',
    layout: 'border',
    viewModel: {
        type: 'administration-content-navigationtrees-view'
    },
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    items: [{
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
                        CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput({
                            name: {
                                bind: {
                                    value: '{theNavigationtree.name}'
                                }
                            }
                        }, true, '[name="description"]'),
                        CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                            description: {
                                allowBlank: false,
                                bind: {
                                    value: '{theNavigationtree.description}'
                                }
                            }
                        })
                    ]
                }]
            }, {
                layout: 'column',
                items: [{
                    columnWidth: 0.5,
                    items: [CMDBuildUI.util.administration.helper.FieldsHelper.getTargetClassesInput({
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
                    })]
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
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.labels.tree'
            },
            items: [{
                xtype: 'administration-content-navigationtrees-tree'
            }],
            bind: {
                hidden: '{!theNavigationtree.targetClass}'
            }
        }]
    }],

    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        dock: 'top',
        padding: '5 10 5 10',
        borderBottom: 0,
        itemId: 'toolbarscontainer',
        style: 'border-bottom-width:0!important',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({},
            'navigationtree',
            'theNavigationtree',
            [{
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.administration.navigationtrees.texts.addnavigationtree,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.administration.navigationtrees.texts.addnavigationtree'
                },
                ui: 'administration-action-small',
                itemId: 'addBtn',
                iconCls: 'x-fa fa-plus',
                autoEl: {
                    'data-testid': 'administration-class-toolbar-addNavigationtreeBtn'
                }
            }, {
                xtype: 'tbfill'
            }],
            null,
            [{
                xtype: 'tbtext',

                bind: {
                    hidden: '{!theNavigationtree.description}',
                    html: CMDBuildUI.locales.Locales.administration.navigationtrees.singular + ': <b data-testid="administration-navigationtree-description">{theNavigationtree.description}</b>'
                }
            }])
    }, {
        dock: 'top',
        xtype: 'container',
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            items:  
            CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true, // #editBtn set true for show the button
                view:  false, // #viewBtn set true for show the button
                open:  false, // #openBtn set true for show the button
                clone:  false, // #cloneBtn set true for show the button
                'delete':  true, // #deleteBtn set true for show the button
                activeToggle:  true, // #enableBtn and #disableBtn set true for show the buttons
                download:  false // #downloadBtn set true for show the buttons
            },
            
                /* testId */
                'navigationtree',
            
                /* viewModel object needed only for activeTogle */
                'theNavigationtree',
            
                /* add custom tools[] on the left of the bar */
                [],
            
                /* add custom tools[] before #editBtn*/
                [],
            
                /* add custom tools[] after at the end of the bar*/
                []
            ),       
            bind: {
                hidden: '{formtoolbarHidden}'
            }
        }]
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        // margin: '5 5 5 5',
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }]
});
Ext.define('CMDBuildUI.view.administration.content.bim.projects.card.ViewEdit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.bim.projects.card.ViewEditController',
        'CMDBuildUI.view.administration.content.bim.projects.card.ViewEditModel'
    ],

    alias: 'widget.administration-content-bim-projects-card-viewedit',
    controller: 'administration-content-bim-projects-card-viewedit',
    viewModel: {
        type: 'administration-content-bim-projects-view'
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    cls: 'administration tab-hidden',
    ui: 'administration-tabandtools',
   
    items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            region: 'north',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true
            }, 'bim-projects'),
            hidden: true,
            bind: {
                hidden: '{!actions.view}'
            }
        },
        {
            ui: 'administration-formpagination',
            xtype: 'container',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            items: [{
                ui: 'administration-formpagination',
                xtype: "fieldset",
                layout: 'column',
                collapsible: true,
                title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
                },
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput({
                        name: {
                            bind: {
                                value: '{theProject.name}',
                                disabled: '{actions.edit}'
                            }
                        }
                    }, true, '[name="description"]'),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                        description: {
                            bind: {
                                value: '{theProject.description}'
                            }
                        }
                    }),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getLastCheckin({
                        lastCheckin: {
                            bind: {
                                value: '{theProject.lastCheckin}',
                                disabled: '{actions.edit}'
                            }
                        }
                    }),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getParentProject({
                        parentId: {
                            displayField: 'name',
                            valueField: '_id',
                            bind: {
                                value: '{theProject.parentId}',
                                disabled: '{!actions.add}',
                                store: '{projects}'
                            }
                        }
                    }),

                    CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                        active: {
                            disabledCls: '',
                            bind: {
                                value: '{theProject.active}'
                            }
                        }
                    })
                ]
            }, {
                ui: 'administration-formpagination',
                xtype: "fieldset",
                layout: 'column',
                collapsible: true,
                title: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.gis.associatedcard'
                },
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getAssociatedClass({
                        associatedClass: {
                            forceSelection: true,
                            bind: {
                                value: '{theProject.ownerClass}',
                                disabled: '{actions.edit}',
                                store: '{getAllClassesProcessesStore}'
                            }
                        }
                    }),
                    {
                        xtype: 'fieldcontainer',
                        layout: 'column',
                        columnWidth: 0.5,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.associatedcard'
                        },
                        items: [{
                            columnWidth: 1,
                            hidden: true,
                            xtype: 'displayfield',
                            bind: {
                                hidden: '{!actions.view}',
                                value: '{theProject.cardDescription}'
                            }
                        }, {
                            columnWidth: 1,
                            hidden: true,
                            xtype: 'referencecombofield',
                            displayField: 'Description',
                            reference: 'ownerClass',
                            valueField: '_id',
                            metadata: '{theProject.ownerClass}',
                            bind: {
                                hidden: '{actions.view}',
                                value: '{theProject.ownerCard}',
                                store: '{getAssociatedCardsStore}'
                            }
                        }]
                    },
                    {
                        xtype: 'filefield',
                        name: 'fileIFC',
                        columnWidth: 0.5,
                        padding: '0 15 0 0',
                        reference: 'fileIFC',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.bim.ifcfile,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.bim.ifcfile'
                        },
                        //allowBlank: false,accept: '.zip',msgTarget: 'side',
                        buttonConfig: {
                            ui: 'administration-secondary-action-small'
                        },
                        accept: '.ifc',
                        hidden: true,
                        bind: {
                            hidden: '{actions.view}'
                        }

                    },
                    {
                        xtype: 'filefield',
                        name: 'fileMapping',
                        columnWidth: 0.5,
                        padding: '0 15 0 0',
                        reference: 'fileMapping',
                        accept: '.xml',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.bim.mappingfile,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.bim.mappingfile'
                        },
                        //allowBlank: false,accept: '.zip',msgTarget: 'side',
                        buttonConfig: {
                            ui: 'administration-secondary-action-small'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{actions.view}'
                        }

                    }
                ]
            }]
        }
    ],
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
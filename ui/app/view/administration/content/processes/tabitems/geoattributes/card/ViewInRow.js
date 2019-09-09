Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.ViewInRowModel'
    ],
    alias: 'widget.administration-content-processes-tabitems-geoattributes-card-viewinrow',
    controller: 'administration-content-processes-tabitems-geoattributes-card-viewinrow',
    viewModel: {
        type: 'administration-content-processes-tabitems-geoattributes-card-viewinrow'
    },
    config: {
        theGeoAttribute: null
    },


    cls: 'administration',
    ui: 'administration-tabandtools',
    items: [{
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',

        items: [{
            xtype: 'administration-processes-tabitems-geoattributes-card-fieldscontainers-generalproperties',
            bind: {
                actions: '{actions}'
            }
        }]
    }, {
        title: CMDBuildUI.locales.Locales.administration.geoattributes.strings.specificproperty,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.geoattributes.strings.specificproperty'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',
        items: [{
            xtype: 'administration-processes-tabitems-geoattributes-card-fieldscontainers-specificproperties',
            bind: {
                actions: '{actions}'
            }
        }]
    }],

    tools: [{
        xtype: 'tbfill'
    }, {
        xtype: 'tool',
        itemId: 'templatesEditBtn',
        reference: 'templatesEditBtn',
        iconCls: 'x-fa fa-pencil',
        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
        },
        callback: 'onEditBtnClick',
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-processes-geoattributes-card-viewInRow-editBtn'
        }
    }, {
        xtype: 'tool',
        itemId: 'templatesDeleteBtn',
        reference: 'templatesBtn',
        iconCls: 'x-fa fa-trash',
        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.delete,
        localized:{
            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.delete'
        },
        callback: 'onDeleteBtnClick',
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-processes-geoattributes-card-viewInRow-deleteBtn'
        }
    }, {
        xtype: 'tool',
        itemId: 'cloneBtn',
        glyph: 'f24d@FontAwesome', // Clone icon
        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.clone,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.clone'
        },
        callback: 'onCloneAttributeBtnClick',
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-attributes-card-view-clonebtn'
        },
        bind: {
            hidden: '{!canClone}'
        }
    }, {
        xtype: 'tool',
        itemId: 'cloneBtn',
        glyph: 'f24d@FontAwesome', // Clone icon
        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.clone,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.clone'
        },
        callback: 'onCloneBtnClick',
        cls: 'administration-tool',
        autoEl: {
            'data-testid': 'administration-attributes-card-view-clonebtn'
        },
        bind: {
            hidden: '{!canClone}'
        }
    }, {
        xtype: 'tool',
        itemId: 'disableBtn',
        cls: 'administration-tool',
        iconCls: 'x-fa fa-ban',
        tooltip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.disableattribute,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.disableattribute'
        },
        callback: 'onToggleBtnClick',
        hidden: true,
        autoEl: {
            'data-testid': 'administration-attributes-tool-disablebtn'
        },
        bind: {
            hidden: '{!theAttribute.active}'
        }
    }, {
        xtype: 'tool',
        itemId: 'enableBtn',
        hidden: true,
        cls: 'administration-tool',
        iconCls: 'x-fa fa-check-circle-o',
        tooltip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.enableattribute,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.enableattribute'
        },
        callback: 'onToggleBtnClick',
        autoEl: {
            'data-testid': 'administration-properties-tool-enablebtn'
        },
        bind: {
            hidden: '{theAttribute.active}'
        }
    }]
});
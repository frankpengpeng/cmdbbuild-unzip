Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.IconFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-classes-tabitems-properties-fieldsets-iconfieldset',

    items: [{
        xtype: 'fieldset',
        collapsible: true,
        collapsed: false,
        layout: 'column',        
        title: CMDBuildUI.locales.Locales.administration.common.labels.icon,
        localized:{
            title: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
        },
        ui: 'administration-formpagination',
        
        items: [{
            columnWidth: 0.5,
            xtype: 'fieldcontainer',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.icon,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
            },
            layout: 'column',
            items: [{
                columnWidth: 1,
                xtype: 'filefield',
                reference: 'iconFile',
                emptyText: CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile,
                localized: {
                    emptyText: 'CMDBuildUI.locales.Locales.administration.common.strings.selectpngfile'
                },
                accept: '.png',
                buttonConfig: {
                    ui: 'administration-secondary-action-small'
                },
                hidden: true,
                bind: {
                    hidden: '{actions.view}'
                }
            }, {
                xtype: 'image',
                height: 32,
                width: 32,
                alt: CMDBuildUI.locales.Locales.administration.common.labels.icon,
                localized: {
                    alt: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
                },
                itemId: 'classIconPreview',
                bind: {
                    src: '{theObject._iconPath}'
                }
            }]
        }]
    }]
});
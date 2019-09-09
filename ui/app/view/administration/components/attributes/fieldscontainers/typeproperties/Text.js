Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Text', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-textfields',
    items: [ {
        // add / edit
        xtype: 'container',
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
            layout: 'column',

            items: [{
                columnWidth: 0.5,
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype'
                },
                name: 'editorType',
                clearFilterOnBlur: false,
                anyMatch: true,
                autoSelect: true,
                forceSelection: true,
                typeAhead: true,
                queryMode: 'local',
                displayField: 'label',
                valueField: 'value',
                bind: {
                    value: '{theAttribute.editorType}',
                    store: '{editorTypeStore}'
                },
                renderer: CMDBuildUI.util.administration.helper.RendererHelper.getEditorType
            }]
        }]
    }, {
        // view
        xtype: 'container',
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            layout: 'column',

            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype'
                },
                bind: {
                    value: '{theAttribute.editorType}'
                },
                renderer: CMDBuildUI.util.administration.helper.RendererHelper.getEditorType
            }]
        }]
    }]
});
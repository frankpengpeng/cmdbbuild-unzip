Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Timestamp', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-timestampfields',

    items: [{
        xtype: 'fieldcontainer',
        items: [{
            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showseconds,
            disabledCls: '',
            bind: {
                value: '{theAttribute.showSeconds}',
                disabled: '{actions.view}'
            }
        }]
    }]
});
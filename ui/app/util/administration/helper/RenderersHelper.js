Ext.define('CMDBuildUI.util.administration.helper.RendererHelper', {

    requires: [
        'Ext.util.Format'
    ],

    singleton: true,

    getDisplayPassword: function (value) {
        if (value) {
            var pswlength = value.length;
            var pswstring = '';
            var i;
            for (i = 0; i < pswlength; i++) {
                pswstring += '*';
            }
            return pswstring;
        }
    },

    getIpType: function (value) {
        if (value) {
            switch (value) {
                case 'ipv4':
                    return CMDBuildUI.locales.Locales.administration.attributes.strings.ipv4;
                case 'ipv6':
                    return CMDBuildUI.locales.Locales.administration.attributes.strings.ipv6;
                case 'any':
                    return CMDBuildUI.locales.Locales.administration.attributes.strings.any;
            }
        }
    },

    getEditorType: function (value, metaData, record, rowIndex, colIndex, store, view) {
        switch (value) {
            case 'HTML':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.editorhtml;
            case 'PLAIN':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.plaintext;
            default:
                return value;
        }
    },
    getAttributeMode: function (value, metaData, record, rowIndex, colIndex, store, view) {
        switch (value) {
            case 'write':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.editable;
            case 'read':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.readonly;
            case 'hidden':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.hidden;
            case 'immutable':
                return CMDBuildUI.locales.Locales.administration.attributes.strings.immutable;
        }
    },

    getAttributeType: function (value, metaData, record, rowIndex, colIndex, store, view) {
        if (value) {
            var attributeTypesStore = Ext.getStore('attributes.AttributeTypes');
            try {
                return attributeTypesStore.findRecord('value', value).get('label');
            } catch (e) {
                CMDBuildUI.util.Notifier.showErrorMessage('Somthing wrong on atrribute type: ' + value);
            }
        }
    },

    getAttachmentDescriptionMode: function (value, element, record, rowIndex, colIndex, store, view) {
        // get label from store
        if (value) {
            var descriptionmodeStore = element.up().down('combo').getStore();
            var descriptionmodeRecord = descriptionmodeStore.findRecord('value', value) || null;
            return descriptionmodeRecord ? descriptionmodeRecord.get('label') : '<em>' + value + '</em>';
        }
        return value;
    },

    getEmailContentType: function (value, metaData, record, rowIndex, colIndex, store, view) {
        // get label from store
        var contetTypesStore = Ext.getStore('administration.emails.ContentTypes');
        var contetTypesRecord = contetTypesStore.findRecord('value', value) || null;
        return contetTypesRecord ? contetTypesRecord.get('label') : '<em>' + value + '</em>';
    }
});
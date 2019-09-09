Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ViewModel', {

    imports: [
        'CMDBuildUI.util.Utilities'
    ],

    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-lookuptypes-tabitems-values-card-view',

    formulas: {
        iconTypeIsImage: {
            bind: '{theValue.icon_type}',
            get: function (iconType) {
                if (iconType && iconType === 'image') {
                    return true;
                }
                return false;
            }
        },
        iconTypeIsFont: {
            bind: '{theValue.icon_type}',
            get: function (iconType) {
                if (iconType && iconType === 'font') {
                    return true;
                }
                return false;
            }
        },
        parentLookupValuesProxy: {
            bind: '{theValue.parent_type}',
            get: function (parentType) {
                if (parentType) {
                    return {
                        url: Ext.String.format("/lookup_types/{0}/values", CMDBuildUI.util.Utilities.stringToHex(parentType)),
                        type: 'baseproxy',
                        extraParams: {
                            active: false
                        }
                    };
                }
            }
        },
        panelTitle: {
            bind: '{theValue.description}',
            get: function (description) {
                if (description) {
                    var title = Ext.String.format(
                        // '{0} - {1} - {2}',
                        '{0} - {2}',
                        this.get('lookupTypeName'),
                        'Value', // TODO: translate
                        this.getData().theValue.get('description')
                    );
                    this.getParent().set('title', title);
                }
            }
        }
    }
});
Ext.define('CMDBuildUI.view.administration.components.attributes.grid.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-attributes-grid-grid',
    data: {
        search: {
            value: null
        },
        selected: null,
        isOtherPropertiesHidden: true
    },

    formulas: {
        pluralObjectType: {
            bind: '{objectType}',
            get: function(objectType){
                return Ext.util.Inflector.pluralize(objectType).toLowerCase();
            }
        },
        canDelete: {
            bind: {
                isInherited: '{theAttribute.inherited}'
            },
            get: function (data) {
                return (data.isInherited) ? false : true;
            }
        },
        setCurrentType: {
            bind: '{theAttribute.type}',
            get: function (type) {
                this.set('types.isDate', type === 'date'); 
                this.set('types.isDatetime', type === 'dateTime'); 
                this.set('types.isDecimal', type === 'decimal'); 
                this.set('types.isDouble', type === 'double'); 
                this.set('types.isForeignkey', type === 'foreignKey'); 
                this.set('types.isInteger', type === 'integer'); 
                this.set('types.isIpAddress', type === 'ipAddress'); 
                this.set('types.isLookup', type === 'lookup'); 
                this.set('types.isReference', type === 'reference'); 
                this.set('types.isString', type === 'string'); 
                this.set('types.isText', type === 'text'); 
                this.set('types.isTime', type === 'time'); 
                this.set('types.isTimestamp', type === 'timestamp'); 
            }
        },
        allAtrributesGorups: function (get) {
            var allAttributesStore = get('allAttributesStore');

            var groups = [],
                data = [];

            Ext.Array.each(allAttributesStore, function (attribute) {
                var attributeData = attribute.getData();
                if (attributeData.group && attributeData.group.length > 0) {
                    if (!Ext.Array.contains(groups, attributeData.group)) {
                        Ext.Array.include(groups, attributeData.group);
                        Ext.Array.include(data, {
                            label: attributeData.group,
                            value: attributeData.group
                        });
                    }
                }
            });

            return data;
        },

        isObjectyTypeNameSet: {
            bind: '{theProcess.name}',
            get: function (objectTypeName) {
                if (objectTypeName) {
                    return true;
                }
                return false;
            }
        }
    }

});
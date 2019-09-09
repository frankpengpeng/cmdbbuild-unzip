Ext.define('CMDBuildUI.view.administration.components.attributes.actionscontainers.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-attributes-actionscontainers-card',
    data: {
        attributeGroups: [],
        attributes: [],
        theAttribute: null,
        isOtherPropertiesHidden: true,
        isGroupHidden: true,
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        types: {
            isReference: false,
            isLookup: false,
            isDecimal: false,
            isDouble: false,
            isInteger: false,
            isText: false,
            isString: false,
            isIpAddress: false,
            isDate: false,
            isDatetime: false,
            isForeignkey: false,
            isTime: false,
            isTimestamp: false
        }
    },

    formulas: {
        getAllPages: {
            bind: '{theAttribute}',
            get: function (theAttribute) {
                var data = [];
                var types = {
                    classes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.classes,
                        childrens: Ext.getStore('classes.Classes').getData().getRange().filter(function (item) { return item.get('type') !== 'simple' && item.get('name') !== 'Class'; })
                    }
                };
                // if workflow is disabled we can't collect processes items
                var wfEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled);
                if(wfEnabled){
                    types.processes = {
                        label: CMDBuildUI.locales.Locales.administration.navigation.processes,
                        childrens: Ext.getStore('processes.Processes').getData().getRange()
                    };
                }
                Object.keys(types).forEach(function (type, typeIndex) {
                    types[type].childrens.forEach(function (value, index) {
                        var item = {
                            group: type,
                            groupLabel: types[type].label,
                            _id: value.get('_id'),
                            label: value.get('description')
                        };
                        data.push(item);
                    });
                });
                data.sort(function (a, b) {
                    var aGroup = a.group.toUpperCase();
                    var bGroup = b.group.toUpperCase();
                    var aLabel = a.label.toUpperCase();
                    var bLabel = b.label.toUpperCase();

                    if (aGroup === bGroup) {
                        return (aLabel < bLabel) ? -1 : (aLabel > bLabel) ? 1 : 0;
                    } else {
                        return (aGroup < bGroup) ? -1 : 1;
                    }
                });
                return data;
            }
        },
        actions: {
            bind: '{action}',
            get: function (action) {
                return {
                    add: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    edit: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    view: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                };
            }
        },
        isGroupHiddenOrView: {
            bind: {
                isGroupHidden: '{isGroupHidden}'
            },
            get: function (data) {
                if (data.isGroupHidden || this.get('actions.view')) {
                    return true;
                }
                return false;
            }
        },
        isGroupHiddenOrNotView: {
            bind: {
                isGroupHidden: '{isGroupHidden}'
            },
            get: function (data) {
                if (data.isGroupHidden || !this.get('actions.view')) {
                    return true;
                }
                return false;
            }
        },
        pluralObjectType: {
            bind: '{objectType}',
            get: function (objectType) {
                return objectType && Ext.util.Inflector.pluralize(objectType).toLowerCase();
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
                this.set('types.isTimestamp', type === 'dateTime');
            }
        },
        panelTitle: {
            bind: '{theAttribute.name}',
            get: function (attributeName) {
                if (this.get('theAttribute') && !this.get('theAttribute').phantom) {
                    var title = Ext.String.format(
                        '{0} - {1} - {2}',
                        this.get('objectTypeName'),
                        CMDBuildUI.locales.Locales.administration.attributes.attributes,
                        attributeName
                    );
                    this.getParent().set('title', title);
                } else {
                    this.getParent().set('title', CMDBuildUI.locales.Locales.administration.attributes.texts.newattribute);
                }
            }
        },
        attributeGroups: {
            bind: '{attributes}',
            get: function (attributes) {
                var attributeGroups = [],
                    data = [];
                var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(this.get('objectTypeName'));
                if (obj) {
                    attributeGroups = obj.attributeGroups().getRange();
                } else {
                    Ext.Array.each(attributes, function (attribute) {
                        if (attribute.get('group') && attribute.get('group').length > 0) {
                            if (!Ext.Array.contains(data, attribute.get('group'))) {
                                Ext.Array.include(data, attribute.get('group'));
                                Ext.Array.include(attributeGroups, {
                                    description: attribute.get('_group_description'),
                                    name: attribute.get('group')
                                });
                            }
                        }
                    });
                }
                return attributeGroups;
            }
        },

        domainExtraparams: {
            bind: '{objectTypeName}',
            get: function (objectTypeName) {
                var type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectTypeName);
                if (type && type !== CMDBuildUI.util.helper.ModelHelper.objecttypes.domain) {
                    var filter = JSON.stringify({
                        "attribute": {
                            "or": [{
                                "and": [{
                                    "simple": {
                                        "attribute": "destination",
                                        "operator": "contain",
                                        "value": objectTypeName
                                    }
                                }, {
                                    "simple": {
                                        "attribute": "cardinality",
                                        "operator": "in",
                                        "value": ["1:N", "1:1"]
                                    }
                                }]
                            }, {
                                "simple": {
                                    "attribute": "source",
                                    "operator": "contain",
                                    "value": objectTypeName
                                }
                            }]
                        }
                    });
                    return filter;
                }
                return [];

            }
        },
        filterAttributeTypesStoreByObjectType: {
            bind: {
                objectType: '{objectType}'
            },
            get: function (data) {
                if (data.objectType) {
                    switch (data.objectType) {
                        case 'Class':
                            var theClassType = this.get('grid').up().getViewModel().get('theObject.type');
                            return [function (item) {
                                return (theClassType === 'standard') ? item.get('_isForStandardClass') : item.get('_isForSimpleClass');
                            }];
                        default:
                            return [function (item) {
                                return item.get('_isFor' + data.objectType);
                            }];
                    }

                } else {
                    return [];
                }
            }
        }
    },

    stores: {
        getAllPagesStore: {
            data: '{getAllPages}',
            autoDestroy: true
        },
        domainsStore: {
            model: "CMDBuildUI.model.domains.Domain",
            proxy: {
                type: 'baseproxy',
                url: '/domains/',
                extraParams: {
                    filter: '{domainExtraparams}',
                    ext: true
                }
            },
            autoLoad: true,
            autoDestroy: true,
            fields: ['_id', 'name'],
            pageSize: 0
        },
        lookupStore: {
            model: "CMDBuildUI.model.lookups.Lookup",
            proxy: {
                type: 'baseproxy',
                url: '/lookup_types/'
            },
            autoLoad: true,
            autoDestroy: true,
            fields: ['_id', 'name'],
            pageSize: 0
        },
        attributeGroupStore: {
            model: "CMDBuildUI.model.Attribute",
            proxy: {
                type: 'memory'
            },
            data: '{attributeGroups}',
            fields: ['label', 'value'],
            autoDestroy: true
        },
        attributeModeStore: {
            data: [{
                value: "write",
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.editable
            },
            {
                value: "read",
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.readonly
            },
            {
                value: "hidden",
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.hidden
            },
            {
                value: "immutable",
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.immutable
            }
            ],
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label']
        },
        editorTypeStore: {
            data: [{
                value: "PLAIN",
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.plaintext
            }, {
                value: "HTML",
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.editorhtml
            }],
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label']
        },
        ipTypeStore: {
            data: [{
                value: "ipv4",
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.ipv4
            }, {
                value: "ipv6",
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.ipv6
            }, {
                value: "any",
                label: CMDBuildUI.locales.Locales.administration.attributes.strings.any
            }],
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label']
        },
        directionStore: {
            data: [{
                value: "",
                label: ""
            }, {
                value: "direct",
                label: CMDBuildUI.locales.Locales.administration.attributes.texts.direct
            }, {
                value: "inverse",
                label: CMDBuildUI.locales.Locales.administration.attributes.texts.inverse
            }]
        },
        attributetypesStore: {
            type: 'chained',
            source: 'attributes.AttributeTypes',
            filters: '{filterAttributeTypesStoreByObjectType}'
        }
    }
});
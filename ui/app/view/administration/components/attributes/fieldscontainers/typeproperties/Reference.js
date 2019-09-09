Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Reference', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-referencefields',

    items: [{
        // add
        xtype: 'container',
        bind: {
            hidden: '{!actions.add}'
        },
        items: [{
            layout: 'column',

            items: [{
                columnWidth: 0.5,

                xtype: 'combobox',
                name: 'domain',
                clearFilterOnBlur: true,
                queryMode: 'local',
                displayField: 'name',
                valueField: 'name',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domain,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domain'
                },
                bind: {
                    value: '{theAttribute.domain}',
                    disabled: '{actions.edit}',
                    store: '{domainsStore}'
                },
                renderer: function (value) {
                    if (value) {
                        var domainStore = Ext.getStore('domains.Domains');
                        if (domainStore) {
                            var domain = domainStore.getById(value);
                            if (domain) {
                                return domain.get('description');
                            }
                        }

                    }
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter'
                },
                name: 'filter',
                bind: {
                    value: "{theAttribute.filter}",
                    readOnly: '{actions.view}'
                },
                labelToolIconCls: 'fa-list',
                labelToolIconQtip: 'Add metadata',
                labelToolIconClick: 'onEditMetadataClickBtn'
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique'
                },
                name: 'preselectIfUnique',
                bind: {
                    value: '{theAttribute.preselectIfUnique}',
                    readOnly: '{actions.view}'
                }
            }]
        }]
    }, {
        // edit
        xtype: 'container',
        bind: {
            hidden: '{!actions.edit}'
        },
        items: [{
            layout: 'column',
            items: [{
                xtype: 'fieldcontainer',
                columnWidth: 0.5,
                layout: 'column',
                items: [{
                    columnWidth: 1,
                    xtype: 'combobox',
                    name: 'domain',
                    itemId: 'attributedomain',
                    clearFilterOnBlur: true,
                    queryMode: 'local',
                    displayField: 'name',
                    valueField: 'name',
                    disabled: true,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domain,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domain'
                    },
                    bind: {
                        value: '{theAttribute.domain}',
                        store: '{domainsStore}'
                    },
                    renderer: function (value) {
                        if (value) {
                            var domainStore = Ext.getStore('domains.Domains');
                            if (domainStore) {
                                var domain = domainStore.getById(value);
                                if (domain) {
                                    return domain.get('description');
                                }
                            }

                        }
                    }
                }, {
                    xtype: 'combobox',
                    name: 'domain',
                    clearFilterOnBlur: true,
                    queryMode: 'local',
                    displayField: 'name',
                    valueField: 'name',
                    itemId: 'domaindirection',
                    disabled: true,
                    hidden: true,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.direction,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.direction'
                    },
                    bind: {
                        value: '{theAttribute.direction}',
                        store: '{directionStore}'
                    }
                }]
            }, {
                columnWidth: 0.5,
                xtype: 'textarea',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter'
                },
                name: 'filter',
                bind: {
                    value: "{theAttribute.filter}"
                },
                labelToolIconCls: 'fa-list',
                labelToolIconQtip: 'Edit metadata',
                labelToolIconClick: 'onEditMetadataClickBtn'
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique'
                },
                name: 'preselectIfUnique',
                bind: {
                    value: '{theAttribute.preselectIfUnique}'
                }
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
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domain,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.domain'
                },
                bind: {
                    value: '{theAttribute.domain}'
                },
                renderer: function (value) {
                    var domainStore = Ext.getStore('domains.Domains');
                    if (domainStore) {
                        var domain = domainStore.getById(value);
                        if (domain) {
                            return domain.get('description');
                        }
                        return value;
                    }
                }
            }, {
                columnWidth: 0.5,
                xtype: 'textarea',
                itemId: 'attribute-filterField',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.filter'
                },
                name: 'filter',
                readOnly: true,
                bind: {
                    value: "{theAttribute.filter}"
                },

                labelToolIconCls: 'fa-list',
                labelToolIconQtip: 'Show metadata',
                labelToolIconClick: 'onViewMetadataClick'
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.preselectifunique'
                },
                name: 'preselectIfUnique',
                bind: {
                    value: '{theAttribute.preselectIfUnique}',
                    readOnly: '{actions.view}'
                }
            }]

        }]
    }]

});
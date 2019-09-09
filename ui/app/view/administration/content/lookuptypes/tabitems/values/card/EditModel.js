Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.view-administration-content-lookuptypes-tabitems-values-card-edit',

    data: {
        actions: {
            view: true,
            add: false,
            edit: true
        },
        valueIconType: {
            isImageOrNone: false,
            isFontOrNone: false,
            isNone: false
        },
        isIconFileRequired: false,
        parentTypeName: null,
        storedata: {
            url: null,
            autoLoad: false
        },
        theTranslation: null
    },

    formulas: {
        action: {
            bind: {
                view: '{actions.view}',
                add: '{actions.add}',
                edit: '{actions.edit}'
            },
            get: function (data) {
                if (data.edit) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.add) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },
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
        parentDescriptionData: {
            bind: {
                parentId: '{theValue.parent_id}',
                store: '{parentLookupsStore}'
            },
            get: function (data) {
                var me = this;
                if (data.store.getData().getRange().length) {
                    var parent = data.store.getData().getRange().find(function (element) {
                        return element.get('_id') === data.parentId.toString();
                    });
                    me.set('parentDescription', (parent) ? parent.get('description') : '');
                } else if (!data.store.isLoaded()) {
                    data.store.on('load', function () {
                        var parent = data.store.getData().getRange().find(function (element) {
                            return element.get('_id') === data.parentId.toString();
                        });
                        me.set('parentDescription', (parent) ? parent.get('description') : '');
                    });
                }

            }
        },
        iconTypeManager: {
            bind: {
                iconType: '{theValue.icon_type}',
                iconImage: '{theValue.icon_image}'
            },
            get: function (data) {
                this.set('valueIconType.isImageOrNone', data.iconType && (data.iconType === 'image' || data.iconType === 'none'));
                this.set('valueIconType.isFontOrNone', data.iconType && (data.iconType === 'font' || data.iconType === 'none'));
                this.set('valueIconType.isNone', data.iconType && data.iconType === 'none');
                this.set('isIconFileRequired', data.iconType && data.iconType === 'image' && !data.iconImage.length);
                if (Ext.ComponentQuery.query('#lookupValueIconFont').length) {
                    switch (data.iconType) {
                        case "none":
                            Ext.ComponentQuery.query('#lookupValueIconFont')[0].allowBlank = true;
                            Ext.ComponentQuery.query('#lookupValueIconColor')[0].allowBlank = true;
                            Ext.ComponentQuery.query('#lookupValueImage')[0].allowBlank = true;
                            break;
                        case "image":
                            Ext.ComponentQuery.query('#lookupValueIconFont')[0].allowBlank = true;
                            Ext.ComponentQuery.query('#lookupValueIconColor')[0].allowBlank = true;
                            if (!this.get('theValue.icon_image')) {
                                Ext.ComponentQuery.query('#lookupValueImage')[0].allowBlank = false;
                            } else {
                                Ext.ComponentQuery.query('#lookupValueImage')[0].allowBlank = true;
                            }
                            break;
                        case "font":
                            Ext.ComponentQuery.query('#lookupValueIconFont')[0].allowBlank = false;
                            Ext.ComponentQuery.query('#lookupValueIconColor')[0].allowBlank = false;
                            Ext.ComponentQuery.query('#lookupValueImage')[0].allowBlank = true;
                            break;
                    }
                    if (this.getView().form) {
                        this.getView().form.checkValidity();
                    }
                }

            }
        },

        parentLookupValuesProxy: {
            bind: '{parentTypeName}',
            get: function (parentTypeName) {
                if (parentTypeName) {
                    this.set('storedata.url', Ext.String.format("/lookup_types/{0}/values", CMDBuildUI.util.Utilities.stringToHex(parentTypeName)));
                    this.set('storedata.autoLoad', true);
                    this.set('isParentDescriptionDisabled', false);
                } else {
                    this.set('isParentDescriptionDisabled', true);
                }
            }
        },

        panelTitle: {
            bind: '{theValue.description}',
            get: function (description) {
                var title;
                if (description) {
                    title = Ext.String.format(
                        '{0} - {1}',
                        CMDBuildUI.util.helper.SessionHelper.getViewportVM().getView().down('administration-content-lookuptypes-tabitems-type-properties').lookupViewModel().get('theLookupType.name'),
                        this.getData().theValue.get('description')
                    );

                } else {
                    title = Ext.String.format(
                        '{0}',
                        CMDBuildUI.util.helper.SessionHelper.getViewportVM().getView().down('administration-content-lookuptypes-tabitems-type-properties').lookupViewModel().get('theLookupType.name')
                    );
                }
                this.getParent().set('title', title);
            }
        }
    },
    stores: {
        parentLookupsStore: {
            model: "CMDBuildUI.model.lookups.Lookup",
            proxy: {
                url: '{storedata.url}',
                type: 'baseproxy'
            },
            extraParams: {
                active: false
            },
            pageSize: 0, // disable pagination
            fields: ['_id', 'description'],
            autoLoad: '{storedata.autoLoad}',
            sorters: [
                'description'
            ]
        }
    }

});
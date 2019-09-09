Ext.define('CMDBuildUI.view.administration.content.setup.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-view',

    data: {
        classList: [],
        functionList: [],
        isClassMode: false,
        isEditBtnHidden: false,
        isEditButtonDisabled: false,
        isFunctionMode: false,
        isMultitenantEnabled: false,
        theSetup: {},
        actions: {
            view: true,
            edit: false
        }
    },
    formulas: {

        allPagesData: {
            get: function (get) {
                var data = [];
                var types = {
                    classes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.classes,
                        childrens: Ext.getStore('classes.Classes').getData().getRange()
                    },
                    processes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.processes,
                        childrens: Ext.getStore('processes.Processes').getData().getRange()
                    },
                    dashboards: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.dashboards,
                        childrens: Ext.getStore('Dashboards').getData().getRange()
                    },
                    custompages: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.custompages,
                        childrens: Ext.getStore('custompages.CustomPages').getData().getRange()
                    },
                    views: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.views,
                        childrens: Ext.getStore('views.Views').getData().getRange()
                    }
                };
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
        editBtnManager: {
            bind: {
                //isEditBtnHidden: '{isEditBtnHidden}',
                multiTenantEnabled: '{multiTenantEnabled}',
                currentPage: '{currentPage}',
                isEdit: '{actions.edit}'
            },
            get: function (data) {
                if (data.isEdit || data.currentPage === 'servermanagement') {
                    this.set('isEditBtnHidden', true);
                } else if (data.multiTenantEnabled && data.currentPage === 'multitenant') {
                    this.set('isEditBtnHidden', true);
                    this.set('isEditButtonDisabledVisible', true);
                } else {
                    this.set('isEditBtnHidden', false);
                    this.set('isEditButtonDisabledVisible', false);
                }
            }
        },
        multitenantConfigurationModeComboHidden: {
            bind: {
                multitenantMode: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}',
                editMode: '{actions.edit}'
            },
            get: function (data) {
                if (data.multitenantMode === 'DISABLED' || !data.editMode) {
                    return true;
                }
                return false;
            }
        },
        multitenantConfigurationModeDisplayHidden: {
            bind: {
                multitenantMode: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}',
                editMode: '{actions.edit}'
            },
            get: function (data) {
                if (data.multitenantMode === 'DISABLED' || data.editMode) {
                    return true;
                }
                return false;
            }
        },
        multitenantConfigurationClassComboHidden: {
            bind: {
                multitenantMode: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}',
                editMode: '{actions.edit}'
            },
            get: function (data) {
                if (data.multitenantMode === 'DISABLED' || data.multitenantMode === 'DB_FUNCTION' || !data.editMode) {
                    return true;
                }
                return false;
            }
        },
        multitenantConfigurationClassDisplayHidden: {
            bind: {
                multitenantMode: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}',
                editMode: '{actions.edit}'
            },
            get: function (data) {
                if (data.multitenantMode === 'DISABLED' || data.multitenantMode === 'DB_FUNCTION' || data.editMode) {
                    return true;
                }
                return false;
            }
        },
        /**
         * Multitenant configuration
         */

        multiTenantEnabled: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}',
            get: function (mode) {
                if (mode) {
                    switch (mode) {
                        case 'DISABLED':
                        case '':
                            this.set('isClassMode', false);
                            this.set('isFunctionMode', false);
                            return false;
                        case 'CMDBUILD_CLASS':
                            this.set('isClassMode', true);
                            this.set('isFunctionMode', false);
                            return true;
                        case 'DB_FUNCTION':
                            this.set('isClassMode', false);
                            this.set('isFunctionMode', true);
                            return true;
                        default:
                            return false;
                    }
                }
            },
            set: function (value) {
                switch (value) {
                    case true:
                        this.set('theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode', 'CMDBUILD_CLASS');
                        break;
                    default:
                        this.set('theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode', 'DISABLED');
                        break;
                }
            }
        },
        defaultProvider: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__provider}',
            get: function (mode) {
                if (mode) {
                    switch (mode) {
                        case 'shark':
                            this.set('isSharkDefault', true);
                            this.set('isRiverDefault', false);
                            break;
                        case 'river':
                            this.set('isSharkDefault', false);
                            this.set('isRiverDefault', true);
                            break;
                    }
                }
            },
            set: function (value) {

            }
        },
        sharkDefaultFieldDisabled: {
            bind: {
                sharkEnabled: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__shark__DOT__enabled}',
                riverEnabled: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__river__DOT__enabled}'
            },
            get: function (data) {
                if (data.sharkEnabled.toString() !== 'true' || data.riverEnabled.toString() !== 'true') {
                    return true;
                }
                return false;
            }
        },
        riverDefaultFieldDisabled: {
            bind: {
                sharkEnabled: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__shark__DOT__enabled}',
                riverEnabled: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__river__DOT__enabled}'
            },
            get: function (data) {
                if (data.sharkEnabled.toString() !== 'true' || data.riverEnabled.toString() !== 'true') {
                    return true;
                }
                return false;
            }
        },
        logo: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__companyLogo}',
            get: function (logoId) {
                if (logoId) {
                    var logoUrl = Ext.String.format('{0}/uploads/{1}/download', CMDBuildUI.util.Config.baseUrl, logoId);

                    return logoUrl;

                }

            }
        }
    },

    stores: {
        getAllPagesStore: {
            data: '{allPagesData}',
            autoDestroy: true
        },
        getConfigurationModeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: [{
                value: 'CMDBUILD_CLASS',
                label: 'Connected to a class' // TODO: translate
            }, {
                value: 'DB_FUNCTION',
                label: 'Connected to a function'
            }],
            autoDestroy: true
        },
        getFilteredClasses: {
            model: 'CMDBuildUI.model.classes.Class',
            autoLoad: true,
            autoDestroy: true,
            sorters: ['description'],
            pageSize: 0,
            filters: [function (item) {
                return item.data.name !== 'Class';
            }]
        },
        getPreferredOfficeSuitesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: [{
                value: 'default',
                label: 'Default' // TODO: translate
            }, {
                value: 'msoffice',
                label: 'Microsoft Office'
            }]
        }
    },

    /**
     * Change form mode
     * 
     * @param {String} mode
     */
    setFormMode: function (mode) {
        var me = this;
        if (me.get('actions.edit') && mode === CMDBuildUI.util.administration.helper.FormHelper.formActions.view) {
            this.set('isEditBtnHidden', false);
        }

        switch (mode) {
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
                me.set('actions.view', false);
                me.set('actions.edit', true);
                break;

            default:
                me.set('actions.view', true);
                me.set('actions.edit', false);
                break;
        }
    }
});
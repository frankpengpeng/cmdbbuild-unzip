Ext.define('CMDBuildUI.view.administration.content.views.card.FormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-views-card-form',
    data: {
        name: 'CMDBuildUI',
        showForm: false,
        viewType: null,
        actions: {
            view: true,
            edit: false,
            add: false
        }
    },

    formulas: {
        isFormHidden: {
            bind: {
                theViewFilter: '{theViewFilter}',
                showForm: '{showForm}'
            },
            get: function (data) {
                if (data.theViewFilter && data.theViewFilter.phantom && data.showForm == 'false') {
                    return true;
                }
                return false;
            }
        },
        isFormButtonsBarHidden: {
            bind: {
                isView: '{actions.view}',
                isFormHidden: '{isFormHidden}'
            },
            get: function (data) {
                return data.isFormHidden || data.isView;
            }
        },
        title: {
            bind: {
                description: '{theViewFilter.description}'
            },
            get: function (data) {
                var me = this;
                me.getParent().set('title', Ext.String.format('View from {0} {1}', me.get('theViewFilter.type'), (data.description) ? ' - ' + data.description : ''));
            }
        },
        viewTypeManager: {
            bind: {
                theViewFilter: '{theViewFilter}',
                description: '{theViewFilter.description}'
            },
            get: function (data) {
                if (data.theViewFilter) {
                    var me = this;
                    if (data.theViewFilter.get('type') === 'SQL') {
                        me.set('isSqlType', true);
                        me.set('viewType', 'SQL');
                        return 'SQL';
                    } else {
                        me.set('isSqlType', false);
                        me.set('viewType', 'FILTER');
                        return 'FILTER';
                    }
                }
            }
        },
        action: {
            bind: {
                isView: '{actions.view}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}'
            },
            get: function (data) {
                if (data.isView) {
                    this.set('formModeCls', 'formmode-view');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit) {
                    this.set('formModeCls', 'formmode-edit');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    this.set('formModeCls', 'formmode-add');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },

        getAllPages: {
            bind: {},
            get: function (theViewFilter) {
                var data = [];
                var types = {
                    classes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.classes,
                        childrens: Ext.getStore('classes.Classes').getData().getRange()
                    },
                    processes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.processes,
                        childrens: Ext.getStore('processes.Processes').getData().getRange()
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
        }
    },

    stores: {
        getAllPagesStore: {
            data: '{getAllPages}',
            autoDestroy: true
        },

        getFunctionsStore: {
            model: 'CMDBuildUI.model.Function',
            sorters: ['description'],
            pageSize: 0, // disable pagination
            autoLoad: true
        }
    }

});
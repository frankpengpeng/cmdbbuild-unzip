Ext.define('CMDBuildUI.view.administration.content.navigationtrees.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-navigationtrees-view',

    data: {
        actions: {
            view: false,
            edit: false,
            add: false
        },
        hideForm: false
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
        allStandardClass: {
            get: function () {
                var data = [];
                Ext.getStore('classes.Classes').getData().getRange().forEach(function (value, index) {
                    if (value.get('name') !== 'Class') {
                        var item = {
                            value: value.get('_id'),
                            label: value.get('_description_translation') || value.get('description')
                        };
                        data.push(item);
                    }
                });
                return data;
            }
        },
        dataManager: {
            bind: {
                theNavigationtree:'{theNavigationtree}',
                targetClass: '{theNavigationtree.targetClass}'
            },
            get: function (data) {                
                if (!data.targetClass && data.theNavigationtree.get('nodes') && data.theNavigationtree.get('nodes').length) {
                    data.theNavigationtree.set('targetClass', data.theNavigationtree.get('nodes')[0].targetClass);
                } else {
                    data.theNavigationtree.set('targetClass', data.targetClass);
                }
            }
        },
        formtoolbarHidden: {
            bind: {
                isView: '{actions.view}',
                isHiddenForm: '{hideForm}'
            },
            get: function (data) {
                if (data.isView && !data.isHiddenForm) {
                    return false;
                }
                return true;
            }
        }
    },

    stores: {
        getAllStandardClassStore: {
            data: '{allStandardClass}',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true
        }
    }
});
Ext.define('CMDBuildUI.view.administration.content.gisnavigationtrees.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gisnavigationtrees-view',

    data: {
        actions: {
            view: false,
            edit: false,
            add: false
        },
        hideForm: false
    },
    formulas: {
        allStandardClass: {
            get: function () {
                var data = [];
                Ext.getStore('classes.Classes').getData().getRange().forEach(function (value, index) {
                    var item = {
                        value: value.get('_id'),
                        label: value.get('_description_translation') || value.get('description')
                    };
                    data.push(item);
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
    },
    /**
     * Change form mode
     * 
     * @param {String} mode
     */
    setFormMode: function (mode) {
        var me = this;
        switch (mode) {
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.view:
                me.set('actions.view', true);
                me.set('actions.edit', false);
                me.set('actions.add', false);
                break;
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.add:
                me.set('actions.view', false);
                me.set('actions.edit', false);
                me.set('actions.add', true);
                break;
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
                me.set('actions.view', false);
                me.set('actions.edit', true);
                me.set('actions.add', false);
                break;

        }
    }
});
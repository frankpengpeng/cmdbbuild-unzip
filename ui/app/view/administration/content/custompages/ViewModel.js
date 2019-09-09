Ext.define('CMDBuildUI.view.administration.content.custompages.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-custompages-view',

    data: {
        theTranslation: false,
        actions: {
            view: false,
            edit: false,
            add: false
        },
        hideForm: false
    },
    formulas: {
        custompageLabel: {
            bind: '{theCustompage.description}',
            get: function(){
                return CMDBuildUI.locales.Locales.administration.custompages.singular;
            }
        },
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

    /**
     * Change form mode
     * 
     * @param {String} mode
     */
    setFormMode: function (mode) {
        var me = this;
        // var inputField = me.getView().lookupReference('file');
        me.set('action', mode);
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
                // inputField.allowBlank = false;
                // inputField.updateLayout();

                break;
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
                me.set('actions.view', false);
                me.set('actions.edit', true);
                me.set('actions.add', false);
                // inputField.allowBlank = true;
                // inputField.el.dom.innerHTML = inputField.el.dom.innerHTML.replace(' *', '');
                break;

        }
    }
});
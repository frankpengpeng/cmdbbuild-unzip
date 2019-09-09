Ext.define('CMDBuildUI.view.administration.content.reports.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-reports-view',

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
        componentTypeName: {
            bind: '{theReport}',
            get: function(){
                return CMDBuildUI.locales.Locales.administration.reports.singular;
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
            get: function(data){
                if(data.isView && !data.isHiddenForm){
                    return false;
                }
                return true;
            }
        }
    }    
});
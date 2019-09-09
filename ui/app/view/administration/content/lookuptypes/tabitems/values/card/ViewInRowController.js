Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabitems-values-card-viewinrow',
    
    mixins: [
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ToolsMixin'
    ],
    control: {
        '#': {
            beforerender: 'onBeforeRender',
            itemupdated: 'onLookupValueUpdated'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        }

    },

    /**
     * @param {CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        this.linkLookupValue();
        this.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        var typeView = Ext.ComponentQuery.query('viewport')[0].down('administration-content-lookuptypes-tabitems-type-properties');
        if (typeView) {
            var typeViewModel = typeView.lookupViewModel();
            view.getViewModel().set('parentTypeName', typeViewModel.get('theLookupType.parent'));
        }
    },
    onLookupValueUpdated: function (v, record) {
        this.linkLookupValue();
    },

    privates: {
        /**
         * Link the lookup value  
         */
        linkLookupValue: function () {
            var view = this.getView();
            var vm = this.getViewModel();
            var config = view.getInitialConfig();
            if(config._rowContext){
                var record = config._rowContext.ownerGrid.selection || config._rowContext.record; // get atttribute record
                var lookupType = Ext.getCmp('CMDBuildAdministrationContentLookupTypesView').getViewModel().get('objectTypeName');
    
                if (record && record.getData()) {
                    if (lookupType) {
                        record.getProxy().setExtraParam('active', false);
                        record.getProxy().setUrl('/lookup_types/' + CMDBuildUI.util.Utilities.stringToHex(lookupType) + '/values/');
                        vm.set("theValue", record);
                    }
                }
            }
        }
    }
});
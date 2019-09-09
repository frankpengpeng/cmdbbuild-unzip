Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabitems-values-card-view',
    mixins: [
        'CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ToolsMixin'
    ],
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick' // from mixin
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick' // from mixin
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick' // from mixin
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick' // from mixin
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.View} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        var lookupTypeName = vm.get('lookupTypeName');
        var valueId = vm.get('valueId');

        if (lookupTypeName && valueId) {
            vm.linkTo("theValue", {
                type: 'CMDBuildUI.model.lookups.Lookup',
                id: encodeURI(valueId)
            });
            var typeViewModel = Ext.ComponentQuery.query('viewport')[0].down('administration-content-lookuptypes-tabitems-type-properties').lookupViewModel();
            if (typeViewModel) {
                vm.set('parentTypeName', typeViewModel.get('theLookupType.parent'));
            }
        }
    }
});
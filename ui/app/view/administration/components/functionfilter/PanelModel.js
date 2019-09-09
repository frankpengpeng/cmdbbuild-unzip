Ext.define('CMDBuildUI.view.administration.components.functionfilters.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-functionfilters-panel',

    data: {
        _function: null
    },
    formulas: {
        manager: function (get) {
            var filter = this.get('theFilter.configuration');
            if (filter.functions && filter.functions.length) {
                this.set('_function', filter.functions[0].name);
            }
        }
    },
    stores: {
        getFunctionsStore: {
            model: 'CMDBuildUI.model.Function',
            sorters: ['description'],
            pageSize: 0, // disable pagination
            autoLoad: true
        }
    }
});

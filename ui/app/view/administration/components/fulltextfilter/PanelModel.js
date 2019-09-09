Ext.define('CMDBuildUI.view.administration.components.fulltextfilters.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-fulltextfilters-panel',
    
    data: {
        _query: null        
    },
    formulas: {
        manager: function(get){
            var filter =this.get('theFilter.configuration');
            if(filter.query && filter.query.length){
                this.set('_query',filter.query );
            }
        }
    }
});

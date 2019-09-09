Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.ViewInRowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-processes-tabitems-geoattributes-card-viewinrow',
    data: {
        name: 'CMDBuildUI',
        theGeoAttribute: null,
        data: {
            actions: {
                view: true,
                edit: false,
                add: false
            }
        }
    }

});

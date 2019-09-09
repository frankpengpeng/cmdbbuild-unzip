Ext.define('CMDBuildUI.store.classes.PrototypeClasses', {
    extend: 'CMDBuildUI.store.Base',

    requires: [
        'CMDBuildUI.store.Base',
        'CMDBuildUI.model.classes.Class'
    ],
    pageSize: 0,
    model: 'CMDBuildUI.model.classes.Class',
    sorters: ['description'],
    alias: 'store.classes.PrototypeClasses',
    filters: [function(item){
        if(item.get('name') === 'Class'){
            item.set('description', '');
        }
        return item.get('prototype');
    }]

});
Ext.define('CMDBuildUI.util.administration.helper.GridHelper', {
    singleton: true,

    /**
     * Filter grid items.
     * @param {CMDBuildUI.store.Base} store
     * @param {string} searchTerm
     * @param {Array} filterOnly //Array of fieldnames
     */    
    searchMoreFields: function (store, searchTerm, filterOnly) {
        store.filter(function (record) {
            var data = record.getData();
            var result = false;
            for (var key in data) {
                if ((filterOnly && filterOnly.includes(key)) || !filterOnly) {
                    if (String(data[key]).toLowerCase().includes(searchTerm.toLowerCase())) {
                        result = true;
                    }
                }
            }
            return result;
        });
    }

});
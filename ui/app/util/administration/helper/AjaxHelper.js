Ext.define('CMDBuildUI.util.administration.helper.AjaxHelper', {
    requires: [
        'Ext.Ajax',
        'CMDBuildUI.util.Config'
    ],
    singleton: true,
    /**
     * Drop cache
     *  
     */

    dropCache: function () {
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId('administration-dropcache');
        Ext.Ajax.request({
            url: CMDBuildUI.util.administration.helper.ApiHelper.server.getDropCacheUrl(),
            // CMDBuildUI.util.Config.baseUrl + '/system/cache/drop',
            method: 'POST',
            success: function (transport) {
                deferred.resolve();
            },
            failure: function(e){
                deferred.reject(e);
            }
        });
        return deferred.promise;
       
    },

    
    /**
     * Update of menu for gropus
     * 
     * @argument {Number} menuId
     * @argument {CMDBuildUI.model.menu.Menu} data
     */
    getMenuForGroup: function(menuId, data){
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId('administration-updatemenu');
        Ext.Ajax.request({
            url: CMDBuildUI.util.administration.helper.ApiHelper.server.getTheMenuUrl(menuId),
            method: 'GET',
            success: function (transport) {
                var response = JSON.parse(transport.responseText).data;
                deferred.resolve(response);
            },
            failure: function(e){
                deferred.reject(e);
            }
        });
        return deferred.promise;
    },

    /**
     * Update of menu for gropus
     * 
     * @argument {Number} menuId
     * @argument {CMDBuildUI.model.menu.Menu} data
     */
    updateMenuForGroup: function(menuId, data){
        var deferred = new Ext.Deferred();
        CMDBuildUI.util.Ajax.setActionId('administration-updatemenu');
        Ext.Ajax.request({
            url: CMDBuildUI.util.administration.helper.ApiHelper.server.getTheMenuUrl(menuId), // CMDBuildUI.util.Config.baseUrl + '/menu/' + menuId,
            method: 'PUT',
            jsonData: data,
            success: function (transport) {
                var response = JSON.parse(transport.responseText).data;
                deferred.resolve(response);
            },
            failure: function(e){
                deferred.reject(e);
            }
        });
        return deferred.promise;
    },

    /**
     * Create menu for groups
     * 
     * 
     */
    createMenuForGroup: function(data){
        var deferred = new Ext.Deferred();

        CMDBuildUI.util.Ajax.setActionId('administration-createmenu');
        Ext.Ajax.request({
            url: CMDBuildUI.util.administration.helper.ApiHelper.server.getTheMenuUrl(),
            method: 'POST',
            jsonData: data,
            success: function (transport) {
                var response = JSON.parse(transport.responseText).data;
                deferred.resolve(response);
            },
            failure: function(e){
                deferred.reject(e);
            }
        });
        return deferred.promise;
    },

    /**
     * Save groups for filters
     */
    setGroupsForFilter: function(filterId, data){
        var deferred = new Ext.Deferred();

        Ext.Ajax.request({
            url: Ext.String.format(
                '{0}/classes/_ANY/filters/{1}/defaultFor',
                CMDBuildUI.util.Config.baseUrl,
                filterId
            ),
            method: 'POST',
            jsonData: data,
            success: function (transport) {
                var response = JSON.parse(transport.responseText).data;
                deferred.resolve(response);
            },
            failure: function(e){
                deferred.reject(e);
            }
        });
        return deferred.promise;
    },

    runJob: function(record){
        var deferred = new Ext.Deferred();
        Ext.Ajax.request({
            url: Ext.String.format("{0}/jobs/{1}/run",CMDBuildUI.util.Config.baseUrl, record.get('code')),
            method: 'POST',
            success: function (response) {
                var res = JSON.parse(response.responseText);
                deferred.resolve(res);
            },
            error: function(response) {
                deferred.resolve(true);
            }
        });
        return deferred.promise;
    }
});
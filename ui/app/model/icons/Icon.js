Ext.define('CMDBuildUI.model.icons.Icon', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'folder',
        type: 'string',
        critical: true
    },{
        name: '_description',
        type: 'string',
        calculate: function(data){
            if(!data.description){
                return data.name;
            }
            return data.description;
        }
    }, {
        name: 'name',
        type: 'string',
        critical: true
    }, {
        name: 'description',
        type: 'string',
        critical: true
    }, {
        name: 'iconelement',
        type: 'string',
        calculate: function (data) {
            if (data._id) {
                var token = CMDBuildUI.util.helper.SessionHelper.getToken();
                var iconpath = Ext.String.format('{0}/uploads/{1}/download?CMDBuild-Authorization={2}&_dc={3}', CMDBuildUI.util.Config.baseUrl, data._id, token, new Date().getTime());
                return '<img style="vertical-align:middle;width:20px;height:20px" src=' + iconpath + ' />';
            } else {
                return null;
            }
        }
    }, {
        name: 'gisimage',
        type: 'boolean',
        calculate: function (data) {

            if (data.path) {
                return data.path.search('images/gis/') !== -1;
            }
            return false;
        }
    }, {
        name: 'path',
        type: 'string',
        defaultValue: 'images/gis/',
        critical: true
    }],
    proxy: {
        type: 'baseproxy',
        url: '/uploads'
    }
});
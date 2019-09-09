Ext.define('CMDBuildUI.model.administration.AdminNavTree', {
    extend: 'CMDBuildUI.model.base.Base',

    fields: [{
        name: 'name',
        type: 'string',
        validators: [
            'presence'
            // {
            //     type: 'format',
            //     matcher:  /^(?![_0-9])[a-zA-Z0-9-_]+$/,
            //     message: Ext.String.format(CMDBuildUI.locales.Locales.administration.common.messages.cantcontainchar, '_ (underscore)')
            // }
        ],
        critical: true,
        persist: true
    },{
        name: 'description',
        type: "string",
        critical: true,
        persist: true
    }, {
        name: 'nodes',
        type: "auto",
        defaultValue: [],
        critical: true,
        persist: true
    }, {
        name: 'sourceClass',
        type: 'string',
        critical: true,
        persist: true
    }, {
        name: 'active',
        type: 'boolean',
        defaultValue: true,
        critical: true,
        persist: true
    }],

    proxy: {
        type: 'baseproxy',
        url: '/domainTrees/',
        extraParams: {
            treeMode: 'tree'
        },
        pageSize: 0
    }
});
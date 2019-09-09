Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.users.UsersModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-users-users',

    stores: {
        assignedUser: {
            model: "CMDBuildUI.model.users.User",
            proxy: {
                url: '/roles/{theGroup._id}/users',
                type: 'baseproxy',
                extraParams: {
                    assigned: true
                }
            },
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0
        },
        notAssignedUser: {
            model: "CMDBuildUI.model.users.User",
            proxy: {
                url: '/roles/{theGroup._id}/users',
                type: 'baseproxy',
                extraParams: {
                    assigned: false
                }
            },
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0
        }
    }
});
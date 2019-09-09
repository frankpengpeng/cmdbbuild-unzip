Ext.define('CMDBuildUI.store.processes.Instances', {
    extend: 'Ext.data.BufferedStore',

    alias: 'store.processes-instances',

    pageSize: 100,
    remoteFilter: true,
    remoteSort: true
});
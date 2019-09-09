(function() {
    window.cmdbuildConfig = window.cmdbuildConfig || {};
    Ext.define('CMDBuildUI.util.Config', {
        singleton: true,
    
        baseUrl: window.cmdbuildConfig.baseUrl,
        socketUrl: window.cmdbuildConfig.socketUrl,
        geoserverBaseUrl: window.cmdbuildConfig.geoserverBaseUrl,
        bimserverBaseUrl: window.cmdbuildConfig.bimserverBaseUrl,
        ajaxTimeout: 15000, // milliseconds
        uiBaseUrl: window.location.origin + window.location.pathname,
    
        ui: {
            relations: {
                collapsedlimit: 10
            }
        },
    
        widgets: {
            customForm: 'widgets-customform-panel',
            linkCards: 'widgets-linkcards-panel',
            createModifyCard: 'widgets-createmodifycard-panel',
            createReport: 'widgets-createreport-panel',
            openAttachment: true,
            openNote: true,
            manageEmail: true,
            calendar: 'widgets-calendar-panel',
            workflow: 'widgets-startworkflow-panel',
            presetFromCard: 'widgets-presetfromcard-panel'
        }
    });
})();
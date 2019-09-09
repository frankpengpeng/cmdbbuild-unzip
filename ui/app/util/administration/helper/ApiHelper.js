Ext.define('CMDBuildUI.util.administration.helper.ApiHelper', {
    singleton: true,
    requires: ['CMDBuildUI.util.Config'],

    client: {
        basePath: 'administration',
        getClassUrl: function (className) {
            if (className) {
                return Ext.String.format('{0}/classes/{1}', this.basePath, className);
            }
            return Ext.String.format('{0}/classes', this.basePath);
        },
        getProcessUrl: function (processName) {
            if (processName) {
                return Ext.String.format('{0}/processes/{1}', this.basePath, processName);
            }
            return Ext.String.format('{0}/processes', this.basePath);
        },
        getDomainUrl: function (domainName) {
            if (domainName) {
                return Ext.String.format('{0}/domains/{1}', this.basePath, domainName);
            }
            return Ext.String.format('{0}/domains', this.basePath);
        },
        getCustomPageUrl: function (customPageId) {
            if (customPageId) {
                return Ext.String.format('{0}/custompages/{1}', this.basePath, customPageId);
            }
            return Ext.String.format('{0}/custompages_empty', this.basePath);
        },
        getCustomComponentUrl: function (componentType, customPageId) {
            if (customPageId && !isNaN(customPageId)) {
                return Ext.String.format('{0}/customcomponents/{1}/{2}', this.basePath, componentType, customPageId);
            }
            return Ext.String.format('{0}/customcomponents_empty/{1}/{2}', this.basePath, componentType, customPageId || 'false');
        },
        getReportUrl: function (reportId) {
            if (reportId) {
                return Ext.String.format('{0}/reports/{1}', this.basePath, reportId);
            }
            return Ext.String.format('{0}/reports_empty', this.basePath);
        },
        getViewUrl: function (viewId) {
            if (viewId) {
                return Ext.String.format('{0}/views/{1}', this.basePath, viewId);
            }
            return Ext.String.format('{0}/views_empty', this.basePath);
        },
        getTheMenuUrl: function (menuId) {
            if (menuId) {
                return Ext.String.format('{0}/menus/{1}', this.basePath, menuId);
            }
            return Ext.String.format('{0}/menus_empty', this.basePath);
        },
        getTheViewFilterUrl: function (filterName, showform) {
            showform = typeof showform === 'undefined' ? false : showform;
            if (filterName) {
                return Ext.String.format('{0}/searchfilters/{1}', this.basePath, filterName);
            }
            return Ext.String.format('{0}/searchfilters_empty/{1}', this.basePath, showform);
        },
        getUsersUrl: function () {
            return Ext.String.format('{0}/users', this.basePath);
        },
        getEmailAccountsUrl: function () {
            return Ext.String.format('{0}/email/accounts', this.basePath);
        },
        getTaskManagerReadEmailsUrl: function () {
            return Ext.String.format('{0}/tasks/reademails', this.basePath);
        },
        getGISManageIconsUrl: function () {
            return Ext.String.format('{0}/gis/manageicons', this.basePath);
        },
        getBIMProjectsUrl: function () {
            return Ext.String.format('{0}/bim/projects', this.basePath);
        },
        getLocalizationConfigurationUrl: function () {
            return Ext.String.format('{0}/localizations/configuration', this.basePath);
        },
        getGeneralOptionsUrl: function () {
            return Ext.String.format('{0}/setup/generaloptions', this.basePath);
        },
        getImportExportTemplatesUrl: function () {
            return Ext.String.format('{0}/importexport/templates', this.basePath);
        },
        getTheLookupTypeUrl: function (lookupHash) {
            if (lookupHash) {
                return Ext.String.format('{0}/lookup_types/{1}', this.basePath, lookupHash);
            }
            return Ext.String.format('{0}/lookup_types_empty', this.basePath);
        },
        getPermissionUrl: function(id){
            if (id) {
                return Ext.String.format('{0}/groupsandpermissions/{1}', this.basePath, id);
            }
            return Ext.String.format('{0}/groupsandpermissions_empty', this.basePath);
        }
    },

    server: {
        baseUrl: CMDBuildUI.util.Config.baseUrl,
        getDropCacheUrl: function () {
            return Ext.String.format('{0}/system/cache/drop', this.baseUrl);
        },
        getTheMenuUrl: function (name) {
            return Ext.String.format('{0}/menu{1}', this.baseUrl, name ? Ext.String.format('/{0}', name) : '');
        },
        getPermissionUrl: function (id) {
            return Ext.String.format('{0}/roles{1}', this.baseUrl, id ? Ext.String.format('/{0}', id) : '');
        },
        getPermissionFiltersUrl: function (id) {
            return Ext.String.format('{0}/filters', this.getPermissionUrl(id));
        }
    }

});
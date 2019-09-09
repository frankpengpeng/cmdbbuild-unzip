Ext.define('CMDBuildUI.util.administration.helper.LocalizationHelper', {
    singleton: true,


    getLocaleKeyOfClassDescription: function (theObjectTypeName) {
        return Ext.String.format('{0}.{1}.description', 'class', theObjectTypeName);
    },

    getLocaleKeyOfClassAttributeDescription: function (theObjectTypeName, theAttributeName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'attributeclass', theObjectTypeName, theAttributeName);
    },
    getLocaleKeyOfClassGroupDescription: function (className, groupName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'attributegroupclass', className, groupName);
    },

    getLocaleKeyOfProcessDescription: function (theProcessName) {
        return this.getLocaleKeyOfClassDescription(theProcessName);
    },

    getLocaleKeyOfProcessAttributeDescription: function (theObjectTypeName, theAttributeName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'class', theObjectTypeName, theAttributeName);
    },

    getLocaleKeyOfDomainDescription: function (domainName) {
        return Ext.String.format('{0}.{1}.description', 'domain', domainName);
    },

    getLocaleKeyOfDomainDirectDescription: function (domainName) {
        return Ext.String.format('{0}.{1}.directdescription', 'domain', domainName);
    },

    getLocaleKeyOfDomainInverseDescription: function (domainName) {
        return Ext.String.format('{0}.{1}.inversedescription', 'domain', domainName);
    },

    getLocaleKeyOfDomainMasterDetail: function (domainName) {
        return Ext.String.format('{0}.{1}.masterdetaillabel', 'domain', domainName);
    },

    getLocaleKeyOfDomainAttributeDescription: function (domainName, attributeName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'attributedomain', domainName, attributeName);
    },

    getLocaleKeyOfViewDescription: function (viewName) {
        return Ext.String.format('{0}.{1}.description', 'view', viewName);
    },

    getLocaleKeyOfSearchFiltreDescription: function (filterName) {
        return Ext.String.format('{0}.{1}.description', 'view', filterName);
    },

    getLocaleKeyOfLookupValueDescription: function (lookupTypeName, lookupValueCode) {
        return Ext.String.format('{0}.{1}.{2}.description', 'lookup', lookupTypeName, lookupValueCode);
    },

    getLocaleKeyOfReportDescription: function (reportName) {
        return Ext.String.format('{0}.{1}.description', 'report', reportName);
    },
    getLocaleKeyOfReportAttributeDescription: function (reportName, attributeName) {
        return Ext.String.format('{0}.{1}.{2}.description', 'class', reportName, attributeName);
    },

    getLocaleKeyOfMenuItemDescription: function (itemId) {
        return Ext.String.format('menuitem.{0}.description', itemId);
    },

    getLocaleKeyOfCustomPageDescription: function (itemId) {
        return Ext.String.format('custompage.{0}.description', itemId);
    },

    getLocaleKeyOfCustomComponentDescription: function (itemId) {
        return Ext.String.format('customcomponent.{0}.description', itemId);
    }
});
/**
 * This class is the controller for the main view for the application. It is specified as
 * the "controller" of the Main view class.
 *
 * TODO - Replace this content of this view to suite the needs of your application.
 */
Ext.define('CMDBuildUI.view.main.MainController', {
    extend: 'Ext.app.ViewController',

    alias: 'controller.main',
    mixins: {
        managementroutes: 'CMDBuildUI.mixins.routes.Management',
        adminroutes: 'CMDBuildUI.mixins.routes.Administration'
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    routes: {
        '': {
            action: 'showManagement',
            before: 'onBeforeShowManagement'
        },
        'patches': {
            action: 'showPatches'
        },
        'login': {
            action: 'showLogin',
            before: 'onBeforeShowLogin'
        },
        'logout': {
            action: 'doLogout'
        },
        'gotomamagement': {
            action: 'goToManagement'
        },
        'management': {
            action: 'showManagement',
            before: 'onBeforeShowManagement'
        },
        'gotoadministration': {
            action: 'goToAdministration',
            before: 'onlyAdmin'
        },
        'administration': {
            action: 'showAdministration',
            before: 'onBeforeShowAdministration'
        },
        'administration/classes': {
            action: 'showClassAdministrationAdd',
            before: 'onlyAdmin'
        },
        'administration/classes_empty': {
            action: 'showClassAdministration_empty',
            before: 'onlyAdmin'
        },
        'administration/classes/:className': {
            action: 'showClassAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/classes/:className/attribute/:attributeName': {
            action: 'showClassAttributeAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/classes/:className/attribute/:attributeName/edit': {
            action: 'showClassAttributeAdministrationEdit',
            before: 'onlyAdmin'
        },
        'administration/lookup_types': {
            action: 'showLookupTypeAdministrationAdd',
            before: 'onlyAdmin'
        },
        'administration/lookup_types_empty': {
            action: 'showLookupTypeAdministration_empty',
            before: 'onlyAdmin'
        },
        'administration/lookup_types/:lookupName': {
            action: 'showLookupTypeAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/domains': {
            action: 'showDomainAdministrationCreate',
            before: 'onlyAdmin'
        },
        'administration/domains_empty': {
            action: 'showDomainAdministration_empty',
            before: 'onlyAdmin'
        },
        'administration/domains/:domain': {
            action: 'showDomainAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/menus': {
            action: 'showMenuAdministrationAdd',
            before: 'onlyAdmin'
        },
        'administration/menus/:menu': {
            action: 'showMenuAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/menus_empty': {
            action: 'showMenuAdministration_empty',
            before: 'onlyAdmin'
        },
        'administration/processes': {
            action: 'showProcessesAdministrationAdd',
            before: 'onlyAdmin'
        },
        'administration/processes/:process': {
            action: 'showProcessAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/reports_empty/:showForm': {
            action: 'showReportAdministration_empty',
            before: 'onlyAdmin'
        },
        'administration/reports_empty': {
            action: 'showReportAdministration_empty',
            before: 'onlyAdmin'
        },
        'administration/reports/:reportId': {
            action: 'showReportAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/custompages_empty/:showForm': {
            action: 'showCustompageAdministration_empty',
            before: 'onlyAdmin'
        },
        'administration/custompages_empty': {
            action: 'showCustompageAdministration_empty',
            before: 'onlyAdmin'
        },
        'administration/custompages/:reportId': {
            action: 'showCustompageAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/customcomponents_empty': {
            action: 'showCustomcomponentAdministration_empty',
            before: 'onlyAdmin'
        },
        'administration/customcomponents_empty/:componentType/:showForm': {
            action: 'showCustomcomponentAdministrationView_empty',
            before: 'onlyAdmin',
            conditions: {
                ':componentType': '(contextmenu)',
                ':showForm': '(true|false)'
            }
        },
        'administration/customcomponents/:componentType/:customcomponentId': {
            action: 'showCustomcomponentAdministrationView',
            before: 'onlyAdmin',
            conditions: {
                ':componentType': '(contextmenu)',
                ':customcomponentId': '([0-9]+)'
            }
        },
        'administration/groupsandpermissions_empty': {
            action: 'showGroupsandpermissionsAdministration_empty',
            before: 'onlyAdmin'
        },
        'administration/groupsandpermissions/:roleId': {
            action: 'showGroupsandpermissionsAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/users': {
            action: 'showUsersAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/users_empty': {
            action: 'showUsersAdministrationView_empty',
            before: 'onlyAdmin'
        },
        'administration/processes_empty': {
            action: 'showProcessAdministration_empty',
            before: 'onlyAdmin'
        },
        'administration/setup_empty': {
            action: 'showSetupAdministrationView_empty',
            before: 'onlyAdmin'
        },
        'administration/setup/:setupPage': {
            action: 'showSetupAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/email_empty': {
            action: 'showEmailAdministrationView_empty',
            before: 'onlyAdmin'
        },
        'administration/email/templates': {
            action: 'showEmailTemplatesAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/email/accounts': {
            action: 'showEmailAccountsAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/email/queue': {
            action: 'showEmailQueueAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/localizations/localization': {
            action: 'showLocalizationsLocalizationAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/navigationtrees/:navigationtreeId': {
            action: 'showNavigationtreeAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/navigationtrees_empty/:showForm': {
            action: 'showNavigationtreeAdministrationView_empty',
            before: 'onlyAdmin'
        },
        'administration/localization_empty': {
            action: 'showLocalizationAdministrationView_empty',
            before: 'onlyAdmin'
        },
        'administration/localizations/configuration': {
            action: 'showLocalizationsConfigurationAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/tasks/:type': {
            action: 'showTaskImportExportAdministrationView',
            before: 'onlyAdmin'
        },
        // 'administration/tasks/sendemails': {
        //     action: 'showTaskSendEmailAdministrationView',
        //     before: 'onlyAdmin'
        // },

        // 'administration/tasks/reademails_empty': {
        //     action: 'showTaskReadEmailAdministrationView_empty',
        //     before: 'onlyAdmin'
        // },
        // 'administration/tasks/reademails': {
        //     action: 'showTaskReadEmailAdministrationView',
        //     before: 'onlyAdmin'
        // },
        // 'administration/tasks/syncevents': {
        //     action: 'showTaskSyncEventsAdministrationView',
        //     before: 'onlyAdmin'
        // },
        // 'administration/tasks/asyncevents': {
        //     action: 'showTaskAsyncEventsAdministrationView',
        //     before: 'onlyAdmin'
        // },
        // 'administration/tasks/startprocess': {
        //     action: 'showTaskStartProcessAdministrationView',
        //     before: 'onlyAdmin'
        // },
        // 'administration/tasks/wizardconnectors': {
        //     action: 'showTaskWizardConnectorAdministrationView',
        //     before: 'onlyAdmin'
        // },
        'administration/searchfilters/:searchfilter': {
            action: 'showSearchFilterAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/searchfilters_empty/:showForm': {
            action: 'showSearchFilterAdministrationView_empty',
            before: 'onlyAdmin'
        },
        // 'administration/views/sql': {
        //     action: 'showViewSqlAdministrationView'
        // },
        // 'administration/views/sql_empty': {
        //     action: 'showSearchFilterAdministrationView_empty'
        // },
        'administration/views/:viewName': {
            action: 'showViewAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/views_empty/:showForm': {
            action: 'showViewAdministrationView_empty',
            before: 'onlyAdmin'
        },
        'administration/views_empty/:showForm/:viewType': {
            action: 'showViewAdministrationView_empty',
            before: 'onlyAdmin'
        },
        'administration/gis_empty': {
            action: 'showGisAdministrationView_empty',
            before: 'onlyAdmin'
        },
        'administration/gis/manageicons': {
            action: 'showGisManageIconsAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/gis/externalservices': {
            action: 'showGisExternalServicesAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/gis/layersorder': {
            action: 'showGisLayersOrderAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/gis/geoserverslayers': {
            action: 'showGisGeoserversLayersAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/gis/gisnavigation': {
            action: 'showGisGisNavigationAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/gis/thematism': {
            action: 'showGisThematismAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/bim_empty': {
            action: 'showBimAdministrationView_empty',
            before: 'onlyAdmin'
        },
        'administration/bim/projects': {
            action: 'showBimProjectsAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/bim/layers': {
            action: 'showBimNavigationAdministrationView',
            before: 'onlyAdmin'
        },
        'administration/importexport_empty': {
            action: 'showImportExportAdministrationView_empty',
            before: 'onlyAdmin'
        },
        'administration/importexport/templates': {
            action: 'showImportExportAdministrationView',
            before: 'onlyAdmin'
        },
        /* END ADMINISTRATION ROUTES */

        // CLASSES
        'classes/:className/cards': {
            action: 'showCardsGrid',
            before: 'onBeforeShowCardsGrid'
        },
        'classes/:className/cards/:idCard': {
            action: 'showCard',
            before: 'onBeforeShowCard',
            conditions: {
                ':idCard': '([0-9]+)'
            }
        },
        'classes/:className/cards/new': {
            action: 'showCardCreate',
            before: 'onBeforeShowCardWindow'
        },
        'classes/:className/cards/:idCard/view': {
            action: 'showCardView',
            before: 'onBeforeShowCardWindow'
        },
        'classes/:className/cards/:idCard/clone': {
            action: 'showCardClone',
            before: 'onBeforeShowCardWindow'
        },
        'classes/:className/cards/:idCard/edit': {
            action: 'showCardEdit',
            before: 'onBeforeShowCardWindow'
        },
        'classes/:className/cards/:idCard/details': {
            action: 'showCardDetails',
            before: 'onBeforeShowCardWindow'
        },
        'classes/:className/cards/:idCard/notes': {
            action: 'showCardNotes',
            before: 'onBeforeShowCardWindow'
        },
        'classes/:className/cards/:idCard/relations': {
            action: 'showCardRelations',
            before: 'onBeforeShowCardWindow'
        },
        'classes/:className/cards/:idCard/history': {
            action: 'showCardHistory',
            before: 'onBeforeShowCardWindow'
        },
        'classes/:className/cards/:idCard/emails': {
            action: 'showCardEmails',
            before: 'onBeforeShowCardWindow'
        },
        'classes/:className/cards/:idCard/attachments': {
            action: 'showCardAttachments',
            before: 'onBeforeShowCardWindow'
        },
        // PROCESSES
        'processes/:processName/instances': {
            action: 'showProcessInstancesGrid',
            before: 'onBeforeShowProcessInstancesGrid'
        },
        'processes/:processName/instances/new': {
            action: 'showProcessInstanceCreate',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        'processes/:processName/instances/:idInstance': {
            action: 'showProcessInstance',
            before: 'onBeforeShowProcessInstance',
            conditions: {
                ':idCard': '([0-9]+)'
            }
        },
        'processes/:processName/instances/:idInstance/activities/:activityId': {
            action: 'showProcessInstance',
            before: 'onBeforeShowProcessInstance'
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/view': {
            action: 'showProcessInstanceView',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/edit': {
            action: 'showProcessInstanceEdit',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        'processes/:processName/instances/:idInstance/notes': {
            action: 'showProcessInstanceNotes',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        'processes/:processName/instances/:idInstance/relations': {
            action: 'showProcessInstanceRelations',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        'processes/:processName/instances/:idInstance/history': {
            action: 'showProcessInstanceHistory',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        'processes/:processName/instances/:idInstance/emails': {
            action: 'showProcessInstanceEmails',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        'processes/:processName/instances/:idInstance/attachments': {
            action: 'showProcessInstanceAttachments',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/notes': {
            action: 'showProcessInstanceNotes',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/relations': {
            action: 'showProcessInstanceRelations',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/history': {
            action: 'showProcessInstanceHistory',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/emails': {
            action: 'showProcessInstanceEmails',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        'processes/:processName/instances/:idInstance/activities/:activityId/attachments': {
            action: 'showProcessInstanceAttachments',
            before: 'onBeforeShowProcessInstanceWindow'
        },
        // CUSTOM PAGES
        'custompages/:pageName': {
            action: 'showCustomPage',
            before: 'onBeforeShowCustomPage'
        },
        // REPORTS
        'reports/:reportName': {
            action: 'showReport'
        },
        'reports/:reportName/:extension': {
            action: 'showReportExtension'
        },
        // VIEWS
        'views/:viewName/items': {
            action: 'showView',
            before: 'onBeforeShowView'
        }
    },

    onBeforeRender: function () {
        var me = this;
        var currentUrl = Ext.History.getToken();
        CMDBuildUI.util.helper.SessionHelper.updateStartingUrlWithCurrentUrl();
        if (currentUrl === 'gotoadministration') {
            me.redirectTo('administration', true);
        }else{
            me.redirectTo('login', true);
        }


    }
});
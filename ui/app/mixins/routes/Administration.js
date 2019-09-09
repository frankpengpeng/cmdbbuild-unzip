Ext.define('CMDBuildUI.mixins.routes.Administration', {
    imports: ['CMDBuildUI.util.Navigation'],
    mixinId: 'administrationroutes-mixin',

    currentmaincontent: null,

    onlyAdmin: function () {
        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
        var action = arguments[arguments.length - 1];
        var rolePrivileges = this.getViewModel().get("theSession");
        if (rolePrivileges && rolePrivileges.get('rolePrivileges').admin_access && rolePrivileges.get('_id') === CMDBuildUI.util.helper.SessionHelper.getToken()) {
            action.resume();
        } else {
            this.redirectTo('gotomamagement', true);
        }
    },
    onCheckAdministrationSession: function (action) {
        var me = this;
        CMDBuildUI.util.helper.SessionHelper.checkSessionValidity().then(function (token) {
            action.resume();
        }, function () {
            action.stop();
            me.redirectTo('login', true);
        });
    },
    /**
     * Administration routes
     */
    /**
     * Show administration page
     */
    onBeforeShowAdministration: function (action) {
        var me = this;
        me.getViewModel().set('isAdministrationModule', true);
        CMDBuildUI.util.Navigation.clearCurrentContext();
        // var container = CMDBuildUI.util.Navigation.getMainAdministrationContainer(true);

        CMDBuildUI.util.helper.SessionHelper.checkSessionValidity().then(function (token) {
            CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                Ext.getBody().removeCls('management');
                Ext.getBody().addCls('administration');
                action.resume();
                var navTree = Ext.getBody().down('#administrationNavigationTree');
                var store = Ext.getStore('administration.MenuAdministration');
                var currentNode = store.findNode("href", Ext.History.getToken());
                if (!currentNode) {
                    currentNode = CMDBuildUI.util.administration.MenuStoreBuilder.getFirstSelectableMenuItem(store.getRootNode().childNodes);
                }

                var vm = navTree.component.getViewModel();
                if (currentNode) {
                    vm.set('selected', currentNode);
                }

            }, true);
        }, function () {
            action.stop();
            me.redirectTo('login', true);
        });
    },

    /**
     *  redirect to administration and refresh the window
     */
    goToAdministration: function () {
        this.redirectTo('administration', true);
        window.location.reload();
    },
    /**
     * Show administration  main container
     */
    showAdministration: function () {
        CMDBuildUI.util.Navigation.addIntoMainContainer('administration-maincontainer');
        this.redirectToStartingUrl();
    },

    /**
     * Show class add
     */
    showClassAdministrationAdd: function (classType, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-classes-view', {
            viewModel: {
                links: {
                    theObject: {
                        reference: 'CMDBuildUI.model.classes.Class',
                        create: true
                    }
                },
                data: {
                    objectType: 'Class',
                    classType: classType,
                    title: CMDBuildUI.locales.Locales.administration.navigation.classes,
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                }
            }
        });
    },

    /**
     * Show class add
     */
    showClassAdministration_empty: function (classType, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-classes-topbar', {
            viewModel: {
                data: {
                    objectType: 'Class',
                    classType: classType,
                    title: CMDBuildUI.locales.Locales.administration.navigation.classes,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.navigation.classes'
                    },
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                }
            }
        });
    },

    /**
     * Show class view
     */
    showClassAdministrationView: function (className, action) {

        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-classes-view', {
            viewModel: {
                links: {
                    theObject: {
                        reference: 'CMDBuildUI.model.classes.Class',
                        id: className
                    }
                },
                data: {
                    objectTypeName: className,
                    objectType: 'Class',
                    title: CMDBuildUI.locales.Locales.administration.navigation.classes,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show class edit
     */
    showClassAdministrationEdit: function (className) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-classes-view', {
            viewModel: {
                links: {
                    theObject: {
                        reference: 'CMDBuildUI.model.classes.Class',
                        id: className
                    }
                },
                data: {
                    objectTypeName: className,
                    objectType: 'Class',
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
                }
            }
        });
        return;
    },

    /**
     * Show class attribute edit
     */
    showClassAttributeAdministrationEdit: function (className, attributeName, attributes) {
        this.addIntoAdministrationDetailsWindow('administration-content-classes-tabitems-attributes-card-edit', {
            viewModel: {
                data: {
                    className: className,
                    attributeName: attributeName,
                    attributes: attributes
                }
            }
        });
    },
    /**
     * Show class attribute view
     */
    showClassAttributeAdministrationView: function (className, attributeName, attributes) {
        this.addIntoAdministrationDetailsWindow('administration-content-classes-tabitems-attributes-card-view', {
            viewModel: {
                data: {
                    className: className,
                    attributeName: attributeName,
                    attributes: attributes
                }
            }
        });
    },

    /**
     * Show lookup type add
     */
    showLookupTypeAdministrationAdd: function (lookupName) {

        lookupName = decodeURI(lookupName);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-lookuptypes-view', {

            viewModel: {
                links: {
                    theLookupType: {
                        reference: 'CMDBuildUI.model.lookups.LookupType',
                        create: true
                    }
                },
                data: {
                    objectTypeName: lookupName,
                    objectType: 'Lookup',
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    title: CMDBuildUI.locales.Locales.administration.localizations.lookup
                }
            }
        });
    },

    /**
     * Show lookup type empty
     */
    showLookupTypeAdministration_empty: function (lookupName) {

        lookupName = decodeURI(lookupName);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-lookuptypes-topbar', {
            viewModel: {}
        });
    },

    /**
     * Show lookup type view
     */
    showLookupTypeAdministrationView: function (lookupName) {
        lookupName = decodeURI(lookupName);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-lookuptypes-view', {

            viewModel: {
                links: {
                    theLookupType: {
                        reference: 'CMDBuildUI.model.lookups.LookupType',
                        id: lookupName
                    }
                },
                data: {
                    objectTypeName: lookupName,
                    objectType: 'Lookup',
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    title: CMDBuildUI.locales.Locales.administration.localizations.lookup
                }
            }
        });
    },

    /**
     * show domain view
     * @param {String} domain
     */
    showDomainAdministrationView: function (domain) {
        domain = decodeURI(domain);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-domains-view', {
            viewModel: {
                links: {
                    theDomain: {
                        reference: 'CMDBuildUI.model.domains.Domain',
                        id: domain
                    }
                },
                data: {
                    objectTypeName: domain,
                    objectType: 'Domain',
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    title: CMDBuildUI.locales.Locales.administration.localizations.domain
                }
            }
        });
    },

    /**
     * show domain create
     */
    showDomainAdministrationCreate: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-domains-view', {
            viewModel: {
                links: {
                    theDomain: {
                        reference: 'CMDBuildUI.model.domains.Domain',
                        create: true
                    }
                },
                data: {
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    },
                    objectType: 'Domain',
                    title: CMDBuildUI.locales.Locales.administration.localizations.domain
                }
            }
        });
    },

    /**
     * show domain empty
     */
    showDomainAdministration_empty: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-domains-topbar', {
            viewModel: {}
        });
    },


    /**
     * show menu add
     */
    showMenuAdministrationAdd: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-menu-view', {
            viewModel: {
                links: {
                    theMenu: {
                        reference: 'CMDBuildUI.model.menu.Menu',
                        create: true
                    }
                },
                data: {
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    },
                    action: 'ADD',
                    objectType: 'Menu',
                    title: CMDBuildUI.locales.Locales.administration.menus.singular
                }
            }
        });
    },
    /**
     * show menu view
     */
    showMenuAdministrationView: function (menu) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-menu-view', {
            viewModel: {
                links: {
                    theMenu: {
                        reference: 'CMDBuildUI.model.menu.Menu',
                        id: menu
                    }
                },
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    },
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    objectType: 'Menu',
                    title: CMDBuildUI.locales.Locales.administration.menus.singular
                }
            }
        });
    },

    /**
     * show menu empty
     */
    showMenuAdministration_empty: function (menu) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-menus-topbar', {
            viewModel: {}

        });
    },

    showProcessAdministration_empty: function (process) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-processes-topbar', {
            viewModel: {}
        });
    },

    /**
     * Show process view
     */
    showProcessAdministrationView: function (processName, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-processes-view', {
            viewModel: {
                links: {
                    theProcess: {
                        reference: 'CMDBuildUI.model.processes.Process',
                        id: processName
                    }
                },
                data: {
                    objectTypeName: processName,
                    objectType: 'Process',
                    title: CMDBuildUI.locales.Locales.administration.processes.toolbar.processLabel,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * show process add
     */
    showProcessesAdministrationAdd: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-processes-view', {
            viewModel: {
                links: {
                    theProcess: {
                        reference: 'CMDBuildUI.model.processes.Process',
                        create: true
                    }
                },
                data: {
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    },
                    action: 'ADD',
                    objectType: 'Process',
                    title: CMDBuildUI.locales.Locales.administration.processes.toolbar.processLabel
                }
            }
        });
    },


    showReportAdministration_empty: function (showForm) {
        var hideForm = (showForm === 'true') ? false : true;
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-reports-view', {
        // CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-externalobjects-view', {
            singularName: 'report',
            viewModel: {
                links: {
                    theReport: {
                        type: 'CMDBuildUI.model.reports.Report',
                        create: true
                    }
                },
                data: {
                    action: 'VIEW',
                    actions: {
                        view: (hideForm) ? true : false,
                        edit: false,
                        add: (hideForm) ? false : true
                    },
                    hideForm: hideForm
                }
            }
        });
    },

    showReportAdministrationView: function (reportId) {
        
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-reports-view', {    
            singularName: 'report',
            viewModel: {
                links: {
                    theReport: {
                        type: 'CMDBuildUI.model.reports.Report',
                        id: reportId
                    }
                },

                data: {
                    reportId: reportId,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showCustompageAdministration_empty: function (showForm) {
        var hideForm = (showForm === 'true') ? false : true;
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-custompages-view', {
            viewModel: {
                links: {
                    theCustompage: {
                        type: 'CMDBuildUI.model.custompages.CustomPage',
                        create: true
                    }
                },
                data: {
                    actions: {
                        view: (hideForm) ? true : false,
                        edit: false,
                        add: (hideForm) ? false : true
                    },
                    hideForm: hideForm
                }
            }
        });
    },

    showCustompageAdministrationView: function (custompageId) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-custompages-view', {
            viewModel: {
                links: {
                    theCustompage: {
                        type: 'CMDBuildUI.model.custompages.CustomPage',
                        id: custompageId
                    }
                },

                data: {
                    custompageId: custompageId,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },
    showCustomcomponentAdministration_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomComponentUrl('contextmenu', false);
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showCustomcomponentAdministrationView_empty: function (componentType, showForm) {
        var hideForm = (showForm === 'true') ? false : true;
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-customcomponents-view', {
            viewModel: {
                links: {
                    theCustomcomponent: {
                        type: 'CMDBuildUI.model.customcomponents.ContextMenu',
                        create: true
                    }
                },
                data: {
                    componentType: componentType,
                    actions: {
                        view: (hideForm) ? true : false,
                        edit: false,
                        add: (hideForm) ? false : true
                    },
                    hideForm: hideForm
                }
            }
        });
    },

    showCustomcomponentAdministrationView: function (componentType, customcomponentId) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-customcomponents-view', {
            viewModel: {
                links: {
                    theCustomcomponent: {
                        type: 'CMDBuildUI.model.customcomponents.ContextMenu',
                        id: customcomponentId
                    }
                },

                data: {
                    componentType: componentType,
                    customcomponentId: customcomponentId,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },
    showUsersAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getUsersUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showEmailAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getEmailAccountsUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showTaskReadEmailAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTaskManagerReadEmailsUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showGisAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getGISManageIconsUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showBimAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getBIMProjectsUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showLocalizationAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getLocalizationConfigurationUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showSetupAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getGeneralOptionsUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showImportExportAdministrationView_empty: function () {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportTemplatesUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, this);
    },
    showUsersAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-users-view', {});
    },

    /**
     * Show settings view pages
     */
    showSetupAdministrationView: function (setupPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-setup-view', {
            viewModel: {
                data: {
                    currentPage: setupPage,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showGroupsandpermissionsAdministrationView: function (roleId) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-groupsandpermissions-view', {
            viewModel: {
                links: {
                    theGroup: {
                        reference: 'CMDBuildUI.model.users.Group',
                        id: roleId
                    }
                },
                data: {
                    objectType: roleId,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showGroupsandpermissionsAdministration_empty: function () {
        Ext.ComponentQuery.query('viewport')[0].getViewModel().set('isFormHidden', true);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-groupsandpermissions-view', {
            viewModel: {
                links: {
                    theGroup: {
                        reference: 'CMDBuildUI.model.users.Group',
                        create: true
                    }
                },
                data: {
                    action: 'ADD',
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                }
            }
        });
    },

    /**
     * Show email template view pages
     */
    showEmailTemplatesAdministrationView: function (emailPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-emails-templates-view', {
            viewModel: {
                data: {
                    currentPage: emailPage,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show email accounts view pages
     */
    showEmailAccountsAdministrationView: function (emailPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-emails-accounts-view', {
            viewModel: {
                data: {
                    currentPage: emailPage,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },
    /**
     * Show email queue page
     */
    showEmailQueueAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-emails-queue-view', {
            viewModel: {}
        });
    },
    /**
     * Show localization view pages
     */
    showLocalizationsLocalizationAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-localizations-localization-view', {
            viewModel: {
                data: {
                    currentPage: localizationPage,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    // /**
    //  * Show navigation tree view pages
    //  */
    // showNavigationtreeAdministration_empty: function (showForm) {
    //     var hideForm = (showForm === 'true') ? false : true;
    //     CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-navigationtrees-view', {
    //         viewModel: {
    //             links: {
    //                 theNavigationtree: {                       
    //                     type: 'CMDBuildUI.model.administration.AdminNavTree',
    //                     create: true
    //                 }
    //             },
    //             data: {
    //                 actions: {
    //                     view: (hideForm) ? true : false,
    //                     edit: false,
    //                     add: (hideForm) ? false : true
    //                 },
    //                 hideForm: hideForm
    //             }
    //         }
    //     });
    // },

    showNavigationtreeAdministrationView: function (navigationtreesId) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-navigationtrees-view', {
            viewModel: {
                links: {
                    theNavigationtree: {
                        type: 'CMDBuildUI.model.administration.AdminNavTree',
                        id: navigationtreesId
                    }
                },

                data: {
                    navigationtreesId: navigationtreesId,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showNavigationtreeAdministrationView_empty: function (showForm) {
        var hideForm = (showForm === 'true') ? false : true;
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-navigationtrees-view', {
            viewModel: {
                links: {
                    theNavigationtree: {
                        type: 'CMDBuildUI.model.administration.AdminNavTree',
                        create: true
                    }
                },

                data: {
                    actions: {
                        view: (hideForm) ? true : false,
                        edit: false,
                        add: (hideForm) ? false : true
                    },
                    hideForm: hideForm
                }
            }
        });
    },

    /**
     * Show localization view pages
     */
    showLocalizationsConfigurationAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-localizations-configuration-view', {
            viewModel: {
                data: {
                    currentPage: localizationPage,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show task empty page
     */
    showTaskAdministration_empty: function (showForm) {
        var hideForm = (showForm === 'true') ? false : true;
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                links: {
                    theTask: {
                        type: 'CMDBuildUI.model.tasks.Task',
                        create: true
                    }
                },
                data: {
                    actions: {
                        view: (hideForm) ? true : false,
                        edit: false,
                        add: (hideForm) ? false : true
                    },
                    hideForm: hideForm
                }
            }
        });
    },
    /**
     * Show read email task page
     */
    showTaskReadEmailAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                taskType: 'emailService',
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showTaskImportExportAdministrationView: function (type) {

        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {

            type: type,
            viewModel: {
                data: {
                    taskType: type,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show send email task page
     */
    showTaskSendEmailAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },
    /**
     * Show sync event task page
     */
    showTaskSyncEventAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show async event task page
     */
    showTaskAsyncEventAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show start process task page
     */
    showTaskStartProcessAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },
    /**
     * Show wizard task page
     */
    showTaskWizardConnectorAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-tasks-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showViewAdministrationView: function (viewName) {
        var store = Ext.getStore('views.Views');
        var record;
        if (store) {
            record = store.findRecord('name', viewName);
        }
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-views-card-form', {
            viewModel: {
                // links: {
                //     type: 'CMDBuildUI.model.views.View',
                //     id: viewName
                // },
                data: {
                    theViewFilter: record,

                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showViewAdministrationView_empty: function (showForm, viewType) {
        // var theViewFilter = CMDBuildUI.model.views.View.create({type: viewType});
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-views-card-form', {
            viewModel: {
                data: {
                    create: true,
                    showForm: showForm,
                    // theViewFilter: theViewFilter,
                    viewType: viewType,
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                }
            }
        });
    },


    showSearchFilterAdministrationView_empty: function (showForm) {
        var hideForm = (showForm === 'true') ? false : true;
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-viewfilters-form', {
            viewModel: {
                links: {
                    theViewFilter: {
                        type: 'CMDBuildUI.model.searchfilters.Searchfilter',
                        create: {
                            shared: true
                        }
                    }
                },

                data: {
                    actions: {
                        view: (hideForm) ? true : false,
                        edit: false,
                        add: (hideForm) ? false : true
                    },
                    hideForm: hideForm
                }
            }
        });
    },
    /**
     * Show search filters page
     */
    showSearchFilterAdministrationView: function (searchfilter) {
        var store = Ext.getStore('searchfilters.Searchfilters');
        var record;
        if (store) {
            record = store.findRecord('name', decodeURI(searchfilter));
        }
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-viewfilters-form', {
            viewModel: {
                links: {
                    theViewFilter: {
                        type: 'CMDBuildUI.model.searchfilters.Searchfilter',
                        id: record.getId()
                    }
                },
                data: {
                    theViewFilter: record,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },
    /**
     * Show GIS Icons management view pages
     */
    showGisManageIconsAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-gis-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show GIS External Services view pages
     */
    showGisExternalServicesAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-gis-externalservices-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show GIS Layers Order view pages
     */
    showGisLayersOrderAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-gis-layersorder-grid', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showGisGisNavigationAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-gisnavigationtrees-view', {
            viewModel: {
                data: {
                    navigationtreesId: 'gisnavigation',
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show GIS Server Layers view pages
     */
    showGisGeoserversLayersAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-gis-geoserverslayers-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show GIS Server Layers view pages
     */
    showGisThematismAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-gis-thematisms-grid', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * Show GIS Server Layers view pages
     */
    showBimProjectsAdministrationView: function (localizationPage, action) {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-bim-projects-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showBimNavigationAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-bimnavigationtrees-view', {
            viewModel: {
                data: {
                    navigationtreesId: 'bimnavigation',
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    showImportExportAdministrationView: function () {
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-importexportdata-templates-view', {
            viewModel: {
                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    }

});
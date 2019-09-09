Ext.define('CMDBuildUI.view.administration.content.tasks.card.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.view-administration-content-tasks-card',
    data: {
        theTask: null,
        currentStep: 0,
        totalStep: 0,
        isPrevDisabled: true,
        isNextDisabled: true,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        isExport: false,
        isImport: false,
        isModifyCard: false,
        isCsv: false,
        isExcell: false,
        isClass: false,
        isDomain: false,
        isAdvancedCron: undefined,
        isFilterFunction: false,
        isFilterRegex: false,
        isMoveReject: false,
        isNeverNotification: true,
        isSourceFile: false,
        isSourceUrl: false,
        isBodyParsing: false,
        isNotificationActive: false,
        isAttachmentActive: false,
        isDmsEnabled: false,
        isWorkflowEnabled: false,
        isStartProcessActive: false,
        defaultDmsLookupType: null,
        allClassOrDomainsAtributesData: [],
        workflowClassName: null,
        isTypeFieldHidden: false,
        attributeKeyValueList: [],
        dmsStoredata: {
            url: null,
            autoLoad: false
        },
        processDmsStoredata: {
            url: null,
            autoLoad: false
        },
        allAttributesOfProcessData: []
    },

    formulas: {
        taskTypeManager: {
            bind: {
                taskType: '{taskType}'
            },
            get: function (data) {
                switch (data.taskType) {
                    case 'emailService':
                    case 'workflow':
                        this.set('isTypeFieldHidden', true);
                        break;

                    default:
                        break;
                }
            }
        },
        workflowClassNameManager: {
            bind: {
                workflowClassName: '{theTask.config.action_workflow_class_name}',
                className: '{theTask.config.classname}',
                comeFromClass: '{comeFromClass}'
            },
            get: function (data) {                              
                if (data.comeFromClass) {                    
                    this.get('theTask')._config.set('class_name', data.comeFromClass);                    
                }else{
                    this.set('workflowClassName', data.workflowClassName);
                }
            }
        },
        allAttributesOfProcesManager: {
            bind: {
                workflowClassName: '{workflowClassName}',
                defaultDmsLookupType: '{defaultDmsLookupType}'
            },
            get: function (data) {
                if (data.workflowClassName && data.defaultDmsLookupType) {
                    var me = this;
                    var process = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(data.workflowClassName);                    
                    me.set('processAttachmentTypeLookup', process.get('attachmentTypeLookup'));
                    process.getAttributes().then(function (attributes) {
                        me.set('allAttributesOfProcessData', attributes.getRange());
                    });
                }
            }
        },

        allAttributesOfProcessDataFilterManager: {
            bind: {
                processAttributesMapStore: '{processAttributesMapStore.data}'
            },
            get: function (data) {
                this.set('allAttributesOfProcessDataFilter', this._allAttributeFilter());
            }
        },

        attributeKeyValueListManager: {
            bind: {
                fieldsMappingsEmailService: '{theTask.config.action_workflow_fields_mapping}',
                fieldsMappingsWorkflow: '{theTask.config.attributes}',
                workflowClassName: '{workflowClassName}'
            },
            get: function (data) {
                if (data.workflowClassName) {
                    this.setProcessAttributesMapStore(data.workflowClassName, data.fieldsMappingsEmailService || data.fieldsMappingsWorkflow);
                }
            }
        },
        processAttachmentTypeLookupManager: {
            bind: {
                processAttachmentTypeLookup: '{processAttachmentTypeLookup}',
                workflowClassName: '{workflowClassName}',
                defaultDmsLookupType: '{defaultDmsLookupType}'
            },
            get: function (data) {                
                if (data.processAttachmentTypeLookup) {
                    this.set('processDmsStoredata.url', Ext.String.format("/lookup_types/{0}/values", CMDBuildUI.util.Utilities.stringToHex(data.processAttachmentTypeLookup)));
                    this.set('processDmsStoredata.autoLoad', true);
                } else {
                    this.set('processDmsStoredata.url', this.get('dmsStoredata.url'));
                    this.set('processDmsStoredata.autoLoad', true);
                }
            }
        },
        parentLookupValuesProxy: {
            bind: '{defaultDmsLookupType}',
            get: function (defaultDmsLookupType) {                
                if (defaultDmsLookupType) {
                    this.set('dmsStoredata.url', Ext.String.format("/lookup_types/{0}/values", CMDBuildUI.util.Utilities.stringToHex(defaultDmsLookupType)));
                    this.set('dmsStoredata.autoLoad', true);
                }
            }
        },

        setupManager: {
            bind: {
                theSetup: '{theSetup}'
            },
            get: function (data) {
                this.set('isDmsEnabled', data.theSetup.org__DOT__cmdbuild__DOT__dms__DOT__enabled === 'true');
                this.set('defaultDmsLookupType', data.theSetup.org__DOT__cmdbuild__DOT__dms__DOT__category__DOT__lookup);
                this.set('isWorkflowEnabled', data.theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__enabled === 'true');
            }
        },

        notificationManager: {
            bind: '{theTask.config.notificationMode}',
            get: function (notificationMode) {
                switch (notificationMode) {
                    case '':
                    case 'never':
                        this.set('isNeverNotification', true);
                        break;
                    default:
                        this.set('isNeverNotification', false);
                        break;
                }
            }
        },

        isMoveRejectManger: {
            bind: {
                moveReject: '{theTask.config.filter_reject}'
            },
            get: function (data) {
                switch (data.moveReject) {
                    case true:
                    case "true":
                        this.set('isMoveReject', true);
                        break;

                    default:
                        this.set('isMoveReject', false);
                        break;
                }

            }
        },

        filterTypeManager: {
            bind: {
                filterType: '{theTask.config.filter_type}'
            },
            get: function (data) {
                // used in emailService
                if (data.filterType) {
                    switch (data.filterType) {
                        case 'function':
                            var functionsStore = this.getStore('allFunctionsStore');
                            if (!functionsStore.source.isLoaded()) {
                                functionsStore.source.load();
                            }
                            this.set('isFilterFunction', true);
                            this.set('isFilterRegex', false);
                            break;
                        case 'regex':
                            this.set('isFilterFunction', false);
                            this.set('isFilterRegex', true);
                            break;
                        case 'isreplay':
                        case 'isnotreplay':
                            this.set('isFilterFunction', false);
                            this.set('isFilterRegex', false);
                            break;
                        default:
                            this.set('isFilterFunction', false);
                            this.set('isFilterRegex', false);
                            break;
                    }
                }
            }
        },

        cronManager: {
            bind: {
                cronExpression: '{theTask.config.cronExpression}'
            },
            get: function (data) {
                if (data.cronExpression === 'advanced' || data.cronExpression === '') {
                    data.cronExpression = '* * * * ?';
                    this.set('isAdvancedCron', true);
                } else {
                    var store = CMDBuildUI.model.tasks.Task.cronSettings;
                    var record = store.findRecord('value', data.cronExpression);
                    this.set('isAdvancedCron', record ? false : !data.cronExpression ? false : true);
                }

                if (data.cronExpression) {
                    this.set('advancedCronMinuteValue', data.cronExpression.split(' ')[0]);
                    this.set('advancedCronHourValue', data.cronExpression.split(' ')[1]);
                    this.set('advancedCronDayValue', data.cronExpression.split(' ')[2]);
                    this.set('advancedCronMonthValue', data.cronExpression.split(' ')[3]);
                    this.set('advancedCronDayofweekValue', data.cronExpression.split(' ')[4]);
                }
            }
        },

        stepManager: {
            bind: {
                currentStep: '{currentStep}',
                totalStep: '{totalStep}'
            },
            get: function (data) {
                this.set('isPrevDisabled', data.currentStep === 0);
                this.set('isNextDisabled', data.currentStep >= this.get('totalStep') - 1);
            }
        },

        sourceManager: {
            bind: {
                action: '{theTask.config.postImportAction}',
                source: '{theTask.config.source}'
            },
            get: function (data) {
                switch (data.source) {
                    case 'file':
                        this.set('isSourceFile', true);
                        this.set('isSourceUrl', false);
                        this.set('isMoveFiles', data.action === 'move_files');
                        break;
                    case 'url':
                        this.set('isSourceFile', false);
                        this.set('isSourceUrl', true);
                        this.set('isMoveFiles', false);
                        break;
                    default:
                        this.set('isSourceFile', false);
                        this.set('isSourceUrl', false);
                        this.set('isMoveFiles', false);
                        break;
                }
            }
        },


        typeManager: {
            bind: '{theTask.type}',
            get: function (type) {
                if (this.get('notificationModesStore').isFiltered()) {
                    this.get('notificationModesStore').clearFilter();
                }
                if (this.get('allImportExportTemplate') && this.get('allImportExportTemplate').isFiltered()) {
                    this.get('allImportExportTemplate').clearFilter();
                }

                switch (type) {
                    case 'import_file':
                        this.get('notificationModesStore').filterBy(function (item) {
                            return item.get('group').includes('import');
                        });
                        this.set('allImportExportTemplateFilter', [function (item) {
                            return item.get('type') === 'import' || item.get('type') === 'import_export';
                        }]);
                        this.set('isImport', true);
                        this.set('isExport', false);
                        break;
                    case 'export_file':
                        this.set('notificationModeFilter', [function (item) {
                            return item.get('group').includes('export');
                        }]);
                        this.set('isImport', false);
                        this.set('isExport', true);
                        this.set('allImportExportTemplateFilter', [function (item) {
                            return item.get('type') === 'export' || item.get('type') === 'import_export';
                        }]);
                        break;
                    default:
                        this.set('notificationModeFilter', [function (item) {
                            return item.get('group').includes(type);
                        }]);

                        this.set('isImport', false);
                        this.set('isExport', false);
                        break;
                }
            }
        },

        isClone: {
            bind: '{theTask}',
            get: function (theTask) {
                return (theTask && theTask.phantom) || false;
            }
        },

        panelTitle: {
            bind: {
                description: '{theTask.description}',
                type: '{theTask.type}',
                currentStep: '{currentStep}',
                totalStep: '{totalStep}'
            },
            get: function (data) {

                if (data) {
                    var type = CMDBuildUI.model.tasks.Task.types.findRecord('value', data.type);
                    var typeLabel = type && type.get('label');
                    var title = Ext.String.format(
                        'Step {0} of {1} - {2} {3} {4} {5} {6}',
                        data.currentStep + 1,
                        data.totalStep,
                        isNaN(this.get('theTask._id')) ? 'New' : '',
                        data.description && typeLabel ? typeLabel : '',
                        'Task',
                        data.description ? '-' : '',
                        data.description
                    );
                    this.getParent().set('title', title);
                } else {
                    this.getParent().set('title', 'Task');
                }
            }
        },

        allClassesOrDomainsData: {
            bind: '{theTask.targetType}',
            get: function (targetType) {
                switch (targetType) {
                    case CMDBuildUI.model.administration.MenuItem.types.klass:
                        return Ext.getStore('classes.Classes').getData().items.filter(function (item) {
                            return !item.get('prototype') && item.get('type') === 'standard';
                        });
                    case CMDBuildUI.model.administration.MenuItem.types.domain:
                        return Ext.getStore('domains.Domains').getData().items;
                    default:
                        return [];
                }
            }
        }
    },

    stores: {
        notificationModesStore: {
            source: CMDBuildUI.model.tasks.TaskImportExportConfig.notificationModes,
            autoDestroy: true
        },
        allClassesOrDomains: {
            fields: ['_name', 'description'],
            proxy: {
                type: 'memory'
            },
            data: '{allClassesOrDomainsData}',
            autoDestroy: true
        },

        targetTypes: {
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            data: [{
                label: CMDBuildUI.locales.Locales.administration.localizations.class,
                value: CMDBuildUI.model.administration.MenuItem.types.klass
            }, {
                label: CMDBuildUI.locales.Locales.administration.localizations.domain,
                value: CMDBuildUI.model.administration.MenuItem.types.domain
            }],
            autoDestroy: true
        },

        allSelectedAttributes: {
            proxy: {
                type: 'memory'
            },
            model: 'CMDBuildUI.model.importexports.Attribute',
            data: '{allSelectedAttributesComboData}',
            autoDestroy: true
        },

        allEmailAccounts: {
            type: 'chained',
            source: 'emails.Accounts',
            autoLoad: true,
            autoDestroy: true
        },

        allEmailTemplates: {
            type: 'chained',
            source: 'emails.Templates',
            autoLoad: true,
            autoDestroy: true
        },

        allImportExportTemplate: {
            type: 'chained',
            source: 'importexports.Templates',
            filters: '{allImportExportTemplateFilter}',
            autoDestroy: true
        },

        allFunctionsStore: {
            type: 'chained',
            source: 'Functions',
            autoLoad: true,
            autoDestroy: true
        },

        dmsLookupStore: {
            model: "CMDBuildUI.model.lookups.Lookup",
            proxy: {
                url: '{dmsStoredata.url}',
                type: 'baseproxy'
            },
            extraParams: {
                active: false
            },
            pageSize: 0, // disable pagination
            fields: ['_id', 'description'],
            autoLoad: '{dmsStoredata.autoLoad}',
            sorters: [
                'description'
            ]
        },

        processDmsLookupStore: {
            model: "CMDBuildUI.model.lookups.Lookup",
            proxy: {
                url: '{processDmsStoredata.url}',
                type: 'baseproxy'
            },
            extraParams: {
                active: false
            },
            pageSize: 0, // disable pagination
            fields: ['_id', 'description'],
            autoLoad: '{processDmsStoredata.autoLoad}',
            sorters: [
                'description'
            ]
        },
        processesStore: {
            source: 'processes.Processes'
        },

        processAttributesMapStore: {
            model: 'CMDBuildUI.model.base.KeyDescriptionValue',
            sorters: ['description']
        },

        newProcessAttributesMapStore: {
            model: 'CMDBuildUI.model.base.KeyDescriptionValue',
            data: [CMDBuildUI.model.base.KeyDescriptionValue.create()]
        },

        allAttributesOfProcessStore: {
            proxy: {
                type: 'memory'
            },
            data: '{allAttributesOfProcessData}',
            sorters: ['description']
        },

        allAttributesOfProcessStoreFiltered: {
            source: '{allAttributesOfProcessStore}',
            proxy: {
                type: 'memory'
            },
            filters: '{allAttributesOfProcessDataFilter}',
            sorters: ['description']
        }
    },

    resetAllAttributesStores: function (classOrDomain) {
        var me = this;
        if (me.get('allClassOrDomainsAtributes')) {
            me.get('allClassOrDomainsAtributes').removeAll();
        }
        if (me.get('allClassOrDomainsAtributes')) {
            me.get('allClassOrDomainsAtributesFiltered').removeAll();
        }
        if (me.get('theTask.columns') && me.get('theTask.columns').isStore && me.get('actions.add')) {
            me.get('theTask.columns').removeAll();
            if (classOrDomain && classOrDomain.entityName === 'CMDBuildUI.model.domains.Domain') {
                Ext.Array.forEach(CMDBuildUI.model.importexports.Attribute.getRange(), function (item) {
                    switch (item.attribute) {
                        case 'IdObj1':
                            item.columnName = classOrDomain.get('source');
                            break;
                        case 'IdObj2':
                            item.columnName = classOrDomain.get('destination');
                            break;
                        default:
                            break;
                    }
                    me.get('theTask.columns').add(item);
                });
            }
        }
    },

    setAllAttributesStores: function (attributeStore) {
        var me = this;
        var store = Ext.create('Ext.data.Store', {
            data: attributeStore.getRange()
        });

        var filteredStore = Ext.create('Ext.data.Store', {
            data: attributeStore.getRange(),
            filters: [function (item) {
                return !me.getStore('allSelectedAttributes').findRecord('attribute', item.get('name'));
            }]
        });

        store.sort('description', 'ASC');
        filteredStore.sort('description', 'ASC');
        me.set('allClassOrDomainsAtributes', store);
        me.set('allClassOrDomainsAtributesFiltered', filteredStore);
    },

    _allAttributeFilter: function () {
        var me = this;
        return [function (item) {
            return !me.getStore('processAttributesMapStore').findRecord('key', item.get('name'));
        }];
    },

    serializeAttributesMapStore: function () {
        var attributesMapData = this.get('processAttributesMapStore').getRange();
        var attributes = [];
        Ext.Array.forEach(attributesMapData, function (item) {
            attributes.push(Ext.String.format('{0}={1}', item.get('key'), item.get('value')));
        });
        return attributes.join('&#124;');
    },

    setProcessAttributesMapStore: function (workflowClassName, fieldsMappings) {
        if (fieldsMappings && fieldsMappings.length) {
            var map = fieldsMappings.split('&#124;');
            var processAttributesMapStore = this.getStore('processAttributesMapStore');
            processAttributesMapStore.removeAll();
            CMDBuildUI.util.helper.ModelHelper.getProcessFromName(workflowClassName).getAttributes().then(function (attributes) {
                Ext.Array.forEach(map, function (item) {
                    var keyValue = item.split('=');
                    try {
                        processAttributesMapStore.add({ key: keyValue[0], description: attributes.findRecord('name', keyValue[0]).get('description'), value: keyValue[1] });
                    } catch (e) {
                        CMDBuildUI.util.Logger.log(Ext.String.format("Process attribute {0} not found", keyValue[0]), CMDBuildUI.util.Logger.levels.warn);
                    }
                });
            });
        }
    }
});
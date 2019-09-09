Ext.define('CMDBuildUI.locales.LocalesAdministration', {
    singleton: true,

    localization: 'en',

    administration: {
        tasks: {
            sincurrentStepgular: 'Task',
            plural: 'Tasks',
            emptytexts: {
                searchingrid: 'Search in grid...',
                searchcustompages: 'Search tasks...'
            },
            fieldlabels: {
                active: 'Active', // mapped
                actions: 'Actions', // mapped
                code: 'Code',
                startonsave: 'Start on save',
                account: 'Account',
                incomingfolder: 'Incoming folder',
                processedfolder: 'Processed folder',
                rejectedfolder: 'Rejected folder',
                filter: 'Filter',
                filtertype: 'Filter type',
                sender: 'Sender',
                subject: 'Subject'
            },
            strings: {
                advanced: 'Advanced'
            },
            texts: {
                addtask: 'Add task',
                reademails: 'Read emails',
                sendemails: 'Send emails',
                syncronousevents: 'Sync events',
                asyncronousevents: 'Async events',
                startprocesses: 'Start processes',
                wizardconnectors: 'Wizard connectors'
            },
            tooltips: {
                delete: 'Delete task',
                disable: 'Disable task',
                enable: 'Enable task',
                edit: 'Edit task',
                singleexecution: 'Single execution',
                cyclicexecution: 'Cyclic execution',
                stop: 'Stop',
                start: 'Start',
                started: 'Started',
                stopped: 'Stopped',
                execution: 'Execution'
            },

            type: 'Type',
            template: 'Template',
            source: 'Source',
            directory: 'Directory',
            filename: 'File name',
            filepattern: 'File pattern',
            url: 'URL',
            cron: 'Cron',
            minutes: 'Minutes',
            hour: 'Hour',
            day: 'Day',
            month: 'Month',
            dayofweek: 'Day of week',
            erroremailtemplate: 'Error email template',
            notificationmode: 'Notification mode',
            account: 'Account',
            emailtemplate: 'Email template',
            postimportaction: 'Post import action',
            filtertype: 'Filter type',
            sender: 'Sender',
            subject: 'Subject',
            movereject: 'Move rejected not matching',
            bodyparsing: 'Body parsing',
            sendnotiifcation: 'Send notification email',
            saveattachmentsdms: 'Save attachemnts to DMS',
            category: 'Category',
            startprocess: 'Start process',
            jobusername: 'Job username',
            advanceworkflow: 'Advance workflow',
            saveattachments: 'Save Attachments',
            processattributes: 'Process attributes',
            value: 'Value',
            incomingfolder: 'Incoming folder',
            processedfolder: 'Processed folder',
            rejectedfolder: 'Rejected folder',
            keystartdelimiter: 'Key start delimiter',
            keyenddelimiter: 'Key end delimiter',
            valuestartdelimiter: 'Value start delimiter',
            valueenddelimiter: 'Value end delimiter',
            settings: 'Settings',
            notifications: 'Notifications',
            parsing: 'Parsing',
            addtask: 'Add task'
        },
        systemconfig: {
            url: 'Url',
            host: 'Host',
            cmis: 'CMIS',
            postgres: 'Postgres',
            webservicepath: 'Webservice path',
            preset: 'Preset',
            alfresco: 'Alfresco',
            generals: 'Generals',
            instancename: 'Instance name',
            defaultpage: 'Default page',
            relationlimit: 'Relation Limit',
            referencecombolimit: 'Reference combobox limit',
            sessiontimeout: 'Session timeout',
            ajaxtimeout: 'AJAX timeout',
            noteinline: 'Inline notes',
            noteinlinedefaultclosed: 'Inline notes default closed',
            preferredofficesuite: 'Preferred Office suite',
            showcardlockerusername: 'Shows the name of the user who blocked the card',
            maxlocktime: 'Maximum lock time (seconds)',
            gridautorefresh: 'Grid autorefresh',
            frequency: 'Frequency (seconds)',
            companylogo: 'Company logo',
            logo: 'Logo',
            initiallatitude: 'Initial latitude',
            initialongitude: 'Initial longitude',
            initialzoom: 'Initial zoom',
            configurationmode: 'Configuration mode',
            dropcache: 'Drop cache',
            synkservices: 'Synchronize services',
            unlockallcards: 'Unlock all cards',
            enableattachmenttoclosedactivities: 'Enable "Add attachment" to closed activities',
            usercandisable: 'User can disable',
            hidesavebutton: 'Hide "Save" button',
            dafaultjobusername: 'Default job username',
            serviceurl: 'Service URL',
            disablesynconmissingvariables: 'Disable synchronization missing variables',
            editmultitenantisnotallowed: 'Edit multitenant settings is not allowed',
            tecnotecariver: 'Tecnoteca River',
            shark: 'Enhydra Shark',
            lockmanagement: 'Lock management',
            multitenantinfomessage: 'It is recommended to change these settings only after having consulted the guidelines present in the Administrator Manual, downloadable from {0}',
            multitenantactivationmessage: 'Changing these settings is irreversible, unless the database is restored. It is recommended to backup the database before proceeding.',
            multitenantapllychangesquest: 'Do you want to apply the changes?'
        },
        importexport: {
            texts: {
                addtemplate: 'Add template',
                import: 'Import',
                export: 'Export',
                importexport: 'Import/Export',
                nodelete: 'No delete',
                delete: 'Delete',
                modifycard: 'Modify card',
                templates: 'Templates',
                emptyattributegridmessage: 'Attributes grid can\'t be empty',
                columnname: 'Column name',
                mode: 'Mode',
                selectmode: 'Select mode',
                default: 'Default',
                selectanattribute: 'Select an attribute',
                importmergecriteria: 'Import merge criteria',
                errorsmanagements: 'Errors management',
                erroremailtemplate: 'Error email template',
                account: 'Account'
            },
            fieldlabels: {
                useheader: 'Use header',
                ignorecolumn: 'Ignore order',
                applyon: 'Apply on',
                classdomain: 'Class/Domain',
                type: 'Type',
                fileformat: 'File Format',
                csvseparator: 'CSV separator',
                firstcolumnnumber: 'First column number',
                headerrownumber: 'Header row number',
                datarownumber: 'Data row number',
                importkeattribute: 'Import key attribute',
                missingrecords: 'Missing records',
                value: 'Value',
                exportfilter: 'Export filter'
            },
            emptyTexts: {
                searchfield: 'Search all templates...'
            }
        },
        tesks: {
            labels: {
                activeonsave: 'Active on save',
                emailaccount: 'Email account',
                incomingfolder: 'Incoming folder',
                filtertype: 'Filter type'
            }
        },
        views: {
            addview: 'Add view',
            ralations: 'Relations',
            addfilter: 'Add filter',
            ragetclass: 'Target class',
            targetclass: 'Target class'
        },
        geoattributes: {
            fieldLabels: {
                referenceclass: 'Reference class', // mapped
                type: 'Type', // mapped
                minzoom: 'Minimum zoom', // mapped
                maxzoom: 'Maximum zoom', // mapped
                defzoom: 'Default zoom',
                visibility: 'Visibility', // mapped
                fillopacity: 'Fill opacity', // need map
                fillcolor: 'Fill color', // need map
                icon: 'Icon', // need map
                pointradius: 'Point radius', // need map
                strokedashstyle: 'Strike dashstyle', // need map
                strokeopacity: 'Stroke opacity', // need map
                strokecolor: 'Stroke color', // need map
                strokewidth: 'Stroke width' // need map
            },
            strings: {
                specificproperty: 'Specific properties'
            }
        },
        localizations: {
            localization: 'Localization',
            configuration: 'Configuration',
            class: 'Class',
            attributeclass: 'Attribute class',
            attributegroup: 'Attribute group',
            custompage: 'Custom page',
            process: 'Process',
            attributeprocess: 'Attribute process',
            domain: 'Domain',
            attributedomain: 'Attribute domain',
            view: 'View',
            lookup: 'Lookup',
            report: 'Report',
            dashboard: 'Dashboard',
            attributereport: 'Attribute report',
            menuitem: 'Menu item',
            languageconfiguration: 'Language Configuration',
            defaultlanguage: 'Default language',
            showlanguagechoice: 'Show language choice',
            enabledlanguages: 'Enabled languages',
            section: 'Section',
            languages: 'Languages',
            format: 'Format',
            separator: 'Separator',
            activeonly: 'Active only',
            export: 'Export',
            cancel: 'Cancel',
            all: 'All',
            file: 'File',
            import: 'Import',
            csv: 'CSV',
            pdf: 'PDF',
            treemenu: 'Tree menu',
            defaulttranslation: 'Default Translation',
            element: 'Element',
            type: 'Type'
        },
        emails: {
            date: 'Date',
            start: 'Start',
            send: 'Send',
            sent: 'Sent',
            queue: 'Queue',
            key: 'Key', // mapped
            value: 'Value', // mapped
            remove: 'Remove', // mapped
            addrow: 'Add row', // mapped
            notnullkey: 'One or more values have a null key',
            name: 'Name', // mapped
            description: 'Description', // mapped
            subject: 'Subject', // mapped
            addtemplate: 'Add template', // mapped
            clonetemplate: 'Clone', // mapped
            email: "Email",
            templates: "Templates", // mapped
            keepsync: 'Keep sync', // mapped
            promptsync: "Prompt sync", // mapped
            delay: "Delay", // mapped
            template: 'Template', // mapped
            defaultaccount: 'Default account', // mapped
            from: 'From', // mapped
            to: 'To', // mapped
            cc: 'Cc', // mapped
            bcc: 'Bcc', // mapped
            body: 'Body', // mapped
            editvalues: 'Edit values', // mapped
            newtemplate: 'New template',
            templatesavedcorrectly: 'Template saved correctly',
            newaccount: 'New account',
            accountsavedcorrectly: 'Account saved correctly',
            delays: { // allmapped
                day1: "In 1 day",
                days2: "In 2 days",
                days4: "In 4 days",
                hour1: "1 hour",
                hours2: "2 hours",
                hours4: "4 hours",
                month1: "In 1 month",
                none: "None",
                week1: "In 1 week",
                weeks2: "In 2 weeks"
            },
            removetemplate: 'Remove', // mapped !!!
            removeaccount: 'Remove account', // mapped
            username: 'Username', // mapped
            password: 'Password', // mapped
            outgoing: 'Outgoing', // mapped
            address: 'Address', // mapped
            smtpserver: 'SMTP server', // mapped
            smtpport: 'SMTP port', // mapped
            enablessl: 'Enable SSL', // mapped
            enablestarttls: 'Enable STARTTLS', // mapped
            sentfolder: 'Sent folder', // mapped
            incoming: 'Incoming', // mapped
            imapserver: 'IMAP server', // mapped
            imapport: 'IMAP port', // mapped
            setdefaultaccount: 'Set default account',
            accounts: 'Accounts',
            contenttype: 'Content type',
            addaccount: 'Add account'
        },
        gis: {
            manageicons: 'Manage icons',
            addicon: 'Add icon',
            addlayer: 'Add layer',
            icon: 'Icon',
            description: 'Description',
            newicon: 'New icon',
            editicon: 'Edit icon',
            deleteicon: 'Delete icon',
            deleteicon_confirmation: 'Are you sure you want to delete this icon?',
            mapservice: 'Map Service',
            servicetype: "Service type",
            defaultzoom: 'Default zoom',
            minimumzoom: 'Minimum zoom',
            maximumzoom: 'Maximum zoom',
            geoserver: 'Geoserver',
            url: 'URL',
            workspace: 'Workspace',
            adminuser: 'Admin user',
            adminpassword: 'Admin password',
            externalservices: 'External services',
            layersorder: 'Layers order',
            geoserverlayers: 'Geoserver layers',
            file: 'File',
            associatedclass: 'Associated class',
            associatedcard: 'Associated card',
            type: 'Type',
            searchemptytext: 'Search thematisms',
            thematism: 'Thematism',
            thematisms: 'Thematisms',
            owneruser: 'User',
            ownerclass: 'Class',
            global: 'Global'
        },
        bim: {
            ifcfile: 'IFC File',
            mappingfile: 'Mapping file',
            lastcheckin: 'Last check-in',
            parentproject: 'Parent project',
            projects: 'Projects', // mapped,
            multilevel: 'Multilevel',
            addproject: 'Add project', // mapped
            newproject: 'New project',
            projectlabel: 'Project'
        },
        attributes: {
            attribute: 'Attribute', // mapped
            attributes: 'Attributes', // mapped
            emptytexts: {
                search: 'Search...' // no map
            },
            fieldlabels: {
                actionpostvalidation: 'Action post validation', // no map
                autovalue: 'Auto value',
                active: 'Active', // mapped
                attributegroupings: 'Attribute groupings',
                description: 'Description', // mapped
                domain: 'Domain', // mapped
                editortype: 'Editor type', // mapped
                filter: 'Filter', // mapped
                format: 'Format',
                group: 'Group', // mapped
                help: 'Help', // no map
                includeinherited: 'Include Inherited', // mapped
                iptype: 'IP type', // mapped
                lookup: 'Lookup', // mapped
                mandatory: 'Mandatory', // mapped
                maxlength: 'Max Length', // mapped
                mode: 'Mode', // mapped
                name: 'Name', // mapped
                precision: 'Precision', // mapped
                preselectifunique: 'Preselect if unique', // mapped
                scale: 'Scale', // mapped
                thousandsseparator: 'Thousands separator',
                decimalseparator: 'Decimal separator',
                separators: 'Separators',
                separator: 'Separator',
                showseparator: 'Show separator',
                unitofmeasure: 'Unit of measure',
                positioningofum: 'Positioning of the UM',
                visibledecimals: 'Visible decimals',
                showif: 'View rules', // no map
                showingrid: 'Show in grid', // no map
                showinreducedgrid: 'Show in reduced grid', // no map
                type: 'Type', // mapped
                unique: 'Unique', // mapped
                validationrules: 'Validation rules', // no map
                showseconds: 'Show seconds'
            },
            strings: {
                any: 'Any', // mapped
                draganddrop: 'Drag and drop to reorganize', // no map
                editable: 'Editable', // mapped
                editorhtml: 'Editor HTML', // mapped
                hidden: 'Hidden', // mapped
                immutable: 'Immutable', // no map
                ipv4: 'IPV4', // mapped
                ipv6: 'IPV6', // mapped
                plaintext: 'Plain text', // mapped
                precisionmustbebiggerthanscale: 'Precision must be bigger than Scale', // no map
                positioningofumrequired: 'The position of the unit of measurement is mandatory', // no map
                readonly: 'Read only', // mapped
                scalemustbesmallerthanprecision: 'Scale must be smaller than Precision', // no map
                thefieldmandatorycantbechecked: 'The field "Mandatory" can\'t be checked', // no map
                thefieldmodeishidden: 'The field "Mode" is hidden', // no map
                thefieldshowingridcantbechecked: 'The field "Show in grid" can\'t be checked', // no map
                thefieldshowinreducedgridcantbechecked: 'The field "Show in reduced grid" can\'t be checked', // no map
                createnewgroup: 'Create new group',
                addnewgroup: 'Add new group'
            },

            texts: {
                active: 'Active', // mapped
                addattribute: 'Add attribute',
                newattribute: 'New attribute',
                cancel: 'Cancel', // mapped
                description: 'Description', // mapped
                editingmode: 'Editing mode',
                editmetadata: 'Edit metadata', // mapped
                grouping: 'Grouping', // no map
                mandatory: 'Mandatory', // mapped
                name: 'Name', // mapped
                save: 'Save', // mapped
                saveandadd: 'Save and Add',
                showingrid: 'Show in grid',
                type: 'Type', // mapped
                unique: 'Unique', // mapped
                viewmetadata: 'View metadata', // no map
                direct: 'Direct',
                inverse: 'Inverse'
            },
            titles: {
                // generalproperties: 'General properties', // no map
                typeproperties: 'Type properties', // mapped
                otherproperties: 'Other properties' // mapped
            },
            tooltips: {
                deleteattribute: 'Delete', // mapped
                disableattribute: 'Disable', // mapped
                editattribute: 'Edit', // mapped
                enableattribute: 'Enable', // mapped
                openattribute: 'Open', //mapped
                translate: 'Translate' // no map
            }
        },
        classes: {
            fieldlabels: {
                superclass: 'Superclass',
                multitenantmode: 'Multitenant mode',
                categorylookup: 'Category Lookup',
                descriptionmode: 'Description mode',
                applicability: 'Applicability',
                widgetname: 'Widget Name',
                guicustom: 'Gui custom',
                guicustomparameter: 'GUI custom parameters',
                defaultimporttemplate: 'Default template for data import',
                defaultexporttemplate: 'Default template for data export',
                attachmentsinline: 'Inline attachments',
                attachmentsinlineclosed: 'Closed inline attachments'
            },
            texts: {
                class: 'class',
                component: 'Component',
                custom: 'Custom',
                separator: 'Separator',
                calendar: 'Calendar',
                createmodifycard: 'Create / Modify Card',
                createreport: 'Create Report',
                ping: 'Ping',
                startworkflow: 'Start workflow',
                standard: 'Standard',
                simple: 'Simple',
                direction: 'Direction'

            },
            strings: {
                geaoattributes: 'Geo attributes', // no map
                levels: 'Layers', // fix key name
                deleteclass: 'Delete class',
                deleteclassquest: "Are you sure you want to delete this class?",
                classactivated: 'Class activated correctly.',
                classdisabled: 'Class disabled correctly.',
                createnewcontextaction: 'Create new context action',
                edittrigger: 'Edit trigger',
                editcontextmenu: 'Edit context menu',
                editformwidget: 'Edit form widget',
                datacardsorting: 'Data cards sorting',
                executeon: 'Execute on'

            },
            properties: {
                form: {
                    fieldsets: {
                        ClassAttachments: 'Class Attachments', // no map
                        classParameters: 'Class Parameters', // no map
                        contextMenus: {
                            actions: {
                                delete: {
                                    tooltip: 'Delete' // mapped
                                },
                                edit: {
                                    tooltip: 'Edit' // mapped
                                },
                                moveDown: {
                                    tooltip: 'Move Down' // no map
                                },
                                moveUp: {
                                    tooltip: 'Move Up' // no map
                                }
                            },
                            inputs: {
                                applicability: {
                                    label: 'Applicability', // no map
                                    values: {
                                        all: {
                                            label: 'All' // mapping
                                        },
                                        many: {
                                            label: 'Current and selected' // no map
                                        },
                                        one: {
                                            label: 'Current' // mapping
                                        }
                                    }
                                },
                                javascriptScript: {
                                    label: 'Javascript script / custom GUI Paramenters' // no map
                                },
                                menuItemName: {
                                    label: 'Menu item name', // no map
                                    values: {
                                        separator: {
                                            label: '[---------]' // no map
                                        }
                                    }
                                },
                                status: {
                                    label: 'Status', // mapped
                                    values: {
                                        active: {
                                            label: 'Active' // mapped
                                        }
                                    }
                                },
                                typeOrGuiCustom: {
                                    label: 'Type / GUI custom', // no map
                                    values: {
                                        component: {
                                            label: 'Custom GUI' // no map
                                        },
                                        custom: {
                                            label: 'Script Javascript' // no map
                                        },
                                        separator: {
                                            label: ''
                                        }
                                    }
                                }
                            },
                            title: 'Context Menus' // no map
                        },
                        defaultOrders: 'Default Orders', // no map
                        formTriggers: {
                            actions: {
                                addNewTrigger: {
                                    tooltip: 'Add new Trigger' // no map
                                },
                                deleteTrigger: {
                                    tooltip: 'Delete' // no map
                                },
                                editTrigger: {
                                    tooltip: 'Edit' // mapped
                                },
                                moveDown: {
                                    tooltip: 'Move Down' // no map
                                },
                                moveUp: {
                                    tooltip: 'Move Up' // no map
                                }
                            },
                            inputs: {
                                createNewTrigger: {
                                    label: 'Create new form trigger' // no map
                                },
                                events: {
                                    label: 'Events', // no map
                                    values: {
                                        afterClone: {
                                            label: 'After clone' // no map
                                        },
                                        afterDelete: {
                                            label: 'After delete' // no map
                                        },
                                        afterEdit: {
                                            label: 'After edit' // no map
                                        },
                                        afterInsert: {
                                            label: 'After insert' // no map
                                        },
                                        beforeClone: {
                                            label: 'Before clone' // no map
                                        },
                                        beforeEdit: {
                                            label: 'Before edit' // no map
                                        },
                                        beforeInsert: {
                                            label: 'Before insert' // no map
                                        },
                                        beforView: {
                                            label: 'Before view' // no map
                                        }
                                    }
                                },
                                javascriptScript: {
                                    label: 'Javascript script' // no map
                                },
                                status: {
                                    label: 'Status' // mapped
                                }
                            },
                            title: 'Form Triggers' // no map
                        },
                        formWidgets: 'Form Widgets', // no map
                        createnewwidget: 'Create new widget',
                        generalData: {
                            inputs: {
                                active: {
                                    label: 'Active' // mapped
                                },
                                classType: {
                                    label: 'Type' // mapped
                                },
                                description: {
                                    label: 'Description' // mapped
                                },
                                name: {
                                    label: 'Name' // mapped
                                },
                                parent: {
                                    label: 'Inherits from' // mapped // duplicate
                                },
                                superclass: {
                                    label: 'Supeclass' // mapped
                                }
                            }
                        },
                        icon: 'Icon', // mapped
                        validation: {
                            inputs: {
                                validationRule: {
                                    label: 'Validation Rule' // no map
                                }
                            },
                            title: 'Validation' // no map
                        }
                    },
                    inputs: {
                        events: 'Events', // no map
                        javascriptScript: 'Javascript Script', // no map
                        status: 'Status' // mapped
                    },
                    tooltips: {},
                    values: {
                        active: 'Active' // mapped
                    }
                },
                title: 'Properties', // mapped
                toolbar: {
                    cancelBtn: 'Cancel', // mapped
                    closeBtn: 'Close', // mapped
                    deleteBtn: {
                        tooltip: 'Delete' // mapped
                    },
                    disableBtn: {
                        tooltip: 'Disable' // mapped
                    },
                    editBtn: {
                        tooltip: 'Modify class' // mapped
                    },
                    enableBtn: {
                        tooltip: 'Enable' // mapped
                    },
                    printBtn: {
                        printAsOdt: 'OpenOffice Odt', // mapped
                        printAsPdf: 'Adobe Pdf', // mapped
                        tooltip: 'Print class' // mapped
                    },
                    saveBtn: 'Save' // mapped
                }
            },
            title: 'Classes', // mapped
            toolbar: {
                addClassBtn: {
                    text: 'Add class' // mapped
                },
                classLabel: 'Class', // mapped
                printSchemaBtn: {
                    text: 'Print schema' // mapped
                },
                searchTextInput: {
                    emptyText: 'Search all classes' // no map
                }
            }
        },
        common: {
            actions: {
                add: 'Add', // mapped
                activate: 'Activate',
                cancel: 'Cancel', // mapped
                clone: 'Clone', // mapped
                close: 'Close', // mapped
                create: 'Create', // mapped
                delete: 'Delete', // mapped
                disable: 'Disable', // mapped
                edit: 'Edit', // mapped
                enable: 'Enable', // mapped
                no: 'No', // mapped
                print: 'Print', // mapped
                save: 'Save', // mapped
                prev: 'Prev',
                next: 'Next',
                saveandadd: 'Save and add',
                update: 'Update', // mapped
                yes: 'Yes', // mapped
                ok: 'Ok',
                open: 'Open',
                relationchart: 'Relation chart',
                moveup: 'Move up',
                movedown: 'Move down',
                remove: 'Remove',
                clonefrom: 'Clone from...',
                download: 'Download'
            },
            messages: {
                applicationupdate: 'Application Update',
                applicationreloadquest: 'This application has an update, reload?',
                areyousuredeleteitem: 'Are you sure you want to delete this item?', // no map
                ascendingordescending: 'This value is not valid, please select "Ascending" or "Descending"', // no map
                attention: 'Attention', // mapped
                cannotsortitems: 'You can not reorder the items if some filters are present or the inherited attributes are hidden. Please remove them and try again.', // no map
                cantcontainchar: 'The class name can\'t contain {0} character.', // no map
                correctformerrors: 'Please correct indicated errors', // no map
                disabled: 'Disabled', // no map
                enabled: 'Enabled', // no map
                error: 'Error', // mapped
                greaterthen: 'The class name can\'t be greater than {0} characters', // no map
                itemwascreated: 'Item was created.', // no map
                loading: 'Loading...', // mapped
                saving: 'Saving...', // no map
                success: 'Success', // mapped
                warning: 'Warning', // mapped
                was: 'was', // no map
                wasdeleted: 'was deleted', // no map
                thisfieldisrequired: 'This field is required'
            },
            tooltips: {
                edit: 'Edit', // mapped
                open: 'Open', // mapped
                clone: 'Clone', // mapped
                localize: 'Localize',
                edittrigger: 'Edit trigger',
                add: 'Add'
            },
            strings: {
                attribute: 'Attribute',
                properties: 'Properties',
                generalproperties: 'General properties',
                ascending: 'Ascending',
                descending: 'Descending',
                default: '*Default*',
                never: 'Never',
                always: 'Always',
                mixed: 'Mixed',
                hidden: 'Hidden',
                visibleoptional: 'Visible optional',
                visiblemandatory: 'Visible mandatory',
                localization: 'Localization text',
                selectpngfile: 'Select an .png file',
                currenticon: 'Current icon',
                selectimage: 'Select image',
                iconimage: 'Image icon',
                image: 'Image',
                string: 'String',
                filtercql: 'Filter CQL',
                recursive: 'Recursive'
            },
            labels: {
                active: 'Active',
                name: 'Name',
                description: 'Description',
                defaultfilter: 'Default filter',
                noteinline: 'Inline notes',
                noteinlineclosed: 'Closed inline notes',
                code: 'Code',
                icon: 'Icon',
                iconpreview: 'Icon preview',
                iconcolor: 'Icon color',
                note: 'Notes',
                colorpreview: 'Color preview',
                icontype: 'Icon type',
                type: 'Type',
                funktion: 'Function',
                tenant: 'Tenant',
                status: 'Status',
                tree: 'Tree',
                textcolor: 'Text color',
                default: 'Default'
            },
            fieldlabels: {

            }
        },
        domains: {
            pluralTitle: 'Domains', // TODO: change with domains
            fieldlabels: {
                cardinality: 'Cardinality',
                masterdetail: 'Master detail',
                destination: 'Destination', // mapped
                enabled: 'Enabled', // mapped
                origin: 'Origin', // mapped
                directdescription: 'Direct description', // mapped
                inversedescription: 'Inverse description', // mapped
                masterdetailshort: 'M/D',
                labelmasterdetail: 'Label M/D',
                labelmasterdataillong: 'Label master detail',
                inline: 'Inline',
                link: 'Link',
                defaultclosed: 'Default closed',
                viewconditioncql: 'View Condition (CQL)'
            },

            domain: 'Domain', // mapped
            strings: {

            },
            texts: {
                properties: 'Properties', // mapped
                enabledclasses: 'Enabled classes', // no map // changed
                emptyText: 'Search all domains',
                addlink: 'Add link',
                adddomain: 'Add domain'
            },
            properties: { // remove

                toolbar: {
                    cancelBtn: 'Cancel', // mappped
                    deleteBtn: {
                        tooltip: 'Delete' // mapped
                    },
                    disableBtn: {
                        tooltip: 'Disable' // mapped
                    },
                    editBtn: {
                        tooltip: 'Edit' // mapped
                    },
                    enableBtn: {
                        tooltip: 'Enable' // mapped
                    },
                    saveBtn: 'Save' // mapped
                }
            },
            singularTitle: 'Domain', // mapped
            toolbar: {
                addBtn: {
                    text: 'Add domain' // mapped
                },
                searchTextInput: {
                    emptyText: 'Search all domains' // no map
                }
            }
        },
        groupandpermissions: {
            singular: 'Group and permission', // no map
            plural: 'Groups and permissions', // no map
            emptytexts: {
                searchingrid: 'Search in grid...', // no map
                searchusers: 'Search users...', // no map
                searchgroups: 'Search groups...'
            },
            fieldlabels: {
                active: 'Active', // mapped
                actions: 'Actions', // mapped
                attachments: 'Attachments', // mapped
                datasheet: 'Data sheet', // no map
                defaultpage: 'Default page', // no map
                description: 'Description', // mapped
                detail: 'Detail', // mapped
                email: 'Email',
                exportcsv: 'Export CSV file', // mapped
                filters: 'Filters', // no map
                history: 'History', // mapped
                importcsvfile: 'Import CSV file', // mapped
                massiveeditingcards: 'Massive editing of cards', // no map
                name: 'Name', // mapped
                note: 'Notes', // mapped
                relations: 'Relations', // mapped
                type: 'Type', // mapped
                username: 'Username' // mapped
            },
            strings: {
                admin: 'Admin', // no map
                limitedadmin: 'Limited administrator', // mapped
                normal: 'Normal', // mapped
                readonlyadmin: 'Read-only admin',
                displaytotalrecords: '{2} records',
                displaynousersmessage: "No users to display"
            },
            texts: {
                addgroup: 'Add group',
                allow: 'Allow',
                class: 'Class',
                copyfrom: 'Clone from', // new
                default: 'Default',
                defaultfilter: 'Default filter', // no map
                defaultfilters: 'Default filters', // mapped
                defaultread: 'Def. + R', // mapped
                description: 'Description', // mapped
                filters: 'Filters', // no map
                group: 'Group', // mapped
                name: 'Name', // mapped
                none: 'None', // mapped
                permissions: 'Permissions', // mapped
                read: 'Read', // mapped
                userslist: 'Users list', // no map
                uiconfig: 'UI configuration', // mapped
                write: 'Write', // mapped
                editfilters: 'Edit filters of {0}: {1}',
                viewfilters: 'View filters of {0}: {1}',
                columnsprivileges: 'Columns privileges',
                rowsprivileges: 'Rows privileges'
            },
            titles: {
                allusers: 'All users', // no map
                generalattributes: 'General Attributes', // no map
                disabledactions: 'Disabled actions', // no map
                disabledallelements: 'Functionality disabled Navigation menu "All Elements"', // no map
                disabledmanagementprocesstabs: 'Tabs Disabled Management Processes', // no map
                disabledutilitymenu: 'Functionality disabled Utilities Menu', // no map
                managementdisabledtabs: 'Tabs Disabled Management Classes', // no map
                usersassigned: 'Users assigned' // no map
            },
            tooltips: {
                filters: 'Filters', // no map
                removefilters: 'Remove filters', // no map
                disabledactions: 'Disabled actions', // no map
                removedisabledactions: 'Remove disabled actions' // no map
            }
        },
        lookuptypes: {
            title: 'Lookup Types',
            toolbar: {
                addClassBtn: {
                    text: 'Add lookup'
                },
                classLabel: 'List',
                printSchemaBtn: {
                    text: 'Print lookup'
                },
                searchTextInput: {
                    emptyText: 'Search all lookups...'
                }
            },
            strings: {
                parentdescription: 'Parent description',
                textcolor: 'Text color',
                colorpreview: 'Color preview',
                addvalue: 'Add value',
                font: 'Font'
            },
            type: {
                form: {
                    fieldsets: {
                        generalData: {
                            inputs: {
                                active: {
                                    label: 'Active' // mapped
                                },
                                name: {
                                    label: 'Name' // mapped
                                },
                                parent: {
                                    label: 'Parent' // mapped
                                }
                            }
                        }
                    },
                    tooltips: {},
                    values: {
                        active: 'Active' // mapped
                    }
                },
                title: 'Properties',
                toolbar: {
                    cancelBtn: 'Cancel', // mapped
                    closeBtn: 'Close', // mapped
                    deleteBtn: {
                        tooltip: 'Delete' // mapped
                    },
                    editBtn: {
                        tooltip: 'Edit' // mapped
                    },
                    saveBtn: 'Save' // mapped
                }
            }
        },
        menus: {
            singular: 'Menu', // no map
            plural: 'Menus',
            emptytexts: {

            },
            fieldlabels: {
                newfolder: 'New folder'
            },
            strings: {
                emptyfoldername: 'Folder name empty',
                areyousuredeleteitem: 'Are you sure you want to delete this menu?',
                delete: 'Delete Menu'
            },
            texts: {
                add: 'Add menu'
            },
            titles: {


            },
            tooltips: {
                remove: 'Remove',
                addfolder: 'Add folder'
            }
        },
        navigation: {
            bim: 'BIM', // mapped
            classes: 'Classes', // mapped
            custompages: 'Custom pages', // mapped
            customcomponents: 'Custom components',
            dashboards: 'Dashboards', // no map
            dms: 'DMS', // mapped
            domains: 'Domains', // mapped
            email: 'Email', // mapped
            generaloptions: 'General options', // mapped
            gis: 'GIS', // mapped
            gisnavigation: 'Gis Navigation', // mapped
            groupsandpermissions: 'Groups and permissions', // no map
            importexports: 'Imports/Exports',
            layers: 'Layers',
            languages: 'Localizations', // mapped
            lookuptypes: 'Lookup types', // mapped
            menus: 'Menus', // no map
            multitenant: 'Multitenant', // no map
            navigationtrees: 'Navigation trees', // mapped
            processes: 'Processes', // mapped
            reports: 'Reports', // no map
            searchfilters: 'Search filters', // mapped
            servermanagement: 'Server management',
            simples: 'Simples', // no map
            standard: 'Standard', // mapped
            systemconfig: 'System config', // no map
            taskmanager: 'Task manager', // no map
            title: 'Navigation', // mapped
            users: 'Users', // mapped
            views: 'Views', // mapped
            workflow: 'Workflow' // no map
        },
        searchfilters: {
            fieldlabels: {
                filters: 'Filters',
                targetclass: 'Target class'
            },
            texts: {
                addfilter: 'Add filter',
                defaultforgroup: 'Default for groups',
                fromfilter: 'From filter',
                fromsql: 'From SQL',
                writefulltextquery: 'Write your full text query',
                fulltextquery: 'Full text query',
                fulltext: 'Full text',
                chooseafunction: 'Choose a function'
            }
        },
        processes: {
            texts: {
                process: 'process',
                processdeactivated: 'Process correctly deactivated.',
                processactivated: 'Process correctly activated.'
            },
            strings: {
                createnewcontextaction: 'Create new context action',
                processattachments: 'Process Attachments',
                engine: 'Engine',
                xpdlfile: 'XPDL file',
                selectxpdlfile: 'Select an XPDL file',
                template: 'Template'
            },
            fieldlabels: {
                applicability: 'Applicability',
                enginetype: 'Engine type'
            },
            properties: {
                form: {
                    fieldsets: {
                        defaultOrders: 'Default Orders', // no map
                        generalData: {
                            inputs: {
                                active: {
                                    label: 'Active' // mapped
                                },
                                description: {
                                    label: 'Description' // mapped
                                },
                                enableSaveButton: {
                                    label: 'Hide "Save" button' // no map
                                },
                                name: {
                                    label: 'Name' // mapped
                                },
                                parent: {
                                    label: 'Inherits from' // mapped
                                },
                                stoppableByUser: {
                                    label: 'Stoppable by User' // mapped
                                },
                                superclass: {
                                    label: 'Superclass' // mapped
                                }
                            }
                        },
                        contextMenus: {
                            actions: {
                                delete: {
                                    tooltip: 'Delete' // mapped
                                },
                                edit: {
                                    tooltip: 'Edit' // mapped
                                },
                                moveDown: {
                                    tooltip: 'Move Down' // no map
                                },
                                moveUp: {
                                    tooltip: 'Move Up' // no map
                                }
                            },
                            inputs: {
                                applicability: {
                                    label: 'Applicability', // no map
                                    values: {
                                        all: {
                                            label: 'All' // mapping
                                        },
                                        many: {
                                            label: 'Current and selected' // no map
                                        },
                                        one: {
                                            label: 'Current' // mapping
                                        }
                                    }
                                },
                                javascriptScript: {
                                    label: 'Javascript script / custom GUI Paramenters' // no map
                                },
                                menuItemName: {
                                    label: 'Menu item name', // no map
                                    values: {
                                        separator: {
                                            label: '[---------]' // no map
                                        }
                                    }
                                },
                                status: {
                                    label: 'Status', // mapped
                                    values: {
                                        active: {
                                            label: 'Active' // mapped
                                        }
                                    }
                                },
                                typeOrGuiCustom: {
                                    label: 'Type / GUI custom', // no map
                                    values: {
                                        component: {
                                            label: 'Custom GUI' // no map
                                        },
                                        custom: {
                                            label: 'Script Javascript' // no map
                                        },
                                        separator: {
                                            label: ''
                                        }
                                    }
                                }
                            },
                            title: 'Context Menus' // no map
                        },
                        icon: 'Icon',
                        processParameter: {
                            inputs: {
                                defaultFilter: {
                                    label: 'Default filter' // no map
                                },
                                messageAttr: {
                                    label: 'Message attribute' // no map
                                },
                                flowStatusAttr: {
                                    label: 'State attribute' // no map
                                }
                            },
                            title: 'Process parameters' // no map
                        },
                        validation: {
                            inputs: {
                                validationRule: {
                                    label: 'Validation Rule' // no map
                                }
                            },
                            title: 'Validation' // no map
                        }
                    },
                    inputs: {
                        status: 'Status' // mapped
                    },
                    tooltips: {},
                    values: {
                        active: 'Active' // mapped
                    }
                },
                title: 'Properties',
                toolbar: {
                    cancelBtn: 'Cancel', // mapped
                    closeBtn: 'Close', // mapped
                    deleteBtn: {
                        tooltip: 'Delete' // mapped
                    },
                    disableBtn: {
                        tooltip: 'Disable' // mapped
                    },
                    editBtn: {
                        tooltip: 'Edit' // mapped
                    },
                    enableBtn: {
                        tooltip: 'Enable' // mapped
                    },
                    saveBtn: 'Save', // mapped
                    versionBtn: {
                        tooltip: 'Version' // mapped
                    }
                }
            },
            title: 'Processes',
            toolbar: {
                addProcessBtn: {
                    text: 'Add process' // mapped
                },
                printSchemaBtn: {
                    text: 'Print schema' // mapped
                },
                processLabel: 'Process', // mapped
                searchTextInput: {
                    emptyText: 'Search all processes' // no map
                }
            }
        },
        viewfilters: {
            texts: {
                filterforgroup: 'Filters for groups',
                addfilter: 'Add filter'
            },
            emptytexts: {
                searchingrid: 'Search ...'
            }

        },
        navigationtrees: {
            singular: 'Navigation tree',
            plural: 'Navigation trees',
            emptytexts: {
                searchingrid: 'Search in grid...',
                searchnavigationtree: 'Search navigation tree...'
            },
            fieldlabels: {
                active: 'Active', // mapped
                actions: 'Actions', // mapped
                name: 'Name',
                description: 'Description',
                source: 'Source'
            },
            strings: {
                sourceclass: 'Source class'
            },
            texts: {
                addnavigationtree: 'Add tree'
            },

            tooltips: {
                delete: 'Delete navigation tree',
                disable: 'Disable navigation tree',
                enable: 'Enable navigation tree',
                edit: 'Edit navigation tree'
            }
        },
        reports: {
            singular: 'Report',
            plural: 'Report',
            emptytexts: {
                searchingrid: 'Search in grid...',
                searchreports: 'Search reports...'
            },
            fieldlabels: {
                active: 'Active', // mapped
                actions: 'Actions', // mapped
                name: 'Name',
                description: 'Description',
                zipfile: 'ZIP file'
            },
            strings: {

            },
            texts: {
                addreport: 'Add report',
                selectfile: 'Select ZIP file'
            },
            titles: {
                file: 'File'
            },
            tooltips: {
                delete: 'Delete report',
                disable: 'Disable report',
                enable: 'Enable report',
                edit: 'Edit report',
                viewsql: 'View report sql',
                downloadpackage: 'Download report package'
            }
        },
        custompages: {
            singular: 'Custom page',
            plural: 'Custom pages',
            emptytexts: {
                searchingrid: 'Search in grid...',
                searchcustompages: 'Search custom pages...'
            },
            fieldlabels: {
                active: 'Active', // mapped
                actions: 'Actions', // mapped
                name: 'Name',
                description: 'Description',
                zipfile: 'ZIP file',
                componentid: 'Component ID'
            },
            strings: {

            },
            texts: {
                addcustompage: 'Add custom page',
                selectfile: 'Select ZIP file'
            },
            titles: {
                file: 'File'
            },
            tooltips: {
                delete: 'Delete custom page',
                disable: 'Disable custom page',
                enable: 'Enable custom page',
                edit: 'Edit custom page',
                downloadpackage: 'Download custom page package'
            }
        },
        customcomponents: {
            singular: 'Custom component',
            plural: 'Custom components',
            emptytexts: {
                searchingrid: 'Search in grid...',
                searchcustompages: 'Search custom components...'
            },
            fieldlabels: {
                active: 'Active', // mapped
                actions: 'Actions', // mapped
                name: 'Name',
                description: 'Description',
                zipfile: 'ZIP file',
                componentid: 'Component ID'
            },
            strings: {
                addcontextmenu: 'Add context menu',
                searchcontextmenus: 'Search context menus...',
                contextmenu: 'Context menu'
            },
            texts: {
                addcustomcomponent: 'Add custom component',
                selectfile: 'Select ZIP file'
            },
            titles: {
                file: 'File'
            },
            tooltips: {
                delete: 'Delete custom component',
                disable: 'Disable custom component',
                enable: 'Enable custom component',
                edit: 'Edit custom component',
                downloadpackage: 'Download custom component package'
            }
        },
        title: 'Administration', // no map
        users: {
            fieldLabels: {
                user: 'User',
                language: 'Language',
                initialpage: 'Initial page',
                service: 'Service',
                privileged: 'Privileged',
                confirmpassword: 'Confirm Password',
                groups: 'Groups',
                defaultgroup: 'Default group',
                multigroup: 'Multigroup',
                defaulttenant: 'Default tenant',
                multitenant: 'Multitenant',
                tenants: 'Tenants',
                tenant: 'Tenant',
                nodata: 'No data',
                multitenantactivationprivileges: 'Allow multitenant'
            },
            properties: {
                form: {
                    fieldsets: {
                        generalData: {
                            inputs: {
                                active: {
                                    label: 'Active' // mapped
                                },
                                description: {
                                    label: 'Description' // mapped
                                },
                                name: {
                                    label: 'Name' // mapped
                                },
                                stoppableByUser: {
                                    label: 'User stoppable' // mapped
                                }
                            }
                        }
                    }
                }
            },
            title: 'Users', // mapped
            toolbar: {
                addUserBtn: {
                    text: 'Add user' // mapped
                },
                searchTextInput: {
                    emptyText: 'Search all users' // no map
                }
            }
        }
    }
});
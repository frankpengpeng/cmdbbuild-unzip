Ext.define('CMDBuildUI.locales.Locales', {
    requires: ['CMDBuildUI.locales.LocalesAdministration'],
    singleton: true,
    localization: 'en',
    administration: CMDBuildUI.locales.LocalesAdministration.administration,
    common: {
        actions: {
            add: 'Add',
            apply: 'Apply',
            cancel: 'Cancel',
            close: 'Close',
            delete: 'Delete',
            edit: 'Edit',
            execute: 'Execute',
            refresh: 'Refresh data',
            remove: 'Remove',
            save: 'Save',
            saveandapply: 'Save and apply',
            saveandclose: 'Save and close',
            search: 'Search',
            searchtext: 'Search...'
        },
        attributes: {
            nogroup: 'Base data'
        },
        editor: {
            clearhtml: 'Clear HTML'
        },
        dates: {
            // Reffer to https://docs.sencha.com/extjs/6.2.0/classic/Ext.Date.html
            date: 'd/m/Y',
            datetime: 'd/m/Y H:i:s',
            time: 'H:i:s'
        },
        grid: {
            opencontextualmenu: 'Open contextual menu',
            disablemultiselection: 'Disable multi selection',
            enamblemultiselection: 'Enable multi selection',
            filterremoved: 'The current filter has been removed',
            itemnotfound: 'Item not found',
            subtype: 'Subtype',
            list: 'List',
            row: 'Item',
            rows: 'Items',
            print: 'Print',
            printcsv: 'Print as CSV',
            printpdf: 'Print as PDF',
            printodt: 'Print as ODT',
            import: 'Import data',
            export: 'Export data'
        },
        tabs: {
            activity: 'Activity',
            attachments: 'Attachments',
            card: 'Card',
            details: 'Details',
            emails: 'Emails',
            history: 'History',
            notes: 'Notes',
            relations: 'Relations'
        }
    },
    main: {
        administrationmodule: 'Administration module',
        baseconfiguration: 'Base configuration',
        changegroup: 'Change group',
        changetenant: 'Change tenant',
        confirmchangegroup: 'Are you sure you want to change the group?',
        confirmchangetenants: 'Are you sure you want to change active tenants?',
        confirmdisabletenant: 'Are you sure you want to disable "Ignore tenants" flag?',
        confirmenabletenant: 'Are you sure you want to enable "Ignore tenants" flag?',
        ignoretenants: 'Ignore tenants',
        logout: 'Logout',
        managementmodule: 'Data management module',
        multitenant: 'Multi tenant',
        multigroup: 'Multi group',
        navigation: 'Navigation',
        searchinallitems: 'Search in all items',
        userpreferences: 'Preferences',
        changepassword: 'Change password',
        oldpassword: 'Old password',
        newpassword: 'New password',
        pagenotfound: 'Page not found',
        pleasecorrecterrors: 'Please correct indicated errors!',
        confirmpassword: 'Confirm password',
        info: 'Info',
        cardlock: {
            lockedmessage: 'You can\'t edit this card because {0} is editing it.',
            someone: 'someone'
        },
        logo: {
            cmdbuild: 'CMDBuild logo',
            cmdbuildready2use: 'CMDBuild READY2USE logo',
            openmaint: 'openMAINT logo',
            companylogo: 'Company logo'
        },
        preferences: {
            defaultvalue: 'Default value',
            timezone: 'Timezone',
            labellanguage: 'Language',
            labeldateformat: 'Date format',
            labeltimeformat: 'Time format',
            labeldecimalsseparator: 'Decimals separator',
            labelthousandsseparator: 'Thousands separator',
            decimalserror: 'Decimals field must be present',
            thousandserror: 'Thousands field must be present',
            decimalstousandserror: 'Decimals and Thousands separator must be differents',
            comma: 'Comma',
            period: 'Period',
            space: 'Space',
            twentyfourhourformat: '24-hour format',
            twelvehourformat: '12-hour format',
            preferredofficesuite: 'Preferred Office suite',
            default: 'Default',
            msoffice: 'Microsoft Office'
        }
    },
    login: {
        title: 'Login',
        fields: {
            username: 'Username',
            password: 'Password',
            language: 'Language',
            group: 'Group',
            tenants: 'Tenants'
        },
        welcome: 'Welcome back {0}.',
        loggedin: 'Logged in',
        buttons: {
            login: 'Login',
            logout: 'Change user'
        }
    },
    notifier: {
        attention: 'Attention',
        error: 'Error',
        info: 'Info',
        success: 'Success',
        warning: 'Warning',
        genericerror: 'Generic error',
        genericwarning: 'Generic warning',
        genericinfo: 'Generic info'
    },
    menu: {
        allitems: 'All items',
        classes: 'Classes',
        processes: 'Processes',
        reports: 'Reports',
        dashboards: 'Dashboards',
        views: 'Views',
        custompages: 'Custom pages'
    },
    classes: {
        cards: {
            label: 'Cards',
            addcard: 'Add card',
            clone: 'Clone',
            clonewithrelations: 'Clone card and relations',
            deletecard: 'Delete card',
            deleteconfirmation: 'Are you sure you want to delete this card?',
            modifycard: 'Modify card',
            opencard: 'Open card',
            print: 'Print card'
        },
        simple: 'Simple',
        standard: 'Standard'
    },
    processes: {
        abortprocess: 'Abort process',
        abortconfirmation: 'Are you sure you want to abort this process?',
        action: {
            advance: 'Advance',
            label: 'Action'
        },
        allstatuses: 'All',
        editactivity: 'Modify activity',
        activeprocesses: 'Active processes',
        openactivity: 'Open activity',
        startworkflow: 'Start',
        workflow: 'Workflow'
    },
    notes: {
        edit: 'Modify notes'
    },
    relations: {
        adddetail: 'Add detail',
        addrelations: 'Add relations',
        attributes: 'Attributes',
        code: 'Code',
        deletedetail: 'Delete detail',
        deleterelation: 'Delete relation',
        description: 'Description',
        editcard: 'Edit card',
        editdetail: 'Edit detail',
        editrelation: 'Edit relation',
        mditems: 'items',
        opencard: 'Open related card',
        opendetail: 'Show detail',
        type: 'Type'
    },
    history: {
        begindate: 'Begin date',
        enddate: 'End date',
        user: 'User',
        activityname: 'Activity name',
        activityperformer: 'Activity performer',
        processstatus: 'Status'
    },
    attachments: {
        add: 'Add attachment',
        author: 'Author',
        category: 'Category',
        creationdate: 'Creation date',
        deleteattachment: 'Delete attachment',
        deleteattachment_confirmation: 'Are you sure you want to delete this attachment?',
        description: 'Description',
        download: 'Download',
        editattachment: 'Modify attachment',
        file: 'File',
        filename: 'File name',
        majorversion: 'Major version',
        modificationdate: 'Modification date',
        uploadfile: 'Upload file...',
        version: 'Version',
        viewhistory: 'View attachment history',
        attachmenthistory: 'Attachment History'
    },
    emails: {
        keepsynchronization: 'Keep synchronization',
        delay: 'Delay',
        dmspaneltitle: 'Choose attachments from Database',
        from: 'From',
        to: 'To',
        cc: 'Cc',
        bcc: 'Bcc',
        subject: 'Subject',
        message: 'Message',
        attachfile: 'Attach file',
        selectaclass: 'Select a class',
        addattachmentsfromdms: 'Add attachments from DMS',
        alredyexistfile: 'Already exists a file with this name',
        composefromtemplate: 'Compose from template',
        composeemail: 'Compose e-mail',
        regenerateallemails: 'Regenerate all e-mails',
        gridrefresh: 'Grid refresh',
        archivingdate: 'Archiving date',
        sendemail: 'Send e-mail',
        reply: 'reply',
        regenerateemail: 'Regenerate e-mail',
        remove: 'Remove',
        edit: 'Edit',
        view: 'View',
        replyprefix: 'On {0}, {1} wrote:',
        remove_confirmation: 'Are you sure you want to delete this email?',
        delays: {
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
        statuses: {
            draft: 'Draft',
            outgoing: 'Outgoing',
            received: 'Received',
            sent: 'Sent'

        }
    },
    widgets: {
        linkcards: {
            togglefilterenabled: 'Enable grid filter',
            togglefilterdisabled: 'Disable grid filter',
            refreshselection: 'Apply default selection',
            opencard: 'Open card',
            editcard: 'Edit card',
            errors: {

            }
        },
        customform: {
            addrow: 'Add row',
            clonerow: 'Clone row',
            deleterow: 'Delete row',
            editrow: 'Edit row',
            export: 'Export',
            import: 'Import',
            refresh: 'Refresh to defaults'
        }
    },
    filters: {
        addfilter: 'Add filter',
        any: 'Any',
        attributes: 'Attributes',
        attribute: 'Choose an attribute',
        clearfilter: 'Clear filter',
        copyof: 'Copy of',
        description: 'Description',
        filterdata: 'Filter data',
        fromselection: 'From selection',
        name: 'Name',
        newfilter: 'New filter',
        noone: 'No one',
        operator: 'Operator',
        relations: 'Relations',
        type: 'Type',
        typeinput: 'Input Parameter',
        value: 'Value',
        actions: 'Actions',
        ignore: 'Ignore',
        migrate: 'Migrates',
        clone: 'Clone',
        domain: 'Domain',
        operators: {
            beginswith: 'Begins with',
            between: 'Between',
            contained: 'Contained',
            containedorequal: 'Contained or equal',
            contains: 'Contains',
            containsorequal: 'Contains or equal',
            different: 'Different',
            doesnotbeginwith: 'Does not begin with',
            doesnotcontain: 'Does not contain',
            doesnotendwith: 'Does not end with',
            endswith: 'Ends with',
            equals: 'Equals',
            greaterthan: 'Greater than',
            isnotnull: 'Is not null',
            isnull: 'Is null',
            lessthan: 'Less than'
        }
    },
    gis: {
        tree: 'Tree',
        list: 'List',
        card: 'Card',
        layers: 'Layers',
        geographicalAttributes: 'Geo Attributes',
        root: 'Root',
        externalServices: 'External services',
        geoserverLayers: 'Geoserver layers',
        mapServices: 'Map Services',
        view: 'View',
        zoom: 'Zoom',
        position: 'Position',
        map: 'Map',
        cardsMenu: 'Cards Menu'

    },
    thematism: {
        thematism: 'Thematisms',
        graduated: 'Graduated',
        punctual: 'Punctual',
        intervals: 'Intervals',
        analysisType: 'Analysis Type',
        legend: 'Legend',
        highlightSelected: 'Highlight selected item',
        source: 'Source',
        table: 'Table',
        function: 'Function',
        attribute: 'Attribute',
        geoAttribute: 'geoAttribute',
        value: 'Value',
        quantity: 'Count',
        color: 'Color',
        generate: 'Generate',
        addThematism: 'Add Thematism',
        newThematism: 'New Thematism',
        clearThematism: 'Clear Thematism',
        calculateRules: 'Generate style rules',
        defineThematism: 'Thematism definition',
        defineLegend: 'Legend definition',
        name: 'name'
    },
    patches: {
        apply: 'Apply patches',
        patches: 'Patches',
        name: 'Name',
        description: 'Description',
        category: 'Category'
    },
    relationGraph: {
        openRelationGraph: 'Open Relation Graph',
        relationGraph: 'Relation Graph',
        card: 'Card',
        cardRelations: 'Card Relations',
        cardList: 'Card List',
        classList: 'Class List',
        relation: 'relation',
        level: 'Level',
        refresh: 'Refresh',
        class: 'Class',
        activity:'activity',
        qt: 'Qt',
        reopengraph: 'Reopen the graph from this node',
        choosenaviagationtree: 'Choose navigation tree',
        enableTooltips: 'Enable/disable tooltips on graph',
        compoundnode: 'Compound Node'

    },
    bim: {
        showBimCard: 'Open 3D viewer',
        bimViewer: 'Bim Viewer',
        tree: {
            label: 'Tree',
            columnLabel: 'Tree',
            arrowTooltip: 'Select element',
            open_card: "Open related card",
            root: 'Ifc Root'
        },
        layers: {
            label: 'Layers',
            name: 'Name',
            visibility: 'Visibility',
            qt: 'Qt',
            menu: {
                showAll: 'Show All',
                hideAll: 'Hide All'
            }
        },
        card: {
            label: 'Card'
        },
        menu: {
            camera: 'Camera',
            resetView: 'Reset View',
            frontView: 'Front View',
            sideView: 'Side View',
            topView: 'Top View',
            mod: 'Viewer controls',
            rotate: 'rotate',
            pan: 'Scroll',
            perspective: 'Perspective Camera',
            orthographic: 'Orthographic Camera'
        }
    },
    errors: {
        classnotfound: 'Class {0} not found',
        notfound: 'Item not found',
        autherror: 'Wrong username or password'
    },
    reports: {
        download: 'Download',
        format: 'Format',
        csv: 'CSV',
        odt: 'ODT',
        pdf: 'PDF',
        rtf: 'RTF',
        print: 'Print',
        reload: 'Reload'
    },
    importexport: {
        import: 'Import',
        export: 'Export',
        template: 'Template',
        importresponse: 'Import response',
        downloadreport: 'Download report',
        sendreport: 'Send report',
        emailsubject: 'Import data report',
        emailsuccess: 'The email has been sent successfully!',
        emailfailure: 'Error occurred while sending email!',
        templatedefinition: 'Template definition',
        response: {
            processed: 'Processed rows',
            created: 'Created items',
            modified: 'Modified items',
            deleted: 'Deleted items',
            unmodified: 'Unmodified items',
            errors: 'Errors',
            recordnumber: 'Record number',
            linenumber: 'Line number',
            message: 'Message'
        }
    }
});
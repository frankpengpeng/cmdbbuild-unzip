Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.FieldsetsHelper', {
    mixinId: 'administration-task-formmixin',
    mixins: [
        'CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin',
        'CMDBuildUI.view.administration.content.tasks.card.helpers.ImportExportMixin',
        'CMDBuildUI.view.administration.content.tasks.card.helpers.EmailServiceMixin',
        'CMDBuildUI.view.administration.content.tasks.card.helpers.SatrtWorkflowMixin'
    ],
    requires: ['CMDBuildUI.util.administration.helper.FormHelper'],

    getGeneralPropertyPanel: function (theVmObject, step, data) {
        var items = [];
        switch (data.type || data[theVmObject].get('type')) {
            /**
            * Name: stringa con regole di validazione standard per i Name. Obbligatorio. Immutabile.
            * Description: stringa traducibile. Obbligatorio.
            * Type: combo con i valori Import, Export. Obbligatorio. Immutabile.
            * Template: combo con l’elenco dei template. Obbligatorio.
            **/
            case 'import_export':
            case 'import_file':
            case 'export_file':
                items = this.importexport.getGeneralPropertyPanel(theVmObject, step, data, this);
                break;
            case 'emailService':
                items = this.emailservice.getGeneralPropertyPanel(theVmObject, step, data, this);
                break;
            case 'workflow':
                items = this.startworkflow.getGeneralPropertyPanel(theVmObject, step, data, this);
                break;


            default:
                break;
        }
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isGeneralPropertyPanelDisabled}'
            },
            items: items
        };

    },

    getSettingsPanel: function (theVmObject, step, data) {
        var items = [];
        switch (data.type || data[theVmObject].get('type')) {
            case 'import_export':
            case 'import_file':
            case 'export_file':
                items = this.importexport.getSettingsPanel(theVmObject, step, data, this);

                break;
            case 'emailService':
                items = this.emailservice.getSettingsPanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }


        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.tasks.settings,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.tasks.settings'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isSettingPanelDisabled}'
            },
            items: items
        };
    },
    getCronPanel: function (theVmObject, step, data) {
        var items = [];
        switch (data.type || data[theVmObject].get('type')) {
            case 'import_export':
            case 'import_file':
            case 'export_file':
                items = this.importexport.getCronPanel(theVmObject, step, data, this);
                break;

            case 'emailService':
                items = this.emailservice.getCronPanel(theVmObject, step, data, this);
                break;
            case 'workflow':
                items = this.startworkflow.getCronPanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.tasks.cron,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.tasks.cron'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isGeneralCronPanelDisabled}'
            },
            items: items
        };
    },

    getNotificationPanel: function (theVmObject, step, data) {
        var items = [];

        switch (data.type || data[theVmObject].get('type')) {
            case 'import_export':
            case 'export_file':
            case 'import_file':
                items = this.importexport.getNotificationPanel(theVmObject, step, data, this);
                break;
            case 'emailService':
                items = this.emailservice.getNotificationPanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.tasks.notifications,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.tasks.notifications'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isGeneralCronPanelDisabled}'
            },
            items: items
        };
    },
    getParsePanel: function (theVmObject, step, data) {
        var items = [];

        switch (data.type || data[theVmObject].get('type')) {
            case 'emailService':
                items = this.emailservice.getParsePanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.tasks.parsing,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.tasks.parsing'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isGeneralCronPanelDisabled}'
            },
            items: items
        };
    },
    getProcessPanel: function (theVmObject, step, data) {
        var items = [];

        switch (data.type || data[theVmObject].get('type')) {
            case 'emailService':
                items = this.emailservice.getProcessPanel(theVmObject, step, data, this);
                break;
            default:
                break;
        }
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: "fieldset",

            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.localizations.process,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.localizations.process'
            },
            layout: {
                type: 'column'
            },
            bind: {
                hidden: '{isGeneralCronPanelDisabled}'
            },
            items: items
        };
    }


});
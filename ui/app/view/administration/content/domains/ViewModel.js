Ext.define('CMDBuildUI.view.administration.content.domains.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-domains-view',
    data: {
        actions: {
            view: true,
            edit: false,
            add: false
        },
        toolbarHiddenButtons: {
            edit: true, // action !== view
            print: true, // action !== view
            disable: true,
            enable: true,
            delete: true
        },
        theDomainDescription: null,
        theDomainDescriptionTranslation: null,
        theDirectDescriptionTranslation: null,
        theInverseDescriptionTranslation: null,
        theMasterDetailTranslation: null
    },

    formulas: {
        domainLabel: {
            bind: '{theDomain.description}',
            get: function(){
                return CMDBuildUI.locales.Locales.administration.domains.singularTitle;
            }
        },
        action: {
            get: function () {
		        this.set('activeTab', this.getView().up('administration-content').getViewModel().get('activeTabs.domains')|| 0);
                if (this.get('actions.edit')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (this.get('actions.add')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },
        getToolbarButtons: {
            bind: '{theDomain.active}',
            get: function (get) {
                this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.print', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.disable', true);
                this.set('toolbarHiddenButtons.enable', false);
            }
        },
        updateToolbarButtons: {
            bind: '{theDomain.active}',
            get: function (data) {
                if (data) {
                    this.set('toolbarHiddenButtons.disable', false);
                    this.set('toolbarHiddenButtons.enable', true);
                } else {
                    this.set('toolbarHiddenButtons.disable', true);
                    this.set('toolbarHiddenButtons.enable', false);
                }
            }
        },
        theDomainDestination: {
            bind: {
                domain: '{theDomain}'
            },
            get: function (data) {
                var storeId = data.domain.destinationProcess ? 'processes.Processes' :'classes.Classes'; 
                var record = Ext.getStore(storeId).getById(data.domain.get('destination'));
                return record && record.get('description');

            }
        },
        theDomainSource: {
            bind: {
                domain: '{theDomain}'
            },
            get: function (data) {
                var storeId = data.domain.destinationProcess ? 'processes.Processes' :'classes.Classes'; 
                var record = Ext.getStore(storeId).getById(data.domain.get('source'));
                return record && record.get('description');

            }
        }
    }
});
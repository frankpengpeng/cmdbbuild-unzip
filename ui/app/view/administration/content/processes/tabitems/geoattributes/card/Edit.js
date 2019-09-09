Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.Edit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.EditController',
        'CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.EditModel'
    ],
    alias: 'widget.administration-content-processes-tabitems-geoattributes-card-edit',
    controller: 'administration-content-processes-tabitems-geoattributes-card-edit',
    viewModel: {
        type: 'administration-content-processes-tabitems-geoattributes-card-edit'
    },

    config: {
        theGeoAttribute: null
    },
    autoScroll: 'y',
    items: [{
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        items: [{
            xtype: 'administration-processes-tabitems-geoattributes-card-fieldscontainers-generalproperties',
            
            viewModel: {
                data: {
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                }
            }
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.geoattributes.strings.specificproperty,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.geoattributes.strings.specificproperty'
        },
        items: [{
            xtype: 'administration-processes-tabitems-geoattributes-card-fieldscontainers-specificproperties',
            
            viewModel: {
                data: {
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                }
            }
        }]
    }],
    buttons: [{
        text: CMDBuildUI.locales.Locales.administration.attributes.texts.save,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.save'
        },
        ui: 'administration-action-small',
        formBind: true, //only enabled once the form is valid
        disabled: true,
        listeners: {
            click: 'onSaveBtnClick'
        }
    }, {
        text: CMDBuildUI.locales.Locales.administration.attributes.texts.cancel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.attributes.texts.cancel'
        },
        ui: 'administration-secondary-action-small',
        listeners: {
            click: 'onCancelBtnClick'
        }
    }],
    initComponent: function () {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var vm = this.getViewModel();
        vm.getParent().set('title', vm.get('grid').lookupViewModel().get('objectTypeName') + ' - '+ 'geo attribute'+ ' - ' + vm.get('theGeoAttribute.name') );
        this.callParent(arguments);
    },
    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    }
});
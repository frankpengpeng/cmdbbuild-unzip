
Ext.define('CMDBuildUI.view.classes.cards.card.Edit', {
    extend: 'Ext.form.Panel',
    alias: 'widget.classes-cards-card-edit',

    requires: [
        'CMDBuildUI.view.classes.cards.card.EditController',
        'CMDBuildUI.view.classes.cards.card.EditModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],

    mixins: [
        'CMDBuildUI.view.classes.cards.card.Mixin'
    ],

    controller: 'classes-cards-card-edit',
    viewModel: {
        type: 'classes-cards-card-edit'
    },

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.update,

    modelValidation: true,

    fieldDefaults: {
        labelAlign: 'top'
    },

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        ui: 'management-action-small',
        itemId: 'savebtn',
        autoEl: {
            'data-testid': 'card-edit-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.saveandclose,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        ui: 'management-action-small',
        itemId: 'saveandclosebtn',
        autoEl: {
            'data-testid': 'card-edit-saveandclose'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.saveandclose'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        ui: 'secondary-action-small',
        itemId: 'cancelbtn',
        autoEl: {
            'data-testid': 'card-edit-cancel'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }]
});

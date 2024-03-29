
Ext.define('CMDBuildUI.view.fields.reference.SelectionPopup', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.fields.reference.SelectionPopupController',
        'CMDBuildUI.view.fields.reference.SelectionPopupModel'
    ],

    mixins: [
        'CMDBuildUI.mixins.grids.AddButtonMixin'
    ],

    alias: 'widget.fields-reference-selectionpopup',
    controller: 'fields-reference-selectionpopup',
    viewModel: {
        type: 'fields-reference-selectionpopup'
    },

    bind: {
        store: '{records}',
        selection: '{selection}'
    },

    selModel: {
        type: 'checkboxmodel',
        mode: 'SINGLE'
    },

    config: {
        defaultSearchFilter: ""
    },

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.classes.cards.addcard,
        reference: 'addcardbtn',
        itemId: 'addcardbtn',
        iconCls: 'x-fa fa-plus',
        ui: 'management-action',
        autoEl: {
            'data-testid': 'selection-popup-addcardbtn'
        },
        bind: {
            text: '{addbtn.text}',
            disabled: '{addbtn.disabled}',
            hidden: '{addbtn.hidden}'
        }
    }, {
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText: CMDBuildUI.locales.Locales.classes.cards.searchtext,
        reference: 'searchtextinput',
        itemId: 'searchtextinput',
        cls: 'management-input',
        localized: {
            text: 'CMDBuildUI.locales.Locales.classes.cards.searchtext'
        },
        autoEl: {
            'data-testid': 'selection-popup-searchtextinput'
        },
        bind: {
            value: '{searchvalue}'
        },
        triggers: {
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                handler: 'onSearchSubmit'
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear'
            }
        }
    }, {
        xtype: 'tbfill'
    }, CMDBuildUI.util.helper.GridHelper.getBufferedGridCounterConfig("records")],

    fbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.common.actions.save,
        itemId: 'savebtn',
        reference: 'savebtn',
        ui: 'management-action-small',
        autoEl: {
            'data-testid': 'selection-popup-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        bind: {
            disabled: '{saveBtnDisabled}'
        }
    }],

    layout: 'fit',
    forceFit: true,

    typeicon: CMDBuildUI.model.menu.MenuItem.icons.klass
});

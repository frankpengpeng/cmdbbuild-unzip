Ext.define('CMDBuildUI.view.relations.list.add.GridContainer', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.relations.list.add.GridContainerController',
        'CMDBuildUI.view.relations.list.add.GridContainerModel'
    ],

    alias: 'widget.relations-list-add-gridcontainer',
    controller: 'relations-list-add-gridcontainer',
    viewModel: {
        type: 'relations-list-add-gridcontainer'
    },
    
    layout: 'border',

    config: {
        originTypeName: null,
        originId: null,
        mode :null, // create|edit
        multiSelect: false
    },

    fbar: [{
        text: CMDBuildUI.locales.Locales.common.actions.save,
        disabled: true,
        reference: 'savebutton',
        itemId: 'savebutton',
        ui: 'management-action',
        autoEl: {
            'data-testid': 'relations-list-add-gridcontainer-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        bind: {
            disabled: '{disableSaveButton}'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        reference: 'cancelbutton',
        ui: 'secondary-action',
        itemId: 'cancelbutton',
        autoEl: {
            'data-testid': 'relations-list-add-gridcontainer-cancel'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }],

    items: [{
        xtype: 'panel',
        region: 'center',
        layout: 'card',
        flex: 1,
        reference: 'gridcontainer'
    }, {
        xtype: 'form',
        flex: 0.4,
        layout: 'column',
        region: 'south',
        resizable: true,
        reference: 'attributesform',
        hidden: true,
        fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
        defaults: {
            xtype: 'fieldcontainer',
            columnWidth: 0.5,
            flex: '0.5',
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
            layout: 'anchor'
        }
    }],

    onSaveSuccess: Ext.emptyFn
});

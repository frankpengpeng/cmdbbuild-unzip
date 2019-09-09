
Ext.define('CMDBuildUI.view.filters.Panel',{
    extend: 'Ext.tab.Panel',

    requires: [
        'CMDBuildUI.view.filters.PanelController',
        'CMDBuildUI.view.filters.PanelModel'
    ],

    alias: 'widget.filters-panel',
    controller: 'filters-panel',
    viewModel: {
        type: 'filters-panel'
    },

    config: {
        /**
         * @cfg {Boolean} showAttributesPanel
         */
        showAttributesPanel: true,

        /**
         * @cfg {Boolean} allowInputParameterForAttributes
         */
        allowInputParameterForAttributes: true,

        /**
         * @cfg {Boolean} showRelationsPanel
         */
        showRelationsPanel: true
    },

    ui: 'managementlighttabpanel',

    fbar: [{
        xtype: 'button',
        reference: 'applybutton',
        itemId: 'applybutton',
        text: CMDBuildUI.locales.Locales.common.actions.apply,
        ui: 'management-action-small',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.apply'
        },
        autoEl: {
            'data-testid': 'filters-panel-applybutton'
        }
    }, {
        xtype: 'button',
        reference: 'savebutton',
        itemId: 'savebutton',
        text: CMDBuildUI.locales.Locales.common.actions.saveandapply,
        ui: 'management-action-small',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.saveandapply'
        },
        autoEl: {
            'data-testid': 'filters-panel-savebutton'
        }
    }, {
        xtype: 'button',
        reference: 'cancelbutton',
        itemId: 'cancelbutton',
        ui: 'secondary-action-small',
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        autoEl: {
            'data-testid': 'filters-panel-cancelbutton'
        }
    }]
});

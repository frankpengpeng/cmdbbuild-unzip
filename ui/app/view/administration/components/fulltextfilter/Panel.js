Ext.define('CMDBuildUI.view.administration.components.fulltextfilters.Panel', {
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.components.fulltextfilters.PanelController',
        'CMDBuildUI.view.administration.components.fulltextfilters.PanelModel'
    ],

    alias: 'widget.administration-components-fulltextfilters-panel',
    controller: 'administration-components-fulltextfilters-panel',
    viewModel: {
        type: 'administration-components-fulltextfilters-panel'
    },
    items: [{
        xtype: 'panel',
        cls: 'panel-with-gray-background',
        padding: '10 10 10 15',
        items: [{
            xtype: 'textfield',
            labelAlign: 'top',
            fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.texts.writefulltextquery,
            itemId: 'filterQueryInput',
            bind: {
                value: '{_query}'
            }
        }],
        bind: {
            hidden: '{actions.view}'
        }
    }, {
        xtype: 'panel',
        
        padding: '10 10 10 15',
        items: [{
            xtype: 'displayfield',
            labelAlign: 'top',
            fieldLabel: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fulltextquery,
            itemId: 'filterQueryInput',
            bind: {
                value: '{_query}'
            }
        }],
        bind: {
            hidden: '{!actions.view}'
        }
    }],

    getQueryData: function(){
        var query = this.getViewModel().get('_query');
        return Ext.String.format(query); 
    }

});
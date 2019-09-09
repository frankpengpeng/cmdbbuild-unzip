
Ext.define('CMDBuildUI.view.classes.cards.TabPanel', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.classes-cards-tabpanel',

    requires: [
        'CMDBuildUI.view.classes.cards.TabPanelController',
        'CMDBuildUI.view.classes.cards.TabPanelModel',

        'CMDBuildUI.view.classes.cards.card.View',
        'CMDBuildUI.view.classes.cards.card.Edit'
    ],

    mixins: [
        'CMDBuildUI.mixins.DetailsTabPanel'
    ],

    controller: 'classes-cards-tabpanel',
    viewModel: {
        type: 'classes-cards-tabpanel'
    },

    ui: 'management',
    border: false,
    tabPosition: 'left',
    tabRotation: 0,

    defaults: {
        textAlign: 'left',
        bodyPadding: 10,
        scrollable: true,
        border: false
    },
    layout: 'fit'
});

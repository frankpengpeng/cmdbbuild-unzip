
Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.View',{
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.ViewController',
        'CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.ViewModel'
    ],

    controller: 'administration-content-processes-tabitems-geoattributes-card-view',
    viewModel: {
        type: 'administration-content-processes-tabitems-geoattributes-card-view'
    },

    html: 'Hello, World!!'
});

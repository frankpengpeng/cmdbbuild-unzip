Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-processes-tabitems-geoattributes-card-edit',
    data: {
        name: 'CMDBuildUI',
        type: {
            isLine: false,
            isPoint: false,
            isPolygon: false
        }
    },

    formulas: {
        subtype: {
            bind: {
                subtype: '{theGeoAttribute.subtype}'
            },
            get: function (data) {
                this.set('type.isLine', data.subtype === 'LINESTRING');
                this.set('type.isPoint', data.subtype === 'POINT');
                this.set('type.isPolygon', data.subtype === 'POLYGON');
            }
        }
    },

    stores: {
        subtypesStore: {
            type: 'store',
            model: 'CMDBuildUI.model.base.ComboItem',
            data: [{
                key: 'LINE',
                value: 'LINESTRING'
            }, {
                key: 'POINT',
                value: 'POINT'
            }, {
                key: 'POLYGON',
                value: 'POLYGON'
            }]
        },

        strokeDashStyleStore: {
            type: 'store',
            model: 'CMDBuildUI.model.base.ComboItem',
            data: [{
                key: 'Dash',
                value: 'dash'
            }, {
                key: 'Dashdot',
                value: 'dashdot'
            }, {
                key: 'Dot',
                value: 'dot'
            }, {
                key: 'Longdash',
                value: 'longdash'
            }, {
                key: 'Longdashdot',
                value: 'longdashdot'
            }, {
                key: 'Solid',
                value: 'solid'
            }]
        }
    }

});
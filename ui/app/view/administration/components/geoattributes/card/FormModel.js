Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.FormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-geoattributes-card-form',
    data: {
        name: 'CMDBuildUI',
        actions: {
            view: true,
            edit: false,
            add: false
        },
        type: {
            isLine: false,
            isPoint: false,
            isPolygon: false
        },
        treeStoreData: []
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
        },
        treeStoreDataManager: {
            bind: {
                theGeoAttribute: '{theGeoAttribute}'
            },
            get: function (data) {

                if (data.theGeoAttribute) {
                    var me = this;
                    var item = {
                        expanded: true,
                        children: []
                    };

                    var childrens = [];
                    var wfEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled);
                    var promises = [CMDBuildUI.util.administration.helper.TreeClassesHelper.appendClasses(true, data.theGeoAttribute.get('visibility'), true, true).then(function (classes) {
                        childrens[0] = classes;
                    }, function () {
                        Ext.Msg.alert('Error', 'Classes store NOT LOADED!');
                    })];

                    if (wfEnabled) {
                        promises.push(CMDBuildUI.util.administration.helper.TreeClassesHelper.appendProcesses(true, data.theGeoAttribute.get('visibility'), true, true).then(function (processes) {
                            childrens[1] = processes;
                        }, function () {
                            Ext.Msg.alert('Error', 'Processes store NOT LOADED!');
                        }));
                    }
                    Ext.Promise.all(promises).then(function () {

                        var tree = {
                            text: 'Root',
                            expanded: true,
                            children: '{treeStoreData}'
                        };
                        tree.children = childrens;
                        me.set('treeStoreData', tree);
                        return tree;
                    });
                }
            }
        }
    },

    stores: {
        subtypesStore: {
            type: 'store',
            model: 'CMDBuildUI.model.base.ComboItem',
            data: [{
                label: 'LINE',
                value: 'LINESTRING'
            }, {
                label: 'POINT',
                value: 'POINT'
            }, {
                label: 'POLYGON',
                value: 'POLYGON'
            }]
        },

        strokeDashStyleStore: {
            type: 'store',
            model: 'CMDBuildUI.model.base.ComboItem',
            data: [{
                label: 'Dash',
                value: 'dash'
            }, {
                label: 'Dashdot',
                value: 'dashdot'
            }, {
                label: 'Dot',
                value: 'dot'
            }, {
                label: 'Longdash',
                value: 'longdash'
            }, {
                label: 'Longdashdot',
                value: 'longdashdot'
            }, {
                label: 'Solid',
                value: 'solid'
            }]
        },


        gridStore: {
            type: 'tree',
            proxy: {
                type: 'memory'
            },
            root: '{treeStoreData}',
            listeners: {
                datachanged: 'onTreeStoreDataChanged'
            }
            // root:'{storeData}'
        },
        icons: {
            model: 'CMDBuildUI.model.icons.Icon',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                url:  Ext.String.format(
                    '{0}/uploads/?path=images/gis',
                    CMDBuildUI.util.Config.baseUrl
                ),
                type: 'baseproxy'
            },
            pagination: 0
        }
    }

});
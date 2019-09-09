Ext.define('CMDBuildUI.view.administration.content.bim.projects.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-bim-projects-view',
    data: {
        storeAutoLoad: false,
        storeProxyUrl: ''
    },
    formulas: {
        panelTitle: {
            bind: {
                theProject: '{theProject}',
                theProjectDescription: '{theProject.description}'
            },
            get: function (data) {
                if (data.theProject.phantom) {
                    return CMDBuildUI.locales.Locales.administration.bim.newproject;
                } else {
                    return Ext.String.format('{0}: {1}', CMDBuildUI.locales.Locales.administration.bim.projectlabel, data.theProjectDescription);
                }

            }
        },
        updateStoreVariables: {
            get: function (data) {
                // set auto load
                this.set("storeAutoLoad", true);
            }
        },
        getAssociatedCards: {
            bind: '{theProject.ownerClass}',
            get: function (associatedClass) {
                if (associatedClass) {
                    var url = CMDBuildUI.util.api.Classes.getCardsUrl(associatedClass);
                    this.set('cardStoreUrl', url);
                    this.set('CardStoreAutoLoad', true);
                    if (this.getStore('getAssociatedCardsStore')) {
                        this.getStore('getAssociatedCardsStore').reload();
                    }
                }
            }
        },
        getAllClassesProcesses: {
            get: function () {
                var data = [];

                var types = {
                    classes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.classes,
                        childrens: Ext.getStore('classes.Classes').getData().getRange()
                    },
                    processes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.processes,
                        childrens: Ext.getStore('processes.Processes').getData().getRange()
                    }
                };
                Object.keys(types).forEach(function (type, typeIndex) {
                    types[type].childrens.forEach(function (value, index) {
                        var item = {
                            value: value.get('_id'),
                            label: value.get('description')
                        };
                        data.push(item);
                    });
                });
                return data;
            }
        }
    },

    stores: {
        projects: {
            type: 'bim-projects',
            autoLoad: '{storeAutoLoad}',
            autoDestroy: true,
            pageSize: 0
        },
        getAssociatedCardsStore: {
            autoDestroy: true,
            type: 'classes',
            proxy: {
                type: 'baseproxy',
                url: '{cardStoreUrl}'
            },
            //pageSize: 0,
            autoLoad: '{CardStoreAutoLoad}'
        },
        getAllClassesProcessesStore: {
            data: '{getAllClassesProcesses}',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true
        }
    }

});
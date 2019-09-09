Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset',

    data: {
        
    },
    formulas: {
        inlineClosed: {
            bind: {
                inline: '{theDomain.inline}'
            },
            get: function (data) {
                if (!data.inline) {
                    this.set('theDomain.defaultClosed', false);
                }
            }
        },
        localizationButtonHidden: {
            bind: {
                isEdit: '{actions.edit}'
            },
            get: function (data) {
                return data.isEdit;
            }
        },
        updateMasterDetail: {
            bind: {
                isMasterDetail: '{theDomain.isMasterDetail}',
                isView: '{actions.view}',
                isAdd: '{actions.add}',
                isEdit: '{actions.edit}'
            },
            get: function (data) {
                if (data.isMasterDetail) {
                    if (data.isView) {
                        this.set('descriptionMasterDetailInput.hidden', true);
                        this.set('descriptionMasterDetailDisplay.hidden', false);
                    } else if (data.isEdit) {
                        this.set('descriptionMasterDetailInput.hidden', false);
                        this.set('descriptionMasterDetailDisplay.hidden', true);
                    } else {
                        this.set('descriptionMasterDetailInput.hidden', false);
                        this.set('descriptionMasterDetailDisplay.hidden', true);
                    }
                } else {
                    this.set('descriptionMasterDetailInput.hidden', true);
                    this.set('descriptionMasterDetailDisplay.hidden', true);

                }
                if (data.isView) {
                    this.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                } else if (data.isEdit) {
                    this.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                } else if (data.isAdd) {
                    this.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
                }
            }
        },
        getAllClassesProcesses: {
            get: function () {
                var data = [];
                var types = {
                    classes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.classes, // Classes
                        childrens: Ext.Array.filter(Ext.getStore('classes.Classes').getData().getRange(), function(item){                        
                            return item.get('type') === 'standard';
                        })
                    },
                    processes: {
                        label: CMDBuildUI.locales.Locales.administration.navigation.processes, // Processes
                        childrens: Ext.getStore('processes.Processes').getData().getRange()
                    }
                };
                Object.keys(types).forEach(function (type, typeIndex) {
                    types[type].childrens.forEach(function (value, index) {
                        var item = {
                            group: type,
                            groupLabel: types[type].label,
                            _id: value.get('_id'),
                            label:value.get('description') 
                        };
                        data.push(item);
                    });
                });
                data.sort(function (a, b) {
                    var aGroup = a.group.toUpperCase();
                    var bGroup = b.group.toUpperCase();
                    var aLabel = a.label.toUpperCase();
                    var bLabel = b.label.toUpperCase();

                    if (aGroup === bGroup) {
                        return (aLabel < bLabel) ? -1 : (aLabel > bLabel) ? 1 : 0;
                    } else {
                        return (aGroup < bGroup) ? -1 : 1;
                    }
                });
                return data;
            }
        }
    },
    stores: {
        cardinalityStore: {
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            data: [
                { 'value': '1:1', 'label': '1:1' },
                { 'value': '1:N', 'label': '1:N' },
                { 'value': 'N:1', 'label': 'N:1' },
                { 'value': 'N:N', 'label': 'N:N' }
            ]
        },
        getAllStandardClassesAndProcessesStore: {
            data: '{getAllClassesProcesses}',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true
        },
        sourceClassStore: {
            source: '{getAllStandardClassesAndProcessesStore}',            
            autoDestroy: true
        },
        destinationClassStore: {
            source: '{getAllStandardClassesAndProcessesStore}',
            autoDestroy: true
        }
    }
});

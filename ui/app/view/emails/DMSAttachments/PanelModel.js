Ext.define('CMDBuildUI.view.emails.DMSAttachments.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.emails-dmsattachments-panel',
    data: {
        selectedClass: null
    },

    formulas: {
        attributeslistdata: {
            get: function () {
                var classStore = Ext.getStore('classes.Classes').getRange();
                var processStore = Ext.getStore('processes.Processes').getRange();
                var resList = [];                
                var processEnabled = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled);
                classStore.forEach(function (element) {
                    resList.push({
                        value: element.get('name'),
                        label: element.get('_description_translation'),
                        type: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass
                    });
                });

                if (processEnabled){
                    processStore.forEach(function (element) {
                        resList.push({
                            value: element.get('name'),
                            label: element.get('_description_translation'),
                            type: CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                        });
                    });
                }
                return resList;
            }
        }
    },

    stores: {
        attributeslist: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: "memory"
            },

            sorters: ['label'],
            data: '{attributeslistdata}',
            autoDestroy: true
        }
    }

});

Ext.define('CMDBuildUI.view.importexport.ExportModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.importexport-export',

    data: {
        exporturl: null
    },

    formulas: {
        updateData: {
            get: function () {
                var view = this.getView();

                // templates
                var templates = view.getTemplates();
                this.set("templatesdata", templates);

                // default template
                var object = view.getObject();
                if (object.get("defaultImportTemplate")) {
                    this.set("values.template", object.get("defaultImportTemplate"));
                } else if (templates.length === 1) {
                    this.set("values.template", templates[0].getId());
                }
            }
        }
    },

    stores: {
        templates: {
            model: 'CMDBuildUI.model.importexports.Template',
            proxy: 'memory',
            data: '{templatesdata}'
        }
    }

});

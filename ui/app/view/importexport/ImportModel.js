Ext.define('CMDBuildUI.view.importexport.ImportModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.importexport-import',

    data: {
        response: {},
        values: {},
        hidden: {},
        disabled: {},
        labels: {}
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
        },

        responseText: {
            bind: {
                response: '{response}'
            },
            get: function (data) {
                if (data.response && !Ext.Object.isEmpty(data.response)) {
                    return Ext.String.format(
                        "<strong>{0}</strong>: {1} - <strong>{2}</strong>: {3} - <strong>{4}</strong>: {5} - <strong>{6}</strong>: {7} - <strong>{8}</strong>: {9} - <strong>{10}</strong>: {11}",
                        CMDBuildUI.locales.Locales.importexport.response.processed,
                        data.response.processed,
                        CMDBuildUI.locales.Locales.importexport.response.created,
                        data.response.created,
                        CMDBuildUI.locales.Locales.importexport.response.modified,
                        data.response.modified,
                        CMDBuildUI.locales.Locales.importexport.response.deleted,
                        data.response.deleted,
                        CMDBuildUI.locales.Locales.importexport.response.unmodified,
                        data.response.unmodified,
                        CMDBuildUI.locales.Locales.importexport.response.errors,
                        data.response.errors.length
                    );
                }
            }
        },

        errorTemplate: {
            bind: {
                haserrors: '{response.hasErrors}',
                store: '{templates}',
                selected: '{values.template}'
            },
            get: function (data) {
                if (data.haserrors) {
                    var template = data.store.getById(data.selected);
                    var tpl = "";
                    template.columns().getRange().forEach(function (c, i) {
                        tpl += '<strong>' + c.get("columnName") + '</strong>: {' + i + '}<br />';
                    });
                    return tpl;
                }
            }
        },

        downloadreportsrc: {
            bind: {
                response: '{responsetext}'
            },
            get: function (data) {
                return 'data:application/octet-stream,' + data.response;
            }
        },

        columns: {
            bind: {
                templates: '{templates}',
                selected: '{values.template}',
                classmodel: '{classmodel}'
            },
            get: function (data) {
                if (data.selected) {
                    var template = data.templates.getById(data.selected);
                    if (template.get("importKeyAttribute")) {
                        var f = data.classmodel.getField(template.get("importKeyAttribute"));
                        this.set("labels.importKeyAttribute", f.attributeconf.description_localized);
                    }
                    if (template.get("mergeMode")) {
                        var r = Ext.Array.findBy(
                            CMDBuildUI.model.importexports.Template.getMergeModes(), 
                            function(item, index) {
                                return item.value === template.get("mergeMode");
                            });
                        this.set("labels.mergeMode", r && r.label || "");
                    }
                    if (template) {
                        return template.columns();
                    }
                }
            }
        }
    },

    stores: {
        templates: {
            model: 'CMDBuildUI.model.importexports.Template',
            proxy: 'memory',
            data: '{templatesdata}',
            autoDestroy: true
        },

        errors: {
            fields: [{
                type: 'int',
                name: 'recordNumber'
            }, {
                type: 'int',
                name: 'lineNumber'
            }, {
                type: 'string',
                name: 'message'
            }],
            proxy: 'memory',
            data: '{response.errors}',
            autoDestroy: true
        }
    }

});

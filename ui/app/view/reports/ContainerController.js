Ext.define('CMDBuildUI.view.reports.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.reports-container',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#refreshbtn': {
            click: 'onRefreshBtnClick'
        }
    },

    /************************************************************************************************************** 
     *                                                                  
     *                                                  REPORTS
     *
     * EVENTS:
     *  onBeforeRender              (view, eOpts)                           --> render view with selected object
     * 
     * METHODS:
     *  addRelationAttibutes        (attributes, ext, title, reportId)      --> add attributes for the current report
     *                                                                  
     * ************************************************************************************************************/

    /**
    * @param {CMDBuildUI.view.reports.ContainerController} view
    * @param {Object} eOpts
    */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = view.lookupViewModel();
        vm.bind({
            bindTo: {
                report: '{theReport}'
            }
        }, function (data) {
            data.report.getAttributes().then(function (attributes) {
                me.addRelationAttibutes(attributes.getRange(), data.report.getId());
            });
        });
    },

    /**
    * add attributes for the current report
    * @param {Object} attributes
    * @param {numeric} reportId
    */
    addRelationAttibutes: function (attributes, reportId) {
        var vm = this.getViewModel();
        var extension = vm.get("extension");

        if (Ext.isEmpty(attributes) && !Ext.isEmpty(extension)) {
            // open report
            this.reportViewer(reportId, extension, null);
            return;
        }

        var form = this.getFormConfig(reportId, attributes, extension);
        if (form) {
            CMDBuildUI.util.Utilities.openPopup(this.parameterspopupid, vm.get("title"), form, {}, {
                width: "50%",
                height: "50%"
            });
        }

    },

    /**
     * 
     * @param {Number} reportId 
     * @param {String} extension One of `CMDBuildUI.model.reports.Report.extensions` items
     * @param {Object} parameters 
     */
    reportViewer: function (reportId, extension, parameters) {
        var view = this.getView();
        var queryparams = {
            extension: extension
        };
        if (!Ext.Object.isEmpty(parameters)) {
            queryparams.parameters = Ext.encode(parameters);
        }
        var url = Ext.String.format(
            "{0}{1}?{2}",
            CMDBuildUI.util.Config.baseUrl,
            CMDBuildUI.util.api.Reports.getReportDownloadUrl(reportId, extension),
            Ext.Object.toQueryString(queryparams)
        );

        // IE 11 Visualization problem
        if(navigator.userAgent.indexOf('MSIE')!==-1 || navigator.appVersion.indexOf('Trident/') > -1){
            /* Microsoft Internet Explorer detected in. */
            view.lookupReference("reportiframe").hide();
            var obj = Ext.String.format('<object style="width:100%;height:100%"data="{0}" type="application/pdf"> <embed src="{0}" type="application/pdf" /></object></div>',
            url);
            view.setHtml(obj);
        } else {
            view.fireEvent("printreport", view, reportId, extension, parameters);
            // load report
            view.lookupReference("reportiframe").load(url);
        }
        // save url on view model to enable download button
        view.lookupViewModel().set("downloadbtn.href", url + "&_download=true");
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Object} eOpts 
     */
    onRefreshBtnClick: function(button, eOpts) {
        var me = this;
        var report = button.lookupViewModel().get("theReport");
        report.getAttributes().then(function (attributes) {
            me.addRelationAttibutes(attributes.getRange(), report.getId());
        });
    },

    privates: {
        /**
         * @property {String} parameterspopupid
         * The id used for the popup.
         */
        parameterspopupid: 'report-parameters-popup',

        /**
         * @property {String} reportformatfield
         * The field name used for the extension combo.
         */
        reportformatfield: '_reportformatfield',

        /**
         * 
         * @param {CMDBuildUI.model.Attribute[]} attributes 
         * @param {String} extension
         * @return {Ext.form.Field[]} Form fields
         */
        getFormFields: function (attributes, extension) {
            var vm = this.getViewModel();
            var fields = [];
            var defaults = vm.get("defaults") || {};
            var allreadonlyfields = true;

            // add extension field if extension is empty
            if (Ext.isEmpty(extension)) {
                fields.push({
                    anchor: '100%',
                    xtype: 'combobox',
                    fieldLabel: CMDBuildUI.locales.Locales.reports.format,
                    name: this.reportformatfield,
                    id: this.reportformatfield,
                    store: {
                        data: [{
                            value: CMDBuildUI.model.reports.Report.extensions.csv,
                            label: CMDBuildUI.locales.Locales.reports.csv
                        }, {
                            value: CMDBuildUI.model.reports.Report.extensions.odt,
                            label: CMDBuildUI.locales.Locales.reports.odt
                        }, {
                            value: CMDBuildUI.model.reports.Report.extensions.pdf,
                            label: CMDBuildUI.locales.Locales.reports.pdf
                        }, {
                            value: CMDBuildUI.model.reports.Report.extensions.rtf,
                            label: CMDBuildUI.locales.Locales.reports.rtf
                        }]
                    },
                    forceSelection: true,
                    queryMode: 'local',
                    displayField: 'label',
                    valueField: 'value',
                    allowBlank: false
                });
                allreadonlyfields = false;
            }

            // generate form fields from attributes list
            attributes.forEach(function (attribute) {
                var field = CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attribute);
                // replace field name with attribute name
                field.name = attribute.get('name');
                var config = {
                    mode: field.mode,
                    linkName: null,
                    overrides: {
                        mandatory: field.mandatory
                    }
                };
                if (defaults[field.name]) {
                    config.defaultValue = defaults[field.name];
                    if (defaults[field.name].editable === undefined || defaults[field.name].editable == true) {
                        allreadonlyfields = false;
                    }
                } else {
                    allreadonlyfields = false;
                }
                var item = CMDBuildUI.util.helper.FormHelper.getFormField(field, config);
                fields.push(item);
            });

            if (allreadonlyfields) {
                return false;
            }
            return fields;
        },

        /**
         * 
         * @param {Number} reportId
         * @param {CMDBuildUI.model.Attribute[]} attributes 
         * @param {String} extension
         * @return {Object}
         */
        getFormConfig: function (reportId, attributes, extension) {
            var view = this.getView();
            var me = this;
            var fields = this.getFormFields(attributes, extension);

            // if all fields are read-only show report without asking parameters
            if (fields === false) {
                var defaults = this.getViewModel().get("defaults") || {};
                var defvalues = {};
                for (var k in defaults) {
                    defvalues[k] = defaults[k].value;
                }
                me.reportViewer(reportId, extension, defvalues);
                return;
            }

            return {
                xtype: 'form',
                bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                scrollable: true,
                items: this.getFormFields(attributes, extension),
                buttons: [{
                    xtype: 'button',
                    ui: 'management-action',
                    text: CMDBuildUI.locales.Locales.reports.print,
                    formBind: true,
                    handler: function (button) {
                        var form = button.up("form");
                        var params = {};
                        form.getForm().getFields().getRange().forEach(function (field) {
                            if (field.getName() === me.reportformatfield) {
                                extension = field.getValue();
                            } else {
                                if (!field.isFieldContainer) {
                                    params[field.getName()] = field.getValue() || "";
                                }
                            }
                        });
                        me.reportViewer(reportId, extension, params);
                        CMDBuildUI.util.Utilities.closePopup(me.parameterspopupid);
                    }
                }, {
                    xtype: 'button',
                    ui: 'secondary-action',
                    text: CMDBuildUI.locales.Locales.common.actions.cancel,
                    handler: function (button) {
                        view.fireEvent("closeparameterspopup", view, reportId);
                        CMDBuildUI.util.Utilities.closePopup(me.parameterspopupid);
                    }
                }]
            };
        }
    }
});

Ext.define('CMDBuildUI.view.widgets.createreport.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-createreport-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
    * @param {CMDBuildUI.view.widgets.createmodifycard.Panel} view
    * @param {Object} eOpts
    */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        var theWidget = vm.get('theWidget');
        var extension;
        if (theWidget.get(this.parameters.ForcePDF)) {
            extension = CMDBuildUI.model.reports.Report.extensions.pdf;
        } else if (theWidget.get(this.parameters.ForceCSV)) {
            extension = CMDBuildUI.model.reports.Report.extensions.csv;
        }
        view.add({
            xtype: 'reports-container',
            layout: 'fit',
            hideTitle: true,
            viewModel: {
                data: {
                    objectTypeName: theWidget.get('ReportCode'),
                    extension: extension,
                    defaults: this.getDefaultValues()
                }
            },
            listeners: {
                closeparameterspopup: function(reportcontainer, reportid) {
                    view.fireEvent("popupclose");
                }
            }
        });
    },

    /**
     * @return {Object[]} 
     * An array of objects with `value` and `editable` properties.
     */
    getDefaultValues: function() {
        var me = this;
        var vm = this.getViewModel();
        var theWidget = vm.get('theWidget');
        var theTarget = vm.get("theTarget");
        var data = theWidget.getData();
        var defaults = {};
        // get default values
        for (var key in data) {
            // check that key is not system key or configuration parameter
            if (!Ext.String.startsWith(key, "_") && !this.parameters[key]) {
                defaults[key] = {
                    value: me.extractVariableFromString(data[key], theTarget)
                };
            }
        }

        // get read only fields
        if (!Ext.isEmpty(vm.get('theWidget').get(this.parameters.ReadOnlyAttributes))) {
            var readonly = vm.get('theWidget').get(this.parameters.ReadOnlyAttributes).split(",");
            readonly.forEach(function(f) {
                if (!defaults[f]) {
                    defaults[f] = {};
                }
                defaults[f].editable = false;
            });
        }
        return defaults;
    },

    /**
     * Resolve variable.
     * @param {String} variable
     * @param {CMDBuildUI.model.base.Base} theTarget 
     * @return {*} The variable resolved.
     */
    extractVariableFromString: function (variable, theTarget) {
        if (Ext.isString(variable) && CMDBuildUI.util.api.Client.testRegExp(/^{(client|server)+:*.+}$/, variable)) {
            variable = variable.replace("{", "").replace("}", "");
            var s_variable = variable.split(":");
            var result;
            if (s_variable[0] === "server") {
                result = CMDBuildUI.util.ecql.Resolver.resolveServerVariables([s_variable[1]], theTarget);
                return result[s_variable[1]];
            } else if (s_variable[0] === "client") {
                result = CMDBuildUI.util.ecql.Resolver.resolveClientVariables([s_variable[1]], theTarget);
                return result[s_variable[1]];
            }
        } else {
            return variable;
        }
    },

    privates: {
        /**
         * Custom configuration parameters for this widget
         */
        parameters: {
            ReportCode: "ReportCode",
            ForcePDF: "ForcePDF",
            ForceCSV: "ForceCSV",
            ReadOnlyAttributes: "ReadOnlyAttributes"
        }
    }
});

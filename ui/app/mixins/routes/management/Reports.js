Ext.define('CMDBuildUI.mixins.routes.management.Reports', {
    mixinId: 'managementroutes-reports-mixin',

    /**
     * Show report
     * 
     * @param {Numeric} reportName
     */
    showReport: function (reportName) {
        reportName = decodeURI(reportName);
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
        CMDBuildUI.util.Navigation.addIntoManagemenetContainer('reports-container', {
            viewModel: {
                data: {
                    objectTypeName: reportName
                }
            }
        });

        // fire global event objecttypechanged
        Ext.GlobalEvents.fireEventArgs("objecttypechanged", [reportName]);

        // update current context
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.report,
            reportName
        );
    },

    /**
     * Show report with defined extension
     * 
     * @param {string} reportName
     * @param {string} extension
     */
    showReportExtension: function (reportName, extension) {
        reportName = decodeURI(reportName);
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
        CMDBuildUI.util.Navigation.addIntoManagemenetContainer('reports-container', {
            viewModel: {
                data: {
                    objectTypeName: reportName,
                    extension: extension
                }
            }
        });

        // fire global event objecttypechanged
        Ext.GlobalEvents.fireEventArgs("objecttypechanged", [reportName, extension]);

        // update current context
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.report,
            reportName,
            extension
        );
    }
});
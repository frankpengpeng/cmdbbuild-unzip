Ext.define('CMDBuildUI.util.api.Reports', {
    singleton: true,

    /**
     * Get class geo attributes
     * 
     * @param {Number} reportId
     * @return {String} The url for api resources
     */
    getReportAttributesUrlByReportId: function (reportId) {
        return Ext.String.format(
            "/reports/{0}/attributes",
            reportId
        );
    },

    /**
     * Get url for report download
     * 
     * @param {Number} reportId
     * @param {String} filename
     * @return {String} The url for api resources
     */
    getReportDownloadUrl: function(reportId, extension) {
        var filename = "report";
        if (reportId) {
            var report = Ext.getStore('reports.Reports').getById(reportId);
            filename = report ? report.get("description") : filename;
        }
        if (extension) {
            filename += "." + extension;
        }
        filename = filename.replace(/\//g, "-");
        return Ext.String.format(
            "/reports/{0}/{1}",
            reportId,
            filename
        );
    }
});
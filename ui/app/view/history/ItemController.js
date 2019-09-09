Ext.define('CMDBuildUI.view.history.ItemController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.history-item',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * @param {CMDBuildUI.view.history.Item} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        // get element id
        var config = view.getInitialConfig();
        if (!Ext.isEmpty(config._rowContext)) {
            var record = config._rowContext.record; // get widget record
            if (record && record.getData()) {
                view.setObjectId(record.get("_id"));
            }
        }

        var vm = this.getViewModel();
        var objectType = vm.get("objectType");
        var objectTypeName = vm.get("objectTypeName");

        var model = CMDBuildUI.util.helper.ModelHelper.getHistoryModel(objectType, objectTypeName);
        model.setProxy({
            url: vm.get("storedata.proxyurl"),
            type: 'baseproxy'
        });
        vm.linkTo("theObject", {
            type: model.getName(),
            id: view.getObjectId()
        });

        // get form fields
        var tabpanel = CMDBuildUI.util.helper.FormHelper.renderForm(model, {
            mode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
            showNotes: true
        });

        Ext.apply(tabpanel, {
            tools: view.tabpaneltools
        });
        view.add(tabpanel);

        // mark changes
        vm.bind({
            bindTo: '{theObject}'
        }, function (theObject) {
            if (theObject) {
                view.getForm().getFields().getRange().forEach(function (field) {
                    if (theObject.get("_" + field.getName() + "_changed")) {
                        field.addCls("highlight-field");
                        var panel = field.up("panel");
                        if (!panel._haschanges) {
                            panel.setTitle(panel.getTitle() + " <small class=\"x-fa fa-circle mark-changes\"></small>");
                            panel._haschanges = true;
                        }
                    }
                });
            }
        });
    }
});

Ext.define('CMDBuildUI.mixins.routes.management.Views', {
    mixinId: 'managementroutes-views-mixin',

    /**
     * Before show view
     * 
     * @param {String} viewName
     * @param {Object} action
     */
    onBeforeShowView: function (viewName, action) {
        viewName = decodeURI(viewName);
        var objectType, objectTypeName;
        var object = CMDBuildUI.util.helper.ModelHelper.getViewFromName(viewName);

        if (object && object.get("type") === CMDBuildUI.model.views.View.types.filter) {
            // define class or process type and typename
            objectTypeName = object.get("sourceClassName");
            objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectTypeName);
        } else {
            // define view type and typename
            objectType = CMDBuildUI.util.helper.ModelHelper.objecttypes.view;
            objectTypeName = viewName;
        }

        // get model
        CMDBuildUI.util.helper.ModelHelper.getModel(
            objectType,
            objectTypeName
        ).then(function (model) {
            action.resume();
        }, function () {
            action.stop();
        });
    },

    /**
     * Show view
     * 
     * @param {String} viewName
     */
    showView: function (viewName) {
        viewName = decodeURI(viewName);
        var xtype, config;
        var object = CMDBuildUI.util.helper.ModelHelper.getViewFromName(viewName);
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();

        if (object && object.get("type") === CMDBuildUI.model.views.View.types.filter) {
            var objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(object.get("sourceClassName"));
            switch (objectType) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    xtype = 'classes-cards-grid-container';
                    config = {
                        objectTypeName: object.get("sourceClassName"),
                        maingrid: true,
                        filter: object.get("filter")
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                    xtype = 'processes-instances-grid';
                    config = {
                        objectTypeName: object.get("sourceClassName"),
                        filter: object.get("filter"),
                        viewModel: {
                            data: {
                                objectTypeName: object.get("sourceClassName")
                            }
                        }
                    };
                    break;
            }
            CMDBuildUI.util.Navigation.addIntoManagemenetContainer(xtype, config);

            // update current context
            CMDBuildUI.util.Navigation.updateCurrentManagementContext(
                objectType,
                object.get("sourceClassName")
            );
        } else {
            CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
            CMDBuildUI.util.Navigation.addIntoManagemenetContainer('views-items-grid', {
                viewModel: {
                    data: {
                        objectTypeName: viewName
                    }
                }
            });

            CMDBuildUI.util.Navigation.updateCurrentManagementContext(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
                viewName
            );
        }

        // fire global event objecttypechanged
        Ext.GlobalEvents.fireEventArgs("objecttypechanged", [viewName]);
    }
});
Ext.define("CMDBuildUI.util.Navigation", {
    singleton: true,

    contexts: {
        administration: 'administration',
        management: 'management'
    },

    /**
     * Return main container.
     * 
     * @param {Boolean} create Create the container if not exists.
     * @return {CMDBuildUI.view.main.content.Container}
     */
    getMainContainer: function (create) {
        var container = Ext.getCmp(CMDBuildUI.view.main.content.Container.elementId);
        if (!container && create) {
            container = Ext.create(CMDBuildUI.view.main.content.Container);
        }
        return container;
    },

    /**
     * Clear main container.
     * 
     * @param {Boolean} create Create the container if not exists.
     * @return {CMDBuildUI.view.main.content.Container}
     */
    clearMainContainer: function (create) {
        var container = this.getMainContainer(create);
        if (container) {
            container.removeAll(true);
        }
        var administrationContent = this.getMainAdministrationContainer();
        if(administrationContent){
            administrationContent.destroy();
        }
        return container;
    },

    /**
     * Add a component into main content.
     * 
     * @param {String} xtype The component xtype
     * @param {Object} parameters
     * @return {CMDBuildUI.view.main.content.Container}
     */
    addIntoMainContainer: function (xtype, parameters) {
        var config = Ext.applyIf({
            xtype: xtype
        }, parameters);

        // get and clear container
        var container = this.clearMainContainer(true);
        // add component
        container.add(config);
        return container;
    },


    /**
     * Return management container.
     * 
     * @param {Boolean} create Create the container if not exists.
     * @return {CMDBuildUI.view.management.Content}
     */
    getManagementContainer: function (create) {
        var container = Ext.getCmp(CMDBuildUI.view.management.Content.elementId);
        if (!container && create) {
            container = Ext.create(CMDBuildUI.view.management.Content);
        }
        return container;
    },

    /**
     * Clear main container.
     * 
     * @param {Boolean} create Create the container if not exists.
     * @return {CMDBuildUI.view.management.Content}
     */
    clearManagementContainer: function (create) {
        var container = this.getManagementContainer(create);
        if (container) {
            container.removeAll(true);
        }
        return container;
    },

    /**
     * Add a component into management container.
     * 
     * @param {String} xtype
     * @param {Object} parameters
     * @return {CMDBuildUI.view.management.Content}
     */
    addIntoManagemenetContainer: function (xtype, parameters) {
        var config = Ext.applyIf({
            xtype: xtype
        }, parameters);

        // get and clear container
        var container = this.clearManagementContainer(true);
        // add component
        container.add(config);
        return container;
    },


    /**
     * Return details window container.
     * 
     * @param {Boolean} create Create the container if not exists.
     * @return {CMDBuildUI.view.management.DetailsWindow}
     */
    getManagementDetailsWindow: function (create) {
        var container = Ext.getCmp(CMDBuildUI.view.management.DetailsWindow.elementId);
        if (!container && create) {
            container = Ext.create(CMDBuildUI.view.management.DetailsWindow);
        }
        return container;
    },

    /**
     * Clear details window container.
     * 
     * @param {Boolean} create Create the container if not exists.
     * @return {CMDBuildUI.view.management.DetailsWindow}
     */
    clearManagementDetailsWindow: function (create) {
        var container = this.getManagementDetailsWindow(create);
        if (container) {
            container.removeAll(true);
        }
        return container;
    },

    /**
     * Removes detail window container.
     */
    removeManagementDetailsWindow: function () {
        var container = this.getManagementDetailsWindow(false);
        if (container) {
            container.close();
        }
    },

    /**
     * Add a component into management container.
     * 
     * @param {String} xtype
     * @param {Object} parameters
     * @return {CMDBuildUI.view.management.DetailsWindow}
     */
    addIntoManagementDetailsWindow: function (xtype, parameters) {
        var config = Ext.applyIf({
            xtype: xtype
        }, parameters);

        // get and clear container
        var container = this.clearManagementDetailsWindow(true);
        // add component
        container.add(config);
        return container;
    },

    /**
     * Removes detail window container.
     * 
     * @param {String} newtitle
     */
    updateTitleOfManagementDetailsWindow: function (newtitle) {
        var container = this.getManagementDetailsWindow(false);
        if (container) {
            container.setTitle(newtitle);
        }
    },


    /**
     * Return administration container.
     * 
     * @param {Boolean} create Create the container if not exists.
     * @return {CMDBuildUI.view.administration.Content}
     */
    getMainAdministrationContainer: function (create) {
        // var container = Ext.getBody().down('administration-content');
        var container = Ext.getCmp(CMDBuildUI.view.administration.Content.elementId);
        if (!container && create) {

            container = Ext.create(CMDBuildUI.view.administration.Content);
        }
        return container;
    },

    /**
     * Clear administration container.
     * 
     * @param {Boolean} create Create the container if not exists.
     * @return {CMDBuildUI.view.administration.Content}
     */
    clearMainAdministrationContainer: function (create) {
        var container = this.getMainAdministrationContainer(create);
        if (container) {
            container.removeAll(true);
        }
        return container;
    },

    /**
     * Add a component into administration container.
     * 
     * @param {String} xtype
     * @param {Object} parameters
     * @return {CMDBuildUI.view.administration.Content}
    */
    addIntoMainAdministrationContent: function (xtype, parameters) {
        var config = Ext.applyIf({
            xtype: xtype
        }, parameters);

        // get and clear container
        var container = this.clearMainAdministrationContainer(true);
        // add component
        container.add(config);
        return container;
    },


    /**
     * Return administration details window container.
     * 
     * @param {Boolean} create Create the container if not exists.
     * @return {CMDBuildUI.view.administration.DetailsWindow}
     */
    getAdministrationDetailsWindow: function (create) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId);
        if (!container && create) {
            container = Ext.create(CMDBuildUI.view.administration.DetailsWindow.elementId);
        }
        return container;
    },

    /**
     * Clear administration container.
     * 
     * @param {Boolean} create Create the container if not exists.
     * @return {CMDBuildUI.view.administration.DetailsWindow}
     */
    clearAdministrationDetailsWindow: function (create) {
        var container = this.getAdministrationDetailsWindow(create);
        if (container) {
            container.removeAll(true);
        }
        return container;
    },

    /**
     * Removes administration detail window container.
     */
    removeAdministrationDetailsWindow: function () {
        var panel = this.clearAdministrationDetailsWindow(false);
        if (panel) {
            panel.destroy();
        }
    },

    /**
     * Add a component into administration details window container.
     * 
     * @param {String} xtype
     * @param {Object} parameters
     * @return {CMDBuildUI.view.administration.DetailsWindow}
     */
    addIntoAdministrationDetailsWindow: function (xtype, parameters) {
        var config = Ext.applyIf({
            xtype: xtype
        }, parameters);

        // get and clear container
        var container = this.clearAdministrationDetailsWindow(true);
        // add component
        container.add(config);
        return container;
    },


    /**
     * Return current main context
     * 
     * @return {Object} Current main context info.
     */
    getCurrentContext: function () {
        return this._currentcontext;
    },

    /**
     * Update current main context.
     * 
     * @param {String} context One of `administration` or `management`.
     * @param {String} objectType 
     * @param {String} objectTypeName
     * @param {String} objectId
     * @param {Object} other
     * @return {Object} Current main context info.
     */
    updateCurrentContext: function (context, objectType, objectTypeName, objectId, other) {
        this._currentcontext = Ext.applyIf({
            context: context,
            objectType: objectType,
            objectTypeName: objectTypeName,
            objectId: objectId
        }, other);
        return this.getCurrentContext();
    },

    /**
     * Clear current main context.
     */
    clearCurrentContext: function() {
        this._currentcontext = {};
        return this.getCurrentContext();
    },

    /**
     * Update current management main context.
     * 
     * @param {String} objectType 
     * @param {String} objectTypeName
     * @param {String} objectId
     * @param {Object} other
     * @return {Object} Current main context info.
     */
    updateCurrentManagementContext: function (objectType, objectTypeName, objectId, other) {
        return this.updateCurrentContext(
            this.contexts.management,
            objectType,
            objectTypeName,
            objectId,
            other
        );
    },

    /**
     * @param {String} action
     */
    updateCurrentManagementContextAction: function(action) {
        this._currentcontext.currentaction = action;
    },

    /**
     * @param {String} activity
     */
    updateCurrentManagementContextActivity: function(activity) {
        this._currentcontext.currentactivity = activity;
    },

    /**
     * Update current administration main context.
     * 
     * @param {String} objectType 
     * @param {String} objectTypeName
     * @param {String} objectId
     * @param {Object} other
     * @return {Object} Current main context info.
     */
    updateCurrentAdministrationContext: function (objectType, objectTypeName, objectId, other) {
        return this.updateCurrentContext(
            this.contexts.administration,
            objectType,
            objectTypeName,
            objectId,
            other
        );
    },

    /**
     * Check the consistency of the current context.
     * 
     * @param {String} objectType
     * @param {String} objectTypeName
     * @return {Boolean} Return true if main content is consistent
     * with selection.
     */
    checkCurrentContext: function (objectType, objectTypeName, checkHierarchy) {
        var context = this.getCurrentContext();
        if (!context) {
            return false;
        }

        if (context.objectTypeName === objectTypeName) {
            return true;
        } else if (!checkHierarchy) {
            return false;
        }

        // check hierarchy
        var item;
        switch (objectType) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                item = CMDBuildUI.util.helper.ModelHelper.getClassFromName(objectTypeName);
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                item = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(objectTypeName);
                break;
        }
        if (item) {
            var result = Ext.Array.contains(item.getHierarchy(), context.objectTypeName);
            return result;
        }
        return false;
    },

    /**
     * @param {String} action
     * @return {Boolean}
     */
    checkCurrentManagementContextAction: function(action) {
        return this._currentcontext.currentaction === action;
    },

    /**
     * @param {String} activity
     * @return {Boolean}
     */
    checkCurrentManagementContextActivity: function(activity) {
        return this._currentcontext.currentactivity === activity;
    },

    privates: {
        /**
         * @property {Object} _currentcontext
         * Current application context.
         */
        _currentcontext: {}
    }
});
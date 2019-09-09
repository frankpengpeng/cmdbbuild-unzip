Ext.define('CMDBuildUI.mixins.DetailsTabPanel', {
    mixinId: 'custompage-mixin',

    statics: {
        actions: {
            attachments: 'attachments',
            clone: 'clone',
            create: 'new',
            edit: 'edit',
            emails: 'emails',
            history: 'history',
            masterdetail: 'details',
            notes: 'notes',
            relations: 'relations',
            view: 'view'
        }
    },

    /**
     * @return {Object} Object form base config
     */
    getObjectFormBaseConfig: function () {
        return {
            bodyPadding: 10,
            reference: this._objectFormReference,
            itemId: this._objectFormReference
        };
    },

    /**
     * @return {Ext.model.Model}
     */
    getFormObject: function () {
        var panel = this.lookup(this._objectFormReference);
        if (panel) {
            return panel.lookupViewModel().get("theObject");
        }
    },

    /**
     * @return {Ext.model.Model}
     */
    getFormMode: function () {
        var panel = this.lookup(this._objectFormReference);
        if (panel) {
            return panel.tabAction;
        }
    },

    /**
     * Update emails from templates
     * @param {function} callback
     */
    updateEmails: function (callback) {
        var panel = this.lookup(this._emailReference);
        if (panel) {
            panel.getController().updateEmailsFromTemplates(callback, true);
        } else if (callback) {
            Ext.callback(callback, null, [true]);
        }
    },

    privates: {
        /**
         * @property {_objectFormReference}
         */
        _objectFormReference: 'objectForm',

        /**
         * @property {_emailReference}
         */
        _emailReference: 'emailGrid'
    }
});
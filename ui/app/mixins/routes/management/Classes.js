Ext.define('CMDBuildUI.mixins.routes.management.Classes', {
    mixinId: 'managementroutes-classes-mixin',

    /******************* CARDS GRID ********************/
    /**
     * Before show cards grid
     * 
     * @param {String} className
     * @param {Object} action
     */
    onBeforeShowCardsGrid: function (className, action) {
        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
        CMDBuildUI.util.helper.ModelHelper.getModel(type, className).then(function (model) {
            if (CMDBuildUI.util.Navigation.checkCurrentContext(type, className)) {
                action.stop();
            } else {
                action.resume();
            }
        }, function () {
            action.stop();
        });
    },
    /**
     * Show cards grid
     * 
     * @param {String} className
     * @param {Numeric} cardId This attribute is used when the function
     * is called dicretly from code, not from router.
     */
    showCardsGrid: function (className, cardId) {
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
        CMDBuildUI.util.Navigation.addIntoManagemenetContainer('classes-cards-grid-container', {
            objectTypeName: className,
            maingrid: true,
            viewModel: {
                data: {
                    selectedId: cardId
                },
                stores: {
                    cards: {
                        model: CMDBuildUI.util.helper.ModelHelper.getModelName('class', className),
                        autoLoad: true,
                        type: 'classes-cards'
                    }
                }
            }
        });

        // fire global event objecttypechanged
        Ext.GlobalEvents.fireEventArgs("objecttypechanged", [className]);

        // update current context
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            className
        );
    },

    /******************* CARD ********************/
    /**
     * Before show card view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     * @param {Object} action
     */
    onBeforeShowCard: function (className, cardId, action) {
        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
        CMDBuildUI.util.helper.ModelHelper.getModel(type, className).then(function (model) {
            if (CMDBuildUI.util.Navigation.checkCurrentContext(type, className, true)) {
                action.stop();
            } else {
                action.resume();
            }
        }, function () {
            action.stop();
        });
    },
    /**
     * Show card view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCard: function (className, cardId) {
        this.showCardsGrid(className, cardId);
    },

    /******************* CARD VIEW ********************/
    /**
     * Before show card view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     * @param {Object} action
     */
    onBeforeShowCardWindow: function (className, cardId, action) {
        var me = this;

        // fix variables for create form
        if (!action) {
            action = cardId;
            cardId = null;
        }

        // load model
        var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
        CMDBuildUI.util.helper.ModelHelper.getModel(type, className).then(function (model) {
            // check consisntence of main content
            if (!CMDBuildUI.util.Navigation.checkCurrentContext(type, className, true)) {
                // show cards grid for className
                me.showCardsGrid(className, cardId);
            }
            // resume action
            action.resume();
        }, function () {
            action.stop();
        });
    },
    /**
     * Show card view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardView: function (className, cardId) {
        this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.view);
    },

    /**
     * Show details view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardDetails: function (className, cardId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.card_tab_detail_access) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.masterdetail);
        } else {
            this.redirectTo(Ext.String.format("classes/{0}/cards/{1}"), className, cardId);
        }
    },

    /**
     * Show notes view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardNotes: function (className, cardId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.card_tab_note_access) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.notes);
        } else {
            this.redirectTo(Ext.String.format("classes/{0}/cards/{1}"), className, cardId);
        }
    },

    /**
     * Show relation view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardRelations: function (className, cardId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.card_tab_relation_access) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.relations);
        } else {
            this.redirectTo(Ext.String.format("classes/{0}/cards/{1}"), className, cardId);
        }
    },

    /**
     * Show history view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardHistory: function (className, cardId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.card_tab_history_access) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.history);
        } else {
            this.redirectTo(Ext.String.format("classes/{0}/cards/{1}"), className, cardId);
        }
    },

    /**
     * Show emails view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardEmails: function (className, cardId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (privileges.card_tab_email_access) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.emails);
        } else {
            this.redirectTo(Ext.String.format("classes/{0}/cards/{1}"), className, cardId);
        }
    },

    /**
     * Show relation view
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardAttachments: function (className, cardId) {
        var privileges = CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get("rolePrivileges");
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled) && privileges.card_tab_attachment_access) {
            this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.attachments);
        } else {
            this.redirectTo(Ext.String.format("classes/{0}/cards/{1}"), className, cardId);
        }
    },

    /**
     * Show card edit
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardEdit: function (className, cardId) {
        this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.edit);
    },

    /**
     * Show card clone
     * 
     * @param {String} className
     * @param {Numeric} cardId
     */
    showCardClone: function (className, cardId) {
        this.showCardTabPanel(className, cardId, CMDBuildUI.mixins.DetailsTabPanel.actions.clone);
    },

    /**
     * Show card create
     * 
     * @param {String} className
     */
    showCardCreate: function (className) {
        this.showCardTabPanel(className, null, CMDBuildUI.mixins.DetailsTabPanel.actions.create);
    },

    privates: {
        /**
         * Show card tab panel
         * @param {String} className 
         * @param {Number} cardId 
         * @param {String} action 
         */
        showCardTabPanel: function (className, cardId, action) {
            if (!CMDBuildUI.util.Navigation.checkCurrentManagementContextAction(action)) {
                CMDBuildUI.util.Navigation.addIntoManagementDetailsWindow('classes-cards-tabpanel', {
                    viewModel: {
                        data: {
                            objectTypeName: className,
                            objectId: cardId,
                            action: action
                        }
                    }
                });
                CMDBuildUI.util.Navigation.updateCurrentManagementContextAction(action);
            }
        }
    }
});
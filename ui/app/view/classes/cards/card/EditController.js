Ext.define('CMDBuildUI.view.classes.cards.card.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-card-edit',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            beforedestroy: 'onBeforeDestroy'
        },
        '#savebtn': {
            click: 'onSaveBtnClick'
        },
        '#saveandclosebtn': {
            click: 'onSaveAndCloseBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.card.EditController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var me = this;

        /**
         * Promise success callback.
         * @param {CMDBuildUI.model.classes.Card} model 
         */
        function success(model) {
            vm.set("objectModel", model);

            // set instance to ViewModel
            vm.linkTo('theObject', {
                type: model.getName(),
                id: view.getObjectId() || vm.get("objectId")
            });
        }

        // get model 
        CMDBuildUI.util.helper.ModelHelper
            .getModel('class', view.getObjectTypeName() || vm.get("objectTypeName"))
            .then(success);

        // bind theObject to add form
        vm.bind({
            bindTo: {
                cardmodel: '{theObject._model}',
                objectmodel: '{objectModel}'
            }
        }, function (data) {
            if (data.cardmodel && data.objectmodel) {
                function redirectToView() {
                    me.redirectTo(Ext.String.format(
                        'classes/{0}/cards/{1}/view',
                        view.getObjectTypeName() || vm.get("objectTypeName"),
                        view.getObjectId() || vm.get("objectId")
                    ));
                }
                if (data.cardmodel[CMDBuildUI.model.base.Base.permissions.edit]) {
                    vm.get("theObject").addLock().then(function (success) {
                        if (success) {
                            me._isLocked = true;
                            // add fields
                            view.add(view.getMainPanelForm(view.getDynFormFields()));

                            // add conditional visibility rules
                            view.addConditionalVisibilityRules();

                            // add auto value rules
                            view.addAutoValueRules();

                            // validate form before edit
                            Ext.asap(function () {
                                view.isValid();
                            });
                        } else {
                            redirectToView();
                        }
                    });
                } else {
                    redirectToView();
                }
            }
        });

        this.initBeforeEditFormTriggers();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = this.getView();
        this.saveForm(function (record) {
            if (form.getRedirectAfterSave()) {
                // redirect to card view
                me.redirectTo(Ext.String.format("classes/{0}/cards/{1}/view", record.get("_type"), record.getId()));
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndCloseBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = this.getView();
        this.saveForm(function (record) {
            if (form.getRedirectAfterSave()) {
                // close detail window
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
                // redirect to card in grid
                me.redirectTo(Ext.String.format("classes/{0}/cards/{1}", record.get("_type"), record.getId()));
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        // discard changes
        vm.get("theObject").reject();
        // close detail window
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
    },

    /**
     * Unlock card on management details window close.
     * @param {CMDBuildUI.view.classes.cards.card.Edit} view 
     * @param {Object} eOpts 
     */
    onBeforeDestroy: function (view, eOpts) {
        if (this._isLocked) {
            this.getViewModel().get("theObject").removeLock();
        }
    },

    privates: {
        /**
         * Initialize before create form triggers.
         */
        initBeforeEditFormTriggers: function () {
            this.getView().initBeforeActionFormTriggers(
                CMDBuildUI.model.classes.Class.formtriggeractions.beforeEdit,
                CMDBuildUI.util.api.Client.getApiForFormBeforeEdit()
            );
        },

        /**
         * Execute after create form triggers.
         * 
         * @param {CMDBuildUI.model.classes.Card} record
         * @param {Object} originalData
         */
        executeAfterEditFormTriggers: function (record, originalData) {
            record.oldData = originalData;
            if (this.getView()) {
                this.getView().executeAfterActionFormTriggers(
                    CMDBuildUI.model.classes.Class.formtriggeractions.afterEdit,
                    record,
                    CMDBuildUI.util.api.Client.getApiForFormAfterEdit()
                );
            }
        },

        /**
         * Save data
         * @param {function} callback 
         */
        saveForm: function (callback) {
            var me = this;
            var form = this.getView();
            var vm = form.lookupViewModel();

            if (form.isValid()) {
                var originalData = vm.get("theObject").getOriginalDataForChangedFields();
                vm.get("theObject").save({
                    success: function (record, operation) {
                        // execute after create form triggers
                        me.executeAfterEditFormTriggers(record, originalData);

                        // fire global card update event
                        if (form.getFireGlobalEventsAfterSave()) {
                            Ext.GlobalEvents.fireEventArgs("cardupdated", [record]);
                        }

                        // execute callback
                        if (Ext.isFunction(callback)) {
                            Ext.callback(callback, me, [record]);
                        }
                    }
                });
            }
        }
    }

});
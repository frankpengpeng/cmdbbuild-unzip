Ext.define('CMDBuildUI.view.classes.cards.card.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-card-create',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
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
     * @param {CMDBuildUI.view.classes.cards.card.Create} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var me = this;
        var isCloneAction = view.getCloneObject() && vm.get("objectId") ? true : false;

        // get model 
        CMDBuildUI.util.helper.ModelHelper
            .getModel('class', vm.get("objectTypeName"))
            .then(function (model) {
                vm.set("objectModel", model);

                if (isCloneAction) {
                    model.load(vm.get("objectId"), {
                        callback: function (record, operation, success) {
                            var create = true;
                            if (success) {
                                create = record.getData();
                            }
                            me.linkObject(view, vm, model.getName(), create);
                        }
                    });
                } else {
                    me.linkObject(view, vm, model.getName(), true);
                }

            });

        if (isCloneAction) {
            this.initBeforeCloneFormTriggers();
        } else {
            this.initBeforeCreateFormTriggers();
        }
    },

    /**
     * Save button click
     * 
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = this.getView();
        me.saveForm(function (record) {
            if (form.getRedirectAfterSave()) {
                // redirect to the card
                me.redirectTo(Ext.String.format("classes/{0}/cards/{1}/view", record.get("_type"), record.getId()));
            }
        });
    },

    /**
     * Save and close button click
     * 
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndCloseBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = this.getView();
        me.saveForm(function (record) {
            if (form.getRedirectAfterSave()) {
                // close detail window
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
                // redirect to the card
                me.redirectTo(Ext.String.format("classes/{0}/cards/{1}", record.get("_type"), record.getId()));
            }
        });
    },

    /**
     * Close creation window
     */
    onCancelBtnClick: function () {
        // close detail window
        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
    },

    privates: {
        linkObject: function (view, vm, modelname, data) {
            // create new instance
            vm.linkTo('theObject', {
                type: modelname,
                create: data
            });

            // get form fields
            view.add(view.getMainPanelForm(view.getDynFormFields()));

            // add conditional visibility rules
            view.addConditionalVisibilityRules();

            // add auto value rules
            view.addAutoValueRules();

            // validate form before edit
            Ext.asap(function() {
                view.isValid();
            });
        },

        /**
         * Initialize before create form triggers.
         */
        initBeforeCreateFormTriggers: function () {
            this.getView().initBeforeActionFormTriggers(
                CMDBuildUI.model.classes.Class.formtriggeractions.beforeInsert,
                CMDBuildUI.util.api.Client.getApiForFormBeforeCreate()
            );
        },
        /**
         * Initialize before create form triggers.
         */
        initBeforeCloneFormTriggers: function () {
            this.getView().initBeforeActionFormTriggers(
                CMDBuildUI.model.classes.Class.formtriggeractions.beforeClone,
                CMDBuildUI.util.api.Client.getApiForFormBeforeClone()
            );
        },

        /**
         * Execute after create form triggers.
         * 
         * @param {CMDBuildUI.model.classes.Card} record
         */
        executeAfterCreateFormTriggers: function (record) {
            this.getView().executeAfterActionFormTriggers(
                CMDBuildUI.model.classes.Class.formtriggeractions.afterInsert,
                record,
                CMDBuildUI.util.api.Client.getApiForFormAfterCreate()
            );
        },

        /**
         * Execute after create form triggers.
         * 
         * @param {CMDBuildUI.model.classes.Card} record
         */
        executeAfterCloneFormTriggers: function (record) {
            this.getView().executeAfterActionFormTriggers(
                CMDBuildUI.model.classes.Class.formtriggeractions.afterClone,
                record,
                CMDBuildUI.util.api.Client.getApiForFormAfterClone()
            );
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
                vm.get("theObject").save({
                    success: function (record, operation) {
                        if (!record.getRecordType()) {
                            record.set('_type', form.getObjectTypeName());
                        }

                        if (form.getCloneObject()) {
                            me.executeAfterCloneFormTriggers(record);
                        } else {
                            // execute after create form triggers
                            me.executeAfterCreateFormTriggers(record);
                        }

                        // fire global card created event
                        if (form.getFireGlobalEventsAfterSave()) {
                            Ext.GlobalEvents.fireEventArgs("cardcreated", [record]);
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

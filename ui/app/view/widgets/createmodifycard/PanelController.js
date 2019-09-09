Ext.define('CMDBuildUI.view.widgets.createmodifycard.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-createmodifycard-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addcardbtn': {
            beforerender: 'onAddCardBtnBeforeRender'
        },
        '#widgetclosebtn': {
            click: 'onWidgetCloseBtnClick'
        },
        '#widgetsavebtn': {
            click: 'onWidgetSaveBtnClick'
        }
    },


    /************************************************************************************************************** 
     *
     *                                          WIDGET: CreateModifyCard
     *
     *
     * EVENTS:
     *  onBeforeRender              (view, eOpts)                          --> render view with selected object
     *  onAddCardBtnBeforeRender    (button, eOpts)                        --> manage addcard button with subclasses
     *  onWidgetCloseBtnClick       (button, e, eOpts)                     --> close popup
     *  onWidgetSaveBtnClick        (button, e, eOpts)                     --> save object if form is valid
     *
     * UTILS:
     *  extractVariableFromString   (variable, theTarget)                  --> serialize parameteres
     *  addFormToContainer          (classname, cardid, mode)              --> add form to popup
     *  getFormButtons              ()                                     --> get buttons configuration for 
     *                                                                         create and edit forms
     *
     * ************************************************************************************************************/


    /**
     * @param {CMDBuildUI.view.widgets.createmodifycard.PanelController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = view.lookupViewModel();
        var theWidget = vm.get('theWidget');
        var theTarget = vm.get('theTarget');
        // read only parameter
        var readonly = theWidget.get("readonly") !== undefined ? theWidget.get("readonly") : false;
        readonly = theWidget.get("ReadOnly") !== undefined ? theWidget.get("ReadOnly") : readonly;
        // get object type and id
        var objectId, objectTypeName;
        if (theWidget.get("ClassName")) {
            objectTypeName = theWidget.get("ClassName");
            if (theWidget.get("ObjId")) {
                var objId = theWidget.get("ObjId");
                var theIdObj = me.extractVariableFromString(objId, theTarget);
                objectId = Object.values(theIdObj).pop();
            }
        } else if (theWidget.get("Reference")) {
            var targetFieldName = theWidget.get("Reference");
            var refDefinition = theTarget.getField(targetFieldName);
            if (!refDefinition) {
                targetFieldName = me.getView().getOutput();
                refDefinition = theTarget.getField(targetFieldName);
            }
            if (refDefinition) {
                objectId = theTarget.get(targetFieldName);
                objectTypeName = refDefinition.attributeconf.targetClass;
            }
        }

        if (!objectTypeName) {
            CMDBuildUI.util.Logger.log("Widget configuration error", CMDBuildUI.util.Logger.levels.error, null, theWidget.getData());
            vm.set('addbtn.hidden', true);
            return this.getView().setHtml('<div style="margin-left:10px"> Widget configuration error </div>');
        }

        var mode;
        if (readonly) {
            mode = CMDBuildUI.util.helper.FormHelper.formmodes.read;
        } else if (objectId) {
            mode = CMDBuildUI.util.helper.FormHelper.formmodes.update;
        } else {
            mode = CMDBuildUI.util.helper.FormHelper.formmodes.create;
        }

        var klass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(objectTypeName);
        if (klass) {
            vm.set("klassdescription", klass.getTranslatedDescription());
            if (klass.get("prototype")) {
                if (mode == CMDBuildUI.util.helper.FormHelper.formmodes.create) {
                    vm.set("addbtn.disabled", false);
                } else {
                    // get submodel
                    CMDBuildUI.util.helper.ModelHelper.getModel(
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, // 'class'
                        objectTypeName
                    ).then(function (model) {
                        model.load(objectId, {
                            success: function (record) {
                                me.addFormToContainer(record.get("_type"), record.getId(), mode);
                            }
                        });
                    });
                }
            } else {
                this.addFormToContainer(objectTypeName, objectId, mode);
            }
        } else {
            CMDBuildUI.util.Logger.log("Class not found");
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddCardBtnBeforeRender: function (button, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        var classname = vm.get('theWidget').get("ClassName");
        this.getView().updateAddButton(
            button,
            function (item, event, eOpts) {
                vm.set("addbtn.disabled", false);
                me.addFormToContainer(item.objectTypeName, undefined, CMDBuildUI.util.helper.FormHelper.formmodes.create);
            },
            classname
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onWidgetCloseBtnClick: function (button, e, eOpts) {
        button.lookupViewModel().get("theObject").reject();
        this.getView().fireEvent("popupclose");
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onWidgetSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        this.form.getController().saveForm(function (record) {
            var wconf = vm.get('theWidget');
            if (wconf.get("Reference") || me.getView().getOutput())  {
                vm.get("theTarget").set(me.getView().getOutput(), record.getId()); // TODO: will become theWidget.get("Reference")
            }
            me.getView().fireEvent("popupclose");
        });
    },

    privates: {
        /**
         * @property
         */
        form: null,

        /**
         * Custom configuration parameters for this widget
         */
        parameters: {
            ReadOnly: "ReadOnly",
            ClassName: "ClassName",
            ObjId: "ObjId",
            Reference: "Reference"
        },

        /**
         * 
         * @param {String} classname 
         * @param {Number} cardid 
         * @param {String} mode 
         */
        addFormToContainer: function (classname, cardid, mode) {
            var config;
            if (mode === CMDBuildUI.util.helper.FormHelper.formmodes.update) {
                config = {
                    xtype: 'classes-cards-card-edit',
                    buttons: this.getFormButtons()
                };
            } else if (mode === CMDBuildUI.util.helper.FormHelper.formmodes.create) {
                config = {
                    xtype: 'classes-cards-card-create',
                    fireGlobalEventsAfterSave: false,
                    defaultValues: this.getDefaultValues(),
                    buttons: this.getFormButtons()
                };
            } else {
                config = {
                    xtype: 'classes-cards-card-view',
                    objectTypeName: classname,
                    objectId: cardid,
                    shownInPopup: true,
                    tabpaneltools: []
                };
            }
            Ext.apply(config, {
                bodyPadding: "0 10",
                viewModel: {
                    data: {
                        objectTypeName: classname,
                        objectId: cardid
                    }
                }
            });

            this.form = this.getView().add(config);
        },

        /**
         * get defaults values
         */
        getDefaultValues: function () {
            var me = this;
            var vm = this.getViewModel();
            var theWidget = vm.get('theWidget');
            var theTarget = vm.get("theTarget");
            var data = theWidget.getData();
            var defaults = [];
            // get default values
            for (var key in data) {
                // check that key is not system key or configuration parameter
                if (!Ext.String.startsWith(key, "_") && !this.parameters[key]) {
                    defaults.push({
                        attribute: key,
                        value: me.extractVariableFromString(data[key], theTarget)
                    });
                }
            }
            defaultValues = me.cleanDefaultValues(defaults);
            return defaultValues;
        },

        /**
         * @param {Array} defaultValues
         * @return {Array} The cleaned array.
         * clean default values
         */
        cleanDefaultValues: function (defaultValues) {
            var cleanedValues = [];
            defaultValues.forEach(function (defaultvalue) {
                var theValue = defaultvalue.value;

                if (Ext.isObject(theValue)) {
                    defaultvalue.value = Object.values(theValue).pop();
                }
                cleanedValues.push(defaultvalue);
            });
            return cleanedValues;
        },



        /**
         * Resolve variable.
         * @param {String} variable
         * @param {CMDBuildUI.model.base.Base} theTarget 
         * @return {*} The variable resolved.
         */
        extractVariableFromString: function (variable, theTarget) {
            variable = variable.replace("{", "").replace("}", "");
            var s_variable = variable.split(":");
            if (s_variable[0] === "server") {
                return CMDBuildUI.util.ecql.Resolver.resolveServerVariables([s_variable[1]], theTarget);
            } else if (s_variable[0] === "client") {
                return CMDBuildUI.util.ecql.Resolver.resolveClientVariables([s_variable[1]], theTarget);
            } else if (s_variable.length === 1 && theTarget.getField(s_variable[0])) {
                return theTarget.get(s_variable[0]);
            }
            return variable;
        },

        /**
         * Return buttons configuration.
         * @return {Object[]}
         */
        getFormButtons: function () {
            return [{
                ui: 'management-action',
                reference: 'widgetsavebtn',
                itemId: 'widgetsavebtn',
                text: CMDBuildUI.locales.Locales.common.actions.save,
                autoEl: {
                    'data-testid': 'widgets-createmodifycard-save'
                },
                formBind: true,
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.save'
                }
            }, {
                ui: 'secondary-action',
                reference: 'widgetclosebtn',
                itemId: 'widgetclosebtn',
                text: CMDBuildUI.locales.Locales.common.actions.close,
                autoEl: {
                    'data-testid': 'widgets-createmodifycard-close'
                },
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.actions.close'
                }
            }];
        }
    }
});
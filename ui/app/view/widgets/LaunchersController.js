Ext.define('CMDBuildUI.view.widgets.LaunchersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-launchers',

    control: {
        '#': {
            widgetschanged: 'onWidgetsChanged',
            widgetbuttonclick: 'onWidgetButtonClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.widgets.Launchers} view
     * @param {Ext.data.Store} newvalue
     * @param {Ext.data.Store} oldvalue
     */
    onWidgetsChanged: function (view, newvalue, oldvalue) {
        var me = this;
        if (newvalue && newvalue.getData().length) {
            Ext.Array.each(newvalue.getRange(), function (widget, index) {
                if (widget.get("_active")) {
                    // add value binding
                    var binding;
                    if (widget.get("_output")) {
                        binding = {
                            value: '{theObject.' + widget.get("_output") + '}'
                        };
                    }
                    var wconf = {
                        xtype: 'widgets-button',
                        reference: 'widgetbutton_' + index,
                        text: widget.get("_label") + (widget.get("_required") ? ' *' : ''),
                        disabled: view.getFormMode() === CMDBuildUI.util.helper.FormHelper.formmodes.read && !widget.get("_alwaysenabled"),
                        required: widget.get("_required"),
                        bind: binding,
                        handler: function (button, e) {
                            view.fireEvent("widgetbuttonclick", view, button, widget, e);
                        }

                    };

                    if (widget.get("_type") === 'manageEmail') {
                        me.getParentTabPanel().lookupViewModel().set("emailtemplatestoevaluate", me.extractEmailTemplatesFromWidgetConf(widget));
                    } else if (widget.get("_type") === 'linkCards') {
                        var theObj = view.lookupViewModel().get("theObject");
                        if (!theObj.get(widget.get("_output")) && widget.get("_output")) {
                            view.lookupViewModel().get("theObject").set(widget.get("_output"), []);
                        }
                        CMDBuildUI.view.widgets.linkcards.Panel.loadDefaults(widget, view.lookupViewModel().get("theObject"), function (records) {
                            var defaults = [];
                            records.forEach(function (r) {
                                defaults.push({
                                    _id: r.getId()
                                });
                            });
                            if (view.lookupViewModel().get('theObject')) {
                                view.lookupViewModel().get("theObject").set(widget.get("_output"), defaults);
                            }
                            if (defaults.length) {
                                var widgetButton = view.lookupReference('widgetbutton_' + index);
                                if (widgetButton) {
                                    widgetButton.fireEvent('validitychange', widgetButton, true);
                                }
                            }
                        });
                    } else if (widget.get('_type') === 'customForm') {
                        if (!Ext.ClassManager.isCreated(me.getModelName(widget))) {
                            CMDBuildUI.view.widgets.customform.Utilities.getModel(widget);
                        }

                        CMDBuildUI.view.widgets.customform.Utilities.loadData(widget, me.getViewModel().get("theObject"), function (response) {
                            var res = CMDBuildUI.view.widgets.customform.Utilities.serialize(widget, response);
                            view.lookupViewModel().get("theObject").set(widget.get("_output"), res);
                        });
                    }

                    try {
                        // add widget button
                        view.add(wconf);
                    } catch (e) {
                        CMDBuildUI.util.Logger.log(
                            "Malformed widget configuration.",
                            CMDBuildUI.util.Logger.levels.warn,
                            null,
                            wconf
                        );
                    }
                }
            });

            // show panel
            this.getViewModel().set("hideLaunchersPanel", false);
        }
    },

    /**
     * Return the name of the model used by the widget.
     * @return {String}
     */
    getModelName: function (theWidget) {
        return 'CMDBuildUI.model.customform.' + theWidget.getId();
    },

    /**
     * @param {Ext.Component} view
     * @param {Ext.button.Button} button
     * @param {CMDBuildUI.model.WidgetDefinition} widget
     * @param {Event} e
     * @param {Object} eOpts
     */
    onWidgetButtonClick: function (view, button, widget, e, eOpts) {
        // update ajax action id
        CMDBuildUI.util.Ajax.setActionId(Ext.String.format(
            'widget.open.{0}.{1}',
            widget.get("_type"),
            widget.getId()
        ));

        var popup;
        var widgettype = widget.get("_type");
        var xtype = CMDBuildUI.util.Config.widgets[widgettype];
        if (!xtype) {
            Ext.Msg.alert(
                'Warning',
                Ext.String.format('Widget <strong>{0}</strong> not implemented!', widgettype) // TODO: translate
            );
            return;
        }

        if (widgettype === 'openAttachment' && !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.enabled)) {
            Ext.Msg.alert(
                'Warning',
                Ext.String.format('Attachments disabled!')
            );
            return;
        } else if (widgettype === 'openAttachment') {
            this.getParentTabPanel().setActiveItem('tab-attachments');
            return;
        } else if (widgettype === 'manageEmail') {
            this.getParentTabPanel().setActiveItem('tab-emails');
            return;
        } else if (widgettype === 'openNote') {
            this.getParentTabPanel().setActiveItem('tab-notes');
            return;
        }

        // create widget configuration
        var config = {
            xtype: xtype,
            widgetId: widget.getId(),
            output: widget.get("_output"),
            _widgetOwner: view,
            viewModel: {
                data: {
                    theWidget: widget,
                    theTarget: this.getViewModel().get("theObject")
                }
            },
            bind: {
                target: '{theTarget}'
            },
            listeners: {
                /**
                 * Custom event to close popup directly from widget
                 */
                popupclose: function (eOpts) {
                    popup.close();
                }
            }
        };

        // custom panel listeners
        var listeners = {
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            beforeclose: function (panel, eOpts) {
                panel.removeAll(true);
            },
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                button.fireEvent('validitychange', button, button.isValid());
            }
        };
        // open popup
        popup = CMDBuildUI.util.Utilities.openPopup(
            'popup-show-widget',
            widget.get("_label"),
            config,
            listeners
        );
    },

    privates: {
        /**
         * 
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @return {Object[]} Array of template definitions.
         */
        extractEmailTemplatesFromWidgetConf: function (widget) {
            var templates = [];
            var expression = /^Template(\d*?)$/;
            for (var k in widget.getData()) {
                var matched = expression.exec(k);
                if (matched) {
                    templates.push({
                        name: widget.get('Template' + matched[1]),
                        condition: widget.get('Condition' + matched[1]),
                        notifywith: widget.get('NotifyWith' + matched[1])
                    });
                }
            }
            return templates;
        },

        /**
         * @return {Ext.tab.Panel}
         */
        getParentTabPanel: function () {
            var tabpanel;
            var view = this.getView();
            var vm = view.lookupViewModel();
            if (vm.get("objectType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass) {
                tabpanel = view.up("classes-cards-tabpanel");
            } else if (vm.get("objectType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                tabpanel = view.up("processes-instances-tabpanel");
            }
            return tabpanel;
        }
    }
});
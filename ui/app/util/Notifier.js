Ext.define("CMDBuildUI.util.Notifier", {
    singleton: true,

    icons: {
        error: 'fa-times-circle',
        info: 'fa-info-circle',
        success: 'fa-check-circle',
        warning: 'fa-exclamation-circle'
    },

    /**
     * Show toast message
     * 
     * @param {String} message (required) The content of the message box. Required.
     * @param {Object} options Message configuration.
     * @param {String} options.title The title of the message box.
     * @param {String} options.icon The icont to display in message box header. 
     *                             Enter the FontAwesome class.
     * @param {String} options.ui Specify the ui for message box.
     * @param {String} options.details An optional text with more details.
     */
    showMessage: function (message, options) {
        if (message) {
            var w;
            options = Ext.applyIf(options, {
                title: CMDBuildUI.locales.Locales.notifier.info,
                icon: this.icons.info,
                ui: 'default'
            });
            var config = {
                title: Ext.String.format('<span data-testid="message-window-title">{0}</span>', options.title),
                iconCls: 'x-fa ' + options.icon,
                html: Ext.String.format('<span data-testid="message-window-text">{0}</span>', message),
                width: 200,
                align: 'br',
                ui: options.ui,
                autoEl: {
                    'data-testid': 'message-window'
                }
            };

            if (options.icon === this.icons.error) {
                config.autoClose = false;
                config.closable = true;
            }

            if (options.details) {
                config.bbar = [{
                    xtype: 'component',
                    flex: 1
                }, {
                    xtype: 'tool',
                    type: 'help',
                    callback: function (owner, tool, event) {
                        CMDBuildUI.util.Utilities.openPopup(
                            null,
                            options.title, {
                                xtype: 'panel',
                                cls: 'x-selectable',
                                html: options.details,
                                scrollable: true,
                                bodyPadding: 10
                            },
                            null, {
                                width: '50%',
                                height: '50%'
                            }
                        );
                        w.close();
                    }
                }];
            }

            w = Ext.create('Ext.window.Toast', config);
            w.show();
        }
    },

    /**
     * Show success message
     * 
     * @param {String} message
     * @param {String} code
     * @param {String} ui
     */
    showSuccessMessage: function (message, code, ui, details) {
        var fullmessage = message;
        if (code) {
            fullmessage = Ext.String.format("{0} - {1}", code, message);
        }
        CMDBuildUI.util.Notifier.showMessage(fullmessage, {
            title: CMDBuildUI.locales.Locales.notifier.success,
            icon: CMDBuildUI.util.Notifier.icons.success,
            ui: ui,
            details: details
        });
        CMDBuildUI.util.Logger.log(message, CMDBuildUI.util.Logger.levels.log, code);
    },

    /**
     * Show info message
     * 
     * @param {String} message
     * @param {String} code
     * @param {String} ui
     */
    showInfoMessage: function (message, code, ui, details) {
        var fullmessage = message;
        if (code) {
            fullmessage = Ext.String.format("{0} - {1}", code, message);
        }
        CMDBuildUI.util.Notifier.showMessage(fullmessage, {
            title: CMDBuildUI.locales.Locales.notifier.info,
            icon: CMDBuildUI.util.Notifier.icons.info,
            ui: ui,
            details: details
        });
        CMDBuildUI.util.Logger.log(message, CMDBuildUI.util.Logger.levels.log, code);
    },

    /**
     * Show warning message
     * 
     * @param {String} message
     * @param {String} code
     * @param {String} ui
     */
    showWarningMessage: function (message, code, ui, details) {
        var fullmessage = message;
        if (code) {
            fullmessage = Ext.String.format("{0} - {1}", code, message);
        }
        CMDBuildUI.util.Notifier.showMessage(fullmessage, {
            title: CMDBuildUI.locales.Locales.notifier.warning,
            icons: CMDBuildUI.util.Notifier.icons.warning,
            ui: ui,
            details: details
        });
        CMDBuildUI.util.Logger.log(message, CMDBuildUI.util.Logger.levels.warn, code);
    },

    /**
     * Show error message
     * 
     * @param {String} message
     * @param {String} code
     * @param {String} ui
     * @param {String} details
     */
    showErrorMessage: function (message, code, ui, details) {
        var fullmessage = message;
        if (code) {
            fullmessage = Ext.String.format("{0} - {1}", code, message);
        }
        CMDBuildUI.util.Notifier.showMessage(fullmessage, {
            title: CMDBuildUI.locales.Locales.notifier.error,
            icon: CMDBuildUI.util.Notifier.icons.error,
            ui: ui,
            details: details
        });
        CMDBuildUI.util.Logger.log(message, CMDBuildUI.util.Logger.levels.error, code);
        if (details) {
            CMDBuildUI.util.Logger.log(details, CMDBuildUI.util.Logger.levels.error);
        }
    }
});
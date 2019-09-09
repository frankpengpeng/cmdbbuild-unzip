Ext.define('CMDBuildUI.util.Ajax', {
    singleton: true,
    /**
     * Pending counter for automated test
     */
    currentPendingCount: 0,
    currentPendingDebug: false,

    /**
     * Initialize singleton
     */
    init: function () {
        this.initBeforeRequest();
        this.initRequestComplete();
        this.initRequestException();
    },

    /**
     * Initialize beforerequest event handler
     */
    initBeforeRequest: function () {
        /**
         * Fired before a network request is made to retrieve a data object.
         * 
         * @param {Ext.data.Connection} conn
         * @param {Object} options
         * @param {Object} eOpts
         */
        Ext.Ajax.on('beforerequest', function (conn, options, eOpts) {
            this.currentPending('add');
            // set headers
            var headers = {
                'Content-Type': "application/json",
                'CMDBuild-ActionId': CMDBuildUI.util.Ajax.getActionId(),
                'CMDBuild-RequestId': CMDBuildUI.util.Utilities.generateUUID(),
                'CMDBuild-View': CMDBuildUI.util.Ajax.getViewContext()
            };

            // add authentication token
            var token = CMDBuildUI.util.helper.SessionHelper.getToken();
            if (token) {
                headers['CMDBuild-Authorization'] = token;
            }

            // add localization
            var localization = CMDBuildUI.util.helper.SessionHelper.getLanguage();
            if (localization) {
                headers['CMDBuild-Localized'] = true;
                headers['CMDBuild-Localization'] = localization;
            }

            //remove _id from PUT request
            if (options.method === 'PUT') {
                Ext.Array.each(options.records, function (value, index) {
                    if (options.jsonData && options.jsonData.hasOwnProperty('_id')) {
                        delete options.jsonData._id;
                    }
                });
            }

            // merge options with custom headers
            Ext.merge(options, {
                headers: headers
            });
        }, this);
    },

    /**
     * Initialize requestcomplete event handler
     */
    initRequestComplete: function () {
        /**
         * Fired if the request was successfully completed.
         * 
         * @param {Ext.data.Connection} conn
         * @param {Object} response
         * @param {Object} options
         * @param {Object} eOpts
         */
        Ext.Ajax.on('requestcomplete', function (conn, response, options, eOpts) {
            this.currentPending('sub');
            this.showMessages(response, options);
        }, this);
    },

    /**
     * Initialize requestexception event handler
     */
    initRequestException: function () {
        /**
         * Fired if an error HTTP status was returned from the server.
         * 
         * @param {Ext.data.Connection} conn
         * @param {Object} response
         * @param {Object} options
         * @param {Object} eOpts
         */
        Ext.Ajax.on('requestexception', function (conn, response, options, eOpts) {
            this.currentPending('sub');
            if (response.status === 401 && !CMDBuildUI.util.Ajax.sessionexpired) {
                // Cmdb.Logger.debug("Got unauthorized (401) status from server, check session...");
                CMDBuildUI.util.helper.SessionHelper.checkSessionValidity().then(function (token) {
                    CMDBuildUI.util.Ajax.sessionexpired = false;
                    CMDBuildUI.util.Logger.log("You cannot access this resource.", CMDBuildUI.util.Logger.levels.warn, 401);
                }, function (err) {
                    CMDBuildUI.util.Ajax.sessionexpired = true;
                    CMDBuildUI.util.helper.SessionHelper.setSessionIntoViewport();
                    if (!CMDBuildUI.util.helper.SessionHelper.getStartingUrl()) {
                        CMDBuildUI.util.helper.SessionHelper.updateStartingUrlWithCurrentUrl();
                    }
                    if (CMDBuildUI.util.Ajax.getActionId() !== "login") {
                        CMDBuildUI.util.Utilities.redirectTo("login", true);
                    }
                });
            } else if (response.status !== 401) {
                this.showMessages(response, options);
            }
        }, this);
    },

    /** 
     * 
     * Get Javascript global variable tracking the number of pending http requests from client (issue #710)
     * 
     * @global CMDBuildUI.util.Ajax.currentPending(null|add|sub|subtract|enable-debug|disable-debug|reset); // return [0-9]
     * 
     * @param {String} operation 
     * @returns {Number} currentPendingCount
     */
    currentPending: function (operation) {
        switch (operation) {
            case 'add':
                this.currentPendingCount++;
                break;
            case 'sub':
            case 'subtract':
                this.currentPendingCount--;
                break;
            case 'enable-debug':
                this.currentPendingDebug = true;
                break;
            case 'disable-debug':
                this.currentPendingDebug = false;
                break;
            case 'reset':
                this.currentPendingCount = 0;
                break;

        }
        if (this.currentPendingDebug) {
            CMDBuildUI.util.Logger.log(Ext.String.format('Pending ajax: {0}', this.currentPendingCount));
        }
        return this.currentPendingCount;
    },

    /**
     * @return {String}
     */
    getActionId: function () {
        return this._actionid;
    },

    /**
     * @param {String} actionid
     */
    setActionId: function (actionid) {
        this._actionid = actionid;
    },

    /**
     * Update Ajax Timeout
     */
    updateAjaxTimeout: function() {
        var timeout_s = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.ajaxtimeout) || 60;
        CMDBuildUI.util.Config.ajaxTimeout = timeout_s * 1000;
        Ext.Ajax.setTimeout(CMDBuildUI.util.Config.ajaxTimeout);
    },

    privates: {
        _actionid: null,
        /**
         * @returns {String} admin|default
         */
        getViewContext: function(){
            return CMDBuildUI.util.helper.SessionHelper.getViewportVM().get('isAdministrationModule') ? 'admin' : 'default';
        },
        
        showMessages: function (response, options) {
            var messages = CMDBuildUI.util.Ajax.getResponseMessage(response);
            if (messages) {
                for (var k in messages) {
                    if (options.hideErrorNotification) {
                        var level;
                        switch (k) {
                            case "WARNING":
                                level = CMDBuildUI.util.Logger.levels.warn;
                                break;
                            case "ERROR":
                                level = CMDBuildUI.util.Logger.levels.error;
                                break;
                            case "INFO":
                                level = CMDBuildUI.util.Logger.levels.info;
                                break;
                            default:
                                level = CMDBuildUI.util.Logger.levels.info;
                        }
                        CMDBuildUI.util.Logger.log(messages[k].message, level, messages[k].code);
                    } else if (response.status !== -1) {
                        var notifier;
                        switch (k) {
                            case "WARNING":
                                notifier = CMDBuildUI.util.Notifier.showWarningMessage;
                                break;
                            case "ERROR":
                                notifier = CMDBuildUI.util.Notifier.showErrorMessage;
                                break;
                            case "INFO":
                                notifier = CMDBuildUI.util.Notifier.showInfoMessage;
                                break;
                            default:
                                notifier = CMDBuildUI.util.Notifier.showInfoMessage;
                        }
                        notifier(messages[k].usermessage, messages[k].code, undefined, messages[k].message);
                    }
                }
            }
        },

        /**
         * @param {Object} response
         * @return {Object} An object conaining error message and error code.
         */
        getResponseMessage: function (response) {         
            var oresponse = Ext.JSON.decode(response.responseText, true);
            var errors = false;
            if (oresponse && oresponse.messages) {
                errors = {};
                var usermessages = {};
                var messages = {};
                var reqid = '';
                if (response.request && response.request.headers) {
                    reqid = Ext.String.format("<b>req id</b>: {0}... <br/>", response.request.headers['CMDBuild-RequestId'].substring(0,15));
                }
                oresponse.messages.forEach(function (m) {                    
                    if (!usermessages[m.level]) {
                        usermessages[m.level] = [];
                    }
                    if (!messages[m.level]) {
                        messages[m.level] = [];
                    }
                    if (m.show_user) {
                        if (reqid) {
                            usermessages[m.level].push(reqid);
                        }                       
                        usermessages[m.level].push(m.message);
                    } else {
                        messages[m.level].push(m.message);
                    }
                });

                for (var k1 in usermessages) {
                    errors[k1] = {};
                    if (usermessages[k1].length) {
                        errors[k1].usermessage = usermessages[k1].join("<br />");
                    } else {
                        switch (k1) {
                            case "WARNING":
                                errors[k1].usermessage = CMDBuildUI.locales.Locales.notifier.genericwarning;
                                break;
                            case "ERROR":
                                errors[k1].usermessage = CMDBuildUI.locales.Locales.notifier.genericerror;
                                break;
                            case "INFO":
                                errors[k1].usermessage = CMDBuildUI.locales.Locales.notifier.genericinfo;
                                break;
                            default:
                                errors[k1].usermessage = "";
                        }
                    }
                }

                for (var k2 in messages) {
                    if (!errors[k2]) {
                        errors[k2] = {};
                    }
                    if (messages[k2].length) {
                        errors[k2].message = messages[k2].join("<br />");
                    } else {
                        errors[k2].message = response.statusText;
                    }
                }
            }
            return errors;
        }
    }
});
Ext.define('CMDBuildUI.util.helper.SessionHelper', {
    singleton: true,

    authorization: 'CMDBuild-Authorization',
    localization: 'CMDBuild-Localization',

    logging: false,

    /**
     * @return {String}
     */
    getToken: function () {
        return Ext.util.Cookies.get(CMDBuildUI.util.helper.SessionHelper.authorization);
    },

    /**
     * @param {String} token
     */
    setToken: function (token) {
        if (token) {
            var tomorrow = new Date();
            tomorrow.setDate(tomorrow.getDate() + 1);
            Ext.util.Cookies.set(CMDBuildUI.util.helper.SessionHelper.authorization, token, tomorrow, this.getCookiesPath());
            CMDBuildUI.util.Ajax.sessionexpired = false;
            this.initWebSoket(token);
        } else {
            CMDBuildUI.util.helper.SessionHelper.clearToken();
        }
    },

    /**
     * 
     */
    clearToken: function () {
        Ext.util.Cookies.clear(CMDBuildUI.util.helper.SessionHelper.authorization, this.getCookiesPath());
    },

    /**
     * @return {String}
     */
    getLanguage: function () {
        return Ext.util.Cookies.get(CMDBuildUI.util.helper.SessionHelper.localization);
    },

    /**
     * @param {String} lang
     */
    setLanguage: function (lang) {
        if (lang) {
            var oneyear = new Date();
            oneyear.setFullYear(oneyear.getFullYear() + 1);
            Ext.util.Cookies.set(CMDBuildUI.util.helper.SessionHelper.localization, lang, oneyear, this.getCookiesPath());
            this.loadLocale(lang);
        } else {
            CMDBuildUI.util.helper.SessionHelper.clearLanguage();
        }
    },

    /**
     * 
     */
    clearLanguage: function () {
        Ext.util.Cookies.clear(CMDBuildUI.util.helper.SessionHelper.localization, this.getCookiesPath());
    },

    /**
     * Check the validity of the token.
     * 
     * @return {Ext.promise.Promise}
     */
    checkSessionValidity: function () {
        var me = this;
        var deferred = new Ext.Deferred();
        // get saved token
        var token = CMDBuildUI.util.helper.SessionHelper.getToken();

        if (token) {
            // check the validity of the saved token.
            CMDBuildUI.model.users.Session.load(token, {
                hideErrorNotification: true,
                success: function (session, operation) {
                    CMDBuildUI.util.helper.SessionHelper.setSessionIntoViewport(session);
                    if (session.get("role") && (Ext.isEmpty(session.get("availableTenants")) || !Ext.isEmpty(session.get("activeTenants")) || session.get("ignoreTenants"))) {
                        me.initWebSoket(token);
                        deferred.resolve(token);
                    } else {
                        var err = "Group or tenant not selected.";
                        CMDBuildUI.util.Logger.log(err, CMDBuildUI.util.Logger.levels.debug, 401);
                        deferred.reject(err);
                    }
                },
                failure: function (action) {
                    CMDBuildUI.util.helper.SessionHelper.setToken(null);
                    var err = "Session token expired.";
                    CMDBuildUI.util.Logger.log(err, CMDBuildUI.util.Logger.levels.debug, 401);
                    deferred.reject(err);
                }
            });
        } else {
            var err = "Session token not found.";
            CMDBuildUI.util.Logger.log(err, CMDBuildUI.util.Logger.levels.debug, 401);
            deferred.reject(err);
        }
        return deferred.promise;
    },

    /**
     * Set session object into Viewport
     * 
     * @param {CMDBuildUI.model.users.Session} session
     */
    setSessionIntoViewport: function (session) {
        this.getViewportVM().set("theSession", session);
    },

    /**
     * Get current session.
     * 
     * @return {CMDBuildUI.model.users.Session} Current session
     */
    getCurrentSession: function () {
        return this.getViewportVM().get("theSession");
    },

    /**
     * Get current session.
     * 
     * @param {String} instancename
     */
    updateInstanceName: function (instancename) {
        this.getViewportVM().set("instancename", instancename);
        var title = Ext.getHead().child("title");
        if (instancename && title && title.dom.text.indexOf(" - ") === -1) {
            title.setText(title.dom.text + " - " + instancename);
        }
    },

    /**
     * Get current session.
     * 
     * @param {String} companylogoid
     */
    updateCompanyLogoId: function (companylogoid) {
        this.getViewportVM().set("companylogoid", companylogoid);
    },

    /**
     * Implementation of window.sessionStorage.setItem()
     * 
     * @param {String} key The key.
     * @param {*} value The new associated value for `key`.
     * 
     */
    setItem: function (key, value) {
        if (!this.localSessionStorage.id) {
            this.localSessionStorage = new Ext.util.LocalStorage({
                id: this.LOCAL_STORAGE_ID,
                session: true
            });
        }
        this.localSessionStorage.setItem(key, Ext.JSON.encode(value));
    },

    /**
     * Implementation of window.sessionStorage.getItem()
     * 
     * @param {String|Number} key The key.
     * @param {*} [defaultValue=null] The default associated value for `key`.
     * @returns {*}
     */
    getItem: function (key, defaultValue) {
        if (this.localSessionStorage.id) {
            return Ext.JSON.decode(this.localSessionStorage.getItem(key)) || defaultValue;
        }
        return defaultValue;
    },

    /**
     * Implementation of window.sessionStorage.removeItem()
     * 
     * @param {String|Number} key The key.
     */
    removeItem: function (key) {
        if (this.localSessionStorage.id) {
            this.localSessionStorage.removeItem(key);
        }
    },

    /**
     * Load localization file.
     * @param {String} lang
     */
    loadLocale: function (lang) {
        if (!lang) {
            lang = this.getLanguage();
        }
        if (lang && lang !== "en") {
            Ext.require([
                Ext.String.format("CMDBuildUI.locales.{0}.LocalesAdministration", lang),
                Ext.String.format("CMDBuildUI.locales.{0}.Locales", lang)
            ]);
        } else if (lang === "en") {
            Ext.require(
                Ext.String.format("CMDBuildUI.locales.Locales", lang)
            );
        }
    },

    /**
     * @param {String} url
     */
    setStartingUrl: function (url) {
        this._startingurl = url;
    },

    /**
     * @return {String} 
     */
    getStartingUrl: function () {
        return this._startingurl;
    },

    /**
     * Sets current url as starting url.
     */
    updateStartingUrlWithCurrentUrl: function () {
        var currentUrl = Ext.History.getToken();
        if (currentUrl.length > 1 && currentUrl !== 'patches') {
            CMDBuildUI.util.helper.SessionHelper.setStartingUrl(currentUrl);
        }
    },

    /**
     * Clear starting url.
     */
    clearStartingUrl: function () {
        CMDBuildUI.util.helper.SessionHelper.setStartingUrl(null);
    },

    /**
     * 
     */
    getActiveTenants: function () {
        var session = this.getCurrentSession();
        var activetenants = session.get("activeTenants");
        var availabletenants = session.get('availableTenantsExtendedData');
        var ignoretenants = session.get("ignoreTenants");
        function activeTenantsFilter(value) {
            return ignoretenants || Ext.Array.contains(activetenants, value.code);
        }
        return availabletenants.filter(activeTenantsFilter);
    },

    /**
     * 
     * @param {String[]} tenants 
     */
    updateActiveTenants: function (tenants) {
        this.getCurrentSession().set("activeTenants", tenants);
    },

    privates: {
        /**
         * An Object contains new Ext.util.LocalStorage
         * @type {Ext.util.LocalStorage}
         */
        localSessionStorage: {},
        /**
         * The id param used in new Ext.util.LocalStorage
         * @type {String}
         */
        LOCAL_STORAGE_ID: 'CMDBUILD-SESSION',

        /**
         * @property {String} _startingurl
         * The starting url
         */
        _startingurl: null,

        /**
         * @property {WebSocket} _socket
         * The web socket used by the application.
         */
        _socket: null,

        /**
         * Get Viewport ViewModel
         * 
         * @return {CMDBuildUI.view.main.MainModel}
         */
        getViewportVM: function () {
            var viewports = Ext.ComponentQuery.query('viewport');
            if (viewports.length) {
                return viewports[0].getViewModel();
            }
        },

        /**
         * 
         * @param {String} token 
         */
        initWebSoket: function (token) {
            try {
                if (!this._socket) {
                    var socket = this._socket = new WebSocket(CMDBuildUI.util.Config.socketUrl);
                    socket.onmessage = function (e) {
                        var data = Ext.JSON.decode(e.data || '');
                        if (data && data.message && data._event == 'alert') {
                            CMDBuildUI.util.Notifier.showInfoMessage(data.message);
                        }
                    };
                    socket.onopen = function (e) {
                        if (socket) {
                            socket.send(Ext.JSON.encode({
                                _action: 'socket.session.login',
                                token: token,
                                _id: CMDBuildUI.util.Utilities.generateUUID()
                            }));
                        }
                    };
                }
            } catch (e) {
                CMDBuildUI.util.Logger.log(
                    "Error on creating socket.",
                    CMDBuildUI.util.Logger.levels.error,
                    null,
                    e
                );
            }
        },

        closeWebSocket: function () {
            if (this._socket) {
                this._socket.close();
            }
        },

        /**
         * Get the path to use for cookies
         */
        getCookiesPath: function() {
            var path = window.location.pathname;
            return path.replace(/\/ui(_dev)?/, "");
        }
    }
});
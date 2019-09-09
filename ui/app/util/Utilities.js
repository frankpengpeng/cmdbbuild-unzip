Ext.define("CMDBuildUI.util.Utilities", {
    singleton: true,

    popupOpened: {},
    /**
     * Update the hash. By default, it will not execute the routes if the current token and the
     * token passed are the same.
     * 
     * @param {String/Ext.data.Model} token The token to redirect to.  Can be either a String
     * or a {@link Ext.data.Model Model} instance - if a Model instance is passed it will
     * internally be converted into a String token by calling the Model's
     * {@link Ext.data.Model#toUrl toUrl} function.
     *
     * @param {Boolean} force Force the update of the hash regardless of the current token.
     * 
     * @return {Boolean} Will return `true` if the token was updated.
     */
    redirectTo: function (token, force) {
        if (token.isModel) {
            token = token.toUrl();
        }

        var isCurrent = Ext.util.History.getToken() === token,
            ret = false;

        if (!isCurrent) {
            ret = true;
            Ext.util.History.add(token);
        } else if (force) {
            ret = true;
            Ext.app.route.Router.onStateChange(token);
        }
        return ret;
    },

    /**
     * @param {String} [popupId='popup-panel-1'] The default id for the panel 
     * @param {String} title
     * @param {Object|Ext.Component} content
     * @param {Object} listeners
     * @param {Object} config
     */
    openPopup: function (popupId, title, content, listeners, config) {
        popupId = (!popupId) ? 'popup-panel-' + (Ext.Object.getSize(this.popupOpened) + 1) : popupId;
        listeners = listeners || {};

        listeners = Ext.applyIf(listeners, {
            beforedestroy: function () {
                this.removeAll(true);
            }
        });

        this.closePopup(popupId);
        config = Ext.applyIf(config || {}, {
            id: popupId,
            title: title,

            width: "80%",
            height: "80%",
            ui: "management",
            floating: true,
            closable: true,
            modal: true,
            layout: 'fit',
            alwaysOnTop: true,
            resizable: true,
            draggable: true,

            items: [content],
            listeners: listeners
        });

        var panel = new Ext.panel.Panel(config);

        panel.show();
        this.popupOpened[popupId] = panel;
        return panel;
    },

    /**
     * Close the pop popup from anywhere
     * @param {String|Number} popupId 
     * @return {Boolean} true|false 
     */
    closePopup: function (popupId) {
        if (this.popupOpened[popupId] && typeof this.popupOpened[popupId].destroy == "function") {
            this.popupOpened[popupId].destroy();
        }
        return delete this.popupOpened[popupId];
    },

    /**
     * @return {String} The generated UID
     */
    generateUUID: function () {
        var d = new Date().getTime();
        var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = (d + Math.random() * 16) % 16 | 0;
            d = Math.floor(d / 16);
            return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
        });
        return uuid;
    },

    /**
     * Add loading mask on component.
     * @param {Ext.component.Component} element
     * @return {Ext.LoadMask}
     */
    addLoadMask: function (element) {
        // add load mask
        var loadmask = new Ext.LoadMask({
            target: element
        });
        loadmask.show();
        return loadmask;
    },

    /**
     * Remove loading mask from component.
     * @param {Ext.LoadMask} loadmask
     */
    removeLoadMask: function (loadmask) {
        loadmask.destroy();
    },

    /**
     * Transform utf-8 string to hex string.
     * @param {String} s
     * @return {String}
     */
    stringToHex: function (s) {
        if (s && !Ext.String.startsWith(s, 'HEX')) {
            // utf8 to latin1
            s = unescape(encodeURIComponent(s));
            var h = '';
            for (var i = 0; i < s.length; i++) {
                h += s.charCodeAt(i).toString(16);
            }
            return "HEX" + h;
        }
        return s;
    },

    /**
     * Transform hex string to utf-8 string.
     * @param {String} s
     * @return {String}
     */
    hexToString: function (h) {
        var s = '';
        if (h && Ext.String.startsWith(h, 'HEX')) {
            h = h.replace("HEX", "");
            for (var i = 0; i < h.length; i += 2) {
                s += String.fromCharCode(parseInt(h.substr(i, 2), 16));
            }
            return decodeURIComponent(escape(s));
        }
        return s;
    },

    /**
     * Remove special characters from string.
     * Leaves only characters from a to z, numbers and _ 
     * @param {String} str 
     * @return {String}
     */
    stringRemoveSpecialCharacters: function (str) {
        return str.replace(/[^a-zA-Z0-9_]/g, "");
    },

    /**
     * Transform major-minor chars into legit html &gt; &lt;
     * @param {String} str 
     * @return {String}
     */
    transformMajorMinor: function (str) {
        if (str) {
            if (str.indexOf('<') != -1) {
                str = str.replace(/</g, '&lt;');
            }
            if (str.indexOf('>') != -1) {
                str = str.replace(/>/g, '&gt;');
            }
        }
        return str;
    },

    /**
     * CheckBootStatus.
     * 
     * @return {Ext.promise.Promise}
     */
    checkBootStatus: function () {
        var deferred = new Ext.Deferred();
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Common.getBootStatusUrl(),
            method: 'GET'
        }).then(function (response, opts) {
            if (response.status === 200 && response.responseText) {
                var jsonresponse = Ext.JSON.decode(response.responseText);
                if (jsonresponse.status === 'WAITING_FOR_USER') {
                    deferred.reject();
                }
            }
            deferred.resolve();
        }, function () {
            deferred.resolve();
        });
        return deferred.promise;
    },

    /**
     * 
     * @param {Object} newobj 
     * @param {Object} oldobj 
     * @return {Object}
     */
    getObjectChanges: function (newobj, oldobj) {
        var changes = {};
        oldobj = oldobj || {};
        // get new or changed values
        for (var key in newobj) {
            if (newobj[key] != oldobj[key]) {
                changes[key] = newobj[key];
            }
        }
        // get removed values
        var oldkeys = Ext.Array.difference(Ext.Object.getAllKeys(oldobj), Ext.Object.getAllKeys(newobj));
        oldkeys.forEach(function (k) {
            changes[k] = undefined;
        });
        return changes;
    },

    /**
     * 
     * @param {String} url 
     */
    openPrintPopup: function (url) {
        CMDBuildUI.util.Utilities.openPopup(null, CMDBuildUI.locales.Locales.common.grid.print, {
            tbar: [{
                xtype: 'tbfill'
            }, {
                xtype: 'button',
                ui: 'management-action',
                iconCls: 'x-fa fa-download',
                itemId: 'downloadbtn',
                tooltip: CMDBuildUI.locales.Locales.reports.download,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.reports.download'
                },
                href: url + "&_download=true",
                listeners: {
                    afterrender: function (view) {
                        view.el.set({
                            "download": CMDBuildUI.locales.Locales.common.grid.print
                        }, false);
                    }
                }

            }],
            items: [{
                xtype: 'uxiframe',
                width: '100%',
                height: '100%',
                ariaAttributes: {
                    role: 'document'
                },
                listeners: {
                    afterrender: function (view) {
                        view.load(url);
                    }
                }
            }]
        });
    }
});
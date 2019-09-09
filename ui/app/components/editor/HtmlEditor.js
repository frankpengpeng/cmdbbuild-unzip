Ext.define('CMDBuildUI.components.editor.HtmlEditor', {
    extend: 'Ext.form.field.HtmlEditor',
    alias: 'widget.cmdbuildhtmleditor',

    allowBlank: true,
    blankText: CMDBuildUI.locales.Locales.administration.common.messages.thisfieldisrequired,
    localized: {
        blankText: 'CMDBuildUI.locales.Locales.administration.common.messages.thisfieldisrequired'
    },

    getToolbarCfg: function () {
        var me = this,
            items = [],
            i,
            tipsEnabled = Ext.quickTipsActive && Ext.tip.QuickTipManager.isEnabled(),
            baseCSSPrefix = Ext.baseCSSPrefix,
            fontSelectItem, undef;

        function btn(id, toggle, handler) {
            return Ext.merge({
                itemId: id,
                cls: baseCSSPrefix + 'btn-icon',
                iconCls: baseCSSPrefix + 'edit-' + id,
                enableToggle: toggle !== false,
                scope: me,
                handler: handler || me.relayBtnCmd,
                clickEvent: 'mousedown',
                tooltip: tipsEnabled ? me.buttonTips[id] : undef,
                overflowText: me.buttonTips[id].title || undef,
                tabIndex: -1
            }, me.buttonDefaults);
        }


        if (me.enableFont && !Ext.isSafari2) {
            fontSelectItem = Ext.widget('component', {
                itemId: 'fontSelect',
                renderTpl: [
                    '<select id="{id}-selectEl" data-ref="selectEl" class="' + baseCSSPrefix + 'font-select">',
                    '</select>'
                ],
                childEls: ['selectEl'],
                afterRender: function () {
                    me.fontSelect = this.selectEl;
                    Ext.Component.prototype.afterRender.apply(this, arguments);
                },
                onDisable: function () {
                    var selectEl = this.selectEl;
                    if (selectEl) {
                        selectEl.dom.disabled = true;
                    }
                    Ext.Component.prototype.onDisable.apply(this, arguments);
                },
                onEnable: function () {
                    var selectEl = this.selectEl;
                    if (selectEl) {
                        selectEl.dom.disabled = false;
                    }
                    Ext.Component.prototype.onEnable.apply(this, arguments);
                },
                listeners: {
                    change: function () {
                        me.win.focus();
                        me.relayCmd('fontName', me.fontSelect.dom.value);
                        me.deferFocus();
                    },
                    element: 'selectEl'
                }
            });

            items.push(
                fontSelectItem,
                '-'
            );
        }

        if (me.enableFormat) {
            items.push(
                btn('bold'),
                btn('italic'),
                btn('underline')
            );
        }

        if (me.enableFontSize) {
            items.push(
                '-',
                btn('increasefontsize', false, me.adjustFont),
                btn('decreasefontsize', false, me.adjustFont)
            );
        }

        if (me.enableColors) {
            items.push(
                '-', Ext.merge({
                    itemId: 'forecolor',
                    cls: baseCSSPrefix + 'btn-icon',
                    iconCls: baseCSSPrefix + 'edit-forecolor',
                    overflowText: me.buttonTips.forecolor.title,
                    tooltip: tipsEnabled ? me.buttonTips.forecolor || undef : undef,
                    tabIndex: -1,
                    menu: Ext.widget('menu', {
                        plain: true,

                        items: [{
                            xtype: 'colorpicker',
                            allowReselect: true,
                            focus: Ext.emptyFn,
                            value: '000000',
                            plain: true,
                            clickEvent: 'mousedown',
                            handler: function (cp, color) {
                                me.relayCmd('forecolor', Ext.isWebKit || Ext.isIE ? '#' + color : color);
                                this.up('menu').hide();
                            }
                        }]
                    })
                }, me.buttonDefaults), Ext.merge({
                    itemId: 'backcolor',
                    cls: baseCSSPrefix + 'btn-icon',
                    iconCls: baseCSSPrefix + 'edit-backcolor',
                    overflowText: me.buttonTips.backcolor.title,
                    tooltip: tipsEnabled ? me.buttonTips.backcolor || undef : undef,
                    tabIndex: -1,
                    menu: Ext.widget('menu', {
                        plain: true,

                        items: [{
                            xtype: 'colorpicker',
                            focus: Ext.emptyFn,
                            value: 'FFFFFF',
                            plain: true,
                            allowReselect: true,
                            clickEvent: 'mousedown',
                            handler: function (cp, color) {
                                if (Ext.isGecko) {
                                    me.execCmd('useCSS', false);
                                    me.execCmd('hilitecolor', '#' + color);
                                    me.execCmd('useCSS', true);
                                    me.deferFocus();
                                } else {
                                    me.relayCmd(Ext.isOpera ? 'hilitecolor' : 'backcolor', Ext.isWebKit || Ext.isIE || Ext.isOpera ? '#' + color : color);
                                }
                                this.up('menu').hide();
                            }
                        }]
                    })
                }, me.buttonDefaults)
            );
        }

        if (me.enableAlignments) {
            items.push(
                '-',
                btn('justifyleft'),
                btn('justifycenter'),
                btn('justifyright')
            );
        }

        if (!Ext.isSafari2) {
            if (me.enableLinks) {
                items.push(
                    '-',
                    btn('createlink', false, me.createLink)
                );
            }

            if (me.enableLists) {
                items.push(
                    '-',
                    btn('insertorderedlist'),
                    btn('insertunorderedlist')
                );
            }
            if (me.enableSourceEdit) {
                items.push(
                    '-',
                    btn('sourceedit', true, function () {
                        me.toggleSourceEdit(!me.sourceEditMode);
                    })
                );
            }
        }

        // add clear html button
        me.buttonTips.clearhtml = {
            title: CMDBuildUI.locales.Locales.common.editor.clearhtml
        };
        items.push(
            '-',
            btn('clearhtml', false, me.clearHtml)
        );

        // Everything starts disabled. 
        for (i = 0; i < items.length; i++) {
            if (items[i].itemId !== 'sourceedit') {
                items[i].disabled = true;
            }
        }

        // build the toolbar 
        // Automatically rendered in Component.afterRender's renderChildren call 
        return {
            xtype: 'toolbar',
            defaultButtonUI: me.defaultButtonUI,
            cls: Ext.baseCSSPrefix + 'html-editor-tb',
            enableOverflow: true,
            items: items,

            // stop form submits 
            listeners: {
                click: function (e) {
                    e.preventDefault();
                },
                element: 'el'
            }
        };
    },

    clearHtml: function () {
        // sanitize HTML
        this.setValue(sanitizeHtml(this.getValue()));
    },

    isValid: function () {
        var checkValid = true;
        var errorMsg = this.blankText;
        if (!this.allowBlank && !this.getValue()) {
            checkValid = false;
        }
        this.markInvalid(errorMsg, checkValid);
        return checkValid;
    },

    markInvalid: function (errorMsg, checkValid) {
        var me = this;
        if (me.containerEl) {
            var containerIframe = me.containerEl;
            if (containerIframe && !checkValid) {
                containerIframe.addCls('x-form-trigger-wrap-invalid');
                me.setActiveError(errorMsg);
            } else {
                containerIframe.removeCls('x-form-trigger-wrap-invalid');
                me.unsetActiveError(errorMsg);
            }
        }
    }
});
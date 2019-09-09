Ext.define('CMDBuildUI.util.administration.helper.FormHelper', {
    singleton: true,

    fieldDefaults: {
        labelAlign: 'top',
        labelPad: 2,
        labelSeparator: '',
        anchor: '100%',
        width: '100%',
        msgTarget: 'qtip'
    },

    formActions: {
        view: 'VIEW',
        add: 'ADD',
        edit: 'EDIT'
    },

    /**
     * Get Invalid Fields helper
     * 
     * CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(form)
     * 
     * @param {Ext.form.Panel} form 
     * 
     * @returns {Ext.form.field.Base[]} Array of invalid form fields
     */
    getInvalidFields: function (form) {
        var invalidFields = [];
        Ext.suspendLayouts();
        form.getFields().filterBy(function (field) {
            if (field.validate()) return;
            invalidFields.push(field);
        });
        Ext.resumeLayouts(true);
        return invalidFields;
    },

    /**
     * Get form buttons bar with "Save" and "Cancel" buttons 
     * 
     * CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(formBind)
     * use formBind = false for ignore form bind
     * 
     * 
     * @param {Boolean} [formBind=true] shortcut form saveBtn.saveBtn AND saveBtn.disabled
     * @param {Ext.button.Button} [saveBtn] config
     * @param {Ext.button.Button} [cancelBtn] config
     * 
     * @returns {Ext.button.Button[]} Array of button items
     */
    getSaveCancelButtons: function (formBind, saveBtn, cancelBtn) {
        formBind = (typeof formBind === 'undefined' || formBind === true) ? true : false;
        return [{
            xtype: 'component',
            flex: 1
        }, Ext.merge({}, saveBtn || {}, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.save,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.save'
            },
            formBind: saveBtn && saveBtn.formBind || formBind,
            disabled: saveBtn && saveBtn.disabled || formBind,
            itemId: 'saveBtn',
            ui: 'administration-action-small'
        }), Ext.merge({}, cancelBtn || {}, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
            ui: 'administration-secondary-action-small',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
            },
            itemId: 'cancelBtn'
        })];
    },

    /**
     * Get form buttons bar with "Save", "Save and add" and "Cancel" buttons 
     * 
     * CMDBuildUI.util.administration.helper.FormHelper.getSaveAndAddCancelButtons(ignoreFormBind)
     * use formBind = false for ignore form bind
     * 
     * @param {Boolean} [formBind=true]
     * 
     * @returns {Array} Array of button items
     */
    getSaveAndAddCancelButtons: function (formBind) {
        formBind = (typeof formBind === 'undefined' || formBind === true) ? true : false;

        return [{
            xtype: 'component',
            flex: 1
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.save,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.save'
            },
            formBind: formBind,
            disabled: formBind,
            itemId: 'saveBtn',
            ui: 'administration-action-small'
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.saveandadd,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.saveandadd'
            },
            formBind: formBind,
            disabled: formBind,
            itemId: 'saveAndAddBtn',
            ui: 'administration-action-small'
        }, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
            ui: 'administration-secondary-action-small',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
            },
            itemId: 'cancelBtn'
        }];
    },

    /**
     * 
     * @param {Object|Ext.button.Button} okProperties 
     * @param {Object|Ext.button.Button} closeProperties 
     */
    getOkCloseButtons: function (okProperties, closeProperties) {

        var okButton = Ext.applyIf(okProperties || {}, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.ok,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.ok'
            },
            formBind: false,
            disabled: false,
            itemId: 'okBtn',
            ui: 'administration-action-small'
        });

        var closeButton = Ext.applyIf(closeProperties || {}, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.close,
            ui: 'administration-secondary-action-small',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.close'
            },
            itemId: 'closeBtn'
        });

        return [{
            xtype: 'component',
            flex: 1
        }, okButton, closeButton];

    },


    /**
     * Get form buttons bar with "Save", "Save and add" and "Cancel" buttons 
     * 
     * CMDBuildUI.util.administration.helper.FormHelper.getSaveAndAddCancelButtons(ignoreFormBind)
     * use formBind = false for ignore form bind
     * 
     * @param {Boolean} [formBind=true]
     * 
     * @returns {Array} Array of button items
     */
    getPrevNextSaveCancelButtons: function (formBind, prevBtn, nextBtn, saveBtn, cancelBtn) {
        formBind = (typeof formBind === 'undefined' || formBind === true) ? true : false;

        return [Ext.merge({}, prevBtn || {}, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.prev,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.prev'
            },
            itemId: 'prevBtn',
            ui: 'administration-secondary-action-small'
        }), Ext.merge({}, nextBtn || {}, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.next,
            ui: 'administration-secondary-action-small',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.next'
            },
            itemId: 'nextBtn'
        }), {
            xtype: 'component',
            flex: 1
        }, Ext.merge({}, saveBtn || {}, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.save,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.save'
            },
            formBind: formBind,
            disabled: formBind,
            itemId: 'saveBtn',
            ui: 'administration-action-small'
        }), Ext.merge({}, cancelBtn || {}, {
            text: CMDBuildUI.locales.Locales.administration.common.actions.cancel,
            ui: 'administration-secondary-action-small',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.actions.cancel'
            },
            itemId: 'cancelBtn'
        })];
    },

    /**
     * Open localization pop up
     * @param {String} translationCode the translation code
     * @param {String} [action="EDIT"] open popup in EDIT or VIEW mode.
     * 
     * @returns {Ext.panel.Panel} popup panel generated with CMDBuildUI.util.Utilities.openPopup()
     */
    openLocalizationPopup: function (translationCode, action, localeVmObject, vm) {
        localeVmObject = localeVmObject || 'theTranslation';
        if (translationCode.length) {
            if (!action) {
                action = CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
            }
            var popupId = Ext.String.format('popup-localization-{0}', translationCode.replace(/.|_| /g, '-'));

            var vmData = {

                action: action,
                actions: {
                    edit: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    add: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    view: action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                },
                translationCode: translationCode
            };

            vmData[localeVmObject] = vm.get(localeVmObject);

            var content = {
                xtype: 'administration-localization-localizecontent',
                scrollable: 'y',

                theVmObject: localeVmObject,

                viewModel: {
                    data: vmData
                }
            };
            // custom panel listeners
            var listeners = {
                setlocalesstore: function (store) {

                    if (vm && vm.get) {
                        vm.set(localeVmObject ? localeVmObject : 'theTranslation', store);
                        CMDBuildUI.util.Utilities.closePopup(popupId);
                    }
                },
                /**
                 * @param {Ext.panel.Panel} panel
                 * @param {Object} eOpts
                 */
                close: function (panel, eOpts) {
                    CMDBuildUI.util.Utilities.closePopup(popupId);
                }
            };
            // create and open panel
            var popup = CMDBuildUI.util.Utilities.openPopup(
                popupId,
                'Localization text',
                content,
                listeners, {
                    ui: 'administration-actionpanel',
                    width: '450px',
                    height: '450px',
                    draggable: true
                }
            );

            return popup;
        }
    },

    /**
     * 
     * @param {Object} tools
     * @param {Boolean} tools.edit
     * @param {Boolean} tools.view
     * @param {Boolean} tools.open alias of tools.view
     * @param {Boolean} tools.clone
     * @param {Boolean} tools.delete
     * @param {Boolean} tools.activeToggle
     * @param {Boolean} tools.download
     * 
     * @param {String} testid 
     * 
     * @param {String} viewModelKey
     * 
     * @param {Object[]|Ext.panel.Tool[]} beforeTools
     * @param {Object[]|Ext.panel.Tool[]} afterTools
     * 
     * @returns {Ext.panel.Tool[]}
     */
    getTools: function (tools, testid, viewModelKey, beforeTbfill, beforeTools, afterTools) {

        var _tools = [{
            // it will set the correct heigth
            xtype: 'button',
            itemId: 'spacer',
            style: {
                "visibility": "hidden"
            }
        }];
        if (beforeTbfill && beforeTbfill.length) {
            _tools = Ext.Array.merge([], beforeTbfill);
        }
        _tools.push({
            xtype: 'tbfill' // it will move the others tools to right
        });

        if (beforeTools && beforeTools.length) {
            _tools = Ext.Array.merge(_tools, beforeTools);
        }


        if (tools.edit) {
            _tools.push(this.getEditTool(testid));
        }
        if (tools.view || tools.open) {
            _tools.push(this.getViewTool(testid));
        }
        if (tools.download) {
            _tools.push(this.getDownloadTool(testid));
        }
        if (tools.clone) {
            _tools.push(this.getCloneTool(testid));
        }
        if (tools.sql) {
            _tools.push(this.getSqlTool(testid));
        }
        if (tools.delete) {
            _tools.push(this.getDeleteTool(testid));
        }
        if (viewModelKey && tools.activeToggle) {
            _tools.push(this.getEnableTool(testid, viewModelKey));
            _tools.push(this.getDisableTool(testid, viewModelKey));
        }
        if (afterTools && afterTools.length) {
            _tools = Ext.Array.merge(_tools, afterTools);
        }
        return _tools;
    },

    privates: {
        _camelize: function (str) {
            return str.replace(/(?:^\w|[A-Z]|\b\w|\s+)/g, function (match, index) {
                if (+match === 0) return ""; // or if (/\s+/.test(match)) for white spaces
                return index == 0 ? match.toLowerCase() : match.toUpperCase();
            });
        },
        getEditTool: function (testid) {
            return {
                xtype: 'tool',
                itemId: 'editBtn',
                glyph: 'f040@FontAwesome', // Pencil icon
                tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
                },
                cls: 'administration-tool',
                autoEl: {
                    'data-testid': Ext.String.format('administration-{0}-editBtn', testid)
                },
                bind: {
                    hidden: '{!actions.view}'
                }
            };
        },

        getViewTool: function (testid) {
            return {
                xtype: 'tool',
                itemId: 'openBtn',
                glyph: 'f08e@FontAwesome', // Open icon
                tooltip: CMDBuildUI.locales.Locales.administration.common.actions.open,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.open'
                },
                cls: 'administration-tool',
                autoEl: {
                    'data-testid': Ext.String.format('administration-{0}-openBtn', testid)
                },
                bind: {
                    hidden: '{hideOpenBtn}'
                }
            };
        },

        getCloneTool: function (testid) {
            return {
                xtype: 'tool',
                itemId: 'cloneBtn',
                glyph: 'f24d@FontAwesome', // Clone icon
                tooltip: CMDBuildUI.locales.Locales.administration.common.actions.clone,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.clone'
                },
                cls: 'administration-tool',
                autoEl: {
                    'data-testid': Ext.String.format('administration-{0}-cloneBtn', testid)
                },
                bind: {
                    hidden: '{!actions.view}'
                }
            };
        },

        getDeleteTool: function (testid) {
            return {
                xtype: 'tool',
                itemId: 'deleteBtn',
                glyph: 'f014@FontAwesome',
                tooltip: CMDBuildUI.locales.Locales.administration.common.actions.delete,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.delete'
                },
                cls: 'administration-tool',
                autoEl: {
                    'data-testid': Ext.String.format('administration-{0}-deleteBtn', testid)
                },
                bind: {
                    hidden: '{!actions.view}'
                }
            };
        },
        getSqlTool: function (testid) {
            return {
                xtype: 'tool',
                itemId: 'sqlBtn',
                iconCls: 'x-fa fa-database',
                tooltip: CMDBuildUI.locales.Locales.administration.reports.tooltips.viewsql, // View report sql
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.reports.tooltips.viewsql'
                },
                cls: 'administration-tool',
                autoEl: {
                    'data-testid': Ext.String.format('administration-{0}-sqlBtn', testid)
                }
            };
        },
        getEnableTool: function (testid, viewModelKey) {
            return {
                xtype: 'tool',
                itemId: 'enableBtn',
                hidden: true,
                cls: 'administration-tool',
                iconCls: 'x-fa fa-check-circle-o',
                tooltip: CMDBuildUI.locales.Locales.administration.common.actions.enable,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.enable'
                },
                autoEl: {
                    'data-testid': Ext.String.format('administration-{0}-enableBtn', testid)
                },
                bind: {
                    hidden: Ext.String.format('{{0}.active}', viewModelKey)
                }
            };
        },

        getDisableTool: function (testid, viewModelKey) {
            return {
                xtype: 'tool',
                itemId: 'disableBtn',
                cls: 'administration-tool',
                iconCls: 'x-fa fa-ban',
                tooltip: CMDBuildUI.locales.Locales.administration.common.actions.disable,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.disable'
                },
                hidden: true,
                autoEl: {
                    'data-testid': Ext.String.format('administration-{0}-disableBtn', testid)
                },
                bind: {
                    hidden: Ext.String.format('{!{0}.active}', viewModelKey)
                }
            };
        },


        getDownloadTool: function (testid) {
            return {
                xtype: 'tool',
                itemId: 'downloadBtn',
                glyph: 'f019@FontAwesome', // download icon
                tooltip: CMDBuildUI.locales.Locales.administration.common.actions.download,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.download'
                },
                cls: 'administration-tool',
                autoEl: {
                    'data-testid': Ext.String.format('administration-{0}-downloadBtn', testid)
                },
                bind: {
                    hidden: '{!actions.view}'
                }
            };
        }
    },

    getDockedTopBar: function (view) {
        return Ext.create('CMDBuildUI.components.administration.toolbars.FormToolbar', {
            // xtype: 'components-administration-toolbars-formtoolbar',
            dock: 'top',
            padding: '6 0 6 8',
            borderBottom: 0,
            itemId: 'toolbarscontainer',
            style: 'border-bottom-width:0!important',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({},
                view.getSingularName(),
                view.getTheCurrentObject(),
                [{
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.administration[view.getPluralName()].texts['add' + view.getSingularName()], // Add customcomponent

                    ui: 'administration-action-small',
                    itemId: 'addBtn',
                    iconCls: 'x-fa fa-plus',
                    autoEl: {
                        'data-testid': Ext.String.format('administration-{0}-toolbar-addBtn', view.getSingularName())
                    }
                }, {
                    xtype: 'textfield',
                    name: 'search',
                    width: 250,
                    emptyText: CMDBuildUI.locales.Locales.administration[view.getPluralName()].emptytexts['search' + view.getPluralName()], // Search customcomponent...
                    localized: {
                        emptyText: Ext.String.format('CMDBuildUI.locales.Locales.administration.{0}.emptytexts.search{1}',
                            view.getPluralName(),
                            view.getPluralName()
                        )
                    },
                    cls: 'administration-input',
                    reference: 'searchtext',
                    itemId: 'searchtext',
                    bind: {
                        value: '{search.value}',
                        hidden: '{!canFilter}'
                    },
                    listeners: {
                        specialkey: 'onSearchSpecialKey'
                    },
                    triggers: {
                        search: {
                            cls: Ext.baseCSSPrefix + 'form-search-trigger',
                            handler: 'onSearchSubmit',
                            autoEl: {
                                'data-testid': Ext.String.format('administration-{0}-toolbar-search-form-search-trigger', view.getSingularName())
                            }
                        },
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: 'onSearchClear',
                            autoEl: {
                                'data-testid': Ext.String.format('administration-{0}-toolbar-search-form-clear-trigger', view.getSingularName())
                            }
                        }
                    },
                    autoEl: {
                        'data-testid': Ext.String.format('administration-{0}-toolbar-search-form', view.getSingularName())
                    }
                }, {
                    xtype: 'tbfill'
                }],
                null,
                [{
                    xtype: 'tbtext',
                    hidden: true,
                    bind: {
                        hidden: Ext.String.format('{!{0}.description}', view.getTheCurrentObject()),
                        html: Ext.String.format('{componentTypeName}: <b data-testid="administration-{0}-description">{{1}.description}</b>',

                            view.getPluralName(),
                            view.getTheCurrentObject()
                        )
                    }
                }])
        });
    },

    getDockedToolBar: function (view) {
        return Ext.create('CMDBuildUI.components.administration.toolbars.FormToolbar', {
            // xtype: 'components-administration-toolbars-formtoolbar',
            region: 'top',
            borderBottom: 0,
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true, // #editBtn set true for show the button
                'delete': true, // #deleteBtn set true for show the button
                activeToggle: true, // #enableBtn and #disableBtn set true for show the buttons
                download: true // #downloadBtn set true for show the buttons
            },

                /* testId */
                view.getSingularName(),
                view.getTheCurrentObject(),

                /* add custom tools[] on the left of the bar */
                [],

                /* add custom tools[] before #editBtn*/
                [],

                /* add custom tools[] after at the end of the bar*/
                []
            ),
            bind: {
                hidden: '{formtoolbarHidden}'
            }
        });
    },

    getDockedFormButtons: function (buttonsUtilFormFn) {
        return Ext.create('Ext.toolbar.Toolbar', {
            // xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,

            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper[buttonsUtilFormFn](true)
        });
    },

    /**
     * 
     * @param {Ext.form.field.Base} item 
     * @return {String} label
     */
    getFieldcontainerLabel: function (item) {
        var itemUp = item.up('fieldcontainer');
        if (itemUp && itemUp.hasOwnProperty('getFieldLabel')) {
            var label = itemUp.getFieldLabel();
            if (!label) {
                return this.getFieldcontainerLabel(itemUp);
            }
            return label;
        }else{
            return item.getFieldLabel();
        }
    },

    /**
     * 
     * @param {Ext.form.field.Base[]} invalid
     */
    showInvalidFieldsMessage: function (invalid) {
        var invalidFields = [];
        Ext.Array.forEach(invalid.items, function (item) {
            invalidFields.push(CMDBuildUI.util.administration.helper.FormHelper.getFieldcontainerLabel(item));
        });
        invalidFields = Ext.Array.unique(invalidFields);
        CMDBuildUI.util.Notifier.showWarningMessage('<strong>Validation Error:</strong></br>' + invalidFields.join('</br>'));
    }
});
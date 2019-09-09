Ext.define('CMDBuildUI.util.helper.FormHelper', {
    singleton: true,

    formmodes: {
        create: 'create',
        update: 'update',
        read: 'read'
    },

    fieldmodes: {
        hidden: 'hidden',
        immutable: 'immutable',
        read: 'read',
        write: 'write'
    },

    properties: {
        padding: '0 15 0 15'
    },
    fieldDefaults: {
        labelAlign: 'top',
        labelPad: 2,
        labelSeparator: '',
        anchor: '100%'
    },

    /**
     * @param {String} fieldname
     * @return {String} The store id
     * @private
     */
    getStoreId: function (fieldname) {
        return fieldname + "Store";
    },

    /**
     * Get form fields
     * @param {Ext.data.Model} model
     * @param {Object} config
     * @param {Boolean} config.mode
     * @param {Object[]} config.defaultValues An array of objects containing default values.
     * @param {String} config.linkName The name of the object linked within the ViewModel.
     * @param {Object} config.attributesOverrides An object containing properties to override for attributes
     * @param {Boolean} config.attributesOverrides.attributename.writable Override writable property
     * @param {Boolean} config.attributesOverrides.attributename.mandatory Override mandatory property
     * @param {Boolean} config.attributesOverrides.attributename.hidden Override writable property
     * @param {Numeric} config.attributesOverrides.attributename.index Override index property
     * @return {Object[]} the list of form fields
     */
    getFormFields: function (model, config) {
        var items = [];
        config = config || {};
        var me = this;

        // set default configuration
        Ext.applyIf(config, {
            readonly: false,
            attributesOverrides: {},
            mode: config.readonly ? this.formmodes.read : this.formmodes.update
        });

        Ext.Array.each(model.getFields(), function (modelField, index) {
            var field = Ext.apply({}, modelField);
            if (!Ext.String.startsWith(field.name, "_")) {
                var defaultValue;
                // check and use default values
                if (!Ext.isEmpty(config.defaultValues)) {
                    defaultValue = Ext.Array.findBy(config.defaultValues, function (item, index) {
                        if (item.value) {
                            if (field.cmdbuildtype.toLowerCase() === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase()) {
                                return (item.attribute && item.attribute === field.name) ||
                                    (item.domain && item.domain === field.attributeconf.domain);
                            } else {
                                return item.attribute && item.attribute === field.name;
                            }
                        }
                    });
                }

                var overrides = config.attributesOverrides[field.name];
                if (overrides) {
                    if (!Ext.isEmpty(overrides.index)) {
                        field.attributeconf.index = overrides.index;
                    }
                    if (!Ext.isEmpty(overrides.mandatory)) {
                        field.mandatory = overrides.mandatory;
                    }
                    if (!Ext.isEmpty(overrides.writable)) {
                        field.writable = overrides.writable;
                    }
                    if (!Ext.isEmpty(overrides.hidden)) {
                        field.hidden = overrides.hidden;
                    }
                }

                var formfield = me.getFormField(field, {
                    mode: config.mode,
                    defaultValue: defaultValue,
                    linkName: config.linkName
                });

                items.push(formfield);
            }
        });

        // sort attributes on index property
        return items.sort(function (a, b) {
            return a.metadata.index - b.metadata.index;
        });
    },

    /**
     * Get form fields
     * 
     * @param {Ext.data.field.Field} field
     * @param {String} config.mode One of `read`, `create` or `update`.
     * @param {Object} config.defaultValue An object containing default value.
     * @param {String} config.linkName The name of the object linked within the ViewModel.
     * @return {Object} An `Ext.form.field.Field` definition.
     */
    getFormField: function (field, config) {
        var fieldsettings;

        config = Ext.applyIf(config, {
            linkName: this._default_link_name,
            mode: this.formmodes.read
        });

        // append asterisk to label for mandatory fields
        var label = field.attributeconf._description_translation || field.description;

        var bind = {};
        if (config.linkName) {
            bind = {
                value: Ext.String.format('{{0}.{1}}', config.linkName, field.name)
            };
        }

        // base field information
        var formfield = {
            fieldLabel: label,
            labelPad: CMDBuildUI.util.helper.FormHelper.properties.labelPad,
            labelSeparator: CMDBuildUI.util.helper.FormHelper.properties.labelSeparator,
            name: field.name,
            hidden: field.hidden,
            anchor: '100%',
            metadata: field.attributeconf,
            formmode: config.mode,
            bind: bind
        };

        if (config.defaultValue) {
            // add listener to set value when field is added to form
            // to apdate theObject within viewmodel.
            formfield.listeners = {
                beforerender: function (f) {
                    var vm = f.lookupViewModel(true); // get form view model
                    if (config.linkName) {
                        vm.get(config.linkName).set(field.name, config.defaultValue.value);
                    } else {
                        f.setValue(config.defaultValue.value);
                    }
                    if (config.defaultValue.valuedescription) {
                        vm.get(config.linkName).set(
                            Ext.String.format("_{0}_description", field.name),
                            config.defaultValue.valuedescription
                        );
                    }
                }
            };
            if (!Ext.isEmpty(config.defaultValue.editable)) {
                field.writable = config.defaultValue.editable;
            }
        }

        if (
            config.mode !== this.formmodes.read &&
            field.writable &&
            (field.mode != this.fieldmodes.immutable || config.mode === this.formmodes.create)
        ) {
            fieldsettings = this.getEditorForField(
                field, {
                    linkName: config.linkName
                }
            );
        }

        if (!fieldsettings) {
            fieldsettings = this.getReadOnlyField(field, config.linkName);
        }

        // override mandatory behaviour
        if (field.mandatory && !field.hidden) {
            fieldsettings.allowBlank = false;
        }
        Ext.merge(formfield, fieldsettings);

        return formfield;
    },

    /**
     * Returns the editor definition for given field
     * @param {Ext.data.field.Field} field
     * @param {Object} config
     * @param {String} config.linkName
     * @return {Object}
     */
    getEditorForField: function (field, config) {
        var editor;
        config = config || {};

        config = Ext.applyIf(config, {
            linkName: this._default_link_name
        });

        // field configuration
        switch (field.cmdbuildtype.toLowerCase()) {
            /**
             * Boolean field
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                editor = {
                    xtype: 'checkboxfield'
                };
                break;
            /** 
             * Date fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase():
                editor = {
                    xtype: 'datefield',
                    format: CMDBuildUI.util.helper.UserPreferences.getDateFormat(),
                    formatText: ''
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase():
                var format = field.attributeconf.showSeconds ?
                    CMDBuildUI.util.helper.UserPreferences.getTimestampWithSecondsFormat() :
                    CMDBuildUI.util.helper.UserPreferences.getTimestampWithoutSecondsFormat();
                editor = {
                    xtype: 'datefield',
                    format: format,
                    formatText: '',
                    listeners: {
                        expand: function (datefield, eOpts) {
                            var todayBtn = datefield.getPicker().todayBtn;
                            todayBtn.on('click', function () {
                                var picker = datefield.getPicker(),
                                    today = new Date(),
                                    selectToday = function () {
                                        datefield.setValue(today);
                                        datefield.focus();
                                        this.hide();
                                    };
                                this.setHandler(selectToday, picker);
                            });
                        }
                    }
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time.toLowerCase():
                editor = {
                    xtype: 'textfield',
                    vtype: 'time',
                    listeners: {
                        blur: function (field, event, eOpts) {
                            // add left pad to numbers
                            var v = field.getValue();
                            if (v) {
                                var nv = [];
                                v.split(":").forEach(function (n) {
                                    nv.push(n.length === 1 ? "0" + n : n);
                                });
                                field.setValue(nv.join(":"));
                            }
                        }
                    }
                };
                break;
            /**
             * IP field
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress.toLowerCase():
                var vtype;
                if (field.attributeconf.ipType === "ipv4") {
                    vtype = "IPv4Address";
                } else if (field.attributeconf.ipType === "ipv6") {
                    vtype = "IPv6Address";
                } else {
                    vtype = "IPAddress";
                }
                editor = {
                    xtype: 'textfield',
                    vtype: vtype
                };
                break;
            /**
             * Numeric fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal.toLowerCase():
                var maxvalue = Math.pow(10, (field.attributeconf.precision - field.attributeconf.scale));
                editor = {
                    xtype: 'numberfield',
                    mouseWheelEnabled: false,
                    hideTrigger: true,
                    keyNavEnabled: false,
                    mouseWhellEnabled: false,
                    decimalPrecision: field.attributeconf.scale,
                    decimalSeparator: CMDBuildUI.util.helper.UserPreferences.getDecimalsSeparator(),
                    validator: function (v) {
                        if (Ext.isEmpty(v)) {
                            return true;
                        }
                        v = parseFloat(v);
                        if (!(v < maxvalue && v > -maxvalue)) {
                            return false;
                        }
                        return true;
                    }
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double.toLowerCase():
                editor = {
                    xtype: 'numberfield',
                    mouseWheelEnabled: false,
                    hideTrigger: true,
                    keyNavEnabled: false,
                    mouseWhellEnabled: false,
                    decimalPrecision: 100,
                    decimalSeparator: CMDBuildUI.util.helper.UserPreferences.getDecimalsSeparator()
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer.toLowerCase():
                editor = {
                    xtype: 'numberfield',
                    mouseWheelEnabled: false,
                    hideTrigger: true,
                    allowDecimals: false,
                    maxValue: 2147483647, // Integer max value
                    minValue: -2147483648 // Integer min value
                };
                break;
            /**
             * Relation fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                editor = {
                    xtype: 'lookupfield',
                    recordLinkName: config.linkName
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                if (CMDBuildUI.util.helper.ModelHelper.getObjectFromName(field.attributeconf.targetClass, field.attributeconf.targetType)) {
                    editor = {
                        xtype: 'referencefield',
                        recordLinkName: config.linkName
                    };
                }
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase():
                editor = {
                    xtype: 'referencefield',
                    recordLinkName: config.linkName
                };
                break;
            /**
             * Text fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char.toLowerCase():
                editor = {
                    xtype: 'textfield',
                    enforceMaxLength: true,
                    maxLength: 1
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string.toLowerCase():
                editor = {
                    xtype: 'textfield',
                    enforceMaxLength: true,
                    maxLength: field.attributeconf.maxLength
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
                if (field.attributeconf.editorType === "HTML") {
                    editor = {
                        xtype: 'cmdbuildhtmleditor',
                        enableAlignments: true,
                        enableColors: true,
                        enableFont: false,
                        enableFontSize: false,
                        enableFormat: true,
                        enableLinks: true,
                        enableLists: true,
                        enableSourceEdit: true
                    };
                } else {
                    editor = {
                        xtype: 'textareafield',
                        resizable: true
                    };
                }
                break;
            default:
                CMDBuildUI.util.Logger.log("Missing field for " + field.name, CMDBuildUI.util.Logger.levels.warn);
                break;
        }

        // Add help tooltip
        if (editor && field.attributeconf && field.attributeconf.help) {
            var converter = new showdown.Converter();
            var help = converter.makeHtml(field.attributeconf.help);
            editor.labelToolIconQtip = help;
            editor.labelToolIconCls = 'fa-question-circle';
        }

        // add updateVisibility function
        this.addUpdateVisibilityToField(editor, field.attributeconf, config.linkname);

        // add custom validator
        this.addCustomValidator(editor, field.attributeconf, config.linkName);

        // add auto value
        this.addAutoValue(editor, field.attributeconf, config.linkName);

        // append metadata to editor configuration
        if (Ext.isObject(editor) && !Ext.Object.isEmpty(editor)) {
            editor.metadata = field.attributeconf;
        }

        return editor;
    },

    /**
     * 
     * @param {Ext.data.field.Field} field
     * @param {String} linkName 
     * @return {Object}
     */
    getReadOnlyField: function (field, linkName) {        
        // setup readonly fields
        var fieldsettings = {
            xtype: 'displayfield'
        };

        switch (field.cmdbuildtype.toLowerCase()) {
            /**
             * Boolean field
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderBooleanField(value);
                };
                break;
            /**
             * Date fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderDateField(value);
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value, {
                        hideSeconds: !f.metadata.showSeconds
                    });
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderTimeField(value, {
                        hideSeconds: !f.metadata.showSeconds
                    });
                };
                break;
            /**
             * Numeric fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderDecimalField(value, {
                        scale: f.metadata.scale,
                        showThousandsSeparator: f.metadata.showThousandsSeparator,
                        unitOfMeasure: f.metadata.unitOfMeasure,
                        unitOfMeasureLocation: f.metadata.unitOfMeasureLocation
                    });
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderDoubleField(value, {
                        visibleDecimals: f.metadata.visibleDecimals,
                        showThousandsSeparator: f.metadata.showThousandsSeparator,
                        unitOfMeasure: f.metadata.unitOfMeasure,
                        unitOfMeasureLocation: f.metadata.unitOfMeasureLocation
                    });
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer.toLowerCase():
                fieldsettings.renderer = function (value, f) {
                    return CMDBuildUI.util.helper.FieldsHelper.renderIntegerField(value, {
                        showThousandsSeparator: f.metadata.showThousandsSeparator,
                        unitOfMeasure: f.metadata.unitOfMeasure,
                        unitOfMeasureLocation: f.metadata.unitOfMeasureLocation
                    });
                };
                break;
            /**
             * Relation fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                fieldsettings.renderer = function (value, f) {                    
                    var output = "";
                    if (value) {
                        var lookupvalue = CMDBuildUI.model.lookups.Lookup.getLookupValueById(field.attributeconf.lookupType, value);
                        if (lookupvalue) {
                            output = lookupvalue.getFormattedDescription();
                        }
                    }
                    return output;
                };
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                if (linkName) {
                    fieldsettings.bind = {
                        value: Ext.String.format('{{0}._{1}_description}', linkName, field.name)
                    };
                }
                break;
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase():
                if (linkName) {
                    fieldsettings.bind = {
                        value: Ext.String.format('{{0}._{1}_description}', linkName, field.name)
                    };
                }
                break;
            /**
             * Text fields
             */
            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
                fieldsettings.renderer = function (value, field) {
                    if (value && field.metadata.editorType !== "HTML") {
                        value = Ext.util.Format.nl2br(value);
                    }
                    return value;
                };
                break;
        }

        // add updateVisibility function
        this.addUpdateVisibilityToField(fieldsettings, field.attributeconf, linkName);

        return fieldsettings;
    },

    /**
     * Returns store definition for LookUp store.
     * @param {String} type
     * @return {Object} Ext.data.Store definition
     */
    getLookupStore: function (type) {
        return {
            model: 'CMDBuildUI.model.lookups.Lookup',
            proxy: {
                url: CMDBuildUI.util.api.Lookups.getLookupValues(type),
                type: 'baseproxy'
            },
            autoLoad: true
        };
    },

    /**
     * Returns store definition for Reference store.
     * @param {String} type Target type.
     * @param {String} name Target name.
     * @return {Object} Ext.data.Store definition
     */
    getReferenceStore: function (type, name) {
        if (type === 'class') {
            return {
                model: 'CMDBuildUI.model.domains.Reference',
                proxy: {
                    url: '/classes/' + name + '/cards/',
                    type: 'baseproxy'
                },
                autoLoad: true
            };
        }
    },

    /**
     * Return the base form for given model
     * @param {Ext.Model} model
     * @param {Object} config
     * @param {Boolean} config.mode Default value is true.
     * @param {Object[]} config.defaultValues An array of objects containing default values.
     * @param {String} config.linkName The name of the object linked within the ViewModel.
     * @param {Boolean} config.showNotes Show notes as new tab.
     * @param {Boolean} config.showAsFieldsets Set to true for display fieldsets instead of tabs.
     * @param {Object} config.attributesOverrides An object containing properties to override for attributes
     * @param {Boolean} config.attributesOverrides.attributename.writable Override writable property
     * @param {Boolean} config.attributesOverrides.attributename.mandatory Override mandatory property
     * @param {Numeric} config.attributesOverrides.attributename.index
     * @param {String[]} config.visibleAttributes An array containing the names of visible attributes
     * @return {CMDBuildUI.components.tab.FormPanel|CMDBuildUI.components.tab.FieldSet[]}
     */
    renderForm: function (model, config) {
        // set default configuration
        Ext.applyIf(config || {}, {
            readonly: true,
            defaultValues: [],
            linkName: this._default_link_name,
            showNotes: false,
            showAsFieldsets: false,
            attributesOverrides: {},
            visibleAttributes: undefined,
            mode: config.readonly == undefined || config.readonly ? this.formmodes.read : this.formmodes.update
        });

        // get form fields
        var fields = CMDBuildUI.util.helper.FormHelper.getFormFields(model, {
            readonly: config.readonly,
            defaultValues: config.defaultValues,
            linkName: config.linkName,
            attributesOverrides: config.attributesOverrides,
            mode: config.mode
        });

        var tabs = [];
        Ext.Array.each(fields, function (field, index) {
            // add only non-excluded fields
            if (config.visibleAttributes === undefined || Ext.Array.indexOf(config.visibleAttributes, field.name) !== -1) {
                var tab = Ext.Array.findBy(tabs, function (item, index) {
                    return item.reference === field.metadata.group.name;
                });
                // create tab key if not exists
                if (!tab) {
                    tab = {
                        reference: field.metadata.group && field.metadata.group.name || "",
                        description: field.metadata.group && field.metadata.group.label || "",
                        position: tabs.length,
                        fields: []
                    };
                    tabs.push(tab);
                }
                // add item
                tab.fields.push(field);
            }
        });

        // add tenant field
        var tenantfield;
        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled)) {
            var objectdefinition = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(model.objectTypeName, model.objectType);
            var multitenantMode = objectdefinition ? objectdefinition.get("multitenantMode") : null;
            if (
                multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.always ||
                multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.mixed
            ) {
                tenantfield = this.getTenantField(config.mode, multitenantMode, config.linkName);
            }
        }

        // create base pagination
        var items = [];
        var tabindex = 1;
        Ext.Array.each(tabs, function (tab, index) {
            var left = [],
                right = [],
                counter = 0;
            tab.fields.forEach(function (f, fi) {
                if (!f.hidden) {
                    f.tabIndex = tabindex++;
                    if (counter % 2) {
                        right.push(f);
                    } else {
                        left.push(f);
                    }
                    counter++;
                }
            });
            var tabconfig = {
                title: tab.description,
                layout: 'column',
                // layout: 'vbox',
                defaults: {
                    xtype: 'fieldcontainer',
                    columnWidth: 0.5,
                    flex: '0.5',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    layout: 'anchor'
                },
                items: [{
                    items: left
                }, {
                    items: right
                }]
            };

            if (index === 0 && tenantfield) {
                tabconfig = {
                    title: tab.description,
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [{
                        layout: 'column',
                        defaults: tabconfig.defaults,
                        items: [tenantfield, {}]
                    }, {
                        layout: 'column',
                        defaults: tabconfig.defaults,
                        items: tabconfig.items
                    }]
                };
            }
            items.push(tabconfig);
        });

        // add notes in a new page
        if (config && config.showNotes) {
            items.push({
                title: CMDBuildUI.locales.Locales.common.tabs.notes,
                reference: "_notes",
                items: [{
                    xtype: 'displayfield',
                    name: 'Notes',
                    anchor: '100%',
                    padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                    bind: {
                        value: Ext.String.format("{{0}.Notes}", config.linkName)
                    }
                }]
            });
        }

        // return as fieldsets
        if (config.showAsFieldsets) {
            // set fieldset configurations
            for (var i = 0; i < items.length; i++) {
                Ext.apply(items[i], {
                    xtype: 'formpaginationfieldset',
                    collapsible: items.length > 1
                });
            }
            return items;
        }

        // hide tab in tabbar if there is only one tab
        if (items.length === 1) {
            items[0].tabConfig = {
                cls: 'hidden-tab'
            };
        }
        // return tab panel
        return {
            xtype: 'formtabpanel',
            items: items
        };
    },

    privates: {
        _default_link_name: 'theObject',

        /**
         * @param {String} formmode
         * @param {String} multitenantmode
         * @return {Object}
         */
        getTenantField: function (formmode, multitenantmode, linkName) {
            linkName = linkName || this._default_link_name;
            var tenants = CMDBuildUI.util.helper.SessionHelper.getActiveTenants();
            var writable = formmode === this.formmodes.update || formmode === this.formmodes.create;
            if (writable) {
                // add combobox
                return {
                    xtype: 'combobox',
                    viewModel: {
                        formulas: {
                            hidetenantcombo: {
                                bind: {
                                    theobject: '{' + linkName + '}'
                                },
                                get: function (data) {

                                    if (multitenantmode === CMDBuildUI.model.users.Tenant.tenantmodes.always && tenants.length === 1) {
                                        data.theobject.set("_tenant", tenants[0].code);
                                        return true;
                                    }
                                    return false;
                                }
                            }
                        }
                    },
                    labelSeparator: CMDBuildUI.util.helper.FormHelper.properties.labelSeparator,
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.tenant,
                    displayField: 'description',
                    valueField: 'code',
                    queryMode: 'local',
                    anchor: '100%',
                    forceSelection: true,
                    allowBlank: multitenantmode !== CMDBuildUI.model.users.Tenant.tenantmodes.always,
                    bind: {
                        value: '{' + linkName + '._tenant}',
                        hidden: '{hidetenantcombo}'
                    },
                    store: {
                        data: tenants
                    },
                    triggers: {
                        clear: {
                            cls: 'x-form-clear-trigger',
                            handler: function (combo, trigger, eOpts) {
                                combo.clearValue();
                            }
                        }
                    }
                };
            } else {
                if (tenants.length > 1) {
                    return {
                        xtype: 'displayfield',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.tenant,
                        labelSeparator: CMDBuildUI.util.helper.FormHelper.properties.labelSeparator,
                        bind: {
                            value: '{theObject._tenant}'
                        },
                        renderer: function (value, field) {
                            var t = Ext.Array.findBy(tenants, function (i) {
                                return i.code == value;
                            });
                            if (t) {
                                return t.description;
                            }
                            return "";
                        }
                    };
                }
            }
            return;
        },

        /**
         * Add update visibility function to field
         * @param {Object} config Ext.form.Field configuration
         * @param {Object} fieldMeta Field metadata
         * @param {Object} fieldMeta.showIf Show if code
         * @param {String} linkname
         */
        addUpdateVisibilityToField: function (config, fieldMeta, linkname) {
            linkname = linkname || this._default_link_name;
            // Add show if control
            if (config && fieldMeta && !Ext.isEmpty(fieldMeta.showIf)) {
                /* jshint ignore:start */
                var jsfn = Ext.String.format(
                    'function executeShowIf(api) {{0}}',
                    fieldMeta.showIf
                );
                eval(jsfn);
                /* jshint ignore:end */
                config.updateFieldVisibility = function () {
                    var form = this.up("form");
                    var record = form && form.getViewModel() ? form.getViewModel().get(linkname) : null;
                    var api = Ext.apply({
                        record: record
                    }, CMDBuildUI.util.api.Client.getApiForFieldVisibility());

                    // use try / catch to manage errors
                    try {
                        var visibility = executeShowIf(api);
                        if (visibility === true || visibility === "true" || visibility === "enabled") {
                            this.setHidden(false);
                            this.setDisabled(false);
                        } else if (visibility === "disabled") {
                            this.setHidden(false);
                            this.setDisabled(true);
                        } else {
                            this.setHidden(true);
                        }
                    } catch (e) {
                        CMDBuildUI.util.Logger.log(
                            "Error on showIf configuration for field " + this.getFieldLabel(),
                            CMDBuildUI.util.Logger.levels.error,
                            null,
                            e
                        );
                    }
                };
            }
        },

        /**
         * Add update visibility function to field
         * @param {Object} config Ext.form.Field configuration
         * @param {Object} fieldMeta Field metadata
         * @param {Object} fieldMeta.validationRules Validation rules code
         * @param {String} linkname
         */
        addCustomValidator: function (config, fieldMeta, linkname) {
            linkname = linkname || this._default_link_name;
            if (config && fieldMeta && !Ext.isEmpty(fieldMeta.validationRules)) {
                /* jshint ignore:start */
                var jsfn = Ext.String.format(
                    'function executeValidationRules(value, api) {{0}}',
                    fieldMeta.validationRules
                );
                eval(jsfn);
                /* jshint ignore:end */

                config.getValidation = function () {
                    var value = this.getValue();
                    if (this.validation !== null && this.validation !== true) {
                        return this.validation;
                    }

                    var form = this.up("form");
                    var record = form && form.getViewModel() ? form.getViewModel().get(linkname) : null;
                    // var api = ;
                    var api = Ext.apply({
                        record: record
                    }, CMDBuildUI.util.api.Client.getApiForFieldCustomValidator());

                    var errors = null;
                    // use try / catch to manage errors
                    try {
                        errors = executeValidationRules(value, api);
                        if (Ext.isBoolean(errors) && errors === false) {
                            errors = "Error"; // TODO: translate
                        }
                    } catch (e) {
                        CMDBuildUI.util.Logger.log(
                            "Error on validationRules configuration for field " + this.getFieldLabel(),
                            CMDBuildUI.util.Logger.levels.error,
                            null,
                            e
                        );
                    }

                    return errors;
                };
            }
        },

        /**
         * Add auto value script to field
         * @param {Object} config Ext.form.Field configuration
         * @param {Object} fieldMeta Field metadata
         * @param {Object} fieldMeta.validationRules Validation rules code
         * @param {String} linkname
         */
        addAutoValue: function (config, fieldMeta, linkname) {
            linkname = linkname || this._default_link_name;
            if (config && fieldMeta && !Ext.isEmpty(fieldMeta.autoValue)) {
                fieldMeta.autoValue.trim();
                var api = {};
                // extract bind property
                var expr = /^api\.bind(\s?)=(\s?)\[.*\](\s?);/;
                var bind = expr.exec(fieldMeta.autoValue);
                try {
                    if (bind && Ext.isArray(bind) && bind.length) {
                        eval(bind[0]);
                    }
                } catch (err) {
                    CMDBuildUI.util.Logger.log("Error evaluating autoValue binds", CMDBuildUI.util.Logger.levels.error, "", err);
                }

                // evaluate script
                var script = fieldMeta.autoValue.replace(expr, "");
                /* jshint ignore:start */
                try {
                    var jsfn = Ext.String.format(
                        'function executeAutoValue(api) {{0}}',
                        script
                    );
                    eval(jsfn);
                } catch (err) {
                    CMDBuildUI.util.Logger.log("Error evaluating autoValue script", CMDBuildUI.util.Logger.levels.error, "", err);
                    var executeAutoValue = Ext.emptyFn;
                }
                /* jshint ignore:end */

                // get auto value binds
                config.getAutoValueBind = function () {
                    if (api.bind) {
                        var b = {};
                        api.bind.forEach(function (k) {
                            b[k] = Ext.String.format("{{0}.{1}}", linkname, k);
                        });
                        return {
                            bindTo: b
                        };
                    }
                    // bind every object change
                    return {
                        bindTo: '{' + linkname + '}',
                        deep: true
                    };
                };

                // set value from autoValue script
                config.setValueFromAutoValue = function () {
                    var record = this.lookupViewModel().get(linkname);
                    var api = Ext.apply({
                        record: record,
                        setValue: function (value) {
                            record.set(fieldMeta.name, value);
                        }
                    }, CMDBuildUI.util.api.Client.getApiForFieldAutoValue());

                    // execute script
                    try {
                        executeAutoValue(api);
                    } catch (err) {
                        CMDBuildUI.util.Logger.log("Error executing autoValue script", CMDBuildUI.util.Logger.levels.error, "", err);
                    }
                };
            }
        }
    }

});
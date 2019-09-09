Ext.define('CMDBuildUI.util.helper.GridHelper', {
    singleton: true,

    /**
     * Returns columns definition for grids.
     * 
     * @param {Ext.data.field.Field[]} fields
     * @param {Object} config
     * @param {String[]/Boolean} config.allowFilter An array of columns on which enable filters or true to enable filter for each column.
     * @param {Boolean} config.addTypeColumn If `true` a new column is added as first item with object type.
     * @param {Boolean} config.reducedGrid if true shows the reducedGrid columns
     * @return {Object[]} An array of Ext.grid.column.Column definitions.
     */
    getColumns: function (fields, config) {
        var columns = [];
        var me = this;

        config = Ext.applyIf(config || {}, {
            allowFilter: false,
            addTypeColumn: false,
            reducedGrid: false
        });

        if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled)) {
            var type = fields[0].owner.objectType;
            var name = fields[0].owner.objectTypeName;
            var objectdefinition = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(name, type);
            var multitenantMode = objectdefinition ? objectdefinition.get("multitenantMode") : null;
            if (
                multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.always ||
                multitenantMode === CMDBuildUI.model.users.Tenant.tenantmodes.mixed
            ) {
                columns.push({
                    text: CMDBuildUI.locales.Locales.administration.common.labels.tenant,
                    dataIndex: "_tenant",
                    hidden: true,
                    align: 'left',
                    renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                        var tenants = CMDBuildUI.util.helper.SessionHelper.getActiveTenants();
                        var t = Ext.Array.findBy(tenants, function (i) {
                            return i.code == value;
                        });
                        return t.description;
                    }
                });
            }
        }

        if (config.addTypeColumn) {
            columns.push({
                text: CMDBuildUI.locales.Locales.common.grid.subtype,
                dataIndex: "_type",
                hidden: false,
                align: 'left',
                renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                    return CMDBuildUI.util.helper.ModelHelper.getObjectDescription(value);
                }
            });
        }

        var hasvisiblefields = false;
        Ext.Array.each(fields, function (field, index) {
            var column = me.getColumn(field, config);
            if (column) {
                columns.push(column);
                hasvisiblefields = !column.hidden ? true : hasvisiblefields;
            }
        });

        if (!hasvisiblefields) {
            var desccol = Ext.Array.findBy(columns, function (c) {
                return c.dataIndex === "Description" || c.dataIndex === "Name" || c.dataIndex === "Code";
            });
            if (desccol) {
                desccol.hidden = false;
            }
            var namecol = Ext.Array.findBy(columns, function (c) {
                return c.dataIndex === "Name";
            });
            if (namecol) {
                namecol.hidden = false;
            }
        }
        return columns;
    },

    /**
     * Returns column definition.
     * 
     * @param {Ext.data.field.Field} field
     * @param {Object} config
     * @param {String[]/Boolean} config.allowFilter An array of columns on which enable filters or true to enable filter for each column.
     * @param {Boolean} config.reducedGrid if true shows the reducedGrid columns
     * @return {Object} An `Ext.grid.column.Column` definition.
     */
    getColumn: function (field, config) {
        var column;
        var me = this;
        if (!Ext.String.startsWith(field.name, "_") && !field.hidden) {
            column = {
                text: field.attributeconf._description_translation || field.description || field.name,
                dataIndex: field.name,
                attributename: field.attributename,
                hidden: config.reducedGrid ? !field.attributeconf.showInReducedGrid : !field.attributeconf.showInGrid,
                align: 'left'
            };

            switch (field.cmdbuildtype.toLowerCase()) {
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderBooleanField(value);
                    };
                    break;
                /**
                 * Date fields
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase():
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderDateField(value);
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time.toLowerCase():
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderTimeField(value, {
                            hideSeconds: !field.attributeconf.showSeconds
                        });
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase():
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value, {
                            hideSeconds: !field.attributeconf.showSeconds
                        });
                    };
                    break;
                /**
                 * Numeric fields
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal.toLowerCase():
                    column.tdCls = Ext.baseCSSPrefix + "numericcell";
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return Ext.String.format(
                            "<div class=\"{0}cell-content\">{1}</div>",
                            Ext.baseCSSPrefix,
                            CMDBuildUI.util.helper.FieldsHelper.renderDecimalField(value, {
                                scale: field.attributeconf.scale,
                                showThousandsSeparator: field.attributeconf.showThousandsSeparator,
                                unitOfMeasure: field.attributeconf.unitOfMeasure,
                                unitOfMeasureLocation: field.attributeconf.unitOfMeasureLocation
                            })
                        );
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double.toLowerCase():
                    column.tdCls = Ext.baseCSSPrefix + "numericcell";
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return Ext.String.format(
                            "<div class=\"{0}cell-content\">{1}</div>",
                            Ext.baseCSSPrefix,
                                CMDBuildUI.util.helper.FieldsHelper.renderDoubleField(value, {
                                visibleDecimals: field.attributeconf.visibleDecimals,
                                showThousandsSeparator: field.attributeconf.showThousandsSeparator,
                                unitOfMeasure: field.attributeconf.unitOfMeasure,
                                unitOfMeasureLocation: field.attributeconf.unitOfMeasureLocation
                            })
                        );
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer.toLowerCase():
                    column.tdCls = Ext.baseCSSPrefix + "numericcell";
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        return Ext.String.format(
                            "<div class=\"{0}cell-content\">{1}</div>",
                            Ext.baseCSSPrefix,
                                CMDBuildUI.util.helper.FieldsHelper.renderIntegerField(value, {
                                showThousandsSeparator: field.attributeconf.showThousandsSeparator,
                                unitOfMeasure: field.attributeconf.unitOfMeasure,
                                unitOfMeasureLocation: field.attributeconf.unitOfMeasureLocation
                            })
                        );
                    };
                    break;
                /**
                 * Relation fields
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                    CMDBuildUI.model.lookups.LookupType.loadLookupValues(field.attributeconf.lookupType);
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        var output = "";
                        // get formatted description
                        if (value) {
                            var lookupvalue = CMDBuildUI.model.lookups.Lookup.getLookupValueById(field.attributeconf.lookupType, value);
                            if (lookupvalue) {
                                output = lookupvalue.getFormattedDescription();
                            }
                        }

                        // if output is empty get description from record data
                        if (!output) {
                            var translation = record.get("_" + field.name + "_description_translation");
                            var description = record.get("_" + field.name + "_description");
                            output = translation ? translation : description;
                        }

                        return output;
                    };
                    break;

                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.foreignkey.toLowerCase():
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        var description = record.get("_" + field.name + "_description");
                        if (value && description !== undefined) {
                            return description;
                        } else if (value && description == undefined) {
                            var type = field.attributeconf.targetType;
                            var name = field.attributeconf.targetClass;
                            CMDBuildUI.util.helper.ModelHelper.getModel(type, name).then(function (m) {
                                m.load(value, {
                                    callback: function (r) {
                                        record.set("_" + field.name + "_description", r.get("Description"));
                                    }
                                });
                            });
                        }
                    };
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        var description = record.get("_" + field.name + "_description");
                        if (value && description !== undefined) {
                            return description;
                        } else if (value && description == undefined) {
                            var type = field.attributeconf.targetType;
                            var name = field.attributeconf.targetClass;
                            CMDBuildUI.util.helper.ModelHelper.getModel(type, name).then(function (m) {
                                m.load(value, {
                                    callback: function (r) {
                                        record.set("_" + field.name + "_description", r.get("Description"));
                                    }
                                });
                            });
                        }
                    };
                    break;
                /**
                 * Text fields
                 */
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
                    column.renderer = function (value, metaData, record, rowIndex, colIndex, store, view) {
                        if (field.attributeconf.editorType === 'HTML') {
                            value = Ext.util.Format.stripTags(value);
                        }
                        return value;
                    };
                    break;

            }

            // add column filter
            if (config && config.allowFilter !== undefined && (
                    (Ext.isBoolean(config.allowFilter) && config.allowFilter === true) ||
                    (Ext.isArray(config.allowFilter) && config.allowFilter.indexOf(field.name) !== -1)
                )) {
                switch (field.cmdbuildtype.toLowerCase()) {
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.boolean.toLowerCase():
                        column.filter = {
                            type: 'boolean'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.char.toLowerCase():
                        column.filter = {
                            type: 'string',
                            itemDefaults: {
                                maxLength: 1
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date.toLowerCase():
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time.toLowerCase():
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime.toLowerCase():
                        column.filter = {
                            type: 'date'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal.toLowerCase():
                        column.filter = {
                            type: 'numeric',
                            itemDefaults: {
                                decimalPrecision: field.attributeconf.scale
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double.toLowerCase():
                        column.filter = {
                            type: 'numeric'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer.toLowerCase():
                        column.filter = {
                            type: 'numeric',
                            itemDefaults: {
                                decimalPrecision: 0
                            }
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.ipaddress.toLowerCase():
                        column.filter = {
                            type: 'string'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                        var lstore = CMDBuildUI.util.helper.FormHelper.getLookupStore(field.attributeconf.lookupType);
                        lstore.autoLoad = false;
                        column.filter = {
                            type: 'list',
                            cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup,
                            store: lstore,
                            idField: '_id',
                            labelField: 'description'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                        var rstore = CMDBuildUI.util.helper.FormHelper.getReferenceStore(field.attributeconf.targetType, field.attributeconf.targetClass);
                        if (rstore) {
                            rstore.autoLoad = false;
                            column.filter = {
                                type: 'list',
                                cmdbuildtype: CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference,
                                store: rstore,
                                idField: '_id',
                                labelField: 'Description'
                            };
                        }
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string.toLowerCase():
                        column.filter = {
                            type: 'string'
                        };
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text.toLowerCase():
                        column.filter = {
                            type: 'string'
                        };
                        break;
                }
            }
        }
        return column;
    },

    /**
     * Return configuration for print button.
     * @param {Object} config 
     */
    getPrintButtonConfig: function (config) {
        config = config || {};
        var buttonCongif = {
            xtype: 'button',
            ui: 'management-action',
            iconCls: 'x-fa fa-print',
            tooltip: CMDBuildUI.locales.Locales.common.grid.print,
            arrowVisible: false,
            autoEl: {
                'data-testid': 'grid-printbtn'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.grid.print'
            },
            menu: [{
                iconCls: 'x-fa fa-file-pdf-o',
                itemId: 'printPdfBtn',
                text: CMDBuildUI.locales.Locales.common.grid.printpdf,
                printformat: 'pdf',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.grid.printpdf'
                }
            }, {
                iconCls: 'x-fa fa-file-excel-o',
                itemId: 'printCsvBtn',
                text: CMDBuildUI.locales.Locales.common.grid.printcsv,
                printformat: 'csv',
                localized: {
                    text: 'CMDBuildUI.locales.Locales.common.grid.printcsv'
                }
            }]
        };

        return Ext.applyIf(config, buttonCongif);
    },

    /**
     * Return configuration for buffered grid counter.
     * @param {String} storeName
     */
    getBufferedGridCounterConfig: function (storeName) {
        if (storeName) {
            return {
                xtype: 'bufferedgridcounter',
                padding: '0 20 0 0',
                bind: {
                    store: '{' + storeName + '}'
                }
            };
        }
    },

    getProcessFlowStatusColumn: function () {
        return {
            dataIndex: "status",
            enableColumnHide: false,
            hideable: false,
            draggable: false,
            sortable: false,
            menuDisabled: true,
            width: "38px",
            maxWidth: "38px",
            minWidth: "38px",
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                var output = "";
                // get formatted description
                if (value) {
                    var lookupvalue = CMDBuildUI.model.lookups.Lookup.getLookupValueById(CMDBuildUI.model.processes.Process.flowstatus.lookuptype, value);
                    if (lookupvalue) {
                        var icon = lookupvalue.get("icon_font") || "x-fa fa-square";
                        var icon_color = lookupvalue.get("icon_color") || "inherit";
                        var txt = lookupvalue.get("_description_translation") || lookupvalue.get("description");

                        output = Ext.String.format(
                            "<span class=\"{0}\" style=\"color: {1}; cursor: help;\" data-qtip=\"{2}\"></span>",
                            icon,
                            icon_color,
                            txt
                        );
                    }
                }
                return output;
            }
        };
    }
});
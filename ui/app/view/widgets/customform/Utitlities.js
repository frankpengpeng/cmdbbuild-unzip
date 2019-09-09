Ext.define("CMDBuildUI.view.widgets.customform.Utilities", {
    singleton: true,

    getModel: function (theWidget) {
        if (theWidget.get("ModelType").toLowerCase() === 'form') {
            this.createModelFromWidgetDefAttributes(theWidget);
        } else if (theWidget.get("ModelType").toLowerCase() === 'class') {
            this.createModelFromClassAttributes(theWidget);
        } else if (theWidget.get("ModelType").toLowerCase() === 'function') {
            // TODO: Show error message - not yet implemented
        } else {
            // TODO: Show error message
        }
    },


    /**
     * Load data
     * @param {Boolean} force If `true` data is always readed from the server or from the configuration.
     * Data saved in output variable in target object will be ignored.
     */

    loadData: function (theWidget, theTarget, callback) {
        // get data from configuration or server
        if (theWidget.get("DataType")) {
            if (theWidget.get("DataType").toLowerCase() === 'raw' || theWidget.get("DataType").toLowerCase() === 'raw_json') {
                var rawJson = theWidget.get('RawData');
                this.loadDataFromJson(rawJson, callback);
            } else if (theWidget.get("DataType").toLowerCase() === 'raw_text') {
                var rawData = theWidget.get('RawData');
                var serializationconfig = this.getSerializationConfig(theWidget);
                this.loadDataFromRawText(rawData, serializationconfig, callback);
            } else if (theWidget.get("DataType").toLowerCase() === 'function') {
                this.loadDataFromFunction(theWidget, theTarget, callback);
            } else {
                // TODO: Show error message
            }
        }

    },

    createModelFromWidgetDefAttributes: function (theWidget) {
        var me = this;
        me.resetModelAttributes();
        var str_attributes = theWidget.get("FormModel");
        var attributes_def = Ext.JSON.decode(str_attributes, true);
        if (attributes_def) {
            attributes_def.forEach(function (attribute_def, i) {
                var attr_def = Ext.applyIf(attribute_def, {
                    index: i,
                    showInGrid: attribute_def.showColumn !== undefined ? !!attribute_def.showColumn : true,
                    writable: true
                });
                if (attribute_def.filter) {
                    attribute_def.filter = attribute_def.filter.expression;
                }
                if (!attribute_def.targetClass && !Ext.isEmpty(attribute_def.target)) {
                    attribute_def.targetClass = attribute_def.target.name;
                    attribute_def.targetType = attribute_def.target.type;
                }
                me.addModelAttribute(Ext.create("CMDBuildUI.model.Attribute", attr_def));
            });
            me.createModel(me.getModelName(theWidget));
        }
    },

    createModelFromClassAttributes: function (theWidget) {
        var me = this;
        me.resetModelAttributes();
        var targetTypeName = theWidget.get("ClassModel");
        var allowedattributes;
        if (!Ext.isEmpty(theWidget.get("ClassAttributes"))) {
            allowedattributes = theWidget.get("ClassAttributes").split(",");
        }

        var baseurl = CMDBuildUI.util.helper.ModelHelper.getBaseUrl(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            targetTypeName
        );

        // create attributes store and load class attributes
        var attributesStore = Ext.create('Ext.data.Store', {
            model: 'CMDBuildUI.model.Attribute',

            proxy: {
                url: baseurl + '/attributes',
                type: 'cmdbuildattributesproxy'
            },
            autoDestroy: true,
            pageSize: 0 // disable pagination
        });

        // load attributes
        attributesStore.load(function (records, operation, success) {
            if (success) {
                records.forEach(function (record, i) {
                    if ((Ext.isEmpty(allowedattributes) || allowedattributes.indexOf(record.get("name")) !== -1) &&
                        record.get("active") &&
                        !Ext.Array.contains(CMDBuildUI.util.helper.ModelHelper.ignoredFields, record.get("name"))
                    ) {
                        me.addModelAttribute(record);
                    }
                });
                me.createModel(me.getModelName(theWidget));
            }
        });

    },

    loadDataFromJson: function (rawJson, callback) {
        var data = [];
        data = Ext.JSON.decode(rawJson);
        Ext.asap(function () {
            Ext.callback(callback, null, [data]);
        });
    },

    loadDataFromRawText: function (rawData, serializationconfig, callback) {
        if (rawData) {
            var data = [];
            rawData.split(serializationconfig.rowseparator).forEach(function (srow) {
                var row = {};
                srow.split(serializationconfig.attributeseparator).forEach(function (sattribute) {
                    var splitted_attr = sattribute.split(serializationconfig.keyseparator);
                    if (splitted_attr.length === 2) {
                        row[splitted_attr[0]] = splitted_attr[1];
                    } else if (splitted_attr.length === 1) {
                        row[splitted_attr[0]] = null;
                    }
                });
                data.push(row);
            });
            Ext.asap(function () {
                Ext.callback(callback, null, [data]);
            });
        }
    },

    loadDataFromFunction: function (theWidget, theTarget, callback) {
        var me = this;
        var fnName = theWidget.get("FunctionData");
        if (!fnName) {
            // TODO: return error - bad configuration
            return;
        }
        Ext.Ajax.request({
            url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Functions.getFunctionByNameUrl(fnName),
            method: "GET",
            callback: function (opitons, success, response) {
                if (response.status < 400) { // has no errors
                    var responseJson = Ext.JSON.decode(response.responseText, true);
                    var fn = Ext.create("CMDBuildUI.model.Function", responseJson.data);

                    // check for paramenters
                    var fn_parameters = {};
                    if (fn.get("parameters") && fn.get("parameters").length) {
                        var parameters = fn.get("parameters");
                        parameters.forEach(function (parameter) {
                            fn_parameters[parameter.name] = me.extractVariableFromString(theWidget.get(parameter.name), theTarget);
                        });
                    }

                    // model configuration
                    var fn_model = {
                        output: []
                    };

                    me.getModelAttributes().forEach(function (attribute) {
                        switch (attribute.get("type").toLowerCase()) {
                            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase():
                                fn_model.output.push({
                                    name: attribute.get("name"),
                                    type: 'lookup',
                                    lookupType: attribute.get("lookupType")
                                });
                                break;
                            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference.toLowerCase():
                                fn_model.output.push({
                                    name: attribute.get("name"),
                                    type: 'foreignkey',
                                    fkTarget: attribute.get("targetClass")
                                });
                                break;
                        }
                    });

                    // load function results
                    Ext.Ajax.request({
                        url: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Functions.getFunctionOutputsByNameUrl(fnName),
                        method: "GET",
                        params: {
                            parameters: Ext.JSON.encode(fn_parameters),
                            model: fn_model.output.length > 0 ? Ext.JSON.encode(fn_model) : null
                        },
                        callback: function (fopitons, fsuccess, fresponse) {
                            if (fresponse.status < 400) { // has no errors
                                var fresponseJson = Ext.JSON.decode(fresponse.responseText, true);
                                Ext.asap(function () {
                                    Ext.callback(callback, me, [fresponseJson.data]);
                                });
                            }
                        }
                    });
                }
            }
        });
    },

    /**
     * Resolve variable.
     * @param {String} variable
     * @return {*} The variable resolved.
     */
    extractVariableFromString: function (variable, theTarget) {
        if (Ext.isString(variable) && CMDBuildUI.util.api.Client.testRegExp(/^{(client|server)+:*.+}$/, variable)) {
            variable = variable.replace("{", "").replace("}", "");
            var s_variable = variable.split(":");
            var result;
            if (s_variable[0] === "server") {
                result = CMDBuildUI.util.ecql.Resolver.resolveServerVariables([s_variable[1]], theTarget);
                return result[s_variable[1]];
            } else if (s_variable[0] === "client") {
                result = CMDBuildUI.util.ecql.Resolver.resolveClientVariables([s_variable[1]], theTarget);
                return result[s_variable[1]];
            }
        } else {
            return variable;
        }
    },

    privates: {

        _model_attributes: [],

        resetModelAttributes: function () {
            this._model_attributes = [];
        },

        /**
         * @param {CMDBuildUI.model.Attribute} attribute
         */
        addModelAttribute: function (attribute) {
            this._model_attributes.push(attribute);
        },

        /**
         * @return {CMDBuildUI.model.Attribute[]}
         */
        getModelAttributes: function () {
            return this._model_attributes;
        },

        /**
         * Create model using given attributes.
         */
        createModel: function (modelname) {
            var attributes = this._model_attributes;
            // sort attributes
            attributes.sort(function (a, b) {
                var ai = a.data.index || 0,
                    bi = b.data.index;
                return ai === bi ? 0 : (ai < bi ? -1 : 1);
            });

            // get fields
            var fields = [];
            for (var i = 0; i < attributes.length; i++) {
                var field = CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attributes[i]);
                if (field) {
                    fields.push(field);
                }
            }

            // create model
            Ext.define(modelname, {
                extend: 'CMDBuildUI.model.base.Base',
                fields: fields,
                proxy: 'memory'
            });

        },

        getModelName: function (theWidget) {
            return 'CMDBuildUI.model.customform.' + theWidget.getId();
        },

        /**
         * Get serialization configs.
         * @return {Object} An object containing `type`, `keyseparator`, `attributeseparator` and `rowseparator`.
         */
        getSerializationConfig: function (configs) {
            return {
                type: configs.get("SerializationType") || 'text',
                keyseparator: configs.get("KeyValueSeparator") || '=',
                attributeseparator: configs.get("AttributesSeparator") || ',',
                rowseparator: configs.get("RowsSeparator") || '\n'
            };
        },

        serialize: function (theWidget, response) {

            var serializationconfig = this.getSerializationConfig(theWidget);
            var model = Ext.ClassManager.get(this.getModelName(theWidget));
            var modelAttributes = {};
            var rows = [];
            model.getFields().forEach(function (field) {
                if (field.getName() !== "_id") {
                    modelAttributes[field.getName()] = field.cmdbuildtype.toLowerCase();
                }
            });

            if (serializationconfig.type === "json") {
                response.forEach(function (row) {
                    var r = {};
                    // save only model attributes
                    Ext.Object.each(row.getData(), function (k, v) {
                        if (modelAttributes[k]) {
                            switch (modelAttributes[k]) {
                                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date:
                                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime:
                                    v = Ext.Date.format(v, "c");
                                    break;
                            }
                            r[k] = v;
                        }
                    });
                    rows.push(r);
                });
            } else {
                response.forEach(function (row) {
                    var rdata = row.data ? row.getData() : row;
                    var attributes = [];
                    for (var k in rdata) {
                        // save only model attributes
                        if (modelAttributes[k]) {
                            var v = rdata[k];
                            switch (modelAttributes[k]) {
                                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date:
                                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime:
                                    v = Ext.Date.format(v, "c");
                                    break;
                            }
                            attributes.push(Ext.String.format("{0}{1}{2}", k, serializationconfig.keyseparator, v));
                        }
                    }
                    rows.push(attributes.join(serializationconfig.attributeseparator));
                });
            }

            return rows.join(serializationconfig.rowseparator);
        }

    }

});
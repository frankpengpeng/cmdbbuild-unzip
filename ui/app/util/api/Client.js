Ext.define('CMDBuildUI.util.api.Client', {
    singleton: true,

    /**
     * @cfg {Ext.data.Model} record
     */
    record: null,

    /********** API GETTERS **********/

    /**
     * @return {Object} Api available for field visibility scripts.
     */
    getApiForFieldVisibility: function () {
        return {
            getValue: this.getValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            testRegExp: this.testRegExp
        };
    },

    /**
     * @return {Object} Api available for field custom validator scripts.
     */
    getApiForFieldCustomValidator: function () {
        return {
            getValue: this.getValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            testRegExp: this.testRegExp
        };
    },

    /**
     * @return {Object} Api available for field custom validator scripts.
     */
    getApiForFieldAutoValue: function () {
        return {
            getValue: this.getValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode
        };
    },

    /**
     * @return {Object} Api available for form before create scritps.
     */
    getApiForFormBeforeCreate: function () {
        return {
            getValue: this.getValue,
            setValue: this.setValue,
            getLookupCode: this.getLookupCode,
            setLookupByCode: this.setLookupByCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode
        };
    },

    /**
     * @return {Object} Api available for form after create scritps.
     */
    getApiForFormAfterCreate: function () {
        return {
            getValue: this.getValue,
            setValue: this.setValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            saveRecord: this.saveRecord,
            goToResource: this.goToResource,
            openPopup: this.openPopup,
            makeRequest: this.makeRequest
        };
    },

    /**
     * @return {Object} Api available for form before clone scritps.
     */
    getApiForFormBeforeClone: function () {
        return {
            getValue: this.getValue,
            setValue: this.setValue,
            getLookupCode: this.getLookupCode,
            setLookupByCode: this.setLookupByCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode
        };
    },

    /**
     * @return {Object} Api available for form after clone scritps.
     */
    getApiForFormAfterClone: function () {
        return {
            getValue: this.getValue,
            setValue: this.setValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            saveRecord: this.saveRecord,
            goToResource: this.goToResource,
            openPopup: this.openPopup,
            makeRequest: this.makeRequest
        };
    },

    /**
     * @return {Object} Api available for form before edit scritps.
     */
    getApiForFormBeforeEdit: function () {
        return {
            getValue: this.getValue,
            setValue: this.setValue,
            getLookupCode: this.getLookupCode,
            setLookupByCode: this.setLookupByCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode
        };
    },

    /**
     * @return {Object} Api available for form after edit scritps.
     */
    getApiForFormAfterEdit: function () {
        return {
            getValue: this.getValue,
            setValue: this.setValue,
            getLookupCode: this.getLookupCode,
            getLookupDescription: this.getLookupDescription,
            getReferenceDescription: this.getReferenceDescription,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode,
            saveRecord: this.saveRecord,
            goToResource: this.goToResource,
            openPopup: this.openPopup,
            makeRequest: this.makeRequest
        };
    },

    /**
     * @return {Object} Api available for form after edit scritps.
     */
    getApiForContextMenu: function () {
        return {
            updateRecord: this.updateRecord,
            goToResource: this.goToResource,
            openPopup: this.openPopup,
            makeRequest: this.makeRequest,
            refreshGrid: this.refreshGrid,
            getRemoteCard: this.getRemoteCard,
            getRemoteProcessInstance: this.getRemoteProcessInstance,
            getRemoteLookupFromCode: this.getRemoteLookupFromCode
        };
    },

    privates: {
        /********** API DEFINITION **********/

        /**
         * Get the value of a form attribute.
         * 
         * @param {String} attribute
         * @return {Strin|Numeric|Object}
         */
        getValue: function (attribute) {
            if (this.record) {
                return this.record.get(attribute);
            } else {
                CMDBuildUI.util.Logger.log(
                    "Record is not evaluated",
                    CMDBuildUI.util.Logger.levels.warn
                );
            }
        },

        /**
         * Set the value of a form attribute.
         * 
         * @param {String} attribute
         * @param {Strin|Numeric|Object} value
         * @return {Strin|Numeric|Object}
         */
        setValue: function (attribute, value) {
            if (this.record) {
                return this.record.set(attribute, value);
            } else {
                CMDBuildUI.util.Logger.log(
                    "Record is not evaluated",
                    CMDBuildUI.util.Logger.levels.warn
                );
            }
        },

        /**
         * Get the code of a lookup form attribute.
         * 
         * @param {String} attribute
         * @return {Strin}
         */
        getLookupCode: function (attribute) {
            if (this.record) {
                return this.record.get("_" + attribute + "_code");
            } else {
                CMDBuildUI.util.Logger.log(
                    "Record is not evaluated",
                    CMDBuildUI.util.Logger.levels.warn
                );
            }
        },

        /**
         * Get the code of a lookup form attribute.
         * 
         * @param {String} attribute
         * @return {Strin}
         */
        setLookupByCode: function (attribute, code) {
            //TODO: implement
        },

        /**
         * Get the description of a lookup form attribute.
         
         * @param {String} attribute
         * @return {Strin}
         */
        getLookupDescription: function (attribute) {
            if (this.record) {
                return this.record.get("_" + attribute + "_description_translation");
            } else {
                CMDBuildUI.util.Logger.log(
                    "Record is not evaluated",
                    CMDBuildUI.util.Logger.levels.warn
                );
            }
        },

        /**
         * Get the description of a reference form attribute.
         * 
         * @param {String} attribute
         * @return {Strin}
         */
        getReferenceDescription: function (attribute) {
            if (this.record) {
                return this.record.get("_" + attribute + "_description");
            } else {
                CMDBuildUI.util.Logger.log(
                    "Record is not evaluated",
                    CMDBuildUI.util.Logger.levels.warn
                );
            }
        },

        /**
         * Test a regular expression on specified value.
         * 
         * @param {RegExp} regex
         * @param {String} value
         * @return {Boolean}
         */
        testRegExp: function (regexp, value) {
            if (!regexp || !(regexp instanceof RegExp)) {
                CMDBuildUI.util.Logger.log(
                    "RegExp not valid!",
                    CMDBuildUI.util.Logger.levels.error,
                    null, {
                        regexp: regexp
                    }
                );
            }
            return regexp.test(value);
        },

        /**
         * Open a resource using routing.
         * 
         * @param {String} path
         */
        goToResource: function (path) {
            if (path) {
                CMDBuildUI.util.Utilities.redirectTo(path, true);
            } else {
                CMDBuildUI.util.Logger.log(
                    "No path defined",
                    CMDBuildUI.util.Logger.levels.warn
                );
            }
        },

        /**
         * Update a record with given data.
         * 
         * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} record
         * @param {Object} data
         * @param {Function} callback
         */
        updateRecord: function (record, data, callback) {
            data = data || {};
            if (record) {
                for (var key in data) {
                    if (data.hasOwnProperty(key) && (record.getField(key) || key === "_advance" || key === "_activity_id")) {
                        record.set(key, data[key]);
                    } else {
                        CMDBuildUI.util.Logger.log(
                            Ext.String.format("Object has not {0} field.", key),
                            CMDBuildUI.util.Logger.levels.warn
                        );
                    }
                }
                var params = {};
                if (callback && Ext.isFunction(callback)) {
                    params.callback = callback;
                }
                record.save(params);
            } else {
                CMDBuildUI.util.Logger.log(
                    "Record is not evaluated",
                    CMDBuildUI.util.Logger.levels.warn
                );
            }
        },

        /**
         * @param {Function} 
         * callback Function to execute when operation completed.
         * @cfg {Ext.data.Model[]} callback.records Array of records.
         * @cfg {Ext.data.operation.Operation} callback.operation The Operation itself.
         * @cfg {Boolean} callback.success True when operation completed successfully.
         */
        saveRecord: function(callback) {
            if (this.record) {
                return this.record.save({
                    callback: callback
                });
            } else {
                CMDBuildUI.util.Logger.log(
                    "Record is not evaluated",
                    CMDBuildUI.util.Logger.levels.warn
                );
            }
        },

        /**
         * Refresh grid.
         */
        refreshGrid: function() {
            this._grid.getStore().load();
        },

        /**
         * @param {String} text
         */
        showMessage: function(text) {
            //TODO: implement
        },

        getCurrentActivityCode: function () {
            //TODO: implement
        },

        openPopup: function () {
            //TODO: implement
        },

        makeRequest: function () {
            //TODO: implement
        },

        /**
         * 
         * @param {String} className 
         * @param {Numeric} cardId 
         * 
         * @return {Ext.promise.Promise}
         */
        getRemoteCard: function(className, cardId) {
            var deferred = new Ext.Deferred();
            // get model
            CMDBuildUI.util.helper.ModelHelper.getModel(CMDBuildUI.util.helper.ModelHelper.objecttypes.klass, className).then(function(model) {
                if (model) {
                    model.load(cardId, {
                        success: function(record, operation) {
                            deferred.resolve(record);
                        },
                        failure: function(record, operation) {
                            CMDBuildUI.util.Logger.log("Card not found for class " + className, CMDBuildUI.util.Logger.levels.warn);
                            deferred.reject();
                        }
                    });
                } else {
                    CMDBuildUI.util.Logger.log("Class model definition for " + className + " not found.", CMDBuildUI.util.Logger.levels.warn);
                    deferred.reject();
                }
            });
            return deferred.promise;
        },

        /**
         * 
         * @param {String} processName 
         * @param {Numeric} instanceId 
         * 
         * @return {Ext.promise.Promise}
         */
        getRemoteProcessInstance: function(processName, instanceId) {
            var deferred = new Ext.Deferred();
            // get model
            CMDBuildUI.util.helper.ModelHelper.getModel(CMDBuildUI.util.helper.ModelHelper.objecttypes.process, processName).then(function(model) {
                if (model) {
                    model.load(instanceId, {
                        success: function(record, operation) {
                            deferred.resolve(record);
                        },
                        failure: function(record, operation) {
                            CMDBuildUI.util.Logger.log("Instance not found for process " + processName, CMDBuildUI.util.Logger.levels.warn);
                            deferred.reject();
                        }
                    });
                } else {
                    CMDBuildUI.util.Logger.log("Instance model definition for " + processName + " not found.", CMDBuildUI.util.Logger.levels.warn);
                    deferred.reject();
                }
            });
            return deferred.promise;
        },

        /**
         * 
         * @param {String} type 
         * @param {String} code 
         * 
         * @return {Ext.promise.Promise}
         */
        getRemoteLookupFromCode: function(type, code) {
            var deferred = new Ext.Deferred();
            var lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(type);
            lt.getLookupValues().then(
                function(values) {
                    var lookupvalue = values.findRecord("code", code, 0, false, true, true);
                    if (lookupvalue) {
                        deferred.resolve(lookupvalue);
                    } else {
                        deferred.reject();
                    }
                }, function() {
                    deferred.reject();
                }
            );
            return deferred.promise;
        }
    }
});
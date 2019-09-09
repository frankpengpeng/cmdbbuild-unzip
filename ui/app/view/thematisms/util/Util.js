Ext.define('CMDBuildUI.thematisms.util.Util', {
    singleton: true,

    /**
     * 
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism 
     * @param {Function} callback;
     * @param {Function} scope
     */
    calculateRules: function (thematism, callback, scope) {
        var analisisType = thematism.get('analysistype');

        switch (analisisType) {
            case CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual:
                this.calculatePunctualRules(thematism, callback, scope);
                break;
            default:
                console.error();
        }
    },

    /**
     * 
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism 
     * @returns {Ext.data.Store}
     */
    createStore: function (thematism) {
        var attribute = this.getTargetAttribute(thematism);
        var objectType = thematism.get('owner');

        return Ext.create('Ext.data.Store', {
            fields: [{
                name: attribute,
                type: 'auto'
            }],
            proxy: {
                type: 'baseproxy',
                url: CMDBuildUI.util.api.Classes.getCardsUrl(objectType)
            },
            autoLoad: false,
            autoDestroy: true
        });
    },

    /**
     * 
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism
     * @param {Function} callback;
     * @param {Function} scope
     */
    calculatePunctualRules: function (thematism, callback, scope) {
        var attribute = this.getTargetAttribute(thematism);
        var store = this.createStore(thematism);

        store.load({
            params: {
                distinct: attribute
                // count: attribute
            },
            callback: function (records, operation, success) {
                var rules = [];
                records.forEach(function (record) {
                    if (!Ext.isEmpty(record.get(attribute))) {
                        rules.push(Ext.create('CMDBuildUI.model.thematisms.Rules', {
                            attribute: attribute,
                            operator: CMDBuildUI.model.thematisms.Thematism.operator.equal,
                            value: record.get(attribute),
                            style: {
                                color: '#' + Math.random().toString(16).substr(-6)
                            }
                        }));
                    }
                });

                //default rule
                rules.push(Ext.create('CMDBuildUI.model.thematisms.Rules', {
                    attribute: CMDBuildUI.model.thematisms.Rules.default.attribute,
                    style: {
                        color: '#' + Math.random().toString(16).substr(-6)
                    }
                }));

                callback.call(scope, thematism, rules)
            },
            scope: this
        })
    },

    /**
     * 
     * @param {CMDBuildUI.model.thematisms.Thematism} thematism 
     * @param {Function} callback
     * @param {Object} scope
     * //TODO: create a static model
     */
    calculateLegend: function (thematism, callback, scope) {
        var attribute = this.getTargetAttribute(thematism);

        if (!attribute) {
            callback.call(scope, []);
            return;
        }

        var store = this.createStore(thematism);

        store.load({
            params: {
                distinct: attribute,
                count: attribute
            },
            callback: function (records, operation, success) {
                if (success) {
                    var legenddata = [];
                    var rules = thematism.rules();

                    records.forEach(function (record) {
                        var distinctValue = record.get(attribute);
                        var viewValue = record.get(Ext.String.format('_{0}_description_translation', attribute)) || record.get(Ext.String.format('_{0}_description', attribute)) || distinctValue;
                        var count = record.get("_count");
                        var recordIndex = rules.find('value', record.get(attribute));

                        if (recordIndex != -1) {
                            legenddata.push(Ext.create('CMDBuildUI.model.thematisms.LegendModel', {
                                value: distinctValue,
                                viewValue: viewValue,
                                count: count,
                                color: rules.getAt(recordIndex).get('style').color
                            }))
                        } else {
                            legenddata.push(Ext.create('CMDBuildUI.model.thematisms.LegendModel', {
                                value: distinctValue,
                                viewValue: viewValue,
                                count: count,
                                color: thematism.getDefaultStyle().color
                            }));
                        }
                    });
                    callback.call(scope, legenddata);
                }
            },
            scope: this
        });
    },

    privates: {

        /**
         * 
         * @param {CMDBuildUI.model.thematisms.Thematism} thematism 
        **/
        getTargetAttribute: function (thematism) {
            return thematism.get('_targetClassAttribute') || thematism.getTargetClassAttribute();
        }
    }


});
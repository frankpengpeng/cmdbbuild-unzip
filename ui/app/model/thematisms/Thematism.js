Ext.define('CMDBuildUI.model.thematisms.Thematism', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        analysistypes: {
            intervals: 'intervals',
            punctual: 'punctual',
            graduated: 'graduated'
        },
        sources: {
            table: 'table',
            function: 'function'
        },
        operator: {
            equal: 'equal'
        }
    },

    fields: [{
        name: 'name',
        type: 'string',
        critical: true
    }, {
        name: 'description', //intervallo, punctual, graduated 
        type: 'string',
        critical: true
    }, {
        name: 'owner',
        type: 'string',
        critical: true
    }, {
        name: 'attribute',
        type: 'string',
        critical: true
    }, {
        name: 'type', //Tabella, funzione
        type: 'string',
        critical: true
    }, {
        name: 'function',
        type: 'string',
        critical: true
    }, {
        name: 'analysistype',
        type: 'string',
        defaultValue: 'punctual', //TODO: used for test. to remove
        critical: true
    }, {
        name: 'rules',
        type: 'auto',
        critical: true,
        convert: function (value, record) {
            if (!value) return [];
            return JSON.parse(value);
        },
        serialize: function (value, record) {
            return JSON.parse(record.stringifyThematism());
        }
    }, {
        name: 'global',
        type: 'boolean',
        critical: true,
        defaultValue: true
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.thematisms.Rules',
        name: 'rules'
    }],

    proxy: {
        type: 'baseproxy'
    },

    stringifyThematism: function () {
        var stringrules = "";
        var s;

        this.rules().getRange().forEach(function (rule) {
            var attribute = rule.get('attribute');

            //handle the default case
            if (attribute == CMDBuildUI.model.thematisms.Rules.default.attribute) {
                s = Ext.String.format('{"condition": {}, "style": {0}}',
                    JSON.stringify(rule.get('style')));
            } else {
                var operator = rule.get('operator');
                var value = rule.get('value');
                var style = rule.get('style');

                //FIXME: when between operato is inserted will need a review
                s = Ext.String.format(
                    '{"condition" : {"attribute": {"simple": {"attribute": "{0}", "operator": "{1}", "value": ["{2}"]}}}, "style": {3}}',
                    attribute,
                    operator,
                    value,
                    JSON.stringify(style)
                );
            }

            if (!stringrules) {
                stringrules = s;
            } else {
                stringrules = Ext.String.format("{0},{1}", stringrules, s);
            }
        }, this);

        return Ext.String.format('[{0}]', stringrules);
    },

    /**
     * This function get's the analysis type of the thematism based on the operators used in the rules
     * @returns {String} the operation types
     */
    getAnalysisType: function () {
        var operation = this.getOperationType();

        switch (operation) {
            case CMDBuildUI.model.thematisms.Thematism.operator.equal:
                return CMDBuildUI.model.thematisms.Thematism.analysistypes.punctual;
            default:
                return this.get('analysistype');
        }

    },
    /**
     * This function tells wich classAttribute is affected by the thematism
     */
    getTargetClassAttribute: function () {
        var rulesRange = this.rules().getRange();

        if (rulesRange && rulesRange.length) {
            return rulesRange[0].get('attribute');
        }
    },

    /**
     * 
     */
    getOperationType: function () {
        var rulesRange = this.rules().getRange();

        if (rulesRange && rulesRange.length) {
            return rulesRange[0].get('operator');
        }
    },

    getDefaultStyle: function () {
        var rulesRange = this.rules().getRange();

        if (rulesRange && rulesRange.length) {
            return rulesRange[rulesRange.length - 1].get('style');
        }
    },

    /**
     * 
     * @param {Function} callback 
     * @param {Object} scope 
     * @param {Boolean} calltype Specify if calling the tryRules or using the id
     */
    calculateResults: function (callback, scope, calltype) {
        var owner = this.get('owner');
        var thematismId = this.getId();
        var me = this;
        if (!calltype) {//if the thematism is saved on the database
            Ext.Ajax.request({
                url: CMDBuildUI.util.api.Classes.getThematismResultUrl(owner, thematismId),
                method: 'GET',
                callback: function (request, success, response) {
                    var parsedResponse = JSON.parse(response.responseText);
                    var data = parsedResponse.data;
                    me.set('result', data);

                    if (callback) {
                        callback.call(scope, data);
                    }
                }
            });
        } else { //if the thematism is not saved
            var jsonData = {
                name: this.get('name'),
                description: this.get('description'),
                owner: owner,
                attribute: this.get('attribute'),
                type: this.get('type'),
                rules: JSON.parse(this.stringifyThematism())
            }
            Ext.Ajax.request({
                url: CMDBuildUI.util.api.Classes.getThematismResultUrl(owner),
                method: 'POST',
                jsonData: jsonData,
                callback: function (request, success, response) {
                    var parsedResponse = JSON.parse(response.responseText);
                    var data = parsedResponse.data;
                    me.set('result', data);

                    if (callback) {
                        callback.call(scope, data);
                    }
                }
            })
        }
    },

    hasRules: function () {
       return this.rules().getRange().length? true : false;
    }
});
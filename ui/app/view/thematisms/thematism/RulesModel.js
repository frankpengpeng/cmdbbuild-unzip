Ext.define('CMDBuildUI.view.thematisms.thematism.RulesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.thematisms-thematism-rules',
    data: {
        // legendhidden: true
    },
    formulas: {
        _thematismRules: {
            bind: {
                thematism: '{theThematism}'
            },
            get: function (data) {
                if (data.thematism) {
                    if (this.getView().config.needListener) {
                        this.getView().mon(data.thematism.rules(), 'datachanged', this.setrules, this)
                    }
                    this.setrules.call(this);
                }
            }
        }
    },
    stores: {
        legendstore: {
            model: 'CMDBuildUI.model.thematisms.LegendModel',
            proxy: {
                type: 'memory'
            },
            data: '{legenddata}'
        }
    },

    setrules: function () {
        CMDBuildUI.thematisms.util.Util.calculateLegend(
            this.get('theThematism'),
            /**
             * 
             * @param {[CMDBuildUI.model.thematisms.LegendModel]} legenddata 
             */
            function (legenddata) {
                try {
                    this.set('legenddata', legenddata);
                } catch (err) { }
            }, this);
    }

});

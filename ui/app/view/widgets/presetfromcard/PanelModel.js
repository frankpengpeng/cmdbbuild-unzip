Ext.define('CMDBuildUI.view.widgets.presetfromcard.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.widgets-presetfromcard-panel',

    data: {
        objectType: null,
        objectTypeName: null,
        selection: null,
        model: null,
        storeinfo: {
            advancedfilter: null,
            autoload: false
        }
    },

    formulas: {
        updateData: {
            bind: {
                model: '{model}',
                widget: '{theWidget}',
                target: '{theTarget}'
            },
            get: function(data) {
                if (data.model && data.widget && data.target) {
                    this.set("storeinfo.modelname", data.model.getName());
                    
                    if (data.widget.get("_Filter_ecql")) {
                        // calculate ecql
                        var ecql = CMDBuildUI.util.ecql.Resolver.resolve(
                            data.widget.get("_Filter_ecql"),
                            data.target
                        );
                        if (ecql) {
                            this.set("storeinfo.advancedfilter", {
                                ecql: ecql
                            });
                        }
                    }
                    this.set("storeinfo.autoload", true);
                }
            }
        }
    },

    stores: {
        // grid data
        gridrows: {
            type: 'classes-cards',
            model: '{storeinfo.modelname}',
            autoLoad: '{storeinfo.autoload}',
            advancedFilter: '{storeinfo.advancedfilter}',
            autoDestroy: true
        }
    }
});

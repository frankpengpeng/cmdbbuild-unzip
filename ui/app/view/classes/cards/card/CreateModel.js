Ext.define('CMDBuildUI.view.classes.cards.card.CreateModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-card-create',

    formulas: {
        /**
         * class object by type name
         */
        classObject: {
            bind: {
                typename: '{objectTypeName}'
            },
            get: function(data) {
                return CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.typename);
            }
        },

        title: function(get) {
            return this.getView().getObjectTypeName();
        }
    }

});

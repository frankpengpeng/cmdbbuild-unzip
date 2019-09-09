Ext.define('CMDBuildUI.view.classes.cards.card.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-card-edit',

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

        updateDescription: {
            bind: {
                description: '{theObject.Description}'
            },
            get: function(data) {
                this.getParent().set("objectDescription", data.description);
            }
        }
    }

});

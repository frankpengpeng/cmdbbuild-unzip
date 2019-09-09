Ext.define('CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-bim-projects-card-viewinrow',
    data: {
        name: 'CMDBuildUI'
    },

    formulas: {
        updateCardDescription: {
            bind: '{theProject.ownerCard}',
            get: function (card) {
                var vm = this;
                var theProject = vm.get('theProject');
                var id = card;
                var type = CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                var c = CMDBuildUI.util.helper.ModelHelper.getModel(type, theProject.get('ownerClass')).then(
                    function (c) {
                        c.load(id, {
                            success: function (record) {
                                vm.set('cardDescription', record.get('Description'));
                            }
                        });
                    }
                );
            }
        }
    }

});
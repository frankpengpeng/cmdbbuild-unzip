Ext.define('CMDBuildUI.view.map.tab.cards.ListController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-list',

    listen: {
        component: {
            '#': {
                beforerender: 'onBeforeRender',
                select: 'onSelect'
            }
        }
    },

    /**
      * @param {CMDBuildUI.view.classes.cards.Grid} view
      * @param {Object} eOpts
      */
    onBeforeRender: function (view, eOpts) {
        view.mon(CMDBuildUI.map.util.Util.getMapGridContainerView(), 'selectedchangeevent', this.onSelectedChange, this);

        var vm = this.getViewModel();
        var objectTypeName = view.getObjectTypeName();
        if (!objectTypeName) {
            objectTypeName = vm.get("objectTypeName");
        }
        CMDBuildUI.util.helper.ModelHelper.getModel("class", objectTypeName).then(function (model) {
            var columns = [];
            view.reconfigure(null, CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                allowFilter: view.getAllowFilter(),
                reducedGrid: true
            }));
        });
    },
    /**
     * @param r Ext.selection.RowModel
     * @param record Ext.data.Model The selected record
     * @param index the index of the row
     * @param eOpts
     */
    onSelect: function (r, record, index, eOpts) {
        CMDBuildUI.map.util.Util.setSelection(
            record.get('_id'),
            record.get('_type')
        );
    },

    /**
     * selectedchangeevent handler
     * @param {Object} selected 
     * {
     *  type: { String }
     *  id: { String }
     *  conf: {
     *      center: true
     *      }
     *  }
     */
    onSelectedChange: function (selected) {
        console.log('Handle the selectionChangeEvent from the list');

        var view = this.getView();
        var selectedRecord = view.getSelection();
        if (!selectedRecord || !selectedRecord.length || selected.id !== selectedRecord[0].getId()) {
            Ext.asap(function () {
                var store = view.getStore();

                function selectRecord() {
                    var record = store.getById(selected.id);
                    if (record) {
                        view.getSelectionModel().select([record], false, true); //HACK: avoid propagation 
                        return true;
                    }
                    return false;
                }

                if (!selectRecord()) {
                    var extraparams = store.getProxy().getExtraParams();
                    extraparams.positionOf = selected.id;
                    extraparams.positionOf_goToPage = false;
                    store.load({
                        callback: function () {
                            var metadata = store.getProxy().getReader().metaData;
                            var posinfo = (metadata && metadata.positions) && metadata.positions[selected.id] || { positionInPage: 0 };
                            if (!posinfo.pageOffset) {
                                Ext.asap(function () {
                                    selectRecord();
                                });
                            } else {
                                view.ensureVisible(posinfo.positionInTable, {
                                    callback: function () {
                                        selectRecord();
                                    }
                                });
                            }
                        }
                    });
                }

            });
        }
    }
});

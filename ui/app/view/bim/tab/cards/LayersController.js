Ext.define('CMDBuildUI.view.bim.tab.cards.LayersController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.bim-tab-cards-layers',
    listen: {
        component: {
            '#gridActionColumn': {
                'click': 'onLayerCheckDidChangeHandler'
            },
            '#topMenuShowAll': {
                'click': 'onShowAll'
            },
            '#topMenuHideAll': {
                'click': 'onHideAll'
            },
            '#': {
                'beforeselect': function (grid, record, index, eOpts) {
                    return false;
                }
            }
        },
        global: {
            highlitedifcobject: 'onHighlitedIfcObject'
        }
    },

    /**
     * @param {Ext.view.Table} tableView
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} event 
     * @param {Ext.data.Model} record
     * @param {HTMLElement} row
     */
    onLayerCheckDidChangeHandler: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {
        // var clicks = (record.get('clicks') + 1) % 3;
        //FIXME: has been disabled the middle stata. reactive when issue #35 of bimsurfer will be closed
        var clicks;
        record.get('clicks') == 0 ? clicks = 2 : clicks = 0;
        var name = record.get('name')
        record.set('clicks', clicks);

        this.onLayerCheckDidChange([name], clicks);
    },

    /**
     * @param {Ext.view.Table} tableView
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} event 
     * @param {Ext.data.Model} record
     * @param {HTMLElement} row
     */
    onShowAll: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {
        var store = this.getStore('bimIfcLayerStore');
        var ifcNames = [];

        store.each(function (element) {
            element.set('clicks', 0); // set each action column with the correct value
            ifcNames.push(element.get('name'));
        }, this);

        this.onLayerCheckDidChange(ifcNames, 0);

    },

    /**
     * @param {Ext.view.Table} tableView
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Object} item
     * @param {Event} event 
     * @param {Ext.data.Model} record
     * @param {HTMLElement} row
     */
    onHideAll: function (actioncolumn, rowIndex, colIndex, item, event, record, row) {
        var store = this.getStore('bimIfcLayerStore');
        var ifcNames = [];

        store.each(function (element) {
            element.set('clicks', 2); // set each action column with the correct value
            ifcNames.push(element.get('name'));
        }, this);

        this.onLayerCheckDidChange(ifcNames, 2);

    },

    /**
     * @param {[Stringi]} ifcNames contains the name of ifcLayer wich will change the transparence
     * @param {Number} value the state of transparence: 0, 1, 2 
     */
    onLayerCheckDidChange: function (ifcNames, value) {
        ifcNames.forEach(function (name) {
            name = 'Ifc' + name;
            switch (value) {
                case 0:
                    CMDBuildUI.util.bim.Viewer.showLayer(name);
                    break;
                case 1:
                    CMDBuildUI.util.bim.Viewer.semiHideLayer(name);
                    break;
                case 2:
                    CMDBuildUI.util.bim.Viewer.hideLayer(name);
                    break;
                default:
                    console.log('Unvalid value');
            }
        }, this);

        CMDBuildUI.util.bim.Viewer.changeTransparence();
    },
    /**
     * 
     */
    onHighlitedIfcObject: function (highlited) {
        store = this.getViewModel().get('bimIfcLayerStore');
        if (store) {
            var name = highlited.type.replace('Ifc', "");
            var record = store.findRecord('name', name);
            var view = this.getView();
            view.ensureVisible(record, {
                select: true,
                focus: true
            });

            this.getView().getSelectionModel().select(record, false, true);
        }
    }
});

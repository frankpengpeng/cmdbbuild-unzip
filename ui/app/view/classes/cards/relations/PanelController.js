Ext.define('CMDBuildUI.view.classes.cards.relations.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-relations-panel',

    control: {
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },
    
    /**
     * Cancel button
     * @param {Ext.button.Button} button 
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        view.up("panel").close();
    }
});

Ext.define('CMDBuildUI.view.notes.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.notes-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editbtn': {
            click: 'onEditBtnClick'
        },
        '#savebtn' : {
            click: 'onSaveBtnClick'
        },
        '#cancelbtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.notes.Panel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();

        // get model
        var model = CMDBuildUI.util.helper.ModelHelper.getNotesModel(vm.get("objectType"), vm.get("objectTypeName"));

        if (vm.get("objectId")) {
            // set instance to ViewModel
            vm.linkTo('theObject', {
                type: model.getName(),
                id: vm.get("objectId")
            });
        }
    },

    /**
     * @param {Ext.button.Button} button Edit button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function(button, event, eOpts) {
        this.getViewModel().set("editmode", true);
    },

    /**
     * @param {Ext.button.Button} button Save button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function(button, event, eOpts) {
        var vm = this.getViewModel();
        var form = this.getView();

        if (form.isValid()) {
            vm.get("theObject").save({
                callback : function(record, operation, success) {
                    vm.set("editmode", false);
                }
            });
        }
    }, 

    /**
     * @param {Ext.button.Button} button Cancel button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function(button, event, eOpts) {
        this.getViewModel().get("theObject").reject(); // discard changes
        this.getViewModel().set("editmode", false);
    }
});

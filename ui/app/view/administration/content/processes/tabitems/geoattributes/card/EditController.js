Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.geoattributes.card.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabitems-geoattributes-card-edit',
    
    control: {
        '#': {
        beforerender: 'onBeforeRender'
        }
    },
    onBeforeRender: function(){
        var vm = this.getViewModel();
        var theGeoAttribute = vm.get('theGeoAttribute');
        if(theGeoAttribute && !theGeoAttribute.getAssociatedData().style){
            theGeoAttribute.set('style', CMDBuildUI.model.map.GeoAttributeStyle.create().getData());
        }
        
        // vm.set('theGeoAttribute.owner_type', vm.get('grid').lookupViewModel().get('objectTypeName'));
        // vm.set('theGeoAttribute.baseClassId', vm.get('grid').lookupViewModel().get('objectTypeName'));
    },
    /**
     * @param {Ext.form.field.File} input
     * @param {Object} value
     * @param {Object} eOpts
     */
    onFileChange: function (input, value, eOpt) {
        var vm = input.lookupViewModel();
        var file = input.fileInputEl.dom.files[0];
        var reader = new FileReader();

        reader.addEventListener("load", function () {
            vm.get('theGeoAttribute').set('style._iconPath', reader.result);
            input.up().down('#geoAttributeIconPreview').setSrc(reader.result);
        }, false);

        if (file) {
            reader.readAsDataURL(file);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        var vm = this.getViewModel();
        var form = this.getView();

        if (form.isValid()) {
            var theGeoAttribute = vm.get('theGeoAttribute');

             theGeoAttribute.set('style', theGeoAttribute.getAssociatedData().style);
             delete theGeoAttribute.data.style.id;

            theGeoAttribute.save({
                success: function (record, operation) {
                    Ext.GlobalEvents.fireEventArgs("itemcreated", [record]);
                    button.setDisabled(false);
                    CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                },
                failure: function (reason) {
                    button.setDisabled(false);
                }
            });
        } else {
            // TODO: show some message
            button.setDisabled(false);
        }
    },
    

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        
        this.getViewModel().get("theGeoAttribute").reject();
        this.getView().up().fireEvent("closed");
    }
});

Ext.define('CMDBuildUI.view.importexport.ExportController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.importexport-export',

    control: {
        '#exportbtn': {
            click: 'onExportBtnClick'
        },
        '#tplcombo': {
            change: 'onTplComboChange'
        },
        '#closebtn': {
            click: 'onCloseBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Object} eOpts 
     */
    onExportBtnClick: function(btn, eOpts) {
        var view = this.getView();
        if (view.closePopup) {
            Ext.asap(view.closePopup, 250);
            // view.closePopup();
        }
    },

    /**
     * 
     * @param {Ext.form.field.ComboBox} combo 
     * @param {Number} newvalue 
     * @param {Number} oldvalue 
     * @param {Object} eOpts 
     */
    onTplComboChange: function(combo, newvalue, oldvalue, eOpts) {
        if (newvalue) {
            combo.lookupViewModel().set("exporturl", Ext.String.format(
                "{0}/etl/templates/{1}/export?_download=true",
                CMDBuildUI.util.Config.baseUrl,
                newvalue
            ));
        }
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Object} eOpts 
     */
    onCloseBtnClick: function(btn, eOpts) {
        var view = this.getView();
        if (view.closePopup) {
            view.closePopup();
        }
    }
});

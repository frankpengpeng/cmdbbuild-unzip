Ext.define('CMDBuildUI.view.fields.reference.Reference', {
    extend: 'Ext.form.FieldContainer',

    requires: [
        'CMDBuildUI.view.fields.reference.ReferenceController'
    ],

    mixins: [
        'Ext.form.field.Field'
    ],

    alias: 'widget.referencefield',
    controller: 'fields-referencefield',

    layout: 'anchor',

    config: {
        /**
         * @cfg {String} recordLinkName (required)
         * The name of the full record in ViewModel used for 
         * value binding.
         */
        recordLinkName: null
    },

    defaults: {
        anchor: '100%'
    },

    /**
     * @property {Boolean} isFieldContainer
     */
    isFieldContainer: true,

    initComponent: function () {
        var combo = {
            xtype: 'referencecombofield',
            reference: 'maincombo',
            metadata: this.metadata,
            bind: this.initialConfig.bind,
            tabIndex: this.tabIndex,
            recordLinkName: this.recordLinkName,
            name: this.name,
            margin: 0
        };

        if (this.allowBlank !== undefined) {
            combo.allowBlank = this.allowBlank;
        }
        if (this.column !== undefined) {
            combo.column = this.column;
        }
        if (this.value !== undefined) {
            combo.value = this.value;
        }
        if (this.formmode !== undefined) {
            combo.formmode = this.formmode;
        }
        if (Ext.isFunction(this.getValidation)) {
            combo.getValidation= this.getValidation;
            this.getValidation = Ext.emptyFn;
        }

        // remove field label from bindings to prevent double label
        if (this.config.bind && this.config.bind.fieldLabel) {
            delete this.config.bind.fieldLabel;
        }

        Ext.apply(this, {
            items: [combo],
            name: this.name + "container"
        });
        this.callParent(arguments);
    },

    /**
     * Set value on main combo
     * @param {Object} value 
     */
    setValue: function(value) {
        var maincombo = this.getMainCombo();
        maincombo.setValue(value);
        maincombo.lookupViewModel().set("initialvalue", value);
    },

    /**
     * Set value on main combo
     * @return {Object} value 
     */
    getValue: function() {
        return this.getMainCombo().getValue();
    },

    getMainCombo: function () {
        return this.lookupReference("maincombo");
    }
});
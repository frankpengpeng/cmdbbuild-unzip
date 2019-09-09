Ext.define('CMDBuildUI.model.Attribute', {
    requires: [
        'Ext.data.validator.Presence'
    ],

    statics: {
        unitOfMeasureLocations: {
            after: 'AFTER',
            before: 'BEFORE'
        }
    },

    extend: 'CMDBuildUI.model.base.Base',
    fields: [{
        // reference, date, decimal, lookup, string, text, iPaddress, boolean, timestamp, dateTime, double
        name: 'type',
        type: 'string',
        persist: true,
        critical: true,
        validators: ['presence']
    }, {
        name: 'name',
        type: 'string',
        persist: true,
        critical: true,
        validators: ['presence']
    }, {
        name: 'metadata',
        type: 'auto',
        persist: true,
        critical: true
    }, {
        name: 'mode',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: 'write'
    }, {
        name: 'description',
        type: 'string',
        persist: true,
        critical: true,
        validators: ['presence']
    }, {
        name: 'showInGrid',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: false
    }, {
        name: 'showInReducedGrid',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: false
    }, {
        name: 'domain',
        type: 'string',
        persist: true,
        critical: true,
        defaultValue: ''
    }, {
        name: 'help',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'showIf',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'validationRules',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'autoValue',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'actionPostValidation',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'unique',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'mandatory',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'inherited',
        type: 'boolean',
        defaultValue: null,
        persist: true,
        critical: true
    }, {
        name: 'active',
        type: 'boolean',
        persist: true,
        critical: true,
        defaultValue: true
    }, {
        name: 'index',
        type: 'integer',
        defaultValue: -1, // server change -1 to to maxValue + 1
        persist: true,
        critical: true
    }, {
        name: 'defaultValue',
        type: 'auto',
        persist: true,
        critical: true
    }, {
        name: 'group',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'writable',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'hidden',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'maxLength',
        type: 'integer',
        defaultValue: 50,
        persist: true,
        critical: true
    }, {
        name: 'precision',
        type: 'integer',
        defaultValue: 10,
        persist: true,
        critical: true
    }, {
        name: 'scale',
        type: 'integer',
        defaultValue: 2,
        persist: true,
        critical: true
    }, {
        name: 'unitOfMeasure',
        type: 'string',
        defaultValue: null,
        persist: true,
        critical: true
    }, {
        name: 'unitOfMeasureLocation',
        type: 'string',
        defaultValue: null, // BEFORE | AFTER
        persist: true,
        critical: true
    }, {
        name: 'showThousandsSeparator',
        type: 'boolean',
        // defaultValue: 'DEFAULT', // DEFAULT | POINT | COMMA
        persist: true,
        critical: true
    }, {
        name: 'visibleDecimals',
        type: 'string',
        persist: true,
        critical: true
    }, {
        name: 'lookupType',
        type: 'string',
        defaultValue: '',
        persist: true,
        critical: true
    }, {
        name: 'targetClass',
        type: 'string',
        defaultValue: '',
        persist: true,
        critical: true
    }, {
        name: 'targetType',
        type: 'string'
    }, {
        name: 'targetClassDescription',
        type: 'string',

        calculate: function (data) {
            if (data.targetClass) {
                var record = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.targetClass, data.targetType);
                return record && record.get('description');
            }
            return '';
        }
    }, {
        name: 'filter',
        type: 'string',
        defaultValue: '',
        persist: true,
        critical: true
    }, {
        name: 'preselectIfUnique',
        type: 'boolean',
        defaultValue: false,
        persist: true,
        critical: true
    }, {
        // this property is used only in attribute type REFERENCE
        name: 'direction',
        type: 'string',
        defaultValue: null,
        persist: true,
        critical: true
    }, {
        name: 'showSeconds',
        type: 'bool',
        defaultValue: false,
        persist: true,
        critical: true
    }, {
        name: 'ipType',
        tpe: 'string',
        persist: true,
        critical: true
    }],

    proxy: {
        type: 'cmdbuildattributesproxy'
    },


    /**
     * TODO: validation not work
     * @param {} options 
     */
    validate: function (options) {
        var errors = this.callParent(arguments),
            precision = this.get('precision'),
            scale = this.get('scale'),
            type = this.get('type');

        if (type === 'decimal') {
            if (scale >= precision) {
                errors.add({
                    field: 'scale',
                    message: 'Scale can\'t be greater o equal to precision'
                });
                errors.add({
                    field: 'precision',
                    message: 'Precision can\'t be greater o equal to precision'
                });
            }
        }

        return errors;
    },
    /**
     * Get group info
     * 
     * @return {Object} Returns an object containing group `name` and `label`.
     */
    getGroupInfo: function () {
        var name = this.get("group");
        var label = this.get("_group_description_translation") || this.get("_group_description");
        if (!name) {
            name = "__NOGROUP";
            label = CMDBuildUI.locales.Locales.common.attributes.nogroup;
        }
        return {
            name: name,
            label: label
        };
    },

    /**
     * Return translated description for the attribute.
     * 
     * @return {String} The translated description if exists. Otherwise the description.
     */
    getTranslatedDescription: function () {
        return this.get("_description_translation") || this.get("description");
    },

    /**
     * Return a clean clone of attribute.
     * 
     * @return {CMDBuildUI.model.Attribute} the fresh cloned attribute
     */
    clone: function () {
        var newAttribute = this.copy();
        newAttribute.set('_id', undefined);
        newAttribute.set('name', '');
        newAttribute.set('description', '');
        newAttribute.crudState = "C";
        newAttribute.phantom = true;
        delete newAttribute.crudStateWas;
        delete newAttribute.previousValues;
        delete newAttribute.modified;
        return newAttribute;
    }

});
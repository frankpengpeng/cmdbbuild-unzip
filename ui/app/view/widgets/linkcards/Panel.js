
Ext.define('CMDBuildUI.view.widgets.linkcards.Panel', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.widgets.linkcards.PanelController',
        'CMDBuildUI.view.widgets.linkcards.PanelModel'
    ],

    mixins: [
        'CMDBuildUI.view.widgets.Mixin'
    ],

    statics: {
        /**
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @return {Object} 
         */
        getTypeInfo: function(widget) {
            var objectTypeName;
            // get object type from type name
            if (widget.get("_Filter_ecql")) {
                objectTypeName = widget.get("_Filter_ecql").from;
            } else {
                objectTypeName = widget.get("ClassName");
            }
            // get object type from type name
            var objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(objectTypeName);
            return {
                objectType: objectType,
                objectTypeName: objectTypeName
            };
        },

        /**
         * @param {String} objecttype
         * @return {String}
         */
        getStoreType: function(objecttype) {
            switch (objecttype) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    return 'CMDBuildUI.store.classes.Cards';
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                    return 'CMDBuildUI.store.processes.Instances';
                default:
                    return null;
            }
        },

        /**
         * @param {CMDBuildUI.model.WidgetDefinition} widget
         * @param {CMDBuildUI.model.classes.Card|CMDBuildUI.model.processes.Instance} target
         * @return {Object} 
         */
        loadDefaults: function(widget, target, callback) {
            if (widget.get("_DefaultSelection_ecql") && callback) {
                var typeinfo = CMDBuildUI.view.widgets.linkcards.Panel.getTypeInfo(widget);
                var storetype = CMDBuildUI.view.widgets.linkcards.Panel.getStoreType(typeinfo.objectType);
    
                CMDBuildUI.util.helper.ModelHelper.getModel(
                    typeinfo.objectType,
                    typeinfo.objectTypeName
                ).then(function (model) {
                    var ecql = CMDBuildUI.util.ecql.Resolver.resolve(
                        widget.get("_DefaultSelection_ecql"),
                        target
                    );
    
                    var store = Ext.create(storetype, {
                        model: model.getName(),
                        autoLoad: false,
                        autoDestroy: true,
                        advancedFilter: {ecql: ecql}
                    });
    
                    store.load(function(records, operation, success) {
                        if (success) {
                            Ext.callback(callback, null, [records]);
                        }
                    });
                });
            }
        }
    },

    alias: 'widget.widgets-linkcards-panel',
    controller: 'widgets-linkcards-panel',
    viewModel: {
        type: 'widgets-linkcards-panel'
    },

    /**
     * @cfg {String} theWidget.ClassName
     * Class or Process name
     */

    /**
     * @cfg {Object} theWidget._Filter_ecql
     * eCQL filter definition.
     */

    /**
     * @cfg {String} theWidget._DefaultSelection_ecql
     * Default selection defined as eCQL filter.
     */

    /**
     * @cfg {Number} theWidget.NoSelect
     * If equals to 1 disable the selection.
     */

    /**
     * @cfg {Number} theWidget.SingleSelect
     * If equals to 1 enable the selection of only one item.
     */

    /**
     * @cfg {*} theWidget.AllowCardEditing
     * If present and different to false, allows the user to modify
     * the row item.
     */

    /**
     * @cfg {Boolean} theWidget.DisableGridFilterToggler
     * If true disable filter toggle button.
     */

    layout: "fit",

    tbar: [{
        xtype: 'button',
        enableToggle: true,
        ui: 'management-action',
        disabled: true,
        reference: 'togglefilter',
        itemId: 'togglefilter',
        text: CMDBuildUI.locales.Locales.widgets.linkcards.togglefilterenabled,
        iconCls: 'x-fa fa-filter',
        bind: {
            text: '{textTogglefilter}',
            disabled: '{disableTogglefilter}',
            pressed: '{disablegridfilter}'
        }
    }, 
    {
        xtype: 'textfield',
        name: 'search',
        width: 250,

        emptyText: CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search,
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search'
        },
        reference: 'searchtext',
        itemId: 'searchtext',
        cls: 'management-input',
        bind: {
            value: '{search.value}',
            hidden: '{!canFilter}'
        },
        listeners: {
            specialkey: 'onSearchSpecialKey'
        },
        triggers: {
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                handler: 'onSearchSubmit'
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear'
            }
        }
    },    
    {
        xtype: 'button',
        ui: 'management-action',
        disabled: true,
        reference: 'refreshselection',
        itemId: 'refreshselection',
        text: CMDBuildUI.locales.Locales.widgets.linkcards.refreshselection,
        iconCls: 'x-fa fa-refresh',
        bind: {
            text: '{textRefreshselection}',
            disabled: '{disableRefreshselection}'
        }
    }],

    fbar: [{
        xtype: 'button',
        ui: 'secondary-action',
        reference: 'closebtn',
        itemId: 'closebtn',
        text: CMDBuildUI.locales.Locales.common.actions.close,
        iconCls: 'x-fa fa-check',
        bind: {
            text: '{textClosebtn}'
        }
    }]
});

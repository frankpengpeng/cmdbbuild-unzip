
Ext.define('CMDBuildUI.view.classes.cards.card.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.classes.cards.card.ViewController',
        'CMDBuildUI.view.classes.cards.card.ViewModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],

    mixins: [
        'CMDBuildUI.view.classes.cards.card.Mixin'
    ],

    alias: 'widget.classes-cards-card-view',
    controller: 'classes-cards-card-view',
    viewModel: {
        type: 'classes-cards-card-view'
    },

    config: {
        /**
        * @cfg {Boolean} shownInPopup
        * Set to true get inline form.
        */
        shownInPopup: false,

        /**
         * @cfg {Boolean} hideTools
         * Set to true to hide tools.
         */
        hideTools: false
    },

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    bind: {
        title: '{title}'
    },

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read,

    tabpaneltools: [{
        xtype: 'tool',
        itemId: 'editBtn',
        iconCls: 'x-fa fa-pencil',
        cls: 'management-tool',
        disabled: true,
        tooltip: CMDBuildUI.locales.Locales.classes.cards.modifycard,
        autoEl: {
            'data-testid': 'cards-card-view-editBtn'
        },
        bind: {
            disabled: '{!permissions.edit}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.classes.cards.modifycard'
        }
    }, {
        xtype: 'tool',
        itemId: 'openBtn',
        iconCls: 'x-fa fa-external-link',
        cls: 'management-tool',
        hidden: true,
        tooltip: CMDBuildUI.locales.Locales.classes.cards.opencard,
        autoEl: {
            'data-testid': 'cards-card-view-openBtn'
        },
        bind: {
            hidden: '{hiddenbtns.open}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.classes.cards.opencard'
        }
    }, {
        xtype: 'tool',
        itemId: 'deleteBtn',
        iconCls: 'x-fa fa-trash',
        cls: 'management-tool',
        disabled: true,
        tooltip: CMDBuildUI.locales.Locales.classes.cards.deletecard,
        autoEl: {
            'data-testid': 'cards-card-view-deleteBtn'
        },
        bind: {
            disabled: '{!permissions.delete}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.classes.cards.deletecard'
        }
    }, {
        xtype: 'tool',
        itemId: 'cloneMenuBtn',
        iconCls: 'x-fa fa-clone',
        cls: 'management-tool',
        tooltip: CMDBuildUI.locales.Locales.classes.cards.clone,
        autoEl: {
            'data-testid': 'cards-card-view-cloneBtn'
        },
        bind: {
            disabled: '{!permissions.clone}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.classes.cards.clone'
        }
    }, {
        xtype: 'tool',
        itemId: 'bimBtn',
        iconCls: 'x-fa fa-building-o',
        cls: 'management-tool',
        hidden: true,
        tooltip: CMDBuildUI.locales.Locales.bim.showBimCard,
        autoEl: {
            'data-testid': 'cards-card-view-bimBtn'
        },
        bind: {
            hidden: '{hiddenbtns.bim}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.bim.showBimCard'
        }
    }, {
        xtype: 'tool',
        itemId: 'relgraphBtn',
        iconCls: 'cmdbuildicon-relgraph',
        cls: 'management-tool',
        hidded: true,
        tooltip: CMDBuildUI.locales.Locales.relationGraph.openRelationGraph,
        autoEl: {
            'data-testid': 'cards-card-view-bimBtn'
        },
        bind: {
            hidden: '{hiddenbtns.relgraph}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.relationGraph.openRelationGraph'
        }
    }, {
        xtype: 'tool',
        itemId: 'printBtn',
        iconCls: 'x-fa fa-print',
        cls: 'management-tool',
        tooltip: CMDBuildUI.locales.Locales.classes.cards.print,
        autoEl: {
            'data-testid': 'cards-card-view-printBtn'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.classes.cards.print'
        }
    }, {
        xtype: 'tool',
        itemId: 'openTabsBtn',
        iconCls: 'x-fa fa-ellipsis-v',
        cls: 'management-tool',
        hidded: true,
        bind: {
            hidden: '{hiddenbtns.opentabs}'
        },
        autoEl: {
            'data-testid': 'cards-card-view-openTabs'
        }
    }],

    fieldDefaults: {
        labelAlign: 'top'
    },

    /**
     * Function called when objectTypeName is updated.
     * 
     * @param {String} newValue
     * @param {String} oldValue
     */
    updateObjectTypeName: function (newValue, oldValue) {
        this.fireEventArgs("objecttypenamechanged", [this, newValue, oldValue]);
    },

    /**
     * Function called when objectId is updated.
     * 
     * @param {Numeric} newValue
     * @param {Numeric} oldValue
     */
    updateObjectId: function (newValue, oldValue) {
        this.fireEventArgs("objectidchanged", [this, newValue, oldValue]);
    }

});

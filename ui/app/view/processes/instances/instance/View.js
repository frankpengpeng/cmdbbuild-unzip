
Ext.define('CMDBuildUI.view.processes.instances.instance.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.processes.instances.instance.ViewController',
        'CMDBuildUI.view.processes.instances.instance.ViewModel'
    ],

    mixins: [
        'CMDBuildUI.view.processes.instances.instance.Mixin'
    ],

    alias: 'widget.processes-instances-instance-view',
    controller: 'processes-instances-instance-view',
    viewModel: {
        type: 'processes-instances-instance-view'
    },

    config: {
        buttons: null,
        objectTypeName: null,
        objectId: null,
        activityId: null,
        shownInPopup: false
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    bind: {
        title: '{title}'
    },


    tabpaneltools: [{
        xtype: 'tool',
        itemId: 'editBtn',
        iconCls: 'x-fa fa-pencil',
        tooltip: CMDBuildUI.locales.Locales.processes.editactivity,
        cls: 'management-tool',
        disabled: true,
        autoEl: {
            'data-testid': 'processes-instance-view-editBtn'
        },
        bind: {
            disabled: '{!basepermissions.edit}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.processes.editactivity'
        }
    }, {
        xtype: 'tool',
        itemId: 'openBtn',
        iconCls: 'x-fa fa-external-link',
        tooltip: CMDBuildUI.locales.Locales.processes.openactivity,
        cls: 'management-tool',
        autoEl: {
            'data-testid': 'processes-instance-view-openBtn'
        },
        bind: {
            hidden: '{hiddenbtns.open}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.processes.openactivity'
        }
    }, {
        xtype: 'tool',
        itemId: 'deleteBtn',
        iconCls: 'x-fa fa-trash',
        tooltip: CMDBuildUI.locales.Locales.processes.abortprocess,
        cls: 'management-tool',
        disabled: true,
        autoEl: {
            'data-testid': 'processes-instance-view-deleteBtn'
        },
        bind: {
            disabled: '{!basepermissions.delete}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.processes.abortprocess'
        }
    }, {
        xtype: 'tool',
        itemId: 'relgraphBtn',
        iconCls: 'x-fa fa-share-alt',
        cls: 'management-tool',
        hidded: true,
        tooltip: CMDBuildUI.locales.Locales.relationGraph.openRelationGraph,
        autoEl: {
            'data-testid': 'processes-instance-view-bimBtn'
        },
        bind: {
            hidden: '{hiddenbtns.relgraph}'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.relationGraph.openRelationGraph'
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
            'data-testid': 'processes-instance-view-openTabs'
        }
    }],

    /**
     * Render form fields
     */
    showForm: function () {
        var vm = this.getViewModel();

        // attributes configuration from activity
        var attrsConf = this.getAttributesConfigFromActivity();

        // generate tabs/fieldsets and fields
        var items = [];

        // get tools
        var tools = Ext.Array.merge([this.getCurrentActivityInfo()], this.tabpaneltools);

        if (this.getShownInPopup()) {
            // get form fields as fieldsets
            var formitems = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get("objectModel"), {
                mode: this.formmode,
                attributesOverrides: attrsConf.overrides,
                visibleAttributes: attrsConf.visibleAttributes,
                showAsFieldsets: true
            });

            // create items
            items = [
                this.getProcessStatusBar(),
                {
                    xtype: 'toolbar',
                    cls: 'fieldset-toolbar',
                    items: Ext.Array.merge([{ xtype: 'tbfill' }], tools),
                    margin: 0
                },
                this.getMainPanelForm(formitems)
            ];
        } else {
            // get form fields as tab panel
            var panel = CMDBuildUI.util.helper.FormHelper.renderForm(vm.get("objectModel"), {
                mode: this.formmode,
                showAsFieldsets: false,
                attributesOverrides: attrsConf.overrides,
                visibleAttributes: attrsConf.visibleAttributes
            });
            Ext.apply(panel, {
                tools: tools
            });
            items.push(panel);
        }
        this.add(items);
        this.addConditionalVisibilityRules();

        if (this.loadmask) {
            CMDBuildUI.util.Utilities.removeLoadMask(this.loadmask);
        }
    }
});

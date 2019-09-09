Ext.define('CMDBuildUI.model.users.Group', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],

    fields: [{
            name: '_id',
            type: 'number',
            persist: false
            // critical: true // This field is allways sent to server even if it has hot changed
        }, {
            name: 'description',
            type: 'string',
            critical: true,
            validators: ['presence']
        }, {
            name: 'name',
            type: 'string',
            critical: true,
            validators: ['presence']
        }, {
            name: 'email',
            type: 'string',
            critical: true
        }, {
            name: 'type',
            type: 'string',
            critical: true,
            defaultValue: 'default'
        }, {
            name: 'processWidgetAlwaysEnabled',
            type: 'boolean',
            critical: true
        }, {
            name: 'disabledCardTabs',
            type: 'auto',
            critical: true
        }, {
            name: 'disabledModules',
            type: 'auto',
            critical: true
        }, {
            name: 'disabledProcessTabs',
            type: 'auto',
            critical: true
        }, {
            name: 'active',
            type: 'boolean',
            critical: true,
            defaultValue: true
        },
        //  {
        //     name: 'admin',
        //     type: 'boolean',
        //     critical: true
        // },
        {
            // used only for exclude role from clone permissions button if value == true
            name: '_rp_data_all_write',
            type: 'boolean'
        },
        {
            name: '_rp_class_access',
            type: 'boolean',
            critical: true,
            defaultValue: false
        }, {
            name: '_rp_process_access',
            type: 'boolean',
            critical: true,
            defaultValue: false,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_dataview_access',
            type: 'boolean',
            critical: true,
            defaultValue: false,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_dashboard_access',
            type: 'boolean',
            critical: true,
            defaultValue: false,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_report_access',
            type: 'boolean',
            defaultValue: false,
            critical: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_custompages_access',
            type: 'boolean',
            critical: true,
            defaultValue: false,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_card_tab_detail_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_card_tab_note_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_card_tab_relation_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_card_tab_history_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_card_tab_email_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_card_tab_attachment_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_flow_tab_detail_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_flow_tab_note_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_flow_tab_relation_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_flow_tab_history_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_flow_tab_email_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_flow_tab_attachment_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }, {
            name: '_rp_bulkupdate_access',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            serialize: function (v, record) {
                return v;
            }
        }
    ],

    // convertOnSet: true,

    proxy: {
        url: '/roles/',
        type: 'baseproxy',
        extraParams: {
            detailed: true
        }
    }
});
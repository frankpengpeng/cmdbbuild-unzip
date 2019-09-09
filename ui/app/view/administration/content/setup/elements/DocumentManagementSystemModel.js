Ext.define('CMDBuildUI.view.administration.content.setup.elements.DocumentManagementSystemModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-documentmanagementsystem',

    data: {
        isAlfresco: false,
        isCmis: false,
        isPostgres: false
    },

    formulas: {
        dmsType: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__dms__DOT__service__DOT__type}',
            get: function (type) {
                switch (type) {
                    case 'alfresco':
                        this.set('isAlfresco', true);
                        this.set('isCmis', false);
                        this.set('isPostgres', false);
                        break;
                    case 'cmis':
                        this.set('isAlfresco', false);
                        this.set('isCmis', true);
                        this.set('isPostgres', false);
                        break;
                    case 'postgres': // sperimental
                        this.set('isAlfresco', false);
                        this.set('isCmis', false);
                        this.set('isPostgres', true);
                        break;
                }
            }
        }
    },
    stores: {
        attachmentTypeLookupStore: {
            model: 'CMDBuildUI.model.lookups.LookupType',
            type: 'attachments-categories',
            fields: [{
                _name: '_id',
                type: 'string'
            }, {
                name: 'name',
                type: 'string'
            }],

            proxy: {
                url: '/lookup_types/',
                type: 'baseproxy'
            },
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0
        }
    }
});
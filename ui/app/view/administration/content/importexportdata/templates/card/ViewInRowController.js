Ext.define('CMDBuildUI.view.administration.content.importexportdata.templates.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    mixins: ['CMDBuildUI.view.administration.content.importexportdata.templates.card.CardMixin'],
    requires: ['CMDBuildUI.util.administration.helper.ConfigHelper'],
    alias: 'controller.administration-content-importexportdata-templates-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.importexportdata.templates.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var selected = view._rowContext.record;
        var type = selected.get('type');

        vm.linkTo('theImportExportTemplate', {
            type: 'CMDBuildUI.model.importexports.Template',
            id: selected.get('_id')
        });

        vm.bind({
            bindTo: {
                theImportExportTemplate: '{theImportExportTemplate}'
            }
        }, function (data) {
            if (data.theImportExportTemplate) {
                view.add(CMDBuildUI.view.administration.content.importexportdata.templates.card.helpers.FieldsetsHelper.getGeneralPropertiesFieldset());
                view.add(CMDBuildUI.view.administration.content.importexportdata.templates.card.helpers.FieldsetsHelper.getAttributesFieldset());

                if (type === CMDBuildUI.model.importexports.Template.types.import || type === CMDBuildUI.model.importexports.Template.types.importexport) {
                    view.add(CMDBuildUI.view.administration.content.importexportdata.templates.card.helpers.FieldsetsHelper.getImportCriteriaFieldset());
                }

                if (data.theImportExportTemplate.targetType === CMDBuildUI.model.administration.MenuItem.types.klass &&
                    (type === CMDBuildUI.model.importexports.Template.types.export ||
                        type === CMDBuildUI.model.importexports.Template.types.importexport)) {
                    view.add(CMDBuildUI.view.administration.content.importexportdata.templates.card.helpers.FieldsetsHelper.getExportCriteriaFieldset());
                }

                view.add(CMDBuildUI.view.administration.content.importexportdata.templates.card.helpers.FieldsetsHelper.getErrorsManagementFieldset());
                Ext.Array.forEach(view.down('#importExportAttributeGrid').getColumns(), function (column) {
                    if (column.xtype === 'actioncolumn') {
                        column.destroy();
                    }
                });
                view.setActiveTab(0);
                Ext.asap(function () {
                    try {
                        view.unmask();
                    } catch (error) {

                    }
                }, this);
            }
        });
        //  vm.set('theImportExportTemplate', selected);
    },

    onAfterRender: function (view) {

        var selected = view._rowContext.record;
        var type = selected.get('type');

    },

    onImportExportTemplateUpdate: function (v, record) {
        // new Ext.util.DelayedTask(function () { }).delay(
        //     150,
        //     function (v, record) {
                var vm = this.getViewModel();
                var view = this.getView();
                this.linkImportExportTemplate(view, vm);
    //         },
    //         this,
    //         arguments);
    },

    linkImportExportTemplate: function (view, vm) {
        var grid = view.up(),
            record = grid.getSelection()[0];

        vm.set("theImportExportTemplate", record);
    }
});
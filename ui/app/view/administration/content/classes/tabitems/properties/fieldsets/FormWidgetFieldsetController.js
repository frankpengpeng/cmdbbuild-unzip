Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.FormWidgetFieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-classes-tabitems-properties-fieldsets-formwidgetfieldset',
    mixins: ['CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin'],
    control: {
        '#formTriggersStore': {
            datachanged: 'onFormTriggersStoreChanged'
        }
    },

    onAddNewWidgetMenuBtn: function (grid, rowIndex, colIndex) {

        var vm = this.getViewModel();
        var widgets = vm.get('theObject.widgets');
        var newWidgetStore = vm.get('formWidgetsStoreNew');
        var newWidget = newWidgetStore.getData().items[0];

        Ext.suspendLayouts();
        widgets.add(newWidget);
        newWidgetStore.removeAt(rowIndex);
        newWidgetStore.add(Ext.create('CMDBuildUI.model.WidgetDefinition', {
            script: ''
        }));
        grid.refresh();
        vm.getParent().set('formWidgetCountManager', widgets.data.length);
        this.lookupReference('formWidgetGrid').view.grid.getView().refresh();
        //this.lookupReference('newFormWidgetScriptField').getAceEditor().getSession().setValue('');
        window.newFormWidgetScriptField.getSession().setValue('');

        Ext.resumeLayouts();

    },
    onEditBtn: function (view, rowIndex, colIndex) {
        var vm = this.getViewModel();
        var grid = this.lookupReference('formWidgetGrid');
        var theWidget = grid.getStore().getAt(rowIndex);
        vm.set('theWidget', theWidget);
        var formFields = [{
            xtype: 'container',
            //flex: 2,

            padding: '0 10 0 10',
            items: [{
                /********************* Triggers **********************/
                xtype: 'fieldcontainer',
                flex: 2,
                columns: 1,
                vertical: true,
                viewModel: {

                },
                fieldDefaults: {
                    labelAlign: 'top'
                },
                items: [{
                    xtype: 'textfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                    },
                    bind: {
                        value: '{theWidget._label}'
                    }
                }, {
                    xtype: 'combo',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.type,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
                    },
                    editable: false,
                    forceSelection: true,
                    allowBlank: false,
                    displayField: 'label',
                    valueField: 'value',
                    store: 'administration.common.WidgetTypes',
                    bind: {
                        value: '{theWidget._type}'
                    }
                }, {
                    xtype: 'checkboxgroup',
                    userCls: 'hideCellCheboxes',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    flex: 1,
                    columns: 1,
                    vertical: false,
                    items: [{
                        xtype: 'checkbox',
                        bind: {
                            value: '{theWidget.active}'
                        },
                        value: theWidget.get('active')
                    }]
                }]
            }]
        }];
        var popup = CMDBuildUI.util.administration.helper.AcePopupHelper.getPopup('theWidget', theWidget, '_config', formFields, 'popup-edit-fromwidget', CMDBuildUI.locales.Locales.administration.classes.strings.editformwidget);
    },

    onFormTriggersStoreChanged: function () {

    }
});
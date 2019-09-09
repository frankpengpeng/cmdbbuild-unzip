Ext.define('CMDBuildUI.view.administration.content.importexportdata.templates.card.CardController', {
    extend: 'Ext.app.ViewController',
    mixins: [
        'CMDBuildUI.view.administration.content.importexportdata.templates.card.CardMixin'
    ],
    alias: 'controller.view-administration-content-importexportdata-templates-card',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editFilterBtn': {
            click: 'onEditFilterBtn'
        },
        '#removeFilterBtn': {
            click: 'onRemoveFilterBtn'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        },

        '#targetName_input': {
            change: 'onTargetNameInputChange'
        }

    },

    /**
     * @param {CMDBuildUI.view.administration.content.importexportdata.templates.card.CardController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        Ext.getStore('emails.Accounts').load();
        Ext.getStore('emails.Templates').load();
        var fiedlsetHelper = CMDBuildUI.view.administration.content.importexportdata.templates.card.helpers.FieldsetsHelper;


        if (vm.get('theImportExportTemplate').phantom) {
            Ext.Array.forEach(vm.get('theImportExportTemplate').get('columns'), function (item, index) {
                vm.get('theImportExportTemplate').columns().add(item);

            });
        } else {
            vm.linkTo("theImportExportTemplate", {
                type: 'CMDBuildUI.model.importexports.Template',
                id: vm.get('grid').getSelection()[0].get('_id')
            });
        }

        vm.bind({
            bindTo: {
                theImportExportTemplate: '{theImportExportTemplate}'
            }
        }, function (data) {
            if (data.theImportExportTemplate) {
                view.add(fiedlsetHelper.getGeneralPropertiesFieldset());
                view.add(fiedlsetHelper.getAttributesFieldset());
                view.add(fiedlsetHelper.getImportCriteriaFieldset());
                view.add(fiedlsetHelper.getExportCriteriaFieldset());
                view.add(fiedlsetHelper.getErrorsManagementFieldset());
                switch (vm.get('theImportExportTemplate.type')) {
                    case CMDBuildUI.model.importexports.Template.types.import:
                        view.down('#importfieldset').setHidden(false);
                        view.down('#exportfilterfieldset').setHidden(true);
                        break;
                    case CMDBuildUI.model.importexports.Template.types.export:
                        view.down('#importfieldset').setHidden(true);
                        view.down('#exportfilterfieldset').setHidden(vm.get('isClass') && false);
                        break;
                    case CMDBuildUI.model.importexports.Template.types.importexport:
                        view.down('#importfieldset').setHidden(false);
                        view.down('#exportfilterfieldset').setHidden(vm.get('isClass') && false);
                        break;
                    default:
                        break;
                }
                if (vm.get('actions.view')) {
                    Ext.Array.forEach(view.down('#importExportAttributeGrid').getColumns(), function (column) {
                        if (column.xtype === 'actioncolumn') {
                            column.destroy();
                        }
                    });
                }
                Ext.asap(function () {
                    try {
                        view.setHidden(false);
                        view.up().unmask();
                    } catch (error) {

                    }
                }, this);

            }
        });




        if (vm.get('actions.view')) {
            var topbar = {
                xtype: 'components-administration-toolbars-formtoolbar',
                dock: 'top',
                hidden: true,
                bind: {
                    hidden: '{!actions.view}'
                },
                items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                    edit: true, // #editBtn set true for show the button
                    view: false, // #viewBtn set true for show the button
                    clone: true, // #cloneBtn set true for show the button
                    'delete': true, // #deleteBtn set true for show the button
                    activeToggle: true // #enableBtn and #disableBtn set true for show the buttons       
                },

                    /* testId */
                    'importexporttemplates',

                    /* viewModel object needed only for activeTogle */
                    'theImportExportTemplate',

                    /* add custom tools[] on the left of the bar */
                    [],

                    /* add custom tools[] before #editBtn*/
                    [],

                    /* add custom tools[] after at the end of the bar*/
                    []
                )
            };
            view.addDocked(topbar);

        } else {
            var formButtons = {
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                hidden: true,
                bind: {
                    hidden: '{actions.view}'
                },
                items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
            };
            view.addDocked(formButtons);
        }
    },

    onAfterRender: function (view) {
        var me = this;
        var vm = this.getViewModel();
        vm.bind({
            bindTo: {
                type: '{theImportExportTemplate.type}'
            }
        }, function (data) {

            if (data.type && view.down('#importfieldset')) {
                switch (data.type) {
                    case CMDBuildUI.model.importexports.Template.types.import:
                        view.down('#importfieldset').setHidden(false);
                        view.down('#exportfilterfieldset').setHidden(true);
                        break;
                    case CMDBuildUI.model.importexports.Template.types.export:
                        view.down('#importfieldset').setHidden(true);
                        if (vm.get('isDomain')) {
                            view.down('#exportfilterfieldset').setHidden(true);
                        } else {
                            view.down('#exportfilterfieldset').setHidden(false);
                        }
                        break;
                    case CMDBuildUI.model.importexports.Template.types.importexport:
                        view.down('#importfieldset').setHidden(false);
                        if (vm.get('isDomain')) {
                            view.down('#exportfilterfieldset').setHidden(true);
                        } else {
                            view.down('#exportfilterfieldset').setHidden(false);
                        }
                        break;
                    default:
                        break;
                }
            }
        });


        vm.bind({
            bindTo: {
                theImportExportTemplate: '{theImportExportTemplate}',
                type: '{theImportExportTemplate.type}',
                targetType: '{theImportExportTemplate.targetType}',
                fileFormat: '{theImportExportTemplate.fileFormat}',
                mergeMode: '{theImportExportTemplate.mergeMode}',
                errorEmailTemplate: '{theImportExportTemplate.errorEmailTemplate}',
                errorEmailAccount: '{theImportExportTemplate.errorEmailAccount}'
            }
        }, function (data) {
            var form = view;
            var importKeyAttr = form.down('#importKeyAttribute_input');
            var mergeMode = form.down('#mergeMode_input');
            // 
            var missingRecordAttr = form.down('#mergeMode_when_missing_update_attr_input');
            //
            var missingRecordValue = form.down('#mergeMode_when_missing_update_value_input');
            //
            var separatorField = form.down('#csv_separator_input');

            var dataRowField = form.down('#dataRow_input');
            var headerRowField = form.down('#headerRow_input');
            var firstColField = form.down('#firstCol_input');

            if (data.type) {

                var isDomain = data.targetType === CMDBuildUI.model.administration.MenuItem.types.domain;
                var isModifyCard = data.mergeMode === CMDBuildUI.model.importexports.Template.missingRecords.modifycard;
                switch (data.type) {
                    case CMDBuildUI.model.importexports.Template.types.export:
                        me.setAllowBlank(importKeyAttr, true);
                        me.setAllowBlank(mergeMode, true);
                        break;

                    default:

                        me.setAllowBlank(importKeyAttr, isDomain);
                        me.setAllowBlank(mergeMode, false);
                        break;

                }
                me.setAllowBlank(missingRecordValue, !isModifyCard);
                me.setAllowBlank(missingRecordAttr, !isModifyCard);
            }

            if (data.fileFormat) {
                switch (data.fileFormat) {
                    case 'csv':
                        me.setAllowBlank(separatorField, false);
                        me.setAllowBlank(dataRowField, true);
                        me.setAllowBlank(headerRowField, true);
                        me.setAllowBlank(firstColField, true);
                        break;

                    default:
                        me.setAllowBlank(separatorField, true);
                        me.setAllowBlank(dataRowField, false);
                        me.setAllowBlank(headerRowField, false);
                        me.setAllowBlank(firstColField, false);
                        break;
                }
            }
            form.form.checkValidity();

        });

    },

    onAddAttributeBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        var attributeInput = button.up().down('combo');
        var selectedAttribute = attributeInput.getValue();
        if (selectedAttribute) {
            var attribute = attributeInput.getStore().findRecord('name', selectedAttribute);
            var attributeDescription = attribute.get('description');
            var newAttribute = CMDBuildUI.model.importexports.Attribute.create({
                attribute: selectedAttribute,
                columnName: attributeDescription,
                mode: ''
            });
            vm.get('allSelectedAttributesStore').add(newAttribute);
            attributeInput.setValue(null);
            var attributesStore = attributeInput.getStore();
            attributesStore.remove(attributesStore.findRecord('name', selectedAttribute));
        } else {
            attributeInput.focus();
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditFilterBtn: function (button, e, eOpts) {
        var me = this;

        var vm = me.getViewModel();
        var record = vm.get('theImportExportTemplate');
        var actions = vm.get('actions');
        var recordFilter = record.get('exportFilter').length ? JSON.parse(record.get('exportFilter')) : {};

        var popuTitle = 'Filter for Template: {0}';
        var type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(record.get("targetName"));

        popuTitle = Ext.String.format(
            popuTitle,
            record.get('description'));

        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            target: record.get("targetName"),
            configuration: recordFilter

        });

        var viewmodel = {
            data: {
                objectType: type,
                objectTypeName: record.get("targetName"),
                theFilter: filter,
                actions: Ext.copy(actions)
            }
        };

        var attrbutePanel = this.getAttributesFilterTab(viewmodel, record);
        var relationsPanel = this.getRelationsFilterTab(viewmodel, record);
        var functionPanel = this.getFunctionFilterTab(viewmodel, record);
        var fulltextPanel = this.getFulltextFilterTab(viewmodel, record);

        var listeners = {
            /**
             * 
             * @param {CMDBuildUI.view.filters.Panel} panel 
             * @param {CMDBuildUI.model.base.Filter} filter 
             * @param {Object} eOpts 
             */
            applyfilter: function (panel, filter, eOpts) {
                me.onApplyFilter(filter);
                me.popup.close();
            },
            /**
             * 
             * @param {CMDBuildUI.view.filters.Panel} panel 
             * @param {CMDBuildUI.model.base.Filter} filter 
             * @param {Object} eOpts 
             */
            saveandapplyfilter: function (panel, filter, eOpts) {
                me.onSaveAndApplyFilter(filter);
                me.popup.close();
            },
            /**
             * Custom event to close popup directly from popup
             * @param {Object} eOpts 
             */
            popupclose: function (eOpts) {
                me.popup.close();
            }
        };

        var dockedItems = [{
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: me.getViewModel().get('actions.view'),
            items: CMDBuildUI.util.administration.helper.FormHelper.getOkCloseButtons({
                handler: function (button) {
                    var jsonValue = {};
                    var attribute = button.up('tabpanel').down('administration-filters-attributes-panel').getAttributesData();
                    if (attribute) {
                        jsonValue.attribute = attribute;
                    }
                    var relation = button.up('tabpanel').down('administration-filters-relations-panel').getRelationsData();
                    if (relation && relation.length) {
                        jsonValue.relation = relation;
                    }
                    var functions = button.up('tabpanel').down('administration-components-functionfilters-panel').getFunctionData();
                    if (functions) {
                        jsonValue.functions = functions;
                    }
                    var query = button.up('tabpanel').down('administration-components-functionfilters-panel').getFunctionData();
                    if (query) {
                        jsonValue.query = query;
                    }
                    var value;
                    if (Ext.Object.isEmpty(value)) {
                        value = '';
                    } else {
                        value = JSON.stringify(jsonValue);
                    }
                    record.set('exportFilter', value);
                    me.popup.close();
                }
            }, {
                    handler: function () {
                        me.popup.close();
                    }
                })
        }];
        var content = {
            xtype: 'tabpanel',
            cls: 'administration',
            ui: 'administration-tabandtools',
            items: [attrbutePanel, relationsPanel, functionPanel, fulltextPanel],
            dockedItems: dockedItems,
            listeners: listeners
        };

        me.popup = CMDBuildUI.util.Utilities.openPopup(
            'filterpopup',
            popuTitle,
            content, {}, {
                ui: 'administration-actionpanel',
                viewModel: {
                    data: {
                        index: '0',
                        grid: {},
                        record: record,
                        canedit: true
                    }
                }
            }
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRemoveFilterBtn: function (button, e, eOpts) {
        this.getViewModel().set('theImportExportTemplate.exportFilter', '');
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = me.getView();
        var vm = this.getViewModel();
        var theImportExportTemplate = vm.get('theImportExportTemplate');
        me.setColumnsData();

        if (!theImportExportTemplate.get('columns').length) {
            Ext.toast({
                html: CMDBuildUI.locales.Locales.administration.importexport.texts.emptyattributegridmessage,
                title: CMDBuildUI.locales.Locales.administration.common.messages.warning
            });
        } else if (theImportExportTemplate.isValid()) {
            theImportExportTemplate.save({
                success: function (record, operation) {
                    Ext.getStore('importexports.Templates').load({
                        callback: function () {
                            vm.get('grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [vm.get('grid'), record, this]);
                            form.up().fireEvent("closed");
                        }
                    });
                }
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        vm.get("theImportExportTemplate").reject(); // discard changes
        this.getView().up().fireEvent("closed");
    },

    // ok
    onTargetNameInputChange: function (combo, newValue, oldValue) {
        var form = combo.up('form');
        var vm = combo.lookupViewModel();
        if (oldValue) {
            vm.set('theImportExportTemplate.importKeyAttribute', null);
            vm.set('theImportExportTemplate.mergeMode_when_missing_update_attr', null);
        }
        form.form.checkValidity();
    },

    privates: {
        setAllowBlank: function (field, value, form) {
            if (field) {
                field.allowBlank = value;
                field.up('fieldcontainer').allowBlank = value;
                if (form && form.form) {
                    form.form.checkValidity();
                }
            }
        },
        /**
         * 
         * @param {CMDBuildUI.model.base.Filter} filter The filter to edit.
         */
        getAttributesFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-filters-attributes-panel',
                title: CMDBuildUI.locales.Locales.administration.attributes.attributes,
                viewModel: viewmodel
            };
            return filterPanel;
        },

        getRelationsFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-filters-relations-panel',
                title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations,
                viewModel: viewmodel
            };
            return filterPanel;
        },

        getFunctionFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-functionfilters-panel',
                title: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
                viewModel: viewmodel
            };
            return filterPanel;
        },

        getFulltextFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-fulltextfilters-panel',
                title: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fulltext,
                viewModel: viewmodel
            };
            return filterPanel;
        }
    }
});
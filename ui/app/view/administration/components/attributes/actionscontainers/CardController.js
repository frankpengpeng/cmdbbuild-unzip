Ext.define('CMDBuildUI.view.administration.components.attributes.actionscontainers.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-attributes-actionscontainers-card',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#saveAndAddBtn': {
            click: 'onSaveAndAddBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#attributedomain': {
            change: 'onAttributeDomainChange'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.processes.tabitems.attributes.card.EditController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        Ext.getStore('domains.Domains').load();
        var vm = view.getViewModel();
        if (vm.get('objectType') !== 'Domain') {
            vm.set('isGroupHidden', false);
            vm.set('isOtherPropertiesHidden', false);
        } else {
            view.down('#groupfield').destroy();
        }
        this.linkAttribute(view, vm);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this,
            form = me.getView(),
            vm = form.getViewModel(),
            eventToCall = vm.get('actions.edit') ? 'attributeupdated' : 'attributecreated';
        var successCb = function (record) {
            me.disableSaveButtons(form, false);
            if (vm.get('theTranslation')) {
                var localeObjectTypeName;
                switch (vm.get('objectType')) {
                    case 'Class':
                        localeObjectTypeName = 'attributeclass';
                        break;
                    case 'Process':
                        localeObjectTypeName = 'attributeclass';
                        break;
                    case 'Domain':
                        localeObjectTypeName = 'attributedomain';
                        break;
                }

                var key = Ext.String.format('{0}.{1}.{2}.description', localeObjectTypeName, vm.get('objectTypeName'), record.get('name'));
                vm.get('theTranslation').set('_id', key);
                vm.get('theTranslation').crudState = 'U';
                vm.get('theTranslation').crudStateWas = 'U';
                vm.get('theTranslation').phantom = false;
                vm.get('theTranslation').save({
                    success: function (record, operation) {
                        Ext.GlobalEvents.fireEventArgs(eventToCall, [record]);
                        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                    }
                });
            } else {
                Ext.GlobalEvents.fireEventArgs(eventToCall, [record]);
                CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
            }

        };

        var errorCb = function () {
            me.disableSaveButtons(form, false);
        };

        this.save(form, successCb, errorCb);
    },
    onEditMetadataClickBtn: function (event, buttonEl, eOpts) {
        var title = CMDBuildUI.locales.Locales.administration.emails.editvalues;
        var metadata = this.getViewModel().get('theAttribute').get('metadata');
        var _metadata = {};

        for (var key in metadata) {
            if (!Ext.String.startsWith(key, 'cm_')) {
                _metadata[key] = metadata[key];
            }
        }
        var config = {
            xtype: 'administration-components-keyvaluegrid-grid',
            viewModel: {
                data: {
                    theKeyvaluedata: _metadata,
                    theOwnerObject: this.getViewModel().get('theAttribute'),
                    theOwnerObjectKey: 'metadata',
                    actions: {
                        view: this.getViewModel().get('actions.view')
                    }
                }
            }

        };

        CMDBuildUI.util.Utilities.openPopup('popup-add-attachmentfromdms-panel', title, config, null, {
            ui: 'administration-actionpanel'
        });
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndAddBtnClick: function (button, e, eOpts) {

        var me = this,
            vm = me.getViewModel(),
            eventToCall = vm.get('actions.edit') ? 'attributeupdated' : 'attributecreated';
        var successCb = function (record) {
            Ext.GlobalEvents.fireEventArgs(eventToCall, [record]);
            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            var viewModel = {
                data: {
                    theAttribute: vm.get('selected'),
                    objectTypeName: vm.get('objectTypeName'),
                    objectType: vm.get('objectType'),
                    attributeName: vm.get('theAttribute').get('name'),
                    attributes: vm.get('attributes'),
                    grid: vm.get('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add
                }
            };

            container.removeAll();
            container.add({
                xtype: 'administration-components-attributes-actionscontainers-create',
                viewModel: viewModel
            });
        };

        var errorCb = function () {

        };
        me.save(button.up('form'), successCb, errorCb);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getViewModel().get("theAttribute").reject();
        this.getView().up().fireEvent("closed");
    },

    /**
     * On translate button click
     * @param {Event} e
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onTranslateClick: function (e, button, eOpts) {
        var vm = this.getViewModel();
        var localeObjectTypeName;
        switch (vm.get('objectType')) {
            case 'Class':
                localeObjectTypeName = 'attributeclass';
                break;
            case 'Process':
                localeObjectTypeName = 'attributeclass';
                break;
            case 'Domain':
                localeObjectTypeName = 'attributedomain';
                break;
        }
        var translationCode = Ext.String.format('{0}.{1}.{2}.description', localeObjectTypeName, vm.get('objectTypeName'), vm.get('actions.edit') ? vm.get('attributeName') : '.');
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(translationCode, vm.get('action'), 'theTranslation', vm);
    },


    // toolbar actions

    onOpenBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-components-attributes-actionscontainers-view',
            viewModel: {
                data: {
                    theAttribute: vm.get('theAttribute'),
                    objectTypeName: vm.get('objectTypeName'),
                    objectType: vm.get('objectType'),
                    attributeName: vm.get('theAttribute').get('name'),
                    attributes: vm.get('allAttributes').getRange(),
                    grid: vm.get('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                }
            }
        });
    },


    onEditBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var viewConfig = {
            xtype: 'administration-components-attributes-actionscontainers-edit',
            viewModel: {
                data: {
                    objectTypeName: vm.get('objectTypeName'),
                    attributeName: vm.get('attributeName'),
                    attributes: vm.get('attributes'),
                    title: vm.get('title'),
                    grid: vm.get('grid'),
                    theAttribute: vm.get('theAttribute'),
                    objectType: vm.get('objectType'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
                }
            }
        };
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        container.add(viewConfig);
    },

    onDeleteBtnClick: function () {
        var me = this;
        var vm = me.getViewModel();
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText.toLowerCase() === 'yes') {
                    CMDBuildUI.util.Ajax.setActionId('delete-attribute');

                    vm.get('theAttribute').getProxy().type = 'baseproxy';
                    vm.get('theAttribute').getProxy().url = Ext.String.format('/{0}/{1}/attributes/', vm.get('pluralObjectType'), vm.get('objectTypeName'));

                    vm.get('theAttribute').erase({
                        success: function (record, operation) {
                            Ext.ComponentQuery.query('administration-components-attributes-grid-grid')[0].getStore().reload();
                        },
                        failure: function () {
                            vm.get('theAttribute').reject();
                            Ext.GlobalEvents.fireEventArgs("attributeupdated", [vm.get('theAttribute')]);
                        }
                    });
                }
            }, this);
    },

    onCloneBtnClick: function (button, event, eOpts) {
        var vm = button.lookupViewModel();
        var theAttribute = Ext.copy(vm.get('theAttribute').clone());
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var viewModel = {
            data: {
                theAttribute: theAttribute,
                objectTypeName: vm.get('objectTypeName'),
                objectType: vm.get('objectType'),
                attributeName: vm.get('theAttribute').get('name'),
                attributes: vm.get('attributes'),
                grid: vm.get('grid'),
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-components-attributes-actionscontainers-create',
            viewModel: viewModel
        });
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theAttribute = vm.get('theAttribute');
        Ext.apply(theAttribute.data, theAttribute.getAssociatedData());
        var value = !theAttribute.get('active');
        CMDBuildUI.util.Ajax.setActionId('toggle-active-attribute');
        theAttribute.set('active', value);
        theAttribute.model = Ext.ClassManager.get('CMDBuildUI.model.Attribute');
        theAttribute.model.setProxy({
            type: 'baseproxy',
            url: Ext.String.format('/{0}/{1}/attributes/', vm.get('pluralObjectType'), vm.get('objectTypeName'))
        });

        theAttribute.save({
            success: function (record, operation) {
                var valueString = record.get('active') ? CMDBuildUI.locales.Locales.administration.common.messages.enabled : CMDBuildUI.locales.Locales.administration.common.messages.disabled;
                CMDBuildUI.util.Notifier.showSuccessMessage(Ext.String.format('{0} {1} {2}.',
                    record.get('name'),
                    CMDBuildUI.locales.Locales.administration.common.messages.was,
                    valueString), null, 'administration');
                Ext.GlobalEvents.fireEventArgs("attributeupdated", [record]);
            }
        });
    },

    privates: {
        disableSaveButtons: function (form, value) {
            if (form.down('#saveAndAddBtn')) {
                form.down('#saveAndAddBtn').setDisabled(value);
            }
            if (form.down('#saveBtn')) {
                form.down('#saveBtn').setDisabled(value);
            }
        },
        linkAttribute: function (view, vm) {

            var pluralObjectType;
            switch (vm.get('objectType')) {
                case 'Class':
                    pluralObjectType = 'classes';
                    break;
                case 'Process':
                    pluralObjectType = 'processes';
                    break;
                case 'Domain':
                    pluralObjectType = 'domains';
                    break;
            }

            Ext.ClassManager.get('CMDBuildUI.model.Attribute').setProxy({
                type: 'baseproxy',
                url: Ext.String.format('/{0}/{1}/attributes/', pluralObjectType, vm.get('objectTypeName'))
            });

            if (!vm.get('theAttribute') || !vm.get('theAttribute').phantom) {
                if (vm.get('theAttribute')) {
                    vm.linkTo("theAttribute", {
                        type: 'CMDBuildUI.model.Attribute',
                        id: vm.get('theAttribute.name')
                    });
                } else {
                    vm.linkTo("theAttribute", {
                        type: 'CMDBuildUI.model.Attribute',
                        create: true
                    });
                }
            }
        },
        save: function (form, successCb, errorCb) {
            var me = this;
            var vm = form.getViewModel();
            me.disableSaveButtons(form, true);

            if (form.isValid()) {
                var theAttribute = vm.getData().theAttribute;
                theAttribute.getProxy().setUrl(
                    Ext.String.format(
                        '/{0}/{1}/attributes',
                        vm.get('pluralObjectType'),
                        vm.get('objectTypeName')
                    )
                );
                delete theAttribute.data.inherited;
                delete theAttribute.data.writable;
                delete theAttribute.data.hidden;

                theAttribute.save({
                    success: function (record, operation) {
                        successCb(record);
                    },
                    failure: function (reason) {
                        errorCb();
                    }
                });
            } else {
                errorCb();
            }
        }
    },

    onAttributeDomainChange: function (combobox, newValue, oldValue) {
        var vm = this.getViewModel();

        var store = vm.getStore('domainsStore');
        if (store) {
            var record = store.getById(newValue);
            var directionCombo = combobox.up('form').down('#domaindirection');
            switch (record.get('cardinality')) {
                case '1:N':
                    directionCombo.hidden = true;
                    directionCombo.setValue('inverse');
                    break;
                case 'N:1':
                    directionCombo.hidden = true;
                    directionCombo.setValue('direct');
                    break;
                case '1:1':
                    directionCombo.hidden = false;
                    break;
                case 'N:N':
                    // TODO: currently not supported
                    break;
                default:
                    break;
            }
        }
    }
});
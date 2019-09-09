Ext.define('CMDBuildUI.view.administration.components.attributes.actionscontainers.ViewInRowController', {
    extend: 'CMDBuildUI.view.administration.components.attributes.actionscontainers.CardController',
    alias: 'controller.administration-components-attributes-actionscontainers-viewinrow',
    
    listen: {
        global: {
            attributeupdated: 'onAttributeUpdated'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            itemupdated: 'onAttributeUpdated'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        }
    },

    /**
     * @override
     * @param {CMDBuildUI.view.administration.components.attributes.actionscontainer.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        // Ext.suspendLayouts();
        var vm = view.lookupViewModel();

        vm.set('theAttribute', view._rowContext.record);
        vm.set('allAttributes', view._rowContext.viewModel.get('allAttributes'));
        vm.set('objectTypeName', view._rowContext.ownerGrid.getViewModel().get('objectTypeName'));
        vm.set('objectType', view._rowContext.ownerGrid.getViewModel().get('objectType'));
        vm.set('attributeName', view._rowContext.record.get('name'));
        vm.set('attributes', view._rowContext.ownerGrid.getViewModel().get('allAttributes').getData().items);
        vm.set('grid', Ext.copy(view._rowContext.ownerGrid));
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);

        if (vm.data && vm.get('objectType') !== 'Domain' && view.child('#otherproperties')) {
            view.child('#otherproperties').tab.show();
            vm.set('isGroupHidden', false);
        } else {
            if (view.down('#groupfield')) {
                view.down('#groupfield').destroy();
            }
        }
    },

    onViewMetadataClick: function (event, buttonEl, eOpts) {
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
                    theOwnerObjectKey: 'metadata'
                }
            }

        };
        CMDBuildUI.util.Utilities.openPopup('popup-add-attachmentfromdms-panel', title, config, null, {
            ui: 'administration-actionpanel'
        });
    },

    onAttributeUpdated: function (view, record) {
        new Ext.util.DelayedTask(function () { }).delay(
            100,
            function (view, record) {
                if (view.crudState) {
                    record = Ext.copy(view);
                    view = this.getView();
                }
                var vm = view.lookupViewModel();
                vm.set('theAttribute', record);
            },
            this,
            arguments);

    },

    /**
     * @override
     */
    onEditBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        
        container.add({
            xtype: 'administration-components-attributes-actionscontainers-edit',
            viewModel: {
                data: {
                    theAttribute: vm.get('theAttribute'),
                    objectTypeName: vm.get('objectTypeName'),
                    objectType: vm.get('objectType'),
                    attributeName: vm.get('theAttribute').get('name'),
                    attributes: vm.get('allAttributes').getData().items,
                    grid: vm.get('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
                }
            }
        });
    },
    onCloneBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var theAttribute = Ext.copy(vm.get('theAttribute').clone());
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-components-attributes-actionscontainers-create',
            viewModel: {
                data: {
                    theAttribute: theAttribute,
                    objectTypeName: vm.get('objectTypeName'),
                    objectType: vm.get('objectType'),
                    attributeName: vm.get('theAttribute').get('name'),
                    attributes: vm.get('allAttributes').getData().items,
                    grid: vm.get('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add
                }
            }
        });
    },

    privates: {
        setAttributeProxyUrl: function (record, vm) {
            var pluralObjectType;
            if (vm.get('theObject')) {
                pluralObjectType = 'classes';
            } else if (vm.get('theProcess')) {
                pluralObjectType = 'processes';
            } else if (vm.get('theDomain')) {
                pluralObjectType = 'domains';
            }
            record.model = Ext.ClassManager.get('CMDBuildUI.model.Attribute');
            record.model.setProxy({
                type: 'baseproxy',
                url: Ext.String.format('/{0}/{1}/attributes/', pluralObjectType, vm.get('objectTypeName'))
            });
        }
    }
});
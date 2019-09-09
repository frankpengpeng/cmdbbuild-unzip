Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.UiConfigController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-groupsandpermissions-tabitems-uiconfig-uiconfig',

    control: {
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        vm.set('actions.view', false);
        vm.set('actions.edit', true);
        vm.set('actions.add', false);
        vm.toggleEnableTabs(3);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var me = this;
        button.setDisabled(true);
        var vm = me.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        if (!vm.get('theGroup').isValid()) {
            var validatorResult = vm.get('theGroup').validate();
            var errors = validatorResult.items;
            for (var i = 0; i < errors.length; i++) {            
                // console.log('Key :' + errors[i].field + ' , Message :' + errors[i].msg);
            }
        } else {
            var theGroup = vm.get('theGroup');
            delete theGroup.data.system;
            Ext.apply(theGroup.data, theGroup.getAssociatedData());
            Ext.suspendLayouts();
            vm.invertAccessFields(theGroup);          
            theGroup.save({
                failure: function () {
                    button.setDisabled(false);
                    vm.invertAccessFields(theGroup);
                    Ext.resumeLayouts();
                },
                success: function (record, operation) {
                    button.setDisabled(false);
                    var objectTypeName = record.getId();
                    var nextUrl = Ext.String.format('administration/groupsandpermissions/{0}', objectTypeName);
                    CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                        function () {
                            var treestore = Ext.getCmp('administrationNavigationTree');
                            var selected = treestore.getStore().findNode("href", nextUrl);
                            treestore.setSelection(selected);
                        });
                    vm.linkTo('theGroup', {
                        reference: 'CMDBuildUI.model.users.Group',
                        id: record.getId()
                    });

                    vm.set('actions.view', true);
                    vm.set('actions.edit', false);
                    vm.set('actions.add', false);
                    vm.toggleEnableTabs();
                    Ext.resumeLayouts();
                },
                callback: function () {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
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
        var vm = this.getView().up('administration-content-groupsandpermissions-view').getViewModel();
        vm.get('theGroup').reject();
        vm.set('actions.view', true);
        vm.set('actions.edit', false);
        vm.set('actions.add', false);
        vm.invertAccessFields(vm.get('theGroup'));
        vm.toggleEnableTabs();
    }

});
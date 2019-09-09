Ext.define('CMDBuildUI.view.administration.content.users.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-users-grid',
    listen: {
        global: {
            userupdated: 'onUserUpdated',
            usercreated: 'onUserCreated'
        }
    },

    control: {
        '#': {
            sortchange: 'onSortChange',
            //afterrender: 'onAfterRender'
            afterrender: 'onAfterRender'
        },
        '#adduser': {
            click: 'onNewBtnClick'
        },
        tableview: {
            deselect: 'onDeselect',
            select: 'onSelect',
            rowdblclick: 'onRowDblclick'
        }
    },

    onAfterRender: function (view) {
        var vm = view.up('administration-content-users-view').getViewModel();
        vm.getStore("allUsers").load();
        vm.getStore("allGroups").getSource().load();
    },

    onIncludeDisabledChange: function (field, newValue, oldValue) {
        var vm = this.getViewModel();

        var filterCollection = vm.get("allUsers").getFilters();

        if (newValue === true) {
            filterCollection.removeByKey('enabledFilter');
        } else {
            filterCollection.add({
                id: 'enabledFilter',
                property: 'enabled',
                value: !newValue
            });

        }
    },

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onDeselect: function (row, record, index, eOpts) {
        this.getView().getPlugins()[0].enable();
    },

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onSelect: function (row, record, index, eOpts) {
        this.getView().setSelection(record);
    
        Ext.GlobalEvents.fireEventArgs('selecteduser', [record]);
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (item, event, eOpts) {

        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-users-card-create',
            viewModel: {
                links: {
                    theUser: {
                        type: 'CMDBuildUI.model.users.User',
                        create: true
                    }
                }
            }
        });
    },

    onUserUpdated: function (form, record) {
        var view = this.getView();
        view.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [this.getView(), record, this]);
    },

    onUserCreated: function (record) {
        var view = this.getView();

        var store = view.getStore();
        store.load();

        store.on('load', function (records, operation, success) {
            view.getView().refresh();
            Ext.asap(function () {
                var store = view.getStore();
                var index = store.findExact("_id", record.getId());
                var storeItem = store.getAt(index);

                view.getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, storeItem, index]);
            });
        });
    },

    onSortChange: function () {
        if (this.view.getSelection().length) {
            var store = this.view.getStore();
            var index = store.findExact("_id", this.view.getSelection()[0].get('_id'));
            var record = store.getAt(index);
            // repeat two time to avoid grid crash 
            this.view.getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, record, index]);
            this.view.getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, record, index]);
        }
    },

    /**
     * 
     * @param {*} row 
     * @param {*} record 
     * @param {*} element 
     * @param {*} rowIndex 
     * @param {*} e 
     * @param {*} eOpts 
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var view = this.getView(),
            container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var formInRow = view.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        view.setSelection(record);
        
        container.removeAll();
        container.add({
            xtype: 'administration-content-users-card-edit',
            viewModel: {
                data: {
                    theUser: record,
                    title: Ext.String.format('{0} {1}', CMDBuildUI.locales.Locales.administration.navigation.users, (record.get('username').length) ? Ext.String.format(' - {0}', record.get('name')) : ''),
                    grid: view.ownerGrid,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                }
            }
        });
    }

});
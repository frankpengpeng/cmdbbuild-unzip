
Ext.define('CMDBuildUI.view.fields.reference.ReferenceCombo', {
    extend: 'Ext.form.field.ComboBox',

    requires: [
        'CMDBuildUI.view.fields.reference.ReferenceComboController',
        'CMDBuildUI.view.fields.reference.ReferenceComboModel'
    ],

    alias: 'widget.referencecombofield',
    controller: 'fields-referencecombofield',
    viewModel: {
        type: 'fields-referencecombofield'
    },

    valueField: '_id',
    displayField: 'Description',
    autoLoadOnValue: false,
    autoSelect: false,
    autoSelectLast: false,

    // query configuration
    anyMatch: true,
    queryMode: 'local',
    queryDelay: 250,

    forceSelection: true,
    typeAhead: false,

    config: {
        /**
         * @cfg {String} recordLinkName (required)
         * The name of the full record in ViewModel used for 
         * value binding.
         */
        recordLinkName: null
    },

    bind: {
        store: '{options}',
        selection: '{selection}'
    },

    triggers: {
        clear: {
            cls: 'x-form-clear-trigger',
            handler: function (combo, trigger, eOpts) {
                combo.fireEvent("cleartrigger", combo, trigger, eOpts);
            }
        },
        search: {
            cls: 'x-form-search-trigger',
            handler: function (combo, trigger, eOpts) {
                combo.fireEvent("searchtrigger", combo, trigger, eOpts);
            }
        }
    },

    /**
     * @override
     * @method
     * Template method, it is called when a new store is bound
     * to the current instance.
     * @protected
     * @param {Ext.data.AbstractStore} store The store being bound
     * @param {Boolean} initial True if this store is being bound as initialization of the instance.
     */
    onBindStore: function (store, initial) {
        var vm = this.getViewModel();
        var me = this;
        if (store.getModel().getName()) {
            // bind reference to dependences
            if (this.metadata.ecqlFilter && this.getRecordLinkName()) {
                var binds = CMDBuildUI.util.ecql.Resolver.getViewModelBindings(
                    this.metadata.ecqlFilter,
                    this.getRecordLinkName()
                );
                if (!Ext.Object.isEmpty(binds)) {
                    vm.bind({
                        bindTo: binds
                    }, function (data) {
                        store.getAdvancedFilter().addEcqlFilter(me.getEcqlFilter());
                        store.load();
                    });
                }
                // add before load listener
                store.getAdvancedFilter().addEcqlFilter(me.getEcqlFilter());
            }
            // add load listener
            store.addListener("load", this.onStoreLoaded, this);
            // load store
            store.load();
        }
        if (this.getInitialConfig().bind && !Ext.Object.isEmpty(this.getInitialConfig().bind)) {
            vm.bind({
                bindTo: this.getInitialConfig().bind.value
            }, function (value) {
                vm.set("initialvalue", value);
            });
        } else {
            vm.set("initialvalue", null);
        }
        this.callParent(arguments);
    },

    /**
     * Called when store is loaded
     * @param {Ext.data.Store} store
     * @param {Ext.data.Model[]} records An array of records
     * @param {Boolean} successful True if the operation was successful.
     * @param {Ext.data.operation.Read} operation The {@link Ext.data.operation.Read Operation} object that was used in the data load call
     * @param {Object} eOpts
     */
    onStoreLoaded: function (store, records, successful, operation, eOpts) {
        // add record if it is not in list
        var count = store.getTotalCount();
        if (!store.getById(this.lookupViewModel().get("initialvalue")) && this._ownerRecord) {
            var _id = this._ownerRecord.get(this.name);
            var desc = this._ownerRecord.get("_" + this.name + "_description");
            if (_id && desc) {
                store.add([{
                    _id: _id,
                    Description: desc
                }]);
                this.setValue(this._ownerRecord.get(this.name));
                count++;
            }
        }
        // preselect item if unique
        if (
            (this.metadata.preselectIfUnique === true ||this.metadata.preselectIfUnique === "true") && 
            count === 1
        ) {
            this.setValue(store.getAt(0).getId());
        }
        // check expander
        if (count > CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.referencecombolimit)) {
            this._allowexpand = false;
        } else {
            this._allowexpand = true;
        }
    },

    /**
     * Get proxy store extra params
     * @return {Object}
     */
    getEcqlFilter: function () {
        var ecql;
        var obj = this._ownerRecord;
        if (!obj) {
            obj = this.lookupViewModel().get(this.getRecordLinkName());
        }
        if (this.metadata.ecqlFilter && obj) {
            ecql = CMDBuildUI.util.ecql.Resolver.resolve(this.metadata.ecqlFilter, obj);
        }
        return ecql || {};
    },

    /**
     * @override
     * Expands this field's picker dropdown.
     */
    expand: function (searchquery) {
        if (this._allowexpand === true) {
            this.callParent(arguments);
        } else {
            this.fireEvent("searchtrigger", this, null, {searchquery: searchquery});
        }
    },

    privates: {
        _allowexpand: null
    }
});

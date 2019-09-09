Ext.define('CMDBuildUI.view.fields.reference.ReferenceComboModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.fields-referencecombofield',

    data: {
        selection: null,
        storeinfo: {
            autoload: false, // load store on onBindStore function
            extraparams: {}
        }
    },

    formulas: {
        updateStoreInfo: {
            bind: {
                initialvalue: '{initialvalue}'
            },
            get: function (data) {
                var view = this.getView();
                var url, object;
                // set url
                switch (view.metadata.targetType) {
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                        url = CMDBuildUI.util.api.Classes.getCardsUrl(view.metadata.targetClass);
                        object = CMDBuildUI.util.helper.ModelHelper.getClassFromName(view.metadata.targetClass);
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                        url = CMDBuildUI.util.api.Processes.getInstancesUrl(view.metadata.targetClass);
                        object = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(view.metadata.targetClass);
                        break;
                }
                this.set("storeinfo.proxyurl", url);

                // page size
                this.set("storeinfo.pagesize", CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.referencecombolimit));

                // sorters
                var sorters = [];
                if (object && object.defaultOrder().getCount()) {
                    object.defaultOrder().getRange().forEach(function (o) {
                        sorters.push({
                            property: o.get("attribute"),
                            direction: o.get("direction") === "descending" ? "DESC" : 'ASC'
                        });
                    });
                } else {
                    sorters.push({
                        property: 'Description'
                    });
                }
                this.set("storeinfo.sorters", sorters);

                if (data.initialvalue) {
                    this.set("storeinfo.extraparams.positionOf", data.initialvalue);
                }
            }
        }
    },

    stores: {
        options: {
            model: 'CMDBuildUI.model.domains.Reference',
            proxy: {
                type: 'baseproxy',
                url: '{storeinfo.proxyurl}',
                extraParams: '{storeinfo.extraparams}'
            },
            remoteFilter: false,
            remoteSort: true,
            pageSize: '{storeinfo.pagesize}',
            sorters: '{storeinfo.sorters}',
            autoLoad: '{storeinfo.autoload}',
            autoDestroy: true
        }
    }
});

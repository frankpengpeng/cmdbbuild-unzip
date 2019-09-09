Ext.define('CMDBuildUI.view.relations.masterdetail.TabPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-masterdetail-tabpanel',

    control: {
        '#': {
            added: 'onAdded',
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.relations.masterdetail.TabPanel} view 
     * @param {Ext.container.Container} container 
     * @param {Number} position 
     * @param {Object} eOpts 
     */
    onAdded: function (view, container, position, eOpts) {
        var vm = this.getViewModel();
        var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(vm.get("objectTypeName"), vm.get("objectType"));
        var mddomains = [];
        item.getDomains().then(function (domains) {
            domains.each(function (d) {
                if (d.get("isMasterDetail")) {
                    if (d.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.onetomany && Ext.Array.contains(item.getHierarchy(), d.get("source"))) {
                        d.set("index", d.get("indexDirect"));
                        mddomains.push(d);
                    } else if (d.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.manytoone && Ext.Array.contains(item.getHierarchy(), d.get("destination"))) {
                        d.set("index", d.get("indexInverse"));
                        mddomains.push(d);
                    }
                }
            });
            Ext.Array.sort(mddomains, function(a, b) {
                if (a.get("index") < b.get("index")) {
                    return -1;
                } else if (a.get("index") > b.get("index")) {
                    return 1;
                } 
                return 0;
            });
            vm.set("mddomains", mddomains);
        });
    },

    /**
     *  load store
     */
    onStoreLoaded: function () {
        var vm = this.getViewModel();
        var view = this.getView();
        var foreignKeys = vm.get('foreignkeys');
        var fkdomains = vm.get('fkdomains');
        if (fkdomains) {
            fkdomains.getRange().forEach(function (record) {
                var newElement = Ext.create('CMDBuildUI.model.domains.Domain', {
                    cardinality: record.get('cardinality'),
                    descriptionMasterDetail: record.get('descriptionMasterDetail'),
                    destination: record.get('destination'),
                    fk_attribute_name: record.get('fk_attribute_name'),
                    id: record.get('_id'),
                    _id: record.get('_id'),
                    isMasterDetail: record.get('isMasterDetail'),
                    source: record.get('source'),
                    name: record.get('_id'),
                    fkDomain: true
                });
                foreignKeys.push(newElement);
            });

            var tabs = [];
            foreignKeys.forEach(function (d) {
                var targetTypeName, isProcess, index;
                if (d.get("cardinality") === '1:N') {
                    targetTypeName = d.get("destination");
                    isProcess = d.get("destinationProcess");
                    index = d.get("indexDirect");
                } else if (d.get("cardinality") === 'N:1') {
                    targetTypeName = d.get("source");
                    isProcess = d.get("sourceProcess");
                    index = d.get("indexInverse");
                }
                // add tab panel
                tabs.push({
                    xtype: 'relations-masterdetail-tab',
                    index: index,
                    viewModel: {
                        data: {
                            domain: d,
                            targetType: isProcess ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                            targetTypeName: targetTypeName
                        }
                    }
                });
            });
            tabs.sort(function (a, b) {
                return a.index === b.index ? 0 : (a.index < b.index ? -1 : 1);
            });
            view.add(tabs);
            view.setActiveTab(0);
        }
    },


    /**
     * @param {CMDBuildUI.view.relations.masterdetail.TabPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        vm.bind({
            bindTo: '{mddomains.length}'
        }, function (length) {
            var tabs = [];
            vm.get("mddomains").forEach(function (d) {
                var targetTypeName, isProcess, index;
                if (d.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.onetomany) {
                    targetTypeName = d.get("destination");
                    isProcess = d.get("destinationProcess");
                    index = d.get("indexDirect");
                } else if (d.get("cardinality") === CMDBuildUI.model.domains.Domain.cardinalities.manytoone) {
                    targetTypeName = d.get("source");
                    isProcess = d.get("sourceProcess");
                    index = d.get("indexInverse");
                }
                // add tab panel
                tabs.push({
                    xtype: 'relations-masterdetail-tab',
                    index: index,
                    viewModel: {
                        data: {
                            domain: d,
                            targetType: isProcess ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                            targetTypeName: targetTypeName
                        }
                    }
                });
            });
            tabs.sort(function (a, b) {
                return a.index === b.index ? 0 : (a.index < b.index ? -1 : 1);
            });
            view.add(tabs);
            view.setActiveTab(0);
        });

    }

});
Ext.define('CMDBuildUI.view.classes.cards.relations.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-relations-panel',
    data: {
        storeinfo: {
            data: []
        }
    },

    formulas: {
        updateStoreData: {
            bind: {
                id: '{objectId}',
                name: '{objectTypeName}',
                theObject: '{theObject}'
            },
            get: function (data) {
                var domains = [];
                var me = this;
                var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.name); 
                item.getDomains().then(function (itemdomains, loaded) {
                    
                    var hierarchy = item.getHierarchy();
                    function addToDomainsList(domain, destination, description, destIsProcess, direction) {
                        var cardinality = domain.get('cardinality');
                        var isDisabled = false;                     
                        if ((cardinality == CMDBuildUI.model.domains.Domain.cardinalities.onetomany) || (cardinality == CMDBuildUI.model.domains.Domain.cardinalities.onetoone)) {
                            isDisabled = true
                        }                        
                        if (destination) {
                            // get destination object
                            var destObj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                destination,
                                destIsProcess ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass
                            );

                            // add domain in list
                            domains.push({
                                domain: domain.getId(),
                                description: description,
                                destination: destination,
                                destinationDescription: destObj.getTranslatedDescription(),
                                destinationIsProcess: destIsProcess,
                                direction: direction,
                                isDisabled: isDisabled
                            });
                        
                        }
                    }

                    itemdomains.each(function (d) {

                        
                        // direct domain
                        if (
                            Ext.Array.contains(hierarchy, d.get("source")) &&
                            !Ext.Array.contains(d.get("disabledSourceDescendants"), data.name)
                        ) {
                            addToDomainsList(
                                d,
                                d.get("destination"),
                                d.get("_descriptionDirect_translation") || d.get("descriptionDirect"),
                                d.get("destinationProcess"),
                                "_1"
                            );
                        }

                        // inverse domain
                        if (
                            Ext.Array.contains(hierarchy, d.get("destination")) &&
                            !Ext.Array.contains(d.get("disabledDestinationDescendants"), data.name)
                        ) {
                            addToDomainsList(
                                d,
                                d.get("source"),
                                d.get("_descriptionInverse_translation") || d.get("descriptionInverse"),
                                d.get("sourceProcess"),
                                "_2"
                            );
                        }

                    });
                    me.set("storeinfo.data", domains);
                });
            }
        }
    },

    stores: {
        relations: {
            model: 'CMDBuildUI.model.domains.Clone',
            autoDestroy: true,
            data: '{storeinfo.data}',
            groupField: '_type',
            proxy: {
                type: 'memory'
            }
        },

        selected: {
            model: 'CMDBuildUI.model.domains.Clone',
            autoDestroy: true,
            data: '{storeinfo.data}',
            proxy: {
                type: 'baseproxy',
                url: '{storedata.proxyurl}'
            }
        }
    }
});

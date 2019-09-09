Ext.define('CMDBuildUI.view.administration.content.gis.geoserverslayers.card.ViewEditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gis-geoserverslayers-card-viewedit',
    control: {
        '#': {
            beforeRender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration-content-gis-geoserverslayers-card-viewedit} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, e, eOpts) {
        var vm = view.getViewModel();
        var title = vm.get('actions.add') ? 'New Layer' : vm.get('theLayer').get('name');
        view.up('administration-detailswindow').getViewModel().set('title', title);
        if (!vm.get('theLayer') || !vm.get('theLayer').phantom) {
            if (vm.get('theLayer')) {
                vm.linkTo("theLayer", {
                    type: 'CMDBuildUI.model.map.GeoLayers',
                    create: vm.get('theLayer').getData()
                });
            }
        }
    },


    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClickF: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var form = this.getView();
        if (form.isValid()) {
            var theLayer = vm.get('theLayer');
            var className = theLayer.get('owner_type');
            var cardId = theLayer.get('owner_id');
            var id = theLayer.crudState === 'C' ? '' : '/' + theLayer.get('_id');
            theLayer.getProxy().setUrl(Ext.String.format(
                '{0}/classes/{1}/cards/{2}/geolayers{3}',
                CMDBuildUI.util.Config.baseUrl,
                className,
                cardId,
                id
            ));
            theLayer.save({
                success: function (record, operation) {
                    this.getView().up('panel').close();
                }
            });
        }

    },
    
    onSaveBtnClick: function (button, event, eOpts) {
        
        var vm = button.lookupViewModel();
        CMDBuildUI.util.Ajax.setActionId('geoserverlayers.upload');
        // define method
        var method = vm.get("actions.add") ? "POST" : "PUT";
        var input = this.getView().down('[name="file"]').extractFileInput();

        // init formData
        var formData = new FormData();

        // append attachment json data
        var theLayer = vm.get('theLayer');
        theLayer.set('geoserver_name', theLayer.get('name'));
        var jsonData = Ext.encode(theLayer.getData());
        var fieldName = 'data';
        try {
            formData.append(fieldName, new Blob([jsonData], {
                type: "application/json"
            }));
        } catch (err) {
            CMDBuildUI.util.Logger.log("Unable to create attachment Blob FormData entry with type 'application/json', fallback to 'text/plain': " + err, CMDBuildUI.util.Logger.levels.error);
            // metadata as 'text/plain' (format compatible with older webviews)
            formData.append(fieldName, jsonData);
        }
        // get url

        var className = theLayer.get('owner_type');
        var cardId = theLayer.get('owner_id');
        var id = theLayer.crudState === 'C' ? '' : '/' + theLayer.get('_id');
        theLayer.getProxy().setUrl();
        var url = Ext.String.format(
            '{0}/classes/{1}/cards/{2}/geolayers{3}',
            CMDBuildUI.util.Config.baseUrl,
            className,
            cardId,
            id
        );
        // var url = vm.get('actions.add') ? reportsUrl : Ext.String.format('{0}/{1}', reportsUrl, vm.get('theReport._id'));
        // upload             
        CMDBuildUI.util.Ajax.initRequestException();
        CMDBuildUI.util.administration.File.upload(method, formData, input, url, {
            success: function (response) {
                button.getView().up('panel').close();
            },
            failure: function (error) {
                
            }
        });
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getView().up('panel').close();
    },

    onEditBtnClick: function () {
        this.getViewModel().set('actions.edit', true);
        this.getViewModel().set('actions.view', false);

    }
});
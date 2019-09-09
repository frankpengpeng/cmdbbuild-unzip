Ext.define('CMDBuildUI.view.administration.content.bim.projects.card.ViewEditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bim-projects-card-viewedit',
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
        var me = this;
        var vm = this.getViewModel();
        vm.bind({
            bindTo: {
                panelTitle: '{panelTitle}'
            }
        }, function (data) {
            if (data.panelTitle) {
                me.getView().up().setTitle(data.panelTitle);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        var form = me.getView();
        var inputFileIFC = me.lookupReference("fileIFC").extractFileInput().files[0];
        var inputFileMapping = me.lookupReference("fileMapping").extractFileInput().files[0];

        var viewports = Ext.ComponentQuery.query('viewport');
        var grid = viewports[0].down('administration-content-bim-projects-grid');

        if (form.isValid()) {
            CMDBuildUI.util.Utilities.addLoadMask(form);
            var theProject = vm.get('theProject');
            var eventtocall = vm.get('theProject').phantom ? 'itemcreated' : 'itemupdated';
            var callback = function (record, operation) {
                var plugin = grid.getPlugin('administration-forminrowwidget');
                if (plugin) {
                    plugin.view.fireEventArgs(eventtocall, [grid, record, this]);
                }
                if (!inputFileIFC) {
                    form.up("panel").close();
                    if (form && form.loadMask) {
                        CMDBuildUI.util.Utilities.removeLoadMask(form);
                    }
                } else {
                    me.sendFileIfc(inputFileIFC);
                }
            };
            if (inputFileMapping) {
                me.sendFileMapping(inputFileMapping, theProject, inputFileIFC, callback);
            } else {
                theProject.save({
                    success: callback
                });
            }
        }
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
    },

    sendFileIfc: function (inputFileIFC) {
        var me = this;
        var reader = new FileReader();

        reader.addEventListener("load", function () {
            var res = reader.result;
            var ifctype;
            if (res.search("(('IFC2X3'))")) {
                ifctype = 'ifc2x3tc1';
            } else {
                ifctype = 'ifc4';
            }
            me.prepareCall(inputFileIFC, ifctype);
        }, false);

        if (inputFileIFC) {
            reader.readAsText(inputFileIFC);
        }
    },

    prepareCall: function (inputFileIFC, ifctype) {
        var me = this;
        var form = me.getView();
        var vm = me.getViewModel();
        // init formData
        var formData = new FormData();
        // append attachment json data
        var jsonData = Ext.encode(vm.get("theProject").getData());
        var fieldName = 'fileIfc';
        try {
            formData.append(fieldName, new Blob([jsonData], {
                type: "application/json"
            }));
        } catch (err) {
            CMDBuildUI.util.Logger.log(
                "Unable to create attachment Blob FormData entry with type 'application/json', fallback to 'text/plain': " + err,
                CMDBuildUI.util.Logger.levels.error
            );
            // metadata as 'text/plain' (format compatible with older webviews)
            formData.append(fieldName, jsonData);
        }
        // get url
        var url = Ext.String.format('{0}/bim/projects/{1}/file?ifcFormat={2}', CMDBuildUI.util.Config.baseUrl, vm.get('theProject').get('_id'), ifctype);
        // define method
        var method = "POST";
        CMDBuildUI.util.File.upload(method, formData, inputFileIFC, url, {
            success: function () {
                // var viewports = Ext.ComponentQuery.query('viewport');
                // var grid = viewports[0].down('administration-content-bim-projects-grid');
                // var plugin = grid.getPlugin('administration-forminrowwidget');
                // if (plugin) {
                //     plugin.view.fireEventArgs(eventtocall, [grid, record, this]);
                // }
                form.up("panel").close();
                if (form && form.loadMask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(form);
                }
            },
            failure: function (error) {
                form.up("panel").close();
                CMDBuildUI.util.Notifier.showErrorMessage(error);
                if (form && form.loadMask) {
                    CMDBuildUI.util.Utilities.removeLoadMask(form);
                }
            }
        });
    },

    sendFileMapping: function (inputFileMapping, theProject, inputFileIFC, callback) {
        var me = this;
        var form = me.getView();
        //var file = input.fileInputEl.dom.files[0];
        var reader = new FileReader();

        reader.addEventListener("load", function () {
            theProject.set('fileMapping', reader.result);
            theProject.save({
                success: callback
            });
        }, false);

        if (inputFileMapping) {
            reader.readAsText(inputFileMapping);
        }
    }
});
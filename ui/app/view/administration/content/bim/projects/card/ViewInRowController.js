Ext.define('CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-bim-projects-card-viewinrow',
    control: {
        '#': {
            beforeRender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
        },
        '#downloadBtn': {
            click: 'onDownloadBtnClick'
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
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var record = view.getInitialConfig()._rowContext.record;
        vm.set("theProject", record);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onEditBtnClick: function (button, eOpts) {
        var vm = this.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var theProject = vm.get('theProject');
        container.removeAll();        
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                links: {
                    theProject: {
                        type: 'CMDBuildUI.model.bim.Projects',
                        id: theProject.getId()
                    }
                },
                data: {
                    actions: {
                        edit: true,
                        view: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onOpenBtnClick: function (button, eOpts) {
        var vm = this.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var theProject = vm.get('theProject').copy();
        var cardDescription = vm.get('cardDescription');
        theProject.set('cardDescription', cardDescription);
        container.removeAll();
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                data: {
                    theProject: theProject,
                    actions: {
                        edit: false,
                        view: true,
                        add: false
                    }
                }
            }
        });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onDownloadBtnClick: function (button, eOpts) {
        var vm = this.getView().getViewModel();
        var url = Ext.String.format('{0}/bim/projects/{1}/file', CMDBuildUI.util.Config.baseUrl, vm.get('theProject').getId());
        window.open(url, '_blank');
    },
    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onCloneBtnClick: function (button, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var clonedProject = Ext.copy(vm.get('theProject').clone());
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-bim-projects-card-viewedit',
            viewModel: {
                data: {
                    theProject: clonedProject,
                    actions: {
                        edit: false,
                        view: false,
                        add: true
                    }
                }
            }
        });
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onDeleteBtnClick: function (button, eOpts) {
        var me = this;
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-project');
                    me.getViewModel().get('theProject').erase({
                        success: function (record, operation) { }
                    });
                }
            }, this);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController} view
     * @param {Object} eOpts
     */

    onToggleActiveBtnClick: function (button, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theProject = vm.get('theProject');
        theProject.set('active', !theProject.get('active'));
        theProject.save({
            success: function (record, operation) {

            },
            failure: function (record, reason) {
                record.reject();
            }
        });

    }
});
Ext.define('CMDBuildUI.view.administration.content.gisnavigationtrees.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gisnavigationtrees-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        'domainsClassTree': {
            checkchange: 'onCheckChange'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }

    },



    /**
     * Before render
     * @param {CMDBuildUI.view.administration.content.navigationtrees.View} view
     */
    onBeforeRender: function (view) {

        var vm = view.getViewModel();
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.gisnavigation);
        var gisNavigation = Ext.getStore('navigationtrees.NavigationTrees').findRecord('_id', 'gisnavigation');

        if (gisNavigation) {
            vm.linkTo('theNavigationtree', {
                type: 'CMDBuildUI.model.administration.AdminNavTree',
                id: 'gisnavigation'
            });
        } else {
            vm.linkTo('theNavigationtree', {
                type: 'CMDBuildUI.model.administration.AdminNavTree',
                create: {
                    _id: 'gisnavigation',
                    name: 'gisnavigation',
                    description: 'Gisnavigation'
                }
            });
            vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        }
    },
    /**
     * On add navigationtree button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, e, eOpts) {
        this.redirectTo('administration/navigationtrees_empty/true');
    },

    /**
     * On delete navigationtree button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this;
        Ext.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-navigationtree');
                    me.getViewModel().get('theNavigationtree').erase({
                        success: function (record, operation) {
                            var nextUrl = 'administration/gis/gisnavigation';
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', Ext.util.History.getToken(), nextUrl, me);
                        }
                    });
                }
            }, this);
    },


    /**
     * On edit navigationtree button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * On cancel button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        if (vm.get('actions.add')) {
            var store = Ext.getStore('administration.MenuAdministration');
            var vmNavigation = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode("objecttype", 'gis');
            vmNavigation.set('selected', currentNode);
            this.redirectTo('administration/gis/gisnavigation', true);
        } else {
            vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        }
    },

    /**
     * On disable navigationtree button click {
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDisableNavigationtreeBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        vm.set('theNavigationtree.active', false);
        this.save(vm);
    },

    /**
     * On enable navigationtree button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEnableNavigationtreeBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        vm.set('theNavigationtree.active', true);
        this.save(vm);
    },

    /**
     * On save button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = this.getView().getForm();
        var vm = this.getView().getViewModel();
        var treeView = this.getView().down('administration-content-gisnavigationtrees-tree');

        if (form.isValid()) {
            var data = {};
            var node = treeView.getStore().getRootNode();
            if (node.data.checked) {
                if (node.get('root')) {
                    data.description = vm.get('theNavigationtree.description') || vm.get('theNavigationtree.name');
                    data.name = vm.get('theNavigationtree.name');
                    data.active = vm.get('theNavigationtree.active');
                    data._id = vm.get('theNavigationtree._id');
                    data.nodes = [{
                        direction: '_1',
                        domain: node.get('domain'),
                        filter: node.get('filter'),
                        nodes: me.collectAllCheckedNodes(node),
                        recursionEnabled: node.get('recursionEnabled'),
                        showOnlyOne: node.get('showOnlyOne'),
                        targetClass: node.get('targetClass')
                    }];
                }
            }
            var method = vm.get('theNavigationtree').crudState === 'U' ? 'PUT' : 'POST';

            Ext.Ajax.request({
                url: Ext.String.format('{0}/domainTrees{1}', CMDBuildUI.util.Config.baseUrl, method === 'PUT' ? '/gisnavigation?treeMode=tree' : ''),
                method: method,
                jsonData: data,
                success: function (transport) {
                    vm.setFormMode(CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    me.redirectTo('administration/gis/gisnavigation', true);
                },
                callback: function (reason) {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                }
            });
        }
    },

    /**
     * 
     * @param {Ext.data.Model} node the node changed
     * @param {Boolean} checked the value of the checked node
     */
    onCheckChange: function (node, checked) {
        this.recursiveParentCheck(node, checked);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theNavigationtree = vm.get('theNavigationtree');
        theNavigationtree.set('active', !theNavigationtree.get('active'));

        theNavigationtree.save({
            success: function (record, operation) {

            },
            failure: function (record, reason) {
                record.reject();
            }
        });

    },
    /**
     * privates
     */
    privates: {

        collectAllCheckedNodes: function (node) {
            var me = this;
            var nodes = [];

            Ext.Array.forEach(node.childNodes, function (child) {
                if (child.get('checked')) {
                    var _node = {
                        filter: child.get('filter'),
                        direction: child.get('direction'),
                        domain: child.get('domain'),
                        showOnlyOne: child.get('showOnlyOne'),
                        recursionEnabled: child.get('recursionEnabled'),
                        targetClass: child.get('targetClass'),
                        nodes: me.collectAllCheckedNodes(child)
                    };
                    nodes.push(_node);
                }
            });
            return nodes;
        }
    }
});
Ext.define('CMDBuildUI.view.administration.content.tasks.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    mixins: ['CMDBuildUI.view.administration.content.tasks.card.CardMixin'],
    requires: ['CMDBuildUI.util.administration.helper.ConfigHelper'],
    alias: 'controller.administration-content-tasks-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.tasks.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var me = this;
        var selected = view._rowContext.record;        
        vm.linkTo('theTask', {
            type: view.lookupViewModel().get('taskModelName'),
            id: selected.get('_id')
        });

        vm.bind({
            bindTo: {
                type: '{type}',
                theTask: '{theTask}'
            }
        }, function (data) {
            if (data.theTask) {
                me.generateCardFor(data.type, data, view);
                Ext.asap(function () {
                    try {                        
                        view.setHidden(false);
                        view.setActiveTab(0);
                        view.up().unmask();
                    } catch (error) {

                    }
                }, this);

            }
        });
        //  vm.set('theTask', selected);
    },

    onAfterRender: function (view) {

        var selected = view._rowContext.record;
        var type = selected.get('type');
        
    },

    onImportExportTemplateUpdate: function (v, record) {
        new Ext.util.DelayedTask(function () { }).delay(
            150,
            function (v, record) {
                var vm = this.getViewModel();
                var view = this.getView();
                this.linkImportExportTemplate(view, vm);
            },
            this,
            arguments);
    },

    linkImportExportTemplate: function (view, vm) {
        var grid = view.up(),
            record = grid.getSelection()[0];

        vm.set("theTask", record);
    }
});
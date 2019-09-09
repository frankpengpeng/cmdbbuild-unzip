Ext.define('CMDBuildUI.view.administration.content.localizations.localization.tabitems.TranslationsMenuTreePanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-localizations-localization-tabitems-translationsmenutreepanel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onBeforeRender: function (view, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
    },

    onStoreLoad: function (store, records) {
        var me = this;
        var emptyTreeStore = this.getView().getStore();
        var view = me.getView();
        var vm = me.getViewModel();
        var menuTreeStore = vm.getStore('completeTranslationsStore');

        menuTreeStore.getProxy().setUrl('/menu?detailed=true');
        emptyTreeStore.getRoot().removeAll();
        menuTreeStore.load({
            callback: function () {
                me.generateMenu(view, emptyTreeStore, menuTreeStore);
            }
        });
    },

    generateMenu: function (view, emptyTreeStore) {
        var me = this;
        var vm = view.lookupViewModel();
        var menuTreeStore = vm.getStore('completeTranslationsStore');
        var menuTrees = menuTreeStore.getRange();
        var languages = this.getViewModel().get('languages');
        var languageRecords = languages.getRange();

        menuTrees.forEach(function (menuTree) {
            var resultArray = me.getRawTree(menuTree.getData());
            emptyTreeStore.getRoot().appendChild(resultArray);
        });
        var columns = [{
            xtype: 'treecolumn',
            dataIndex: 'text',
            text: CMDBuildUI.locales.Locales.administration.localizations.treemenu,
            width: '25%'
        }, {
            text: CMDBuildUI.locales.Locales.administration.localizations.defaulttranslation,
            dataIndex: 'objectDescription',
            align: 'left'
        }];
        languageRecords.forEach(function (record) {
            var lang = record.get('description');
            var code = record.get('code');
            var flag = '<img width="20px" style="vertical-align:middle;margin-right:5px" src="resources/images/flags/' + code + '.png" />';
            columns.push({
                text: flag + lang,
                dataIndex: code,
                align: 'left',
                locked: false
            });
        });

        view.reconfigure(null, columns);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
    },

    buildTranslationsRow: function (translationsQuery, _item, languages) {
        var rows = translationsQuery.getRange();
        rows.forEach(function (row) {
            var lang = row.get('lang');
            if (languages.includes(lang)) {
                _item[lang] = row.get('value');
                _item.key = row.get('code');
            }
        });
    },

    getRawTree: function (childNodes) {
        var me = this;
        var output = [];
        var translationsStore = this.getViewModel().get('translations');
        var languages = this.getViewModel().get('languages').getRange();
        var languageRecords = [];
        languages.forEach(function (language) {
            languageRecords.push(language.get('code'));
        });
        if (childNodes.group) {
            var _item = {
                children: this.getRawTree(childNodes.children),
                menuType: 'folder',
                objectDescription: childNodes.group,
                _id: childNodes._id,
                expanded: true

            };
            output.push(_item);
        } else {
            childNodes.forEach(function (item, index) {
                var _item = {
                    children: [],
                    menuType: item.menuType,
                    objectDescription: item.objectDescription,
                    _id: item._id,
                    expanded: true
                };

                if (item.children.length) {
                    _item.children = me.getRawTree(item.children);
                } else {
                    _item.children = undefined;
                    _item.leaf = true;
                }
                var row = {};
                row.element = _item._id;
                row.type = CMDBuildUI.locales.Locales.administration.localizations.menuitem;
                var key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfMenuItemDescription(_item._id);
                var translationsQuery = translationsStore.query('code', key, false, false, true);
                if (translationsQuery.length) {
                    me.buildTranslationsRow(translationsQuery, _item, languageRecords);
                }
                output.push(_item);
            });
        }
        return output;
    },

    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        vm.set('actions.view', true);
        this.getView().getColumns().forEach(function (column) {
            if (!column.locked) {
                column.setEditor(false);
            }
        });
        vm.set('actions.view', true);
        vm.set('actions.edit', false);
        vm.getParent().toggleEnableTabs();
    },

    editedCell: function (editor, context, eOpts) {
        var me = this;
        var field = context.field;
        var modvalue = context.value;
        var store = me.getViewModel().get('translations');
        var key = context.record.get('key') || context.record.get('_id');

        var res = store.queryBy(function (record, id) {
            if (record.get('code') == key && record.get('lang') == field) {
                return true;
            }
        });

        if (res.length) {
            res.getRange()[0].set('value', modvalue);
        } else {
            var newTranslation = Ext.create('CMDBuildUI.model.localizations.Localization');
            newTranslation.set('code', key);
            newTranslation.set('lang', field);
            newTranslation.set('value', modvalue);
            store.add([newTranslation]);
        }
    },

    onSaveBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        vm.set('actions.view', true);
        vm.set('actions.edit', false);
        vm.getParent().toggleEnableTabs();
        this.getView().getColumns().forEach(function (column) {
            if (!column.locked) {
                column.setEditor(false);
            }
        });

        var modifiedRecords = vm.get('translations').getModifiedRecords();

        modifiedRecords.forEach(function (record) {
            var data = {};
            var lang = record.get('lang');
            var value = record.get('value');
            var code = record.get('code').startsWith('menuitem') ? record.get('code') : CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfMenuItemDescription(record.get('code'));

            data[lang] = value;
            Ext.Ajax.request({
                url: Ext.String.format('{0}/translations/{1}', CMDBuildUI.util.Config.baseUrl, code),
                method: 'PUT',
                jsonData: data
            });
        });
    }

});
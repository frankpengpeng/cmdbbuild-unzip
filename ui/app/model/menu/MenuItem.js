Ext.define('CMDBuildUI.model.menu.MenuItem', {
    extend: 'Ext.data.Model',

    requires: [
        'CMDBuildUI.proxy.MenuProxy'
    ],

    statics: {
        types: {
            folder: 'folder',
            klass: 'class',
            klassparent: 'classparent',
            process: 'processclass',
            processparent: 'processclassparent',
            view: 'view',
            report: 'report',
            reportpdf: 'reportpdf',
            reportodt: 'reportodt',
            reportrtf: 'reportrtf',
            reportcsv: 'reportcsv',
            dashboard: 'dashboard',
            custompage: 'custompage',
            searchfilter: 'filter'
        },
        icons: {
            folder: 'x-fa fa-folder',
            klass: 'x-fa fa-file-text-o',
            klassparent: 'cmdbuildicon-classparent',
            process: 'x-fa fa-cog',
            processparent: 'cmdbuildicon-processparent',
            view: 'x-fa fa-table',
            report: 'x-fa fa-file-o',
            reportpdf: 'x-fa fa-file-pdf-o',
            reportodt: 'x-fa fa-file-word-o',
            reportrtf: 'x-fa fa-file-o',
            reportcsv: 'x-fa fa-file-excel-o',
            dashboard: 'x-fa fa-pie-chart',
            custompage: 'x-fa fa-code'
        }
    },

    fields: [{
        name: 'menutype',
        type: 'string',
        mapping: 'menuType',
        persist: true,
        critical: true
    }, {
        name: 'index',
        type: 'integer',
        persist: true,
        critical: true
    }, {
        name: 'objecttypename',
        type: 'string',
        mapping: 'objectTypeName',
        persist: true,
        critical: true
    }, {
        name: 'objectdescription',
        type: 'string',
        mapping: 'objectDescription',
        persist: true,
        critical: true
    }, {
        name: 'objectdescription_translation',
        type: 'string',
        mapping: '_objectDescription_translation'
    }, {
        name: 'text',
        type: 'string',
        calculate: function (data) {
            return data.objectdescription_translation || data.objectdescription;
        }
    }, {
        name: 'leaf',
        type: 'boolean',
        persist: true,
        critical: true
    }, {
        name: 'iconCls',
        type: 'string',
        persist: false,
        calculate: function (data) {
            switch (data.menutype) {
                case CMDBuildUI.model.menu.MenuItem.types.folder:
                    return CMDBuildUI.model.menu.MenuItem.icons.folder;
                case CMDBuildUI.model.menu.MenuItem.types.klass:
                    var cls = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.objecttypename);
                    if (cls && cls.get("prototype")) {
                        return CMDBuildUI.model.menu.MenuItem.icons.klassparent;
                    }
                    return CMDBuildUI.model.menu.MenuItem.icons.klass;
                case CMDBuildUI.model.menu.MenuItem.types.process:
                    var proc = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(data.objecttypename);
                    if (proc && proc.get("prototype")) {
                        return CMDBuildUI.model.menu.MenuItem.icons.processparent;
                    }
                    return CMDBuildUI.model.menu.MenuItem.icons.process;
                case CMDBuildUI.model.menu.MenuItem.types.view:
                    return CMDBuildUI.model.menu.MenuItem.icons.view;
                case CMDBuildUI.model.menu.MenuItem.types.report:
                    return CMDBuildUI.model.menu.MenuItem.icons.report;
                case CMDBuildUI.model.menu.MenuItem.types.reportpdf:
                    return CMDBuildUI.model.menu.MenuItem.icons.reportpdf;
                case CMDBuildUI.model.menu.MenuItem.types.reportodt:
                    return CMDBuildUI.model.menu.MenuItem.icons.reportodt;
                case CMDBuildUI.model.menu.MenuItem.types.reportrtf:
                    return CMDBuildUI.model.menu.MenuItem.icons.reportrtf;
                case CMDBuildUI.model.menu.MenuItem.types.reportcsv:
                    return CMDBuildUI.model.menu.MenuItem.icons.reportcsv;
                case CMDBuildUI.model.menu.MenuItem.types.dashboard:
                    return CMDBuildUI.model.menu.MenuItem.icons.dashboard;
                case CMDBuildUI.model.menu.MenuItem.types.custompage:
                    return CMDBuildUI.model.menu.MenuItem.icons.custompage;
            }
        }
    }, {
        name: 'children',
        type: 'auto',
        persist: true,
        critical: true
    }],

    proxy: {
        url: '/sessions/current/',
        type: 'menuproxy'
    }
});
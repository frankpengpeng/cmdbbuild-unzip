Ext.define('CMDBuildUI.mixins.grids.AddButtonMixin', {
    mixinId: 'grids-addbutton-mixin',

    /**
     * @property {String} typeicon
     * 
     * The icon to use in add button menu.
     */
    typeicon: null,

    /**
     * Updates add button by adding handler 
     * or menu with addable sub-types.
     * 
     * @param {Ext.button.Button} button
     * @param {String} handler
     * @param {String} objectTypeName
     */
    updateAddButton: function (button, handler, objectTypeName, objectType) {
        var me = this;
        var object = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);

        // default value 
        var hasChildrens = true;

        // disable add button if class is undefined
        if (!object) {
            button.setDisabled(true);
            return;
        }

        if (object.get("prototype")) {
            var menu = [];
            var childrens = object.getChildren(true);

            if (!childrens.length) {
                hasChildrens = false;
            }

            // create menu definition by adding non-prototype classes
            childrens.forEach(function (child) {
                menu.push({
                    text: child.getTranslatedDescription(),
                    iconCls: me.typeicon,
                    disabled: !child.get(CMDBuildUI.model.base.Base.permissions.add),
                    listeners: {
                        click: handler
                    },
                    objectTypeName: child.get("name")
                });
            });

            // sort menu by description
            Ext.Array.sort(menu, function (a, b) {
                return a.text === b.text ? 0 : (a.text < b.text ? -1 : 1);
            });

            // add menu to button
            button.setMenu(menu);
        } else {
            button.objectTypeName = objectTypeName;
            button.addListener("click", handler, this);
        }

        // enable button if can add
        var hasPermissions = object.get(CMDBuildUI.model.base.Base.permissions.add);
        button.setDisabled(!hasPermissions || !hasChildrens);
    }
});
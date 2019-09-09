Ext.define('CMDBuildUI.view.fields.reference.SelectionPopupModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.fields-reference-selectionpopup',

    data: {
        searchvalue: null,
        addbtn: {
            text: null,
            disabled: false,
            hidden: true
        }
    },

    formulas: {
        /**
         * Return add card button text.
         */
        updateAddBtnInfo: {
            bind: {
                objectTypeDescription: '{objectTypeDescription}'
            },
            get: function (data) {
                this.set("addbtn.text", Ext.String.format(
                    "{0} {1}",
                    CMDBuildUI.locales.Locales.classes.cards.addcard,
                    data.objectTypeDescription
                ));

                this.set("addbtn.hidden", this.get("objectType") !== CMDBuildUI.util.helper.ModelHelper.objecttypes.klass);
            }
        },

        /**
         * Update store info
         */
        updateStoreInfo: {
            get: function () {
                // store type
                var type, object;
                switch (this.get("objectType")) {
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                        type = 'classes-cards';
                        object = CMDBuildUI.util.helper.ModelHelper.getClassFromName(this.get("objectTypeName"));
                        break;
                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                        type = 'processes-instances';
                        object = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(this.get("objectTypeName"));
                        break;
                }
                this.set("storeinfo.type", type);

                // add ecql filter
                var advancedfilter = null;
                if (this.get("storeinfo.ecqlfilter") && !Ext.Object.isEmpty(this.get("storeinfo.ecqlfilter"))) {
                    advancedfilter = {
                        ecql: this.get("storeinfo.ecqlfilter")
                    };
                }
                this.set("storeinfo.advancedfilter", advancedfilter);

                // sorters
                var sorters = [];
                if (object && object.defaultOrder().getCount()) {
                    object.defaultOrder().getRange().forEach(function (o) {
                        sorters.push({
                            property: o.get("attribute"),
                            direction: o.get("direction") === "descending" ? "DESC" : 'ASC'
                        });
                    });
                } else {
                    sorters.push({
                        property: 'Description'
                    });
                }
                this.set("storeinfo.sorters", sorters);
            }
        },

        /**
         * Save button disabled property.
         * Bindend on selection.
         */
        saveBtnDisabled: {
            bind: '{selection}',
            get: function (selection) {
                return !selection;
            }
        }
    },

    stores: {
        records: {
            type: '{storeinfo.type}',
            model: '{storeinfo.modelname}',
            proxy: {
                type: 'baseproxy',
                url: '{storeinfo.proxyurl}'
            },
            advancedFilter: '{storeinfo.advancedfilter}',
            sorters: '{storeinfo.sorters}',
            autoLoad: '{storeinfo.autoload}'
        }
    }

});

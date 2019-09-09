Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Foreignkey', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-foreignkeyfields',

    items: [{
        xtype: 'fieldcontainer',
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'combo',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                name: 'initialPage',
                valueField: '_id',
                displayField: 'label',
                queryMode: 'local',
                typeAhead: true,
                bind: {
                    value: '{theAttribute.targetClass}',
                    store: '{getAllPagesStore}',
                    disabled: '{actions.edit}'
                },
                triggers: {
                    foo: {
                        cls: 'x-form-clear-trigger',
                        handler: function () {
                            this.clearValue();
                        }
                    }
                },
                tpl: new Ext.XTemplate(
                    '<tpl for=".">',
                    '<tpl for="group" if="this.shouldShowHeader(group)"><div class="group-header">{[this.showHeader(values.group)]}</div></tpl>',
                    '<div class="x-boundlist-item">{label}</div>',
                    '</tpl>', {
                        shouldShowHeader: function (group) {
                            return this.currentGroup !== group;
                        },
                        showHeader: function (group) {
                            this.currentGroup = group;
                            return group;
                        }
                    })
            }]

        }]
    }, {
        xtype: 'fieldcontainer',
        bind: {
            hidden: '{!actions.view}'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                bind: {
                    value: '{theAttribute.targetClassDescription}'
                }
            }]

        }]
    }]
});
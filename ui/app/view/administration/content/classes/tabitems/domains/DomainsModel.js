Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.domains.DomainsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-classes-tabitems-domains-domains',
    data: {
        name: 'CMDBuildUI',
        toolbarHiddenButtons: {
            edit: false
        },
        actions: {
            view: true,
            edit: false
        },
        searchdomain:{
            value: null
        }
    },

    setFormMode: function (_mode) {
        var mode = _mode.toLowerCase();
        switch (mode) {
            case CMDBuildUI.util.administration.helper.FormHelper.formActions.edit:
                this.set('actions.view', false);
                this.set('actions.edit', true);
                this.set('toolbarHiddenButtons.edit', true);
                break;

            default:
                this.set('actions.view', true);
                this.set('actions.edit', false);
                this.set('toolbarHiddenButtons.edit', false);
                break;
        }
    }
});
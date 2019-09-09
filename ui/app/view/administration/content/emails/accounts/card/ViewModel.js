Ext.define('CMDBuildUI.view.administration.content.emails.accounts.card.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-accounts-card-view',
    data: {
        name: 'CMDBuildUI'
    },

    formulas: {
        updateDisplayPassword: {
            bind: {
                password: '{theAccount.password}'
            },
            get: function (data) {
                var hiddenPassword = CMDBuildUI.util.administration.helper.RendererHelper.getDisplayPassword(data.password);
                this.set('hiddenPassword', hiddenPassword);
            }
        }
    }

});

Ext.define('CMDBuildUI.view.main.footer.Container',{
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.main.footer.ContainerController',
        'CMDBuildUI.view.main.footer.ContainerModel'
    ],

    xtype: 'main-footer-container',
    controller: 'main-footer-container',
    viewModel: {
        type: 'main-footer-container'
    },

    dock: 'bottom',
    padding: '5px 10px',
    cls: 'main-footer',
    layout: 'hbox',

    // add data-testid attribute to element
    autoEl: {
        'data-testid' : 'main-footer-container'
    },

    style: {
        textAlign: 'center'
    },

    items: [{
        xtype: 'component',
        flex: 1
    },{
        xtype: 'component',
        html: '<a href="http://www.openmaint.org" target="_blank">www.openmaint.org</a>',
        width: 200,
        style: {
            textAlign: 'right'
        }
    },{
        xtype: 'component',
        html: '&middot',
        width: 40
    },{
        xtype: 'component',
        html: 'Info'
    },{
        xtype: 'component',
        html: '&middot',
        width: 40
    },{
        xtype: 'component',
        html: '<a href="http://www.tecnoteca.com" target="_blank">Copyright &copy; Tecnoteca srl</a>',
        width: 200,
        style: {
            textAlign: 'left'
        }
    },{
        xtype: 'component',
        flex: 1
    }]
});

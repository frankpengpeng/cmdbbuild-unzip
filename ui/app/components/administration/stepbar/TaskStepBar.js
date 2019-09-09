Ext.define("CMDBuildUI.components.administration.stepbar.TaskStepBar", {
    extend: 'Ext.view.View',
    xtype: 'administration-taskstepbar',

    config: {
        /**
         * @cfg {Numeric} currentStep 
         * The current step.
         */
        currentStep: null,

        /**
         * @cfg {Numeric} maxAdvanceable 
         * The max index clickable
         */
        maxAdvanceable: null
    },

    // make lookupvalue bindable
    publishes: ['currentStep', 'maxAdvanceable'],

    baseCls: Ext.baseCSSPrefix + 'administration-taskstepbar',
    ui: 'administration',
    /*
    <ol class="steps">
    <li class="steps__item  steps__item--done  steps__item--first"><a href="#" class="steps__link">Cart</a></li>
    <li class="steps__item  steps__item--done"><a href="#" class="steps__link">Authentication</a></li>
    <li class="steps__item  steps__item--active"><a href="#" class="steps__link">Delivery</a></li>
    <li class="steps__item" disabled="disabled"><span class="steps__link">Summary</span></li>
    <li class="steps__item  steps__item--last" disabled="disabled"><span class="steps__link">Payment</span></li>
  </ol>
  */
    tpl: new Ext.XTemplate(
        '<ol class="steps">',
        '<tpl for=".">',
        '<li class="steps_item {[xindex === 1 ? " steps_item_first" : ""]} {[xindex === xcount -1 ? " steps_item_last" : ""]} "><span class="steps_link">{description}</span></li>',
        '</tpl>',
        '</ol>', {
            addListener: function (id) {                
                Ext.get(id).on('click', function (e) {
                    e.stopEvent();
                   //  alert('link ' + id + ' clicked');
                });
            }
        }
    ),
    // itemTpl: '<span class="steps_item {[done ? " steps_item_done" : ""]} {[xindex === 1 ? " steps_item_first" : ""]} {[xindex === xcount -1 ? " steps_item_last" : ""]} "> {description} <span class="x-fa fa-dot" style=""></span></span>',
    // itemTpl: '<span class="steps_item {[xindex === 1 ? " steps_item_first" : ""]} {[xindex === xcount ? " steps_item_last" : ""]} "> {description} {xcount} <span class="x-fa fa-dot" style=""></span></span>',

    // itemSelector: '.' + Ext.baseCSSPrefix + 'steps_item',
    // itemCls: Ext.baseCSSPrefix + 'statuses-progress-bar-item',
    scrollable: false,

    // disable load mask
    loadMask: false,

    // disable selection
    selectionModel: false,
    itemSelector: 'li',
    store: {
        // model: 'CMDBuildUI.model.lookups.Lookup',
        fields: ['description', 'done', 'id'],
        proxy: {
            type: 'memory'
        },
        data: [{
            description: 'One',
            done: true,
            id: 1
        }, {
            description: 'two',
            done: true,
            id: 2
        }, {
            description: 'tree',
            done: true,
            id: 3
        }, {
            description: 'four',
            done: false,
            id: 4
        }, {
            description: 'five',
            done: false,
            id: 5
        }],
        autoLoad: true
    },
    /**
     * Custom init component
     */
    initComponent: function () {
        //debugger;
        // add store to progress bar
        Ext.apply(this, {

        });

        this.callParent(arguments);
    },

    privates: {

    },
    listeners: {
        itemclick: function (view, record, item, index, e, eOpts) {            
            var clickedEl = Ext.get(e.target);

            // console.log(clickedEl.hasCls('add'));
        }
    }

});
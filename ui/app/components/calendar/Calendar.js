Ext.define("CMDBuildUI.components.calendar.Calendar", {
    extend: 'Ext.panel.Panel',
    alias: 'widget.ux-calendar',

    config: {
        /**
         * @cfg {Boolean} allowViewDay
         * Allow daily view. Default `true`.
         */
        allowViewDay: true,

        /**
         * @cfg {Boolean} allowViewWeek
         * Allow weekly view. Default `true`.
         */
        allowViewWeek: true,

        /**
         * @cfg {Boolean} allowViewMonth
         * Allow monthly view. Default `true`.
         */
        allowViewMonth: true,

        /**
         * @cfg {Boolean} allowViewAgenda
         * Allow monthly view. Default `true`.
         */
        allowViewAgenda: true,

        /**
         * @cfg {Boolean} allowPrevNextYear
         * Allow buttons to navigate to next or previous year. Default `true`.
         */
        allowPrevNextYear: true,

        /**
         * @cfg {String} defaultView
         * One of `day`, `week`, `month`, `agenda`. Default is `month`.
         */
        defaultView: 'month',

        /**
         * @cfg {String} dataSourceType
         */
        dataSourceType: null,

        /**
         * @cfg {String} dataSourceTypeName
         */
        dataSourceTypeName: null,

        /**
         * @cfg {Object} dataSourceFilter 
         * eCQL filter
         */
        dataSourceFilter: null,

        /**
         * @cfg {CMDBuild.model.Base} targetObject
         */
        targetObject: null,

        /**
         * @cfg {String} eventStartDateAttribute
         */
        eventStartDateAttribute: null,

        /**
         * @cfg {String} eventEndDateAttribute
         */
        eventEndDateAttribute: null,

        /**
         * @cfg {String} eventTitleAttribute
         * Default is `Description`.
         */
        eventTitleAttribute: "Description",

        /**
         * @cfg {String} eventTypeLookup
         * The LookUp type to use for type. 
         * The lookup icon color is used as event color. If not specifed the default is the primary color.
         * The lookup text color is used as event text color. If not specifed the default is white.
         */
        eventTypeLookup: null,

        /**
         * @cfg {String} eventTypeLookup
         */
        eventTypeAttribute: null,

        /**
         * @cfg {String|Date} openingDate
         */
        openingDate: null
    },

    layout: 'fit',
    bodyPadding: 10,
    scroller: true,

    baseCls: Ext.baseCSSPrefix + 'ux-calendar',

    onBoxReady: function (width, height) {
        var me = this;
        this.callParent(arguments);

        this._currentstate = {
            start: null,
            end: null,
            titleattr: this.getEventTitleAttribute(),
            startdateattr: me.getEventStartDateAttribute(),
            enddateattr: me.getEventEndDateAttribute() || me.getEventStartDateAttribute(),
            typeattr: me.getEventTypeAttribute()
        };

        // generate data store
        Ext.Loader.loadScript({
            url: "resources/js/fullcalendar/main.min.js",
            onLoad: function() {
                Ext.Promise.all([
                    me.initStore(),
                    me.initCategories()
                ]).then(function () {
                    me.initCalendar();
                });
            }
        });
    },

    /**
     * Initialize calendar
     */
    initCalendar: function () {
        var me = this;
        var rightbtns = [];
        var leftbtns = "prev,today,next";
        var startdate = this.getOpeningDate() || new Date();
        var datasourcetype = this.getDataSourceType();

        // configure right buttons
        if (this.getAllowViewMonth()) {
            rightbtns.push("dayGridMonth");
        }
        if (this.getAllowViewWeek()) {
            rightbtns.push("timeGridWeek");
        }
        if (this.getAllowViewDay()) {
            rightbtns.push("timeGridDay");
        }
        if (this.getAllowViewAgenda()) {
            rightbtns.push("listWeek");
        }

        // configure left buttons
        if (this.getAllowPrevNextYear()) {
            leftbtns = 'prevYear,' + leftbtns + ',nextYear';
        }

        var calendarEl = Ext.dom.Helper.append(
            this.body,
            '<div></div>'
        );

        this.calendar = new FullCalendar.Calendar(calendarEl, {
            plugins: ['dayGrid', 'timeGrid', 'list', 'interaction'],
            header: {
                left: leftbtns,
                center: 'title',
                right: rightbtns.join(",")
            },
            height: this.body.getHeight() - (this.bodyPadding * 2),
            defaultDate: startdate,
            navLinks: false, // can click day/week names to navigate views
            editable: false,
            eventLimit: true, // allow "more" link when too many events
            locale: CMDBuildUI.util.helper.SessionHelper.getLanguage(),
            datesRender: function (info) {
                me.loadEvents(info.view.activeStart, info.view.activeEnd);
            },

            eventClick: function(eventClickInfo) {
                var config = {
                    viewModel: {
                        data: {
                            objectTypeName: eventClickInfo.event.extendedProps._objecttypename,
                            objectId: eventClickInfo.event.id
                        }
                    },
                    shownInPopup: true,
                    tabpaneltools: [],
                    padding: 10
                };
                 if (datasourcetype === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass) {
                    config.xtype = 'classes-cards-card-view';
                } else if (datasourcetype === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                    config.xtype = 'processes-instances-instance-view';
                }
                var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(eventClickInfo.event.extendedProps._objecttypename, datasourcetype);
                var title = Ext.String.format("{0} &mdash; {1}", item.getTranslatedDescription(), eventClickInfo.event.title);
                CMDBuildUI.util.Utilities.openPopup(null, title, config);
            }
        });

        this.calendar.render();
    },

    privates: {
        /**
         * 
         * @param {Date} start 
         * @param {Date} end 
         */
        loadEvents: function (start, end) {
            var me = this;
            if (
                !me._currentstate.start || Ext.Date.diff(me._currentstate.start, start, Ext.Date.DAY) < 0 &&
                !me._currentstate.end || Ext.Date.diff(me._currentstate.end, end, Ext.Date.DAY) > 0
            ) {
                me._currentstate.start = start;
                me._currentstate.end = end;

                // remove events
                me.removeAllEvents();

                // set store filter
                var advancedFilter = me._currentstate.store.getAdvancedFilter();
                advancedFilter.addAttributeFilter(
                    me._currentstate.startdateattr,
                    CMDBuildUI.model.base.Filter.operators.less,
                    end.toISOString()
                );
                advancedFilter.addAttributeFilter(
                    me._currentstate.enddateattr,
                    CMDBuildUI.model.base.Filter.operators.greater,
                    start.toISOString()
                );

                // load store
                me._currentstate.store.load({
                    callback: function (records, operation, success) {
                        records.forEach(function (record) {
                            me.addEvent(record);
                        });
                    }
                });
            }
        },

        /**
         * 
         */
        removeAllEvents: function () {
            this.calendar.getEvents().forEach(function (event) {
                event.remove();
            });
        },

        /**
         * 
         * @param {CMDBuild.model.classes.Card|CMDBuild.model.processes.Instance} record 
         */
        addEvent: function(record) {
            var event = {
                id: record.getId(),
                start: record.get(this._currentstate.startdateattr),
                end: record.get(this._currentstate.enddateattr),
                title: record.get(this._currentstate.titleattr),
                classNames: ['event-cursor-pointer'],
                _objecttypename: record.get("_type")
            };

            if (this._currentstate.eventtypes && record.get(this._currentstate.typeattr)) {
                var lv = this._currentstate.eventtypes.getById(record.get(this._currentstate.typeattr));
                if (lv) {
                    if (lv.get("icon_color")) {
                        event.backgroundColor = lv.get("icon_color");
                        event.borderColor = lv.get("icon_color");
                    }
                    if (lv.get("text_color")) {
                        event.textColor = lv.get("text_color");
                    }
                }
            }
            this.calendar.addEvent(event);
        },

        /**
         * Load classes store.
         * 
         * @return {Ext.promise.Promise}
         */
        initStore: function () {
            var deferred = new Ext.Deferred();
            var me = this;
            if (this.getDataSourceType() && this.getDataSourceTypeName() && this.getEventStartDateAttribute()) {
                CMDBuildUI.util.helper.ModelHelper.getModel(this.getDataSourceType(), this.getDataSourceTypeName()).then(function (model) {
                    var filter;
                    if (me.getDataSourceFilter() && me.getTargetObject()) {
                        filter = {
                            baseFilter: {
                                ecql: CMDBuildUI.util.ecql.Resolver.resolve(me.getDataSourceFilter(), me.getTargetObject())
                            }
                        };
                    }
                    // create store
                    me._currentstate.store = Ext.create("Ext.data.Store", {
                        model: model.getName(),
                        autoDestroy: true,
                        autoLoad: false,
                        advancedFilter: filter,
                        remoteSort: true,
                        sorters: [me.getEventStartDateAttribute()],
                        pageSize: 0
                    });
                    deferred.resolve();
                });
            } else {
                deferred.resolve();
            }

            return deferred.promise;
        },

        /**
         * Load classes store.
         * 
         * @return {Ext.promise.Promise}
         */
        initCategories: function () {
            var deferred = new Ext.Deferred();
            var me = this;
            if (me.getEventTypeAttribute() && me.getEventTypeLookup()) {
                var lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(me.getEventTypeLookup());
                if (lt) {
                    lt.getLookupValues().then(function (values) {
                        me._currentstate.eventtypes = values;
                        deferred.resolve();
                    });
                } else {
                    deferred.resolve();
                }
            } else {
                deferred.resolve();
            }

            return deferred.promise;
        }
    }
});
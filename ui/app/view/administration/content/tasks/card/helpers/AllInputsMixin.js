Ext.define('CMDBuildUI.view.administration.content.tasks.card.helpers.AllInputsMixin', {
    mixinId: 'administration-task-allinputs',

    requires: ['CMDBuildUI.util.administration.helper.FormHelper'],
    setAllowBlank: function (field, value, form) {
        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, value, form);
    },
    privates: {
        getRowFieldContainer: function (items, config) {
            var fieldcontainer = Ext.merge({}, {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                items: items
            }, config || {});

            fieldcontainer.items = items;

            return fieldcontainer;
        },
        getNameInput: function (vmKeyObject, attribute) {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput({
                name: {
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    }
                }
            }, true, '[name="description"]');
        },

        getDescriptionInput: function (vmKeyObject, attribute) {
            return CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                description: {
                    bind: {
                        value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                    }
                }
            });
        },

        getTypeInput: function (vmKeyObject, attribute, disabled) {
            var config = {};
            var me = this;
            var taskType = me.getView().lookupViewModel().get('taskType');
            var store = CMDBuildUI.model.tasks.Task.types;
            if (store.isFiltered) {
                store.clearFilter();
            }
            store.filterBy(function (item) {
                return item.get('group') === taskType;
            });
            config[attribute] = {
                fieldcontainer: {
                    bind: {
                        hidden: '{isTypeFieldHidden}'
                    }
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.type,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
                },
                name: attribute,
                allowBlank: false,
                disabled: disabled,
                store: CMDBuildUI.model.tasks.Task.types,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {

                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();

                        if (vm.get(vmKeyObject)._config && vm.get(vmKeyObject)._config.get('type') !== newValue) {
                            vm.get(vmKeyObject)._config.set('type', newValue);
                        }
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(attribute, config);
        },

        getTemplateInput: function (vmKeyObject, attribute) {
            var config = {};
            config.template = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.template,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.template'
                },
                name: attribute,
                allowBlank: false,
                displayField: "description",
                valueField: "code",

                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{allImportExportTemplate}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config && vm.get(vmKeyObject)._config.get('template') !== newValue) {
                            vm.get(vmKeyObject)._config.set('template', newValue);
                        }

                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('template', config);
        },

        getSourceInput: function (vmKeyObject, attribute) {
            var config = {};
            config.source = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.source,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.source'
                },
                allowBlank: false,
                name: attribute,
                store: CMDBuildUI.model.tasks.TaskImportExportConfig.sourceTypes,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('source') !== newValue) {
                            vm.get(vmKeyObject)._config.set('source', newValue);
                        }

                    },
                    validator: function (field) {
                        var form = this.up('form');
                        var dependes = form.down('[name="type"]');
                        if (dependes.getValue() === 'import_file') {
                            return this.getValue().length;
                        }
                        return true;
                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('source', config);
        },

        getDirectoryInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge({}, {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.directory,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.directory'
                },
                allowBlank: false,
                name: attribute,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(_attribute, newValue);
                        }

                    }
                }
            }, config || {});
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(_attribute, _config);
        },

        getFilenameInput: function (vmKeyObject, attribute) {
            var config = {};
            config.filename = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.filename,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.filename'
                },
                allowBlank: false,
                name: attribute,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('fileName') !== newValue) {
                            vm.get(vmKeyObject)._config.set('fileName', newValue);
                        }

                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('filename', config);
        },

        getFilepatternInput: function (vmKeyObject, attribute) {
            var config = {};
            config.filename = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.filepattern,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.filepattern'
                },
                allowBlank: false,
                name: attribute,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('filePattern') !== newValue) {
                            vm.get(vmKeyObject)._config.set('filePattern', newValue);
                        }

                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('filename', config);
        },

        getUrlInput: function (vmKeyObject, attribute) {
            var config = {};
            config.url = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.url,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.url'
                },
                allowBlank: false,
                name: attribute,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('url') !== newValue) {
                            vm.get(vmKeyObject)._config.set('url', newValue);
                        }

                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('url', config);
        },

        getBasicCronInput: function (vmKeyObject, attribute) {
            var config = {};
            config.basiccron = {

                allowBlank: false,
                name: attribute,
                store: CMDBuildUI.model.tasks.Task.cronSettings,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('cronExpression') !== newValue) {
                            vm.get(vmKeyObject)._config.set('cronExpression', newValue);
                        }

                    }
                }
            };


            var fieldcontainer = Ext.merge({}, {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: config.basiccron.columnWidth || 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.cron,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.cron'
                },
                items: [],
                itemId: Ext.String.format('{0}_fieldcontainer', 'basiccron'),
                allowBlank: config.basiccron.allowBlank
            }, config.basiccron.fieldcontainer || {});

            delete config.basiccron.fieldcontainer;
            delete config.basiccron.fieldLabel;
            var combo = CMDBuildUI.util.administration.helper.FieldsHelper._getCombofield(config.basiccron, 'basiccron');
            combo.valueNotFoundText = CMDBuildUI.locales.Locales.administration.tasks.strings.advanced;
            fieldcontainer.items.push(combo);


            var displayfield = CMDBuildUI.util.administration.helper.FieldsHelper._getDisplayfield(config.basiccron);
            displayfield.renderer = function (value, input) {
                var store = CMDBuildUI.model.tasks.Task.cronSettings;
                if (store && value) {
                    var record = store.findRecord('value', value);
                    if (record) {
                        return record.get('label');
                    } else {
                        return CMDBuildUI.locales.Locales.administration.tasks.strings.advanced;
                    }
                }
                return value;
            };

            fieldcontainer.items.push(displayfield);

            return fieldcontainer;
        },

        getAdvancedCronInput: function (vmKeyObject, attribute) {

            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                style: {
                    marginTop: '15px'
                },
                // fieldLabel: 'Advanced cron',
                items: [],
                bind: {
                    hidden: '{!isAdvancedCron}'
                },
                itemId: Ext.String.format('{0}_fieldcontainer', 'advancedcron'),
                allowBlank: false
            };

            var minutes = {
                xtype: 'textfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.minutes,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.minutes'
                },
                itemId: Ext.String.format('{0}_field', 'advancedcron_minute'),
                name: 'advancedcron_minute',
                labelAlign: 'left',
                enableKeyEvents: true,
                allowBlank: false,
                bind: {
                    hidden: '{actions.view}',
                    value: '{advancedCronMinuteValue}'
                },
                regex: /^(\?|\*|(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?(?:,(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?)*)$/
                // isValid: function () {
                //     var isValid = this.lookupController().cronValidator(this);

                //     return isValid;
                // }                
            };
            var minutesDisplay = {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.minutes,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.minutes'
                },
                labelAlign: 'left',
                bind: {
                    hidden: '{!actions.view}',
                    value: '{advancedCronMinuteValue}'
                }
            };
            var hours = {
                xtype: 'textfield',
                style: {
                    paddingRight: '15px'
                },
                name: 'advancedcron_hour',
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.hour,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.hour'
                },
                itemId: Ext.String.format('{0}_field', 'advancedcron_hour'),                
                labelAlign: 'left',
                enableKeyEvents: true,
                allowBlank: false,
                bind: {
                    hidden: '{actions.view}',
                    value: '{advancedCronHourValue}'
                },
                regex: /^(\?|\*|(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?(?:,(?:[0-5]?\d)(?:(?:-|\/|\,)(?:[0-5]?\d))?)*)$/
                // isValid: function () {
                //     var isValid = this.lookupController().cronValidator(this);
                //     this.markInvalid(!isValid ? 'Cron invalid' : null, !isValid);
                //     return isValid;
                // }                
            };
            var hoursDisplay = {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.hour,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.hour'
                },
                labelAlign: 'left',
                bind: {
                    hidden: '{!actions.view}',
                    value: '{advancedCronHourValue}'
                }
            };
            var days = {
                xtype: 'textfield',
                style: {
                    paddingRight: '15px'
                },
                name: 'advancedcron_day',
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.day,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.day'
                },
                itemId: Ext.String.format('{0}_field', 'advancedcron_day'),                
                labelAlign: 'left',
                enableKeyEvents: true,
                allowBlank: false,
                bind: {
                    hidden: '{actions.view}',
                    value: '{advancedCronDayValue}'
                },
                regex: /^(\?|\*|(?:0?[1-9]|[12]\d|3[01])(?:(?:-|\/|\,)(?:0?[1-9]|[12]\d|3[01]))?(?:,(?:0?[1-9]|[12]\d|3[01])(?:(?:-|\/|\,)(?:0?[1-9]|[12]\d|3[01]))?)*)$/
                // isValid: function () {
                //     var isValid = this.lookupController().cronValidator(this);
                //     this.markInvalid(!isValid ? 'Cron invalid' : null, !isValid);
                //     return isValid;
                // }
            };
            var daysDisplay = {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.day,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.day'
                },
                labelAlign: 'left',
                bind: {
                    hidden: '{!actions.view}',
                    value: '{advancedCronDayValue}'
                }
            };
            var months = {
                xtype: 'textfield',
                style: {
                    paddingRight: '15px'
                },
                name: 'advancedcron_month',
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.month,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.month'
                },
                itemId: Ext.String.format('{0}_field', 'advancedcron_month'),                
                labelAlign: 'left',
                enableKeyEvents: true,
                allowBlank: false,
                bind: {
                    hidden: '{actions.view}',
                    value: '{advancedCronMonthValue}'
                },
                regex: /^(\?|\*|(?:[1-9]|1[012])(?:(?:-|\/|\,)(?:[1-9]|1[012]))?(?:L|W)?(?:,(?:[1-9]|1[012])(?:(?:-|\/|\,)(?:[1-9]|1[012]))?(?:L|W)?)*|\?|\*|(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?(?:,(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)(?:(?:-)(?:JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC))?)*)$/
                // isValid: function () {
                //     var isValid = this.lookupController().cronValidator(this);
                //     this.markInvalid(!isValid ? 'Cron invalid' : null, !isValid);
                //     return isValid;
                // }
            };
            var monthsDisplay = {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.month,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.month'
                },                
                labelAlign: 'left',
                bind: {
                    hidden: '{!actions.view}',
                    value: '{advancedCronMonthValue}'
                }
            };
            var daysofweek = {
                xtype: 'textfield',
                style: {
                    paddingRight: '15px'
                },
                name: 'advancedcron_dayofweek',
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.dayofweek,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.dayofweek'
                },
                itemId: Ext.String.format('{0}_field', 'advancedcron_dayofweek'),                
                labelAlign: 'left',
                enableKeyEvents: true,
                allowBlank: false,
                bind: {
                    hidden: '{actions.view}',
                    value: '{advancedCronDayofweekValue}'
                },
                regex: /^(\?|\*|(?:[0-6])(?:(?:-|\/|\,|#)(?:[0-6]))?(?:L)?(?:,(?:[0-6])(?:(?:-|\/|\,|#)(?:[0-6]))?(?:L)?)*|\?|\*|(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?(?:,(?:MON|TUE|WED|THU|FRI|SAT|SUN)(?:(?:-)(?:MON|TUE|WED|THU|FRI|SAT|SUN))?)*)(|\s)+(\?|\*|(?:|\d{4})(?:(?:-|\/|\,)(?:|\d{4}))?(?:,(?:|\d{4})(?:(?:-|\/|\,)(?:|\d{4}))?)*)$/
                // isValid: function () {
                //     var isValid = this.lookupController().cronValidator(this);
                //     this.markInvalid(!isValid ? 'Cron invalid' : null, !isValid);
                //     return isValid;
                // }
            };
            var daysofweekDisplay = {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.dayofweek,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.dayofweek'
                },
                labelAlign: 'left',
                bind: {
                    hidden: '{!actions.view}',
                    value: '{advancedCronDayofweekValue}'
                }
            };
            fieldcontainer.items.push(minutes);
            fieldcontainer.items.push(hours);
            fieldcontainer.items.push(days);
            fieldcontainer.items.push(months);
            fieldcontainer.items.push(daysofweek);
            fieldcontainer.items.push(minutesDisplay);
            fieldcontainer.items.push(hoursDisplay);
            fieldcontainer.items.push(daysDisplay);
            fieldcontainer.items.push(monthsDisplay);
            fieldcontainer.items.push(daysofweekDisplay);
            return fieldcontainer;
        },

        getErrorEmailTemplateInput: function (vmKeyObject, attribute) {
            var config = {};
            config.errorEmailTemplate = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.erroremailtemplate,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.erroremailtemplate'
                },
                allowBlank: false,
                name: attribute,
                displayField: 'description',
                valueField: 'name',
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{allEmailTemplates}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('errorEmailTemplate') !== newValue) {
                            vm.get(vmKeyObject)._config.set('errorEmailTemplate', newValue);
                        }

                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('errorEmailTemplate', config);
        },
        getNotificationInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            _config.notificationMode = Ext.merge({}, {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.notificationmode,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.notificationmode'
                },
                allowBlank: false,
                name: attribute,
                displayField: 'label',
                valueField: 'value',
                queryMode: 'local',
                bind: {
                    store: '{notificationModesStore}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('notificationMode') !== newValue) {
                            vm.get(vmKeyObject)._config.set('notificationMode', newValue);
                        }
                    }
                }
            }, config || {});
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('notificationMode', _config);
        },

        getErrorEmailAccountInput: function (vmKeyObject, attribute) {
            var config = {};
            config.errorEmailAccount = {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.account,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.account'
                },
                name: attribute,
                displayField: 'name',
                valueField: 'name',
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{allEmailAccounts}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('errorEmailAccount') !== newValue) {
                            vm.get(vmKeyObject)._config.set('errorEmailAccount', newValue);
                        }

                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('errorEmailAccount', config);
        },

        getEmailTemplateInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge({}, {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.emailtemplate,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.emailtemplate'
                },
                allowBlank: false,
                name: attribute,
                displayField: 'description',
                valueField: 'name',
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{allEmailTemplates}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(_attribute, newValue);
                        }

                    }
                }
            }, config || {});
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, _config);
        },

        getEmailAccountInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge({}, {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.fieldlabels.account,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.fieldlabels.account'
                },
                name: attribute,
                displayField: 'name',
                valueField: 'name',
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{allEmailAccounts}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(_attribute, newValue);
                        }

                    }
                }
            }, config || {});

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, _config);
        },

        getPostImportActionInput: function (vmKeyObject, attribute) {
            var config = {};
            config.postImportAction = {
                fieldcontainer: {
                    bind: {
                        hidden: '{!isSourceFile}'
                    }
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.postimportaction,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.postimportaction'
                },
                allowBlank: false,
                name: attribute,
                store: CMDBuildUI.model.tasks.TaskImportExport.postImportAsctions,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('postImportAction') !== newValue) {
                            vm.get(vmKeyObject)._config.set('postImportAction', newValue);
                        }

                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('postImportAction', config);
        },

        getTargetDirectoryInput: function (vmKeyObject, attribute) {
            var config = {};
            config.targetDirectory = {
                fieldcontainer: {
                    bind: {
                        hidden: '{!isMoveFiles}'
                    }
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.directory,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.directory'
                },
                allowBlank: false,
                name: attribute,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get('targetDirectory') !== newValue) {
                            vm.get(vmKeyObject)._config.set('targetDirectory', newValue);
                        }

                    }
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput('targetDirectory', config);
        },
        getFilterTypeInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var _config = {};

            _config.filterType = Ext.merge({}, {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.filtertype,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.filtertype'
                },
                allowBlank: false,
                name: attribute,
                store: CMDBuildUI.model.tasks.TaskReadEmail.filterTypes,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        var form = vm.getView();
                        var fromRegexInput = form.down('#filterSenderRegex_input');
                        var subjectRegexInput = form.down('#filterSubjectRegex_input');
                        var functionNameInput = form.down('#filter_function_name_input');
                        if (vm.get(vmKeyObject)._config.get('filter_type') !== newValue) {
                            vm.get(vmKeyObject)._config.set('filter_type', newValue);
                        }
                        me.setAllowBlank(fromRegexInput, !(form && newValue === 'regex'), form.form);
                        me.setAllowBlank(subjectRegexInput, !(form && newValue === 'regex'), form.form);
                        me.setAllowBlank(functionNameInput, !(form && newValue === 'function'), form.form);

                    }
                }
            }, config || {});
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('filterType', _config);
        },
        getFunctionsInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var _config = {};
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            _config[_attribute] = Ext.merge({}, {
                disabledCls: '',
                displayField: 'description',
                valueField: 'name',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.funktion'
                },
                allowBlank: false,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{allFunctionsStore}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = me.getViewModel();
                        if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(_attribute, newValue);
                        }
                    }
                }
            }, config || {});


            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(_attribute, _config);
        },

        getFilterRegexFromInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            _config.filterSenderRegex = Ext.merge({}, {
                fieldcontainer: {
                    localized: {
                        // fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description',
                        // labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                    },
                    // userCls: 'with-tool',
                    labelToolIconCls: 'fa-list',
                    labelToolIconQtip: 'Regex',
                    labelToolIconClick: 'onSenderRegexClick',
                    hideToolOnViewMode: true,
                    bind: {
                        hidden: '{!isRegex}'
                    }
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.sender,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.sender'
                },
                allowBlank: false,
                disabled: true,
                disabledCls: '',
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                }
            }, config || {});


            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('filterSenderRegex', _config);
        },

        getFilterRegexSubjectInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            _config.filterSubjectRegex = Ext.merge({}, {
                fieldcontainer: {
                    localized: {
                        // fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description',
                        // labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                    },
                    userCls: 'with-tool',
                    labelToolIconCls: 'fa-list',
                    labelToolIconQtip: 'Regex',
                    labelToolIconClick: 'onSubjectRegexClick',
                    hideToolOnViewMode: true
                },

                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.subject,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.subject'
                },
                allowBlank: false,
                disabled: true,
                disabledCls: '',
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                }
            }, config || {});

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('filterSubjectRegex', _config);
        },

        getFilterRejectInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var configAttribute = attribute.split('.');
            var _attribute = configAttribute[configAttribute.length - 1];
            var input = Ext.merge({}, {
                xtype: 'checkbox',

                name: name,
                itemId: Ext.String.format('{0}_input', name),
                bind: {
                    disabled: '{actions.view}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (checkbox, newValue, oldValue) {
                        var form = me.getView();
                        var vm = form.getViewModel();
                        if (vm.get(vmKeyObject)._config.get(_attribute) !== newValue) {
                            vm.get(vmKeyObject)._config.set(_attribute, newValue);
                        }
                        var folderRejectInput = form.down('#folder_rejected_input');
                        me.setAllowBlank(folderRejectInput, !(form && newValue), form.form);
                        checkbox.lookupViewModel().set('isMoveReject', newValue);
                    }
                }
            }, config || {});
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.movereject,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.movereject'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getBodyParsingInput: function (vmKeyObject, attribute, config) {
            var input = Ext.merge({}, {
                xtype: 'checkbox',

                name: name,
                itemId: Ext.String.format('{0}_input', name),
                bind: {
                    disabled: '{actions.view}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (checkbox, newValue, oldValue) {
                        checkbox.lookupViewModel().set('isMoveReject', newValue);
                    }
                }
            }, config || {});
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.bodyparsing,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.bodyparsing'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getParsingInput: function (vmKeyObject, attribute, config) {
            var _config = {};
            var configAttribute = attribute.split('.');
            _config[configAttribute[configAttribute.length - 1]] = Ext.merge({}, {
                name: attribute,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var vm = input.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !== newValue) {
                            vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                        }
                    }
                }
            }, config || {});
            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(configAttribute[configAttribute.length - 1], _config);
        },

        getActionNotificationActiveInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var form = me.getView();
            var configAttribute = attribute.split('.');
            var input = Ext.merge({}, {
                xtype: 'checkbox',

                name: configAttribute[1],
                itemId: Ext.String.format('{0}_input', configAttribute[1]),
                disabledCls: '',
                bind: {
                    disabled: '{actions.view}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (checkbox, newValue, oldValue) {
                        var vm = form.getViewModel();
                        if (vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !== newValue) {
                            vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                        }
                        vm.set('isNotificationActive', newValue);
                        var notificationTemplateInput = form.down('#action_notification_template_input');
                        me.setAllowBlank(notificationTemplateInput, !(form && newValue), form.form);
                    }
                }
            }, config || {});
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.sendnotiifcation,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.sendnotiifcation'
                },

                items: [input]
            };
            return fieldcontainer;
        },
        getSaveAttachmentInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var configAttribute = attribute.split('.');
            var input = Ext.merge({}, {
                xtype: 'checkbox',

                name: configAttribute[1],
                itemId: Ext.String.format('{0}_input', configAttribute[1]),
                disabledCls: '',
                bind: {
                    disabled: '{actions.view}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (checkbox, newValue, oldValue) {

                        var form = me.getView();
                        var attachmentsCategoryInput = form.down('#action_attachments_category_input');
                        me.setAllowBlank(attachmentsCategoryInput, !(form && newValue), form.form);
                        var vm = form.getViewModel();
                        if (vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !== newValue) {
                            vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                        }
                        vm.set('isAttachmentActive', newValue);
                    }
                }
            }, config || {});
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.saveattachmentsdms,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.saveattachmentsdms'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getAttachmentCategoryInput: function (vmKeyObject, attribute, config) {
            var me = this;
            // dmsLookupStore
            var _config = {};
            var configAttribute = attribute.split('.');

            _config[configAttribute[configAttribute.length - 1]] = Ext.merge({}, {
                disabledCls: '',
                displayField: 'description',
                valueField: '_id',
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.category,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.category'
                },
                allowBlank: true,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{dmsLookupStore}'
                },
                listeners: {
                    change: function (checkbox, newValue, oldValue) {
                        var form = me.getView();
                        var vm = form.getViewModel();
                        if (vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !== newValue) {
                            vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                        }
                    }
                }
            }, config || {});


            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(configAttribute[configAttribute.length - 1], _config);
        },

        getStartProcessInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var configAttribute = attribute.split('.');
            var input = Ext.merge({}, {
                xtype: 'checkbox',
                name: name,
                itemId: Ext.String.format('{0}_input', name),
                disabledCls: '',
                bind: {
                    disabled: '{actions.view}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (checkbox, newValue, oldValue) {
                        var form = me.getView();
                        var workflowClassNameInput = form.down('#action_workflow_class_name_input');
                        var attachmentscategoryInput = form.down('#action_workflow_attachmentscategory_input');
                        var workfowAdvanceInput = form.down('#action_workflow_advance_input');

                        var saveAttachementInput = form.down('#action_workflow_attachmentssave_input');
                        var classNameInput = form.down('#action_workflow_class_name_input');

                        var vm = form.getViewModel();
                        if (!newValue) {
                            attachmentscategoryInput.reset();
                            saveAttachementInput.reset();
                            classNameInput.reset();
                            workfowAdvanceInput.reset();
                            me.setAllowBlank(attachmentscategoryInput, !(form && newValue), form.form);

                        }
                        me.setAllowBlank(workflowClassNameInput, !(form && newValue), form.form);
                        if (vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !== newValue) {
                            vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                        }
                        vm.set('isStartProcessActive', newValue);
                    }
                }
            }, config || {});
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.startprocess,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.startprocess'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getProcessesInput: function (vmKeyObject, attribute, config) {

            var me = this;
            var _config = {};
            var configAttribute = attribute.split('.');

            _config[configAttribute[configAttribute.length - 1]] = Ext.merge({}, {
                displayField: 'description',
                valueField: '_id',
                fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.process,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.process'
                },
                allowBlank: true,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{processesStore}',
                    disabled: '{comeFromClass}'
                },
                listeners: {
                    change: function (combo, newValue, oldValue) {
                        me.getViewModel().set('workflowClassName', newValue);
                    }
                }
            }, config || {});

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(configAttribute[configAttribute.length - 1], _config);
        },

        getTaskUserInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var _config = {};
            var configAttribute = attribute.split('.');

            _config[configAttribute[configAttribute.length - 1]] = Ext.merge({}, {
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.jobusername,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.jobusername'
                },
                allowBlank: true,
                name: attribute,
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var form = me.getView();
                        var vm = form.getViewModel();

                        if (vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !== newValue) {
                            vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                        }
                    }
                }
            }, config || {});

            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(configAttribute[configAttribute.length - 1], _config);
        },

        getWorkflowAdvanceInput: function (vmKeyObject, attribute, config) {
            var configAttribute = attribute.split('.');
            var input = Ext.merge({}, {
                xtype: 'checkbox',
                name: configAttribute[1],
                itemId: Ext.String.format('{0}_input', configAttribute[1]),
                disabledCls: '',
                bind: {
                    disabled: '{actions.view}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (checkbox, newValue, oldValue) {
                        var vm = checkbox.lookupViewModel();
                        if (vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !== newValue) {
                            vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                        }
                    }
                }
            }, config || {});
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.advanceworkflow,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.advanceworkflow'
                },

                items: [input]
            };
            return fieldcontainer;
        },
        getWorkflowSaveAttachmentsInput: function (vmKeyObject, attribute, config) {
            var me = this;
            var configAttribute = attribute.split('.');
            var input = Ext.merge({}, {
                xtype: 'checkbox',
                name: configAttribute[1],
                itemId: Ext.String.format('{0}_input', configAttribute[1]),
                disabledCls: '',
                bind: {
                    disabled: '{actions.view}',
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute)
                },
                listeners: {
                    change: function (checkbox, newValue, oldValue) {
                        var form = me.getView();
                        var vm = form.getViewModel();
                        var attachmentscategoryInput = form.down('#action_workflow_attachmentscategory_input');
                        me.setAllowBlank(attachmentscategoryInput, !(form && newValue), form.form);
                        if (vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !== newValue) {
                            vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                        }
                    }
                }
            }, config || {});
            var fieldcontainer = {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.saveattachments,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.saveattachments'
                },

                items: [input]
            };
            return fieldcontainer;
        },

        getWorkflowAttachmentCategoryInput: function (vmKeyObject, attribute, config) {
            // dmsLookupStore
            var me = this;
            var configAttribute = attribute.split('.');
            var _config = {};
            _config[configAttribute[configAttribute.length - 1]] = Ext.merge({}, {
                disabledCls: '',
                displayField: 'description',
                valueField: 'code',
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.category,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.category'
                },
                bind: {
                    value: Ext.String.format('{{0}.{1}}', vmKeyObject, attribute),
                    store: '{processDmsLookupStore}'
                },
                listeners: {
                    change: function (checkbox, newValue, oldValue) {
                        var form = me.getView();
                        var vm = form.getViewModel();
                        if (vm.get(vmKeyObject)._config.get(configAttribute[configAttribute.length - 1]) !== newValue) {
                            vm.get(vmKeyObject)._config.set(configAttribute[configAttribute.length - 1], newValue);
                        }
                    }
                }
            }, config || {});


            return CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(configAttribute[configAttribute.length - 1], _config);
        },
        getProcessAttributesGrid: function () {
            return {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.tasks.processattributes,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.tasks.processattributes'
                },
                items: [{
                    columnWidth: 1,
                    items: [{
                        xtype: 'components-grid-reorder-grid',
                        itemId: 'processAttributesGrid',
                        bind: {
                            store: '{processAttributesMapStore}'
                        },
                        viewConfig: {
                            markDirty: false
                        },
                        columns: [{
                            flex: 1,
                            text: CMDBuildUI.locales.Locales.administration.common.labels.name,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                            },
                            dataIndex: 'description',
                            align: 'left'
                        }, {
                            flex: 1,
                            text: CMDBuildUI.locales.Locales.administration.tasks.value,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.tasks.value'
                            },
                            dataIndex: 'value',
                            align: 'left'
                        }, {
                            xtype: 'actioncolumn',
                            minWidth: 75,
                            maxWidth: 75,
                            bind: {
                                hidden: '{actions.view}'
                            },
                            align: 'center',
                            items: [{
                                handler: function (grid, rowIndex, colIndex, item, e, record) {
                                    var vm = grid.lookupViewModel().getParent();
                                    var formGridStore = vm.getStore('newProcessAttributesMapStore');
                                    grid.getStore().remove(record);
                                    formGridStore.removeAll();
                                    vm.set('allAttributesOfProcessDataFilter', vm._allAttributeFilter());
                                    formGridStore.add(record);

                                },
                                getClass: function (value, metadata, record, rowIndex, colIndex, store) {
                                    return 'x-fa fa-pencil';
                                },
                                getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                    return CMDBuildUI.locales.Locales.administration.common.tooltips.edit;
                                }
                            }, {
                                iconCls: 'x-fa fa-times',
                                tooltip: CMDBuildUI.locales.Locales.administration.common.actions.remove, // Remove
                                localized: {
                                    tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.remove'
                                },
                                handler: function (grid, rowIndex, colIndex, item, e, record) {
                                    var vm = grid.lookupViewModel().getParent();
                                    grid.getStore().remove(record);
                                    vm.set('allAttributesOfProcessDataFilter', vm._allAttributeFilter());
                                },
                                margin: '0 10 0 10'
                            }]
                        }]
                    }]
                }]
            };
        },

        getProcessAttributesGridForm: function () {
            return {

                columnWidth: 1,
                fieldLabel: '',
                bind: {
                    hidden: '{actions.view}'
                },
                margin: '20 0 20 0',
                items: [{
                    xtype: 'components-grid-reorder-grid',
                    itemId: 'processAttributesGridForm',
                    bind: {
                        store: '{newProcessAttributesMapStore}'
                    },
                    viewConfig: {
                        markDirty: false
                    },
                    columns: [{
                        flex: 1,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: 'key',
                        widget: {
                            xtype: 'combobox',
                            queryMode: 'local',
                            typeAhead: true,
                            displayField: 'description',
                            valueField: 'name',
                            forceSelection: true,
                            bind: {
                                value: '{record.key}',
                                store: '{allAttributesOfProcessStoreFiltered}'
                            },
                            listeners: {
                                change: function (combo, attributename, oldValue) {
                                    // if (attributename) {
                                    //     var me = this;
                                    //     debugger;
                                    //     // var container = combo.up('panel').down('#mergeMode_when_missing_update_value_fieldcontainer');
                                    //     var vm = combo.lookupViewModel();
                                    //     var allattributes = {};
                                    //     CMDBuildUI.util.helper.ModelHelper.getModel('process', vm.get("workflowClassName")).then(function (model) {

                                    //         model.getFields().forEach(function (field) {
                                    //             allattributes[field.name] = field;
                                    //         });
                                    //         if (attributename && allattributes[attributename]) {
                                    //             var attribute = allattributes[attributename];


                                    //             var editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(
                                    //                 attribute
                                    //             );
                                    //             var display = CMDBuildUI.util.helper.FormHelper.getReadOnlyField(
                                    //                 attribute, editor.recordLinkName
                                    //             );

                                    //             var field = {
                                    //                 itemId: 'attribute_value_description',
                                    //                 bind: {
                                    //                     hidden: '{!actions.view}',
                                    //                     value: '{record.value}',
                                    //                 }
                                    //             };
                                    //             if (editor.metadata.type === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.lookup.toLowerCase()) {
                                    //                 vm.bind("{record.value}", function (value) {

                                    //                     if (value) {
                                    //                         var lt = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(editor.metadata.lookupType);
                                    //                         lt.getLookupValues().then(function (values) {
                                    //                             var v = values.getById(value);
                                    //                             vm.set("theTask.attribute_value_description", v.get("description"));
                                    //                         });
                                    //                     } else {
                                    //                         vm.set("theTask.attribute_value_description", "");
                                    //                     }
                                    //                 });
                                    //                 field.renderer = function (value) {
                                    //                     return value;
                                    //                 };
                                    //                 field.bind.value = '{theTask.attribute_value_description}';
                                    //             }
                                    //             // container.removeAll(true);
                                    //             debugger;
                                    //             me.up('grid').getColumns()[1].widget = Ext.apply({
                                    //                 itemId: 'mergeMode_when_missing_update_value_input',
                                    //                 bind: {
                                    //                     value: '{theTask.attribute_value_description}',
                                    //                     hidden: '{actions.view}'
                                    //                 }
                                    //             }, editor);
                                    //             // me.up('grid').refresh();
                                    //             // container.add([
                                    //             //     Ext.apply({
                                    //             //         itemId: 'mergeMode_when_missing_update_value_input',
                                    //             //         bind: {
                                    //             //             value: '{theImportExportTemplate.mergeMode_when_missing_update_value}',
                                    //             //             hidden: '{actions.view}'
                                    //             //         }
                                    //             //     }, editor),
                                    //             //     Ext.apply(display, field)

                                    //             // ]);
                                    //         }
                                    //     });

                                    // }
                                }

                            }
                        }
                    }, {
                        flex: 1,
                        text: '',
                        xtype: 'widgetcolumn',
                        align: 'left',
                        dataIndex: 'value',
                        widget: {
                            xtype: 'textfield',
                            bind: {
                                value: '{record.value}'
                            }
                        }
                    }, {
                        xtype: 'actioncolumn',
                        minWidth: 75,
                        maxWidth: 75,

                        align: 'center',
                        items: [{
                            iconCls: 'x-fa fa-ellipsis-h',
                            disabled: true
                        }, {
                            iconCls: 'x-fa fa-plus',
                            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.add, // Add
                            localized: {
                                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.add'
                            },
                            handler: function (grid, rowIndex, colIndex, item, e, record) {
                                var formGridStore = grid.getStore();
                                var vm = grid.lookupViewModel().getParent();
                                var gridStore = vm.getStore('processAttributesMapStore');
                                record.set('description', vm.get('allAttributesOfProcessStore').findRecord('name', record.get('key')).get('description'));
                                gridStore.add(record);
                                formGridStore.removeAll();
                                formGridStore.add(CMDBuildUI.model.base.KeyDescriptionValue.create());
                                vm.set('allAttributesOfProcessDataFilter', vm._allAttributeFilter());
                            }
                        }]
                    }]
                }]
            };
        }
    }

});
Ext.define('CMDBuildUI.util.administration.helper.AcePopupHelper', {
    singleton: true,

    getPopup: function (viewModelKey, viewModelValue, aceAttribute, formFields, popupId, popupTitle) {
        
        var content = {
            xtype: 'panel',
            layout: {
                type: 'hbox'
            },
            ui: 'administration-formpagination',
            fieldDefaults: {
                labelAlign: 'top'
            },
            viewModel: {},
            dockedItems: [{
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',

                items: [{
                    xtype: 'component',
                    flex: 1
                }, {
                    text: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.saveBtn,

                    ui: 'administration-action-small',
                    reference: 'saveTriggerBtn',
                    itemId: 'saveTriggerBtn',
                    listeners: {
                        click: function (_button) {
                            viewModelValue.commit();
                            // update value of the widget on grid 
                            if (viewModelValue && viewModelValue.widget) {
                                viewModelValue.widget.getAceEditor().setValue(viewModelValue.get(aceAttribute), 1);
                            }
                            popUp.fireEvent('close');

                        }
                    }
                }, {
                    text: CMDBuildUI.locales.Locales.administration.classes.properties.toolbar.cancelBtn,
                    ui: 'administration-secondary-action-small',
                    listeners: {
                        click: function () {
                            viewModelValue.reject();
                            Ext.asap(function (viewModelValue) {
                                // update value of the widget on grid 
                                if (viewModelValue && viewModelValue.widget) {
                                    viewModelValue.widget.getAceEditor().setValue(viewModelValue.get(aceAttribute));
                                }
                            }, this, [viewModelValue]);
                            popUp.fireEvent('close');
                        }
                    }
                }]
            }],

            items: [{
                flex: 1,
                height: '700px',
                viewModel: {},
                items: [{
                    xtype: 'component',
                    height: '660px',
                    viewModel: {},
                    html: Ext.String.format('<div id="{0}"></div>', viewModelKey),
                    listeners: {
                        afterrender: function (cmp) {
                            var editor = ace.edit(viewModelKey, {
                                //set autoscroll
                                autoScrollEditorIntoView: true,
                                maxLines: 90, // this will be changed on "heightUpdateFunction"
                                // set theme
                                theme: "ace/theme/chrome",
                                // show line numbers
                                showLineNumbers: true,
                                // hide print margin
                                showPrintMargin: false,
                                wrap: true

                            });
                            editor.getSession().setMode("ace/mode/javascript");
                            editor.getSession().setUseWrapMode(true);
                            editor.setValue(viewModelValue.get(aceAttribute), -1);

                            var heightUpdateFunction = function (editor) {
                                var editorDiv = document.getElementById(viewModelKey); // its container
                                var doc = editor.getSession().getDocument(); // a reference to the doc
                                var lineHeight = editor.renderer.lineHeight;
                                editorDiv.style.height = lineHeight * doc.getLength() + "px"; // set new container height 
                                editorDiv.style.width = '100%'; // force container width to 100%
                                editor.setOption('maxLines', Math.ceil((cmp.up().getHeight() - 50) / lineHeight));
                                editor.setOption('minLines', Math.ceil((cmp.up().getHeight() - 50) / lineHeight));
                            };

                            // Set initial size to match initial content
                            Ext.asap(function (editor) {
                                heightUpdateFunction(editor);
                            }, this, [editor]);

                            // Whenever a change happens inside the ACE editor, update
                            // the size again
                            var onUpdateFunction = function (event, doc) {
                                Ext.asap(function (doc, viewModelValue) {

                                    // update stored value 
                                    viewModelValue.set(aceAttribute, doc.getValue());
                                }, this, [doc, viewModelValue]);
                            };

                            editor.getSession().on('change', onUpdateFunction);
                        }
                    }
                }]
            }, this.getFormFields(viewModelKey, viewModelValue, formFields)]
        };
        var listeners = this.getListeners(popupId);
        var popUp = CMDBuildUI.util.Utilities.openPopup(
            popupId,
            popupTitle,
            content,
            listeners, {
                ui: 'administration',
                width: '800px',
                height: '700px',
                viewModel: {},
                draggable: true
            }
        );
        return popUp;
    },
    getFormFields: function (viewModelKey, viewModelValue, formFields) {
        var container = {
            widht: '120px',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            viewModel: {
                data: {}
            },
            items: formFields
        };

        container.viewModel.data[viewModelKey] = viewModelValue;
        return container;
    },

    getListeners: function(popupId){
        return {
            closed: function () {
                CMDBuildUI.util.Utilities.closePopup(popupId);
            },
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                CMDBuildUI.util.Utilities.closePopup(popupId);
            }
        };
    }
});
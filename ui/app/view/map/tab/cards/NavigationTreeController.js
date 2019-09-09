//TODO: Handle the loading of new records in the main store
Ext.define('CMDBuildUI.view.map.tab.cards.NavigationTreeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-navigationtree',
    listen: {
        component: {
            '#': {
                beforerender: 'onBeforeRender',
                afterrender: 'onAfterRender',
                checkchange: 'onCheckChange',
                /**
                 * 
                 * @param {*} treePanel  
                 * @param {*} record 
                 * @param {*} index 
                 * @param {*} eOpts 
                 */
                beforeselect: function (treePanel, record, index, eOpts) {
                    this.selectInTree();
                    return false;
                }
            },
            '#navTreeActioncolumn': {
                click: 'onActionColumnClick'
            }
        },
        store: {
            '#navigationTreeStore': {
                nodeexpand: 'onExpandNode'
            }
            // '#cards': {
            //     load: 'onStoreLoad'
            // }
        }
    },

    /**
     * 
     * @param {*} grid 
     * @param {*} rowIndex 
     * @param {*} colIndex 
     * @param {*} column 
     * @param {*} e 
     * @param {*} record 
     * @param {*} row 
     */
    onActionColumnClick: function (grid, rowIndex, colIndex, column, e, record, row) {
        var cardId = record.get('cardId');
        var cardType = record.get('cardType');
        if (record.get('cardType') === this.getViewModel().get('objectTypeName')) {
            CMDBuildUI.map.util.Util.setSelection(
                cardId,
                cardType
            );
        } else {
            var url = 'classes/' + cardType + '/cards/' + cardId;
            CMDBuildUI.util.Utilities.redirectTo(url, true);
        }
    },

    /**
     * @param {Ext.Component} view the view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        this.getView().getSelectionModel().setSelectionMode('MULTI');
        Ext.data.NodeInterface.decorate('CMDBuildUI.model.map.navigation.Tree');
        view.mon(CMDBuildUI.map.util.Util.getMapGridContainerView(), 'selectedchangeevent', this.onSelectedChange, this);
        view.mon(CMDBuildUI.map.util.Util.getMapContainer(), 'navtreeload', this.onNavTreeLoad, this);

    },

    /**
     * @param {Ext.data.Model} navTreeRecords model: CMDBuildUI.model.map.navigation.NavigationTree
     */
    onNavTreeLoad: function (navTreeRecords) {
        //removes record that shouldn't be put in the navigationTree
        filteredRecords = [];
        navTreeRecords.forEach(function (navTreeRecord) {
            if (this.isInNavTree(navTreeRecord.get('_type'))) {
                filteredRecords.push(navTreeRecord);
            }
        }, this);

        this.geovaluesProxyCallHandler(filteredRecords);
    },

    /**
     * 
     * @param {Ext.data.Store} cards The cards store
     * @param {[Ext.data.Model]} records model: The dimanic one of the loaded class
     * @param {boolean} successful 
     * @param {Ext.data.operation.Read} operation 
     * @param {Object} eOpts 
     */
    onStoreLoad: function (cards, records, successful, operation, eOpts) {
        var gisRecords = this._getGisNodeFromClass(this.getViewModel().get('objectTypeName'));
        var counter = 0;

        //Could find
        gisRecords.forEach(function (gisRecord) {
            var navId = gisRecord.getId();
            var parentGisRecord = CMDBuildUI.map.util.Util.getGisNavigationTree().getParent(navId);

            var parentNavId
            parentGisRecord ? parentNavId = parentGisRecord.getId() : parentNavId = 'root';

            //Gets all the records with that navId
            var parentTreeRecords = this.getNode(parentNavId, null, '_ANY');
            parentTreeRecords.forEach(function (parentTreeRecord) {

                //Here are taken only a subset of childs of the given nodeInterface because it could have
                //childs of different type (i.e. If specified in the gisnavigation)
                //
                // -Building
                //      -BuildingRoom
                //      -BuildingFloor
                //
                var validChildNodes = parentTreeRecord.childNodes.filter(function (currentValue, index, arr) {
                    return currentValue.get('navId') == navId;
                }, this);

                //Hide all the passed childs
                //This function hides even the childs of the childs, it's recursive
                this.removeNodes(validChildNodes, false);
                var parentType = parentTreeRecord.get('cardType');
                var parentId = parentTreeRecord.get('cardId');

                /**
                 * Load the children of the record applying the right
                 * advancedFilter. The filter is taken from the cards Store
                 * and applied in the proxyCall function.
                 */
                counter++; //Every server request increments the counter
                this.loadChildren(parentNavId, parentType, parentId, {
                    navTreeRecords: [navId],
                    scope: this,
                    /**
                     * @param {String} sourceNavId the source navId
                     * @param {String} sourceClass the source cardType
                     * @param {SourceId} sourceId the source cardId
                     * @param {String} destinationNavId CMDBuildUI.model.navigationTrees.TreeNode
                     * @param {Ext.data.Model[]} records //The model of the loaded targetClass   
                     */
                    callback: function (sourceNavId, sourceClass, sourceId, destinationNavId, records) {
                        var parentNode = this.getNode(sourceNavId, sourceClass, sourceId);
                        this.fillTree(parentNode, destinationNavId, records);
                    },
                    finalCallback: function () {
                        counter--; //decrements Counter
                        if (counter == 0) {
                            this.selectInTree();
                            this.updateCheckNavigationTree();
                        }
                    }
                })
            }, this);
        }, this);
    },
    /**
     * @param {Ext.Component} view the view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        var gisNavigationTree = CMDBuildUI.map.util.Util.getGisNavigationTree();

        var root = this.getView().getStore().getRoot();
        this._saveNode('root', 'root', root);
        root.set('navId', 'root');

        gisNavigationTree.load({
            /**
             * @param {Ext.data.Model} record
             * @param {Ext.data.operation.Operation} operation
             * @param {Boolean} true;
             */
            callback: function (record, operation, success) {
                this.loadChildren('root', 'root', 'root', {
                    scope: this,
                    /**
                     * @param {String} sourceNavId the source navId
                     * @param {String} sourceClass the source cardType
                     * @param {SourceId} sourceId the source cardId
                     * @param {String} destinationNavId CMDBuildUI.model.navigationTrees.TreeNode
                     * @param {Ext.data.Model[]} records //The model of the loaded targetClass   
                     */
                    callback: function (sourceNavId, sourceClass, sourceId, destinationNavId, records) {
                        var parentNode = this.getNode(sourceNavId, sourceClass, sourceId);
                        this.fillTree(parentNode, destinationNavId, records);
                        //
                    }
                });
            },
            scope: this
        });
    },

    /**
     * This functions selects the rows in the tree
     */
    selectInTree: function () {
        var selected = CMDBuildUI.map.util.Util.getSelection();

        if (selected.id && selected.type) {
            var node = this.getNode('_ANY', selected.type, selected.id);
        }
        this.getView().getSelectionModel().select(node, true, true);
    },

    /**
     * 
     */
    updateCheckNavigationTree: function () {
        var containerModel = CMDBuildUI.map.util.Util.getMapContainerViewModel();
        containerModel.set('checkNavigationTree', Ext.clone(this._checkNavigationTree));
    },

    /**
     * fired by it's view Model
     * @param {Object} selected 
     * {
     *  type: { String }
     *  id: { String }
     *  conf: {
     *      center: true || false,
     *      zoom: true || false
     *  
     *      }
     *  }
     * @param {Ext.data.Model} records the records rapresenting the geovalues of the selected card
     */
    onSelectedChange: function (selected, records) {
        console.log('Handle selection change from navigation');
        this.getView().getSelectionModel().deselectAll();

        this.loadPath(selected.type, selected.id, {
            scope: this,
            /** callback parameters
            * @param {String} navId 
            * @param {String} cardType 
            * @param {String} cardId 
            */
            onFound: function (navId, cardType, cardId) {
                var node = this.getNode(navId, cardType, cardId);
                this.getView().getSelectionModel().select(node, true, true);

                node = node.parentNode
                while (node !== this.getNode('root', 'root', 'root')) {
                    node.expand();
                    node = node.parentNode;
                }
            },

            /** callback parameters
            * @param {String} navId 
            * @param {String} cardType 
            * @param {String} cardId 
            */
            onNotFound: function (navId, cardType, cardId) {
            }
        });
    },

    /**
     * @param {String} cardType
     * @param {String} cardId
     * @param {Object} option
     * @param {Function} options.onFound
     * @param {Function} options.onNotFound
     * @param {Object} options.scope
     */
    loadPath: function (cardType, cardId, options) {
        var navIds = this._getGisNodeFromClass(cardType);

        navIds.forEach(function (navId) {
            this._loadPath(navId.getId(), cardType, cardId, [], options);
        }, this);
    },

    /**
     * 
     * @param {String} navId 
     * @param {String} cardType 
     * @param {String} cardId 
     * @param {[Object]} queue Identifies nodes. have 3 fields: navid, cardType, cardId
     * @param {Object} options 
     * @param {Function} options.onFound
     * @param {Function} options.onNonFoundi
     * @param {Object} options.scope
     */
    _loadPath: function (navId, cardType, cardId, queue, options) {
        if (!this.isInTree(navId, cardType, cardId)) {
            queue.push({
                navId: navId,
                cardType: cardType,
                cardId: cardId
            });

            this.loadParent(navId, cardType, cardId, {
                scope: this,
                /** //option.callback arguments  
                 * @param {String} sourceNavId the source navId
                 * @param {String} sourceClass the source cardType
                 * @param {SourceId} sourceId the source cardId
                 * @param {String} destinationNavId CMDBuildUI.model.navigationTrees.TreeNode
                 * @param {Ext.data.Model[]} records //The model of the loaded targetClass 
                 */
                callback: function (sourceNavId, sourceClass, sourceId, destinationNavId, records) {
                    if (records.length == 1) {
                        var cardType = records[0].get('_type');
                        var cardId = records[0].getId();
                        this._loadPath(destinationNavId, cardType, cardId, queue, options);

                    } else {
                        console.log(Ext.String.format(
                            'The following node doesn\'t have a parent in the navigationTree or have more than one.\n navid: {0}, \n cardType: {1}, \n cardId: {2} \n numberOfParent\'s: {3}',
                            sourceNavId,
                            sourceClass,
                            sourceId,
                            records.length
                        ));
                        options.onNotFound.call(options.scope, sourceNavId, sourceClass, sourceId);
                    }
                }
            })
        } else {
            this._loadQueue(navId, cardType, cardId, queue, options);
        }
    },

    /**
     * 
     * @param {String} navId 
     * @param {String} cardType 
     * @param {String} cardId 
     * @param {[Object]} queue Identifies nodes. have 3 fields: navid, cardType, cardId
     * @param {Object} options 
     * @param {Function} options.onFound
     * @param {Function} options.onNotFound
     * @param {Object} options.scope
     * 
     * callback parameters
     * @param {String} navId 
     * @param {String} cardType 
     * @param {String} cardId 
     */
    _loadQueue: function (navId, cardType, cardId, queue, options) {
        if (queue.length != 0) {
            this.loadChildren(navId, cardType, cardId, {
                scope: this,
                /**
                 * //option.callback arguments  
                 * @param {String} sourceNavId the source navId
                 * @param {String} sourceClass the source cardType
                 * @param {SourceId} sourceId the source cardId
                 * @param {String} destinationNavId CMDBuildUI.model.navigationTrees.TreeNode
                 * @param {Ext.data.Model[]} records //The model of the loaded targetClass   
                 */
                callback: function (sourceNavId, sourceClass, sourceId, destinationNavId, records) {
                    var parentNode = this.getNode(sourceNavId, sourceClass, sourceId);
                    this.fillTree(parentNode, destinationNavId, records);
                },

                finalCallback: function () {

                    var nodeObj = queue.pop();
                    this._loadQueue(nodeObj.navId, nodeObj.cardType, nodeObj.cardId, queue, options);
                }
            });
        } else {
            if (options.onFound) {
                options.onFound.call(options.scope, navId, cardType, cardId);
            }
        }
    },

    /**
     * @param {Ext.data.Model} records model: CMDBuildUI.model.map.navigation.NavigationTree
     */
    geovaluesProxyCallHandler: function (records) {
        records.forEach(function (record) {
            var cardId = record.get('_id');
            var cardType = record.get('_type');
            var navId = record.get('navTreeNodeId');

            if (!this.isInTree(navId, cardType, cardId)) {
                //make the case in wich the parent is the root node;
                var parentId = record.get('parentid');
                var parentType;
                var parentNavId;

                if (parentId === 0) {
                    parentType = 'root';
                    parentId = 'root'
                    parentNavId = 'root'
                } else {
                    var parentNavId = CMDBuildUI.map.util.Util.getGisNavigationTree().getParent(navId).getId();
                    parentType = record.get('parenttype');
                }
                var parentNodeInterface = this.getNode(parentNavId, parentType, parentId);

                if (!parentNodeInterface) {
                    console.error('The parent should always be in the tree. \nCheck the server response for the geovalues call \n with attach_nav_tree: true');
                }
                this.fillTree(parentNodeInterface, navId, [record]);
            }

        }, this);
    },

    /**
     * Handle the onCheckChange event
     * @param {Ext.data.TreeModel} node 
     * @param {Boolean} checked 
     * @param {Ext.event.Event} e 
     * @param {Object} eOpts 
     */
    onCheckChange: function (node, checked, e, eOpts) {
        this._onCheckChange(node, checked);

        if (checked == true) {
            this._onCheckChangeDown(node, checked);
        }
        this.updateCheckNavigationTree();
        console.log("ChackChangeComplete");
    },

    /**
     * Changes only up or only down checked value recursively in base at the checked value passed
     * @param {Ext.data.NodeInterface} node 
     * @param {Boolean} checked 
     */
    _onCheckChange: function (node, checked) {
        var cardType = node.get('cardType');
        var cardId = node.get('cardId');
        var navId = node.get('navId');

        switch (checked) {
            case true:
                if (navId == 'root') {
                    this.setChecked(node, true);
                } else {
                    var allOccurences = this.getNode('_ANY', cardType, cardId);

                    allOccurences.forEach(function (nodeOcurence) {
                        navId = nodeOcurence.get('navId');
                        var parentNode = nodeOcurence.parentNode;

                        var showOnlyOne = CMDBuildUI.map.util.Util.getGisNavigationTree().getNode(navId).get('showOnlyOne');
                        if (showOnlyOne == true) {
                            var siblings = this.getSiblings(navId, cardType, cardId, parentNode);
                            siblings.forEach(function (sibling) {
                                this._onCheckChange(sibling, false);
                            }, this);
                        }
                        this.setChecked(nodeOcurence, checked);
                        var parent = nodeOcurence.parentNode;
                        this._onCheckChange(parent, true);
                    }, this);
                }
                break;

            case false:
                //FIXME: there shoud be the instance of root in this._checkNavigationTree;
                var allOccurences = [];
                if (navId == 'root') {
                    allOccurences = [node];
                } else {
                    allOccurences = this.getNode('_ANY', cardType, cardId);
                }

                allOccurences.forEach(function (nodeOcurence) {
                    this.setChecked(nodeOcurence, checked);

                    nodeOcurence.childNodes.forEach(function (child) {
                        this._onCheckChange(child, checked);
                    }, this);
                }, this);
                break;
        }
    },


    /**
     * TODO://use the recursiveDown function
     * Changes the value of check for all the childs of the node. Is recursive
     * @param {Ext.data.NodeInterface} node 
     * @param {Boolean} checked usually allways true;
     */
    _onCheckChangeDown: function (node, checked) {
        this.recursiveVisit(node, function (visitedNode) {
            this.setChecked(visitedNode, checked);
            var cardType = visitedNode.get('cardType');
            var cardId = visitedNode.get('cardId');

            var allOccurences = this.getNode('_ANY', cardType, cardId);
            allOccurences.forEach(function (occurence) {
                this._onCheckChange(occurence, checked);
            }, this);

        }, this);
    },

    /**
     * @param {Ext.data.NodeInterface} node 
     * @param {Object} eOpts
     */
    onExpandNode: function (node, eOpts) {
        //
        this.loadChildren(node.get('navId'), node.get('cardType'), node.get('cardId'), {
            scope: this,
            /**
             * @param {String} sourceNavId the source navId
             * @param {String} sourceClass the source cardType
             * @param {SourceId} sourceId the source cardId
             * @param {String} destinationNavId CMDBuildUI.model.navigationTrees.TreeNode
             * @param {Ext.data.Model[]} records //The model of the loaded targetClass   
             */
            callback: function (sourceNavId, sourceClass, sourceId, destinationNavId, records) {
                console.log(node);
                console.log('onExpandNode callback');
                var parentNode = this.getNode(sourceNavId, sourceClass, sourceId);
                this.fillTree(parentNode, destinationNavId, records);
                //
            }
        });
    },

    /**
     * This function Loads the child of the card with classType = type and cardId = id with domain constraint specified in the gisNavigationTree.
     * Also appends the found childs in the tree if options.appendChilds is true. Makes a callback if setted
     * @param {String} navId The parent navId
     * @param {String} type The parent Type
     * @param {Number} id The parent Id
     * @param {Object} options
     * @param {Object} options.scope
     * @param {Function} options.callback
     * @param {Ext.data.Model[] || [String]} options.navTreeRecords specify the destinations. Must be a subSet of the childs of the navId
     * 
     * //option.callback arguments  
     * @param {String} sourceNavId the source navId
     * @param {String} sourceClass the source cardType
     * @param {SourceId} sourceId the source cardId
     * @param {String} destinationNavId CMDBuildUI.model.navigationTrees.TreeNode
     * @param {Ext.data.Model[]} records //The model of the loaded targetClass 
     */
    loadChildren: function (navId, type, id, options) {
        options = options || {};
        options.direction = true;
        options.navTreeRecords = options.navTreeRecords || null;

        Ext.applyIf(options, {
            scope: this
        });

        var gisNavigationTree = CMDBuildUI.map.util.Util.getGisNavigationTree();
        var nodes;
        var navTreeRecords = options.navTreeRecords;
        delete options.navTreeRecords;

        if (!navTreeRecords) {
            if (typeof navId === 'string') {
                if (navId == 'root') {
                    nodes = gisNavigationTree.getRoot();
                    navTreeRecords = [nodes];
                } else {
                    nodes = gisNavigationTree.getChild(navId);
                    navTreeRecords = nodes;
                }
            } else if (typeof navId === 'object') {
                navId = navId.get('navId');
                nodes = gisNavigationTree.getChild(navId);
                navTreeRecords = nodes;
            }
        }

        this.proxyCall(navId, type, id, navTreeRecords, options);
    },


    /**
     * This function loads the parent of the givenCard
     * 
     * @param {String} navId 
     * @param {String} cardType 
     * @param {String} cardId 
     * @param {Object} options
     * @param {Function} options.callback
     * @param {Object} options.scope
     * 
     * //option.callback arguments  
     * @param {String} sourceNavId the source navId
     * @param {String} sourceClass the source cardType
     * @param {SourceId} sourceId the source cardId
     * @param {String} destinationNavId CMDBuildUI.model.navigationTrees.TreeNode
     * @param {Ext.data.Model[]} records //The model of the loaded targetClass 
     */

    loadParent: function (navId, cardType, cardId, options) {
        options = options || {};
        options.direction = false;
        Ext.applyIf(options, {
            scope: this
        });

        var parentNav = CMDBuildUI.map.util.Util.getGisNavigationTree().getParent(navId);

        if (parentNav == null) {
            if (options.callback) {
                options.callback.call(options.scope, navId, cardType, cardId, 'root', [Ext.create('Ext.data.Model', {
                    _type: 'root',
                    id: 'root'
                })]);
            }
        } else {
            var parentNavId = parentNav.getId();
            this.proxyCall(navId, cardType, cardId, [parentNavId], options);
        }
    },

    /**
     * This function calls the server for the relations of the card with cardType = sourceClass  and clardId = sourceId
     * Ther relation is described by te "records" providing domain and direction. The records can be a string or directly an object
     * It' made a different call for each record;
     * @param {String} sourceNavId
     * @param {String} sourceClass
     * @param {Number} sourceId
     * @param {Ext.data.Model[] || [String]} destinationNavRecords CMDBuildUI.model.navigationTrees.TreeNode 
     * @param {Object} options
     * @param {Function} option.callback The callback execute after loading each of the destinationNavRecords
     * @param {Function} option.finalCallback The callback execute after loading all of the destinationNavRecords
     * @param {Object} otpion.scope
     * 
     * //option.callback arguments  
     * @param {String} sourceNavId the source navId
     * @param {String} sourceClass the source cardType
     * @param {SourceId} sourceId the source cardId
     * @param {String} destinationNavId CMDBuildUI.model.navigationTrees.TreeNode
     * @param {Ext.data.Model[]} records //The model of the loaded targetClass   
     */
    proxyCall: function (sourceNavId, sourceClass, sourceId, destinationNavRecords, options) {
        var me = this;
        options = options || {};
        Ext.applyIf(options, {
            scope: this,
            direction: true
        });

        var gisNavTree = CMDBuildUI.map.util.Util.getGisNavigationTree();
        var sourceNavRecord = gisNavTree.getNode(sourceNavId);

        var l = destinationNavRecords.length;

        destinationNavRecords.forEach(function (destinationNavRecord) {
            if (typeof destinationNavRecord === 'string') {
                //
                var tmpRecord = gisNavTree.getNode(destinationNavRecord);

                if (!tmpRecord) {
                    CMDBuildUI.ModelHelper.Notifier.showErrorMessage('Wrong id. No record found for id: ' + destinationNavRecord);
                    return;
                } else {
                    destinationNavRecord = tmpRecord;
                }
            }

            var targetClass = destinationNavRecord.get('targetClass');

            CMDBuildUI.util.helper.ModelHelper.getModel('class', targetClass).then(function (model) {

                store = Ext.create('CMDBuildUI.store.Base', {
                    model: model.getName(),
                    proxy: {
                        type: 'baseproxy',
                        url: Ext.String.format('/classes/{0}/cards', targetClass),
                        extraParams: {
                            page: 1,
                            start: 0,
                            limit: -1 //no limit
                        }
                    }
                });

                //Apply the existing filter. The filter is taken from the store cards
                var objectTypeName = options.scope.getViewModel().get('objectTypeName');
                if (targetClass.toLowerCase() == objectTypeName.toLowerCase()) {
                    var cards = CMDBuildUI.map.util.Util.getMapGridContainerViewModel().getStore('cards');
                    var majorAdvancedFilter = cards.getAdvancedFilter();
                }
                majorAdvancedFilter ? store.setAdvancedFilter(JSON.parse(majorAdvancedFilter.encode())) : null;
                //--

                var applyFilter;
                options.direction ? applyFilter = destinationNavRecord.get('domain') : applyFilter = sourceNavRecord.get('domain');

                if (applyFilter) {
                    var advancedFilter = store.getAdvancedFilter();
                    advancedFilter.addRelationFilter(
                        options.direction ? destinationNavRecord.get('domain') : sourceNavRecord.get('domain'),
                        sourceClass,
                        targetClass,
                        options.direction ? me._reverseDirection(destinationNavRecord.get('direction')) : sourceNavRecord.get('direction'),//verify here
                        'oneof',
                        [{
                            className: sourceClass,
                            id: sourceId
                        }]
                    )
                }

                store.load({
                    scope: options.scope,
                    /**
                     * @param {Ext.data.Mode} recordsLoaded
                     * @param {Ext.data.operation.Operation} operation
                     * @param {Boolean} success;
                     */
                    callback: function (recordsLoaded, operation, success) {
                        --l;
                        if (success) {
                            if (options.callback) {
                                options.callback.call(options.scope,
                                    sourceNavId,
                                    sourceClass,
                                    sourceId,
                                    destinationNavRecord.getId(),
                                    recordsLoaded
                                )
                            }
                        } else {
                            CMDBuildUI.util.Notifier.showErrorMessage('Someting went wrong on loading the gisNavigationTree');
                        }

                        if (l == 0 && options.finalCallback) {
                            options.finalCallback.call(options.scope);
                        }
                    }
                })
            })

        }, this);
    },

    /**
     * This function sets the child of treeNode with the ones in records
     * @param {Ext.data.NodeInterface} viewTreeNode the model: CMDBuildUI.model.map.navigation.Tree
     * @param {String} childNavId The id of the CMDBuildUI.model.map.navigation.Tree. It must be on of the 
     * @param {Ext.data.Model[]} records //The model is the one of the loaded class, can change every time 
     */
    fillTree: function (viewTreeNode, childNavId, records) { //Test performance
        var queue = [];
        /**
         * 
         * @param {*} childs 
         * @param {*} navId 
         * @param {*} cardType 
         * @param {*} cardId 
         * @param {*} cardDescription 
         * @param {Ext.data.NodeInterface} parentNode
         */
        function _fillTreeAux(childs, navId, cardType, cardId, cardDescription, parentNode) {
            childs.push({
                navId: navId,
                cardId: cardId,
                cardType: cardType,
                description: cardDescription,
                text: cardDescription,
                leaf: CMDBuildUI.map.util.Util.getGisNavigationTree().getChild(navId).length == 0 ? true : false,
                expanded: false,
                checked: _checkedValue.call(this, navId, cardType, cardId, parentNode)
            })
        };


        /**
         * 
         * @param {String} navId 
         * @param {String} cardType 
         * @param {String} cardId
         * @param {Ext.data.NodeInterface} parentNode
         */
        function _checkedValue(navId, cardType, cardId, parentNode) {
            //ParentNode = null should not appear after server issue roesolved on wrong navTree attached
            var checked = this.getChecked(cardType, cardId);

            if (checked === null) {
                var parentCheck = parentNode.get('checked');

                if (parentCheck == false) {
                    return false;
                } else {
                    var showOnlyOne = CMDBuildUI.map.util.Util.getGisNavigationTree().getNode(navId).get('showOnlyOne');
                    if (showOnlyOne == false) {
                        return parentCheck;
                    } else {
                        var siblings = this.getSiblings(navId, cardType, cardId, parentNode);

                        for (var i = 0; i < siblings.length; i++) {
                            if (siblings[i].get('checked') == true) {
                                return false;
                            }
                        }

                        for (var i = 0; i < childs.length; i++) {
                            if (childs[i].checked == true) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            } else if (checked == true) {
                queue.push({
                    navId: navId,
                    cardType: cardType,
                    cardId: cardId
                })
            }

            return false;
        }

        var childs = [];
        records.forEach(function (record) {
            var cardId = record.getId();
            var cardType = record.get('_type');
            var cardDescription = record.get('Description') || record.get('description');

            if (!this.isInTree(childNavId, cardType, cardId)) {
                _fillTreeAux.call(this, childs, childNavId, cardType, cardId, cardDescription, viewTreeNode);
            }

        }, this);

        this.addNode(viewTreeNode, childs);

        queue.forEach(function (el) {
            var node = this.getNode(el.navId, el.cardType, el.cardId);
            this._onCheckChange(node, true);
        }, this);

        this.updateCheckNavigationTree();
    },

    /**
     * @param {Ext.data.NodeInterface} viewParentNode The parent node in wich append the node
     * @param {[Object]} nodes Contains information for the Ext.data.NodeInterface node to create
     * @param {String} nodes[i].navId The id of the navigation tree
     * @param {String} nodes[i].cardType the card type
     * @param {String} nodes[i].cardId cthe card ids
     * @param {String} nodes[i].text The text to show
     */
    addNode: function (viewParentNode, nodes) {

        nodes.forEach(function (node) {
            var nodeInterface = Ext.create('CMDBuildUI.model.map.navigation.Tree', node);
            var navId = node.navId;
            var cardType = node.cardType;
            var cardId = node.cardId;
            var checked = node.checked;

            this._saveNode(navId, cardId, nodeInterface);
            this._saveChecked(cardType, cardId, checked);

            var index = this._addNodeAux(viewParentNode.childNodes, nodeInterface);
            viewParentNode.insertChild(index, nodeInterface);

        }, this);
    },

    /**
     * This function returns the index in wich to insert the node to preserver the alphabeticahal order
     * @param {[Ext.data.NodeInterface]} siblings
     * @param {Ext.data.NodeInterface} node
     * @returns {Number} the indes in wich insert the node
     */
    _addNodeAux: function (siblings, node) {
        var toInsert = node.get('text');
        if (toInsert) {
            toInsert = toInsert.toLowerCase();
        } else {
            return siblings.length;
        }

        for (var i = 0; i < siblings.length; i++) {
            var inserted = siblings[i].get('text');
            if (inserted) {
                inserted = inserted.toLowerCase();
            } else {
                return i;
            }

            if (toInsert <= inserted) {
                return i;
            }
        }

        return siblings.length;
    },

    /**
     * Removes the child from the parent.
     * @param {Ext.data.NodeInterface} viewParentNode The parent node in wich append the node
     * @param {Ext.data.NodeInterface} child viewParentNode The parent node in wich append the node
     */
    _removeNode: function (viewParentNode, child) {
        var childNavId = child.get('navId');
        var childId = child.get('cardId');

        this._deleteNode(childNavId, childId);
        viewParentNode.removeChild(child);
    },

    /**
     * @param {[Ext.data.NodeInterface]} nodes
     * @param {Boolean} visibility
     */
    removeNodes: function (nodes, visibility) {
        nodes.forEach(function (node) {
            this.recursiveVisit(node, function (visitedNode) {
                var internalId = visitedNode.internalId;
                var viewTreeNode = this.getView().getView().getNodeById(internalId);

                if (visibility) {
                    this._onCheckChange(visitedNode, true);
                } else {
                    this._removeNode(visitedNode.parentNode, visitedNode);
                    this._onCheckChange(visitedNode, false);
                }
                if (!viewTreeNode) {
                    console.error(Ext.String.format('Non ho trovato la view del record: \n navId: {0},\ncardType: {1}\n,cardId: {}',
                        visitedNode.get('navId'),
                        visitedNode.get('cardType'),
                        visitedNode.get('cardId')
                    ))
                }
            }, this, { deleteOperations: true });
        }, this);
    },

    /**
     * The only function that writes data the this._loadedNodes structure.
     * @param {String} navId 
     * @param {String} cardId 
     * @param {Ext.data.NodeInterface} node The node to save
     */
    _saveNode: function (navId, cardId, node) {
        try {
            this._loadedNodes[navId][cardId] = node
        } catch (e) {
            try {
                this._loadedNodes[navId] = {};
                this._loadedNodes[navId][cardId] = node;
            } catch (e) {
                this._loadedNodes = {};
                this._loadedNodes[navId] = {};
                this._loadedNodes[navId][cardId] = node;
            }
        }
    },

    /**
     * The only function that deletes data from the this._loadedNodes structure.
     * @param {String} navId 
     * @param {String} cardId 
     * @param {Ext.data.NodeInterface} node The node to save
     */
    _deleteNode: function (navId, cardId) {
        try {
            delete this._loadedNodes[navId][cardId];
        } catch (e) { }
    },

    /**
     * 
     * @param {String} navId The id of the navigation tree record. in navId == '_ANY' looks in all navIds
     * @param {String} cardType 
     * @param {String} cardId 
     * @returns {Boolean} returns the information in wich navId it appears;
     */
    isInTree: function (navId, cardType, cardId) {
        switch (navId) {
            case '_ANY':
                for (var el in this._loadedNodes) {
                    if (this._loadedNodes[el][cardId] != null) {
                        return true;
                    }
                }
                return false;
            default:
                try {
                    if (this._loadedNodes[navId][cardId]) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (e) {
                    return false;
                }
        }
    },

    /**
     * 
     * @param {String} navId The id of the navigation tree record. if '_ANY' return for each navId the found instance
     * @param {String} cardType 
     * @param {String} cardId 
     * @returns {Ext.data.NodeInterface || [Ext.data.NodeInterface]} the required node. Array if required for all navIds;
     */
    getNode: function (navId, cardType, cardId) {
        switch (navId) {
            case '_ANY':
                var returned = [];
                for (var el in this._loadedNodes) {
                    if (this._loadedNodes[el][cardId] != null) {
                        returned.push(this._loadedNodes[el][cardId]);
                    }
                }
                return returned;
            default:
                switch (cardId) {
                    case '_ANY':
                        var returned = [];
                        for (var el in this._loadedNodes[navId]) {
                            returned.push(this._loadedNodes[navId][el]);
                        }
                        return returned;
                        break;
                    default:
                        try {
                            return this._loadedNodes[navId][cardId];
                        } catch (e) {
                            return false;
                        }
                        break;
                }
        }

    },

    /**
     * This function visits all the children of the given node and applyes the specified function
     * @param {Ext.data.nodeInterface} node the Node to wich apply now the function
     * @param {Function} functionToApply
     * @param {Object}  scope
     * @param {Object} options
     * @param {Boolean} options.deleteOperations have operation of deletion on the nodes. Default = false
     * 
     * the arguments of the functionToApply is the node being analized
     * @param {Ext.data.nodeInterface} node
     */
    recursiveVisit: function (node, functionToApply, scope, options) {
        scope = scope || this;
        options = options || {};
        Ext.applyIf(options, {
            deleteOperations: false
        })

        var childs = node.childNodes;

        if (!options.deleteOperations) {
            for (var i = 0; i < childs.length; i++) {
                this.recursiveVisit(childs[i], functionToApply, scope, options);
            }
        } else {
            while (childs && childs.length != 0) {
                this.recursiveVisit(childs[0], functionToApply, scope, options);
            }
        }
        functionToApply.call(scope, node);
    },
    /**
     * 
     * @param {String} navId 
     * @param {String} cardType 
     * @param {String} cardId 
     * @param {Ext.data.nodeInterface} parentNode
     * @returns {[Ext.data.NodeInterface]} The siblings of the given node
     */
    getSiblings: function (navId, cardType, cardId, parentNode) {
        var siblings = [];
        var childs = parentNode.childNodes;

        childs.forEach(function (child) {
            if (child.get('navId') == navId && child.get('cardId') != cardId) {
                siblings.push(child);
            }
        })

        return siblings;
    },

    /**
     * 
     * @param {Ext.data.NodeInterface} node 
     * @param {Boolead} checked 
     */
    setChecked: function (node, checked) { //NOTE: Should be better have cardType and cardId as input parameters?
        var cardType = node.get('cardType');
        var cardId = node.get('cardId');

        node.set('checked', checked);
        this._saveChecked(cardType, cardId, checked);

    },

    /**
     * 
     * @param {String} cardType 
     * @param {String} cardId 
     */
    getChecked: function (cardType, cardId) {
        try {
            return this._checkNavigationTree[cardType][cardId].visible
        } catch (e) {
            return null;
        }
    },
    /**
     * 
     * @param {String} type 
     * @param {String} id 
     * @param {Boolean} checked 
     */
    _saveChecked: function (cardType, cardId, checked) {
        try {
            this._checkNavigationTree[cardType][cardId].visible = checked;
        } catch (e) {
            try {
                this._checkNavigationTree[cardType][cardId] = {};
                this._checkNavigationTree[cardType][cardId].visible = checked;
            }
            catch (e) {
                try {
                    this._checkNavigationTree[cardType] = {}
                    this._checkNavigationTree[cardType][cardId] = {};
                    this._checkNavigationTree[cardType][cardId].visible = checked;
                } catch (e) {
                    this._checkNavigationTree = {}
                    this._checkNavigationTree[cardType] = {}
                    this._checkNavigationTree[cardType][cardId] = {};
                    this._checkNavigationTree[cardType][cardId].visible = checked;

                }
            }
        }
    },

    //----------------
    privates: {
        /**
         * @param {String} direction can be '_1' or '_2'
         * @param {String} returns the other direction 
         */
        _reverseDirection: function (direction) {
            switch (direction) {
                case '_1':
                    return '_2';
                case '_2':
                    return '_1';
                default:
                    return null;
            }
        },

        /**
         * This function returns the node of thTree store having the targhetClass = nodeName
         * @param {string} nodename Teh node to look for in the theTree store
         * @returns {[CMDBuildUI.model.navigationTrees.TreeNode]} the classes that matchs in the navigationTree
         */
        _getGisNodeFromClass: function (nodeName) {
            var nodes = CMDBuildUI.map.util.Util.getGisNavigationTree().nodes().getRange();
            var rememberNodeName;
            var returned = [];

            nodes.forEach(function (node) {
                rememberNodeName = nodeName;
                while (rememberNodeName != '') {
                    if (node.get('targetClass') == rememberNodeName) {
                        returned.push(node);
                        return;
                    } else {
                        rememberNodeName = CMDBuildUI.util.helper.ModelHelper.getClassFromName(rememberNodeName).get('parent');
                    }
                }
            }, this);

            return returned;
        },

        /**
         * This function tells if a class appears in the navigationTree
         * @param {String} className 
         */
        isInNavTree: function (className) {
            var nodes = CMDBuildUI.map.util.Util.getGisNavigationTree().nodes().getRange();
            var rememberNodeName;

            for (var i = 0; i < nodes.length; i++) {
                var node = nodes[i];
                rememberNodeName = className;
                while (rememberNodeName != '') {
                    if (node.get('targetClass') == rememberNodeName) {
                        return true;
                    } else {
                        rememberNodeName = CMDBuildUI.util.helper.ModelHelper.getClassFromName(rememberNodeName).get('parent');
                    }
                }
            }
            return false;
        }
    } //END PRIVATES
});

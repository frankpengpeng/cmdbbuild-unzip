Ext.define('CMDBuildUI.view.administration.content.navigationtrees.TreeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-navigationtrees-tree',

    control: {
        '#': {
            itemexpand: 'onItemExpand'
        }
    },

    onItemExpand: function (node) {

        Ext.suspendLayouts();

        var me = this;
        var vm = me.view.getViewModel();
        var theNavigationTreeNodes = vm.get('theNavigationtree.nodes');
        var targetClass = node.get('targetClass') || vm.get('theNavigationtree.targetClass');
        if (targetClass) {
            var targetType = node.get("targetIsProcess") ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
            var targetObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(targetClass, targetType);
            var targetHierarchy = targetObject.getHierarchy();
            targetObject.getDomains().then(function (domains) {
                domains.getRange().forEach(function (d) {
                    if (d.get("active")) {

                        var isCheckedNode = false; // found && found.domain === d.get('name');
                        if (Ext.Array.contains(targetHierarchy, d.get("source"))) {
                            var destinationType = d.get("destinationProcess") ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                            var destinationObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(d.get("destination"), destinationType);

                            node.appendChild({
                                _id: new Date().getTime(),
                                text: d.get("description") + ' [' + d.getTranslatedDescriptionDirect() + ' ' + destinationObject.get("description") + ']',
                                targetClass: d.get("destination"),
                                targetIsProcess: d.get("destinationProcess"),
                                domain: d.get("name"),
                                checked: false,
                                direction: '_1',
                                filter: '',
                                parent: node.get('_id'),
                                showOnlyOne: false,
                                recursionEnabled: false,
                                expanded: false,
                                leaf: false
                            });
                        }
                        if (Ext.Array.contains(targetHierarchy, d.get("destination"))) {
                            var sourceType = d.get("sourceProcess") ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                            var sourceObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(d.get("source"), sourceType);
                            if (!node.findChild('domain', d.get('name'))) {
                                node.appendChild({
                                    _id: new Date().getTime(),
                                    text: d.get("description") + ' [' + d.getTranslatedDescriptionInverse() + ' ' + sourceObject.get("description") + ']',
                                    targetClass: d.get("source"),
                                    targetIsProcess: d.get("sourceProcess"),
                                    domain: d.get("name"),
                                    checked: false,
                                    filter: '',
                                    direction: '_2',
                                    parent: node.get('_id'),
                                    showOnlyOne: false,
                                    recursionEnabled: false,
                                    expanded: false,
                                    leaf: false
                                });
                            }
                        }
                    }
                });
                me.findCheckedNodes(node, theNavigationTreeNodes[0].nodes);
            });
        }


        Ext.resumeLayouts();
    },
    privates: {
        /**
         * @private
         * 
         */
        recursiveParentCheck: function (node, checked) {
            var me = this;
            if (checked) {
                node.set('checked', true);
                if (node.parentNode) {
                    // up direction
                    me.recursiveParentCheck(node.parentNode, checked);
                }
            } else {
                // down direction
                node.set('checked', false);
                if (node.childNodes && node.childNodes.length) {
                    Ext.Array.forEach(node.childNodes, function (item) {
                        me.recursiveParentCheck(item, checked);
                    });
                }
            }
        },

        findCheckedNodes: function (nodeClient, nodesServer) {

            var me = this;
            Ext.Array.forEach(nodesServer, function (itemServer) {
                if (nodeClient.get('_id') === itemServer.parent) {
                    Ext.Array.forEach(nodeClient.childNodes, function (itemClient) {
                        if (itemServer.domain === itemClient.get('domain')) {
                            itemClient.set('_id', itemServer._id);
                            itemClient.set('parent', nodeClient.get('_id'));
                            itemClient.set('checked', true);
                            itemClient.set('recursionEnabled', itemServer.recursionEnabled);
                            itemClient.set('showOnlyOne', itemServer.showOnlyOne);
                            itemClient.set('filter', (typeof itemServer.filter === 'string') ? itemServer.filter : '');
                            itemClient.expand();
                        }
                    });

                } else {
                    me.findCheckedNodes(nodeClient, itemServer.nodes);
                }
            });
        }
    }

});
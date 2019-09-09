Ext.define('CMDBuildUI.util.administration.File', {
    singleton: true,

    /**
     * @param {String} method
     * @param {FormData} formData
     * @param {DOM} inputFile
     * @param {String} url
     * @param {Function/Object} callback
     * @param {Function} callback.success
     * @param {Function} callback.failure
     * @param {Function} callback.callback
     * @param {Object} callback.scope
     * 
     */
    upload: function (method, formData, inputFile, url, callback) {
        var xmlhttp = new XMLHttpRequest();

        // TODO: show progress bar
        // Uploading progress handler
        // xmlhttp.upload.onprogress = function (e) {
        //     if (e.lengthComputable) {
        //         var percentComplete = (e.loaded / e.total) * 100;
        //         Ext.Viewport.cmdbMask(percentComplete.toFixed(0) + '%');
        //     }
        // };

        // Response handler
        xmlhttp.onreadystatechange = function (e) {
            if (this.readyState === 4) {
                if (Ext.Array.indexOf([200, 201, 204], parseInt(this.status, 10)) !== -1) {

                    if (Ext.isFunction(callback)) {
                        Ext.callback(callback, null, [Ext.decode(this.responseText)]);
                    } else if (Ext.isObject(callback)) {
                        if (callback.success) {
                            Ext.callback(callback.success, callback.scope, [Ext.decode(this.responseText)]);
                        }
                        if (callback.callback) {
                            Ext.callback(callback.callback, callback.scope, [Ext.decode(this.responseText)]);
                        }
                    }
                } else {
                    CMDBuildUI.util.Ajax.showMessages(this, {hideErrorNotification: false});
                    if (Ext.isFunction(callback)) {
                        Ext.callback(callback, null, [false, Ext.decode(this.responseText)]);
                    } else if (Ext.isObject(callback)) {
                        if (callback.failure) {
                            Ext.callback(callback.failure, callback.scope, [Ext.encode(this.responseText)]);
                        }
                        if (callback.callback) {
                            Ext.callback(callback.callback, callback.scope, [false, Ext.encode(this.responseText)]);
                        }
                    }
                }
            }
        };

        // Error handler
        xmlhttp.upload.onerror = function () {
            if (Ext.isFunction(callback)) {                
                Ext.callback(callback, null, [false, this.responseText]);
            } else if (Ext.isObject(callback)) {
                if (callback.failure) {
                    Ext.callback(callback.failure, callback.scope, [this.responseText]);
                }
                if (callback.callback) {
                    Ext.callback(callback.callback, callback.scope, [false, this.responseText]);
                }
            }
        };

        if (inputFile && inputFile.files.length) {
            // update formData
            formData.append("file", inputFile.files[0]);
        }

        // open form with file using XMLHttpRequest POST request
        xmlhttp.open(method, url);

        // set headers
        xmlhttp.setRequestHeader("CMDBuild-ActionId", CMDBuildUI.util.Ajax.getActionId());
        xmlhttp.setRequestHeader("CMDBuild-RequestId", CMDBuildUI.util.Utilities.generateUUID());
        xmlhttp.setRequestHeader("CMDBuild-Authorization", CMDBuildUI.util.helper.SessionHelper.getToken());

        // finally send
        xmlhttp.send(formData);
    }
});
'use strict';
var beautify = require('js-beautify').js;
var beautifyconf = {
    indent_size: 4,
    space_in_empty_paren: true
};

var clearfn = `
    function cleardata(obj) {
        for (var key in obj) {
            if (typeof obj[key] === "string") {
                obj[key] = obj[key].replace(/^<em>(.+)<\\/em>$/, "$1");
            } else if (typeof obj[key] === "object") {
                cleardata(obj[key]);
            }
        }
    }
    cleardata(CMDBuildUI.locales.Locales);
`;

/**
 * Dive
 * @param {String} currentKey 
 * @param {Object} into 
 * @param {Object} target 
 */
function dive(currentKey, into, target) {
    var me = this;
    for (var i in into) {
        if (into.hasOwnProperty(i)) {
            var newKey = i;
            var newVal = into[i];

            if (currentKey.length > 0) {
                newKey = currentKey + '.' + i;
            }

            if (typeof newVal === "object") {
                dive(newKey, newVal, target);
            } else {
                target[newKey] = newVal;
            }
        }
    }
}

/**
 * Flatten object
 * @param {Object} arr 
 */
function flatten(arr) {
    var newObj = {};
    dive("", arr, newObj);
    return newObj;
}

/**
 * To nested object
 * @param {Object} obj 
 */
function toNested(obj) {
    var nested = {};
    var keys = Object.keys(obj).sort();
    keys.forEach(function (key) {
        var skey = key.split('.');
        var current = nested;
        skey.forEach(function (k, i) {
            if (i === (skey.length - 1)) {
                current[k] = obj[key];
            } else if (!current[k]) {
                current[k] = {};
            }
            current = current[k];
        });
    });
    return nested;
}

/**
 * 
 * @param {String} lang 
 * @param {String} classname 
 * @param {Object} locales 
 */
function getLocalesFileContent(lang, classname, locales) {
    var clocales = {
        requires: ['CMDBuildUI.locales.' + lang + '.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: lang,
        administration: '',
    };
    // insert ordered keys
    for (var key in locales) {
        clocales[key] = locales[key];
    }
    // override adminsitration key
    clocales.administration = 'CMDBuildUI.locales.' + lang + '.LocalesAdministration.administration';
    var filecontent = "(function () {Ext.define('" + classname + "'," + JSON.stringify(clocales) + ");" + clearfn + "})();";
    filecontent = filecontent.replace('"' + clocales.administration + '"', clocales.administration);
    return beautify(filecontent, beautifyconf);
}

/**
 * 
 * @param {String} lang 
 * @param {String} classname 
 * @param {Object} locales 
 */
function getLocalesAdminFileContent(lang, classname, locales) {
    var clocales = {
        singleton: true,
        localization: lang,
    };
    // insert ordered keys
    for (var key in locales) {
        clocales[key] = locales[key];
    }

    var filecontent = "Ext.define('" + classname + "'," + JSON.stringify(clocales) + ");";
    return beautify(filecontent, beautifyconf);
}

// locales variable
var locales = {
    administration: {},
    management: {}
};

// declare exports
module.exports = {
    // fs
    fs: require('fs'),

    // beautify
    beautify: beautify,
    beautifyconf: beautifyconf,

    // locales
    languages: [
        'ar', 'da', 'de', 'en', 'fa', 'hr', 
        'it', 'ko', 'pt_BR', 'ru', 'sr', 'tr', 
        'vn', 'cs', 'el_GR', 'es', 'fr', 'hu', 
        'ja', 'nl', 'no', 'pt_PT', 'sk', 'sl', 
        'sr_RS', 'ua', 'zh_CN'
    ],

    // locales
    oldlanguages: [
        'ar', 'de', 'en', 'fa', 'hr', 'it',
        'ko', 'pt_BR', 'ru', 'sr', 'tr', 'vn',
        'cs', 'el_GR', 'es', 'fr', 'hu', 'ja',
        'nl', 'pt_PT', 'sl', 'sr_RS', 'ua', 'zh_CN'
    ],

    basepath: '../app/locales/',
    locales: locales,

    // ext override
    ext: {
        define: function (n, l) {
            delete l.requires;
            delete l.singleton;
            delete l.localization;
            if (n === "CMDBuildUI.locales.Locales") {
                locales.management = flatten(l);
            } else if (n === "CMDBuildUI.locales.LocalesAdministration") {
                locales.administration = flatten(l);
            } else {
                locales[n] = flatten(l);
            }
        }
    },

    clearfn: clearfn,

    // utilities
    flatten: flatten,
    toNested: toNested,
    getLocalesFileContent: getLocalesFileContent,
    getLocalesAdminFileContent: getLocalesAdminFileContent
};
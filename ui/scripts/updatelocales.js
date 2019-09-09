'use strict';
var cmdbuild = require('./tools');

var Ext = cmdbuild.ext;

/**
 * 
 * @param {Object} main 
 * @param {String} classname
 * @param {String} lang
 * @return {Object}
 */
function _updateLocalesObject(main, classname, lang) {
    var clocales = cmdbuild.locales[classname];
    for (var key in main) {
        if (!clocales[key]) {
            if (lang === 'en') {
                clocales[key] = main[key];
            } else {
                clocales[key] = '<em>' + main[key] + '</em>';
            }
            console.log('Added "' + key + '" for "' + lang + '"');
        }
    }
    return cmdbuild.toNested(clocales);
}

function readLocales() {
    cmdbuild.fs.readFile(cmdbuild.basepath + 'Locales.js', 'utf8', function (err, contents) {
        // remove CMDBuildUI variable
        contents = contents.replace('CMDBuildUI.locales.LocalesAdministration.administration', '""');
        eval(contents);
        readLocalesAdmin();
    });
}

function readLocalesAdmin() {
    cmdbuild.fs.readFile(cmdbuild.basepath + 'LocalesAdministration.js', 'utf8', function (err, contents) {
        eval(contents);
        updateLocales();
    });
}

/**
 * Updata Locales.js for language
 * @param {String} lang 
 */
function updateLangLocales(lang) {
    var filename = cmdbuild.basepath + lang + '/Locales.js';
    cmdbuild.fs.readFile(filename, 'utf8', function (err, contents) {
        var classname = 'CMDBuildUI.locales.' + lang + '.Locales';
        if (err) {
            cmdbuild.locales[classname] = {};
        } else {
            contents = contents.replace(/CMDBuildUI\.locales\.\w*\.LocalesAdministration\.administration/, '""');
            // remove clear fn
            contents = contents.replace(cmdbuild.clearfn, "");
            eval(contents);
        }

        // update locales object and get file content
        var updatedlocales = _updateLocalesObject(cmdbuild.locales.management, classname, lang);
        var filecontent = cmdbuild.getLocalesFileContent(lang, classname, updatedlocales);
        cmdbuild.fs.writeFile(filename, filecontent, function (err) {
            if (err) {
                console.log(err);
            }
            console.log("Locales.js file for " + lang + " was saved!");
        });
    });
}
/**
 * Updata LocalesAdministration.js for language
 * @param {String} lang 
 */
function updateLangLocalesAdmin(lang) {
    var filename = cmdbuild.basepath + lang + '/LocalesAdministration.js';
    cmdbuild.fs.readFile(filename, 'utf8', function (err, contents) {
        var classname = 'CMDBuildUI.locales.' + lang + '.LocalesAdministration';
        if (err) {
            cmdbuild.locales[classname] = {};
        } else {
            eval(contents);
        }

        // update locales object and get file content
        var updatedlocales = _updateLocalesObject(cmdbuild.locales.administration, classname, lang);
        var filecontent = cmdbuild.getLocalesAdminFileContent(lang, classname, updatedlocales);
        cmdbuild.fs.writeFile(filename, filecontent, function (err) {
            if (err) {
                console.log(err);
            }
            console.log("LocalesAdministration.js file for " + lang + " was saved!");
        });
    });
}

function updateLocales() {
    cmdbuild.languages.forEach(function (lang) {
        if (!cmdbuild.fs.existsSync(cmdbuild.basepath + lang)) {
            cmdbuild.fs.mkdirSync(cmdbuild.basepath + lang);
        }
        updateLangLocales(lang);
        updateLangLocalesAdmin(lang);
    });
}

readLocales();
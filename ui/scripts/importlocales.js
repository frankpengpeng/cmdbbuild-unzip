'use strict';
var cmdbuild = require('./tools');
const args = require('minimist')(process.argv.slice(2), {
    alias: {
        p: 'path'
    }
});
var Ext = cmdbuild.ext;

function readMapping() {
    cmdbuild.fs.readFile(cmdbuild.basepath + '/mapping.json', 'utf8', function(err, data) {
        cmdbuild.locales.management = JSON.parse(data);
        readMappingAdmin();
    });
}

function readMappingAdmin() {
    cmdbuild.fs.readFile(cmdbuild.basepath + '/mappingAdministration.json', 'utf8', function(err, data) {
        cmdbuild.locales.administration = JSON.parse(data);
        updateTranslations();
    });
}

function updateTranslations() {
    cmdbuild.oldlanguages.forEach(function (lang) {
        if (!cmdbuild.fs.existsSync(cmdbuild.basepath + lang)) {
            cmdbuild.fs.mkdirSync(cmdbuild.basepath + lang);
        }
        // get old locale
        var oldfilename = args.dir + lang + '.json';
        cmdbuild.fs.readFile(oldfilename, 'utf8', function (err, oldcontents) {
            var oldlocales = cmdbuild.flatten(JSON.parse(oldcontents));
            // update new locales
            updateLangLocales(lang, oldlocales);
            updateLangLocalesAdmin(lang, oldlocales);
        });
    });
}

function _updateLocalesObject(mapping, oldlocales, classname, lang) {
    var clocales = cmdbuild.locales[classname];
    for (var key in mapping) {
        var t = oldlocales[mapping[key].replace("CMDBuild.Translation.", "")];
        if (!t) {
            console.log("Error for map " + key + " for lang " + lang);
        }
        clocales[key.replace("CMDBuildUI.locales.Locales.", "")] = t;
    }
    return cmdbuild.toNested(clocales);
}

/**
 * Updata Locales.js for language
 * @param {String} lang 
 * @param {Object} oldlocales
 */
function updateLangLocales(lang, oldlocales) {
    var filename = cmdbuild.basepath + lang + '/Locales.js';
    // read cmdbuild 3 locale file
    cmdbuild.fs.readFile(filename, 'utf8', function (err, contents) {
        var classname = 'CMDBuildUI.locales.' + lang + '.Locales';
        contents = contents.replace(/CMDBuildUI\.locales\.\w*\.LocalesAdministration\.administration/, '""');
        // remove clear fn
        contents = contents.replace(cmdbuild.clearfn, "");
        eval(contents);

        // update locales object and get file content
        var updatedlocales = _updateLocalesObject(cmdbuild.locales.management, oldlocales, classname, lang);
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
 * @param {Object} oldlocales
 */
function updateLangLocalesAdmin(lang, oldlocales) {
    var filename = cmdbuild.basepath + lang + '/LocalesAdministration.js';
    // read cmdbuild 3 locale file
    cmdbuild.fs.readFile(filename, 'utf8', function (err, contents) {
        var classname = 'CMDBuildUI.locales.' + lang + '.LocalesAdministration';
        eval(contents);

        // update locales object and get file content
        var updatedlocales = _updateLocalesObject(cmdbuild.locales.administration, oldlocales, classname, lang);
        var filecontent = cmdbuild.getLocalesAdminFileContent(lang, classname, updatedlocales);
        cmdbuild.fs.writeFile(filename, filecontent, function (err) {
            if (err) {
                console.log(err);
            }
            console.log("LocalesAdministration.js file for " + lang + " was saved!");
        });
    });
}

if (!args.dir) {
    throw "Path is required. Use --dir=/my/directory or -d /my/directory";
}
if (!cmdbuild.fs.existsSync(args.dir)) {
    throw "Directory not found.";
}
if (!args.dir.endsWith("/")) {
    args.dir += "/";
}
readMapping();
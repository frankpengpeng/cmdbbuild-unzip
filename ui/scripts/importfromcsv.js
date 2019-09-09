'use strict';
const cmdbuild = require('./tools');
const csv = require('csvtojson');
var Ext = cmdbuild.ext;

const args = require('minimist')(process.argv.slice(2), {
    string: [
        'dir',      // --dir=/path/where/save/csv/files
        'lang'      // --lang=it
    ],
    alias: {
        d: 'dir',
        l: 'lang'
    }
});

// check arguments
if (!args.dir) {
    throw "Directory is required. Use --dir=/my/directory or -d /my/directory";
}
if (!cmdbuild.fs.existsSync(args.dir)) {
    throw "Directory not found.";
}
if (!args.dir.endsWith("/")) {
    args.dir += "/";
}
if (!args.lang) {
    throw "Language is required. Use --lang=/my/directory or -d /my/directory";
}

const csvconf = {
    noheader: false,
    headers: ['key', 'default', 'value'],
    delimiter: 'auto'
};

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
        importLocales();
    });
}

function importLocales() {
    var lfile = args.dir + 'Locales-' + args.lang + '.csv';
    var lafile = args.dir + 'LocalesAdministration-' + args.lang + '.csv';
    importLangLocales(args.lang, lfile).then(() => {
        importLangLocalesAdmin(args.lang, lafile);
    });
}

/**
 * Updata Locales.js for language
 * @param {String} lang 
 * @param {String} csvfile file fullpath
 */
function importLangLocales(lang, csvfile) {
    return new Promise((resolve, reject) => {
        if (cmdbuild.fs.existsSync(csvfile)) {
            var filename = cmdbuild.basepath + lang + '/Locales.js';
            // read cmdbuild 3 locale file
            cmdbuild.fs.readFile(filename, 'utf8', function (err, contents) {
                var classname = 'CMDBuildUI.locales.' + lang + '.Locales';
                contents = contents.replace(/CMDBuildUI\.locales\.\w*\.LocalesAdministration\.administration/, '""');
                // remove clear fn
                contents = contents.replace(cmdbuild.clearfn, "");
                eval(contents);
        
                csv(csvconf).fromFile(csvfile).then((jsonArray) => {
                    var updated = _getUpdated(classname, jsonArray, cmdbuild.locales.management);
                    var filecontent = cmdbuild.getLocalesFileContent(lang, classname, updated);
                    cmdbuild.fs.writeFile(filename, filecontent, function (err) {
                        if (err) {
                            console.log(err);
                        }
                        console.log("Locales.js file for " + lang + " was saved!");
                        resolve();
                    });
                });
            });
        } else {
            resolve();
        }
    });
}

/**
 * Updata LocalesAdministration.js for language
 * @param {String} lang 
 * @param {Object} oldlocales
 */
function importLangLocalesAdmin(lang, csvfile) {
    return new Promise((resolve, reject) => {
        if (cmdbuild.fs.existsSync(csvfile)) {
            var filename = cmdbuild.basepath + lang + '/LocalesAdministration.js';
            // read cmdbuild 3 locale file
            cmdbuild.fs.readFile(filename, 'utf8', function (err, contents) {
                var classname = 'CMDBuildUI.locales.' + lang + '.LocalesAdministration';
                eval(contents);
        
                csv(csvconf).fromFile(csvfile).then((jsonArray) => {
                    var updated = _getUpdated(classname, jsonArray, cmdbuild.locales.administration);
                    var filecontent = cmdbuild.getLocalesAdminFileContent(lang, classname, updated);
                    cmdbuild.fs.writeFile(filename, filecontent, function (err) {
                        if (err) {
                            console.log(err);
                        }
                        console.log("LocalesAdministration.js file for " + lang + " was saved!");
                        resolve();
                    });
                });
            });
        } else {
            resolve();
        }
    });
}

/**
 * 
 * @param {String} classname 
 * @param {Object[]} jsonArray 
 * @param {Object} main 
 */
function _getUpdated(classname, jsonArray, main) {
    var clocales = cmdbuild.locales[classname];
    jsonArray.forEach(function (record) {
        clocales[record.key] = record.value || '<em>' + main[record.key] + '</em>';
    });
    return cmdbuild.toNested(clocales);
}

readLocales();
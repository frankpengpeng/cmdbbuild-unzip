'use strict';
var cmdbuild = require('./tools');
var Ext = cmdbuild.ext;

const { Parser } = require('json2csv');
const args = require('minimist')(process.argv.slice(2), {
    string: 'dir',     // --dir /path/where/save/csv/files
    alias: {
        d: 'dir'
    }
});

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
        exportLocales();
    });
}

function exportLocales() {
    cmdbuild.languages.forEach(function (lang) {
        if (!cmdbuild.fs.existsSync(cmdbuild.basepath + lang)) {
            cmdbuild.fs.mkdirSync(cmdbuild.basepath + lang);
        }
        exportLangLocales(lang);
        exportLangLocalesAdmin(lang);
    });
}

/**
 * Export Locales.js for language
 * @param {String} lang 
 */
function exportLangLocales(lang) {
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

        _exportData(cmdbuild.locales.management, classname, lang, "Locales");

    });
}

/**
 * Export LocalesAdministration.js for language
 * @param {String} lang 
 */
function exportLangLocalesAdmin(lang) {
    var filename = cmdbuild.basepath + lang + '/LocalesAdministration.js';
    cmdbuild.fs.readFile(filename, 'utf8', function (err, contents) {
        var classname = 'CMDBuildUI.locales.' + lang + '.LocalesAdministration';
        if (err) {
            cmdbuild.locales[classname] = {};
        } else {
            eval(contents);
        }

        _exportData(cmdbuild.locales.administration, classname, lang, "LocalesAdministration");
    });
}
function _exportData(main, classname, lang, csvfilename) {
    var items = [];
    var clocales = cmdbuild.locales[classname];

    for (var key in main) {
        var value = clocales[key];
        if (/^<em>.*<\/em>?/.test(value)) {
            value = '';
        }
        items.push({
            key: key,
            default: main[key],
            value: value
        });
    }

    saveCSV(lang, items, csvfilename);
}

function saveCSV(lang, data, csvfilename) {
    var filename = args.dir + csvfilename + '-' + lang + '.csv';

    var fields = [{
        label: 'Key',
        value: 'key'
      },{
        label: 'Default',
        value: 'default'
      },{
        label: lang.toUpperCase(),
        value: 'value'
      }];

    var json2csvParser = new Parser({ fields, delimiter: ';', withBOM: true });
    var csv = json2csvParser.parse(data);

    cmdbuild.fs.writeFile(filename, csv, function (err) {
        if (err) {
            console.log(err);
        }
        console.log(filename + " was saved!");
    });
}

if (!args.dir) {
    throw "Directory is required. Use --dir=/my/directory or -d /my/directory";
}
if (!cmdbuild.fs.existsSync(args.dir)) {
    throw "Directory not found.";
}
if (!args.dir.endsWith("/")) {
    args.dir += "/";
}
readLocales();
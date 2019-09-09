(function() {
    Ext.define('CMDBuildUI.locales.hu.Locales', {
        "requires": ["CMDBuildUI.locales.hu.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "hu",
        "administration": CMDBuildUI.locales.hu.LocalesAdministration.administration,
        "attachments": {
            "add": "Csatolmány hozzáadása",
            "attachmenthistory": "<em>Attachment History</em>",
            "author": "Szerző",
            "category": "<em>Category</em>",
            "creationdate": "<em>Creation date</em>",
            "deleteattachment": "<em>Delete attachment</em>",
            "deleteattachment_confirmation": "<em>Are you sure you want to delete this attachment?</em>",
            "description": "Leírás",
            "download": "Letöltés",
            "editattachment": "<em>Modify attachment</em>",
            "file": "Fájl",
            "filename": "Fájl neve",
            "majorversion": "<em>Major version</em>",
            "modificationdate": "Módosítás dátuma",
            "uploadfile": "<em>Upload file...</em>",
            "version": "Verzija",
            "viewhistory": "<em>View attachment history</em>"
        },
        "bim": {
            "bimViewer": "<em>Bim Viewer</em>",
            "card": {
                "label": "<em>Card</em>"
            },
            "layers": {
                "label": "<em>Layers</em>",
                "menu": {
                    "hideAll": "<em>Hide all</em>",
                    "showAll": "<em>Show all</em>"
                },
                "name": "<em>Name</em>",
                "qt": "<em>Qt</em>",
                "visibility": "<em>Visibility</em>",
                "visivility": "Láthatóság"
            },
            "menu": {
                "camera": "Kamera",
                "frontView": "<em>Front View</em>",
                "mod": "<em>Viewer controls</em>",
                "orthographic": "<em>Orthographic Camera</em>",
                "pan": "<em>Scroll</em>",
                "perspective": "<em>Perspective Camera</em>",
                "resetView": "<em>Reset View</em>",
                "rotate": "Forgatás",
                "sideView": "<em>Side View</em>",
                "topView": "<em>Top View</em>"
            },
            "showBimCard": "<em>Open 3D viewer</em>",
            "tree": {
                "arrowTooltip": "<em>Select element</em>",
                "columnLabel": "<em>Tree</em>",
                "label": "<em>Tree</em>",
                "open_card": "Kapcsolt kártya megnyitása",
                "root": "<em>Ifc Root</em>"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Kártya hozzáadása a következőhöz: ",
                "clone": "Másolás",
                "clonewithrelations": "<em>Clone card and relations</em>",
                "deletecard": "Kártya törlése",
                "deleteconfirmation": "<em>Are you sure you want to delete this card?</em>",
                "label": "Kártyák",
                "modifycard": "Kártya módosítása",
                "opencard": "<em>Open card</em>",
                "print": "<em>Print card</em>"
            },
            "simple": "Egyszerű",
            "standard": "Általános"
        },
        "common": {
            "actions": {
                "add": "Hozzáadás",
                "apply": "Alkalmaz",
                "cancel": "Mégse",
                "close": "Bezárás",
                "delete": "Törlés",
                "edit": "Szerkesztés",
                "execute": "<em>Execute</em>",
                "refresh": "<em>Refresh data</em>",
                "remove": "Eltávolítás",
                "save": "Mentés",
                "saveandapply": "Mentés és alkalmazás",
                "saveandclose": "<em>Save and close</em>",
                "search": "<em>Search</em>",
                "searchtext": "<em>Search...</em>"
            },
            "attributes": {
                "nogroup": "<em>Base data</em>"
            },
            "dates": {
                "date": "<em>d/m/Y</em>",
                "datetime": "<em>d/m/Y H:i:s</em>",
                "time": "<em>H:i:s</em>"
            },
            "editor": {
                "clearhtml": "<em>Clear HTML</em>"
            },
            "grid": {
                "disablemultiselection": "<em>Disable multi selection</em>",
                "enamblemultiselection": "<em>Enable multi selection</em>",
                "export": "<em>Export data</em>",
                "filterremoved": "<em>The current filter has been removed</em>",
                "import": "<em>Import data</em>",
                "itemnotfound": "A tétel nem található",
                "list": "<em>List</em>",
                "opencontextualmenu": "<em>Open contextual menu</em>",
                "print": "Nyomtatás",
                "printcsv": "<em>Print as CSV</em>",
                "printodt": "<em>Print as ODT</em>",
                "printpdf": "<em>Print as PDF</em>",
                "row": "<em>Item</em>",
                "rows": "<em>Items</em>",
                "subtype": "<em>Subtype</em>"
            },
            "tabs": {
                "activity": "Aktivitás",
                "attachments": "Csatolmányok",
                "card": "Kártya",
                "details": "<em>Details</em>",
                "emails": "<em>Emails</em>",
                "history": "Történet",
                "notes": "Jegyzetek",
                "relations": "Relációk"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Csatolmány hozzáadása DMS-ből",
            "alredyexistfile": "<em>Already exists a file with this name</em>",
            "archivingdate": "Archiválás dátuma",
            "attachfile": "Fájl csatolása",
            "bcc": "Rejtett másolat",
            "cc": "Másolat",
            "composeemail": "E-mail írása",
            "composefromtemplate": "E-mail írása sablonból",
            "delay": "Késleltetés",
            "delays": {
                "day1": "1 nap alatt",
                "days2": "2 nap alatt",
                "days4": "4 nap alatt",
                "hour1": "1 óra alatt",
                "hours2": "2 óra alatt",
                "hours4": "4 óra alatt",
                "month1": "1 hónap alatt",
                "none": "Nincs",
                "week1": "1 hét alatt",
                "weeks2": "2 hét alatt"
            },
            "dmspaneltitle": "Csatolmány választása adatbázisból",
            "edit": "Szerkesztés",
            "from": "Tőle",
            "gridrefresh": "Rács frissítés",
            "keepsynchronization": "Folyamatos sync",
            "message": "<em>Message</em>",
            "regenerateallemails": "Minden e-mail újragenerálása",
            "regenerateemail": "E-mail újragenerálása",
            "remove": "Eltávolítás",
            "remove_confirmation": "<em>Are you sure you want to delete this email?</em>",
            "reply": "Válasz",
            "replyprefix": "<em>On {0}, {1} wrote:</em>",
            "selectaclass": "<em>Select a class</em>",
            "sendemail": "<em>Send e-mail</em>",
            "statuses": {
                "draft": "Piszkozat",
                "outgoing": "Kimenő",
                "received": "Kapott",
                "sent": "Elküldött"
            },
            "subject": "Tárgy",
            "to": "Címzett",
            "view": "Nézet"
        },
        "errors": {
            "autherror": "Hibás felhasználónév vagy jelszó",
            "classnotfound": "Az {0} osztály nem található",
            "notfound": "A tétel nem található"
        },
        "filters": {
            "actions": "<em>Actions</em>",
            "addfilter": "Szűrő hozzáadása",
            "any": "Bármely",
            "attribute": "Válasszon attribútumot",
            "attributes": "Attribútumok",
            "clearfilter": "Szűrő törlése",
            "clone": "Másolás",
            "copyof": "Amelyik másolata",
            "description": "Leírás",
            "domain": "Domain",
            "filterdata": "<em>Filter data</em>",
            "fromselection": "Szelektáltakból",
            "ignore": "<em>Ignore</em>",
            "migrate": "<em>Migrate</em>",
            "name": "Név",
            "newfilter": "<em>New filter</em>",
            "noone": "Semmi",
            "operator": "<em>Operator</em>",
            "operators": {
                "beginswith": "Amivel kezdődik",
                "between": "Között",
                "contained": "Tartalmaz",
                "containedorequal": "Tartalmaz vagy egyenlő",
                "contains": "Tartalmazza",
                "containsorequal": "Tartalmaz vagy egyenlő",
                "different": "Különböző",
                "doesnotbeginwith": "Amivel nem kezdődik ",
                "doesnotcontain": "Nem tartalmazza",
                "doesnotendwith": "Amivel nem fejeződik be",
                "endswith": "Amivel befejeződik",
                "equals": "Egyenlő",
                "greaterthan": "Nagyobb mint",
                "isnotnull": "Nem nulla",
                "isnull": "Nulla",
                "lessthan": "Kisebb mint"
            },
            "relations": "Relációk",
            "type": "Típus",
            "typeinput": "Bemeneti paraméter",
            "value": "Érték"
        },
        "gis": {
            "card": "Kártya",
            "cardsMenu": "<em>Cards Menu</em>",
            "externalServices": "Külső szolgáltatás",
            "geographicalAttributes": "Földrajzi attribútumok",
            "geoserverLayers": "Geoserver rétegek",
            "layers": "Rétegek",
            "list": "Lista",
            "map": "Térkép",
            "mapServices": "<em>Map Services</em>",
            "position": "<em>Position</em>",
            "root": "Root",
            "tree": "Navigációs fa",
            "view": "Nézet",
            "zoom": "Zoom"
        },
        "history": {
            "activityname": "Aktivitás neve",
            "activityperformer": "Aktivitás végzője",
            "begindate": "Kezdő dátum",
            "enddate": "Befejező dátum",
            "processstatus": "Státusz",
            "user": "Felhasználó"
        },
        "importexport": {
            "downloadreport": "<em>Download report</em>",
            "emailfailure": "<em>Error occurred while sending email!</em>",
            "emailsubject": "<em>Import data report</em>",
            "emailsuccess": "<em>The email has been sent successfully!</em>",
            "export": "<em>Export</em>",
            "import": "<em>Import</em>",
            "importresponse": "<em>Import response</em>",
            "response": {
                "created": "<em>Created items</em>",
                "deleted": "<em>Deleted items</em>",
                "errors": "<em>Errors</em>",
                "linenumber": "<em>Line number</em>",
                "message": "<em>Message</em>",
                "modified": "<em>Modified items</em>",
                "processed": "<em>Processed rows</em>",
                "recordnumber": "<em>Record number</em>",
                "unmodified": "<em>Unmodified items</em>"
            },
            "sendreport": "<em>Send report</em>",
            "template": "<em>Template</em>",
            "templatedefinition": "<em>Template definition</em>"
        },
        "login": {
            "buttons": {
                "login": "Bejelentkezés",
                "logout": "<em>Change user</em>"
            },
            "fields": {
                "group": "<em>Group</em>",
                "language": "Nyelv",
                "password": "Jelszó",
                "tenants": "<em>Tenants</em>",
                "username": "Felhasználónév"
            },
            "loggedin": "<em>Logged in</em>",
            "title": "Bejelentkezés",
            "welcome": "<em>Welcome back {0}.</em>"
        },
        "main": {
            "administrationmodule": "Adminisztrációs modul",
            "baseconfiguration": "<em>Base configuration</em>",
            "cardlock": {
                "lockedmessage": "<em>You can't edit this card because {0} is editing it.</em>",
                "someone": "<em>someone</em>"
            },
            "changegroup": "<em>Change group</em>",
            "changepassword": "Jelszó változtatás",
            "changetenant": "<em>Change tenant</em>",
            "confirmchangegroup": "<em>Are you sure you want to change the group?</em>",
            "confirmchangetenants": "<em>Are you sure you want to change active tenants?</em>",
            "confirmdisabletenant": "<em>Are you sure you want to disable \"Ignore tenants\" flag?</em>",
            "confirmenabletenant": "<em>Are you sure you want to enable \"Ignore tenants\" flag?</em>",
            "confirmpassword": "Jelszó megerősítése",
            "ignoretenants": "<em>Ignore tenants</em>",
            "info": "Információ",
            "logo": {
                "cmdbuild": "<em>CMDBuild logo</em>",
                "cmdbuildready2use": "<em>CMDBuild READY2USE logo</em>",
                "companylogo": "<em>Company logo</em>",
                "openmaint": "<em>openMAINT logo</em>"
            },
            "logout": "Kijelentkezés",
            "managementmodule": "Adatkezelő modul",
            "multigroup": "Több csoport",
            "multitenant": "<em>Multi tenant</em>",
            "navigation": "Navigation",
            "newpassword": "Új jelszó",
            "oldpassword": "Eddigi jelszó",
            "pagenotfound": "<em>Page not found</em>",
            "pleasecorrecterrors": "<em>Please correct indicated errors!</em>",
            "preferences": {
                "comma": "<em>Comma</em>",
                "decimalserror": "<em>Decimals field must be present</em>",
                "decimalstousandserror": "<em>Decimals and Thousands separato must be differents</em>",
                "default": "<em>Default</em>",
                "defaultvalue": "Alapértelmezett érték",
                "labeldateformat": "<em>Date format</em>",
                "labeldecimalsseparator": "<em>Decimals separator</em>",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "<em>Language</em>",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "<em>Thousands separator</em>",
                "labeltimeformat": "<em>Time format</em>",
                "msoffice": "<em>Microsoft Office</em>",
                "period": "<em>Period</em>",
                "preferredofficesuite": "<em>Preferred Office suite</em>",
                "space": "<em>Space</em>",
                "thousandserror": "<em>Thousands field must be present</em>",
                "timezone": "<em>Timezone</em>",
                "twelvehourformat": "<em>12-hour format</em>",
                "twentyfourhourformat": "<em>24-hour format</em>"
            },
            "searchinallitems": "<em>Search in all items</em>",
            "userpreferences": "<em>Preferences</em>"
        },
        "menu": {
            "allitems": "<em>All items</em>",
            "classes": "Osztályok",
            "custompages": "<em>Custom pages</em>",
            "dashboards": "<em>Dashboards</em>",
            "processes": "Folyamatok",
            "reports": "<em>Reports</em>",
            "views": "Nézetek"
        },
        "notes": {
            "edit": "Jegyzet szerkesztése"
        },
        "notifier": {
            "attention": "Figyelem",
            "error": "Hiba",
            "genericerror": "<em>Generic error</em>",
            "genericinfo": "<em>Generic info</em>",
            "genericwarning": "<em>Generic warning</em>",
            "info": "Információ",
            "success": "<em>Success</em>",
            "warning": "<em>Warning</em>"
        },
        "patches": {
            "apply": "<em>Apply patches</em>",
            "category": "<em>Category</em>",
            "description": "<em>Description</em>",
            "name": "<em>Name</em>",
            "patches": "<em>Patches</em>"
        },
        "processes": {
            "abortconfirmation": "Biztos hogy meg akarja szakítani a folyamatot?",
            "abortprocess": "Folyamat megszakítása",
            "action": {
                "advance": "Tovább",
                "label": "<em>Action</em>"
            },
            "activeprocesses": "<em>Active processes</em>",
            "allstatuses": "<em>All</em>",
            "editactivity": "Aktivitás szerkesztése",
            "openactivity": "<em>Open activity</em>",
            "startworkflow": "Indítás",
            "workflow": "Folyamat"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Kártya",
            "cardList": "<em>Card List</em>",
            "cardRelation": "Reláció",
            "cardRelations": "Reláció",
            "choosenaviagationtree": "<em>Choose navigation tree</em>",
            "class": "<em>Class</em>",
            "class:": "Osztály",
            "classList": "<em>Class List</em>",
            "compoundnode": "<em>Compound Node</em>",
            "enableTooltips": "<em>Enable/disable tooltips on graph</em>",
            "level": "<em>Level</em>",
            "openRelationGraph": "Relációs gráf megnyitása",
            "qt": "<em>Qt</em>",
            "refresh": "<em>Refresh</em>",
            "relation": "Reláció",
            "relationGraph": "Relációs gráf",
            "reopengraph": "<em>Reopen the graph from this node</em>"
        },
        "relations": {
            "adddetail": "Részlet hozzáadása",
            "addrelations": "Reláció hozzáadása",
            "attributes": "Attribútumok",
            "code": "<em>Code</em>",
            "deletedetail": "Részlet törlése",
            "deleterelation": "Reláció törlése",
            "description": "Leírás",
            "editcard": "Kártya módosítása",
            "editdetail": "Részlet szerkesztése",
            "editrelation": "Reláció szerkesztése",
            "mditems": "<em>items</em>",
            "opencard": "Kapcsolt kártya megnyitása",
            "opendetail": "Részlet megjelenítése",
            "type": "Típus"
        },
        "reports": {
            "csv": "<em>CSV</em>",
            "download": "Letöltés",
            "format": "Formátum",
            "odt": "<em>ODT</em>",
            "pdf": "<em>PDF</em>",
            "print": "Nyomtatás",
            "reload": "Újratöltés",
            "rtf": "<em>RTF</em>"
        },
        "thematism": {
            "addThematism": "<em>Add Thematism</em>",
            "analysisType": "<em>Analysis Type</em>",
            "attribute": "<em>Attribute</em>",
            "calculateRules": "<em>Generate style rules</em>",
            "clearThematism": "<em>Clear Thematism</em>",
            "color": "<em>Color</em>",
            "defineLegend": "<em>Legend definition</em>",
            "defineThematism": "<em>Thematism definition</em>",
            "function": "<em>Function</em>",
            "generate": "<em>generate</em>",
            "geoAttribute": "<em>geoAttribute</em>",
            "graduated": "<em>Graduated</em>",
            "highlightSelected": "<em>Highlight selected item</em>",
            "intervals": "<em>Intervals</em>",
            "legend": "<em>legend</em>",
            "name": "<em>name</em>",
            "newThematism": "<em>New Thematism</em>",
            "punctual": "<em>Punctual</em>",
            "quantity": "<em>Quantity</em>",
            "source": "<em>Source</em>",
            "table": "<em>Table</em>",
            "thematism": "<em>Thematisms</em>",
            "value": "<em>Value</em>"
        },
        "widgets": {
            "customform": {
                "addrow": "Sor hozzáadása",
                "clonerow": "<em>Clone row</em>",
                "deleterow": "Sor törlése",
                "editrow": "Sor szerkesztése",
                "export": "Exportálás",
                "import": "<em>Import</em>",
                "refresh": "<em>Refresh to defaults</em>"
            },
            "linkcards": {
                "editcard": "<em>Edit card</em>",
                "opencard": "<em>Open card</em>",
                "refreshselection": "<em>Apply default selection</em>",
                "togglefilterdisabled": "Rács szűrő tiltása",
                "togglefilterenabled": "<em>Rács szűrő engedélyezése</em>"
            }
        }
    });

    function cleardata(obj) {
        for (var key in obj) {
            if (typeof obj[key] === "string") {
                obj[key] = obj[key].replace(/^<em>(.+)<\/em>$/, "$1");
            } else if (typeof obj[key] === "object") {
                cleardata(obj[key]);
            }
        }
    }
    cleardata(CMDBuildUI.locales.Locales);
})();
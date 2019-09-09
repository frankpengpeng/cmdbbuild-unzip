(function() {
    Ext.define('CMDBuildUI.locales.cs.Locales', {
        "requires": ["CMDBuildUI.locales.cs.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "cs",
        "administration": CMDBuildUI.locales.cs.LocalesAdministration.administration,
        "attachments": {
            "add": "Nová příloha",
            "attachmenthistory": "<em>Attachment History</em>",
            "author": "Autor",
            "category": "Kategorie",
            "creationdate": "<em>Creation date</em>",
            "deleteattachment": "<em>Delete attachment</em>",
            "deleteattachment_confirmation": "<em>Are you sure you want to delete this attachment?</em>",
            "description": "Popis",
            "download": "Uložit",
            "editattachment": "Upravit přílohu",
            "file": "Soubor",
            "filename": "Jméno souboru",
            "majorversion": "Hlavní verze",
            "modificationdate": "Datum změny",
            "uploadfile": "<em>Upload file...</em>",
            "version": "Verze",
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
                "visivility": "Viditelnost"
            },
            "menu": {
                "camera": "Kamera",
                "frontView": "<em>Front View</em>",
                "mod": "<em>Viewer controls</em>",
                "orthographic": "<em>Orthographic Camera</em>",
                "pan": "Panorama",
                "perspective": "<em>Perspective Camera</em>",
                "resetView": "<em>Reset View</em>",
                "rotate": "Otočit",
                "sideView": "<em>Side View</em>",
                "topView": "<em>Top View</em>"
            },
            "showBimCard": "<em>Open 3D viewer</em>",
            "tree": {
                "arrowTooltip": "<em>Select element</em>",
                "columnLabel": "<em>Tree</em>",
                "label": "<em>Tree</em>",
                "open_card": "Otevřít navázaný záznam",
                "root": "<em>Ifc Root</em>"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Nový záznam",
                "clone": "Klonovat",
                "clonewithrelations": "<em>Clone card and relations</em>",
                "deletecard": "Odstranit záznam",
                "deleteconfirmation": "<em>Are you sure you want to delete this card?</em>",
                "label": "Záznamy ve třídách",
                "modifycard": "Upravit záznam",
                "opencard": "<em>Open card</em>",
                "print": "<em>Print card</em>"
            },
            "simple": "Jednoduchá",
            "standard": "Standardní"
        },
        "common": {
            "actions": {
                "add": "Nový",
                "apply": "Použít",
                "cancel": "Storno",
                "close": "Zavřít",
                "delete": "Odstranit",
                "edit": "Upravit",
                "execute": "<em>Execute</em>",
                "refresh": "<em>Refresh data</em>",
                "remove": "Odstranit",
                "save": "Uložit",
                "saveandapply": "Uložit a použít",
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
                "itemnotfound": "Položka nebyla nalezena",
                "list": "<em>List</em>",
                "opencontextualmenu": "<em>Open contextual menu</em>",
                "print": "Tisknout",
                "printcsv": "<em>Print as CSV</em>",
                "printodt": "<em>Print as ODT</em>",
                "printpdf": "<em>Print as PDF</em>",
                "row": "<em>Item</em>",
                "rows": "<em>Items</em>",
                "subtype": "<em>Subtype</em>"
            },
            "tabs": {
                "activity": "Krok procesu",
                "attachments": "Přílohy",
                "card": "Záznam",
                "details": "Detaily",
                "emails": "<em>Emails</em>",
                "history": "Historie",
                "notes": "Poznámky",
                "relations": "Vazby"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Nové přílohy z DMS",
            "alredyexistfile": "<em>Already exists a file with this name</em>",
            "archivingdate": "Datum archivace",
            "attachfile": "Přiložit soubor",
            "bcc": "Slepá kopie (BCC)",
            "cc": "Kopie (CC)",
            "composeemail": "Vytvořit e-mail",
            "composefromtemplate": "Vytvořit z šablony",
            "delay": "Prodleva odeslání",
            "delays": {
                "day1": "1 den",
                "days2": "2 dny",
                "days4": "4 dny",
                "hour1": "1 hodinu",
                "hours2": "2 hodiny",
                "hours4": "4 hodiny",
                "month1": "1 měsíc",
                "none": "Žádná",
                "week1": "1 týden",
                "weeks2": "2 týdny"
            },
            "dmspaneltitle": "Vyberte přílohy z databáze",
            "edit": "Upravit",
            "from": "Od",
            "gridrefresh": "Obnovit mřížku",
            "keepsynchronization": "Udržovat aktuální",
            "message": "Zpráva",
            "regenerateallemails": "Obnovit všechny e-maily",
            "regenerateemail": "Obnovit e-mail",
            "remove": "Odstranit",
            "remove_confirmation": "<em>Are you sure you want to delete this email?</em>",
            "reply": "Odpovědět",
            "replyprefix": "<em>On {0}, {1} wrote:</em>",
            "selectaclass": "<em>Select a class</em>",
            "sendemail": "Odeslat zprávu",
            "statuses": {
                "draft": "Koncepty",
                "outgoing": "K odeslání",
                "received": "Doručeno",
                "sent": "Odesláno"
            },
            "subject": "Předmět",
            "to": "Komu",
            "view": "Pohledy"
        },
        "errors": {
            "autherror": "Chybné uživatelské jméno nebo heslo",
            "classnotfound": "Třída {0} nebyla nalezena",
            "notfound": "Položka nebyla nalezena"
        },
        "filters": {
            "actions": "<em>Actions</em>",
            "addfilter": "Nový filtr",
            "any": "Jakýkoliv",
            "attribute": "Vyberte atribut",
            "attributes": "Atributy",
            "clearfilter": "Zrušit filtr",
            "clone": "Klonovat",
            "copyof": "Kopírovat",
            "description": "Popis",
            "domain": "Doména",
            "filterdata": "<em>Filter data</em>",
            "fromselection": "Z výběru",
            "ignore": "<em>Ignore</em>",
            "migrate": "<em>Migrate</em>",
            "name": "Název",
            "newfilter": "<em>New filter</em>",
            "noone": "Nikdo",
            "operator": "<em>Operator</em>",
            "operators": {
                "beginswith": "Začíná",
                "between": "Mezi",
                "contained": "Je obsažen",
                "containedorequal": "Je obsažen nebo se rovná ",
                "contains": "Obsahuje",
                "containsorequal": "Obsahuje nebo se rovná",
                "different": "Odlišný",
                "doesnotbeginwith": "Nezačíná",
                "doesnotcontain": "Neobsahuje",
                "doesnotendwith": "Nekončí",
                "endswith": "Končí",
                "equals": "Rovná se",
                "greaterthan": "Více než",
                "isnotnull": "Je vyplněno",
                "isnull": "Není vyplněno",
                "lessthan": "Méně než"
            },
            "relations": "Vazby",
            "type": "Typ",
            "typeinput": "<em>Input Parameter</em>",
            "value": "Hodnota"
        },
        "gis": {
            "card": "Záznam",
            "cardsMenu": "<em>Cards Menu</em>",
            "externalServices": "Externí služby",
            "geographicalAttributes": "Zeměpisné atributy",
            "geoserverLayers": "Vrstvy geoserveru",
            "layers": "Vrstvy",
            "list": "Seznam",
            "map": "Mapa",
            "mapServices": "<em>Map Services</em>",
            "position": "Pozice",
            "root": "Kořen",
            "tree": "Navigační strom",
            "view": "Pohledy",
            "zoom": "Měřítko"
        },
        "history": {
            "activityname": "Název kroku procesu",
            "activityperformer": "Realizátor kroku procesu",
            "begindate": "Datum počástku platnosti",
            "enddate": "Datum konce platnosti",
            "processstatus": "Stav",
            "user": "Uživatel"
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
                "login": "Přihlásit",
                "logout": "<em>Change user</em>"
            },
            "fields": {
                "group": "<em>Group</em>",
                "language": "Jazyk",
                "password": "Heslo",
                "tenants": "<em>Tenants</em>",
                "username": "Uživatelské jméno"
            },
            "loggedin": "<em>Logged in</em>",
            "title": "Přihlášení",
            "welcome": "<em>Welcome back {0}.</em>"
        },
        "main": {
            "administrationmodule": "Administrační modul",
            "baseconfiguration": "<em>Base configuration</em>",
            "cardlock": {
                "lockedmessage": "<em>You can't edit this card because {0} is editing it.</em>",
                "someone": "<em>someone</em>"
            },
            "changegroup": "<em>Change group</em>",
            "changepassword": "Změnit heslo",
            "changetenant": "<em>Change tenant</em>",
            "confirmchangegroup": "<em>Are you sure you want to change the group?</em>",
            "confirmchangetenants": "<em>Are you sure you want to change active tenants?</em>",
            "confirmdisabletenant": "<em>Are you sure you want to disable \"Ignore tenants\" flag?</em>",
            "confirmenabletenant": "<em>Are you sure you want to enable \"Ignore tenants\" flag?</em>",
            "confirmpassword": "Opakovat heslo",
            "ignoretenants": "<em>Ignore tenants</em>",
            "info": "Info",
            "logo": {
                "cmdbuild": "<em>CMDBuild logo</em>",
                "cmdbuildready2use": "<em>CMDBuild READY2USE logo</em>",
                "companylogo": "<em>Company logo</em>",
                "openmaint": "<em>openMAINT logo</em>"
            },
            "logout": "Odhlásit",
            "managementmodule": "Modul pro správu dat",
            "multigroup": "Vícenásobná role",
            "multitenant": "<em>Multi tenant</em>",
            "navigation": "Navigace",
            "newpassword": "Nové heslo",
            "oldpassword": "Původní heslo",
            "pagenotfound": "<em>Page not found</em>",
            "pleasecorrecterrors": "<em>Please correct indicated errors!</em>",
            "preferences": {
                "comma": "<em>Comma</em>",
                "decimalserror": "<em>Decimals field must be present</em>",
                "decimalstousandserror": "<em>Decimals and Thousands separato must be differents</em>",
                "default": "<em>Default</em>",
                "defaultvalue": "Implicitní hodnota",
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
            "classes": "Třídy",
            "custompages": "Uživatelské stránky",
            "dashboards": "<em>Dashboards</em>",
            "processes": "Procesy",
            "reports": "<em>Reports</em>",
            "views": "Pohledy"
        },
        "notes": {
            "edit": "Upravit poznámku"
        },
        "notifier": {
            "attention": "Pozor",
            "error": "Chyba",
            "genericerror": "<em>Generic error</em>",
            "genericinfo": "<em>Generic info</em>",
            "genericwarning": "<em>Generic warning</em>",
            "info": "Info",
            "success": "Provedeno úspěšně",
            "warning": "Varování"
        },
        "patches": {
            "apply": "<em>Apply patches</em>",
            "category": "<em>Category</em>",
            "description": "<em>Description</em>",
            "name": "<em>Name</em>",
            "patches": "<em>Patches</em>"
        },
        "processes": {
            "abortconfirmation": "Opravdu si přejete přerušit tento proces?",
            "abortprocess": "Přerušit proces",
            "action": {
                "advance": "Pokročit",
                "label": "<em>Action</em>"
            },
            "activeprocesses": "<em>Active processes</em>",
            "allstatuses": "Všechny",
            "editactivity": "Upravit krok",
            "openactivity": "<em>Open activity</em>",
            "startworkflow": "Spustit",
            "workflow": "Proces"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Záznam",
            "cardList": "<em>Card List</em>",
            "cardRelation": "Vazba",
            "cardRelations": "Vazba",
            "choosenaviagationtree": "<em>Choose navigation tree</em>",
            "class": "<em>Class</em>",
            "class:": "Třída",
            "classList": "<em>Class List</em>",
            "compoundnode": "<em>Compound Node</em>",
            "enableTooltips": "<em>Enable/disable tooltips on graph</em>",
            "level": "<em>Level</em>",
            "openRelationGraph": "Otevřít graf vazeb",
            "qt": "<em>Qt</em>",
            "refresh": "<em>Refresh</em>",
            "relation": "Vazba",
            "relationGraph": "Graf vazeb",
            "reopengraph": "<em>Reopen the graph from this node</em>"
        },
        "relations": {
            "adddetail": "Nový detail",
            "addrelations": "Nové vazby",
            "attributes": "Atributy",
            "code": "Kód",
            "deletedetail": "Odstranit detail",
            "deleterelation": "Odstranit vazbu",
            "description": "Popis",
            "editcard": "Upravit záznam",
            "editdetail": "Upravit detail",
            "editrelation": "Upravit vazbu",
            "mditems": "<em>items</em>",
            "opencard": "Otevřít navázaný záznam",
            "opendetail": "Zobrazit detail",
            "type": "Typ"
        },
        "reports": {
            "csv": "<em>CSV</em>",
            "download": "Uložit",
            "format": "Formát",
            "odt": "<em>ODT</em>",
            "pdf": "<em>PDF</em>",
            "print": "Tisknout",
            "reload": "Načíst znovu",
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
                "addrow": "Nový řádek",
                "clonerow": "Klonovat řádek",
                "deleterow": "Odstranit řádek",
                "editrow": "Upravit řádek",
                "export": "Export",
                "import": "Import",
                "refresh": "<em>Refresh to defaults</em>"
            },
            "linkcards": {
                "editcard": "<em>Edit card</em>",
                "opencard": "<em>Open card</em>",
                "refreshselection": "Použít implicitní výběr",
                "togglefilterdisabled": "Zakázat filtr v mřížkách",
                "togglefilterenabled": "Povolit filtr v mřížkách"
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
(function() {
    Ext.define('CMDBuildUI.locales.da.Locales', {
        "requires": ["CMDBuildUI.locales.da.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "da",
        "administration": CMDBuildUI.locales.da.LocalesAdministration.administration,
        "attachments": {
            "add": "Tilføj vedhæftede filer",
            "attachmenthistory": "Vedhæftningsoversigt",
            "author": "Forfatter",
            "category": "Kategori",
            "creationdate": "Oprettelsesdato",
            "deleteattachment": "Slet vedhæftet fil",
            "deleteattachment_confirmation": "Er du sikker på, at du vil slette denne vedhæftede fil?",
            "description": "Beskrivelse",
            "download": "Hent",
            "editattachment": "Ændr vedhæftet fil",
            "file": "Fil",
            "filename": "filnavn",
            "majorversion": "Nyeste version",
            "modificationdate": "Ændringsdato",
            "uploadfile": "Upload fil...",
            "version": "Version",
            "viewhistory": "Se vedhæftelseshistorik"
        },
        "bim": {
            "bimViewer": "Bim Viewer",
            "card": {
                "label": "Kort"
            },
            "layers": {
                "label": "Lag",
                "menu": {
                    "hideAll": "Skjul alle",
                    "showAll": "Vis alle"
                },
                "name": "Navn",
                "qt": "Antal",
                "visibility": "Synlighed"
            },
            "menu": {
                "camera": "Kamera",
                "frontView": "Forfra visning",
                "mod": "Viewer kontroller",
                "orthographic": "Ortografisk kamera",
                "pan": "Rulle op/ned",
                "perspective": "perspektiv kamera",
                "resetView": "Nulstil visning",
                "rotate": "rotere",
                "sideView": "Side visning",
                "topView": "Ovnfra visning"
            },
            "showBimCard": "Åbn 3D-viewer",
            "tree": {
                "arrowTooltip": "Vælg element",
                "columnLabel": "Filstruktur",
                "label": "Filstruktur",
                "open_card": "Åbn relateret kort",
                "root": "Ifc Root"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Tilføj kort",
                "clone": "Duplikere",
                "clonewithrelations": "Duplikere kort og relationer",
                "deletecard": "Slet kort",
                "deleteconfirmation": "Er du sikker på, at du vil slette dette kort?",
                "label": "Kort",
                "modifycard": "Ændre kort",
                "opencard": "Åbn kort",
                "print": "Print kort"
            },
            "simple": "Enkel",
            "standard": "Standard"
        },
        "common": {
            "actions": {
                "add": "Tilføj",
                "apply": "Anvend",
                "cancel": "Annullere",
                "close": "Luk",
                "delete": "Slet",
                "edit": "Redigere",
                "execute": "Udfør",
                "refresh": "Opdater data",
                "remove": "Fjern",
                "save": "Gem",
                "saveandapply": "Gem og anvend",
                "saveandclose": "Gem og luk",
                "search": "Søg",
                "searchtext": "Søg…"
            },
            "attributes": {
                "nogroup": "Basis data"
            },
            "dates": {
                "date": "dd/mm/åå",
                "datetime": "dd/mm/åå tt:mm:ss",
                "time": "tt;mm:ss"
            },
            "editor": {
                "clearhtml": "Slet HTML"
            },
            "grid": {
                "disablemultiselection": "Deaktiver multi-valg",
                "enamblemultiselection": "Aktivér multi-valg",
                "export": "Eksport data",
                "filterremoved": "Det nuværende filter er blevet fjernet",
                "import": "Import data",
                "itemnotfound": "Emnet blev ikke fundet",
                "list": "List",
                "opencontextualmenu": "Åbn kontekstmenu",
                "print": "Print",
                "printcsv": "Print som CSV",
                "printodt": "Print som ODT",
                "printpdf": "Print som PDF",
                "row": "Undertype",
                "rows": "Emne",
                "subtype": "Undertype"
            },
            "tabs": {
                "activity": "Aktivitet",
                "attachments": "Vedhæftede filer",
                "card": "Kort",
                "details": "Detaljer",
                "emails": "E-mails",
                "history": "Historik",
                "notes": "Noter",
                "relations": "Relationer"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Vedhæft filer fra DMS",
            "alredyexistfile": "Der findes allerede en fil med dette navn",
            "archivingdate": "Arkiveringsdato",
            "attachfile": "Vedhæft fil",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "Opret e-mail",
            "composefromtemplate": "Opret fra skabelon",
            "delay": "Forsinke",
            "delays": {
                "day1": "In 1 day",
                "days2": "Om 2 dage",
                "days4": "Om 4 dage",
                "hour1": "1 time",
                "hours2": "2 timer",
                "hours4": "4 timer",
                "month1": "om 1 måned",
                "none": "Ingen",
                "week1": "Om 1 uge",
                "weeks2": "Om 2 uger"
            },
            "dmspaneltitle": "Vælg vedhæftede filer fra databasen",
            "edit": "Redigerer",
            "from": "Fra",
            "gridrefresh": "Gitter opdatering",
            "keepsynchronization": "Forsøg synkronisering",
            "message": "Besked",
            "regenerateallemails": "Regenerér alle e-mails",
            "regenerateemail": "Regenerere e-mail",
            "remove": "Fjern",
            "remove_confirmation": "Er du sikker på, at du vil slette denne email?",
            "reply": "svar",
            "replyprefix": "På {0} skrev {1}:",
            "selectaclass": "Vælg en klasse",
            "sendemail": "Send e-mail",
            "statuses": {
                "draft": "Klade",
                "outgoing": "Udgående",
                "received": "Modtaget",
                "sent": "Sendt"
            },
            "subject": "Emne",
            "to": "Til",
            "view": "Oversigt"
        },
        "errors": {
            "autherror": "Forkert brugernavn eller adgangskode",
            "classnotfound": "Klasse {0} ikke fundet",
            "notfound": "Item blev ikke fundet"
        },
        "filters": {
            "actions": "Handlinger",
            "addfilter": "Tilføj filter",
            "any": "Nogen",
            "attribute": "Vælg en egenskab",
            "attributes": "Egenskaber",
            "clearfilter": "Ryd filter",
            "clone": "Duplikere",
            "copyof": "Kopi af",
            "description": "Beskrivelse",
            "domain": "Domæne",
            "filterdata": "Filtrer data",
            "fromselection": "Fra markering",
            "ignore": "Ignorere",
            "migrate": "Migrerer",
            "name": "Navn",
            "newfilter": "Ny filter",
            "noone": "Ingen",
            "operator": "Operatør",
            "operators": {
                "beginswith": "Start med",
                "between": "Mellem",
                "contained": "Indeholdt",
                "containedorequal": "Indeholder eller lige",
                "contains": "Indeholder",
                "containsorequal": "Indeholder eller lige",
                "different": "Forskellige",
                "doesnotbeginwith": "Begynder ikke med",
                "doesnotcontain": "Indeholder ikke",
                "doesnotendwith": "Slutter ikke med",
                "endswith": "Slutter med",
                "equals": "Lige med",
                "greaterthan": "større end",
                "isnotnull": "Er ikke null",
                "isnull": "Er null",
                "lessthan": "Mindre end"
            },
            "relations": "Relationer",
            "type": "Type",
            "typeinput": "Input Parameter",
            "value": "Værdi"
        },
        "gis": {
            "card": "Kort",
            "cardsMenu": "Kort Menu",
            "externalServices": "Eksterne tjenester",
            "geographicalAttributes": "Geografiske egenskab",
            "geoserverLayers": "Geoserver lag",
            "layers": "Lag",
            "list": "List",
            "map": "Kort",
            "mapServices": "Korttjenester",
            "position": "Position",
            "root": "Rod",
            "tree": "Filstruktur",
            "view": "Oversigt",
            "zoom": "Zoom"
        },
        "history": {
            "activityname": "Aktivitets navn",
            "activityperformer": "Aktivitet udfører",
            "begindate": "Start dato",
            "enddate": "Slut dato",
            "processstatus": "Status",
            "user": "Bruger"
        },
        "importexport": {
            "downloadreport": "Hent rapport",
            "emailfailure": "Der opstod en fejl under afsendelse af e-mail!",
            "emailsubject": "Import rapport data",
            "emailsuccess": "E-mailen er blevet sendt med succes!",
            "export": "Eksport",
            "import": "Import",
            "importresponse": "Import svar",
            "response": {
                "created": "Oprettede varer/enhed",
                "deleted": "Slet varer/enhed",
                "errors": "Fejl",
                "linenumber": "Linjenummer",
                "message": "Besked",
                "modified": "Ændrede elementer",
                "processed": "Behandlede rækker",
                "recordnumber": "rekordnumre",
                "unmodified": "Uændrede elementer"
            },
            "sendreport": "Send rapport",
            "template": "Skabelon",
            "templatedefinition": "Skabelon definition"
        },
        "login": {
            "buttons": {
                "login": "Log på",
                "logout": "Skift bruger"
            },
            "fields": {
                "group": "Gruppe",
                "language": "Sprog",
                "password": "Kodeord",
                "tenants": "Tenants",
                "username": "Brugernavn"
            },
            "loggedin": "Logget ind",
            "title": "Log på",
            "welcome": "Velkommen tilbage {0}."
        },
        "main": {
            "administrationmodule": "Administrationsmodul",
            "baseconfiguration": "Basis konfiguration",
            "cardlock": {
                "lockedmessage": "Du kan ikke redigere dette kort, fordi {0} redigerer i det.",
                "someone": "nogen"
            },
            "changegroup": "Skift gruppe",
            "changepassword": "Skift kodeord",
            "changetenant": "Skift tenant",
            "confirmchangegroup": "Er du sikker på, at du vil ændre gruppen?",
            "confirmchangetenants": "Er du sikker på, at du vil ændre de aktive tenants?",
            "confirmdisabletenant": "Er du sikker på, at du vil indaktivere \"Ignorere tenants\" flag?",
            "confirmenabletenant": "Er du sikker på, at du vil aktivere \"Ignorere tenants\" flag?",
            "confirmpassword": "Godkend kodeord",
            "ignoretenants": "Indaktivere tenants",
            "info": "Info",
            "logo": {
                "cmdbuild": "CMDBuild logo",
                "cmdbuildready2use": "CMDBuild READY2USE logo",
                "companylogo": "Virksomheds logo",
                "openmaint": "openMAINT logo"
            },
            "logout": "log ud",
            "managementmodule": "Datastyringsmodul",
            "multigroup": "Multi-gruppe",
            "multitenant": "Multi-tenant",
            "navigation": "Navigation",
            "newpassword": "Ny kodeord",
            "oldpassword": "Tidligere kodeord",
            "pagenotfound": "Siden blev ikke fundet",
            "pleasecorrecterrors": "Ret venligst angivne fejl!",
            "preferences": {
                "comma": "Komma",
                "decimalserror": "Decimalfelt skal være til stede",
                "decimalstousandserror": "Decimal og tusind separator skal være forskellige",
                "default": "Standard",
                "defaultvalue": "Standard værdi",
                "labeldateformat": "Datoformat",
                "labeldecimalsseparator": "Decimal separator",
                "labellanguage": "Sprog",
                "labelthousandsseparator": "Tusind separator",
                "labeltimeformat": "Tidsformat",
                "msoffice": "Microsoft Office",
                "period": "Periode",
                "preferredofficesuite": "Foretrukken Office-pakke",
                "space": "Plads",
                "thousandserror": "Tusindfelt skal være til stede",
                "timezone": "Tidszone",
                "twelvehourformat": "12-timers format",
                "twentyfourhourformat": "24-timers format"
            },
            "searchinallitems": "Søg i alle emner",
            "userpreferences": "Indstillinger"
        },
        "menu": {
            "allitems": "Alle Emner",
            "classes": "Klasser",
            "custompages": "Brugerdefinerede sider",
            "dashboards": "Dashboards",
            "processes": "Processer",
            "reports": "Rapporter",
            "views": "Oversigt"
        },
        "notes": {
            "edit": "Ændr noter"
        },
        "notifier": {
            "attention": "Bemærk",
            "error": "Fejl",
            "genericerror": "Generisk fejl",
            "genericinfo": "Generisk info",
            "genericwarning": "Generisk advarsel",
            "info": "Info",
            "success": "Succes",
            "warning": "Advarsel"
        },
        "patches": {
            "apply": "Påfør patches",
            "category": "Kategori",
            "description": "Beskrivelse",
            "name": "Navn",
            "patches": "Patches"
        },
        "processes": {
            "abortconfirmation": "Er du sikker på, at du vil afbryde denne proces?",
            "abortprocess": "Afbryd processen",
            "action": {
                "advance": "Fortsæt",
                "label": "Handling"
            },
            "activeprocesses": "Aktivitets processer",
            "allstatuses": "Alle",
            "editactivity": "Ændr aktivitet",
            "openactivity": "Åbn aktivitet",
            "startworkflow": "Start",
            "workflow": "Arbejdsgang"
        },
        "relationGraph": {
            "activity": "Aktivitet",
            "card": "Kort",
            "cardList": "Kortlist",
            "cardRelations": "Kortrelationer",
            "choosenaviagationtree": "Vælg navigationstræ",
            "class": "Klasse",
            "classList": "Klass-liste",
            "compoundnode": "Forbindelsesnode",
            "enableTooltips": "Aktivér / deaktiver værktøjstip på grafen",
            "level": "Niveau",
            "openRelationGraph": "Open Relation Graph",
            "qt": "Antal",
            "refresh": "Opdater",
            "relation": "forhold",
            "relationGraph": "Relation Graph",
            "reopengraph": "Genåbn grafen fra dette punkt"
        },
        "relations": {
            "adddetail": "Tilføj kort",
            "addrelations": "Tilføj detaljer",
            "attributes": "Egenskaber",
            "code": "Kode",
            "deletedetail": "Slet detaljer",
            "deleterelation": "Slet relation",
            "description": "Beskrivelse",
            "editcard": "Redigere kort",
            "editdetail": "Redigere detaljer",
            "editrelation": "Redigere relation",
            "mditems": "Emner",
            "opencard": "Åbn relateret kort",
            "opendetail": "Vis detaljer",
            "type": "Type"
        },
        "reports": {
            "csv": "CSV",
            "download": "Hent",
            "format": "Format",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Print",
            "reload": "Opdater",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "<em>Add Thematism</em>",
            "analysisType": "Analyse Type",
            "attribute": "Egenskab",
            "calculateRules": "<em>Generate style rules</em>",
            "clearThematism": "<em>Clear Thematism</em>",
            "color": "Farve",
            "defineLegend": "<em>Legend definition</em>",
            "defineThematism": "<em>Thematism definition</em>",
            "function": "funktion",
            "generate": "Generere",
            "geoAttribute": "<em>geoAttribute</em>",
            "graduated": "Gradueret",
            "highlightSelected": "Fremhæv markeret element",
            "intervals": "intervaller",
            "legend": "legende",
            "name": "<em>name</em>",
            "newThematism": "<em>New Thematism</em>",
            "punctual": "Punktlig",
            "quantity": "Quantity",
            "source": "Kilde",
            "table": "Tabel",
            "thematism": "Thematisms",
            "value": "Værdi"
        },
        "widgets": {
            "customform": {
                "addrow": "Tilføj række",
                "clonerow": "Dubliker række",
                "deleterow": "Slet række",
                "editrow": "Rediger række",
                "export": "Eksport",
                "import": "Import",
                "refresh": "Andvend standardindstillinger"
            },
            "linkcards": {
                "editcard": "Redigere kort",
                "opencard": "Åben kort",
                "refreshselection": "Anvend standardvalg",
                "togglefilterdisabled": "Aktivér gitterfilter",
                "togglefilterenabled": "Deaktiver gitterfilter"
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
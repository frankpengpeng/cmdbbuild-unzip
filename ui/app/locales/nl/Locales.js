(function() {
    Ext.define('CMDBuildUI.locales.nl.Locales', {
        "requires": ["CMDBuildUI.locales.nl.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "nl",
        "administration": CMDBuildUI.locales.nl.LocalesAdministration.administration,
        "attachments": {
            "add": "Bijlage toevoegen",
            "attachmenthistory": "Bijlage geschiedenis",
            "author": "Schrijver",
            "category": "Categorie",
            "creationdate": "Aanmaak datum",
            "deleteattachment": "Verwijder bijlage",
            "deleteattachment_confirmation": "Weet  u zeker dat u deze bijlage wilt verwijderen?",
            "description": "Omschrijving",
            "download": "Ophalen",
            "editattachment": "Modificeer bijlage",
            "file": "Bestand",
            "filename": "Bestandsnaam",
            "majorversion": "Hoofd versie",
            "modificationdate": "Modificatie datum",
            "uploadfile": "Ophalen bestand…",
            "version": "Versie",
            "viewhistory": "Toon bijlage geschiedenis"
        },
        "bim": {
            "bimViewer": "Bim afbeeldingsweergave",
            "card": {
                "label": "Kaart"
            },
            "layers": {
                "label": "Lagen",
                "menu": {
                    "hideAll": "Verberg alles",
                    "showAll": "Toon alles"
                },
                "name": "Naam",
                "qt": "Qt",
                "visibility": "Zichtbaarheid",
                "visivility": "Zichtbaarheid"
            },
            "menu": {
                "camera": "Camera",
                "frontView": "Vooraanzicht",
                "mod": "mod (afbeeldingsweergave instellingen)",
                "orthographic": "<em>Orthographic Camera</em>",
                "pan": "Schuiven",
                "perspective": "<em>Perspective Camera</em>",
                "resetView": "Reset weergave",
                "rotate": "Draaien",
                "sideView": "Zijaanzicht",
                "topView": "Bovenaanzicht"
            },
            "showBimCard": "Open 3D afbeeldingsweergave",
            "tree": {
                "arrowTooltip": "Selecteer element",
                "columnLabel": "Boom",
                "label": "Boom",
                "open_card": "Open gerelateerde kaart",
                "root": "<em>Ifc Root</em>"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Kaart toevoegen",
                "clone": "Dupliceer",
                "clonewithrelations": "Dupliceer kaart en relaties",
                "deletecard": "Verwijder kaart",
                "deleteconfirmation": "Weet u zeker dat u deze kaart wilt verwijderen?",
                "label": "Kaarten",
                "modifycard": "Kaart aanpassen",
                "opencard": "Kaart openen",
                "print": "Kaart afdrukken"
            },
            "simple": "Eenvoudig",
            "standard": "Standaard"
        },
        "common": {
            "actions": {
                "add": "Toevoegen",
                "apply": "Toepassen",
                "cancel": "Annuleren",
                "close": "Sluiten",
                "delete": "Verwijderen",
                "edit": "Modificeer",
                "execute": "Uitvoeren",
                "refresh": "Ververs gegevens",
                "remove": "Verwijderen",
                "save": "Opslaan",
                "saveandapply": "Opslaan en toepassen",
                "saveandclose": "Opslaan en sluiten",
                "search": "Zoeken",
                "searchtext": "Zoeken…"
            },
            "attributes": {
                "nogroup": "Basis gegevens"
            },
            "dates": {
                "date": "d/m/j",
                "datetime": "d/m/j h:m:s",
                "time": "h:m:s"
            },
            "editor": {
                "clearhtml": "Verwijder HTML"
            },
            "grid": {
                "disablemultiselection": "Blokkeer meerdere selecties",
                "enamblemultiselection": "Toestaan meerdere selecties",
                "export": "<em>Export data</em>",
                "filterremoved": "Het huidige filter is verwijderd",
                "import": "<em>Import data</em>",
                "itemnotfound": "Item niet gevonden",
                "list": "Lijst",
                "opencontextualmenu": "Open context menu",
                "print": "Afdrukken",
                "printcsv": "Afdrukken als CSV",
                "printodt": "Afdrukken als ODT",
                "printpdf": "Afdrukken als PDF",
                "row": "Onderdeel",
                "rows": "Onderdelen",
                "subtype": "Onderliggend Type"
            },
            "tabs": {
                "activity": "Activiteit",
                "attachments": "Bijlage",
                "card": "Kaart",
                "details": "Details",
                "emails": "Emails",
                "history": "Geschiedenis",
                "notes": "Notities",
                "relations": "Relaties"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Bijlage van DMS toevoegen",
            "alredyexistfile": "<em>Already exists a file with this name</em>",
            "archivingdate": "Archiverings datum",
            "attachfile": "Bestand toevoegen",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "Samenstellen email",
            "composefromtemplate": "Samenstellen vanaf sjabloon",
            "delay": "Vertraging",
            "delays": {
                "day1": "In 1 dag",
                "days2": "In 2 dagen",
                "days4": "In 4 dagen",
                "hour1": "1 uur",
                "hours2": "2 uren",
                "hours4": "4 uren",
                "month1": "In 1 maand",
                "none": "Geen",
                "week1": "In 1 week",
                "weeks2": "In 2 weken"
            },
            "dmspaneltitle": "Kies bijlages uit database",
            "edit": "Modificeer",
            "from": "Van",
            "gridrefresh": "Raster verversen",
            "keepsynchronization": "Houd synchronisatie",
            "message": "Message",
            "regenerateallemails": "Regenereer alle e-mails",
            "regenerateemail": "Regenereer e-mails",
            "remove": "Verwijderen",
            "remove_confirmation": "Weet u zeker dat u deze email wilt verwijderen?",
            "reply": "Reaktie",
            "replyprefix": "On {0}, {1} schreef:",
            "selectaclass": "<em>Select a class</em>",
            "sendemail": "Zend e-mail",
            "statuses": {
                "draft": "Klad",
                "outgoing": "Uitgaand",
                "received": "Ontvangen",
                "sent": "verstuurd"
            },
            "subject": "Onderwerp",
            "to": "Aan",
            "view": "Zicht"
        },
        "errors": {
            "autherror": "Verkeerde gebruikersnaam of wachtwoord",
            "classnotfound": "Klas {0} niet gevonden",
            "notfound": "Item niet gevonden"
        },
        "filters": {
            "actions": "Acties",
            "addfilter": "Filter toevoegen",
            "any": "Alle",
            "attribute": "Kies een attribuut",
            "attributes": "Attributen",
            "clearfilter": "Leegmaken filter",
            "clone": "Dupliceer",
            "copyof": "Kopie van",
            "description": "Omschrijving",
            "domain": "Domein",
            "filterdata": "Filter gegevens",
            "fromselection": "Van Selectie",
            "ignore": "Negeer",
            "migrate": "Migreren",
            "name": "Naam",
            "newfilter": "Nieuw filter",
            "noone": "Geen enkele",
            "operator": "Bewerking",
            "operators": {
                "beginswith": "Begin met",
                "between": "Tussen",
                "contained": "Bevat",
                "containedorequal": "Bevat of is gelijk aan",
                "contains": "Bevat",
                "containsorequal": "Bevat of is gelijk aan",
                "different": "Verschillend",
                "doesnotbeginwith": "Begint niet met",
                "doesnotcontain": "Bevat geen",
                "doesnotendwith": "Eindigd niet op",
                "endswith": "Eindigd op",
                "equals": "Gelijk",
                "greaterthan": "Grooter dan",
                "isnotnull": "Is niet leeg",
                "isnull": "Null",
                "lessthan": "Kleiner dan"
            },
            "relations": "Relaties",
            "type": "Soort",
            "typeinput": "Input Parameter",
            "value": "Waarde"
        },
        "gis": {
            "card": "Kaart",
            "cardsMenu": "<em>Cards Menu</em>",
            "externalServices": "Externe Services",
            "geographicalAttributes": "Geografische attributen",
            "geoserverLayers": "Geoserver lagen",
            "layers": "Lagen",
            "list": "Lijst",
            "map": "Kaart",
            "mapServices": "Kaart services",
            "position": "Positie",
            "root": "Begin",
            "tree": "Navigatie boomstructuur",
            "view": "Zicht",
            "zoom": "Zoom"
        },
        "history": {
            "activityname": "Activiteit naam",
            "activityperformer": "Activiteit uitvoerder",
            "begindate": "Begin datum",
            "enddate": "Eind datum",
            "processstatus": "Status",
            "user": "Gebruiker"
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
                "login": "Login",
                "logout": "Modificeer gebruiker"
            },
            "fields": {
                "group": "Groep",
                "language": "Taal",
                "password": "Wachtwoord",
                "tenants": "Leden",
                "username": "Gebruikersnaam"
            },
            "loggedin": "Aangemeld",
            "title": "Login",
            "welcome": "Welkom terug {0}."
        },
        "main": {
            "administrationmodule": "Administratie module",
            "baseconfiguration": "<em>Base configuration</em>",
            "cardlock": {
                "lockedmessage": "U kunt deze kaart niet aanpassen omdat {0} deze aan het aanpassen is",
                "someone": "iemand"
            },
            "changegroup": "Wijzig groep",
            "changepassword": "Wachtwoord veranderen",
            "changetenant": "Wijzig lid",
            "confirmchangegroup": "Weet u zeker dat de groep gewijzigd moet worden",
            "confirmchangetenants": "Weet u zeker dat de active leden gewijzigd moeten worden",
            "confirmdisabletenant": "Weet u zeker dat de “negeer leden” vlag uitgezet moet worden",
            "confirmenabletenant": "Weet u zeker dat de “negeer leden” vlag aangezet moet worden",
            "confirmpassword": "Bevestig wachtwoord",
            "ignoretenants": "Negeer leden",
            "info": "Info",
            "logo": {
                "cmdbuild": "CMDBuild logo",
                "cmdbuildready2use": "CMDBuild READY2USE logo",
                "companylogo": "<em>Company logo</em>",
                "openmaint": "openMAINT logo"
            },
            "logout": "Uitloggen",
            "managementmodule": "Gegevens beheer module",
            "multigroup": "Meervoudige groep",
            "multitenant": "Multitenant",
            "navigation": "Navigatie",
            "newpassword": "Nieuw wachtwoord",
            "oldpassword": "Oud wachtwoord",
            "pagenotfound": "<em>Page not found</em>",
            "pleasecorrecterrors": "<em>Please correct indicated errors!</em>",
            "preferences": {
                "comma": "Komma",
                "decimalserror": "Decimaal veld moet aanwezig zijn",
                "decimalstousandserror": "Decimaal en Duizendtal scheidingsteken moeten verschillen",
                "default": "<em>Default</em>",
                "defaultvalue": "Default waarde",
                "labeldateformat": "Datum formaat",
                "labeldecimalsseparator": "Decimaal scheidingsteken",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "Taal",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "Duizendtal scheidingsteken",
                "labeltimeformat": "Tijd formaat",
                "msoffice": "<em>Microsoft Office</em>",
                "period": "Punt",
                "preferredofficesuite": "<em>Preferred Office suite</em>",
                "space": "Spatie",
                "thousandserror": "Duizendtal veld moet aanwezig zijn",
                "timezone": "<em>Timezone</em>",
                "twelvehourformat": "12-uurs formaat",
                "twentyfourhourformat": "24-uurs formaat"
            },
            "searchinallitems": "Zoek in alle onderdelen",
            "userpreferences": "Voorkeuren"
        },
        "menu": {
            "allitems": "Alle onderdelen",
            "classes": "Klassen",
            "custompages": "Gebruikers bladzijden",
            "dashboards": "Instrumenten paneel",
            "processes": "Processen",
            "reports": "Rapporten",
            "views": "Vensters"
        },
        "notes": {
            "edit": "Notitie aanpassen"
        },
        "notifier": {
            "attention": "Attentie",
            "error": "Fout",
            "genericerror": "Algemene fout",
            "genericinfo": "Algemene informatie",
            "genericwarning": "Algemene waarschuwing",
            "info": "Info",
            "success": "Succes",
            "warning": "Waarschuwing"
        },
        "patches": {
            "apply": "<em>Apply patches</em>",
            "category": "<em>Category</em>",
            "description": "<em>Description</em>",
            "name": "<em>Name</em>",
            "patches": "<em>Patches</em>"
        },
        "processes": {
            "abortconfirmation": "Weet je zeker dat je dit proces wilt afbreken?",
            "abortprocess": "Proces afbreken",
            "action": {
                "advance": "Doorschuiven",
                "label": "Aktie"
            },
            "activeprocesses": "Actieve processen",
            "allstatuses": "Alles",
            "editactivity": "Activiteit aanpassen",
            "openactivity": "Activiteit openen",
            "startworkflow": "Start",
            "workflow": "Procesgang"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Kaart",
            "cardList": "Kaart lijst",
            "cardRelation": "Relatie",
            "cardRelations": "Relatie",
            "choosenaviagationtree": "Kies navigatie boom",
            "class": "Klas",
            "class:": "Klas",
            "classList": "Klassen lijst",
            "compoundnode": "<em>Compound Node</em>",
            "enableTooltips": "Toestaan/blokkeren tooltips bij grafiek",
            "level": "Niveau",
            "openRelationGraph": "Open relatie grafiek",
            "qt": "Qt",
            "refresh": "Ververs",
            "relation": "Relatie",
            "relationGraph": "Relatie grafiek",
            "reopengraph": "Heropen de grafiek van dit knooppunt"
        },
        "relations": {
            "adddetail": "Detail toevoegen",
            "addrelations": "Relaties toevoegen",
            "attributes": "Attributen",
            "code": "Code",
            "deletedetail": "Detail verwijderen",
            "deleterelation": "Verwijder relatie",
            "description": "Omschrijving",
            "editcard": "Kaart aanpassen",
            "editdetail": "Detail aanpassen",
            "editrelation": "Aanpassen relatie",
            "mditems": "onderdelen",
            "opencard": "Open gerelateerde kaart",
            "opendetail": "Toon detail",
            "type": "Soort"
        },
        "reports": {
            "csv": "CSV",
            "download": "Ophalen",
            "format": "Formaat",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Afdrukken",
            "reload": "Herladen",
            "rtf": "RTF"
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
                "addrow": "Rij toevoegen",
                "clonerow": "Kloon rij",
                "deleterow": "Rij verwijderen",
                "editrow": "Rij aanpassen",
                "export": "Export",
                "import": "Importeer",
                "refresh": "Terug naar standaard"
            },
            "linkcards": {
                "editcard": "Kaart aanpassen",
                "opencard": "Kaart openen",
                "refreshselection": "Standaard selectie toepassen",
                "togglefilterdisabled": "Verwijder raster filter",
                "togglefilterenabled": "Instellen raster filter"
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
(function() {
    Ext.define('CMDBuildUI.locales.no.Locales', {
        "requires": ["CMDBuildUI.locales.no.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "no",
        "administration": CMDBuildUI.locales.no.LocalesAdministration.administration,
        "attachments": {
            "add": "Legg til vedlegg",
            "attachmenthistory": "Vedleggshistorikk",
            "author": "Forfatter",
            "category": "Kategori",
            "creationdate": "Opprettelsesdato",
            "deleteattachment": "Slett vedlegg",
            "deleteattachment_confirmation": "Er du sikker på at du vil slette vedlegget?",
            "description": "Beskrivelse",
            "download": "Last ned",
            "editattachment": "Endre vedlegg",
            "file": "Fil",
            "filename": "Filnavn",
            "majorversion": "Hovedversjon",
            "modificationdate": "Endringsdato",
            "uploadfile": "Last opp fil…",
            "version": "Versjon",
            "viewhistory": "Vis vedleggshistorikk"
        },
        "bim": {
            "bimViewer": "Bim visning",
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
                "qt": "Qt",
                "visibility": "Synlighet"
            },
            "menu": {
                "camera": "Kamera",
                "frontView": "Frontvisning",
                "mod": "Visningskontroller",
                "orthographic": "Ortografisk kamera",
                "pan": "panorer",
                "perspective": "Perspektivkamera",
                "resetView": "Nullstill visning",
                "rotate": "roter",
                "sideView": "Sidevisning",
                "topView": "Toppvisning"
            },
            "showBimCard": "Åpne 3D visning",
            "tree": {
                "arrowTooltip": "Velg element",
                "columnLabel": "Tre",
                "label": "Tre",
                "open_card": "Åpne relatert kort",
                "root": "Ifc Root"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Legg til kort",
                "clone": "Klone",
                "clonewithrelations": "Klone kort og relasjoner",
                "deletecard": "Slett kort",
                "deleteconfirmation": "Er du sikker på at du vil slette kortet?",
                "label": "Kort",
                "modifycard": "Endre kort",
                "opencard": "Åpne kort",
                "print": "Skriv ut kort"
            },
            "simple": "Simple",
            "standard": "Standard"
        },
        "common": {
            "actions": {
                "add": "Legg til",
                "apply": "Bruk",
                "cancel": "Avbryt",
                "close": "Lukk",
                "delete": "Slett",
                "edit": "Endre",
                "execute": "Kjør",
                "refresh": "Oppfrisk data",
                "remove": "Fjern",
                "save": "Lagre",
                "saveandapply": "Lagre og bruk",
                "saveandclose": "Lagre og lukk",
                "search": "Søk",
                "searchtext": "Søk…"
            },
            "attributes": {
                "nogroup": "Basisdata"
            },
            "dates": {
                "date": "d.m.Y",
                "datetime": "d.m.Y H:i:s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "Rens HTML"
            },
            "grid": {
                "disablemultiselection": "Slå av multivalg",
                "enamblemultiselection": "Slå på multivalg",
                "export": "Eksporter data",
                "filterremoved": "Gjeldende filter har blitt fjernet",
                "import": "Importer data",
                "itemnotfound": "Ingen elementer funnet",
                "list": "Liste",
                "opencontextualmenu": "Åpne kontekstmeny",
                "print": "Skriv ut",
                "printcsv": "Skriv ut som CSV",
                "printodt": "Skriv ut som ODT",
                "printpdf": "Skriv ut som PDF",
                "row": "Element",
                "rows": "Elementer",
                "subtype": "Subtype"
            },
            "tabs": {
                "activity": "Aktivitet",
                "attachments": "Vedlegg",
                "card": "Kort",
                "details": "Detaljer",
                "emails": "E-post",
                "history": "Historikk",
                "notes": "Notater",
                "relations": "Relasjoner"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Legg ved vedlegg fra DMS",
            "alredyexistfile": "Det finnes allerede en fil med dette navnet",
            "archivingdate": "Arkiveringsdato",
            "attachfile": "Legg ved fil",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "Skriv e-post",
            "composefromtemplate": "Lag fra mal",
            "delay": "Forsinkelse",
            "delays": {
                "day1": "Om 1 dag",
                "days2": "Om 2 dager",
                "days4": "Om 4 dager",
                "hour1": "1 time",
                "hours2": "2 timer",
                "hours4": "4 timer",
                "month1": "Om 1 måned",
                "none": "Ingen",
                "week1": "Om 1 uke",
                "weeks2": "Om 2 uker"
            },
            "dmspaneltitle": "Velg vedlegg fra database",
            "edit": "Endre",
            "from": "Fra",
            "gridrefresh": "Gridoppdatering",
            "keepsynchronization": "Behold synkronisering",
            "message": "Melding",
            "regenerateallemails": "Regenerer alle e-poster",
            "regenerateemail": "Regenerer e-post",
            "remove": "Slett",
            "remove_confirmation": "Er du sikker på at du vil slette e-posten?",
            "reply": "Svar",
            "replyprefix": "{1} wrote:",
            "selectaclass": "Velg en klasse",
            "sendemail": "Send e-post",
            "statuses": {
                "draft": "Kladd",
                "outgoing": "Utgående",
                "received": "Mottatt",
                "sent": "Sendt"
            },
            "subject": "Emne",
            "to": "Til",
            "view": "Vis"
        },
        "errors": {
            "autherror": "Feil brukernavn eller passord",
            "classnotfound": "Finner ikke klasse {0}",
            "notfound": "Ingen elementer funnet"
        },
        "filters": {
            "actions": "Handlinger",
            "addfilter": "Legg til filter",
            "any": "Alle",
            "attribute": "Velg et atributt",
            "attributes": "Atributter",
            "clearfilter": "Fjern filter",
            "clone": "Klone",
            "copyof": "Kopi av",
            "description": "Beskrivelse",
            "domain": "Domene",
            "filterdata": "Filtrer data",
            "fromselection": "Fra seleksjon",
            "ignore": "Ignorer",
            "migrate": "Migrer",
            "name": "Navn",
            "newfilter": "Nytt filter",
            "noone": "Ingen",
            "operator": "Operatør",
            "operators": {
                "beginswith": "Starter med",
                "between": "Mellom",
                "contained": "Innenfor",
                "containedorequal": "Innenfor eller lik",
                "contains": "Inneholder",
                "containsorequal": "Inneholder eller lik",
                "different": "Forskjellig fra",
                "doesnotbeginwith": "Starter ikke med",
                "doesnotcontain": "Inneholder ikke",
                "doesnotendwith": "Slutter ikke med",
                "endswith": "Slutter med",
                "equals": "Lik",
                "greaterthan": "Større enn",
                "isnotnull": "Er ikke null",
                "isnull": "Er null",
                "lessthan": "Mindre enn"
            },
            "relations": "Relasjoner",
            "type": "Type",
            "typeinput": "Inndataparameter",
            "value": "Verdi"
        },
        "gis": {
            "card": "Kort",
            "cardsMenu": "Kortmeny",
            "externalServices": "Eksterne tjenester",
            "geographicalAttributes": "Geo atributter",
            "geoserverLayers": "Geoserverlag",
            "layers": "Kartlag",
            "list": "Liste",
            "map": "Kart",
            "mapServices": "Karttjenester",
            "position": "Posisjon",
            "root": "Rot",
            "tree": "Tre",
            "view": "Visning",
            "zoom": "Zoom"
        },
        "history": {
            "activityname": "Aktivitetsnavn",
            "activityperformer": "Aktivitetsyter",
            "begindate": "Startdato",
            "enddate": "Sluttdato",
            "processstatus": "Status",
            "user": "Brukerkonto"
        },
        "importexport": {
            "downloadreport": "Last ned rapport",
            "emailfailure": "En feil oppsto ved sending av e-post!",
            "emailsubject": "Import datarapport",
            "emailsuccess": "E-posten ble sendt!",
            "export": "Eksport",
            "import": "Import",
            "importresponse": "Importsvar",
            "response": {
                "created": "Opprettede elementer",
                "deleted": "Slettede elementer",
                "errors": "Feil",
                "linenumber": "Linjenummer",
                "message": "Melding",
                "modified": "Endrede elementer",
                "processed": "Prosesserte rader",
                "recordnumber": "Antall oppføringer",
                "unmodified": "Uendrede elementer"
            },
            "sendreport": "Send rapport",
            "template": "Mal",
            "templatedefinition": "<em>Template definition</em>"
        },
        "login": {
            "buttons": {
                "login": "Logg inn",
                "logout": "Bytt konto"
            },
            "fields": {
                "group": "Gruppe",
                "language": "Språk",
                "password": "Passord",
                "tenants": "Tenants",
                "username": "Brukernavn"
            },
            "loggedin": "Logget inn",
            "title": "Logg inn",
            "welcome": "Velkommen tilbake {0}."
        },
        "main": {
            "administrationmodule": "Administrasjonsmodul",
            "baseconfiguration": "Basiskonfigurasjon",
            "cardlock": {
                "lockedmessage": "Du kan ikke endre dette kortet",
                "someone": "noen"
            },
            "changegroup": "Endre gruppe",
            "changepassword": "Endre passord",
            "changetenant": "Endre tenant",
            "confirmchangegroup": "Vil du virkelig endre gruppen?",
            "confirmchangetenants": "Vil du virkelig endre aktiv tenant?",
            "confirmdisabletenant": "Vil du virkelig skru av «Ignorer tenants» statusflagget?",
            "confirmenabletenant": "Vil du virkelig skru på «Ignorer tenants» statusflagget?",
            "confirmpassword": "Gjenta passord",
            "ignoretenants": "Ignorer tenants",
            "info": "Info",
            "logo": {
                "cmdbuild": "CMDBuild logo",
                "cmdbuildready2use": "CMDBuild READY2USE logo",
                "companylogo": "Bedriftslogo",
                "openmaint": "openMAINT logo"
            },
            "logout": "Logg ut",
            "managementmodule": "Datahåndteringsmodulen",
            "multigroup": "Multigruppe",
            "multitenant": "Multitenant",
            "navigation": "Navigasjon",
            "newpassword": "Nytt passord",
            "oldpassword": "Gammel passord",
            "pagenotfound": "Side ikke funnet",
            "pleasecorrecterrors": "Vennligs rett merkede feil!",
            "preferences": {
                "comma": "Komma",
                "decimalserror": "Desimalskille må være tilstede",
                "decimalstousandserror": "Desimalskille og tusenskille må være forskjellig",
                "default": "Standard",
                "defaultvalue": "Standardverdi",
                "labeldateformat": "Datoformat",
                "labeldecimalsseparator": "Desimalskille",
                "labellanguage": "Språk",
                "labelthousandsseparator": "Tusenskille",
                "labeltimeformat": "Tidsformat",
                "msoffice": "Microsoft Office",
                "period": "Punktum",
                "preferredofficesuite": "Foretrukket Office programpakke",
                "space": "Mellomrom",
                "thousandserror": "Tusenskille må være tilstede",
                "timezone": "Tidssone",
                "twelvehourformat": "12-timers format",
                "twentyfourhourformat": "24-timers format"
            },
            "searchinallitems": "Søk i alle elementer",
            "userpreferences": "Innstillinger"
        },
        "menu": {
            "allitems": "Alle elementer",
            "classes": "Klasser",
            "custompages": "Tilpassede sider",
            "dashboards": "Dashboards",
            "processes": "Prosesser",
            "reports": "Rapporter",
            "views": "Visninger"
        },
        "notes": {
            "edit": "Endre notater"
        },
        "notifier": {
            "attention": "Attensjon",
            "error": "Feil",
            "genericerror": "Generisk feil",
            "genericinfo": "Generisk info",
            "genericwarning": "Generisk advarsel",
            "info": "Info",
            "success": "Suksess",
            "warning": "Advarsel"
        },
        "patches": {
            "apply": "Legg inn oppdateringer",
            "category": "Kategori",
            "description": "Beskrivelse",
            "name": "Navn",
            "patches": "Oppdateringer"
        },
        "processes": {
            "abortconfirmation": "Er du sikker på at du vil abryte prosessen?",
            "abortprocess": "Avbryt prosess",
            "action": {
                "advance": "Avensert",
                "label": "Handling"
            },
            "activeprocesses": "Aktive prosesser",
            "allstatuses": "Alle",
            "editactivity": "Endre aktivitet",
            "openactivity": "Åpne aktivitet",
            "startworkflow": "Start",
            "workflow": "Arbeidsflyt"
        },
        "relationGraph": {
            "activity": "Aktivitet",
            "card": "Kort",
            "cardList": "Kortliste",
            "cardRelations": "Kortrelasjoner",
            "choosenaviagationtree": "Velg navigasjonstre",
            "class": "Klasse",
            "classList": "Klasseliste",
            "compoundnode": "Sammensatt node",
            "enableTooltips": "Slå på/av verktøytips på grafen",
            "level": "Nivå",
            "openRelationGraph": "Åpne relasjonsgraf",
            "qt": "Qt",
            "refresh": "Oppfrisk",
            "relation": "relasjon",
            "relationGraph": "Relasjonsgraf",
            "reopengraph": "Gjenåpne grafen fra denne noden"
        },
        "relations": {
            "adddetail": "Legg til detaljer",
            "addrelations": "Legg til relasjoner",
            "attributes": "Atributter",
            "code": "Kode",
            "deletedetail": "Slett detaljer",
            "deleterelation": "Slett relasjoner",
            "description": "Beskrivelse",
            "editcard": "Endre kort",
            "editdetail": "Endre detaljer",
            "editrelation": "Endre relasjoner",
            "mditems": "Elementer",
            "opencard": "Åpne relatert kort",
            "opendetail": "Vis detaljer",
            "type": "Type"
        },
        "reports": {
            "csv": "CSV",
            "download": "Last ned",
            "format": "Format",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Skriv ut",
            "reload": "Oppfrisk",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "<em>Add Thematism</em>",
            "analysisType": "Analysetype",
            "attribute": "Atributt",
            "calculateRules": "<em>Generate style rules</em>",
            "clearThematism": "<em>Clear Thematism</em>",
            "color": "Farge",
            "defineLegend": "<em>Legend definition</em>",
            "defineThematism": "<em>Thematism definition</em>",
            "function": "Funksjon",
            "generate": "Generer",
            "geoAttribute": "<em>geoAttribute</em>",
            "graduated": "Graduated",
            "highlightSelected": "Marker valgte element",
            "intervals": "Intervaller",
            "legend": "Forklaring",
            "name": "<em>name</em>",
            "newThematism": "<em>New Thematism</em>",
            "punctual": "Punctual",
            "quantity": "Mengde",
            "source": "Kilde",
            "table": "Tabell",
            "thematism": "Tematikk",
            "value": "Verdi"
        },
        "widgets": {
            "customform": {
                "addrow": "Legg til rad",
                "clonerow": "Klone rad",
                "deleterow": "Slett rad",
                "editrow": "Endre rad",
                "export": "Eksport",
                "import": "Import",
                "refresh": "Oppfrisk til standard"
            },
            "linkcards": {
                "editcard": "Endre kort",
                "opencard": "Åpne kort",
                "refreshselection": "Bruk standard seleksjon",
                "togglefilterdisabled": "Slå på gridfilter",
                "togglefilterenabled": "Slå av gridfilter"
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
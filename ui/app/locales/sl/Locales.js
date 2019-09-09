(function() {
    Ext.define('CMDBuildUI.locales.sl.Locales', {
        "requires": ["CMDBuildUI.locales.sl.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "sl",
        "administration": CMDBuildUI.locales.sl.LocalesAdministration.administration,
        "attachments": {
            "add": "Dodaj priponko",
            "attachmenthistory": "Zgodovina prilog",
            "author": "Avtor",
            "category": "Kategorija",
            "creationdate": "Datum nastanka",
            "deleteattachment": "Izbriši priponko",
            "deleteattachment_confirmation": "Ali ste prepričani, da želite izbrisati priponko?",
            "description": "Opis",
            "download": "Prenesi",
            "editattachment": "Spremeni priponko",
            "file": "Datoteka",
            "filename": "Ime datoteke",
            "majorversion": "Glavna različica",
            "modificationdate": "Datum spremembe",
            "uploadfile": "Prenesi datoteko…",
            "version": "Različica",
            "viewhistory": "Pokaži zgodovino prilog"
        },
        "bim": {
            "bimViewer": "BIM pregledovalnik",
            "card": {
                "label": "Kartica"
            },
            "layers": {
                "label": "Sloji",
                "menu": {
                    "hideAll": "Skrij vse",
                    "showAll": "Prikaži vse"
                },
                "name": "Ime",
                "qt": "Qt",
                "visibility": "Vidnost",
                "visivility": "Vidnost"
            },
            "menu": {
                "camera": "Kamera",
                "frontView": "Pogled od spredaj",
                "mod": "Kontrolnik pogleda",
                "orthographic": "Pogled orto",
                "pan": "Premikati se",
                "perspective": "Pogled perspektiva",
                "resetView": "Resetiraj pogled",
                "rotate": "Rotiraj",
                "sideView": "Pogled od strani",
                "topView": "Pogled od zgoraj"
            },
            "showBimCard": "Odpri 3D pregledovalnik",
            "tree": {
                "arrowTooltip": "Izberi element",
                "columnLabel": "Drevesni pogled",
                "label": "Drevesni pogled",
                "open_card": "Odpri povezane kartice",
                "root": "IFC root"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Dodaj kartico",
                "clone": "Podvoji",
                "clonewithrelations": "Podvoji kartico in relacije",
                "deletecard": "Izbriši kartico",
                "deleteconfirmation": "Ali ste prepričani, da želite izbrisati kartico?",
                "label": "Kartice",
                "modifycard": "Spremeni kartico",
                "opencard": "Odpri kartico",
                "print": "Natisni kartico"
            },
            "simple": "Enostavno",
            "standard": "Standard"
        },
        "common": {
            "actions": {
                "add": "Dodaj",
                "apply": "Potrdi",
                "cancel": "Prekliči",
                "close": "Zapri",
                "delete": "Izbriši",
                "edit": "Uredi",
                "execute": "Izvrši",
                "refresh": "Posodobi podatke",
                "remove": "Odstrani",
                "save": "Shrani",
                "saveandapply": "Shrani in potrdi",
                "saveandclose": "Shrani in zapri",
                "search": "Najdi",
                "searchtext": "Najdi…"
            },
            "attributes": {
                "nogroup": "Osnovni podatki"
            },
            "dates": {
                "date": "d/M/L",
                "datetime": "d/M/L HH:mm:ss",
                "time": "HH:mm:ss"
            },
            "editor": {
                "clearhtml": "Pošisti HTML"
            },
            "grid": {
                "disablemultiselection": "Izklopi multi izbiro",
                "enamblemultiselection": "Omogoči multi-izbiro",
                "export": "Izvozi podatke",
                "filterremoved": "Trenutni filter je bil odstranjen",
                "import": "Uvozi podatke",
                "itemnotfound": "Predmet ni najden",
                "list": "Seznam",
                "opencontextualmenu": "Odpri kontekstualni meni",
                "print": "Natisni",
                "printcsv": "Natisni kot CSV",
                "printodt": "Natisni kot ODT",
                "printpdf": "Natisni kot PDF",
                "row": "Predmet",
                "rows": "Predmeti",
                "subtype": "Podvrsta"
            },
            "tabs": {
                "activity": "Aktivnost",
                "attachments": "Priponke",
                "card": "Kartica",
                "details": "Podrobnosti",
                "emails": "Elektronska sporočila",
                "history": "Zgodovina",
                "notes": "Opombe",
                "relations": "Relacija"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Dodaj priponko iz DMS",
            "alredyexistfile": "Datoteka s tem imenom že obstaja",
            "archivingdate": "Datum shranjevanja",
            "attachfile": "Dodaj prilogo",
            "bcc": "Skp",
            "cc": "Kp",
            "composeemail": "Ustvari elektronsko sporočilo",
            "composefromtemplate": "Ustvari iz predloge",
            "delay": "Zamik",
            "delays": {
                "day1": "1 dan",
                "days2": "2 dneva",
                "days4": "4 dni",
                "hour1": "1 ura",
                "hours2": "2 uri",
                "hours4": "4 ure",
                "month1": "1 mesec",
                "none": "Brez",
                "week1": "1 teden",
                "weeks2": "2 tedna"
            },
            "dmspaneltitle": "Izberi priponko iz baze podatkov",
            "edit": "Uredi",
            "from": "Od",
            "gridrefresh": "Osveži omrežje",
            "keepsynchronization": "Obdrži sinhroniziranje",
            "message": "Sporočilo",
            "regenerateallemails": "Pošlji/prejmi vse mape",
            "regenerateemail": "Obnovi sporočilo",
            "remove": "Odstrani",
            "remove_confirmation": "Ali ste prepričani, da želite izbrisati sporočilo?",
            "reply": "Odgovori",
            "replyprefix": "Na {0}, {1} napiši:",
            "selectaclass": "Izberi razred",
            "sendemail": "Pošlji sporočilo",
            "statuses": {
                "draft": "Osnutek",
                "outgoing": "Odhodna",
                "received": "Prejeta",
                "sent": "Pošlji"
            },
            "subject": "Zadeva",
            "to": "Za",
            "view": "Pogledi"
        },
        "errors": {
            "autherror": "Napačno uporabniško ime ali geslo",
            "classnotfound": "Razreda {0} ni mogoče najti",
            "notfound": "Predmet in najden"
        },
        "filters": {
            "actions": "Aktivnost",
            "addfilter": "Dodaj filter",
            "any": "Katerikoli",
            "attribute": "Izberi atribut",
            "attributes": "Atributi",
            "clearfilter": "Počisti filter",
            "clone": "Podvoji",
            "copyof": "Kopija",
            "description": "Opis",
            "domain": "Domena",
            "filterdata": "Filtriraj podatke",
            "fromselection": "Izberi",
            "ignore": "Prezri",
            "migrate": "Migriraj",
            "name": "Ime",
            "newfilter": "Nov filter",
            "noone": "Brez",
            "operator": "Operater",
            "operators": {
                "beginswith": "Začni s/z",
                "between": "Med",
                "contained": "Vključuje",
                "containedorequal": "Vključuje ali enako",
                "contains": "Vsebuje",
                "containsorequal": "Vsebuje ali enako",
                "different": "Različen",
                "doesnotbeginwith": "Se ne prične s/z",
                "doesnotcontain": "Ne vsebuje",
                "doesnotendwith": "Se ne konča s/z",
                "endswith": "Se konča s/z",
                "equals": "Enako",
                "greaterthan": "Večji kot",
                "isnotnull": "Ni brez",
                "isnull": "Je brez",
                "lessthan": "Manj kot"
            },
            "relations": "Relacije",
            "type": "Vrsta",
            "typeinput": "Vnesi parameter",
            "value": "Vrednost"
        },
        "gis": {
            "card": "Kartica",
            "cardsMenu": "Meni kartic",
            "externalServices": "Zunanje storitve",
            "geographicalAttributes": "GEO atributi",
            "geoserverLayers": "Geoserver ravni",
            "layers": "Ravni",
            "list": "Seznam",
            "map": "Zemljevid",
            "mapServices": "Storitve zemljevidov",
            "position": "Pozicija",
            "postition": "Pozicioniraj",
            "root": "Izvorna oblika",
            "tree": "Drevesni pogled",
            "view": "Pogledi",
            "zoom": "Povečaji"
        },
        "history": {
            "activityname": "Naziv aktivnosti",
            "activityperformer": "Izvajalec aktivnosti",
            "begindate": "Pričetek",
            "enddate": "Zaključek",
            "processstatus": "Status",
            "user": "Uporabnik"
        },
        "importexport": {
            "downloadreport": "Prenesi poročilo",
            "emailfailure": "Pri pošiljanju elektronske pošte je prišlo do napake!",
            "emailsubject": "Prenesi podatke poročila",
            "emailsuccess": "Elektronsko sporočilo je bilo uspešno poslano!",
            "export": "Izvozi",
            "import": "Uvozi",
            "importresponse": "Uvozi odzive",
            "response": {
                "created": "Ustvarjeni elementi",
                "deleted": "Izbrisani elementi",
                "errors": "Napake",
                "linenumber": "Št.",
                "message": "Sporočilo",
                "modified": "Spremenjeni elementi",
                "processed": "Obdelane vrstice",
                "recordnumber": "ID št.",
                "unmodified": "Nespremenjeni elementi"
            },
            "sendreport": "Pošlji poročilo",
            "template": "Predloga",
            "templatedefinition": "<em>Template definition</em>"
        },
        "login": {
            "buttons": {
                "login": "Prijava",
                "logout": "Spremeni uporabnika"
            },
            "fields": {
                "group": "Skupina",
                "language": "Jezik",
                "password": "Geslo",
                "tenants": "Najemniki",
                "username": "Uporabniško ime"
            },
            "loggedin": "Prijavljeni kot",
            "title": "Prijava",
            "welcome": "Dobrodošli nazaj {0}"
        },
        "main": {
            "administrationmodule": "Administracijski modul",
            "baseconfiguration": "Osnovna konfiguracija",
            "cardlock": {
                "lockedmessage": "Žal ne morete spremeniti kartice, saj jo ureja {0}.",
                "someone": "nekdo"
            },
            "changegroup": "Spremeni skupino",
            "changepassword": "Spremeni geslo",
            "changetenant": "Spremeni najemnika",
            "confirmchangegroup": "Ali ste prepričani, da želite spremeniti skupino?",
            "confirmchangetenants": "Ali ste prepričani, da želite spremeniti najemnika?",
            "confirmdisabletenant": "Ali ste prepričani, da želite izklopiti obvestilo \"prezri najemnika\"?",
            "confirmenabletenant": "Ali ste prepričani, da želite vklopiti obvestilo \"prezri najemnika\"?",
            "confirmpassword": "Potrdi geslo",
            "ignoretenants": "Prezri najemnika",
            "info": "Informacija",
            "logo": {
                "cmdbuild": "CMDBuild Logo",
                "cmdbuildready2use": "CMDBuild READY2USE Logo",
                "companylogo": "Logotip podjetja",
                "openmaint": "openMAINT Logo"
            },
            "logout": "Odjava",
            "managementmodule": "Podatkovni modul",
            "multigroup": "Več skupin",
            "multitenant": "Več-najemnikov",
            "navigation": "Navigacija",
            "newpassword": "Novo geslo",
            "oldpassword": "Staro geslo",
            "pagenotfound": "Strani ni bilo mogoče najti",
            "pleasecorrecterrors": "Prosim popravite navedene napake!",
            "preferences": {
                "comma": "Vejica",
                "decimalserror": "Decimalni simbol naj bo aktiviran",
                "decimalstousandserror": "Decimalni simbol in simbol za ločilo morata biti različna.",
                "default": "Privzeto",
                "defaultvalue": "Privzete nastavitve",
                "labeldateformat": "Oblika datuma",
                "labeldecimalsseparator": "Decimalni simbol",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "Jezik",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "Ločilo med skupinami",
                "labeltimeformat": "Oblika ure",
                "msoffice": "Microsoft Office",
                "period": "Periodičnost",
                "preferredofficesuite": "Želeno programsko okolje",
                "space": "Prostor",
                "thousandserror": "Ločilo med skupinami naj bo aktivirano",
                "timezone": "Časovni pas",
                "twelvehourformat": "12-urni format",
                "twentyfourhourformat": "24-urni format"
            },
            "searchinallitems": "Išči po vseh predmetih",
            "userpreferences": "Nastavitve"
        },
        "menu": {
            "allitems": "Vsi predmeti",
            "classes": "Razredi",
            "custompages": "Stran po meri",
            "dashboards": "Nadzorna plošča",
            "processes": "Procesi",
            "reports": "Poročila",
            "views": "Pogledi"
        },
        "notes": {
            "edit": "Spremeni opombe"
        },
        "notifier": {
            "attention": "Pozor",
            "error": "Napaka",
            "genericerror": "Splošna napaka",
            "genericinfo": "Splošna informacija",
            "genericwarning": "Splošno opozorilo",
            "info": "Informacija",
            "success": "Uspešno",
            "warning": "Opozorilo"
        },
        "patches": {
            "apply": "Uporabi popravke",
            "category": "Kategorija",
            "description": "Opis",
            "name": "Ime",
            "patches": "Popravki"
        },
        "processes": {
            "abortconfirmation": "Ali ste prepričani, da želite prekiniti proces?",
            "abortprocess": "Prekliči proces",
            "action": {
                "advance": "Nadaljuj",
                "label": "Aktivnost"
            },
            "activeprocesses": "Aktivni procesi",
            "allstatuses": "Vsi",
            "editactivity": "Spremeni aktivnost",
            "openactivity": "Odpri aktivnost",
            "startworkflow": "Začetek",
            "workflow": "Delovni proces"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Kartica",
            "cardList": "Seznam kartic",
            "cardRelation": "Povezava",
            "cardRelations": "Relacija povezav",
            "choosenaviagationtree": "Izberi drevo navigacije",
            "class": "Klasa",
            "class:": "Razred",
            "classList": "Seznam klas",
            "compoundnode": "Sestavljeno vozlišče",
            "enableTooltips": "Vklopi/izklopi nasvete diagramov",
            "level": "Raven",
            "openRelationGraph": "Odpri diagram relacij",
            "qt": "Qt",
            "refresh": "Osveži",
            "relation": "relacija",
            "relationGraph": "Diagram povezav",
            "reopengraph": "Ponovno odpri graf vozlišča"
        },
        "relations": {
            "adddetail": "Dodaj podrobnosti",
            "addrelations": "Dodaj relacijo",
            "attributes": "Atributi",
            "code": "Koda",
            "deletedetail": "Izbriši podrobnosti",
            "deleterelation": "Izbriši relacijo",
            "description": "Opis",
            "editcard": "Spremeni kartico",
            "editdetail": "Uredi podrobnosti",
            "editrelation": "Uredi relacijo",
            "mditems": "predmeti",
            "opencard": "Odpri povezane kartice",
            "opendetail": "Pokaži podrobnosti",
            "type": "Vrsta"
        },
        "reports": {
            "csv": "CSV",
            "download": "Prenesi",
            "format": "Formatiraj",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Natisni",
            "reload": "Znova naloži",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "<em>Add Thematism</em>",
            "analysisType": "Vrsta analize",
            "attribute": "Atribut",
            "calculateRules": "<em>Generate style rules</em>",
            "clearThematism": "<em>Clear Thematism</em>",
            "color": "Barva",
            "defineLegend": "<em>Legend definition</em>",
            "defineThematism": "<em>Thematism definition</em>",
            "function": "Funkcija",
            "generate": "Ustvari",
            "geoAttribute": "<em>geoAttribute</em>",
            "graduated": "Graduated",
            "highlightSelected": "Označi izbrane elemente",
            "intervals": "Intervali",
            "legend": "legenda",
            "name": "<em>name</em>",
            "newThematism": "<em>New Thematism</em>",
            "punctual": "Točnost",
            "quantity": "Količina",
            "source": "Izvor",
            "table": "Tabela",
            "thematism": "Thematisms",
            "value": "Vrednost"
        },
        "widgets": {
            "customform": {
                "addrow": "Dodaj vrstico",
                "clonerow": "Podvoji vrstico",
                "deleterow": "Izbriši vrstico",
                "editrow": "Uredi vrstico",
                "export": "Izvozi",
                "import": "Uvozi",
                "refresh": "Prevzami privzeto"
            },
            "linkcards": {
                "editcard": "Uredi kartico",
                "opencard": "Odpri kartico",
                "refreshselection": "Potrdi privzeto izbiro",
                "togglefilterdisabled": "Omogoči mrežni filter",
                "togglefilterenabled": "Onemogoči mrežni filter"
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
(function() {
    Ext.define('CMDBuildUI.locales.hr.Locales', {
        "requires": ["CMDBuildUI.locales.hr.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "hr",
        "administration": CMDBuildUI.locales.hr.LocalesAdministration.administration,
        "attachments": {
            "add": "Dodaj prilog",
            "attachmenthistory": "Povijest Priloga",
            "author": "Autor",
            "category": "Kategorija",
            "creationdate": "Datum kreiranja",
            "deleteattachment": "Obriši prilog",
            "deleteattachment_confirmation": "Želite li sigurno obrisati ovaj prilog?",
            "description": "Opis",
            "download": "Preuzimanje",
            "editattachment": "Uredi prilog",
            "file": "Datoteka",
            "filename": "Ime datoteke",
            "majorversion": "Glavna verzija",
            "modificationdate": "Datum izmjene",
            "uploadfile": "Učitaj datoteku...",
            "version": "Verzija",
            "viewhistory": "Pogledaj povijest priloga"
        },
        "bim": {
            "bimViewer": "Bim preglednik",
            "card": {
                "label": "Kartica"
            },
            "layers": {
                "label": "Slojevi",
                "menu": {
                    "hideAll": "Sakrij sve",
                    "showAll": "Prikaži sve"
                },
                "name": "Ime",
                "qt": "Kol.",
                "visibility": "Vidljivost",
                "visivility": "Vidljivost"
            },
            "menu": {
                "camera": "Kamera",
                "frontView": "Prednji prikaz",
                "mod": "mod (kontrole preglednika)",
                "orthographic": "<em>Orthographic Camera</em>",
                "pan": "pan (pomiči)",
                "perspective": "<em>Perspective Camera</em>",
                "resetView": "Poništi prikaz",
                "rotate": "rotiraj",
                "sideView": "Prikaz sa strane",
                "topView": "Prikaz odozgo"
            },
            "showBimCard": "Otvori 3D preglednik",
            "tree": {
                "arrowTooltip": "Odaberi element",
                "columnLabel": "Drvo",
                "label": "Drvo",
                "open_card": "Otvori povezanu karticu",
                "root": "<em>Ifc Root</em>"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Dodaj karticu",
                "clone": "Kloniraj",
                "clonewithrelations": "Kloniraj karticu i odnose",
                "deletecard": "Ukloni karticu",
                "deleteconfirmation": "Želite li sigurno obrisati ovu karticu?",
                "label": "Kartice podataka",
                "modifycard": "Izmjeni karticu",
                "opencard": "Otvori karticu",
                "print": "Ispiši karticu"
            },
            "simple": "Jednostavna",
            "standard": "Standardna"
        },
        "common": {
            "actions": {
                "add": "Dodaj",
                "apply": "Primjeni",
                "cancel": "Odustani",
                "close": "Zatvori",
                "delete": "Obriši",
                "edit": "Uredi",
                "execute": "Izvrši",
                "refresh": "Osvježi podatke",
                "remove": "Ukloni",
                "save": "Spremi",
                "saveandapply": "Spremi i primjeni",
                "saveandclose": "Spremi i zatvori",
                "search": "Traži",
                "searchtext": "Traženje..."
            },
            "attributes": {
                "nogroup": "Osnovni podaci"
            },
            "dates": {
                "date": "d/m/G",
                "datetime": "<em>d/m/Y H:i:s</em>",
                "time": "<em>H:i:s</em>"
            },
            "editor": {
                "clearhtml": "Očisti HTML"
            },
            "grid": {
                "disablemultiselection": "Onemogući višestruki izbor",
                "enamblemultiselection": "Omogući višestruki izbor",
                "export": "<em>Export data</em>",
                "filterremoved": "Trenutni filter je uklonjen",
                "import": "<em>Import data</em>",
                "itemnotfound": "Stavka nije pronađena",
                "list": "Lista",
                "opencontextualmenu": "Otvori kontekstualni meni",
                "print": "Ispiši",
                "printcsv": "Ispiši kao CSV",
                "printodt": "Ispiši kao ODT",
                "printpdf": "Ispiši kao PDF",
                "row": "Stavka",
                "rows": "Stavke",
                "subtype": "Podtip"
            },
            "tabs": {
                "activity": "Aktivnost",
                "attachments": "Prilozi",
                "card": "Kartica",
                "details": "Detalji",
                "emails": "Email poruke",
                "history": "Povijest",
                "notes": "Napomene",
                "relations": "Relacije"
            }
        },
        "emails": {
            "addattachmentsfromdms": "<em>Add attachments from DMS</em>",
            "alredyexistfile": "<em>Already exists a file with this name</em>",
            "archivingdate": "Datum arhiviranja",
            "attachfile": "Priloži datoteku",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "Pripremi email",
            "composefromtemplate": "<em>Compose from template</em>",
            "delay": "Kašnjenje",
            "delays": {
                "day1": "Za 1 dan",
                "days2": "Za 2 dana",
                "days4": "Za 4 dana",
                "hour1": "1 sat",
                "hours2": "2 sata",
                "hours4": "4 sata",
                "month1": "Za 1 mjesec",
                "none": "Ništa",
                "week1": "Za 1 tjedan",
                "weeks2": "Za 2 tjedna"
            },
            "dmspaneltitle": "Odaberi prilog iz baze podataka",
            "edit": "Uredi",
            "from": "<em>Od<em>",
            "gridrefresh": "Osvježi grid",
            "keepsynchronization": "Zadrži sinkronizaciju",
            "message": "Poruka",
            "regenerateallemails": "Regeneriraj sve e-mail poruke",
            "regenerateemail": "Iznova napravi e-poštu",
            "remove": "Ukloni",
            "remove_confirmation": "Želite li sigurno obrisati ovaj email?",
            "reply": "odgovori",
            "replyprefix": "Na {0}, {1} napisao je:",
            "selectaclass": "<em>Select a class</em>",
            "sendemail": "Pošalji e-mail",
            "statuses": {
                "draft": "Skica",
                "outgoing": "Za slanje",
                "received": "Primljene",
                "sent": "Poslane"
            },
            "subject": "Naslov",
            "to": "Za",
            "view": "Pogled"
        },
        "errors": {
            "autherror": "Pogrešno korisničko ime i/ili lozinka",
            "classnotfound": "Klasa {0} ne postoji",
            "notfound": "Element nije pronađen"
        },
        "filters": {
            "actions": "Radnje",
            "addfilter": "Dodaj filter",
            "any": "Bilo koji",
            "attribute": "Izaberi atribut",
            "attributes": "Atributi",
            "clearfilter": "Očisti Filter",
            "clone": "Kloniraj",
            "copyof": "Kopija od",
            "description": "Opis",
            "domain": "Domena",
            "filterdata": "Filtriraj podatke",
            "fromselection": "Iz odabira",
            "ignore": "Ignoriraj",
            "migrate": "Migrira",
            "name": "Naziv",
            "newfilter": "Novi filter",
            "noone": "Nijedan",
            "operator": "Operater",
            "operators": {
                "beginswith": "Koji počinju sa",
                "between": "Između",
                "contained": "Sadržani",
                "containedorequal": "Sadržano ili jednako",
                "contains": "Koji sadrže",
                "containsorequal": "Sadrži ili jednako",
                "different": "Različit",
                "doesnotbeginwith": "Koji ne počinju sa",
                "doesnotcontain": "Koji ne sadrže",
                "doesnotendwith": "Ne završava sa",
                "endswith": "Završava sa",
                "equals": "Jednako",
                "greaterthan": "Veće od",
                "isnotnull": "Ne može biti null",
                "isnull": "Sa null vrijednošću",
                "lessthan": "Manje od"
            },
            "relations": "Relacije",
            "type": "Tip",
            "typeinput": "Ulazni Parametar",
            "value": "Vrijednost"
        },
        "gis": {
            "card": "Kartica",
            "cardsMenu": "<em>Cards Menu</em>",
            "externalServices": "Vanjski servisi",
            "geographicalAttributes": "Zemljopisni atributi",
            "geoserverLayers": "Geoserver slojevi",
            "layers": "Slojevi",
            "list": "Lista",
            "map": "Mapa",
            "mapServices": "<em>Map Services</em>",
            "position": "<em>Position</em>",
            "postition": "Pozicija",
            "root": "Izvorište",
            "tree": "Stablo",
            "view": "Prikazi",
            "zoom": "Zoom"
        },
        "history": {
            "activityname": "Naziv aktivnosti",
            "activityperformer": "Izvršitelj aktivnosti",
            "begindate": "Datum početka",
            "enddate": "Datum završetka",
            "processstatus": "Status",
            "user": "Korisnik"
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
                "login": "Prijava",
                "logout": "Promijeni korisnika"
            },
            "fields": {
                "group": "Grupa",
                "language": "Jezik",
                "password": "Lozinka",
                "tenants": "Zakupci",
                "username": "Korisničko ime"
            },
            "loggedin": "Prijavljen",
            "title": "Prijava",
            "welcome": "Dobrodošao/la {0}."
        },
        "main": {
            "administrationmodule": "Upravljački modul",
            "baseconfiguration": "<em>Base configuration</em>",
            "cardlock": {
                "lockedmessage": "Ne možete uređivati ovu karticu jer je {0} uređuje.",
                "someone": "netko"
            },
            "changegroup": "Promijeni grupu",
            "changepassword": "Promijeni lozinku",
            "changetenant": "Promijeni zakupca",
            "confirmchangegroup": "Jeste li sigurni da želite promijeniti grupu?",
            "confirmchangetenants": "Jeste li sigurni da želite promijeniti aktivne zakupce?",
            "confirmdisabletenant": "Jeste li sigurni da želite onemogućiti \"\"Ignoriraj zakupce\"\" oznaku?",
            "confirmenabletenant": "Jeste li sigurni da želite omogućiti \"\"Ignoriraj zakupce\"\" oznaku?",
            "confirmpassword": "Potvrdi lozinku",
            "ignoretenants": "Ignoriraj zakupce",
            "info": "Informacija",
            "logo": {
                "cmdbuild": "CMDBuild logotip",
                "cmdbuildready2use": "CMDbuild READY2USE logotip",
                "companylogo": "<em>Company logo</em>",
                "openmaint": "openMAINT logotip"
            },
            "logout": "Odjava",
            "managementmodule": "Modul za upravljanje podacima",
            "multigroup": "Višestruka grupa",
            "multitenant": "Višestruki zakupac",
            "navigation": "Navigacija",
            "newpassword": "Nova lozinka",
            "oldpassword": "Stara lozinka",
            "pagenotfound": "<em>Page not found</em>",
            "pleasecorrecterrors": "<em>Please correct indicated errors!</em>",
            "preferences": {
                "comma": "Zarez",
                "decimalserror": "Decimalno polje mora biti prisutno",
                "decimalstousandserror": "Separatori decimala i tisućica moraju biti različiti",
                "default": "<em>Default</em>",
                "defaultvalue": "Zadana vrijednost",
                "labeldateformat": "Format datuma",
                "labeldecimalsseparator": "Separator decimala",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "Jezik",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "Separator tisućica",
                "labeltimeformat": "Format vremena",
                "msoffice": "<em>Microsoft Office</em>",
                "period": "Točka",
                "preferredofficesuite": "<em>Preferred Office suite</em>",
                "space": "Razmak",
                "thousandserror": "Polje tisućica mora biti prisutno",
                "timezone": "<em>Timezone</em>",
                "twelvehourformat": "12-satni format",
                "twentyfourhourformat": "24-satni format"
            },
            "searchinallitems": "Traži u svim stavkama",
            "userpreferences": "Mogućnosti"
        },
        "menu": {
            "allitems": "Sve stavke",
            "classes": "Klase",
            "custompages": "Vlastite stranice",
            "dashboards": "Kontrolne ploče",
            "processes": "Procesi",
            "reports": "Izvještaji",
            "views": "Prikazi"
        },
        "notes": {
            "edit": "Izmjeni napomenu"
        },
        "notifier": {
            "attention": "Pažnja",
            "error": "Greška",
            "genericerror": "Generička greška",
            "genericinfo": "Opće informacije",
            "genericwarning": "Opće upozorenje",
            "info": "Informacija",
            "success": "Uspjeh",
            "warning": "Upozorenje"
        },
        "patches": {
            "apply": "<em>Apply patches</em>",
            "category": "<em>Category</em>",
            "description": "<em>Description</em>",
            "name": "<em>Name</em>",
            "patches": "<em>Patches</em>"
        },
        "processes": {
            "abortconfirmation": "Želite li sigurno prekinuti ovaj proces?",
            "abortprocess": "Prekini proces",
            "action": {
                "advance": "Dalje",
                "label": "Radnja"
            },
            "activeprocesses": "Aktivni procesi",
            "allstatuses": "Sve",
            "editactivity": "Izmjeni aktivnost",
            "openactivity": "Otvori aktivnost",
            "startworkflow": "Kreni",
            "workflow": "Tijek rada"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Kartica",
            "cardList": "Lista kartica",
            "cardRelation": "<em>Relation</em>",
            "cardRelations": "Kartične veze",
            "choosenaviagationtree": "Odaberi navigacijsko stablo",
            "class": "Klasa",
            "class:": "<em>Class</em>",
            "classList": "Lista klasa",
            "compoundnode": "<em>Compound Node</em>",
            "enableTooltips": "Uključi/isključi napomene na grafu",
            "level": "Razina",
            "openRelationGraph": "Otvori graf relacija",
            "qt": "Kol.",
            "refresh": "Osvježi",
            "relation": "relacija",
            "relationGraph": "Graf relacija",
            "reopengraph": "Otvori graf sa ovog čvora"
        },
        "relations": {
            "adddetail": "Dodaj detalje",
            "addrelations": "Dodaj relaciju",
            "attributes": "Atributi",
            "code": "Kod",
            "deletedetail": "Ukloni detalj",
            "deleterelation": "Ukloni relaciju",
            "description": "Opis",
            "editcard": "Izmjeni karticu",
            "editdetail": "Izmjeni detalje",
            "editrelation": "Izmjeni relaciju",
            "mditems": "stavke",
            "opencard": "Otvori pripadajuću karticu",
            "opendetail": "Prikaži detalje",
            "type": "Vrsta"
        },
        "reports": {
            "csv": "CSV",
            "download": "Preuzmi",
            "format": "Formatiraj",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Ispiši",
            "reload": "Ponovo učitaj",
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
                "addrow": "Dodaj red",
                "clonerow": "Kloniraj red",
                "deleterow": "Obriši red",
                "editrow": "Uredi red",
                "export": "Izvoz",
                "import": "Uvoz",
                "refresh": "Osvježi na zadane postavke"
            },
            "linkcards": {
                "editcard": "Uredi karticu",
                "opencard": "Otvori karticu",
                "refreshselection": "Primjeni zadani odabir",
                "togglefilterdisabled": "Omogući grid filter",
                "togglefilterenabled": "Onemogući grid filter"
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
(function() {
    Ext.define('CMDBuildUI.locales.sr.Locales', {
        "requires": ["CMDBuildUI.locales.sr.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "sr",
        "administration": CMDBuildUI.locales.sr.LocalesAdministration.administration,
        "attachments": {
            "add": "Dodaj prilog",
            "attachmenthistory": "Istorija priloga",
            "author": "Autor",
            "category": "Kategorija",
            "creationdate": "Datum kreiranja",
            "deleteattachment": "Obriši prilog",
            "deleteattachment_confirmation": "Zaista želite da uklonite prilog?",
            "description": "Opis",
            "download": "Preuzmi",
            "editattachment": "Modifikuj prilog",
            "file": "Datoteka",
            "filename": "Naziv datoteke",
            "majorversion": "Glavna verzija",
            "modificationdate": "Datum izmene",
            "uploadfile": "Pošalji datoteku…",
            "version": "Verzija",
            "viewhistory": "Prikaži istoriju priloga"
        },
        "bim": {
            "bimViewer": "Bim prikaz",
            "card": {
                "label": "Kartica"
            },
            "layers": {
                "label": "Slojevi",
                "menu": {
                    "hideAll": "Sakrij sve",
                    "showAll": "Prikaži sve"
                },
                "name": "Naziv",
                "qt": "Qt",
                "visibility": "Vidljivost",
                "visivility": "Vidljivost"
            },
            "menu": {
                "camera": "Kamera",
                "frontView": "Prikaz spreda",
                "mod": "Kontrole prikaza",
                "orthographic": "Ortografska kamera",
                "pan": "Pomeranje",
                "perspective": "Perspektivna kamera",
                "resetView": "Resetuj prikaz",
                "rotate": "Rotiraj",
                "sideView": "Prikaz sa strane",
                "topView": "Prikaz od gore"
            },
            "showBimCard": "Otvori 3D prikaz",
            "tree": {
                "arrowTooltip": "Izaberi element",
                "columnLabel": "Stablo",
                "label": "Stablo",
                "open_card": "Otvori pripadajuću karticu",
                "root": "Ifc koren"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Dodaj karticu",
                "clone": "Kloniraj",
                "clonewithrelations": "Kloniraj karticu i relacije",
                "deletecard": "Ukloni karticu",
                "deleteconfirmation": "Zaista želite da obrišete karticu?",
                "label": "Kartice podataka",
                "modifycard": "Izmeni karticu",
                "opencard": "Otvori karticu",
                "print": "Štampaj karticu"
            },
            "simple": "Jednostavna",
            "standard": "Standardna"
        },
        "common": {
            "actions": {
                "add": "Dodaj",
                "apply": "Primeni",
                "cancel": "Odustani",
                "close": "Zatvori",
                "delete": "Ukloni",
                "edit": "Izmeni",
                "execute": "Izvrši",
                "refresh": "Osveži podatke",
                "remove": "Ukloni",
                "save": "Snimi",
                "saveandapply": "Snimi i primeni",
                "saveandclose": "Snimi i zatvori",
                "search": "Pretraga",
                "searchtext": "Pretraga…"
            },
            "attributes": {
                "nogroup": "Osnovni podaci"
            },
            "dates": {
                "date": "d/m/Y",
                "datetime": "d/m/Y H:i:s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "Čist HTML"
            },
            "grid": {
                "disablemultiselection": "Onemogući višestruko selektovanje",
                "enamblemultiselection": "Omogući višestruko selektovanje",
                "export": "Izvezi podatke",
                "filterremoved": "Trenutni filter je uklonjen",
                "import": "Uvezi podatke",
                "itemnotfound": "Element nije pronađen",
                "list": "Lista",
                "opencontextualmenu": "Otvori kontekstni meni",
                "print": "Štampaj",
                "printcsv": "Štampaj kao CSV",
                "printodt": "Štampaj kao ODT",
                "printpdf": "Štampaj kao PDF",
                "row": "Stavka",
                "rows": "Stavke",
                "subtype": "Podtip"
            },
            "tabs": {
                "activity": "Aktivnost",
                "attachments": "Prilozi",
                "card": "Kartica",
                "details": "Detalji",
                "emails": "E-mailovi",
                "history": "Istorija",
                "notes": "Napomene",
                "relations": "Relacije"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Dodaj prilog iz DMS",
            "alredyexistfile": "Datoteka s ovim imenom već postoji",
            "archivingdate": "Datum arhiviranja",
            "attachfile": "Priloži datoteku",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "Kreiraj e-poštu",
            "composefromtemplate": "Kreiraj iz obrasca",
            "delay": "Odlaganje",
            "delays": {
                "day1": "Za 1 dan",
                "days2": "Za 2 dana",
                "days4": "Za 4 dana",
                "hour1": "1 sat",
                "hours2": "2 sata",
                "hours4": "4 sata",
                "month1": "Za 1 mesec",
                "none": "Bez",
                "week1": "Za 1 nedelju",
                "weeks2": "Za 2 nedelju"
            },
            "dmspaneltitle": "Izaberi prilog iz baze podataka",
            "edit": "Izmeni",
            "from": "Od",
            "gridrefresh": "Osveži mrežu",
            "keepsynchronization": "Održavaj sinhronizovano",
            "message": "Poruka",
            "regenerateallemails": "Generiši sve emailove ponovo",
            "regenerateemail": "Iznova generiši e-poštu",
            "remove": "Ukloni",
            "remove_confirmation": "Zaista želite da uklonite ovaj e-mail?",
            "reply": "Odgovori",
            "replyprefix": "{0}, {1} je napisao",
            "selectaclass": "Izaberi klasu",
            "sendemail": "Pošalji e-poštu",
            "statuses": {
                "draft": "Započete",
                "outgoing": "Za slanje",
                "received": "Primljene",
                "sent": "Poslane"
            },
            "subject": "Subjekat",
            "to": "Za",
            "view": "Prikaz"
        },
        "errors": {
            "autherror": "Pogrešno korisničko ime i/ili lozinka",
            "classnotfound": "Klasa {0} ne postoji",
            "notfound": "Element nije pronađen"
        },
        "filters": {
            "actions": "Akcije",
            "addfilter": "Dodaj filter",
            "any": "Bilo koji",
            "attribute": "Izaberi atribut",
            "attributes": "Atributi",
            "clearfilter": "Očisti filter",
            "clone": "Kloniraj",
            "copyof": "Kopija",
            "description": "Opis",
            "domain": "Relacija",
            "filterdata": "Filtriraj podatke",
            "fromselection": "Iz selekcije",
            "ignore": "Ignoriši",
            "migrate": "Premešta",
            "name": "Naziv",
            "newfilter": "Novi filter",
            "noone": "Nijedan",
            "operator": "Operator",
            "operators": {
                "beginswith": "Koji počinju sa",
                "between": "Između",
                "contained": "Sadržan",
                "containedorequal": "Sadržan ili jednak",
                "contains": "Sadrži",
                "containsorequal": "Sadrži ili je jednak",
                "different": "Različite od",
                "doesnotbeginwith": "Koji ne počinju sa",
                "doesnotcontain": "Koji ne sadrže",
                "doesnotendwith": "Ne završava sa",
                "endswith": "Završava sa",
                "equals": "Jednake",
                "greaterthan": "Veće",
                "isnotnull": "Ne može biti null",
                "isnull": "Sa null vrednošću",
                "lessthan": "Manje"
            },
            "relations": "Relacije",
            "type": "Tip",
            "typeinput": "Ulazni parametar",
            "value": "Vrednosti"
        },
        "gis": {
            "card": "Kartica",
            "cardsMenu": "Meni kartica",
            "externalServices": "Spoljni servisi",
            "geographicalAttributes": "Geografski atributi",
            "geoserverLayers": "Geoserver slojevi",
            "layers": "Slojevi",
            "list": "Lista",
            "map": "Mapa",
            "mapServices": "Servisi mapa",
            "position": "Pozicija",
            "root": "Koren",
            "tree": "Stablo navigacije",
            "view": "Prikaz",
            "zoom": "Uvećanje"
        },
        "history": {
            "activityname": "Naziv aktivnosti",
            "activityperformer": "Izvršilac aktivnosti",
            "begindate": "Datum početka",
            "enddate": "Datum završetka",
            "processstatus": "Status",
            "user": "Korisnik"
        },
        "importexport": {
            "downloadreport": "Preuzmi izveštaj",
            "emailfailure": "Greška prilikom slanja e-maila",
            "emailsubject": "Uvezi izveštaj s podacima",
            "emailsuccess": "E-maili je uspešno poslan",
            "export": "Izvezi",
            "import": "Uvezi",
            "importresponse": "Uvezi odgovor",
            "response": {
                "created": "Kreirane stavke",
                "deleted": "Obrisane stavke",
                "errors": "Greške",
                "linenumber": "Broj linije",
                "message": "Poruka",
                "modified": "Izmenjene stavke",
                "processed": "Obrađeni redovi",
                "recordnumber": "Broj zapisa",
                "unmodified": "Neizmenjene stavke"
            },
            "sendreport": "Pošalji izveštaj",
            "template": "Šablon",
            "templatedefinition": "<em>Template definition</em>"
        },
        "login": {
            "buttons": {
                "login": "Prijavi se",
                "logout": "Promeni korisnika"
            },
            "fields": {
                "group": "Grupa",
                "language": "Jezik",
                "password": "Lozinka",
                "tenants": "Klijenti",
                "username": "Korisničko ime"
            },
            "loggedin": "Prijavljen",
            "title": "Prijavi se",
            "welcome": "Dobro došli nazad, {0}"
        },
        "main": {
            "administrationmodule": "Administracioni modul",
            "baseconfiguration": "Osnovna konfiguracija",
            "cardlock": {
                "lockedmessage": "Ne možete menjati ovu karticu jer je trenutno menja {0}",
                "someone": "neko"
            },
            "changegroup": "Promeni grupu",
            "changepassword": "Promeni lozinku",
            "changetenant": "Promeni klijenta",
            "confirmchangegroup": "Zaista želite da promenite grupu?",
            "confirmchangetenants": "Zaista želite da promenite aktivnog klijenta?",
            "confirmdisabletenant": "Zaista želite da isključite oznaku „Ignoriši klijente“?",
            "confirmenabletenant": "Zaista želite da uključite oznaku „Ignoriši klijente“?",
            "confirmpassword": "Potvrdi lozinku",
            "ignoretenants": "Ignoriši klijente",
            "info": "Informacija",
            "logo": {
                "cmdbuild": "CMDBuild logotip",
                "cmdbuildready2use": "CMDBuild READY2USE logotip",
                "companylogo": "Kompanijski logotip",
                "openmaint": "openMAINT logotip"
            },
            "logout": "Izađi",
            "managementmodule": "Modul za upravljanje podacima",
            "multigroup": "Više grupa",
            "multitenant": "Više klijenata",
            "navigation": "Navigacija",
            "newpassword": "Nova lozinka",
            "oldpassword": "Stara lozinka",
            "pagenotfound": "Stranica nije pronađena",
            "pleasecorrecterrors": "Molimo korigujte navedene greške!",
            "preferences": {
                "comma": "Zapeta",
                "decimalserror": "Decimalni deo mora postojati",
                "decimalstousandserror": "Decimalni separator i separator hiljada ne smeju biti isti",
                "default": "Podrazumevani",
                "defaultvalue": "Podrazumevana vrednost",
                "labeldateformat": "Format datuma",
                "labeldecimalsseparator": "Decimalni separator",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "Jezik",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "Separator hiljada",
                "labeltimeformat": "Format vremena",
                "msoffice": "Microsoft Office",
                "period": "Tačka",
                "preferredofficesuite": "Preferirani paket kancelarijskih aplikacija",
                "space": "Razmak",
                "thousandserror": "Hiljade moraju biti prisutne",
                "timezone": "Vremenska zona",
                "twelvehourformat": "12-časovni format",
                "twentyfourhourformat": "24-časovni format"
            },
            "searchinallitems": "Pretraga kroz sve stavke",
            "userpreferences": "Podešavanja"
        },
        "menu": {
            "allitems": "Sve stavke",
            "classes": "Klase",
            "custompages": "Posebne stranice",
            "dashboards": "Kontrolne table",
            "processes": "Kartice procesa",
            "reports": "izveštaji",
            "views": "Prikazi"
        },
        "notes": {
            "edit": "Izmeni napomenu"
        },
        "notifier": {
            "attention": "Pažnja",
            "error": "Greška",
            "genericerror": "Generička greška",
            "genericinfo": "Generička informacija",
            "genericwarning": "Generičko upozorenje",
            "info": "Informacija",
            "success": "Uspeh",
            "warning": "Pažnja"
        },
        "patches": {
            "apply": "Primeni ispravke",
            "category": "Kategorija",
            "description": "Opis",
            "name": "Naziv",
            "patches": "Ispravke"
        },
        "processes": {
            "abortconfirmation": "Da li ste sigurni da želite prekinuti proces?",
            "abortprocess": "Prekini proces",
            "action": {
                "advance": "Dalje",
                "label": "Akcija"
            },
            "activeprocesses": "Aktivni procesi",
            "allstatuses": "Sve",
            "editactivity": "Izmeni aktivnost",
            "openactivity": "Otvori aktivnost",
            "startworkflow": "Start",
            "workflow": "Radni procesi"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Kartica",
            "cardList": "Lista kartica",
            "cardRelation": "Veza",
            "cardRelations": "Veza",
            "choosenaviagationtree": "Izaberi stablo navigacije",
            "class": "Klasa",
            "class:": "Klasa",
            "classList": "Lista klasa",
            "compoundnode": "Složeni čvor",
            "enableTooltips": "Uključi/isključi pomoć (tooltip) na grafu",
            "level": "Nivo",
            "openRelationGraph": "Otvori graf relacija",
            "qt": "Qt",
            "refresh": "Osveži",
            "relation": "Veza",
            "relationGraph": "Graf relacija",
            "reopengraph": "Ponovo otvori graf od ovog čvora"
        },
        "relations": {
            "adddetail": "Dodaj detalje",
            "addrelations": "Dodaj relaciju",
            "attributes": "Atributi",
            "code": "Kod",
            "deletedetail": "Ukloni detalj",
            "deleterelation": "Ukloni relaciju",
            "description": "Opis",
            "editcard": "Izmeni karticu",
            "editdetail": "Izmeni detalje",
            "editrelation": "Izmeni relaciju",
            "mditems": "stavke",
            "opencard": "Otvori pripadajuću karticu",
            "opendetail": "Prikaži detalje",
            "type": "Tip"
        },
        "reports": {
            "csv": "CSV",
            "download": "Preuzmi",
            "format": "Formatiraj",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Štampaj",
            "reload": "Ponovo učitaj",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "<em>Add Thematism</em>",
            "analysisType": "Tip analize",
            "attribute": "Atribut",
            "calculateRules": "<em>Generate style rules</em>",
            "clearThematism": "<em>Clear Thematism</em>",
            "color": "Boja",
            "defineLegend": "<em>Legend definition</em>",
            "function": "Funkcija",
            "generate": "Generiši",
            "geoAttribute": "<em>geoAttribute</em>",
            "graduated": "Diplomirao",
            "highlightSelected": "Označi selektovanu stavku",
            "intervals": "Intervali",
            "legend": "legenda",
            "name": "<em>name</em>",
            "newThematism": "<em>New Thematism</em>",
            "punctual": "Tačan",
            "quantity": "Broj (kvantitet)",
            "source": "Izvor",
            "table": "Tabela",
            "thematism": "Tematike",
            "value": "Vrednost"
        },
        "widgets": {
            "customform": {
                "addrow": "Dodaj red",
                "clonerow": "Kloniraj red",
                "deleterow": "Obriši red",
                "editrow": "Izmeni red",
                "export": "Izvezi",
                "import": "Uvezi",
                "refresh": "Vrati na podrazumevane vrednosti"
            },
            "linkcards": {
                "editcard": "Izmeni karticu",
                "opencard": "Otvori karticu",
                "refreshselection": "Primeni podrazumevani izbor",
                "togglefilterdisabled": "Isključi filtriranje tabele",
                "togglefilterenabled": "Uključi filtriranje tabele"
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
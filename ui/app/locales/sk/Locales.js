(function() {
    Ext.define('CMDBuildUI.locales.sk.Locales', {
        "requires": ["CMDBuildUI.locales.sk.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "sk",
        "administration": CMDBuildUI.locales.sk.LocalesAdministration.administration,
        "attachments": {
            "add": "Pridať prílohu",
            "attachmenthistory": "História prílohy",
            "author": "Autor",
            "category": "Kategória",
            "creationdate": "Dátum vytvorenia",
            "deleteattachment": "Zmazať prílohu",
            "deleteattachment_confirmation": "Ste si istí, že chcete zmazať túto prílohu?",
            "description": "Popis",
            "download": "Stiahnuť",
            "editattachment": "Upraviť prílohu",
            "file": "Súbor",
            "filename": "Názov súboru",
            "majorversion": "Hlavná verzia",
            "modificationdate": "Dátum zmeny",
            "uploadfile": "Nahrať súbor...",
            "version": "Verzia",
            "viewhistory": "Zobraziť históriu prílohy"
        },
        "bim": {
            "bimViewer": "BIM Prehliadač",
            "card": {
                "label": "Karta"
            },
            "layers": {
                "label": "Vrstvy",
                "menu": {
                    "hideAll": "Skryť všetky",
                    "showAll": "Zobraziť všetky"
                },
                "name": "Názov",
                "qt": "Mn",
                "visibility": "Viditeľnosť"
            },
            "menu": {
                "camera": "Kamera",
                "frontView": "Pohľad spredu",
                "mod": "Ovládacie prvky prehliadača",
                "orthographic": "Ortografická kamera",
                "pan": "Rolovanie",
                "perspective": "Perspektívna kamera",
                "resetView": "Obnoviť pohľad",
                "rotate": "Otočiť",
                "sideView": "Podľad z boku",
                "topView": "Pohľad zhora"
            },
            "showBimCard": "Otvoriť 3D prehliadač",
            "tree": {
                "arrowTooltip": "TODO",
                "columnLabel": "Hierarchicky",
                "label": "Hierarchicky",
                "open_card": "Otvoriť príslušnú kartu",
                "root": "Ifc Root"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Pridať kartu",
                "clone": "Klonovať",
                "clonewithrelations": "Klonovať kartu a prepojenia",
                "deletecard": "Zmazať kartu",
                "deleteconfirmation": "Ste si istí, že chcete zamazať túto kartu?",
                "label": "Karty",
                "modifycard": "Upraviť kartu",
                "opencard": "Otvoriť kartu",
                "print": "Vytlačiť kartu"
            },
            "simple": "Jednoduchá",
            "standard": "Štandardná"
        },
        "common": {
            "actions": {
                "add": "Pridať",
                "apply": "Použiť",
                "cancel": "Zrušiť",
                "close": "Zavrieť",
                "delete": "Zmazať",
                "edit": "Upraviť",
                "execute": "Vykonať",
                "refresh": "Obnoviť údaje",
                "remove": "Odstrániť",
                "save": "Uložiť",
                "saveandapply": "Uložiť a použiť",
                "saveandclose": "Uložiť a zavrieť",
                "search": "Vyhľadávanie",
                "searchtext": "Vyhľadať..."
            },
            "attributes": {
                "nogroup": "Základné údaje"
            },
            "dates": {
                "date": "d/m/R",
                "datetime": "d/m/R H:m:s",
                "time": "H:m:s"
            },
            "editor": {
                "clearhtml": "Clear HTML"
            },
            "grid": {
                "disablemultiselection": "Zakázať výber viacerých položiek",
                "enamblemultiselection": "Povoliť výber viacerých položiek",
                "export": "Exportovať údaje",
                "filterremoved": "Aktuálny filter bol odstránený",
                "import": "Importovať údaje",
                "itemnotfound": "Položka sa nenašla",
                "list": "Zoznam",
                "opencontextualmenu": "Otvoriť kontextové menu",
                "print": "Tlačiť",
                "printcsv": "Tlačiť ako CSV",
                "printodt": "Tlačiť ako ODT",
                "printpdf": "Tlačiť ako PDF",
                "row": "Položka",
                "rows": "Položky",
                "subtype": "Podtyp"
            },
            "tabs": {
                "activity": "Činnosť",
                "attachments": "Prílohy",
                "card": "Karta",
                "details": "Podrobnosti",
                "emails": "E-Maily",
                "history": "História",
                "notes": "Poznámky",
                "relations": "Prepojenia"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Pridať prílohy z DMS",
            "alredyexistfile": "Súbor s týmto názvom už existuje",
            "archivingdate": "Dátum archivovania",
            "attachfile": "Priložiť súbor",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "Vytvoriť e-mail",
            "composefromtemplate": "Vytvoriť zo šablóny",
            "delay": "Oneskorenie",
            "delays": {
                "day1": "Za 1 deň",
                "days2": "Za 2 dni",
                "days4": "Za 4 dni",
                "hour1": "1 hodinu",
                "hours2": "2 hodiny",
                "hours4": "4 hodiny",
                "month1": "Za 1 mesiac",
                "none": "Žiadne",
                "week1": "Za 1 týždeň",
                "weeks2": "Za 2 týždne"
            },
            "dmspaneltitle": "Vyberte prílohy z databázy",
            "edit": "Upraviť",
            "from": "Od",
            "gridrefresh": "Obnoviť mriežku",
            "keepsynchronization": "Ponechať synchronizáciu",
            "message": "Správa",
            "regenerateallemails": "Obnoviť všetky E-maily",
            "regenerateemail": "Obnoviť E-mail",
            "remove": "Odstrániť",
            "remove_confirmation": "Naozaj chcete odstrániť tento e-mail?",
            "reply": "odpovedať",
            "replyprefix": "Na {0}, {1} napísal:",
            "selectaclass": "Vyberte triedu",
            "sendemail": "odoslať E-Mail",
            "statuses": {
                "draft": "Návrh",
                "outgoing": "Odosielané",
                "received": "Prijaté",
                "sent": "Odoslané"
            },
            "subject": "Predmet",
            "to": "Komu",
            "view": "Náhľad"
        },
        "errors": {
            "autherror": "Chybné uživateľské meno alebo heslo",
            "classnotfound": "Trieda {0} sa nenašla",
            "notfound": "Položka nebola nájdená"
        },
        "filters": {
            "actions": "Funkcie",
            "addfilter": "Pridať filter",
            "any": "Ktorý koľvek",
            "attribute": "Zvoliť atribút",
            "attributes": "Atribúty",
            "clearfilter": "Vynulovať filter",
            "clone": "Klonovať",
            "copyof": "Kópia",
            "description": "Popis",
            "domain": "Doména",
            "filterdata": "Filtrovať údaje",
            "fromselection": "Z výberu",
            "ignore": "Ignorovať",
            "migrate": "Migrovať",
            "name": "Názov",
            "newfilter": "Nový filter",
            "noone": "Žiadny",
            "operator": "Pravidlá",
            "operators": {
                "beginswith": "Začína s",
                "between": "Medzi",
                "contained": "Obsiahnutý",
                "containedorequal": "Obsiahnutý alebo rovný",
                "contains": "Obsahuje",
                "containsorequal": "Obsahuje alebo rovné",
                "different": "Nezhodný",
                "doesnotbeginwith": "Nezačína s",
                "doesnotcontain": "Neobsahuje",
                "doesnotendwith": "Nekončí s",
                "endswith": "Končí s",
                "equals": "Rovná sa",
                "greaterthan": "Väčší než",
                "isnotnull": "Nie je prázdny",
                "isnull": "Je prázdny",
                "lessthan": "Menej než"
            },
            "relations": "Prepojenia",
            "type": "Typ",
            "typeinput": "Vstupný parameter",
            "value": "Hodnota"
        },
        "gis": {
            "card": "Karta",
            "cardsMenu": "Menu Máp",
            "externalServices": "Externé služby",
            "geographicalAttributes": "Geografické atribúty",
            "geoserverLayers": "Geoserver vrstvy",
            "layers": "Vrstvy",
            "list": "Zoznam",
            "map": "Mapa",
            "mapServices": "Mapové služby",
            "position": "Pozícia",
            "root": "Hlavný",
            "tree": "Strom",
            "view": "Náhľad",
            "zoom": "Zoom"
        },
        "history": {
            "activityname": "Názov činnosti",
            "activityperformer": "Vykonávajúci činnosť",
            "begindate": "Dátum začiatku",
            "enddate": "Dátum ukončenia",
            "processstatus": "Status",
            "user": "Užívateľ"
        },
        "importexport": {
            "downloadreport": "Prevziať správu",
            "emailfailure": "Pri odosielaní e-mailu sa vyskytla chyba!",
            "emailsubject": "Import údajov správy",
            "emailsuccess": "E-mail bol úspešne odoslaný!",
            "export": "Export",
            "import": "Import",
            "importresponse": "Importovať odpoveď",
            "response": {
                "created": "Vytvorené položky",
                "deleted": "Zmazané položky",
                "errors": "Chyby",
                "linenumber": "Číslo riadku",
                "message": "Správa",
                "modified": "Upravené položky",
                "processed": "Spracované riadky",
                "recordnumber": "Číslo záznamu",
                "unmodified": "Nezmenené položky"
            },
            "sendreport": "Odoslať správu",
            "template": "Šablóna",
            "templatedefinition": "<em>Template definition</em>"
        },
        "login": {
            "buttons": {
                "login": "Prihlásiť",
                "logout": "Zmeniť užívateľa"
            },
            "fields": {
                "group": "Skupina",
                "language": "Jazyk",
                "password": "Heslo",
                "tenants": "Užívatelia",
                "username": "Užívateľské meno"
            },
            "loggedin": "Prihlásený",
            "title": "Prihlásenie",
            "welcome": "Vitaj späť {0}."
        },
        "main": {
            "administrationmodule": "Administračný modul",
            "baseconfiguration": "Základná konfigurácia",
            "cardlock": {
                "lockedmessage": "Túto kartu nemôžete upraviť, pretože {0} sa práve edituje.",
                "someone": "niekto"
            },
            "changegroup": "Zmeniť skupinu",
            "changepassword": "Zmeniť heslo",
            "changetenant": "Zmeniť Užívateľa",
            "confirmchangegroup": "Ste si istí, že chcete zmeniť skupinu?",
            "confirmchangetenants": "Ste si istí, že chcete zmeniť aktívnych Užívateľov?",
            "confirmdisabletenant": "Naozaj chcete vypnúť príznak \"Ignorovať Užívateľov\"?",
            "confirmenabletenant": "Naozaj chcete zapnúť príznak \"Ignorovať Užívateľov\"?",
            "confirmpassword": "Potvrdiť heslo",
            "ignoretenants": "Ignorovať Užívateľov",
            "info": "Info",
            "logo": {
                "cmdbuild": "CMDBuild logo",
                "cmdbuildready2use": "CMDBuild READY2USE logo",
                "companylogo": "Logo spoločnosti",
                "openmaint": "openMAINT logo"
            },
            "logout": "Odhlásiť sa",
            "managementmodule": "Modul správy údajov",
            "multigroup": "Viacnásobná skupina",
            "multitenant": "Viacužívateľský prístup",
            "navigation": "Navigácia",
            "newpassword": "Nové heslo",
            "oldpassword": "Staré heslo",
            "pagenotfound": "Stránka nenájdená",
            "pleasecorrecterrors": "Opravte uvedené chyby!",
            "preferences": {
                "comma": "Desatinná Čiarka",
                "decimalserror": "Pole desatinných miest musí byť prítomné",
                "decimalstousandserror": "Oddeľovač desatinných miest a tisícok musí byť odlišný",
                "default": "Predvolený",
                "defaultvalue": "Predvolená hodnota",
                "labeldateformat": "Formát dátumu",
                "labeldecimalsseparator": "Oddeľovač desatinných miest",
                "labellanguage": "Jazyk",
                "labelthousandsseparator": "Oddeľovač tisícok",
                "labeltimeformat": "Formát času",
                "msoffice": "Microsoft Office",
                "period": "Obdobie",
                "preferredofficesuite": "Preferovaný balík Office",
                "space": "Medzera",
                "thousandserror": "Pole tisícok musí byť prítomné",
                "timezone": "Časové pásmo",
                "twelvehourformat": "12-hodinový formát",
                "twentyfourhourformat": "24-hodinový formát"
            },
            "searchinallitems": "Vyhľadávať vo všetkých položkách",
            "userpreferences": "Predvoľby užívateľa"
        },
        "menu": {
            "allitems": "Všetky položky",
            "classes": "Triedy",
            "custompages": "Vlastné stránky",
            "dashboards": "Info Panely",
            "processes": "Procesy",
            "reports": "Reporty",
            "views": "Pohľady"
        },
        "notes": {
            "edit": "Upraviť poznámky"
        },
        "notifier": {
            "attention": "Upozornenie",
            "error": "Chyba",
            "genericerror": "Všeobecná chyba",
            "genericinfo": "Všeobecné info",
            "genericwarning": "Všeobecné upozornenie",
            "info": "Info",
            "success": "Výsledok",
            "warning": "Výstraha"
        },
        "patches": {
            "apply": "Použite opravy",
            "category": "Kategória",
            "description": "Popis",
            "name": "Názov",
            "patches": "Opravy"
        },
        "processes": {
            "abortconfirmation": "Ste si istí, že chcete prerušiť tento proces?",
            "abortprocess": "Prerušiť proces",
            "action": {
                "advance": "Ďalej",
                "label": "Vykonať"
            },
            "activeprocesses": "Aktívne procesy",
            "allstatuses": "Všetky",
            "editactivity": "Upraviť aktivitu",
            "openactivity": "Otvoriť aktivitu",
            "startworkflow": "Štart",
            "workflow": "Workflow"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Karta",
            "cardList": "Zoznam kariet",
            "cardRelations": "Karta prepojení",
            "choosenaviagationtree": "Vyberte strom navigácie",
            "class": "Trieda",
            "classList": "Zoznam tried",
            "compoundnode": "Zložený uzol",
            "enableTooltips": "Povoliť alebo zakázať popisky v grafe",
            "level": "Úroveň",
            "openRelationGraph": "Otvoriť graf prepojenia",
            "qt": "Mn",
            "refresh": "Obnoviť",
            "relation": "prepojenie",
            "relationGraph": "Graf prepojení",
            "reopengraph": "Znovu otvoriť graf z tohto uzla"
        },
        "relations": {
            "adddetail": "Pridať podrobnosti",
            "addrelations": "Pridať prepojenia",
            "attributes": "Attribúty",
            "code": "Kód",
            "deletedetail": "Odstrániť podrobnosti",
            "deleterelation": "Odstrániť prepojenie",
            "description": "Popis",
            "editcard": "Upraviť kartu",
            "editdetail": "Upraviť podrobnosti",
            "editrelation": "Upraviť prepojenie",
            "mditems": "položky",
            "opencard": "Otvoriť súvisiacu kartu",
            "opendetail": "Zobraziť podrobnosti",
            "type": "Typ"
        },
        "reports": {
            "csv": "CSV",
            "download": "Stiahnuť",
            "format": "Formát",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Tlačiť",
            "reload": "Opäť načítať",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "<em>Add Thematism</em>",
            "analysisType": "Typ analýzy",
            "attribute": "Atribút",
            "calculateRules": "<em>Generate style rules</em>",
            "clearThematism": "<em>Clear Thematism</em>",
            "color": "Farba",
            "defineLegend": "<em>Legend definition</em>",
            "defineThematism": "<em>Thematism definition</em>",
            "function": "Funkcia",
            "generate": "Generovať",
            "geoAttribute": "<em>geoAttribute</em>",
            "graduated": "Delená",
            "highlightSelected": "Zvýrazniť vybratú položku",
            "intervals": "Intervaly",
            "legend": "legenda",
            "name": "<em>name</em>",
            "newThematism": "<em>New Thematism</em>",
            "punctual": "Presné",
            "quantity": "Množstvo",
            "source": "Zdroj",
            "table": "Tabuľka",
            "thematism": "Tématické",
            "value": "Hodnota"
        },
        "widgets": {
            "customform": {
                "addrow": "Pridať riadok",
                "clonerow": "Klonovať riadok",
                "deleterow": "Odstrániť riadok",
                "editrow": "Upraviť riadok",
                "export": "Export",
                "import": "Import",
                "refresh": "Obnoviť predvolené hodnoty"
            },
            "linkcards": {
                "editcard": "Upraviť kartu",
                "opencard": "Otvoriť kartu",
                "refreshselection": "Použiť predvolený výber",
                "togglefilterdisabled": "Zapnúť grid filter",
                "togglefilterenabled": "Vypnúť grid filter"
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
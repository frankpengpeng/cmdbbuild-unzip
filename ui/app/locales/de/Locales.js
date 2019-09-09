(function() {
    Ext.define('CMDBuildUI.locales.de.Locales', {
        "requires": ["CMDBuildUI.locales.de.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "de",
        "administration": CMDBuildUI.locales.de.LocalesAdministration.administration,
        "attachments": {
            "add": "Anlage hinzufügen",
            "attachmenthistory": "Anhangsverlauf",
            "author": "Autor",
            "category": "Kategorie",
            "creationdate": "Erstellungsdatum",
            "deleteattachment": "Anlage löschen",
            "deleteattachment_confirmation": "Möchten Sie die Anlage wirklich löschen?",
            "description": "Beschreibung",
            "download": "Herunterladen",
            "editattachment": "Anlage bearbeiten",
            "file": "Datei",
            "filename": "File-Name",
            "majorversion": "Hauptversion",
            "modificationdate": "Änderungsdatum",
            "uploadfile": "Datei hochladen",
            "version": "Version",
            "viewhistory": "Anhangsverlauf sehen"
        },
        "bim": {
            "bimViewer": "Bim Viewer",
            "card": {
                "label": "Karte>"
            },
            "layers": {
                "label": "Schichten",
                "menu": {
                    "hideAll": "Alle verstecken",
                    "showAll": "Alle zeigen"
                },
                "name": "Name",
                "qt": "Qt",
                "visibility": "Sichtbarkeit",
                "visivility": "Sichtbarkeit"
            },
            "menu": {
                "camera": "Kamera",
                "frontView": "Vorderansicht",
                "mod": "Viewer-Kontrolle",
                "orthographic": "Orthogonale Kamera",
                "pan": "Schwenken",
                "perspective": "Perspektivenkamera",
                "resetView": "Ansicht zurücksetzen",
                "rotate": "Rotieren",
                "sideView": "Seitenansicht",
                "topView": "Draufsicht"
            },
            "showBimCard": "3D-Viewer aufmachen",
            "tree": {
                "arrowTooltip": "TODO",
                "columnLabel": "Baum",
                "label": "Baum",
                "open_card": "Verbundene Karte öffnen",
                "root": "Ifc-Wurzel"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Karte hinzufügen",
                "clone": "Klonen",
                "clonewithrelations": "Karte und entsprechende Beziehungen klonen",
                "deletecard": "Karte löschen",
                "deleteconfirmation": "Möchten Sie diese Karte wirklich löschen?",
                "label": "Datenkarten",
                "modifycard": "Karte bearbeiten",
                "opencard": "Karte offen",
                "print": "Karte ausdrucken"
            },
            "simple": "Einfach",
            "standard": "Standard"
        },
        "common": {
            "actions": {
                "add": "Hinzufügen",
                "apply": "Anwenden",
                "cancel": "Abbrechen",
                "close": "Schließen",
                "delete": "Löschen",
                "edit": "Bearbeiten",
                "execute": "Durchführen",
                "refresh": "Daten aktualisieren",
                "remove": "Löschen",
                "save": "Bestätigen",
                "saveandapply": "Speichern und Anwenden",
                "saveandclose": "Speichern und schließen",
                "search": "Suchen",
                "searchtext": "Suchen..."
            },
            "attributes": {
                "nogroup": "Basisdaten"
            },
            "dates": {
                "date": "d/m/Y",
                "datetime": "d/m/Y H:i:s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "HTML löschen"
            },
            "grid": {
                "disablemultiselection": "Mehrfachauswahl deaktivieren",
                "enamblemultiselection": "Mehrfachauswahl aktivieren",
                "export": "Exportieren von Daten",
                "filterremoved": "Dieser Filter wurde entfernt",
                "import": "Importieren von Daten",
                "itemnotfound": "Element nicht gefunden",
                "list": "Liste",
                "opencontextualmenu": "Kontextmenü öffnen",
                "print": "Ausdrucken",
                "printcsv": "als CSV drucken",
                "printodt": "als ODT drucken",
                "printpdf": "als PDF drucken",
                "row": "Element",
                "rows": "Elemente",
                "subtype": "Untertyp"
            },
            "tabs": {
                "activity": "Aktivität",
                "attachments": "Anlagen",
                "card": "Karte",
                "details": "Details",
                "emails": "E-mails",
                "history": "Chronologie",
                "notes": "Notizen",
                "relations": "Beziehungen"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Anhänge aus DMS hinzufügen",
            "alredyexistfile": "Eine Datei mit diesem Namen existiert schon",
            "archivingdate": "Archivierungsdatum",
            "attachfile": "File beilegen",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "E-Mail schreiben",
            "composefromtemplate": "Vom Template bilden",
            "delay": "Verzögerung",
            "delays": {
                "day1": "In 1 Tag",
                "days2": "In 2 Tagen",
                "days4": "In 4 Tagen",
                "hour1": "1 Stunde",
                "hours2": "2 Stunden",
                "hours4": "4 Stunden",
                "month1": "In 1 Monat",
                "none": "Keine",
                "week1": "In 1 Woche",
                "weeks2": "In 2 Wochen"
            },
            "dmspaneltitle": "Anhang aus Datenbank auswählen",
            "edit": "Bearbeiten",
            "from": "Von",
            "gridrefresh": "Gitter aktualisieren",
            "keepsynchronization": "Sync halten",
            "message": "Meldung",
            "regenerateallemails": "Alle E-mails regenerien",
            "regenerateemail": "E-Mail regenerieren",
            "remove": "Löschen",
            "remove_confirmation": "Möchten Sie diese E-mail wirklich löschen?",
            "reply": "Antworten",
            "replyprefix": "Am {0}, {1} schrieb:",
            "selectaclass": "Wählen Sie eine Klasse aus",
            "sendemail": "E-mail senden",
            "statuses": {
                "draft": "Entwürfe",
                "outgoing": "Ausgehende",
                "received": "Eingehende",
                "sent": "Gesendete"
            },
            "subject": "Betreff",
            "to": "An",
            "view": "Ansichten"
        },
        "errors": {
            "autherror": "ID oder Passwort ungültig",
            "classnotfound": "Keine Klasse {0} gefunden",
            "notfound": "Element nicht gefunden"
        },
        "filters": {
            "actions": "Aktionen",
            "addfilter": "Filter hinzufügen",
            "any": "Irgendein",
            "attribute": "Ein Attribut auswählen",
            "attributes": "Attribute",
            "clearfilter": "Filter leeren",
            "clone": "Klonen",
            "copyof": "Kopien von",
            "description": "Beschreibung",
            "domain": "Domain",
            "filterdata": "Datei filtern",
            "fromselection": "Aus der Auswahl",
            "ignore": "Ignorieren",
            "migrate": "Migrieren",
            "name": "Name",
            "newfilter": "Neu Filter",
            "noone": "Keine",
            "operator": "Operator",
            "operators": {
                "beginswith": "Starten mit",
                "between": "Zwischen",
                "contained": "Enthalten",
                "containedorequal": "Enthalten oder gleich",
                "contains": "Enthält",
                "containsorequal": "Enthält oder gleich",
                "different": "Verschieden",
                "doesnotbeginwith": "Es startet nicht mit",
                "doesnotcontain": "Es enthält nicht",
                "doesnotendwith": "Es endet nicht mit",
                "endswith": "Es endet mit",
                "equals": "Gleich",
                "greaterthan": "Höher",
                "isnotnull": "Not null",
                "isnull": "Null",
                "lessthan": "Niedriger"
            },
            "relations": "Beziehungen",
            "type": "Typ",
            "typeinput": "Input Parameter",
            "value": "Wert"
        },
        "gis": {
            "card": "Karte",
            "cardsMenu": "Karten-Menü",
            "externalServices": "Externe Dienste",
            "geographicalAttributes": "Geographische Attribute",
            "geoserverLayers": "Ebenen von Geoserver",
            "layers": "Ebenen",
            "list": "Liste",
            "map": "Map",
            "mapServices": "Map Services",
            "position": "Stelle",
            "root": "Quelle",
            "tree": "Baumansicht",
            "view": "Ansichten",
            "zoom": "Zoom"
        },
        "history": {
            "activityname": "Aktivitätsname",
            "activityperformer": "Ausführende der Aktivität",
            "begindate": "Anfangsdatum",
            "enddate": "Enddatum",
            "processstatus": "Status",
            "user": "Benutzer"
        },
        "importexport": {
            "downloadreport": "Bericht vom Download",
            "emailfailure": "Beim Senden der E-Mail ist ein Fehler aufgetreten!",
            "emailsubject": "Datenbericht importieren",
            "emailsuccess": "Die E-Mail wurde erfolgreich gesendet!",
            "export": "Exportieren",
            "import": "Importieren",
            "importresponse": "Einfuhrentscheidung",
            "response": {
                "created": "Erzeugte Elemente",
                "deleted": "Gelöschte Elemente",
                "errors": "Fehler",
                "linenumber": "Zeilennummer",
                "message": "Nachricht",
                "modified": "Geänderte Elemente",
                "processed": "Verarbeitete Zeilen",
                "recordnumber": "Rekordzahl",
                "unmodified": "Unveränderte Elemente"
            },
            "sendreport": "Bericht senden",
            "template": "Template",
            "templatedefinition": "<em>Template definition</em>"
        },
        "login": {
            "buttons": {
                "login": "Anmelden",
                "logout": "Benutzer wechseln"
            },
            "fields": {
                "group": "Gruppe",
                "language": "Sprache",
                "password": "Passwort",
                "tenants": "Mandanten",
                "username": "Benutzername"
            },
            "loggedin": "Angemeldet",
            "title": "Anmelden",
            "welcome": "Willkommen zurück {0}."
        },
        "main": {
            "administrationmodule": "Verwaltungsformular",
            "baseconfiguration": "Basiskonfiguration",
            "cardlock": {
                "lockedmessage": "Sie dürfen diese Karte nicht bearbeiten. Sie wird gerade von {0} bearbeitet.",
                "someone": "jemand"
            },
            "changegroup": "Gruppe wechseln",
            "changepassword": "Passwort ändern",
            "changetenant": "Mandant wechseln",
            "confirmchangegroup": "Möchten Sie Gruppe wirklich wechseln?",
            "confirmchangetenants": "Möchten Sie aktive Mandanten wirklich wechseln?",
            "confirmdisabletenant": "Möchten Sie das Flag \"Mandanten ignorieren\" wirklich deaktivieren?",
            "confirmenabletenant": "Möchten Sie das Flag \"Mandanten ignorieren\" wirklich aktivieren?",
            "confirmpassword": "Passwort bestätigen",
            "ignoretenants": "Mandanten ignorieren",
            "info": "Information",
            "logo": {
                "cmdbuild": "CMDBuild Logo",
                "cmdbuildready2use": "CMDBuild READY2USE Logo",
                "companylogo": "Firmenlogo",
                "openmaint": "openMAINT Logo"
            },
            "logout": "Abmeldung",
            "managementmodule": "Datenmanagement-Modul",
            "multigroup": "Multigruppe",
            "multitenant": "Mandantenfähig",
            "navigation": "Navigieren",
            "newpassword": "Neues Passwort",
            "oldpassword": "Altes Passwort",
            "pagenotfound": "Seite nicht gefunden",
            "pleasecorrecterrors": "Bitte korrigieren Sie die angezeigten Fehler!",
            "preferences": {
                "comma": "Komma",
                "decimalserror": "Dezimalfeld muss anwesend sein",
                "decimalstousandserror": "Dezimal- und Tausendertrennzeichen müssen unterschiedlich sein",
                "default": "Standard",
                "defaultvalue": "Default-Wert",
                "labeldateformat": "Datumsformat",
                "labeldecimalsseparator": "Dezimaltrennzeichen",
                "labelintegerformat": "Ganzzahlformat",
                "labellanguage": "Sprache",
                "labelnumericformat": "Numerisches Format",
                "labelthousandsseparator": "Tausendertrennzeichen",
                "labeltimeformat": "Zeitformat",
                "msoffice": "Microsoft Office",
                "period": "Periode",
                "preferredofficesuite": "Bevorzugtes Office Suite",
                "space": "Raum",
                "thousandserror": "Tausendfeld muss anwesend sein",
                "timezone": "Zeitzone",
                "twelvehourformat": "12-Stunden-Format",
                "twentyfourhourformat": "24-Stunden-Format"
            },
            "searchinallitems": "In allen Elementen suchen",
            "userpreferences": "Einstellungen"
        },
        "menu": {
            "allitems": "Alle Elemente",
            "classes": "Klassen",
            "custompages": "Maßseiten",
            "dashboards": "Dashboards",
            "processes": "Vorgänge",
            "reports": "Berichte",
            "views": "Sicht"
        },
        "notes": {
            "edit": "Notizen öffnen"
        },
        "notifier": {
            "attention": "Achtung",
            "error": "Fehler",
            "genericerror": "Allgemeiner Fehler",
            "genericinfo": "Allgemeine Info",
            "genericwarning": "Allgemeine Warnmeldung",
            "info": "Information",
            "success": "Erfolg",
            "warning": "Achtung"
        },
        "patches": {
            "apply": "Patches anwenden",
            "category": "Kategorie",
            "description": "Beschreibung",
            "name": "Name",
            "patches": "Patches"
        },
        "processes": {
            "abortconfirmation": "Sind Sie sicher, dass Sie diesen Vorgang abbrechen möchten?",
            "abortprocess": "Vorgang abbrechen",
            "action": {
                "advance": "Weiter",
                "label": "Aktion"
            },
            "activeprocesses": "Aktive Prozesse",
            "allstatuses": "Alle",
            "editactivity": "Aktivität bearbeiten",
            "openactivity": "Aktivität öffnen",
            "startworkflow": "Anlassen",
            "workflow": "Prozesse"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Karte",
            "cardList": "Kartenliste",
            "cardRelation": "Beziehung",
            "cardRelations": "Beziehung",
            "choosenaviagationtree": "Navigationsbaum auswählen",
            "class": "Klasse",
            "class:": "Klasse",
            "classList": "Klassenliste",
            "compoundnode": "Knoten-Verbindung",
            "enableTooltips": "Tooltips auf Graph aktivieren/deaktivieren",
            "level": "Ebene",
            "openRelationGraph": "Beziehungsgraph öffnen",
            "qt": "Qt",
            "refresh": "Aktualisieren",
            "relation": "Beziehung",
            "relationGraph": "Beziehungsgraph",
            "reopengraph": "Graph von diesem Knoten wieder öffnen"
        },
        "relations": {
            "adddetail": "Detail hinzufügen",
            "addrelations": "Beziehungen hinzufügen",
            "attributes": "Attribute",
            "code": "Code",
            "deletedetail": "Detail löschen",
            "deleterelation": "Beziehung löschen",
            "description": "Beschreibung",
            "editcard": "Karte bearbeiten",
            "editdetail": "Detail bearbeiten",
            "editrelation": "Beziehung bearbeiten",
            "mditems": "Elemente",
            "opencard": "Verbundene Karte öffnen",
            "opendetail": "Ansicht Detail",
            "type": "Typ"
        },
        "reports": {
            "csv": "CSV",
            "download": "Herunterladen",
            "format": "Format",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Ausdrucken",
            "reload": "Neu laden",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "<em>Add Thematism</em>",
            "analysisType": "Analysetyp",
            "attribute": "Attribut",
            "calculateRules": "<em>Generate style rules</em>",
            "clearThematism": "<em>Clear Thematism</em>",
            "color": "Farbe",
            "defineLegend": "<em>Legend definition</em>",
            "defineThematism": "<em>Thematism definition</em>",
            "function": "Funktion",
            "generate": "Erzeugen",
            "geoAttribute": "<em>geoAttribute</em>",
            "graduated": "Graduell",
            "highlightSelected": "Markieren Sie das ausgewählte Element",
            "intervals": "Abständen",
            "legend": "Legende",
            "name": "<em>name</em>",
            "newThematism": "<em>New Thematism</em>",
            "punctual": "Pünktlich",
            "quantity": "Menge",
            "source": "Quelle",
            "table": "Tabelle",
            "thematism": "Thematisierungen",
            "value": "Wert"
        },
        "widgets": {
            "customform": {
                "addrow": "Zeile hinzufügen",
                "clonerow": "Zeile klonen",
                "deleterow": "Zeile löschen",
                "editrow": "Zeile verändern",
                "export": "Exportieren",
                "import": "Importieren",
                "refresh": "Standardeinstellungen wiederherstellen"
            },
            "linkcards": {
                "editcard": "Karte bearbeiten",
                "opencard": "Karte öffnen",
                "refreshselection": "Default Auswahl einsetzen",
                "togglefilterdisabled": "Filter des Gitters deaktivieren",
                "togglefilterenabled": "Filter Gitter aktivieren"
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
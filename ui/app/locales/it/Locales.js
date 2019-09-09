(function() {
    Ext.define('CMDBuildUI.locales.it.Locales', {
        "requires": ["CMDBuildUI.locales.it.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "it",
        "administration": CMDBuildUI.locales.it.LocalesAdministration.administration,
        "attachments": {
            "add": "Aggiungi allegato",
            "attachmenthistory": "Storia dell'allegato",
            "author": "Autore",
            "category": "Categoria",
            "creationdate": "Data di creazione",
            "deleteattachment": "Elimina allegato",
            "deleteattachment_confirmation": "Sei sicuro di voler eliminare questo allegato?",
            "description": "Descrizione",
            "download": "Scarica",
            "editattachment": "Modifica allegato",
            "file": "File",
            "filename": "Nome del file",
            "majorversion": "Versione principale",
            "modificationdate": "Data modifica",
            "uploadfile": "Carica file...",
            "version": "Versione",
            "viewhistory": "Vedi la storia dell'allegato"
        },
        "bim": {
            "bimViewer": "Visualizzatore Bim",
            "card": {
                "label": "Scheda"
            },
            "layers": {
                "label": "Layers",
                "menu": {
                    "hideAll": "Nascondi Tutto",
                    "showAll": "Mostra Tutto"
                },
                "name": "Nome",
                "qt": "Quantità",
                "visibility": "Visibilità",
                "visivility": "Visibilità"
            },
            "menu": {
                "camera": "Camera",
                "frontView": "Vista frontale",
                "mod": "Modalità di visualizzazione",
                "orthographic": "Vista Ortografica",
                "pan": "Sposta",
                "perspective": "Vista Prospettica",
                "resetView": "Reset della Vista",
                "rotate": "Ruota",
                "sideView": "Vista laterale",
                "topView": "Vista dall'alto"
            },
            "showBimCard": "Mostra scheda Bim",
            "tree": {
                "arrowTooltip": "Seleziona elemento",
                "columnLabel": "Albero Ifc",
                "label": "Albero",
                "open_card": "Apri scheda collegata",
                "root": "Radice Ifc"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Aggiungi scheda",
                "clone": "Clona",
                "clonewithrelations": "Clona scheda e relazioni",
                "deletecard": "Cancella scheda",
                "deleteconfirmation": "Sei sicuro di voler cancellare questa scheda?",
                "label": "Schede dati",
                "modifycard": "Modifica scheda",
                "opencard": "Apri scheda",
                "print": "Stampa scheda"
            },
            "simple": "Semplice",
            "standard": "Standard"
        },
        "common": {
            "actions": {
                "add": "Aggiungi",
                "apply": "Applica",
                "cancel": "Annulla",
                "close": "Chiudi",
                "delete": "Cancella",
                "edit": "Modifica",
                "execute": "Esegui",
                "refresh": "Ricarica dati",
                "remove": "Rimuovi",
                "save": "Salva",
                "saveandapply": "Salva e applica",
                "saveandclose": "Salva e chiudi",
                "search": "Cerca",
                "searchtext": "Cerca..."
            },
            "attributes": {
                "nogroup": "Dati di base"
            },
            "dates": {
                "date": "d/m/Y",
                "datetime": "d/m/Y H:i:s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "Pulisci HTML"
            },
            "grid": {
                "disablemultiselection": "Disabilita selezione multipla",
                "enamblemultiselection": "Abilita selezione multipla",
                "export": "Esporta dati",
                "filterremoved": "Il filtro impostato è stato rimosso",
                "import": "Importa dati",
                "itemnotfound": "Elemento non trovato",
                "list": "Lista",
                "opencontextualmenu": "Apri menu contestuale",
                "print": "Stampa",
                "printcsv": "Stampa come CSV",
                "printodt": "Stampa come ODT",
                "printpdf": "Stampa come PDF",
                "row": "Elemento",
                "rows": "Elementi",
                "subtype": "Sottotipo"
            },
            "tabs": {
                "activity": "Attività",
                "attachments": "Allegati",
                "card": "Scheda",
                "details": "Dettagli",
                "emails": "Email",
                "history": "Storia",
                "notes": "Note",
                "relations": "Relazioni"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Aggiungi allegato da DMS",
            "alredyexistfile": "Esiste già un file con lo stesso nome",
            "archivingdate": "Data di archiviazione",
            "attachfile": "Allega file",
            "bcc": "Ccn",
            "cc": "Cc",
            "composeemail": "Componi e-mail",
            "composefromtemplate": "Componi dal template",
            "delay": "Ritardo",
            "delays": {
                "day1": "1 giorno",
                "days2": "2 giorni",
                "days4": "4 giorni",
                "hour1": "1 ora",
                "hours2": "2 ore",
                "hours4": "4 ore",
                "month1": "1 mese",
                "none": "Nessuno",
                "week1": "1 settimana",
                "weeks2": "2 settimane"
            },
            "dmspaneltitle": "Seleziona allegati da Database",
            "edit": "Modifica",
            "from": "Da",
            "gridrefresh": "Refresh griglia",
            "keepsynchronization": "Mantieni sync",
            "message": "Messaggio",
            "regenerateallemails": "Rigenera tutte le e-mail",
            "regenerateemail": "Rigenera e-mail",
            "remove": "Rimuovi",
            "remove_confirmation": "Sei sicuro di voler eliminare questa email?",
            "reply": "Rispondi",
            "replyprefix": "Il {0} {1} ha scritto",
            "selectaclass": "Seleziona una classe",
            "sendemail": "Invio e-mail",
            "statuses": {
                "draft": "Bozze",
                "outgoing": "In uscita",
                "received": "Ricevute",
                "sent": "Inviate"
            },
            "subject": "Oggetto",
            "to": "A",
            "view": "Vista"
        },
        "errors": {
            "autherror": "Utente o password sbagliati",
            "classnotfound": "Classe {0} non trovata",
            "notfound": "Elemento non trovato"
        },
        "filters": {
            "actions": "Azioni",
            "addfilter": "Aggiungi filtro",
            "any": "Una qualsiasi",
            "attribute": "Scegli un attributo",
            "attributes": "Attributi",
            "clearfilter": "Cancella Filtro",
            "clone": "Clona",
            "copyof": "Copia di",
            "description": "Descrizione",
            "domain": "Dominio",
            "filterdata": "Dati filtro",
            "fromselection": "Dalla selezione",
            "ignore": "Ignora",
            "migrate": "Migra",
            "name": "Nome",
            "newfilter": "Nuovo filtro",
            "noone": "Nessuna",
            "operator": "Operatore",
            "operators": {
                "beginswith": "Inizia con",
                "between": "Compreso",
                "contained": "Contenuto",
                "containedorequal": "Contenuto o uguale",
                "contains": "Contiene",
                "containsorequal": "Contiene o uguale",
                "different": "Diverso",
                "doesnotbeginwith": "Non inizia con",
                "doesnotcontain": "Non contiene",
                "doesnotendwith": "Non finisce con",
                "endswith": "Finisce con",
                "equals": "Uguale",
                "greaterthan": "Maggiore",
                "isnotnull": "Non è nullo",
                "isnull": "È nullo",
                "lessthan": "Minore"
            },
            "relations": "Relazioni",
            "type": "Tipo",
            "typeinput": "Parametro di input",
            "value": "Valore"
        },
        "gis": {
            "card": "Scheda",
            "cardsMenu": "Menu Mappa",
            "externalServices": "Servizi esterni",
            "geographicalAttributes": "Attributi geografici",
            "geoserverLayers": "Layers di Geoserver",
            "layers": "Livelli",
            "list": "Lista",
            "map": "Mappa",
            "mapServices": "Servizi geografici",
            "position": "Posizione",
            "root": "Root",
            "tree": "Albero di navigazione",
            "view": "Vista",
            "zoom": "Zoom"
        },
        "history": {
            "activityname": "Nome attività",
            "activityperformer": "Esecutore attività",
            "begindate": "Data inizio",
            "enddate": "Data fine",
            "processstatus": "Stato",
            "user": "Utente"
        },
        "importexport": {
            "downloadreport": "Scarica report",
            "emailfailure": "Si sono verificati dei problemi durante l’invio dell’email!",
            "emailsubject": "Report import dati",
            "emailsuccess": "L’email è stata inviata correttamente!",
            "export": "Esporta",
            "import": "Importa",
            "importresponse": "Risposta import",
            "response": {
                "created": "Elementi creati",
                "deleted": "Elementi cancellati",
                "errors": "Errori",
                "linenumber": "Numero linea",
                "message": "Messaggio",
                "modified": "Elementi modificati",
                "processed": "Righe processate",
                "recordnumber": "Numero elemento",
                "unmodified": "Elementi non modificati"
            },
            "sendreport": "Invia report",
            "template": "Template",
            "templatedefinition": "Definizione template"
        },
        "login": {
            "buttons": {
                "login": "Accedi",
                "logout": "Cambia utente"
            },
            "fields": {
                "group": "Gruppo",
                "language": "Lingua",
                "password": "Password",
                "tenants": "Tenant",
                "username": "Username"
            },
            "loggedin": "Autenticato",
            "title": "Accedi",
            "welcome": "Bentornato {0}."
        },
        "main": {
            "administrationmodule": "Modulo di Amministrazione",
            "baseconfiguration": "Configurazioni base",
            "cardlock": {
                "lockedmessage": "Non puoi modificare questa scheda perché la sta modificando {0}.",
                "someone": "qualcuno"
            },
            "changegroup": "Cambia gruppo",
            "changepassword": "Cambia password",
            "changetenant": "Cambia tenant",
            "confirmchangegroup": "Vuoi davvero cambiare il gruppo?",
            "confirmchangetenants": "Vuoi davvero cambiare i tenant attivi?",
            "confirmdisabletenant": "Vuoi davvero disablitare il flag \"Ignora tenant\"?",
            "confirmenabletenant": "Vuoi davvero abilitare il flag \"Ignora tenant\"?",
            "confirmpassword": "Conferma password",
            "ignoretenants": "Ignora tenant",
            "info": "Informazione",
            "logo": {
                "cmdbuild": "Logo CMDBuild",
                "cmdbuildready2use": "Logo CMDBuild READY2USE",
                "companylogo": "Logo dell’azienda",
                "openmaint": "Logo openMAINT"
            },
            "logout": "Esci",
            "managementmodule": "Modulo gestione dati",
            "multigroup": "Multi gruppo",
            "multitenant": "Multi tenant",
            "navigation": "Navigazione",
            "newpassword": "Nuova password",
            "oldpassword": "Vecchia password",
            "pagenotfound": "Pagina non trovata",
            "pleasecorrecterrors": "Correggi gli errori evidenziati!",
            "preferences": {
                "comma": "Virgola",
                "decimalserror": "Il campo separatore decimali è obbligatorio",
                "decimalstousandserror": "I campi separatore migliaia e decimali devono essere diversi",
                "default": "Predefinito",
                "defaultvalue": "Valore di default",
                "labeldateformat": "Formato data",
                "labeldecimalsseparator": "Separatore decimali",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "Lingua",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "Separatore migliaia",
                "labeltimeformat": "Formato ora",
                "msoffice": "Microsoft Office",
                "period": "Punto",
                "preferredofficesuite": "Suite Office preferita",
                "space": "Spazio",
                "thousandserror": "Il campo separatore migliaia è obbligatorio",
                "timezone": "Fuso orario",
                "twelvehourformat": "Formato 12 ore",
                "twentyfourhourformat": "Formato 24 ore"
            },
            "searchinallitems": "Cerca in tutti gli elementi",
            "userpreferences": "Preferenze"
        },
        "menu": {
            "allitems": "Tutti gli elementi",
            "classes": "Classi",
            "custompages": "Pagine custom",
            "dashboards": "Dashboard",
            "processes": "Processi",
            "reports": "Report",
            "views": "Viste"
        },
        "notes": {
            "edit": "Modifica nota"
        },
        "notifier": {
            "attention": "Attenzione",
            "error": "Errore",
            "genericerror": "Errore generico",
            "genericinfo": "Info generico",
            "genericwarning": "Warning generico",
            "info": "Informazione",
            "success": "Successo",
            "warning": "Attenzione"
        },
        "patches": {
            "apply": "Applica patch",
            "category": "Categoria",
            "description": "Descrizione",
            "name": "Nome",
            "patches": "Patch"
        },
        "processes": {
            "abortconfirmation": "Sicuro di voler interrompere questo processo?",
            "abortprocess": "Interrompi processo",
            "action": {
                "advance": "Continua",
                "label": "Azione"
            },
            "activeprocesses": "Processi attivi",
            "allstatuses": "Tutti",
            "editactivity": "Modifica attività",
            "openactivity": "Apri attività",
            "startworkflow": "Avvia",
            "workflow": "Processo"
        },
        "relationGraph": {
            "activity": "Processo",
            "card": "Scheda",
            "cardList": "Lista schede",
            "cardRelation": "Relazioni",
            "cardRelations": "Relazioni",
            "choosenaviagationtree": "Scegli albero di navigazione",
            "class": "Classe",
            "class:": "Classe",
            "classList": "Lista classi",
            "compoundnode": "Raggruppamento di nodi",
            "enableTooltips": "Abilita/Disabilita tooltips",
            "level": "Livello",
            "openRelationGraph": "Apri grafo delle relazioni",
            "qt": "Quantità",
            "refresh": "Ricarica",
            "relation": "Relazioni",
            "relationGraph": "Grafo delle relazioni",
            "reopengraph": "Riapri il grafo da questo nodo"
        },
        "relations": {
            "adddetail": "Aggiungi Dettaglio",
            "addrelations": "Aggiungi relazioni",
            "attributes": "Attributi",
            "code": "Codice",
            "deletedetail": "Elimina dettaglio",
            "deleterelation": "Cancella relazione",
            "description": "Descrizione",
            "editcard": "Modifica scheda",
            "editdetail": "Modifica dettaglio",
            "editrelation": "Modifica relazione",
            "mditems": "elementi",
            "opencard": "Apri scheda collegata",
            "opendetail": "Visualizza dettaglio",
            "type": "Tipo"
        },
        "reports": {
            "csv": "CSV",
            "download": "Scarica",
            "format": "Formato",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Stampa",
            "reload": "Ricarica",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "Aggiungi Tematismo",
            "analysisType": "Tipo di Analisi",
            "attribute": "Attributi",
            "calculateRules": "Genera regole di stile",
            "clearThematism": "Cancella Tematismo",
            "color": "Colore",
            "defineLegend": "Definizione Legenda",
            "defineThematism": "Definizione Tematismo",
            "function": "Funzione",
            "generate": "Genera",
            "geoAttribute": "Attributo Geografico",
            "graduated": "Graduato",
            "highlightSelected": "Evidenzia elemento selezionato",
            "intervals": "Intervalli",
            "legend": "Legenda",
            "name": "Nome",
            "newThematism": "Nuovo Tematismo",
            "punctual": "Puntuale",
            "quantity": "Quantità",
            "source": "Fonte Dati",
            "table": "Tabella",
            "thematism": "Tematismi",
            "value": "Valore"
        },
        "widgets": {
            "customform": {
                "addrow": "Aggiungi riga",
                "clonerow": "Clona riga",
                "deleterow": "Cancella riga",
                "editrow": "Modifica riga",
                "export": "Esporta",
                "import": "Importa",
                "refresh": "Aggiorna ai valori predefiniti"
            },
            "linkcards": {
                "editcard": "Modifica scheda",
                "opencard": "Apri scheda",
                "refreshselection": "Applica selezione di default",
                "togglefilterdisabled": "Disabilita filtro griglia",
                "togglefilterenabled": "Abilita filtro griglia"
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
Ext.define('CMDBuildUI.locales.da.LocalesAdministration', {
    "singleton": true,
    "localization": "da",
    "administration": {
        "attributes": {
            "attribute": "Attribute",
            "attributes": "Egnskaber",
            "emptytexts": {
                "search": "Søg…"
            },
            "fieldlabels": {
                "actionpostvalidation": "Handling efter validering",
                "active": "Aktiv",
                "attributegroupings": "Egnskabgrupperinger",
                "autovalue": "Automatisk værdi",
                "decimalseparator": "Decimal separator",
                "description": "Bskrivelse",
                "domain": "Domæne",
                "editortype": "Editor type",
                "filter": "Filter",
                "format": "Format",
                "group": "Gruppe",
                "help": "Hjælp",
                "includeinherited": "Inkludere arvelig",
                "iptype": "IP type",
                "lookup": "Opslag",
                "mandatory": "Obligatorisk",
                "maxlength": "Max længde",
                "mode": "Mode",
                "name": "Navn",
                "positioningofum": "Placering af UM",
                "precision": "Præcision",
                "preselectifunique": "Valg hvis unikt",
                "scale": "vægt",
                "separator": "Separator",
                "separators": "Separatorer",
                "showif": "Vis om",
                "showingrid": "Vis i gitter",
                "showinreducedgrid": "Vis i reduceret gitter",
                "showseconds": "Vis sekunder",
                "showseparator": "Vis separator",
                "thousandsseparator": "Tusind separator",
                "type": "Type",
                "unique": "Unik",
                "unitofmeasure": "Måleenhed",
                "validationrules": "Valideringsregler",
                "visibledecimals": "Synlige decimaler"
            },
            "strings": {
                "addnewgroup": "Tilføj ny gruppe",
                "any": "Nogen",
                "createnewgroup": "Opret ny gruppe",
                "draganddrop": "Træk og slip for at omorganisere",
                "editable": "Redigerbar",
                "editorhtml": "Editor HTML",
                "hidden": "Skjult",
                "immutable": "uforanderlige",
                "ipv4": "IPV4",
                "ipv6": "IPV6",
                "plaintext": "Simpel tekst",
                "positioningofumrequired": "Måleenhedens placering er obligatorisk",
                "precisionmustbebiggerthanscale": "Præcisionen skal være større end skalaen",
                "readonly": "Læs kun",
                "scalemustbesmallerthanprecision": "Skalaen skal være mindre end Precision",
                "thefieldmandatorycantbechecked": "Feltet \"Obligatorisk\" kan ikke kontrolleres",
                "thefieldmodeishidden": "Feltet \"Mode\" er skjult",
                "thefieldshowingridcantbechecked": "Feltet \"Vis i gitter\" kan ikke kontrolleres",
                "thefieldshowinreducedgridcantbechecked": "Feltet \"Vis i reduceret gitter\" kan ikke kontrolleres"
            },
            "texts": {
                "active": "Aktiv",
                "addattribute": "Tilføj attribut",
                "cancel": "Annullere",
                "description": "Beskrivelse",
                "direct": "Direkte",
                "editingmode": "Redigeringstilstand",
                "editmetadata": "Rediger metadata",
                "grouping": "Gruppering",
                "inverse": "Inverse",
                "mandatory": "Obligatorisk",
                "name": "Navn",
                "newattribute": "Ny egnskab",
                "save": "Gem",
                "saveandadd": "Gem og tilføj",
                "showingrid": "Vis i gitter",
                "type": "Type",
                "unique": "Unik",
                "viewmetadata": "Se metadata"
            },
            "titles": {
                "generalproperties": "Generelle egenskaber",
                "otherproperties": "Andre egenskaber",
                "typeproperties": "Type egenskaber"
            },
            "tooltips": {
                "deleteattribute": "Slet",
                "disableattribute": "Deaktiver",
                "editattribute": "Redigere",
                "enableattribute": "Aktiver",
                "openattribute": "Åben",
                "translate": "Oversætte"
            }
        },
        "bim": {
            "addproject": "Tilføj projekt",
            "ifcfile": "IFC Fil",
            "lastcheckin": "Sidste check-in",
            "mappingfile": "Mapping-fil",
            "multilevel": "Multi-level",
            "newproject": "<em>New project</em>",
            "parentproject": "Parent projekt",
            "projectlabel": "<em>Project</em>",
            "projects": "Projekt"
        },
        "classes": {
            "fieldlabels": {
                "applicability": "Anvendelsesområde",
                "attachmentsinline": "Vedhæftede inline",
                "attachmentsinlineclosed": "Vedhæftede inline lukket",
                "categorylookup": "Kategori opslag",
                "defaultexporttemplate": "Standard skabelon til dataeksport",
                "defaultimporttemplate": "Standard skabelon til dataimport",
                "descriptionmode": "Beskrivelse tilstand",
                "guicustom": "Brugerdefineret GUI",
                "guicustomparameter": "Brugerdefinerede GUI parametre",
                "multitenantmode": "Multitenant tilstand",
                "superclass": "superklasse",
                "widgetname": "Widget Navn"
            },
            "properties": {
                "form": {
                    "fieldsets": {
                        "ClassAttachments": "Vedhæftede klasse filer",
                        "classParameters": "Klasseparametre",
                        "contextMenus": {
                            "actions": {
                                "delete": {
                                    "tooltip": "Slet"
                                },
                                "edit": {
                                    "tooltip": "Redigere"
                                },
                                "moveDown": {
                                    "tooltip": "Flyt ned"
                                },
                                "moveUp": {
                                    "tooltip": "Flyt op"
                                }
                            },
                            "inputs": {
                                "applicability": {
                                    "label": "Anvendelsesområde",
                                    "values": {
                                        "all": {
                                            "label": "Alle"
                                        },
                                        "many": {
                                            "label": "Aktuel og valgt"
                                        },
                                        "one": {
                                            "label": "Aktuel"
                                        }
                                    }
                                },
                                "javascriptScript": {
                                    "label": "Javascript script / brugerdefinerede GUI parametre"
                                },
                                "menuItemName": {
                                    "label": "Menupunktsnavn",
                                    "values": {
                                        "separator": {
                                            "label": "[---------]"
                                        }
                                    }
                                },
                                "status": {
                                    "label": "status",
                                    "values": {
                                        "active": {
                                            "label": "Aktiv"
                                        }
                                    }
                                },
                                "typeOrGuiCustom": {
                                    "label": "Type / brugerdefineret GUI",
                                    "values": {
                                        "component": {
                                            "label": "Brugerdefineret GUI"
                                        },
                                        "custom": {
                                            "label": "Script Javascript"
                                        },
                                        "separator": {
                                            "label": "<em></em>"
                                        }
                                    }
                                }
                            },
                            "title": "Kontekstmenuer"
                        },
                        "createnewwidget": "Opret ny widget",
                        "defaultOrders": "Standardordrer",
                        "formTriggers": {
                            "actions": {
                                "addNewTrigger": {
                                    "tooltip": "Tilføj ny Trigger"
                                },
                                "deleteTrigger": {
                                    "tooltip": "Slet"
                                },
                                "editTrigger": {
                                    "tooltip": "Redigere"
                                },
                                "moveDown": {
                                    "tooltip": "Flyt ned"
                                },
                                "moveUp": {
                                    "tooltip": "Flyt op"
                                }
                            },
                            "inputs": {
                                "createNewTrigger": {
                                    "label": "Opret ny trigger formular"
                                },
                                "events": {
                                    "label": "Events",
                                    "values": {
                                        "afterClone": {
                                            "label": "Efter klon"
                                        },
                                        "afterDelete": {
                                            "label": "Efter slet"
                                        },
                                        "afterEdit": {
                                            "label": "Efter Rediger"
                                        },
                                        "afterInsert": {
                                            "label": "Efter Indsæt"
                                        },
                                        "beforView": {
                                            "label": "Før View"
                                        },
                                        "beforeClone": {
                                            "label": "Før Klon"
                                        },
                                        "beforeEdit": {
                                            "label": "Før Rediger"
                                        },
                                        "beforeInsert": {
                                            "label": "Før Indsæt"
                                        }
                                    }
                                },
                                "javascriptScript": {
                                    "label": "Javascript script"
                                },
                                "status": {
                                    "label": "status"
                                }
                            },
                            "title": "Trigger Form"
                        },
                        "formWidgets": "Widgets Form",
                        "generalData": {
                            "inputs": {
                                "active": {
                                    "label": "Aktiv"
                                },
                                "classType": {
                                    "label": "Type"
                                },
                                "description": {
                                    "label": "Beskrivelse"
                                },
                                "name": {
                                    "label": "Navn"
                                },
                                "parent": {
                                    "label": "Arver fra"
                                },
                                "superclass": {
                                    "label": "Supeclass"
                                }
                            }
                        },
                        "icon": "Ikon",
                        "validation": {
                            "inputs": {
                                "validationRule": {
                                    "label": "Valideringsregel"
                                }
                            },
                            "title": "Validering"
                        }
                    },
                    "inputs": {
                        "events": "Events",
                        "javascriptScript": "Javascript Script",
                        "status": "Status"
                    },
                    "values": {
                        "active": "Aktiv"
                    }
                },
                "title": "Egenskab",
                "toolbar": {
                    "cancelBtn": "Annullere",
                    "closeBtn": "Luk",
                    "deleteBtn": {
                        "tooltip": "Slet"
                    },
                    "disableBtn": {
                        "tooltip": "Deaktiver"
                    },
                    "editBtn": {
                        "tooltip": "Redigere klasse"
                    },
                    "enableBtn": {
                        "tooltip": "Aktiver"
                    },
                    "printBtn": {
                        "printAsOdt": "OpenOffice Odt",
                        "printAsPdf": "Adobe Pdf",
                        "tooltip": "Udskriv klasse"
                    },
                    "saveBtn": "Gem"
                }
            },
            "strings": {
                "classactivated": "Klassen aktiveret korrekt.",
                "classdisabled": "Klassen er deaktiveret korrekt.",
                "createnewcontextaction": "Opret ny konteksthandling",
                "datacardsorting": "Sortering af datakort",
                "deleteclass": "Slet klasse",
                "deleteclassquest": "Er du sikker på, at du vil slette denne klasse?",
                "editcontextmenu": "Rediger kontekstmenu",
                "editformwidget": "Rediger form widget",
                "edittrigger": "Rediger trigger",
                "executeon": "Udfør på",
                "geaoattributes": "Geo attributter",
                "levels": "Lag"
            },
            "texts": {
                "calendar": "Kalender",
                "class": "klasse",
                "component": "Komponent",
                "createmodifycard": "Opret / modificer kort",
                "createreport": "Opret rapport",
                "custom": "Brugerdefineret",
                "direction": "Retning",
                "ping": "Ping",
                "separator": "Separator",
                "simple": "Enkel",
                "standard": "Standard",
                "startworkflow": "Start workflow"
            },
            "title": "Klasser",
            "toolbar": {
                "addClassBtn": {
                    "text": "Tilføj klasse"
                },
                "classLabel": "Klasse",
                "printSchemaBtn": {
                    "text": "Udskriv skema"
                },
                "searchTextInput": {
                    "emptyText": "Søg i alle klasser"
                }
            }
        },
        "common": {
            "actions": {
                "activate": "Aktiver",
                "add": "Tilføj",
                "cancel": "Annullere",
                "clone": "Dublere",
                "clonefrom": "Dublere fra…",
                "close": "Luk",
                "create": "Opret",
                "delete": "Slet",
                "disable": "Deaktiver",
                "download": "Hent",
                "edit": "Redigere",
                "enable": "Aktiver",
                "movedown": "Flyt ned",
                "moveup": "Flyt op",
                "next": "Næste",
                "no": "Nej",
                "ok": "Ok",
                "open": "Åben",
                "prev": "Forrige",
                "print": "Udskriv",
                "relationchart": "Relation diagram",
                "remove": "Fjern",
                "save": "Gem",
                "saveandadd": "Gem og tilføj",
                "update": "Opdatering",
                "yes": "Ja"
            },
            "labels": {
                "active": "Aktiv",
                "code": "Kode",
                "colorpreview": "Farve preview",
                "default": "Standard",
                "defaultfilter": "Standard filter",
                "description": "Beskrivelse",
                "funktion": "Funktion",
                "icon": "Ikon",
                "iconcolor": "Ikon farve",
                "iconpreview": "Ikon preview",
                "icontype": "Ikon type",
                "name": "Navn",
                "note": "Bemærk",
                "noteinline": "Bemærk inline",
                "noteinlineclosed": "Bemærk inline lukket",
                "status": "Status",
                "tenant": "Tenant",
                "textcolor": "Tekst farve",
                "tree": "Træ",
                "type": "Type"
            },
            "messages": {
                "applicationreloadquest": "Denne applikation har en opdatering, genindlæs?",
                "applicationupdate": "Programopdatering",
                "areyousuredeleteitem": "Er du sikker på, at du vil slette denne element?",
                "ascendingordescending": "Denne værdi er ikke gyldig, vælg venligst \"Stigende\" eller \"Aftagende\"",
                "attention": "Være opmærksom",
                "cannotsortitems": "Du kan ikke reorganisere elementer, hvis nogle filtre er til stede eller de arvede attributer er skjulte. Fjern dem og prøv igen.",
                "cantcontainchar": "Klassenavnet kan ikke indeholde {0} tegn.",
                "correctformerrors": "Ret venligst angivne fejl",
                "disabled": "Deaktiveret",
                "enabled": "Aktiveret",
                "error": "Fejl",
                "greaterthen": "Klassenavnet kan ikke være større end {0} tegn",
                "itemwascreated": "Elementet blev oprettet.",
                "loading": "Indlæser…",
                "saving": "Gemmer…",
                "success": "Succes",
                "thisfieldisrequired": "Dette felt er påkrævet",
                "warning": "Advarsel",
                "was": "var",
                "wasdeleted": "blev slettet"
            },
            "strings": {
                "always": "Altid",
                "ascending": "Stigende",
                "attribute": "Attribute",
                "currenticon": "Aktuel ikon",
                "default": "*Standard*",
                "descending": "Aftagende",
                "filtercql": "Filter CQL",
                "generalproperties": "Generelle egenskaber",
                "hidden": "Skjult",
                "iconimage": "Billede ikon",
                "image": "Billede",
                "localization": "Lokaliserings-tekst",
                "mixed": "Blandet",
                "never": "Aldrig",
                "properties": "Egenskab",
                "recursive": "rekursiv",
                "selectimage": "Vælg billede",
                "selectpngfile": "Vælg en .png-fil",
                "string": "String",
                "visiblemandatory": "Synlig obligatorisk",
                "visibleoptional": "Synlig valgfri"
            },
            "tooltips": {
                "add": "Tilføj",
                "clone": "Dublere",
                "edit": "Redigere",
                "edittrigger": "Rediger trigger",
                "localize": "Lokaliser",
                "open": "Åben"
            }
        },
        "customcomponents": {
            "emptytexts": {
                "searchcustompages": "Søg efter tilpassede komponenter ...",
                "searchingrid": "Søg i gitter…"
            },
            "fieldlabels": {
                "actions": "Aktioner",
                "active": "Aktiv",
                "componentid": "komponent ID",
                "description": "Beskrivelse",
                "name": "Navn",
                "zipfile": "ZIP fil"
            },
            "plural": "Brugerdefineret komponenter",
            "singular": "Brugerdefineret komponent",
            "strings": {
                "addcontextmenu": "Tilføj kontekstmenu",
                "contextmenu": "Kontekstmenu",
                "searchcontextmenus": "Søg i kontekstmenuer..."
            },
            "texts": {
                "addcustomcomponent": "Tilføj brugerdefineret komponent",
                "selectfile": "Vælg ZIP-fil"
            },
            "titles": {
                "file": "Fil"
            },
            "tooltips": {
                "delete": "Slet brugerdefineret komponent",
                "disable": "Deaktiver brugerdefineret komponent",
                "downloadpackage": "Hent brugerdefineret komponentpakke",
                "edit": "Rediger brugerdefineret komponent",
                "enable": "Aktivér brugerdefineret komponent"
            }
        },
        "custompages": {
            "emptytexts": {
                "searchcustompages": "Søg brugerdefineret sider…",
                "searchingrid": "Søg i gitter…"
            },
            "fieldlabels": {
                "actions": "Aktioner",
                "active": "Aktiv",
                "componentid": "komponent ID",
                "description": "Beskrivelse",
                "name": "Navn",
                "zipfile": "ZIP fil"
            },
            "plural": "Brugerdefineret sider",
            "singular": "Brugerdefineret side",
            "texts": {
                "addcustompage": "Tilføj brugerdefineret side",
                "selectfile": "Vælg ZIP-fil"
            },
            "titles": {
                "file": "Fil"
            },
            "tooltips": {
                "delete": "Slet brugerdefineret side",
                "disable": "Deaktiver brugerdefineret side",
                "downloadpackage": "Hent brugerdefineret sidepakke",
                "edit": "Rediger brugerdefineret side",
                "enable": "Aktivér brugerdefineret side"
            }
        },
        "domains": {
            "domain": "Domæne",
            "fieldlabels": {
                "cardinality": "kardinalitet",
                "defaultclosed": "Standard lukket",
                "destination": "Bestemmelsessted",
                "directdescription": "Direkte beskrivelse",
                "enabled": "Aktiveret",
                "inline": "Inline",
                "inversedescription": "Omvendt beskrivelse",
                "labelmasterdataillong": "Etiket master detaljer",
                "labelmasterdetail": "Etiket M / D",
                "link": "Link",
                "masterdetail": "Hoveddetaljer",
                "masterdetailshort": "M/D",
                "origin": "Oprindelse",
                "viewconditioncql": "Se betingelse (CQL)"
            },
            "pluralTitle": "Domæner",
            "properties": {
                "toolbar": {
                    "cancelBtn": "Annullere",
                    "deleteBtn": {
                        "tooltip": "Slet"
                    },
                    "disableBtn": {
                        "tooltip": "Deaktivere"
                    },
                    "editBtn": {
                        "tooltip": "Redigere"
                    },
                    "enableBtn": {
                        "tooltip": "Aktivere"
                    },
                    "saveBtn": "Gem"
                }
            },
            "singularTitle": "Domæne",
            "texts": {
                "adddomain": "Tilføj domæne",
                "addlink": "Tilføj link",
                "emptyText": "Søg i alle domæner",
                "enabledclasses": "Akivere klasser",
                "properties": "Egnskaber"
            },
            "toolbar": {
                "addBtn": {
                    "text": "Tilføj domæne"
                },
                "searchTextInput": {
                    "emptyText": "Søg  i alle domæner"
                }
            }
        },
        "emails": {
            "accounts": "Konti",
            "accountsavedcorrectly": "Kontoen er gemt korrekt",
            "addaccount": "Tilføj konto",
            "address": "Adresse",
            "addrow": "Tilføj række",
            "addtemplate": "Tilføj skabelon",
            "bcc": "Bcc",
            "body": "Body",
            "cc": "Cc",
            "clonetemplate": "Dublere",
            "contenttype": "Indholdstype",
            "date": "Dato",
            "defaultaccount": "Standard account",
            "delay": "Forsinke",
            "delays": {
                "day1": "Om 1 dag",
                "days2": "Om 2 dage",
                "days4": "Om 4 dage",
                "hour1": "1 time",
                "hours2": "2 timer",
                "hours4": "4 timer",
                "month1": "Om 1 måned",
                "none": "Ingen",
                "week1": "Om 1 uge",
                "weeks2": "Om 2 uger"
            },
            "description": "Beskrivelse",
            "editvalues": "Rediger værdier",
            "email": "E-mail",
            "enablessl": "Aktivér SSL",
            "enablestarttls": "Aktivér STARTTLS",
            "from": "Fra",
            "imapport": "IMAP port",
            "imapserver": "IMAP server",
            "incoming": "Indgående",
            "keepsync": "Hold synkronisering",
            "key": "Nøgle",
            "name": "Navn",
            "newaccount": "Ny konto",
            "newtemplate": "Ny skabelon",
            "notnullkey": "En eller flere værdier har en null nøgle",
            "outgoing": "Udgående",
            "password": "Kodeord",
            "promptsync": "Hurtig synkronisering",
            "queue": "Kø",
            "remove": "Fjern",
            "removeaccount": "Fjern konto",
            "removetemplate": "Fjern",
            "send": "Send",
            "sent": "Sent",
            "sentfolder": "Sendt mappe",
            "setdefaultaccount": "Indstil standardkonto",
            "smtpport": "SMTP port",
            "smtpserver": "SMTP server",
            "start": "Start",
            "subject": "Emne",
            "template": "Skabelon",
            "templates": "Skabeloner",
            "templatesavedcorrectly": "Skabelonen gemt korrekt",
            "to": "Til",
            "username": "Brugernavn",
            "value": "Værdi"
        },
        "geoattributes": {
            "fieldLabels": {
                "defzoom": "Standard zoom",
                "fillcolor": "Fil farve",
                "fillopacity": "Fil gennemsigtighed",
                "icon": "Ikon",
                "maxzoom": "Maksimum zoom",
                "minzoom": "Minimum zoom",
                "pointradius": "Point radius",
                "referenceclass": "Reference klass",
                "strokecolor": "Vælg faver",
                "strokedashstyle": "Træk punkttype",
                "strokeopacity": "Vælg gennemsigtighed",
                "strokewidth": "Vælg længde",
                "type": "Type",
                "visibility": "Sigtbarhed"
            },
            "strings": {
                "specificproperty": "Specifikke egenskaber"
            }
        },
        "gis": {
            "addicon": "Tilføj ikon",
            "addlayer": "Tilføj lag",
            "adminpassword": "Admin kodeord",
            "adminuser": "Admin bruger",
            "associatedcard": "Tilknyttet kort",
            "associatedclass": "Tilknyttet klasse",
            "defaultzoom": "Standard zoom",
            "deleteicon": "Slet ikon",
            "deleteicon_confirmation": "Er du sikker på, at du vil slette dette ikon?",
            "description": "Beskrivelse",
            "editicon": "Rediger ikon",
            "externalservices": "Eksterne tjenester",
            "file": "Fil",
            "geoserver": "Geo-server",
            "geoserverlayers": "Geo-server lag",
            "global": "<em>Global</em>",
            "icon": "Ikon",
            "layersorder": "Lag-bestilling",
            "manageicons": "Administrer ikoner",
            "mapservice": "Kort service",
            "maximumzoom": "Maximum zoom",
            "minimumzoom": "Minimum zoom",
            "newicon": "Ny ikon",
            "ownerclass": "<em>Class</em>",
            "owneruser": "<em>User</em>",
            "searchemptytext": "<em>Search thematisms</em>",
            "servicetype": "Servicetype",
            "thematism": "<em>Thematism</em>",
            "thematisms": "<em>Thematisms</em>",
            "type": "Type",
            "url": "URL",
            "workspace": "Arbejdsområde"
        },
        "groupandpermissions": {
            "emptytexts": {
                "searchgroups": "Søg grupper...",
                "searchingrid": "Søg i gitter…",
                "searchusers": "Søg bruger…"
            },
            "fieldlabels": {
                "actions": "Handliger",
                "active": "Aktiv",
                "attachments": "Vedhæftede filer",
                "datasheet": "Datablad",
                "defaultpage": "Standard side",
                "description": "Beskrivelse",
                "detail": "Detalje",
                "email": "E-mail",
                "exportcsv": "Eksport CSV-fil",
                "filters": "Filtre",
                "history": "Historik",
                "importcsvfile": "Import CSV-fil",
                "massiveeditingcards": "Massiv redigering af kort",
                "name": "Navn",
                "note": "Bemærk",
                "relations": "Relationer",
                "type": "Type",
                "username": "Brugernavn"
            },
            "plural": "Groupper og rettigheder",
            "singular": "Gruppe og rettighed",
            "strings": {
                "admin": "Administrator",
                "displaynousersmessage": "Ingen bruger for visning",
                "displaytotalrecords": "{2} records",
                "limitedadmin": "Begrænset administrator",
                "normal": "Normal",
                "readonlyadmin": "Læse-adgang admin"
            },
            "texts": {
                "addgroup": "Tilføj gruppe",
                "allow": "Tillade",
                "class": "Klass",
                "columnsprivileges": "Kolonne privilegier",
                "copyfrom": "Dupliker fra",
                "default": "Standard",
                "defaultfilter": "Standard filter",
                "defaultfilters": "Standard filtre",
                "defaultread": "Def. + R.",
                "description": "Beskrivelse",
                "editfilters": "Ændr filtre af {0}: {1}",
                "filters": "Filtre",
                "group": "Gruppe",
                "name": "Navn",
                "none": "Ingen",
                "permissions": "Tilladelser",
                "read": "Læs",
                "rowsprivileges": "Række privilegier",
                "uiconfig": "UI konfiguration",
                "userslist": "Brugerlist",
                "viewfilters": "Se filtre af {0}: {1}",
                "write": "Skriv"
            },
            "titles": {
                "allusers": "Alle Bruger",
                "disabledactions": "Deaktiverede handlinger",
                "disabledallelements": "Funktionalitet deaktiveret Navigationsmenu \"Alle elementer\"",
                "disabledmanagementprocesstabs": "Fane Deaktiverede Styre-Proceser",
                "disabledutilitymenu": "Funktionalitet deaktiveret Hjælpeprogrammenu",
                "generalattributes": "Generelle egenskaber",
                "managementdisabledtabs": "Fane Deaktiverede Styre-Klasser",
                "usersassigned": "Brugere tildelt"
            },
            "tooltips": {
                "disabledactions": "Deaktiverede handlinger",
                "filters": "Filtre",
                "removedisabledactions": "Fjern deaktiverde handlinger",
                "removefilters": "Fjern  filtre"
            }
        },
        "importexport": {
            "emptyTexts": {
                "searchfield": "Søg i alle skabeloner…"
            },
            "fieldlabels": {
                "applyon": "Ansøg om",
                "classdomain": "Klasse / domæne",
                "csvseparator": "CSV-separator",
                "datarownumber": "Data række nummer",
                "exportfilter": "Eksport filter",
                "fileformat": "Filformat",
                "firstcolumnnumber": "Første kolonne nummer",
                "headerrownumber": "Overskrift række nummer",
                "ignorecolumn": "Ignorer rækkefølge",
                "importkeattribute": "Importnøgleattribut",
                "missingrecords": "Manglende poster",
                "type": "Type",
                "useheader": "Brug overskrift",
                "value": "Værdi"
            },
            "texts": {
                "account": "Konto",
                "addtemplate": "Tilføj skabelon",
                "columnname": "Kolonne navn",
                "default": "Standard",
                "delete": "Slet",
                "emptyattributegridmessage": "Egnskabsgitter kan ikke være tomt",
                "erroremailtemplate": "Fejl email skabelon",
                "errorsmanagements": "Fejlstyring",
                "export": "Eksport",
                "import": "Import",
                "importexport": "Import/Eksport",
                "importmergecriteria": "Import fusionskriterier",
                "mode": "Mode",
                "modifycard": "Redigere kort",
                "nodelete": "Ingen sletning",
                "selectanattribute": "Vælg en egnskab",
                "selectmode": "Vælg tilstand",
                "templates": "Skabeloner"
            }
        },
        "localizations": {
            "activeonly": "Kun aktiv",
            "all": "Alle",
            "attributeclass": "Egnskab klass",
            "attributedomain": "Egnskab domæne",
            "attributegroup": "Egnskab gruppe",
            "attributeprocess": "Egnskab process",
            "attributereport": "Attribute-rapport",
            "cancel": "Annullere",
            "class": "Klass",
            "configuration": "Konfigurering",
            "csv": "CSV",
            "custompage": "Brugerdefineret side",
            "dashboard": "Dashboard",
            "defaultlanguage": "Standard sprog",
            "defaulttranslation": "Standardoversættelse",
            "domain": "Domæne",
            "element": "Element",
            "enabledlanguages": "Aktiverede sprog",
            "export": "Eksport",
            "file": "Fil",
            "format": "Format",
            "import": "Import",
            "languageconfiguration": "Sprogkonfiguration",
            "languages": "Sprog",
            "localization": "Lokalisering",
            "lookup": "Opslag",
            "menuitem": "Menupunkt",
            "pdf": "PDF",
            "process": "Process",
            "report": "Rapport",
            "section": "Afsnit",
            "separator": "Separator",
            "showlanguagechoice": "Vis sprogvalg",
            "treemenu": "Træmenu",
            "type": "Type",
            "view": "Oversigt"
        },
        "lookuptypes": {
            "strings": {
                "addvalue": "Tilføj værdi",
                "colorpreview": "Farve preview",
                "font": "Skrifttype",
                "generalproperties": "Standard engskab",
                "parentdescription": "Parent beskrivelse",
                "textcolor": "Tekst farve"
            },
            "title": "Opslag Typer",
            "toolbar": {
                "addClassBtn": {
                    "text": "Tilføj Opslag"
                },
                "classLabel": "List",
                "printSchemaBtn": {
                    "text": "Udskriv opslag"
                },
                "searchTextInput": {
                    "emptyText": "Søg i alle opslag…"
                }
            },
            "type": {
                "form": {
                    "fieldsets": {
                        "generalData": {
                            "inputs": {
                                "active": {
                                    "label": "Aktiv"
                                },
                                "name": {
                                    "label": "Navn"
                                },
                                "parent": {
                                    "label": "Parent"
                                }
                            }
                        }
                    },
                    "values": {
                        "active": "Aktiv"
                    }
                },
                "title": "Egenskaber",
                "toolbar": {
                    "cancelBtn": "Annullere",
                    "closeBtn": "Luk",
                    "deleteBtn": {
                        "tooltip": "Slet"
                    },
                    "editBtn": {
                        "tooltip": "Redigere"
                    },
                    "saveBtn": "Gem"
                }
            }
        },
        "menus": {
            "fieldlabels": {
                "newfolder": "Ny mappe"
            },
            "plural": "Menuer",
            "singular": "Menu",
            "strings": {
                "areyousuredeleteitem": "Er du sikker på, at du vil slette denne menu?",
                "delete": "Slet Menu",
                "emptyfoldername": "Mappe navn mangler"
            },
            "texts": {
                "add": "Tilføj Menu"
            },
            "tooltips": {
                "addfolder": "Tilføj mappe",
                "remove": "Fjern"
            }
        },
        "navigation": {
            "bim": "BIM",
            "classes": "Klasser",
            "customcomponents": "Brugerdefineret komponenter",
            "custompages": "Brugerdefinerede sider",
            "dashboards": "Kontrolpanel",
            "dms": "DMS",
            "domains": "domæner",
            "email": "E-mail",
            "generaloptions": "Generelle muligheder",
            "gis": "GIS",
            "gisnavigation": "Gis Navigation",
            "groupsandpermissions": "Grupper og tilladelser",
            "importexports": "Import/Eksport",
            "languages": "lokaliseringer",
            "layers": "Lag",
            "lookuptypes": "Søgeliste type",
            "menus": "Menu",
            "multitenant": "Multi-tenant",
            "navigationtrees": "Navigationtræ",
            "processes": "Processen",
            "reports": "Rapport",
            "searchfilters": "Søge filter",
            "servermanagement": "Server styre",
            "simples": "Simpel",
            "standard": "Standard",
            "systemconfig": "System config",
            "taskmanager": "Opgave styre",
            "title": "Navigation",
            "users": "Bruger",
            "views": "Oversigt",
            "workflow": "Arbejdsgang"
        },
        "navigationtrees": {
            "emptytexts": {
                "searchingrid": "Søg i gitter…",
                "searchnavigationtree": "Søg i gitter…"
            },
            "fieldlabels": {
                "actions": "Aktioner",
                "active": "Aktiv",
                "description": "Bekrivelse",
                "name": "Navn",
                "source": "Kilde"
            },
            "plural": "Navigeringstrær",
            "singular": "Navigeringstræ",
            "strings": {
                "sourceclass": "Kilde klasse"
            },
            "texts": {
                "addnavigationtree": "Tilføj træstruktur"
            },
            "tooltips": {
                "delete": "Slet navigeringstræet",
                "disable": "Deaktiver navigeringstræet",
                "edit": "Rediger navigeringstræet",
                "enable": "Aktiver navigeringstræet"
            }
        },
        "processes": {
            "fieldlabels": {
                "applicability": "Anvendelsesområde",
                "enginetype": "Motor type"
            },
            "properties": {
                "form": {
                    "fieldsets": {
                        "contextMenus": {
                            "actions": {
                                "delete": {
                                    "tooltip": "Slet"
                                },
                                "edit": {
                                    "tooltip": "Redigere"
                                },
                                "moveDown": {
                                    "tooltip": "Flyt ned"
                                },
                                "moveUp": {
                                    "tooltip": "Flyt op"
                                }
                            },
                            "inputs": {
                                "applicability": {
                                    "label": "Anvendelsesområde",
                                    "values": {
                                        "all": {
                                            "label": "Alle"
                                        },
                                        "many": {
                                            "label": "Aktuel og valgt"
                                        },
                                        "one": {
                                            "label": "Aktuel"
                                        }
                                    }
                                },
                                "javascriptScript": {
                                    "label": "Javascript script / brugerdefinerede GUI parametre"
                                },
                                "menuItemName": {
                                    "label": "Menupunktsnavn",
                                    "values": {
                                        "separator": {
                                            "label": "[---------]"
                                        }
                                    }
                                },
                                "status": {
                                    "label": "Status",
                                    "values": {
                                        "active": {
                                            "label": "Aktiv"
                                        }
                                    }
                                },
                                "typeOrGuiCustom": {
                                    "label": "Type / brugerdefineret GUI",
                                    "values": {
                                        "component": {
                                            "label": "Brugerdefineret GUI"
                                        },
                                        "custom": {
                                            "label": "Script Javascript"
                                        },
                                        "separator": {
                                            "label": "<em></em>"
                                        }
                                    }
                                }
                            },
                            "title": "Kontekstmenuer"
                        },
                        "defaultOrders": "Standard Bruger",
                        "generalData": {
                            "inputs": {
                                "active": {
                                    "label": "Aktiv"
                                },
                                "description": {
                                    "label": "Beskrivelse"
                                },
                                "enableSaveButton": {
                                    "label": "Skjul \"Gem\" knappen"
                                },
                                "name": {
                                    "label": "Navn"
                                },
                                "parent": {
                                    "label": "Arver fra"
                                },
                                "stoppableByUser": {
                                    "label": "Stoppes af brugeren"
                                },
                                "superclass": {
                                    "label": "Superklass"
                                }
                            }
                        },
                        "icon": "Ikon",
                        "processParameter": {
                            "inputs": {
                                "defaultFilter": {
                                    "label": "Standardfilter"
                                },
                                "flowStatusAttr": {
                                    "label": "Tilstand egenskab"
                                },
                                "messageAttr": {
                                    "label": "Besked egenskab"
                                }
                            },
                            "title": "Procesparametre"
                        },
                        "validation": {
                            "inputs": {
                                "validationRule": {
                                    "label": "Validerings regler"
                                }
                            },
                            "title": "Validering"
                        }
                    },
                    "inputs": {
                        "status": "Status"
                    },
                    "values": {
                        "active": "Aktiv"
                    }
                },
                "title": "Egenskaber",
                "toolbar": {
                    "cancelBtn": "Annullere",
                    "closeBtn": "Luk",
                    "deleteBtn": {
                        "tooltip": "Slet"
                    },
                    "disableBtn": {
                        "tooltip": "Deakiver"
                    },
                    "editBtn": {
                        "tooltip": "Redigere"
                    },
                    "enableBtn": {
                        "tooltip": "Akiver"
                    },
                    "saveBtn": "Gem",
                    "versionBtn": {
                        "tooltip": "Version"
                    }
                }
            },
            "strings": {
                "createnewcontextaction": "Opret ny konteksthandling",
                "engine": "Motor",
                "processattachments": "Procesvedhæftninger",
                "selectxpdlfile": "Vælg en XPDL fil",
                "template": "Skabelon",
                "xpdlfile": "XPDL fil"
            },
            "texts": {
                "process": "proces",
                "processactivated": "Proces korrekt aktiveret.",
                "processdeactivated": "Proces korrekt deaktiveret."
            },
            "title": "Proceser",
            "toolbar": {
                "addProcessBtn": {
                    "text": "Tilføj process"
                },
                "printSchemaBtn": {
                    "text": "Udskriv skema"
                },
                "processLabel": "Process",
                "searchTextInput": {
                    "emptyText": "Søg i alle porcesser"
                }
            }
        },
        "reports": {
            "emptytexts": {
                "searchingrid": "Søg i gitter…",
                "searchreports": "Søg rapporter…"
            },
            "fieldlabels": {
                "actions": "Aktioner",
                "active": "Aktiv",
                "description": "Beskrivelse",
                "name": "Navn",
                "zipfile": "ZIP fil"
            },
            "plural": "Rapport",
            "singular": "Rapport",
            "texts": {
                "addreport": "Tilføj rapport",
                "selectfile": "Vælg ZIP-fil"
            },
            "titles": {
                "file": "Fil"
            },
            "tooltips": {
                "delete": "Slet rapport",
                "disable": "Deaktiver rapport",
                "downloadpackage": "Hent rapport pakker",
                "edit": "Ret rapport",
                "enable": "Aktiver rapport",
                "viewsql": "SQL rapport oversigt"
            }
        },
        "searchfilters": {
            "fieldlabels": {
                "filters": "Filtre",
                "targetclass": "Mål klasse"
            },
            "texts": {
                "addfilter": "Tilføj filter",
                "chooseafunction": "Vælg en funktion",
                "defaultforgroup": "Standard for grupper",
                "fromfilter": "Fra filter",
                "fromsql": "Fra SQL",
                "fulltext": "Fuld tekst",
                "fulltextquery": "Fuldtekst forespørgsel",
                "writefulltextquery": "Skriv din fulde tekst forespørgsel"
            }
        },
        "systemconfig": {
            "ajaxtimeout": "AJAX timeout",
            "alfresco": "Alfresco",
            "cmis": "CMIS",
            "companylogo": "Virksomheds logo",
            "configurationmode": "Konfigurationstilstand",
            "dafaultjobusername": "Standard job brugernavn",
            "defaultpage": "Standard side",
            "disablesynconmissingvariables": "Deaktiver synkronisering manglende variabler",
            "dropcache": "Drop cache",
            "editmultitenantisnotallowed": "Rediger multitenant indstillinger er ikke tilladt",
            "enableattachmenttoclosedactivities": "Aktivér \"Tilføj vedhæftet fil\" til lukkede aktiviteter",
            "frequency": "Frekvens (sekunder)",
            "generals": "Generel",
            "gridautorefresh": "Gitter automatisk opdatering",
            "hidesavebutton": "Skjul \"Gem\" knappen",
            "host": "Vært",
            "initiallatitude": "Indledende breddegrad",
            "initialongitude": "Indledende længdegrad",
            "initialzoom": "Initial zoom",
            "instancename": "Instansnavn",
            "lockmanagement": "Låsestyring",
            "logo": "Logo",
            "maxlocktime": "Maksimal låsningstid (sekunder)",
            "multitenantactivationmessage": "Ændring af disse indstillinger er irreversibel, medmindre databasen er gendannet. Det anbefales at sikkerhedskopiere databasen, inden du fortsætter.",
            "multitenantapllychangesquest": "Vil du anvende ændringerne?",
            "multitenantinfomessage": "Det anbefales kun at ændre disse indstillinger efter at have konsulteret de retningslinjer, der findes i administratorhåndbogen, der kan downloades fra {0}",
            "noteinline": "Bemærk inline",
            "noteinlinedefaultclosed": "Bemærk standard inline lukket",
            "postgres": "Postgres",
            "preferredofficesuite": "Foretrukken kontor site",
            "preset": "forudindstillet",
            "referencecombolimit": "Referencebox-grænse",
            "relationlimit": "Relation grænse",
            "serviceurl": "Service URL",
            "sessiontimeout": "Session timeout",
            "shark": "Enhydra Shark",
            "showcardlockerusername": "Viser navnet på den bruger, der blokerede kortet",
            "synkservices": "Synkroniser tjenester",
            "tecnotecariver": "Tecnoteca River",
            "unlockallcards": "Lås alle kort op",
            "url": "Url",
            "usercandisable": "Bruger kan deaktivere",
            "webservicepath": "Webservice-sti"
        },
        "tasks": {
            "account": "Konto",
            "addtask": "Tilføj opgave",
            "advanceworkflow": "Avanceret arbejdsgang",
            "bodyparsing": "Body parsing",
            "category": "Kategori",
            "cron": "Cron",
            "day": "Dag",
            "dayofweek": "Ugedag",
            "directory": "Mappe",
            "emailtemplate": "E-mail skabelon",
            "emptytexts": {
                "searchcustompages": "Søg opgaver…",
                "searchingrid": "Søg i gitter…"
            },
            "erroremailtemplate": "Fejl email skabelon",
            "fieldlabels": {
                "account": "Konto",
                "actions": "Aktioner",
                "active": "Aktiv",
                "code": "Kode",
                "filter": "Filter",
                "filtertype": "Filter typpe",
                "incomingfolder": "Kommende folder",
                "processedfolder": "Behandlet mappe",
                "rejectedfolder": "Afviste mappe",
                "sender": "Afsender",
                "startonsave": "Start og gem",
                "subject": "Emne"
            },
            "filename": "Filnavn",
            "filepattern": "Fil mønster",
            "filtertype": "Filter typpe",
            "hour": "Time",
            "incomingfolder": "Kommende folder",
            "jobusername": "Job brugernavn",
            "keyenddelimiter": "Key end-afgrænser",
            "keystartdelimiter": "Key start-afgrænser",
            "minutes": "minutter",
            "month": "Måned",
            "movereject": "Flyt afviste ikke matchende",
            "notificationmode": "Meddelelsestilstand",
            "notifications": "Meddelelser",
            "parsing": "Parsing",
            "plural": "Opgaver",
            "postimportaction": "Post import handling",
            "processattributes": "Procesegnskaber",
            "processedfolder": "Behandlet mappe",
            "rejectedfolder": "Afviste mappe",
            "saveattachments": "Gem vedhæftede filer",
            "saveattachmentsdms": "Gem vedhæftede filer til DMS",
            "sender": "Afsender",
            "sendnotiifcation": "Send besked email",
            "settings": "Indstillinger",
            "sincurrentStepgular": "Opgave",
            "singular": "Opave",
            "source": "Kilde",
            "startprocess": "Start process",
            "strings": {
                "advanced": "Avanceret"
            },
            "subject": "Emne",
            "template": "Skabelon",
            "texts": {
                "addtask": "Tilføj opgave",
                "asyncronousevents": "Async begivenheder",
                "reademails": "Læs emails",
                "sendemails": "Send emails",
                "startprocesses": "Start processer",
                "syncronousevents": "Sync begivenheder",
                "wizardconnectors": "Guide forbinder"
            },
            "tooltips": {
                "cyclicexecution": "Cyklisk udførelse",
                "delete": "Slet opgave",
                "disable": "Deaktiver opgave",
                "edit": "Afslut opgve",
                "enable": "Aktivér opgave",
                "execution": "Udførelse",
                "singleexecution": "Enkelt udførelse",
                "start": "Start",
                "started": "Startede",
                "stop": "Afslut",
                "stopped": "Holdt op"
            },
            "type": "Type",
            "url": "URL",
            "value": "Værdi",
            "valueenddelimiter": "Værdi endeafgrænser",
            "valuestartdelimiter": "Værdi endeafgrænser"
        },
        "tesks": {
            "labels": {
                "activeonsave": "Akive i gem",
                "emailaccount": "E-mail konto",
                "filtertype": "Filter typpe",
                "incomingfolder": "Kommende folder"
            }
        },
        "title": "Administration",
        "users": {
            "fieldLabels": {
                "confirmpassword": "Bekræft kodeord",
                "defaultgroup": "Standard gruppe",
                "defaulttenant": "Standard tenant",
                "groups": "Grupper",
                "initialpage": "Indlendende side",
                "language": "Sprog",
                "multigroup": "Multi-gruppe",
                "multitenant": "Multi-tenant",
                "multitenantactivationprivileges": "Tillad multitenant",
                "nodata": "Ingen data",
                "privileged": "privilegeret",
                "service": "Service",
                "tenant": "Tenant",
                "tenants": "Tenants",
                "user": "Bruger"
            },
            "properties": {
                "form": {
                    "fieldsets": {
                        "generalData": {
                            "inputs": {
                                "active": {
                                    "label": "Aktiv"
                                },
                                "description": {
                                    "label": "Beskrivelse"
                                },
                                "name": {
                                    "label": "Navn"
                                },
                                "stoppableByUser": {
                                    "label": "Bruger stoppes"
                                }
                            }
                        }
                    }
                }
            },
            "title": "Bruger",
            "toolbar": {
                "addUserBtn": {
                    "text": "Tilføj bruger"
                },
                "searchTextInput": {
                    "emptyText": "Søg alle bruger"
                }
            }
        },
        "viewfilters": {
            "emptytexts": {
                "searchingrid": "Søg…"
            },
            "texts": {
                "addfilter": "Tilføj filter",
                "filterforgroup": "Filter for grupper"
            }
        },
        "views": {
            "addfilter": "Tilføj filter",
            "addview": "Tilføj visning",
            "ragetclass": "Mål klasse",
            "ralations": "Relationer",
            "targetclass": "Mål klasse"
        }
    }
});
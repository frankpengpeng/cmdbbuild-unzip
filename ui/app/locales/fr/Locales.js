(function() {
    Ext.define('CMDBuildUI.locales.fr.Locales', {
        "requires": ["CMDBuildUI.locales.fr.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "fr",
        "administration": CMDBuildUI.locales.fr.LocalesAdministration.administration,
        "attachments": {
            "add": "Ajouter des pièces jointes",
            "attachmenthistory": "Historique des pièces jointes",
            "author": "Auteur",
            "category": "Catégorie",
            "creationdate": "Date de création",
            "deleteattachment": "Supprimer la pièce jointe",
            "deleteattachment_confirmation": "Êtes-vous sûr de vouloir supprimer cette pièce jointe?",
            "description": "Description",
            "download": "Télécharger",
            "editattachment": "Modifier la pièce jointe",
            "file": "Fichier",
            "filename": "Nom de fichier",
            "majorversion": "Version majeure",
            "modificationdate": "Date de modification",
            "uploadfile": "Charger un fichier...",
            "version": "Version",
            "viewhistory": "Afficher l'historique des pièces jointes"
        },
        "bim": {
            "bimViewer": "Bim Viewer",
            "card": {
                "label": "Fiche"
            },
            "layers": {
                "label": "Couches",
                "menu": {
                    "hideAll": "Cacher tout",
                    "showAll": "Montrer tout"
                },
                "name": "Nom",
                "qt": "Qt",
                "visibility": "Visibilité",
                "visivility": "Visibilité"
            },
            "menu": {
                "camera": "Caméra",
                "frontView": "Vue de face",
                "mod": "Commandes de la visionneuse",
                "orthographic": "Caméra orthographique",
                "pan": "Panoramique",
                "perspective": "Caméra perspective",
                "resetView": "Réinitialiser la vue",
                "rotate": "Pivoter",
                "sideView": "Vue de côté",
                "topView": "Vue de dessus"
            },
            "showBimCard": "Ouvrir la visionneuse 3D",
            "tree": {
                "arrowTooltip": "Sélectionner un élément",
                "columnLabel": "Arbre",
                "label": "Arbre",
                "open_card": "Ouvrir la fiche associée",
                "root": "Ifc Root"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Ajouter une fiche",
                "clone": "Copier",
                "clonewithrelations": "Carte de clonage et relations",
                "deletecard": "Supprimer la fiche",
                "deleteconfirmation": "Êtes-vous sûr de vouloir supprimer cette carte?",
                "label": "Fiches",
                "modifycard": "Modifier la fiche",
                "opencard": "Carte ouverte",
                "print": "Carte d'impression"
            },
            "simple": "Simple",
            "standard": "Standard"
        },
        "common": {
            "actions": {
                "add": "Ajouter",
                "apply": "Appliquer",
                "cancel": "Abandonner",
                "close": "Fermer",
                "delete": "Supprimer",
                "edit": "Modifier",
                "execute": "Exécuter",
                "refresh": "Actualiser les données",
                "remove": "Supprimer",
                "save": "Enregistrer",
                "saveandapply": "Enregistrer et appliquer",
                "saveandclose": "Sauver et fermer",
                "search": "Chercher",
                "searchtext": "Chercher..."
            },
            "attributes": {
                "nogroup": "Données de base"
            },
            "dates": {
                "date": "j / m / a",
                "datetime": "d / m / Y H: i: s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "Nettoyer HTML"
            },
            "grid": {
                "disablemultiselection": "Désactiver la sélection multiple",
                "enamblemultiselection": "Activer la sélection multiple",
                "export": "Exporter des données",
                "filterremoved": "Le filtre actuel a été supprimé",
                "import": "Importer des données",
                "itemnotfound": "Objet non trouvé",
                "list": "liste",
                "opencontextualmenu": "Ouvrir le menu contextuel",
                "print": "Imprimer",
                "printcsv": "Imprimer en format CSV",
                "printodt": "Imprimer en ODT",
                "printpdf": "Imprimer en PDF",
                "row": "Article",
                "rows": "Articles",
                "subtype": "Sous-type"
            },
            "tabs": {
                "activity": "Activité",
                "attachments": "Pièces jointes",
                "card": "Fiche",
                "details": "Détails",
                "emails": "Emails",
                "history": "Historique",
                "notes": "Notes",
                "relations": "Relations"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Ajouter des pièces jointes depuis le dépôt",
            "alredyexistfile": "Existe déjà un fichier avec ce nom",
            "archivingdate": "Date d'archivage",
            "attachfile": "Joindre un fichier",
            "bcc": "Cci",
            "cc": "Cc",
            "composeemail": "Nouveau message",
            "composefromtemplate": "Créer à partir d'un modèle",
            "delay": "Temps d'attente",
            "delays": {
                "day1": "Dans 1 jour",
                "days2": "Dans 2 jours",
                "days4": "Dans 4 jours",
                "hour1": "1 heure",
                "hours2": "2 heures",
                "hours4": "4 heures",
                "month1": "Dans 1 mois",
                "none": "Aucun",
                "week1": "Dans 1 semaine",
                "weeks2": "Dans 2 semaines"
            },
            "dmspaneltitle": "Choisir des pièces jointes depuis la base de données",
            "edit": "Modifier",
            "from": "De",
            "gridrefresh": "Rafraîchir le tableau des valeurs",
            "keepsynchronization": "Rester synchronisé",
            "message": "Message",
            "regenerateallemails": "Regénérer tous les emails",
            "regenerateemail": "Regénérer les emails",
            "remove": "Supprimer",
            "remove_confirmation": "Êtes-vous sûr de vouloir supprimer cet email?",
            "reply": "Répondre",
            "replyprefix": "Sur {0}, {1} a écrit:",
            "selectaclass": "Sélectionnez une classe",
            "sendemail": "Envoyer un message électronique",
            "statuses": {
                "draft": "Brouillon",
                "outgoing": "Sortant",
                "received": "Reçu",
                "sent": "Envoyé"
            },
            "subject": "Sujet",
            "to": "À",
            "view": "Vue"
        },
        "errors": {
            "autherror": "Mauvais utilisateur ou mot de passe",
            "classnotfound": "Classe {0} non touvée",
            "notfound": "Non trouvé"
        },
        "filters": {
            "actions": "actes",
            "addfilter": "Ajouter un filtre",
            "any": "Tous",
            "attribute": "Choisir un attribut",
            "attributes": "Attributs",
            "clearfilter": "Supprimer les filtres",
            "clone": "Copier",
            "copyof": "Copie de",
            "description": "Description",
            "domain": "Lien",
            "filterdata": "Filtrer les données",
            "fromselection": "À partir de la sélection",
            "ignore": "Ignorer",
            "migrate": "Migrer",
            "name": "Nom",
            "newfilter": "Nouveau filtre",
            "noone": "Personne",
            "operator": "Opérateur",
            "operators": {
                "beginswith": "Commence par",
                "between": "Entre",
                "contained": "Contenu",
                "containedorequal": "Contenu ou égal",
                "contains": "Contient",
                "containsorequal": "Contient ou est égal",
                "different": "Différent",
                "doesnotbeginwith": "Ne commence pas par",
                "doesnotcontain": "Ne contient pas",
                "doesnotendwith": "Ne se termine pas par",
                "endswith": "Se termine par",
                "equals": "Égale",
                "greaterthan": "Majeur",
                "isnotnull": "N'est pas nul",
                "isnull": "Est nul",
                "lessthan": "Mineur"
            },
            "relations": "Relations",
            "type": "Type du serveur de fichiers",
            "typeinput": "Paramètre d'entrée",
            "value": "Valeur"
        },
        "gis": {
            "card": "Fiche",
            "cardsMenu": "Menu de cartes",
            "externalServices": "Services externes",
            "geographicalAttributes": "Attributs géographiques",
            "geoserverLayers": "Niveaux du Geoserver",
            "layers": "Niveaux",
            "list": "Liste",
            "map": "Carte",
            "mapServices": "Services de carte",
            "position": "Position",
            "root": "Racine",
            "tree": "Arbre de navigation",
            "view": "Vue",
            "zoom": "Zoom"
        },
        "history": {
            "activityname": "Nom d'activité",
            "activityperformer": "Performance d'activité",
            "begindate": "Date de début",
            "enddate": "Date de fin",
            "processstatus": "Statut",
            "user": "Utilisateur"
        },
        "importexport": {
            "downloadreport": "Télécharger le rapport",
            "emailfailure": "Une erreur est survenue lors de l'envoi du courrier électronique!",
            "emailsubject": "Rapport de données d'importation",
            "emailsuccess": "L'email a été envoyé avec succès!",
            "export": "Exportation",
            "import": "Importation",
            "importresponse": "Réponse d'importation",
            "response": {
                "created": "Articles créés",
                "deleted": "Eléments supprimés",
                "errors": "Erreurs",
                "linenumber": "Numéro de ligne",
                "message": "Message",
                "modified": "Articles modifiés",
                "processed": "Lignes traitées",
                "recordnumber": "Numéro d'enregistrement",
                "unmodified": "Articles non modifiés"
            },
            "sendreport": "Envoyer un rapport",
            "template": "Modèle",
            "templatedefinition": "Modèle de définition"
        },
        "login": {
            "buttons": {
                "login": "Se connecter",
                "logout": "Changer d'utilisateur"
            },
            "fields": {
                "group": "Groupe",
                "language": "Langue",
                "password": "Mot de passe",
                "tenants": "Les locataires",
                "username": "Nom d'utilisateur"
            },
            "loggedin": "Connecté",
            "title": "Se connecter",
            "welcome": "Bienvenue à nouveau {0}."
        },
        "main": {
            "administrationmodule": "Module d'administration",
            "baseconfiguration": "Configuration de base",
            "cardlock": {
                "lockedmessage": "Vous ne pouvez pas modifier cette carte car {0} la modifie.",
                "someone": "Quelqu'un"
            },
            "changegroup": "Changer de groupe",
            "changepassword": "Changer le mot de passe",
            "changetenant": "Changer de locataire",
            "confirmchangegroup": "Êtes-vous sûr de vouloir changer de groupe?",
            "confirmchangetenants": "Êtes-vous sûr de vouloir changer de locataire actif?",
            "confirmdisabletenant": "Êtes-vous sûr de vouloir désactiver l'indicateur \"Ignorer les locataires\"?",
            "confirmenabletenant": "Êtes-vous sûr de vouloir activer le drapeau \"Ignorer les locataires\"?",
            "confirmpassword": "Confirmer le mot de passe",
            "ignoretenants": "Ignorer les locataires",
            "info": "Info",
            "logo": {
                "cmdbuild": "Logo CMDBuild",
                "cmdbuildready2use": "CMDBuild READY2USE logo",
                "companylogo": "Logo d'entreprise",
                "openmaint": "logo openMAINT"
            },
            "logout": "Déconnexion",
            "managementmodule": "Module de gestion des données",
            "multigroup": "Multi-groupes",
            "multitenant": "Multi-locataire",
            "navigation": "Navigation",
            "newpassword": "Nouveau mot de passe",
            "oldpassword": "Ancien mot de passe",
            "pagenotfound": "Page non trouvée",
            "pleasecorrecterrors": "Veuillez corriger les erreurs indiquées!",
            "preferences": {
                "comma": "Virgule",
                "decimalserror": "Le champ décimal doit être présent",
                "decimalstousandserror": "Les  séparateurs décimales et des milliers doivent être différents",
                "default": "Défaut",
                "defaultvalue": "Valeur par défaut",
                "labeldateformat": "Format de date",
                "labeldecimalsseparator": "Séparateur décimal",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "langue",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "Séparateur de milliers",
                "labeltimeformat": "Format de l'heure",
                "msoffice": "Microsoft Office",
                "period": "Période",
                "preferredofficesuite": "Suite Office préférée",
                "space": "Espace",
                "thousandserror": "Le champ milliers doivent être présents",
                "timezone": "Fuseau horaire",
                "twelvehourformat": "Format de 12 heures",
                "twentyfourhourformat": "Format 24 heures"
            },
            "searchinallitems": "Rechercher dans tous les articles",
            "userpreferences": "Préférences"
        },
        "menu": {
            "allitems": "Tous les articles",
            "classes": "Classe",
            "custompages": "Pages personnalisées",
            "dashboards": "Tableaux de bord",
            "processes": "Processus",
            "reports": "Rapports",
            "views": "Vues"
        },
        "notes": {
            "edit": "Modifier la note"
        },
        "notifier": {
            "attention": "Attention",
            "error": "Erreur",
            "genericerror": "Erreur générique",
            "genericinfo": "Informations génériques",
            "genericwarning": "Avertissement générique",
            "info": "Info",
            "success": "Succès",
            "warning": "Attention"
        },
        "patches": {
            "apply": "Appliquer des patchs",
            "category": "Catégorie",
            "description": "Description",
            "name": "Prénom",
            "patches": "Patchs"
        },
        "processes": {
            "abortconfirmation": "Êtes-vous sûr de vouloir abandonner le processus ?",
            "abortprocess": "Abandonner le processus",
            "action": {
                "advance": "Avancer",
                "label": "Action"
            },
            "activeprocesses": "Processus actifs",
            "allstatuses": "Tous",
            "editactivity": "Modifier l'action",
            "openactivity": "Activité ouverte",
            "startworkflow": "Démarrer",
            "workflow": "Processus"
        },
        "relationGraph": {
            "activity": "Activité",
            "card": "Fiche",
            "cardList": "Liste des fiches",
            "cardRelation": "Relation",
            "cardRelations": "Relation des fiches",
            "choosenaviagationtree": "Choisir l'arbre de navigation",
            "class": "Classe",
            "class:": "Classe",
            "classList": "Liste de classe",
            "compoundnode": "Nœud composé",
            "enableTooltips": "Activer / désactiver les info-bulles sur le graphique",
            "level": "Niveau",
            "openRelationGraph": "Ouvrir le graphe des relations",
            "qt": "Qt",
            "refresh": "Refresh",
            "relation": "relation",
            "relationGraph": "Graphe des relations",
            "reopengraph": "Rouvrir le graphe de ce noeud"
        },
        "relations": {
            "adddetail": "Ajouter un détail",
            "addrelations": "Ajouter des relations",
            "attributes": "Attributs",
            "code": "Code",
            "deletedetail": "Supprimer le détail",
            "deleterelation": "Supprimer la relation",
            "description": "Description",
            "editcard": "Modifier la fiche",
            "editdetail": "Modifier le détail",
            "editrelation": "Modifier la relation",
            "mditems": "Objets",
            "opencard": "Ouvrir la fiche associée",
            "opendetail": "Voir le détail",
            "type": "Type du serveur de fichiers"
        },
        "reports": {
            "csv": "CSV",
            "download": "Télécharger",
            "format": "Format",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Imprimer",
            "reload": "Recharger",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "Ajouter un thématisme",
            "analysisType": "Type d'analyse",
            "attribute": "Attribut",
            "calculateRules": "Générer des règles de style",
            "clearThematism": "Effacer le thématisme",
            "color": "Couleur",
            "defineLegend": "Définition de la légende",
            "defineThematism": "Thématisme",
            "function": "Fonction",
            "generate": "Produire",
            "geoAttribute": "geoAttribute",
            "graduated": "Diplômé",
            "highlightSelected": "Mettre en surbrillance l'élément sélectionné",
            "intervals": "Intervalles",
            "legend": "Légende",
            "name": "prénom",
            "newThematism": "Nouveau thématisme",
            "punctual": "Ponctuel",
            "quantity": "Quantité",
            "source": "Source",
            "table": "Table",
            "thematism": "Thématismes",
            "value": "Valeur"
        },
        "widgets": {
            "customform": {
                "addrow": "Ajouter une ligne",
                "clonerow": "Cloner la ligne",
                "deleterow": "Supprimer la ligne",
                "editrow": "Modifier la ligne",
                "export": "Exporter",
                "import": "Importer",
                "refresh": "Actualiser aux valeurs par défaut"
            },
            "linkcards": {
                "editcard": "Modifier la carte",
                "opencard": "Carte ouverte",
                "refreshselection": "Appliquer la sélection par défaut",
                "togglefilterdisabled": "Désactiver le filtre de la table",
                "togglefilterenabled": "Activer le filtre de table"
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
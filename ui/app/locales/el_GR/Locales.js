(function() {
    Ext.define('CMDBuildUI.locales.el_GR.Locales', {
        "requires": ["CMDBuildUI.locales.el_GR.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "el_GR",
        "administration": CMDBuildUI.locales.el_GR.LocalesAdministration.administration,
        "attachments": {
            "add": "Προσθήκη συνημμένου",
            "attachmenthistory": "Ιστορικό συνημμένων",
            "author": "Συγγραφέας",
            "category": "Κατηγορία",
            "creationdate": "Ημερομηνία δημιουργίας",
            "deleteattachment": "Διαγραφή συνημμένου",
            "deleteattachment_confirmation": "Είστε βέβαιοι ότι θέλετε να διαγράψετε αυτό το συνημμένο;",
            "description": "Περιγραφή",
            "download": "Κατέβασμα",
            "editattachment": "Τροποποίηση του συνημμένου",
            "file": "Αρχείο",
            "filename": "Όνομα αρχείου",
            "majorversion": "Κύρια έκδοση",
            "modificationdate": "Ημερομηνία τροποποίησης",
            "uploadfile": "Ανέβασμα αρχείου...",
            "version": "Έκδοση",
            "viewhistory": "Προβολή ιστορικού συνημμένων"
        },
        "bim": {
            "bimViewer": "Bim Viewer",
            "card": {
                "label": "Κάρτα"
            },
            "layers": {
                "label": "Επίπεδα",
                "menu": {
                    "hideAll": "Απόκρυψη όλων",
                    "showAll": "Εμφάνιση όλων"
                },
                "name": "Ονομα",
                "qt": "Qt",
                "visibility": "Ορατότητα",
                "visivility": "Ορατότητα"
            },
            "menu": {
                "camera": "Φωτογραφική μηχανή",
                "frontView": "Εμπρόσθια όψη",
                "mod": "Έλεγχοι προβολής",
                "orthographic": "Ορθογραφική κάμερα",
                "pan": "Μετακινηθείτε",
                "perspective": "Προοπτική κάμερα",
                "resetView": "Επαναφορά προβολής",
                "rotate": "Περιστροφή",
                "sideView": "Πλευρική όψη",
                "topView": "Κάτοψη"
            },
            "showBimCard": "Ανοίξτε το πρόγραμμα προβολής 3D",
            "tree": {
                "arrowTooltip": "Επιλέξτε στοιχείο",
                "columnLabel": "Δέντρο",
                "label": "Δέντρο",
                "open_card": "Άνοιγμα σχετικής κάρτας",
                "root": "Ifc Root"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Προσθήκη κάρτας",
                "clone": "Κλώνος",
                "clonewithrelations": "Κλωνοποιήστε την κάρτα και τις σχέσεις",
                "deletecard": "Διαγραφή κάρτας",
                "deleteconfirmation": "Είστε βέβαιοι ότι θέλετε να διαγράψετε αυτήν την κάρτα;",
                "label": "Κάρτες",
                "modifycard": "Τροποποίηση κάρτας",
                "opencard": "Άνοιγμα κάρτας",
                "print": "Κάρτα εκτύπωσης"
            },
            "simple": "Απλό",
            "standard": "Standard"
        },
        "common": {
            "actions": {
                "add": "Προσθήκη",
                "apply": "Εφαρμογή",
                "cancel": "Ακύρωση",
                "close": "Κλείσιμο",
                "delete": "Διαγραφή",
                "edit": "Επεξεργασία",
                "execute": "Εκτέλεση",
                "refresh": "Ανανέωση δεδομένων",
                "remove": "Αφαίρεση",
                "save": "Αποθήκευση",
                "saveandapply": "Αποθήκευση και εφαρμογή",
                "saveandclose": "Αποθήκευση και κλείσιμο",
                "search": "Αναζήτηση",
                "searchtext": "Αναζήτηση…"
            },
            "attributes": {
                "nogroup": "Τα βασικά δεδομένα"
            },
            "dates": {
                "date": "d / m / Y",
                "datetime": "d/m/Y H:i:s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "Διαγραφή HTML"
            },
            "grid": {
                "disablemultiselection": "Απενεργοποιήστε την πολλαπλή επιλογή",
                "enamblemultiselection": "Ενεργοποιήστε την επιλογή πολλαπλών επιλογών",
                "export": "Εξαγωγή δεδομένων",
                "filterremoved": "Το τρέχον φίλτρο έχει αφαιρεθεί",
                "import": "Εισαγωγή δεδομένων",
                "itemnotfound": "Δεν βρέθηκε το αντικείμενο",
                "list": "Λίστα",
                "opencontextualmenu": "Ανοίξτε το μενού πλαισίου",
                "print": "Εκτύπωση",
                "printcsv": "Εκτύπωση ως CSV",
                "printodt": "Εκτύπωση ως ODT",
                "printpdf": "Εκτύπωση ως PDF",
                "row": "Στοιχείο",
                "rows": "Αντικείμενα",
                "subtype": "Υποτύπου"
            },
            "tabs": {
                "activity": "Δραστηριότητα",
                "attachments": "Συνημμένα αρχεία",
                "card": "Κάρτα",
                "details": "Λεπτομέρειες",
                "emails": "Emails",
                "history": "Ιστορικό",
                "notes": "Σημειώσεις",
                "relations": "Συσχετίσεις"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Προσθήκη συνημμένων από DMS",
            "alredyexistfile": "Υπάρχει ήδη ένα αρχείο με αυτό το όνομα",
            "archivingdate": "Ημερομηνία αρχειοθέτησης",
            "attachfile": "Επισύναψη αρχείου",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "Σύνθεση e-mail",
            "composefromtemplate": "Σύνθεση από το πρότυπο",
            "delay": "Καθυστέρηση",
            "delays": {
                "day1": "Σε 1 ημέρα",
                "days2": "Σε 2 ημέρες",
                "days4": "Σε 4 ημέρες",
                "hour1": "Σε 1 ώρα",
                "hours2": "Σε 2 ώρες",
                "hours4": "Σε 4 ώρες",
                "month1": "Σε 1 μήνα",
                "none": "Κανένα",
                "week1": "Σε 1 εβδομάδα",
                "weeks2": "Σε 2 εβδομάδες"
            },
            "dmspaneltitle": "Επιλογή των συνημμένων από τη Βάση Δεδομένων",
            "edit": "Επεξεργασία",
            "from": "Από",
            "gridrefresh": "Ανανέωση πλέγματος",
            "keepsynchronization": "Διατήρηση συγχρονισμού",
            "message": "Μήνυμα",
            "regenerateallemails": "Ξαναδημιουργία όλων των e-mail",
            "regenerateemail": "Ξαναδημιουργία e-mail",
            "remove": "Αφαίρεση",
            "remove_confirmation": "Είστε βέβαιοι ότι θέλετε να διαγράψετε αυτό το email;",
            "reply": "Απάντηση",
            "replyprefix": "{1} wrote:",
            "selectaclass": "Επιλέξτε μια κατηγορία",
            "sendemail": "Αποστολή e-mail",
            "statuses": {
                "draft": "Πρόχειρο",
                "outgoing": "Εξόδου",
                "received": "Ληφθέντα",
                "sent": "Απεσταλμένα"
            },
            "subject": "Θέμα",
            "to": "Προς",
            "view": "Προβολή"
        },
        "errors": {
            "autherror": "Λάθος όνομα χρήστη ή κωδικός πρόσβασης",
            "classnotfound": "Η κατηγορία {0} δεν βρέθηκε",
            "notfound": "Δεν βρέθηκε το αντικείμενο"
        },
        "filters": {
            "actions": "Ενέργειες",
            "addfilter": "Προσθήκη φίλτρου",
            "any": "Οποιοδήποτε",
            "attribute": "Επιλέξτε χαρακτηριστικό",
            "attributes": "Χαρακτηριστικά",
            "clearfilter": "Καθαρισμός φίλτρου",
            "clone": "Κλώνος",
            "copyof": "Αντίγραφο του",
            "description": "Περιγραφή",
            "domain": "Πεδίο ορισμού",
            "filterdata": "Φιλτράρετε δεδομένα",
            "fromselection": "Από επιλογή",
            "ignore": "Αγνοήστε",
            "migrate": "Μεταναστεύει",
            "name": "Όνομα",
            "newfilter": "Νέο φίλτρο",
            "noone": "Κανένα",
            "operator": "Χειριστής",
            "operators": {
                "beginswith": "Ξεκινήστε με",
                "between": "Ανάμεσα",
                "contained": "Περιέχονται",
                "containedorequal": "Περιλαμβανόμενο ή ίσο",
                "contains": "Περιλαμβάνει",
                "containsorequal": "Περιλαμβάνει ή είναι ίσο",
                "different": "Διαφορετικό",
                "doesnotbeginwith": "Δεν ξεκινάει με",
                "doesnotcontain": "Δεν περιέχει",
                "doesnotendwith": "Δεν τελειώνει με",
                "endswith": "Τελειώνει με",
                "equals": "Ισούται",
                "greaterthan": "Μεγαλότερο από",
                "isnotnull": "Δεν είναι κενό",
                "isnull": "Είναι κενό",
                "lessthan": "Λιγότερο από"
            },
            "relations": "Συσχετίσεις",
            "type": "Τύπος",
            "typeinput": "Παράμετρος εισαγωγής",
            "value": "Τιμή"
        },
        "gis": {
            "card": "Κάρτα",
            "cardsMenu": "Μενού Καρτών",
            "externalServices": "Εξωτερικές υπηρεσίες",
            "geographicalAttributes": "Γεωγραφικά χαρακτηριστικά",
            "geoserverLayers": "Επίπεδα Geoserver",
            "layers": "Επίπεδα",
            "list": "Λίστα",
            "map": "Χάρτης",
            "mapServices": "Υπηρεσίες Χάρτη",
            "position": "Θέση",
            "postition": "Θέση",
            "root": "Αρχή",
            "tree": "Δέντρο",
            "view": "Προβολή",
            "zoom": "Μεγέθυνση"
        },
        "history": {
            "activityname": "Όνομα δραστηριότητας",
            "activityperformer": "Εκτελεστής δραστηριότητας",
            "begindate": "Ημερομηνία έναρξης",
            "enddate": "Ημερομηνία λήξης",
            "processstatus": "Κατάσταση",
            "user": "Χρήστης"
        },
        "importexport": {
            "downloadreport": "Λήψη αναφοράς",
            "emailfailure": "Παρουσιάστηκε σφάλμα κατά την αποστολή email",
            "emailsubject": "Εισαγωγή αναφοράς δεδομένων",
            "emailsuccess": "Το μήνυμα ηλεκτρονικού ταχυδρομείου έχει σταλεί με επιτυχία!",
            "export": "Εξαγωγή",
            "import": "Εισαγωγή",
            "importresponse": "Απόκριση εισαγωγής",
            "response": {
                "created": "Δημιουργημένα στοιχεία",
                "deleted": "Διεγραμμένα αντικείμενα",
                "errors": "Λάθη",
                "linenumber": "Αριθμός γραμμής",
                "message": "Μήνυμα",
                "modified": "Τροποποιημένα στοιχεία",
                "processed": "Οι επεξεργασμένες γραμμές",
                "recordnumber": "Αριθμός εγγραφής",
                "unmodified": "Μη τροποποιημένα στοιχεία"
            },
            "sendreport": "Αποστολή αναφοράς",
            "template": "Πρότυπο",
            "templatedefinition": "<em>Template definition</em>"
        },
        "login": {
            "buttons": {
                "login": "Σύνδεση",
                "logout": "Αλλαγή χρήστη"
            },
            "fields": {
                "group": "Ομάδα",
                "language": "Γλώσσα",
                "password": "Κωδικός πρόσβασης",
                "tenants": "Οι μισθωτές",
                "username": "Όνομα χρήστη"
            },
            "loggedin": "Συνδεδεμένοι",
            "title": "Σύνδεση",
            "welcome": "Καλώς ορίσατε πίσω {0}."
        },
        "main": {
            "administrationmodule": "Ενότητα Διαχείρισης",
            "baseconfiguration": "Διαμόρφωση βάσης",
            "cardlock": {
                "lockedmessage": "Δεν μπορείτε να επεξεργαστείτε αυτήν την κάρτα επειδή ο {0} την επεξεργάζεται.",
                "someone": "Κάποιος"
            },
            "changegroup": "Αλλαγή ομάδας",
            "changepassword": "Αλλαγή κωδικού πρόσβασης",
            "changetenant": "Αλλαγή ενοικιαστή",
            "confirmchangegroup": "Είστε βέβαιοι ότι θέλετε να αλλάξετε την ομάδα;",
            "confirmchangetenants": "Είστε βέβαιοι ότι θέλετε να αλλάξετε ενεργούς ενοικιαστές;",
            "confirmdisabletenant": "Είστε βέβαιοι ότι θέλετε να απενεργοποιήσετε τη σημαία \"Αγνόηση ενοικιαστών\";",
            "confirmenabletenant": "Είστε βέβαιοι ότι θέλετε να ενεργοποιήσετε τη σημαία \"Αγνόηση ενοικιαστών\";",
            "confirmpassword": "Επιβεβαίωση κωδικού πρόσβασης",
            "ignoretenants": "Αγνοήστε τους μισθωτές",
            "info": "Πληροφορίες",
            "logo": {
                "cmdbuild": "Λογότυπο CMDBuild",
                "cmdbuildready2use": "Λογότυπο CMDBuild READY2USE",
                "companylogo": "Λογότυπο Εταιρείας",
                "openmaint": "το λογότυπο openMAINT"
            },
            "logout": "Αποσύνδεση",
            "managementmodule": "Ενότητα διαχείρησης δεδομένων",
            "multigroup": "Πολλαπλή ομάδα",
            "multitenant": "Πολλοί μισθωτές",
            "navigation": "Πλοήγηση",
            "newpassword": "Νέος κωδικός πρόσβασης",
            "oldpassword": "Παλιός κωδικός πρόσβασης",
            "pagenotfound": "Η σελίδα δεν βρέθηκε",
            "pleasecorrecterrors": "Παρακαλούμε διορθώστε τα υποδεικνυόμενα!",
            "preferences": {
                "comma": "Κόμμα",
                "decimalserror": "Πρέπει να υπάρχει πεδίο δεκαδικών",
                "decimalstousandserror": "Τα δεκαδικά ψηφία και οι διαχωριστές χιλιάδων πρέπει να είναι διαφορετικά",
                "default": "Προκαθορισμένο",
                "defaultvalue": "Προεπιλεγμένη τιμή",
                "labeldateformat": "Μορφή ημερομηνίας",
                "labeldecimalsseparator": "Διαχωριστής δεκαδικών",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "Γλώσσα",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "Δδιαχωριστικό Χιλιάδων",
                "labeltimeformat": "Μορφή ώρας",
                "msoffice": "Microsoft Office",
                "period": "Περίοδος",
                "preferredofficesuite": "Προτιμώμενη σουίτα του Office",
                "space": "Χώρος",
                "thousandserror": "Το πεδίο χιλιάδων πρέπει να είναι υπάρχει",
                "timezone": "Ζώνη ώρας",
                "twelvehourformat": "Μορφή 12 ωρών",
                "twentyfourhourformat": "Μορφή 24 ωρών"
            },
            "searchinallitems": "Αναζήτηση σε όλα τα στοιχεία",
            "userpreferences": "Προτιμήσεις"
        },
        "menu": {
            "allitems": "Ολα τα αντικείμενα",
            "classes": "Κατηγορίες",
            "custompages": "προσαρμοσμένες σελίδες",
            "dashboards": "Dashboards",
            "processes": "Διαδικασίες",
            "reports": "Αναφορές",
            "views": "Προβολές"
        },
        "notes": {
            "edit": "Τροποποίηση σημειώσεων"
        },
        "notifier": {
            "attention": "Προσοχή",
            "error": "Σφάλμα",
            "genericerror": "Γενικό σφάλμα",
            "genericinfo": "Γενικές πληροφορίες",
            "genericwarning": "Γενική προειδοποίηση",
            "info": "Πληροφορίες",
            "success": "Επιτυχία",
            "warning": "Προειδοποίηση"
        },
        "patches": {
            "apply": "Εφαρμογή επιδιορθώσεων",
            "category": "Κατηγορία",
            "description": "Περιγραφή",
            "name": "Ονομα",
            "patches": "Επιδιορθώσεις λογισμικού"
        },
        "processes": {
            "abortconfirmation": "Είστε σίγουροι ότι θέλετε να εγκαταλείψετε αυτή τη διαδικασία;",
            "abortprocess": "Ακύρωση διαδικασίας",
            "action": {
                "advance": "Προκαταβολή",
                "label": "Ενέργεια"
            },
            "activeprocesses": "Οι ενεργές διαδικασίες",
            "allstatuses": "Όλα",
            "editactivity": "Τροποποίηση δραστηριότητας",
            "openactivity": "Ανοικτή δραστηριότητα",
            "startworkflow": "Έναρξη",
            "workflow": "Διαδικασία"
        },
        "relationGraph": {
            "activity": "Δραστηριότητα",
            "card": "Κάρτα",
            "cardList": "Λίστα κάρτας",
            "cardRelation": "Συσχέτιση",
            "cardRelations": "Συσχετίσεις καρτών",
            "choosenaviagationtree": "Επιλέξτε ένα δέντρο πλοήγησης",
            "class": "Κατηγορία",
            "class:": "Κλάση",
            "classList": "Λίστα κατηγοριών",
            "compoundnode": "Σύνθετος κόμβος",
            "enableTooltips": "Ενεργοποίηση / απενεργοποίηση εργαλείων στο γράφημα",
            "level": "Επίπεδο",
            "openRelationGraph": "Άνοιγμα γραφήματος συσχετισης",
            "qt": "Qt",
            "refresh": "Ανανέωση",
            "relation": "Συσχετισμός",
            "relationGraph": "Γράφημα συσχετισης",
            "reopengraph": "Ανοίξτε ξανά το γράφημα από αυτόν τον κόμβο"
        },
        "relations": {
            "adddetail": "Προσθήκη λεπτομέρειας",
            "addrelations": "Προσθήκη συσχετίσεων",
            "attributes": "Χαρακτηριστικά",
            "code": "Κωδικός",
            "deletedetail": "Διαγραφή λεπτομέρειας",
            "deleterelation": "Διαγραφή συσχετισμού",
            "description": "Περιγραφή",
            "editcard": "Επεξεργασία κάρτας",
            "editdetail": "Επεξεργασία λεπτομέρειας",
            "editrelation": "Επεξεργασία συσχετισμού",
            "mditems": "Αντικείμενα",
            "opencard": "Άνοιγμα σχετικής κάρτας",
            "opendetail": "Εμφάνιση λεπτομερειών",
            "type": "Τύπος"
        },
        "reports": {
            "csv": "CSV",
            "download": "Κατέβασμα",
            "format": "Μορφή",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Εκτύπωση",
            "reload": "Επαναφόρτωση",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "<em>Add Thematism</em>",
            "analysisType": "Τύπος ανάλυσης",
            "attribute": "Χαρακτηριστικό",
            "calculateRules": "<em>Generate style rules</em>",
            "clearThematism": "<em>Clear Thematism</em>",
            "color": "Χρώμα",
            "defineLegend": "<em>Legend definition</em>",
            "defineThematism": "<em>Thematism definition</em>",
            "function": "Λειτουργία",
            "generate": "Δημιουργήστε",
            "geoAttribute": "<em>geoAttribute</em>",
            "graduated": "Αποφοίτησε",
            "highlightSelected": "Επισημάνετε το επιλεγμένο στοιχείο",
            "intervals": "Διαστήματα",
            "legend": "Μύθος",
            "name": "<em>name</em>",
            "newThematism": "<em>New Thematism</em>",
            "punctual": "Ακριβής",
            "quantity": "Ποσότητα",
            "source": "Πηγή",
            "table": "Πίνακας",
            "thematism": "Θεματισμοί",
            "value": "Τιμή"
        },
        "widgets": {
            "customform": {
                "addrow": "Προσθήκη γραμμής",
                "clonerow": "Κλωνοποίηση γραμμής",
                "deleterow": "Διαγραφή γραμμής",
                "editrow": "Επεξεργασία γραμμής",
                "export": "Εξαγωγή",
                "import": "Εισαγωγή",
                "refresh": "Ανανέωση στις προεπιλογές"
            },
            "linkcards": {
                "editcard": "Επεξεργασία κάρτας",
                "opencard": "Άνοιγμα κάρτας",
                "refreshselection": "Εφαρμογή προεπιλογής",
                "togglefilterdisabled": "Ενεργοποίηση φίλτρου πλέγματος",
                "togglefilterenabled": "Απενεργοποίηση φίλτρου πλέγματος"
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
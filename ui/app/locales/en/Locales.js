(function() {
    Ext.define('CMDBuildUI.locales.en.Locales', {
        "requires": ["CMDBuildUI.locales.en.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "en",
        "administration": CMDBuildUI.locales.en.LocalesAdministration.administration,
        "attachments": {
            "add": "Add attachment",
            "attachmenthistory": "Attachment History",
            "author": "Author",
            "category": "Category",
            "creationdate": "Creation date",
            "deleteattachment": "Delete attachment",
            "deleteattachment_confirmation": "Are you sure you want to delete this attachment?",
            "description": "Description",
            "download": "Download",
            "editattachment": "Modify attachment",
            "file": "File",
            "filename": "File name",
            "majorversion": "Major version",
            "modificationdate": "Modification date",
            "uploadfile": "Upload file...",
            "version": "Version",
            "viewhistory": "View attachment history"
        },
        "bim": {
            "bimViewer": "Bim Viewer",
            "card": {
                "label": "Card"
            },
            "layers": {
                "label": "Layers",
                "menu": {
                    "hideAll": "Hide all",
                    "showAll": "Show all"
                },
                "name": "Name",
                "qt": "Qt",
                "visibility": "Visibility",
                "visivility": "Visibility"
            },
            "menu": {
                "camera": "Camera",
                "frontView": "Front View",
                "mod": "<em>Viewer controls</em>",
                "orthographic": "Orthographic Camera",
                "pan": "<em>Scroll</em>",
                "perspective": "Perspective Camera",
                "resetView": "Reset View",
                "rotate": "Rotate",
                "sideView": "Side View",
                "topView": "Top View"
            },
            "showBimCard": "Open 3D viewer",
            "tree": {
                "arrowTooltip": "TODO",
                "columnLabel": "Tree",
                "label": "Tree",
                "open_card": "Open related card",
                "root": "Ifc Root"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Add card",
                "clone": "Clone",
                "clonewithrelations": "Clone card and relations",
                "deletecard": "Delete card",
                "deleteconfirmation": "Are you sure you want to delete this card?",
                "label": "Cards",
                "modifycard": "Modify card",
                "opencard": "Open card",
                "print": "Print card"
            },
            "simple": "Simple",
            "standard": "Standard"
        },
        "common": {
            "actions": {
                "add": "Add",
                "apply": "Apply",
                "cancel": "Cancel",
                "close": "Close",
                "delete": "Delete",
                "edit": "Edit",
                "execute": "Execute",
                "refresh": "Refresh data",
                "remove": "Remove",
                "save": "Save",
                "saveandapply": "Save and apply",
                "saveandclose": "Save and close",
                "search": "Search",
                "searchtext": "Search..."
            },
            "attributes": {
                "nogroup": "Base data"
            },
            "dates": {
                "date": "d/m/Y",
                "datetime": "d/m/Y H:i:s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "Clear HTML"
            },
            "grid": {
                "disablemultiselection": "Disable multi selection",
                "enamblemultiselection": "Enable multi selection",
                "export": "Export data",
                "filterremoved": "The current filter has been removed",
                "import": "Import data",
                "itemnotfound": "Item not found",
                "list": "List",
                "opencontextualmenu": "Open contextual menu",
                "print": "Print",
                "printcsv": "Print as CSV",
                "printodt": "Print as ODT",
                "printpdf": "Print as PDF",
                "row": "Item",
                "rows": "Items",
                "subtype": "Subtype"
            },
            "tabs": {
                "activity": "Activity",
                "attachments": "Attachments",
                "card": "Card",
                "details": "Details",
                "emails": "Emails",
                "history": "History",
                "notes": "Notes",
                "relations": "Relations"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Add attachments from DMS",
            "alredyexistfile": "Already exists a file with this name",
            "archivingdate": "Archiving date",
            "attachfile": "Attach file",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "Compose e-mail",
            "composefromtemplate": "Compose from template",
            "delay": "Delay",
            "delays": {
                "day1": "In 1 day",
                "days2": "In 2 days",
                "days4": "In 4 days",
                "hour1": "1 hour",
                "hours2": "2 hours",
                "hours4": "4 hours",
                "month1": "In 1 month",
                "none": "None",
                "week1": "In 1 week",
                "weeks2": "In 2 weeks"
            },
            "dmspaneltitle": "Choose attachments from Database",
            "edit": "Edit",
            "from": "From",
            "gridrefresh": "Grid refresh",
            "keepsynchronization": "Keep sync",
            "message": "Message",
            "regenerateallemails": "Regenerate all e-mails",
            "regenerateemail": "Regenerate e-mail",
            "remove": "Remove",
            "remove_confirmation": "Are you sure you want to delete this email?",
            "reply": "Reply",
            "replyprefix": "On {0}, {1} wrote:",
            "selectaclass": "Select a class",
            "sendemail": "Send e-mail",
            "statuses": {
                "draft": "Draft",
                "outgoing": "Outgoing",
                "received": "Received",
                "sent": "Sent"
            },
            "subject": "Subject",
            "to": "To",
            "view": "View"
        },
        "errors": {
            "autherror": "Wrong username or password",
            "classnotfound": "Class {0} not found",
            "notfound": "Item not found"
        },
        "filters": {
            "actions": "Actions",
            "addfilter": "Add filter",
            "any": "Any",
            "attribute": "Choose an attribute",
            "attributes": "Attributes",
            "clearfilter": "Clear filter",
            "clone": "Clone",
            "copyof": "Copy of",
            "description": "Description",
            "domain": "Domain",
            "filterdata": "Filter data",
            "fromselection": "From selection",
            "ignore": "Ignore",
            "migrate": "Migrates",
            "name": "Name",
            "newfilter": "New filter",
            "noone": "No one",
            "operator": "Operator",
            "operators": {
                "beginswith": "Begins with",
                "between": "Between",
                "contained": "Contained",
                "containedorequal": "Contained or equal",
                "contains": "Contains",
                "containsorequal": "Contains or equal",
                "different": "Different",
                "doesnotbeginwith": "Does not begin with",
                "doesnotcontain": "Does not contain",
                "doesnotendwith": "Does not end with",
                "endswith": "Ends with",
                "equals": "Equals",
                "greaterthan": "Greater than",
                "isnotnull": "Is not null",
                "isnull": "Is null",
                "lessthan": "Less than"
            },
            "relations": "Relations",
            "type": "Type",
            "typeinput": "Input Parameter",
            "value": "Value"
        },
        "gis": {
            "card": "Card",
            "cardsMenu": "Cards Menu",
            "externalServices": "External services",
            "geographicalAttributes": "Geographical attributes",
            "geoserverLayers": "Geoserver layers",
            "layers": "Layers",
            "list": "List",
            "map": "Map",
            "mapServices": "Map Services",
            "position": "Position",
            "root": "Root",
            "tree": "Navigation tree",
            "view": "View",
            "zoom": "Zoom"
        },
        "history": {
            "activityname": "Activity name",
            "activityperformer": "Activity performer",
            "begindate": "Begin date",
            "enddate": "End date",
            "processstatus": "Status",
            "user": "User"
        },
        "importexport": {
            "downloadreport": "Download report",
            "emailfailure": "Error occurred while sending email!",
            "emailsubject": "Import data report",
            "emailsuccess": "The email has been sent successfully!",
            "export": "Export",
            "import": "Import",
            "importresponse": "Import response",
            "response": {
                "created": "Created items",
                "deleted": "Deleted items",
                "errors": "Errors",
                "linenumber": "Line number",
                "message": "Message",
                "modified": "Modified items",
                "processed": "Processed rows",
                "recordnumber": "Record number",
                "unmodified": "Unmodified items"
            },
            "sendreport": "Send report",
            "template": "Template",
            "templatedefinition": "Template definition"
        },
        "login": {
            "buttons": {
                "login": "Login",
                "logout": "Change user"
            },
            "fields": {
                "group": "Group",
                "language": "Language",
                "password": "Password",
                "tenants": "Tenants",
                "username": "Username"
            },
            "loggedin": "Logged in",
            "title": "Login",
            "welcome": "Welcome back {0}."
        },
        "main": {
            "administrationmodule": "Administration module",
            "baseconfiguration": "Base configuration",
            "cardlock": {
                "lockedmessage": "You can't edit this card because {0} is editing it.",
                "someone": "someone"
            },
            "changegroup": "Change group",
            "changepassword": "Change password",
            "changetenant": "Change tenant",
            "confirmchangegroup": "Are you sure you want to change the group?",
            "confirmchangetenants": "Are you sure you want to change active tenants?",
            "confirmdisabletenant": "Are you sure you want to disable \"Ignore tenants\" flag?",
            "confirmenabletenant": "Are you sure you want to enable \"Ignore tenants\" flag?",
            "confirmpassword": "Confirm password",
            "ignoretenants": "Ignore tenants",
            "info": "Info",
            "logo": {
                "cmdbuild": "CMDBuild logo",
                "cmdbuildready2use": "CMDBuild READY2USE logo",
                "companylogo": "Company logo",
                "openmaint": "openMAINT logo"
            },
            "logout": "Logout",
            "managementmodule": "Data management module",
            "multigroup": "Multi group",
            "multitenant": "Multi tenant",
            "navigation": "Navigation",
            "newpassword": "New password",
            "oldpassword": "Old password",
            "pagenotfound": "Page not found",
            "pleasecorrecterrors": "Please correct indicated errors!",
            "preferences": {
                "comma": "Comma",
                "decimalserror": "Decimals field must be present",
                "decimalstousandserror": "Decimals and Thousands separato must be differents",
                "default": "Default",
                "defaultvalue": "Default value",
                "labeldateformat": "Date format",
                "labeldecimalsseparator": "Decimals separator",
                "labelintegerformat": "Integer format",
                "labellanguage": "Language",
                "labelnumericformat": "Numeric format",
                "labelthousandsseparator": "Thousands separator",
                "labeltimeformat": "Time format",
                "msoffice": "Microsoft Office",
                "period": "Period",
                "preferredofficesuite": "Preferred Office suite",
                "space": "Space",
                "thousandserror": "Thousands field must be present",
                "timezone": "Timezone",
                "twelvehourformat": "12-hour format",
                "twentyfourhourformat": "24-hour format"
            },
            "searchinallitems": "Search in all items",
            "userpreferences": "Preferences"
        },
        "menu": {
            "allitems": "All items",
            "classes": "Classes",
            "custompages": "Custom pages",
            "dashboards": "Dashboards",
            "processes": "Processes",
            "reports": "Reports",
            "views": "Views"
        },
        "notes": {
            "edit": "Modify note"
        },
        "notifier": {
            "attention": "Attention",
            "error": "Error",
            "genericerror": "Generic error",
            "genericinfo": "Generic info",
            "genericwarning": "Generic warning",
            "info": "Info",
            "success": "Success",
            "warning": "Warning"
        },
        "patches": {
            "apply": "Apply patches",
            "category": "Category",
            "description": "Description",
            "name": "Name",
            "patches": "Patches"
        },
        "processes": {
            "abortconfirmation": "Are you sure you want to abort this process?",
            "abortprocess": "Abort process",
            "action": {
                "advance": "Advance",
                "label": "Action"
            },
            "activeprocesses": "Active processes",
            "allstatuses": "All",
            "editactivity": "Modify activity",
            "openactivity": "Open activity",
            "startworkflow": "Start",
            "workflow": "Process"
        },
        "relationGraph": {
            "activity": "activity",
            "card": "Card",
            "cardList": "Card List",
            "cardRelation": "Relation",
            "cardRelations": "Relation",
            "choosenaviagationtree": "Choose navigation tree",
            "class": "Class",
            "class:": "Class",
            "classList": "Class List",
            "compoundnode": "Compound Node",
            "enableTooltips": "Enable/disable tooltips on graph",
            "level": "Level",
            "openRelationGraph": "Open relation graph",
            "qt": "Qt",
            "refresh": "Refresh",
            "relation": "Relation",
            "relationGraph": "Relation graph",
            "reopengraph": "Reopen the graph from this node"
        },
        "relations": {
            "adddetail": "Add detail",
            "addrelations": "Add relations",
            "attributes": "Attributes",
            "code": "Code",
            "deletedetail": "Delete detail",
            "deleterelation": "Delete relation",
            "description": "Description",
            "editcard": "Modify card",
            "editdetail": "Edit detail",
            "editrelation": "Edit relation",
            "mditems": "items",
            "opencard": "Open related card",
            "opendetail": "Show detail",
            "type": "Type"
        },
        "reports": {
            "csv": "CSV",
            "download": "Download",
            "format": "Format",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Print",
            "reload": "Reload",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "Add Thematism",
            "analysisType": "Analysis Type",
            "attribute": "Attribute",
            "calculateRules": "Generate style rules",
            "clearThematism": "Clear Thematism",
            "color": "Color",
            "defineLegend": "Legend definition",
            "defineThematism": "Thematism definition",
            "function": "Function",
            "generate": "generate",
            "geoAttribute": "geoAttribute",
            "graduated": "Graduated",
            "highlightSelected": "Highlight selected item",
            "intervals": "Intervals",
            "legend": "legend",
            "name": "name",
            "newThematism": "New Thematism",
            "punctual": "Punctual",
            "quantity": "Quantity",
            "source": "Source",
            "table": "Table",
            "thematism": "Thematisms",
            "value": "Value"
        },
        "widgets": {
            "customform": {
                "addrow": "Add row",
                "clonerow": "Clone row",
                "deleterow": "Delete row",
                "editrow": "Edit row",
                "export": "Export",
                "import": "Import",
                "refresh": "Refresh to defaults"
            },
            "linkcards": {
                "editcard": "Edit card",
                "opencard": "Open card",
                "refreshselection": "Apply default selection",
                "togglefilterdisabled": "Disable grid filter",
                "togglefilterenabled": "Enable grid filter"
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
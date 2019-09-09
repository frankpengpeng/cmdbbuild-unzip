(function() {
    Ext.define('CMDBuildUI.locales.tr.Locales', {
        "requires": ["CMDBuildUI.locales.tr.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "tr",
        "administration": CMDBuildUI.locales.tr.LocalesAdministration.administration,
        "attachments": {
            "add": "Ek ekle",
            "attachmenthistory": "<em>Attachment History</em>",
            "author": "Yazar",
            "category": "Kategori",
            "creationdate": "<em>Creation date</em>",
            "deleteattachment": "<em>Delete attachment</em>",
            "deleteattachment_confirmation": "<em>Are you sure you want to delete this attachment?</em>",
            "description": "Açıklama",
            "download": "İndir",
            "editattachment": "Eki değiştir",
            "file": "Dosya",
            "filename": "Dosya Adı",
            "majorversion": "Geçerli versiyon",
            "modificationdate": "Düzenlenme Tarihi",
            "uploadfile": "<em>Upload file...</em>",
            "version": "Versiyon",
            "viewhistory": "<em>View attachment history</em>"
        },
        "bim": {
            "bimViewer": "<em>Bim Viewer</em>",
            "card": {
                "label": "<em>Card</em>"
            },
            "layers": {
                "label": "<em>Layers</em>",
                "menu": {
                    "hideAll": "Tümünü Gizle",
                    "showAll": "Tümünü Göster"
                },
                "name": "<em>Name</em>",
                "qt": "<em>Qt</em>",
                "visibility": "<em>Visibility</em>",
                "visivility": "Görüş Mesafesi"
            },
            "menu": {
                "camera": "Kamera",
                "frontView": "<em>Front View</em>",
                "mod": "<em>Viewer controls</em>",
                "orthographic": "<em>Orthographic Camera</em>",
                "pan": "Kaydırma",
                "perspective": "<em>Perspective Camera</em>",
                "resetView": "<em>Reset View</em>",
                "rotate": "Döndür",
                "sideView": "<em>Side View</em>",
                "topView": "<em>Top View</em>"
            },
            "showBimCard": "<em>Open 3D viewer</em>",
            "tree": {
                "arrowTooltip": "<em>Select element</em>",
                "columnLabel": "<em>Tree</em>",
                "label": "<em>Tree</em>",
                "open_card": "Ilgili kartı aç",
                "root": "<em>Ifc Root</em>"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Kart Ekle",
                "clone": "Klonla",
                "clonewithrelations": "<em>Clone card and relations</em>",
                "deletecard": "Kartı Sil",
                "deleteconfirmation": "<em>Are you sure you want to delete this card?</em>",
                "label": "Kartlar",
                "modifycard": "Kartı değiştir",
                "opencard": "<em>Open card</em>",
                "print": "<em>Print card</em>"
            },
            "simple": "Basit",
            "standard": "Standart"
        },
        "common": {
            "actions": {
                "add": "Ekle",
                "apply": "Uygula",
                "cancel": "İptal",
                "close": "Kapat",
                "delete": "Sil",
                "edit": "Düzenle",
                "execute": "<em>Execute</em>",
                "refresh": "<em>Refresh data</em>",
                "remove": "Kaldır",
                "save": "Kaydet",
                "saveandapply": "Kaydet ve uygula",
                "saveandclose": "<em>Save and close</em>",
                "search": "<em>Search</em>",
                "searchtext": "<em>Search...</em>"
            },
            "attributes": {
                "nogroup": "<em>Base data</em>"
            },
            "dates": {
                "date": "<em>d/m/Y</em>",
                "datetime": "<em>d/m/Y H:i:s</em>",
                "time": "<em>H:i:s</em>"
            },
            "editor": {
                "clearhtml": "<em>Clear HTML</em>"
            },
            "grid": {
                "disablemultiselection": "<em>Disable multi selection</em>",
                "enamblemultiselection": "<em>Enable multi selection</em>",
                "export": "<em>Export data</em>",
                "filterremoved": "<em>The current filter has been removed</em>",
                "import": "<em>Import data</em>",
                "itemnotfound": "Öğe bulunamadı",
                "list": "<em>List</em>",
                "opencontextualmenu": "<em>Open contextual menu</em>",
                "print": "Yazdır",
                "printcsv": "<em>Print as CSV</em>",
                "printodt": "<em>Print as ODT</em>",
                "printpdf": "<em>Print as PDF</em>",
                "row": "<em>Item</em>",
                "rows": "<em>Items</em>",
                "subtype": "<em>Subtype</em>"
            },
            "tabs": {
                "activity": "Etkinlik",
                "attachments": "Ekler",
                "card": "Kart",
                "details": "Detaylar",
                "emails": "<em>Emails</em>",
                "history": "Tarihçe",
                "notes": "Notlar",
                "relations": "İlişkiler"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Veritabanın'dan ek ekle",
            "alredyexistfile": "<em>Already exists a file with this name</em>",
            "archivingdate": "Arşivleme tarihi",
            "attachfile": "Dosya ekle",
            "bcc": "Bcc - Gizli Alıcılar",
            "cc": "Cc - Bilgi Alıcılar",
            "composeemail": "E-posta oluştur",
            "composefromtemplate": "Şablondan oluştur",
            "delay": "Gecikme",
            "delays": {
                "day1": "1 Gün içinde",
                "days2": "2 Gün içinde",
                "days4": "4 Gün içinde",
                "hour1": "1 Saat",
                "hours2": "2 Saat",
                "hours4": "4 Saat",
                "month1": "1 Hafta içinde",
                "none": "Yok",
                "week1": "1 Hafta içinde",
                "weeks2": "2 Hafta içinde"
            },
            "dmspaneltitle": "Veritabanından ek seç",
            "edit": "Düzenle",
            "from": "İtibaren",
            "gridrefresh": "Izgara yenileme",
            "keepsynchronization": "Senkronize et",
            "message": "Message",
            "regenerateallemails": "Tüm e-postaları yenileyin",
            "regenerateemail": "E-postayı yeniden oluştur",
            "remove": "Kaldır",
            "remove_confirmation": "<em>Are you sure you want to delete this email?</em>",
            "reply": "Cevapla",
            "replyprefix": "<em>On {0}, {1} wrote:</em>",
            "selectaclass": "<em>Select a class</em>",
            "sendemail": "E-mail Gönder",
            "statuses": {
                "draft": "taslak",
                "outgoing": "Giden",
                "received": "Gelen",
                "sent": "Gönderilen"
            },
            "subject": "Konu",
            "to": "Alıcı",
            "view": "Görünüm"
        },
        "errors": {
            "autherror": "Yanlış kullanıcı adı veya parola",
            "classnotfound": "{0} Sııfı bulunamadı",
            "notfound": "Öğe bulunamadı"
        },
        "filters": {
            "actions": "<em>Actions</em>",
            "addfilter": "Filtre Ekle",
            "any": "Hiçbiri",
            "attribute": "Bir nitelik seçin",
            "attributes": "Öznitellikler",
            "clearfilter": "Filtreyi Temizle",
            "clone": "Klonla",
            "copyof": "için Kopya",
            "description": "Açıklama",
            "domain": "Domain",
            "filterdata": "<em>Filter data</em>",
            "fromselection": "Seçimden",
            "ignore": "<em>Ignore</em>",
            "migrate": "<em>Migrates</em>",
            "name": "Adı",
            "newfilter": "<em>New filter</em>",
            "noone": "Hiç kimse",
            "operator": "<em>Operator</em>",
            "operators": {
                "beginswith": "İle başlar",
                "between": "Arasında",
                "contained": "Varolan",
                "containedorequal": "Varolan veya eşit",
                "contains": "Kapsananlar",
                "containsorequal": "Kapsar veya eşittir",
                "different": "Farklı",
                "doesnotbeginwith": "Başlamaz",
                "doesnotcontain": "İçermiyor",
                "doesnotendwith": "Bitmiyor",
                "endswith": "İle biter",
                "equals": "Eşittir",
                "greaterthan": "Bundan büyük",
                "isnotnull": "Boş değil",
                "isnull": "Boş",
                "lessthan": "Bundan küçük"
            },
            "relations": "İlişkiler",
            "type": "Tip",
            "typeinput": "örnek Parametre",
            "value": "Değer"
        },
        "gis": {
            "card": "Kart",
            "cardsMenu": "<em>Cards Menu</em>",
            "externalServices": "Dış Hizmetler",
            "geographicalAttributes": "Coğrafi özellikler",
            "geoserverLayers": "CBS server katmanları",
            "layers": "Katmanlar",
            "list": "Liste",
            "map": "Harita",
            "mapServices": "<em>Map Services</em>",
            "position": "Pozisyonu",
            "root": "Kök dizin",
            "tree": "Navigasyon ağacı",
            "view": "Görünüm",
            "zoom": "Zoom"
        },
        "history": {
            "activityname": "Etkinlik Adı",
            "activityperformer": "Etkinliği gerçekleştiren",
            "begindate": "Başlangıç tarihi",
            "enddate": "Bitiş tarihi",
            "processstatus": "Durum",
            "user": "Kullanıcı"
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
                "login": "Giriş",
                "logout": "<em>Change user</em>"
            },
            "fields": {
                "group": "<em>Group</em>",
                "language": "Dil",
                "password": "Şifre",
                "tenants": "<em>Tenants</em>",
                "username": "Kullanıcı Adı"
            },
            "loggedin": "<em>Logged in</em>",
            "title": "Kullanıcı Girişi",
            "welcome": "<em>Welcome back {0}.</em>"
        },
        "main": {
            "administrationmodule": "Admin Paneli",
            "baseconfiguration": "<em>Base configuration</em>",
            "cardlock": {
                "lockedmessage": "<em>You can't edit this card because {0} is editing it.</em>",
                "someone": "<em>someone</em>"
            },
            "changegroup": "<em>Change group</em>",
            "changepassword": "Şifre değiştir",
            "changetenant": "<em>Change tenant</em>",
            "confirmchangegroup": "<em>Are you sure you want to change the group?</em>",
            "confirmchangetenants": "<em>Are you sure you want to change active tenants?</em>",
            "confirmdisabletenant": "<em>Are you sure you want to disable \"Ignore tenants\" flag?</em>",
            "confirmenabletenant": "<em>Are you sure you want to enable \"Ignore tenants\" flag?</em>",
            "confirmpassword": "Şifreyi Onayla",
            "ignoretenants": "<em>Ignore tenants</em>",
            "info": "Bilgi",
            "logo": {
                "cmdbuild": "<em>CMDBuild logo</em>",
                "cmdbuildready2use": "<em>CMDBuild READY2USE logo</em>",
                "companylogo": "<em>Company logo</em>",
                "openmaint": "<em>openMAINT logo</em>"
            },
            "logout": "Çıkış",
            "managementmodule": "Veri yönetimi modülü",
            "multigroup": "Çoklu grup",
            "multitenant": "<em>Multi tenant</em>",
            "navigation": "Navigasyon",
            "newpassword": "Yeni Şifre",
            "oldpassword": "Eski Şifre",
            "pagenotfound": "<em>Page not found</em>",
            "pleasecorrecterrors": "<em>Please correct indicated errors!</em>",
            "preferences": {
                "comma": "<em>Comma</em>",
                "decimalserror": "<em>Decimals field must be present</em>",
                "decimalstousandserror": "<em>Decimals and Thousands separato must be differents</em>",
                "default": "<em>Default</em>",
                "defaultvalue": "Varsayılan Değerler",
                "labeldateformat": "<em>Date format</em>",
                "labeldecimalsseparator": "<em>Decimals separator</em>",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "<em>Language</em>",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "<em>Thousands separator</em>",
                "labeltimeformat": "<em>Time format</em>",
                "msoffice": "<em>Microsoft Office</em>",
                "period": "<em>Period</em>",
                "preferredofficesuite": "<em>Preferred Office suite</em>",
                "space": "<em>Space</em>",
                "thousandserror": "<em>Thousands field must be present</em>",
                "timezone": "<em>Timezone</em>",
                "twelvehourformat": "<em>12-hour format</em>",
                "twentyfourhourformat": "<em>24-hour format</em>"
            },
            "searchinallitems": "<em>Search in all items</em>",
            "userpreferences": "<em>Preferences</em>"
        },
        "menu": {
            "allitems": "<em>All items</em>",
            "classes": "Sınıflar",
            "custompages": "Özel sayfalar",
            "dashboards": "<em>Dashboards</em>",
            "processes": "İşlemler",
            "reports": "<em>Reports</em>",
            "views": "Görünümler"
        },
        "notes": {
            "edit": "Notu değiştir"
        },
        "notifier": {
            "attention": "Dikkat",
            "error": "Hata",
            "genericerror": "<em>Generic error</em>",
            "genericinfo": "<em>Generic info</em>",
            "genericwarning": "<em>Generic warning</em>",
            "info": "Bilgi",
            "success": "Başarılı",
            "warning": "UYARI"
        },
        "patches": {
            "apply": "<em>Apply patches</em>",
            "category": "<em>Category</em>",
            "description": "<em>Description</em>",
            "name": "<em>Name</em>",
            "patches": "<em>Patches</em>"
        },
        "processes": {
            "abortconfirmation": "Bu işlemi iptal etmek istediğinizden emin misiniz?",
            "abortprocess": "İptal işlemi",
            "action": {
                "advance": "İleri Düzey",
                "label": "<em>Action</em>"
            },
            "activeprocesses": "<em>Active processes</em>",
            "allstatuses": "Tümü",
            "editactivity": "Etkinliği değiştir",
            "openactivity": "<em>Open activity</em>",
            "startworkflow": "Başlangıç",
            "workflow": "Süreç"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Kart",
            "cardList": "<em>Card List</em>",
            "cardRelation": "İlişki",
            "cardRelations": "İlişki",
            "choosenaviagationtree": "<em>Choose navigation tree</em>",
            "class": "<em>Class</em>",
            "class:": "Sınıf",
            "classList": "<em>Class List</em>",
            "compoundnode": "<em>Compound Node</em>",
            "enableTooltips": "<em>Enable/disable tooltips on graph</em>",
            "level": "<em>Level</em>",
            "openRelationGraph": "ilişki grafiğini Aç",
            "qt": "<em>Qt</em>",
            "refresh": "<em>Refresh</em>",
            "relation": "İlişki",
            "relationGraph": "Grafik ilişkileri",
            "reopengraph": "<em>Reopen the graph from this node</em>"
        },
        "relations": {
            "adddetail": "Ayrıntı ekle",
            "addrelations": "İlişki Ekle",
            "attributes": "Öznitellikler",
            "code": "Kod",
            "deletedetail": "Ayrıntı Sil",
            "deleterelation": "Ilişkiyi sil",
            "description": "Açıklama",
            "editcard": "Kartı değiştir",
            "editdetail": "Ayrıntı Düzenle",
            "editrelation": "İlişkim Düzenle",
            "mditems": "<em>items</em>",
            "opencard": "Ilgili kartı aç",
            "opendetail": "Detayları göster",
            "type": "Tip"
        },
        "reports": {
            "csv": "<em>CSV</em>",
            "download": "İndir",
            "format": "Format",
            "odt": "<em>ODT</em>",
            "pdf": "<em>PDF</em>",
            "print": "Yazdır",
            "reload": "Tekrar yükle",
            "rtf": "<em>RTF</em>"
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
                "addrow": "Satır Ekle",
                "clonerow": "Satırı Klonla",
                "deleterow": "Satır Sil",
                "editrow": "Satırı düzenle",
                "export": "Dışarı Aktar",
                "import": "İçeri Aktar",
                "refresh": "<em>Refresh to defaults</em>"
            },
            "linkcards": {
                "editcard": "<em>Edit card</em>",
                "opencard": "<em>Open card</em>",
                "refreshselection": "Varsayılan seçimi uygula",
                "togglefilterdisabled": "Izgara filtresini devre dışı bırak",
                "togglefilterenabled": "Izgara filtresini etkinleştir"
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
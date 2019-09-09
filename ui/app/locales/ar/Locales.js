(function() {
    Ext.define('CMDBuildUI.locales.ar.Locales', {
        "requires": ["CMDBuildUI.locales.ar.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "ar",
        "administration": CMDBuildUI.locales.ar.LocalesAdministration.administration,
        "attachments": {
            "add": "أضف مرفق",
            "attachmenthistory": "تاريخ المرفق",
            "author": "المؤلف",
            "category": "الفئة",
            "creationdate": "تاريخ الإنشاء",
            "deleteattachment": "حذف المرفق",
            "deleteattachment_confirmation": "هل أنت متأكد من حذف المرفق؟",
            "description": "الوصف",
            "download": "تنزيل",
            "editattachment": "تعديل المرفق",
            "file": "ملف",
            "filename": "اسم الملف",
            "majorversion": "الإصدار الرئيسي",
            "modificationdate": "تاريخ التعديل",
            "uploadfile": "رفع ملف...",
            "version": "الإصدار",
            "viewhistory": "عرض تاريخ المرفق"
        },
        "bim": {
            "bimViewer": "عارض نمذجة معلومات البناء",
            "card": {
                "label": "البطاقة"
            },
            "layers": {
                "label": "الطبقات",
                "menu": {
                    "hideAll": "إخفاء الكل",
                    "showAll": "عرض الكل"
                },
                "name": "الاسم",
                "qt": "الكم",
                "visibility": "المرئية",
                "visivility": "المرئية"
            },
            "menu": {
                "camera": "الكاميرا",
                "frontView": "عرض أمامي",
                "mod": "متحكمات العارض",
                "orthographic": "الكاميرا التصويرية",
                "pan": "تمرير",
                "perspective": "الكاميرا المنظورية",
                "resetView": "عرض طبيعي",
                "rotate": "تدوير",
                "sideView": "عرض جانبي",
                "topView": "عرض علوي"
            },
            "showBimCard": "فتح العارض الثلاثي الأبعاد",
            "tree": {
                "arrowTooltip": "ما يجب عمله",
                "columnLabel": "الشجرة",
                "label": "الشجرة",
                "open_card": "فتح البطاقة ذات الصلة",
                "root": "جذر IFC"
            }
        },
        "classes": {
            "cards": {
                "addcard": "أضف بطاقة",
                "clone": "نسخ",
                "clonewithrelations": "نسخ البطاقة والعلاقات",
                "deletecard": "حذف بطاقة",
                "deleteconfirmation": "هل أنت متأكد من حذف هذه البطاقة؟",
                "label": "البطاقات",
                "modifycard": "تعديل البطاقة",
                "opencard": "فتح البطاقة",
                "print": "طباعة البطاقة"
            },
            "simple": "بسيط",
            "standard": "قياسي"
        },
        "common": {
            "actions": {
                "add": "أضف",
                "apply": "إعمال",
                "cancel": "إلغاء",
                "close": "غلق",
                "delete": "حذف",
                "edit": "تحرير",
                "execute": "تنفيذ",
                "refresh": "تحديث البيانات",
                "remove": "حذف",
                "save": "حفظ",
                "saveandapply": "حفظ وإعمال",
                "saveandclose": "حفظ وغلق",
                "search": "بحث",
                "searchtext": "بحث..."
            },
            "attributes": {
                "nogroup": "البيانات الأساسية"
            },
            "dates": {
                "date": "d/m/Y",
                "datetime": "d/m/Y H:i:s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "تصفية الـ HTML"
            },
            "grid": {
                "disablemultiselection": "تعطيل التحديد المتعدد",
                "enamblemultiselection": "تفعيل التحديد المتعدد",
                "export": "تصدير البيانات",
                "filterremoved": "تم حذف الفرز الحالي",
                "import": "استيراد البيانات",
                "itemnotfound": "العنصر غير موجود",
                "list": "القائمة",
                "opencontextualmenu": "فتح قائمة السياق",
                "print": "طباعة",
                "printcsv": "طباعة كـ CSV",
                "printodt": "طباعة كـ ODT",
                "printpdf": "طباعة كـ PDF",
                "row": "عنصر",
                "rows": "عناصر",
                "subtype": "نوع فرعي"
            },
            "tabs": {
                "activity": "النشاط",
                "attachments": "المرفقات",
                "card": "البطاقة",
                "details": "التفاصيل",
                "emails": "الرسائل",
                "history": "التاريخ",
                "notes": "الملاحظات",
                "relations": "العلاقات"
            }
        },
        "emails": {
            "addattachmentsfromdms": "أضف مرفق من نظام الملفات",
            "alredyexistfile": "هناك ملف موجود يحمل نفس الاسم",
            "archivingdate": "تاريخ الأرشفة",
            "attachfile": "أرفق ملف",
            "bcc": "نسخة كبرونية عمياء",
            "cc": "نسخة كربونية",
            "composeemail": "أنشئ بريد إلكتروني",
            "composefromtemplate": "أنشئ من قالب",
            "delay": "التأخير",
            "delays": {
                "day1": "في يوم واحد",
                "days2": "في يومين",
                "days4": "في أربعة أيام",
                "hour1": "ساعة واحدة",
                "hours2": "ساعتان",
                "hours4": "أربع ساعات",
                "month1": "في شهر واحد",
                "none": "بلا",
                "week1": "في اسبوع",
                "weeks2": "في اسبوعان"
            },
            "dmspaneltitle": "اختر مرفقات من قاعدة البيانات",
            "edit": "تحرير",
            "from": "من",
            "gridrefresh": "إعادة تحميل لائحة البطافات",
            "keepsynchronization": "أبقي متزامن",
            "message": "الرسالة",
            "regenerateallemails": "إعادة تجديد كل الرسائل الإلكترونية",
            "regenerateemail": "إعادة تجديد الرسالة الإلكترونية",
            "remove": "حذف",
            "remove_confirmation": "هل أنت متأكد من حذف هذه الرسائل؟",
            "reply": "الرد",
            "replyprefix": "على {0}, {1} كتب:",
            "selectaclass": "اختر صنف",
            "sendemail": "أرسل رسالة",
            "statuses": {
                "draft": "مسودة",
                "outgoing": "صادر",
                "received": "مستلم",
                "sent": "مرسل"
            },
            "subject": "العنوان",
            "to": "إلى",
            "view": "عرض"
        },
        "errors": {
            "autherror": "اسم مستخدم وكلمة سر غير صحيحة",
            "classnotfound": "الصنف {0} لا يوجد",
            "notfound": "العنصر غير موجود"
        },
        "filters": {
            "actions": "الأفعال",
            "addfilter": "أضف فرز",
            "any": "أي",
            "attribute": "اختر سمة",
            "attributes": "السمات",
            "clearfilter": "مسح الفرز",
            "clone": "نسخ",
            "copyof": "نسخة من",
            "description": "الوصف",
            "domain": "العلاقة",
            "filterdata": "بيانات الفرز",
            "fromselection": "من المُختار",
            "ignore": "إهمال",
            "migrate": "ترحيل",
            "name": "اسم",
            "newfilter": "فرز جديد",
            "noone": "لا أحد",
            "operator": "العملية",
            "operators": {
                "beginswith": "يبدأ بـ",
                "between": "بين",
                "contained": "تتضمن",
                "containedorequal": "تتضمن أو تساوي",
                "contains": "تحتوي",
                "containsorequal": "تحتوي أو تساوي",
                "different": "مختلف",
                "doesnotbeginwith": "لا يبدأ بـ ",
                "doesnotcontain": "لا يحتوي على",
                "doesnotendwith": "لا ينتهي بـ",
                "endswith": "ينتهي بـ",
                "equals": "يساوي",
                "greaterthan": "أكبر من",
                "isnotnull": "ليس بفارغ",
                "isnull": "فارغ",
                "lessthan": "أصغر من"
            },
            "relations": "العلاقات",
            "type": "النوع",
            "typeinput": "المعامل المدخل",
            "value": "القيمة"
        },
        "gis": {
            "card": "البطاقة",
            "cardsMenu": "قائمة البطاقات",
            "externalServices": "الخدمات الخارجية",
            "geographicalAttributes": "السمات الجغرافية",
            "geoserverLayers": "طبقات الـ Geoserver",
            "layers": "الطبقات",
            "list": "اللائحة",
            "map": "الخريطة",
            "mapServices": "خدمات الخريطة",
            "position": "الموضع",
            "root": "الجذر",
            "tree": "شجرة الملاحة",
            "view": "عرض",
            "zoom": "تكبير"
        },
        "history": {
            "activityname": "اسم النشاط",
            "activityperformer": "منجز النشاط",
            "begindate": "تاريخ البداية",
            "enddate": "تاريخ النهاية",
            "processstatus": "الحالة",
            "user": "المستخدم"
        },
        "importexport": {
            "downloadreport": "تنزيل التقرير",
            "emailfailure": "حدث خطأ أثناء إرسال البريد!",
            "emailsubject": "استيراد تقرير البيانات",
            "emailsuccess": "تم إرسال البريد بنجاح!",
            "export": "تصدير",
            "import": "استيراد",
            "importresponse": "استيراد الرد",
            "response": {
                "created": "العناصر المنشئة",
                "deleted": "العناصر المحذوفة",
                "errors": "الأخطاء",
                "linenumber": "رقم السطر",
                "message": "الرسالة",
                "modified": "العناصر المعدلة",
                "processed": "الصفوف المعالجة",
                "recordnumber": "رقم السجل",
                "unmodified": "العناصر غير المعدلة"
            },
            "sendreport": "إرسال التقرير",
            "template": "قالب",
            "templatedefinition": "<em>Template definition</em>"
        },
        "login": {
            "buttons": {
                "login": "الدخول",
                "logout": "تغيير المستخدم"
            },
            "fields": {
                "group": "المجموعة",
                "language": "اللغة",
                "password": "كلمة السر",
                "tenants": "المستأجرين",
                "username": "اسم المستخدم"
            },
            "loggedin": "مسجل الدخول",
            "title": "الدخول",
            "welcome": "مرحبا {0}."
        },
        "main": {
            "administrationmodule": "لوحة الإدارة",
            "baseconfiguration": "الإعدادات الأساسية",
            "cardlock": {
                "lockedmessage": "لا يمكنك تحرير هذه البطاقة لأن {0} قاعد يحررها.",
                "someone": "شخص ما"
            },
            "changegroup": "تغيير المجموعة",
            "changepassword": "تغيير كلمة السر",
            "changetenant": "تغيير المستأجر",
            "confirmchangegroup": "هل أنت متأكد من أنك تريد تغيير هذه المجموعة؟",
            "confirmchangetenants": "هل أنت متأكد من تغيير المستأجرين النشطين؟",
            "confirmdisabletenant": "هل أنت متأكد من تعطيل خيار \"تجاهل المستأجرين\"?",
            "confirmenabletenant": "هل أنت متأكد من أنك تريد تنشيط خيار \"تجاهل المستأجرين\"?",
            "confirmpassword": "تأكيد كلمة السر",
            "ignoretenants": "تجاهل المستأجرين",
            "info": "معلومات",
            "logo": {
                "cmdbuild": "CMDBuild شعار",
                "cmdbuildready2use": "CMDBuild READY2USE شعار",
                "companylogo": "شعار الشركة",
                "openmaint": "openMAINT شعار"
            },
            "logout": "خروج",
            "managementmodule": "وحدة إدارة البيانات",
            "multigroup": "متعدد المجموعة",
            "multitenant": "متعدد المستأجرين",
            "navigation": "الملاحة",
            "newpassword": "كلمة السر الجديدة",
            "oldpassword": "كلمة السر القديمة",
            "pagenotfound": "الصفحة غير موجودة",
            "pleasecorrecterrors": "رجاءً، صحح الأخطاء المشار إليها!",
            "preferences": {
                "comma": "فاصلة",
                "decimalserror": "حقل عشري يجب أن يكون حاضر",
                "decimalstousandserror": "يجب أن تكونا الفاصلة العشرية والألفية مختلفتان",
                "default": "الافتراضي",
                "defaultvalue": "القيمة الأساسية",
                "labeldateformat": "نسق التاريخ",
                "labeldecimalsseparator": "الفاصلة العشرية",
                "labelintegerformat": "نسق العدد الصحيح",
                "labellanguage": "اللغة",
                "labelnumericformat": "نسق العدد",
                "labelthousandsseparator": "الفاصلة الألفية",
                "labeltimeformat": "نسق الوقت",
                "msoffice": "مايكروسوفت أوفيس",
                "period": "الفترة",
                "preferredofficesuite": "حزمة الأوفيس المفضلة",
                "space": "مسافة",
                "thousandserror": "حقل ألفي يجب أن يكون حاضر",
                "timezone": "المنطقة الزمنية",
                "twelvehourformat": "نظام الـ 12 ساعة",
                "twentyfourhourformat": "نظام الـ 24 ساعة"
            },
            "searchinallitems": "بحث في كل العناصر",
            "userpreferences": "التفضيلات"
        },
        "menu": {
            "allitems": "كل العناصر",
            "classes": "الأصناف",
            "custompages": "الصفحات الخيارية",
            "dashboards": "لوح المعلومات",
            "processes": "الآليات",
            "reports": "التقارير",
            "views": "الاشتقاقات"
        },
        "notes": {
            "edit": "تعديل الملاحظة"
        },
        "notifier": {
            "attention": "انتباه",
            "error": "خطأ",
            "genericerror": "خطأ عام",
            "genericinfo": "معلومة عامة",
            "genericwarning": "تنبيه عام",
            "info": "معلومات",
            "success": "نجاح",
            "warning": "تنبيه"
        },
        "patches": {
            "apply": "تطبيق التغييرات",
            "category": "الفئة",
            "description": "الوصف",
            "name": "الاسم",
            "patches": "التغييرات"
        },
        "processes": {
            "abortconfirmation": "هل أنت متأكد من أنك تريد أن تجهض هذه الآلية؟",
            "abortprocess": "إجهاض الآلية",
            "action": {
                "advance": "تقدم",
                "label": "فعل"
            },
            "activeprocesses": "العمليات النشطة",
            "allstatuses": "الكل",
            "editactivity": "تحرير النشاط",
            "openactivity": "فتح نشاط",
            "startworkflow": "بدء",
            "workflow": "الآلية"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "البطاقة",
            "cardList": "قائمة البطاقة",
            "cardRelation": "العلاقة",
            "cardRelations": "العلاقة",
            "choosenaviagationtree": "اختر شجرة التنقل",
            "class": "الصنف",
            "class:": "الصنف:",
            "classList": "قائمة الصنف",
            "compoundnode": "العقدة المركبة",
            "enableTooltips": "تنشيط/تعطيل صندوق التلميح على الرسمة",
            "level": "المستوى",
            "openRelationGraph": "فتح مخطوطة العلاقات",
            "qt": "كم",
            "refresh": "تحديث",
            "relation": "العلاقة",
            "relationGraph": "رسمة العلاقات",
            "reopengraph": "فتح الرسمة من هذه العقدة"
        },
        "relations": {
            "adddetail": "أضف تفصيل",
            "addrelations": "أضف علاقات",
            "attributes": "السمات",
            "code": "الرمز",
            "deletedetail": "حذف تفصيل",
            "deleterelation": "حذف علاقة",
            "description": "الوصف",
            "editcard": "تعديل البطاقة",
            "editdetail": "تحرير تفصيل",
            "editrelation": "تحرير العلاقة",
            "mditems": "العناصر",
            "opencard": "فتح البطاقة ذات الصلة",
            "opendetail": "أظهر التفصيل",
            "type": "النوع"
        },
        "reports": {
            "csv": "CSV",
            "download": "تنزيل",
            "format": "تنسيق",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "طباعة",
            "reload": "إعادة تحميل",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "<em>Add Thematism</em>",
            "analysisType": "نوع التحليل",
            "attribute": "سمة",
            "calculateRules": "<em>Generate style rules</em>",
            "clearThematism": "<em>Clear Thematism</em>",
            "color": "لون",
            "defineLegend": "<em>Legend definition</em>",
            "defineThematism": "<em>Thematism definition</em>",
            "function": "<em>Function</em>",
            "generate": "<em>generate</em>",
            "geoAttribute": "<em>geoAttribute</em>",
            "function": "وظيفة",
            "generate": "توليد",
            "graduated": "متدرج",
            "highlightSelected": "حدد العناصر المختارة",
            "intervals": "المراحل",
            "legend": "المفاتيح",
            "name": "<em>name</em>",
            "newThematism": "<em>New Thematism</em>",
            "punctual": "دقيق",
            "quantity": "كمية",
            "source": "المصدر",
            "table": "جدول",
            "thematism": "المواضيع",
            "value": "قيمة"
        },
        "widgets": {
            "customform": {
                "addrow": "أضف صف",
                "clonerow": "انسخ صف",
                "deleterow": "حذف صف",
                "editrow": "تحرير صف",
                "export": "تصدير",
                "import": "استيراد",
                "refresh": "ارجع القيم الافتراضية"
            },
            "linkcards": {
                "editcard": "تحرير البطاقة",
                "opencard": "فتح البطاقة",
                "refreshselection": "إعمال الخيارات الأساسية",
                "togglefilterdisabled": "تعطيل الفرز على لائحة البطاقات",
                "togglefilterenabled": "تمكين الفرز على لائحة البطاقات"
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
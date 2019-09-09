(function() {
    Ext.define('CMDBuildUI.locales.fa.Locales', {
        "requires": ["CMDBuildUI.locales.fa.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "fa",
        "administration": CMDBuildUI.locales.fa.LocalesAdministration.administration,
        "attachments": {
            "add": "اضافه کردن ضمیمه",
            "attachmenthistory": "تاریخچه پیوست",
            "author": "نویسنده",
            "category": "دسته بندی",
            "creationdate": "تاریخ ایجاد",
            "deleteattachment": "پیوست را حذف کنید",
            "deleteattachment_confirmation": "آیا مطمئن هستید که می خواهید این پیوست را حذف کنید؟",
            "description": "توضیحات",
            "download": "دانلود",
            "editattachment": "ویرایش ضمیمه",
            "file": "فایل",
            "filename": "نام فایل",
            "majorversion": "نسخه اصلی",
            "modificationdate": "تاریخ تغییر",
            "uploadfile": "آپلود فایل...",
            "version": "نسخه",
            "viewhistory": "مشاهده تاریخچه پیوست"
        },
        "bim": {
            "bimViewer": "نمایشگر BIM",
            "card": {
                "label": "کارت"
            },
            "layers": {
                "label": "لایه ها",
                "menu": {
                    "hideAll": "همه را پنهان کن",
                    "showAll": "نمایش همه"
                },
                "name": "نام",
                "qt": "عدد",
                "visibility": "دید",
                "visivility": "Visibility"
            },
            "menu": {
                "camera": "دوربین",
                "frontView": "نمای جلویی",
                "mod": "کنترل های مشاهده",
                "orthographic": "دوربین ارتوپدی",
                "pan": "طومار",
                "perspective": "دوربین چشم انداز",
                "resetView": "تنظیم مجدد نما",
                "rotate": "چرخش",
                "sideView": "نمای کنار",
                "topView": "نمای بالا"
            },
            "showBimCard": "بیننده 3Dباز کن",
            "tree": {
                "arrowTooltip": "عنصر را انتخاب کنید",
                "columnLabel": "درخت هدایت",
                "label": "درخت هدایت",
                "open_card": "بازکردن کارتهای مرتبط",
                "root": "ریشه ifc"
            }
        },
        "classes": {
            "cards": {
                "addcard": "اضافه کردن کارت",
                "clone": "مشابه گیری",
                "clonewithrelations": "کارت مشابه و روابط",
                "deletecard": "حذف کارت",
                "deleteconfirmation": "آیا مطمئن هستید که می خواهید این کارت را حذف کنید؟",
                "label": "کارتها",
                "modifycard": "ویرایش کارت",
                "opencard": "کارت را باز کن",
                "print": "چاپ کارت"
            },
            "simple": "ساده",
            "standard": "استاندارد"
        },
        "common": {
            "actions": {
                "add": "اضافه",
                "apply": "اعمال کردن",
                "cancel": "انصراف",
                "close": "بستن",
                "delete": "حذف",
                "edit": "ویرایش",
                "execute": "اجرا کردن",
                "refresh": "تازه کردن داده ها",
                "remove": "حذف",
                "save": "ذخیره",
                "saveandapply": "ذخیره و اعمال کردن",
                "saveandclose": "ذخیره کن و ببند",
                "search": "جستجو کردن",
                "searchtext": "جستجو کردن..."
            },
            "attributes": {
                "nogroup": "داده های پایه"
            },
            "dates": {
                "date": "<em>d/m/Y</em>",
                "datetime": "<em>d/m/Y H:i:s</em>",
                "time": "<em>H:i:s</em>"
            },
            "editor": {
                "clearhtml": "پاک کردن HTML"
            },
            "grid": {
                "disablemultiselection": "چند انتخابی را غیرفعال کنید",
                "enamblemultiselection": "انتخاب چندگانه را فعال کنید",
                "export": "صادرات داده ها",
                "filterremoved": "فیلتر کنونی حذف شده است",
                "import": "وارد کردن داده ها",
                "itemnotfound": "آیتم پیدا نشد",
                "list": "لیست",
                "opencontextualmenu": "منوی متنی را باز کنید",
                "print": "چاپ",
                "printcsv": "چاپ به عنوان CSV",
                "printodt": "چاپ به عنوان ODT",
                "printpdf": "چاپ به عنوان PDF",
                "row": "آیتم",
                "rows": "آیتم ها",
                "subtype": "زیرمجموعه"
            },
            "tabs": {
                "activity": "فعالیت",
                "attachments": "ضمیمه",
                "card": "کارت",
                "details": "جزئیات",
                "emails": "ایمیل ها",
                "history": "تاریخچه",
                "notes": "یاداشتها",
                "relations": "روابط"
            }
        },
        "emails": {
            "addattachmentsfromdms": "اضافه کردن ضمیمه از پایگاه داده",
            "alredyexistfile": "در حال حاضر یک فایل با این نام وجود دارد",
            "archivingdate": "تاریخ آرشیو کردن",
            "attachfile": "ضمیمه کردن فایل",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "نوشتن ایمیل",
            "composefromtemplate": "نوشتن از قالب",
            "delay": "تاخیر انداختن",
            "delays": {
                "day1": "در یک روز",
                "days2": "در دو روز",
                "days4": "در چهار روز",
                "hour1": "یک ساعت",
                "hours2": "دو ساعت",
                "hours4": "چهار ساعت",
                "month1": "در یک ماه",
                "none": "هیچ کدام",
                "week1": "در یک هفته",
                "weeks2": "در دو هفته"
            },
            "dmspaneltitle": "انتخاب ضمیمه از پایگاه داده",
            "edit": "ویرایش",
            "from": "از جانب",
            "gridrefresh": "تازه کردن شبکه",
            "keepsynchronization": "همگام سازی را حفظ کنید",
            "message": "پیام",
            "regenerateallemails": "بازتولید تمام ایمیلها",
            "regenerateemail": "باز تولید ایمیل",
            "remove": "حذف",
            "remove_confirmation": "آیا مطمئن هستید که می خواهید این ایمیل را حذف کنید؟",
            "reply": "پاسخ",
            "replyprefix": "در {0}، {1} نوشت:",
            "selectaclass": "یک کلاس را انتخاب کنید",
            "sendemail": "ارسال ایمیل",
            "statuses": {
                "draft": "پیش نویس",
                "outgoing": "خروجی",
                "received": "رسیده",
                "sent": "ارسالی"
            },
            "subject": "موضوع",
            "to": "به",
            "view": "مشاهده"
        },
        "errors": {
            "autherror": "نام کاربری یا پسورداشتباه است",
            "classnotfound": "کلاس {0} پیدا نشد",
            "notfound": "آیتم پیدا نشد"
        },
        "filters": {
            "actions": "اقدامات",
            "addfilter": "اضافه کردن فیلتر",
            "any": "هر",
            "attribute": "یک خاصیت را انتخاب کنید",
            "attributes": "خواص",
            "clearfilter": "پاک کردن فیلتر",
            "clone": "مشابه گیری",
            "copyof": "کپی از",
            "description": "توضیحات",
            "domain": "دامنه",
            "filterdata": "فیلتر کردن داده ها",
            "fromselection": "از انتخاب",
            "ignore": "چشم پوشی",
            "migrate": "کوچیدن",
            "name": "نام",
            "newfilter": "فیلتر جدید",
            "noone": "هیچ کدام",
            "operator": "اپراتور",
            "operators": {
                "beginswith": "شروع میشود با",
                "between": "مابین",
                "contained": "شامل است",
                "containedorequal": "شامل یا برابر است",
                "contains": "شامل",
                "containsorequal": "شامل یا برابر",
                "different": "تفاوت",
                "doesnotbeginwith": "شروع نشود با",
                "doesnotcontain": "شامل نباشد",
                "doesnotendwith": "تمام نشود با",
                "endswith": "پایان یابد با",
                "equals": "مساوی باشد با",
                "greaterthan": "بزرگتر از",
                "isnotnull": "تهی نیست",
                "isnull": "تهی است",
                "lessthan": "کمتر از"
            },
            "relations": "روابط",
            "type": "نوع",
            "typeinput": "پارامتر ورودی",
            "value": "مقدار"
        },
        "gis": {
            "card": "کارت",
            "cardsMenu": "منو کارت",
            "externalServices": "سرویسهای خارجی",
            "geographicalAttributes": "خواص جئوگرافیک",
            "geoserverLayers": "لایه های Geoserver",
            "layers": "لایه ها",
            "list": "لیست",
            "map": "نقشه",
            "mapServices": "نقشه خدمات",
            "position": "محدوده",
            "root": "ریشه",
            "tree": "درخت هدایت",
            "view": "مشاهده",
            "zoom": "زوم"
        },
        "history": {
            "activityname": "نام فعالیت",
            "activityperformer": "انجام دهنده",
            "begindate": "تاریخ شروع",
            "enddate": "تاریخ اتمام",
            "processstatus": "وضعیت",
            "user": "کاربر"
        },
        "importexport": {
            "downloadreport": "گزارش را دانلود کنید",
            "emailfailure": "هنگام ارسال ایمیل خطایی رخ داد!",
            "emailsubject": "گزارش واردات داده ها",
            "emailsuccess": "ایمیل با موفقیت فرستاده شد!",
            "export": "صادرات",
            "import": "وارد كردن",
            "importresponse": "پاسخ واردات",
            "response": {
                "created": "موارد ایجاد شده",
                "deleted": "موارد حذف شده",
                "errors": "خطاها",
                "linenumber": "شماره خط",
                "message": "پیام",
                "modified": "موارد اصلاح شده",
                "processed": "ردیفهای پردازش شده",
                "recordnumber": "شماره ثبت",
                "unmodified": "آیتم های بدون تغییر"
            },
            "sendreport": "ارسال گزارش",
            "template": "قالب",
            "templatedefinition": "<em>Template definition</em>"
        },
        "login": {
            "buttons": {
                "login": "ورود",
                "logout": "تغییر کاربر"
            },
            "fields": {
                "group": "گروه",
                "language": "زبان",
                "password": "رمز عبور",
                "tenants": "Tennants",
                "username": "نام کاربری"
            },
            "loggedin": "وارد شده",
            "title": "ورود",
            "welcome": "خوش آمدید {0}."
        },
        "main": {
            "administrationmodule": "ماژول ادمین",
            "baseconfiguration": "پیکربندی پایه",
            "cardlock": {
                "lockedmessage": "شما نمی توانید این کارت را ویرایش کنید زیرا {0} آن را ویرایش می کند.",
                "someone": "کسی"
            },
            "changegroup": "تغییر گروه",
            "changepassword": "تغییر رمز عبور",
            "changetenant": "تغییر tenant",
            "confirmchangegroup": "آیا مطمئنید که میخواهید گروه را تغییر دهید؟",
            "confirmchangetenants": "آیا مطمئن هستید که می خواهید «tenants » فعال را تغییر دهید؟",
            "confirmdisabletenant": "آیا مطمئن هستید که میخواهید «tenants flag» را غیرفعال کنید؟",
            "confirmenabletenant": "آیا مطمئن هستید که میخواهید «tenats flag» را فعال کنید؟",
            "confirmpassword": "تایید رمز",
            "ignoretenants": "نادیده گرفتن tenants",
            "info": "اطلاعات",
            "logo": {
                "cmdbuild": "آرم CMDBuild",
                "cmdbuildready2use": "آرم  CMDBuild READY2USE",
                "companylogo": "آرم شرکت",
                "openmaint": "آرم openMAINT"
            },
            "logout": "خروج",
            "managementmodule": "ماژول مدیریت اطلاعات",
            "multigroup": "چند گروهی",
            "multitenant": "چند tenant",
            "navigation": "هدایت",
            "newpassword": "رمز عبور جدید",
            "oldpassword": "رمز عبور قدیمی",
            "pagenotfound": "صفحه یافت نشد",
            "pleasecorrecterrors": "لطفا خطاهای مشخص شده را اصلاح کنید",
            "preferences": {
                "comma": "کاما",
                "decimalserror": "فیلد دهدهی باید حضور داشته باشد",
                "decimalstousandserror": "تفکیک کننده دهها و هزاران باید متفاوت باشد",
                "default": "پیش فرض",
                "defaultvalue": "مقدار پیش فرض",
                "labeldateformat": "فرمت تاریخ",
                "labeldecimalsseparator": "تفکیک کننده دهم",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "زبان",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "هزاران جداساز",
                "labeltimeformat": "فرمت زمان",
                "msoffice": "مایکروسافت آفیس",
                "period": "دوره زمانی",
                "preferredofficesuite": "مجموعه اداری ترجیح داده شده",
                "space": "فضا",
                "thousandserror": "عدد هزاران باید حضور داشته باشند",
                "timezone": "منطقه زمانی",
                "twelvehourformat": "فرمت 12 ساعته",
                "twentyfourhourformat": "فرمت 24 ساعته"
            },
            "searchinallitems": "جستجو در همه آیتم ها",
            "userpreferences": "اولویت ها"
        },
        "menu": {
            "allitems": "همه موارد",
            "classes": "کلاسها",
            "custompages": "صفحات سفارشی",
            "dashboards": "داشبورد",
            "processes": "فرآیندها",
            "reports": "گزارش ها",
            "views": "نمایش"
        },
        "notes": {
            "edit": "ویرایش یاداشت"
        },
        "notifier": {
            "attention": "توجه",
            "error": "خطا",
            "genericerror": "خطای عمومی",
            "genericinfo": "اطلاعات عمومی",
            "genericwarning": "هشدار عمومی",
            "info": "اطلاعات",
            "success": "موفقیت",
            "warning": "هشدار"
        },
        "patches": {
            "apply": "پچ ها را اعمال کنید",
            "category": "دسته بندی",
            "description": "توضیحات",
            "name": "نام",
            "patches": "پچ ها"
        },
        "processes": {
            "abortconfirmation": "آیا مطمئنید که میخواهید این پروسه را نگه دارید؟",
            "abortprocess": "روند را لغو کنید",
            "action": {
                "advance": "جلو",
                "label": "عمل"
            },
            "activeprocesses": "فرآیندهای فعال",
            "allstatuses": "همه",
            "editactivity": "ویرایش فعالیت",
            "openactivity": "فعالیت را باز کنید",
            "startworkflow": "شروع",
            "workflow": "گردش کار"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "کارت",
            "cardList": "لیست کارت",
            "cardRelation": "رابطه",
            "cardRelations": "رابطه",
            "choosenaviagationtree": "شاخه ناوبری را انتخاب کنید",
            "class": "کلاس",
            "class:": "کلاس",
            "classList": "لیست کلاس",
            "compoundnode": "گره مرکب",
            "enableTooltips": "فعال کردن / غیرفعال کردن راهنمایی ها در نمودار",
            "level": "سطح",
            "openRelationGraph": "باز کردن گراف رابطه",
            "qt": "عدد",
            "refresh": "تازه کردن",
            "relation": "رابطه",
            "relationGraph": "رابطه گراف",
            "reopengraph": "باز کردن نمودار از این گره"
        },
        "relations": {
            "adddetail": "اضافه کردن جزئیات",
            "addrelations": "اضافه کردن رابطه",
            "attributes": "خواص",
            "code": "کد",
            "deletedetail": "حذف جزئیات",
            "deleterelation": "حذف رابطه",
            "description": "توضیحات",
            "editcard": "ویرایش کارت",
            "editdetail": "ویرایش جزئیات",
            "editrelation": "ویرایش رابطه",
            "mditems": "آیتم ها",
            "opencard": "بازکردن کارتهای مرتبط",
            "opendetail": "نمایش جزئیات",
            "type": "نوع"
        },
        "reports": {
            "csv": "CSV",
            "download": "دانلود",
            "format": "فرمت",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "چاپ",
            "reload": "بارگیری مجدد",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "<em>Add Thematism</em>",
            "analysisType": "نوع تجزیه و تحلیل",
            "attribute": "صفت",
            "calculateRules": "<em>Generate style rules</em>",
            "clearThematism": "<em>Clear Thematism</em>",
            "color": "رنگ",
            "defineLegend": "<em>Legend definition</em>",
            "defineThematism": "<em>Thematism definition</em>",
            "function": "تابع",
            "generate": "تولید می کنند",
            "geoAttribute": "<em>geoAttribute</em>",
            "graduated": "پله پله",
            "highlightSelected": "برجسته مورد انتخاب شده",
            "intervals": "خلال",
            "legend": "افسانه",
            "name": "<em>name</em>",
            "newThematism": "<em>New Thematism</em>",
            "punctual": "صحیح",
            "quantity": "تعداد",
            "source": "منبع",
            "table": "جدول",
            "thematism": "تاملات",
            "value": "مقدار"
        },
        "widgets": {
            "customform": {
                "addrow": "اضافه کردن سطر",
                "clonerow": "شبیه سازی سطر",
                "deleterow": "حذف سطر",
                "editrow": "ویرایش سطر",
                "export": "صادرات",
                "import": "وارد كردن",
                "refresh": "تازه کردن به پیش فرضها"
            },
            "linkcards": {
                "editcard": "ویرایش کارت",
                "opencard": "کارت را باز کن",
                "refreshselection": "انتخابهای پیشفرض را انجام بده",
                "togglefilterdisabled": "فیلتر شبکه را فعال کنید",
                "togglefilterenabled": "فیلتر شبکه را غیرفعال کنید"
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
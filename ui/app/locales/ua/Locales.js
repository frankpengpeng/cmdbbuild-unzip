(function() {
    Ext.define('CMDBuildUI.locales.ua.Locales', {
        "requires": ["CMDBuildUI.locales.ua.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "ua",
        "administration": CMDBuildUI.locales.ua.LocalesAdministration.administration,
        "attachments": {
            "add": "Додати вкладення",
            "attachmenthistory": "Історія вкладень",
            "author": "Автор",
            "category": "Категорія",
            "creationdate": "Дата створення",
            "deleteattachment": "Видалити вкладення",
            "deleteattachment_confirmation": "Ви впевнені, що хочете видалити це вкладення?",
            "description": "Опис",
            "download": "Завантажити",
            "editattachment": "Змінити вкладення",
            "file": "Файл",
            "filename": "Ім'я файла",
            "majorversion": "Основна версія",
            "modificationdate": "Дата зміни",
            "uploadfile": "Завантажити файл...",
            "version": "Версія",
            "viewhistory": "Перегляд історії вкладень"
        },
        "bim": {
            "bimViewer": "Переглядач Bim",
            "card": {
                "label": "Картка"
            },
            "layers": {
                "label": "Шари",
                "menu": {
                    "hideAll": "Приховати все",
                    "showAll": "Показати все"
                },
                "name": "Назва",
                "qt": "Qt",
                "visibility": "Видимість",
                "visivility": "Видимість"
            },
            "menu": {
                "camera": "Камера",
                "frontView": "Вид спереду",
                "mod": "елементи керування перегляду",
                "orthographic": "<em>Orthographic Camera</em>",
                "pan": "панорамування",
                "perspective": "<em>Perspective Camera</em>",
                "resetView": "Відновити вид",
                "rotate": "повернути",
                "sideView": "Вид збоку",
                "topView": "Вид зверху"
            },
            "showBimCard": "Відкрити 3D-переглядач",
            "tree": {
                "arrowTooltip": "Вибрати елемент",
                "columnLabel": "Дерево",
                "label": "Дерево",
                "open_card": "Відкрити пов'язану картку",
                "root": "<em>Ifc Root</em>"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Додати картку",
                "clone": "Клонувати",
                "clonewithrelations": "Клонувати картку та зв'язки",
                "deletecard": "Видалити картку",
                "deleteconfirmation": "Ви впевнені, що хочете видалити цю карту?",
                "label": "Картки",
                "modifycard": "Змінити картку",
                "opencard": "Відкрити картку",
                "print": "Друкувати картку"
            },
            "simple": "Простий",
            "standard": "Стандартний"
        },
        "common": {
            "actions": {
                "add": "Додати",
                "apply": "Застосувати",
                "cancel": "Скасувати",
                "close": "Закрити",
                "delete": "Видалити",
                "edit": "Редагувати",
                "execute": "Виконати",
                "refresh": "Оновити дані",
                "remove": "Видалити",
                "save": "Зберегти",
                "saveandapply": "Зберегти і застосувати",
                "saveandclose": "Зберегти та закрити",
                "search": "Пошук",
                "searchtext": "Пошук…"
            },
            "attributes": {
                "nogroup": "Вихідні дані"
            },
            "dates": {
                "date": "д/м/р",
                "datetime": "д/м/р г:х:с",
                "time": "г:х:с"
            },
            "editor": {
                "clearhtml": "Очистити HTML"
            },
            "grid": {
                "disablemultiselection": "Вимкнути множинний вибір",
                "enamblemultiselection": "Увімкнути множинний вибір",
                "export": "<em>Export data</em>",
                "filterremoved": "Поточний фільтр був видалений",
                "import": "<em>Import data</em>",
                "itemnotfound": "Елемент не знайдено",
                "list": "Список",
                "opencontextualmenu": "Відкрити контекстне меню",
                "print": "Друк",
                "printcsv": "Друкувати як CSV",
                "printodt": "Друкувати як ODT",
                "printpdf": "Друкувати як PDF",
                "row": "Елемент",
                "rows": "Елементи",
                "subtype": "Підтип"
            },
            "tabs": {
                "activity": "Активність",
                "attachments": "Вкладення",
                "card": "Картка",
                "details": "Подробиці",
                "emails": "Електронні листи",
                "history": "Історія",
                "notes": "Замітки",
                "relations": "Зв'язки"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Додати вкладення з DMS",
            "alredyexistfile": "<em>Already exists a file with this name</em>",
            "archivingdate": "Дата архівації",
            "attachfile": "Прикріпити файл",
            "bcc": "Прихована копія",
            "cc": "Копія",
            "composeemail": "Створити електронний лист",
            "composefromtemplate": "Створити з шаблону",
            "delay": "Затримка",
            "delays": {
                "day1": "За 1 день",
                "days2": "За 2 дні",
                "days4": "За 4 дні",
                "hour1": "1 година",
                "hours2": "2 години",
                "hours4": "4 години",
                "month1": "За 1 місяць",
                "none": "Ніяка",
                "week1": "За 1 тиждень",
                "weeks2": "За 2 тижні"
            },
            "dmspaneltitle": "Обрати вкладення з бази даних",
            "edit": "Редагувати",
            "from": "Від",
            "gridrefresh": "Оновити таблицю",
            "keepsynchronization": "Постійно синхронізувати",
            "message": "Повідомлення",
            "regenerateallemails": "Відновити всі електронні адреси",
            "regenerateemail": "Відновити електронну адресу",
            "remove": "Видалити",
            "remove_confirmation": "Точно видалити цей електронний лист?",
            "reply": "Відповісти",
            "replyprefix": "{0} {1} писав:",
            "selectaclass": "<em>Select a class</em>",
            "sendemail": "Надіслати електронного листа",
            "statuses": {
                "draft": "Чернетки",
                "outgoing": "Вихідні",
                "received": "Отримані",
                "sent": "Відправлені"
            },
            "subject": "Тема",
            "to": "До",
            "view": "Переглянути"
        },
        "errors": {
            "autherror": "Невірне ім'я користувача чи пароль",
            "classnotfound": "Клас {0} не знайдено",
            "notfound": "Елемент не знайдено"
        },
        "filters": {
            "actions": "Дії",
            "addfilter": "Додати фільтр",
            "any": "Будь-який",
            "attribute": "Вибрати атрибут",
            "attributes": "Атрибути",
            "clearfilter": "Очистити фільтр",
            "clone": "Клонувати",
            "copyof": "Копія з",
            "description": "Опис",
            "domain": "Домен",
            "filterdata": "Фільтрувати дані",
            "fromselection": "Із виділенного",
            "ignore": "Ігнорувати",
            "migrate": "Мігрує",
            "name": "Ім'я",
            "newfilter": "Новый фильтр",
            "noone": "Жодного",
            "operator": "Оператор",
            "operators": {
                "beginswith": "Починається з",
                "between": "Між",
                "contained": "Міститься",
                "containedorequal": "Міститься або рівний",
                "contains": "Містить",
                "containsorequal": "Містить або рівні",
                "different": "Різний",
                "doesnotbeginwith": "Не починається з",
                "doesnotcontain": "Не містить",
                "doesnotendwith": "Не закінчується на",
                "endswith": "Закінчується на",
                "equals": "Рівний",
                "greaterthan": "Більше ніж",
                "isnotnull": "Не пусте",
                "isnull": "Пусте",
                "lessthan": "Менше ніж"
            },
            "relations": "Зв'язки",
            "type": "Тип",
            "typeinput": "Вхідний параметр",
            "value": "Значення"
        },
        "gis": {
            "card": "Картка",
            "cardsMenu": "<em>Cards Menu</em>",
            "externalServices": "Зовнішні сервіси",
            "geographicalAttributes": "Географічні атрибути",
            "geoserverLayers": "Географічні шари",
            "layers": "Шари",
            "list": "Список",
            "map": "Карта",
            "mapServices": "Картографічні сервіси",
            "position": "Позиція",
            "root": "Корінь",
            "tree": "Дерево",
            "view": "Вид",
            "zoom": "Масштаб"
        },
        "history": {
            "activityname": "Назва активності",
            "activityperformer": "Виконавець активності",
            "begindate": "Дата початку",
            "enddate": "Дата закінчення",
            "processstatus": "Статус",
            "user": "Користувач"
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
                "login": "Увійти",
                "logout": "Змінити користувача"
            },
            "fields": {
                "group": "Група",
                "language": "Мова",
                "password": "Пароль",
                "tenants": "Орендарі",
                "username": "Ім'я користувача"
            },
            "loggedin": "Авторизовано",
            "title": "Вхід",
            "welcome": "З поверненням, {0}."
        },
        "main": {
            "administrationmodule": "Модуль адміністрування",
            "baseconfiguration": "<em>Base configuration</em>",
            "cardlock": {
                "lockedmessage": "Ви не можете редагувати цю картку, оскільки {0} її редагує.",
                "someone": "хтось"
            },
            "changegroup": "Змінити групу",
            "changepassword": "Змінити пароль",
            "changetenant": "Змінити орендаря",
            "confirmchangegroup": "Ви точно хочете змінити групу?",
            "confirmchangetenants": "Ви точно хочете змінити активних орендарів?",
            "confirmdisabletenant": "Ви точно хочете вимкнути прапорець \"Ігнорувати орендарів\"?",
            "confirmenabletenant": "Ви точно хочете ввімкнути прапорець \"Ігнорувати орендарів\"?",
            "confirmpassword": "Підтвердити пароль",
            "ignoretenants": "Ігнорувати орендарів",
            "info": "Інформація",
            "logo": {
                "cmdbuild": "Логотип CMDBuild",
                "cmdbuildready2use": "Логотип CMDBuild READY2USE",
                "companylogo": "<em>Company logo</em>",
                "openmaint": "Логотип openMAINT"
            },
            "logout": "Вийти",
            "managementmodule": "Модуль керування даними",
            "multigroup": "Множинна група",
            "multitenant": "Множинна оренда",
            "navigation": "Навігація",
            "newpassword": "Новий пароль",
            "oldpassword": "Старий пароль",
            "pagenotfound": "<em>Page not found</em>",
            "pleasecorrecterrors": "<em>Please correct indicated errors!</em>",
            "preferences": {
                "comma": "Кома",
                "decimalserror": "Поле десяткових знаків має бути присутнім",
                "decimalstousandserror": "Роздільники груп розрядів і десяткового дробу мають відрізнятися",
                "default": "<em>Default</em>",
                "defaultvalue": "Значення за замовчанням",
                "labeldateformat": "Формат дати",
                "labeldecimalsseparator": "Роздільник десяткового дробу",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "Мова",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "Роздільник груп розрядів",
                "labeltimeformat": "Формат часу",
                "msoffice": "<em>Microsoft Office</em>",
                "period": "Крапка",
                "preferredofficesuite": "<em>Preferred Office suite</em>",
                "space": "Пробіл",
                "thousandserror": "Поле тисячних знаків має бути присутнім",
                "timezone": "<em>Timezone</em>",
                "twelvehourformat": "12-годинний формат",
                "twentyfourhourformat": "24-годинний формат"
            },
            "searchinallitems": "Пошук в усіх елементах",
            "userpreferences": "Налаштування"
        },
        "menu": {
            "allitems": "Всі елементи",
            "classes": "Класи",
            "custompages": "Спеціальні сторінки",
            "dashboards": "Інформаційні панелі",
            "processes": "Процеси",
            "reports": "Звіти",
            "views": "Вигляди"
        },
        "notes": {
            "edit": "Змінити замітку"
        },
        "notifier": {
            "attention": "Увага",
            "error": "Помилка",
            "genericerror": "Загальна помилка",
            "genericinfo": "Загальне повідомлення",
            "genericwarning": "Загальне попередження",
            "info": "Повідомлення",
            "success": "Успіх",
            "warning": "Попередження"
        },
        "patches": {
            "apply": "<em>Apply patches</em>",
            "category": "<em>Category</em>",
            "description": "<em>Description</em>",
            "name": "<em>Name</em>",
            "patches": "<em>Patches</em>"
        },
        "processes": {
            "abortconfirmation": "Ви впевнені, що хочете скасувати цей процес?",
            "abortprocess": "Скасувати процес",
            "action": {
                "advance": "Просування",
                "label": "Дія"
            },
            "activeprocesses": "Активні процеси",
            "allstatuses": "Всі",
            "editactivity": "Змінити активність",
            "openactivity": "Відкрити активність",
            "startworkflow": "Запустити",
            "workflow": "Робочий процес"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Картка",
            "cardList": "Список карток",
            "cardRelation": "Зв'язок",
            "cardRelations": "Відношення картки",
            "choosenaviagationtree": "Обрати навігаційне дерево",
            "class": "Клас",
            "class:": "Клас",
            "classList": "Список класів",
            "compoundnode": "<em>Compound Node</em>",
            "enableTooltips": "Увімкнути/вимкнути спливаючі підказки на графі",
            "level": "Рівень",
            "openRelationGraph": "Відкрити граф відношень",
            "qt": "Qt",
            "refresh": "Оновити",
            "relation": "відношення",
            "relationGraph": "Граф відношень",
            "reopengraph": "Перевідкрити графік із цього вузла"
        },
        "relations": {
            "adddetail": "Додати детальне",
            "addrelations": "Додати відношення",
            "attributes": "Атрибути",
            "code": "Код",
            "deletedetail": "Видалити детальне",
            "deleterelation": "Видалити відношення",
            "description": "Опис",
            "editcard": "Редагувати картку",
            "editdetail": "Редагувати детальне",
            "editrelation": "Редагувати відношення",
            "mditems": "елементи",
            "opencard": "Відкрити пов'язану картку",
            "opendetail": "Показати детальне",
            "type": "Тип"
        },
        "reports": {
            "csv": "CSV",
            "download": "Завантажити",
            "format": "Формат",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Друк",
            "reload": "Перезавантажити",
            "rtf": "RTF"
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
                "addrow": "Додати рядок",
                "clonerow": "Клонувати рядок",
                "deleterow": "Видалити рядок",
                "editrow": "Редагувати рядок",
                "export": "Експортувати",
                "import": "Імпортувати",
                "refresh": "Оновити до налаштувань за замовчанням"
            },
            "linkcards": {
                "editcard": "Змінити картку",
                "opencard": "Відкрити картку",
                "refreshselection": "Застосувати вибір за замовчуванням",
                "togglefilterdisabled": "Вимкнути фільтр в таблиці",
                "togglefilterenabled": "Увімкнути фільтр в таблиці"
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
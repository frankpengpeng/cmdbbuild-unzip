(function() {
    Ext.define('CMDBuildUI.locales.ru.Locales', {
        "requires": ["CMDBuildUI.locales.ru.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "ru",
        "administration": CMDBuildUI.locales.ru.LocalesAdministration.administration,
        "attachments": {
            "add": "Вложить файл",
            "attachmenthistory": "<em>Attachment History</em>",
            "author": "Автор",
            "category": "Категория",
            "creationdate": "<em>Creation date</em>",
            "deleteattachment": "<em>Delete attachment</em>",
            "deleteattachment_confirmation": "<em>Are you sure you want to delete this attachment?</em>",
            "description": "Описание",
            "download": "Скачать",
            "editattachment": "Изменить вложение",
            "file": "File",
            "filename": "Имя файла",
            "majorversion": "Основная версия",
            "modificationdate": "Дата изменения",
            "uploadfile": "<em>Upload file...</em>",
            "version": "Версия",
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
                    "hideAll": "Скрыть все",
                    "showAll": "Показать все"
                },
                "name": "<em>Name</em>",
                "qt": "<em>Qt</em>",
                "visibility": "<em>Visibility</em>",
                "visivility": "Видимость"
            },
            "menu": {
                "camera": "Камера",
                "frontView": "<em>Front View</em>",
                "mod": "<em>Viewer controls</em>",
                "orthographic": "<em>Orthographic Camera</em>",
                "pan": "Переместить",
                "perspective": "<em>Perspective Camera</em>",
                "resetView": "<em>Reset View</em>",
                "rotate": "Повернуть",
                "sideView": "<em>Side View</em>",
                "topView": "<em>Top View</em>"
            },
            "showBimCard": "<em>Open 3D viewer</em>",
            "tree": {
                "arrowTooltip": "<em>Select element</em>",
                "columnLabel": "<em>Tree</em>",
                "label": "<em>Tree</em>",
                "open_card": "Открыть связанную запись",
                "root": "<em>Ifc Root</em>"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Добавить запись",
                "clone": "Дублировать",
                "clonewithrelations": "<em>Clone card and relations</em>",
                "deletecard": "Удалить запись",
                "deleteconfirmation": "<em>Are you sure you want to delete this card?</em>",
                "label": "Конфигурационные единицы",
                "modifycard": "Редактировать запись",
                "opencard": "<em>Open card</em>",
                "print": "<em>Print card</em>"
            },
            "simple": "Простой",
            "standard": "Стандартный"
        },
        "common": {
            "actions": {
                "add": "Добавить",
                "apply": "Применить",
                "cancel": "Отменить",
                "close": "Закрыть",
                "delete": "Удаление",
                "edit": "Редактировать",
                "execute": "<em>Execute</em>",
                "refresh": "<em>Refresh data</em>",
                "remove": "Удалить",
                "save": "Сохранить",
                "saveandapply": "Сохранить и применить",
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
                "itemnotfound": "Элемент не найден",
                "list": "<em>List</em>",
                "opencontextualmenu": "<em>Open contextual menu</em>",
                "print": "Распечатать",
                "printcsv": "<em>Print as CSV</em>",
                "printodt": "<em>Print as ODT</em>",
                "printpdf": "<em>Print as PDF</em>",
                "row": "<em>Item</em>",
                "rows": "<em>Items</em>",
                "subtype": "<em>Subtype</em>"
            },
            "tabs": {
                "activity": "Процесс",
                "attachments": "Вложенные файлы",
                "card": "Данные",
                "details": "Подробно",
                "emails": "<em>Emails</em>",
                "history": "История",
                "notes": "Комментарии",
                "relations": "Связи"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Вложить файлы из DMS",
            "alredyexistfile": "<em>Already exists a file with this name</em>",
            "archivingdate": "Дата",
            "attachfile": "Вложить файл",
            "bcc": "Скрытая копия",
            "cc": "Копия",
            "composeemail": "Составление сообщения",
            "composefromtemplate": "Составить из шаблона",
            "delay": "Задержка",
            "delays": {
                "day1": "Через 1 день",
                "days2": "Через 2 дня",
                "days4": "Через 4 дня",
                "hour1": "1 час",
                "hours2": "2 часа",
                "hours4": "4 часа",
                "month1": "Через 1 месяц",
                "none": "нисколько",
                "week1": "Через 1 неделю",
                "weeks2": "Через 2 недели"
            },
            "dmspaneltitle": "Выберите вложение из базы данных",
            "edit": "Редактировать",
            "from": "От",
            "gridrefresh": "Обновление сетки",
            "keepsynchronization": "Сохранять синхронизацию",
            "message": "Сообщение",
            "regenerateallemails": "Восстановить все электронные письма",
            "regenerateemail": "Повторить генерацию почтовых сообщений",
            "remove": "Удалить",
            "remove_confirmation": "<em>Are you sure you want to delete this email?</em>",
            "reply": "Ответить",
            "replyprefix": "<em>On {0}, {1} wrote:</em>",
            "selectaclass": "<em>Select a class</em>",
            "sendemail": "Отправить электронное письмо",
            "statuses": {
                "draft": "Черновики",
                "outgoing": "Исходящие",
                "received": "Входящие",
                "sent": "Отправленные"
            },
            "subject": "Тема",
            "to": "До",
            "view": "Представление данных"
        },
        "errors": {
            "autherror": "Неверное имя пользователя или пароль",
            "classnotfound": "Класс {0} не найден",
            "notfound": "Элемент не найден"
        },
        "filters": {
            "actions": "<em>Actions</em>",
            "addfilter": "Создать фильтр",
            "any": "Любой",
            "attribute": "Выберите атрибут",
            "attributes": "Атрибуты",
            "clearfilter": "Сбросить фильтр",
            "clone": "Дублировать",
            "copyof": "Копия",
            "description": "Описание",
            "domain": "Связь",
            "filterdata": "<em>Filter data</em>",
            "fromselection": "Из отбора",
            "ignore": "<em>Ignore</em>",
            "migrate": "<em>Migrate</em>",
            "name": "Имя",
            "newfilter": "<em>New filter</em>",
            "noone": "Ни один из",
            "operator": "<em>Operator</em>",
            "operators": {
                "beginswith": "начинается с",
                "between": "между",
                "contained": "Содержится",
                "containedorequal": "Содержится или равен",
                "contains": "содержит",
                "containsorequal": "Содержится или равен",
                "different": "не равно",
                "doesnotbeginwith": "не начинается с",
                "doesnotcontain": "Não contém",
                "doesnotendwith": "не заканчивается на",
                "endswith": "заканчиватся на",
                "equals": "равно",
                "greaterthan": "больше чем",
                "isnotnull": "не null",
                "isnull": "пустое",
                "lessthan": "меньше чем"
            },
            "relations": "Связи",
            "type": "Тип",
            "typeinput": "Переменный параметр",
            "value": "Значение"
        },
        "gis": {
            "card": "Данные",
            "cardsMenu": "<em>Cards Menu</em>",
            "externalServices": "Внешние сервисы",
            "geographicalAttributes": "Гео. атрибуты",
            "geoserverLayers": "Слои Geoserver",
            "layers": "Слои",
            "list": "Список",
            "map": "Карта",
            "mapServices": "<em>Map Services</em>",
            "position": "Положение",
            "root": "Корень",
            "tree": "Навигационное дерево",
            "view": "Представление данных",
            "zoom": "Масштабировать"
        },
        "history": {
            "activityname": "Действие",
            "activityperformer": "Исполнитель",
            "begindate": "Начальная дата",
            "enddate": "Конечная дата",
            "processstatus": "Состояние",
            "user": "Пользователь"
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
                "login": "Вход в систему",
                "logout": "<em>Change user</em>"
            },
            "fields": {
                "group": "<em>Group</em>",
                "language": "Язык",
                "password": "Пароль",
                "tenants": "<em>Tenants</em>",
                "username": "Имя пользователя"
            },
            "loggedin": "<em>Logged in</em>",
            "title": "Вход в систему",
            "welcome": "<em>Welcome back {0}.</em>"
        },
        "main": {
            "administrationmodule": "Администрирование",
            "baseconfiguration": "<em>Base configuration</em>",
            "cardlock": {
                "lockedmessage": "<em>You can't edit this card because {0} is editing it.</em>",
                "someone": "<em>someone</em>"
            },
            "changegroup": "<em>Change group</em>",
            "changepassword": "Изменить пароль",
            "changetenant": "<em>Change tenant</em>",
            "confirmchangegroup": "<em>Are you sure you want to change the group?</em>",
            "confirmchangetenants": "<em>Are you sure you want to change active tenants?</em>",
            "confirmdisabletenant": "<em>Are you sure you want to disable \"Ignore tenants\" flag?</em>",
            "confirmenabletenant": "<em>Are you sure you want to enable \"Ignore tenants\" flag?</em>",
            "confirmpassword": "Подтверждение пароля",
            "ignoretenants": "<em>Ignore tenants</em>",
            "info": "Информация",
            "logo": {
                "cmdbuild": "<em>CMDBuild logo</em>",
                "cmdbuildready2use": "<em>CMDBuild READY2USE logo</em>",
                "companylogo": "<em>Company logo</em>",
                "openmaint": "<em>openMAINT logo</em>"
            },
            "logout": "Выход",
            "managementmodule": "Управление данными",
            "multigroup": "Несколько групп",
            "multitenant": "<em>Multi tenant</em>",
            "navigation": "Навигация",
            "newpassword": "Новый пароль",
            "oldpassword": "Старый пароль",
            "pagenotfound": "<em>Page not found</em>",
            "pleasecorrecterrors": "<em>Please correct indicated errors!</em>",
            "preferences": {
                "comma": "<em>Comma</em>",
                "decimalserror": "<em>Decimals field must be present</em>",
                "decimalstousandserror": "<em>Decimals and Thousands separato must be differents</em>",
                "default": "<em>Default</em>",
                "defaultvalue": "Значение по умолчанию",
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
            "classes": "Классы",
            "custompages": "Пользовательские страницы",
            "dashboards": "<em>Dashboards</em>",
            "processes": "Рабочие процессы",
            "reports": "<em>Reports</em>",
            "views": "Представления"
        },
        "notes": {
            "edit": "Редактировать комментарий"
        },
        "notifier": {
            "attention": "Внимание",
            "error": "Ошибка",
            "genericerror": "<em>Generic error</em>",
            "genericinfo": "<em>Generic info</em>",
            "genericwarning": "<em>Generic warning</em>",
            "info": "Информация",
            "success": "Успешно выполнено",
            "warning": "Предупреждение"
        },
        "patches": {
            "apply": "<em>Apply patches</em>",
            "category": "<em>Category</em>",
            "description": "<em>Description</em>",
            "name": "<em>Name</em>",
            "patches": "<em>Patches</em>"
        },
        "processes": {
            "abortconfirmation": "Действительно удалить процесс?",
            "abortprocess": "Прервать процесс",
            "action": {
                "advance": "Продолжить",
                "label": "<em>Action</em>"
            },
            "activeprocesses": "<em>Active processes</em>",
            "allstatuses": "Все",
            "editactivity": "Редактировать процесс",
            "openactivity": "<em>Open activity</em>",
            "startworkflow": "Начало",
            "workflow": "Процесс"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Данные",
            "cardList": "<em>Card List</em>",
            "cardRelation": "Связь",
            "cardRelations": "Связь",
            "choosenaviagationtree": "<em>Choose navigation tree</em>",
            "class": "<em>Class</em>",
            "class:": "Класс",
            "classList": "<em>Class List</em>",
            "compoundnode": "<em>Compound Node</em>",
            "enableTooltips": "<em>Enable/disable tooltips on graph</em>",
            "level": "<em>Level</em>",
            "openRelationGraph": "Показать диаграмму связей",
            "qt": "<em>Qt</em>",
            "refresh": "<em>Refresh</em>",
            "relation": "Связь",
            "relationGraph": "Отображение связей",
            "reopengraph": "<em>Reopen the graph from this node</em>"
        },
        "relations": {
            "adddetail": "Добавить элемент",
            "addrelations": "Добавить связь",
            "attributes": "Атрибуты",
            "code": "Код",
            "deletedetail": "Удалить элемент",
            "deleterelation": "Удалить связь",
            "description": "Описание",
            "editcard": "Редактировать запись",
            "editdetail": "Редактировать элемент",
            "editrelation": "Редактировать связь",
            "mditems": "<em>items</em>",
            "opencard": "Открыть связанную запись",
            "opendetail": "Показать элемент",
            "type": "Тип"
        },
        "reports": {
            "csv": "<em>CSV</em>",
            "download": "Скачать",
            "format": "Формат",
            "odt": "<em>ODT</em>",
            "pdf": "<em>PDF</em>",
            "print": "Распечатать",
            "reload": "Обновить",
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
                "addrow": "Добавить строку",
                "clonerow": "Строка клонов",
                "deleterow": "Удалить строку",
                "editrow": "Редактировать строку",
                "export": "Экспорт",
                "import": "Импортировать",
                "refresh": "<em>Refresh to defaults</em>"
            },
            "linkcards": {
                "editcard": "<em>Edit card</em>",
                "opencard": "<em>Open card</em>",
                "refreshselection": "Применить выбор по умолчанию",
                "togglefilterdisabled": "Отключить фильтр сетки",
                "togglefilterenabled": "Включить фильтр сетки"
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
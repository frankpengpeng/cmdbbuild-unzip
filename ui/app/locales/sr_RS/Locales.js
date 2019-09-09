(function() {
    Ext.define('CMDBuildUI.locales.sr_RS.Locales', {
        "requires": ["CMDBuildUI.locales.sr_RS.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "sr_RS",
        "administration": CMDBuildUI.locales.sr_RS.LocalesAdministration.administration,
        "attachments": {
            "add": "Додај прилог",
            "attachmenthistory": "Историја прилога",
            "author": "Аутор",
            "category": "Категорија",
            "creationdate": "Датум креирања",
            "deleteattachment": "Обриши прилог",
            "deleteattachment_confirmation": "Заиста желите да уклоните прилог?",
            "description": "Опис",
            "download": "Преузми",
            "editattachment": "Модификуј прилог",
            "file": "Датотека",
            "filename": "Назив датотеке",
            "majorversion": "Главна верзија",
            "modificationdate": "Датум измене",
            "uploadfile": "Пошаљи датотеку…",
            "version": "Верзија",
            "viewhistory": "Прикажи историју прилога"
        },
        "bim": {
            "bimViewer": "Bim приказ",
            "card": {
                "label": "Картица"
            },
            "layers": {
                "label": "Слојеви",
                "menu": {
                    "hideAll": "Сакриј све",
                    "showAll": "Прикажи све"
                },
                "name": "Назив",
                "qt": "Qt",
                "visibility": "Видљивост",
                "visivility": "Видљивост"
            },
            "menu": {
                "camera": "Камера",
                "frontView": "Приказ спреда",
                "mod": "Контроле приказа",
                "orthographic": "Ортографска камера",
                "pan": "Померање",
                "perspective": "Перспективна камера",
                "resetView": "Ресетуј приказ",
                "rotate": "Ротирај",
                "sideView": "Приказ са стране",
                "topView": "Приказ од горе"
            },
            "showBimCard": "Отвори 3Д приказ",
            "tree": {
                "arrowTooltip": "Изабери елемент",
                "columnLabel": "Стабло",
                "label": "Стабло",
                "open_card": "Отвори припадајућу картицу",
                "root": "Ifc корен"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Додај картицу",
                "clone": "Клонирај",
                "clonewithrelations": "Клонирај картицу и релације",
                "deletecard": "Уклони картицу",
                "deleteconfirmation": "Заиста желите да обришете картицу?",
                "label": "Картице података",
                "modifycard": "Измени картицу",
                "opencard": "Отвори картицу",
                "print": "Штампај картицу"
            },
            "simple": "Једноставна",
            "standard": "Стандардна"
        },
        "common": {
            "actions": {
                "add": "Додај",
                "apply": "Примени",
                "cancel": "Одустани",
                "close": "Затвори",
                "delete": "Уклони",
                "edit": "Измени",
                "execute": "Изврши",
                "refresh": "Освежи податке",
                "remove": "Уклони",
                "save": "Сними",
                "saveandapply": "Сними и примени",
                "saveandclose": "Сними и затвори",
                "search": "Претрага",
                "searchtext": "Претрага…"
            },
            "attributes": {
                "nogroup": "Основни подаци"
            },
            "dates": {
                "date": "d/m/Y",
                "datetime": "d/m/Y H:i:s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "Чист ХТМЛ"
            },
            "grid": {
                "disablemultiselection": "Онемугићи вишеструко селектовање",
                "enamblemultiselection": "Омогући вишеструко селектовање",
                "export": "Извези податке",
                "filterremoved": "Тренутни филтер је уклоњен",
                "import": "Увези податке",
                "itemnotfound": "Елемент није пронађен",
                "list": "Листа",
                "opencontextualmenu": "Отвори контекстни мени",
                "print": "Штампај",
                "printcsv": "Штампај као CSV",
                "printodt": "Штампај као ODT",
                "printpdf": "Штампај као PDF",
                "row": "Ставка",
                "rows": "Ставке",
                "subtype": "Подтип"
            },
            "tabs": {
                "activity": "Активност",
                "attachments": "Прилози",
                "card": "Картица",
                "details": "Детаљи",
                "emails": "Е-маилови",
                "history": "Историја",
                "notes": "Напомене",
                "relations": "Релације"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Додај прилог из DMS",
            "alredyexistfile": "Датотека с овим именом већ постоји",
            "archivingdate": "Датум архивирања",
            "attachfile": "Приложи датотеку",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "Креирај е-пошту",
            "composefromtemplate": "Креирај из обрасца",
            "delay": "Одлагање",
            "delays": {
                "day1": "За 1 дан",
                "days2": "За 2 дана",
                "days4": "За 4 дана",
                "hour1": "1 сат",
                "hours2": "2 сата",
                "hours4": "4 сата",
                "month1": "За 1 месец",
                "none": "Без",
                "week1": "За 1 недељу",
                "weeks2": "За 2 недељу"
            },
            "dmspaneltitle": "Изабери прилог из базе података",
            "edit": "Измени",
            "from": "Од",
            "gridrefresh": "Освежи мрежу",
            "keepsynchronization": "Одржавај синхронизовано",
            "message": "Порука",
            "regenerateallemails": "Генериши све емаилове поново",
            "regenerateemail": "Изнова генериши е-пошту",
            "remove": "Уклони",
            "remove_confirmation": "Заиста желите да уклоните овај е-маил?",
            "reply": "Одговори",
            "replyprefix": "{0}, {1} је написао",
            "selectaclass": "Изабери класу",
            "sendemail": "Пошаљи е-пошту",
            "statuses": {
                "draft": "Започете",
                "outgoing": "За слање",
                "received": "Примљене",
                "sent": "Послане"
            },
            "subject": "Субјекат",
            "to": "За",
            "view": "Приказ"
        },
        "errors": {
            "autherror": "Погрешно корисничко име и/или лозинка",
            "classnotfound": "Класа {0} не постоји",
            "notfound": "Елемент није пронађен"
        },
        "filters": {
            "actions": "Акције",
            "addfilter": "Додај филтер",
            "any": "Било који",
            "attribute": "Изабери атрибут",
            "attributes": "Атрибути",
            "clearfilter": "Очисти филтер",
            "clone": "Клонирај",
            "copyof": "Копија",
            "description": "Опис",
            "domain": "Релација",
            "filterdata": "Филтрирај податке",
            "fromselection": "Из селекције",
            "ignore": "Игнориши",
            "migrate": "Премешта",
            "name": "Назив",
            "newfilter": "Нови филтер",
            "noone": "Ниједан",
            "operator": "Оператор",
            "operators": {
                "beginswith": "Који почињу са",
                "between": "Између",
                "contained": "Садржан",
                "containedorequal": "Садржан или једнак",
                "contains": "Садржи",
                "containsorequal": "Садржи или је једнак",
                "different": "Различите од",
                "doesnotbeginwith": "Који не почињу са",
                "doesnotcontain": "Који не садрже",
                "doesnotendwith": "Не завршава са",
                "endswith": "Завршава са",
                "equals": "Једнаке",
                "greaterthan": "Веће",
                "isnotnull": "Не може бити null",
                "isnull": "Са null вредношћу",
                "lessthan": "Мање"
            },
            "relations": "Релације",
            "type": "Тип",
            "typeinput": "Улазни параметар",
            "value": "Вредности"
        },
        "gis": {
            "card": "Картица",
            "cardsMenu": "Мени картица",
            "externalServices": "Спољни сервиси",
            "geographicalAttributes": "Географски атрибути",
            "geoserverLayers": "Geoserver слојеви",
            "layers": "Слојеви",
            "list": "Листа",
            "map": "Мапа",
            "mapServices": "Сервиси мапа",
            "position": "Позиција",
            "root": "Корен",
            "tree": "Стабло навигације",
            "view": "Приказ",
            "zoom": "Увећање"
        },
        "history": {
            "activityname": "Назив активности",
            "activityperformer": "Извршилац активности",
            "begindate": "Датум почетка",
            "enddate": "Датум завршетка",
            "processstatus": "Статус",
            "user": "Корисник"
        },
        "importexport": {
            "downloadreport": "Преузми извештај",
            "emailfailure": "Грешка приликом слања е-маила",
            "emailsubject": "Увези извештај с подацима",
            "emailsuccess": "Е-маил је успешно послан",
            "export": "Извези",
            "import": "Увези",
            "importresponse": "Увези одговор",
            "response": {
                "created": "Креиране ставке",
                "deleted": "Обрисане ставке",
                "errors": "Грешке",
                "linenumber": "Број линије",
                "message": "Порука",
                "modified": "Измењене ставке",
                "processed": "Обрађени редови",
                "recordnumber": "Број записа",
                "unmodified": "Неизмењене ставке"
            },
            "sendreport": "Пошаљи извештај",
            "template": "Шаблон",
            "templatedefinition": "<em>Template definition</em>"
        },
        "login": {
            "buttons": {
                "login": "Пријави се",
                "logout": "Промени корисника"
            },
            "fields": {
                "group": "Група",
                "language": "Језик",
                "password": "Лозинка",
                "tenants": "Клијенти",
                "username": "Корисничко име"
            },
            "loggedin": "Пријављен",
            "title": "Пријави се",
            "welcome": "Добро дошли назад, {0}"
        },
        "main": {
            "administrationmodule": "Администрациони модул",
            "baseconfiguration": "Основна конфигурација",
            "cardlock": {
                "lockedmessage": "Не можете мењати ову картицу јер је тренутно мења {0}",
                "someone": "неко"
            },
            "changegroup": "Промени групу",
            "changepassword": "Промени лозинку",
            "changetenant": "Промени клијента",
            "confirmchangegroup": "Заиста желите да промените групу?",
            "confirmchangetenants": "Заиста желите да промените активног клијента?",
            "confirmdisabletenant": "Заиста желите да искључите ознаку ”Игнориши клијенте”?",
            "confirmenabletenant": "Заиста желите да укључите ознаку ”Игнориши клијенте”?",
            "confirmpassword": "Потврди лозинку",
            "ignoretenants": "Игнориши клијенте",
            "info": "Информација",
            "logo": {
                "cmdbuild": "CMDBuild логотип",
                "cmdbuildready2use": "CMDBuild READY2USE логотип",
                "companylogo": "Компанијски логотип",
                "openmaint": "openMAINT логотип"
            },
            "logout": "Изађи",
            "managementmodule": "Модул за управљање подацима",
            "multigroup": "Више група",
            "multitenant": "Више клијената",
            "navigation": "Навигација",
            "newpassword": "Нова лозинка",
            "oldpassword": "Стара лозинка",
            "pagenotfound": "Страница није пронађена",
            "pleasecorrecterrors": "Молимо вас коригујте наведене грешке!",
            "preferences": {
                "comma": "Запета",
                "decimalserror": "Децимални део мора постојати",
                "decimalstousandserror": "Децимални сепаратор и сепаратор хиљада не смеју бити исти",
                "default": "Подразумевани",
                "defaultvalue": "Подразумевана вредност",
                "labeldateformat": "Формат датума",
                "labeldecimalsseparator": "Децимални сепаратор",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "Језик",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "Сепаратор хиљада",
                "labeltimeformat": "Формат времена",
                "msoffice": "Microsoft Office",
                "period": "Тачка",
                "preferredofficesuite": "Преферирани пакет канцеларијских апликација",
                "space": "Размак",
                "thousandserror": "Хиљаде морају бити присутне",
                "timezone": "Временска зона",
                "twelvehourformat": "12-часовни формат",
                "twentyfourhourformat": "24-часовни формат"
            },
            "searchinallitems": "Претрага кроз све ставке",
            "userpreferences": "Подешавања"
        },
        "menu": {
            "allitems": "Све ставке",
            "classes": "Класе",
            "custompages": "Посебне странице",
            "dashboards": "Контролне табле",
            "processes": "Картице процеса",
            "reports": "Извештаји",
            "views": "Прикази"
        },
        "notes": {
            "edit": "Измени напомену"
        },
        "notifier": {
            "attention": "Пажња",
            "error": "Грешка",
            "genericerror": "Генеричка грешка",
            "genericinfo": "Генеричка информација",
            "genericwarning": "Генеричко упозорење",
            "info": "Информација",
            "success": "Успех",
            "warning": "Пажња"
        },
        "patches": {
            "apply": "Примени исправке",
            "category": "Категорија",
            "description": "Опис",
            "name": "Назив",
            "patches": "Исправке"
        },
        "processes": {
            "abortconfirmation": "Да ли сте сигурни да желите прекинути процес?",
            "abortprocess": "Прекини процес",
            "action": {
                "advance": "Даље",
                "label": "Акција"
            },
            "activeprocesses": "Активни процес",
            "allstatuses": "Све",
            "editactivity": "Измени активност",
            "openactivity": "Отвори активност",
            "startworkflow": "Старт",
            "workflow": "Радни процеси"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Картица",
            "cardList": "Листа картица",
            "cardRelation": "Веза",
            "cardRelations": "Веза",
            "choosenaviagationtree": "Изабери стабло навигације",
            "class": "Класа",
            "class:": "Класа",
            "classList": "Листа класа",
            "compoundnode": "Сложени чвор",
            "enableTooltips": "Укључи/искључи помоћ (tooltip) на графу",
            "level": "Ниво",
            "openRelationGraph": "Отвори граф релација",
            "qt": "Qt",
            "refresh": "Освежи",
            "relation": "Веза",
            "relationGraph": "Граф релација",
            "reopengraph": "Поново отоври граф од овог чвора"
        },
        "relations": {
            "adddetail": "Додај детаље",
            "addrelations": "Додај релацију",
            "attributes": "Атрибути",
            "code": "Код",
            "deletedetail": "Уклони детаљ",
            "deleterelation": "Уклони релацију",
            "description": "Опис",
            "editcard": "Измени картицу",
            "editdetail": "Измени детаље",
            "editrelation": "Измени релацију",
            "mditems": "ставке",
            "opencard": "Отвори припадајућу картицу",
            "opendetail": "Прикажи детаље",
            "type": "Тип"
        },
        "reports": {
            "csv": "CSV",
            "download": "Преузми",
            "format": "Форматирај",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Штампај",
            "reload": "Поново учитај",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "<em>Add Thematism</em>",
            "analysisType": "Тип анализе",
            "attribute": "Атрибут",
            "calculateRules": "<em>Generate style rules</em>",
            "clearThematism": "<em>Clear Thematism</em>",
            "color": "Боја",
            "defineLegend": "<em>Legend definition</em>",
            "defineThematism": "<em>Thematism definition</em>",
            "geoAttribute": "<em>geoAttribute</em>",
            "function": "Функција",
            "generate": "Генериши",
            "graduated": "Дипломирао",
            "highlightSelected": "Означи изабрану ставку",
            "intervals": "Интервали",
            "legend": "легенда",
            "name": "<em>name</em>",
            "newThematism": "<em>New Thematism</em>",
            "punctual": "Тачан",
            "quantity": "Број (кванитет)",
            "source": "Извор",
            "table": "Табела",
            "thematism": "Тематике",
            "value": "Вредност"
        },
        "widgets": {
            "customform": {
                "addrow": "Додај ред",
                "clonerow": "Клонирај ред",
                "deleterow": "Обриши ред",
                "editrow": "Измени ред",
                "export": "Извези",
                "import": "Увези",
                "refresh": "Врати на подразумеване вредности"
            },
            "linkcards": {
                "editcard": "Измени картицу",
                "opencard": "Отвори картицу",
                "refreshselection": "Примени подразумевани избор",
                "togglefilterdisabled": "Искључи филтрирање табеле",
                "togglefilterenabled": "Укључи филтрирање табеле"
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
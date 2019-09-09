(function() {
    Ext.define('CMDBuildUI.locales.zh_CN.Locales', {
        "requires": ["CMDBuildUI.locales.zh_CN.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "zh_CN",
        "administration": CMDBuildUI.locales.zh_CN.LocalesAdministration.administration,
        "attachments": {
            "add": "增加附件",
            "attachmenthistory": "附件历史",
            "author": "作者",
            "category": "类别",
            "creationdate": "创建日期",
            "deleteattachment": "删除附件",
            "deleteattachment_confirmation": "确定要删除附件？",
            "description": "描述",
            "download": "下载",
            "editattachment": "修改附件",
            "file": "文件",
            "filename": "文件名称",
            "majorversion": "主版本",
            "modificationdate": "变更日期",
            "uploadfile": "上传文件",
            "version": "版本",
            "viewhistory": "查看附件历史"
        },
        "bim": {
            "bimViewer": "BIM视图",
            "card": {
                "label": "卡片"
            },
            "layers": {
                "label": "层",
                "menu": {
                    "hideAll": "隐藏所有",
                    "showAll": "显示所有"
                },
                "name": "名称",
                "qt": "<em>Qt</em>",
                "visibility": "可见性",
                "visivility": "可视"
            },
            "menu": {
                "camera": "相机",
                "frontView": "前置视图",
                "mod": "<em>Viewer controls</em>",
                "orthographic": "<em>Orthographic Camera</em>",
                "pan": "<em>Scroll</em>",
                "perspective": "<em>Perspective Camera</em>",
                "resetView": "重置视图",
                "rotate": "旋转",
                "sideView": "边视图",
                "topView": "顶部视图"
            },
            "showBimCard": "打开3D视图",
            "tree": {
                "arrowTooltip": "选择元素",
                "columnLabel": "树",
                "label": "树",
                "open_card": "打开关系卡片",
                "root": "<em>Ifc Root</em>"
            }
        },
        "classes": {
            "cards": {
                "addcard": "增加卡片",
                "clone": "克隆",
                "clonewithrelations": "克隆卡片和关系",
                "deletecard": "删除卡片",
                "deleteconfirmation": "确定要删除该卡片？",
                "label": "卡片",
                "modifycard": "修改卡片",
                "opencard": "打开卡片",
                "print": "打印卡片"
            },
            "simple": "简单",
            "standard": "标准"
        },
        "common": {
            "actions": {
                "add": "增加",
                "apply": "应用",
                "cancel": "取消",
                "close": "关闭",
                "delete": "删除",
                "edit": "编辑",
                "execute": "执行",
                "refresh": "刷新数据",
                "remove": "删除",
                "save": "保存",
                "saveandapply": "保存并应用",
                "saveandclose": "保存并关闭",
                "search": "搜索",
                "searchtext": "搜索"
            },
            "attributes": {
                "nogroup": "基本数据"
            },
            "dates": {
                "date": "d/m/Y",
                "datetime": "d/m/Y H:i:s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "清除HTML"
            },
            "grid": {
                "disablemultiselection": "禁止多选",
                "enamblemultiselection": "开启多选",
                "export": "<em>Export data</em>",
                "filterremoved": "该过滤已经被删除",
                "import": "<em>Import data</em>",
                "itemnotfound": "未发现项目",
                "list": "列表",
                "opencontextualmenu": "打开上下文菜单",
                "print": "打印",
                "printcsv": "打印为CSV",
                "printodt": "打印为ODT",
                "printpdf": "打印为PDF",
                "row": "条目",
                "rows": "条目",
                "subtype": "子类"
            },
            "tabs": {
                "activity": "活动",
                "attachments": "附件",
                "card": "卡片",
                "details": "明细",
                "emails": "邮件",
                "history": "历史",
                "notes": "备注",
                "relations": "关系"
            }
        },
        "emails": {
            "addattachmentsfromdms": "从DMS增加附件",
            "alredyexistfile": "<em>Already exists a file with this name</em>",
            "archivingdate": "存档日期",
            "attachfile": "附加文件",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "编辑 e-mail",
            "composefromtemplate": "Compose from template",
            "delay": "延迟",
            "delays": {
                "day1": "在1天里",
                "days2": "在2天里",
                "days4": "在4天里",
                "hour1": "1个小时",
                "hours2": "2个小时",
                "hours4": "4个小时",
                "month1": "在1个月内",
                "none": "空",
                "week1": "在1周内",
                "weeks2": "在2周内"
            },
            "dmspaneltitle": "在数据库中选择附件",
            "edit": "编辑",
            "from": "从",
            "gridrefresh": "格子更新",
            "keepsynchronization": "保持同步",
            "message": "消息",
            "regenerateallemails": "重新生成所有e-mails",
            "regenerateemail": "重新生成e-mail",
            "remove": "删除",
            "remove_confirmation": "确定要删除该邮件？",
            "reply": "回复",
            "replyprefix": "<em>On {0}, {1} wrote:</em>",
            "selectaclass": "<em>Select a class</em>",
            "sendemail": "发送邮件",
            "statuses": {
                "draft": "草稿",
                "outgoing": "发件箱",
                "received": "收件箱",
                "sent": "已发送"
            },
            "subject": "主题",
            "to": "到",
            "view": "浏览"
        },
        "errors": {
            "autherror": "错误用户名或密码",
            "classnotfound": "未发现类{0}",
            "notfound": "未发现项目"
        },
        "filters": {
            "actions": "动作",
            "addfilter": "增加过滤器",
            "any": "任何",
            "attribute": "选择一个属性",
            "attributes": "属性",
            "clearfilter": "清除过滤器",
            "clone": "克隆",
            "copyof": "拷贝",
            "description": "描述",
            "domain": "域",
            "filterdata": "过滤数据",
            "fromselection": "从...选择",
            "ignore": "忽略",
            "migrate": "移植",
            "name": "名称",
            "newfilter": "新建过滤",
            "noone": "无",
            "operator": "操作符",
            "operators": {
                "beginswith": "以　开始",
                "between": "在　之间",
                "contained": "受限的",
                "containedorequal": "受限或相当的",
                "contains": "包涵",
                "containsorequal": "包含或等于",
                "different": "不同",
                "doesnotbeginwith": "不以...开始",
                "doesnotcontain": "为包括",
                "doesnotendwith": "不以...结束",
                "endswith": "以...结束",
                "equals": "相等",
                "greaterthan": "大于",
                "isnotnull": "是非零型",
                "isnull": "是非零型",
                "lessthan": "小于"
            },
            "relations": "关系",
            "type": "类型",
            "typeinput": "输入参数",
            "value": "值"
        },
        "gis": {
            "card": "卡片",
            "cardsMenu": "<em>Cards Menu</em>",
            "externalServices": "外部服务",
            "geographicalAttributes": "地理属性",
            "geoserverLayers": "地理信息服务器层",
            "layers": "层",
            "list": "清单",
            "map": "地图",
            "mapServices": "地图服务",
            "position": "<em>Position</em>",
            "postition": "位置",
            "root": "根",
            "tree": "导航树",
            "view": "浏览",
            "zoom": "缩放"
        },
        "history": {
            "activityname": "活动名称",
            "activityperformer": "活动执行者",
            "begindate": "开始日期",
            "enddate": "结束日期",
            "processstatus": "状态",
            "user": "用户"
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
                "login": "登录",
                "logout": "用户"
            },
            "fields": {
                "group": "组",
                "language": "语言",
                "password": "密码",
                "tenants": "租户",
                "username": "用户名"
            },
            "loggedin": "已登录",
            "title": "登录",
            "welcome": "欢迎{0}归来"
        },
        "main": {
            "administrationmodule": "管理模块",
            "baseconfiguration": "<em>Base configuration</em>",
            "cardlock": {
                "lockedmessage": "无法进行编辑，因为{0}正在编辑",
                "someone": "某人"
            },
            "changegroup": "变更组",
            "changepassword": "更改密码",
            "changetenant": "变更租户",
            "confirmchangegroup": "确定要变更组？",
            "confirmchangetenants": "确定要变更活动租户？",
            "confirmdisabletenant": "确定要禁用“忽略租户”标志？",
            "confirmenabletenant": "确定要开启“忽略租户”标志？",
            "confirmpassword": "确认密码",
            "ignoretenants": "忽略租户",
            "info": "信息",
            "logo": {
                "cmdbuild": "CMDBuild 图标",
                "cmdbuildready2use": "CMDBuild READY2USE 图标",
                "companylogo": "<em>Company logo</em>",
                "openmaint": "openMAINT 图标"
            },
            "logout": "退出",
            "managementmodule": "数据管理模块",
            "multigroup": "群组",
            "multitenant": "多租户",
            "navigation": "导航",
            "newpassword": "新的密码",
            "oldpassword": "旧的密码",
            "pagenotfound": "<em>Page not found</em>",
            "pleasecorrecterrors": "<em>Please correct indicated errors!</em>",
            "preferences": {
                "comma": "逗号",
                "decimalserror": "小数位必须存在",
                "decimalstousandserror": "小数位和千数位分隔符必须不同",
                "default": "<em>Default</em>",
                "defaultvalue": "缺省值",
                "labeldateformat": "日期格式",
                "labeldecimalsseparator": "小数分割符",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "语言",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "千位分隔符",
                "labeltimeformat": "时间格式",
                "msoffice": "<em>Microsoft Office</em>",
                "period": "周期",
                "preferredofficesuite": "<em>Preferred Office suite</em>",
                "space": "空格",
                "thousandserror": "千数位必须存在",
                "timezone": "<em>Timezone</em>",
                "twelvehourformat": "12小时制格式",
                "twentyfourhourformat": "24小时制格式"
            },
            "searchinallitems": "在所有条目中搜索",
            "userpreferences": "偏好"
        },
        "menu": {
            "allitems": "所有条目",
            "classes": "类",
            "custompages": "定制页面",
            "dashboards": "仪表盘",
            "processes": "流程",
            "reports": "报表",
            "views": "浏览"
        },
        "notes": {
            "edit": "编辑注释"
        },
        "notifier": {
            "attention": "注意",
            "error": "错误",
            "genericerror": "通用错误",
            "genericinfo": "通用信息",
            "genericwarning": "通用警告",
            "info": "信息",
            "success": "成功",
            "warning": "警告"
        },
        "patches": {
            "apply": "<em>Apply patches</em>",
            "category": "<em>Category</em>",
            "description": "<em>Description</em>",
            "name": "<em>Name</em>",
            "patches": "<em>Patches</em>"
        },
        "processes": {
            "abortconfirmation": "你确定要放弃此进程吗?",
            "abortprocess": "放弃进程",
            "action": {
                "advance": "高级",
                "label": "动作"
            },
            "activeprocesses": "激活流程",
            "allstatuses": "所有",
            "editactivity": "编辑活动",
            "openactivity": "打开活动",
            "startworkflow": "开始",
            "workflow": "过程"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "卡片",
            "cardList": "卡片列表",
            "cardRelation": "关系",
            "cardRelations": "关系",
            "choosenaviagationtree": "选择导航树",
            "class": "类",
            "class:": "类",
            "classList": "类列表",
            "compoundnode": "<em>Compound Node</em>",
            "enableTooltips": "在图中开启/禁用提示",
            "level": "级别",
            "openRelationGraph": "打开关系图表",
            "qt": "<em>Qt</em>",
            "refresh": "刷新",
            "relation": "关系",
            "relationGraph": "关系图",
            "reopengraph": "从这个节点重新打开图"
        },
        "relations": {
            "adddetail": "增加细节",
            "addrelations": "增加关系",
            "attributes": "属性",
            "code": "编码",
            "deletedetail": "删除细节",
            "deleterelation": "删除关系",
            "description": "描述",
            "editcard": "修改卡片",
            "editdetail": "编辑细节",
            "editrelation": "编辑关系",
            "mditems": "条目",
            "opencard": "打开关系卡片",
            "opendetail": "显示细节",
            "type": "类型"
        },
        "reports": {
            "csv": "CSV",
            "download": "下载",
            "format": "格式",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "打印",
            "reload": "重新加载",
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
                "addrow": "增加行",
                "clonerow": "克隆行",
                "deleterow": "删除行",
                "editrow": "编辑行",
                "export": "导出",
                "import": "导入",
                "refresh": "重置为缺省值"
            },
            "linkcards": {
                "editcard": "编辑卡片",
                "opencard": "打开卡片",
                "refreshselection": "应用缺省选择",
                "togglefilterdisabled": "无效的方格过滤器",
                "togglefilterenabled": "使方格过滤器生效"
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
(function() {
    Ext.define('CMDBuildUI.locales.vn.Locales', {
        "requires": ["CMDBuildUI.locales.vn.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "vn",
        "administration": CMDBuildUI.locales.vn.LocalesAdministration.administration,
        "attachments": {
            "add": "Thêm tập tin đính kèm",
            "attachmenthistory": "<em>Attachment History</em>",
            "author": "Người cài đặt",
            "category": "<em>Category</em>",
            "creationdate": "<em>Creation date</em>",
            "deleteattachment": "<em>Delete attachment</em>",
            "deleteattachment_confirmation": "<em>Are you sure you want to delete this attachment?</em>",
            "description": "Mô tả",
            "download": "<em>Download</em>",
            "editattachment": "<em>Modifica allegato</em>",
            "file": "Tập tin",
            "filename": "<em>File name</em>",
            "majorversion": "<em>Major version</em>",
            "modificationdate": "sửa đổi ngày thang",
            "uploadfile": "<em>Upload file...</em>",
            "version": "Phiên bản",
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
                    "hideAll": "<em>Hide all</em>",
                    "showAll": "<em>Show all</em>"
                },
                "name": "<em>Name</em>",
                "qt": "<em>Qt</em>",
                "visibility": "<em>Visibility</em>",
                "visivility": "Hiển thị"
            },
            "menu": {
                "camera": "<em>Camera</em>",
                "frontView": "<em>Front View</em>",
                "mod": "<em>Viewer controls</em>",
                "orthographic": "<em>Orthographic Camera</em>",
                "pan": "<em>Scroll</em>",
                "perspective": "<em>Perspective Camera</em>",
                "resetView": "<em>Reset View</em>",
                "rotate": "<em>Rotate</em>",
                "sideView": "<em>Side View</em>",
                "topView": "<em>Top View</em>"
            },
            "showBimCard": "<em>Open 3D viewer</em>",
            "tree": {
                "arrowTooltip": "<em>Select element</em>",
                "columnLabel": "<em>Tree</em>",
                "label": "<em>Tree</em>",
                "open_card": "Mở thẻ liên quan",
                "root": "<em>Ifc Root</em>"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Thêm thẻ",
                "clone": "Sao chép",
                "clonewithrelations": "<em>Clone card and relations</em>",
                "deletecard": "Xóa thẻ",
                "deleteconfirmation": "<em>Are you sure you want to delete this card?</em>",
                "label": "Các thẻ",
                "modifycard": "Sửa đổi thẻ",
                "opencard": "<em>Open card</em>",
                "print": "<em>Print card</em>"
            },
            "simple": "Ðơn giản",
            "standard": "Tiêu chuẩn"
        },
        "common": {
            "actions": {
                "add": "Thêm",
                "apply": "Áp dụng",
                "cancel": "Hủy bỏ",
                "close": "Đóng",
                "delete": "<em>Delete</em>",
                "edit": "<em>Edit</em>",
                "execute": "<em>Execute</em>",
                "refresh": "<em>Refresh data</em>",
                "remove": "Gỡ bỏ",
                "save": "Lưu",
                "saveandapply": "Lưu và áp dụng",
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
                "itemnotfound": "Mục không trùng khớp",
                "list": "<em>List</em>",
                "opencontextualmenu": "<em>Open contextual menu</em>",
                "print": "In",
                "printcsv": "<em>Print as CSV</em>",
                "printodt": "<em>Print as ODT</em>",
                "printpdf": "<em>Print as PDF</em>",
                "row": "<em>Item</em>",
                "rows": "<em>Items</em>",
                "subtype": "<em>Subtype</em>"
            },
            "tabs": {
                "activity": "Hoạt động",
                "attachments": "Các tập tin đính kèm",
                "card": "Thẻ",
                "details": "Chi tiết",
                "emails": "<em>Emails</em>",
                "history": "Lich su",
                "notes": "Các ghi chú",
                "relations": "Các mối quan hệ"
            }
        },
        "emails": {
            "addattachmentsfromdms": "<em>Add attachments from DMS</em>",
            "alredyexistfile": "<em>Already exists a file with this name</em>",
            "archivingdate": "Ngày lưu trữ",
            "attachfile": "<em>Attach File</em>",
            "bcc": "<em>Bcc</em>",
            "cc": "<em>Cc</em>",
            "composeemail": "Soạn email",
            "composefromtemplate": "<em>Compose from template</em>",
            "delay": "<em>Delay</em>",
            "delays": {
                "day1": "<em>In 1 day</em>",
                "days2": "<em>In 2 days</em>",
                "days4": "<em>In 4 days</em>",
                "hour1": "<em>1 hour</em>",
                "hours2": "<em>2 hours</em>",
                "hours4": "<em>4 hours</em>",
                "month1": "<em>In 1 month</em>",
                "none": "<em>None</em>",
                "week1": "<em>In 1 week</em>",
                "weeks2": "<em>In 2 weeks</em>"
            },
            "dmspaneltitle": "<em>Choose attachments from Database</em>",
            "edit": "<em>Edit</em>",
            "from": "<em>From<em>",
            "gridrefresh": "<em>Grid refresh</em>",
            "keepsynchronization": "<em>Keep sync</em>",
            "message": "<em>Message</em>",
            "regenerateallemails": "<em>Regenerate all e-mails</em>",
            "regenerateemail": "Tái tạo e-mail",
            "remove": "Gỡ bỏ",
            "remove_confirmation": "<em>Are you sure you want to delete this email?</em>",
            "reply": "<em>Reply</em>",
            "replyprefix": "<em>On {0}, {1} wrote:</em>",
            "selectaclass": "<em>Select a class</em>",
            "sendemail": "<em>Send e-mail</em>",
            "statuses": {
                "draft": "Dự thảo",
                "outgoing": "Hộp gửi đi",
                "received": "Nhận được",
                "sent": "Gửi"
            },
            "subject": "<em>Subject</em>",
            "to": "<em>To</em>",
            "view": "<em>View</em>"
        },
        "errors": {
            "autherror": "Sai Tên người dùng hay Mật khẩu",
            "classnotfound": "Class {0} không tìm thấy",
            "notfound": "Mục không trùng khớp"
        },
        "filters": {
            "actions": "<em>Actions</em>",
            "addfilter": "Thêm bộ lọc",
            "any": "Tùy chọn",
            "attribute": "Chọn một thuộc tính",
            "attributes": "Các thuộc tính",
            "clearfilter": "Làm sạch bộ lọc",
            "clone": "Sao chép",
            "copyof": "Sao chép của",
            "description": "Mô tả",
            "domain": "Tên miền",
            "filterdata": "<em>Filter data</em>",
            "fromselection": "Lựa chọn từ",
            "ignore": "<em>Ignore</em>",
            "migrate": "<em>Migrate</em>",
            "name": "Tên",
            "newfilter": "<em>New filter</em>",
            "noone": "Không có",
            "operator": "<em>Operator</em>",
            "operators": {
                "beginswith": "Bắt đầu với",
                "between": "Ở giữa",
                "contained": "<em>Contained</em>",
                "containedorequal": "<em>Contained or equal</em>",
                "contains": "Bao gồm",
                "containsorequal": "<em>Contains or equal</em>",
                "different": "Khác biệt",
                "doesnotbeginwith": "Không bắt đầu với",
                "doesnotcontain": "Koji ne sadrže",
                "doesnotendwith": "Không kết thúc tại",
                "endswith": "Kết thúc tại",
                "equals": "Cân bằng",
                "greaterthan": "Nhiều hơn",
                "isnotnull": "Co phai no vo hieu luc",
                "isnull": "No vo hieu luc",
                "lessthan": "Ít hơn"
            },
            "relations": "Các mối quan hệ",
            "type": "Kiểu",
            "typeinput": "Thông số đầu vào",
            "value": "<em>Value</em>"
        },
        "gis": {
            "card": "Thẻ",
            "cardsMenu": "<em>Cards Menu</em>",
            "externalServices": "Các dịch vụ ngoài",
            "geographicalAttributes": "Thuộc tính địa lí",
            "geoserverLayers": "Các lớp Geoserver",
            "layers": "<em>Layers</em>",
            "list": "Danh sách",
            "map": "Bản đồ",
            "mapServices": "<em>Map Services</em>",
            "position": "<em>Position</em>",
            "root": "<em>Root</em>",
            "tree": "<em>Navigation tree</em>",
            "view": "<em>View</em>",
            "zoom": "<em>Zoom</em>"
        },
        "history": {
            "activityname": "Tên hoạt động",
            "activityperformer": "Hoạt động biểu diễn",
            "begindate": "Ngày bắt đầu",
            "enddate": "Ngày kết thúc",
            "processstatus": "<em>Status</em>",
            "user": "Người dùng"
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
                "login": "Đăng nhập",
                "logout": "<em>Change user</em>"
            },
            "fields": {
                "group": "<em>Group</em>",
                "language": "<em>Language</em>",
                "password": "<em>Password</em>",
                "tenants": "<em>Tenants</em>",
                "username": "<em>Username</em>"
            },
            "loggedin": "<em>Logged in</em>",
            "title": "Đăng nhập",
            "welcome": "<em>Welcome back {0}.</em>"
        },
        "main": {
            "administrationmodule": "Mô-đun Quản lý",
            "baseconfiguration": "<em>Base configuration</em>",
            "cardlock": {
                "lockedmessage": "<em>You can't edit this card because {0} is editing it.</em>",
                "someone": "<em>someone</em>"
            },
            "changegroup": "<em>Change group</em>",
            "changepassword": "Đổi mật khẩu",
            "changetenant": "<em>Change tenant</em>",
            "confirmchangegroup": "<em>Are you sure you want to change the group?</em>",
            "confirmchangetenants": "<em>Are you sure you want to change active tenants?</em>",
            "confirmdisabletenant": "<em>Are you sure you want to disable \"Ignore tenants\" flag?</em>",
            "confirmenabletenant": "<em>Are you sure you want to enable \"Ignore tenants\" flag?</em>",
            "confirmpassword": "Xác nhận mật khẩu",
            "ignoretenants": "<em>Ignore tenants</em>",
            "info": "Thông tin",
            "logo": {
                "cmdbuild": "<em>CMDBuild logo</em>",
                "cmdbuildready2use": "<em>CMDBuild READY2USE logo</em>",
                "companylogo": "<em>Company logo</em>",
                "openmaint": "<em>openMAINT logo</em>"
            },
            "logout": "Đăng xuất",
            "managementmodule": "Danh muc quản lí dữ liệu",
            "multigroup": "<em>Multi group</em>",
            "multitenant": "<em>Multi tenant</em>",
            "navigation": "Hướng",
            "newpassword": "Mật khẩu mới",
            "oldpassword": "Mật khẩu cũ",
            "pagenotfound": "<em>Page not found</em>",
            "pleasecorrecterrors": "<em>Please correct indicated errors!</em>",
            "preferences": {
                "comma": "<em>Comma</em>",
                "decimalserror": "<em>Decimals field must be present</em>",
                "decimalstousandserror": "<em>Decimals and Thousands separato must be differents</em>",
                "default": "<em>Default</em>",
                "defaultvalue": "Giá trị mặc định",
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
            "classes": "Các lớp",
            "custompages": "<em>Custom pages</em>",
            "dashboards": "<em>Dashboards</em>",
            "processes": "Các quy trình",
            "reports": "<em>Reports</em>",
            "views": "Lượt xem"
        },
        "notes": {
            "edit": "Chỉnh sửa ghi chú"
        },
        "notifier": {
            "attention": "Chú ý",
            "error": "Lỗi",
            "genericerror": "<em>Generic error</em>",
            "genericinfo": "<em>Generic info</em>",
            "genericwarning": "<em>Generic warning</em>",
            "info": "Thông tin",
            "success": "Thành công",
            "warning": "Cảnh báo"
        },
        "patches": {
            "apply": "<em>Apply patches</em>",
            "category": "<em>Category</em>",
            "description": "<em>Description</em>",
            "name": "<em>Name</em>",
            "patches": "<em>Patches</em>"
        },
        "processes": {
            "abortconfirmation": "Bạn có chắc là bạn muốn hủy bỏ quá trình?",
            "abortprocess": "Hủy bỏ quá trình",
            "action": {
                "advance": "Thành quả",
                "label": "<em>Action</em>"
            },
            "activeprocesses": "<em>Active processes</em>",
            "allstatuses": "<em>All</em>",
            "editactivity": "Chỉnh sửa hoạt đọng",
            "openactivity": "<em>Open activity</em>",
            "startworkflow": "<em>Start</em>",
            "workflow": "<em>Workflow</em>"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Thẻ",
            "cardList": "<em>Card List</em>",
            "cardRelation": "<em>Relation</em>",
            "cardRelations": "<em>Relation</em>",
            "choosenaviagationtree": "<em>Choose navigation tree</em>",
            "class": "<em>Class</em>",
            "class:": "<em>Class</em>",
            "classList": "<em>Class List</em>",
            "compoundnode": "<em>Compound Node</em>",
            "enableTooltips": "<em>Enable/disable tooltips on graph</em>",
            "level": "<em>Level</em>",
            "openRelationGraph": "Mở rộng đồ thị mối quan hệ",
            "qt": "<em>Qt</em>",
            "refresh": "<em>Refresh</em>",
            "relation": "<em>Relation</em>",
            "relationGraph": "Đồ thị mối quan hệ",
            "reopengraph": "<em>Reopen the graph from this node</em>"
        },
        "relations": {
            "adddetail": "Thêm chi tiết",
            "addrelations": "Thêm các mối quan hệ",
            "attributes": "Các thuộc tính",
            "code": "Mã",
            "deletedetail": "Xóa chi tiết",
            "deleterelation": "Xóa mối quan hệ",
            "description": "Mô tả",
            "editcard": "Sửa đổi thẻ",
            "editdetail": "Chỉnh sửa chi tiết",
            "editrelation": "Chỉnh sửa mối quan hệ",
            "mditems": "<em>items</em>",
            "opencard": "Mở thẻ liên quan",
            "opendetail": "Hiện thị chi tiết",
            "type": "Kiểu"
        },
        "reports": {
            "csv": "<em>CSV</em>",
            "download": "<em>Download</em>",
            "format": "Mẫu",
            "odt": "<em>ODT</em>",
            "pdf": "<em>PDF</em>",
            "print": "In",
            "reload": "Tải lại",
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
                "addrow": "<em>Add row</em>",
                "clonerow": "<em>Clone row</em>",
                "deleterow": "<em>Delete row</em>",
                "editrow": "<em>Edit row</em>",
                "export": "Xuất",
                "import": "<em>Import</em>",
                "refresh": "<em>Refresh to defaults</em>"
            },
            "linkcards": {
                "editcard": "<em>Edit card</em>",
                "opencard": "<em>Open card</em>",
                "refreshselection": "<em>Apply default selection</em>",
                "togglefilterdisabled": "<em>Disable grid filter</em>",
                "togglefilterenabled": "<em>Enable grid filter</em>"
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
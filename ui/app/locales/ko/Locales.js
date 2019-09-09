(function() {
    Ext.define('CMDBuildUI.locales.ko.Locales', {
        "requires": ["CMDBuildUI.locales.ko.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "ko",
        "administration": CMDBuildUI.locales.ko.LocalesAdministration.administration,
        "attachments": {
            "add": "첨부 추가",
            "attachmenthistory": "첨부이력",
            "author": "저자",
            "category": "카테고리",
            "creationdate": "날짜 생성",
            "deleteattachment": "첨부 삭제",
            "deleteattachment_confirmation": "이 첨부를 삭제 하시겠습니까?",
            "description": "설명",
            "download": "다운로드",
            "editattachment": "첨부 수정",
            "file": "파일",
            "filename": "파일명",
            "majorversion": "메이저버전",
            "modificationdate": "갱신일",
            "uploadfile": "파일 업로드",
            "version": "버전",
            "viewhistory": "첨부이력 보기"
        },
        "bim": {
            "bimViewer": "BIM 뷰어",
            "card": {
                "label": "카드"
            },
            "layers": {
                "label": "레이어",
                "menu": {
                    "hideAll": "모두 숨김",
                    "showAll": "모두 표시"
                },
                "name": "이름",
                "qt": "수랑",
                "visibility": "가시성",
                "visivility": "표시"
            },
            "menu": {
                "camera": "카메라",
                "frontView": "정면도",
                "mod": "모드(뷰어 컨트롤)",
                "orthographic": "<em>Orthographic Camera</em>",
                "pan": "팬 (스크롤)",
                "perspective": "<em>Perspective Camera</em>",
                "resetView": "보기 재설정",
                "rotate": "회전",
                "sideView": "측면도",
                "topView": "상면도"
            },
            "showBimCard": "3D 뷰어 열기",
            "tree": {
                "arrowTooltip": "구성요소 선택",
                "columnLabel": "트리",
                "label": "트리",
                "open_card": "관련 카드 열기",
                "root": "<em>Ifc Root</em>"
            }
        },
        "classes": {
            "cards": {
                "addcard": "카드 추가",
                "clone": "복제",
                "clonewithrelations": "카드 및 관계 복제",
                "deletecard": "카드 삭제",
                "deleteconfirmation": "이 카드를 삭제 하시겠습니까?",
                "label": "카드",
                "modifycard": "카드 수정",
                "opencard": "카드 열기",
                "print": "카드 출력"
            },
            "simple": "단순",
            "standard": "표준"
        },
        "common": {
            "actions": {
                "add": "추가",
                "apply": "적용",
                "cancel": "취소",
                "close": "닫기",
                "delete": "삭제",
                "edit": "편집",
                "execute": "실행",
                "refresh": "데이터 새로고침",
                "remove": "삭제",
                "save": "저장",
                "saveandapply": "저장하고 적용",
                "saveandclose": "저장하고 닫기",
                "search": "검색",
                "searchtext": "검색"
            },
            "attributes": {
                "nogroup": "기초 데이터"
            },
            "dates": {
                "date": "일/월/연",
                "datetime": "일/월/연 시:분:초",
                "time": "시:분:초"
            },
            "editor": {
                "clearhtml": "HTML 정리"
            },
            "grid": {
                "disablemultiselection": "복수 선택 불가",
                "enamblemultiselection": "복수 선택 가능",
                "export": "<em>Export data</em>",
                "filterremoved": "현태 필터는 제거되었습니다.",
                "import": "<em>Import data</em>",
                "itemnotfound": "아이템이 발견되지 않았습니다.",
                "list": "목록",
                "opencontextualmenu": "컨텍스트메뉴 열기",
                "print": "인쇄",
                "printcsv": "CSV로 출력",
                "printodt": "ODT로 출력",
                "printpdf": "PDF로 출력",
                "row": "아이템",
                "rows": "아이템",
                "subtype": "서브타입"
            },
            "tabs": {
                "activity": "액티비티",
                "attachments": "첨부",
                "card": "카드",
                "details": "상세",
                "emails": "이메일",
                "history": "이력",
                "notes": "노트",
                "relations": "관계"
            }
        },
        "emails": {
            "addattachmentsfromdms": "자료관리시스템에서 첨부 추가",
            "alredyexistfile": "<em>Already exists a file with this name</em>",
            "archivingdate": "보관 일자",
            "attachfile": "파일 첨부",
            "bcc": "Bcc 숨은참조",
            "cc": "cc 참조",
            "composeemail": "email 작성",
            "composefromtemplate": "템플릿에서 작성",
            "delay": "지연",
            "delays": {
                "day1": "하루",
                "days2": "이틀",
                "days4": "나흘",
                "hour1": "1시간",
                "hours2": "2시간",
                "hours4": "4시간",
                "month1": "1개월",
                "none": "None",
                "week1": "1주일",
                "weeks2": "2주일"
            },
            "dmspaneltitle": "데이터베이스에서 첨부 선택",
            "edit": "편집",
            "from": "From 발신",
            "gridrefresh": "그리드 새로고침",
            "keepsynchronization": "동기화 유지",
            "message": "메시지",
            "regenerateallemails": "모든 이메일 다시 생성",
            "regenerateemail": "이메일 다시 작성",
            "remove": "삭제",
            "remove_confirmation": "이 이메일을 삭제 하시겠습니까?",
            "reply": "답장",
            "replyprefix": "<em>On {0}, {1} wrote:</em>",
            "selectaclass": "<em>Select a class</em>",
            "sendemail": "이메일 보내기",
            "statuses": {
                "draft": "초안",
                "outgoing": "발신",
                "received": "수신",
                "sent": "송신"
            },
            "subject": "제목",
            "to": "To 수신",
            "view": "보기"
        },
        "errors": {
            "autherror": "잘못된 유저명 또는 비밀번호",
            "classnotfound": "클래스 {0}가 발견되지 않았습니다.",
            "notfound": "아이템이 발견되지 않습니다"
        },
        "filters": {
            "actions": "<em>Actions</e",
            "addfilter": "필터 추가",
            "any": "모든",
            "attribute": "속성 선택",
            "attributes": "속성",
            "clearfilter": "필터 클리어",
            "clone": "복제",
            "copyof": "복사",
            "description": "설명",
            "domain": "도메인",
            "filterdata": "필터 데이터",
            "fromselection": "선택",
            "ignore": "무시",
            "migrate": "이송",
            "name": "이름",
            "newfilter": "새로운 필터",
            "noone": "아무것도 없음",
            "operator": "운영자",
            "operators": {
                "beginswith": "~로 시작",
                "between": "간(사이)",
                "contained": "포함",
                "containedorequal": "포함 또는 동일",
                "contains": "포함",
                "containsorequal": "포함 또는 동일",
                "different": "다른",
                "doesnotbeginwith": "~로 시작하지 않는",
                "doesnotcontain": "포함하지 않는",
                "doesnotendwith": "끝나지 않는",
                "endswith": "~로 끝나는",
                "equals": "동일",
                "greaterthan": "크게",
                "isnotnull": "null값 아님",
                "isnull": "null 값",
                "lessthan": "작은"
            },
            "relations": "관계",
            "type": "타입",
            "typeinput": "파라미터 입력",
            "value": "값"
        },
        "gis": {
            "card": "카드",
            "cardsMenu": "<em>Cards Menu</em>",
            "externalServices": "외부 서비스",
            "geographicalAttributes": "지리적 속성",
            "geoserverLayers": "Geo서버 레이어",
            "layers": "레이어",
            "list": "목록",
            "map": "지도",
            "mapServices": "지도 서비스",
            "position": "위치",
            "postition": "위치",
            "root": "루트",
            "tree": "네비게이션 트리",
            "view": "보기",
            "zoom": "줌"
        },
        "history": {
            "activityname": "액티비티 이름",
            "activityperformer": "액티비티 실행자",
            "begindate": "시작일",
            "enddate": "종료일",
            "processstatus": "상태",
            "user": "사용자"
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
                "login": "로그인",
                "logout": "사용자 변경"
            },
            "fields": {
                "group": "그룹",
                "language": "언어",
                "password": "비밀번호",
                "tenants": "사용자",
                "username": "사용자명"
            },
            "loggedin": "로그인 됨",
            "title": "로그인",
            "welcome": "{0}님 다시 오신 걸 환영합니다."
        },
        "main": {
            "administrationmodule": "관리자 모듈",
            "baseconfiguration": "<em>Base configuration</em>",
            "cardlock": {
                "lockedmessage": "{0}이 편집 중이므로 이 카드를 편집할 수 없습니다.",
                "someone": "특정사용자"
            },
            "changegroup": "그룹 변경",
            "changepassword": "비밀번호 변경",
            "changetenant": "사용자 변경",
            "confirmchangegroup": "그룹을 변경하시겠습니까?",
            "confirmchangetenants": "현재 사용자를 변경하시겠습니다?",
            "confirmdisabletenant": " \"사용자 무시\" 표시를 사용하지 않겠습니까?",
            "confirmenabletenant": " \"사용자 무시\" 표시를 사용하겠습니까?",
            "confirmpassword": "비밀 번호 확인",
            "ignoretenants": "사용자 무시",
            "info": "정보",
            "logo": {
                "cmdbuild": "CMDBuild 로고",
                "cmdbuildready2use": "READY2USE 로고",
                "companylogo": "<em>Company logo</em>",
                "openmaint": "openMAINT 로고"
            },
            "logout": "로그아웃",
            "managementmodule": "데이터관리모듈",
            "multigroup": "멀티 그룹",
            "multitenant": "복수 사용자",
            "navigation": "네비게이션",
            "newpassword": "새로운 비밀번호",
            "oldpassword": "기존 비밀번호",
            "pagenotfound": "<em>Page not found</em>",
            "pleasecorrecterrors": "<em>Please correct indicated errors!</em>",
            "preferences": {
                "comma": "콤마",
                "decimalserror": "소수(Decimals) 필드는 반드시 존재해야 합니다.",
                "decimalstousandserror": "소수 분리기호와 천단위 분리기호는 달라야 합니다.",
                "default": "<em>Default</em>",
                "defaultvalue": "기본 값",
                "labeldateformat": "날짜 형식",
                "labeldecimalsseparator": "소수 분리기호",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "언어",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "천단위 분리기호",
                "labeltimeformat": "시간 형식",
                "msoffice": "<em>Microsoft Office</em>",
                "period": "마침표",
                "preferredofficesuite": "<em>Preferred Office suite</em>",
                "space": "빈공간",
                "thousandserror": "천단위 필드는 반드시 존재해야 합니다.",
                "timezone": "<em>Timezone</em>",
                "twelvehourformat": "12시간 형식",
                "twentyfourhourformat": "24시간 형식"
            },
            "searchinallitems": "모든 아이템에서 검색",
            "userpreferences": "사용자 설정"
        },
        "menu": {
            "allitems": "모든 아이템",
            "classes": "클래스",
            "custompages": "커스텀 페이지",
            "dashboards": "대시보드",
            "processes": "프로세스",
            "reports": "보고서",
            "views": "보기"
        },
        "notes": {
            "edit": "노트 수정"
        },
        "notifier": {
            "attention": "주의",
            "error": "에러",
            "genericerror": "일반 오류",
            "genericinfo": "일반 정보",
            "genericwarning": "일반 경고",
            "info": "정보",
            "success": "성공",
            "warning": "경고"
        },
        "patches": {
            "apply": "<em>Apply patches</em>",
            "category": "<em>Category</em>",
            "description": "<em>Description</em>",
            "name": "<em>Name</em>",
            "patches": "<em>Patches</em>"
        },
        "processes": {
            "abortconfirmation": "이 프로세스를 중단하시겠습니까?",
            "abortprocess": "프로세스 중단",
            "action": {
                "advance": "진행",
                "label": "실행"
            },
            "activeprocesses": "프로세스 활성화",
            "allstatuses": "모두",
            "editactivity": "액티비티 수정",
            "openactivity": "액티비티 열기",
            "startworkflow": "시작",
            "workflow": "워크플로우"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "카드",
            "cardList": "카드 목록",
            "cardRelation": "릴레이션",
            "cardRelations": "카드 관계",
            "choosenaviagationtree": "내비게이션 트리 선택",
            "class": "클래스",
            "class:": "반",
            "classList": "클래스 목록",
            "compoundnode": "<em>Compound Node</em>",
            "enableTooltips": "그래프 상에 툴팁 미표시",
            "level": "레벨",
            "openRelationGraph": "릴레이션 그래프 열기",
            "qt": "수량",
            "refresh": "새로고침",
            "relation": "릴레이션",
            "relationGraph": "릴레이션 그래프",
            "reopengraph": "이 노드로부터 그래프 다시 열기"
        },
        "relations": {
            "adddetail": "상세 추가",
            "addrelations": "관계 추가",
            "attributes": "속성",
            "code": "코드",
            "deletedetail": "상세 삭제",
            "deleterelation": "관계 삭제",
            "description": "설명",
            "editcard": "카드 편집",
            "editdetail": "상세 편집",
            "editrelation": "관계 편집",
            "mditems": "아이템",
            "opencard": "관련 카드 열기",
            "opendetail": "상세 보기",
            "type": "타입"
        },
        "reports": {
            "csv": "CSV",
            "download": "다운로드",
            "format": "포맷",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "인쇄",
            "reload": "다시 읽기",
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
                "addrow": "행 추가",
                "clonerow": "행 복사",
                "deleterow": "행 삭제",
                "editrow": "행 편집",
                "export": "내보내기",
                "import": "불러오기",
                "refresh": "기본으로 새로고침"
            },
            "linkcards": {
                "editcard": "카드 폍집",
                "opencard": "카드 열기",
                "refreshselection": "기본 선택 적용",
                "togglefilterdisabled": "그리드 필터 유효화",
                "togglefilterenabled": "그리드 필터 무효화"
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
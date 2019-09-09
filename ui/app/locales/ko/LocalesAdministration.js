Ext.define('CMDBuildUI.locales.ko.LocalesAdministration', {
    "singleton": true,
    "localization": "ko",
    "administration": {
        "attributes": {
            "attribute": "속성",
            "attributes": "속성",
            "emptytexts": {
                "search": "검색"
            },
            "fieldlabels": {
                "actionpostvalidation": "검증 후 액션",
                "active": "액티브",
                "attributegroupings": "<em>Attribute groupings</em>",
                "autovalue": "<em>Auto value</em>",
                "decimalseparator": "소수점 구분기호",
                "description": "설명",
                "domain": "도메인",
                "editortype": "에디터 타입",
                "filter": "필터",
                "format": "포맷",
                "group": "그룹",
                "help": "도움",
                "includeinherited": "상속 포함",
                "iptype": "IP타입",
                "lookup": "룩업",
                "mandatory": "필수",
                "maxlength": "최대 길이",
                "mode": "모드",
                "name": "이름",
                "positioningofum": "<em>Positioning of the UM</em>",
                "precision": "정확도",
                "preselectifunique": "유일한 경우 미리 선택",
                "scale": "스케일",
                "separator": "분리자",
                "separators": "분리자",
                "showif": "표시 여부",
                "showingrid": "그리드에 표시",
                "showinreducedgrid": "축소된 그리드에 표시",
                "showseconds": "<em>Show seconds</em>",
                "showseparator": "분리자 표시",
                "thousandsseparator": "천단위 구분기호",
                "type": "타입",
                "unique": "유일",
                "unitofmeasure": "측정 단위",
                "unitofmeasurelocation": "측정 위치 단위",
                "validationrules": "검증 규칙",
                "visibledecimals": "소수점 표시"
            },
            "strings": {
                "addnewgroup": "<em>Add new group</em>",
                "any": "모든",
                "createnewgroup": "<em>Create new group</em>",
                "draganddrop": "끌어 놓아서 재구성",
                "editable": "편집 가능",
                "editorhtml": "Html 편집",
                "hidden": "숨김",
                "immutable": "변경불가",
                "ipv4": "<em>IPV4</em>",
                "ipv6": "<em>IPV6</em>",
                "plaintext": "일반 텍스트",
                "positioningofumrequired": "<em>The position of the unit of measurement is mandatory</em>",
                "precisionmustbebiggerthanscale": "정확도는 스케일보다 반드시 커야 함",
                "readonly": "읽기 전용",
                "scalemustbesmallerthanprecision": "스케일은 정밀도보다 작아야 함",
                "thefieldmandatorycantbechecked": "필수 필드는 체크할 수 없습니다.",
                "thefieldmodeishidden": "모드 필드가 숨겨졌습니다.",
                "thefieldshowingridcantbechecked": "그리드에 표시 필드를 체크할 수 없습니다.",
                "thefieldshowinreducedgridcantbechecked": "축소된 그리드에 표시 필드를 체크할 수 없습니다.",
                "unitofmeasurelocationrequired": "측정 단위의 위치는 필수 사항입니다."
            },
            "texts": {
                "active": "액티브",
                "addattribute": "속성 추가",
                "cancel": "취소",
                "description": "설명",
                "direct": "<em>Direct</em>",
                "editingmode": "편집 모드",
                "editmetadata": "메타 데이터 편집",
                "grouping": "그룹만들기",
                "inverse": "<em>Inverse</em>",
                "mandatory": "필수",
                "name": "이름",
                "newattribute": "<em>New attribute</em>",
                "save": "저장",
                "saveandadd": "저장 및 추가",
                "showingrid": "그리드에 표시",
                "type": "타입",
                "unique": "유일",
                "viewmetadata": "메타데이터 보기"
            },
            "titles": {
                "generalproperties": "일반 속성",
                "otherproperties": "다른 속성",
                "typeproperties": "타입 속성"
            },
            "tooltips": {
                "deleteattribute": "삭제",
                "disableattribute": "비활성화",
                "editattribute": "편집",
                "enableattribute": "유효",
                "openattribute": "열기",
                "translate": "번역"
            }
        },
        "bim": {
            "addproject": "<em>Add project</em>",
            "ifcfile": "<em>IFC File</em>",
            "lastcheckin": "<em>Last check-in</em>",
            "mappingfile": "<em>Mapping file</em>",
            "multilevel": "<em>Multilevel</em>",
            "newproject": "<em>New project</em>",
            "parentproject": "<em>Parent project</em>",
            "projectlabel": "<em>Project</em>",
            "projects": "<em>Projects</em>"
        },
        "classes": {
            "fieldlabels": {
                "applicability": "적용성",
                "attachmentsinline": "<em>Inline attachments</em>",
                "attachmentsinlineclosed": "<em>Closed inline attachments</em>",
                "categorylookup": "카테고리 조회",
                "defaultexporttemplate": "<em>Default template for data export</em>",
                "defaultimporttemplate": "<em>Default template for data import</em>",
                "descriptionmode": "설명 모드",
                "guicustom": "GUI 커스텀",
                "guicustomparameter": "GUI 커스텀 변수",
                "multitenantmode": "다수 사용자 모드",
                "superclass": "슈퍼클래스",
                "widgetname": "위젯명"
            },
            "properties": {
                "form": {
                    "fieldsets": {
                        "ClassAttachments": "클래스 첨부 파일",
                        "classParameters": "클래스 매개 변수",
                        "contextMenus": {
                            "actions": {
                                "delete": {
                                    "tooltip": "삭제"
                                },
                                "edit": {
                                    "tooltip": "편집"
                                },
                                "moveDown": {
                                    "tooltip": "아래로 이동"
                                },
                                "moveUp": {
                                    "tooltip": "위로 이동"
                                }
                            },
                            "inputs": {
                                "applicability": {
                                    "label": "적용성",
                                    "values": {
                                        "all": {
                                            "label": "모두"
                                        },
                                        "many": {
                                            "label": "현재 및 선택됨"
                                        },
                                        "one": {
                                            "label": "현재"
                                        }
                                    }
                                },
                                "javascriptScript": {
                                    "label": "자바스크립트 / 사용자 지정 GUI 매개 변수"
                                },
                                "menuItemName": {
                                    "label": "메뉴 아이템명",
                                    "values": {
                                        "separator": {
                                            "label": "<em>[---------]</em>"
                                        }
                                    }
                                },
                                "status": {
                                    "label": "상태",
                                    "values": {
                                        "active": {
                                            "label": "액티브"
                                        }
                                    }
                                },
                                "typeOrGuiCustom": {
                                    "label": "타입 / GUI 커스텀",
                                    "values": {
                                        "component": {
                                            "label": "사용자지정 GUI"
                                        },
                                        "custom": {
                                            "label": "<em>Script Javascript</em>"
                                        },
                                        "separator": {
                                            "label": "<em></em>"
                                        }
                                    }
                                }
                            },
                            "title": "컨텍스트 메뉴"
                        },
                        "createnewwidget": "<em>Create new widget</em>",
                        "defaultOrders": "기본 정렬",
                        "formTriggers": {
                            "actions": {
                                "addNewTrigger": {
                                    "tooltip": "신규 트리거 추가"
                                },
                                "deleteTrigger": {
                                    "tooltip": "삭제"
                                },
                                "editTrigger": {
                                    "tooltip": "편집"
                                },
                                "moveDown": {
                                    "tooltip": "아래로 이동"
                                },
                                "moveUp": {
                                    "tooltip": "위로 이동"
                                }
                            },
                            "inputs": {
                                "createNewTrigger": {
                                    "label": "새 양식 트리거 생성"
                                },
                                "events": {
                                    "label": "이벤트",
                                    "values": {
                                        "afterClone": {
                                            "label": "복제 후"
                                        },
                                        "afterDelete": {
                                            "label": "<em>After delete</em>"
                                        },
                                        "afterEdit": {
                                            "label": "편집 후"
                                        },
                                        "afterInsert": {
                                            "label": "삽입 후"
                                        },
                                        "beforView": {
                                            "label": "보기 전"
                                        },
                                        "beforeClone": {
                                            "label": "복제 전"
                                        },
                                        "beforeEdit": {
                                            "label": "편집 이전"
                                        },
                                        "beforeInsert": {
                                            "label": "삽입 전"
                                        }
                                    }
                                },
                                "javascriptScript": {
                                    "label": "<em>Javascript script</em>"
                                },
                                "status": {
                                    "label": "상태"
                                }
                            },
                            "title": "트리거로부터"
                        },
                        "formWidgets": "양식 위젯 편집",
                        "generalData": {
                            "inputs": {
                                "active": {
                                    "label": "액티브"
                                },
                                "classType": {
                                    "label": "타입"
                                },
                                "description": {
                                    "label": "설명"
                                },
                                "name": {
                                    "label": "이름"
                                },
                                "parent": {
                                    "label": "~로부터 상속"
                                },
                                "superclass": {
                                    "label": "슈퍼클래스"
                                }
                            }
                        },
                        "icon": "아이콘",
                        "validation": {
                            "inputs": {
                                "validationRule": {
                                    "label": "검증 규칙"
                                }
                            },
                            "title": "확인(검증)"
                        }
                    },
                    "inputs": {
                        "events": "이벤트",
                        "javascriptScript": "<em>Javascript Script</em>",
                        "status": "상태"
                    },
                    "values": {
                        "active": "액티브"
                    }
                },
                "title": "속성",
                "toolbar": {
                    "cancelBtn": "취소",
                    "closeBtn": "닫기",
                    "deleteBtn": {
                        "tooltip": "삭제"
                    },
                    "disableBtn": {
                        "tooltip": "비활성화"
                    },
                    "editBtn": {
                        "tooltip": "클래스 변경"
                    },
                    "enableBtn": {
                        "tooltip": "유효"
                    },
                    "printBtn": {
                        "printAsOdt": "<em>OpenOffice Odt</em>",
                        "printAsPdf": "<em>Adobe Pdf</em>",
                        "tooltip": "클래스 인쇄"
                    },
                    "saveBtn": "저장"
                }
            },
            "strings": {
                "classactivated": "클래스가 올바르게 활성화됨.",
                "classdisabled": "클래스가 올바르게 비활성화됨.",
                "createnewcontextaction": "새 컨텍스트 작업 생성",
                "datacardsorting": "데이터 카드 정렬",
                "deleteclass": "클래스 삭제",
                "deleteclassquest": "이 클래스를 삭제하시겠습니까?",
                "editcontextmenu": "컨텍스트 메뉴 편집",
                "editformwidget": "양식 위젯 편집",
                "edittrigger": "트리거 편집",
                "executeon": "<em>Execute on</em>",
                "geaoattributes": "Geo 속성",
                "levels": "레이어"
            },
            "texts": {
                "calendar": "달력",
                "class": "클래스",
                "component": "구성요소",
                "createmodifycard": "카드 생성/수정",
                "createreport": "보고서 작성",
                "custom": "사용자지정",
                "direction": "<em>Direction</em>",
                "ping": "<em>Ping</em>",
                "separator": "분리자",
                "simple": "간단",
                "standard": "표준",
                "startworkflow": "워크플로우 시작"
            },
            "title": "클래스들",
            "toolbar": {
                "addClassBtn": {
                    "text": "클래스 추가"
                },
                "classLabel": "클래스",
                "printSchemaBtn": {
                    "text": "스키마 인쇄"
                },
                "searchTextInput": {
                    "emptyText": "모든 클래스 검색"
                }
            }
        },
        "common": {
            "actions": {
                "activate": "활성화",
                "add": "추가",
                "cancel": "취소",
                "clone": "복제",
                "clonefrom": "복제 대상",
                "close": "닫기",
                "create": "생성",
                "delete": "삭제",
                "disable": "비활성화",
                "download": "<em>Download</em>",
                "edit": "편집",
                "enable": "유효",
                "movedown": "아래로 이동",
                "moveup": "위로 이동",
                "next": "다음",
                "no": "아니오",
                "ok": "<em>Ok</em>",
                "open": "열기",
                "prev": "이전",
                "print": "인쇄",
                "relationchart": "관련성 차트",
                "remove": "제거",
                "save": "저장",
                "saveandadd": "저장 및 추가",
                "update": "갱신",
                "yes": "네"
            },
            "labels": {
                "active": "액티브",
                "code": "코드",
                "colorpreview": "<em>Color preview</em>",
                "default": "<em>Default</em>",
                "defaultfilter": "기본 필터",
                "description": "설명",
                "funktion": "<em>Function</em>",
                "icon": "아이콘",
                "iconcolor": "<em>Icon color</em>",
                "iconpreview": "<em>Icon preview</em>",
                "icontype": "<em>Icon type</em>",
                "name": "이름",
                "note": "<em>Note</em>",
                "noteinline": "일직선 참고",
                "noteinlineclosed": "일직선 닫힘 참고",
                "status": "<em>Status</em>",
                "tenant": "<em>Tenant</em>",
                "textcolor": "<em>Text color</em>",
                "tree": "<em>Tree</em>",
                "type": "<em>Type</em>"
            },
            "messages": {
                "applicationreloadquest": "이 어플리케이션에는 업데이트가 있음",
                "applicationupdate": "어플리케이션 업데이트",
                "areyousuredeleteitem": "이 아이템을 삭제하시겠습니까?",
                "ascendingordescending": "이 값은 유효하지 않음",
                "attention": "주의",
                "cannotsortitems": "일부 필터가 있거나 상속된 특성이 숨겨져 있으면 항목을 다시 정렬할 수 없습니다. 해당 항목을 제거한 후 다시 시도하십시오.",
                "cantcontainchar": "클래스 이름에 {0}자를 포함할 수 없음",
                "correctformerrors": "표시된 에러를 교정해 주십시오.",
                "disabled": "비활성화",
                "enabled": "유효",
                "error": "에러",
                "greaterthen": "클래스 이름은 {0}자를 초과할 수 없음",
                "itemwascreated": "항목 생성됨",
                "loading": "로드 중...",
                "saving": "저장중",
                "success": "성공",
                "thisfieldisrequired": "<em>This field is required</em>",
                "warning": "경고",
                "was": "<em>was</em>",
                "wasdeleted": "삭제됨"
            },
            "strings": {
                "always": "항상",
                "ascending": "오름차순",
                "attribute": "속성",
                "currenticon": "<em>Current icon</em>",
                "default": "<em>*Default*</em>",
                "descending": "내림차순",
                "filtercql": "<em>Filter CQL</em>",
                "generalproperties": "일반 속성",
                "hidden": "숨김",
                "iconimage": "<em>Image icon</em>",
                "image": "<em>Image</em>",
                "localization": "현지화 텍스트",
                "mixed": "복합",
                "never": "<em>Never</em>",
                "properties": "속성",
                "recursive": "<em>Recursive</em>",
                "selectimage": "<em>Select image</em>",
                "selectpngfile": "<em>Select an .png file</em>",
                "string": "<em>String</em>",
                "visiblemandatory": "필수 표시",
                "visibleoptional": "선택 표시"
            },
            "tooltips": {
                "add": "<em>Add</em>",
                "clone": "복제",
                "edit": "편집",
                "edittrigger": "<em>Edit trigger</em>",
                "localize": "현지화",
                "open": "열기"
            }
        },
        "customcomponents": {
            "emptytexts": {
                "searchcustompages": "<em>Search custom components...</em>",
                "searchingrid": "<em>Search in grid...</em>"
            },
            "fieldlabels": {
                "actions": "<em>Actions</em>",
                "active": "<em>Active</em>",
                "componentid": "<em>Component ID</em>",
                "description": "<em>Description</em>",
                "name": "<em>Name</em>",
                "zipfile": "<em>ZIP file</em>"
            },
            "plural": "<em>Custom components</em>",
            "singular": "<em>Custom component</em>",
            "strings": {
                "addcontextmenu": "<em>Add context menu</em>",
                "contextmenu": "<em>Context menu</em>",
                "searchcontextmenus": "<em>Search context menus...</em>"
            },
            "texts": {
                "addcustomcomponent": "<em>Add custom component</em>",
                "selectfile": "<em>Select ZIP file</em>"
            },
            "titles": {
                "file": "<em>File</em>"
            },
            "tooltips": {
                "delete": "<em>Delete custom component</em>",
                "disable": "<em>Disable custom component</em>",
                "downloadpackage": "<em>Download custom component package</em>",
                "edit": "<em>Edit custom component</em>",
                "enable": "<em>Enable custom component</em>"
            }
        },
        "custompages": {
            "emptytexts": {
                "searchcustompages": "사용자지정 검색",
                "searchingrid": "그리드에서 검색"
            },
            "fieldlabels": {
                "actions": "액션",
                "active": "액티브",
                "componentid": "구성요소 ID",
                "description": "설명",
                "name": "이름",
                "zipfile": "압축파일"
            },
            "plural": "사용자지정 페이지",
            "singular": "사용자지정 페이지",
            "texts": {
                "addcustompage": "커스텀 페이지 추가",
                "selectfile": "압축파일 선택"
            },
            "titles": {
                "file": "파일"
            },
            "tooltips": {
                "delete": "커스텀 페이지 삭제",
                "disable": "커스텀 페이지 비활성화",
                "downloadpackage": "사용자지정 페이지 패키지 다운로드",
                "edit": "사용자지정 메뉴 편집",
                "enable": "사용자지정 페이지 활성화"
            }
        },
        "domains": {
            "domain": "도메인",
            "fieldlabels": {
                "cardinality": "개체 수(카디널리티)",
                "defaultclosed": "기본 닫힘",
                "destination": "목적지",
                "directdescription": "순방향 설명",
                "enabled": "유효",
                "inline": "일직선",
                "inversedescription": "역 방향 설명",
                "labelmasterdataillong": "라벨 마스터 세부 정보",
                "labelmasterdetail": "라벨 M/D",
                "link": "링크",
                "masterdetail": "마스터 상세",
                "masterdetailshort": "<em>M/D</em>",
                "origin": "기점",
                "viewconditioncql": "조건 보기(CQL)"
            },
            "pluralTitle": "도메인",
            "properties": {
                "form": {
                    "fieldsets": {
                        "generalData": {
                            "inputs": {
                                "description": {
                                    "label": "설명"
                                },
                                "descriptionDirect": {
                                    "label": "순방향 설명"
                                },
                                "descriptionInverse": {
                                    "label": "역 방향 설명"
                                },
                                "name": {
                                    "label": "이름"
                                }
                            }
                        }
                    }
                },
                "toolbar": {
                    "cancelBtn": "취소",
                    "deleteBtn": {
                        "tooltip": "삭제"
                    },
                    "disableBtn": {
                        "tooltip": "비활성화"
                    },
                    "editBtn": {
                        "tooltip": "편집"
                    },
                    "enableBtn": {
                        "tooltip": "유효"
                    },
                    "saveBtn": "저장"
                }
            },
            "singularTitle": "도메인",
            "texts": {
                "adddomain": "도메인 추가",
                "addlink": "링크 추가",
                "emptyText": "모든 도메인 검색",
                "enabledclasses": "유효한 클래스들",
                "properties": "속성"
            },
            "toolbar": {
                "addBtn": {
                    "text": "도메인 추가"
                },
                "searchTextInput": {
                    "emptyText": "모든 도메인 검색"
                }
            }
        },
        "emails": {
            "accounts": "계정",
            "accountsavedcorrectly": "계정이 저장됐습니다.",
            "addaccount": "<em>Add account</em>",
            "address": "주소",
            "addrow": "행 추가",
            "addtemplate": "양식 추가",
            "bcc": "숨은참조",
            "body": "본문",
            "cc": "참조",
            "clonetemplate": "복제",
            "contenttype": "콘텐츠 유형",
            "date": "날짜",
            "defaultaccount": "기본 계정",
            "delay": "지연",
            "delays": {
                "day1": "하루 이내",
                "days2": "2일 후",
                "days4": "4일 후",
                "hour1": "1시간 후",
                "hours2": "2시간 후",
                "hours4": "4시간 후",
                "month1": "1개월 후",
                "none": "<em>None</em>",
                "week1": "1주일 후",
                "weeks2": "2주일 후"
            },
            "description": "설명",
            "editvalues": "값 편집",
            "email": "이메일",
            "enablessl": "SSL 활성화",
            "enablestarttls": "STARTTLS 활성화",
            "from": "발신",
            "imapport": "IMAP포트",
            "imapserver": "IMAP서버",
            "incoming": "수신",
            "keepsync": "동기화 유지",
            "key": "키",
            "name": "이름",
            "newaccount": "신규 계정",
            "newtemplate": "신규 양식",
            "notnullkey": "하나 이상의 값에 null(무효)키가 있음",
            "outgoing": "발신",
            "password": "비밀번호",
            "promptsync": "즉시 동기화",
            "queue": "줄",
            "remove": "제거",
            "removeaccount": "계정 삭제",
            "removetemplate": "제거",
            "send": "송부",
            "sent": "보냄",
            "sentfolder": "발신 폴더",
            "setdefaultaccount": "기본 계정 설정",
            "smtpport": "SMTP포트",
            "smtpserver": "SMTP서버",
            "start": "시작",
            "subject": "주제",
            "template": "양식",
            "templates": "양식",
            "templatesavedcorrectly": "양식이 올바르게 저장됨",
            "to": "수신인",
            "username": "사용자명",
            "value": "값"
        },
        "geoattributes": {
            "fieldLabels": {
                "defzoom": "기본 줌",
                "fillcolor": "색상 채우기",
                "fillopacity": "불투명도 채우기",
                "icon": "아이콘",
                "maxzoom": "최대 줌",
                "minzoom": "최소 줌",
                "pointradius": "<em>Point radius</em>",
                "referenceclass": "참조 클래스",
                "strokecolor": "<em>Stroke color</em>",
                "strokedashstyle": "<em>Strike dashstyle</em>",
                "strokeopacity": "<em>Stroke opacity</em>",
                "strokewidth": "<em>Stroke width</em>",
                "type": "타입",
                "visibility": "표시"
            },
            "strings": {
                "specificproperty": "특정 속성"
            }
        },
        "gis": {
            "addicon": "아이콘 추가",
            "addlayer": "<em>Add layer</em>",
            "adminpassword": "관리자 비밀번호",
            "adminuser": "관리자",
            "associatedcard": "관련 카드",
            "associatedclass": "관련 클래스",
            "defaultzoom": "기본 줌",
            "deleteicon": "아이콘 삭제",
            "deleteicon_confirmation": "이 아이콘을 삭제하시겠습니까?",
            "description": "설명",
            "editicon": "아이콘 편집",
            "externalservices": "외부 서비스",
            "file": "파일",
            "geoserver": "<em>Geoserver</em>",
            "geoserverlayers": "Geo서버 레이어",
            "global": "<em>Global</em>",
            "icon": "아이콘",
            "layersorder": "레이어 순서",
            "manageicons": "아이콘 관리",
            "mapservice": "지도서비스",
            "maximumzoom": "최대 줌",
            "minimumzoom": "최소 줌",
            "newicon": "신규 아이콘",
            "ownerclass": "<em>Class</em>",
            "owneruser": "<em>User</em>",
            "searchemptytext": "<em>Search thematisms</em>",
            "servicetype": "서비스 유형",
            "thematism": "<em>Thematism</em>",
            "thematisms": "<em>Thematisms</em>",
            "type": "타입",
            "url": "<em>URL</em>",
            "workspace": "작업 영역"
        },
        "groupandpermissions": {
            "emptytexts": {
                "searchgroups": "<em>Search groups...</em>",
                "searchingrid": "그리드에서 검색",
                "searchusers": "사용자 검색"
            },
            "fieldlabels": {
                "actions": "<em>Actions</e",
                "active": "액티브",
                "attachments": "첨부",
                "datasheet": "데이터 시트",
                "defaultpage": "기본 페이지",
                "description": "설명",
                "detail": "상세",
                "email": "이메일",
                "exportcsv": "CSV파일 내보내기",
                "filters": "필터",
                "history": "이력",
                "importcsvfile": "CSV파일 불러오기",
                "massiveeditingcards": "다량의 카드 편집",
                "name": "이름",
                "note": "노트",
                "relations": "관련성",
                "type": "타입",
                "username": "사용자명"
            },
            "plural": "그룹 및 권한",
            "singular": "그룹 및 권한",
            "strings": {
                "admin": "관리자",
                "displaynousersmessage": "표시할 사용자 없음",
                "displaytotalrecords": "<em>{2} records</em>",
                "limitedadmin": "제한된 관리자",
                "normal": "보통",
                "readonlyadmin": "관리자 읽기전용"
            },
            "texts": {
                "addgroup": "<em>Add group</em>",
                "allow": "<em>Allow</em>",
                "class": "클래스",
                "columnsprivileges": "열 권한",
                "copyfrom": "복제 대상",
                "default": "기본",
                "defaultfilter": "기본 필터",
                "defaultfilters": "기본 필터",
                "defaultread": "<em>Def. + R</em>",
                "description": "설명",
                "editfilters": "{0}: {1}의 필터 편집",
                "filters": "필터",
                "group": "그룹",
                "name": "이름",
                "none": "<em>None</em>",
                "permissions": "허가",
                "read": "읽기",
                "rowsprivileges": "행 권한",
                "uiconfig": "UI 구성",
                "userslist": "사용자 리스트",
                "viewfilters": "{0}: {1}의 필터 편집",
                "write": "쓰기"
            },
            "titles": {
                "allusers": "모든 사용자",
                "disabledactions": "작업 비활성화",
                "disabledallelements": "기능 비활성화된 탐색 메뉴",
                "disabledmanagementprocesstabs": "탭 비활성화 관리 프로세스",
                "disabledutilitymenu": "기능 비활성화된 유틸리티 메뉴",
                "generalattributes": "일반 속성",
                "managementdisabledtabs": "탭 비활성화 관리 클래스",
                "usersassigned": "사용가 지정됨"
            },
            "tooltips": {
                "disabledactions": "작업 비활성화",
                "filters": "필터",
                "removedisabledactions": "비활성화된 액션 삭제",
                "removefilters": "필터 삭제"
            }
        },
        "importexport": {
            "emptyTexts": {
                "searchfield": "<em>Search all templates...</em>"
            },
            "fieldlabels": {
                "applyon": "<em>Apply on</em>",
                "classdomain": "<em>Class/Domain</em>",
                "csvseparator": "<em>CSV separator</em>",
                "datarownumber": "<em>Data row number</em>",
                "exportfilter": "<em>Export filter</em>",
                "fileformat": "<em>File Format</em>",
                "firstcolumnnumber": "<em>First column number</em>",
                "headerrownumber": "<em>Header row number</em>",
                "ignorecolumn": "<em>Ignore order</em>",
                "importkeattribute": "<em>Import key attribute</em>",
                "missingrecords": "<em>Missing records</em>",
                "type": "<em>Type</em>",
                "useheader": "<em>Use header</em>",
                "value": "<em>Value</em>"
            },
            "texts": {
                "account": "<em>Account</em>",
                "addtemplate": "<em>Add template</em>",
                "columnname": "<em>Column name</em>",
                "default": "<em>Default</em>",
                "delete": "<em>Delete</em>",
                "emptyattributegridmessage": "<em>Attributes grid can't be empty</em>",
                "erroremailtemplate": "<em>Error email template</em>",
                "errorsmanagements": "<em>Errors management</em>",
                "export": "<em>Export</em>",
                "import": "<em>Import</em>",
                "importexport": "<em>Import/Export</em>",
                "importmergecriteria": "<em>Import merge criteria</em>",
                "mode": "<em>Mode</em>",
                "modifycard": "<em>Modify card</em>",
                "nodelete": "<em>No delete</em>",
                "selectanattribute": "<em>Select an attribute</em>",
                "selectmode": "<em>Select mode*</em>",
                "templates": "<em>Templates</em>"
            }
        },
        "localizations": {
            "activeonly": "액티브만",
            "all": "모두",
            "attributeclass": "속성 클래스",
            "attributedomain": "속성 도메인",
            "attributegroup": "속성 그룹",
            "attributeprocess": "속성 프로세스",
            "attributereport": "속성 리포트",
            "cancel": "취소",
            "class": "클래스",
            "configuration": "설정",
            "csv": "<em>CSV</em>",
            "custompage": "<em>Custom page</em>",
            "dashboard": "<em>Dashboard</em>",
            "defaultlanguage": "기본 언어",
            "defaulttranslation": "<em>Default Translation</em>",
            "domain": "도메인",
            "element": "<em>Element</em>",
            "enabledLanguages": "이용 가능한 언어",
            "enabledlanguages": "유효한 언어",
            "export": "내보내기",
            "file": "파일",
            "format": "포맷",
            "import": "불러오기",
            "languageconfiguration": "언어 설정",
            "languages": "언어",
            "localization": "현지화",
            "localizations": "현지화",
            "lookup": "룩업",
            "menuitem": "메뉴 아이템",
            "pdf": "<em>PDF</em>",
            "process": "프로세스",
            "report": "보고서",
            "section": "섹션",
            "separator": "분리자",
            "showlanguagechoice": "언어 선택 표시",
            "treemenu": "<em>Tree menu</em>",
            "type": "<em>Type</em>",
            "view": "보기"
        },
        "lookuptypes": {
            "strings": {
                "addvalue": "<em>Add value</em>",
                "colorpreview": "<em>Color preview</em>",
                "font": "<em>Font</em>",
                "generalproperties": "일반 속성",
                "parentdescription": "<em>Parent description</em>",
                "textcolor": "<em>Text color</em>"
            },
            "title": "룩업 타입",
            "toolbar": {
                "addClassBtn": {
                    "text": "룩업 추가"
                },
                "classLabel": "목록",
                "printSchemaBtn": {
                    "text": "룩업 인쇄"
                },
                "searchTextInput": {
                    "emptyText": "모든 룩업 검색"
                }
            },
            "type": {
                "form": {
                    "fieldsets": {
                        "generalData": {
                            "inputs": {
                                "active": {
                                    "label": "액티브"
                                },
                                "name": {
                                    "label": "이름"
                                },
                                "parent": {
                                    "label": "부모"
                                }
                            }
                        }
                    },
                    "values": {
                        "active": "액티브"
                    }
                },
                "title": "속성",
                "toolbar": {
                    "cancelBtn": "취소",
                    "closeBtn": "닫기",
                    "deleteBtn": {
                        "tooltip": "삭제"
                    },
                    "editBtn": {
                        "tooltip": "편집"
                    },
                    "saveBtn": "저장"
                }
            }
        },
        "menus": {
            "fieldlabels": {
                "newfolder": "신규 폴더"
            },
            "main": {
                "toolbar": {
                    "cancelBtn": "취소",
                    "closeBtn": "클로스",
                    "deleteBtn": {
                        "tooltip": "삭제"
                    },
                    "editBtn": {
                        "tooltip": "편집"
                    },
                    "saveBtn": "저장"
                }
            },
            "plural": "메뉴",
            "pluralTitle": "<em>Menus</em>",
            "singular": "메뉴",
            "singularTitle": "메뉴",
            "strings": {
                "areyousuredeleteitem": "<em>Are you sure you want to delete this menu?</em>",
                "delete": "<em>Delete Menu</em>",
                "emptyfoldername": "폴더명 없음"
            },
            "texts": {
                "add": "메뉴 추가"
            },
            "toolbar": {
                "addBtn": {
                    "text": "<em>Add menu</em>"
                }
            },
            "tooltips": {
                "addfolder": "폴더 추가",
                "remove": "제거"
            }
        },
        "modClass": {
            "attributeProperties": {
                "typeProperties": "타입 속성"
            }
        },
        "navigation": {
            "bim": "<em>BIM</em>",
            "classes": "클래스들",
            "customcomponents": "<em>Custom components</em>",
            "custompages": "사용자지정 페이지",
            "dashboards": "대시보드",
            "dms": "<em>DMS</em>",
            "domains": "도메인",
            "email": "이메일",
            "generaloptions": "일반 옵션",
            "gis": "<em>GIS</em>",
            "gisnavigation": "<em>Gis Navigation</em>",
            "groupsandpermissions": "그룹 및 권한",
            "importexport": "<em>Import/export</em>",
            "importexports": "<em>Imports/Exports</em>",
            "languages": "현지화",
            "layers": "<em>Layers</em>",
            "lookuptypes": "룩업 타입",
            "menus": "메뉴",
            "multitenant": "다수 사용자",
            "navigationtrees": "네비게이션 트리",
            "processes": "프로세스",
            "reports": "보고서",
            "searchfilters": "검색 필터",
            "servermanagement": "서버 관리",
            "simples": "간단",
            "standard": "표준",
            "systemconfig": "시스템 설정",
            "taskmanager": "작업관리자",
            "title": "네비게이션",
            "users": "사용자",
            "views": "보기",
            "workflow": "워크플로우"
        },
        "navigationtrees": {
            "emptytexts": {
                "searchingrid": "그리드에서 검색",
                "searchnavigationtree": "탐색 트리 검색"
            },
            "fieldlabels": {
                "actions": "액션",
                "active": "액티브",
                "description": "설명",
                "name": "이름",
                "source": "소스",
                "zipfile": "<em>ZIP file</em>"
            },
            "plural": "네비게이션 트리",
            "singular": "네비게이션 트리",
            "strings": {
                "sourceclass": "<em>Source class</em>"
            },
            "texts": {
                "addnavigationtree": "트리 추가",
                "selectfile": "<em>Select ZIP file</em>"
            },
            "titles": {
                "file": "<em>File</em>"
            },
            "tooltips": {
                "delete": "탐색 트리 삭제",
                "disable": "탐색 트리 비활성화",
                "downloadpackage": "<em>Download report package</em>",
                "edit": "탐색 트리 편집",
                "enable": "탐색 트리 활성화",
                "viewsql": "<em>View report sql</em>"
            }
        },
        "processes": {
            "fieldlabels": {
                "applicability": "<em>Applicability</em>",
                "enginetype": "<em>Engine type</em>"
            },
            "properties": {
                "form": {
                    "fieldsets": {
                        "contextMenus": {
                            "actions": {
                                "delete": {
                                    "tooltip": "<em>Delete</em>"
                                },
                                "edit": {
                                    "tooltip": "<em>Edit</em>"
                                },
                                "moveDown": {
                                    "tooltip": "<em>Move Down</em>"
                                },
                                "moveUp": {
                                    "tooltip": "<em>Move Up</em>"
                                }
                            },
                            "inputs": {
                                "applicability": {
                                    "label": "<em>Applicability</em>",
                                    "values": {
                                        "all": {
                                            "label": "<em>All</em>"
                                        },
                                        "many": {
                                            "label": "<em>Current and selected</em>"
                                        },
                                        "one": {
                                            "label": "<em>Current</em>"
                                        }
                                    }
                                },
                                "javascriptScript": {
                                    "label": "<em>Javascript script / custom GUI Paramenters</em>"
                                },
                                "menuItemName": {
                                    "label": "<em>Menu item name</em>",
                                    "values": {
                                        "separator": {
                                            "label": "<em>[---------]</em>"
                                        }
                                    }
                                },
                                "status": {
                                    "label": "<em>Status</em>",
                                    "values": {
                                        "active": {
                                            "label": "<em>Active</em>"
                                        }
                                    }
                                },
                                "typeOrGuiCustom": {
                                    "label": "<em>Type / GUI custom</em>",
                                    "values": {
                                        "component": {
                                            "label": "<em>Custom GUI</em>"
                                        },
                                        "custom": {
                                            "label": "<em>Script Javascript</em>"
                                        },
                                        "separator": {
                                            "label": "<em></em>"
                                        }
                                    }
                                }
                            },
                            "title": "<em>Context Menus</em>"
                        },
                        "defaultOrders": "기본 정렬",
                        "generalData": {
                            "inputs": {
                                "active": {
                                    "label": "액티브"
                                },
                                "description": {
                                    "label": "설명"
                                },
                                "enableSaveButton": {
                                    "label": "숨김"
                                },
                                "name": {
                                    "label": "이름"
                                },
                                "parent": {
                                    "label": "~로부터 상속"
                                },
                                "stoppableByUser": {
                                    "label": "사용자에 의한 중단 가능"
                                },
                                "superclass": {
                                    "label": "슈퍼클래스"
                                }
                            }
                        },
                        "icon": "아이콘",
                        "processParameter": {
                            "inputs": {
                                "defaultFilter": {
                                    "label": "기본 필터"
                                },
                                "flowStatusAttr": {
                                    "label": "속성 시작"
                                },
                                "messageAttr": {
                                    "label": "메시지 속성"
                                }
                            },
                            "title": "프로세스 변수"
                        },
                        "validation": {
                            "inputs": {
                                "validationRule": {
                                    "label": "검증 규칙"
                                }
                            },
                            "title": "확인(검증)"
                        }
                    },
                    "inputs": {
                        "status": "상태"
                    },
                    "values": {
                        "active": "액티브"
                    }
                },
                "title": "속성",
                "toolbar": {
                    "cancelBtn": "취소",
                    "closeBtn": "닫기",
                    "deleteBtn": {
                        "tooltip": "삭제"
                    },
                    "disableBtn": {
                        "tooltip": "비활성화"
                    },
                    "editBtn": {
                        "tooltip": "편집"
                    },
                    "enableBtn": {
                        "tooltip": "유효"
                    },
                    "saveBtn": "저장",
                    "versionBtn": {
                        "tooltip": "버전"
                    }
                }
            },
            "strings": {
                "createnewcontextaction": "<em>Create new context action</em>",
                "engine": "<em>Engine</em>",
                "processattachments": "<em>Process Attachments</em>",
                "selectxpdlfile": "<em>Select an XPDL file</em>",
                "template": "<em>Template</em>",
                "xpdlfile": "<em>XPDL file</em>"
            },
            "texts": {
                "process": "프로세스",
                "processactivated": "<em>Process correctly activated.</em>",
                "processdeactivated": "<em>Process correctly deactivated.</em>"
            },
            "title": "프로세스",
            "toolbar": {
                "addProcessBtn": {
                    "text": "프로세스 추가"
                },
                "printSchemaBtn": {
                    "text": "스키마 인쇄"
                },
                "processLabel": "프로세스",
                "searchTextInput": {
                    "emptyText": "모든 프로세스 검색"
                }
            }
        },
        "reports": {
            "emptytexts": {
                "searchingrid": "그리드에서 검색",
                "searchreports": "보고서 검색"
            },
            "fieldlabels": {
                "actions": "액션",
                "active": "액티브",
                "description": "설명",
                "name": "이름",
                "zipfile": "압축파일"
            },
            "plural": "보고서",
            "singular": "보고서",
            "texts": {
                "addreport": "리포트 추가",
                "selectfile": "압축파일 선택"
            },
            "titles": {
                "file": "파일"
            },
            "tooltips": {
                "delete": "보고서 삭제",
                "disable": "보고서 비활성화",
                "downloadpackage": "보고서 패키지 다운로드",
                "edit": "보고서 편집",
                "enable": "보고서 활성화",
                "viewsql": "보고서 sql 보기"
            }
        },
        "searchfilters": {
            "fieldlabels": {
                "filters": "<em>Filters</em>",
                "targetclass": "<em>Target class</em>"
            },
            "texts": {
                "addfilter": "<em>Add filter</em>",
                "chooseafunction": "<em>Choose a function</em>",
                "code": "<em>Code</em>",
                "defaultforgroup": "<em>Default for groups</em>",
                "fromfilter": "<em>From filter</em>",
                "fromsql": "<em>From SQL</em>",
                "fulltext": "<em>Full text</em>",
                "fulltextquery": "<em>Full text query</em>",
                "type": "<em>Type</em>",
                "writefulltextquery": "<em>Write your full text query</em>"
            }
        },
        "systemconfig": {
            "ajaxtimeout": "<em>AJAX timeout</em>",
            "alfresco": "<em>Alfresco</em>",
            "cmis": "<em>CMIS</em>",
            "companylogo": "<em>Company logo</em>",
            "configurationmode": "<em>Configuration mode</em>",
            "dafaultjobusername": "<em>Default job username</em>",
            "defaultpage": "<em>Default page</em>",
            "disablesynconmissingvariables": "<em>Disable synchronization missing variables</em>",
            "dropcache": "<em>Drop cache</em>",
            "editmultitenantisnotallowed": "<em>Edit multitenant settings is not allowed</em>",
            "enableattachmenttoclosedactivities": "<em>Enable \"Add attachment\" to closed activities</em>",
            "frequency": "<em>Frequency (seconds)</em>",
            "generals": "<em>Generals</em>",
            "gridautorefresh": "<em>Grid autorefresh</em>",
            "hidesavebutton": "<em>Hide \"Save\" button</em>",
            "host": "<em>Host</em>",
            "initiallatitude": "<em>Initial latitude</em>",
            "initialongitude": "<em>Initial longitude</em>",
            "initialzoom": "<em>Initial zoom</em>",
            "instancename": "<em>Instance name</em>",
            "lockmanagement": "<em>Lock management</em>",
            "logo": "<em>Logo</em>",
            "maxlocktime": "<em>Maximum lock time (seconds)</em>",
            "multitenantactivationmessage": "<em>Changing these settings is irreversible, unless the database is restored. It is recommended to backup the database before proceeding.</em>",
            "multitenantapllychangesquest": "<em>Do you want to apply the changes?</em>",
            "multitenantinfomessage": "<em>It is recommended to change these settings only after having consulted the guidelines present in the Administrator Manual, downloadable from {0}</em>",
            "noteinline": "<em>Inline notes</em>",
            "noteinlinedefaultclosed": "<em>Inline notes default closed</em>",
            "postgres": "<em>Posgres</em>",
            "preferredofficesuite": "<em>Preferred Office suite</em>",
            "preset": "<em>Preset</em>",
            "referencecombolimit": "<em>Reference combobox limit</em>",
            "relationlimit": "<em>Relation Limit</em>",
            "serviceurl": "<em>Service URL</em>",
            "sessiontimeout": "<em>Session timeout</em>",
            "shark": "<em>Enhydra Shark</em>",
            "showcardlockerusername": "<em>Shows the name of the user who blocked the card</em>",
            "synkservices": "<em>Synchronize services</em>",
            "tecnotecariver": "<em>Tecnoteca River</em>",
            "unlockallcards": "<em>Unlock all cards</em>",
            "url": "<em>Url</em>",
            "usercandisable": "<em>User can disable</em>",
            "webservicepath": "<em>Webservice path</em>"
        },
        "tasks": {
            "account": "<em>Account</em>",
            "addtask": "<em>Add task</em>",
            "advanceworkflow": "<em>Advance workflow</em>",
            "bodyparsing": "<em>Body parsing</em>",
            "category": "<em>Category</em>",
            "cron": "<em>Cron</em>",
            "day": "<em>Day</em>",
            "dayofweek": "<em>Day of week</em>",
            "directory": "<em>Directory</em>",
            "emailtemplate": "<em>Email template</em>",
            "emptytexts": {
                "searchcustompages": "작업 검색",
                "searchingrid": "그리드에서 검색"
            },
            "erroremailtemplate": "<em>Error email template</em>",
            "fieldlabels": {
                "account": "계정",
                "actions": "액션",
                "active": "액티브",
                "code": "코드",
                "filter": "필터",
                "filtertype": "필터 유형",
                "incomingfolder": "수신 폴더",
                "processedfolder": "처리된 폴더",
                "rejectedfolder": "거부된 폴더",
                "sender": "발신자",
                "startonsave": "저장상태에서 시작",
                "subject": "주제"
            },
            "filename": "<em>File name</em>",
            "filepattern": "<em>File pattern</em>",
            "filtertype": "<em>Filter type</em>",
            "hour": "<em>Hour</em>",
            "incomingfolder": "<em>Incoming folder</em>",
            "jobusername": "<em>Job username</em>",
            "keyenddelimiter": "<em>Key end delimiter</em>",
            "keystartdelimiter": "<em>Key start delimiter</em>",
            "minutes": "<em>Minutes</em>",
            "month": "<em>Month</em>",
            "movereject": "<em>Move rejected not matching</em>",
            "notificationmode": "<em>Notification mode</em>",
            "notifications": "<em>Notifications</em>",
            "parsing": "<em>Parsing</em>",
            "plural": "작업",
            "postimportaction": "<em>Post import action</em>",
            "processattributes": "<em>Process attributes</em>",
            "processedfolder": "<em>Processed folder</em>",
            "rejectedfolder": "<em>Rejected folder</em>",
            "saveattachments": "<em>Save Attachments</em>",
            "saveattachmentsdms": "<em>Save attachemnts to DMS</em>",
            "sender": "<em>Sender</em>",
            "sendnotiifcation": "<em>Send notification email</em>",
            "settings": "<em>Settings</em>",
            "sincurrentStepgular": "<em>Task</em>",
            "singular": "작업",
            "source": "<em>Source</em>",
            "startprocess": "<em>Start process</em>",
            "strings": {
                "advanced": "<em>Advanced</em>"
            },
            "subject": "<em>Subject</em>",
            "template": "<em>Template</em>",
            "texts": {
                "addtask": "작업 추가",
                "asyncronousevents": "비동기 이벤트",
                "reademails": "이메일 읽기",
                "sendemails": "이메일 송부",
                "startprocesses": "프로세스 시작",
                "syncronousevents": "이벤트 동기화",
                "wizardconnectors": "마법사 커넥터"
            },
            "tooltips": {
                "cyclicexecution": "반복 실행",
                "delete": "작업 삭제",
                "disable": "작업 비활성화",
                "edit": "작업 편집",
                "enable": "작업 활성화",
                "execution": "<em>Execution</em>",
                "singleexecution": "단일실행",
                "start": "<em>Start</em>",
                "started": "<em>Started</em>",
                "stop": "멈춤",
                "stopped": "<em>Stopped</em>"
            },
            "type": "<em>Type</em>",
            "url": "<em>URL</em>",
            "value": "<em>Value</em>",
            "valueenddelimiter": "<em>Value end delimiter</em>",
            "valuestartdelimiter": "<em>Value start delimiter</em>"
        },
        "tesks": {
            "labels": {
                "activeonsave": "저장시 활성화",
                "emailaccount": "이메일 계정",
                "filtertype": "<em>Filter type</em>",
                "incomingfolder": "수신 폴더"
            }
        },
        "title": "관리",
        "users": {
            "fieldLabels": {
                "confirmpassword": "<em>Confirm Password</em>",
                "defaultgroup": "<em>Default group</em>",
                "defaulttenant": "<em>Default tenant</em>",
                "groups": "<em>Groups</em>",
                "initialpage": "<em>Initial page</em>",
                "language": "<em>Language</em>",
                "multigroup": "<em>Multigroup</em>",
                "multitenant": "<em>Multitenant</em>",
                "multitenantactivationprivileges": "<em>Allow multitenant</em>",
                "nodata": "<em>No data</em>",
                "privileged": "<em>Priviliged</em>",
                "service": "<em>Service</em>",
                "tenant": "<em>Tenant</em>",
                "tenants": "<em>Tenants</em>",
                "user": "<em>User</em>"
            },
            "properties": {
                "form": {
                    "fieldsets": {
                        "generalData": {
                            "inputs": {
                                "active": {
                                    "label": "액티브"
                                },
                                "description": {
                                    "label": "설명"
                                },
                                "name": {
                                    "label": "이름"
                                },
                                "stoppableByUser": {
                                    "label": "사용자에 의한 중단 가능"
                                }
                            }
                        },
                        "icon": "아이콘"
                    }
                }
            },
            "title": "사용자",
            "toolbar": {
                "addUserBtn": {
                    "text": "사용자 추가"
                },
                "searchTextInput": {
                    "emptyText": "모든 사용자 검색"
                }
            }
        },
        "viewfilters": {
            "emptytexts": {
                "searchingrid": "<em>Search ...</em>"
            },
            "texts": {
                "addfilter": "<em>Add filter</em>",
                "filterforgroup": "그룹용 필터"
            }
        },
        "views": {
            "addfilter": "<em>Add filter</em>",
            "addview": "<em>Add view</em>",
            "ragetclass": "<em>Target class</em>",
            "ralations": "<em>Relations</em>",
            "targetclass": "<em>Target class</em>"
        }
    }
});
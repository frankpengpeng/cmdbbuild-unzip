(function() {
    Ext.define('CMDBuildUI.locales.ja.Locales', {
        "requires": ["CMDBuildUI.locales.ja.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "ja",
        "administration": CMDBuildUI.locales.ja.LocalesAdministration.administration,
        "attachments": {
            "add": "添付追加",
            "attachmenthistory": "添付履歴",
            "author": "作成者",
            "category": "カテゴリー",
            "creationdate": "作成日",
            "deleteattachment": "添付削除",
            "deleteattachment_confirmation": "本当にこの添付を削除しますか?",
            "description": "説明",
            "download": "ダウンロード",
            "editattachment": "添付変更",
            "file": "ファイル",
            "filename": "ファイル名",
            "majorversion": "メジャーバージョン",
            "modificationdate": "更新日",
            "uploadfile": "アップロード中...",
            "version": "バージョン",
            "viewhistory": "添付履歴表示"
        },
        "bim": {
            "bimViewer": "BIMビューワー",
            "card": {
                "label": "カード"
            },
            "layers": {
                "label": "レイヤー",
                "menu": {
                    "hideAll": "全て隠す",
                    "showAll": "全て表示"
                },
                "name": "名称",
                "qt": "数",
                "visibility": "表示",
                "visivility": "表示"
            },
            "menu": {
                "camera": "カメラ",
                "frontView": "フロントビュー",
                "mod": "ビューワーコントロール",
                "orthographic": "正射",
                "pan": "スクロール",
                "perspective": "投射",
                "resetView": "リセットビュー",
                "rotate": "回転",
                "sideView": "サイドビュー",
                "topView": "トップビュー"
            },
            "showBimCard": "BIM 3D表示",
            "tree": {
                "arrowTooltip": "選択",
                "columnLabel": "ツリー",
                "label": "ツリー",
                "open_card": "関連するカードを開く",
                "root": "IFCルート"
            }
        },
        "classes": {
            "cards": {
                "addcard": "カード追加",
                "clone": "複製",
                "clonewithrelations": "カードとリレーションを複製",
                "deletecard": "カード削除",
                "deleteconfirmation": "本当にこのカードを削除しますか?",
                "label": "カード",
                "modifycard": "カード更新",
                "opencard": "カード表示",
                "print": "カード印刷"
            },
            "simple": "シンプルクラス",
            "standard": "標準クラス"
        },
        "common": {
            "actions": {
                "add": "追加",
                "apply": "適用",
                "cancel": "キャンセル",
                "close": "閉じる",
                "delete": "削除",
                "edit": "編集",
                "execute": "実行",
                "refresh": "リフレッシュ",
                "remove": "削除",
                "save": "保存",
                "saveandapply": "保存",
                "saveandclose": "保存して閉じる",
                "search": "検索",
                "searchtext": "検索中..."
            },
            "attributes": {
                "nogroup": "基本データ"
            },
            "dates": {
                "date": "Y/m/d",
                "datetime": "Y/m/d H:i:s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "HTMLクリア"
            },
            "grid": {
                "disablemultiselection": "複数選択無効",
                "enamblemultiselection": "複数選択有効",
                "export": "エキスポート",
                "filterremoved": "フィルターは削除されました",
                "import": "インポート",
                "itemnotfound": "アイテムが見つかりません",
                "list": "リスト",
                "opencontextualmenu": "コンテキストメニュー",
                "print": "印刷",
                "printcsv": "CSV出力",
                "printodt": "ODT出力",
                "printpdf": "PDF出力",
                "row": "アイテム",
                "rows": "アイテム",
                "subtype": "サブタイプ"
            },
            "tabs": {
                "activity": "アクティビティ",
                "attachments": "添付",
                "card": "カード",
                "details": "詳細",
                "emails": "Eメール",
                "history": "履歴",
                "notes": "ノート",
                "relations": "リレーション"
            }
        },
        "emails": {
            "addattachmentsfromdms": "DMSから添付追加",
            "alredyexistfile": "この名前のファイルは既に存在します",
            "archivingdate": "アーカイブ日",
            "attachfile": "ファイルを添付",
            "bcc": "Bcc",
            "cc": "cc",
            "composeemail": "メール作成",
            "composefromtemplate": "テンプレートから作成",
            "delay": "後で送信",
            "delays": {
                "day1": "1日後",
                "days2": "2日後",
                "days4": "4日後",
                "hour1": "1時間後",
                "hours2": "２時間後",
                "hours4": "4時間後",
                "month1": "1カ月後",
                "none": "なし",
                "week1": "1週間後",
                "weeks2": "2週間後"
            },
            "dmspaneltitle": "データベースから添付選択",
            "edit": "編集",
            "from": "差出人",
            "gridrefresh": "リフレッシュ",
            "keepsynchronization": "同期を維持",
            "message": "本文",
            "regenerateallemails": "全メールを再作成",
            "regenerateemail": "メール再作成",
            "remove": "削除",
            "remove_confirmation": "本当にこのメールを削除しますか?",
            "reply": "返信",
            "replyprefix": "{0}, {1}が書きました:",
            "selectaclass": "クラス選択",
            "sendemail": "メール送信",
            "statuses": {
                "draft": "下書き",
                "outgoing": "送信中",
                "received": "受信",
                "sent": "送信"
            },
            "subject": "件名",
            "to": "宛先",
            "view": "ビュー"
        },
        "errors": {
            "autherror": "ユーザー名またはパスワードに誤りがあります",
            "classnotfound": "クラス{0} は見つかりません",
            "notfound": "アイテムが見つかりません"
        },
        "filters": {
            "actions": "アクション",
            "addfilter": "フィルタ追加",
            "any": "すべての",
            "attribute": "属性選択",
            "attributes": "属性",
            "clearfilter": "フィルタークリア",
            "clone": "複製",
            "copyof": "コピー",
            "description": "説明",
            "domain": "ドメイン",
            "filterdata": "フィルター",
            "fromselection": "選択から",
            "ignore": "除外",
            "migrate": "移行",
            "name": "名称",
            "newfilter": "新規フィルター",
            "noone": "何もない",
            "operator": "演算子",
            "operators": {
                "beginswith": "開始する",
                "between": "間",
                "contained": "含む",
                "containedorequal": "含むまたは同じ",
                "contains": "含む",
                "containsorequal": "含むまたは同じ",
                "different": "異なる",
                "doesnotbeginwith": "開始しない",
                "doesnotcontain": "含まない",
                "doesnotendwith": "終了しない",
                "endswith": "終了する",
                "equals": "同じ",
                "greaterthan": "大きい",
                "isnotnull": "null値でない",
                "isnull": "null値",
                "lessthan": "小さい"
            },
            "relations": "リレーション",
            "type": "タイプ",
            "typeinput": "入力パラメータ",
            "value": "値"
        },
        "gis": {
            "card": "カード",
            "cardsMenu": "メニュー",
            "externalServices": "外部サービス",
            "geographicalAttributes": "地理属性",
            "geoserverLayers": "Geoサーバレイヤー",
            "layers": "レイヤー",
            "list": "リスト",
            "map": "マップ",
            "mapServices": "地図サービス",
            "position": "位置",
            "root": "ルート",
            "tree": "ナビゲーションツリー",
            "view": "ビュー",
            "zoom": "ズーム"
        },
        "history": {
            "activityname": "アクティビティ名",
            "activityperformer": "アクティビティの実施者",
            "begindate": "開始日",
            "enddate": "終了日",
            "processstatus": "ステータス",
            "user": "ユーザー"
        },
        "importexport": {
            "downloadreport": "ダウンロードレポート",
            "emailfailure": "送信エラー",
            "emailsubject": "インポートレポート",
            "emailsuccess": "送信されました",
            "export": "エキスポート",
            "import": "インポート",
            "importresponse": "インポート",
            "response": {
                "created": "作成レコード",
                "deleted": "削除レコード",
                "errors": "エラー",
                "linenumber": "行番号",
                "message": "メッセージ",
                "modified": "更新レコード",
                "processed": "処理行数",
                "recordnumber": "レコード数",
                "unmodified": "未更新レコード"
            },
            "sendreport": "レポート送信",
            "template": "テンプレート",
            "templatedefinition": "<em>Template definition</em>"
        },
        "login": {
            "buttons": {
                "login": "ログイン",
                "logout": "ユーザー変更"
            },
            "fields": {
                "group": "グループ",
                "language": "言語",
                "password": "パスワード",
                "tenants": "テナント",
                "username": "ユーザー名"
            },
            "loggedin": "ログイン",
            "title": "ログイン",
            "welcome": "おかえりなさい {0}"
        },
        "main": {
            "administrationmodule": "管理モジュール",
            "baseconfiguration": "基本設定",
            "cardlock": {
                "lockedmessage": "このカードは{0}が編集しているため、編集できません。",
                "someone": "誰か"
            },
            "changegroup": "グループ変更",
            "changepassword": "パスワード変更",
            "changetenant": "テナント変更",
            "confirmchangegroup": "本当にこのグループを変更しますか?",
            "confirmchangetenants": "本当に有効なテナントを変更しますか?",
            "confirmdisabletenant": "本当に\"テナント無視\"フラグを無効にしますか?",
            "confirmenabletenant": "本当に\"テナント無視\"フラグを有効にしますか??</em>",
            "confirmpassword": "パスワード確認",
            "ignoretenants": "テナント無視",
            "info": "情報",
            "logo": {
                "cmdbuild": "CMDBuild ロゴ",
                "cmdbuildready2use": "CMDBuild READY2USE ロゴ",
                "companylogo": "ユーザーロゴ",
                "openmaint": "openMAINT ロゴ"
            },
            "logout": "ログアウト",
            "managementmodule": "データモジュール",
            "multigroup": "マルチグループ",
            "multitenant": "マルチテナント",
            "navigation": "ナビゲーション",
            "newpassword": "新パスワード",
            "oldpassword": "旧パスワード",
            "pagenotfound": "ページがありません",
            "pleasecorrecterrors": "エラーを修正してください",
            "preferences": {
                "comma": "カンマ",
                "decimalserror": "小数点の項目が必要です",
                "decimalstousandserror": "小数点と桁区切りは異なる必要があります",
                "default": "デフォルト",
                "defaultvalue": "デフォルト",
                "labeldateformat": "日付表示",
                "labeldecimalsseparator": "小数点記号",
                "labelintegerformat": "整数表示",
                "labellanguage": "言語",
                "labelnumericformat": "数値表示",
                "labelthousandsseparator": "桁区切り記号",
                "labeltimeformat": "時刻表示",
                "msoffice": "Microsoft Office",
                "period": "ピリオド",
                "preferredofficesuite": "使用Officeプログラム",
                "space": "スペース",
                "thousandserror": "整数の項目が必要です",
                "timezone": "タイムゾーン",
                "twelvehourformat": "12時間表示",
                "twentyfourhourformat": "24時間表示"
            },
            "searchinallitems": "全アイテム検索",
            "userpreferences": "表示設定"
        },
        "menu": {
            "allitems": "全アイテム",
            "classes": "クラス",
            "custompages": "カスタムページ",
            "dashboards": "ダッシュボード",
            "processes": "プロセス",
            "reports": "レポート",
            "views": "ビュー"
        },
        "notes": {
            "edit": "ノート編集"
        },
        "notifier": {
            "attention": "注意",
            "error": "エラー",
            "genericerror": "エラー",
            "genericinfo": "一般的情報",
            "genericwarning": "一般的警告",
            "info": "情報",
            "success": "成功",
            "warning": "警告"
        },
        "patches": {
            "apply": "パッチ適用",
            "category": "カテゴリー",
            "description": "説明",
            "name": "名称",
            "patches": "パッチ"
        },
        "processes": {
            "abortconfirmation": "本当にこのプロセスを中止しますか?",
            "abortprocess": "プロセスを中止",
            "action": {
                "advance": "次へ",
                "label": "アクション"
            },
            "activeprocesses": "有効プロセス",
            "allstatuses": "全て",
            "editactivity": "アクティビティ変更",
            "openactivity": "アクティビティ表示",
            "startworkflow": "開始",
            "workflow": "ワークフロー"
        },
        "relationGraph": {
            "activity": "アクティビティ",
            "card": "カード",
            "cardList": "カード一覧",
            "cardRelation": "リレーション",
            "cardRelations": "リレーション",
            "choosenaviagationtree": "ナビゲーション選択",
            "class": "クラス",
            "class:": "クラス",
            "classList": "クラス一覧",
            "compoundnode": "複合ノード",
            "enableTooltips": "ツールチップ表示・非表示",
            "level": "レベル",
            "openRelationGraph": "リレーショングラフを開く",
            "qt": "数",
            "refresh": "リフレッシュ",
            "relation": "リレーション",
            "relationGraph": "リレーショングラフ",
            "reopengraph": "このノードから再表示"
        },
        "relations": {
            "adddetail": "詳細追加",
            "addrelations": "リレーション追加",
            "attributes": "属性",
            "code": "コード",
            "deletedetail": "詳細削除",
            "deleterelation": "リレーション削除",
            "description": "説明",
            "editcard": "カード編集",
            "editdetail": "詳細編集",
            "editrelation": "リレーション編集",
            "mditems": "アイテム",
            "opencard": "関連するカードを開く",
            "opendetail": "詳細表示",
            "type": "タイプ"
        },
        "reports": {
            "csv": "CSV",
            "download": "ダウンロード",
            "format": "フォーマット",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "印刷",
            "reload": "再ロード",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "<em>Add Thematism</em>",
            "analysisType": "表示タイプ",
            "attribute": "属性",
            "calculateRules": "<em>Generate style rules</em>",
            "clearThematism": "<em>Clear Thematism</em>",
            "color": "色",
            "defineLegend": "<em>Legend definition</em>",
            "defineThematism": "<em>Thematism definition</em>",
            "function": "ファンクション",
            "generate": "作成",
            "geoAttribute": "<em>geoAttribute</em>",
            "graduated": "段階",
            "highlightSelected": "ハイライト",
            "intervals": "間隔",
            "legend": "凡例",
            "name": "<em>name</em>",
            "newThematism": "<em>New Thematism</em>",
            "punctual": "点",
            "quantity": "数",
            "source": "ソース",
            "table": "テーブル",
            "thematism": "テーマ",
            "value": "値"
        },
        "widgets": {
            "customform": {
                "addrow": "行追加",
                "clonerow": "行を複製",
                "deleterow": "行削除",
                "editrow": "行編集",
                "export": "エキスポート",
                "import": "インポート",
                "refresh": "初期設定に戻す"
            },
            "linkcards": {
                "editcard": "カード編集",
                "opencard": "カード表示",
                "refreshselection": "デフォルトを適用",
                "togglefilterdisabled": "フィルター無効化",
                "togglefilterenabled": "フィルター有効化"
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
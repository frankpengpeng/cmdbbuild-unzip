(function() {
    Ext.define('CMDBuildUI.locales.pt_BR.Locales', {
        "requires": ["CMDBuildUI.locales.pt_BR.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "pt_BR",
        "administration": CMDBuildUI.locales.pt_BR.LocalesAdministration.administration,
        "attachments": {
            "add": "Adicionar anexo",
            "attachmenthistory": "Histórico de anexos",
            "author": "Autor",
            "category": "Categoria",
            "creationdate": "Data de criação",
            "deleteattachment": "Apagar Anexo",
            "deleteattachment_confirmation": "Tem a certeza que deseja apagar este anexo?",
            "description": "Descrição",
            "download": "Descarregar",
            "editattachment": "Editar anexos",
            "file": "Ficheiro",
            "filename": "Nome do ficheiro",
            "majorversion": "Versão principal",
            "modificationdate": "Data de modificação",
            "uploadfile": "Transferir ficheiro",
            "version": "Versão",
            "viewhistory": "Visualizar histórico de anexos"
        },
        "bim": {
            "bimViewer": "Visualizador BIM",
            "card": {
                "label": "Cartão"
            },
            "layers": {
                "label": "Camadas",
                "menu": {
                    "hideAll": "Esconder tudo",
                    "showAll": "Mostrar tudo"
                },
                "name": "Nome",
                "qt": "Qt",
                "visibility": "Visibilidade",
                "visivility": "Visibilidade"
            },
            "menu": {
                "camera": "Câmara",
                "frontView": "Vista Frontal",
                "mod": "Mod (controlos de visualização)",
                "orthographic": "<em>Orthographic Camera</em>",
                "pan": "Deslocar",
                "perspective": "<em>Perspective Camera</em>",
                "resetView": "Restabelecer vista",
                "rotate": "Rodar",
                "sideView": "Vista lateral",
                "topView": "Vista de Topo"
            },
            "showBimCard": "Abrir visualizador 3D",
            "tree": {
                "arrowTooltip": "Selecionar elemento",
                "columnLabel": "Árvore",
                "label": "Árvore",
                "open_card": "Abrir cartão relacionado",
                "root": "<em>Ifc Root</em>"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Adicionar cartão",
                "clone": "Clonar",
                "clonewithrelations": "Clonar cartão e relações",
                "deletecard": "Apagar cartão",
                "deleteconfirmation": "Tem a certeza que deseja apagar este cartão?",
                "label": "Cartões",
                "modifycard": "Editar cartão",
                "opencard": "Abrir cartão",
                "print": "Imprimir cartão"
            },
            "simple": "Simples",
            "standard": "Standard"
        },
        "common": {
            "actions": {
                "add": "Adicionar",
                "apply": "Aplicar",
                "cancel": "Cancelar",
                "close": "Fechar",
                "delete": "Apagar",
                "edit": "Editar",
                "execute": "Executar",
                "refresh": "Atualizar dados",
                "remove": "Apagar",
                "save": "Gravar",
                "saveandapply": "Guardar e aplicar",
                "saveandclose": "Guardar e fechar",
                "search": "Pesquisar",
                "searchtext": "Pesquisar…."
            },
            "attributes": {
                "nogroup": "Dados base"
            },
            "dates": {
                "date": "dd/mm/yyyy",
                "datetime": "d/m/Y H:i:s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "Limpar HTML"
            },
            "grid": {
                "disablemultiselection": "Desativar seleção múltipla",
                "enamblemultiselection": "Ativar seleção múltipla",
                "export": "<em>Export data</em>",
                "filterremoved": "O filtro atual foi removido",
                "import": "<em>Import data</em>",
                "itemnotfound": "Item não localizado",
                "list": "Lista",
                "opencontextualmenu": "Abrir menu de contexto",
                "print": "Imprimir",
                "printcsv": "Imprimir como CSV",
                "printodt": "Imprimir como ODT",
                "printpdf": "Imprimir como PDF",
                "row": "Item",
                "rows": "Itens",
                "subtype": "Sub Tipo"
            },
            "tabs": {
                "activity": "Atividade",
                "attachments": "Anexos",
                "card": "Cartão",
                "details": "Detalhes",
                "emails": "Emails",
                "history": "Histórico",
                "notes": "Notas",
                "relations": "Relações"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Adicionar anexos do SGD",
            "alredyexistfile": "<em>Already exists a file with this name</em>",
            "archivingdate": "Data de arquivo",
            "attachfile": "Anexar ficheiro",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "Compor e-mail",
            "composefromtemplate": "Compor a partir de modelo",
            "delay": "Atraso",
            "delays": {
                "day1": "Em 1 dia",
                "days2": "Em 2 dias",
                "days4": "Em 4 dias",
                "hour1": "1 hora",
                "hours2": "2 horas",
                "hours4": "4 horas",
                "month1": "Em 1 mês",
                "none": "Nenhum",
                "week1": "Em 1 semana",
                "weeks2": "Em 2 semanas"
            },
            "dmspaneltitle": "Selecionar anexos via base de dados",
            "edit": "Editar",
            "from": "De",
            "gridrefresh": "Atualizar grelha",
            "keepsynchronization": "Manter sincronizado",
            "message": "Mensagem",
            "regenerateallemails": "Recriar todos os e-mails",
            "regenerateemail": "Recriar e-mail",
            "remove": "Apagar",
            "remove_confirmation": "Tem a certeza que deseja apagar este email?",
            "reply": "Responder",
            "replyprefix": "{1} wrote:",
            "selectaclass": "<em>Select a class</em>",
            "sendemail": "Enviar email",
            "statuses": {
                "draft": "Rascunho",
                "outgoing": "A Enviar",
                "received": "Recebido",
                "sent": "Enviado"
            },
            "subject": "Assunto",
            "to": "Para",
            "view": "Vista"
        },
        "errors": {
            "autherror": "Utilizador ou password incorretos",
            "classnotfound": "Classe {0} não localizada",
            "notfound": "Item não localizado"
        },
        "filters": {
            "actions": "Ações",
            "addfilter": "Adicionar filtro",
            "any": "Qualquer",
            "attribute": "Escolher um atributo",
            "attributes": "Atributos",
            "clearfilter": "Limpar filtro",
            "clone": "Clonar",
            "copyof": "Cópia de",
            "description": "Descrição",
            "domain": "Domínio",
            "filterdata": "Filtrar data",
            "fromselection": "A partir da seleção",
            "ignore": "Ignorar",
            "migrate": "Migrar",
            "name": "Nome",
            "newfilter": "Novo filtro",
            "noone": "Ninguém",
            "operator": "Operador",
            "operators": {
                "beginswith": "Inicia com",
                "between": "Entre",
                "contained": "Contido",
                "containedorequal": "Contido ou igual",
                "contains": "Contêm",
                "containsorequal": "Contêm ou igual",
                "different": "Diferente",
                "doesnotbeginwith": "Não inicia com",
                "doesnotcontain": "Não contém",
                "doesnotendwith": "Não finaliza com",
                "endswith": "Finaliza com",
                "equals": "É igual a",
                "greaterthan": "Maior que",
                "isnotnull": "Não é nulo",
                "isnull": "É nulo",
                "lessthan": "Menor do que"
            },
            "relations": "Relações",
            "type": "Tipo",
            "typeinput": "Parâmetro de entrada",
            "value": "Valor"
        },
        "gis": {
            "card": "Cartão",
            "cardsMenu": "<em>Cards Menu</em>",
            "externalServices": "Serviços externos",
            "geographicalAttributes": "Atributos Geo",
            "geoserverLayers": "Camadas Geoserver",
            "layers": "Camadas",
            "list": "Lista",
            "map": "Mapa",
            "mapServices": "Serviços de mapa",
            "position": "<em>Position</em>",
            "postition": "Posição",
            "root": "Raiz",
            "tree": "Árvore",
            "view": "Vista",
            "zoom": "Ampliação"
        },
        "history": {
            "activityname": "Nome da atividade",
            "activityperformer": "Executante da atividade",
            "begindate": "Data de início",
            "enddate": "Data de fim",
            "processstatus": "Estado",
            "user": "Utilizador"
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
                "login": "Autenticar",
                "logout": "Alterar utilizador"
            },
            "fields": {
                "group": "Grupo",
                "language": "Idioma",
                "password": "Password",
                "tenants": "Tenants",
                "username": "Nome de utilizador"
            },
            "loggedin": "Autenticado",
            "title": "Autenticar",
            "welcome": "Bem vindo de volta"
        },
        "main": {
            "administrationmodule": "Módulo de Administração",
            "baseconfiguration": "<em>Base configuration</em>",
            "cardlock": {
                "lockedmessage": "Não é possível editar este cartão pois está a ser editado por {0}",
                "someone": "Alguém"
            },
            "changegroup": "Alterar grupo",
            "changepassword": "Alterar password",
            "changetenant": "Alterar tenant",
            "confirmchangegroup": "Tem a certeza que deseja alterar o grupo?",
            "confirmchangetenants": "Tem a certeza que deseja alterar os tenants ativos?",
            "confirmdisabletenant": "Tem a certeza que deseja desabilitar a flag \"ignorar tenants\"?",
            "confirmenabletenant": "Tem a certeza que deseja habilitar a flag \"ignorar tenants\"?",
            "confirmpassword": "Confirmar password",
            "ignoretenants": "Ignorar tenants",
            "info": "Informação",
            "logo": {
                "cmdbuild": "Logótipo CMDBBuild",
                "cmdbuildready2use": "Logótipo CMDBBuild READY2USE",
                "companylogo": "<em>Company logo</em>",
                "openmaint": "logótipo openMAINT"
            },
            "logout": "Sair",
            "managementmodule": "Módulo de Gestão de Dados",
            "multigroup": "Multi grupo",
            "multitenant": "Multi tenant",
            "navigation": "Navegação",
            "newpassword": "Nova password",
            "oldpassword": "Password Antiga",
            "pagenotfound": "<em>Page not found</em>",
            "pleasecorrecterrors": "<em>Please correct indicated errors!</em>",
            "preferences": {
                "comma": "Vírgula",
                "decimalserror": "O campo de decimais tem de estar presente",
                "decimalstousandserror": "Os separadores de decimais e milhares tem de ser diferentes",
                "default": "<em>Default</em>",
                "defaultvalue": "Valor Padrão",
                "labeldateformat": "Formato da data",
                "labeldecimalsseparator": "Separador de decimais",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "Idioma",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "Separador de milhares",
                "labeltimeformat": "formato de tempo",
                "msoffice": "<em>Microsoft Office</em>",
                "period": "Período",
                "preferredofficesuite": "<em>Preferred Office suite</em>",
                "space": "Espaço",
                "thousandserror": "O campo de milhares tem de estar presente",
                "timezone": "<em>Timezone</em>",
                "twelvehourformat": "Formato 12 horas",
                "twentyfourhourformat": "Formato 24 horas"
            },
            "searchinallitems": "Pesquisar em todos os itens",
            "userpreferences": "Preferências"
        },
        "menu": {
            "allitems": "Todos os itens",
            "classes": "Classes",
            "custompages": "Páginas personalizadas",
            "dashboards": "Dashboards",
            "processes": "Processos",
            "reports": "Relatórios",
            "views": "Visualizações"
        },
        "notes": {
            "edit": "Editar nota"
        },
        "notifier": {
            "attention": "Atenção",
            "error": "Erro",
            "genericerror": "Erro Genérico",
            "genericinfo": "Informação Genérica",
            "genericwarning": "Aviso Genérico",
            "info": "Informação",
            "success": "Sucesso",
            "warning": "Aviso"
        },
        "patches": {
            "apply": "<em>Apply patches</em>",
            "category": "<em>Category</em>",
            "description": "<em>Description</em>",
            "name": "<em>Name</em>",
            "patches": "<em>Patches</em>"
        },
        "processes": {
            "abortconfirmation": "Tem a certeza que deseja abortar este processo?",
            "abortprocess": "Abortar processo",
            "action": {
                "advance": "Avançar",
                "label": "Ação"
            },
            "activeprocesses": "Processos ativos",
            "allstatuses": "Todos",
            "editactivity": "Editar atividade",
            "openactivity": "Abrir atividade",
            "startworkflow": "Iniciar",
            "workflow": "Fluxo de trabalho"
        },
        "relationGraph": {
            "activity": "<em>activity</em>",
            "card": "Cartão",
            "cardList": "Lista de cartões",
            "cardRelation": "<em>Relation</em>",
            "cardRelations": "Relação entre Cartões",
            "choosenaviagationtree": "Alterar árvore de navegação",
            "class": "Classe",
            "class:": "<em>Class</em>",
            "classList": "Lista de Classes",
            "compoundnode": "<em>Compound Node</em>",
            "enableTooltips": "Ativar / Desativar legenda no gráfico",
            "level": "Nível",
            "openRelationGraph": "Abrir gráfico de relacionamentos",
            "qt": "Qt",
            "refresh": "Atualizar",
            "relation": "Relação",
            "relationGraph": "Gráfico de relacionamento",
            "reopengraph": "Reabrir o gráfico deste nó"
        },
        "relations": {
            "adddetail": "Adicionar detalhe",
            "addrelations": "Adicionar relacionamentos",
            "attributes": "Atributos",
            "code": "Código",
            "deletedetail": "Apagar detalhe",
            "deleterelation": "Apagar relacionamento",
            "description": "Descrição",
            "editcard": "Editar cartão",
            "editdetail": "Editar Detalhe",
            "editrelation": "Editar relacionamento",
            "mditems": "Itens",
            "opencard": "Abrir cartão relacionado",
            "opendetail": "mostrar detalhe",
            "type": "Tipo"
        },
        "reports": {
            "csv": "CSV",
            "download": "Descarregar",
            "format": "Formato",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Imprimir",
            "reload": "Recarregar",
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
                "addrow": "Adicionar linha",
                "clonerow": "Clonar linha",
                "deleterow": "Apagar linha",
                "editrow": "Editar linha",
                "export": "Exportar",
                "import": "Importar",
                "refresh": "Atualizar para dados padrão"
            },
            "linkcards": {
                "editcard": "Editar cartão",
                "opencard": "Abrir cartão",
                "refreshselection": "Aplicar seleção padrão",
                "togglefilterdisabled": "Ativar filtro de grelha",
                "togglefilterenabled": "Ativar filtro de grelha"
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
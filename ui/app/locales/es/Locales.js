(function() {
    Ext.define('CMDBuildUI.locales.es.Locales', {
        "requires": ["CMDBuildUI.locales.es.LocalesAdministration"],
        "override": "CMDBuildUI.locales.Locales",
        "singleton": true,
        "localization": "es",
        "administration": CMDBuildUI.locales.es.LocalesAdministration.administration,
        "attachments": {
            "add": "Agregar adjunto",
            "attachmenthistory": "Historia adjunto",
            "author": "Autor",
            "category": "Categoria",
            "creationdate": "Fecha de creación",
            "deleteattachment": "Eliminar adjunto",
            "deleteattachment_confirmation": "¿Estás seguro de que quieres eliminar este adjunto?",
            "description": "Descripción",
            "download": "Descargar",
            "editattachment": "Modificar adjunto",
            "file": "Archivo",
            "filename": "Nombre archivo",
            "majorversion": "Versión principal",
            "modificationdate": "Fecha de modificación",
            "uploadfile": "Subir archivio",
            "version": "Versión",
            "viewhistory": "Vista historia adjunto"
        },
        "bim": {
            "bimViewer": "Visor BIM",
            "card": {
                "label": "Tarjeta"
            },
            "layers": {
                "label": "Capas",
                "menu": {
                    "hideAll": "Esconder todos",
                    "showAll": "Mostrar todos"
                },
                "name": "Nombre",
                "qt": "Cantidad",
                "visibility": "Visibilidad",
                "visivility": "Visibilidad"
            },
            "menu": {
                "camera": "Cámara",
                "frontView": "Vista frontal",
                "mod": "Controles de visor",
                "orthographic": "Camara ortografica",
                "pan": "Desplazar",
                "perspective": "Cámara de perspectiva",
                "resetView": "Reestablecer vista",
                "rotate": "Rota",
                "sideView": "Vista lateral",
                "topView": "Vista superior"
            },
            "showBimCard": "Abrir visualizador 3D",
            "tree": {
                "arrowTooltip": "Seleccionar elemento",
                "columnLabel": "Vista de árbol",
                "label": "Vista de árbol",
                "open_card": "Abrir tarjeta relacionada",
                "root": "Raíz de IFC"
            }
        },
        "classes": {
            "cards": {
                "addcard": "Agregar tarjeta",
                "clone": "Clonar",
                "clonewithrelations": "Clonar tarjeta y relaciones",
                "deletecard": "Eliminar tarjeta",
                "deleteconfirmation": "¿Estás seguro de que quieres eliminar esta tarjeta?",
                "label": "Tarjetas de datos",
                "modifycard": "Modificar tarjeta",
                "opencard": "Abrir tarjeta",
                "print": "Imprimir tarjeta"
            },
            "simple": "Simple",
            "standard": "Estándar"
        },
        "common": {
            "actions": {
                "add": "Agregar",
                "apply": "Aplicar",
                "cancel": "Cancelar",
                "close": "Cerrar",
                "delete": "Eliminar",
                "edit": "Editar",
                "execute": "Ejecutar",
                "refresh": "Actualizar datos",
                "remove": "Eliminar",
                "save": "Guardar",
                "saveandapply": "Guardar y aplicar",
                "saveandclose": "Guardar y cerrar",
                "search": "Buscar",
                "searchtext": "Buscar..."
            },
            "attributes": {
                "nogroup": "Base de datos"
            },
            "dates": {
                "date": "d/m/Y",
                "datetime": "d/m/Y H:i:s",
                "time": "H:i:s"
            },
            "editor": {
                "clearhtml": "Limpiar HTML"
            },
            "grid": {
                "disablemultiselection": "Desactivar selección múltiple",
                "enamblemultiselection": "Habilitar selección múltiple",
                "export": "Exportar datos",
                "filterremoved": "El filtro actual ha sido eliminado",
                "import": "Importar datos",
                "itemnotfound": "Elemento no encontrado",
                "list": "Lista",
                "opencontextualmenu": "Abrir menú contextual",
                "print": "Imprimir",
                "printcsv": "Imprimir como CSV",
                "printodt": "Imprimir como ODT",
                "printpdf": "Imprimir como PDF",
                "row": "Elemento",
                "rows": "Elementos",
                "subtype": "Subtipo"
            },
            "tabs": {
                "activity": "Actividad",
                "attachments": "Adjuntos",
                "card": "Tarjeta",
                "details": "Detalles",
                "emails": "Emails",
                "history": "Historia",
                "notes": "Notas",
                "relations": "Relaciones"
            }
        },
        "emails": {
            "addattachmentsfromdms": "Agregar adjunto desde DMS",
            "alredyexistfile": "Ya existe un archivo con este nombre",
            "archivingdate": "Fecha de archivo",
            "attachfile": "Adjunta archivo",
            "bcc": "Bcc",
            "cc": "Cc",
            "composeemail": "Crear email",
            "composefromtemplate": "Crear email desde plantilla",
            "delay": "Retrasar",
            "delays": {
                "day1": "En 1 día",
                "days2": "En 2 días",
                "days4": "En 4 días",
                "hour1": "1 hora",
                "hours2": "2 horas",
                "hours4": "4 horas",
                "month1": "En 1 mes",
                "none": "Ninguno",
                "week1": "En 1 semana",
                "weeks2": "En 2 semanas"
            },
            "dmspaneltitle": "Elegir adjuntos desde la base de datos",
            "edit": "Editar",
            "from": "De",
            "gridrefresh": "Recargar tabla",
            "keepsynchronization": "Mantener sincronizado",
            "message": "Mensaje",
            "regenerateallemails": "Regenerar todos los correos",
            "regenerateemail": "Regenerar email",
            "remove": "Eliminar",
            "remove_confirmation": "¿Estás seguro de que quieres eliminar este email?",
            "reply": "Contestar",
            "replyprefix": "En {0}, {1} escribió:",
            "selectaclass": "Seleccionar clase",
            "sendemail": "Enviar correo electrónico",
            "statuses": {
                "draft": "Borradores",
                "outgoing": "Salida",
                "received": "Recibidos",
                "sent": "Enviados"
            },
            "subject": "Subjeto",
            "to": "A",
            "view": "Vista"
        },
        "errors": {
            "autherror": "Usuario o clave incorrectos",
            "classnotfound": "Clase {0} no encontrada",
            "notfound": "Elemento no encontrado"
        },
        "filters": {
            "actions": "Comportamiento",
            "addfilter": "Agregar filtro",
            "any": "Uno cualquiera",
            "attribute": "Elegir un atributo",
            "attributes": "Atributos",
            "clearfilter": "Limpiar filtro",
            "clone": "Clonar",
            "copyof": "Copia de",
            "description": "Descripción",
            "domain": "Dominio",
            "filterdata": "Filtrar datos",
            "fromselection": "Desde la selección",
            "ignore": "Ignorar",
            "migrate": "Emigrar",
            "name": "Nombre",
            "newfilter": "Nuevo filtro",
            "noone": "Ninguno",
            "operator": "Operador",
            "operators": {
                "beginswith": "Inicia con",
                "between": "Entre",
                "contained": "Contenido",
                "containedorequal": "Contenido o igual",
                "contains": "Contiene",
                "containsorequal": "Contiene o igual",
                "different": "Diferente",
                "doesnotbeginwith": "No inicia con",
                "doesnotcontain": "No contiene",
                "doesnotendwith": "No termina con",
                "endswith": "Termina con",
                "equals": "Iguales",
                "greaterthan": "Mayor",
                "isnotnull": "No es nulo",
                "isnull": "Es nulo",
                "lessthan": "Menor"
            },
            "relations": "Relaciones",
            "type": "Tipo",
            "typeinput": "Parámetro de input",
            "value": "Valor"
        },
        "gis": {
            "card": "Tarjeta",
            "cardsMenu": "Menú de tarjetas",
            "externalServices": "Servicios externos",
            "geographicalAttributes": "Atributos geográficos",
            "geoserverLayers": "Capas del Geoserver",
            "layers": "Capas",
            "list": "Lista",
            "map": "Mapa",
            "mapServices": "Servicios de mapas",
            "position": "Posición",
            "root": "Raíz",
            "tree": "Vista de árbol",
            "view": "Vista",
            "zoom": "Zoom"
        },
        "history": {
            "activityname": "Nombre actividad",
            "activityperformer": "Ejecutor actividades",
            "begindate": "Fecha de inicio",
            "enddate": "Fecha de finalización",
            "processstatus": "Estado",
            "user": "Usuario"
        },
        "importexport": {
            "downloadreport": "Descargar informe",
            "emailfailure": "¡Se produjo un error al enviar un correo electrónico!",
            "emailsubject": "Importar informe de datos",
            "emailsuccess": "El correo electrónico ha sido enviado con éxito!",
            "export": "Exportar",
            "import": "Importar",
            "importresponse": "Importar respuesta",
            "response": {
                "created": "Artículos creados",
                "deleted": "Objetos eliminados",
                "errors": "Errores",
                "linenumber": "Número de línea",
                "message": "Mensaje",
                "modified": "Elementos modificados",
                "processed": "Filas procesadas",
                "recordnumber": "Número de registro",
                "unmodified": "Artículos no modificados"
            },
            "sendreport": "Enviar informe",
            "template": "Plantilla",
            "templatedefinition": "Definición de plantilla"
        },
        "login": {
            "buttons": {
                "login": "Iniciar sesión",
                "logout": "Cambiar usuario"
            },
            "fields": {
                "group": "Grupo",
                "language": "Idioma",
                "password": "Clave",
                "tenants": "'Tenants'",
                "username": "Nombre de usuario"
            },
            "loggedin": "Registrado",
            "title": "Iniciar sesión",
            "welcome": "Bienvenido de nuevo {0}."
        },
        "main": {
            "administrationmodule": "Módulo de Administración",
            "baseconfiguration": "Base de configuracion",
            "cardlock": {
                "lockedmessage": "No puedes editar esta tarjeta porque {0} lo está editando.",
                "someone": "Alguien"
            },
            "changegroup": "Cambiar grupo",
            "changepassword": "Cambiar clave",
            "changetenant": "Cambiar 'tenant'",
            "confirmchangegroup": "¿Estás seguro de que quieres cambiar el grupo?",
            "confirmchangetenants": "¿Estás seguro de que quieres cambiar 'tenant' activo?",
            "confirmdisabletenant": "¿Estás seguro de que desea deshabilitar la marca \"Ignorar 'tenants'\"?",
            "confirmenabletenant": "¿Estás seguro de que desea habilitar la marca \"Ignorar 'tenants'\"?",
            "confirmpassword": "Confirmar clave",
            "ignoretenants": "Ignorar tenants",
            "info": "Información",
            "logo": {
                "cmdbuild": "CMDBuild logo",
                "cmdbuildready2use": "CMDBuild READY2USE logo",
                "companylogo": "Logo de la compañía",
                "openmaint": "openMAINT logo"
            },
            "logout": "Cerrar sesión",
            "managementmodule": "Módulo gestión de datos",
            "multigroup": "Multi grupo",
            "multitenant": "Multitenant",
            "navigation": "Navegación",
            "newpassword": "Nueva clave",
            "oldpassword": "Antigua clave",
            "pagenotfound": "Página no encontrada",
            "pleasecorrecterrors": "Por favor, corrija los errores indicados!",
            "preferences": {
                "comma": "Coma",
                "decimalserror": "Campo decimales debe estar presente",
                "decimalstousandserror": "Decimales y miles deben ser diferentes",
                "default": "Por defecto",
                "defaultvalue": "Valor por defecto",
                "labeldateformat": "Formato de fecha",
                "labeldecimalsseparator": "Separador de decimales",
                "labelintegerformat": "<em>Integer format</em>",
                "labellanguage": "Idioma",
                "labelnumericformat": "<em>Numeric format</em>",
                "labelthousandsseparator": "Separador de miles",
                "labeltimeformat": "Formato de tiempo",
                "msoffice": "Microsoft Office",
                "period": "Período",
                "preferredofficesuite": "Office suite preferida",
                "space": "Espacio",
                "thousandserror": "Campo miles debe estar presente",
                "timezone": "Zona horaria",
                "twelvehourformat": "Formato de 12 horas",
                "twentyfourhourformat": "Formato de 24 horas"
            },
            "searchinallitems": "Buscar en todos los artículos",
            "userpreferences": "Preferencias"
        },
        "menu": {
            "allitems": "Todos los artículos",
            "classes": "Clases",
            "custompages": "Páginas personalizadas",
            "dashboards": "Tableros",
            "processes": "Procesos",
            "reports": "Informes",
            "views": "Vistas"
        },
        "notes": {
            "edit": "Modifica nota"
        },
        "notifier": {
            "attention": "Atención",
            "error": "Error",
            "genericerror": "Error generico",
            "genericinfo": "Información genérica",
            "genericwarning": "Advertencia genérica",
            "info": "Información",
            "success": "Éxito",
            "warning": "Atención"
        },
        "patches": {
            "apply": "Aplicar parches",
            "category": "Categoria",
            "description": "Descripción",
            "name": "Nombre",
            "patches": "Parches"
        },
        "processes": {
            "abortconfirmation": "¿Está seguro de querer interrumpir este proceso?",
            "abortprocess": "Interrumpir proceso",
            "action": {
                "advance": "Continúa",
                "label": "Acción"
            },
            "activeprocesses": "Procesos activos",
            "allstatuses": "Todos",
            "editactivity": "Modificar actividad",
            "openactivity": "Actividad abierta",
            "startworkflow": "Inicio",
            "workflow": "Flujo de trabajo"
        },
        "relationGraph": {
            "activity": "Actividad",
            "card": "Tarjeta",
            "cardList": "Lista de tarjetas",
            "cardRelation": "Relación",
            "cardRelations": "Relaciones de tarjeta",
            "choosenaviagationtree": "Elegir el árbol de navegación",
            "class": "Clase",
            "class:": "Clase",
            "classList": "Lista de clase",
            "compoundnode": "Nodo Compuesto",
            "enableTooltips": "Habilitar/deshabilitar información sobre herramientas en el gráfico",
            "level": "Nivel",
            "openRelationGraph": "Abrir gráfico de las relaciones",
            "qt": "Cantidad",
            "refresh": "Actualizar",
            "relation": "Relación",
            "relationGraph": "Gráfico de las relaciones",
            "reopengraph": "Reabrir el grafico desde este nodo"
        },
        "relations": {
            "adddetail": "Agregar detalle",
            "addrelations": "Agregar relaciones",
            "attributes": "Atributos",
            "code": "Código",
            "deletedetail": "Eliminar detalle",
            "deleterelation": "Eliminar relación",
            "description": "Descripción",
            "editcard": "Editar tarjeta",
            "editdetail": "Editar Detalle",
            "editrelation": "Editar relación",
            "mditems": "Elementos",
            "opencard": "Abrir tarjeta relacionada",
            "opendetail": "Visualizar detalle",
            "type": "Tipo"
        },
        "reports": {
            "csv": "CSV",
            "download": "Descargar",
            "format": "Formato",
            "odt": "ODT",
            "pdf": "PDF",
            "print": "Imprimir",
            "reload": "Recargar",
            "rtf": "RTF"
        },
        "thematism": {
            "addThematism": "Agregar tematismo",
            "analysisType": "Tipo de analisis",
            "attribute": "Atributo",
            "calculateRules": "Generar reglas de estilo",
            "clearThematism": "Limpiar tematismo",
            "color": "Color",
            "defineLegend": "Definición de leyenda",
            "defineThematism": "Definición de tematismo",
            "function": "Función",
            "generate": "Generar",
            "geoAttribute": "Atributo geográfico",
            "graduated": "Graduado",
            "highlightSelected": "Resaltar el elemento seleccionado",
            "intervals": "Intervalos",
            "legend": "Leyenda",
            "name": "Nombre",
            "newThematism": "Nuevo tematismo",
            "punctual": "Puntual",
            "quantity": "Cantidad",
            "source": "Fuente",
            "table": "Mesa",
            "thematism": "Tematismos",
            "value": "Valor"
        },
        "widgets": {
            "customform": {
                "addrow": "Agregar fila",
                "clonerow": "Clonar fila",
                "deleterow": "Eliminar fila",
                "editrow": "Editar fila",
                "export": "Exportar",
                "import": "Importar",
                "refresh": "Actualizar a los valores predeterminados"
            },
            "linkcards": {
                "editcard": "Editar tarjeta",
                "opencard": "Abrir tarjeta",
                "refreshselection": "Aplicar selección por defecto",
                "togglefilterdisabled": "Habilitar filtro de tabla",
                "togglefilterenabled": "Habilitar filtro de tabla"
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
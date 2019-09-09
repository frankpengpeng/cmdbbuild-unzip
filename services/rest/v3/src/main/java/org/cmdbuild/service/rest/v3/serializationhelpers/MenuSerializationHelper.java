/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.serializationhelpers;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.menu.MenuItemType;
import static org.cmdbuild.menu.MenuItemType.CLASS;
import static org.cmdbuild.menu.MenuItemType.CUSTOM_PAGE;
import static org.cmdbuild.menu.MenuItemType.DASHBOARD;
import static org.cmdbuild.menu.MenuItemType.FOLDER;
import static org.cmdbuild.menu.MenuItemType.PROCESS;
import static org.cmdbuild.menu.MenuItemType.REPORT_CSV;
import static org.cmdbuild.menu.MenuItemType.REPORT_ODT;
import static org.cmdbuild.menu.MenuItemType.REPORT_PDF;
import static org.cmdbuild.menu.MenuItemType.REPORT_XML;
import static org.cmdbuild.menu.MenuItemType.ROOT;
import static org.cmdbuild.menu.MenuItemType.SYSTEM_FOLDER;
import static org.cmdbuild.menu.MenuItemType.VIEW;
import org.cmdbuild.menu.MenuTreeNode;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.queue;
import org.springframework.stereotype.Component;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class MenuSerializationHelper {

    public final static BiMap<MenuItemType, String> MENU_ITEM_TYPE_WS_MAP = HashBiMap.create(map(
            CLASS, "class",
            DASHBOARD, "dashboard",
            PROCESS, "processclass",
            FOLDER, "folder",
            SYSTEM_FOLDER, "system_folder",
            REPORT_CSV, "reportcsv",
            REPORT_PDF, "reportpdf",
            REPORT_ODT, "reportodt",
            REPORT_XML, "reportxml",
            VIEW, "view",
            CUSTOM_PAGE, "custompage",
            ROOT, "root"));

    private final ObjectTranslationService translationService;
    private final DaoService dao;
    private final ReportService reportService;

    public MenuSerializationHelper(ObjectTranslationService translationService, DaoService dao, ReportService reportService) {
        this.translationService = checkNotNull(translationService);
        this.dao = checkNotNull(dao);
        this.reportService = checkNotNull(reportService);
    }

    public Object serializeFlatMenu(MenuTreeNode item) {
        Queue<MenuTreeNode> queue = queue(item);
        List list = list();
        while (!queue.isEmpty()) {
            MenuTreeNode thisItem = queue.poll();
            list.add(doSerializeMenu(thisItem, emptyList()));
            queue.addAll(thisItem.getChildren());
        }
        return list;
    }

    public FluentMap serializeMenu(MenuTreeNode item) {
        return doSerializeMenu(item, item.getChildren());
    }

    private FluentMap doSerializeMenu(MenuTreeNode item, List<MenuTreeNode> children) {
        AtomicInteger index = new AtomicInteger(-1);
        return map(
                "_id", item.getCode(),
                "menuType", checkNotNull(MENU_ITEM_TYPE_WS_MAP.get(item.getType())),
                "objectDescription", item.getDescription(),
                "_objectDescription_translation", getMenuTranslationOrClassTranslation(item),
                "children", children.stream().map((i) -> serializeMenu(i).with("index", index.incrementAndGet())).collect(toList())
        ).skipNullValues().with("objectTypeName", emptyToNull(item.getTarget())).then();
    }

    private String getMenuTranslationOrClassTranslation(MenuTreeNode item) {
        String menuItemTranslation = translationService.translateMenuitemDescription(item.getCode(), item.getDescription()),
                itemLabel = null,
                itemTranslation = null;
        switch (item.getType()) {
            case CLASS:
            case PROCESS:
                Classe classe = dao.getClasse(item.getTarget());
                itemLabel = classe.getDescription();
                itemTranslation = translationService.translateClassDescription(classe);
                break;
            case REPORT_CSV:
            case REPORT_PDF:
            case REPORT_ODT:
            case REPORT_XML:
                ReportInfo report = reportService.getByCode(item.getTarget());
                itemLabel = report.getDescription();
                itemTranslation = translationService.translateReportDesciption(report.getCode(), report.getDescription());
        }

        if (equal(menuItemTranslation, itemLabel)) {
            return itemTranslation;
        } else {
            return menuItemTranslation;
        }
    }
}

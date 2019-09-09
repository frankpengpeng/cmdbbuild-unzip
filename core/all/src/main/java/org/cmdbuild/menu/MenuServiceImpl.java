package org.cmdbuild.menu;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static org.cmdbuild.menu.MenuConstants.DEFAULT_MENU_GROUP_NAME;

import java.util.List;

import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.auth.user.OperationUserStore;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.dao.entrytype.Classe;
import org.springframework.stereotype.Component;
import org.cmdbuild.extcomponents.custompage.CustomPageService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.report.ReportService;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.menu.MenuItemType.CLASS;
import static org.cmdbuild.menu.MenuItemType.CUSTOM_PAGE;
import static org.cmdbuild.menu.MenuItemType.DASHBOARD;
import static org.cmdbuild.menu.MenuItemType.FOLDER;
import static org.cmdbuild.menu.MenuItemType.PROCESS;
import static org.cmdbuild.menu.MenuItemType.REPORT_ODT;
import static org.cmdbuild.menu.MenuItemType.REPORT_PDF;
import static org.cmdbuild.menu.MenuItemType.REPORT_XML;
import static org.cmdbuild.menu.MenuItemType.VIEW;
import static org.cmdbuild.menu.MenuItemType.REPORT_CSV;
import static org.cmdbuild.menu.MenuItemType.ROOT;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.cmdbuild.view.ViewDefinitionService;

@Component
public class MenuServiceImpl implements MenuService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final MenuRepository menuRepository;
    private final ViewDefinitionService viewService;
    private final CustomPageService customPageService;
    private final OperationUserStore userStore;
    private final ReportService reportService;
    private final UserClassService userClassService;

    public MenuServiceImpl(UserClassService userClassService, MenuRepository menuRepository, ReportService reportStore, DaoService dao, ViewDefinitionService viewLogic, CustomPageService customPagesLogic, OperationUserStore userStore) {
        this.menuRepository = checkNotNull(menuRepository);
        this.dao = checkNotNull(dao);
        this.viewService = checkNotNull(viewLogic);
        this.customPageService = checkNotNull(customPagesLogic);
        this.userStore = checkNotNull(userStore);
        this.reportService = checkNotNull(reportStore);
        this.userClassService = checkNotNull(userClassService);
    }

    @Nullable
    @Override
    public Menu getMenuForGroupOrNull(String groupName) {
        MenuData data = menuRepository.getMenuDataForGroupOrNull(groupName);
        return data == null ? null : toMenu(data);
    }

    @Nullable
    @Override
    public Menu getMenuByIdOrNull(long menuId) {
        MenuData data = menuRepository.getMenuDataByIdOrNull(menuId);
        return data == null ? null : toMenu(data);
    }

    private Menu toMenu(MenuData data) {
        return new MenuImpl(data.getId(), toMenuItem(data.getMenuRootNode()), data.getGroupName());
    }

    @Override
    public List<MenuInfo> getAllMenuInfos() {
        return menuRepository.getAllMenuInfos();
    }

    @Override
    public Menu create(String groupName, MenuTreeNode menu) {
        return toMenu(menuRepository.createMenuData(MenuDataImpl.builder()
                .withGroupName(groupName)
                .withMenuRootNode(toJsonMenu(menu))
                .build()));
    }

    @Override
    public Menu update(long menuId, MenuTreeNode menu) {
        MenuData data = menuRepository.getMenuDataById(menuId);
        return toMenu(menuRepository.updateMenuData(MenuDataImpl.copyOf(data)
                .withMenuRootNode(toJsonMenu(menu))
                .build()));
    }

    private MenuJsonRootNode toJsonMenu(MenuTreeNode menu) {
        checkArgument(equal(menu.getType(), ROOT));
        return new MenuJsonRootNodeImpl(toJsonMenuNodes(menu.getChildren()));
    }

    private MenuJsonNode toJsonMenuNode(Pair<MenuTreeNode, Integer> pair) {
        MenuTreeNode node = pair.getLeft();
        return new MenuJsonNodeImpl(node.getType(),
                node.getTarget(),
                node.getDescription(),
                node.getCode(),
                toJsonMenuNodes(node.getChildren()));
    }

    private List<MenuJsonNode> toJsonMenuNodes(List<MenuTreeNode> list) {
        return IntStream.range(0, list.size()).mapToObj((i) -> Pair.of(list.get(i), i + 1)).map(this::toJsonMenuNode).collect(toList());
    }

    @Override
    public void delete(long menuId) {
        menuRepository.delete(menuId);
    }

    @Override
    public MenuTreeNode getMenuForCurrentUser() {
        OperationUser user = userStore.getUser();
        String defaultGroup = user.getDefaultGroupNameOrNull();
        Menu menu = isBlank(defaultGroup) ? null : getMenuForGroupOrNull(defaultGroup);
        if (menu == null) {
            menu = getMenuForGroupOrNull(DEFAULT_MENU_GROUP_NAME);
        }
        if (menu == null) {
            menu = create(defaultGroup, MenuTreeNodeImpl.builder()
                    .withType(ROOT)
                    .withDescription("ROOT")
                    .build());
        }
        List<MenuTreeNode> nodes = filterMenuForUser(menu.getRootNode().getChildren());
        return MenuTreeNodeImpl.copyOf(menu.getRootNode()).withChildren(nodes).build();
    }

    private List<MenuTreeNode> filterMenuForUser(List<MenuTreeNode> menu) {
        OperationUser user = userStore.getUser();
        UserPrivileges privilegeContext = user.getPrivilegeContext();
        return menu.stream()
                .filter((m) -> {
                    try {
                        switch (m.getType()) {
                            case FOLDER:
                            case ROOT:
                                return true;
                            case CLASS:
                            case PROCESS:
                                return userClassService.isActiveAnduserCanRead(m.getTarget());
                            case REPORT_CSV:
                            case REPORT_ODT:
                            case REPORT_PDF:
                            case REPORT_XML:
                                return reportService.isAccessibleByCode(m.getTarget());
                            case DASHBOARD:
                                logger.warn("TODO: dashboard currently disabled, skipping menu record = {}", m);
                                return false;
                            case VIEW:
                                return viewService.isActiveAndUserAccessibleByName(m.getTarget());
                            case CUSTOM_PAGE:
                                return customPageService.isAccessibleByName(m.getTarget());
                            default:
                                throw unsupported("unsupported menu type = %s", m.getType());
                        }
                    } catch (Exception ex) {
                        logger.error(marker(), "error processing menu record = {}", m, ex);
                        return false;
                    }

                }).map((n) -> {
            if (n.getChildren().isEmpty()) {
                return n;
            } else {
                return MenuTreeNodeImpl.copyOf(n).withChildren(filterMenuForUser(n.getChildren())).build();
            }
        }).collect(toList());
    }

    private MenuTreeNode toMenuItem(MenuJsonRootNode root) {
        return MenuTreeNodeImpl.buildRoot(root.getMenuNodes().stream().map(this::convertMenuElementToMenuItemBuilderOrNullIfError).filter(notNull()).collect(toList()));
    }

    private @Nullable
    MenuTreeNode convertMenuElementToMenuItemBuilderOrNullIfError(MenuJsonNode menuElement) {
        try {
            return convertMenuElementToMenuItemBuilder(menuElement);
        } catch (Exception e) {
            logger.error(marker(), "Error converting MenuItem from element = {}", menuElement, e);
            return null;
        }
    }

    private MenuTreeNode convertMenuElementToMenuItemBuilder(MenuJsonNode record) {
        return MenuTreeNodeImpl.builder()
                .withCode(record.getCode())
                .withType(record.getMenuType())
                .withDescription(record.getDescription())
                .accept((b) -> {
                    switch (record.getMenuType()) {
                        case CLASS:
                        case PROCESS:
                            Classe classe = dao.getClasse(record.getTarget());
                            b.withTarget(classe.getName());
                            break;
                        case REPORT_CSV:
                        case REPORT_ODT:
                        case REPORT_PDF:
                        case REPORT_XML:
                        case CUSTOM_PAGE:
                        case VIEW:
                            b.withTarget(record.getTarget());//TODO check this
                            break;
                        case DASHBOARD:
                            b.withTarget(toStringNotBlank(toLong(record.getTarget())));//TODO check this 
                            break;
                        case FOLDER:
                        case ROOT:
                        case SYSTEM_FOLDER:
                            break;//no target
                        default:
                            throw unsupported("unsupported menu item type = %s", record.getMenuType());
                    }
                })
                //				.withChildren(record.getChildren().stream().sorted(Ordering.natural().onResultOf(MenuJsonNode::getIndex)).map(this::convertMenuElementToMenuItemBuilderOrNullIfError).filter(notNull()).collect(toList()))
                .withChildren(record.getChildren().stream().map(this::convertMenuElementToMenuItemBuilderOrNullIfError).filter(notNull()).collect(toList()))
                .build();
    }

}

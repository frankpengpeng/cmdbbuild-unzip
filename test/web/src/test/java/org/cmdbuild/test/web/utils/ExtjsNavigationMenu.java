package org.cmdbuild.test.web.utils;


import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.cmdbuild.test.web.utils.ExtjsUtils.*;

/*
 * If an item is not visible (e.g.: son of collapsed parent) it's getText returns an empty string!
 * We need a to use different strategy if we want to build the full tree
 */

//DEV, not finished
public class ExtjsNavigationMenu {

  

    //FIXME getMenuType optional or UNKNOWN menu type?
    public enum MenuType {

        FOLDER("Folder", FontAwesomeUtils.ICON_FOLDER, "folder"),
        CLASS("Class", FontAwesomeUtils.ICON_CLASS, "class"),
        PROCESS("Process", FontAwesomeUtils.ICON_PROCESS, "processclass"),
        DASHBOARD("Dashboard", FontAwesomeUtils.ICON_DASHBOARD, "dashboard"),
        VIEW("View", FontAwesomeUtils.ICON_VIEW, "view"),
        CUSTOMPAGE("CustomPage", FontAwesomeUtils.ICON_CUSTOMPAGE, "custompage"),
        /**
         * All non pdf reports
         */
        REPORT("Report", FontAwesomeUtils.ICON_REPORT, Arrays.asList("report", "reportcsv", "reportrtf", "reportodt" , "reportpdf")),
        REPORTPDF("ReportPDF", FontAwesomeUtils.ICON_REPORTPDF, "reportpdf"),;


        private final String description;
        private final String cssIconClass;
        private final List<String> jsonMenuTypes;

        MenuType(String description, String cssIconClass, String jsonMenuType) {
            this.description = description;
            this.cssIconClass = cssIconClass;
            this.jsonMenuTypes = Arrays.asList(jsonMenuType);
        }

        MenuType(String description, String cssIconClass, List<String> jsonMenuTypes) {
            this.description = description;
            this.cssIconClass = cssIconClass;
            this.jsonMenuTypes = jsonMenuTypes;
        }

        public static Optional<MenuType> fromIconCssClass(String cssIconClass) {
            return Arrays.asList(MenuType.values()).stream().filter(mt -> mt.matchesIcon(cssIconClass)).findFirst();
        }

        public boolean matches(MenuType menu) {
            return (description.equals(menu.description));
        }
        public boolean matchesIcon(String iconClass) {
            return cssIconClass.equals(iconClass);
        }

        public boolean matchesJson(String jsonMenuType) {
            return jsonMenuTypes.contains(jsonMenuType);
        }

        public String toString() {
            return description;
        }
    }

    public static class MenuItem {

        private final WebElement rootWebElement;
        private final WebElement textWrapperWebElement; //convienience, TODO remove
        private String path;

        public MenuItem(WebElement root, @Nullable String parentPath) {//root is the <li> element...
            rootWebElement = root;
            textWrapperWebElement = root.findElement(By.className("x-treelist-item-text"));
            this.path = Strings.nullToEmpty(parentPath) + "/" + getText();
        }

        /**
         *  This does not address every case.
         *  E.g.: superclasses have class icon but are not (generally leaves). Use isExpandable in that context.
         *
         * @return true iff icon is folder.
         */
        public boolean isLeaf() {
            if (MenuType.FOLDER.equals(getMenuType().get()))
                return false;
            return true;
            //below NOT WORKING
            //return isClass(getParent(rootWebElement),"x-treelist-item-leaf");
        }

        public boolean isExpandable() {
           // throw new RuntimeException("Not yet expandable");
            return (null == rootWebElement.findElement(By.className("x-treelist-item-expander")));
        }

        @Deprecated //probably working only for firs level elements
        public boolean isFirstLevel() {
            return isClass(getParent(rootWebElement), "x-treelist-root-container");
        }

        public String getText() {
            return textWrapperWebElement.getText();
        }

        public String getPath() {
            return path;
        }

        public boolean isDisplayed() {
            return rootWebElement.isDisplayed();
        }

        public Dimension getSize() {
            return rootWebElement.getSize();
        }

        public String getIconClass() {
            WebElement iconDiv = rootWebElement.findElement(By.className("x-treelist-item-icon"));
            String classes = iconDiv.getAttribute("class");
            return Splitter.on(" ").omitEmptyStrings().trimResults().splitToList(classes)
                    .stream().filter(c -> c.startsWith("fa-")).filter(FontAwesomeUtils::isNotFontAwesomeIconModifier)
                    .findFirst().get();
        }

        public Optional<MenuType> getMenuType() {
            return MenuType.fromIconCssClass(getIconClass());
        }

        /**
         * @return true if expander found and clicked
         */
        //TODO should wait until action is executed
        public boolean expandByClick() {
            WebElement expander = rootWebElement.findElement(By.className("x-treelist-item-expander"));
            String expanderClass = expander.getAttribute("class");
            if (expander != null && expander.isEnabled()) {
                //check if already expanded (first ancestor li x-treelist-item-expanded)
                Optional<WebElement> xTreeListRootItem = getFirstAncestorByAttribute(expander, "class", "x-treelist-item-expandable");
                if (xTreeListRootItem.isPresent() && xTreeListRootItem.get().getAttribute("class").contains("x-treelist-item-expanded"))
                    return true;
                expander.click();
                return true;
            }
            return false;
        }

        public void click() {
            rootWebElement.click();
        }

        public List<MenuItem> getVisibleChildren() {

            ArrayList<MenuItem> menu = new ArrayList<>();
//				WebElement navigationContainer = findElementByTestId(driver, "management-navigation-container");
            WebElement container = rootWebElement;
            //finds FIRST <ul>, used to ignore inner <li>
            //<ul> seems to be present even if no sub item is present
            WebElement ulTag = container.findElement(By.tagName("ul"));
            String ulId = ulTag.getAttribute("id");
            List<WebElement> navItems = container.findElements(By.tagName("li"));
            navItems.stream().filter(li -> getParent(li).getAttribute("id").equals(ulId))
                    .forEach(li -> menu.add(from(li, getPath())));

            return menu;
        }


        public static MenuItem from(WebElement rootLIWebElement, String parentPath) {
            return new MenuItem(rootLIWebElement, parentPath);
        }

    }


    private WebDriver driver;

    public ExtjsNavigationMenu(@Nonnull WebDriver driver) {
        this.driver = driver;
    }


    /**
     * @return first level nodes.
     * <p>
     * Seems that all items returned are visible (an item returns non empty string only if rendered)
     */
    public List<MenuItem> fetchFirstLevelNodes() {

        ArrayList<MenuItem> menu = new ArrayList<>();
        String url = driver.getCurrentUrl();
        String moduleTestId = "management-navigation-container";
        if (url.indexOf("/#administration") >= 0)
            moduleTestId = "administration-navigation-container";
        WebElement navigationContainer = findElementByTestId(driver, moduleTestId);
        List<WebElement> navItems = navigationContainer.findElements(By.tagName("li"));
        navItems.stream().filter(li -> getParent(li).getAttribute("class").contains("x-treelist-root-container"))
                .forEach(li -> menu.add(MenuItem.from(li, null)));
        return menu;
    }

    public Optional<MenuItem> fetchFirstClass() {
        List<MenuItem> nodes = fetchFirstLevelNodes();
        Optional<MenuItem> firstNodeWithClass = nodes.stream().filter(n -> fetchFirstClassInNodeDepthFirst(n).isPresent()).findFirst();
        if (firstNodeWithClass.isPresent())
            return fetchFirstClassInNodeDepthFirst(firstNodeWithClass.get());
        return Optional.empty();
    }

    private Optional<MenuItem> fetchFirstClassInNodeDepthFirst(MenuItem node) {

        if (node.isLeaf()) return Optional.of(node);
        List<MenuItem> children = node.getVisibleChildren();
        for(MenuItem c : children) {
            if (fetchFirstClassInNodeDepthFirst(c).isPresent() )
                return Optional.of(fetchFirstClassInNodeDepthFirst(c).get());
        }
        return Optional.empty();
    }

    public Optional<MenuItem> getAtPath(String... navigationTree) {

        List<MenuItem> nodes = fetchFirstLevelNodes(); //.stream().filter(mi -> mi.getText())
        for (int depth = 0; depth < navigationTree.length; depth++) {
            final String expectedNode = navigationTree[depth];
            Optional<MenuItem> node = nodes.stream().filter(n -> expectedNode.equals(Strings.nullToEmpty(n.getText()).trim())).findFirst();
            if (node.isPresent()) {
                if (depth == navigationTree.length -1)
                    return node;
                node.get().expandByClick();
                ExtjsUtils.artificialSleep(500, null) ;
                nodes = node.get().getVisibleChildren();
                continue;
            } else {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Raises runtime exception if navigationTree is wrong.
     *
     * @param navigationTree sequence of node names composing navigation tree to reach leaf node
     */
    public void selectAtPath(String... navigationTree) {
        getAtPath(navigationTree).get().click();
    }

    public static ExtjsNavigationMenu extract(WebDriver driver) {
        return new ExtjsNavigationMenu(driver);
    }

}

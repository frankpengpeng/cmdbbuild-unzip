package org.cmdbuild.test.web.utils;


import com.google.common.base.Strings;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.cmdbuild.test.web.utils.ExtjsUtils.waitForElementPresence;
import static org.cmdbuild.test.web.utils.ExtjsUtils.waitForPresenceOfNestedElement;
import static org.cmdbuild.test.web.utils.UILocators.gridRowsTable;

// This class is initially being developed for administration attributes tab grid but aims to be as general as possible
public class ExtjsGenericGrid {


    //TODO locator, locators or webelements
    //setted once
    WebDriver driver;
    By gridParentLocator;
    By gridHeaderLocator;
    By gridContentLocator;


    //dinamic? (maybe they change upon refresh
    //really needed?? try
    @Deprecated WebElement rootHeaderElement;
    @Deprecated WebElement rootGridElement;

    //EXTRACTED
    boolean initialized = false;
    boolean valid = false; //set to true only if extraction was successfully
    List<String> headers = new ArrayList();
    List<List<GridCell>> cells = new ArrayList<>();
    //TODO does not address non string types (or simply fill the content?)
    //TODO think about GridTypedCellsClass


    //TODO  add cheks?
    public static  ExtjsGenericGrid from(WebDriver driver, By gridParentLocator, By gridHeaderLocator, By gridContentLocator) {
        ExtjsGenericGrid grid = new ExtjsGenericGrid();
        grid.driver = driver;
        grid.gridContentLocator = gridContentLocator;
        grid.gridHeaderLocator = gridHeaderLocator;
        grid.gridParentLocator = gridParentLocator;
        grid.valid = false;
        return grid;
    }


    public List<String> getHeaders() {

        if (! initialized)
            extract();
        return headers;
    }

    public List<List<GridCell>> getCells() {

        if (! initialized)
            extract();
        return cells;
    }

    public boolean isValid() {
        return valid;
    }

    //TODO implement to be called before accessing the grid
    public ExtjsGenericGrid extract() {
        //extract content filling content related structures
        valid = false;
        headers.clear();
        cells.clear();
        //fetch headers
        WebElement rootElement = waitForElementPresence(driver, gridParentLocator);
        rootHeaderElement = waitForPresenceOfNestedElement(driver, gridHeaderLocator , gridParentLocator);
        if (rootHeaderElement == null) {
            valid = false;
            return this;
        }
        List<WebElement> columnHeaders = rootHeaderElement.findElements(By.className("x-column-header")).stream().filter(h -> !h.getAttribute("class").contains("x-column-header-first")).collect(Collectors.toList());
        //List<WebElement> columnHeaders = rootHeaderElement.findElements(By.className("x-column-header-text"));
        columnHeaders.stream().forEach(h ->{
            List<WebElement> innerText =  h.findElements(By.className("x-column-header-text-inner")) ;
            if (! innerText.isEmpty())
                headers.add(Strings.nullToEmpty(innerText.get(0).getText()));
            else
                headers.add("");
        });
        //fetch content FIXME: perform some sort of check on header and row dimensions
        WebElement contentRoot = waitForPresenceOfNestedElement(driver, gridContentLocator, gridParentLocator);
        //WebElement table = waitForPresenceOfNestedElement(driver, By.tagName("table"), contentRoot);
        //WebElement tbody = waitForPresenceOfNestedElement(driver, By.tagName("tbody"), table);
        List<WebElement> rows = contentRoot.findElements(By.tagName("tr"));
        
        for (int j = 0; j < rows.size(); j++) {
        	if(j%2==0) {
        		List<WebElement> tds = rows.get(j).findElements(By.tagName("td")).stream().filter(td -> !td.getAttribute("class").contains("x-grid-cell-first")).collect(Collectors.toList());
        		List<GridCell> rowCells = new ArrayList<>();
        		cells.add(rowCells);
        		for (int i = 0; i < tds.size(); i++) {
        			WebElement td = tds.get(i);
        			rowCells.add(gridCellFromTableDataDiv(td, headers.get(i)));
        		}
        	}
        }

        initialized = true;
        valid = true;
        return this;
    }

    public void refreshGrid(){
        extract();
    }

    public static GridCell gridCellFromTableDataDiv(WebElement td, String columnName) {

        if (td == null)
            return null;
        GridCell cell = new GridCell();
        cell.setFieldName(columnName);
        String tdClass = Strings.nullToEmpty(td.getAttribute("class"));
        if (tdClass.contains("x-grid-cell-checkcolumn-"))  {//it's a checkbox!
            if (td.findElements(By.tagName("span")).stream().anyMatch(s -> s.getAttribute("class").contains("x-grid-checkcolumn-checked"))) {//checked
                cell.setContent(GridCell.CHECKBOX_CHECKED);
            } else {//unchecked
                cell.setContent(GridCell.CHECKBOX_UNCHECKED);
            }
        } else {//an ordinary text field
            cell.setContent(Strings.nullToEmpty(td.getText()).trim());
        }
        return cell;

    }


    public List<Integer> getIndexOfRowsCointainingAllCells(GridCell... searchCells) {

        List<Integer> matchingRows = new ArrayList<>();
        for (int i = 0; i < cells.size(); i++) {
            List<GridCell> row = cells.get(i);
            if (rowContainingAllCells(row, searchCells))
                matchingRows.add(new Integer(i));
        }
        return matchingRows;
    }

    private boolean rowContainingAllCells(List<GridCell> row, GridCell... searchCells) {

        for (GridCell cell : searchCells) {
            boolean found = false;
            for (GridCell sc : row) {
                if (cell.matches(sc)) {
                    found = true;
                    break;
                }
            }
            if  (!found) return false;
        }
        return true;
    }
}

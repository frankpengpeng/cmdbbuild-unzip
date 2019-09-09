package org.cmdbuild.test.web.utils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class GridCell implements FormField {

    public GridCell(String fieldName, String content) {
        this.fieldName = fieldName;
        this.content = content;
    }

    public GridCell() {}

    public static GridCell from(String fieldName, String content) {
        return new GridCell(fieldName, content);
    }

    public String getName() {
        return fieldName;
    }

    public String getContent() {
        return content;
    }

    public void setFieldName(String fieldName) {this.fieldName = fieldName;}

    public void setContent(String content) {this.content = content;}

    public String toString() {
        return "Grid cell [" + fieldName +"," + content + "]";
    }

//    public boolean matches(@Nonnull GridCell cell) {
//        if (this.fieldName.equals(cell.fieldName) && this.content.equals(cell.content))
//            return true;
//        return false;
//    }
//
//    public boolean matchesIgnoreCase(@Nonnull GridCell cell) {
//        if (this.fieldName.equalsIgnoreCase(cell.fieldName) && this.content.equalsIgnoreCase(cell.content))
//            return true;
//        return false;
//    }

    private String fieldName;
    private String content;


    public static @Nonnull GridCell[] formfieldsToCellArray(@Nonnull List<FormField> fields) {
        GridCell[] cells = new GridCell[fields.size()];
        IntStream.range(0, fields.size()).forEach(i -> cells[i] = formFieldToCell(fields.get(i)));
        return cells;
    }

    public static List<GridCell> formfieldsToCells(@Nonnull List<FormField> fields) {
        return fields.stream().map(ff -> new GridCell(ff.getName(),ff.getContent())).collect(Collectors.toList());
    }

    public static GridCell formFieldToCell (@Nonnull FormField ff) {
        return new GridCell(ff.getName(), ff.getContent());
    }

    public static String getContent(@Nonnull List<GridCell> row, @Nonnull String fieldName) throws NoSuchElementException {
        return row.stream().filter(c -> fieldName.equalsIgnoreCase(c.getName())).findFirst().get().getContent();
    }

    public static String asString(List<GridCell> fieldList) {
        StringBuilder builder = new StringBuilder("FieldList: [ ");
        fieldList.stream().forEach(f -> {builder.append(f.toString());builder.append(" ");});
        return builder.toString();
//		fieldList.stream().forEach(f -> {builder.append("(");builder.append(f.getName());builder.append(" , ");builder.append(f.getContent())});
    }

}

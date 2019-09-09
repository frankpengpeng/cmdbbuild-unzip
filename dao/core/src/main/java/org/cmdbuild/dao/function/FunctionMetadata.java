/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.function;

import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import org.cmdbuild.dao.entrytype.EntryTypeMetadata;

public interface FunctionMetadata extends EntryTypeMetadata {

    static final String CATEGORIES = "cm_categories",
            MASTERTABLE = "cm_mastertable",//TODO check this
            SOURCE = "cm_source",
            TAGS = "cm_tags";

    Collection<StoredFunction.Category> getCategories();

    Set<String> getTags();

    String getMasterTable();

    @Nullable
    String getSource();

}

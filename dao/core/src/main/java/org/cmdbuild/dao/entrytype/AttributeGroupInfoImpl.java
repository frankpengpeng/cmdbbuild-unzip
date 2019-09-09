/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class AttributeGroupInfoImpl implements AttributeGroupInfo {

    private final String name, description;

    public AttributeGroupInfoImpl(String name, @Nullable String description) {
        this.name = checkNotBlank(name);
        this.description = firstNotBlank(description, name);
    }

    public AttributeGroupInfoImpl(String name) {
        this(name, null);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "AttributeGroupInfo{" + "name=" + name + '}';
    }

}

package org.cmdbuild.dao.beans;

import javax.annotation.Nullable;

public class IdAndDescriptionImpl implements IdAndDescription {

    private final Long id;
    private final String description, code;

    public IdAndDescriptionImpl(@Nullable Long id, @Nullable String description) {
        this(id, description, null);
    }

    public IdAndDescriptionImpl(@Nullable Long id, @Nullable String description, @Nullable String code) {
        this.id = id;
        this.description = description;
        this.code = code;
    }

    @Nullable
    @Override
    public Long getId() {
        return id;
    }

    @Nullable
    @Override
    public String getDescription() {
        return description;
    }

    @Nullable
    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return "IdAndDescription{" + "id=" + id + ", description=" + description + '}';
    }

}

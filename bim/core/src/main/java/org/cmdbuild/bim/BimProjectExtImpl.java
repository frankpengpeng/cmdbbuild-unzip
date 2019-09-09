/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.CardIdAndClassName;
import static org.cmdbuild.dao.beans.CardIdAndClassNameImpl.card;

public class BimProjectExtImpl implements BimProjectExt {

    private final BimProject bimProject;
    private final CardIdAndClassName owner;

    public BimProjectExtImpl(BimProject bimProject, @Nullable CardIdAndClassName owner) {
        this.bimProject = checkNotNull(bimProject);
        this.owner = owner;
    }

    public BimProjectExtImpl(BimProject bimProject, @Nullable BimObject bimObject) {
        this.bimProject = checkNotNull(bimProject);
        this.owner = bimObject == null ? null : card(bimObject.getOwnerClassId(), bimObject.getOwnerCardId());
    }

    @Override
    @Nullable
    public Long getId() {
        return bimProject.getId();
    }

    @Override
    @Nullable
    public Long getParentId() {
        return bimProject.getParentId();
    }

    @Override
    public String getProjectId() {
        return bimProject.getProjectId();
    }

    @Override
    public String getName() {
        return bimProject.getName();
    }

    @Override
    public String getDescription() {
        return bimProject.getDescription();
    }

    @Override
    @Nullable
    public String getImportMapping() {
        return bimProject.getImportMapping();
    }

    @Override
    public boolean isActive() {
        return bimProject.isActive();
    }

    @Override
    @Nullable
    public ZonedDateTime getLastCheckin() {
        return bimProject.getLastCheckin();
    }

    @Override
    @Nullable
    public CardIdAndClassName getOwnerOrNull() {
        return owner;
    }

}

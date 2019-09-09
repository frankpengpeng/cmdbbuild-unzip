/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import static org.cmdbuild.bim.BimObjectImpl.BIM_OBJECT_ATTR_OWNER_CARD_ID;
import static org.cmdbuild.bim.BimObjectImpl.BIM_OBJECT_ATTR_OWNER_CLASS_ID;
import org.cmdbuild.dao.beans.CardIdAndClassName;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.ISNULL;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class BimObjectRepositoryImpl implements BimObjectRepository {

    private final DaoService dao;

    public BimObjectRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Nullable
    @Override
    public BimObject getBimObjectForCardOrNull(CardIdAndClassName card) {
        return dao.selectAll().from(BimObject.class)
                .where(BIM_OBJECT_ATTR_OWNER_CLASS_ID, EQ, card.getClassName())
                .where(BIM_OBJECT_ATTR_OWNER_CARD_ID, EQ, card.getId())
                .getOneOrNull();
    }

    @Override
    @Nullable
    public BimObject getBimObjectForProjectOrNull(BimProject bimProject) {
        return dao.selectAll().from(BimObject.class).where("ProjectId", EQ, bimProject.getProjectId()).where("GlobalId", ISNULL).getOneOrNull();
    }

    @Override
    @Nullable
    public BimObject getBimObjectForGlobalIdOrNull(String globalId) {
        return dao.selectAll().from(BimObject.class).where("GlobalId", EQ, checkNotBlank(globalId)).getOneOrNull();
    }

    @Override
    public BimObject createBimObjectForProject(BimProject bimProject, CardIdAndClassName card) {
        return dao.create(BimObjectImpl.builder().withProjectId(bimProject.getProjectId()).withOwnerClassId(card.getClassName()).withOwnerCardId(card.getId()).build());
    }

}

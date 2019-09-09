/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.CardIdAndClassName;

public interface BimObjectRepository {

    BimObject createBimObjectForProject(BimProject bimProject, CardIdAndClassName card);

    @Nullable
    BimObject getBimObjectForCardOrNull(CardIdAndClassName card);

    @Nullable
    BimObject getBimObjectForProjectOrNull(BimProject bimProject);

    @Nullable
    BimObject getBimObjectForGlobalIdOrNull(String globalId);

    default BimObject getBimObjectForCard(CardIdAndClassName card) {
        return checkNotNull(getBimObjectForCardOrNull(card), "bim object not found for card = %s", card);
    }

    default BimObject getBimObjectForProject(BimProject bimProject) {
        return checkNotNull(getBimObjectForProjectOrNull(bimProject), "bim object not found for project = %s", bimProject);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import java.util.List;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.CardIdAndClassName;
import org.cmdbuild.dao.entrytype.Classe;

public interface BimService extends BimObjectRepository, BimProjectRepository {

    final static String BIM_NAV_TREE = "bimnavigation";

    boolean isEnabled();

    boolean hasBim(Classe classe);

    DataHandler downloadIfcFile(long projectId, @Nullable String ifcFormat);

    void uploadIfcFile(long projectId, DataHandler dataHandler, @Nullable String ifcFormat);

    BimProjectExt createProjectExt(BimProjectExt bimProject);

    BimProjectExt updateProjectExt(BimProjectExt bimProject);

    BimProjectExt getProjectExt(long projectId);

    List<BimProjectExt> getAllProjectsAndObjects();

    @Nullable
    BimObject getBimObjectForCardOrViaNavTreeOrNull(CardIdAndClassName card);

    default BimProjectExt createProjectExt(BimProject project, CardIdAndClassName card) {
        return createProjectExt(new BimProjectExtImpl(project, card));
    }

    default BimProjectExt updateProjectExt(BimProject project, CardIdAndClassName card) {
        return updateProjectExt(new BimProjectExtImpl(project, card));
    }

}

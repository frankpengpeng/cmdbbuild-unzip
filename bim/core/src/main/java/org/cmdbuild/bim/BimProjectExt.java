/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import org.cmdbuild.dao.beans.CardIdAndClassName;

public interface BimProjectExt extends BimProject {

    @Nullable
    CardIdAndClassName getOwnerOrNull();

    default CardIdAndClassName getOwner() {
        return Preconditions.checkNotNull(getOwnerOrNull(), "this project does not have a card mapping (bim object record)");
    }

    default boolean hasOwner() {
        return getOwnerOrNull() != null;
    }

}

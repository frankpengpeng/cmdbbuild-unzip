/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import java.time.ZonedDateTime;
import javax.annotation.Nullable;

public interface BimProject {

    @Nullable
    Long getId();

    @Nullable
    Long getParentId();

    String getProjectId();

    String getName();

    String getDescription();

    @Nullable
    String getImportMapping();

    boolean isActive();

    @Nullable
    ZonedDateTime getLastCheckin();

}

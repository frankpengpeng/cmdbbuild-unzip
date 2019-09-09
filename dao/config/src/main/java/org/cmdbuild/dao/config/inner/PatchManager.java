/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;

public interface PatchManager {

    List<Patch> getAvailableCorePatches();

    String getPatchSourcesChecksum();

    @Nullable
    String getLastPatchOnDbKeyOrNull();

    List<PatchInfo> getAllPatches();

    boolean hasPendingPatches();

    void applyPatchAndStore(Patch patch);

    default List<Patch> getPatchesOnDb() {
        return getAllPatches().stream().filter(PatchInfo::hasPatchOnDb).map(PatchInfo::getPatchOnDb).collect(toList());
    }

    void reset();

}

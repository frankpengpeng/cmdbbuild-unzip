/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static org.cmdbuild.utils.io.CmStreamProgressUtils.detailedProgressDescription;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.progressDescription;
import static org.cmdbuild.utils.io.CmStreamProgressUtils.progressDescriptionEta;

public interface StreamProgressEvent {

    String getStreamId();

    String getStreamDescription();

    /**
     * @return progress, between 0.0d and 1.0d
     */
    double getProgress();

    long getCount();

    long getTotal();

    long getElapsedTime();

    long getBeginTimestamp();

    default String getProgressDescription() {
        return progressDescription(getCount(), getTotal());
    }

    default String getProgressDescriptionDetailed() {
        return detailedProgressDescription(getCount(), getTotal(), getBeginTimestamp());
    }

    default String getProgressDescriptionEta() {
        return progressDescriptionEta(getCount(), getTotal(), getBeginTimestamp());
    }

}

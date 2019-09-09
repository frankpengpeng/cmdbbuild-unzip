package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.transform;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.service.rest.common.helpers.ProcessStatusHelper;
import org.cmdbuild.translation.TranslationService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Path("configuration/processes/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessConfigurationWsV2 {

    private final ProcessStatusHelper processStatusHelper;
    private final TranslationService translationService;
    private final DaoService dao;

    public ProcessConfigurationWsV2(ProcessStatusHelper processStatusHelper, TranslationService translationService, DaoService dao) {
        this.processStatusHelper = checkNotNull(processStatusHelper);
        this.translationService = checkNotNull(translationService);
        this.dao = checkNotNull(dao);
    }

    @GET
    @Path("statuses/")
    public Object readStatuses() {
        return map("data", list(transform(processStatusHelper.allValues(), ps -> map(
                "_id", ps.getId(),
                "value", ps.getValue(),
                "description", translationService.translateLookupDescription("FlowStatus", dao.getCard(ps.getId()).getCode(), ps.getDescription())
        ))));
    }

}

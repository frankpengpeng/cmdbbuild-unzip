package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.data.filter.utils.CmdbSorterUtils;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelper;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.v2.endpoint.CardWsV2.mapSorterAttributeNames;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("cql/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CqlWsV2 {

    private final UserCardService cardService;
    private final DaoService dao;
    private final CardWsSerializationHelper helper;

    public CqlWsV2(UserCardService cardService, DaoService dao, CardWsSerializationHelper helper) {
        this.cardService = checkNotNull(cardService);
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@QueryParam(FILTER) String filter, @QueryParam(SORT) String sort, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        CmdbFilter cmFilter = CmdbFilterUtils.parseFilter(filter);
        CmdbSorter sorter = mapSorterAttributeNames(CmdbSorterUtils.parseSorter(sort));

        List<Card> cards = dao.selectAll()
                .orderBy(sorter)
                .where(cmFilter)
                .paginate(offset, limit)
                .getCards();
        return map("data", cards.stream().map(helper::serializeCard).collect(toList()));
    }

}

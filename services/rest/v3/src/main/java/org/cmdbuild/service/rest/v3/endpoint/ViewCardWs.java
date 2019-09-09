package org.cmdbuild.service.rest.v3.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dao.core.q3.DaoService;

import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.data.filter.utils.CmdbFilterUtils.parseFilter;
import static org.cmdbuild.data.filter.utils.CmdbSorterUtils.parseSorter;
import org.cmdbuild.report.SysReportService;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import org.cmdbuild.service.rest.v3.endpoint.CardWs.WsCardData;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelper;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF_GOTOPAGE;
import static org.cmdbuild.service.rest.v3.endpoint.CardWs.mapSorterAttributeNames;
import static org.cmdbuild.service.rest.v3.endpoint.ProcessActivityWs.handlePositionOfAndGetMeta;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewService;
import static org.cmdbuild.view.ViewUtils.checkViewIsFilterView;

@Path("views/{viewId}/cards/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ViewCardWs {

    private final DaoService dao;
    private final CardWsSerializationHelper helper;
    private final ViewService viewService;
    private final CardFilterService filterService;
    private final SysReportService reportService;

    public ViewCardWs(DaoService dao, CardWsSerializationHelper helper, ViewService viewService, CardFilterService filterService, SysReportService reportService) {
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
        this.viewService = checkNotNull(viewService);
        this.filterService = checkNotNull(filterService);
        this.reportService = checkNotNull(reportService);
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("viewId") String viewId, WsCardData data) {
        View view = viewService.getForCurrentUserByName(viewId);
        checkViewIsFilterView(view);
        Classe targetClass = dao.getClasse(view.getSourceClass());
        Card card = CardImpl.buildCard(targetClass, data.getValues());
        card = dao.create(card);//TODO check filter
        return response(helper.serializeCard(card));
    }

    @GET
    @Path("{" + CARD_ID + "}/")
    public Object readOne(@PathParam("viewId") String viewId, @PathParam(CARD_ID) Long cardId) {
        View view = viewService.getForCurrentUserByName(viewId);
        checkViewIsFilterView(view);
        Card card = dao.getCard(view.getSourceClass(), cardId);//TODO apply filter
        return response(helper.serializeCard(card));
    }

    @GET
    @Path(EMPTY)
    public Object readMany(
            @PathParam("viewId") String viewId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(POSITION_OF) Long positionOf,
            @QueryParam(POSITION_OF_GOTOPAGE) @DefaultValue(TRUE) Boolean goToPage
    ) {
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder()
                .withFilter(parseFilter(getFilterOrNull(filterStr)))//TODO map filter attribute names; 
                .withSorter(mapSorterAttributeNames(parseSorter(sort)))
                .withPaging(offset, limit)
                .withPositionOf(positionOf, goToPage)
                .build();
        View view = viewService.getForCurrentUserByName(viewId);

        switch (view.getType()) {
            case FILTER:
                PagedElements<Card> cards = viewService.getCards(view, queryOptions);
                return response(cards.stream().map(helper::serializeCard).collect(toList()), cards.totalSize(), handlePositionOfAndGetMeta(queryOptions, cards));
            case SQL:
                PagedElements<Map<String, Object>> records = viewService.getRecords(view, queryOptions);
                return response(records.stream().map(r -> map().accept(m -> helper.addCardValuesAndDescriptionsAndExtras(viewService.getAttributesForView(view), r::get, m::put))).collect(toList()), records.totalSize(), handlePositionOfAndGetMeta(queryOptions, records));
            default:
                throw unsupported("unsupported view type = %s", view.getType());
        }
    }

    @PUT
    @Path("{" + CARD_ID + "}/")
    public Object update(@PathParam("viewId") String viewId, @PathParam(CARD_ID) Long cardId, WsCardData data) {
        View view = viewService.getForCurrentUserByName(viewId);
        checkViewIsFilterView(view);
        Classe classe = dao.getClasse(view.getSourceClass());
        Card card = CardImpl.builder()
                .withType(classe)
                .withAttributes(data.getValues())
                .withId(cardId)
                .build();
        card = dao.update(card);//TODO check filter
        return response(helper.serializeCard(card));
    }

    @DELETE
    @Path("{" + CARD_ID + "}/")
    public Object delete(@PathParam("viewId") String viewId, @PathParam(CARD_ID) Long cardId) {
        View view = viewService.getForCurrentUserByName(viewId);
        checkViewIsFilterView(view);
        dao.delete(view.getSourceClass(), cardId);//TODO check filter
        return success();
    }

    @GET
    @Path("/cards/{cardId}/print/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler readOne(@PathParam("viewId") String viewId, @PathParam("cardId") Long cardId, @QueryParam(EXTENSION) String extension) {
        View view = viewService.getForCurrentUserByName(viewId);
        Card card = viewService.getCardById(view, cardId);
        return reportService.executeCardReport(card, reportExtFromString(extension));
    }

    @Nullable//TODO duplicate code
    private String getFilterOrNull(@Nullable String filter) {
        return CardWs.getFilterOrNull(filter, (id) -> filterService.getById(id).getConfiguration());
    }
}

package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Function;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Lists.transform;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import static java.lang.String.format;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.stream.Collectors.toList;
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
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.classe.access.UserCardAccess;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.utils.PagedElements.hasLimit;
import static org.cmdbuild.common.utils.PagedElements.isPaged;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import org.cmdbuild.data.filter.utils.CmdbSorterUtils;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirectionUtils.parseRelationDirection;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_USER;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelper;
import static org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelper.ExtendedCardOptions.INCLUDE_MODEL;
import static org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelper.ExtendedCardOptions.NONE;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDTENANT;
import static org.cmdbuild.dao.core.q3.DaoService.COUNT;
import static org.cmdbuild.dao.core.q3.WhereOperator.ISNOTNULL;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_MANY_TO_MANY;
import static org.cmdbuild.dao.entrytype.Domain.DOMAIN_MANY_TO_ONE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF_GOTOPAGE;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.data.filter.SorterProcessor.sorted;

@Path("{a:classes}/{" + CLASS_ID + "}/{b:cards}/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardWs {

    public final static Map<String, String> SYSTEM_ATTR_NAME_MAPPING = ImmutableMap.of("_id", ATTR_ID,
            "_type", ATTR_IDCLASS,
            "_user", ATTR_USER,
            "_tenant", ATTR_IDTENANT,
            "_beginDate", ATTR_BEGINDATE
    );

    private final UserClassService classService;
    private final UserCardService cardService;
    private final DaoService dao;
    private final CardFilterService filterService;
    private final CardWsSerializationHelper helper;

    public CardWs(UserClassService classService, UserCardService cardService, DaoService dao, CardFilterService filterService, CardWsSerializationHelper helper) {
        this.classService = checkNotNull(classService);
        this.cardService = checkNotNull(cardService);
        this.dao = checkNotNull(dao);
        this.filterService = checkNotNull(filterService);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path("{" + CARD_ID + "}/")
    public Object readOne(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, @QueryParam("includeModel") Boolean includeModel) {
//		UserCardAccess cardAccess = cardService.getUserCardAccess(classId);
//		Card card = cardAccess.addCardAccessPermissionsFromSubfilterMark(dao.selectAll().from(classId)
//				.accept(cardAccess.addSubsetFilterMarkersToQueryVisitor())
//				.where(cardAccess.getWholeClassFilter())
//				.where(ATTR_ID, EQ, checkNotNull(cardId))
//				.getCard());
//		checkArgument(card.getType().hasServiceReadPermission(), "user not authorized to access card %s.%s", classId, cardId);
        Card card = cardService.getUserCard(classId, cardId);
        return response(helper.serializeCard(card, defaultIfNull(includeModel, false) ? INCLUDE_MODEL : NONE));
    }

    @GET
    @Path(EMPTY)
    public Object readMany(
            @PathParam(CLASS_ID) String classId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(POSITION_OF) Long positionOfCard,
            @QueryParam(POSITION_OF_GOTOPAGE) @DefaultValue(TRUE) Boolean goToPage,
            @QueryParam("forDomain_name") String forDomainName,
            @QueryParam("forDomain_direction") String forDomainDirection,
            @QueryParam("forDomain_originId") Long forDomainOriginId,
            @QueryParam("distinct") String distinctAttribute,
            @QueryParam("count") String countAttribute) {

        CmdbFilter filter = CmdbFilterUtils.parseFilter(getFilterOrNull(filterStr));//TODO map filter attribute names
        CmdbSorter sorter = mapSorterAttributeNames(CmdbSorterUtils.parseSorter(sort));

        UserCardAccess cardAccess = cardService.getUserCardAccess(classId);
        CmdbFilter cardAccessFilter = cardAccess.getWholeClassFilter();

        Domain forDomain = isBlank(forDomainName) ? null : dao.getDomain(forDomainName);

        filter = filter.and(cardAccessFilter);

        FluentMap meta = map();

        if (isNotNullAndGtZero(positionOfCard)) {
            checkArgument(hasLimit(limit), "must set valid 'limit' along with 'positionOf'");
            offset = firstNonNull(offset, 0l);
            Long rowNumber = dao.selectRowNumber().where(ATTR_ID, EQ, positionOfCard).then()
                    .from(classId)
                    .orderBy(sorter)
                    .where(filter)
                    .build().getRowNumberOrNull();
            Map positionMeta;
            if (rowNumber == null) {
                positionMeta = map("found", false);
            } else {
                long positionInPage = rowNumber % limit;
                long pageOffset = rowNumber - positionInPage;
                if (goToPage) {
                    offset = pageOffset;
                }
                positionMeta = map("found", true,
                        "positionInPage", positionInPage,
                        "positionInTable", rowNumber,
                        "pageOffset", pageOffset);
            }
            meta.put("positions", map(positionOfCard, positionMeta), START, offset, LIMIT, limit);
        }

        if (isNotBlank(distinctAttribute)) {
            //TODO order, card level permissions
            boolean count;
            if (isNotBlank(countAttribute)) {
                checkArgument(equal(countAttribute, distinctAttribute), "count attribute must match distinct attribute");
                count = true;
            } else {
                count = false;
            }
            List<Map<String, Object>> list = (List) dao.selectDistinct(distinctAttribute).accept(q -> {
                if (count) {
                    q.selectCount();
                }
            }).from(classId).where(filter).where(distinctAttribute, ISNOTNULL).run().stream().map(r -> helper.serializeAttributeValue(classService.getUserClass(classId), distinctAttribute, r.asMap()).accept(m -> {
                if (count) {
                    m.put("_count", r.get(COUNT, Long.class));
                }
            })).collect(toList());
            list = sorted(list, sorter);
            return response(paged(list, offset, limit));
        } else {

            List<Card> cards = dao.selectAll()
                    .from(classId)
                    .orderBy(sorter)
                    .where(filter)
                    .accept(cardAccess.addSubsetFilterMarkersToQueryVisitor())
                    .accept(q -> {
                        if (forDomain != null) {
                            checkNotNullAndGtZero(forDomainOriginId);
                            RelationDirection dir = parseRelationDirection(forDomainDirection);
                            if (equal(dir, RD_DIRECT)) {//TODO move in dao (?), add for processes
                                q.selectExpr("_fordomain_hasthisrelation", "(Q3_MASTER.\"Id\" IN (SELECT \"IdObj2\" FROM %s WHERE \"IdObj1\" = %s AND \"Status\" = 'A'))", entryTypeToSqlExpr(forDomain), forDomainOriginId, entryTypeToSqlExpr(dao.getClasse(classId)));
                                q.selectExpr("_fordomain_hasanyrelation", "(Q3_MASTER.\"Id\" IN (SELECT \"IdObj2\" FROM %s WHERE \"Status\" = 'A'))", entryTypeToSqlExpr(forDomain), forDomainOriginId);
                            } else {
                                q.selectExpr("_fordomain_hasthisrelation", "(Q3_MASTER.\"Id\" IN (SELECT \"IdObj1\" FROM %s WHERE \"IdObj2\" = %s AND \"Status\" = 'A'))", entryTypeToSqlExpr(forDomain), forDomainOriginId, entryTypeToSqlExpr(dao.getClasse(classId)));
                                q.selectExpr("_fordomain_hasanyrelation", "(Q3_MASTER.\"Id\" IN (SELECT \"IdObj1\" FROM %s WHERE \"Status\" = 'A'))", entryTypeToSqlExpr(forDomain), forDomainOriginId);
                            }
                        }
                    })
                    .paginate(offset, limit)
                    .getCards();

            long total;
            if (isPaged(offset, limit)) {
                total = dao.selectCount()
                        .from(classId)
                        .where(filter)
                        .getCount();
            } else {
                total = cards.size();
            }

            cards = list(transform(cards, cardAccess::addCardAccessPermissionsFromSubfilterMark));

            return response(cards.stream().map(c -> helper.serializeCard(c).accept((m) -> {
                if (forDomain != null) {
                    RelationDirection dir = parseRelationDirection(forDomainDirection);
                    Domain dom = forDomain.getThisDomainWithDirection(dir);
                    boolean hasAnyRelation = c.get("_fordomain_hasanyrelation", Boolean.class),
                            hasThisRelation = c.get("_fordomain_hasthisrelation", Boolean.class),
                            available = (!hasThisRelation || dom.hasDomainKeyAttrs()) && dom.isDomainForTargetClasse(c.getType()) && (!hasAnyRelation || (dom.hasCardinality(DOMAIN_MANY_TO_ONE) || dom.hasCardinality(DOMAIN_MANY_TO_MANY)));
                    m.put(format("_%s_available", forDomain.getName()), available, format("_%s_hasrelation", forDomain.getName()), hasThisRelation);
                }
            })).collect(toList()), total, meta);
        }
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam(CLASS_ID) String classId, WsCardData data) {
        return response(helper.serializeCard(cardService.createCard(classId, data.getValues())));
    }

    @PUT
    @Path("{" + CARD_ID + "}/")
    public Object update(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId, WsCardData data) {
        return response(helper.serializeCard(cardService.updateCard(classId, cardId, data.getValues())));
    }

    @DELETE
    @Path("{" + CARD_ID + "}/")
    public Object delete(@PathParam(CLASS_ID) String classId, @PathParam(CARD_ID) Long cardId) {
        cardService.deleteCard(classId, cardId);
        return success();
    }

    public static CmdbSorter mapSorterAttributeNames(CmdbSorter sorter) {//TODO use this everywhere
        return sorter.mapAttributeNames(SYSTEM_ATTR_NAME_MAPPING);
    }

    @Nullable
    private String getFilterOrNull(@Nullable String filter) {
        return getFilterOrNull(filter, (id) -> filterService.getById(id).getConfiguration());
    }

    @Nullable
    public static String getFilterOrNull(@Nullable String filter, Function<Long, String> filterRepo) {
        if (isBlank(filter)) {
            return null;
        } else {
            JsonPrimitive filterId = new JsonParser().parse(filter).getAsJsonObject().getAsJsonPrimitive("_id");
            if (filterId != null && !filterId.isJsonNull()) {
                return filterRepo.apply(filterId.getAsLong());
            } else {
                return filter;
            }
        }
    }

//	public static FluentMap<String, Object> mapClassValues(Map<String, Object> values, Map<String, String>... otherMappings) {
//		Map<String, String> mapping = map(SYSTEM_ATTR_NAME_MAPPING).accept((m) -> list(otherMappings).forEach(m::putAll));
//		return values.entrySet().stream().collect(toMap(e -> mapping.getOrDefault(e.getKey(), e.getKey()), Entry::getValue));
//	}
    public static FluentMap<String, Object> mapClassValues(Map<String, Object> values) {
        return values.entrySet().stream().collect(toMap(e -> SYSTEM_ATTR_NAME_MAPPING.getOrDefault(e.getKey(), e.getKey()), Entry::getValue));
    }

    public static List<String> mapAttrNames(List<String> values) {
        return values.stream().map(a -> SYSTEM_ATTR_NAME_MAPPING.getOrDefault(a, a)).collect(toList());
    }

    public static class WsCardData {

        private final Map<String, Object> values;

        @JsonCreator
        public WsCardData(Map<String, Object> values) {
            this.values = mapClassValues(checkNotNull(values)).immutable();
        }

        public Map<String, Object> getValues() {
            return values;
        }

    }

}

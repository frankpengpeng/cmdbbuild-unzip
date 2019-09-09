package org.cmdbuild.service.rest.v3.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.DomainDefinitionImpl;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.utils.CmdbFilterUtils;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ACTIVE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DESTINATION;

import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DOMAIN_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SOURCE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.data.filter.AttributeFilterProcessor;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.auth.login.AuthorityConst.HAS_ADMIN_ACCESS_AUTHORITY;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.data.filter.FilterType;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import org.cmdbuild.service.rest.v3.serializationhelpers.DomainSerializationHelper;

@Path("domains/")
@Produces(APPLICATION_JSON)
public class DomainWs {

    private final DomainRepository domainRepository;
    private final UserDomainService domainService;
    private final DaoService dao;
    private final DomainSerializationHelper domainSerializationHelper;

    public DomainWs(DomainRepository domainRepository, UserDomainService domainService, DaoService dao, DomainSerializationHelper domainSerializationHelper) {
        this.domainRepository = checkNotNull(domainRepository);
        this.domainService = checkNotNull(domainService);
        this.dao = checkNotNull(dao);
        this.domainSerializationHelper = checkNotNull(domainSerializationHelper);
    }

    /**
     * get domains
     *
     * @param filterStr
     * @param limit
     * @param offset
     * @param includeFullDetails if <i>true</i> return full domain details
     * @return
     */
    @GET
    @Path(EMPTY)
    public Object readAll(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @QueryParam(FILTER) String filterStr, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(EXT) Boolean includeFullDetails) {
        List<Domain> domains = isAdminViewMode(viewMode) ? domainService.getUserDomains() : domainService.getActiveUserDomains();
        CmdbFilter filter = CmdbFilterUtils.parseFilter(filterStr);
        filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
        if (filter.hasAttributeFilter()) {
            domains = AttributeFilterProcessor.<Domain>builder()
                    .withKeyToValueFunction((key, domain) -> {
                        switch (key) {
                            case SOURCE:
                                return ((Domain) domain).getSourceClass();
                            case DESTINATION:
                                return ((Domain) domain).getTargetClass();
                            case ACTIVE:
                                return Boolean.toString(((Domain) domain).isActive());
                            case "cardinality":
                                return ((Domain) domain).getCardinality();
                            default:
                                throw new IllegalArgumentException("unsupported filter key = " + key);
                        }
                    })
                    .withConditionEvaluatorFunction(new AttributeFilterProcessor.ConditionEvaluatorFunction() {

                        @Override
                        public boolean evaluate(AttributeFilterCondition condition, Object value) {
                            switch (condition.getOperator()) {
                                case EQUAL:
                                    return equal(valueToString(value), condition.getSingleValue());
                                case IN:
                                    return condition.getValues().contains(valueToString(value));
                                case CONTAIN:
                                    return ((Classe) value).equalToOrAncestorOf(dao.getClasse(condition.getSingleValue())); //TODO filter also 
                                default:
                                    throw new IllegalArgumentException("unsupported operator = " + condition.getOperator());
                            }
                        }

                        private String valueToString(Object value) {
                            if (value instanceof Classe) {
                                return ((Classe) value).getName();
                            } else {
                                return CmStringUtils.toStringOrNull(value);
                            }
                        }
                    })
                    .withFilter(filter.getAttributeFilter())
                    .filter(domains);
        }
        List list = paged(domains, offset, limit).stream().map(equal(includeFullDetails, Boolean.TRUE) ? domainSerializationHelper::serializeDetailedDomain : domainSerializationHelper::serializeBasicDomain).collect(toList());
        return response(list, domains.size());
    }

    /**
     * get domain details
     *
     * @param domainId
     * @return
     */
    @GET
    @Path("{" + DOMAIN_ID + "}/")
    public Object read(@PathParam(DOMAIN_ID) String domainId) {
        Domain domain = domainService.getUserDomain(domainId);
        return response(domainSerializationHelper.serializeDetailedDomain(domain));
    }

    @POST
    @Path(EMPTY)
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object create(WsDomainData data) {
        Domain domain = domainRepository.createDomain(toDomainDefinition(data).build());
        return response(domainSerializationHelper.serializeDetailedDomain(domain));
    }

    @PUT
    @Path("{domainId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object update(@PathParam("domainId") String domainId, WsDomainData data) {
        Domain domain = domainRepository.getDomain(domainId);
        domain = domainRepository.updateDomain(toDomainDefinition(data).withOid(domain.getId()).build());
        return response(domainSerializationHelper.serializeDetailedDomain(domain));
    }

    @DELETE
    @Path("{domainId}/")
    @PreAuthorize(HAS_ADMIN_ACCESS_AUTHORITY)
    public Object delete(@PathParam("domainId") String domainId) {
        domainRepository.deleteDomain(domainRepository.getDomain(domainId));
        return success();
    }

    private DomainDefinitionImpl.DomainDefinitionImplBuilder toDomainDefinition(WsDomainData domainData) {
        return DomainDefinitionImpl.builder()
                .withName(domainData.name)
                .withSourceClass(dao.getClasse(domainData.source))
                .withTargetClass(dao.getClasse(domainData.destination))
                .withMetadata(DomainMetadataImpl.builder()
                        .withCardinality(domainData.cardinality)
                        .withDescription(domainData.description)
                        .withDirectDescription(domainData.descriptionDirect)
                        .withInverseDescription(domainData.descriptionInverse)
                        .withIsActive(domainData.isActive)
                        .withIsMasterDetail(domainData.isMasterDetail)
                        .withDisabledSourceDescendants(domainData.disabledSourceDescendants)
                        .withDisabledTargetDescendants(domainData.disabledDestinationDescendants)
                        .withMasterDetailDescription(domainData.descriptionMasterDetail)
                        .withMasterDetailFilter(domainData.filterMasterDetail)
                        .withSourceIndex(domainData.indexDirect)
                        .withTargetIndex(domainData.indexInverse)
                        .withInline(domainData.inline)
                        .withDefaultClosed(domainData.defaultClosed)
                        .build());
    }

    public static class WsDomainData {

        private final String name, description, source, destination, cardinality, descriptionDirect, descriptionInverse, descriptionMasterDetail, filterMasterDetail;
        private final Integer indexDirect, indexInverse;
        private final boolean isActive, isMasterDetail;
        private final Boolean inline, defaultClosed;
        private final List<String> disabledSourceDescendants, disabledDestinationDescendants;

        public WsDomainData(@JsonProperty("source") String source,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("destination") String destination,
                @JsonProperty("cardinality") String cardinality,
                @JsonProperty("descriptionDirect") String descriptionDirect,
                @JsonProperty("descriptionInverse") String descriptionInverse,
                @JsonProperty("indexDirect") Integer indexDirect,
                @JsonProperty("indexInverse") Integer indexInverse,
                @JsonProperty("descriptionMasterDetail") String descriptionMasterDetail,
                @JsonProperty("filterMasterDetail") String filterMasterDetail,
                @JsonProperty("disabledSourceDescendants") List<String> disabledSourceDescendants,
                @JsonProperty("disabledDestinationDescendants") List<String> disabledDestinationDescendants,
                @JsonProperty("active") boolean isActive,
                @JsonProperty("isMasterDetail") boolean isMasterDetail,
                @JsonProperty("inline") Boolean inline,
                @JsonProperty("defaultClosed") Boolean defaultClosed) {
            this.source = checkNotBlank(source);
            this.destination = checkNotBlank(destination);
            this.cardinality = checkNotBlank(cardinality);
            this.descriptionDirect = descriptionDirect;
            this.descriptionInverse = descriptionInverse;
            this.indexDirect = indexDirect;
            this.indexInverse = indexInverse;
            this.descriptionMasterDetail = descriptionMasterDetail;
            this.filterMasterDetail = filterMasterDetail;
            this.isActive = isActive;
            this.isMasterDetail = isMasterDetail;
            this.inline = inline;
            this.defaultClosed = defaultClosed;
            this.name = name;
            this.description = description;
            this.disabledSourceDescendants = disabledSourceDescendants;
            this.disabledDestinationDescendants = disabledDestinationDescendants;
        }

    }

}

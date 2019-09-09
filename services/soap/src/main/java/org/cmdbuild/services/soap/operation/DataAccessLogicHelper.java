package org.cmdbuild.services.soap.operation;

import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.isNumeric;
import static org.cmdbuild.services.soap.utils.SoapToJsonUtilsService.toJsonArray;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.activation.DataHandler;
import javax.activation.DataSource;

import org.apache.commons.lang3.ObjectUtils;
import org.cmdbuild.auth.login.AuthenticationStore;
import org.cmdbuild.common.Constants;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.CardStatus;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import org.cmdbuild.dao.query.clause.QueryDomain;
import org.cmdbuild.dao.query.clause.QueryDomain.Source;
import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationInfo;
import org.cmdbuild.dao.postgres.legacy.relationquery.GetRelationHistoryResponse;
import org.cmdbuild.dao.postgres.legacy.relationquery.DomainInfo;
import org.cmdbuild.dao.postgres.legacy.relationquery.GetRelationListResponse;
import org.cmdbuild.logic.data.QueryOptionsImpl;
import org.cmdbuild.data2.impl.RelationDTO;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.services.soap.serializer.MenuSchemaSerializer;
import org.cmdbuild.services.soap.structure.AttributeSchema;
import org.cmdbuild.services.soap.structure.ClassSchema;
import org.cmdbuild.services.soap.structure.MenuSchema;
import org.cmdbuild.services.soap.types.Attribute;
import org.cmdbuild.services.soap.types.CQLParameter;
import org.cmdbuild.services.soap.types.CQLQuery;
import org.cmdbuild.services.soap.types.Card.ValueSerializer;
import org.cmdbuild.services.soap.types.CardExt;
import org.cmdbuild.services.soap.types.CardList;
import org.cmdbuild.services.soap.types.CardListExt;
import org.cmdbuild.services.soap.types.Metadata;
import org.cmdbuild.services.soap.types.Order;
import org.cmdbuild.services.soap.types.Query;
import org.cmdbuild.services.soap.types.Reference;
import org.cmdbuild.services.soap.types.Relation;
import org.cmdbuild.services.soap.types.RelationExt;
import org.cmdbuild.services.soap.types.Report;
import org.cmdbuild.services.soap.types.ReportParams;
import org.cmdbuild.services.soap.utils.DateTimeSerializer;
import org.cmdbuild.workflow.model.WorkflowException;
import org.joda.time.DateTime;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.compose;
import static com.google.common.base.Predicates.equalTo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import static java.lang.Math.toIntExact;
import static java.util.stream.Collectors.toList;
import javax.annotation.PostConstruct;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.data2.api.DataAccessService;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.services.soap.utils.SoapToJsonUtilsService;
import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
//import static org.cmdbuild.spring.configuration.BeanNamesAndQualifiers.USER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.dao.beans.CardIdAndClassName;
import static org.cmdbuild.dao.beans.CardIdAndClassNameImpl.card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import org.cmdbuild.dao.beans.RelationImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.postgres.legacy.relationquery.DomainWithSource;
import org.cmdbuild.dao.postgres.legacy.relationquery.GetRelationListResponseImpl;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationInfoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.lookup.LookupRepository;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.postgres.ClasseHierarchyService;
import org.cmdbuild.dao.query.clause.QueryRelation;
import org.cmdbuild.menu.MenuService;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.dao.view.DataView;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.report.ReportProcessor;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

@Component
public class DataAccessLogicHelper {

    private static final String ACTIVITY_DESCRIPTION_ATTRIBUTE = "ActivityDescription";
    private static final String INVALID_ACTIVITY_DESCRIPTION = EMPTY;

    private static final Function<Attribute, String> ATTRIBUTE_NAME = (Attribute input) -> input.getName();

    private static final Attribute[] NO_ATTRIBUTES = new Attribute[]{};
    private static final ReportParams[] NO_PARAMS = new ReportParams[]{};

    private static final Function<ReportParams, String> REPORT_PARAM_KEY = (ReportParams input) -> input.getKey();

    private static final Function<ReportParams, Object> REPORT_PARAM_VALUE = (ReportParams input) -> input.getValue();

    private static final List<org.cmdbuild.dao.entrytype.Attribute> EMPTY_ATTRIBUTES = emptyList();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DaoService dao;
    @Autowired
    private DataView dataView;
    @Autowired
    private DataAccessService dataAccessLogic;
    @Autowired
    private WorkflowService workflowLogic;
    @Autowired
    private OperationUserSupplier operationUserSupplier;
    @Autowired
    @Qualifier(SYSTEM_LEVEL_ONE)
    private javax.sql.DataSource dataSource;
    @Autowired
    private AuthenticationStore authenticationStore;
    @Autowired
    private CoreConfiguration configuration;
    @Autowired
    private SoapToJsonUtilsService soapToJsonUtilsService;
    @Autowired
    private ReportService reportLogic;
    @Autowired
    private MenuService menuStore;
    @Autowired
    private LookupRepository lookupStore;
    @Autowired
    private ClasseHierarchyService classHierarchyService;

    private SerializationStuff serializationUtils;
    @Autowired
    private WorkflowLogicHelper workflowLogicHelper;

    @PostConstruct
    public void init() {
        serializationUtils = new SerializationStuff(dataView);
    }

    public void setMenuStore(MenuService menuStore) {
        this.menuStore = menuStore;
    }

    public void setLookupStore(LookupRepository lookupStore) {
        this.lookupStore = lookupStore;
    }

    public AttributeSchema[] getAttributeList(String className) {
        logger.info("getting attributes schema for class '{}'", className);
        List<AttributeSchema> attributes = Lists.newArrayList();
        for (org.cmdbuild.dao.entrytype.Attribute cmAttribute : dataAccessLogic.findClass(className).getCoreAttributes()) {
            attributes.add(serializationUtils.serialize(cmAttribute));
        }
        return attributes.toArray(new AttributeSchema[attributes.size()]);
    }

    public long createCard(org.cmdbuild.services.soap.types.Card card) {
        return dataAccessLogic.createCard(transform(card));
    }

    public boolean updateCard(org.cmdbuild.services.soap.types.Card card) {
        dataAccessLogic.updateCard(transform(card));
        return true;
    }

//	public boolean deleteCard(String className, long cardId) {
//		dataAccessLogic.deleteCard(className, cardId);
//		return true;
//	}
    public boolean createRelation(Relation relation) {
        dao.create(RelationImpl.builder()
                .withType(dao.getDomain(relation.getDomainName()))
                .withSourceCard(card(relation.getClass1Name(), relation.getCard1Id()))
                .withTargetCard(card(relation.getClass2Name(), relation.getCard2Id()))
                .build());
        return true;
    }

    public boolean createRelationWithAttributes(Relation relation, List<Attribute> attributes) {
        RelationDTO relationDTO = transform(relation);
        Domain domain = dataView.findDomain(relation.getDomainName());
        relationDTO.relationAttributeToValue = transform(attributes, domain);
        dataAccessLogic.createRelations(relationDTO);
        return true;
    }

    public List<Attribute> getRelationAttributes(Relation relation) {
        List<Attribute> relationAttributes = Lists.newArrayList();
        Domain domain = dataView.findDomain(relation.getDomainName());
        CMRelation fetchedRelation = fetch(relation);
        for (Entry<String, Object> entry : fetchedRelation.getRawValues()) {
            CardAttributeType<?> attributeType = domain.getAttributeOrNull(entry.getKey()).getType();
            Attribute attribute = new Attribute();
            attribute.setName(entry.getKey());
            attribute.setValue(entry.getValue() != null ? entry.getValue().toString() : EMPTY);
            if (attributeType instanceof LookupAttributeType) {
                if (entry.getValue() != null) {
                    IdAndDescriptionImpl cardReference = (IdAndDescriptionImpl) entry.getValue();
                    attribute.setCode(cardReference.getId() != null ? cardReference.getId().toString() : null);
                    attribute.setValue(fetchLookupDecription((cardReference.getId())));
                } else {
                    attribute.setCode(EMPTY);
                    attribute.setValue(EMPTY);
                }
            } else if (attributeType instanceof DateAttributeType
                    || //
                    attributeType instanceof TimeAttributeType
                    || //
                    attributeType instanceof DateTimeAttributeType) {
                attribute.setValue(org.cmdbuild.services.soap.types.Card.LEGACY_VALUE_SERIALIZER
                        .serializeValueForAttribute(attributeType, entry.getKey(), entry.getValue()));
            }
            relationAttributes.add(attribute);
        }
        return relationAttributes;
    }

    public void setRelationAttributes(Relation relation, Collection<Attribute> attributes) {
        CMRelation _relation = fetch(relation);
        RelationDTO relationDTO = new RelationDTO();
        relationDTO.relationId = _relation.getId();
        relationDTO.domainName = relation.getDomainName();
        relationDTO.master = "_1";
        relationDTO.addSourceCard((relation.getCard1Id()), relation.getClass1Name());
        relationDTO.addDestinationCard((relation.getCard2Id()), relation.getClass2Name());
        relationDTO.relationAttributeToValue
                = transformValues(uniqueIndex(attributes, input -> input.getName()), input -> input.getValue());
        dataAccessLogic.updateRelation(relationDTO);
    }

    /**
     * {@link Relation} does not have an {@code id} attribute, so that, just for
     * not change it, we must fetch relation according with {@link Relation}
     * data.
     */
    private CMRelation fetch(Relation relation) {
        Classe sourceClass = dataView.findClasse(relation.getClass1Name());
        Classe destinationClass = dataView.findClasse(relation.getClass2Name());
        Domain domain = dataView.findDomain(relation.getDomainName());
        return dataAccessLogic.getRelation((relation.getCard1Id()), (relation.getCard2Id()),
                domain, sourceClass, destinationClass);
    }

    public boolean deleteRelation(Relation relation) {
        Domain domain = dataView.findDomain(relation.getDomainName());
        DomainWithSource dom = DomainWithSource.create(domain.getId(), Source._1.toString());
//		Card srcCard = Card.newInstance() //
//				.withClassName(relation.getClass1Name()) //
//				.withId((relation.getCard1Id())) //
//				.build();
        GetRelationListResponse response = dataAccessLogic.getRelationList(card(relation.getClass1Name(), (long) relation.getCard1Id()), dom);
        for (DomainInfo domainInfo : response) {
            for (RelationInfo relationInfo : domainInfo) {
                if (relationInfo.getTargetId().equals((relation.getCard2Id()))) {
                    RelationDTO relationToDelete = transform(relation);
                    relationToDelete.relationId = relationInfo.getRelationId();
                    dataAccessLogic.deleteRelation(relationToDelete.domainName, relationToDelete.relationId);
                }
            }
        }
        return true;
    }

    private Card transform(org.cmdbuild.services.soap.types.Card card) {
        Classe entryType = dataView.getClasse(card.getClassName());
        Card cardModel = CardImpl.builder().withType(entryType).withAttributes(card.getAttributeList().stream().collect(CmMapUtils.toMap(Attribute::getName, Attribute::getValue))).withId((card.getId())).build();
        return cardModel;
    }

    private List<Attribute> attributesOf(org.cmdbuild.services.soap.types.Card card) {
        return card.getAttributeList();
    }

    private Map<String, Object> transform(List<Attribute> attributes, EntryType entryType) {
        Map<String, Object> keysAndValues = Maps.newHashMap();
        for (Attribute attribute : attributes) {
            org.cmdbuild.dao.entrytype.Attribute _attribute = entryType.getAttributeOrNull(attribute.getName());
            if (_attribute == null) {
                logger.warn("missing attribute '{}' for type '{}'", attribute.getName(), entryType.getName());
                continue;
            }
            CardAttributeType<?> attributeType = _attribute.getType();
            String name = attribute.getName();
            Object value = attribute.getValue();
            if (attributeType instanceof LookupAttributeType) {
                LookupAttributeType lookupAttributeType = (LookupAttributeType) attributeType;
                String lookupTypeName = lookupAttributeType.getLookupTypeName();
                Long lookupId = null;
                if (isNotBlank((String) value) && isNumeric((String) value)) {
                    if (existsLookup(lookupTypeName, Long.parseLong((String) value))) {
                        lookupId = Long.parseLong((String) value);
                    }
                } else {
                    Iterable<Lookup> lookupList = lookupStore.getAll();
                    for (Lookup lookup : lookupList) {
//						if (lookup.active()
                        if (lookup.getType().getName().equals(lookupTypeName)
                                && //
                                lookup.getDescription() != null
                                && //
                                ObjectUtils.equals(lookup.getDescription(), value)) {
                            lookupId = lookup.getId();
                            break;
                        }
                    }
                }
                value = lookupId == null ? null : lookupId.toString();
            } else if (attributeType instanceof DateAttributeType
                    || //
                    attributeType instanceof TimeAttributeType
                    || //
                    attributeType instanceof DateTimeAttributeType) {
                value = new DateTimeSerializer(attribute.getValue()).getValue();
            }
            keysAndValues.put(name, value);
        }
        return keysAndValues;
    }

    private boolean existsLookup(String lookupTypeName, Long lookupId) {
        Iterable<Lookup> lookupList = lookupStore.getAll();
        for (Lookup lookup : lookupList) {
            if (lookup.getType().getName().equals(lookupTypeName) && lookup.getId().equals(lookupId)) {
                return true;
            }
        }
        return false;
    }

    private RelationDTO transform(Relation relation) {
        RelationDTO relationDTO = new RelationDTO();
        relationDTO.domainName = relation.getDomainName();
        relationDTO.master = Source._1.toString();
        relationDTO.addSourceCard((relation.getCard1Id()), relation.getClass1Name());
        relationDTO.addDestinationCard((relation.getCard2Id()), relation.getClass2Name());
        return relationDTO;
    }

    private Relation transform(RelationInfo relationInfo, long source) {
        QueryDomain queryDomain = relationInfo.getQueryDomain();
        Domain domain = queryDomain.getDomain();
        Relation output = new Relation();
        output.setBeginDate(relationInfo.getRelationBeginDate().toGregorianCalendar());
        DateTime endDate = relationInfo.getRelationEndDate();
        output.setEndDate(endDate != null ? endDate.toGregorianCalendar() : null);
        output.setStatus(CardStatus.ACTIVE.value());
        output.setDomainName(domain.getName());
        String targetName = relationInfo.getTargetCard().getType().getName();
        if (queryDomain.getQuerySource().equals(Source._1.toString())) {
            output.setClass1Name(domain.getSourceClass().getName());
            output.setClass2Name(targetName);
        } else {
            output.setClass1Name(targetName);
            output.setClass2Name(domain.getTargetClass().getName());
        }
        if (queryDomain.getQuerySource().equals(Source._1.toString())) {
            output.setCard1Id(relationInfo.getSourceId());
            output.setCard2Id(relationInfo.getTargetId());
        } else {
            output.setCard1Id(relationInfo.getTargetId());
            output.setCard2Id(relationInfo.getSourceId());
        }
        return output;
    }

    private RelationExt transform(RelationInfo relationInfo, Relation input) {
        QueryDomain queryDomain = relationInfo.getQueryDomain();
        RelationExt output = new RelationExt();
        output.setBeginDate(input.getBeginDate());
        output.setEndDate(input.getEndDate());
        output.setStatus(input.getStatus());
        output.setDomainName(input.getDomainName());
        output.setClass1Name(input.getClass1Name());
        output.setClass2Name(input.getClass2Name());
        output.setCard1Id(input.getCard1Id());
        output.setCard2Id(input.getCard2Id());
        if (queryDomain.getQuerySource().equals(Source._1.toString())) {
            output.setCard1Code(relationInfo.getSourceCode());
            output.setCard1Description(relationInfo.getSourceDescription());
            output.setCard2Code(relationInfo.getTargetCode());
            output.setCard2Description(relationInfo.getTargetDescription());
        } else {
            output.setCard1Code(relationInfo.getTargetCode());
            output.setCard1Description(relationInfo.getTargetDescription());
            output.setCard2Code(relationInfo.getSourceCode());
            output.setCard2Description(relationInfo.getSourceDescription());
        }
        return output;
    }

    private String fetchLookupDecription(Long lookupId) {
        if (lookupId == null) {
            return null;
        } else {
            Lookup fetchedLookup = lookupStore.getById(lookupId);
            return fetchedLookup.getDescription();
        }
    }

    public List<Relation> getRelations(String className, String domainName, Long cardId) {
        List<Relation> relations = Lists.newArrayList();
        for (DomainInfo domainInfo : relations(className, domainName, cardId)) {
            for (RelationInfo relationInfo : domainInfo) {
                relations.add(transform(relationInfo, cardId));
            }
        }
        return relations;
    }

    public List<RelationExt> getRelationsExt(String className, String domainName, Long cardId) {
        List<RelationExt> relations = Lists.newArrayList();
        for (DomainInfo domainInfo : relations(className, domainName, cardId)) {
            for (RelationInfo relationInfo : domainInfo) {
                relations.add(transform(relationInfo, transform(relationInfo, cardId)));
            }
        }
        return relations;
    }

    private GetRelationListResponse relations(String className, String domainName, Long cardId) {
        Domain domain = isBlank(domainName) ? null : dao.getDomain(domainName);
        Classe classe = isBlank(className) ? null : dao.getClasse(className);
        checkArgument(domain != null || classe != null);
        if (domain != null && classe != null) {
            domain = domain.getThisDomainDirectAndOrReversedForClass(classe).get(0);
        }
        List<CMRelation> relations;
        if (isNotNullAndGtZero(cardId)) {
            CardIdAndClassName srcCard = card((classe == null) ? domain.getSourceClass().getName() : classe.getName(), cardId);
            relations = dao.getServiceRelationsForCard(srcCard);
            if (domain != null) {
                relations = relations.stream().filter(compose(equalTo(domain.getName()), r -> r.getDomainWithThisRelationDirection().getName())).collect(toList());
            }
        } else {
            relations = dao.selectAll().from(domain).getRelations();
        }
        return new GetRelationListResponseImpl(relations.stream().map((r) -> {
            return new RelationInfoImpl(new QueryRelation(r, (String) map(RD_DIRECT, "_1", RD_INVERSE, "_2").get(r.getDirection())),//TODO check this
                    CardImpl.builder().withType(dao.getClasse(r.getSourceCard().getClassName())).withId(r.getSourceId()).build(),
                    CardImpl.builder().withType(dao.getClasse(r.getTargetCard().getClassName())).withId(r.getTargetId()).build());
        }).collect(toList()), relations.size());
    }

    public Relation[] getRelationHistory(Relation relation) {
        List<Relation> historicRelations = Lists.newArrayList();
        Domain domain = dataView.findDomain(relation.getDomainName());
        CardIdAndClassName srcCard = card(relation.getClass1Name(), (relation.getCard1Id()));
        GetRelationHistoryResponse response = dataAccessLogic.getRelationHistory(srcCard, domain);
        for (RelationInfo relationInfo : response) {
            if (relationInfo.getRelation().getSourceId().equals((relation.getCard1Id()))
                    && relationInfo.getRelation().getTargetId().equals((relation.getCard2Id()))) {
                historicRelations.add(transform(relationInfo, relation.getCard1Id()));
            }
        }
        return historicRelations.toArray(new Relation[historicRelations.size()]);
    }

    public CardExt getCardExt(String className, Long cardId, Attribute[] attributeList,
            boolean enableLongDateFormat) {
        Card fetchedCard;
        if (attributeList == null || attributeList.length == 0) {
            fetchedCard = dataAccessLogic.fetchCard(className, cardId);
        } else {
            QueryOptionsImpl queryOptions = QueryOptionsImpl.builder() //
                    .onlyAttributes(namesOf(attributeList)) //
                    .build();
            fetchedCard = dataAccessLogic.fetchCardShort(className, cardId, queryOptions);
        }
        return transformToCardExt(fetchedCard, attributeList, enableLongDateFormat);
    }

    private Iterable<String> namesOf(Attribute[] attributeList) {
        return from(asList(defaultIfNull(attributeList, NO_ATTRIBUTES))) //
                .transform(ATTRIBUTE_NAME);
    }

    private CardExt transformToCardExt(Card card, Attribute[] attributeList,
            boolean enableLongDateFormat) {
        CardExt cardExt;
        ValueSerializer valueSerializer
                = enableLongDateFormat ? org.cmdbuild.services.soap.types.Card.HACK_VALUE_SERIALIZER
                        : org.cmdbuild.services.soap.types.Card.LEGACY_VALUE_SERIALIZER;
        if (attributeList == null || attributeList.length == 0) {
            cardExt = new CardExt(card, valueSerializer);
        } else {
            cardExt = new CardExt(card, attributeList, valueSerializer);
        }
        addExtras(card, cardExt);
        return cardExt;
    }

    private void addExtras(Card card, CardExt cardExt) {
        Classe activityClass = dataAccessLogic.findClass(Constants.BASE_PROCESS_CLASS_NAME);
        if (activityClass.isAncestorOf(card.getType())) {
            Flow processInstance = workflowLogic.getFlowCard(card.getClassName(), card.getId());
//			WorkflowLogicHelper workflowLogicHelper = new WorkflowLogicHelper(workflowLogic, dataView, metadataStoreFactory, cardAdapter);
            Task activityInstance = null;
            try {
                activityInstance = workflowLogicHelper.selectActivityInstanceFor(processInstance);
            } catch (Exception e) {
                activityInstance = null;
            }
            addActivityExtras(activityInstance, cardExt);
            addActivityMetadata(activityInstance, cardExt);
        } else {
            addMetadata(card, cardExt);
        }
    }

    private void addActivityExtras(Task actInst, CardExt cardExt) {
        String activityDescription = INVALID_ACTIVITY_DESCRIPTION;
        if (actInst != null) {
            try {
                activityDescription = actInst.getDefinition().getDescription();
            } catch (WorkflowException e) {
                // keep the placeholder description
            }
        }
        cardExt.getAttributeList().add(newAttribute(ACTIVITY_DESCRIPTION_ATTRIBUTE, activityDescription));
    }

    private Attribute newAttribute(String name, String value) {
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setValue(value);
        return attribute;
    }

    public static enum PrivilegeType {
        READ, WRITE, NONE
    }

    private void addActivityMetadata(Task actInst, CardExt cardExt) {
        PrivilegeType privileges;
        if (actInst != null) {
            privileges = actInst.isWritable() ? PrivilegeType.WRITE : PrivilegeType.READ;
        } else {
            privileges = PrivilegeType.NONE;
        }
        addPrivilege(privileges, cardExt);
    }

    private void addPrivilege(PrivilegeType privilege, CardExt cardExt) {
        cardExt.setMetadata(asList(newPrivilegeMetadata(privilege)));
    }

    private Metadata newPrivilegeMetadata(PrivilegeType privilege) {
        Metadata meta = new Metadata();
        meta.setKey("runtime.privileges");
        meta.setValue(privilegeSerialization(privilege));
        return meta;
    }

    private String privilegeSerialization(PrivilegeType privileges) {
        return privileges.toString().toLowerCase();
    }

    // TODO: fetch privileges for table outside...
    private void addMetadata(Card card, CardExt cardExt) {
        Classe type = card.getType();
        PrivilegeType privilege;
        if (operationUserSupplier.getUser().hasWriteAccess(type)) {
            privilege = PrivilegeType.WRITE;
        } else if (operationUserSupplier.getUser().hasReadAccess(type)) {
            privilege = PrivilegeType.READ;
        } else {
            privilege = PrivilegeType.NONE;
        }
        addPrivilege(privilege, cardExt);
    }

    public CardList getCardList(String className, Attribute[] attributeList, Query queryType,
            Order[] orderType, Long limit, Long offset, String fullTextQuery,
            CQLQuery cqlQuery, boolean enableLongDateFormat) {
        PagedElements<Card> response = cardList(className, attributeList, queryType, orderType, limit, offset, fullTextQuery, cqlQuery);
        return toCardList(response, attributeList, enableLongDateFormat);
    }

    public CardListExt getCardListExt(String className, Attribute[] attributeList, Query queryType,
            Order[] orderType, Long limit, Long offset, String fullTextQuery,
            CQLQuery cqlQuery) {
        PagedElements<Card> response
                = cardList(className, attributeList, queryType, orderType, limit, offset, fullTextQuery, cqlQuery);
        return toCardListExt(response);
    }

    private PagedElements<Card> cardList(String className, Attribute[] attributeList, Query queryType,
            Order[] orderType, Long limit, Long offset, String fullTextQuery,
            CQLQuery cqlQuery) {
        Classe targetClass = dataView.findClasse(className);
        QueryOptionsImpl queryOptions = new GuestFilter(authenticationStore, dataView) //
                .apply(targetClass,
                        QueryOptionsImpl.builder() //
                                .limit(limit != null ? toIntExact(limit) : Integer.MAX_VALUE) //TODO int to long
                                .offset(offset != null ? toIntExact(offset) : 0) //TODO int to long
                                .filter(soapToJsonUtilsService.createJsonFilterFrom(queryType, fullTextQuery, cqlQuery, targetClass,
                                        lookupStore)) //
                                .orderBy(toJsonArray(orderType, attributeList)) //
                                .onlyAttributes(namesOf(attributeList)) //
                                .parameters(parametersOf(cqlQuery)) //
                                .build());
        return dataAccessLogic.fetchCards(className, queryOptions);
    }

    private Map<String, Object> parametersOf(CQLQuery cqlQuery) {
        boolean hasParameters = (cqlQuery != null) && (cqlQuery.getParameters() != null);
        return hasParameters ? toMap(cqlQuery.getParameters()) : new HashMap<>();
    }

    private CardList toCardList(PagedElements<Card> response, Attribute[] subsetAttributesForSelect, boolean enableLongDateFormat) {
        CardList cardList = new CardList();
        long totalNumberOfCards = response.totalSize();
        cardList.setTotalRows(toIntExact(totalNumberOfCards));
        for (Card card : response.elements()) {
            ValueSerializer valueSerializer
                    = enableLongDateFormat ? org.cmdbuild.services.soap.types.Card.HACK_VALUE_SERIALIZER
                            : org.cmdbuild.services.soap.types.Card.LEGACY_VALUE_SERIALIZER;
            org.cmdbuild.services.soap.types.Card soapCard
                    = new org.cmdbuild.services.soap.types.Card(card, valueSerializer);
            removeNotSelectedAttributesFrom(soapCard, subsetAttributesForSelect);
            cardList.addCard(soapCard);
        }
        return cardList;
    }

    private void removeNotSelectedAttributesFrom(org.cmdbuild.services.soap.types.Card soapCard,
            Attribute[] attributesSubset) {
        if (attributesSubset == null || attributesSubset.length == 0) {
            return;
        }
        List<Attribute> onlyRequestedAttributes = Lists.newArrayList();
        for (Attribute cardAttribute : attributesOf(soapCard)) {
            if (belongsToAttributeSubset(cardAttribute, attributesSubset)) {
                onlyRequestedAttributes.add(cardAttribute);
            }
        }
        soapCard.setAttributeList(onlyRequestedAttributes);
    }

    private boolean belongsToAttributeSubset(Attribute attribute, Attribute[] attributesSubset) {
        for (Attribute attr : attributesSubset) {
            if (attr.getName().equals(attribute.getName())) {
                return true;
            }
        }
        return false;
    }

    private CardListExt toCardListExt(PagedElements<Card> response) {
        CardListExt cardListExt = new CardListExt();
        long totalNumberOfCards = response.totalSize();
        cardListExt.setTotalRows(toIntExact(totalNumberOfCards));
        for (Card card : response.elements()) {
            CardExt cardExt = new CardExt(card);
            addExtras(card, cardExt);
            cardListExt.addCard(cardExt);
        }
        return cardListExt;
    }

    private Map<String, Object> toMap(List<CQLParameter> cqlParameters) {
        Map<String, Object> parameters = Maps.newHashMap();
        for (CQLParameter cqlParameter : cqlParameters) {
            parameters.put(cqlParameter.getKey(), cqlParameter.getValue());
        }
        return parameters;
    }

    public Reference[] getReference(String classname, Query query, Order[] order, Long limit, Long offset, String fullText, CQLQuery cqlQuery) {
        CardListExt cardList = getCardListExt(classname, null, query, order, limit, offset, fullText, cqlQuery);
        return from(cardList.getCards()) //
                .transform((CardExt input) -> {
                    Reference reference = new Reference();
                    reference.setId(input.getId());
                    reference.setClassname(classname);
                    reference.setDescription(descriptionOf(input));
                    reference.setTotalRows(cardList.getTotalRows());
                    return reference;
                }) //
                .toArray(Reference.class);
    }

    private String descriptionOf(CardExt card) {
        for (Attribute attribute : card.getAttributeList()) {
            if ("Description".equals(attribute.getName())) {
                return attribute.getValue();
            }
        }
        return EMPTY;
    }

    public CardList getCardHistory(String className, long cardId, Long limit, Long offset) {
        logger.info("getting history for '{}' card with id '{}'", className, cardId);
//		CardList cardList = new CardList();
//		Card card = Card.newInstance() //
//				.withClassName(className) //
//				.withId((cardId)) //
//				.build();
        throw new UnsupportedOperationException("TODO"); //TODO
//		Iterable<Card> response = dataAccessLogic.getCardHistory(card, true); 
//
//		List<Card> ordered = Ordering.from(BEGIN_DATE_DESC).sortedCopy(response);
//		for (org.cmdbuild.services.soap.types.Card element : from(ordered) //
//				.skip((offset != null) ? offset : 0) //
//				.limit(((limit != null) && (limit != 0)) ? limit : Integer.MAX_VALUE) //
//				.transform(TO_SOAP_CARD) //
//				) {
//			cardList.addCard(element);
//		}
//		cardList.setTotalRows(size(response));
//		return cardList;
    }

    public ClassSchema getClassSchema(String name, boolean includeAttributes) {
        logger.info("getting schema for class '{}'", name);
        return toClassSchema(dataAccessLogic.findClass(name), includeAttributes);
    }

    public ClassSchema getClassSchema(long id, boolean includeAttributes) {
        logger.info("getting schema for class '{}'", id);
        return toClassSchema(dataAccessLogic.findClass(id), includeAttributes);
    }

    private ClassSchema toClassSchema(Classe input, boolean includeAttributes) {
        ClassSchema output = new ClassSchema();
        output.setId(input.getId());
        output.setName(input.getName());
        output.setDescription(input.getDescription());
        output.setSuperClass(input.isSuperclass());
        List<AttributeSchema> attributes = new ArrayList<>();
        for (org.cmdbuild.dao.entrytype.Attribute attribute : includeAttributes ? input.getServiceAttributes() : EMPTY_ATTRIBUTES) {
            if (attribute.hasNotServiceListPermission() || !attribute.isActive()) {
                logger.debug("skipping attribute '{}'", attribute.getName());
                continue;
            }
            logger.debug("keeping attribute '{}'", attribute.getName());
            attributes.add(serializationUtils.serialize(attribute));
        }
        output.setAttributes(attributes);
        return output;
    }

    public MenuSchema getVisibleClassesTree() {
        Classe rootClass = dataView.findClasse("Class");
        MenuSchemaSerializer serializer = new MenuSchemaSerializer(menuStore, operationUserSupplier.getUser(), dataAccessLogic, workflowLogic, classHierarchyService);
        return serializer.serializeVisibleClassesFromRoot(rootClass);
    }

    public MenuSchema getVisibleProcessesTree() {
        Classe rootClass = dataView.findClasse("Activity");
        MenuSchemaSerializer serializer = new MenuSchemaSerializer(menuStore, operationUserSupplier.getUser(), dataAccessLogic, workflowLogic, classHierarchyService);
        return serializer.serializeVisibleClassesFromRoot(rootClass);
    }

    public MenuSchema getMenuSchemaForPreferredGroup() {
        MenuSchemaSerializer serializer = new MenuSchemaSerializer(menuStore, operationUserSupplier.getUser(), dataAccessLogic, workflowLogic, classHierarchyService);
        return serializer.serializeMenuTree();
    }

    public Report[] getReportsByType(String type, long limit, long offset) {
        return paged(reportLogic.getForCurrentUser(), offset, limit).stream().map((org.cmdbuild.report.ReportInfo input) -> {
            Report output = new Report();
            output.setId(input.getId());
            output.setTitle(input.getCode());
//					output.setType(input.getType());
            output.setType(null);//TODO check this
            output.setDescription(input.getDescription());
            return output;
        }).collect(toList()).toArray(new Report[]{});
    }

    public AttributeSchema[] getReportParameters(long id, String extension) {
        return from(reportLogic.getParamsById(id)) //
                .transform((org.cmdbuild.dao.entrytype.Attribute input) -> serializationUtils.serialize(input)) //
                .toArray(AttributeSchema.class);
    }

    public DataHandler getReport(long id, String extension, ReportParams[] params) {
        Map<String, Object> paramsAsMap = transformValues( //
                uniqueIndex( //
                        asList(defaultIfNull(params, NO_PARAMS)), //
                        REPORT_PARAM_KEY), //
                REPORT_PARAM_VALUE);
        return reportLogic.executeReportAndDownload(id, reportExtFromString(extension), paramsAsMap);
    }

    public DataHandler getReport(String reportId, String extension, ReportParams[] params) {
        try {
            BuiltInReport builtInReport = BuiltInReport.from(reportId);
            ReportProcessor reportFactory = builtInReport //
                    //					.newBuilder(dataView, filesStore, authenticationStore, configuration) //
                    .newBuilder(dataView, authenticationStore, configuration) //
                    .withExtension(extension) //
                    .withProperties(propertiesFrom(params)) //
                    .withDataSource(dataSource) //
                    .withDataAccessLogic(dataAccessLogic) //
                    .withOperationUser(operationUserSupplier.getUser()) //
                    .build();
            DataSource thisDataSource = reportFactory.executeReport();
            return new DataHandler(thisDataSource);
        } catch (Throwable e) {
            throw new Error(e);
        }
    }

    private Map<String, String> propertiesFrom(ReportParams[] params) {
        Map<String, String> properties = Maps.newHashMap();
        if (params != null) {
            for (ReportParams param : params) {
                properties.put(param.getKey(), param.getValue());
            }
        }
        return properties;
    }

}

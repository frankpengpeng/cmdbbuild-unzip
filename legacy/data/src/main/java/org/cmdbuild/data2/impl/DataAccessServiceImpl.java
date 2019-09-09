package org.cmdbuild.data2.impl;

import org.cmdbuild.data2.api.DataAccessService;
import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.size;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphanumeric;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.dao.constants.Cardinality.CARDINALITY_1N;
import static org.cmdbuild.dao.constants.Cardinality.CARDINALITY_N1;
import static org.cmdbuild.dao.query.clause.join.Over.over;
import static org.cmdbuild.dao.query.clause.where.AndWhereClause.and;
import static org.cmdbuild.exception.NotFoundException.NotFoundExceptionType.CARD_NOTFOUND;
import static org.cmdbuild.exception.NotFoundException.NotFoundExceptionType.DOMAIN_NOTFOUND;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.common.Constants;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.query.CMQueryRow;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.cmdbuild.exception.ORMException.ORMExceptionType;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationInfo;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationHistoryService;
import org.cmdbuild.dao.postgres.legacy.relationquery.GetRelationHistoryResponse;
import org.cmdbuild.dao.postgres.legacy.relationquery.GetRelationListResponse;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationSingleService;
import org.cmdbuild.logic.data.QueryOptionsImpl;
import org.cmdbuild.logic.data.access.resolver.CardSerializer;
import org.cmdbuild.logic.data.access.resolver.ForeignReferenceResolver;
import org.cmdbuild.servlets.json.management.export.CMDataSource;
import org.cmdbuild.servlets.json.management.export.DBDataSource;
import org.cmdbuild.servlets.json.management.export.DataExporter;
import org.cmdbuild.servlets.json.management.export.csv.CsvExporter;
import org.supercsv.prefs.CsvPreference;

import com.google.common.base.Functions;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import static com.google.common.base.Predicates.notNull;
import com.google.common.collect.FluentIterable;
import static com.google.common.collect.FluentIterable.from;
import static com.google.common.collect.Iterables.isEmpty;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Streams;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import javax.annotation.Nullable;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.grant.AuthorizationException;
import org.cmdbuild.auth.user.UserPrivilegesImpl;
import org.cmdbuild.lock.LockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.cmdbuild.dao.query.QueryResult;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.common.data.QueryOptions;
import org.cmdbuild.dao.query.clause.DomainHistory;
import org.cmdbuild.data2.api.SimpleAttrQueryParams;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.dao.postgres.legacy.relationquery.DomainWithSource;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.query.clause.alias.NameAlias.nameAlias;
import org.cmdbuild.dao.beans.CardIdAndClassName;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.beans.RelationDefinition;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.query.clause.AnyAttribute.anyAttribute;
import org.cmdbuild.dao.query.clause.QueryAliasAttribute;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import org.cmdbuild.dao.query.clause.QueryAttribute;
import static org.cmdbuild.dao.query.clause.where.EqualsOperatorAndValue.eq;
import static org.cmdbuild.dao.query.clause.where.SimpleWhereClause.condition;
import static org.cmdbuild.exception.NotFoundException.NotFoundExceptionType.CLASS_NOTFOUND;
import org.cmdbuild.dao.view.DataView;
import org.cmdbuild.auth.login.AuthenticationService;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;

//TODO check transactional methods; why update card methods were not transactional ???
@Component
@Deprecated
public class DataAccessServiceImpl implements DataAccessService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String BEGIN_DATE = "BeginDate";

	private static final String ID_ATTRIBUTE = org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
	public static final String USER_PARAM = "__user__";
	public static final String ROLE_PARAM = "__role__";

	private static final Alias DOM_ALIAS = nameAlias("DOM");
	private static final Alias DST_ALIAS = nameAlias("DST");

	private final DataView dataView;
	private final LookupRepository lookupStore;
	private final OperationUserSupplier userStore;
	private final LockService lockService;
	private final SessionService sessionLogic;
	private final AuthenticationService authenticationService;
//	private final RelationListService relationListService;
	private final RelationHistoryService relationHistoryService;
	private final RelationSingleService relationSingleService;
	private final DaoService dao;

	public DataAccessServiceImpl(AuthenticationService authenticationService, DataView systemDataView, LookupRepository lookupStore, OperationUserSupplier userStore, LockService lockService, SessionService sessionLogic, DaoService dao, RelationSingleService relationSingleService, RelationHistoryService relationHistoryService) {
		this.dataView = systemDataView;
		this.lookupStore = lookupStore;
		this.userStore = userStore;
		this.lockService = lockService;
		this.sessionLogic = sessionLogic;
		this.dao = dao;
		this.authenticationService = authenticationService;
//		this.relationListService = checkNotNull(relationListService);
		this.relationHistoryService = checkNotNull(relationHistoryService);
		this.relationSingleService = checkNotNull(relationSingleService);
	}

	@Override
	public Card fetchCMCard(String className, Long cardId) {
		Classe entryType = dataView.findClasse(className);
		if (entryType == null) {
			throw CLASS_NOTFOUND.createException(className);
		}
		return getCard(entryType, cardId, emptyList());
	}

	private Card getCard(Classe entryType, Long cardId, Iterable<String> attributeNames) {
		return checkNotNull(getCardOrNull(entryType, cardId, attributeNames), "card not found for type = %s id = %s", entryType, cardId);
	}

	private @Nullable
	Card getCardOrNull(Classe entryType, Long cardId, Iterable<String> attributeNames) {
		QueryAttribute[] queryAttributes = (attributeNames == null || isEmpty(attributeNames))
				? new QueryAttribute[]{anyAttribute(entryType)}
				: from(attributeNames)
						.transform(input -> attribute(entryType, input))
						.toArray(QueryAliasAttribute.class);
		QueryResult result = dataView.select(queryAttributes)
				.from(entryType)
				.where(condition(attribute(entryType, ID_ATTRIBUTE), eq(cardId)))
				.limit(1)
				.skipDefaultOrdering()
				.run();
		return from(result)
				.transform(input -> input.getCard(entryType))
				.transform(input -> resolveCardReferences(entryType, input))
				//				.transform(function)
				.first()
				.orNull();
	}

	@Override
	public Card resolveCardReferences(final Classe entryType, final Card card) {
		return ForeignReferenceResolver.<Card>newInstance()
				.withEntries(asList(card))
				.withEntryFiller(CardEntryFiller.newInstance()
						.build())
				.withSerializer(new CardSerializer<>())
				.build()
				.resolve()
				.iterator()
				.next();
	}

	private OperationUser getUser() {
		return userStore.getUser();
	}

	@Override
	public DataView getView() {
		return dataView;
	}

//	@Deprecated
//	private DataViewStore<Card> storeOf(Card card) {
//		return DataViewStore.<Card>builder().withDataView(dataView).withStorableConverter(CardStorableConverter.of(card)).build();
//	}
	@Override
	public Map<Object, List<RelationInfo>> relationsBySource(String sourceTypeName, DomainWithSource dom) {
		throw new UnsupportedOperationException("broken for 30");
//		return relationListService.list(sourceTypeName, dom);
	}

	@Override
	public GetRelationListResponse getRelationList(CardIdAndClassName srcCard, DomainWithSource dom, QueryOptionsImpl options) {
		throw new UnsupportedOperationException("broken for 30");
//		return relationListService.getRelationList(srcCard.getClassName(), srcCard.getId(), dom, options);
	}

	@Override
	public GetRelationListResponse getRelationList(CardIdAndClassName srcCard, DomainWithSource dom) {
		throw new UnsupportedOperationException("broken for 30");
//		return relationListService.getRelationList(srcCard.getClassName(), srcCard.getId(), dom, QueryOptionsImpl.builder().build());
	}

	@Override
	public GetRelationListResponse getRelationList(Domain domain, QueryOptionsImpl queryOptions) {
		throw new UnsupportedOperationException("broken for 30");
//		return relationListService.getRelationList(domain, queryOptions);
	}

	@Override
	public Optional<RelationInfo> getRelation(String domain, Long id) {
		final Domain _domain = dataView.findDomain(domain);
		return getRelation(_domain, id);
	}

	@Override
	public Optional<RelationInfo> getRelation(Domain domain, Long id) {
		return relationSingleService.getRelationInfo(domain, id);
	}

	@Override
	public GetRelationHistoryResponse getRelationHistory(CardIdAndClassName srcCard) {
		return relationHistoryService.getRelationHistory(srcCard.getClassName(), srcCard.getId());
	}

	@Override
	public GetRelationHistoryResponse getRelationHistory(CardIdAndClassName srcCard, Domain domain) {
		return relationHistoryService.getRelationHistory(srcCard.getClassName(), srcCard.getId(), domain);
	}

	@Override
	public Optional<RelationInfo> getHistoricRelation(String domain, Long id) {
		Domain target = dataView.getDomain(domain);
		return relationSingleService.getRelationInfo(DomainHistory.of(target), id);
	}

//	@Override
//	public HistoryService history() {
//		return new HistoryServiceImpl(dataView);
//	}
	@Override
	public Classe findClass(Long classId) {
		return dataView.getClassOrNull(classId);
	}

	@Override
	public Classe findClass(String className) {
		return dataView.findClasse(className);
	}

	@Override
	public boolean hasClass(Long classId) {
		return findClass(classId) != null;
	}

	@Override
	public Domain findDomain(Long domainId) {
		return dataView.findDomain(domainId);
	}

	@Override
	public Domain findDomain(String domainName) {
		return dataView.findDomain(domainName);
	}

	/**
	 *
	 * @return only active classes (all classes, included superclasses, simple
	 * classes and process classes).
	 */
	@Override
	public Iterable<? extends Classe> findActiveClasses() {
		return from(dataView.getUserClasses()).filter(Classe::isActive);
	}

	/**
	 *
	 * @return active and non active domains
	 */
	@Override
	public List<Domain> getAllDomains() {
		return dataView.getDomains();
	}

	/**
	 *
	 * @return only active domains
	 */
	@Override
	public Iterable<? extends Domain> findActiveDomains() {
		return from(dataView.getDomains()).filter(Domain::isActive);
	}

	@Override
	public Iterable<? extends Domain> findReferenceableDomains(String className) {
		throw new UnsupportedOperationException("BROKEN - TODO");
//		Classe fetchedClass = dataView.findClasse(className);
//		Collection<Domain> output = new ArrayList<>();
//		for (Domain element : from(dataView.getDomains()) //
//				.filter(Domain::isActive) //
//				.filter((d) -> d.isDomainForClasse(fetchedClass)) //
//				.filter(usableForReferences(fetchedClass))) {
//			switch (element.getCardinality()) {
//				case "1:N":
//					if (!element.getDisabledTargetDescendants().contains(className)) {
//						output.add(element);
//					}
//					break;
//				case "N:1":
//					if (!element.getDisabledSourceDescendants().contains(className)) {
//						output.add(element);
//					}
//					break;
//				default:
//					throw new IllegalArgumentException(element.getName());
//			}
//		}
//		return output;
	}

	@Override
	public Iterable<? extends Classe> findAllClasses() {
		return dataView.getUserClasses();
	}

	@Override
	public Iterable<? extends Classe> findClasses(boolean activeOnly) {
		logger.trace("find classes, activeOnly = {}", activeOnly);
		Iterable<? extends Classe> fetchedClasses = activeOnly ? findActiveClasses() : findAllClasses();
		Iterable<? extends Classe> nonProcessClasses = filter(fetchedClasses, nonProcessClasses());
		Iterable<? extends Classe> classesToBeReturned = activeOnly ? filter(nonProcessClasses, nonSystemButUsable()) : nonProcessClasses;
		return classesToBeReturned;
	}

	private Predicate<Classe> nonProcessClasses() {
		Classe processBaseClass = findClass(Constants.BASE_PROCESS_CLASS_NAME);
		Predicate<Classe> nonProcessClasses = (Classe input) -> !processBaseClass.isAncestorOf(input);
		return nonProcessClasses;
	}

	/**
	 *
	 * @return a predicate that will filter classes whose mode does not start
	 * with sys... (e.g. sysread or syswrite)
	 */
	private Predicate<Classe> nonSystemButUsable() {
		return Classe::hasServiceListPermission;
	}

	@Override
	public PagedElements<Attribute> getAttributes(String className, boolean onlyActive, @Nullable AttrQueryParams attributesQuery) {
		if (attributesQuery == null) {
			attributesQuery = new SimpleAttrQueryParams(Integer.MAX_VALUE, 0);
		}
		Classe target = findClass(className);
		Iterable<? extends Attribute> elements = onlyActive ? target.getCoreAttributes() : target.getServiceAttributes();
		Iterable<? extends Attribute> ordered = Ordering.from(NAME_ASC).sortedCopy(elements);
		Integer offset = attributesQuery.offset();
		Integer limit = attributesQuery.limit();
		FluentIterable<Attribute> limited = from(ordered) //
				.skip((offset == null) ? 0 : offset) //
				.limit((limit == null) ? Integer.MAX_VALUE : limit) //
				.transform(Functions.<Attribute>identity());
		return new PagedElements<>(limited, size(elements));
	}

	@Override
	public PagedElements<Attribute> getDomainAttributes(String className, boolean onlyActive, @Nullable AttrQueryParams attributesQuery) {
		if (attributesQuery == null) {
			attributesQuery = new SimpleAttrQueryParams(Integer.MAX_VALUE, 0);
		}
		Domain target = findDomain(className);
		Iterable<? extends Attribute> elements = onlyActive ? target.getCoreAttributes() : target.getServiceAttributes();
		Iterable<? extends Attribute> ordered = Ordering.from(NAME_ASC).sortedCopy(elements);
		Integer offset = attributesQuery.offset();
		Integer limit = attributesQuery.limit();
		FluentIterable<Attribute> limited = from(ordered) //
				.skip((offset == null) ? 0 : offset) //
				.limit((limit == null) ? Integer.MAX_VALUE : limit) //
				.transform(Functions.<Attribute>identity());
		return new PagedElements<>(limited, size(elements));
	}

	/**
	 * Fetches the card with the specified Id from the class with the specified
	 * name
	 *
	 * @param className
	 * @param cardId
	 * @throws NoSuchElementException if the card with the specified Id number
	 * does not exist or it is not unique
	 * @return the card with the specified Id.
	 */
	@Override
	public Card fetchCard(String className, Long cardId) {
		Classe entryType = dataView.findClasse(className);
		if (entryType == null) {
			throw CLASS_NOTFOUND.createException(className);
		}
		return getCard(entryType, cardId, emptyList());
	}

//		Classe entryType = dataView.findClass(className);
//		if (entryType == null) {
//			throw CLASS_NOTFOUND.createException(className);
//		}
//		return fetchCard(entryType, cardId, emptyList(), identity());
//	}
//
//	private <T> T fetchCard(Classe entryType, Long cardId, Iterable<String> attributes, Function<CMCard, T> function) {
//		QueryAttribute[] _attributes = (attributes == null || isEmpty(attributes))
//				? new QueryAttribute[]{anyAttribute(entryType)}
//				: from(attributes) //
//						.transform(input -> attribute(entryType, input)) //
//						.toArray(QueryAliasAttribute.class);
//		return from(dataView.select(_attributes) //
//				.from(entryType) //
//				.where(condition(attribute(entryType, ID_ATTRIBUTE), eq(cardId))) //
//				.limit(1) //
//				.skipDefaultOrdering() //
//				.run()) //
//				.transform(input -> input.getCard(entryType)) //
//				.transform(input -> resolveCardReferences(entryType, input)) //
//				.transform(function) //
//				.first() //
//				.or(() -> {
//					throw CARD_NOTFOUND.createException(entryType.getName());
//				});
//	}
	@Override
	public Card fetchCardShort(String className, Long cardId, QueryOptions queryOptions) {
		Classe entryType = dataView.findClasse(className);
		if (entryType == null) {
			throw CLASS_NOTFOUND.createException(className);
		}
		return getCard(entryType, cardId, queryOptions.getAttributes());
	}

//	/**
//	 * @param entryType
//	 * @param card
//	 * @return
//	 */
//	@Override
//	public Card resolveCardReferences(final Classe entryType, final Card card) {
//		return super.resolveCardReferences(entryType, card);
//	}
//		return ForeignReferenceResolver.<CMCard>newInstance() //
//				.withEntries(asList(card)) //
//				.withEntryFiller(CardEntryFiller.newInstance() //
//						.build()) //
//				.withSerializer(new CardSerializer<>()) //
//				.build() //
//				.resolve() //
//				.iterator() //
//				.next();
//	}
	@Override
	public Card fetchCard(final Long classId, final Long cardId) {
		final Classe entryType = dataView.getClassOrNull(classId);
		return fetchCard(entryType.getName(), cardId);
	}

	/**
	 * Retrieve the cards of a given class that matches the given query options
	 *
	 * @param className
	 * @param queryOptions
	 * @return a FetchCardListResponse
	 */
	@Override
	public PagedElements<Card> fetchCards(@Nullable String className, QueryOptions queryOptions) {
		/*
		 * preferred solution to avoid pre-release errors
		 */
		final PagedElements<Card> output;
		if (isNotBlank(className)) {
			output = fetchCardsWithClassName(className, queryOptions);
		} else {
			output = fetchCardsWithoutClassName(queryOptions);//TODO check this
		}
		return output;
	}

	private PagedElements<Card> fetchCardsWithClassName(String className, QueryOptions queryOptions) {
		logger.debug("fetch cards for class name = {} query options = {}", className, queryOptions);
		Classe fetchedClass = dataView.findClasse(className);
		PagedElements<Card> fetchedCards = DataViewCardFetcher.newInstance()
				.withDataView(dataView)
				.withClassName(className)
				.withQueryOptions(queryOptions)
				.build().fetch();
		Iterable<Card> cards = resolveCardForeignReferences(fetchedClass, fetchedCards);
		logger.trace("fetched cards = {}", cards);
		return new PagedElements<>(cards, fetchedCards.totalSize());
	}

	/**
	 * @param fetchedClass CMClass
	 * @param fetchedCards PagedElements<CMCard>
	 * @return
	 */
	private Iterable<Card> resolveCMCardForeignReferences(Classe fetchedClass, Iterable<Card> fetchedCards) {
		final Iterable<Card> cardsWithForeingReferences = ForeignReferenceResolver.<Card>newInstance() //
				.withEntries(fetchedCards) //
				.withEntryFiller(CardEntryFiller.newInstance() //
						.build()) //
				.withSerializer(new CardSerializer<>()) //
				.build() //
				.resolve();
		return cardsWithForeingReferences;
	}

	public Iterable<Card> resolveCardForeignReferences(Classe fetchedClass, Iterable<Card> fetchedCards) {
		Iterable<Card> cardsWithForeingReferences = resolveCMCardForeignReferences(fetchedClass, fetchedCards);
		return cardsWithForeingReferences;
	}

	private PagedElements<Card> fetchCardsWithoutClassName(QueryOptions queryOptions) {
		PagedElements<Card> fetchedCards = DataViewCardFetcher.newInstance() //
				.withDataView(dataView) //
				.withQueryOptions(queryOptions) //
				.build() //
				.fetch();

		Iterable<Card> cardsWithForeingReferences = ForeignReferenceResolver.<Card>newInstance() //
				.withEntries(fetchedCards) //
				.withEntryFiller(CardEntryFiller.newInstance() //
						.build()) //
				.withSerializer(new CardSerializer<>()) //
				.build() //
				.resolve();

		Iterable<Card> cards = cardsWithForeingReferences;

		return new PagedElements<>(cards, fetchedCards.totalSize());
	}

	public static final Map<String, Object> NO_PARAMETERS = emptyMap();

	/**
	 * Execute a given SQL function to select a set of rows Return these rows as
	 * fake cards
	 *
	 * @param functionName
	 * @param queryOptions
	 * @return
	 */
	@Override
	public PagedElements<Card> fetchSQLCards(String functionName, QueryOptions queryOptions) {
		throw new UnsupportedOperationException("TODO");
//		StoredFunction fetchedFunction = dataView.findFunctionByName(functionName);
//		if (fetchedFunction == null) {
//			throw NOTFOUND_FUNCTION.createException(functionName);
//
//		}
//		Map<String, Object> parameters = newHashMap(defaultIfNull(queryOptions.getParameters(), NO_PARAMETERS));
//		if (!getUser().getAuthenticatedUser().isAnonymous()) {
//			parameters.put(USER_PARAM, getUser().getAuthenticatedUser().getId());
//		}
//		if (getUser().hasDefaultGroup()) {
//			parameters.put(ROLE_PARAM, getUser().getDefaultGroupOrNull().getId());
//		}
//		Alias functionAlias = nameAlias("f");
//		QueryResult queryResult = new DataViewCardFetcher.SqlQuerySpecsBuilderBuilder() //
//				.withDataView(dataView) //
//				.withSystemDataView(systemDataView) //
//				.withQueryOptions(queryOptions) //
//				.withFunction(functionCallService.create(fetchedFunction, parameters)) //
//				.withAlias(functionAlias) //
//				.build() //
//				.count() //
//				.run();
//		Iterable<Card> filteredCards = from(queryResult) //
//				.transform(toValueSet(functionAlias)) //
//				.transform((CMValueSet input) -> Card.newInstance() //
//				.withClassName(functionName) //
//				.withAllAttributes(input.getAttributeValues()) //
//				.build());
//		return new PagedElements<>(filteredCards, queryResult.totalSize());
	}

	/**
	 *
	 * @param className
	 * @param cardId
	 * @param queryOptions
	 * @return a long (zero based) with the position of this card in relation of
	 * current sorting and filter
	 */
	@Override
	public Card getCardPosition(String className, Long cardId, QueryOptions queryOptions) {
//		try {
		PagedElements<Card> cards = fetchCardsWithPosition(className, queryOptions, cardId);
		return cards.iterator().next();
//		} catch (Exception ex) {
//			logger.error("Cannot calculate the position for card with id " + cardId + " from class " + className, ex);
//			return new Card(null, -1L);
//		}
	}

	@Override
	public PagedElements<Card> fetchCardsWithPosition(String className, QueryOptions queryOptions, Long cardId) {
		logger.debug("fetchCardsWithPosition from classId = {} with options = {} for cardId = {}", className, queryOptions, cardId);
		Classe fetchedClass = dataView.findClasse(className);
		PagedElements<CMQueryRow> rows = DataViewCardFetcher.newInstance() //
				.withClassName(className) //
				.withQueryOptions(queryOptions) //
				.withDataView(dataView) //
				.build() //
				.fetchNumbered(condition(attribute(fetchedClass, ID_ATTRIBUTE), eq(cardId)));
		return new PagedElements<>(from(rows) //
				.transform((CMQueryRow input) -> {
					Card card = input.getCard(fetchedClass);
					Card res = from(resolveCardForeignReferences(fetchedClass, asList(card)))
							.get(0);
					throw new UnsupportedOperationException("TODO");
//					return new Card(res, input.getNumber() - 1);
				}), rows.totalSize());
	}

	@Override
	@Transactional
	public Long createCard(Card userGivenCard) {
		assureWrite(userGivenCard.getClassName());
		return createCard(userGivenCard, true);
	}

	@Override
	@Transactional
	public Long createCard(Card userGivenCard, boolean manageAlsoDomainsAttributes) {
		assureWrite(userGivenCard.getClassName());
		return createCard(new CreateCard() {

			@Override
			public Card card() {
				return userGivenCard;
			}

			@Override
			public boolean manageAlsoDomainsAttributes() {
				return manageAlsoDomainsAttributes;
			}

			@Override
			public void created(Long value) {
				// nothing to do
			}

		});
	}

	@Override
	@Transactional
	public Long createCard(CreateCard value) {
		assureWrite(value.card().getClassName());
		return doCreateCard(value);
	}

	@Override
	@Transactional
	public void createCards(Iterable<CreateCard> values) {
		Streams.stream(values).map(input -> input.card()).forEach(input -> assureWrite(input.getClassName()));
		values.forEach(input -> doCreateCard(input));
	}

	private Long doCreateCard(CreateCard value) {
		return dao.create(CardImpl.buildCard(dao.getClasse(value.card().getClassName()), value.card().getAllValuesAsMap())).getId();
//		throw new UnsupportedOperationException("TODO");  //TODO
//		Card userGivenCard = value.card();
//		Classe entryType = dataView.findClasse(userGivenCard.getClassName());
//		if (entryType == null) {
//			throw CLASS_NOTFOUND.createException(userGivenCard.getClassName());
//		}
//
////		Card _userGivenCard = Card.newInstance() //
////				.clone(userGivenCard) //
////				.withUser(getUser().getAuthenticatedUser().getUsername()) //
////				.build();
////		Store<Card> store = storeOf(_userGivenCard);
////		Storable created = store.create(_userGivenCard);
////		Long createdId = Long.valueOf(created.getIdentifier());
////		Long createdId = dataView.cr;
//		if (value.manageAlsoDomainsAttributes()) {
//			updateRelationAttributesFromReference( //
//					createdId, //
//					userGivenCard, //
//					userGivenCard, //
//					entryType //
//			);
//		}
//
//		value.created(createdId);
//		return createdId;
	}

	@Override
	public void updateCard(Card card) {
		lockService.requireLockedByCurrent(LockService.itemIdFromCardId(card.getId()));
		doUpdateCard(card);
		lockService.releaseLock(LockService.itemIdFromCardId(card.getId()));
	}

	private Card doUpdateCard(Card card) {
		return dao.update(card); //TODO permission check (?) 
	}

//	private boolean canWrite(Classe entryType, Card card) {
//		//FIXME this is a little ugly; anyway it does work, and it's called only when we open a card, so performance is acceptable;
//		// would be better to load 'can write' attribute eager with the rest of the data....
//		return getUser().getLoginUser().getGroupNames() //
//				.stream() //
//				.map(input -> authenticationService.getGroupWithName(input)) //
//				.collect(toMap(input -> input.getName(),
//						input -> UserPrivilegesImpl.builder().withGroups(input).build())) //
//				.entrySet() //
//				.stream() //
//				.filter(input -> input.getValue().hasWriteAccess(entryType)) //
//				//				.anyMatch(input -> dataView.use(input.getValue()) //
//				.anyMatch(input -> dataView
//				.select(entryType.isSimpleClass() ? attribute(entryType, BEGIN_DATE) : attribute(entryType, ATTR_DESCRIPTION)) //
//				.from(entryType) //
//				.where(condition(attribute(entryType, ID_ATTRIBUTE), eq(card.getId()))) //
//				.limit(1) //
//				.run() //
//				.iterator() //
//				.hasNext());
//	}
//	private void updateRelationAttributesFromReference(Long storedCardId, Card fetchedCard, Card userGivenCard, Classe entryType) {
//		logger.debug("updating relation attributes for references of card '{}'", storedCardId);
//
//		Map<String, Object> fetchedCardAttributes = fetchedCard.getAllValuesAsMap();
//		Map<String, Object> userGivenCardAttributes = userGivenCard.getAllValuesAsMap();
//
//		for (Attribute attribute : from(entryType.getCoreAttributes()) //
//				.filter(attributeTypeInstanceOf(ReferenceAttributeType.class))) {
//			Long sourceCardId = null;
//			Long destinationCardId = null;
//			try {
//				String referenceAttributeName = attribute.getName();
//				logger.debug("checking attribute '{}'", referenceAttributeName);
//
//				/*
//				 * Before save, some trigger can update the card If the
//				 * reference attribute value is the same of the one given from
//				 * the user update the attributes over the relation, and take
//				 * the values to set from the card given by the user
//				 */
//				if (haveDifferentValues(fetchedCard, userGivenCard, referenceAttributeName)) {
//					continue;
//				}
//
//				// retrieve the reference value
//				Object referencedCardIdObject = fetchedCardAttributes.get(referenceAttributeName);
//				Long referencedCardId = getReferenceCardIdAsLong(referencedCardIdObject);
//				if (referencedCardIdObject == null) {
//					continue;
//				}
//
//				// retrieve the relation attributes
//				String domainName = ((ReferenceAttributeType) attribute.getType()).getDomainName();
//				Domain domain = dataView.findDomain(domainName);
//				Map<String, Object> relationAttributes = Maps.newHashMap();
//				for (Attribute domainAttribute : domain.getUiAttributes()) {
//					String domainAttributeName = format("_%s_%s", referenceAttributeName,
//							domainAttribute.getName());
//					Object domainAttributeValue = userGivenCardAttributes.get(domainAttributeName);
//					relationAttributes.put(domainAttribute.getName(), domainAttributeValue);
//				}
//
//				// update the attributes if needed
//				Classe sourceClass = domain.getSourceClass();
//				Classe destinationClass = domain.getTargetClass();
//
//				if (domain.getCardinality().equals("N:1")) {
//					sourceCardId = storedCardId;
//					destinationCardId = referencedCardId;
//				} else {
//					sourceCardId = referencedCardId;
//					destinationCardId = storedCardId;
//				}
//
//				if (sourceCardId == null || destinationCardId == null) {
//					continue;
//				}
//
//				Alias DOM = nameAlias("DOM");
//				Alias DST = nameAlias(format("DST-%s-%s", destinationClass.getName(), randomAlphanumeric(10)));
//				CMQueryRow row = dataView.select(anyAttribute(DOM)) //
//						.from(sourceClass) //
//						.join(destinationClass, DST, over(domain, (DOM))) //
//						.where(and( //
//								condition(attribute(sourceClass, ID_ATTRIBUTE), eq(sourceCardId)), //
//								condition(attribute(DST, ID_ATTRIBUTE), eq(destinationCardId)) //
//						)) //
//						.limit(1) //
//						.skipDefaultOrdering() //
//						.run() //
//						.getOnlyRow();
//				Card fetchedSourceCard = row.getCard(sourceClass);
//				Card fetchedDestinationCard = row.getCard(DST);
//				CMRelation relation = row.getRelation(DOM).getRelation();
//
//				boolean updateRelationNeeded = areRelationAttributesModified(relation.getAttributeValues(),
//						relationAttributes, domain);
//
//				if (updateRelationNeeded) {
//					RelationDefinition mutableRelation = dataView.update(relation) //
//							.setSourceCard(fetchedSourceCard) //
//							.setTargetCard(fetchedDestinationCard); //
//					updateRelationDefinitionAttributes(relationAttributes, mutableRelation);
//					mutableRelation.update();
//				}
//
//			} catch (Exception ex) {
//				logger.error("error updating attributes", ex);
//			}
//		}
//	}
	private boolean haveDifferentValues(Card fetchedCard, Card userGivenCard, String referenceAttributeName) {

		Long fetchedCardAttributeValue = getReferenceCardIdAsLong( fetchedCard.get(referenceAttributeName));

		Long userGivenCardAttributeValue = getReferenceCardIdAsLong( userGivenCard.get(referenceAttributeName));

		boolean output;
		if (fetchedCardAttributeValue == null) {
			output = (userGivenCard != null);
		} else {
			output = !fetchedCardAttributeValue.equals(userGivenCardAttributeValue);
		}
		return output;
	}

	private Long getReferenceCardIdAsLong(Object value) {
		Long out;
		if (value instanceof Number) {
			out = Number.class.cast(value).longValue();
		} else if (value instanceof IdAndDescriptionImpl) {
			out = ((IdAndDescriptionImpl) value).getId();
		} else if (value instanceof String) {
			String stringCardId = String.class.cast(value);
			if (isEmpty(stringCardId)) {
				out = null;
			} else {
				out = Long.parseLong(stringCardId);
			}
		} else {
			if (value != null) {
				throw new UnsupportedOperationException("A reference could have a CardReference value");
			}
			out = null;
		}
		return out;
	}

	private boolean areRelationAttributesModified(Iterable<Entry<String, Object>> oldValues, Map<String, Object> newValues, Domain domain) {

		for (Entry<String, Object> oldEntry : oldValues) {
			String attributeName = oldEntry.getKey();
			Object oldAttributeValue = oldEntry.getValue();
			CardAttributeType<?> attributeType = domain.getAttributeOrNull(attributeName).getType();
			Object newValueConverted = rawToSystem(attributeType, newValues.get(attributeName));

			/*
			 * Usually null == null is false. But, here we wanna know if the
			 * value is been changed, so if it was null, and now is still null,
			 * the attribute value is not changed. Do you know that the
			 * CardReferences (value of reference and lookup attributes)
			 * sometimes are null and sometimes is a null-object... Cool! isn't
			 * it? So compare them could be a little tricky
			 */
			if (oldAttributeValue == null) {
				if (newValueConverted == null) {
					continue;
				} else {
					return true;
				}
			} else {
				if (!oldAttributeValue.equals(newValueConverted)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public void updateCards(Iterable<Card> cards) {
		Streams.stream(cards).filter(notNull()).forEach(input -> assureWrite(input.getClassName()));
		for (Card card : cards) {
			doUpdateCard(card);
		}
	}

	@Override
	public void updateFetchedCard(Card card, Map<String, Object> attributes) {
		if (card != null) {
			assureWrite(card.getClassName());
			card = CardImpl.copyOf(card).addAttributes(attributes).build();
//			Card updatedCard = Card.newInstance() //
//					.clone(card) //
//					.clearAttributes() //
//					.withAllAttributes(attributes) //
//					.withUser(getUser().getAuthenticatedUser().getUsername()) //
//					.build();
			doUpdateCard(card);
		}
	}

	@Override
	@Transactional
	public void deleteCard(String className, Long cardId) {
		assureWrite(className);
		lockService.requireNotLockedByOthers(LockService.itemIdFromCardId(cardId));
		dataView.delete(dataView.getCard(className, cardId));

//		Card card = Card.newInstance() //
//				.withClassName(className) //
//				.withId(cardId) //
//				.build();
//
//		try {
//			storeOf(card).delete(card);
//		} catch (UncategorizedSQLException e) {
//			/*
//			 * maybe not the best way to identify the SQL error..
//			 */
//			String message = e.getMessage();
//			RuntimeException _e;
//			if (message != null && message.contains("ERROR: CM_RESTRICT_VIOLATION")) {
//				_e = ORM_CANT_DELETE_CARD_WITH_RELATION.createException();
//			} else {
//				_e = new RuntimeException("error deleting card", e);
//			}
//			throw _e;
//
//		}
	}

	@Override
	public Iterable<Domain> findDomainsForClass(String className, boolean skipDisabledClasses) {
		Classe fetchedClass = dataView.findClasse(className);
		if (fetchedClass == null) {
			throw CLASS_NOTFOUND.createException(className);
		}
		return from(dataView.getDomains()) //
				.filter((d) -> d.isDomainForClasse(fetchedClass)) //
				.filter(skipDisabledClasses ? and(Domain::isActive, (d) -> !d.isDisabledSourceDescendant(fetchedClass) && !d.isDisabledTargetDescendant(fetchedClass)) : allDomains()) //
				.filter(Domain.class);
	}

	@Override
	public Iterable<Domain> findDomains(Optional<String> source, Optional<String> destination, boolean activeOnly, boolean excludeProcesses) {
		Classe _source = source.isPresent() ? dataView.findClasse(source.get()) : null;
		if (source.isPresent() && _source == null) {
			throw CLASS_NOTFOUND.createException(source.get());
		}
		Classe _destination = destination.isPresent() ? dataView.findClasse(destination.get()) : null;
		if (destination.isPresent() && _destination == null) {
			throw CLASS_NOTFOUND.createException(destination.get());
		}

		Predicate<Domain> class1Contained = source.isPresent()
				? (d) -> d.getSourceClass().isAncestorOf(_source) : alwaysTrue();
		Predicate<Domain> class2Contained = destination.isPresent()
				? (d) -> d.getTargetClass().isAncestorOf(_destination) : alwaysTrue();

		Predicate<Domain> class1NotDisabled = source.isPresent()
				? (d) -> !d.getDisabledSourceDescendants().contains(source.get()) : alwaysTrue();
		Predicate<Domain> class2NotDisabled = destination.isPresent()
				? (d) -> !d.getDisabledTargetDescendants().contains(source.get()) : alwaysTrue();

		return from(dataView.getDomains()) //
				.filter(and(class1Contained, class2Contained)) //
				.filter(and(class1NotDisabled, class2NotDisabled)) //
				.filter(activeOnly
						? (d) -> d.isActive() && d.getSourceClass().isActive() && d.getTargetClass().isActive()
						: alwaysTrue()) //
				.filter(excludeProcesses ? (d) -> !d.getSourceClass().isProcess() && !d.getTargetClass().isProcess() : alwaysTrue()) //
				.filter(Domain::hasServiceListPermission) //
				.filter(Domain.class);
	}

	/**
	 * Tells if the given class is a subclass of Activity
	 *
	 * @param target
	 * @return {@code true} if if the given class is a subclass of Activity,
	 * {@code false} otherwise
	 */
	@Override
	public boolean isProcess(Classe target) {
		Classe activity = dataView.getActivityClass();
		return activity.isAncestorOf(target);
	}

	/**
	 * Relations.... move the following code to another class
	 *
	 * @param relationDTO
	 */
	@Override
	@Transactional
	public Iterable<Long> createRelations(RelationDTO relationDTO) {
		Domain domain = dataView.findDomain(relationDTO.domainName);
		if (domain == null) {
			throw DOMAIN_NOTFOUND.createException(relationDTO.domainName);
		}
		Card parentCard = retrieveParentCard(relationDTO);
		List<Card> childCards = retrieveChildCards(relationDTO);

		List<Long> ids = Lists.newArrayList();
		if (relationDTO.master.equals("_1")) {
			for (Card dstCard : childCards) {
				if (from(domain.getDisabledSourceDescendants()).contains(parentCard)) {
					throw DOMAIN_NOTFOUND.createException(relationDTO.domainName);
				}
				if (from(domain.getDisabledTargetDescendants()).contains(dstCard)) {
					throw DOMAIN_NOTFOUND.createException(relationDTO.domainName);
				}
				Long id = saveRelation(domain, parentCard, dstCard, relationDTO.relationAttributeToValue);
				ids.add(id);
			}
		} else {
			for (Card srcCard : childCards) {
				if (from(domain.getDisabledSourceDescendants()).contains(srcCard)) {
					throw DOMAIN_NOTFOUND.createException(relationDTO.domainName);
				}
				if (from(domain.getDisabledTargetDescendants()).contains(parentCard)) {
					throw DOMAIN_NOTFOUND.createException(relationDTO.domainName);
				}
				Long id = saveRelation(domain, srcCard, parentCard, relationDTO.relationAttributeToValue);
				ids.add(id);
			}
		}
		return ids;
	}

	private Card retrieveParentCard(RelationDTO relation) {
		Map<Long, String> cardToClassName;
		if (relation.master.equals("_1")) {
			cardToClassName = relation.srcCardIdToClassName;
		} else {
			cardToClassName = relation.dstCardIdToClassName;
		}
		for (Long cardId : cardToClassName.keySet()) {
			String className = cardToClassName.get(cardId);
			return cardOf(className, cardId);
		}
		return null; // should be unreachable
	}

	private List<Card> retrieveChildCards(RelationDTO relationDTO) {
		List<Card> childCards = Lists.newArrayList();
		Map<Long, String> cardToClassName;
		if (relationDTO.master.equals("_1")) {
			cardToClassName = relationDTO.dstCardIdToClassName;
		} else {
			cardToClassName = relationDTO.srcCardIdToClassName;
		}
		for (Long cardId : cardToClassName.keySet()) {
			String className = cardToClassName.get(cardId);
			childCards.add(cardOf(className, cardId));
		}
		return childCards;
	}

	private Long saveRelation(Domain domain, Card srcCard, Card dstCard, Map<String, Object> attributeToValue) {
		RelationDefinition mutableRelation = dataView.createRelationFor(domain);
		mutableRelation.setSourceCard(srcCard);
		mutableRelation.setTargetCard(dstCard);
		for (String attributeName : attributeToValue.keySet()) {
			Object value = attributeToValue.get(attributeName);
			mutableRelation.set(attributeName, value);
		}
		try {
			mutableRelation.setUser(getUser().getLoginUser().getUsername());
			CMRelation relation = mutableRelation.create();
			return relation.getId();
		} catch (RuntimeException ex) {
			throw ORMExceptionType.ORM_ERROR_RELATION_CREATE.createException();
		}
	}

	@Override
	@Transactional
	public void updateRelation(RelationDTO relationDTO) {
		Domain domain = dataView.findDomain(relationDTO.domainName);
		if (domain == null) {
			throw DOMAIN_NOTFOUND.createException(relationDTO.domainName);
		}

		Entry<Long, String> srcCard = relationDTO.getUniqueEntryForSourceCard();
		String srcClassName = srcCard.getValue();
		Long srcCardId = srcCard.getKey();
		Classe srcClass = dataView.findClasse(srcClassName);

		Entry<Long, String> dstCard = relationDTO.getUniqueEntryForDestinationCard();
		String dstClassName = dstCard.getValue();
		Long dstCardId = dstCard.getKey();

		Card fetchedDstCard = cardOf(dstClassName, dstCardId);
		Card fetchedSrcCard = cardOf(srcClassName, srcCardId);
		Classe dstClass = dataView.findClasse(dstClassName);

		CMQueryRow row;
		WhereClause whereCondition;
		Classe directedSource;

		Alias destinationAlias = (DST_ALIAS);
		Alias domainAlias = (DOM_ALIAS);

		if (relationDTO.master.equals("_1")) {
			directedSource = srcClass;
			whereCondition = and( //
					condition(attribute(srcClass, ID_ATTRIBUTE), eq(srcCardId)), //
					and( //
							condition(attribute(domainAlias, ID_ATTRIBUTE), eq(relationDTO.relationId)), //
							condition(attribute(domainAlias, "_Src"), eq("_1")) //
					));
		} else {
			directedSource = dstClass;
			whereCondition = and( //
					condition(attribute(dstClass, ID_ATTRIBUTE), eq(dstCardId)), //
					and( //
							condition(attribute(domainAlias, ID_ATTRIBUTE), eq(relationDTO.relationId)), //
							condition(attribute(domainAlias, "_Src"), eq("_2"))));
		}

		throw new UnsupportedOperationException("BROKEN - TODO");
//		row = dataView.select(anyAttribute(directedSource)) //
//				.from(directedSource) //
//				.join(anyClass(), destinationAlias, over(domain, domainAlias)) //
//				.where(whereCondition) //
//				.limit(1) //
//				.skipDefaultOrdering() //
//				.run() //
//				.getOnlyRow(); //
//
//		CMRelation relation = row.getRelation(domainAlias).getRelation();
//		RelationDefinition mutableRelation = dataView.update(relation) //
//				.setSourceCard(fetchedSrcCard) //
//				.setTargetCard(fetchedDstCard);
//
//		updateRelationDefinitionAttributes(relationDTO.relationAttributeToValue, mutableRelation);
//		mutableRelation.setUser(getUser().getAuthenticatedUser().getUsername());
//		mutableRelation.update();
	}

	private void updateRelationDefinitionAttributes(Map<String, Object> attributeToValue, RelationDefinition relDefinition) {

		for (Entry<String, Object> entry : attributeToValue.entrySet()) {
			relDefinition.set(entry.getKey(), entry.getValue());
		}
	}

	@Override
	@Transactional
	public void deleteRelation(String domainName, Long relationId) {
		Domain domain = dataView.findDomain(domainName);
		if (domain == null) {
			throw DOMAIN_NOTFOUND.createException(domainName);
		}
		throw new UnsupportedOperationException("broken for 30");
//		dataView.delete(new IdentifiedRelation(domain, relationId));
	}

	@Override
	@Transactional
	public void deleteRelation(String srcClassName, Long srcCardId, String dstClassName, Long dstCardId, Domain domain) {
		Classe sourceClass = dataView.findClasse(srcClassName);
		Classe destinationClass = dataView.findClasse(dstClassName);
		CMRelation relation = getRelation(srcCardId, dstCardId, domain, sourceClass, destinationClass);
		dataView.delete(relation);
	}

	@Override
	public CMRelation getRelation(Long srcCardId, Long dstCardId, Domain domain, Classe sourceClass, Classe destinationClass) {
		/**
		 * The destination alias is mandatory in order to support also
		 * reflective domains
		 */
		Alias DOM = nameAlias("DOM");
		Alias DST = nameAlias(format("DST-%s-%s", destinationClass.getName(), randomAlphanumeric(10)));
		CMQueryRow row = dataView.select(anyAttribute(sourceClass), anyAttribute(DOM)) //
				.from(sourceClass) //
				.join(destinationClass, DST, over(domain, (DOM))) //
				.where(and( //
						condition(attribute(sourceClass, ID_ATTRIBUTE), eq(srcCardId)), //
						condition(attribute(DST, ID_ATTRIBUTE), eq(dstCardId)) //
				)) //
				.limit(1) //
				.skipDefaultOrdering() //
				.run() //
				.getOnlyRow();

		CMRelation relation = row.getRelation(DOM).getRelation();
		return relation;
	}

	@Override
	@Transactional
	public void deleteDetail(final Card master, Card detail, String domainName) {
		Domain domain = dataView.findDomain(domainName);
		if (domain == null) {
			throw DOMAIN_NOTFOUND.createException(domainName);
		}

		String sourceClassName, destinationClassName;
		Long sourceCardId, destinationCardId;

		if (CARDINALITY_1N.value().equals(domain.getCardinality())) {
			sourceClassName = master.getClassName();
			sourceCardId = master.getId();
			destinationClassName = detail.getClassName();
			destinationCardId = detail.getId();
		} else if (CARDINALITY_N1.value().equals(domain.getCardinality())) {
			sourceClassName = detail.getClassName();
			sourceCardId = detail.getId();
			destinationClassName = master.getClassName();
			destinationCardId = master.getId();
		} else {
			throw new UnsupportedOperationException("You are tring to delete a detail over a N to N domain");
		}

		deleteRelation(sourceClassName, sourceCardId, destinationClassName, destinationCardId, domain);
		deleteCard(detail.getClassName(), detail.getId());
	}

	@Override
	public File exportClassAsCsvFile(String className, String separator) {
		Classe fetchedClass = dataView.findClasse(className);
		int separatorInt = separator.charAt(0);
		CsvPreference exportCsvPrefs = new CsvPreference.Builder('"', separatorInt, "\n").build();
		String fileName = fetchedClass.getName() + ".csv";
		String dirName = System.getProperty("java.io.tmpdir");
		File targetFile = new File(dirName, fileName);
		DataExporter dataExporter = new CsvExporter(targetFile, exportCsvPrefs);
		CMDataSource dataSource = new DBDataSource(dataView, fetchedClass);
		return dataExporter.export(dataSource);
	}

//	@Override
//	public CSVData importCsvFileFor(DataHandler csvFile, Long classId, String separator, Collection<? super String> notFoundAttributes) throws IOException, JSONException {
//		Classe destinationClassForImport = dataView.getClassOrNull(classId);
//		int separatorInt = separator.charAt(0);
//		CsvPreference importCsvPreferences = new CsvPreference('"', separatorInt, "\n");
//		CsvReader csvReader = new FilteredCsvReader(new SuperCsvCsvReader(importCsvPreferences),
//				destinationClassForImport, (name) -> {
//					notFoundAttributes.add(name);
//				});
//		CSVImporter csvImporter = new CSVImporter(csvReader, dataView, lookupStore, destinationClassForImport);
//
//		CSVData csvData = csvImporter.getCsvDataFrom(csvFile);
//		return csvData;
//	}
	private Card cardOf(String className, Long cardId) {
		Classe entryType = dataView.findClasse(className);
		CMQueryRow row;
		try {
			row = dataView.select(anyAttribute(entryType)) //
					.from(entryType) //
					.where(condition(attribute(entryType, ID_ATTRIBUTE), eq(cardId))) //
					.limit(1) //
					.skipDefaultOrdering() //
					.run() //
					.getOnlyRow();
		} catch (NoSuchElementException ex) {
			throw CARD_NOTFOUND.createException();
		}
		Card card = row.getCard(entryType);
		return card;
	}

	private void assureWrite(String className) {
		Classe cmClass = findClass(className);
		UserPrivileges privilegeContext = getUser().getPrivilegeContext();
		if (cmClass == null) {
			logger.warn(marker(), "current privilegeContext = {} cannot see class = {} (or class not found)", privilegeContext, className);
			throw new AuthorizationException("operation not allowed on class = %s", className);
		} else if (cmClass.isSuperclass()) {
			logger.warn(marker(), "cannot write class = {}, class is superclass", className);
			throw new AuthorizationException("operation not allowed on class = %s", className);
		} else if (!privilegeContext.hasWriteAccess(cmClass)) {
			logger.warn(marker(), "current privilegeContext = {} cannot write class = {}", privilegeContext, className);
			throw new AuthorizationException("operation not allowed on class = %s", className);
		}
	}

//	protected static final Function<Card, Card> CMCARD_TO_CARD = (Card input) -> CardStorableConverter.of(input).convert(input);
	private static final Comparator<Attribute> NAME_ASC = (Attribute o1, Attribute o2) -> {
		int v1 = o1.getClassOrder();
		int v2 = o2.getClassOrder();
		return (v1 < v2 ? -1 : (v1 == v2 ? 0 : 1));
	};

	private static <T extends Domain> Predicate<T> allDomains() {
		return alwaysTrue();
	}

//	private static <T extends Domain> Predicate<T> usableForReferences(Classe target) {
//		return or(
//				and(domain(cardinality(), equalTo(CARDINALITY_1N.value())),
//						domain(class2(), clazz(anchestorOf(target), equalTo(true)))),
//				and(domain(cardinality(), equalTo(CARDINALITY_N1.value())),
//						domain(class1(), clazz(anchestorOf(target), equalTo(true)))));
//	}
//
//	private static <T extends Domain> Predicate<T> disabledClass(Classe target) {
//		return or(domain(disabled1(), contains(target.getName())), domain(disabled2(), contains(target.getName())));
//	}
//	private final Function<Card, Card> CARD_TO_CARD_WITH_METADATA = (Card input) -> {
//		throw new UnsupportedOperationException("TODO");
////		Map<String, String> metadata = new HashMap<>();
////		metadata.put("writable", Boolean.toString(canWrite(input.getType(), input)));
////		return new CardWithMetadata(input, metadata);
//	};
	@Override
	public Iterable<? extends Classe> findLocalizableClasses(final boolean activeOnly) {
		return from(findClasses(activeOnly)) //
				.filter(Classe::hasServiceModifyPermission);
	}
}

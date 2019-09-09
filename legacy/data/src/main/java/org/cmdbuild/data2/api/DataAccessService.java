package org.cmdbuild.data2.api;

import org.cmdbuild.dao.beans.CardIdAndClassName;
import java.io.File;
import java.util.List;
import java.util.Map;


import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationInfo;
import org.cmdbuild.dao.postgres.legacy.relationquery.GetRelationHistoryResponse;
import org.cmdbuild.dao.postgres.legacy.relationquery.GetRelationListResponse;
import org.cmdbuild.logic.data.QueryOptionsImpl;

import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import org.cmdbuild.common.data.QueryOptions;
import org.cmdbuild.dao.postgres.legacy.relationquery.DomainWithSource;
import org.cmdbuild.data2.impl.RelationDTO;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.view.DataView;

/**
 * Business Logic Layer for Data Access
 */
@Deprecated
public interface DataAccessService {

	DataView getView();

	Map<Object, List<RelationInfo>> relationsBySource(String sourceTypeName, DomainWithSource dom);
	
	GetRelationListResponse getRelationList(CardIdAndClassName srcCard, DomainWithSource dom, QueryOptionsImpl options);

	GetRelationListResponse getRelationList(CardIdAndClassName srcCard, DomainWithSource dom);

	GetRelationListResponse getRelationList(Domain domain, QueryOptionsImpl queryOptions);

	Optional<RelationInfo> getRelation(Domain domain, Long id);

	Optional<RelationInfo> getRelation(String domain, Long id);

	GetRelationHistoryResponse getRelationHistory(CardIdAndClassName srcCard);

	GetRelationHistoryResponse getRelationHistory(CardIdAndClassName srcCard, Domain domain);

	CMRelation getRelation(Long srcCardId, Long dstCardId, Domain domain, Classe sourceClass,
			Classe destinationClass);

	Optional<RelationInfo> getHistoricRelation(String domain, Long id);

//	@Deprecated
//	default Iterable<Card> getCardHistory(CardIdAndClassName srcCard, boolean allAttributes) {
//		return history().query().withAllAttributes(allAttributes).getHistory(srcCard);
//	}
//
//	@Deprecated
//	default Card fetchHistoricCard(String className, Long cardId) {
//		return fetchHistoricCard(new CardIdAndClassNameImpl(className, cardId));
//	}
//
//	@Deprecated
//	default Card fetchHistoricCard(CardIdAndClassName card) {
//		return history().getHistoricCard(card);
//	}
//
//	HistoryService history();

	Classe findClass(Long classId);

	Classe findClass(String className);

	Domain findDomain(Long domainId);

	@Nullable
	Domain findDomain(String domainName);

	default Domain getDomain(String domainId) {
		return checkNotNull(findDomain(domainId), "domain not found for id = %s", domainId);
	}

	boolean hasClass(Long classId);

	/**
	 *
	 * @return only active classes (all classes, included superclasses, simple
	 * classes and process classes).
	 */
	Iterable<? extends Classe> findActiveClasses();

	/**
	 *
	 * @return active and non active domains
	 */
	List<Domain> getAllDomains();

	/**
	 *
	 * @return only active domains
	 */
	Iterable<? extends Domain> findActiveDomains();

	Iterable<? extends Domain> findReferenceableDomains(String className);

	/**
	 *
	 * @return active and non active classes
	 */
	Iterable<? extends Classe> findAllClasses();

	/**
	 *
	 * @param activeOnly
	 * @return all {@link Classe} according with specified status.
	 */
	Iterable<? extends Classe> findClasses(boolean activeOnly);

	PagedElements<Attribute> getAttributes(String className, boolean onlyActive, @Nullable AttrQueryParams attributesQuery);

	PagedElements<Attribute> getDomainAttributes(String className, boolean onlyActive, AttrQueryParams attributesQuery);

	Card fetchCard(String className, Long cardId);

	Card fetchCMCard(String className, Long cardId);

	Card fetchCardShort(String className, Long cardId, QueryOptions queryOptions);

	Card fetchCard(Long classId, Long cardId);

	default Card getCard(CardIdAndClassName cardIdAndClassName) {
		return checkNotNull(fetchCard(cardIdAndClassName.getClassName(), cardIdAndClassName.getId()), "card not found for params = %s", cardIdAndClassName);
	}

	/**
	 * Retrieve the cards of a given class that matches the given query options
	 *
	 * @param className
	 * @param queryOptions
	 * @return a FetchCardListResponse
	 */
	PagedElements<Card> fetchCards(@Nullable String className, QueryOptions queryOptions);

	/**
	 * Execute a given SQL function to select a set of rows Return these rows as
	 * fake cards
	 *
	 * @param functionName
	 * @param queryOptions
	 * @return
	 */
	PagedElements<Card> fetchSQLCards(String functionName, QueryOptions queryOptions);

	/**
	 *
	 * @param className
	 * @param cardId
	 * @param queryOptions
	 * @return a long (zero based) with the position of this card in relation of
	 * current sorting and filter
	 */
	Card getCardPosition(String className, Long cardId, QueryOptions queryOptions);

	PagedElements<Card> fetchCardsWithPosition(String className, QueryOptions queryOptions, Long cardId);

	interface CreateCard {

		Card card();

		boolean manageAlsoDomainsAttributes();

		void created(Long value);

	}

	Long createCard(CreateCard value);

	void createCards(Iterable<CreateCard> values);

	/**
	 * Call createCard forwarding the given card, and saying to manage also the
	 * attributes over references domains
	 *
	 * @param card
	 * @return
	 *
	 * @deprecated
	 */
	@Deprecated
	Long createCard(Card card);

	/**
	 *
	 * @param userGivenCard
	 * @param manageAlsoDomainsAttributes if true iterate over the attributes to
	 * extract the ones with type ReferenceAttributeType. For that attributes
	 * fetch the relation and update the attributes if present in the
	 * userGivenCard
	 * @return
	 *
	 * @deprecated
	 */
	@Deprecated
	Long createCard(Card userGivenCard, boolean manageAlsoDomainsAttributes);

	void updateCard(Card card);

	void updateCards(Iterable<Card> cards);

	void updateFetchedCard(Card card, Map<String, Object> attributes);

	void deleteCard(String className, Long cardId);

	/**
	 * Retrieves all domains in which the class with id = classId is involved
	 * (both direct and inverse relation)
	 *
	 * @param className the class name involved in the relation
	 * @param skipDisabledClasses
	 *
	 * @return a list of all domains defined for the class
	 */
	Iterable<Domain> findDomainsForClass(String className, boolean skipDisabledClasses);

	Iterable<Domain> findDomains(Optional<String> source, Optional<String> destination, boolean activeOnly, boolean excludeProcesses);

	/**
	 * Tells if the given class is a subclass of Activity
	 *
	 * @return {@code true} if if the given class is a subclass of Activity,
	 * {@code false} otherwise
	 */
	boolean isProcess(Classe target);

	/**
	 * Relations.... move the following code to another class
	 *
	 * @return all created relation'ids.
	 */
	Iterable<Long> createRelations(RelationDTO relationDTO);

	void updateRelation(RelationDTO relationDTO);

	void deleteRelation(String domainName, Long relationId);

	void deleteDetail(Card master, Card detail, String domainName);

	void deleteRelation(String srcClassName, Long srcCardId, String dstClassName, Long dstCardId, Domain domain);

	File exportClassAsCsvFile(String className, String separator);

//	CSVData importCsvFileFor(DataHandler csvFile, Long classId, String separator, Collection<? super String> notFoundAttributes) throws IOException, JSONException;

	Card resolveCardReferences(Classe entryType, Card card);

	Iterable<? extends Classe> findLocalizableClasses(boolean activeOnly);

	interface AttrQueryParams {

		Integer limit();

		Integer offset();

	}
}

package org.cmdbuild.dao.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.query.QuerySpecs;
import org.cmdbuild.dao.query.QuerySpecsBuilder;
import org.cmdbuild.dao.query.clause.QueryAttribute;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.cmdbuild.dao.query.QueryResult;
import static org.cmdbuild.dao.query.clause.AnyAttribute.anyAttribute;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import static org.cmdbuild.dao.query.clause.where.EqualsOperatorAndValue.eq;
import static org.cmdbuild.dao.query.clause.where.SimpleWhereClause.condition;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.springframework.jdbc.core.JdbcTemplate; 
import org.cmdbuild.dao.query.QuerySpecsBuilderImpl;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.ClassDefinition;
import org.cmdbuild.dao.entrytype.DomainDefinition; 
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.beans.CardDefinition;
import org.cmdbuild.dao.beans.RelationDefinition;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.PostgresService;

/**
 * This interface provides an abstract view over the data model.
 */
public interface DataView {

	Classe getClassOrNull(Long id);

	default Classe getClass(long oid) {
		return checkNotNull(getClassOrNull(oid), "class not found for oid = %s", oid);
	}

	@Nullable
	Classe findClasse(String name);

	/**
	 *
	 * @param className
	 * @return a cmclass object (never null)
	 * @throws NullPointerException if class not found
	 */
	default Classe getClasse(String className) {
		checkNotBlank(className, "class name cannot be null");
		return checkNotNull(findClasse(className), "class not found for name = %s", className);
	}

//	@Deprecated
//	Classe findClass(NameAndSchema identifier);

	List<Classe> getUserClasses();

	Classe create(ClassDefinition definition);

	Classe update(ClassDefinition definition);

//	Attribute createAttribute(AttributeDefinition definition);
//
//	Attribute updateAttribute(AttributeDefinition definition);
//
//	List<Attribute> updateAttributes(List<AttributeDefinition> attributes);

	void deleteAttribute(Attribute attribute);

	Domain findDomain(Long id);

	Domain findDomain(String name);

//	Domain findDomain(NameAndSchema identifier);

	default Domain getDomain(String domainName) {
		return checkNotNull(findDomain(domainName), "domain not found for name = %s", domainName);
	}

	/**
	 * Returns the active domains.
	 *
	 * @return active domains
	 */
	List<Domain> getDomains();

	Domain create(DomainDefinition definition);

	Domain update(DomainDefinition definition);

	void delete(Domain domain);

	@Nullable
	StoredFunction findFunctionByName(String name);

	default StoredFunction getFunctionByName(String name) {
		return checkNotNull(findFunctionByName(name), "function not found for name = %s", name);
	}

	@Nullable
	default StoredFunction findFunctionById(long id) {
		return getOnlyElement(findAllFunctions().stream().filter((f) -> f.getId() == id).collect(toList()), null);
	}

	default StoredFunction getFunctionById(long id) {
		return checkNotNull(findFunctionById(id), "function not found for id = %s", id);
	}

	List<StoredFunction> findAllFunctions();

	/**
	 * Deletes the specified entry type.
	 *
	 * @param entryType
	 */
	void delete(EntryType entryType);

	/**
	 * Returns an empty card to be modified and saved.
	 *
	 * Note: it does not create a card in the data store until
	 * {@link CardDefinition#save()} is called on the resulting object.
	 *
	 * @param type class for the card
	 *
	 * @return an empty modifiable card
	 */
	CardDefinition createCardFor(Classe type);

	default CardDefinition createCardFor(String classId) {
		return createCardFor(DataView.this.getClasse(classId));
	}

	/**
	 * Returns a modifiable card.
	 *
	 * Note: the changes are not saved in the data store until
	 * {@link CardDefinition#save()} is called on the resulting object.
	 *
	 * @param card immutable card to be modified
	 *
	 * @return a modifiable card from the immutable card
	 */
	CardDefinition update(Card card);

	/**
	 * Deletes the specified card
	 *
	 * @param card
	 */
	void delete(Card card);

	RelationDefinition createRelationFor(Domain domain);

	RelationDefinition update(CMRelation relation);

	CMRelation getRelation(Domain domain, long relationId);

	default CMRelation getRelation(String domainId, long relationId) {
		return getRelation(getDomain(domainId), relationId);
	}

	void delete(CMRelation relation);

	/**
	 * Starts a query. Invoke {@link QuerySpecsBuilder.run()} to execute it.
	 *
	 * @param attrDef select parameters
	 * @return the builder for a new query
	 */
	default QuerySpecsBuilder select(QueryAttribute... attrDef) {
		return new QuerySpecsBuilderImpl(this).select(attrDef);
	}

	/**
	 * Executes a query returning its result.
	 *
	 * @param querySpecs
	 *
	 * @return the query result
	 */
	QueryResult executeQuery(QuerySpecs querySpecs);

	/**
	 * Clears all the contents for the specified type.
	 *
	 * @param type is the type that is to be cleared.
	 */
	void clear(EntryType type);

	Classe getActivityClass();

	Iterable<? extends WhereClause> getAdditionalFiltersFor(EntryType classToFilter);

	/**
	 * return the db driver used by this db data view
	 *
	 * @return db driver
	 */
	PostgresService getDbDriver();

	default JdbcTemplate getJdbcTemplate() {
		return getDbDriver().getJdbcTemplate();
	}

	default @Nullable
	Card findCard(Classe thisClass, long cardId) {
		QueryResult result = select(anyAttribute(thisClass)).from(thisClass).where(condition(attribute(thisClass, ATTR_ID), eq(cardId))).run();
		return result.isEmpty() ? null : result.getOnlyRow().getCard(thisClass);
	}

	default Card getCard(Classe thisClass, long cardId) {
		return checkNotNull(findCard(thisClass, cardId), "card not found for class = %s cardId = %s", thisClass, cardId);
	}

	default Card getCard(String classId, long cardId) {
		return getCard(DataView.this.getClasse(classId), cardId);
	}

	default void delete(Classe classe, long cardId) {
		delete(getCard(classe, cardId));
	}

	default void delete(String classId, long cardId) {
		delete(getCard(getClasse(classId), cardId));
	}

}

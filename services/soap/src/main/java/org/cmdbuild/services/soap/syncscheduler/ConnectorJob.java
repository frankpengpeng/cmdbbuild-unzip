package org.cmdbuild.services.soap.syncscheduler;

import static org.cmdbuild.dao.query.clause.AnyAttribute.anyAttribute;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import static org.cmdbuild.dao.query.clause.where.AndWhereClause.and;
import static org.cmdbuild.dao.query.clause.where.EqualsOperatorAndValue.eq;
import static org.cmdbuild.dao.query.clause.where.SimpleWhereClause.condition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.axis.AxisFault;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.query.CMQueryRow;
import org.cmdbuild.dao.query.QuerySpecsBuilder;
import org.cmdbuild.dao.query.clause.QueryDomain.Source;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.cmdbuild.lookup.Lookup;
import org.cmdbuild.exception.CMDBException;
import org.cmdbuild.exception.NotFoundException;
import org.cmdbuild.dao.postgres.legacy.relationquery.GetRelationListResponse;
import org.cmdbuild.data2.impl.RelationDTO;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.services.soap.connector.DomainDirection;
import org.dom4j.Element;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.postgres.legacy.relationquery.DomainWithSource;
import org.cmdbuild.data2.api.DataAccessService;
import org.cmdbuild.dao.query.QueryResult;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.lookup.LookupRepository;
import org.cmdbuild.dao.entrytype.Domain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.view.DataView;

public class ConnectorJob implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private Action action;
	private boolean isMaster; // id of the master card
	private Long masterCardId; // id of the master card
	private String masterClassName; // name of the class of the master class
	private Long detailCardId; // id of the detail card
	private String detailClassName; // name of the class of the detail class
	private String domainName; // name of the domain between master and detail
	private DomainDirection domainDirection;
	private boolean isShared; // relation 1:N -> 1 detail : N master
	private List<String> sharedIds; // the "id" used to search for the details
	private Element elementCard;
	private final int jobNumber;
	private static int jobNumberCounter = 0;
	private final DataView view;
	private final DataAccessService dataAccessLogic;
	private final LookupRepository lookupStore;

	public ConnectorJob(final DataView view, final DataAccessService dataAccessLogic, final LookupRepository lookupStore) {
		this.dataAccessLogic = dataAccessLogic;
		this.lookupStore = lookupStore;
		this.view = view;
		jobNumber = ++jobNumberCounter;
	}

	private final Map<String, String> referenceToMaster = new HashMap<String, String>();

	public enum Action {
		CREATE("create"), UPDATE("update"), DELETE("delete");

		private final String action;

		Action(final String action) {
			this.action = action;
		}

		public String getAction() {
			return action;
		}

		public static Action getAction(final String action) throws Exception {
			if (Action.CREATE.getAction().equals(action)) {
				return Action.CREATE;
			} else {
				if (Action.UPDATE.getAction().equals(action)) {
					return Action.UPDATE;
				} else if (Action.DELETE.getAction().equals(action)) {
					return Action.DELETE;
				}
			}
			throw new Exception();
		}
	}

	@Override
	public void run() {
		try {
			if (action != null) {
				switch (action) {
					case CREATE:
						logger.info("ExternalSync - create job started [" + jobNumber + "]");
						create();
						break;
					case DELETE:
						logger.info("ExternalSync - delete job started [" + jobNumber + "]");
						delete();
						break;
					case UPDATE:
						logger.info("ExternalSync - update job started [" + jobNumber + "]");
						update();
						break;
					default:
						throw new Exception("No action selected");
				}
			} else {
				logger.info("External Sync - running the current process has failed, try to star the next job");
				throw new Exception("No action selected");
			}
		} catch (final Exception e) {
			logger.info("External Sync - running the current process has failed, try to start the next job " + e);
		}
	}

	/** GETTER / SETTER **/
	public void setIsMaster(final boolean isMaster) {
		this.isMaster = isMaster;
	}

	public void setMasterClassName(final String name) {
		this.masterClassName = name;
	}

	public void setMasterCardId(final Long id) {
		this.masterCardId = id;
	}

	public void setDetailClassName(final String name) {
		this.detailClassName = name;
	}

	public void setDetailCardId(final Long id) {
		this.detailCardId = id;
	}

	public void setElementCard(final Element element) {
		this.elementCard = element;
	}

	public void setDomainName(final String name) {
		this.domainName = name;
	}

	public void setAction(final Action action) {
		this.action = action;
	}

	public void setDomainDirection(final DomainDirection domainDirection) {
		this.domainDirection = domainDirection;
	}

	public void setIsShared(final boolean isShared) {
		this.isShared = isShared;
	}

	public void setDetailIdentifiers(final List<String> identifiers) {
		this.sharedIds = identifiers;
	}

	/**
	 * @throws AxisFault
	 **/
	private void create() throws Exception {
		if (isMaster) {
			createCard();
		} else {
			if (this.masterCardId > 0) {
				if (detailHasReferenceToMaster()) {
					final String referenceName = referenceToMaster.get(detailClassName);
					detailCardId = createCard(referenceName);
				} else {
					detailCardId = createCard();
					if (detailCardId > 0) {
						createRelation();
					}
				}
			} else {
				throw new Exception("MasterCardId is 0");
			}
		}
	}

	private void update() {
		updateCard();
		logger.info("ExternalSync - end update card");

	}

	private void delete() throws Exception {
		if (isMaster) {
			deleteCard();
		} else {
			if (this.masterCardId > 0) {
				if ((!this.isShared) || (this.isShared && isLastSharedDetail())) {
					deleteCard();
					logger.info("ExternalSync - deleting card ");
				} else {
					logger.info("ExternalSync - card detail is shared and has other relations - cannot delete! ");
				}
				if (!detailHasReferenceToMaster()) {
					deleteRelation();
				}
			} else {
				throw new Exception("MasterCardId is 0");
			}
		}
	}

	private boolean isLastSharedDetail() {
		final Card cardDetail = dataAccessLogic.fetchCard(detailClassName, detailCardId);
		final Domain domain = view.findDomain(domainName);
		final DomainWithSource domWithSource;
		if (domain.getSourceClass().getName().equals(detailClassName)) {
			domWithSource = DomainWithSource.create(domain.getId(), Source._1.toString());
		} else {
			domWithSource = DomainWithSource.create(domain.getId(), Source._2.toString());
		}
		final GetRelationListResponse response = dataAccessLogic.getRelationList(cardDetail, domWithSource);
		return response.getTotalNumberOfRelations() > 0;
	}

	/**********************
	 ** CARD MANAGEMENT **
	 **********************/
	private Long createCard() {
		return createCard("");
	}

	private Long createCard(final String referenceName) {
		if (isShared) {
			// searching for an existent object
			final Classe detailClass = view.findClasse(detailClassName);
			final QuerySpecsBuilder querySpecsBuilder = view.select(anyAttribute(detailClass)) //
					.from(detailClass);
			final List<WhereClause> whereClauses = Lists.newArrayList();
			for (final String attributeName : sharedIds) {
				final String attributeValue = searchAttributeValue(attributeName);
				whereClauses.add(condition(attribute(detailClass, attributeName), eq(attributeValue)));
			}
			final WhereClause globalWhereClause = createWhereClauseFrom(whereClauses);
			final QueryResult result = querySpecsBuilder.where(globalWhereClause).run();
			final boolean existingCard = Iterables.size(result) > 0;
			if (existingCard) {
				final CMQueryRow row = result.iterator().next();
				return row.getCard(detailClass).getId();
			}
		}
		// detail does not exist, must be inserted
		logger.info("ExternalSync - insert a new card [class:" + this.detailClassName + "]");
		try {
			final Classe fetchedClass = view.findClasse(detailClassName);
			if (fetchedClass == null) {
				logger.info("The class " + fetchedClass.getName()
						+ " does not exist or the user does not have the privileges to read it");
				throw NotFoundException.NotFoundExceptionType.CLASS_NOTFOUND.createException(detailClassName);
			}
			CardImpl.CardImplBuilder cardBuilder = CardImpl.builder().withType(fetchedClass);
			setCardValues(fetchedClass, cardBuilder);
			if (!referenceName.equals(StringUtils.EMPTY)) {
				logger.info("ExternalSync - the card [class:" + this.detailClassName
						+ "] has a reference to the card-master");
				cardBuilder.addAttribute(referenceName, masterCardId);
			}
			final Card cardToCreate = cardBuilder.build();
			dataAccessLogic.createCard(cardToCreate);
			return cardToCreate.getId();
		} catch (final CMDBException e) {
			logger.error("ExternalSync - exception raised while creating a new card", e);
		}
		return 0L;
	}

	private WhereClause createWhereClauseFrom(List<WhereClause> whereClauses) {
		return and(whereClauses);
	}

	@SuppressWarnings("unchecked")
	private void setCardValues(final Classe cmClass, CardImpl.CardImplBuilder cardBuilder) {
		final Iterator<Element> attributeIterator = elementCard.elementIterator();
		while (attributeIterator.hasNext()) {
			final Element cardAttribute = attributeIterator.next();
			final String attributeName = cardAttribute.getName();
			final String attributeValue = cardAttribute.getText();
			final Attribute attribute = cmClass.getAttributeOrNull(attributeName);
			if (attribute == null) {
				continue;
			}
			if (attribute.getType() instanceof ReferenceAttributeType) {
				addReferenceAttributeTo(attribute, cardBuilder, attributeValue);
			} else if (attribute.getType() instanceof LookupAttributeType) {
				addLookupAttributeTo(attribute, cardBuilder, attributeValue);
			} else {
				cardBuilder.addAttribute(attributeName, attributeValue);
			}
		}
	}

	private Classe referencedClass(final Domain domain, final Classe sourceClass) {
		if (domain.getSourceClass().getName().equals(sourceClass.getName())) {
			return domain.getTargetClass();
		} else {
			return domain.getSourceClass();
		}
	}

	private void addReferenceAttributeTo(final Attribute attribute, CardImpl.CardImplBuilder cardBuilder, final String attributeValue) {
		final ReferenceAttributeType referenceAttributeType = (ReferenceAttributeType) attribute.getType();
		if (StringUtils.isNotBlank(attributeValue)) {
			final String domainName = referenceAttributeType.getDomainName();
			final Domain referenceDomain = view.findDomain(domainName);
			final Classe referencedClass = referencedClass(referenceDomain, (Classe) attribute.getOwner());
			final QueryResult result = view.select(anyAttribute(referencedClass)) //
					.from(referencedClass) //
					.where(condition(attribute(referencedClass, "Description"), eq(attributeValue))) //
					.limit(1) //
					.skipDefaultOrdering() //
					.run();
			if (result.size() > 0) {
				final Long referencedCardId = result.getOnlyRow().getCard(referencedClass).getId();
				cardBuilder.addAttribute(attribute.getName(), referencedCardId);
			}
		}
	}

	private void addLookupAttributeTo(final Attribute attribute, CardImpl.CardImplBuilder cardBuilder,
			final String attributeValue) {
		final LookupAttributeType lookupAttributeType = (LookupAttributeType) attribute.getType();
		if (StringUtils.isNotBlank(attributeValue)) {
			final String lookupTypeName = lookupAttributeType.getLookupTypeName();
			for (final Lookup lookupDto : lookupStore.getAllByType(lookupTypeName)) {
				if (lookupDto.getDescription().equals(attributeValue)) {
					cardBuilder.addAttribute(attribute.getName(), lookupDto.getId());
				}
			}
		}
	}

	/**
	 * params className the classname of the card to update params cardId the id
	 * of the card to update params elementCard the xml Element containing all
	 * values to insert
	 */
	private void updateCard() {
		logger.info("ExternalSync - update card [id:" + this.detailCardId + " classname: " + this.detailClassName
				+ "]");
		try {

			if (this.detailCardId > 0) {
				final Card cardToUpdate = dataAccessLogic.fetchCard(detailClassName, detailCardId);

				logger.info("ExternalSync - set card fields [id:" + this.detailCardId + " classname: "
						+ this.detailClassName + "]");

				CardImpl.CardImplBuilder cardBuilder = CardImpl.copyOf(cardToUpdate);
				setCardValues(cardToUpdate.getType(), cardBuilder);

				logger.info("ExternalSync - end set card fields [id:" + this.detailCardId + " classname: "
						+ this.detailClassName + "]");

				dataAccessLogic.updateCard(cardBuilder.build());

				logger.info("ExternalSync - end save card [id:" + this.detailCardId + " classname: "
						+ this.detailClassName + "]");
			} else {
				logger.warn("ExternalSync - required an update of card with cardId " + this.detailCardId
						+ " (classname " + this.detailClassName + ")");
			}
		} catch (final CMDBException e) {
			logger.error("ExternalSync - exception raised while updating card [id:" + this.detailCardId
					+ " classname: " + this.detailClassName + "]", e);
		}
	}

	/**
	 * params className the classname of the card to update params cardId the id
	 * of the card to update params elementCard the xml Element containg all
	 * values to insert
	 */
	private int deleteCard() {
		logger.info("ExternalSync - delete card [id:" + this.detailCardId + " classname: " + this.detailClassName
				+ "]");
		try {
			dataAccessLogic.deleteCard(detailClassName, detailCardId);
		} catch (final CMDBException e) {
			logger.error("ExternalSync - exception raised while deleting card [id:" + this.detailCardId
					+ " classname: " + this.detailClassName + "]", e);
		}
		return 0;
	}

	/****************************
	 *** RELATION MANAGEMENT ***
	 ****************************/
	private void createRelation() {

		logger.info("ExternalSync - create new relation between " + "card [id:" + this.masterCardId + " classname: "
				+ this.masterClassName + "] and " + "card [id:" + detailCardId + " classname: " + detailClassName
				+ "] " + "on domain: " + domainName);
		try {

			final RelationDTO relationToCreate = new RelationDTO();
			relationToCreate.relationId = null;
			relationToCreate.domainName = domainName;
			if (DomainDirection.DIRECT.equals(this.domainDirection)) {
				relationToCreate.master = Source._1.toString();
				relationToCreate.addSourceCard(masterCardId, masterClassName);
				relationToCreate.addDestinationCard(detailCardId, detailClassName);
			} else {
				relationToCreate.master = Source._2.toString();
				relationToCreate.addSourceCard(detailCardId, detailClassName);
				relationToCreate.addDestinationCard(masterCardId, masterClassName);
			}

			dataAccessLogic.createRelations(relationToCreate);
		} catch (final CMDBException e) {
			logger.error("ExternalSync - exception raised while creating a new relation", e);
			logger.debug("Exception parameters" + e.getExceptionParameters());
		}
	}

	private void deleteRelation() {
		logger.info("ExternalSync - deleting relation between " + "card [id:" + this.masterCardId + " classname: "
				+ this.masterClassName + "] and " + "card [id:" + detailCardId + " classname: " + this.detailClassName
				+ "] " + "on domain: " + domainName);
		try {
			final Domain domain = view.findDomain(domainName);
			dataAccessLogic.deleteRelation(masterClassName, masterCardId, detailClassName, detailCardId, domain);
		} catch (final CMDBException e) {
			logger.error("ExternalSync - exception raised while creating a new relation", e);
		}
	}

	private boolean detailHasReferenceToMaster() {
		if (referenceToMaster.containsKey(this.detailClassName)) {
			final String referenceName = referenceToMaster.get(this.detailClassName);
			return referenceName != null && !referenceName.equals("");
		} else {
			final Classe detailClass = view.findClasse(detailClassName);
			for (final Attribute attribute : detailClass.getServiceAttributes()) {
				if (attribute.getType() instanceof ReferenceAttributeType) {
					final ReferenceAttributeType referenceAttributeType = (ReferenceAttributeType) attribute.getType();
					final String referencedDomainName = referenceAttributeType.getDomainName();
					if (referencedDomainName.equals(domainName)) {
						referenceToMaster.put(this.detailClassName, attribute.getName());
						return true;
					}
				}
			}
			referenceToMaster.put(domainName, StringUtils.EMPTY);
			return false;
		}
	}

	@SuppressWarnings(value = {"unchecked"})
	private String searchAttributeValue(final String attributeName) {
		final Iterator<Element> attributeIterator = elementCard.elementIterator();
		while (attributeIterator.hasNext()) {
			final Element cardAttribute = attributeIterator.next();
			if (cardAttribute.getName().equals(attributeName)) {
				return cardAttribute.getText();
			}
		}
		return "";
	}
}

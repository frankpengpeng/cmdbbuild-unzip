package org.cmdbuild.dao.postgres.legacy.relationquery;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.cmdbuild.dao.postgres.Const.SystemAttributes.DomainId1;
import static org.cmdbuild.dao.postgres.Const.SystemAttributes.DomainId2;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import static org.cmdbuild.dao.query.clause.where.AndWhereClause.and;
import static org.cmdbuild.dao.query.clause.where.EqualsOperatorAndValue.eq;
import static org.cmdbuild.dao.query.clause.where.SimpleWhereClause.condition;
import static org.cmdbuild.dao.query.clause.where.TrueWhereClause.trueWhereClause;

import org.cmdbuild.dao.query.QuerySpecsBuilder;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.query.clause.where.EmptyWhereClause;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.query.clause.alias.NameAlias.nameAlias;
import org.cmdbuild.dao.view.DataView;

public class AbstractGetRelation {

	// TODO Change Code, Description, Id with something meaningful
	protected static final String ID = org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
	public static final String IDOBJ1 = DomainId1.getDBName();
	public static final String IDOBJ2 = DomainId2.getDBName();
	protected static final String CODE = org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
	protected static final String DESCRIPTION = org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;

	protected static final Alias SRC_ALIAS = nameAlias("SRC");
	protected static final Alias DOM_ALIAS = nameAlias("DOM");
	protected static final Alias DST_ALIAS = nameAlias("DST");

	protected final DataView view;

	public AbstractGetRelation(DataView view) {
		this.view = checkNotNull(view);
	}

	protected QuerySpecsBuilder getRelationQuerySpecsBuilder(String classId, Long cardId, Domain domain, WhereClause whereClause) {
		Classe srcCardType = view.getClasse(classId);
		WhereClause _whereClause = ((whereClause == null) || (whereClause instanceof EmptyWhereClause)) ? trueWhereClause() : whereClause;
		WhereClause clause;
		if (defaultIfNull(cardId, 0L) > 0L) {
			clause = and(condition(attribute(SRC_ALIAS, ID), eq(cardId)), _whereClause);
		} else {
			clause = _whereClause;
		}
		return getRelationQuery(srcCardType, domain).where(clause);
	}

	protected QuerySpecsBuilder getRelationQuery(Classe sourceType, Domain domain) {
			throw new UnsupportedOperationException("BROKEN - TODO");
//		return view
//				.select(attribute(SRC_ALIAS, CODE), attribute(SRC_ALIAS, DESCRIPTION), anyAttribute(DOM_ALIAS),
//						attribute(DST_ALIAS, CODE), attribute(DST_ALIAS, DESCRIPTION)) //
//				.from(sourceType, (SRC_ALIAS)) //
//				.join(anyClass(), (DST_ALIAS), over(domain, (DOM_ALIAS))) //
//				.orderBy(attribute(DST_ALIAS, DESCRIPTION), Direction.ASC) //
//				.count();
	}

}

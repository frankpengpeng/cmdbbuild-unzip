package org.cmdbuild.dao.postgres.legacy.relationquery;

import static com.google.common.collect.FluentIterable.from;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import static org.cmdbuild.dao.query.clause.where.EqualsOperatorAndValue.eq;
import static org.cmdbuild.dao.query.clause.where.WhereClauses.condition;

import org.cmdbuild.dao.query.CMQueryRow;
import org.cmdbuild.dao.query.clause.QueryRelation;

import com.google.common.base.Optional;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationInfo;
import org.cmdbuild.dao.postgres.legacy.relationquery.RelationSingleService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.view.DataView;

@Component
public class RelationSingleServiceImpl extends AbstractGetRelation implements RelationSingleService {

	public RelationSingleServiceImpl(DataView dataView) {
		super(dataView);
	}

	@Override
	public Optional<RelationInfo> getRelationInfo(Domain domain, Long id) {
		Classe source = domain.getSourceClass();
		return from(getRelationQuery(source, domain) //
				.where(condition(attribute(DOM_ALIAS, ID), eq(id))) //
				.run()) //
				.transform((CMQueryRow input) -> {
					QueryRelation rel = input.getRelation(DOM_ALIAS);
					Card src = input.getCard(SRC_ALIAS);
					Card dst = input.getCard(DST_ALIAS);
					return (RelationInfo) new RelationInfoImpl(rel, src, dst);
				}) //
				.filter((RelationInfo input) -> input.getQueryDomain().getDirection()) //
				.first();
	}

}

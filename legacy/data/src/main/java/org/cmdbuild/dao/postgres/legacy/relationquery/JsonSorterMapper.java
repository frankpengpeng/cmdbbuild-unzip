package org.cmdbuild.dao.postgres.legacy.relationquery;

import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;

import java.util.List;

import org.cmdbuild.dao.query.clause.OrderByClause;
import org.cmdbuild.dao.query.clause.OrderByClause.Direction;
import org.cmdbuild.dao.query.clause.QueryAliasAttribute;
import org.cmdbuild.dao.query.clause.alias.Alias;

import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class JsonSorterMapper implements SorterMapper {

	private final EntryType entryType;
	private final CmdbSorter sorters;
	private final Alias entryTypeAlias;

	public JsonSorterMapper(EntryType entryType, CmdbSorter sorters, Alias alias) {
		this.entryType = entryType;
		this.sorters = sorters;
		this.entryTypeAlias = alias;
	}

	public JsonSorterMapper(EntryType entryType, CmdbSorter sorters) {
		this(entryType, sorters, null);
	}

	@Override
	public List<OrderByClause> deserialize() {
		List<OrderByClause> orderByClauses = list();
		if (sorters != null) {
			sorters.getElements().forEach((element) -> {
				QueryAliasAttribute queryAliasAttribute = buildQueryAliasAttribute(element.getProperty());
				orderByClauses.add(new OrderByClause(queryAliasAttribute, Direction.valueOf(element.getDirection().name())));
			});
		}
		return orderByClauses;
	}

	private QueryAliasAttribute buildQueryAliasAttribute(String attribute) {
		QueryAliasAttribute queryAliasAttribute;
		if (entryTypeAlias == null) {
			queryAliasAttribute = attribute(entryType, attribute);
		} else {
			queryAliasAttribute = attribute(entryTypeAlias, attribute);
		}
		return queryAliasAttribute;
	}

}

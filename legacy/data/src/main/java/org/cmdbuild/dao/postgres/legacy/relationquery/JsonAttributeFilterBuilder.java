package org.cmdbuild.dao.postgres.legacy.relationquery;

import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.size;
import static java.util.Arrays.asList;
import static org.cmdbuild.dao.postgres.Const.SystemAttributes.DomainId1;
import static org.cmdbuild.dao.postgres.Const.SystemAttributes.DomainId2;
import static org.cmdbuild.dao.postgres.Const.SystemAttributes.Id;
import static org.cmdbuild.dao.postgres.Const.SystemAttributes.IdClass;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;
import static org.cmdbuild.dao.query.clause.where.AndWhereClause.and;
import static org.cmdbuild.dao.query.clause.where.NotWhereClause.not;
import static org.cmdbuild.dao.query.clause.where.NullOperatorAndValue.isNull;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.beginsWith;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.contains;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.endsWith;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.eq;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.gt;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.gteq;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.in;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.lt;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.lteq;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.networkContained;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.networkContainedOrEqual;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.networkContains;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.networkContainsOrEqual;
import static org.cmdbuild.dao.query.clause.where.OperatorAndValues.networkRelationed;
import static org.cmdbuild.dao.query.clause.where.OrWhereClause.or;
import static org.cmdbuild.dao.query.clause.where.SimpleWhereClause.condition;
import static org.cmdbuild.logic.mapping.json.Constants.Filters._TYPE;

import java.util.List;

import org.apache.commons.lang3.Validate;
import org.cmdbuild.common.Builder;
import org.cmdbuild.dao.entrytype.attributetype.RegclassAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.UndefinedAttributeType;
import org.cmdbuild.dao.query.clause.HistoricEntryType;
import org.cmdbuild.dao.query.clause.QueryAliasAttribute;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.query.clause.where.WhereClause;

import com.google.common.base.Function;
import static com.google.common.base.Functions.identity;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import com.google.common.collect.Lists;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.data.filter.AttributeFilter;
import org.cmdbuild.data.filter.AttributeFilterCondition;
import org.cmdbuild.data.filter.AttributeFilterCondition.ConditionOperator;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.view.DataView;

/**
 * Class that creates a WhereClause starting from a json string. This where
 * clause will "retrieve" the cards of the specified entry type that match the
 * filter. It is used only for filter on attributes.
 */
@Deprecated
public class JsonAttributeFilterBuilder implements Builder<WhereClause> {

	private AttributeFilter filter;
	private EntryType entryType;
	private Alias entryTypeAlias;
	private DataView dataView;
	private Function<WhereClause, WhereClause> function = identity();

	public JsonAttributeFilterBuilder withFilter(AttributeFilter filter) {
		this.filter = filter;
		return this;
	}

	public JsonAttributeFilterBuilder withEntryType(EntryType entryType) {
		this.entryType = entryType;
		return this;
	}

	public JsonAttributeFilterBuilder withEntryTypeAlias(Alias entryTypeAlias) {
		this.entryTypeAlias = entryTypeAlias;
		return this;
	}

	public JsonAttributeFilterBuilder withDataView(DataView dataView) {
		this.dataView = dataView;
		return this;
	}

	public JsonAttributeFilterBuilder withFunction(Function<WhereClause, WhereClause> function) {
		this.function = function;
		return this;
	}

	@Override
	public WhereClause build() {
		checkNotNull(filter, "missing filter");
		checkNotNull(entryType, "missing entry type");
		checkNotNull(dataView, "missing data view");
		return buildWhereClause(filter);
	}

	protected WhereClause buildWhereClause(AttributeFilter filter) {
		EntryType queryEntryType = this.entryType;
		switch (filter.getMode()) {
			case SIMPLE:
				AttributeFilterCondition condition = filter.getCondition();
				if (condition.hasClassName()) {
					queryEntryType = dataView.getClasse(condition.getClassName());
				}
				QueryAliasAttribute attribute = (entryTypeAlias == null) ? attribute(queryEntryType, condition.getKey()) : attribute(entryTypeAlias, condition.getKey());
				return function.apply(buildSimpleWhereClause(attribute, condition.getOperator(), condition.getValues()));
			case AND:
				return and(filter.getElements().stream().map(this::buildWhereClause).collect(toList()));
			case OR:
				return or(filter.getElements().stream().map(this::buildWhereClause).collect(toList()));
			case NOT:
				return not(buildWhereClause(filter.getOnlyElement()));
			default:
				throw new IllegalArgumentException(format("unsupoorted filter mode = %s", filter.getMode()));

		}
	}

	/**
	 * NOTE: @parameter values is always an array of strings
	 */
	private WhereClause buildSimpleWhereClause(QueryAliasAttribute attribute, ConditionOperator operator, List values) {
		/**
		 * In this way if the user does not have privileges to read that
		 * attributes, it is possible to fetch it to build the correct where
		 * clause
		 */
		EntryType _entryType;
		if (entryType instanceof HistoricEntryType<?>) {
			_entryType = HistoricEntryType.class.cast(entryType).getType();
		} else {
			_entryType = entryType;
		}
		EntryType dbEntryType = dataView.findClasse(_entryType.getName());
		Attribute a = dbEntryType.getAttributeOrNull(attribute.getName());
		CardAttributeType<?> type = (a == null) ? UndefinedAttributeType.undefined() : a.getType();
		return buildSimpleWhereClause(attribute, operator, values, type);
	}

	private WhereClause buildSimpleWhereClause(QueryAliasAttribute attribute, ConditionOperator operator, List<?> values, CardAttributeType<?> type) {
		QueryAliasAttribute _attribute;
		if (asList(_TYPE).contains(attribute.getName())) {
			_attribute = attribute(attribute.getAlias(), IdClass.getDBName());
		} else {
			_attribute = attribute;
		}

		CardAttributeType<?> _type;
		if (asList(Id.getDBName(), DomainId1.getDBName(), DomainId2.getDBName()).contains(attribute.getName())) {
			_type = new IntegerAttributeType();
		} else if (asList(IdClass.getDBName()).contains(attribute.getName())) {
			_type = RegclassAttributeType.INSTANCE;
		} else if (asList(_TYPE).contains(attribute.getName())) {
			_type = RegclassAttributeType.INSTANCE;
		} else {
			_type = type;
		}

		List _values;
		if (asList(_TYPE).contains(attribute.getName())) {
			_values = values.stream()
					.map((input) -> dataView.findClasse((String) input))
					.filter(notNull())
					.map((c) -> c.getId())
					.collect(toList());
		} else {
			_values = values;
		}

		return buildSimpleWhereClause0(_attribute, operator, _values, _type);
	}

	private WhereClause buildSimpleWhereClause0(QueryAliasAttribute attribute, ConditionOperator operator, List values, CardAttributeType<?> type) {
		switch (operator) {
			case EQUAL:
				Validate.isTrue(size(values) == 1);
				return condition(attribute, eq(rawToSystem(type, get(values, 0))));
			case NOTEQUAL:
				Validate.isTrue(size(values) == 1);
				return or(not(condition(attribute, eq(rawToSystem(type, get(values, 0))))), condition(attribute, isNull()));
			case ISNULL:
				Validate.isTrue(size(values) == 0);
				return condition(attribute, isNull());
			case ISNOTNULL:
				Validate.isTrue(size(values) == 0);
				return not(condition(attribute, isNull()));
			case GREATER:
				Validate.isTrue(size(values) == 1);
				return condition(attribute, gt(rawToSystem(type, get(values, 0))));
			case LESS:
				Validate.isTrue(size(values) == 1);
				return condition(attribute, lt(rawToSystem(type, get(values, 0))));
			case BETWEEN:
				Validate.isTrue(size(values) == 2);
				return and(condition(attribute, gteq(rawToSystem(type, get(values, 0)))), condition(attribute, lteq(rawToSystem(type, get(values, 1)))));
			case LIKE:
				Validate.isTrue(size(values) == 1);
				return condition(attribute, contains(rawToSystem(type, get(values, 0))));
			case CONTAIN:
				Validate.isTrue(size(values) == 1);
				return condition(attribute, contains(rawToSystem(type, get(values, 0))));
			case NOTCONTAIN:
				Validate.isTrue(size(values) == 1);
				return or(not(condition(attribute, contains(rawToSystem(type, get(values, 0))))), condition(attribute, isNull()));
			case BEGIN:
				Validate.isTrue(size(values) == 1);
				return condition(attribute, beginsWith(rawToSystem(type, get(values, 0))));
			case NOTBEGIN:
				Validate.isTrue(size(values) == 1);
				return or(not(condition(attribute, beginsWith(rawToSystem(type, get(values, 0))))), condition(attribute, isNull()));
			case END:
				Validate.isTrue(size(values) == 1);
				return condition(attribute, endsWith(rawToSystem(type, get(values, 0))));
			case NOTEND:
				Validate.isTrue(size(values) == 1);
				return or(not(condition(attribute, endsWith(rawToSystem(type, get(values, 0))))), condition(attribute, isNull()));
			case IN:
				Validate.isTrue(size(values) >= 1);
				List<Object> _values = Lists.newArrayList();
				for (int i = 0; i < size(values); i++) {
					_values.add(rawToSystem(type, get(values, i)));
				}
				return condition(attribute, in(_values.toArray()));
			case NET_CONTAINED:
				Validate.isTrue(size(values) == 1);
				return condition(attribute, networkContained(rawToSystem(type, get(values, 0))));
			case NET_CONTAINEDOREQUAL:
				Validate.isTrue(size(values) == 1);
				return condition(attribute, networkContainedOrEqual(rawToSystem(type, get(values, 0))));
			case NET_CONTAINS:
				Validate.isTrue(size(values) == 1);
				return condition(attribute, networkContains(rawToSystem(type, get(values, 0))));
			case NET_CONTAINSOREQUAL:
				Validate.isTrue(size(values) == 1);
				return condition(attribute, networkContainsOrEqual(rawToSystem(type, get(values, 0))));
			case NET_RELATION:
				Validate.isTrue(size(values) == 1);
				return condition(attribute, networkRelationed(rawToSystem(type, get(values, 0))));
			default:
				throw new IllegalArgumentException("The operator " + operator + " is not supported");
		}
	}
}

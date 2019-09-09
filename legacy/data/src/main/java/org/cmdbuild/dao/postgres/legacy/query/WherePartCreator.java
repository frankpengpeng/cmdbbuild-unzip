package org.cmdbuild.dao.postgres.legacy.query;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.dao.postgres.Const.OPERATOR_EQ;
import static org.cmdbuild.dao.postgres.Const.OPERATOR_GT;
import static org.cmdbuild.dao.postgres.Const.OPERATOR_GT_EQ;
import static org.cmdbuild.dao.postgres.Const.OPERATOR_ILIKE;
import static org.cmdbuild.dao.postgres.Const.OPERATOR_IN;
import static org.cmdbuild.dao.postgres.Const.OPERATOR_LT;
import static org.cmdbuild.dao.postgres.Const.OPERATOR_LT_EQ;
import static org.cmdbuild.dao.postgres.Const.OPERATOR_NULL;
import static org.cmdbuild.dao.query.clause.QueryAliasAttribute.attribute;

import java.util.List;

import org.cmdbuild.dao.CardStatus;
import org.cmdbuild.dao.postgres.Const.SystemAttributes;
import org.cmdbuild.dao.postgres.utils.SqlQueryUtils;
import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.entrytype.CMEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.CMFunctionCall;
import org.cmdbuild.dao.entrytype.ForwardingEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.NullEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.UndefinedAttributeType;
import org.cmdbuild.dao.query.QuerySpecs;
import org.cmdbuild.dao.query.clause.QueryAliasAttribute;
import org.cmdbuild.dao.query.clause.QueryAttribute;
import org.cmdbuild.dao.query.clause.from.FromClause;
import org.cmdbuild.dao.query.clause.where.AndWhereClause;
import org.cmdbuild.dao.query.clause.where.BeginsWithOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.ContainsOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.EmptyArrayOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.EmptyWhereClause;
import org.cmdbuild.dao.query.clause.where.EndsWithOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.EqualsIgnoreCaseOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.EqualsOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.FalseWhereClause;
import org.cmdbuild.dao.query.clause.where.FunctionWhereClause;
import org.cmdbuild.dao.query.clause.where.GreaterThanOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.GreaterThanOrEqualToOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.InOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.LessThanOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.LessThanOrEqualToOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.NetworkContained;
import org.cmdbuild.dao.query.clause.where.NetworkContainedOrEqual;
import org.cmdbuild.dao.query.clause.where.NetworkContains;
import org.cmdbuild.dao.query.clause.where.NetworkContainsOrEqual;
import org.cmdbuild.dao.query.clause.where.NetworkRelationed;
import org.cmdbuild.dao.query.clause.where.NotWhereClause;
import org.cmdbuild.dao.query.clause.where.NullOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.OperatorAndValueVisitor;
import org.cmdbuild.dao.query.clause.where.OrWhereClause;
import org.cmdbuild.dao.query.clause.where.SimpleWhereClause;
import org.cmdbuild.dao.query.clause.where.StringArrayOverlapOperatorAndValue;
import org.cmdbuild.dao.query.clause.where.TrueWhereClause;
import org.cmdbuild.dao.query.clause.where.WhereClause;
import org.cmdbuild.dao.query.clause.where.WhereClauseVisitor;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import static com.google.common.collect.Iterables.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.postgres.SqlTypeName;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.getSystemToSqlCastOrNull;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSql;

@Deprecated
public class WherePartCreator extends PartCreator implements WhereClauseVisitor {

    private static final String CAST_OPERATOR = "::";

    private final QuerySpecs querySpecs;

    public WherePartCreator(QuerySpecs querySpecs) {
        this(querySpecs, () -> {
            final FromClause fromClause = querySpecs.getFromClause();
            return fromClause.getType().hasHistory() && !fromClause.isHistory();
        });
    }

    public WherePartCreator(QuerySpecs querySpecs, ActiveStatusChecker activeStatusChecker) {
        super();
        this.querySpecs = querySpecs;
        querySpecs.getWhereClause().accept(this);
        if (activeStatusChecker.needsActiveStatus()) {
            and(attributeFilter(attribute(querySpecs.getFromClause().getAlias(), SystemAttributes.Status.getDBName()),
                    null, OPERATOR_EQ, valuesOf(CardStatus.ACTIVE.value())));
        }
        excludeEntryTypes();
    }

    /**
     * Excludes disabled classes or not accessible classes (due to lack of
     * privileges)
     */
    private void excludeEntryTypes() {
        querySpecs.getFromClause().getType().accept(new ForwardingEntryTypeVisitor() {

            private final CMEntryTypeVisitor delegate = NullEntryTypeVisitor.getInstance();

            @Override
            protected CMEntryTypeVisitor delegate() {
                return delegate;
            }

            @Override
            public void visit(final Classe type) {
                throw new UnsupportedOperationException();
//				for (final Classe cmClass : type.getLeaves()) {
//					final FromClause.EntryTypeStatus status = querySpecs.getFromClause().getStatus(cmClass);
//					if (!status.isAccessible() || !status.isActive()) {
//						andNot(attributeFilter(
//								attribute(querySpecs.getFromClause().getAlias(), SystemAttributes.IdClass.getDBName()),
//								null, OPERATOR_EQ, valuesOf(cmClass.getId())));
//					}
//				}
            }

        });
    }

    private WherePartCreator append(final String string) {
        if (sb.length() == 0) {
            sb.append("WHERE");
        }
        sb.append(" ").append(string);
        return this;
    }

    private void and(final String string) {
        if (sb.length() > 0) {
            append("AND");
        }
        append(string);
    }

    private void or(final String string) {
        if (sb.length() > 0) {
            append("OR");
        }
        append(string);
    }

    private void andNot(final String string) {
        if (sb.length() > 0) {
            append("AND NOT");
        }
        append(string);
    }

    @Override
    public void visit(final AndWhereClause whereClause) {
        append("(");
        // TODO do it better
        final List<? extends WhereClause> clauses = whereClause.getClauses();
        for (int i = 0; i < clauses.size(); i++) {
            if (i > 0) {
                and(" ");
            }
            clauses.get(i).accept(this);
        }
        append(")");
    }

    @Override
    public void visit(final SimpleWhereClause whereClause) {
        whereClause.getOperator().accept(new OperatorAndValueVisitor() {

            private static final String OPERATOR_INET_IS_CONTAINED_WITHIN = "<<";
            private static final String OPERATOR_INET_IS_CONTAINED_WITHIN_OR_EQUALS = "<<=";
            private static final String OPERATOR_INET_CONTAINS = ">>";
            private static final String OPERATOR_INET_CONTAINS_OR_EQUALS = ">>=";

            @Override
            public void visit(final EqualsOperatorAndValue operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(), OPERATOR_EQ,
                        valueOf(operatorAndValue.getValue())));
            }

            @Override
            public void visit(final EqualsIgnoreCaseOperatorAndValue operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(), OPERATOR_ILIKE,
                        valueOf(operatorAndValue.getValue())));
            }

            @Override
            public void visit(final GreaterThanOperatorAndValue operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(), OPERATOR_GT,
                        valueOf(operatorAndValue.getValue())));
            }

            @Override
            public void visit(final GreaterThanOrEqualToOperatorAndValue operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(), OPERATOR_GT_EQ,
                        valueOf(operatorAndValue.getValue())));
            }

            @Override
            public void visit(final LessThanOperatorAndValue operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(), OPERATOR_LT,
                        valueOf(operatorAndValue.getValue())));
            }

            @Override
            public void visit(final LessThanOrEqualToOperatorAndValue operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(), OPERATOR_LT_EQ,
                        valueOf(operatorAndValue.getValue())));
            }

            @Override
            public void visit(final ContainsOperatorAndValue operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(), OPERATOR_ILIKE,
                        valueOf("%" + operatorAndValue.getValue() + "%")));
            }

            @Override
            public void visit(final BeginsWithOperatorAndValue operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(), OPERATOR_ILIKE,
                        valueOf(operatorAndValue.getValue() + "%")));
            }

            @Override
            public void visit(final EndsWithOperatorAndValue operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(), OPERATOR_ILIKE,
                        valueOf("%" + operatorAndValue.getValue())));
            }

            @Override
            public void visit(final NullOperatorAndValue operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(), OPERATOR_NULL, null));
            }

            @Override
            public void visit(final InOperatorAndValue operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(), OPERATOR_IN, valuesOf(operatorAndValue.getValue())));
            }

            @Override
            public void visit(StringArrayOverlapOperatorAndValue operatorAndValue) {
                QueryAttribute attributeAlias = whereClause.getAttribute();
                String quotedAttributeName = SqlQueryUtils.quoteAttribute(attributeAlias.getAlias(), attributeAlias.getName());
                Object value = operatorAndValue.getValue();
                if (value instanceof Iterable) {
                    checkArgument(!isEmpty((Iterable) value));
                    value = Joiner.on(",").join((Iterable) value);
                }
                append(format(" %s && string_to_array('%s',',')::varchar[] ", quotedAttributeName, value));
            }

            @Override
            public void visit(final EmptyArrayOperatorAndValue operatorAndValue) {
                final String template = " coalesce(array_length(%s, 1), 0) = 0 ";
                final QueryAttribute attributeAlias = whereClause.getAttribute();
                final String quotedAttributeName
                        = SqlQueryUtils.quoteAttribute(attributeAlias.getAlias(), attributeAlias.getName());

                append(String.format(template, quotedAttributeName));
            }

            @Override
            public void visit(final NetworkContained operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(),
                        OPERATOR_INET_IS_CONTAINED_WITHIN, valueOf(operatorAndValue.getValue())));
            }

            @Override
            public void visit(final NetworkContainedOrEqual operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(),
                        OPERATOR_INET_IS_CONTAINED_WITHIN_OR_EQUALS, valueOf(operatorAndValue.getValue())));
            }

            @Override
            public void visit(final NetworkContains operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(),
                        OPERATOR_INET_CONTAINS, valueOf(operatorAndValue.getValue())));
            }

            @Override
            public void visit(final NetworkContainsOrEqual operatorAndValue) {
                append(attributeFilter(whereClause.getAttribute(), whereClause.getAttributeNameCast(),
                        OPERATOR_INET_CONTAINS_OR_EQUALS, valueOf(operatorAndValue.getValue())));
            }

            @Override
            public void visit(final NetworkRelationed operatorAndValue) {
                final QueryAttribute attribute = whereClause.getAttribute();
                final String cast = whereClause.getAttributeNameCast();
                final Supplier<Object> valueOf = valueOf(operatorAndValue.getValue());
                append("(" //
                        + //
                        attributeFilter(attribute, cast, OPERATOR_INET_IS_CONTAINED_WITHIN_OR_EQUALS, valueOf) //
                        + " OR " //
                        + attributeFilter(attribute, cast, OPERATOR_EQ, valueOf) //
                        + " OR " //
                        + attributeFilter(attribute, cast, OPERATOR_INET_CONTAINS_OR_EQUALS, valueOf) //
                        + ")");
            }

            private Supplier<Object> valueOf(final Object value) {
                return WherePartCreator.this.valuesOf(value);
            }

        });
    }

    private Supplier<Object> valuesOf(final Object value) {
        return Suppliers.ofInstance(value);
    }

    @Override
    public void visit(final OrWhereClause whereClause) {
        append("(");
        // TODO do it better
        final List<? extends WhereClause> clauses = whereClause.getClauses();
        for (int i = 0; i < clauses.size(); i++) {
            if (i > 0) {
                or(" ");
            }
            clauses.get(i).accept(this);
        }
        append(")");
    }

    @Override
    public void visit(final NotWhereClause whereClause) {
        append("NOT (");
        whereClause.getClause().accept(this);
        append(")");
    }

    @Override
    public void visit(final EmptyWhereClause whereClause) {
        if (sb.length() != 0) {
            throw new IllegalArgumentException("Cannot use an empty clause along with other where clauses");
        }
    }

    @Override
    public void visit(final TrueWhereClause whereClause) {
        append(" TRUE ");
    }

    @Override
    public void visit(final FalseWhereClause whereClause) {
        append(" FALSE ");
    }

    @Override
    public void visit(final FunctionWhereClause whereClause) {
        append(format("%s %s (SELECT %s(?, ?, ?)) ", nameOf(whereClause.attribute), OPERATOR_IN, whereClause.name));
        param(whereClause.userId.intValue());
        param(whereClause.roleId.intValue());
        param(whereClause.entryType.getName());
    }

    private String attributeFilter(QueryAttribute attribute, String attributeNameCast, String operator, Supplier<Object> holder) {
        String attributeName = nameOf(attribute, attributeNameCast);
        String attributeCast = attributeCastOf(attribute, attributeNameCast);
        String value;
        if (holder == null) {
            value = EMPTY;
        } else if (isNotBlank(attributeNameCast)) {
            value = param(systemToSql(SqlTypeName.valueOf(attributeNameCast), holder.get()), attributeCast);
        } else {
            value = param(sqlValueOf(attribute, holder.get()), attributeCast);
        }
        return format("%s %s %s", attributeName, operator, value);
    }

    private String nameOf(final QueryAliasAttribute attribute) {
        return nameOf(attribute, null);
    }

    private String nameOf(QueryAttribute attribute, final String attributeNameCast) {
        String attributeName = SqlQueryUtils.quoteAttribute(attribute.getAlias(), attribute.getName());
        return new StringBuilder(attributeName) //
                .append(attributeNameCast != null ? (CAST_OPERATOR + attributeNameCast) : EMPTY) //
                .toString();
    }

    private String attributeCastOf(final QueryAttribute attribute, final String attributeNameCast) {
        final boolean isAttributeNameCastSpecified = (attributeNameCast != null);
        return isAttributeNameCastSpecified ? null : getSystemToSqlCastOrNull(typeOf(attribute));
    }

    private Object sqlValueOf(final QueryAttribute attribute, final Object value) {
        if (value instanceof IdAndDescriptionImpl) {
            return IdAndDescriptionImpl.class.cast(value).getId();
        }
//		return systemToSql(typeOf(attribute), value);
        throw new UnsupportedOperationException();
    }

    private CardAttributeType typeOf(final QueryAttribute attribute) {
        final Attribute _attribute = new CMEntryTypeVisitor() {

            private Attribute _attribute;

            public Attribute findAttribute(final EntryType type) {
                type.accept(this);
                return _attribute;
            }

            @Override
            public void visit(final Classe type) {
                throw new UnsupportedOperationException();
//				final String key = attribute.getName();
//				_attribute = querySpecs.getFromClause().getType().getAttributeOrNull(key);
//				if (_attribute == null) {
//					/*
//					 * attribute not found, probably it's a superclass so we
//					 * search within all subclasses (leaves) hoping to find it:
//					 * the first one is selected, keeping fingers crossed...
//					 * TODO the query generation must be implemented is a
//					 * different way or the QueryAliasAttribute must keep an
//					 * information of it's owner (entry type)
//					 */
//					for (final Classe leaf : type.getLeaves()) {
//						_attribute = leaf.getAttributeOrNull(key);
//						if (_attribute != null) {
//							break;
//						}
//					}
//				}
            }

            @Override
            public void visit(final Domain type) {
                final String key = attribute.getName();
                _attribute = querySpecs.getFromClause().getType().getAttributeOrNull(key);
            }

            @Override
            public void visit(final CMFunctionCall type) {
                final String key = attribute.getName();
                _attribute = querySpecs.getFromClause().getType().getAttributeOrNull(key);
            }

        }.findAttribute(querySpecs.getFromClause().getType());
        return (_attribute == null) ? UndefinedAttributeType.undefined() : _attribute.getType();
    }

    public static interface ActiveStatusChecker {

        boolean needsActiveStatus();

    }
}

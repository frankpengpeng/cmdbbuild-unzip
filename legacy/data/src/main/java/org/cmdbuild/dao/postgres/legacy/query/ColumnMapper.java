package org.cmdbuild.dao.postgres.legacy.query;

import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.nameForUserAttribute;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.cmdbuild.dao.postgres.SqlTypeName;
import org.cmdbuild.dao.entrytype.CMEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.CMFunctionCall;
import org.cmdbuild.dao.entrytype.attributetype.UndefinedAttributeType;
import org.cmdbuild.dao.query.QuerySpecs;
import org.cmdbuild.dao.query.clause.AnyAttribute;
import org.cmdbuild.dao.query.clause.QueryAliasAttribute;
import org.cmdbuild.dao.query.clause.QueryAttribute;
import org.cmdbuild.dao.query.clause.QueryAttributeVisitor;
import org.cmdbuild.dao.query.clause.QueryDomain;
import org.cmdbuild.dao.query.clause.alias.Alias;

import com.google.common.collect.Lists;
import org.cmdbuild.dao.DaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.dao.query.clause.alias.EntryTypeAlias.canonicalAlias;
import static org.cmdbuild.dao.query.clause.alias.NameAlias.nameAlias;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.attributeTypeToSqlType;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.attributeTypeToSqlTypeName;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.getSystemToSqlCastOrNull;

/**
 * Holds the information about which attribute to query for every alias and
 * entry type of that alias. Also it is used to keep a mapping between the alias
 * attributes and the position in the select clause.
 */
public class ColumnMapper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AliasStore cardSourceAliases = new AliasStore();
    private final AliasStore functionCallAliases = new AliasStore();
    private final AliasStore domainAliases = new AliasStore();
    private final List<String> externalReferenceAliases = Lists.newArrayList();

    private final SelectAttributesHolder selectAttributesHolder;

    private Integer currentIndex;

    public ColumnMapper(QuerySpecs query, SelectAttributesHolder holder) {
        this.selectAttributesHolder = holder;
        this.currentIndex = 0;
        fillAliases(query);
    }

    private void fillAliases(QuerySpecs querySpecs) {
        logger.trace("filling aliases");
        querySpecs.getFromClause().getType().accept(new CMEntryTypeVisitor() {

            @Override
            public void visit(Classe type) {
                throw new UnsupportedOperationException();
//				List<Classe> classes = Lists.newArrayList(type.getDescendants());
//				classes.add(type);
//
//				addClasses(querySpecs.getFromClause().getAlias(), classes);
//				for (JoinClause joinClause : querySpecs.getJoins()) {
//					addDomainAlias(joinClause.getDomainAlias(), joinClause.getQueryDomains());
//					addClasses(joinClause.getTargetAlias(), from(joinClause.getTargets()).transform(Map.Entry::getKey));
//				}
//				for (DirectJoinClause directJoinClause : querySpecs.getDirectJoins()) {
//					List<Classe> classesToJoin = Lists.newArrayList();
//					if (directJoinClause.getTargetClass() != null) {
//						classesToJoin.add(directJoinClause.getTargetClass());
//					}
//					addClasses(directJoinClause.getTargetClassAlias(), classesToJoin);
//					externalReferenceAliases.add(directJoinClause.getTargetClassAlias().toString());
//				}
            }

            private void addClasses(Alias alias, Iterable<? extends Classe> classes) {
                add(cardSourceAliases, alias, classes);
            }

            private void addDomainAlias(Alias alias, Iterable<QueryDomain> queryDomains) {
                add(domainAliases, alias, newHashSet(transform(queryDomains, QueryDomain::getDomain)));
            }

            @Override
            public void visit(Domain type) {
                throw new IllegalArgumentException("domain is an illegal 'from' type");
            }

            @Override
            public void visit(CMFunctionCall type) {
                add(functionCallAliases, querySpecs.getFromClause().getAlias(), newArrayList(type));
            }

            private void add(AliasStore store, Alias alias, Iterable<? extends EntryType> entryTypes) {
                logger.trace("adding '{}' for alias '{}'", namesOfEntryTypes(entryTypes), alias);
                store.addAlias(alias, entryTypes);
            }

        });
    }

    public List<String> getExternalReferenceAliases() {
        return externalReferenceAliases;
    }

    public Iterable<Alias> getClassAliases() {
        return cardSourceAliases.getAliases();
    }

    public Iterable<Alias> getDomainAliases() {
        return domainAliases.getAliases();
    }

    public Iterable<Alias> getFunctionCallAliases() {
        return functionCallAliases.getAliases();
    }

    public Iterable<EntryTypeAttribute> getAttributes(Alias alias, EntryType type) {
        return aliasAttributesFor(alias).getAttributes(type);
    }

    public void addAllAttributes(Iterable<? extends QueryAttribute> attributes) {
        for (QueryAttribute a : attributes) {
            addAttribute(a);
        }
    }

    private void addAttribute(QueryAttribute queryAttribute) {
        logger.trace("adding attribute '{}'", queryAttribute);

        Alias attributeEntryTypeAlias = queryAttribute.getAlias();
        AliasAttributes aliasAttributes = aliasAttributesFor(attributeEntryTypeAlias);
        queryAttribute.accept(new QueryAttributeVisitor() {

            @Override
            public void accept(AnyAttribute value) {
                logger.trace("any attribute required");
                Iterable<EntryType> entryTypes = entryTypesOf(aliasAttributes);
                EntryType rootEntryType = rootOf(entryTypes);
                for (EntryType entryType : entryTypes) {
                    logger.trace("adding attributes for type '{}'", entryType.getName());
                    Alias entryTypeAlias = new CMEntryTypeVisitor() {

                        private Alias alias;

                        @Override
                        public void visit(Classe type) {
                            alias = attributeEntryTypeAlias;
                        }

                        @Override
                        public void visit(Domain type) {
                            alias = attributeEntryTypeAlias;
                        }

                        @Override
                        public void visit(CMFunctionCall type) {
                            alias = canonicalAlias(type);
                        }

                        public Alias typeAlias() {
                            entryType.accept(this);
                            return alias;
                        }

                    }.typeAlias();

                    for (Attribute attribute : entryType.getAllAttributes()) {
                        logger.trace("adding attribute '{}'", attribute.getName());

                        if (attribute.isInherited()) {
                            if (!entryType.equals(rootEntryType)) {
                                continue;
                            }
                        }

                        String attributeName = attribute.getName();

                        new CMEntryTypeVisitor() {

                            private Alias alias;

                            @Override
                            public void visit(Classe type) {
                                alias = nameAlias(nameForUserAttribute(entryTypeAlias, attributeName));
                                selectAttributesHolder.add(entryTypeAlias, attributeName, getSystemToSqlCastOrNull(attribute.getType()), alias);
                            }

                            @Override
                            public void visit(Domain type) {
                                /**
                                 * The alias is updated. Bug fix for domains
                                 * that have an attribute with the same name
                                 */
                                alias = nameAlias(nameForUserAttribute(entryTypeAlias, attributeName + "##" + currentIndex));
                                selectAttributesHolder.add(getSystemToSqlCastOrNull(attribute.getType()), alias);
                            }

                            @Override
                            public void visit(CMFunctionCall type) {
                                alias = nameAlias(nameForUserAttribute(entryTypeAlias, attributeName));
                                selectAttributesHolder.add(entryTypeAlias, attributeName, getSystemToSqlCastOrNull(attribute.getType()), alias);
                            }

                            public void execute(EntryType entryType) {
                                entryType.accept(this);
                                aliasAttributes.addAttribute(attributeName, alias, ++currentIndex, entryType);
                            }

                        }.execute(entryType);

                    }
                }
            }

            @Override
            public void visit(QueryAliasAttribute value) {
                String attributeName = queryAttribute.getName();
                int index = ++currentIndex;
                for (EntryType entryType : aliasAttributes.getEntryTypes()) {
                    aliasAttributes.addAttribute(attributeName, null, index, entryType);
                }
                EntryType type = rootOf(aliasAttributes.getEntryTypes());
                Alias attributeAlias = nameAlias(nameForUserAttribute(attributeEntryTypeAlias, attributeName));
                selectAttributesHolder.add(attributeEntryTypeAlias, attributeName, getSystemToSqlCastOrNull(type.getAttributeOrNull(attributeName).getType()), attributeAlias);
            }

        });
    }

    private Iterable<EntryType> entryTypesOf(AliasAttributes aliasAttributes) {
        Iterable<EntryType> entryTypes = aliasAttributes.getEntryTypes();
        return new CMEntryTypeVisitor() {

            private Iterable<EntryType> resultEntryTypes;

            public Iterable<EntryType> entryTypes() {
                assert entryTypes.iterator().hasNext() : "at least one element expected";
                entryTypes.iterator().next().accept(this);
                return resultEntryTypes;
            }

            @Override
            public void visit(Classe type) {
                resultEntryTypes = Arrays.asList(rootOf(entryTypes));
            }

            @Override
            public void visit(Domain type) {
                resultEntryTypes = entryTypes;
            }

            @Override
            public void visit(CMFunctionCall type) {
                // should be one only
                resultEntryTypes = entryTypes;
            }

        }.entryTypes();
    }

    private EntryType rootOf(Iterable<EntryType> entryTypes) {
        EntryType root = null;
        for (EntryType entryType : entryTypes) {
            root = new CMEntryTypeVisitor() {

                private EntryType anchestor;

                @Override
                public void visit(Classe type) {
                    if (type.isAncestorOf(Classe.class.cast(anchestor))) {
                        anchestor = type;
                    }
                }

                @Override
                public void visit(Domain type) {
                    /*
					 * domain hierarchies are not supported, we are just
					 * returning the first one
                     */
                }

                @Override
                public void visit(CMFunctionCall type) {
                    throw new IllegalArgumentException("function hierarchies are not supported");
                }

                public EntryType anchestorOf(EntryType entryType1, EntryType entryType2) {
                    if (entryType1 == null) {
                        anchestor = entryType2;
                    } else if (entryType2 == null) {
                        anchestor = entryType1;
                    } else {
                        anchestor = entryType1;
                        entryType2.accept(this);
                    }
                    return anchestor;
                }

            }.anchestorOf(root, entryType);
        }
        return root;
    }

    private AliasAttributes aliasAttributesFor(Alias alias) {
        logger.trace("getting alias attributes for alias '{}'", alias);
        try {
            return firstNotNull(
                    cardSourceAliases.getAliasAttributes(alias),
                    domainAliases.getAliasAttributes(alias),
                    functionCallAliases.getAliasAttributes(alias));
        } catch (Exception ex) {
            throw new DaoException(ex, "alias attributes not found for alias = %s", alias);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("Classes", cardSourceAliases) //
                .append("Domains", domainAliases) //
                .append("Functions", functionCallAliases) //
                .toString();
    }

    private static Iterable<String> namesOfEntryTypes(Iterable<? extends EntryType> aliasClasses) {
        return transform(aliasClasses, (EntryType input) -> input.getName());
    }

    public static class EntryTypeAttribute {

        public final String name;
        public final Alias alias;
        public final Integer index;
        public final SqlTypeName sqlType;
        public final String sqlTypeString;

        private transient final String toString;

        /*
		 * Usable within this class only!
         */
        private EntryTypeAttribute( //
                String name, //
                Alias alias, //
                Integer index, //
                SqlTypeName sqlType, //
                String sqlTypeString //
        ) {
            this.name = name;
            this.alias = alias;
            this.index = index;
            this.sqlType = sqlType;
            this.sqlTypeString = sqlTypeString;

            this.toString = ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }

        @Override
        public String toString() {
            return toString;
        }
    }

    /**
     * Stores all {@link EntryTypeAttribute} by {@link EntryType}.
     */
    private static class AliasAttributes {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Map<EntryType, List<EntryTypeAttribute>> map = newHashMap();

        public AliasAttributes(Iterable<? extends EntryType> entryTypes) {
            for (EntryType entryType : entryTypes) {
                map.put(entryType, Lists.<EntryTypeAttribute>newArrayList());
            }
        }

        /*
		 * Adds the attribute to the specified type
         */
        public void addAttribute(String attributeName, Alias attributeAlias, Integer index, EntryType type) {
            new CMEntryTypeVisitor() {

                private final String sqlTypeString = sqlTypeString(type, attributeName);
                private final SqlTypeName sqlType = sqlType(type, attributeName);

                @Override
                public void visit(Classe type) {
                    throw new UnsupportedOperationException();
//					List<EntryType> entryTypes = Lists.newArrayList();
//					entryTypes.add(type);
//					for (Classe descendant : type.getDescendants()) {
//						entryTypes.add(descendant);
//					}
//					add(entryTypes);
                }

                @Override
                public void visit(Domain type) {
                    addWithMissingAttributesAlso(Arrays.asList(type));
                }

                @Override
                public void visit(CMFunctionCall type) {
                    add(Arrays.asList(type));
                }

                private void add(Iterable<? extends EntryType> types) {
                    for (EntryType type : types) {
                        EntryTypeAttribute eta = new EntryTypeAttribute(attributeName, attributeAlias, index, sqlType, sqlTypeString);
                        if (map.containsKey(type)) {
                            map.get(type).add(eta);
                        }
                    }
                }

                private void addWithMissingAttributesAlso(Iterable<? extends EntryType> types) {
                    for (EntryType type : types) {
                        for (EntryType currentType : map.keySet()) {
                            String currentName = (attributeAlias == null || currentType.equals(type)) ? attributeName : null;
                            EntryTypeAttribute eta = new EntryTypeAttribute(currentName, attributeAlias, index, sqlType, sqlTypeString);
                            map.get(currentType).add(eta);
                        }
                    }
                }

                public void addFor(EntryType type) {
                    type.accept(this);
                }

                private SqlTypeName sqlType(EntryType type, String attributeName) {
                    CardAttributeType<?> attributeType = safeAttributeTypeFor(type, attributeName);
                    return attributeTypeToSqlTypeName(attributeType);
                }

                private String sqlTypeString(EntryType type, String attributeName) {
                    CardAttributeType<?> attributeType = safeAttributeTypeFor(type, attributeName);
                    return attributeTypeToSqlType(attributeType).toSqlTypeString();
                }

                private CardAttributeType<?> safeAttributeTypeFor(EntryType type, String attributeName) {
                    CardAttributeType<?> attributeType;
                    if (type != null) {
                        Attribute attribute = type.getAttributeOrNull(attributeName);
                        attributeType = (attribute != null) ? attribute.getType() : UndefinedAttributeType.undefined();
                    } else {
                        attributeType = UndefinedAttributeType.undefined();
                    }
                    return attributeType;
                }

            }.addFor(type);
        }

        public Iterable<EntryTypeAttribute> getAttributes(EntryType type) {
            Iterable<EntryTypeAttribute> entryTypeAttributes = map.get(type);
            logger.trace("getting all attributes for type '{}': {}", type.getName(), entryTypeAttributes);
            return entryTypeAttributes;
        }

        public Iterable<EntryType> getEntryTypes() {
            return map.keySet();
        }

        @Override
        public String toString() {
            return map.toString();
        }

    }

    /**
     * Stores {@link AliasAttributes} by {@link Alias}.
     */
    private static class AliasStore {

        private final Map<Alias, AliasAttributes> map;

        public AliasStore() {
            map = newHashMap();
        }

        public void addAlias(Alias alias, Iterable<? extends EntryType> entryTypes) {
            map.put(alias, new AliasAttributes(entryTypes));
        }

        public AliasAttributes getAliasAttributes(Alias alias) {
            return map.get(alias);
        }

        public Set<Alias> getAliases() {
            return map.keySet();
        }

        @Override
        public String toString() {
            return map.toString();
        }

    }

}

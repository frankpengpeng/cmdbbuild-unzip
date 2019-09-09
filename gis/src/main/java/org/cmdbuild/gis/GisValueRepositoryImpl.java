package org.cmdbuild.gis;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.isEmpty;
import com.google.common.collect.Lists;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Multimap;
import static com.google.common.collect.Multimaps.index;
import static java.lang.String.format;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.postgres.q3.AliasBuilder;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;

import static org.cmdbuild.gis.utils.GisUtils.buildGisTableNameForQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.gis.utils.GisUtils.postgisSqlToCmGeometryOrNull;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;
import static org.cmdbuild.gis.utils.GisUtils.buildGisTableName;
import org.cmdbuild.navtree.NavTreeNode;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.entryTypeToSqlExpr;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.postgis.PGbox2d;

@Component("geoFeatureStore")
public class GisValueRepositoryImpl implements GisValueRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final JdbcTemplate jdbcTemplate;
    private final GisSchemaService databaseService;
    private final GisAttributeRepository attributeRepository;

    public GisValueRepositoryImpl(DaoService dao, GisSchemaService databaseService, GisAttributeRepository attributeRepository) {
        this.dao = checkNotNull(dao);
        this.jdbcTemplate = dao.getJdbcTemplate();
        this.databaseService = checkNotNull(databaseService);
        this.attributeRepository = checkNotNull(attributeRepository);
    }

    @Override
    public void checkGisSchemaAndCreateIfMissing() {
        databaseService.checkGisSchemaAndCreateIfMissing();
    }

    @Override
    public boolean isGisSchemaOk() {
        return databaseService.isGisSchemaOk();
    }

    @Override
    public void createGisTable(GisAttribute gisAttribute) {
        jdbcTemplate.queryForObject("SELECT _cm3_gis_table_create(?::regclass, ?, ?)", Object.class, quoteSqlIdentifier(gisAttribute.getOwnerClassName()), gisAttribute.getLayerName(), getSqlGeoType(gisAttribute));
    }

    @Override
    public void deleteGisTable(String targetClassName, String geoAttributeName) {
        String geoAttributeTableName = buildGisTableNameForQuery(targetClassName, geoAttributeName);
        logger.debug("Delete GIS table = {}", geoAttributeTableName);
        jdbcTemplate.queryForObject("SELECT _cm3_class_delete(?::regclass)", Object.class, geoAttributeTableName);
    }

    @Override
    public void setGisValue(GisAttribute gisAttribute, String value, long ownerCardId) {
        jdbcTemplate.queryForObject("SELECT _cm3_gis_value_set(?::regclass,?,?,?)", Object.class, quoteSqlIdentifier(gisAttribute.getOwnerClassName()), gisAttribute.getLayerName(), ownerCardId, value);
    }

    @Override
    public void deleteGisValue(GisAttribute gisAttribute, long ownerCardId) {
        jdbcTemplate.queryForObject("SELECT _cm3_gis_value_delete(?::regclass,?,?)", Object.class, quoteSqlIdentifier(gisAttribute.getOwnerClassName()), gisAttribute.getLayerName(), ownerCardId);
    }

    @Nullable
    @Override
    public GisValue getGisValueOrNull(GisAttribute gisAttribute, long ownerCardId) {
        String strValue = jdbcTemplate.queryForObject("SELECT _cm3_gis_value_get(?::regclass,?,?)", String.class, quoteSqlIdentifier(gisAttribute.getOwnerClassName()), gisAttribute.getLayerName(), ownerCardId);
        return postgisSqlToCmGeometryOrNull(gisAttribute, ownerCardId, strValue);
    }

    @Override
    public List<GisValue> getGisValues(Iterable<Long> attributeIdList, String bboxArea) {
        return jdbcTemplate.query(buildGeoValueBaseQuery(attributeIdList, null, bboxArea), (rs, i) -> gisValueFromResultSet(rs));
    }

    @Override
    public Pair<List<GisValue>, List<GisNavTreeNode>> getGeoValuesAndNavTree(Iterable<Long> layers, String bbox, NavTreeNode navTreeDomains) {
        List<GisValue> gisValues = list();
        List<GisNavTreeNode> navTreeNodes = list();
        DomainTreeNodeAux domainTreeNodes = new DomainTreeNodeAux(navTreeDomains);
        for (long attrId : layers) {
            logger.debug("get geo values and nav tree for attr = {}", attrId);
            Pair<List<GisValue>, List<GisNavTreeNode>> geoValuesAndNavTree = new GeoValuesAndNavTreeAux(attrId, domainTreeNodes).getGeoValuesAndNavTree(bbox);
            gisValues.addAll(geoValuesAndNavTree.getLeft());
            navTreeNodes.addAll(geoValuesAndNavTree.getRight());
        }

        return Pair.of(gisValues, navTreeNodes);
    }

    @Override
    @Nullable
    public Area getAreaForValues(Collection<Long> attrs) {
        checkArgument(!isNullOrEmpty(attrs), "attrs is null");
        String area = jdbcTemplate.queryForObject(format("SELECT gis.st_extent(geometry) FROM (%s) g",
                attrs.stream().map(attributeRepository::getLayer).map(a -> format("SELECT \"Geometry\"::gis.geometry geometry FROM %s", getTableName(a))).collect(joining(" UNION "))), String.class);
        if (isBlank(area)) {
            return null;
        } else {
            try {
                PGbox2d postgisBox = new PGbox2d(area);
                return new AreaImpl(postgisBox.getLLB().x, postgisBox.getLLB().y, postgisBox.getURT().x, postgisBox.getURT().y);
            } catch (SQLException ex) {
                throw new GisException(ex);
            }
        }
    }

    @Override
    public List<GisValue> readGeoFeatures(GisAttribute gisAttribute, @Nullable String bbox) {

        String tableName = getTableName(gisAttribute);
        Classe ownerClass = dao.getClasse(gisAttribute.getOwnerClassName());

        String sql = format("SELECT _gis_table.\"Master\" AS _owner, gis.st_astext(_gis_table.\"Geometry\"::gis.geometry) AS _geometry FROM %s AS _gis_table"
                + " JOIN \"%s\" AS _master_table " + "ON _gis_table.\"Master\" = _master_table.\"Id\" WHERE _master_table.\"Status\" = 'A'", tableName, ownerClass.getName());

        if (isNotBlank(bbox)) {
            sql += format(" AND (_gis_table.\"Geometry\" && gis.st_makeenvelope(%s::varchar,900913))", getEnvelopeParamsFromBboxArea(bbox));
        }

        return jdbcTemplate.query(sql, (rs, i) -> {
            String geometryAsString = rs.getString("_geometry");
            Long ownerCardId = rs.getLong("_owner");
            return checkNotNull(postgisSqlToCmGeometryOrNull(gisAttribute.getLayerName(), ownerClass.getName(), ownerCardId, geometryAsString));
        });
    }

    private static String getSqlGeoType(GisAttribute gisAttribute) {
        return gisAttribute.getType();
    }

    private static GisValue gisValueFromResultSet(ResultSet rs) throws SQLException {
        String geometryAsString = rs.getString("_geometry"),
                ownerClass = rs.getString("_ownerclass"),
                attrName = rs.getString("_attrname");
        Long ownerCard = rs.getLong("_ownercard");
        return postgisSqlToCmGeometryOrNull(attrName, ownerClass, ownerCard, checkNotBlank(geometryAsString));
    }

    private String buildGeoValueBaseQuery(Iterable<Long> layers, List<String> select, String bbox) {
        checkArgument(!isEmpty(layers), "geo attribute (layer) list param cannot be null");
        checkNotBlank(bbox, "area param cannot be null");
        String otherSelect;
        if (isNullOrEmpty(select)) {
            otherSelect = "";
        } else {
            otherSelect = "," + Joiner.on(",").join(select);
        }
        return format("SELECT _cm3_utils_regclass_to_name(_geometry.ownerclass) _ownerclass,_geometry.attrname _attrname,_geometry.ownercard _ownercard,_geometry.geometry _geometry%s FROM _cm3_gis_find_values('{%s}'::bigint[],'%s') _geometry", otherSelect, Joiner.on(",").join(layers), getEnvelopeParamsFromBboxArea(bbox));
    }

    private class DomainTreeNodeAux {

        private final List<NavTreeNode> domainTreeNodes;
        private final Multimap<String, NavTreeNode> domainTreeNodesByTargetClassName;
        private final Map<String, NavTreeNode> domainTreeNodesById;

        public DomainTreeNodeAux(NavTreeNode navTreeDomains) {
            this.domainTreeNodes = navTreeDomains.getThisNodeAndAllDescendants();
            domainTreeNodesByTargetClassName = index(domainTreeNodes, NavTreeNode::getTargetClassName);
            domainTreeNodesById = uniqueIndex(domainTreeNodes, NavTreeNode::getId);
        }

        public Collection<NavTreeNode> getNodesByTargetClass(String classId) {
            Classe classe = dao.getClasse(classId);
            return getNodesByTargetClass(classe);
        }

        private Collection<NavTreeNode> getNodesByTargetClass(Classe classe) {
            Collection<NavTreeNode> nodes = domainTreeNodesByTargetClassName.get(classe.getName());
            if (nodes.isEmpty() && classe.hasParent()) {
                nodes = getNodesByTargetClass(classe.getParent());
            }
            return nodes;
        }

        public NavTreeNode getNodeById(String id) {
            return checkNotNull(domainTreeNodesById.get(id), "node not found for id = %s", id);
        }
    }

    private class GeoValuesAndNavTreeAux {

        private final long attrId;
        private final DomainTreeNodeAux domainTreeNodes;

        private final AliasBuilder aliasBuilder = new AliasBuilder();
        private final List<List<NavTreeNodeQueryAliases>> aliasesForCardIdAndDescription = list();
        private final List<String> select = list();
        private final List<String> queryJoins = list();

        public GeoValuesAndNavTreeAux(long layer, DomainTreeNodeAux domainTreeNodes) {
            this.attrId = layer;
            this.domainTreeNodes = checkNotNull(domainTreeNodes);
        }

        public Pair<List<GisValue>, List<GisNavTreeNode>> getGeoValuesAndNavTree(String bbox) {
            GisAttribute attr = attributeRepository.getLayer(attrId);
            List<List<NavTreeNode>> navTreeBranches = buildReverseNavTreeBranchesForAttr(attr);

            NavTreeNodeQueryAliases baseNodeAlias;

            {
                Classe targetClass = dao.getClasse(attr.getOwnerClassName());
                String targetClassAlias = aliasBuilder.buildAlias(targetClass.getName());

                queryJoins.add(format(" JOIN %s %s ON %s.\"IdClass\" = _geometry.ownerclass AND %s.\"Id\" = _geometry.ownercard AND %s.\"Status\" = 'A'",
                        entryTypeToSqlExpr(targetClass), targetClassAlias, targetClassAlias, targetClassAlias, targetClassAlias));

                baseNodeAlias = selectCardAttrs(targetClass, targetClassAlias, "-");
            }

            for (List<NavTreeNode> navTreeBranch : navTreeBranches) {
                logger.debug("prepare query for nav tree branch = {}", navTreeBranch);

                String sourceClassIdExpr = "_geometry.ownerclass";
                String sourceCardIdExpr = "_geometry.ownercard";

                List<NavTreeNodeQueryAliases> list = list(new NavTreeNodeQueryAliases(baseNodeAlias.cardIdAlias, baseNodeAlias.cardDescAlias, baseNodeAlias.targetClass, navTreeBranch.get(0).getId()));
//                navTreeBranch = navTreeBranch.subList(1, navTreeBranch.size()-1);

                for (NavTreeNode node : navTreeBranch) {
                    if (isNotBlank(node.getDomainName())) {
                        logger.debug("add join for node = {}", node);
                        String domainId = node.getDomainName();
                        Domain domain = dao.getDomain(domainId);
                        Classe targetClass;
                        String id1, id2;
                        if (node.getDirect()) {
                            id1 = "2";
                            id2 = "1";
                            targetClass = domain.getSourceClass();
                        } else {
                            id1 = "1";
                            id2 = "2";
                            targetClass = domain.getTargetClass();
                        }
                        String domainAlias = aliasBuilder.buildAlias(domainId);

                        queryJoins.add(format(" LEFT JOIN %s %s ON %s.\"IdObj%s\" = %s AND %s.\"IdClass%s\" = %s AND %s.\"Status\" = 'A' ",
                                entryTypeToSqlExpr(domain), domainAlias, domainAlias, id1, sourceCardIdExpr, domainAlias, id1, sourceClassIdExpr, domainAlias));

                        String targetClassAlias = aliasBuilder.buildAlias(targetClass.getName());

                        queryJoins.add(format(" LEFT JOIN %s %s ON %s.\"IdClass\" = %s.\"IdClass%s\" AND %s.\"Id\" = %s.\"IdObj%s\" AND %s.\"Status\" = 'A'",
                                entryTypeToSqlExpr(targetClass), targetClassAlias, targetClassAlias, domainAlias, id2, targetClassAlias, domainAlias, id2, targetClassAlias));

                        sourceClassIdExpr = format("%s.\"IdClass\"", targetClassAlias);
                        sourceCardIdExpr = format("%s.\"Id\"", targetClassAlias);

//                        list.add(selectCardAttrs(targetClass, targetClassAlias, node.getId()));
                        list.add(selectCardAttrs(targetClass, targetClassAlias, node.getParentId()));
                    } else {
                        logger.debug("break on node = {} (no domain)", node);
                        break;
                    }
                }

                aliasesForCardIdAndDescription.add(list);
            }

            String query = buildGeoValueBaseQuery(singleton(attrId), select, bbox) + " " + Joiner.on(" ").join(queryJoins);

            List<GisNavTreeNode> navTreeNodes = list();
            Set<String> addedNodeKeys = set();
            List<GisValue> gisValues = list();

            jdbcTemplate.query(query, (ResultSet rs) -> {
                GisValue gisValue = gisValueFromResultSet(rs);
                logger.trace("process gis value = {}", gisValue);
                gisValues.add(gisValue);
                for (List<NavTreeNodeQueryAliases> list : aliasesForCardIdAndDescription) {
                    GisNavTreeNode parent = null;
                    logger.trace("process results for nav tree branch = {}", list);
                    for (NavTreeNodeQueryAliases navTreeNodeQueryAliases : Lists.reverse(list)) {
                        long cardId = rs.getLong(navTreeNodeQueryAliases.cardIdAlias);
                        if (isNotNullAndGtZero(cardId)) {
                            GisNavTreeNode thisNode = GisNavTreeNodeImpl.builder()
                                    .withCardId(cardId)
                                    .withClassId(navTreeNodeQueryAliases.targetClass.getName())
                                    .withDescription(rs.getString(navTreeNodeQueryAliases.cardDescAlias))
                                    .withParentClassId(parent == null ? null : parent.getClassId())
                                    .withParentCardId(parent == null ? null : parent.getCardId())
                                    .withNavTreeNodeId(navTreeNodeQueryAliases.navTreeNodeId)
                                    .build();
                            if (addedNodeKeys.add(key(thisNode.getClassId(), thisNode.getCardId(), navTreeNodeQueryAliases.navTreeNodeId))) {
                                logger.trace("add nav tree node = {}", thisNode);
                                navTreeNodes.add(thisNode);
                            } else {
                                logger.trace("skip nav tree node = {} (already added)", thisNode);
                            }
                            parent = thisNode;
                        } else {
                            break;
                        }
                    }
                }
            });
            return Pair.of(gisValues, navTreeNodes);
        }

        private NavTreeNodeQueryAliases selectCardAttrs(Classe targetClass, String targetClassAlias, String navTreeNodeId) {
            String cardIdAlias = aliasBuilder.buildAlias(targetClass.getName() + "cardid");
            String cardDescAlias = aliasBuilder.buildAlias(targetClass.getName() + "carddesc");
            select.add(format("%s.\"Id\" %s", targetClassAlias, cardIdAlias));
            select.add(format("%s.\"Description\" %s", targetClassAlias, cardDescAlias));
            return new NavTreeNodeQueryAliases(cardIdAlias, cardDescAlias, targetClass, navTreeNodeId);
        }

        private List<List<NavTreeNode>> buildReverseNavTreeBranchesForAttr(GisAttribute attr) {
            Classe classe = dao.getClasse(attr.getOwnerClassName());
            return buildReverseNavTreeBranchesForClassId(classe.getName());
        }

        private List<List<NavTreeNode>> buildReverseNavTreeBranchesForClassId(String classId) {
            logger.debug("buildReverseNavTreeBranchForClassId = {}", classId);
            Collection<NavTreeNode> nodes = domainTreeNodes.getNodesByTargetClass(classId);
            if (nodes.isEmpty()) {
                logger.debug("nav tree domain not found for class = {}", classId);
                return emptyList();
            } else {
                return nodes.stream().map((node) -> {
                    logger.debug("found node = {}", node);
                    List<NavTreeNode> list = list(node);
                    while (node.hasParent()) {
                        node = domainTreeNodes.getNodeById(node.getParentId());
                        logger.debug("found node = {}", node);
                        list.add(node);
                    }
                    return list;
                }).collect(toList());
            }
        }
    }

    private String getTableName(GisAttribute gisAttribute) {
        Classe classe = dao.getClasse(gisAttribute.getOwnerClassName());
        return buildGisTableNameForQuery(classe.getName(), gisAttribute.getLayerName());
    }

    private String getEnvelopeParamsFromBboxArea(String bbox) {
        String[] coordinates = bbox.split(",");
        Preconditions.checkArgument(coordinates.length == 4);
        return Joiner.on(",").join(coordinates);
    }

    private static class NavTreeNodeQueryAliases {

        private final String cardIdAlias, cardDescAlias, navTreeNodeId;
        private final Classe targetClass;

        public NavTreeNodeQueryAliases(String cardIdAlias, String cardDescAlias, Classe targetClass, String navTreeNodeId) {
            this.cardIdAlias = checkNotBlank(cardIdAlias);
            this.cardDescAlias = checkNotBlank(cardDescAlias);
            this.targetClass = checkNotNull(targetClass);
            this.navTreeNodeId = checkNotBlank(navTreeNodeId);
        }

        @Override
        public String toString() {
            return "NavTreeNodeQueryAliases{" + "cardIdAlias=" + cardIdAlias + ", cardDescAlias=" + cardDescAlias + ", navTreeNodeId=" + navTreeNodeId + ", targetClass=" + targetClass + '}';
        }

    }
}

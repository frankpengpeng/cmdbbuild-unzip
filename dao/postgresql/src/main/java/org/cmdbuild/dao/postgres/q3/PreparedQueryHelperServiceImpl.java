/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDDOMAIN;
import static org.cmdbuild.dao.core.q3.DaoService.COUNT;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.driver.repository.ClasseReadonlyRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.orm.CardMapper;
import org.cmdbuild.dao.orm.CardMapperService;
import org.cmdbuild.dao.postgres.QueryService;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.parseEntryTypeQueryResponseData;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.sqlTableToDomainName;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PreparedQueryHelperServiceImpl implements PreparedQueryHelperService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ClasseReadonlyRepository classeRepository;
    private final DomainRepository domainRepository;
    private final QueryService queryService;
    private final CardMapperService mapper;

    public PreparedQueryHelperServiceImpl(ClasseReadonlyRepository classeRepository, DomainRepository domainRepository, QueryService queryService, CardMapperService mapper) {
        this.classeRepository = checkNotNull(classeRepository);
        this.domainRepository = checkNotNull(domainRepository);
        this.queryService = checkNotNull(queryService);
        this.mapper = checkNotNull(mapper);
    }

    @Override
    public PreparedQuery prepareQuery(String query, List<SelectElement> preparedQuerySelect, List<WhereElement> where, EntryType from, CardMapper cardMapper) {
        return new PreparedQueryImpl(query, preparedQuerySelect, where, from, cardMapper);
    }

    private class PreparedQueryImpl implements InnerPreparedQuery {

        private final EntryType from;
        private final String query;
        private final List<SelectElement> select;
        private final List<WhereElement> where;
        private final CardMapper cardMapper;

        private PreparedQueryImpl(String query, List<SelectElement> select, List<WhereElement> where, @Nullable EntryType from, @Nullable CardMapper cardMapper) {
            this.query = checkNotBlank(query);
            this.from = from;
            this.cardMapper = cardMapper;
            this.select = ImmutableList.copyOf(select);
            this.where = ImmutableList.copyOf(where);
        }

        @Override
        public List<ResultRow> run() {
            logger.trace("execute query:\n\tquery = {} ", query);
            try {
                return queryService.query(query, (ResultSet rs) -> {

                    try {
                        return new ResultRowImpl(parseEntryTypeQueryResponseData(from, select, SelectElement::getName, rethrowFunction(s -> rs.getObject(s.getAlias()))));
                    } catch (SQLException ex) {
                        throw new DaoException(ex);
                    }
                });
            } catch (Exception ex) {
                throw new DaoException(ex, "error executing query = '%s'", query);
            }
        }

        @Override
        public int getCount() {
            return checkNotNull(convert(getOnlyElement(run()).asMap().get(COUNT), Integer.class), "count not found: not a count query?");
        }

        @Override
        public List<WhereElement> getFilters() {
            return where;
        }

        private class ResultRowImpl extends AbstractResultRow {

            private final Map<String, Object> map;

            public ResultRowImpl(Map<String, Object> map) {
                this.map = Collections.unmodifiableMap(checkNotNull(map));
            }

            @Override
            public Map<String, Object> asMap() {
                return map;
            }

            @Override
            public <T> T toModel(Class<T> model) {
                CardMapper thisMapper;
                if (cardMapper != null) {
                    thisMapper = cardMapper;
                } else {
                    thisMapper = mapper.getMapperForModel(model);
                }
                return (T) thisMapper.dataToObject(map::get).build();
            }

            @Override
            public <T> T toModel() {
                CardMapper thisMapper;
                if (cardMapper != null) {
                    thisMapper = cardMapper;
                } else {
                    checkArgument(from instanceof Classe, "cannot map row to model: unable to retrieve model for entry type = %s", from);
                    thisMapper = mapper.getMapperForClasse((Classe) from);
                }
                try {
                    return (T) thisMapper.dataToObject(map::get).build();
                } catch (Exception ex) {
                    throw new DaoException(ex, "error mapping record id = %s type = %s to model = %s", map.get(ATTR_ID), firstNotNullOrNull(map.get(ATTR_IDCLASS), map.get(ATTR_IDDOMAIN)), thisMapper.getTargetClass());
                }
            }

            @Override
            public Card toCard() {
                String classId = checkNotNull(convert(map.get(ATTR_IDCLASS), String.class), "class id param not found in query result");
                Classe classe = classeRepository.getClasse(classId);
                return CardImpl.buildCard(classe, map);
            }

            @Override
            public CMRelation toRelation() {
                String domainId = sqlTableToDomainName(checkNotNull(convert(map.get(ATTR_IDDOMAIN), String.class), "domain id param not found in query result"));
                Domain domain = domainRepository.getDomain(domainId);
                return RelationImpl.builder()
                        .withType(domain)
                        .withAttributes(map)
                        .build();
            }

        }
    }
}

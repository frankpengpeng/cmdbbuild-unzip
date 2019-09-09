/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.core.q3.CompositeWhereOperator;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.JOIN_ID_DEFAULT;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.REQUIRE_WITH_EXPR;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.SELECT_FROM_JOIN_ID;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmConvertUtils;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.lang.CmPreconditions;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class WhereArg {

    private final WhereArgType type;
    private final Map<String, Object> meta;
    private final List<Object> params;
    private final String expr;
    private final Object operator;
    private final List<WhereArg> inners;

    public WhereArg(String expr, Object operator, Map<String, Object> meta, List<Object> params) {
        this.meta = ImmutableMap.copyOf(meta);
        this.params = CmCollectionUtils.list(params).immutable();
        this.expr = CmPreconditions.checkNotBlank(expr);
        this.operator = operator;
        this.type = WhereArgType.WA_SIMPLE;
        this.inners = null;
    }

    public WhereArg(Object operator, List<WhereArg> inners, Map<String, Object> meta) {
        this.meta = (Map) CmMapUtils.map().accept((m) -> inners.stream().forEach((i) -> m.putAll(i.getMeta()))).with(meta); //TODO check this, do proper meta merge
        this.params = Collections.emptyList();
        this.expr = null;
        this.operator = Preconditions.checkNotNull(operator);
        this.type = WhereArgType.WA_COMPOSITE;
        this.inners = ImmutableList.copyOf(inners);
        Preconditions.checkArgument(operator instanceof CompositeWhereOperator);
    } //TODO check this, do proper meta merge

    public Map<String, Object> getMeta() {
        return meta;
    }

    public List<Object> getParams() {
        return params;
    }

    public String getExpr() {
        return CmPreconditions.checkNotBlank(expr);
    }

    public Object getOperator() {
        return operator;
    }

    public WhereArgType getType() {
        return type;
    }

    public List<WhereArg> getInners() {
        return Preconditions.checkNotNull(inners);
    }

    public boolean isSimple() {
        return Objects.equal(type, WhereArgType.WA_SIMPLE);
    }

    public boolean requireWithExpr() {
        return CmConvertUtils.toBooleanOrDefault(meta.get(REQUIRE_WITH_EXPR), false);
    }

    public String getSelectFromJoin() {
        return firstNotBlank(toStringOrNull(meta.get(SELECT_FROM_JOIN_ID)), JOIN_ID_DEFAULT);
    }

    public WhereArg withMeta(Map<String, Object> metaToAdd) {
        //TODO improve this
        if (isSimple()) {
            return new WhereArg(expr, operator, CmMapUtils.map(meta).with(metaToAdd), params);
        } else {
            return new WhereArg(operator, inners, CmMapUtils.map(meta).with(metaToAdd));
        }
    }

    public WhereArg withMeta(Object... metaToAdd) {
        return this.withMeta(map(metaToAdd));
    }

}

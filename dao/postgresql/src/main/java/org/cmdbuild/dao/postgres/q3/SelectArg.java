/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.JOIN_ID_DEFAULT;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.SELECT_DISTINCT;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.SELECT_FROM_JOIN_ID;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.lang.CmPreconditions;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class SelectArg {

    private final Map<String, Object> meta;
    private final List<Object> params;
    private final String name;
    private final Object expr;

    public SelectArg(String name, Object expr) {
        this(name, expr, Collections.emptyMap(), Collections.emptyList());
    }

    public SelectArg(String name, Object expr, Map<String, Object> meta, List<Object> params) {
        this.meta = ImmutableMap.copyOf(meta);
        this.params = ImmutableList.copyOf(params);
        this.name = CmPreconditions.checkNotBlank(name);
        this.expr = Preconditions.checkNotNull(expr);
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public List<Object> getParams() {
        return params;
    }

    public String getName() {
        return name;
    }

    public Object getExpr() {
        return expr;
    }

    public String getSelectForJoin() {
        return firstNotBlank(toStringOrNull(meta.get(SELECT_FROM_JOIN_ID)), JOIN_ID_DEFAULT);
    }

    @Override
    public String toString() {
        return "SelectArg{" + "name=" + name + ", expr=" + expr + '}';
    }

    public SelectArg withMeta(String key, Object value) {
        return new SelectArg(name, expr, map(meta).with(key, value), params);
    }

    public boolean isDistinct() {
        return toBooleanOrDefault(meta.get(SELECT_DISTINCT), false);
    }

}

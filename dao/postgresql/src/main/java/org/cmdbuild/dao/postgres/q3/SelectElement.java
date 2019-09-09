/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.dao.postgres.SqlTypeName;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.JOIN_ID_DEFAULT;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.SELECT_DISTINCT;
import static org.cmdbuild.dao.postgres.q3.QueryBuilderServiceImpl.SELECT_FROM_JOIN_ID;
import org.cmdbuild.utils.lang.Builder;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.lang.CmPreconditions;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class SelectElement {

    private final Map<String, Object> meta;
    private final List<Object> params;
    private final String expr;
    private final String alias;
    private final String name;
    private final SqlTypeName sqlTypeHint;

    private SelectElement(SelectElementBuilder builder) {
        this.expr = CmPreconditions.checkNotBlank(builder.expr);
        this.alias = CmPreconditions.checkNotBlank(builder.alias);
        this.name = CmPreconditions.checkNotBlank(builder.name);
        this.meta = ImmutableMap.copyOf(builder.meta);
        this.params = ImmutableList.copyOf(builder.params);
        this.sqlTypeHint = CmConvertUtils.parseEnumOrNull((String) this.meta.get(QueryBuilderServiceImpl.SQL_TYPE_SELECT_ELEMENT_HINT), SqlTypeName.class);
    }

    public String getName() {
        return name;
    }

    public String getExpr() {
        return expr;
    }

    public String getAlias() {
        return alias;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public List<Object> getParams() {
        return params;
    }

    @Nullable
    public SqlTypeName getSqlTypeHint() {
        return sqlTypeHint;
    }

    public String getSelectFromJoin() {
        return firstNotBlank(toStringOrNull(meta.get(SELECT_FROM_JOIN_ID)), JOIN_ID_DEFAULT);
    }

    public boolean isDistinct() {
        return toBooleanOrDefault(meta.get(SELECT_DISTINCT), false);
    }

    public SelectElement withMeta(String key, Object value) {
        return copyOf(this).withMeta(key, value).build();
    }

    @Override
    public String toString() {
        return "SelectElement{" + "expr=" + expr + ", alias=" + alias + ", name=" + name + '}';
    }

    public static SelectElementBuilder builder() {
        return new SelectElementBuilder();
    }

    public static SelectElement build(String name, String expr, String alias) {
        return builder().withName(name).withExpr(expr).withAlias(alias).build();
    }

    public static SelectElementBuilder copyOf(SelectElement source) {
        return new SelectElementBuilder().withMeta(source.getMeta()).withParams(source.getParams()).withExpr(source.getExpr()).withAlias(source.getAlias()).withName(source.getName());
    }

    public static class SelectElementBuilder implements Builder<SelectElement, SelectElementBuilder> {

        private Map<String, Object> meta = Collections.emptyMap();
        private List<Object> params = Collections.emptyList();
        private String expr;
        private String alias;
        private String name;

        public SelectElementBuilder withMeta(Map<String, Object> meta) {
            this.meta = meta;
            return this;
        }

        private SelectElementBuilder withMeta(String key, Object value) {
            return this.withMeta(map(meta).with(key, value));
        }

        public SelectElementBuilder withParams(List<Object> params) {
            this.params = params;
            return this;
        }

        public SelectElementBuilder withExpr(String expr) {
            this.expr = expr;
            return this;
        }

        public SelectElementBuilder withAlias(String alias) {
            this.alias = alias;
            return this;
        }

        public SelectElementBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SelectElementBuilder withSqlTypeHint(SqlTypeName sqlTypeName) {
            meta = CmMapUtils.map(meta).with(QueryBuilderServiceImpl.SQL_TYPE_SELECT_ELEMENT_HINT, sqlTypeName.name());
            return this;
        }

        @Override
        public SelectElement build() {
            return new SelectElement(this);
        }

    }

}

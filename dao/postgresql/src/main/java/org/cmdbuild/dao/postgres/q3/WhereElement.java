/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import org.cmdbuild.utils.lang.CmConvertUtils;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmPreconditions;

public class WhereElement {

    private final Map<String, Object> meta;
    private final String expr;

    public WhereElement(String expr, Map<String, Object> meta) {
        this.meta = ImmutableMap.copyOf(meta);
        this.expr = CmPreconditions.trimAndCheckNotBlank(expr);
    }

    public WhereElement(String expr) {
        this(expr, Collections.emptyMap());
    }

    public String getExpr() {
        return expr;
    }

    public boolean forRowNumber() {
        return CmConvertUtils.toBooleanOrDefault(getMeta().get(QueryBuilderServiceImpl.WHERE_ELEMENT_FOR_ROW_NUMBER), false);
    }

    public boolean requireWithExpr() {
        return CmConvertUtils.toBooleanOrDefault(getMeta().get(QueryBuilderServiceImpl.REQUIRE_WITH_EXPR), false);
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public WhereElement mapExpr(Function<String, String> exprFun) {
        return new WhereElement(exprFun.apply(expr), meta);
    }

    public WhereElement withMeta(Map<String, Object> meta) {
        return new WhereElement(expr, CmMapUtils.map(this.meta).with(meta));
    }

}

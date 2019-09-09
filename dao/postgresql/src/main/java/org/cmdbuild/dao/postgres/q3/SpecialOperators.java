/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

public enum SpecialOperators {
    EXPR

}
//
//    private final static String SQL_TYPE_SELECT_ELEMENT_HINT = "sqlTypeHint";
//
//    private static class SelectElement {
//
//        private final Map<String, Object> meta;
//        private final List<Object> params;
//        private final String expr, alias, name;
//        private final SqlTypeName sqlTypeHint;
//
//        private SelectElement(SelectElementBuilder builder) {
//            this.expr = checkNotBlank(builder.expr);
//            this.alias = checkNotBlank(builder.alias);
//            this.name = checkNotBlank(builder.name);
//            this.meta = ImmutableMap.copyOf(builder.meta);
//            this.params = ImmutableList.copyOf(builder.params);
//            this.sqlTypeHint = parseEnumOrNull((String) this.meta.get(SQL_TYPE_SELECT_ELEMENT_HINT), SqlTypeName.class);
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public String getExpr() {
//            return expr;
//        }
//
//        public String getAlias() {
//            return alias;
//        }
//
//        public Map<String, Object> getMeta() {
//            return meta;
//        }
//
//        public List<Object> getParams() {
//            return params;
//        }
//
//        @Nullable
//        public SqlTypeName getSqlTypeHint() {
//            return sqlTypeHint;
//        }
//
//        public static SelectElementBuilder builder() {
//            return new SelectElementBuilder();
//        }
//
//        public static SelectElement build(String name, String expr, String alias) {
//            return builder().withName(name).withExpr(expr).withAlias(alias).build();
//        }
//
//        public static SelectElementBuilder copyOf(SelectElement source) {
//            return new SelectElementBuilder()
//                    .withMeta(source.getMeta())
//                    .withParams(source.getParams())
//                    .withExpr(source.getExpr())
//                    .withAlias(source.getAlias())
//                    .withName(source.getName());
//        }
//
//        public static class SelectElementBuilder implements Builder<SelectElement, SelectElementBuilder> {
//
//            private Map<String, Object> meta = emptyMap();
//            private List<Object> params = emptyList();
//            private String expr;
//            private String alias;
//            private String name;
//
//            public SelectElementBuilder withMeta(Map<String, Object> meta) {
//                this.meta = meta;
//                return this;
//            }
//
//            public SelectElementBuilder withParams(List<Object> params) {
//                this.params = params;
//                return this;
//            }
//
//            public SelectElementBuilder withExpr(String expr) {
//                this.expr = expr;
//                return this;
//            }
//
//            public SelectElementBuilder withAlias(String alias) {
//                this.alias = alias;
//                return this;
//            }
//
//            public SelectElementBuilder withName(String name) {
//                this.name = name;
//                return this;
//            }
//
//            public SelectElementBuilder withSqlTypeHint(SqlTypeName sqlTypeName) {
//                meta = map(meta).with(SQL_TYPE_SELECT_ELEMENT_HINT, sqlTypeName.name());
//                return this;
//            }
//
//            @Override
//            public SelectElement build() {
//                return new SelectElement(this);
//            }
//
//        }
//    }
//
//    private static class WhereElement {
//
//        private final Map<String, Object> meta;
//        private final String expr;
//
//        public WhereElement(String expr, Map<String, Object> meta) {
//            this.meta = ImmutableMap.copyOf(meta);
//            this.expr = trimAndCheckNotBlank(expr);
//        }
//
//        public WhereElement(String expr) {
//            this(expr, emptyMap());
//        }
//
//        public String getExpr() {
//            return expr;
//        }
//
//        public boolean forRowNumber() {
//            return toBooleanOrDefault(getMeta().get(WHERE_ELEMENT_FOR_ROW_NUMBER), false);
//        }
//
//        public boolean requireWithExpr() {
//            return toBooleanOrDefault(getMeta().get(REQUIRE_WITH_EXPR), false);
//        }
//
//        public Map<String, Object> getMeta() {
//            return meta;
//        }
//
//        public WhereElement mapExpr(Function<String, String> exprFun) {
//            return new WhereElement(exprFun.apply(expr), meta);
//        }
//
//        public WhereElement withMeta(Map<String, Object> meta) {
//            return new WhereElement(expr, map(this.meta).with(meta));
//        }
//
//    }

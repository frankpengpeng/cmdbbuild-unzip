/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Streams.stream;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.utils.lang.ToPrimitive;
import org.cmdbuild.utils.object.CmBeanUtils;

public class AttributeFilterProcessor<T> {

    private final KeyToValueFunction<T> keyToValueFunction;
    private final ConditionEvaluatorFunction conditionEvaluatorFunction;
    private final AttributeFilter attributeFilter;

    private AttributeFilterProcessor(AttributeFilterProcessorBuilder builder) {
        this.keyToValueFunction = checkNotNull(builder.keyToValueFunction);
        this.conditionEvaluatorFunction = checkNotNull(builder.conditionEvaluatorFunction);
        this.attributeFilter = checkNotNull(builder.attributeFilter);
    }

    public List<T> filter(Iterable<T> list) {
        return stream(list)
                .filter(this::match)
                .collect(toList());
    }

    public boolean match(T item) {
        return match(attributeFilter, item);
    }

    private boolean match(AttributeFilter filter, T item) {
        switch (filter.getMode()) {
            case NOT:
                return !match(filter.getOnlyElement(), item);
            case AND:
                return filter.getElements().stream().allMatch((f) -> match(f, item));
            case OR:
                return filter.getElements().stream().anyMatch((f) -> match(f, item));
            case SIMPLE:
                return match(filter.getCondition(), item);
            default:
                throw new IllegalStateException("unsupported filter mode = " + filter.getMode());
        }
    }

    private boolean match(AttributeFilterCondition condition, T item) {
        Object value = keyToValueFunction.apply(condition.getKey(), item);
        return conditionEvaluatorFunction.evaluate(condition, value);
    }

    public static interface ConditionEvaluatorFunction<E> {

        boolean evaluate(AttributeFilterCondition condition, E value);

    }

    public static interface KeyToValueFunction<T> {

        Object apply(String key, T object);
    }

    public KeyToValueFunction<T> getKeyToValueFunction() {
        return keyToValueFunction;
    }

    public ConditionEvaluatorFunction getConditionEvaluatorFunction() {
        return conditionEvaluatorFunction;
    }

    public static <T> AttributeFilterProcessorBuilder<T> builder() {
        return new AttributeFilterProcessorBuilder<>();
    }

    public static class AttributeFilterProcessorBuilder<T> implements Builder<AttributeFilterProcessor<T>, AttributeFilterProcessorBuilder<T>> {

        private KeyToValueFunction keyToValueFunction = DefaultBeanKeyToValueFunction.INSTANCE;
        private ConditionEvaluatorFunction conditionEvaluatorFunction = DefaultConditionEvaluatorFunction.INSTANCE;
        private AttributeFilter attributeFilter;

        public AttributeFilterProcessorBuilder withKeyToValueFunction(KeyToValueFunction<T> keyToValueFunction) {
            this.keyToValueFunction = keyToValueFunction;
            return this;
        }

        public AttributeFilterProcessorBuilder withDefaultBeanKeyToValueFunction() {
            return this.withKeyToValueFunction(DefaultBeanKeyToValueFunction.INSTANCE);
        }

        public AttributeFilterProcessorBuilder withConditionEvaluatorFunction(ConditionEvaluatorFunction conditionEvaluatorFunction) {
            this.conditionEvaluatorFunction = conditionEvaluatorFunction;
            return this;
        }

        public AttributeFilterProcessorBuilder withDefaultConditionEvaluatorFunction() {
            return this.withConditionEvaluatorFunction(DefaultConditionEvaluatorFunction.INSTANCE);
        }

        public AttributeFilterProcessorBuilder withFilter(AttributeFilter attributeFilter) {
            this.attributeFilter = attributeFilter;
            return this;
        }

        @Override
        public AttributeFilterProcessor build() {
            return new AttributeFilterProcessor(this);
        }

        public <T> List<T> filter(List<T> list) {
            return build().filter(list);
        }

    }

    public static enum DefaultBeanKeyToValueFunction implements KeyToValueFunction {

        INSTANCE;

        @Override
        public Object apply(String key, Object object) {
            return CmBeanUtils.getBeanPropertyValue(object, key);
        }

    }

    public static enum DefaultConditionEvaluatorFunction implements ConditionEvaluatorFunction {
        INSTANCE;

        @Override
        public boolean evaluate(AttributeFilterCondition condition, Object value) {
            switch (condition.getOperator()) {
                case EQUAL:
                    return equal(toPrimitiveStringOrNull(value), condition.getSingleValue());
                case IN:
                    return condition.getValues().contains(toPrimitiveStringOrNull(value));
                default:
                    throw new IllegalArgumentException("unsupported operator = " + condition.getOperator());
            }
        }

        @Nullable
        private String toPrimitiveStringOrNull(@Nullable Object value) {
            if (value == null) {
                return null;
            } else if (value instanceof ToPrimitive) {
                return toPrimitiveStringOrNull(((ToPrimitive) value).toPrimitive());
            } else {
                return toStringOrNull(value);
            }
        }

    }
}

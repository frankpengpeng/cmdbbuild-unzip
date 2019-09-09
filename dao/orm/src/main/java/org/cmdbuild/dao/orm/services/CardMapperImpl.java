/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.services;

import com.google.common.base.Function;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.collect.LinkedListMultimap;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import static com.google.common.collect.Sets.newTreeSet;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.util.Arrays.stream;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.CardMapper;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import static org.cmdbuild.dao.orm.annotations.CardAttr.NO_DEFAULT_VALUE;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.utils.lang.CmExceptionUtils.cause;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;
import org.cmdbuild.dao.beans.CardDefinition;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl.CardImplBuilder;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.hasJsonBeanAnnotation;
import org.springframework.context.annotation.Primary;

public class CardMapperImpl<T, B extends Builder<T, B>> implements CardMapper<T, B> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Class<T> targetClass;
    private final String className;
    private Class<B> builderClass;
    private Supplier<B> builderSupplier;
    private final Map<String, GetterHelper> getterMethods;
    private final Map<String, SetterHelper> setterMethods;
    private final boolean primary;

    public CardMapperImpl(Class<T> targetClass) {
        logger.debug("create card mapper for target class = {}", targetClass);
        this.targetClass = checkNotNull(targetClass);
        CardMapping classAnnotation = checkNotNull(targetClass.getAnnotation(CardMapping.class));
//		className = checkNotBlank(classAnnotation.value(), "className not found for card mapping annotation in class %s", targetClass);
        className = checkNotNull(classAnnotation.value());
        primary = targetClass.isAnnotationPresent(Primary.class);
        logger.debug("card class name = '{}'", className);
        scanBuilderMethod();
        logger.debug("builder class = {}", builderClass);
        getterMethods = findGetterMethods();
        setterMethods = findSetterMethods();
        logger.debug("mapped attributes = {}", newTreeSet(concat(getterMethods.keySet(), setterMethods.keySet())));
        checkArgument(getterMethods.size() == setterMethods.size(), "missing card mapper setter methods for attributes = %s while parsing %s", Sets.difference(getterMethods.keySet(), setterMethods.keySet()), targetClass);
    }

    private void scanBuilderMethod() {
        ReflectionUtils.doWithMethods(targetClass, (method) -> {
            if (Modifier.isStatic(method.getModifiers())) {
                logger.trace("looking for builder method, check method {}", method);
                if (returnsCorrectBuilder(method)) {
                    logger.debug("selected builder method {}", method);
                    builderClass = (Class<B>) method.getReturnType();
                    builderSupplier = () -> {
                        try {
                            return checkNotNull((B) method.invoke(null), "error, builder method %s return null", method);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            throw new RuntimeException(ex);
                        }
                    };
                }
            }
        });
        checkNotNull(builderSupplier, "cannot find builder method in mapped card %s", targetClass);
    }

    private boolean returnsCorrectBuilder(Method method) {
        if (Builder.class.isAssignableFrom(method.getReturnType()) && method.getName().toLowerCase().contains("builder")) {
//			if ((method.getGenericReturnType() instanceof ParameterizedType)
//					&& (((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments().length == 2)
//					&& targetClass.isAssignableFrom((Class<?>) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0])) {
//				return true;
//			} else {
            Optional<Type> builderType = stream(method.getReturnType().getGenericInterfaces()).filter((type) -> Builder.class.isAssignableFrom(toClass(type))
                    && type instanceof ParameterizedType
                    && ((ParameterizedType) type).getActualTypeArguments().length == 2
                    && targetClass.isAssignableFrom(toClass(((ParameterizedType) type).getActualTypeArguments()[0]))
            ).findAny();
            if (builderType.isPresent()) {
                return true;
            }
//			}
        }
        return false;
    }

    public static Class toClass(Type type) {
        if (type instanceof Class) {
            return (Class) type;
        } else if (type instanceof ParameterizedType) {
            return toClass(((ParameterizedType) type).getRawType());
        } else {
            throw new RuntimeException("unable to convert type " + type + " to class");
        }
    }

    private Map<String, GetterHelper> findGetterMethods() {
        FluentMap<String, GetterHelper> map = map();
        ReflectionUtils.doWithMethods(targetClass, (method) -> {
            if (method.isAnnotationPresent(CardAttr.class)) {
                CardAttr annotation = method.getAnnotation(CardAttr.class);
                String key = trimToNull(annotation.value());
                if (key == null) {
                    key = processGetterMethodName(method);
                }
                Object defaultValue = hasDefaultValue(annotation) ? annotation.defaultValue() : null;
                GetterHelper getterHelper = new GetterHelper(method, key, defaultValue, !annotation.readFromDb());
                map.put(key, getterHelper);
            }
        });
        return map.immutable();
    }

    private String processGetterMethodName(Method method) {
        return capitalize(method.getName().replaceFirst("^(get|is)", ""));
    }

    private Map<String, SetterHelper> findSetterMethods() {
        FluentMap<String, SetterHelper> map = map();
        Map<String, GetterHelper> getterMethodsByMethodProcessedName = uniqueIndex(getterMethods.values(), (entry) -> processGetterMethodName(entry.getMethod()));

        Multimap<String, Method> candidateSetterMethods = LinkedListMultimap.create();

        ReflectionUtils.doWithMethods(builderClass, (method) -> {
            if (method.getParameterCount() == 1 || method.isAnnotationPresent(CardAttr.class)) {
                CardAttr annotation = method.getAnnotation(CardAttr.class);
                if (annotation == null || !annotation.ignore()) {
                    String key = annotation == null ? null : trimToNull(annotation.value());
                    boolean keyFromAnnotation;
                    if (key == null) {
                        keyFromAnnotation = false;
                        key = capitalize(method.getName().replaceFirst("^(set|with)", ""));
                    } else {
                        keyFromAnnotation = true;
                    }
                    GetterHelper getterHelper;
                    if (keyFromAnnotation) {
                        getterHelper = getterMethods.get(key);
                    } else {
                        getterHelper = getterMethodsByMethodProcessedName.get(key);
                    }
                    if (getterHelper != null && !getterHelper.isIgnoreReadFromOnDb()) {
                        key = getterHelper.getKey();
                        candidateSetterMethods.put(key, method);
                    }
                }
            }
        });

        candidateSetterMethods.asMap().forEach((key, methods) -> {
            GetterHelper getterHelper = checkNotNull(getterMethods.get(key));
            Method method = methods.size() == 1 ? getOnlyElement(methods) : methods.stream().filter(m -> equal(getterHelper.getMethod().getReturnType(), m.getParameterTypes()[0])).collect(onlyElement("unable to find matching setter method for key = %s bean = %s", key, targetClass));
            Object defaultValue = getterHelper.getDefaultValue();
            CardAttr annotation = method.getAnnotation(CardAttr.class);
            if (hasDefaultValue(annotation)) {
                defaultValue = annotation.defaultValue();
            }
            SetterHelper setterHelper = new SetterHelperImpl(method, defaultValue);
            map.put(key, setterHelper);
        });

        getterMethods.values().stream().filter(GetterHelper::isIgnoreReadFromOnDb).forEach((g) -> {
            map.put(g.getKey(), NoopSetterHelper.INSTANCE);
        });
        return map.immutable();
    }

    @Override
    public Class<T> getTargetClass() {
        return targetClass;
    }

    @Override
    public String getClassId() {
        return className;
    }

    @Override
    public boolean isPrimaryMapper() {
        return primary;
    }

    @Override
    public Long getCardId(T object) {
        try {
            return getValue(object, ATTR_ID, Long.class);
        } catch (Exception ex) {
            throw new DaoException(ex, "unable to retrieve Id attribute from bean = %s", object);
        }
    }

    @Override
    public String toString() {
        return "CardMapperImpl{" + "targetClass=" + targetClass.getName() + ", className=" + className + '}';
    }

    private @Nullable
    <V> V getValue(T object, String key, Class<V> valueClass) {
        GetterHelper getter = checkNotNull(getterMethods.get(key), "getter not found for attribute = %s targetClass = %s", key, targetClass);
        Object sourceValue = getter.get(object);
        if (sourceValue == null) {
            return null;
        } else {
            V targetValue = convert(sourceValue, valueClass);
            return targetValue;
        }
    }

    @Override
    @Deprecated
    public CardDefinition objectToCard(CardDefinition cardDefinition, T object) {
        logger.debug("mapping bean = {} to card = {}", object, cardDefinition);
        getterMethods.forEach((key, getter) -> {
            logger.trace("copy bean attribute {} to card", key);
            Object value = getter.get(object);
            if (isSpecialCardValue(key)) {
                setSpecialCardValue(cardDefinition, key, value);
            } else {
                cardDefinition.set(key, value);
            }
        });
        return cardDefinition;
    }

    @Override
    public CardImplBuilder objectToCard(CardImplBuilder builder, T object) {
        logger.debug("mapping bean = {} to card = {}", object, builder);
        getterMethods.forEach((key, getter) -> {
            logger.trace("copy bean attribute {} to card", key);
            Object value = getter.get(object);
            builder.addAttribute(key, value);
        });
        return builder;
    }

    @Override
    public B cardToObject(Card card) {
        try {
            return dataToObject((key) -> {
                Object value;
                if (isSpecialCardValue(key)) {
                    value = getSpecialCardValue(card, key);
                } else {
                    value = card.get(key);
                }
                return value;
            });
        } catch (Exception ex) {
            throw new DaoException(ex, "error mapping card = %s to model = %s", card, targetClass);
        }
    }

    @Override
    public B dataToObject(Function<String, Object> dataSource) {
        B builder = builderSupplier.get();
        setterMethods.forEach((key, setter) -> {
            Object value = dataSource.apply(key);
            setter.set(value, builder);
        });
        return builder;
    }

    private boolean isSpecialCardValue(String key) {
        return equal(key, ATTR_ID);
    }

    private Object getSpecialCardValue(Card card, String key) {
        switch (key) {
            case ATTR_ID:
                return card.getId();
            default:
                throw new UnsupportedOperationException("not forund a special card value for name = " + key);
        }
    }

    private void setSpecialCardValue(CardDefinition card, String key, Object value) {
        switch (key) {
            case ATTR_ID:
                break;//id already set, nothing to do
            default:
                throw new UnsupportedOperationException("not forund a special card value for name = " + key);
        }
    }

    @Override
    public B sqlToObject(ResultSet resultSet) {
        return dataToObject((key) -> {
            try {
                return resultSet.getObject(key);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @Override
    public Class<B> getBuilderClass() {
        return builderClass;
    }

    private boolean hasDefaultValue(@Nullable CardAttr annotation) {
        return annotation != null && !equal(NO_DEFAULT_VALUE, annotation.defaultValue());
    }

    private static class GetterHelper {

        private final Method method;
        private final String key;
        private final Object defaultValue;
        private final boolean ignoreReadFromDb;

        public GetterHelper(Method method, String key, @Nullable Object defaultValue, boolean ignoreReadFromDb) {
            this.method = checkNotNull(method);
            this.key = checkNotNull(key);
            this.defaultValue = defaultValue;
            this.ignoreReadFromDb = ignoreReadFromDb;
        }

        public boolean isIgnoreReadFromOnDb() {
            return ignoreReadFromDb;
        }

        public Method getMethod() {
            return method;
        }

        public String getKey() {
            return key;
        }

        public Object getDefaultValue() {
            return defaultValue;
        }

        public Object get(Object targetObject) {
            try {
                Object value = method.invoke(targetObject);
                if (value != null && (hasJsonBeanAnnotation(value.getClass()) || hasJsonBeanAnnotation(method.getReturnType()) || hasJsonBeanAnnotation(method))) { //TODO replace this with attribute awareness - check card attribute type, and use that to identify json data
                    value = toJson(value);
                }
                return value;
            } catch (InvocationTargetException ex) {
                throw cause(ex);
            } catch (IllegalAccessException | IllegalArgumentException ex) {
                throw runtime(ex);
            }
        }

    }

    private static interface SetterHelper {

        void set(@Nullable Object sourceValue, Object targetObject);
    }

    private static enum NoopSetterHelper implements SetterHelper {

        INSTANCE;

        @Override
        public void set(Object sourceValue, Object targetObject) {
        }

    }

    private static class SetterHelperImpl implements SetterHelper {

        private final Method method;
        private final Object defaultValue;

        public SetterHelperImpl(Method method, @Nullable Object defaultValue) {
            this.method = checkNotNull(method);
            this.defaultValue = defaultValue;
        }

        public Method getMethod() {
            return method;
        }

        @Nullable
        public Object getDefaultValue() {
            return defaultValue;
        }

        @Override
        public void set(@Nullable Object sourceValue, Object targetObject) {
            if (sourceValue == null) {
                sourceValue = defaultValue;
            }
            Object param;
            try {
                param = convert(sourceValue, method.getGenericParameterTypes()[0]);
            } catch (Exception ex) {
                throw runtime(ex, "error converting value for setter method = %s.%s", method.getDeclaringClass().getSimpleName(), method.getName());
            }
            try {
                method.invoke(targetObject, param);
            } catch (Exception ex) {
                throw runtime(ex, "error invoking obj setter = %s.%s with value = '%s' (%s)", method.getDeclaringClass().getSimpleName(), method.getName(), abbreviate(param), getClassOfNullable(param));
            }
        }

    }

}

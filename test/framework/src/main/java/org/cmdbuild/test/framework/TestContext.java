/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.framework;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.lang.reflect.InvocationTargetException;
import static java.util.Arrays.stream;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import static java.util.function.Function.identity;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.context.ApplicationContext;

public class TestContext {

    private final Consumer<TestContext> cleanupFunction, wakeupFunction;
    private final Map<String, Object> context;

    public TestContext(Object context, Consumer<TestContext> cleanupFunction, Consumer<TestContext> wakeupFunction) {
        this(singletonList(context), cleanupFunction, wakeupFunction);
    }

    private TestContext(List context, Consumer<TestContext> cleanupFunction, Consumer<TestContext> wakeupFunction) {
        this(uniqueIndex(context, (o) -> o instanceof Class ? ((Class) o).getSimpleName() : o.getClass().getSimpleName()), cleanupFunction, wakeupFunction);
    }

    public TestContext(Map<String, ?> context) {
        this(context, x -> {
        }, x -> {
        });
    }

    public TestContext(Map<String, ?> context, Consumer<TestContext> cleanupFunction, Consumer<TestContext> wakeupFunction) {
        this.cleanupFunction = checkNotNull(cleanupFunction);
        this.wakeupFunction = checkNotNull(wakeupFunction);
        this.context = map(context);
        if (!this.context.values().stream().anyMatch(ApplicationContext.class::isInstance)) {
            this.context.values().stream().filter(not(isNull())).map((b) -> stream(b.getClass().getMethods()).filter((m) -> ApplicationContext.class.isAssignableFrom(m.getReturnType())).map((m) -> {
                try {
                    return (ApplicationContext) m.invoke(b);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw runtime(ex);
                }
            })).flatMap(identity()).filter(not(isNull())).forEach((a) -> this.context.put("applicationContext", a));
        }
    }

    public void cleanup() {
        cleanupFunction.accept(this);
        context.clear();
    }

    public Map<String, Object> getContextMap() {
        return context;
    }

    public boolean hasApplicationContext() {
        return context.values().stream().filter(ApplicationContext.class::isInstance).count() > 0;
    }

    public ApplicationContext getApplicationContext() {
        return context.values().stream().filter(ApplicationContext.class::isInstance).map(ApplicationContext.class::cast).collect(onlyElement());
    }

    public <T> T getBean(Class<T> classe) {
        T bean = context.values().stream().filter(classe::isInstance).map(classe::cast).collect(toOptional()).orElse(null);
        if (bean == null && hasApplicationContext()) {
            bean = getApplicationContext().getBean(classe);
        }
        return checkNotNull(bean, "bean not found in test context for class = %s", classe);
    }

    @Nullable
    public <T> T get(String key) {
        return (T) context.get(key);
    }

    public void prepare() {
        wakeupFunction.accept(this);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.utils;

import com.google.common.collect.ComparisonChain;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableSet;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import static java.util.Arrays.stream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import javax.inject.Named;
import javax.inject.Singleton;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.quick.QuickEventListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class QuickUtils {

    public final static String QUALIFIER_PRIMARY = "org.cmdbuild.utils.quick.inner.PRIMARY_QUALIFIER",
            EVENT_LISTENER_SPRING_CONTEXT_REFRESH = "org.springframework.context.event.ContextRefreshedEvent";

    private final static Set<Class> CLASS_ANNOTATIONS = ImmutableSet.of(Named.class, Singleton.class, Component.class);//TODO

    public static List<Constructor> getConstructorsWithStandardOrder(Class classe) {
        return list(classe.getConstructors()).stream().sorted((a, b) -> ComparisonChain.start()
                .compare(a.getParameterCount(), b.getParameterCount())
                .compare(stream(a.getParameterTypes()).map(c -> c.getName()).collect(joining(",")), stream(b.getParameterTypes()).map(c -> c.getName()).collect(joining(",")))
                .result()).collect(toImmutableList());
    }

    public static Set<String> getQualifiersForClass(Class classe) {
        Set<String> qualifiers = set();
        if (classe.isAnnotationPresent(Named.class)) {
            Named annotation = (Named) classe.getAnnotation(Named.class);
            if (isNotBlank(annotation.value())) {
                qualifiers.add(annotation.value());
            }
        }
        if (classe.isAnnotationPresent(Qualifier.class)) {
            Qualifier annotation = (Qualifier) classe.getAnnotation(Qualifier.class);
            if (isNotBlank(annotation.value())) {
                qualifiers.add(annotation.value());
            }
        }
        if (classe.isAnnotationPresent(Component.class)) {
            Component annotation = (Component) classe.getAnnotation(Component.class);
            if (isNotBlank(annotation.value())) {
                qualifiers.add(annotation.value());
            }
        }
        if (classe.isAnnotationPresent(Primary.class)) {
            qualifiers.add(QUALIFIER_PRIMARY);
        }
        return ImmutableSet.copyOf(qualifiers);
    }

    public static Collection<QuickEventListener> getEventListenersForMethod(Method method) {
        Collection<QuickEventListener> events = list();
        if (method.isAnnotationPresent(EventListener.class) && method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(ContextRefreshedEvent.class)) {
            events.add(new QuickEventListenerImpl(method.getName(), EVENT_LISTENER_SPRING_CONTEXT_REFRESH));
        }
        return events;
    }

    public static Set<String> getQualifiersForParameter(Parameter parameter) {
        Set<String> paramQualifiers = set();
        if (parameter.isAnnotationPresent(Qualifier.class)) {
            Qualifier annotation = (Qualifier) parameter.getAnnotation(Qualifier.class);
            if (isNotBlank(annotation.value())) {
                paramQualifiers.add(annotation.value());
            }
        }
        return ImmutableSet.copyOf(paramQualifiers);
    }

    public static boolean shouldLoadItem(Class classe) {
        return CLASS_ANNOTATIONS.stream().anyMatch(classe::isAnnotationPresent);
    }

    private static class QuickEventListenerImpl implements QuickEventListener {

        private final String methodName, listenerType;

        public QuickEventListenerImpl(String methodName, String listenerType) {
            this.methodName = checkNotBlank(methodName);
            this.listenerType = checkNotBlank(listenerType);
        }

        @Override
        public String getMethodName() {
            return methodName;
        }

        @Override
        public String getListenerType() {
            return listenerType;
        }
        
    }
}

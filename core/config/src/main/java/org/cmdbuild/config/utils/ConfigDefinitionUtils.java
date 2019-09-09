/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.utils;

import static com.google.common.base.Preconditions.checkArgument;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigDefinition;
import org.cmdbuild.config.api.ConfigDefinitionImpl;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.utils.ConfigUtils.addNamespaceToKey;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ReflectionUtils;

public class ConfigDefinitionUtils {

    public static List<ConfigDefinition> getAllConfigDefinitionsFromClasspath() {
        List<ConfigDefinition> list = list();
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ConfigComponent.class));
        scanner.findCandidateComponents("org.cmdbuild").stream().forEach(b -> {
            try {
                Class c = Class.forName(b.getBeanClassName());
                ConfigComponent classAnnotation = (ConfigComponent) c.getAnnotation(ConfigComponent.class);
                if (classAnnotation != null) {
                    String namespace = checkNotBlank(classAnnotation.value());
                    parseBeanForConfigDefinitions(c, (d) -> list.add(ConfigDefinitionImpl.copyOf(d).withKey(addNamespaceToKey(namespace, d.getKey())).build()));
                }
            } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                throw runtime(ex, "error loading class for name = %s", b.getBeanClassName());
            }
        });
        checkArgument(!list.isEmpty(), "error retrieving config definitions from classpath: config definitions not found");
        return list;
    }

    public static void parseBeanForConfigDefinitions(Class classe, Consumer<ConfigDefinition> configDefinitionCallback) {
        parseBeanForConfigDefinitions(classe, configDefinitionCallback, (x, y) -> {
        }, (x, y) -> {
        });
    }

    public static void parseBeanForConfigDefinitions(Class classe, Consumer<ConfigDefinition> configDefinitionCallback, BiConsumer<ConfigDefinition, Field> fieldCallback, BiConsumer<ConfigDefinition, Method> methodCallback) {
        ReflectionUtils.doWithFields(classe, (field) -> {
            if (field.isAnnotationPresent(ConfigValue.class)) {
                ConfigValue fieldAnnotation = field.getAnnotation(ConfigValue.class);
                ConfigDefinition configDefinition = buildConfigDefinitionFromAnnotation(fieldAnnotation);
                configDefinitionCallback.accept(configDefinition);
                fieldCallback.accept(configDefinition, field);
            }
        });
        ReflectionUtils.doWithMethods(classe, (method) -> {
            if (method.isAnnotationPresent(ConfigValue.class)) {
                checkArgument(method.getParameterCount() == 1, "config value methods must have one and only one parameter; invalid method = %s", method);
                ConfigValue methodAnnotation = method.getAnnotation(ConfigValue.class);
                ConfigDefinition configDefinition = buildConfigDefinitionFromAnnotation(methodAnnotation);
                configDefinitionCallback.accept(configDefinition);
                methodCallback.accept(configDefinition, method);
            }
        });
    }

    public static ConfigDefinition buildConfigDefinitionFromAnnotation(ConfigValue annotation) {
        String key = firstNotBlank(nullIfEqualToNullConst(annotation.key()), nullIfEqualToNullConst(annotation.value()));
        return ConfigDefinitionImpl.builder()
                .withKey(key)
                .withDescription(annotation.description())
                .withDefaultValue(nullIfEqualToNullConst(annotation.defaultValue()))
                .withProtected(annotation.isProtected())
                .withLocation(annotation.location())
                .withCategory(annotation.category())
                .build();
    }

    private static @Nullable
    String nullIfEqualToNullConst(@Nullable String value) {
        return (value == null || ConfigValue.NULL.equals(value)) ? null : value;
    }

}

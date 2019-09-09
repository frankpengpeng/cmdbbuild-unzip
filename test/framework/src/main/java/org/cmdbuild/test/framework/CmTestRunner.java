/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.framework;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Strings;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.collect.Ordering;
import static java.lang.String.format;
import java.lang.reflect.Method;
import static java.util.Arrays.stream;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.util.function.Function.identity;
import java.util.function.Supplier;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import org.cmdbuild.utils.lang.CmExceptionUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.inner;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmReflectionUtils.executeMethod;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CmTestRunner extends Runner implements Filterable {

    private final static String DUMMY_TEST_CONTEXT = "DUMMY_TEST_CONTEXT";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Class mainClass;

    private final List<TestMethod> testMethods;
    private final List<Method> before, after;
    private final Map<String, TestContextProvider> availableContextProviders;

    public CmTestRunner(Class<?> testClass) throws InitializationError {
        mainClass = testClass;
        availableContextProviders = map(getGlobalContextProviders()).with(stream(mainClass.getMethods()).filter((m) -> TestContext.class.isAssignableFrom(m.getReturnType()))
                .map((m) -> new TestContextProvider(buildContextIdWithClassNamespace(getContextIdForProviderMethod(m)), () -> executeMethod(m), Optional.ofNullable(m.getAnnotation(ContextProvider.class)).map(ContextProvider::primary).orElse(false)))
                .collect(toMap(TestContextProvider::getContextId, identity())));

        String defaultContextProviderId = availableContextProviders.values().stream().filter(TestContextProvider::isPrimary).collect(toOptional()).map(TestContextProvider::getContextId).orElse(null);

        Set<String> classContextToUse = Optional.ofNullable((Context) mainClass.getAnnotation(Context.class)).map((c) -> list(c.value()).stream().filter(not(Strings::isNullOrEmpty)).collect(toSet())).orElse(emptySet());

        testMethods = stream(mainClass.getMethods()).filter((m) -> m.getAnnotation(Test.class) != null).sorted(Ordering.natural().onResultOf(Method::getName))
                .map((m) -> {
                    Collection<String> contextToUse = Optional.ofNullable(m.getAnnotation(Context.class)).map((c) -> list(c.value()).stream().filter(not(Strings::isNullOrEmpty)).collect(toSet())).orElse(classContextToUse);
                    if (contextToUse.isEmpty()) {
                        if (isNotBlank(defaultContextProviderId)) {
                            contextToUse = singletonList(defaultContextProviderId);
                        } else if (availableContextProviders.isEmpty()) {
                            contextToUse = singletonList(DUMMY_TEST_CONTEXT);
                        } else {
                            contextToUse = availableContextProviders.keySet();
                        }
                    }
                    boolean requireCleanContext = Optional.ofNullable(m.getAnnotation(Context.class)).map(Context::clean).orElse(false);
                    return contextToUse.stream().map((k) -> new TestMethod(m, k, requireCleanContext)).collect(toList());
                }).flatMap(Collection::stream).collect(toList());

        before = stream(mainClass.getMethods()).filter((m) -> m.getAnnotation(Before.class) != null).sorted(Ordering.natural().onResultOf(Method::getName)).collect(toImmutableList());
        after = stream(mainClass.getMethods()).filter((m) -> m.getAnnotation(After.class) != null).sorted(Ordering.natural().onResultOf(Method::getName)).collect(toImmutableList());

    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        testMethods.removeIf((m) -> !(filter.shouldRun(m.getDescriptionIncludingContext()) || filter.shouldRun(m.getSimpleDescriptionWithoutContext())));
    }

    private Map<String, TestContextProvider> getGlobalContextProviders() {
        ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
        provider.addIncludeFilter(new AnnotationTypeFilter(ContextProvider.class));
        return provider.findCandidateComponents(mainClass.getPackage().getName()).stream().map(BeanDefinition::getBeanClassName).map((n) -> {
            try {
                return Class.forName(n);
            } catch (Exception ex) {
                throw runtime(ex);
            }
        }).map((c) -> list(c.getMethods())).flatMap(List::stream).filter((m) -> TestContext.class.isAssignableFrom(m.getReturnType())).map((m) -> {
            return new TestContextProvider(buildContextIdWithPackageNamespace(getContextIdForProviderMethod(m)), () -> executeMethod(m), false);
        }).collect(toMap(TestContextProvider::getContextId, identity()));
    }

    private String buildContextIdWithPackageNamespace(String localName) {
        return format("%s.%s", mainClass.getPackage().getName(), localName);
    }

    private static class TestContextProvider {

        private final Supplier<TestContext> supplier;
        private final String contextId;
        private final boolean isPrimary;

        public TestContextProvider(String contextId, Supplier<TestContext> supplier, boolean isPrimary) {
            this.supplier = checkNotNull(supplier);
            this.contextId = checkNotBlank(contextId);
            this.isPrimary = isPrimary;
        }

        public Supplier<TestContext> getSupplier() {
            return supplier;
        }

        public String getContextId() {
            return contextId;
        }

        public boolean isPrimary() {
            return isPrimary;
        }

    }

    private String getContextIdForProviderMethod(Method m) {
        return Optional.ofNullable(m.getAnnotation(ContextProvider.class)).map(ContextProvider::value).map(Strings::emptyToNull).orElse(m.getName());
    }

    private String buildContextIdWithClassNamespace(String localId) {
        return format("%s.%s", mainClass.getName(), checkNotBlank(localId));
    }

    @Override
    public Description getDescription() {
        Description mainDescription = Description.createSuiteDescription(format("MyRunnerForDaoTest_%s", mainClass.getSimpleName()));
        testMethods.stream().map(TestMethod::getDescriptionIncludingContext).forEach(mainDescription::addChild);
        return mainDescription;
    }

    @Override
    public void run(RunNotifier notifier) {
        testMethods.forEach((m) -> runTestMethod(m, notifier));
    }

    private void runTestMethod(TestMethod testMethod, RunNotifier notifier) {
        testMethod.new TestMethodRun(notifier).run();
    }

    private class TestMethod {

        private final Method method;
        private final Description descriptionWithoutContext, descriptionWithContext;
        private final Supplier<TestContext> contextProvider;

        public TestMethod(Method method, String contextId, boolean requireCleanContext) {
            this.method = checkNotNull(method);
            checkNotBlank(contextId);
            String name = method.getName();
            descriptionWithoutContext = Description.createTestDescription(mainClass, name);
            if (equal(contextId, DUMMY_TEST_CONTEXT)) {
                contextProvider = () -> new TestContext(emptyMap());
            } else {
                TestContextProvider contextProviderMethod = checkNotNull(firstNotNullOrNull(
                        availableContextProviders.get(buildContextIdWithClassNamespace(contextId)),
                        availableContextProviders.get(buildContextIdWithPackageNamespace(contextId)),
                        availableContextProviders.get(contextId)), "test context not found for name = %s", contextId);
                if (requireCleanContext) {
                    contextProvider = contextProviderMethod.getSupplier();
                } else {
                    contextProvider = () -> TestContextHelper.getTestContext(contextProviderMethod.getContextId(), contextProviderMethod.getSupplier());
                }
                name = format("%s@%s", name, contextId);
            }

            descriptionWithContext = Description.createTestDescription(mainClass, name);
        }

        public Description getDescriptionIncludingContext() {
            return descriptionWithContext;
        }

        public Description getSimpleDescriptionWithoutContext() {
            return descriptionWithoutContext;
        }

        private class TestMethodRun {

            private final RunNotifier notifier;
            private TestContext context;
            private Object mainInstance;
            private final Class<? extends Throwable> expectedException;

            public TestMethodRun(RunNotifier notifier) {
                this.notifier = checkNotNull(notifier);
                Test annotation = method.getAnnotation(Test.class);
                if (annotation != null && annotation.expected() != null) {
                    expectedException = annotation.expected();
                } else {
                    expectedException = null;
                }
            }

            public void run() {
                if (method.getAnnotation(Ignore.class) != null) {
                    notifier.fireTestIgnored(descriptionWithContext);
                    logger.info("{} IGNORED", descriptionWithContext.getDisplayName());
                } else {
                    notifier.fireTestStarted(descriptionWithContext);
                    logger.info("{} BEGIN", descriptionWithContext.getDisplayName());
                    boolean initSuccessful = false;
                    try {
                        init();
                        initSuccessful = true;
                    } catch (Exception ex) {
                        logger.error("error preparing test run for test = {}", descriptionWithContext.getDisplayName(), ex);
                        notifier.fireTestFailure(new Failure(descriptionWithContext, ex));
                    }
                    if (initSuccessful) {
                        try {
                            logger.debug("{} running test", descriptionWithContext.getDisplayName());
                            method.invoke(mainInstance);
                            notifier.fireTestFinished(descriptionWithContext);
                        } catch (Exception ex) {
                            if (expectedException != null && expectedException.isInstance(ex) && !AssertionError.class.isInstance(ex)) {
                                notifier.fireTestFinished(descriptionWithContext);
                            } else {
                                logger.debug("test error", ex);
                                notifier.fireTestFailure(new Failure(descriptionWithContext, CmExceptionUtils.inner(ex)));
                            }
                        }
                    }
                    cleanupSafe();
                    logger.info("{} END", descriptionWithContext.getDisplayName());
                }
            }

            private void init() throws InstantiationException, IllegalAccessException {
                logger.debug("{} preparing test", descriptionWithContext.getDisplayName());
                context = contextProvider.get();
                context.prepare();
                if (context.hasApplicationContext()) {
                    AnnotationConfigApplicationContext subApplicationContext = new AnnotationConfigApplicationContext();
                    subApplicationContext.register(mainClass);
                    subApplicationContext.setParent(context.getApplicationContext());
                    subApplicationContext.refresh();
                    mainInstance = subApplicationContext.getBean(mainClass);
                } else {
                    mainInstance = mainClass.newInstance();
                }

                before.stream().forEach(this::executeSupportMethod);
            }

            private <T> T executeSupportMethod(Method method) {
                logger.debug("{} execute support method = {}", descriptionWithContext.getDisplayName(), method.getName());
                try {
                    return executeMethod(checkNotNull(mainInstance, "test class instance is null"), method);
                } catch (Exception ex) {
                    throw runtime(inner(ex), "error executing support method = %s", method.getName());
                }
            }

            private void cleanupSafe() {
                try {
                    cleanupTestInstance();
                } catch (Exception ex) {
                    logger.warn("error during test object cleanup for test = {}", descriptionWithContext.getDisplayName(), ex);
                }
                try {
                    if (context != null) {
                        context.cleanup();
                        context = null;
                    }
                } catch (Exception ex) {
                    logger.warn("error during test context cleanup for test = {}", descriptionWithContext.getDisplayName(), ex);
                }
            }

            private void cleanupTestInstance() {
                logger.debug("{} test cleanup", descriptionWithContext.getDisplayName());
                if (mainInstance != null) {
                    after.stream().forEach(this::executeSupportMethod);
                }
                mainInstance = null;
            }

        }

    }

}

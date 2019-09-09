/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.scanner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.transform;
import com.google.common.collect.Ordering;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.quick.QuickClasspathSource;
import org.cmdbuild.utils.quick.QuickContextBindings;
import org.cmdbuild.utils.quick.QuickEventListener;
import static org.cmdbuild.utils.quick.QuickItemLifecycleEvent.QL_POSTCONSTRUCT;
import static org.cmdbuild.utils.quick.QuickItemLifecycleEvent.QL_PREDESTROY;
import static org.cmdbuild.utils.quick.scanner.QuickItemBindingType.BT_MULTIPLE;
import static org.cmdbuild.utils.quick.scanner.QuickItemBindingType.BT_SINGLE;
import org.cmdbuild.utils.quick.utils.QuickUtils;
import static org.cmdbuild.utils.quick.utils.QuickUtils.getConstructorsWithStandardOrder;
import static org.cmdbuild.utils.quick.utils.QuickUtils.getQualifiersForClass;
import static org.cmdbuild.utils.quick.utils.QuickUtils.getQualifiersForParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import static org.cmdbuild.utils.quick.utils.QuickUtils.QUALIFIER_PRIMARY;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class QuickClasspathScanner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final QuickClasspathSource classpathSource;

    private final Map<String, ClasspathItem> items = map();

    private QuickClasspathScanner(QuickClasspathSource classpathSource) {
        this.classpathSource = checkNotNull(classpathSource);
    }

    public static QuickContextBindings scanClasspathAndBuildContextBindings(QuickClasspathSource classpathSource) {
        return new QuickClasspathScanner(classpathSource).scanClasspathAndBuildContextBindings();
    }

    private QuickContextBindings scanClasspathAndBuildContextBindings() {
        logger.debug("retrieve classes from classpath source");
        Collection<Class> classes = classpathSource.getClasses();
        logger.debug("processing {} classes", classes.size());
        classes.stream().filter(QuickUtils::shouldLoadItem).forEach(this::loadClass);
        items.values().forEach(ClasspathItem::loadConstructorParamBindings);
        return buildContextBindings();
    }

    private void loadClass(Class classe) {
        logger.debug("loading class = {}", classe.getName());
        ClasspathItem item = new ClasspathItem(classe);
        items.put(item.getItemId(), item);
    }

    private QuickContextBindings buildContextBindings() {
        logger.debug("build context bindings and loading order");
        List<ClasspathItem> listOfItems = list();
        Set<String> loadedItems = set();
        List<ClasspathItem> itemsToProcess = list(items.values());
        while (!itemsToProcess.isEmpty()) {
            List<ClasspathItem> loadableItems = itemsToProcess.stream().filter(i -> loadedItems.containsAll(i.getBindings())).collect(toList());
            checkArgument(!loadableItems.isEmpty(), "error: unable to find loadable items: unreasolvable dependency or dependency loop");
            itemsToProcess.removeAll(loadableItems);
            loadableItems.stream().sorted(Ordering.natural().onResultOf(ClasspathItem::getItemId)).forEach(i -> {
                listOfItems.add(i);
                loadedItems.add(i.getItemId());
            });
        }
        QuickContextBindingsImpl res = new QuickContextBindingsImpl(transform(listOfItems, ClasspathItem::toQuickItemConfig));
        logger.debug("build context bindings = {}", res);
        return res;
    }

    private class ClasspathItem {

        private final Class classe;
        private final String itemId;
        private final int constructorIndex;
        private final Constructor constructor;
        private final Set<String> qualifiers;
        private final List<QuickItemBindingConfig> constructorParams = list();
        private final Set<String> bindings = set();
        private final List<QuickLifecycleTrigger> triggers = list();
        private final List<QuickEventListener> eventListeners;

        public ClasspathItem(Class classe) {
            this.classe = classe;
            this.qualifiers = getQualifiersForClass(classe);

            this.itemId = (classe.getName() + "_" + bindings.stream().sorted().collect(joining("_"))).replaceFirst("_$", "");

            List<Constructor> constructors = getConstructorsWithStandardOrder(classe);
            if (constructors.size() == 1) {
                constructor = getOnlyElement(constructors);
                constructorIndex = 0;
            } else {
                Pair<Integer, Constructor> pair = IntStream.of(constructors.size()).mapToObj(i -> Pair.of(i, constructors.get(i))).filter(p
                        -> p.getValue().isAnnotationPresent(Autowired.class) || p.getValue().isAnnotationPresent(Inject.class)
                ).collect(onlyElement("unable to find valid autowire constructor for class = %s", classe));
                constructor = pair.getValue();
                constructorIndex = pair.getKey();
            }

            list(classe.getMethods()).stream().filter(m -> m.isAnnotationPresent(PostConstruct.class)).forEach(m -> triggers.add(new QuickLifecycleTriggerImpl(m.getName(), QL_POSTCONSTRUCT)));
            list(classe.getMethods()).stream().filter(m -> m.isAnnotationPresent(PreDestroy.class)).forEach(m -> triggers.add(new QuickLifecycleTriggerImpl(m.getName(), QL_PREDESTROY)));
            eventListeners = list(classe.getMethods()).stream().map(QuickUtils::getEventListenersForMethod).flatMap(Collection::stream).collect(toImmutableList());
        }

        public void loadConstructorParamBindings() {
            logger.debug("loading constructor bindings for class = {}", classe.getName());
            list(constructor.getParameters()).forEach(p -> {

                Class type;
                Set<String> paramQualifiers = getQualifiersForParameter(p);

                QuickItemBindingType bindingType;
                if (Collection.class.isAssignableFrom(p.getType())) {
                    type = (Class) ((ParameterizedType) p.getParameterizedType()).getActualTypeArguments()[0];
                    bindingType = BT_MULTIPLE;
                } else {
                    type = p.getType();
                    bindingType = BT_SINGLE;
                }

                List<ClasspathItem> candidateItems = items.values().stream().filter(c -> c.isValidCandidate(type, paramQualifiers)).collect(toImmutableList());
                switch (bindingType) {
                    case BT_SINGLE:
                        ClasspathItem bindItem;
                        if (candidateItems.size() == 1) {
                            bindItem = getOnlyElement(candidateItems);
                        } else {
                            bindItem = candidateItems.stream().filter(ClasspathItem::isPrimary).collect(onlyElement("unable to find single valid binding for type = %s qualifiers = %s (found %s : %s)", type.getName(), paramQualifiers, candidateItems.size(), candidateItems));
                        }
                        bindings.add(bindItem.getItemId());
                        constructorParams.add(new SingleBindingConfig(bindItem.getItemId()));
                        break;
                    case BT_MULTIPLE:
                        constructorParams.add(new MultipleBindingConfig(transform(candidateItems, ClasspathItem::getItemId)));
                        bindings.addAll(transform(candidateItems, ClasspathItem::getItemId));
                        break;
                    default:
                        throw new IllegalArgumentException("unsupported binding type = " + bindingType);
                }
            });
            checkArgument(constructorParams.size() == constructor.getParameterCount());
        }

        public Set<String> getBindings() {
            return bindings;
        }

        public Set<String> getQualifiers() {
            return qualifiers;
        }

        public Class getClasse() {
            return classe;
        }

        public String getItemId() {
            return itemId;
        }

        public boolean isValidCandidate(Class type, Set<String> qualifiers) {
            return type.isAssignableFrom(classe) && this.qualifiers.containsAll(qualifiers);
        }

        public QuickItemConfig toQuickItemConfig() {
            return new QuickItemConfigImpl(itemId, classe.getName(), constructorIndex, constructorParams, triggers, eventListeners);
        }

        public boolean isPrimary() {
            return getQualifiers().contains(QUALIFIER_PRIMARY);
        }
    }

    private static class SingleBindingConfig implements QuickItemBindingConfig {

        private final String itemId;

        public SingleBindingConfig(String itemId) {
            this.itemId = checkNotBlank(itemId);
        }

        @Override
        public QuickItemBindingType getType() {
            return BT_SINGLE;
        }

        @Override
        public String getSingleItemId() {
            return itemId;
        }

        @Override
        public List<String> getMultipleItemId() {
            throw new IllegalArgumentException();
        }

    }

    private static class MultipleBindingConfig implements QuickItemBindingConfig {

        private final List<String> items;

        public MultipleBindingConfig(List<String> items) {
            this.items = ImmutableList.copyOf(items);
        }

        @Override
        public QuickItemBindingType getType() {
            return BT_MULTIPLE;
        }

        @Override
        public String getSingleItemId() {
            throw new IllegalArgumentException();
        }

        @Override
        public List<String> getMultipleItemId() {
            return items;
        }

    }

    private static class QuickItemConfigImpl implements QuickItemConfig {

        private final String itemId, className;
        private final int constructorIndex;
        private final List<QuickItemBindingConfig> constructorBindings;
        private final List<QuickLifecycleTrigger> triggers;
        private final List<QuickEventListener> eventListeners;

        public QuickItemConfigImpl(String itemId, String className, int constructorIndex, List<QuickItemBindingConfig> constructorBindings, List<QuickLifecycleTrigger> triggers, List<QuickEventListener> eventListeners) {
            this.itemId = checkNotBlank(itemId);
            this.className = checkNotBlank(className);
            this.constructorIndex = constructorIndex;
            this.constructorBindings = ImmutableList.copyOf(constructorBindings);
            this.triggers = ImmutableList.copyOf(triggers);
            this.eventListeners = ImmutableList.copyOf(eventListeners);
        }

        @Override
        public String getItemId() {
            return itemId;
        }

        @Override
        public String getClassName() {
            return className;
        }

        @Override
        public int getConstructorIndex() {
            return constructorIndex;
        }

        @Override
        public List<QuickItemBindingConfig> getConstructorBindings() {
            return constructorBindings;
        }

        @Override
        public List<QuickLifecycleTrigger> getTriggers() {
            return triggers;
        }

        @Override
        public List<QuickEventListener> getEventListeners() {
            return eventListeners;
        }

        @Override
        public String toString() {
            return "QuickItemConfigImpl{" + "itemId=" + itemId + ", className=" + className + '}';
        }

    }

    private static class QuickContextBindingsImpl implements QuickContextBindings {

        private final List<QuickItemConfig> items;

        public QuickContextBindingsImpl(List<QuickItemConfig> items) {
            this.items = ImmutableList.copyOf(items);
        }

        @Override
        public List<QuickItemConfig> getItemConfigsInLoadingOrder() {
            return items;
        }

    }
}

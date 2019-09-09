/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.loader;

import org.cmdbuild.utils.quick.scanner.QuickItemConfig;
import org.cmdbuild.utils.quick.scanner.QuickItemBindingConfig;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmReflectionUtils.executeMethod;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.cmdbuild.utils.quick.QuickApplicationContext;
import org.cmdbuild.utils.quick.QuickContextBindings;
import org.cmdbuild.utils.quick.QuickException;
import static org.cmdbuild.utils.quick.QuickItemLifecycleEvent.QL_POSTCONSTRUCT;
import static org.cmdbuild.utils.quick.QuickItemLifecycleEvent.QL_PREDESTROY;
import static org.cmdbuild.utils.quick.utils.QuickUtils.EVENT_LISTENER_SPRING_CONTEXT_REFRESH;
import static org.cmdbuild.utils.quick.utils.QuickUtils.getConstructorsWithStandardOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class QuickApplicationContextLoader {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, QuickItemInstance> loadedItemsById = map();

    public static QuickApplicationContext loadApplicationContext(QuickContextBindings bindings) {
        return new QuickApplicationContextLoader().doLoadApplicationContext(bindings);
    }

    private QuickApplicationContext doLoadApplicationContext(QuickContextBindings bindings) {
        logger.debug("loading application context {} items", bindings.getItemConfigsInLoadingOrder().size());
        bindings.getItemConfigsInLoadingOrder().forEach(this::loadItem);
        QuickApplicationContextImpl applicationContext = new QuickApplicationContextImpl(loadedItemsById, bindings);
        applicationContext.getItems().values().forEach((i) -> {
            i.getConfig().getEventListeners().forEach((e) -> {
                if (e.isOfType(EVENT_LISTENER_SPRING_CONTEXT_REFRESH)) {
                    executeMethod(i.getInstance(), e.getMethodName(), (Object) null);
                }
            });
        });
        logger.debug("application context ready");
        return applicationContext;
    }

    private void loadItem(QuickItemConfig config) {
        logger.debug("load item = {}", config);
        try {
            Class classe = Class.forName(config.getClassName());
            Constructor constructor = getConstructorsWithStandardOrder(classe).get(config.getConstructorIndex());
            checkArgument(constructor.getParameterCount() == config.getConstructorBindings().size());
            List constructorArgs = list();
            for (int i = 0; i < constructor.getParameterCount(); i++) {
                constructorArgs.add(loadConstructorBinding(config.getConstructorBindings().get(i), constructor.getParameterTypes()[i]));
            }
            Object newInstance = constructor.newInstance(constructorArgs.toArray(new Object[]{}));
            config.getTriggers().stream().filter(t -> t.matchesEvent(QL_POSTCONSTRUCT)).forEach(rethrowConsumer(t -> {
                executeMethod(newInstance, t.getMethodName());
            }));
            QuickItemInstance item = new QuickItemInstance(config, newInstance);
            loadedItemsById.put(item.getItemId(), item);
        } catch (Exception ex) {
            throw new QuickException(ex, "error loading item = %s", config);
        }
    }

    private <T> T loadConstructorBinding(QuickItemBindingConfig config, Class<T> paramType) {
        switch (config.getType()) {
            case BT_SINGLE:
                return paramType.cast(getLoadedItemById(config.getSingleItemId()).getInstance());
            case BT_MULTIPLE:
                return paramType.cast(config.getMultipleItemId().stream().map(this::getLoadedItemById).map(QuickItemInstance::getInstance).collect(toImmutableList()));//TODO check this, convert collection type
            default:
                throw new IllegalArgumentException("unsupported binding type = " + config.getType());
        }
    }

    private QuickItemInstance getLoadedItemById(String itemId) {
        return checkNotNull(loadedItemsById.get(itemId), "item not found for id = %s", itemId);
    }

    private class QuickItemInstance {

        private final QuickItemConfig config;
        private final Object instance;

        public QuickItemInstance(QuickItemConfig config, Object instance) {
            this.config = checkNotNull(config);
            this.instance = checkNotNull(instance);
        }

        public String getItemId() {
            return config.getItemId();
        }

        public Object getInstance() {
            return instance;
        }

        public QuickItemConfig getConfig() {
            return config;
        }

        public boolean implementsInterface(Class iface) {
            return iface.isInstance(instance);
        }

    }

    private static class QuickApplicationContextImpl implements QuickApplicationContext {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Map<String, QuickItemInstance> items;
        private final QuickContextBindings config;

        public QuickApplicationContextImpl(Map<String, QuickItemInstance> items, QuickContextBindings config) {
            this.items = ImmutableMap.copyOf(items);
            this.config = checkNotNull(config);
        }

        @Override
        public void destroy() {
            Lists.reverse(config.getItemConfigsInLoadingOrder()).stream().forEach(this::destroyItem);
        }

        private QuickItemInstance getItem(String key) {
            return checkNotNull(items.get(key), "item not found for key = %s", key);
        }

        private void destroyItem(QuickItemConfig itemConfig) {
            if (itemConfig.getTriggers().stream().anyMatch(t -> t.matchesEvent(QL_PREDESTROY))) {
                QuickItemInstance item = getItem(itemConfig.getItemId());
                itemConfig.getTriggers().stream().filter(t -> t.matchesEvent(QL_PREDESTROY)).forEach(t -> {
                    executeMethod(item, t.getMethodName());
                });
            }
        }

        @Override
        public <T> T getItem(Class<T> type) {
            return type.cast(items.values().stream().filter(i -> i.implementsInterface(type)).map(QuickItemInstance::getInstance).collect(onlyElement("unable to find only element for type = %s", type)));
        }

        public Map<String, QuickItemInstance> getItems() {
            return items;
        }

    }

}

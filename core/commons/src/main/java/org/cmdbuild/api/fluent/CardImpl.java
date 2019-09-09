package org.cmdbuild.api.fluent;

import static java.util.Collections.emptyMap;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CardImpl extends CardDescriptorImpl implements Card {

    private final Map<String, Object> attributes;

    public CardImpl(String className, Long id, Map<String, Object> attributes) {
        super(className, id);
        this.attributes = new HashMap<>(attributes);
    }

    public CardImpl(String className, Long id) {
        this(className, id, emptyMap());
    }

    @Override
    public boolean hasAttribute(final String name) {
        return attributes.containsKey(name);
    }

    @Override
    public Set<String> getAttributeNames() {
        return unmodifiableSet(attributes.keySet());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return unmodifiableMap(attributes);
    }

    @Override
    public Object get(String name) {
        return attributes.get(name);
    }

    @Override
    public void set(String name, Object value) {
        attributes.put(name, value);
    }

}

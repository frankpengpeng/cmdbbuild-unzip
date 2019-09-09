/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.MoreCollectors;
import com.google.common.collect.Ordering;
import com.google.common.collect.Streams;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;

public class CmCollectionUtils {

    public static <T> Stream<T> stream(Object obj) {
        checkNotNull(obj);
        if (obj instanceof Iterable) {
            return Streams.stream((Iterable) obj);
        } else if (obj.getClass().isArray()) {
            return convert(obj, List.class).stream();//TODO improve performance
        } else {
            throw unsupported("unsupported conversion of value = %s (%s) to stream", obj, getClassOfNullable(obj));
        }
    }

    public static <K1, K2, V> Map<K2, V> transformKeys(Map<K1, V> map, Function<K1, K2> fun) {
        return map.entrySet().stream().collect(toMap(e -> fun.apply(e.getKey()), Entry::getValue));
    }

    public static <T, A, R> Collector<T, A, R> onlyElement() {
        return (Collector) MoreCollectors.onlyElement();
    }

    public static <T, A, R> Collector<T, A, R> onlyElement(String message) {
        return onlyElement(message, new Object[]{});
    }

    public static <T, A, R> Collector<T, A, R> onlyElement(String message, Object... args) {
        return (Collector) Collector.of(CollectorHelper::new, CollectorHelper::add, CollectorHelper::combine, (c) -> checkNotNull(c.getOptional().orElse(null), message, args), Collector.Characteristics.UNORDERED);
    }

    public static boolean isNullOrEmpty(@Nullable Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean hasContent(@Nullable Collection collection) {
        return !isNullOrEmpty(collection);
    }

    public static boolean isNullOrEmpty(@Nullable Map map) {
        return map == null || map.isEmpty();
    }

    public static <X, T extends Collection<X>> T nullToEmpty(@Nullable T collection) {
        if (collection == null) {
            return (T) emptyList();//TODO: will break if T is, for example, Set
        } else {
            return collection;
        }
    }

    @Nullable
    public static <X, T extends Collection<X>> T emptyToNull(@Nullable T collection) {
        if (isNullOrEmpty(collection)) {
            return null;
        } else {
            return collection;
        }
    }

    public static <X, T extends Set<X>> T nullToEmpty(T set) {
        if (set == null) {
            return (T) emptySet();
        } else {
            return set;
        }
    }

    /**
     * cast to list; avoid making a copy if possible
     */
    public static <T> List<T> toList(Iterable<T> iterable) {
        return (List) ((iterable instanceof List) ? ((List) iterable) : list(iterable));
    }

    @Nullable
    public static <T> List<T> toListOrNull(@Nullable Iterable<T> iterable) {
        return iterable == null ? null : toList(iterable);
    }

    @Nullable
    public static <T> List<T> toListOrNull(@Nullable T[] arr) {
        return arr == null ? null : list(arr);
    }

    /**
     * cast to collection; avoid making a copy if possible
     */
    public static <T> Collection<T> toCollection(Iterable<T> iterable) {
        return (iterable instanceof Collection) ? ((Collection) iterable) : (Collection) list(iterable);
    }

    /**
     * always return a copy
     */
    public static <T> FluentList<T> list(Iterable<T> iterable) {
        return new FluentListImpl<T>().with(iterable);
    }

    public static <T> FluentList<T> listOf(Class<T> classe) {
        return new FluentListImpl<>();
    }

    public static <T> FluentList<T> list(Iterator<T> iterator) {
        return new FluentListImpl<T>().with(iterator);
    }

    public static <T> FluentList<T> list(Stream<T> stream) {
        return new FluentListImpl<T>().accept((l) -> stream.forEach(l::add));
    }

    public static <T> FluentList<T> list(T... items) {
        return new FluentListImpl<T>().with(items);
    }

    public static <T> FluentList<T> list(Enumeration<T> enumeration) {
        return list(Collections.list(enumeration));
    }

    public static <T> Queue<T> queue() {
        return new ConcurrentLinkedQueue<>();
    }

    public static <T> Queue<T> queue(Iterable<T> iterable) {
        Queue<T> queue = queue();
        Iterables.addAll(queue, iterable);
        return queue;
    }

    public static <T> Queue<T> queue(T... items) {
        return queue(asList(items));
    }

    public static <T> FluentList<T> list() {
        return new FluentListImpl<>();
    }

    public static <T> FluentSet<T> set(Iterable<T> iterable) {
        return new FluentSetImpl<T>().with(iterable);
    }

    public static <T> FluentSet<T> setFromNullable(@Nullable Iterable<T> iterable) {
        return new FluentSetImpl<T>().withNullable(iterable);
    }

    public static <T> FluentSet<T> set(T... items) {
        return new FluentSetImpl<T>().with(items);
    }

    public static <T> FluentSet<T> set() {
        return new FluentSetImpl<>();
    }

    public interface FluentList<T> extends List<T> {

        FluentList<T> with(T entry);

        FluentList<T> with(Iterable<T> entries);

        FluentList<T> with(Iterator<T> entries);

        default FluentList<T> with(Stream<T> stream) {
            stream.forEach(this::add);
            return this;
        }

        default FluentList<T> add(Iterable<T> entries) {
            return this.with(entries);
        }

        FluentList<T> with(T... items);

        FluentList<T> without(Predicate<T> predicate);

        default FluentList<T> add(T... items) {
            return this.with(items);
        }

        default FluentList<T> accept(Consumer<FluentList<T>> consumer) {
            consumer.accept(this);
            return this;
        }

        default T addAndReturn(T item) {
            add(item);
            return item;
        }

        default <O> FluentList<O> map(Function<T, O> mapper) {
            return list(this.stream().map(mapper).collect(Collectors.toList()));
        }

        List<T> immutable();

        default List<T> immutableCopy() {
            return ImmutableList.copyOf(this);
        }
    }

    public interface FluentSet<T> extends Set<T> {

        FluentSet<T> with(T entry);

        FluentSet<T> with(Iterable<T> entries);

        default FluentSet<T> withNullable(@Nullable Iterable<T> entries) {
            if (entries != null) {
                with(entries);
            }
            return this;
        }

        FluentSet<T> with(T... items);

        FluentSet<T> without(T entry);

        FluentSet<T> without(Iterable<T> entries);

        default FluentSet<T> without(Predicate<T> filter) {
            this.removeIf(filter);
            return this;
        }

        default FluentSet<T> withOnly(Collection<T> entries) {
            this.retainAll(entries);
            return this;
        }

        FluentSet<T> without(T... items);

        default FluentSet<T> sorted() {
            return this.sorted((Comparator) Ordering.natural());
        }

        FluentSet<T> sorted(Comparator<T> comparator);

        Set<T> immutable();

        default FluentSet<T> accept(Visitor<FluentSet<T>> visitor) {
            visitor.visit(this);
            return this;
        }
    }

    private static class FluentListImpl<T> extends ArrayList<T> implements FluentList<T> {

        public FluentListImpl() {
        }

        @Override
        public FluentList<T> with(Iterable<T> entries) {
            Iterables.addAll(this, entries);
            return this;
        }

        @Override
        public FluentList<T> with(Iterator<T> entries) {
            Iterators.addAll(this, entries);
            return this;
        }

        @Override
        public FluentList<T> with(T entry) {
            add(entry);
            return this;
        }

        @Override
        public FluentList<T> with(T... items) {
            return this.with(asList(items));
        }

        @Override
        public FluentList<T> without(Predicate<T> predicate) {
            removeIf(predicate);
            return this;
        }

        @Override
        public List<T> immutable() {
            return Collections.unmodifiableList(this);
        }

    }

    private static class FluentSetImpl<T> extends LinkedHashSet<T> implements FluentSet<T> {

        public FluentSetImpl() {
        }

        public FluentSetImpl(Collection<? extends T> c) {
            super(c);
        }

        @Override
        public FluentSet<T> with(T entry) {
            add(entry);
            return this;
        }

        @Override
        public FluentSet<T> with(Iterable<T> entries) {
            Iterables.addAll(this, entries);
            return this;
        }

        @Override
        public FluentSet<T> with(T... items) {
            return this.with(asList(items));
        }

        @Override
        public FluentSet<T> without(T entry) {
            remove(entry);
            return this;
        }

        @Override
        public FluentSet<T> without(Iterable<T> entries) {
            entries.forEach((e) -> remove(e));
            return this;
        }

        @Override
        public FluentSet<T> without(T... items) {
            return this.without(asList(items));
        }

        @Override
        public FluentSet<T> sorted(Comparator<T> comparator) {
            List<T> content = list(this);
            content.sort(comparator);
            clear();
            addAll(content);
            return this;
        }

        @Override
        public Set<T> immutable() {
            return Collections.unmodifiableSet(this);
        }

    }

    private static final class CollectorHelper {

        static final int MAX_EXTRAS = 4;

        @Nullable
        Object element;
        @Nullable
        List<Object> extras;

        CollectorHelper() {
            element = null;
            extras = null;
        }

        IllegalArgumentException multiples(boolean overflow) {
            StringBuilder sb
                    = new StringBuilder().append("expected one element but was: <").append(element);
            for (Object o : extras) {
                sb.append(", ").append(o);
            }
            if (overflow) {
                sb.append(", ...");
            }
            sb.append('>');
            throw new IllegalArgumentException(sb.toString());
        }

        void add(Object o) {
            checkNotNull(o);
            if (element == null) {
                this.element = o;
            } else if (extras == null) {
                extras = new ArrayList<>(MAX_EXTRAS);
                extras.add(o);
            } else if (extras.size() < MAX_EXTRAS) {
                extras.add(o);
            } else {
                throw multiples(true);
            }
        }

        CollectorHelper combine(CollectorHelper other) {
            if (element == null) {
                return other;
            } else if (other.element == null) {
                return this;
            } else {
                if (extras == null) {
                    extras = new ArrayList<>();
                }
                extras.add(other.element);
                if (other.extras != null) {
                    this.extras.addAll(other.extras);
                }
                if (extras.size() > MAX_EXTRAS) {
                    extras.subList(MAX_EXTRAS, extras.size()).clear();
                    throw multiples(true);
                }
                return this;
            }
        }

        Optional<Object> getOptional() {
            if (extras == null) {
                return Optional.ofNullable(element);
            } else {
                throw multiples(false);
            }
        }

        Object getElement() {
            if (element == null) {
                throw new NoSuchElementException();
            } else if (extras == null) {
                return element;
            } else {
                throw multiples(false);
            }
        }
    }

}

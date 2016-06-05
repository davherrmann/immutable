package de.davherrmann.immutable;

import static com.google.common.base.Joiner.on;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.newArrayList;
import static de.davherrmann.immutable.Compare.areEqual;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableMap;

public class NextImmutable
{
    public static Map<String, Object> IMMUTABLE_NODE = ImmutableMap.<String, Object>builder() //
        .put("immutableNode", true) //
        .build();
    public static Entry<String, Object> IMMUTABLE_NODE_ENTRY = getOnlyElement(IMMUTABLE_NODE.entrySet());

    public Object getInPath(Map<String, Object> dataStructure, List<String> path)
    {
        final Object nestedValue = dataStructure.get(path.get(0));

        if (path.size() == 1 || nestedValue == null)
        {
            return Copy.defensiveCopyOf(nestedValue);
        }

        return getInPath(dataStructure(nestedValue), path.subList(1, path.size()));
    }

    public Map<String, Object> updateIn(Map<String, Object> dataStructure, List<String> path,
        Function<Object, Object> updater)
    {
        return setIn(dataStructure, path, updater.apply(getInPath(dataStructure, path)));
    }

    public Map<String, Object> setIn(final Map<String, Object> dataStructure, final List<String> path, Object value)
    {
        return merge(dataStructure, changeForSinglePath(path, value));
    }

    public Map<String, Object> merge(final Map<String, Object> dataStructure, final Map<String, Object> changes)
    {
        return ImmutableMap.<String, Object>builder() //
            .putAll(Stream.of(dataStructure, changes, IMMUTABLE_NODE) //
                .map(Map::entrySet) //
                .flatMap(Collection::stream) //
                .collect(toMap( //
                    Entry::getKey, //
                    e -> Copy.defensiveCopyOf(e.getValue()), //
                    (oldValue, newValue) -> isDataStructure(oldValue) && isDataStructure(newValue)
                        ? merge(dataStructure(oldValue), dataStructure(newValue))
                        : newValue))) //
            .build();
    }

    public Map<String, Object> diff(Map<String, Object> dataStructure0, Map<String, Object> dataStructure1)
    {
        return ImmutableMap.<String, Object>builder() //
            .putAll(Stream.of(dataStructure0, dataStructure1, IMMUTABLE_NODE) //
                .map(Map::entrySet) //
                .flatMap(Collection::stream) //
                .filter(e -> IMMUTABLE_NODE_ENTRY.equals(e) //
                    || !areEqual(dataStructure0.get(e.getKey()), dataStructure1.get(e.getKey()))) //
                .collect(toMap( //
                    Entry::getKey, //
                    e -> {
                        final String key = e.getKey();
                        final Object oldValue = dataStructure0.get(key);
                        final Object newValue = dataStructure1.get(key);
                        return newValue == null
                            ? empty()
                            : isDataStructure(oldValue) && isDataStructure(newValue)
                                ? diff(dataStructure(oldValue), dataStructure(newValue))
                                : newValue;
                    }, //
                    (oldValue, newValue) -> newValue))) //
            .build();
    }

    public void visitNodes(final Map<String, Object> dataStructure, final NodeVisitor nodeVisitor)
    {
        visitNodes(dataStructure, newArrayList(), nodeVisitor);
    }

    private void visitNodes(final Map<?, ?> values, final List<String> path, final NodeVisitor nodeVisitor)
    {
        values.entrySet().stream() //
            .filter(e -> !IMMUTABLE_NODE_ENTRY.equals(e)) //
            .forEach(e -> {
                final List<String> nestedPath = newArrayList(path);
                nestedPath.add(e.getKey().toString());

                nodeVisitor.visit(on(".").join(nestedPath), e.getValue());

                if (e.getValue() instanceof Map)
                {
                    visitNodes((Map<?, ?>) e.getValue(), nestedPath, nodeVisitor);
                }
            });
    }

    private Map<String, Object> changeForSinglePath(final List<String> path, final Object value)
    {
        // TODO test first!
        /*if (path.size() < 1)
        {
            throw new IllegalArgumentException("path must have a size greater than one");
        }*/

        if (path.size() == 1)
        {
            return ImmutableMap.<String, Object>builder() //
                .put(IMMUTABLE_NODE_ENTRY) //
                .put(path.get(0), value) //
                .build();
        }

        return ImmutableMap.<String, Object>builder() //
            .put(IMMUTABLE_NODE_ENTRY) //
            .put(path.get(0), changeForSinglePath(path.subList(1, path.size()), value)) //
            .build();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> dataStructure(Object value)
    {
        return (Map<String, Object>) value;
    }

    private boolean isDataStructure(Object value)
    {
        return value != null //
            && Map.class.isAssignableFrom(value.getClass()) //
            && ((Map<?, ?>) value).get("immutableNode") != null;
    }
}

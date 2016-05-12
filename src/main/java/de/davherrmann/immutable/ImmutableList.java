package de.davherrmann.immutable;

import java.util.List;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(ImmutableListTypeAdapter.class)
public class ImmutableList<I>
{
    private final List<I> values;

    public ImmutableList()
    {
        this(com.google.common.collect.ImmutableList.of());
    }

    private ImmutableList(List<I> initialValues)
    {
        this.values = initialValues;
    }

    // TODO add(Immutable<I> immutableItem) & addAll(ImmutableList<I> immutableItems)?

    public ImmutableList<I> add(I item)
    {
        return new ImmutableList<>(com.google.common.collect.ImmutableList.<I>builder() //
            .addAll(values) //
            .add(item) //
            .build());
    }

    public ImmutableList<I> addAll(List<I> items)
    {
        return new ImmutableList<>(com.google.common.collect.ImmutableList.<I>builder() //
            .addAll(values) //
            .addAll(items) //
            .build());
    }

    public List<I> asList()
    {
        return values;
    }

    public int size()
    {
        return values.size();
    }
}

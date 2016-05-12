package de.davherrmann.immutable;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ImmutableListTest
{
    private final ImmutableList<String> immutableList = new ImmutableList<>();
    @Test
    public void asList_returnsEmptyList_whenCalledInitially() throws Exception
    {
        // when / then
        assertThat(immutableList.asList(), is(empty()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void asList_returnsImmutableList() throws Exception
    {
        // when
        immutableList.asList().add("CANNOT ADD THIS TO AN IMMUTABLE LIST");
    }

    @Test
    public void add_doesNotChangeImmutableList() throws Exception
    {
        // given / when
        immutableList.add("foo");

        // then
        assertThat(immutableList.asList(), is(empty()));
    }

    @Test
    public void add_returnsNewImmutableList_withAddedItem() throws Exception
    {
        // given / when
        final ImmutableList<String> newImmutableList = immutableList.add("foo");

        // then
        assertThat(newImmutableList.asList(), is(newArrayList("foo")));
    }

    @Test
    public void addAll_returnsNewImmutableList_withAddedItems() throws Exception
    {
        // given / when
        final ImmutableList<String> newImmutableList = immutableList.addAll(newArrayList("foo", "bar"));

        // then
        assertThat(newImmutableList.asList(), is(newArrayList("foo", "bar")));
    }

    @Test
    public void size_returnsSize() throws Exception
    {
        // given / when
        final ImmutableList<String> newImmutableList = immutableList.addAll(newArrayList("foo", "bar", "baz"));

        // then
        assertThat(newImmutableList.size(), is(newImmutableList.asList().size()));
    }
}
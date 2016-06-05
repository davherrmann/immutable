package de.davherrmann.immutable;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Optional.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class NextImmutableTest
{
    private final NextImmutable nextImmutable = new NextImmutable();
    private final ImmutableMap<String, Object> immutableDataStructure = ImmutableMap.<String, Object>builder() //
        .put("A", "AFoo") //
        .put("B", "BFoo") //
        .put("C", ImmutableMap.<String, Object>builder() //
            .put("D", "DFoo") //
            .put("E", "EFoo") //
            .build()) //
        .put("F", newHashMap()).build();

    @Test
    public void mergeWithImmutables() throws Exception
    {
        // given / when
        final Map<String, Object> newImmutableDataStructure = nextImmutable.merge(immutableDataStructure,
            change("A", "ABar"));

        // then
        assertThat(immutableDataStructure.get("A"), is("AFoo"));
        assertThat(newImmutableDataStructure.get("A"), is("ABar"));
    }

    @Test
    public void merge_stillHasUnchangedData() throws Exception
    {
        // given / when
        final Map<String, Object> newImmutableDataStructure = nextImmutable.merge(immutableDataStructure,
            change("A", "ABar"));

        // then
        assertThat(newImmutableDataStructure.get("B"), is("BFoo"));
    }

    @Test
    public void merge_putsNewKeyValuePairIntoMap() throws Exception
    {
        // given / when
        final Map<String, Object> newImmutableDataStructure = nextImmutable.merge(immutableDataStructure,
            change("X", "XBar"));

        // then
        assertThat(newImmutableDataStructure.get("X"), is("XBar"));
    }

    @Test
    public void merge_changesNestedEntry() throws Exception
    {
        // given / when
        final Map<String, Object> newImmutableDataStructure = nextImmutable.merge(immutableDataStructure,
            change("C", change("D", "DBar")));

        // then
        assertThat(nextImmutable.getInPath(newImmutableDataStructure, newArrayList("C", "D")), is("DBar"));
    }

    @Test
    public void merge_stillHasNestedUnchangedData() throws Exception
    {
        // given / when
        final Map<String, Object> newImmutableDataStructure = nextImmutable.merge(immutableDataStructure,
            change("C", change("D", "DBar")));

        // then
        assertThat(nextImmutable.getInPath(newImmutableDataStructure, newArrayList("C", "E")), is("EFoo"));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void merge_returnsImmutableDataStructure() throws Exception
    {
        // given / when
        final Map<String, Object> newImmutableDataStructure = nextImmutable.merge(immutableDataStructure,
            change("A", "ABar"));

        // then
        newImmutableDataStructure.put("A", "ABaz");
    }

    @Test(expected = UnsupportedOperationException.class)
    @SuppressWarnings("unchecked")
    public void merge_returnsNestedImmutableDataStructure() throws Exception
    {
        // given / when
        final Map<String, Object> newImmutableDataStructure = nextImmutable.merge(immutableDataStructure,
            change("C", change("D", "DBar")));

        // then
        ((Map<String, Object>) newImmutableDataStructure.get("C")).put("D", "DBar");
    }

    @Test
    public void merge_canHandleMapsAsValues() throws Exception
    {
        // given / when
        final Map<String, Object> newImmutableDataStructure = nextImmutable.merge(immutableDataStructure,
            change("F", "FFoo"));

        // then
        assertThat(newImmutableDataStructure.get("F"), is("FFoo"));
    }

    @Test
    public void setIn_worksAsSimpleMerge() throws Exception
    {
        // given / when
        final Map<String, Object> newImmutableDataStructure = nextImmutable.setIn(immutableDataStructure,
            newArrayList("C", "D"), "DBar");

        // then
        assertThat(nextImmutable.getInPath(newImmutableDataStructure, newArrayList("C", "D")), is("DBar"));
    }

    @Test
    public void updateIn_worksAsSimpleMerge() throws Exception
    {
        // given / when
        final Map<String, Object> newImmutableDataStructure = nextImmutable.updateIn(immutableDataStructure,
            newArrayList("C", "D"), value -> value + "Bar");

        // then
        assertThat(nextImmutable.getInPath(newImmutableDataStructure, newArrayList("C", "D")), is("DFooBar"));
    }

    @Test
    public void getInPath_worksWithNonExistingPath() throws Exception
    {
        // given / when
        final Object notExisting = nextImmutable.getInPath(immutableDataStructure, newArrayList("NON", "EXISTING"));

        // then
        assertNull(notExisting);
    }

    @Test
    public void diff_worksWithNonNestedChanges() throws Exception
    {
        // given
        final Map<String, Object> immutableDataStructure1 = nextImmutable.setIn(immutableDataStructure,
            newArrayList("B"), "BBar");
        final Map<String, Object> immutableDataStructure2 = nextImmutable.setIn(immutableDataStructure1,
            newArrayList("A"), "ABar");

        // when
        final Map<String, Object> diff = nextImmutable.diff(immutableDataStructure, immutableDataStructure2);

        // then
        assertThat(diff, is(ImmutableMap.builder().put("A", "ABar").put("B", "BBar").build()));
    }

    @Test
    public void diff_worksWithNestedChanges() throws Exception
    {
        // given
        final Map<String, Object> newImmutableDataStructure = nextImmutable.setIn(immutableDataStructure,
            newArrayList("C", "D"), "DBar");

        // when
        final Map<String, Object> diff = nextImmutable.diff(immutableDataStructure, newImmutableDataStructure);

        // then
        assertThat(diff, //
            is(ImmutableMap.builder() //
                .put("C", ImmutableMap.builder() //
                    .put("D", "DBar") //
                    .build()) //
                .build()));
    }

    @Test
    public void diff_worksWithNewNestedPath() throws Exception
    {
        // given
        final Map<String, Object> newImmutableDataStructure = nextImmutable.setIn(immutableDataStructure,
            newArrayList("S", "T"), "TBar");

        // when
        final Map<String, Object> diff = nextImmutable.diff(immutableDataStructure, newImmutableDataStructure);

        // then
        assertThat(diff, //
            is(ImmutableMap.builder() //
                .put("S", ImmutableMap.builder() //
                    .put("T", "TBar") //
                    .build()) //
                .build()));
    }

    @Test
    public void diff_worksWithSecondDataStructureHavingLessElements() throws Exception
    {
        // given
        final Map<String, Object> newImmutableDataStructure = nextImmutable.setIn(immutableDataStructure,
            newArrayList("S", "T"), "TBar");

        // when
        final Map<String, Object> diff = nextImmutable.diff(newImmutableDataStructure, immutableDataStructure);

        // then
        assertThat(diff, //
            is(ImmutableMap.builder() //
                .put("S", empty()) //
                .build()));
    }

    // TODO do we need this method in NextImmutable?
    private Map<String, Object> change(String key, Object value)
    {
        return ImmutableMap.<String, Object>builder() //
            .put(key, value) //
            .build();
    }
}

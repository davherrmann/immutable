package de.davherrmann.immutable;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ImmutableTest
{
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final Immutable<POJO> immutable = new Immutable<>(POJO.class);
    private final POJO path = immutable.path();
    private final POJO pojo = immutable.asObject();

    @Ignore("some room for ideas")
    @Test
    public void someRoomForIdeas() throws Exception
    {
        immutable //
            .in(path::title).set("Test") //
            .in(path::wantToClose).set(true) //

            .in(path.pojo()::wantToClose).update(wantToClose -> !wantToClose) //
            .in(path::title).update(title -> title + "!");

        immutable.in(path::currentPage).update(page -> page + 1);

        // TODO offer a map set function?
        // immutable.in(path.myMap(), "key").set("value");

        // TODO offer a merge function?

        // TODO set and update without in?
        // immutable.set(path::currentPage, 0);
        // immutable.update(path::currentPage, page -> page + 1);
        // immutable.setList(path::list, list);
        // immutable.updateList(path::list, list -> list.add(""));

        fail("please add @ignore and write another test - this is just some room for ideas!");
    }

    @Test
    public void asObject_returnsObject() throws Exception
    {
        // given / then
        assertNotNull(pojo);
    }

    @Test
    public void get_returnsDefaultBooleanValue() throws Exception
    {
        // given / then
        assertThat(immutable.asObject().wantToClose(), is(false));
    }

    @Test
    public void set_changesBooleanValue() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path::wantToClose).set(true);

        // then
        assertThat(newImmutable.asObject().wantToClose(), is(true));
    }

    @Test
    public void update_changesBooleanValue() throws Exception
    {
        // given /  when
        final Immutable<POJO> newImmutable = immutable.in(path::wantToClose).update(value -> !value);

        // then
        assertThat(newImmutable.asObject().wantToClose(), is(true));
    }

    @Test
    public void set_doesNotChangeCurrentObject() throws Exception
    {
        // given / when
        immutable.in(path::wantToClose).set(true);

        // then
        assertThat(pojo.wantToClose(), is(false));
    }

    @Test
    public void set_returnsNewImmutable2() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path::wantToClose).set(false);

        // then
        assertThat(newImmutable, is(not(immutable)));
    }

    @Test
    public void set_returnsImmutable2_withSamePath() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path::wantToClose).set(false);

        // then
        assertThat(immutable.path(), is(newImmutable.path()));
    }

    @Test
    public void set_inNestedObject_changesBooleanValue() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.pojo()::wantToClose).set(true);

        // then
        assertThat(newImmutable.asObject().wantToClose(), is(false));
        assertThat(newImmutable.asObject().pojo().wantToClose(), is(true));
    }

    @Test
    public void equals_returnsTrue_forEqualImmutables() throws Exception
    {
        // given / then
        assertThat(new Immutable<>(POJO.class), is(new Immutable<>(POJO.class)));
    }

    @Test
    public void diff_returnsChanges() throws Exception
    {
        // given
        final Immutable<POJO> newImmutable = immutable.in(path.pojo()::wantToClose).set(true);

        // TODO should you be able to pass a PathRecorder into constructor?
        // TODO -> one "unnecessary" variable: initialDiffImmutable
        final Immutable<POJO> initialDiffImmutable = new Immutable<>(POJO.class);
        final POJO diffPath = initialDiffImmutable.path();
        final Immutable<POJO> diffImmutable = initialDiffImmutable.in(diffPath.pojo()::wantToClose).set(true);

        // when
        final Immutable<POJO> diff = immutable.diff(newImmutable);

        // then
        assertThat(diff, is(diffImmutable));
    }

    @Test
    public void changeWithOwnUnusedPath_usingPathFromOtherImmutableInSameThread_changesImmutable() throws Exception
    {
        // when
        final Immutable<POJO> newImmutable = new Immutable<>(POJO.class).in(path::wantToClose).set(true);

        // then
        assertThat(newImmutable.get(path::wantToClose), is(true));
    }

    @Test
    public void clear_returnsEmptyState() throws Exception
    {
        // when
        final Immutable<POJO> clearedImmutable = immutable  //
            .in(path::wantToClose).set(true) //
            .clear();

        // then
        assertThat(clearedImmutable.values().isEmpty(), is(true));
    }

    @Test
    public void setIn_overwriteCustomType() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable //
            .in(path.name()::firstname).set("Foo") //
            .in(path.name()::lastname).set("Bar") //
            .in(path::name).set(name("F", "B").asObject());

        // then
        assertThat(newImmutable.asObject().name().firstname(), is("F"));
        assertThat(newImmutable.asObject().name().lastname(), is("B"));
    }

    @Test
    public void setIn_inCustomTypeObject() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable  //
            .in(path::name).set(name("F", "B").asObject()) //
            .in(path.name()::firstname).set("Foo");

        // then
        assertThat(newImmutable.asObject().name().firstname(), is("Foo"));
        assertThat(newImmutable.asObject().name().lastname(), is("B"));
    }

    // TODO still relevant?
    /*@Test
    public void setIn_setsInCorrectPath_whenPathIsUsedInAnotherThread() throws Exception
    {
        // given
        path.name().firstname();
        final Thread thread = new Thread(() -> path.name().lastname());
        thread.start();
        thread.join();

        // when
        final Immutable<POJO> newImmutable = immutable.in("NOT USING PATH").set("A");

        // then
        assertThat(newImmutable.asObject().name().firstname(), is("A"));
        assertThat(newImmutable.asObject().name().lastname(), is(nullValue()));
    }*/

    @Test
    public void get_returnsCorrectList_whenImmutableListWasSet() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.inList(path::titles).set(new ImmutableList<String>() //
            .add("foo").add("bar"));

        // then
        assertThat(newImmutable.asObject().titles(), is(newArrayList("foo", "bar")));
    }

    @Test
    public void get_returnsCorrectList_whenListWasSet() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.inList(path::titles).set(newArrayList("foo", "bar"));

        // then
        assertThat(newImmutable.asObject().titles(), is(newArrayList("foo", "bar")));
    }

    @Test
    public void get_returnsUpdatedList_whenListWasUpdated() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.inList(path::titles).updateList(
            list -> newArrayList("foo", "bar"));

        // then
        assertThat(newImmutable.asObject().titles(), is(newArrayList("foo", "bar")));
    }

    @Test
    public void get_returnsUpdatedList_whenImmutableListWasUpdated() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.inList(path::titles).update(list -> list.add("foo").add("bar"));

        // then
        assertThat(newImmutable.asObject().titles(), is(newArrayList("foo", "bar")));
    }

    @Test
    public void get_returnsClonedArray_whenMutableArrayWasSet() throws Exception
    {
        // given
        final Immutable<POJO> newImmutable = immutable.in(path::titleArray).set(new String[]{"foo", "bar"});

        // when
        newImmutable.asObject().titleArray()[1] = "baz";

        // then
        assertThat(newImmutable.asObject().titleArray(), is(new String[]{"foo", "bar"}));
    }

    @Test
    public void changingASetMutableArray_doesNotChangeImmutable() throws Exception
    {
        // given
        final String[] array = {"foo", "bar"};
        final Immutable<POJO> newImmutable = immutable.in(path::titleArray).set(array);

        // when
        array[1] = "baz";

        // then
        assertThat(newImmutable.asObject().titleArray(), is(new String[]{"foo", "bar"}));
    }

    @Test
    public void diff_ofImmutablesWithEqualStringSet_shouldBeEmpty() throws Exception
    {
        // given
        final Immutable<POJO> immutable1 = immutable.in(path::title).set("foo");
        final Immutable<POJO> immutable2 = immutable.in(path::title).set("foo");

        // when
        final Immutable<POJO> diff = immutable1.diff(immutable2);

        // then
        assertThat(diff.values(), is(newHashMap()));
    }

    @Test
    public void diff_ofImmutablesWithEqualArrays_shouldBeEmpty() throws Exception
    {
        // given
        final Immutable<POJO> immutable1 = immutable.in(path::titleArray).set(new String[]{"foo", "bar"});
        final Immutable<POJO> immutable2 = immutable.in(path::titleArray).set(new String[]{"foo", "bar"});

        // when
        final Immutable<POJO> diff = immutable1.diff(immutable2);

        // then
        assertThat(diff.values(), is(newHashMap()));
    }

    @Test
    public void setIn_worksWithPathMapping() throws Exception
    {
        // given / when
        final Immutable<POJO> initialisedImmutable = immutable //
            .in(path -> path.pojo()::title) //
            .set("Foo");

        // then
        assertThat(initialisedImmutable.asObject().pojo().title(), is("Foo"));
    }

    @Test
    public void setInList_worksWithPathMapping() throws Exception
    {
        // given / when
        final Immutable<POJO> initialisedImmutable = immutable //
            .inList(path -> path::titles) //
            .set(newArrayList("Foo"));

        // then
        assertThat(initialisedImmutable.asObject().titles(), is(newArrayList("Foo")));
    }

    @Test
    public void get_returnsSetValue() throws Exception
    {
        // given
        final Immutable<POJO> newImmutable = immutable.in(p -> p::title).set("Foo");

        // when / then
        assertThat(newImmutable.get(path::title), is("Foo"));
    }

    @Test
    public void get_returnsSetValue_withPathMapping() throws Exception
    {
        // given
        final Immutable<POJO> newImmutable = immutable.inList(p -> p::titles).update(l -> l.add("Foo"));

        // when / then
        assertThat(newImmutable.get(path -> path::titles), is(newArrayList("Foo")));
    }

    @Test
    public void merge_combinesTwoImmutables() throws Exception
    {
        // given
        final Immutable<POJO> immutable0 = immutable.in(path::wantToClose).set(true);
        final Immutable<POJO> immutable1 = immutable.in(path.pojo()::title).set("Foo");

        // when
        final Immutable<POJO> mergedImmutable = immutable0.merge(immutable1);

        // then
        final Immutable<POJO> manuallyMergedImmutable = immutable0.in(path.pojo()::title).set("Foo");
        assertEquals(manuallyMergedImmutable, mergedImmutable);
    }

    @Test
    public void type_returnsCorrectType() throws Exception
    {
        // when / then
        assertThat(immutable.type(), equalTo(POJO.class));
    }

    private Immutable<POJO.Name> name(String firstname, String lastname)
    {
        return new Immutable<>(POJO.Name.class) //
            .in(p -> p::firstname).set(firstname) //
            .in(p -> p::lastname).set(lastname);
    }

    private interface POJO
    {
        String title();

        List<String> titles();

        String[] titleArray();

        boolean wantToClose();

        int currentPage();

        // TODO add map support
        Map<String, String> myMap();

        POJO pojo();

        Name name();

        interface Name
        {
            String firstname();

            String lastname();
        }
    }
}
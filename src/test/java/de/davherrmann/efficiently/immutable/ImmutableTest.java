package de.davherrmann.efficiently.immutable;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

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

    @Test
    public void immutableWorks() throws Exception
    {
        immutable //
            .in(path.title()).set("Test") //
            .in(path.wantToClose()).set(true) //

            .in(path.pojo().wantToClose()).update(wantToClose -> !wantToClose) //
            .in(path.title()).update(title -> title + "!");

        immutable.in(path.currentPage()).update(page -> page + 1);

        // TODO offer a map set function?
        // immutable.in(path.myMap(), "key").set("value");

        // TODO offer a merge function?

        // then
        final POJO changedPOJO = immutable.asObject();
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
        final Immutable<POJO> newImmutable = immutable.in(path.wantToClose()).set(true);

        // then
        assertThat(newImmutable.asObject().wantToClose(), is(true));
    }

    @Test
    public void update_changesBooleanValue() throws Exception
    {
        // given /  when
        final Immutable<POJO> newImmutable = immutable.in(path.wantToClose()).update(value -> !value);

        // then
        assertThat(newImmutable.asObject().wantToClose(), is(true));
    }

    @Test
    public void set_doesNotChangeCurrentObject() throws Exception
    {
        // given / when
        immutable.in(path.wantToClose()).set(true);

        // then
        assertThat(pojo.wantToClose(), is(false));
    }

    @Test
    public void set_returnsNewImmutable2() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.wantToClose()).set(false);

        // then
        assertThat(newImmutable, is(not(immutable)));
    }

    @Test
    public void set_returnsImmutable2_withSamePath() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.wantToClose()).set(false);

        // then
        assertThat(immutable.path(), is(newImmutable.path()));
    }

    @Test
    public void set_inNestedObject_changesBooleanValue() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.pojo().wantToClose()).set(true);

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
        final Immutable<POJO> newImmutable = immutable.in(path.pojo().wantToClose()).set(true);

        // TODO should you be able to pass a PathRecorder into constructor?
        // TODO -> one "unnecessary" variable: initialDiffImmutable
        final Immutable<POJO> initialDiffImmutable = new Immutable<>(POJO.class);
        final POJO diffPath = initialDiffImmutable.path();
        final Immutable<POJO> diffImmutable = initialDiffImmutable.in(diffPath.pojo().wantToClose()).set(true);

        // when
        final Immutable<POJO> diff = immutable.diff(newImmutable);

        // then
        assertThat(diff, is(diffImmutable));
    }

    @Test
    public void changeWithOwnUnusedPath_usingWrongPath_throwsMeaningfulException() throws Exception
    {
        // then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No path was recorded. Did you use the correct Immutable#path()?");

        // when
        new Immutable<>(POJO.class).in(path.wantToClose()).set(true);
    }

    @Test
    public void clear_returnsEmptyState() throws Exception
    {
        // when
        final Immutable<POJO> clearedImmutable = immutable.clear();

        // then
        assertThat(immutable.values().isEmpty(), is(true));
    }

    @Test
    public void setIn_overwriteCustomType() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable //
            .in(path.name().firstname()).set("Foo") //
            .in(path.name().lastname()).set("Bar") //
            .in(path.name()).set(name("F", "B").asObject());

        // then
        assertThat(newImmutable.asObject().name().firstname(), is("F"));
        assertThat(newImmutable.asObject().name().lastname(), is("B"));
    }

    @Test
    public void setIn_inCustomTypeObject() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable  //
            .in(path.name()).set(name("F", "B").asObject()) //
            .in(path.name().firstname()).set("Foo");

        // then
        assertThat(newImmutable.asObject().name().firstname(), is("Foo"));
        assertThat(newImmutable.asObject().name().lastname(), is("B"));
    }

    @Test
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
    }

    @Test
    public void get_returnsCorrectList_whenImmutableListWasSet() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.titles()).set(new ImmutableList<String>() //
            .add("foo").add("bar"));

        // then
        assertThat(newImmutable.asObject().titles(), is(newArrayList("foo", "bar")));
    }

    @Test
    public void get_returnsCorrectList_whenListWasSet() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.titles()).set(newArrayList("foo", "bar"));

        // then
        assertThat(newImmutable.asObject().titles(), is(newArrayList("foo", "bar")));
    }

    @Test
    public void get_returnsUpdatedList_whenListWasUpdated() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.titles()).updateList(list -> newArrayList("foo", "bar"));

        // then
        assertThat(newImmutable.asObject().titles(), is(newArrayList("foo", "bar")));
    }

    @Test
    public void get_returnsUpdatedList_whenImmutableListWasUpdated() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path.titles()).update(list -> list.add("foo").add("bar"));

        // then
        assertThat(newImmutable.asObject().titles(), is(newArrayList("foo", "bar")));
    }

    private Immutable<POJO.Name> name(String firstname, String lastname)
    {
        final Immutable<POJO.Name> immutable = new Immutable<>(POJO.Name.class);
        final POJO.Name path = immutable.path();
        return immutable //
            .in(path.firstname()).set(firstname) //
            .in(path.lastname()).set(lastname);
    }

    private interface POJO
    {
        String title();

        List<String> titles();

        boolean wantToClose();

        int currentPage();

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
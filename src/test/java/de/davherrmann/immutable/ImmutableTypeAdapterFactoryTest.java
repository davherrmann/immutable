package de.davherrmann.immutable;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ImmutableTypeAdapterFactoryTest
{
    private final Immutable<POJO> immutable = new Immutable<>(POJO.class);
    private final POJO path = immutable.path();
    private final Gson gson = new GsonBuilder() //
        .registerTypeAdapterFactory(new ImmutableTypeAdapterFactory()) //
        .create();

    @Test
    public void write_withEmptyImmutable_writesEmptyObject() throws Exception
    {
        // when / then
        assertThat(gson.toJson(immutable),
            is("{\"type\":\"de.davherrmann.immutable.ImmutableTypeAdapterFactoryTest$POJO\",\"data\":{}}"));
    }

    @Test
    public void write_setInCustomType_works() throws Exception
    {
        // when
        final Immutable<POJO> newImmutable = immutable //
            .in(path.name()::firstname).set("Foo");

        // then
        assertThat(gson.toJson(newImmutable),
            is("{\"type\":\"de.davherrmann.immutable.ImmutableTypeAdapterFactoryTest$POJO\",\"data\":{\"name\":{\"firstname\":\"Foo\"}}}"));
    }

    @Test
    public void write_setCustomTypeObject_works() throws Exception
    {
        // when
        final Immutable<POJO> newImmutable = immutable //
            .in(path::name).set(name("Foo", "Bar"));

        // then
        assertThat(gson.toJson(newImmutable),
            is("{\"type\":\"de.davherrmann.immutable.ImmutableTypeAdapterFactoryTest$POJO\",\"data\":{\"name\":{\"firstname\":\"Foo\",\"lastname\":\"Bar\"}}}"));
    }

    @Test
    public void write_setInCustomType_withPreviousCustomTypeObject_works() throws Exception
    {
        // when
        final Immutable<POJO> newImmutable = immutable //
            .in(path::name).set(name("Foo", "Bar")) //
            .in(path.name()::lastname).set("B");

        // then
        assertThat(gson.toJson(newImmutable),
            is("{\"type\":\"de.davherrmann.immutable.ImmutableTypeAdapterFactoryTest$POJO\",\"data\":{\"name\":{\"firstname\":\"Foo\",\"lastname\":\"B\"}}}"));
    }

    @Test
    public void write_setCustomTypeList_works() throws Exception
    {
        // when
        final Immutable<POJO> newImmutable = immutable  //
            .inList(path::names).set(newArrayList( //
                name("A", "AFoo").asObject(), //
                name("B", "BFoo").asObject()));

        // then
        assertThat(gson.toJson(newImmutable),
            is("{\"type\":\"de.davherrmann.immutable.ImmutableTypeAdapterFactoryTest$POJO\",\"data\":{\"names\":[{\"firstname\":\"A\",\"lastname\":\"AFoo\"},{\"firstname\":\"B\",\"lastname\":\"BFoo\"}]}}"));
    }

    private Immutable<POJO.Name> name(String firstname, String lastname)
    {
        final Immutable<POJO.Name> immutable = new Immutable<>(POJO.Name.class);
        final POJO.Name path = immutable.path();

        return immutable //
            .in(path::firstname).set(firstname) //
            .in(path::lastname).set(lastname);
    }

    private interface POJO
    {
        String title();

        boolean wantToClose();

        int currentPage();

        Map<String, String> myMap();

        POJO pojo();

        List<Name> names();

        Name name();

        interface Name
        {
            String firstname();

            String lastname();
        }
    }
}
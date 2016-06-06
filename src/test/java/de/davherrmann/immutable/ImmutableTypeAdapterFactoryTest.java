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
        assertThat(gson.fromJson(gson.toJson(immutable), Immutable.class).values(), is(immutable.values()));
    }

    @Test
    public void write_setInCustomType_works() throws Exception
    {
        // when
        final Immutable<POJO> newImmutable = immutable //
            .in(path.name()::firstname).set("Foo");

        // then
        assertThat(gson.fromJson(gson.toJson(newImmutable), Immutable.class).values(), is(newImmutable.values()));
    }

    @Test
    public void write_setCustomTypeObject_works() throws Exception
    {
        // when
        final Immutable<POJO> newImmutable = immutable //
            .in(path::name).set(name("Foo", "Bar"));

        // then
        assertThat(gson.fromJson(gson.toJson(newImmutable), Immutable.class).values(), is(newImmutable.values()));
    }

    @Test
    public void write_setInCustomType_withPreviousCustomTypeObject_works() throws Exception
    {
        // when
        final Immutable<POJO> newImmutable = immutable //
            .in(path::name).set(name("Foo", "Bar")) //
            .in(path.name()::lastname).set("B");

        // then
        assertThat(gson.fromJson(gson.toJson(newImmutable), Immutable.class).values(), is(newImmutable.values()));
    }

    @Test
    public void write_setCustomTypeList_works() throws Exception
    {
        // when
        final Immutable<POJO> newImmutable = immutable  //
            .inList(path::names).set(newArrayList( //
                name("A", "AFoo").asObject(), //
                name("B", "BFoo").asObject()));
        System.out.println(gson.toJson(newImmutable));
        // then
        assertThat(gson.toJson(newImmutable),
            is("{\"type\":\"de.davherrmann.immutable.ImmutableTypeAdapterFactoryTest$POJO\",\"data\":{\"names\":[{\"firstname\":\"A\",\"immutableNode\":true,\"lastname\":\"AFoo\"},{\"firstname\":\"B\",\"immutableNode\":true,\"lastname\":\"BFoo\"}],\"immutableNode\":true}}"));
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
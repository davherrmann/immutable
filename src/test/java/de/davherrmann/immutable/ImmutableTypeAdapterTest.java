package de.davherrmann.immutable;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.google.gson.Gson;

public class ImmutableTypeAdapterTest
{
    private final Immutable<POJO> immutable = new Immutable<>(POJO.class);
    private final POJO path = immutable.path();

    @Test
    public void toJson_worksWithPlainGson() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path::pojo).set(immutable.asObject());

        // then
        assertThat(new Gson().fromJson(new Gson().toJson(newImmutable), Immutable.class), is(newImmutable));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void fromJson_worksWithPlainGson() throws Exception
    {
        // given / when
        final String json = "{\"type\":\"de.davherrmann.immutable.ImmutableTypeAdapterTest$POJO\",\"data\":{\"pojo\":{\"name\":\"Foo\"}}}";

        // then
        assertThat(((Immutable<POJO>) new Gson().fromJson(json, Immutable.class)).asObject().pojo().name(), is("Foo"));
    }

    private interface POJO
    {
        POJO pojo();

        String name();
    }
}
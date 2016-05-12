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
    public void wrappingImmutableTypeAdapterFactory_works() throws Exception
    {
        // given / when
        final Immutable<POJO> newImmutable = immutable.in(path::pojo).set(immutable.asObject());

        // then
        assertThat(new Gson().toJson(newImmutable), is("{\"pojo\":{}}"));
    }

    private interface POJO
    {
        POJO pojo();
    }
}
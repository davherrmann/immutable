package de.davherrmann.immutable;

import static org.junit.Assert.assertThat;

import org.hamcrest.core.Is;
import org.junit.Test;

import com.google.gson.Gson;

public class ImmutableListTypeAdapterTest
{
    @Test
    public void serialisingImmutableList_usingPlainGson_works() throws Exception
    {
        // given
        final ImmutableList<String> immutableList = new ImmutableList<String>().add("foo").add("bar");

        // when / then
        assertThat(new Gson().toJson(immutableList), Is.is("[\"foo\",\"bar\"]"));
    }
}
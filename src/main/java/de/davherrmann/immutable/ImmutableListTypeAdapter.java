package de.davherrmann.immutable;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ImmutableListTypeAdapter extends TypeAdapter<ImmutableList<?>>
{
    private final Gson gson = new GsonBuilder() //
        .registerTypeAdapterFactory(new ImmutableTypeAdapterFactory()) //
        .create();

    @Override
    public void write(JsonWriter out, ImmutableList<?> value) throws IOException
    {
        gson.getAdapter(List.class).write(out, value.asList());
    }

    @Override
    public ImmutableList<?> read(JsonReader in) throws IOException
    {
        // TODO implement gson read
        return null;
    }
}

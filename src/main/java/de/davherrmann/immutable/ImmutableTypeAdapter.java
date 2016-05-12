package de.davherrmann.immutable;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ImmutableTypeAdapter extends TypeAdapter<Immutable<?>>
{
    private final Gson gson = new GsonBuilder() //
        .registerTypeAdapterFactory(new ImmutableTypeAdapterFactory()) //
        .create();
    
    @Override
    public void write(JsonWriter out, Immutable<?> value) throws IOException
    {
        gson.getAdapter(Map.class).write(out, value.values());
    }

    @Override
    public Immutable<?> read(JsonReader in) throws IOException
    {
        return null;
    }
}

package de.davherrmann.immutable;

import java.io.IOException;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ImmutableTypeAdapterFactory implements TypeAdapterFactory
{
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type)
    {
        if (Immutable.ImmutableNode.class.isAssignableFrom(type.getRawType()))
        {
            return new TypeAdapter<T>()
            {
                @Override
                public void write(JsonWriter out, T value) throws IOException
                {
                    gson.getAdapter(Map.class).write(out, ((Immutable.ImmutableNode) value).values());
                }

                @Override
                public T read(JsonReader in) throws IOException
                {
                    return null;
                }
            };
        }

        return null;
    }
}

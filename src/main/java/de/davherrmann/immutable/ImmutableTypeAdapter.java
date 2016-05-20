package de.davherrmann.immutable;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class ImmutableTypeAdapter<T> extends TypeAdapter<Immutable<T>>
{
    private final Gson gson = new GsonBuilder() //
        .registerTypeAdapterFactory(new ImmutableTypeAdapterFactory()) //
        .create();

    private final static Logger log = org.slf4j.LoggerFactory.getLogger(ImmutableTypeAdapter.class);

    @Override
    public void write(JsonWriter out, Immutable<T> value) throws IOException
    {
        final ImmutableJSONWrapper jsonWrapper = new ImmutableJSONWrapper(value.type().getName(), value.values());
        gson.getAdapter(ImmutableJSONWrapper.class).write(out, jsonWrapper);
    }

    @Override
    public Immutable<T> read(JsonReader in) throws IOException
    {
        final ImmutableJSONWrapper jsonWrapper = gson.fromJson(in, ImmutableJSONWrapper.class);
        return new Immutable<>(classFor(jsonWrapper.fullQualifiedType()), jsonWrapper.data());
    }

    @SuppressWarnings("unchecked")
    private Class<T> classFor(final String fullQualifiedType)
    {
        try
        {
            return (Class<T>) Class.forName(fullQualifiedType);
        }
        catch (ClassNotFoundException e)
        {
            log.error("Could not find class " + fullQualifiedType + ". Please check the visibility of the class. "
                + "Returning Immutable<Object>, which could lead to future errors.", e);
            return (Class<T>) Object.class;
        }
    }

    private static class ImmutableJSONWrapper
    {
        private final String type;
        private final Map<String, Object> data;

        private ImmutableJSONWrapper(String type, Map<String, Object> data)
        {
            this.type = type;
            this.data = data;
        }

        public String fullQualifiedType()
        {
            return type;
        }

        public Map<String, Object> data()
        {
            return data;
        }
    }
}

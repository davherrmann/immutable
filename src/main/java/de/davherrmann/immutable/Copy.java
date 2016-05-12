package de.davherrmann.immutable;

import static java.util.Arrays.copyOf;

public class Copy
{
    private Copy()
    {
    }

    // TODO is defensive copying task of NextImmutable? Or the layer above?
    // TODO extend for other mutable types!
    // TODO extract
    @SuppressWarnings("unchecked")
    public static <T> T defensiveCopyOf(T value)
    {
        if (value == null)
        {
            return null;
        }

        if (value.getClass().isArray())
        {
            final Object[] array = (Object[]) value;
            return (T) copyOf(array, array.length);
        }

        return value;
    }
}

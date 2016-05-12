package de.davherrmann.immutable;

import java.util.Arrays;

public class Compare
{
    private Compare()
    {
    }

    public static <T> boolean areEqual(T arg1, T arg2)
    {
        if (arg1 == null || arg2 == null)
        {
            return arg1 == arg2;
        }
        else if (arg1 instanceof Object[] && arg2 instanceof Object[])
        {
            Object[] arr1 = (Object[]) arg1;
            Object[] arr2 = (Object[]) arg2;
            return Arrays.equals(arr1, arr2);
        }
        else
        {
            return arg1.equals(arg2);
        }
    }
}

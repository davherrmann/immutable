package de.davherrmann.immutable;

import static com.google.common.base.Defaults.defaultValue;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.validation.constraints.NotNull;

public class PathRecorder<I>
{
    private static ThreadLocal<Map<Class<?>, PathRecorder<?>>> pathRecorders = new ThreadLocal<Map<Class<?>, PathRecorder<?>>>()
    {
        @Override
        protected Map<Class<?>, PathRecorder<?>> initialValue()
        {
            return newHashMap();
        }
    };

    private final I path;

    private static ThreadLocal<PathInfo> lastPathInfo = new ThreadLocal<>();

    private PathRecorder(Class<I> type)
    {
        this.path = pathFor(type, emptyList());
    }

    public I path()
    {
        return path;
    }

    @NotNull
    public List<String> pathFor(Supplier<?> method)
    {
        return pathInfoFor(method).lastPath();
    }

    public Method methodFor(Supplier<?> method)
    {
        return pathInfoFor(method).lastMethod();
    }

    private PathInfo pathInfoFor(Supplier<?> method)
    {
        lastPathInfo.set(null);

        method.get();

        final PathInfo pathInfo = lastPathInfo.get();

        if (pathInfo == null)
        {
            throw new IllegalStateException("No path was recorded. Did you use the correct Immutable#path()?");
        }
        return pathInfo;
    }

    @SuppressWarnings("unchecked")
    private static <T> T pathFor(Class<T> type, List<String> nestedPath)
    {
        return (T) Proxy.newProxyInstance( //
            type.getClassLoader(), //
            new Class<?>[]{type}, //
            new PathInvocationHandler(nestedPath));
    }

    @SuppressWarnings("unchecked")
    public static <T> T pathInstanceFor(Class<T> type)
    {
        pathRecorders.get().putIfAbsent(type, new PathRecorder<>(type));
        return (T) pathRecorders.get().get(type).path();
    }
    @SuppressWarnings("unchecked")
    public static <T> PathRecorder<T> pathRecorderInstanceFor(Class<T> type)
    {
        pathRecorders.get().putIfAbsent(type, new PathRecorder<>(type));
        return (PathRecorder<T>) pathRecorders.get().get(type);
    }

    private static class PathInvocationHandler extends AbstractPathInvocationHandler
    {
        public PathInvocationHandler(final List<String> nestedPath)
        {
            super(nestedPath);
        }

        @Override
        protected Object handleInvocation(List<String> path, Method method) throws Throwable
        {
            final List<String> lastPath = pathWith(method);
            lastPathInfo.set(new PathInfo()
            {
                @Override
                public Method lastMethod()
                {
                    return method;
                }
                @Override
                public List<String> lastPath()
                {
                    return lastPath;
                }
            });

            final Class<?> returnType = method.getReturnType();
            final Object defaultValue = defaultValue(returnType);

            return defaultValue != null || !returnType.isInterface()
                ? defaultValue
                : pathFor(returnType, lastPathInfo.get().lastPath());
        }
    }

    private interface PathInfo
    {
        Method lastMethod();

        List<String> lastPath();
    }

}


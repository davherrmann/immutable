package de.davherrmann.immutable;

import java.lang.reflect.Method;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.AbstractInvocationHandler;

public abstract class AbstractPathInvocationHandler extends AbstractInvocationHandler
{
    private final List<String> path;

    public AbstractPathInvocationHandler(List<String> path)
    {
        this.path = path;
    }

    @Override
    protected Object handleInvocation(Object o, Method method, Object[] objects) throws Throwable
    {
        return handleInvocation(path, method);
    }

    protected List<String> pathWith(Method method)
    {
        return ImmutableList.<String>builder() //
            .addAll(path) //
            .add(method.getName()) //
            .build();
    }

    protected abstract Object handleInvocation(List<String> path, Method method) throws Throwable;
}

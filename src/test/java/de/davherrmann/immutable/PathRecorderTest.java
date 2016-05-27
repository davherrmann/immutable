package de.davherrmann.immutable;

import static com.google.common.collect.Lists.newArrayList;
import static de.davherrmann.immutable.PathRecorder.pathInstanceFor;
import static de.davherrmann.immutable.PathRecorder.pathRecorderInstanceFor;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PathRecorderTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final PathRecorder<POJO> pathRecorder = pathRecorderInstanceFor(POJO.class);
    private final POJO path = pathRecorder.path();

    @Ignore("some room for ideas")
    @Test
    public void someRoomForIdeas() throws Exception
    {
        // static methods return a ThreadLocal path/pathRecorder
        pathInstanceFor(POJO.class);
        // TODO PathRecorder.pathRecorderInstanceFor(POJO.class);

        fail("This is just some room for ideas. Please write your own test.");
    }

    @Test
    public void pathIsRecorded() throws Exception
    {
        // when / then
        assertThat(pathRecorder.pathFor(path::integer), is(newArrayList("integer")));
    }

    @Test
    public void pathIsRecorded_inNestedObject() throws Exception
    {
        // when / then
        assertThat(pathRecorder.pathFor(path.pojo().pojo()::integer), is(newArrayList("pojo", "pojo", "integer")));
    }

    @Test
    public void pathFor_returnsPath_forPassedSupplier() throws Exception
    {
        // when / then
        assertThat(pathRecorder.pathFor(path.pojo()::integer), is(newArrayList("pojo", "integer")));
    }

    @Test
    public void pathFor_throwsMeaningfulError_whenExtraneousSupplierIsPassed() throws Exception
    {
        // then
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No path was recorded. Did you use the correct Immutable#path()?");

        // when
        pathRecorder.pathFor(() -> "");

        fail();
    }

    @Test
    public void pathInstanceFor_isAlwaysTheSameInstanceInSameThread() throws Exception
    {
        // given
        final POJO path0 = pathInstanceFor(POJO.class);
        final POJO path1 = pathInstanceFor(POJO.class);

        // then
        assertThat(path0, is(path1));
    }

    @Test
    public void pathInstanceFor_isDifferentInDifferentThreads() throws Exception
    {
        // given
        final POJO path0 = pathInstanceFor(POJO.class);
        final POJO[] path1 = new POJO[1];

        final Thread thread = new Thread(() -> path1[0] = pathInstanceFor(POJO.class));
        thread.start();
        thread.join();

        // then
        assertThat(path0, is(not(path1)));
    }

    @Test
    public void pathRecorderInstanceFor_isAlwaysTheSameInstanceInSameThread() throws Exception
    {
        // given
        final PathRecorder<POJO> pathRecorder0 = pathRecorderInstanceFor(POJO.class);
        final PathRecorder<POJO> pathRecorder1 = pathRecorderInstanceFor(POJO.class);

        // then
        assertThat(pathRecorder0, is(pathRecorder1));
    }

    @Test
    public void pathRecorderInstanceFor_isDifferentInDifferentThreads() throws Exception
    {
        // given
        final PathRecorder<POJO> pathRecorder0 = pathRecorderInstanceFor(POJO.class);
        @SuppressWarnings("rawtypes")
        final PathRecorder[] pathRecorder1 = new PathRecorder[1];

        final Thread thread = new Thread(() -> pathRecorder1[0] = pathRecorderInstanceFor(POJO.class));
        thread.start();
        thread.join();

        // then
        assertThat(pathRecorder0, is(not(pathRecorder1)));
    }

    @Test
    public void methodFor_returnsMethod_forPassedSupplier() throws Exception
    {
        // given
        final Method method = POJO.class.getMethod("integer");

        // when / then
        assertThat(pathRecorder.methodFor(path.pojo()::integer), is(method));
    }

    private interface POJO
    {
        POJO pojo();

        int integer();
    }
}
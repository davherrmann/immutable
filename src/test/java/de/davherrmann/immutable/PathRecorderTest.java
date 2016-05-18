package de.davherrmann.immutable;

import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PathRecorderTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final PathRecorder<POJO> pathRecorder = new PathRecorder<>(POJO.class);
    private final POJO path = pathRecorder.path();

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

    private interface POJO
    {
        POJO pojo();

        int integer();
    }
}
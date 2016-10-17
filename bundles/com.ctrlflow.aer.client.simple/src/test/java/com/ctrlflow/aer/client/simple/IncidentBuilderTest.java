package com.ctrlflow.aer.client.simple;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.array;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.ctrlflow.aer.client.dto.Incident;
import com.ctrlflow.aer.client.dto.Status;
import com.ctrlflow.aer.client.dto.Throwable;

public class IncidentBuilderTest {

    /**
     * <q>A value of -2 indicates that the method containing the execution point is a native method.</q>
     * 
     * @see StackTraceElement#StackTraceElement(String, String, String, int)
     */
    private static int NATIVE = -2;

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildFromJavaException() {
        IllegalArgumentException e = mockException(new IllegalArgumentException("Exception message"),
                new StackTraceElement("org.example.Example", "method", "Example.java", 42),
                new StackTraceElement("org.example.Main", "main", "Main.java", 23));

        Incident incident = IncidentBuilder.from(e).build();

        Status status = incident.getStatus();
        assertThat(status.getMessage(), is(equalTo("Exception message")));

        Throwable exception = status.getException();
        assertThat(exception.getClassName(), is(equalTo("java.lang.IllegalArgumentException")));
        assertThat(exception.getMessage(), is(equalTo("Exception message")));
        assertThat(exception.getStackTrace(),
                is(array(
                        allOf(hasProperty("className", equalTo("org.example.Example")),
                                hasProperty("methodName", equalTo("method")),
                                hasProperty("fileName", equalTo("Example.java")),
                                hasProperty("lineNumber", equalTo(42)),
                                hasProperty("native", equalTo(false))),
                        allOf(hasProperty("className",
                                equalTo("org.example.Main")),
                                hasProperty("methodName", equalTo("main")),
                                hasProperty("fileName", equalTo("Main.java")),
                                hasProperty("lineNumber", equalTo(23)),
                                hasProperty("native", equalTo(false))))));
        assertThat(exception.getCause(), is(nullValue()));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildFromNestedJavaException() {
        IllegalArgumentException e = mockException(new IllegalArgumentException("Exception message"),
                new StackTraceElement("org.example.Example", "method", "Example.java", 42),
                new StackTraceElement("org.example.Job", "run", "Job.java", NATIVE));
        e.initCause(mockException(new NullPointerException(),
                new StackTraceElement("org.example.Example", "helper", "Example.java", 84)));

        Incident incident = IncidentBuilder.from(e).build();

        Status status = incident.getStatus();
        assertThat(status.getMessage(), is(equalTo("Exception message")));

        Throwable exception = status.getException();
        assertThat(exception.getClassName(), is(equalTo("java.lang.IllegalArgumentException")));
        assertThat(exception.getMessage(), is(equalTo("Exception message")));
        assertThat(exception.getStackTrace(),
                is(array(
                        allOf(hasProperty("className", equalTo("org.example.Example")),
                                hasProperty("methodName", equalTo("method")),
                                hasProperty("fileName", equalTo("Example.java")),
                                hasProperty("lineNumber", equalTo(42)),
                                hasProperty("native", equalTo(false))),
                        allOf(hasProperty("className",
                                equalTo("org.example.Job")),
                                hasProperty("methodName", equalTo("run")),
                                hasProperty("fileName", equalTo("Job.java")),
                                hasProperty("lineNumber", equalTo(NATIVE)),
                                hasProperty("native", equalTo(true))))));
        assertThat(exception.getCause().getStackTrace(),
                is(array(
                        allOf(hasProperty("className", equalTo("org.example.Example")),
                                hasProperty("methodName", equalTo("helper")),
                                hasProperty("fileName", equalTo("Example.java")),
                                hasProperty("lineNumber", equalTo(84)),
                                hasProperty("native", equalTo(false))))));
    }

    @Test
    public void testBuildWithLogMessageAndBundle() {
        NullPointerException e = mockException(new NullPointerException(),
                new StackTraceElement("org.example.Main", "main", "Main.java", 5));

        Incident incident = IncidentBuilder.from(e).
                withLogMessage("Log message").withLoggingBundle("org.example", "1.0.0").build();

        Status status = incident.getStatus();
        assertThat(status.getMessage(), is(equalTo("Log message")));
        assertThat(status.getPluginId(), is(equalTo("org.example")));
        assertThat(status.getPluginVersion(), is(equalTo("1.0.0")));

        assertThat(status.getException(), is(Matchers.notNullValue()));
    }

    private static <T extends java.lang.Throwable> T mockException(T e, StackTraceElement... elements) {
        e.setStackTrace(elements);
        return e;
    }
}

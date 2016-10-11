package com.ctrlflow.aer.client.logback;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.ctrlflow.aer.client.dto.Bundle;
import com.ctrlflow.aer.client.dto.Incident;
import com.ctrlflow.aer.client.dto.Throwable;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ClassPackagingData;

public class AerAppenderTest {

    private AerAppender sut;
    private Logger log;
    private ArgumentCaptor<Incident> captor;
    private LoggerContext context;

    @Before
    public void setUp() {
        context = new LoggerContext();
        context.setMaxCallerDataDepth(120);
        log = context.getLogger(Logger.ROOT_LOGGER_NAME);
        sut = spy(new AerAppender());
        Mockito.doNothing().when(sut).sendIncident(Mockito.any(Incident.class));
        sut.setContext(context);
        sut.start();
        log.addAppender(sut);

        captor = ArgumentCaptor.forClass(Incident.class);
    }

    @Test
    public void testLogInfo() {
        log.info("info");
        verify(sut).sendIncident(Mockito.any(Incident.class));
    }

    @Test
    public void testLogDebug() {
        log.info("debug");
        verify(sut).sendIncident(Mockito.any(Incident.class));
    }

    @Test
    public void testLogWarn() {
        log.info("warn");
        verify(sut).sendIncident(Mockito.any(Incident.class));
    }

    @Test
    public void testLogError() {
        log.info("error");
        verify(sut).sendIncident(Mockito.any(Incident.class));
    }

    @Test
    public void testArgumentsToIncidentStatusMessage() {
        log.error("Test error with {} and {}", "argument1", "argument2");
        verify(sut).sendIncident(captor.capture());
        assertThat(captor.getValue().getStatus().getMessage(), is("Test error with argument1 and argument2"));
    }

    @Test
    public void includeBundlesForCallerInformation() {
        log.info("error");
        verify(sut).sendIncident(captor.capture());
        Incident incident = captor.getValue();
        assertThat(incident.getPresentBundles(), not(empty()));
    }

    @Test
    public void testExceptionToIncidentStatusThrowable() {
        log.error("Error", new RuntimeException());
        verify(sut).sendIncident(captor.capture());
        Incident incident = captor.getValue();
        Throwable exception = incident.getStatus().getException();
        assertThat(exception.getClassName(), is("java.lang.RuntimeException"));
        assertThat(exception.getStackTrace()[0].getClassName(), containsString(AerAppenderTest.class.getName()));
        // requires LoggerContext.setPackagingDataEnabled(true)
        assertThat(incident.getPresentBundles(), not(empty()));
    }

    @Test
    public void testRememberDuplicates() {
        for (int i = 0; i < 2; i++) {
            log.info("test");
        }
        verify(sut).sendIncident(Mockito.any(Incident.class));
    }

    @Test
    public void testRememberDuplicates2() {
        for (int i = 0; i < 2; i++) {
            log.info("test");
        }
        log.warn("warn");
        verify(sut, times(2)).sendIncident(Mockito.any(Incident.class));
    }

    @Test
    public void testVersionRemovedFromBundleName() {
        ClassPackagingData classPackagingData = new ClassPackagingData("junit-4.11.jar", "4.11");
        Bundle bundle = sut.toBundle(classPackagingData);
        assertThat(bundle.getName(), is("junit"));
        assertThat(bundle.getVersion(), is("4.11"));
    }

    @Test
    public void testUnknownVersionDoesNotRemoveBundleName() {
        ClassPackagingData classPackagingData = new ClassPackagingData("banana.jar", "na");
        Bundle bundle = sut.toBundle(classPackagingData);
        assertThat(bundle.getName(), is("banana"));
        assertThat(bundle.getVersion(), is("na"));
    }

    @Test
    public void testFirstBundleIsIncludedInStatusPlugin() {
        log.error("Error", new RuntimeException());
        verify(sut).sendIncident(captor.capture());
        Incident incident = captor.getValue();
        Bundle firstBundle = incident.getPresentBundles().get(0);
        assertThat(incident.getStatus().getPluginId(), is(firstBundle.getName()));
        assertThat(incident.getStatus().getPluginVersion(), is(firstBundle.getVersion()));
    }

    @Test
    public void testProductIdAndVersionSetAsEclipseProductAndVersion() {
        sut.setProductId("theEclipseProductId");
        sut.setBuildId("theEclipseBuildId");
        log.error("Error", new RuntimeException());
        verify(sut).sendIncident(captor.capture());
        Incident incident = captor.getValue();
        assertThat(incident.getEclipseProduct(), is("theEclipseProductId"));
        assertThat(incident.getEclipseBuildId(), is("theEclipseBuildId"));
    }

    @After
    public void tearDown() {
        sut.stop();
    }
}

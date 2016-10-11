package com.ctrlflow.aer.client.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.ctrlflow.aer.client.dto.Bundle;
import com.ctrlflow.aer.client.dto.Incident;
import com.ctrlflow.aer.client.dto.StackTraceElement;
import com.ctrlflow.aer.client.dto.Status;
import com.ctrlflow.aer.client.dto.Throwable;

public class IncidentBuilder {

    private final Throwable exception;

    private UUID anonymousId;

    private String userName;

    private String userEmail;

    private String comment;

    private Bundle loggingBundle;

    private String logMessage;

    private int errorCode;

    private String eclipseProduct;

    private String eclipseBuildId;

    private String osgiArch;

    private String osgiOs;

    private String osgiOsVersion;

    private String osgiWs;

    private String javaRuntimeVersion;

    private Map<String, String> auxiliaryInformation;

    private List<Bundle> presentBundles;

    private IncidentBuilder(Throwable throwable) {
        this.exception = throwable;
    }

    public static IncidentBuilder from(Throwable exception) {
        Objects.requireNonNull(exception);

        IncidentBuilder builder = new IncidentBuilder(exception);
        return builder;
    }

    public static IncidentBuilder from(java.lang.Throwable javaException) {
        Objects.requireNonNull(javaException);

        IncidentBuilder builder = new IncidentBuilder(convert(javaException));
        return builder;
    }

    public IncidentBuilder withAnonymousId(UUID anonymousId) {
        this.anonymousId = anonymousId;
        return this;
    }

    public IncidentBuilder withUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public IncidentBuilder withUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public IncidentBuilder withComment(String comment) {
        this.comment = comment;
        return this;
    }

    public IncidentBuilder withLogMessage(String logMessage) {
        this.logMessage = logMessage;
        return this;
    }

    public IncidentBuilder withLoggingBundle(Bundle loggingBundle) {
        this.loggingBundle = loggingBundle;
        return this;
    }

    public IncidentBuilder withLoggingBundle(String symbolicName, String version) {
        Bundle loggingBundle = new Bundle(symbolicName, version);
        return withLoggingBundle(loggingBundle);
    }

    public IncidentBuilder withErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public IncidentBuilder withEclipseProduct(String eclipseProduct) {
        this.eclipseProduct = eclipseProduct;
        return this;
    }

    public IncidentBuilder withEclipseBuildId(String eclipseBuildId) {
        this.eclipseBuildId = eclipseBuildId;
        return this;
    }

    public IncidentBuilder withOsgiArch(String osgiArch) {
        this.osgiArch = osgiArch;
        return this;
    }

    public IncidentBuilder withOsgiOs(String osgiOs) {
        this.osgiOs = osgiOs;
        return this;
    }

    public IncidentBuilder withOsgiOsVersion(String osgiOsVersion) {
        this.osgiOsVersion = osgiOsVersion;
        return this;
    }

    public IncidentBuilder withOsgiWs(String osgiWs) {
        this.osgiWs = osgiWs;
        return this;
    }

    public IncidentBuilder withJavaRuntimeVersion(String javaRuntimeVersion) {
        this.javaRuntimeVersion = javaRuntimeVersion;
        return this;
    }

    public IncidentBuilder withPresentBundle(Bundle presentBundle) {
        if (presentBundles == null) {
            presentBundles = new ArrayList<>();
        }
        presentBundles.add(presentBundle);
        return this;
    }

    public IncidentBuilder withPresentBundle(String symbolicName, String version) {
        Bundle presentBundle = new Bundle(symbolicName, version);
        return withPresentBundle(presentBundle);
    }

    public IncidentBuilder withAuxiliaryInformation(String key, String value) {
        if (auxiliaryInformation == null) {
            auxiliaryInformation = new HashMap<>();
        }
        auxiliaryInformation.put(key, value);
        return this;
    }

    public Incident build() {
        Incident incident = new Incident();

        incident.setAnonymousId(anonymousId);
        incident.setName(userName);
        incident.setEmail(userEmail);
        incident.setComment(comment);

        incident.setEclipseProduct(eclipseProduct);
        incident.setEclipseBuildId(eclipseBuildId);

        incident.setOsgiArch(osgiArch);
        incident.setOsgiOs(osgiOs);
        incident.setOsgiOsVersion(osgiOsVersion);
        incident.setOsgiWs(osgiWs);

        incident.setJavaRuntimeVersion(javaRuntimeVersion);

        incident.setAuxiliaryInformation(auxiliaryInformation);

        Status status = new Status();
        status.setPluginId(loggingBundle != null ? loggingBundle.getName() : "unknown");
        status.setPluginVersion(loggingBundle != null ? loggingBundle.getVersion() : "unknown");
        status.setMessage(logMessage != null ? logMessage : exception.getMessage());
        status.setCode(errorCode);
        status.setException(exception);

        incident.setStatus(status);

        incident.setPresentBundles(presentBundles);

        return incident;
    }

    private static Throwable convert(java.lang.Throwable javaException) {
        Throwable exception = new Throwable();
        exception.setClassName(javaException.getClass().getName());
        exception.setMessage(javaException.getMessage());
        if (javaException.getCause() != null) {
            exception.setCause(convert(javaException.getCause()));
        }
        exception.setStackTrace(convert(javaException.getStackTrace()));
        return exception;
    }

    private static StackTraceElement[] convert(java.lang.StackTraceElement[] javaStackTrace) {
        StackTraceElement[] stackTrace = new StackTraceElement[javaStackTrace.length];
        for (int i = 0; i < stackTrace.length; i++) {
            stackTrace[i] = convert(javaStackTrace[i]);
        }
        return stackTrace;
    }

    private static StackTraceElement convert(java.lang.StackTraceElement javaStackTraceElement) {
        StackTraceElement stackTraceElement = new StackTraceElement();
        stackTraceElement.setClassName(javaStackTraceElement.getClassName());
        stackTraceElement.setFileName(javaStackTraceElement.getFileName());
        stackTraceElement.setLineNumber(javaStackTraceElement.getLineNumber());
        stackTraceElement.setMethodName(javaStackTraceElement.getMethodName());
        stackTraceElement.setNative(javaStackTraceElement.isNativeMethod());
        return stackTraceElement;
    }
}

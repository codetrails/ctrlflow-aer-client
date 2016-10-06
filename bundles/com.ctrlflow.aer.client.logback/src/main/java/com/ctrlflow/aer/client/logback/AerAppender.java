package com.ctrlflow.aer.client.logback;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.apache.http.client.HttpResponseException;

import com.ctrlflow.aer.client.dto.Bundle;
import com.ctrlflow.aer.client.dto.Incident;
import com.ctrlflow.aer.client.dto.StackTraceElement;
import com.ctrlflow.aer.client.dto.Status;
import com.ctrlflow.aer.client.dto.Throwable;
import com.ctrlflow.aer.client.logback.internal.IO;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.EvictingQueue;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ClassPackagingData;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.PackagingDataCalculator;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.status.WarnStatus;

/**
 * Appender which creates incidents for log events and sends them to a given url. It is recommended to attach the appender to an
 * {@link AsyncAppender}. If {@link LoggerContext#setPackagingDataEnabled(boolean)} is set to true, the appender will collect the
 * {@link ClassPackagingData} and include them as {@link Bundle} in the incident. In addition,
 * {@link LoggerContext#setMaxCallerDataDepth(int)} should be set to a value high enough to get the full stacktrace of a logging call.
 * 
 * @since 2.0.0
 */
public class AerAppender extends AppenderBase<ILoggingEvent> {

    private static final UUID HOST_ID = computeHostId();

    private static UUID computeHostId() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            return UUID.nameUUIDFromBytes(mac);
        } catch (Exception e) {
            return UUID.fromString("00000000-0000-0000-000-000000000000");
        }
    }

    private String url;
    private int historySize = 50;
    private Queue<Incident> history = EvictingQueue.create(50);

    private String productId = "undefined";
    private String buildId = "undefined";
    private String userName = getSystemProperty("user.name");
    private String email = userName + "@localhost";

    private final String osgiArch = Strings.nullToEmpty(getSystemProperty("os.arch"));
    private final String osgiOs = Strings.nullToEmpty(getSystemProperty("os.name"));
    private final String osgiOsVersion = Strings.nullToEmpty(getSystemProperty("os.version"));
    private final String javaRuntimeVersion = Strings.nullToEmpty(getSystemProperty("java.runtime.version"));

    private Map<String, String> auxiliaryInformation = new HashMap<>();

    private String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException e) {
            addStatus(new WarnStatus(String.format("Cannot read system property '%s'.", property), this, e));
            return null;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    protected void append(ILoggingEvent event) {
        Incident incident = createIncident(event);
        if (!history.contains(incident)) {
            history.add(incident);
            sendIncident(incident);
        }
    }

    private Incident createIncident(ILoggingEvent event) {
        event.prepareForDeferredProcessing();

        Incident incident = new Incident();
        incident.setAnonymousId(HOST_ID);
        incident.setEclipseProduct(productId);
        incident.setEclipseBuildId(buildId);
        incident.setName(userName);
        incident.setEmail(email);
        incident.setOsgiArch(osgiArch);
        incident.setOsgiOs(osgiOs);
        incident.setOsgiOsVersion(osgiOsVersion);
        incident.setOsgiWs("");
        incident.setJavaRuntimeVersion(javaRuntimeVersion);
        incident.setAuxiliaryInformation(auxiliaryInformation);
        Status status = new Status();
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        // if proxy is null, no exception was logged. add a synthetic one with the caller data and calculate the
        // packagingData
        if (throwableProxy == null) {
            java.lang.Throwable t = new NoStackTrace();
            t.setStackTrace(event.getCallerData());
            throwableProxy = new ThrowableProxy(t);
            new PackagingDataCalculator().calculate(throwableProxy);
        }
        Throwable throwable = convert(throwableProxy);
        LinkedHashSet<ClassPackagingData> data = collectPackagingData(throwableProxy);
        List<Bundle> presentBundles = new ArrayList<>();
        for (ClassPackagingData classPackagingData : data) {
            Bundle bundle = toBundle(classPackagingData);
            if (!presentBundles.contains(bundle)) {
                presentBundles.add(bundle);
            }
        }
        incident.setPresentBundles(presentBundles);
        if (!presentBundles.isEmpty()) {
            Bundle bundle = presentBundles.get(0);
            status.setPluginId(bundle.getName());
            status.setPluginVersion(bundle.getVersion());
        } else {
            status.setPluginId("unknown");
            status.setPluginVersion("unknown");
        }
        status.setException(throwable);
        status.setMessage(event.getFormattedMessage());

        if (Strings.isNullOrEmpty(status.getMessage())) {
            status.setMessage(throwable.getMessage());
        }
        incident.setStatus(status);
        return incident;
    }

    @VisibleForTesting
    protected Bundle toBundle(ClassPackagingData classPackagingData) {
        Bundle bundle = new Bundle();
        String codeLocation = classPackagingData.getCodeLocation();
        // remove any file extension
        int indexOfLastDot = codeLocation.lastIndexOf(".");
        if (indexOfLastDot != -1) {
            codeLocation = codeLocation.substring(0, indexOfLastDot);
        }
        String version = classPackagingData.getVersion();
        // assuming codelocation = name-version.jar
        int indexOfVersion = codeLocation.lastIndexOf("-" + version);
        if (indexOfVersion != -1) {
            codeLocation = codeLocation.substring(0, indexOfVersion);
        }
        bundle.setName(codeLocation);
        bundle.setVersion(version);
        return bundle;
    }

    private LinkedHashSet<ClassPackagingData> collectPackagingData(IThrowableProxy throwableProxy) {
        LinkedHashSet<ClassPackagingData> classPackagingData = new LinkedHashSet<>();
        for (StackTraceElementProxy proxy : throwableProxy.getStackTraceElementProxyArray()) {
            ClassPackagingData data = proxy.getClassPackagingData();
            if (data != null) {
                classPackagingData.add(data);
            }
        }
        if (throwableProxy.getCause() != null) {
            classPackagingData.addAll(collectPackagingData(throwableProxy.getCause()));
        }
        return classPackagingData;
    }

    private static Throwable convert(IThrowableProxy proxy) {
        Throwable t = new Throwable();
        t.setClassName(proxy.getClassName());
        t.setMessage(proxy.getMessage());
        if (proxy.getCause() != null) {
            t.setCause(convert(proxy.getCause()));
        }
        t.setStackTrace(convert(proxy.getStackTraceElementProxyArray()));
        return t;
    }

    private static StackTraceElement[] convert(StackTraceElementProxy[] proxyArray) {
        StackTraceElement[] st = new StackTraceElement[proxyArray.length];
        for (int i = 0; i < st.length; i++) {
            st[i] = convert(proxyArray[i].getStackTraceElement());
        }
        return st;
    }

    private static StackTraceElement convert(java.lang.StackTraceElement ste) {
        StackTraceElement st = new StackTraceElement();
        st.setClassName(ste.getClassName());
        st.setFileName(ste.getFileName());
        st.setLineNumber(ste.getLineNumber());
        st.setMethodName(ste.getMethodName());
        st.setNative(ste.isNativeMethod());
        return st;
    }

    @VisibleForTesting
    void sendIncident(Incident i) {
        try {
            IO.sendIncident(i, url);
        } catch (HttpResponseException e) {
            addStatus(new WarnStatus(
                    String.format("Failed to send incident '%s'. HTTP status code: %s", i.getStatus().getMessage(), e.getStatusCode()),
                    this, e));
        } catch (Exception e) {
            addStatus(new WarnStatus(String.format("Failed to send incident '%s'", i.getStatus().getMessage()), this, e));
        }
    }

    public int getHistorySize() {
        return historySize;
    }

    public void setHistorySize(int historySize) {
        this.historySize = historySize;
        this.history = EvictingQueue.create(historySize);

    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, String> getAuxiliaryInformation() {
        return auxiliaryInformation;
    }

    public void setAuxiliaryInformation(Map<String, String> auxiliaryInformation) {
        this.auxiliaryInformation = auxiliaryInformation;
    }

    private static final class NoStackTrace extends java.lang.Throwable {

        private static final long serialVersionUID = 1L;

        private NoStackTrace() {
            super("This event was logged without a stacktrace. The caller data is used instead.");
        }
    }
}

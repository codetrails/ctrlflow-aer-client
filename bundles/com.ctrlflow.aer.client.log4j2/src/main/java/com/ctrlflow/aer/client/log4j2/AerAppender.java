package com.ctrlflow.aer.client.log4j2;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AsyncAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.impl.ExtendedClassInfo;
import org.apache.logging.log4j.core.impl.ExtendedStackTraceElement;
import org.apache.logging.log4j.core.impl.ThrowableProxy;

import com.ctrlflow.aer.client.dto.Bundle;
import com.ctrlflow.aer.client.dto.Incident;
import com.ctrlflow.aer.client.dto.StackTraceElement;
import com.ctrlflow.aer.client.dto.Throwable;
import com.ctrlflow.aer.client.simple.IncidentBuilder;
import com.ctrlflow.aer.client.simple.SimpleAerClient;
import com.google.common.base.Strings;
import com.google.common.collect.EvictingQueue;

/**
 * Appender which creates incidents for log events and sends them to a given URL. It is recommended to attach the appender to an
 * {@link AsyncAppender}.
 * 
 * @since 2.0.2
 */
@Plugin(name = AerAppender.PLUGIN_NAME, category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class AerAppender extends AbstractAppender {

    public static final String PLUGIN_NAME = "AER";

    private static final int DEFAULT_HISTORY_SIZE = 50;

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
    private int historySize = DEFAULT_HISTORY_SIZE;
    private Queue<Incident> history = EvictingQueue.create(historySize);

    private String productId = "undefined";
    private String productVersion = "undefined";
    private String userName = getSystemProperty("user.name");
    private String email = userName + "@localhost";

    private final String osgiArch = Strings.nullToEmpty(getSystemProperty("os.arch"));
    private final String osgiOs = Strings.nullToEmpty(getSystemProperty("os.name"));
    private final String osgiOsVersion = Strings.nullToEmpty(getSystemProperty("os.version"));
    private final String javaRuntimeVersion = Strings.nullToEmpty(getSystemProperty("java.runtime.version"));

    private Map<String, String> auxiliaryInformation = new HashMap<>();

    private AerAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
    }

    private String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException e) {
            super.error(String.format("Cannot read system property '%s'.", property), e);
            return null;
        }
    }

    @Override
    public void append(LogEvent event) {
        Incident incident = createIncident(event);
        if (!history.contains(incident)) {
            history.add(incident);
            sendIncident(incident);
        }
    }

    private Incident createIncident(LogEvent event) {
        ThrowableProxy throwableProxy = event.getThrownProxy();
        // If proxy is null, no exception was logged. add a synthetic one with the caller data
        if (throwableProxy == null) {
            java.lang.Throwable t = new NoStackTrace();
            t.setStackTrace(new java.lang.StackTraceElement[] { event.getSource() });
            throwableProxy = new ThrowableProxy(t);
        }
        Throwable throwable = convert(throwableProxy);

        LinkedHashSet<ExtendedClassInfo> data = collectPackagingData(throwableProxy);
        List<Bundle> presentBundles = new ArrayList<>();
        for (ExtendedClassInfo classPackagingData : data) {
            Bundle bundle = toBundle(classPackagingData);
            if (!presentBundles.contains(bundle)) {
                presentBundles.add(bundle);
            }
        }

        IncidentBuilder builder = IncidentBuilder.from(throwable);

        if (!presentBundles.isEmpty()) {
            builder.withLoggingBundle(presentBundles.get(0));
        }
        for (Bundle presentBundle : presentBundles) {
            builder.withPresentBundle(presentBundle);
        }

        builder.withLogMessage(event.getMessage().getFormattedMessage());

        builder.withAnonymousId(HOST_ID).withUserName(userName).withUserEmail(email);

        builder.withProductId(productId).withProductVersion(productVersion);

        builder.withOsgiArch(osgiArch).withOsgiOs(osgiOs).withOsgiOsVersion(osgiOsVersion).withOsgiWs("");

        builder.withJavaRuntimeVersion(javaRuntimeVersion);

        for (Map.Entry<String, String> entry : auxiliaryInformation.entrySet()) {
            builder.withAuxiliaryInformation(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    private static Throwable convert(ThrowableProxy proxy) {
        Throwable t = new Throwable();
        t.setClassName(proxy.getName());
        t.setMessage(proxy.getMessage());
        if (proxy.getCauseProxy() != null) {
            t.setCause(convert(proxy.getCauseProxy()));
        }
        t.setStackTrace(convert(proxy.getExtendedStackTrace()));
        return t;
    }

    private static StackTraceElement[] convert(ExtendedStackTraceElement[] proxyArray) {
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

    private LinkedHashSet<ExtendedClassInfo> collectPackagingData(ThrowableProxy throwableProxy) {
        LinkedHashSet<ExtendedClassInfo> classPackagingData = new LinkedHashSet<>();
        for (ExtendedStackTraceElement proxy : throwableProxy.getExtendedStackTrace()) {
            ExtendedClassInfo data = proxy.getExtraClassInfo();
            if (data != null) {
                classPackagingData.add(data);
            }
        }
        if (throwableProxy.getCauseProxy() != null) {
            classPackagingData.addAll(collectPackagingData(throwableProxy.getCauseProxy()));
        }
        return classPackagingData;
    }

    protected Bundle toBundle(ExtendedClassInfo classPackagingData) {
        Bundle bundle = new Bundle();
        String codeLocation = classPackagingData.getLocation();
        if (codeLocation.endsWith("/")) {
        	// when using bundles from the workspace instead of jars
        	codeLocation = codeLocation.substring(0, codeLocation.length() - 1);
        }
        else {
        	// remove any file extension
        	int indexOfLastDot = codeLocation.lastIndexOf(".");
        	if (indexOfLastDot != -1) {
        		codeLocation = codeLocation.substring(0, indexOfLastDot);
        	}
        }
		// log4j2 uses package.getImplementationVersion() to determine Version of a bundle. The
		// Version is read from the Manifest.mf Implementation-Version property.
		// In eclipse its not present when using bundles from within the workspace because the
		// Manifest is not copied to the bin directory.
		// It should work in the deployed product.
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

    private void sendIncident(Incident incident) {
        try {
            SimpleAerClient.send(incident, url);
        } catch (IOException e) {
            super.error(String.format("Failed to send incident '%s'", incident.getStatus().getMessage()), e);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
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

    public void addAuxiliaryInformation(AuxiliaryInformation info) {
        this.auxiliaryInformation.put(info.getKey(), info.getValue());
    }

    @Plugin(name = "AuxiliaryInformation", category = Core.CATEGORY_NAME, printObject = true)
    public static class AuxiliaryInformation {

        private final String key;
        private final String value;

        public AuxiliaryInformation(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        @PluginFactory
        public static AuxiliaryInformation createAuxiliaryInformation(@PluginAttribute("key") final String key,
                @PluginAttribute("value") final String value) {

            if (key == null) {
                LOGGER.error("Auxilliary information must contain a key");
                return null;
            }

            if (value == null) {
                LOGGER.error("Auxilliary information must contain a value");
                return null;
            }

            return new AuxiliaryInformation(key, value);
        }
    }

    @PluginBuilderFactory
    public static <B extends Builder<B>> B newBuilder() {
        return new Builder<B>().asBuilder();
    }

    public static class Builder<B extends Builder<B>> extends AbstractAppender.Builder<B>
            implements org.apache.logging.log4j.core.util.Builder<AerAppender> {

        @PluginBuilderAttribute
        @Required(message = "No URL provided for AerAppender")
        private String url;

        @PluginBuilderAttribute
        private int historySize = DEFAULT_HISTORY_SIZE;

        @PluginBuilderAttribute
        private String productId;

        @PluginBuilderAttribute
        private String productVersion;

        @PluginBuilderAttribute
        private String userName;

        @PluginBuilderAttribute
        private String email;

        @PluginElement("AuxiliaryInformation")
        private AuxiliaryInformation[] auxiliaryInformation;

        public B withUrl(String url) {
            this.url = url;
            return asBuilder();
        }

        public String getUrl() {
            return url;
        }

        public B withHistorySize(int historySize) {
            this.historySize = historySize;
            return asBuilder();
        }

        public int getHistorySize() {
            return historySize;
        }

        public B withProductId(String productId) {
            this.productId = productId;
            return asBuilder();
        }

        public String getProductId() {
            return productId;
        }

        public B withProductVersion(String productVersion) {
            this.productVersion = productVersion;
            return asBuilder();
        }

        public String getProductVersion() {
            return productVersion;
        }

        public B withUserName(String userName) {
            this.userName = userName;
            return asBuilder();
        }

        public String getUserName() {
            return userName;
        }

        public B withEmail(String email) {
            this.email = email;
            return asBuilder();
        }

        public String getEmail() {
            return email;
        }

        public B withAuxiliaryInformation(AuxiliaryInformation[] auxiliaryInformation) {
            this.auxiliaryInformation = auxiliaryInformation;
            return asBuilder();
        }

        public AuxiliaryInformation[] getAuxiliaryInformation() {
            return auxiliaryInformation;
        }

        @Override
        public AerAppender build() {
            AerAppender appender = new AerAppender(getName(), getFilter(), getLayout(), isIgnoreExceptions());
            appender.setUrl(url);
            if (historySize != DEFAULT_HISTORY_SIZE) {
                appender.setHistorySize(historySize);
            }
            if (productId != null) {
                appender.setProductId(productId);
            }
            if (productVersion != null) {
                appender.setProductVersion(productVersion);
            }
            if (userName != null) {
                appender.setUserName(userName);
            }
            if (email != null) {
                appender.setEmail(email);
            }
            if (auxiliaryInformation != null) {
                for (AuxiliaryInformation info : auxiliaryInformation) {
                    appender.addAuxiliaryInformation(info);
                }
            }
            return appender;
        }
    }

    private static final class NoStackTrace extends java.lang.Throwable {

        private static final long serialVersionUID = 1L;

        private NoStackTrace() {
            super("This event was logged without a stacktrace. The caller data is used instead.");
        }
    }
}

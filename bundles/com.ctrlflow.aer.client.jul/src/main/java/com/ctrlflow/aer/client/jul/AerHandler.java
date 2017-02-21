package com.ctrlflow.aer.client.jul;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.ErrorManager;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import com.ctrlflow.aer.client.dto.Incident;
import com.ctrlflow.aer.client.simple.IncidentBuilder;
import com.ctrlflow.aer.client.simple.SimpleAerClient;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.EvictingQueue;

/**
 * {@link Handler} which creates incidents for log events and sends them to a given URL.
 * 
 * @since 2.0.2
 */
public class AerHandler extends Handler {

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
    private int historySize;
    private Queue<Incident> history;

    private String productId;
    private String productVersion;
    private String userName;
    private String email;

    private final String osgiArch = Strings.nullToEmpty(getSystemProperty("os.arch"));
    private final String osgiOs = Strings.nullToEmpty(getSystemProperty("os.name"));
    private final String osgiOsVersion = Strings.nullToEmpty(getSystemProperty("os.version"));
    private final String javaRuntimeVersion = Strings.nullToEmpty(getSystemProperty("java.runtime.version"));

    private Map<String, String> auxiliaryInformation = new HashMap<>();

    private String getSystemProperty(String property) {
        try {
            return System.getProperty(property);
        } catch (SecurityException e) {
            reportError(null, e, ErrorManager.GENERIC_FAILURE);
            return null;
        }
    }

    public AerHandler() {
        configure();
    }

    private void configure() {
        LogManager manager = LogManager.getLogManager();
        String cname = getClass().getName();

        setLevel(getLevelProperty(manager, cname + ".level", Level.SEVERE));
        setFilter(getFilterProperty(manager, cname + ".filter", null));

        setUrl(getStringProperty(manager, cname + ".url", null));

        setHistorySize(getIntProperty(manager, cname + ".historySize", DEFAULT_HISTORY_SIZE));

        setProductId(getStringProperty(manager, cname + ".productId", "undefined"));
        setProductVersion(getStringProperty(manager, cname + ".productVersion", "undefined"));

        String userName = getSystemProperty("user.name");
        setUserName(getStringProperty(manager, cname + ".userName", userName));
        setEmail(getStringProperty(manager, cname + ".email", userName + "@localhost"));

        String keys = getStringProperty(manager, cname + ".auxilliaryInformationKeys", null);
        if (keys != null) {
            for (String key : Splitter.on(',').omitEmptyStrings().trimResults().split(keys)) {
                String value = getStringProperty(manager, cname + ".auxilliaryInformation." + key, null);
                if (value != null) {
                    auxiliaryInformation.put(key, value);
                }
            }
        }
    }

    private Level getLevelProperty(LogManager manager, String property, Level defaultValue) {
        String rawValue = manager.getProperty(property);
        if (rawValue == null) {
            return defaultValue;
        }
        try {
            return Level.parse(rawValue.trim());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    private Filter getFilterProperty(LogManager manager, String property, Filter defaultValue) {
        return getClassProperty(manager, Filter.class, property, defaultValue);
    }

    private <T> T getClassProperty(LogManager manager, Class<T> expectedClass, String property, T defaultValue) {
        String rawValue = manager.getProperty(property);
        if (rawValue == null) {
            return defaultValue;
        }
        try {
            Class<?> loadedClass = ClassLoader.getSystemClassLoader().loadClass(rawValue.trim());
            return expectedClass.cast(loadedClass.newInstance());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private String getStringProperty(LogManager manager, String property, String defaultValue) {
        String value = manager.getProperty(property);
        return value != null ? value.trim() : defaultValue;
    }

    private int getIntProperty(LogManager manager, String property, int defaultValue) {
        String rawValue = manager.getProperty(property);
        if (rawValue == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(rawValue.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }

        Incident incident = createIncident(record);
        if (!history.contains(incident)) {
            history.add(incident);
            sendIncident(incident);
        }
    }

    private Incident createIncident(LogRecord record) {
        java.lang.Throwable t = record.getThrown() != null ? record.getThrown() : new NoStackTrace();

        IncidentBuilder builder = IncidentBuilder.from(t);

        builder.withLogMessage(getFormattedMessage(record));

        builder.withAnonymousId(HOST_ID).withUserName(userName).withUserEmail(email);

        builder.withProductId(productId).withProductVersion(productVersion);

        builder.withOsgiArch(osgiArch).withOsgiOs(osgiOs).withOsgiOsVersion(osgiOsVersion).withOsgiWs("");

        builder.withJavaRuntimeVersion(javaRuntimeVersion);

        for (Map.Entry<String, String> entry : auxiliaryInformation.entrySet()) {
            builder.withAuxiliaryInformation(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    public String getFormattedMessage(LogRecord record) {
        String format = record.getMessage();
        Object[] parameters = record.getParameters();

        ResourceBundle resourceBundle = record.getResourceBundle();
        if (resourceBundle != null) {
            try {
                format = resourceBundle.getString(format);
            } catch (MissingResourceException e) {
                // Ignore
            }
        }

        return MessageFormat.format(format, parameters);
    }

    private void sendIncident(Incident incident) {
        try {
            SimpleAerClient.send(incident, url);
        } catch (IOException e) {
            reportError(String.format("Failed to send incident '%s'", incident.getStatus().getMessage()), e,
                    ErrorManager.FLUSH_FAILURE);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
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

    public void addAuxilliaryInformation(String key, String value) {
        this.auxiliaryInformation.put(key, value);
    }

    private static final class NoStackTrace extends java.lang.Throwable {

        private static final long serialVersionUID = 1L;

        private NoStackTrace() {
            super("This event was logged without a stacktrace. The caller data is used instead.");
        }
    }
}

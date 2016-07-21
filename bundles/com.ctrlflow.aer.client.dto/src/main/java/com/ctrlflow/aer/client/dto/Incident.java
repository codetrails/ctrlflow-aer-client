package com.ctrlflow.aer.client.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @since 2.0.0
 */
@SuppressWarnings("all")
public class Incident {
    private String id;

    private String token;

    private UUID anonymousId;

    private String name;

    private String email;

    private String comment;

    private String fingerprint;

    private String eclipseBuildId;

    private String eclipseProduct;

    private String javaRuntimeVersion;

    private boolean logMessage;

    private String osgiArch;

    private String osgiOs;

    private String osgiOsVersion;

    private String osgiWs;

    private List<Bundle> presentBundles = new ArrayList<>();

    private Status status;

    private Set<String> problemIds = new HashSet<>();

    public boolean hasId() {
        return this.id != null;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Incident withId(final String id) {
        this.id = id;
        return this;
    }

    public boolean hasAnonymousId() {
        return this.anonymousId != null;
    }

    public UUID getAnonymousId() {
        return this.anonymousId;
    }

    public void setAnonymousId(final UUID anonymousId) {
        this.anonymousId = anonymousId;
    }

    public Incident withAnonymousId(final UUID anonymousId) {
        this.anonymousId = anonymousId;
        return this;
    }

    public boolean hasName() {
        return this.name != null;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Incident withName(final String name) {
        this.name = name;
        return this;
    }

    public boolean hasEmail() {
        return this.email != null;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Incident withEmail(final String email) {
        this.email = email;
        return this;
    }

    public boolean hasComment() {
        return this.comment != null;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public Incident withComment(final String comment) {
        this.comment = comment;
        return this;
    }

    public boolean hasFingerprint() {
        return this.fingerprint != null;
    }

    public String getFingerprint() {
        return this.fingerprint;
    }

    public void setFingerprint(final String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public Incident withFingerprint(final String fingerprint) {
        this.fingerprint = fingerprint;
        return this;
    }

    public boolean hasEclipseBuildId() {
        return this.eclipseBuildId != null;
    }

    public String getEclipseBuildId() {
        return this.eclipseBuildId;
    }

    public void setEclipseBuildId(final String eclipseBuildId) {
        this.eclipseBuildId = eclipseBuildId;
    }

    public Incident withEclipseBuildId(final String eclipseBuildId) {
        this.eclipseBuildId = eclipseBuildId;
        return this;
    }

    public boolean hasEclipseProduct() {
        return this.eclipseProduct != null;
    }

    public String getEclipseProduct() {
        return this.eclipseProduct;
    }

    public void setEclipseProduct(final String eclipseProduct) {
        this.eclipseProduct = eclipseProduct;
    }

    public Incident withEclipseProduct(final String eclipseProduct) {
        this.eclipseProduct = eclipseProduct;
        return this;
    }

    public boolean hasJavaRuntimeVersion() {
        return this.javaRuntimeVersion != null;
    }

    public String getJavaRuntimeVersion() {
        return this.javaRuntimeVersion;
    }

    public void setJavaRuntimeVersion(final String javaRuntimeVersion) {
        this.javaRuntimeVersion = javaRuntimeVersion;
    }

    public Incident withJavaRuntimeVersion(final String javaRuntimeVersion) {
        this.javaRuntimeVersion = javaRuntimeVersion;
        return this;
    }

    public boolean hasOsgiArch() {
        return this.osgiArch != null;
    }

    public String getOsgiArch() {
        return this.osgiArch;
    }

    public void setOsgiArch(final String osgiArch) {
        this.osgiArch = osgiArch;
    }

    public Incident withOsgiArch(final String osgiArch) {
        this.osgiArch = osgiArch;
        return this;
    }

    public boolean hasOsgiOs() {
        return this.osgiOs != null;
    }

    public String getOsgiOs() {
        return this.osgiOs;
    }

    public void setOsgiOs(final String osgiOs) {
        this.osgiOs = osgiOs;
    }

    public Incident withOsgiOs(final String osgiOs) {
        this.osgiOs = osgiOs;
        return this;
    }

    public boolean hasOsgiOsVersion() {
        return this.osgiOsVersion != null;
    }

    public String getOsgiOsVersion() {
        return this.osgiOsVersion;
    }

    public void setOsgiOsVersion(final String osgiOsVersion) {
        this.osgiOsVersion = osgiOsVersion;
    }

    public Incident withOsgiOsVersion(final String osgiOsVersion) {
        this.osgiOsVersion = osgiOsVersion;
        return this;
    }

    public boolean hasOsgiWs() {
        return this.osgiWs != null;
    }

    public String getOsgiWs() {
        return this.osgiWs;
    }

    public void setOsgiWs(final String osgiWs) {
        this.osgiWs = osgiWs;
    }

    public Incident withOsgiWs(final String osgiWs) {
        this.osgiWs = osgiWs;
        return this;
    }

    public boolean hasPresentBundles() {
        return this.presentBundles != null;
    }

    public List<Bundle> getPresentBundles() {
        return this.presentBundles;
    }

    public void setPresentBundles(final List<Bundle> presentBundles) {
        this.presentBundles = presentBundles;
    }

    public Incident withPresentBundles(final List<Bundle> presentBundles) {
        this.presentBundles = presentBundles;
        return this;
    }

    public boolean hasStatus() {
        return this.status != null;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }

    public Incident withStatus(final Status status) {
        this.status = status;
        return this;
    }

    public boolean hasBugId() {
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (anonymousId == null ? 0 : anonymousId.hashCode());
        result = prime * result + (comment == null ? 0 : comment.hashCode());
        result = prime * result + (eclipseBuildId == null ? 0 : eclipseBuildId.hashCode());
        result = prime * result + (eclipseProduct == null ? 0 : eclipseProduct.hashCode());
        result = prime * result + (email == null ? 0 : email.hashCode());
        result = prime * result + (fingerprint == null ? 0 : fingerprint.hashCode());
        result = prime * result + (id == null ? 0 : id.hashCode());
        result = prime * result + (javaRuntimeVersion == null ? 0 : javaRuntimeVersion.hashCode());
        result = prime * result + (logMessage ? 1231 : 1237);
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (osgiArch == null ? 0 : osgiArch.hashCode());
        result = prime * result + (osgiOs == null ? 0 : osgiOs.hashCode());
        result = prime * result + (osgiOsVersion == null ? 0 : osgiOsVersion.hashCode());
        result = prime * result + (osgiWs == null ? 0 : osgiWs.hashCode());
        result = prime * result + (presentBundles == null ? 0 : presentBundles.hashCode());
        result = prime * result + (problemIds == null ? 0 : problemIds.hashCode());
        result = prime * result + (status == null ? 0 : status.hashCode());
        result = prime * result + (token == null ? 0 : token.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Incident other = (Incident) obj;
        if (anonymousId == null) {
            if (other.anonymousId != null) {
                return false;
            }
        } else if (!anonymousId.equals(other.anonymousId)) {
            return false;
        }
        if (comment == null) {
            if (other.comment != null) {
                return false;
            }
        } else if (!comment.equals(other.comment)) {
            return false;
        }
        if (eclipseBuildId == null) {
            if (other.eclipseBuildId != null) {
                return false;
            }
        } else if (!eclipseBuildId.equals(other.eclipseBuildId)) {
            return false;
        }
        if (eclipseProduct == null) {
            if (other.eclipseProduct != null) {
                return false;
            }
        } else if (!eclipseProduct.equals(other.eclipseProduct)) {
            return false;
        }
        if (email == null) {
            if (other.email != null) {
                return false;
            }
        } else if (!email.equals(other.email)) {
            return false;
        }
        if (fingerprint == null) {
            if (other.fingerprint != null) {
                return false;
            }
        } else if (!fingerprint.equals(other.fingerprint)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (javaRuntimeVersion == null) {
            if (other.javaRuntimeVersion != null) {
                return false;
            }
        } else if (!javaRuntimeVersion.equals(other.javaRuntimeVersion)) {
            return false;
        }
        if (logMessage != other.logMessage) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (osgiArch == null) {
            if (other.osgiArch != null) {
                return false;
            }
        } else if (!osgiArch.equals(other.osgiArch)) {
            return false;
        }
        if (osgiOs == null) {
            if (other.osgiOs != null) {
                return false;
            }
        } else if (!osgiOs.equals(other.osgiOs)) {
            return false;
        }
        if (osgiOsVersion == null) {
            if (other.osgiOsVersion != null) {
                return false;
            }
        } else if (!osgiOsVersion.equals(other.osgiOsVersion)) {
            return false;
        }
        if (osgiWs == null) {
            if (other.osgiWs != null) {
                return false;
            }
        } else if (!osgiWs.equals(other.osgiWs)) {
            return false;
        }
        if (presentBundles == null) {
            if (other.presentBundles != null) {
                return false;
            }
        } else if (!presentBundles.equals(other.presentBundles)) {
            return false;
        }
        if (problemIds == null) {
            if (other.problemIds != null) {
                return false;
            }
        } else if (!problemIds.equals(other.problemIds)) {
            return false;
        }
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (token == null) {
            if (other.token != null) {
                return false;
            }
        } else if (!token.equals(other.token)) {
            return false;
        }
        return true;
    }
}

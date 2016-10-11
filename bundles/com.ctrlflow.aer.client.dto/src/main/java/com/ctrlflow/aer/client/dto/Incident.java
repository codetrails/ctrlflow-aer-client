package com.ctrlflow.aer.client.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @since 2.0.0
 */
public class Incident {

    private String id;

    private UUID anonymousId;

    private String name;

    private String email;

    private String comment;

    private String fingerprint;

    private String eclipseBuildId;

    private String eclipseProduct;

    private String javaRuntimeVersion;

    private String osgiArch;

    private String osgiOs;

    private String osgiOsVersion;

    private String osgiWs;

    private List<Bundle> presentBundles = new ArrayList<>();

    private Status status;

    /**
     * @since 2.0.1
     */
    private Map<String, String> auxiliaryInformation;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UUID getAnonymousId() {
        return this.anonymousId;
    }

    public void setAnonymousId(UUID anonymousId) {
        this.anonymousId = anonymousId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getComment() {
        return this.comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getFingerprint() {
        return this.fingerprint;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }

    public String getEclipseBuildId() {
        return this.eclipseBuildId;
    }

    public void setEclipseBuildId(String eclipseBuildId) {
        this.eclipseBuildId = eclipseBuildId;
    }

    public String getEclipseProduct() {
        return this.eclipseProduct;
    }

    public void setEclipseProduct(String eclipseProduct) {
        this.eclipseProduct = eclipseProduct;
    }

    public String getJavaRuntimeVersion() {
        return this.javaRuntimeVersion;
    }

    public void setJavaRuntimeVersion(String javaRuntimeVersion) {
        this.javaRuntimeVersion = javaRuntimeVersion;
    }

    public String getOsgiArch() {
        return this.osgiArch;
    }

    public void setOsgiArch(String osgiArch) {
        this.osgiArch = osgiArch;
    }

    public String getOsgiOs() {
        return this.osgiOs;
    }

    public void setOsgiOs(String osgiOs) {
        this.osgiOs = osgiOs;
    }

    public String getOsgiOsVersion() {
        return this.osgiOsVersion;
    }

    public void setOsgiOsVersion(String osgiOsVersion) {
        this.osgiOsVersion = osgiOsVersion;
    }

    public String getOsgiWs() {
        return this.osgiWs;
    }

    public void setOsgiWs(String osgiWs) {
        this.osgiWs = osgiWs;
    }

    public List<Bundle> getPresentBundles() {
        return this.presentBundles;
    }

    public void setPresentBundles(List<Bundle> presentBundles) {
        this.presentBundles = presentBundles;
    }

    public Status getStatus() {
        return this.status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @since 2.0.1
     */
    public Map<String, String> getAuxiliaryInformation() {
        return auxiliaryInformation != null ? auxiliaryInformation : Collections.emptyMap();
    }

    /**
     * @since 2.0.1
     */
    public void setAuxiliaryInformation(Map<String, String> auxiliaryInformation) {
        this.auxiliaryInformation = auxiliaryInformation;
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
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (osgiArch == null ? 0 : osgiArch.hashCode());
        result = prime * result + (osgiOs == null ? 0 : osgiOs.hashCode());
        result = prime * result + (osgiOsVersion == null ? 0 : osgiOsVersion.hashCode());
        result = prime * result + (osgiWs == null ? 0 : osgiWs.hashCode());
        result = prime * result + (presentBundles == null ? 0 : presentBundles.hashCode());
        result = prime * result + (status == null ? 0 : status.hashCode());
        result = prime * result + (auxiliaryInformation == null ? 0 : auxiliaryInformation.hashCode());
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
        if (status == null) {
            if (other.status != null) {
                return false;
            }
        } else if (!status.equals(other.status)) {
            return false;
        }
        if (auxiliaryInformation == null) {
            if (other.auxiliaryInformation != null) {
                return false;
            }
        } else if (!auxiliaryInformation.equals(other.auxiliaryInformation)) {
            return false;
        }
        return true;
    }
}

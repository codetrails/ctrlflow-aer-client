package com.ctrlflow.aer.client.dto;

@SuppressWarnings("all")
public class Bundle {

    private String name;

    private String version;

    public Bundle() {
    }

    public Bundle(final String name, final String version) {
        this.setName(name);
        this.setVersion(version);
    }

    @Override
    public String toString() {
        String _name = this.getName();
        String _plus = _name + "_";
        String _version = this.getVersion();
        return _plus + _version;
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

    public Bundle withName(final String name) {
        this.name = name;
        return this;
    }

    public boolean hasVersion() {
        return this.version != null;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    public Bundle withVersion(final String version) {
        this.version = version;
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (name == null ? 0 : name.hashCode());
        result = prime * result + (version == null ? 0 : version.hashCode());
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
        Bundle other = (Bundle) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }

}

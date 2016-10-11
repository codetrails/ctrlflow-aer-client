package com.ctrlflow.aer.client.dto;

import java.util.Set;
import java.util.TreeSet;

/**
 * @since 2.0.0
 */
public class StackTraceElement {

    private String className;

    private String methodName;

    private String fileName;

    private int lineNumber;

    private boolean native_;

    private Set<String> tags = new TreeSet<>();

    public StackTraceElement() {
    }

    public StackTraceElement(String className, String methodName) {
        setClassName(className);
        setMethodName(methodName);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public boolean isNative() {
        return native_;
    }

    public void setNative(boolean nativeMethod) {
        this.native_ = nativeMethod;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (className == null ? 0 : className.hashCode());
        result = prime * result + (methodName == null ? 0 : methodName.hashCode());
        result = prime * result + (fileName == null ? 0 : fileName.hashCode());
        result = prime * result + lineNumber;
        result = prime * result + (native_ ? 1231 : 1237);
        result = prime * result + (tags == null ? 0 : tags.hashCode());
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
        StackTraceElement other = (StackTraceElement) obj;
        if (className == null) {
            if (other.className != null) {
                return false;
            }
        } else if (!className.equals(other.className)) {
            return false;
        }
        if (methodName == null) {
            if (other.methodName != null) {
                return false;
            }
        } else if (!methodName.equals(other.methodName)) {
            return false;
        }
        if (fileName == null) {
            if (other.fileName != null) {
                return false;
            }
        } else if (!fileName.equals(other.fileName)) {
            return false;
        }
        if (lineNumber != other.lineNumber) {
            return false;
        }
        if (native_ != other.native_) {
            return false;
        }
        if (tags == null) {
            if (other.tags != null) {
                return false;
            }
        } else if (!tags.equals(other.tags)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return className + "." + methodName + "(" + fileName + ":" + lineNumber + ")"
                + (!tags.isEmpty() ? tags.toString() : "");
    }
}

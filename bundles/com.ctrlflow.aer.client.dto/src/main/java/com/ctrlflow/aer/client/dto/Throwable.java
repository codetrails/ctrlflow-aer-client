package com.ctrlflow.aer.client.dto;

import java.util.Arrays;

/**
 * @since 2.0.0
 */
public class Throwable {

    private static final StackTraceElement[] EMPTY = new StackTraceElement[0];
    private String className;
    private String message;
    private Throwable cause;
    private StackTraceElement[] stackTrace = EMPTY;

    public Throwable() {
    }

    public Throwable(String message, String className, StackTraceElement[] trace, Throwable cause) {
        setClassName(className);
        setMessage(message);
        setStackTrace(trace);
        setCause(cause);
    }

    public Throwable getCause() {
        return cause;
    }

    public String getClassName() {
        return className;
    }

    public String getMessage() {
        return message;
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
        this.stackTrace = stackTrace;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (cause == null ? 0 : cause.hashCode());
        result = prime * result + (className == null ? 0 : className.hashCode());
        result = prime * result + (message == null ? 0 : message.hashCode());
        result = prime * result + Arrays.hashCode(stackTrace);
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
        Throwable other = (Throwable) obj;
        if (cause == null) {
            if (other.cause != null) {
                return false;
            }
        } else if (!cause.equals(other.cause)) {
            return false;
        }
        if (className == null) {
            if (other.className != null) {
                return false;
            }
        } else if (!className.equals(other.className)) {
            return false;
        }
        if (message == null) {
            if (other.message != null) {
                return false;
            }
        } else if (!message.equals(other.message)) {
            return false;
        }
        if (!Arrays.equals(stackTrace, other.stackTrace)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append(className).append(": ").append(message);
        if (stackTrace != null) {
            for (StackTraceElement elem : stackTrace) {
                sb.append("\n    at ").append(elem);
            }
        }
        if (cause != null) {
            sb.append("\ncaused by: ").append(cause.toString());
        }
        return sb.toString();
    }

}

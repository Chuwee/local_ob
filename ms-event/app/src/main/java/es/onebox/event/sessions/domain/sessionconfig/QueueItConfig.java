package es.onebox.event.sessions.domain.sessionconfig;

import java.io.Serializable;

public class QueueItConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean active;
    private String alias;

    private boolean parameterActive;
    private String parameter;
    private String value;
    private String version;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isParameterActive() {
        return parameterActive;
    }

    public void setParameterActive(boolean parameterActive) {
        this.parameterActive = parameterActive;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

package es.onebox.event.catalog.elasticsearch.dto;

import es.onebox.event.catalog.elasticsearch.enums.VirtualQueueVersion;

import java.io.Serial;
import java.io.Serializable;

public class VirtualQueue implements Serializable {

    @Serial
    private static final long serialVersionUID = 5624181496950955077L;

    private Boolean enabled;
    private VirtualQueueVersion version;
    private String alias;


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public VirtualQueueVersion getVersion() {
        return version;
    }

    public void setVersion(VirtualQueueVersion version) {
        this.version = version;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}

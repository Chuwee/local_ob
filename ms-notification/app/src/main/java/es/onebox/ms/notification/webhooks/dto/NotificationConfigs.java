package es.onebox.ms.notification.webhooks.dto;

import es.onebox.couchbase.annotations.CouchDocument;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

@CouchDocument
public class NotificationConfigs implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<NotificationConfig> configs;
    private long totalElements;

    public List<NotificationConfig> getConfigs() {
        return configs;
    }

    public void setConfigs(List<NotificationConfig> configs) {
        this.configs = configs;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

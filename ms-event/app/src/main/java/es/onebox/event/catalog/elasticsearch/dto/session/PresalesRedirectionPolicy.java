package es.onebox.event.catalog.elasticsearch.dto.session;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

import es.onebox.event.catalog.elasticsearch.enums.PresalesRedirectionLinkMode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PresalesRedirectionPolicy implements Serializable {

    @Serial
    private static final long serialVersionUID = 5629387491023456789L;

    private PresalesRedirectionLinkMode mode;

    private Map<String, String> url;

    public PresalesRedirectionLinkMode getMode() {
        return mode;
    }

    public void setMode(PresalesRedirectionLinkMode mode) {
        this.mode = mode;
    }

    public Map<String, String> getUrl() {
        return url;
    }

    public void setUrl(Map<String, String> url) {
        this.url = url;
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

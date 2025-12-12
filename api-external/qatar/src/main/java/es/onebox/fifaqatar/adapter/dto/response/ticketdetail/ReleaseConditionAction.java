package es.onebox.fifaqatar.adapter.dto.response.ticketdetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class ReleaseConditionAction implements Serializable {

    @Serial
    private static final long serialVersionUID = -4031418677603850762L;

    private String cta;
    @JsonProperty("is_enabled")
    private Boolean enabled;
    private String url;

    public String getCta() {
        return cta;
    }

    public void setCta(String cta) {
        this.cta = cta;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

package es.onebox.common.datasources.ms.event.dto.response.session.secmkt;

import java.io.Serial;
import java.io.Serializable;

public class SessionSecMktConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 3189635939458874186L;

    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}

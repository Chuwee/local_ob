package es.onebox.mgmt.datasources.ms.venue.dto.templateelements;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class TemplateInfo3DConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -1448379692964515687L;

    private Boolean enabled;
    private List<String> codes;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }
}

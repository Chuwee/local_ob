package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.channel.enums.DonationType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class DonationSettingsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 5555018121213597734L;

    @JsonProperty("type")
    private DonationType type;
    @JsonProperty("options")
    private Set<Double> options;

    public DonationType getType() {
        return type;
    }

    public void setType(DonationType type) {
        this.type = type;
    }

    public Set<Double> getOptions() {
        return options;
    }

    public void setOptions(Set<Double> options) {
        this.options = options;
    }
}

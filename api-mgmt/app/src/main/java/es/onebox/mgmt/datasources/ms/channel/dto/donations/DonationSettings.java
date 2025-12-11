package es.onebox.mgmt.datasources.ms.channel.dto.donations;

import es.onebox.mgmt.datasources.ms.channel.enums.DonationType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class DonationSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = 3134263952824100095L;

    private DonationType type;
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

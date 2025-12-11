package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import es.onebox.mgmt.datasources.ms.event.dto.event.Rate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class SeasonTicketRate extends Rate implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public SeasonTicketRate() {}

    public SeasonTicketRate(Long id, String name, Boolean restrictive, Boolean defaultRate, Boolean enabled, Map<String, String> translations) {
        super(id, name, restrictive, defaultRate, translations);
        this.enabled = enabled;
    }

    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

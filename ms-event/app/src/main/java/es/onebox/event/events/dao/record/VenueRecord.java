package es.onebox.event.events.dao.record;

import es.onebox.event.events.domain.VenueTemplateType;
import es.onebox.jooq.cpanel.tables.records.CpanelRecintoRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class VenueRecord extends CpanelRecintoRecord {

    private static final long serialVersionUID = 1L;

    private Long venueConfigId;
    private String venueConfigName;
    private VenueTemplateType venueConfigType;

    public Long getVenueConfigId() {
        return venueConfigId;
    }

    public void setVenueConfigId(Long venueConfigId) {
        this.venueConfigId = venueConfigId;
    }

    public String getVenueConfigName() {
        return venueConfigName;
    }

    public void setVenueConfigName(String venueConfigName) {
        this.venueConfigName = venueConfigName;
    }

    public VenueTemplateType getVenueConfigType() {
        return venueConfigType;
    }

    public void setVenueConfigType(VenueTemplateType venueConfigType) {
        this.venueConfigType = venueConfigType;
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

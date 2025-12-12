package es.onebox.common.datasources.catalog.dto.session.availability;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

public class ChannelSessionAvailabilityResponse extends IdNameDTO {

    private static final long serialVersionUID = -8328092166076078771L;

    protected ChannelSessionAvailability availability;
    protected List<ChannelSessionSector> sectors;

    public ChannelSessionAvailability getAvailability() {
        return availability;
    }

    public void setAvailability(ChannelSessionAvailability availability) {
        this.availability = availability;
    }

    public List<ChannelSessionSector> getSectors() {
        return sectors;
    }

    public void setSectors(List<ChannelSessionSector> sectors) {
        this.sectors = sectors;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}

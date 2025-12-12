package es.onebox.common.datasources.catalog.dto.session.availability;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

public class ChannelSessionVenueMapElement implements Serializable {

    private static final long serialVersionUID = -2351666594174095549L;

    protected List<ChannelSessionVenueMapLink> links;

    public List<ChannelSessionVenueMapLink> getLinks() {
        return links;
    }

    public void setLinks(List<ChannelSessionVenueMapLink> links) {
        this.links = links;
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

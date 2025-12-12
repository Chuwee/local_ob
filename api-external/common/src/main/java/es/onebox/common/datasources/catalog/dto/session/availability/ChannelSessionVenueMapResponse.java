package es.onebox.common.datasources.catalog.dto.session.availability;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class ChannelSessionVenueMapResponse extends IdNameCodeDTO {

    private static final long serialVersionUID = -8328092166076078771L;

    @JsonProperty("root_view")
    protected Boolean rootView;
    protected ChannelSessionVenueMapElement element;

    public Boolean getRootView() {
        return rootView;
    }

    public void setRootView(Boolean rootView) {
        this.rootView = rootView;
    }

    public ChannelSessionVenueMapElement getElement() {
        return element;
    }

    public void setElement(ChannelSessionVenueMapElement element) {
        this.element = element;
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

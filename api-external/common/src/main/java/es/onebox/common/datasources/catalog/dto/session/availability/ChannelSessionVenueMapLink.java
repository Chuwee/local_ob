package es.onebox.common.datasources.catalog.dto.session.availability;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

public class ChannelSessionVenueMapLink extends IdDTO {

    private static final long serialVersionUID = -8328092166076078771L;

    @JsonProperty("target_view")
    protected IdNameCodeDTO targetView;
    protected List<IdNameCodeDTO> sectors;

    public IdNameCodeDTO getTargetView() {
        return targetView;
    }

    public void setTargetView(IdNameCodeDTO targetView) {
        this.targetView = targetView;
    }

    public List<IdNameCodeDTO> getSectors() {
        return sectors;
    }

    public void setSectors(List<IdNameCodeDTO> sectors) {
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

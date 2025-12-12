package es.onebox.common.datasources.catalog.dto.session.availability;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

public class ChannelSessionSector extends IdNameCodeDTO {

    private static final long serialVersionUID = -8328092166076078771L;

    @JsonProperty("price_types")
    protected List<ChannelSessionPricetype> priceTypes;

    public List<ChannelSessionPricetype> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<ChannelSessionPricetype> priceTypes) {
        this.priceTypes = priceTypes;
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

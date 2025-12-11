package es.onebox.mgmt.channels.purchaseconfig.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.purchaseconfig.enums.ChannelSessionVisualizationFormat;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelPurchaseConfigVisualizationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("format")
    private ChannelSessionVisualizationFormat format;

    @JsonProperty("max_listed")
    private Integer maxListed;

    public ChannelSessionVisualizationFormat getFormat() {
        return format;
    }

    public void setFormat(ChannelSessionVisualizationFormat format) {
        this.format = format;
    }

    public Integer getMaxListed() {
        return maxListed;
    }

    public void setMaxListed(Integer maxListed) {
        this.maxListed = maxListed;
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

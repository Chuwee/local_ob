package es.onebox.mgmt.salerequests.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class PriceTypeDTO implements Serializable {
    private static final long serialVersionUID = 4299337968753342484L;

    private Long id;
    private String name;
    @JsonProperty("venue_template")
    private BaseVenueSaleRequestDTO venueConfig;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BaseVenueSaleRequestDTO getVenueConfig() {
        return venueConfig;
    }

    public void setVenueConfig(BaseVenueSaleRequestDTO venueConfig) {
        this.venueConfig = venueConfig;
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

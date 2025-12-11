package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.dto.RateGroupDataDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SessionRateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3980809690597061215L;
    private Long id;

    private String name;

    @JsonProperty("default")
    private Boolean isDefault;

    @JsonProperty("rate_group")
    private RateGroupDataDTO rateGroupDataDTO;

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

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public RateGroupDataDTO getRateGroupDataDTO() {
        return rateGroupDataDTO;
    }

    public void setRateGroupDataDTO(RateGroupDataDTO rateGroupDataDTO) {
        this.rateGroupDataDTO = rateGroupDataDTO;
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

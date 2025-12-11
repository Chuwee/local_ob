package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class RateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4417322762892006939L;
    private Long id;

    @JsonProperty("name")
    @Size(max = 50, message = "Rate name may have up to 50 chars")
    private String name;

    @JsonProperty("default")
    private Boolean defaultRate;

    public RateDTO() {
    }

    public RateDTO(Long id, String name, Boolean defaultRate) {
        this.id = id;
        this.name = name;
        this.defaultRate = defaultRate;
    }

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

    public Boolean getDefaultRate() {
        return defaultRate;
    }

    public void setDefaultRate(Boolean defaultRate) {
        this.defaultRate = defaultRate;
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

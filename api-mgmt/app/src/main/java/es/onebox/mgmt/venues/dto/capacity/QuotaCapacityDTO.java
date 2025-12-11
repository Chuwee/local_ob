package es.onebox.mgmt.venues.dto.capacity;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.LimitlessValueDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class QuotaCapacityDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("code")
    private String code;
    @JsonProperty("default")
    private Boolean defaultQuota;
    @JsonProperty("max_capacity")
    private LimitlessValueDTO maxCapacity;
    @JsonProperty("price_types")
    private List<IdCapacityDTO> priceTypes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getDefaultQuota() {
        return defaultQuota;
    }

    public void setDefaultQuota(Boolean defaultQuota) {
        this.defaultQuota = defaultQuota;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LimitlessValueDTO getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(LimitlessValueDTO maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public List<IdCapacityDTO> getPriceTypes() {
        return priceTypes;
    }

    public void setPriceTypes(List<IdCapacityDTO> priceTypes) {
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

}

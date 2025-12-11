package es.onebox.mgmt.sessions.dynamicprice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice.ConditionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;


public class RequestDynamicPriceDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3239241740049453216L;
    @NotNull(message = "name is required")
    private String name;

    private Integer capacity;
    @JsonProperty("valid_date")
    private ZonedDateTime validDate;
    @Valid
    @JsonProperty("condition_types")
    private Set<@NotNull ConditionType> conditionTypes;
    @NotNull(message = "order is required")
    private Integer order;
    @Valid
    @JsonProperty("dynamic_rates_price")
    private List<@NotNull DynamicRatesPriceDTO> dynamicRatesPriceDTO;
    @JsonProperty("translations")
    private List<DynamicPriceTranslationDTO> translationsDTO;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public ZonedDateTime getValidDate() {
        return validDate;
    }

    public void setValidDate(ZonedDateTime validDate) {
        this.validDate = validDate;
    }

    public Set<ConditionType> getConditionTypes() {
        return conditionTypes;
    }

    public void setConditionTypes(Set<ConditionType> conditionTypes) {
        this.conditionTypes = conditionTypes;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<DynamicRatesPriceDTO> getDynamicRatesPriceDTO() {
        return dynamicRatesPriceDTO;
    }

    public void setDynamicRatesPriceDTO(List<DynamicRatesPriceDTO> dynamicRatesPriceDTO) {
        this.dynamicRatesPriceDTO = dynamicRatesPriceDTO;
    }

    public List<DynamicPriceTranslationDTO> getTranslationsDTO() {
        return translationsDTO;
    }

    public void setTranslationsDTO(List<DynamicPriceTranslationDTO> translationsDTO) {
        this.translationsDTO = translationsDTO;
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

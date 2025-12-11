package es.onebox.mgmt.datasources.ms.event.dto.session.dynamicprice;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public class DynamicPrice implements Serializable {

    @Serial
    private static final long serialVersionUID = -6379525766563177227L;
    private String name;
    private Integer capacity;
    private ZonedDateTime validDate;
    private Set<ConditionType> conditionTypes;
    private Integer order;
    private List<DynamicRatesPrice> dynamicRatesPriceDTO;
    private List<DynamicPriceTranslation> translationsDTO;

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

    public List<DynamicRatesPrice> getDynamicRatesPriceDTO() {
        return dynamicRatesPriceDTO;
    }

    public void setDynamicRatesPriceDTO(List<DynamicRatesPrice> dynamicRatesPriceDTO) {
        this.dynamicRatesPriceDTO = dynamicRatesPriceDTO;
    }

    public List<DynamicPriceTranslation> getTranslationsDTO() {
        return translationsDTO;
    }

    public void setTranslationsDTO(List<DynamicPriceTranslation> translationsDTO) {
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

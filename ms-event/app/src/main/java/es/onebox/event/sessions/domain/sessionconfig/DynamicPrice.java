package es.onebox.event.sessions.domain.sessionconfig;

import es.onebox.event.sessions.dto.ConditionType;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public class DynamicPrice {

    private String name;
    private Integer capacity;
    private ZonedDateTime validDate;
    private Set<ConditionType> conditionTypes;
    private Integer order;
    private List<DynamicRatesPrice> dynamicRatesPrice;
    private List<DynamicPriceTranslation> translations;

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

    public List<DynamicRatesPrice> getDynamicRatesPrice() {
        return dynamicRatesPrice;
    }

    public void setDynamicRatesPrice(List<DynamicRatesPrice> dynamicRatesPrice) {
        this.dynamicRatesPrice = dynamicRatesPrice;
    }

    public List<DynamicPriceTranslation> getTranslations() {
        return translations;
    }

    public void setTranslations(List<DynamicPriceTranslation> translations) {
        this.translations = translations;
    }
}

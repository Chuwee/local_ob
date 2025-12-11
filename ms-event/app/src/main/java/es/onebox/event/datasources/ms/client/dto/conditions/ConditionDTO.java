package es.onebox.event.datasources.ms.client.dto.conditions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import es.onebox.event.datasources.ms.client.dto.conditions.generic.ConditionCurrencyValue;
import es.onebox.event.sessions.dto.ConditionType;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonSubTypes({
        @JsonSubTypes.Type(BookingExpirationDaysCondition.class),
        @JsonSubTypes.Type(CanBookCondition.class),
        @JsonSubTypes.Type(CanBuyCondition.class),
        @JsonSubTypes.Type(CanPublishCondition.class),
        @JsonSubTypes.Type(ClientDiscountCondition.class),
        @JsonSubTypes.Type(ClientDiscountPercentageCondition.class),
        @JsonSubTypes.Type(ClientCommissionCondition.class),
        @JsonSubTypes.Type(MaxBookedSeatsPerEventCondition.class),
        @JsonSubTypes.Type(PaymentMethodsCondition.class),
        @JsonSubTypes.Type(ShowTicketClientDiscountCondition.class),
        @JsonSubTypes.Type(ShowTicketPriceCondition.class),
})
public class ConditionDTO<T>  {

    @JsonProperty("type_id")
    protected Integer typeId;

    protected String type;

    @NotNull(message = "value can not be null")
    protected T value;

    @JsonProperty("condition_type")
    @NotNull(message = "condition_type can not be null")
    protected ConditionType conditionType;

    protected List<ConditionCurrencyValue> currencies;

    public ConditionDTO() {
    }

    public ConditionDTO(Integer typeId, T value) {
        this.typeId = typeId;
        this.value = value;
    }
    public ConditionDTO(Integer typeId, T value, List<ConditionCurrencyValue> currencies) {
        this.typeId = typeId;
        this.value = value;
        this.currencies = currencies;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public List<ConditionCurrencyValue> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<ConditionCurrencyValue> currencies) {
        this.currencies = currencies;
    }
}

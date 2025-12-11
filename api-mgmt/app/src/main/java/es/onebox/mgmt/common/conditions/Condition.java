package es.onebox.mgmt.common.conditions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import es.onebox.mgmt.common.conditions.conditiontype.BookingExpirationDaysCondition;
import es.onebox.mgmt.common.conditions.conditiontype.CanBookCondition;
import es.onebox.mgmt.common.conditions.conditiontype.CanBuyCondition;
import es.onebox.mgmt.common.conditions.conditiontype.CanPublishCondition;
import es.onebox.mgmt.common.conditions.conditiontype.ClientCommissionCondition;
import es.onebox.mgmt.common.conditions.conditiontype.ClientDiscountCondition;
import es.onebox.mgmt.common.conditions.conditiontype.ClientDiscountPercentageCondition;
import es.onebox.mgmt.common.conditions.conditiontype.ConditionType;
import es.onebox.mgmt.common.conditions.conditiontype.MaxBookedSeatsPerEventCondition;
import es.onebox.mgmt.common.conditions.conditiontype.PaymentMethodsCondition;
import es.onebox.mgmt.common.conditions.conditiontype.ShowTicketClientDiscountCondition;
import es.onebox.mgmt.common.conditions.conditiontype.ShowTicketPriceCondition;
import jakarta.validation.constraints.NotNull;


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
public class Condition<T>  {

    @JsonProperty("type_id")
    protected Integer typeId;

    protected String type;

    @NotNull(message = "value can not be null")
    protected T value;

    @JsonProperty("condition_type")
    @NotNull(message = "condition_type can not be null")
    protected ConditionType conditionType;

    public Condition() {
    }

    public Condition(Integer typeId, T value) {
        this.typeId = typeId;
        this.value = value;
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

}

package es.onebox.mgmt.b2b.conditions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.conditions.conditiontype.ConditionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ConditionDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private T value;

    @JsonProperty("condition_type")
    private ConditionType conditionType;

    @JsonProperty("currencies")
    private List<ConditionCurrenciesDTO> currenciesDTO;

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

    public List<ConditionCurrenciesDTO> getCurrenciesDTO() {
        return currenciesDTO;
    }

    public void setCurrenciesDTO(List<ConditionCurrenciesDTO> currenciesDTO) {
        this.currenciesDTO = currenciesDTO;
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

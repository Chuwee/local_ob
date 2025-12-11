package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.sessions.enums.SessionPassType;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Range;

import java.io.Serializable;

public class SessionExternalBarcodeConfigDTO implements Serializable {

    private static final Long serialVersionUID = 1L;

    @JsonProperty("person_type")
    private String personType;

    @JsonProperty("variable_code")
    private String variableCode;

    @NotNull(message = "pass_type can not be null")
    @JsonProperty("pass_type")
    private SessionPassType passType;

    @Range(min = 1, max = 10, message = "uses must be between 1 and 9")
    private Integer uses;

    @Range(min = 1, max = 10, message = "days must be between 1 and 9")
    private Integer days;

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getVariableCode() {
        return variableCode;
    }

    public void setVariableCode(String variableCode) {
        this.variableCode = variableCode;
    }

    public SessionPassType getPassType() {
        return passType;
    }

    public void setPassType(SessionPassType passType) {
        this.passType = passType;
    }

    public Integer getUses() {
        return uses;
    }

    public void setUses(Integer uses) {
        this.uses = uses;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
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

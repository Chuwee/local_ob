package es.onebox.mgmt.users.dto.ratelimit;

import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UserRateLimitConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean unlimited;
    @Valid
    private List<UserRateLimitRuleDTO> rules;


    public Boolean getUnlimited() {
        return unlimited;
    }

    public void setUnlimited(Boolean unlimited) {
        this.unlimited = unlimited;
    }

    public List<UserRateLimitRuleDTO> getRules() {
        return rules;
    }

    public void setRules(List<UserRateLimitRuleDTO> rules) {
        this.rules = rules;
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

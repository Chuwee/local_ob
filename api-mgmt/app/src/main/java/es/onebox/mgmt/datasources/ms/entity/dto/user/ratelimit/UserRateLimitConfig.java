package es.onebox.mgmt.datasources.ms.entity.dto.user.ratelimit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UserRateLimitConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean unlimited;
    private List<UserRateLimitRule> rules;

    public Boolean getUnlimited() {
        return unlimited;
    }

    public void setUnlimited(Boolean unlimited) {
        this.unlimited = unlimited;
    }

    public List<UserRateLimitRule> getRules() {
        return rules;
    }

    public void setRules(List<UserRateLimitRule> rules) {
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

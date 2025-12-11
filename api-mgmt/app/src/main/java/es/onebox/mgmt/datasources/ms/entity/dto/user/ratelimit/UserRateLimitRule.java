package es.onebox.mgmt.datasources.ms.entity.dto.user.ratelimit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UserRateLimitRule implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String pattern;
    private List<UserRateLimitQuota> quotas;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public List<UserRateLimitQuota> getQuotas() {
        return quotas;
    }

    public void setQuotas(List<UserRateLimitQuota> quotas) {
        this.quotas = quotas;
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

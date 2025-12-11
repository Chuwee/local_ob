package es.onebox.mgmt.users.dto.ratelimit;

import es.onebox.mgmt.validation.annotation.UrlPathPattern;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UserRateLimitRuleDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @UrlPathPattern
    private String pattern;
    @Valid
    private List<UserRateLimitQuotaDTO> quotas;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public List<UserRateLimitQuotaDTO> getQuotas() {
        return quotas;
    }

    public void setQuotas(List<UserRateLimitQuotaDTO> quotas) {
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

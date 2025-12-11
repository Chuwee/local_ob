package es.onebox.mgmt.seasontickets.dto.changeseats;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class LimitChangeSeatQuotasDTO implements Serializable {
    private static final long serialVersionUID = -5083810816453509198L;

    private Boolean enable;
    @JsonProperty("quota_ids")
    private List<Integer> quotaIds;

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public List<Integer> getQuotaIds() {
        return quotaIds;
    }

    public void setQuotaIds(List<Integer> quotaIds) {
        this.quotaIds = quotaIds;
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

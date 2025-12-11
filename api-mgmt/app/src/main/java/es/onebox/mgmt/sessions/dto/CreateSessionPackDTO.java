package es.onebox.mgmt.sessions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CreateSessionPackDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 5724268030088689630L;

    @JsonProperty("session_ids")
    private List<Long> packSessionIds;

    @JsonProperty("blocking_actions")
    private List<PackBlockingActionsDTO> packBlockingActions;

    private String color;

    @JsonProperty("allow_partial_refund")
    private Boolean allowPartialRefund;

    public List<Long> getPackSessionIds() {
        return packSessionIds;
    }

    public void setPackSessionIds(List<Long> packSessionIds) {
        this.packSessionIds = packSessionIds;
    }

    public List<PackBlockingActionsDTO> getPackBlockingActions() {
        return packBlockingActions;
    }

    public void setPackBlockingActions(List<PackBlockingActionsDTO> packBlockingActions) {
        this.packBlockingActions = packBlockingActions;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Boolean getAllowPartialRefund() {
        return allowPartialRefund;
    }

    public void setAllowPartialRefund(Boolean allowPartialRefund) {
        this.allowPartialRefund = allowPartialRefund;
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

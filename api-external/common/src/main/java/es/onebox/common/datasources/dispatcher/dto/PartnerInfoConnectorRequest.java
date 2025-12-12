package es.onebox.common.datasources.dispatcher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

public class PartnerInfoConnectorRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -5779475701007780919L;

    @JsonProperty("partner_id")
    private String memberId;

    @JsonProperty("person_id")
    private String personId;

    @JsonProperty("partner_pass")
    private String partnerPass;

    @JsonProperty("venue_id")
    private Long entityId;

    @JsonProperty("capacity_id")
    private Integer capacityId;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPartnerPass() {
        return partnerPass;
    }

    public void setPartnerPass(String partnerPass) {
        this.partnerPass = partnerPass;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Integer getCapacityId() {
        return capacityId;
    }

    public void setCapacityId(Integer capacityId) {
        this.capacityId = capacityId;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}

package es.onebox.mgmt.events.eventchannel.b2b.dto;

import es.onebox.mgmt.events.eventchannel.b2b.enums.ChannelEventQuotaAssignationType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class EventChannelB2BAssignationsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "type can not be null")
    private ChannelEventQuotaAssignationType type;

    @Valid
    private List<QuotaClientAssignationDTO> assignations;

    public ChannelEventQuotaAssignationType getType() {
        return type;
    }

    public void setType(ChannelEventQuotaAssignationType type) {
        this.type = type;
    }

    public List<QuotaClientAssignationDTO> getAssignations() {
        return assignations;
    }

    public void setAssignations(List<QuotaClientAssignationDTO> assignations) {
        this.assignations = assignations;
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

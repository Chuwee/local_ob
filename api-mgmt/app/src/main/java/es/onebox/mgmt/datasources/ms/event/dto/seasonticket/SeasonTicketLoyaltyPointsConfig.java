package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketLoyaltyPointsConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<SessionLoyaltyPoints> sessions;

    public List<SessionLoyaltyPoints> getSessions() { return sessions; }

    public void setSessions(List<SessionLoyaltyPoints> sessions) { this.sessions = sessions; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
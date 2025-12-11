package es.onebox.event.datasources.ms.ticket.dto.occupation;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.datasources.ms.ticket.dto.SessionWithQuotasDTO;

public class SessionOccupationByPriceZoneDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private SessionWithQuotasDTO session;

    private List<SessionPriceZoneOccupationDTO> occupation;

    public SessionWithQuotasDTO getSession() {
        return session;
    }

    public void setSession(SessionWithQuotasDTO session) {
        this.session = session;
    }

    public List<SessionPriceZoneOccupationDTO> getOccupation() {
        return occupation;
    }

    public void setOccupation(List<SessionPriceZoneOccupationDTO> occupation) {
        this.occupation = occupation;
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

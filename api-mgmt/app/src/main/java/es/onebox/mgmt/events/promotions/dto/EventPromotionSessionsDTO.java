package es.onebox.mgmt.events.promotions.dto;

import es.onebox.mgmt.channels.promotions.dto.EventPromotionSessionDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

public class EventPromotionSessionsDTO extends PromotionType {

    private static final long serialVersionUID = 2L;

	private Set<EventPromotionSessionDTO> sessions;

    public Set<EventPromotionSessionDTO> getSessions() {
        return sessions;
    }

    public void setSessions(Set<EventPromotionSessionDTO> sessions) {
        this.sessions = sessions;
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

package es.onebox.common.datasources.ms.promotion.dto;

import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PromotionEventSessionsDTO extends PromotionTarget {

    private static final long serialVersionUID = 2L;

    private Set<EventSessionDTO> sessions;

    public Set<EventSessionDTO> getSessions() {
        return sessions;
    }

    public void setSessions(Set<EventSessionDTO> sessions) {
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

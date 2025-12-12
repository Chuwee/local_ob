package es.onebox.common.datasources.webhook.dto.fever.promotion;

import es.onebox.common.datasources.webhook.dto.fever.event.EventSessionFeverDTO;
import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PromotionEventSessionsFeverDTO extends PromotionTarget {

    private static final long serialVersionUID = 2L;

    private Set<EventSessionFeverDTO> sessions;

    public Set<EventSessionFeverDTO> getSessions() {
        return sessions;
    }

    public void setSessions(Set<EventSessionFeverDTO> sessions) {
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

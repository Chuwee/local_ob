package es.onebox.mgmt.datasources.ms.promotion.dto;

import es.onebox.mgmt.common.promotions.dto.PromotionTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

public class UpdateEventPromotionSessions extends PromotionTarget {

	private static final long serialVersionUID = 1L;

    private Set<Long> sessions;

    public Set<Long> getSessions() {
        return sessions;
    }

    public void setSessions(Set<Long> sessions) {
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

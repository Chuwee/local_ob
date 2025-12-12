package es.onebox.ms.notification.webhooks.dto.promotion;

import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UpdatePromotionSessionsDTO extends PromotionTarget {

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

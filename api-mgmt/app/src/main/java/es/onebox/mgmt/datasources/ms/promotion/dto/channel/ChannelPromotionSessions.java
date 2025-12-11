package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.common.promotions.dto.PromotionTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

public class ChannelPromotionSessions extends PromotionTarget {

    private static final long serialVersionUID = 2541117156268234758L;

    private Set<ChannelPromotionSession> sessions;

    public Set<ChannelPromotionSession> getSessions() {
        return sessions;
	}

    public void setSessions(Set<ChannelPromotionSession> sessions) {
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

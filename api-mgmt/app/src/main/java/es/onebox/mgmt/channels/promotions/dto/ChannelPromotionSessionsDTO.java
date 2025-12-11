package es.onebox.mgmt.channels.promotions.dto;

import es.onebox.mgmt.common.promotions.dto.PromotionTarget;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

public class ChannelPromotionSessionsDTO extends PromotionTarget {

    private static final long serialVersionUID = 1071373053918113857L;

    private Set<ChannelPromotionSessionDTO> sessions;

    public Set<ChannelPromotionSessionDTO> getSessions() {
        return sessions;
    }

    public void setSessions(Set<ChannelPromotionSessionDTO> sessions) {
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

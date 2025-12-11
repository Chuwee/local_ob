package es.onebox.mgmt.common.promotions.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Set;

public class PromotionChannelsDTO extends PromotionType {

    private static final long serialVersionUID = 2L;

	private Set<IdNameDTO> channels;

	public Set<IdNameDTO> getChannels() {
		return channels;
	}

	public void setChannels(Set<IdNameDTO> channels) {
		this.channels = channels;
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

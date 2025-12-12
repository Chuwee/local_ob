package es.onebox.ms.notification.webhooks.dto.promotion;

import java.util.Set;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class UpdateEventPromotionChannelsDTO extends PromotionTarget {

    private static final long serialVersionUID = 1L;

	private Set<Long> channels;

	public Set<Long> getChannels() {
		return channels;
	}

	public void setChannels(Set<Long> channels) {
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

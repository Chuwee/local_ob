package es.onebox.mgmt.events.dto.channel;

import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class EventChannelsResponse extends ListWithMetadata<BaseEventChannelDTO> implements DateConvertible {

    private static final long serialVersionUID = 1L;

    @Override
    public void convertDates() {
        if (!CommonUtils.isEmpty(getData())) {
            for (BaseEventChannelDTO eventChannel : getData()) {
                eventChannel.convertDates();
            }
        }
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

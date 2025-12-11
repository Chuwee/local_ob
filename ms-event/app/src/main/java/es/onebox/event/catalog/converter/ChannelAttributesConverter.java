package es.onebox.event.catalog.converter;

import es.onebox.event.catalog.elasticsearch.dto.attributes.ChannelAttributesDTO;
import es.onebox.event.datasources.ms.channel.dto.attributes.ChannelAttributes;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChannelAttributesConverter {

    private ChannelAttributesConverter() {
    }

    public static Map<Integer, ChannelAttributes> convert(List<ChannelAttributesDTO> in) {
        if (CollectionUtils.isEmpty(in)) {
            return null;
        }
        return in.stream().collect(Collectors.toMap(ChannelAttributesDTO::getChannelId, ChannelAttributesConverter::convert));
    }

    private static ChannelAttributes convert(ChannelAttributesDTO in) {
        ChannelAttributes out = new ChannelAttributes();
        out.setHiddenBillboardEvents(in.getHiddenBillboardEvents());
        if (in.getEventsCustomisedOrder() != null && BooleanUtils.isTrue(in.getEventsCustomisedOrder().getUseCustomOrder())) {
            out.setCustomEventsOrder(in.getEventsCustomisedOrder().getEventsOrder());
        }
        if (in.getCarousel() != null && in.getCarousel().getEventsOrder() != null) {
            out.setCarouselEventsOrder(in.getCarousel().getEventsOrder());
        }
        if (in.getExtendedEvents() != null) {
            out.setExtendedEvents(in.getExtendedEvents());
        }
        return out;
    }
}

package es.onebox.mgmt.channels.catalog.converter;

import es.onebox.mgmt.channels.catalog.dto.ChannelEventCatalogDataDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventFilter;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventInfoDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventUpdateDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventUpdateDetailDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventsDTO;
import es.onebox.mgmt.channels.catalog.dto.ChannelEventsUpdateDTO;
import es.onebox.mgmt.currencies.CurrenciesUtils;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEvent;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventMsFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEvents;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventsUpdate;
import es.onebox.mgmt.datasources.ms.entity.dto.Currency;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelEventConverter {

    private ChannelEventConverter() {
    }

    public static ChannelEventMsFilter toFilter(ChannelEventFilter filter) {
        ChannelEventMsFilter out = new ChannelEventMsFilter();
        out.setLimit(filter.getLimit());
        out.setOffset(filter.getOffset());
        out.setOnSale(filter.getOnSale());
        out.setPublished(filter.getPublished());
        out.setQ(filter.getQ());
        return out;
    }

    public static ChannelEventsDTO toDTO(ChannelEvents in, List<Currency> currencies) {
        ChannelEventsDTO out = new ChannelEventsDTO();
        out.setMetadata(in.getMetadata());
        if (CollectionUtils.isEmpty(in.getData())) {
            return out;
        }
    out.setData(in.getData().stream().map(event -> toDTO(event, currencies)).toList());
        return out;
    }

    public static ChannelEventDTO toDTO(ChannelEvent in, List<Currency> currencies) {
        ChannelEventDTO out = new ChannelEventDTO();
        out.setId(in.getId());
        out.setPublished(in.getPublished());
        out.setOnSale(in.getOnSale());
        out.setStatus(in.getStatus());
        out.setCatalog(toDTOCatalogData(in));
        map(in, out, currencies);
        return out;
    }

    private static ChannelEventCatalogDataDTO toDTOCatalogData(ChannelEvent in) {
        ChannelEventCatalogDataDTO out = new ChannelEventCatalogDataDTO();
        out.setPosition(in.getCatalogPosition());
        out.setVisible(in.getOnCatalog() == null || in.getOnCatalog());
        out.setCarouselPosition(in.getCarouselPosition());
        out.setExtended(in.getExtended());
        return out;
    }

    public static ChannelEventsUpdate fromDTO(ChannelEventsUpdateDTO in) {
        return in.stream().map(ChannelEventConverter::fromDTO)
                .collect(Collectors.toCollection(ChannelEventsUpdate::new));
    }

    private static ChannelEventUpdate fromDTO(ChannelEventUpdateDTO in) {
        ChannelEventUpdate out = new ChannelEventUpdate();
        if (in.getCatalog() != null) {
            out.setOnCatalog(in.getCatalog().getVisible());
            out.setCatalogPosition(in.getCatalog().getPosition());
            out.setCarouselPosition(in.getCatalog().getCarouselPosition());
            out.setExtended(in.getCatalog().getExtended());
        }
        out.setEventId(in.getEventId());
        return out;
    }

    public static ChannelEventUpdate fromDTO(ChannelEventUpdateDetailDTO in) {
        ChannelEventUpdate out = new ChannelEventUpdate();
        if (in.getCatalog() != null) {
            out.setOnCatalog(in.getCatalog().getVisible());
            out.setCatalogPosition(in.getCatalog().getPosition());
        }
        return out;
    }

    private static void map(ChannelEvent in, ChannelEventDTO out, List<Currency> currencies) {
        ChannelEventInfoDTO info = new ChannelEventInfoDTO();
        info.setId(in.getEventId());
        info.setName(in.getEventName());
        info.setStartDate(in.getStartDate());
        info.setEndDate(in.getEndDate());
        info.setCurrency(CurrenciesUtils.getCurrencyCode(currencies, in.getCurrencyId().longValue()));
        out.setEvent(info);
    }
}

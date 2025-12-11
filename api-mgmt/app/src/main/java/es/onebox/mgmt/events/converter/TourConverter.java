package es.onebox.mgmt.events.converter;

import es.onebox.mgmt.datasources.ms.event.dto.tour.Tour;
import es.onebox.mgmt.datasources.ms.event.dto.tour.TourEvent;
import es.onebox.mgmt.datasources.ms.event.dto.tour.TourEventFilter;
import es.onebox.mgmt.events.enums.EventStatus;
import es.onebox.mgmt.events.enums.TourStatus;
import es.onebox.mgmt.events.tours.dto.BaseTourDTO;
import es.onebox.mgmt.events.tours.dto.TourDTO;
import es.onebox.mgmt.events.tours.dto.TourEventDTO;
import es.onebox.mgmt.events.tours.dto.TourEventFilterDTO;

import java.util.stream.Collectors;

public class TourConverter {

    private TourConverter() {
    }

    public static TourDTO fromMsEvent(Tour tour) {
        TourDTO result = null;
        if (tour != null) {
            result = (TourDTO) fromMsEventToBase(tour, new TourDTO());
            result.setEvents(tour.getEvents().stream()
                    .map(TourConverter::toDTO)
                    .collect(Collectors.toList()));
        }
        return result;
    }

    public static BaseTourDTO fromMsEventToBase(Tour tour) {
        if (tour == null) {
            return null;
        }
        return fromMsEventToBase(tour, new BaseTourDTO());
    }

    public static BaseTourDTO fromMsEventToBase(Tour tour, BaseTourDTO target) {
        if (tour != null) {
            target.setId(tour.getId());
            target.setName(tour.getName());
            target.setStatus(TourStatus.valueOf(tour.getStatus().name()));
            target.setEntity(tour.getEntity());
        }
        return target;
    }

    private static TourEventDTO toDTO(TourEvent source) {
        TourEventDTO target = new TourEventDTO();
        target.setName(source.getName());
        target.setId(source.getId());
        target.setArchived(source.getArchived());
        target.setCapacity(source.getCapacity());
        target.setStartDate(source.getStartDate());
        target.setStatus(EventStatus.valueOf(source.getStatus().name()));
        return target;
    }

    public static TourEventFilter toMs(TourEventFilterDTO source) {
        TourEventFilter target = new TourEventFilter();
        target.setLimit(source.getLimit());
        target.setOffset(source.getOffset());
        target.setSort(source.getSort());
        return target;
    }

}

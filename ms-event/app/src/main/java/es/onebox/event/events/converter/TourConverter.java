package es.onebox.event.events.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.TourRecord;
import es.onebox.event.events.dto.BaseTourDTO;
import es.onebox.event.events.dto.DateDTO;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.dto.EventsDTO;
import es.onebox.event.events.dto.TourDTO;
import es.onebox.event.events.dto.TourEventDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.TourStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelGiraRecord;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TourConverter {

    private TourConverter() {
    }

    public static TourDTO convert(Map.Entry<TourRecord, List<EventRecord>> tour) {
        if (tour == null) {
            return null;
        }
        TourDTO tourDTO = (TourDTO) convert(tour.getKey(), new TourDTO());
        tourDTO.setEvents(tour.getValue().stream()
                .map(TourConverter::toDTO)
                .collect(Collectors.toList()));
        return tourDTO;
    }

    public static BaseTourDTO convert(TourRecord tour) {
        if (tour == null) {
            return null;
        }
        return convert(tour, new BaseTourDTO());
    }

    public static void updateRecord(CpanelGiraRecord tourRecord, BaseTourDTO tourDTO) {
        ConverterUtils.updateField(tourRecord::setNombre, tourDTO.getName());
        if (tourDTO.getStatus() != null) {
            ConverterUtils.updateField(tourRecord::setEstado, tourDTO.getStatus().getId());
        }
    }

    private static BaseTourDTO convert(TourRecord tour, BaseTourDTO target) {
        target.setId(tour.getIdgira().longValue());
        target.setName(tour.getNombre());
        target.setStatus(TourStatus.byId(tour.getEstado()));
        target.setEntity(new IdNameDTO(tour.getIdentidad().longValue(), tour.getEntityName()));
        return target;
    }

    private static TourEventDTO toDTO(EventRecord event) {
        TourEventDTO target = new TourEventDTO();
        target.setId(event.getIdevento().longValue());
        target.setName(event.getNombre());
        target.setStatus(EventStatus.byId(event.getEstado()));
        target.setArchived(ConverterUtils.isByteAsATrue(event.getArchivado()));
        target.setCapacity(event.getAforo());
        target.setStartDate(CommonUtils.timestampToZonedDateTime(event.getFechainicio()));
        return target;
    }
}

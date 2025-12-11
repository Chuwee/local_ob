package es.onebox.event.events.service;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.enums.EventTagType;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.events.converter.EventCommunicationElementConverter;
import es.onebox.event.events.converter.TourConverter;
import es.onebox.event.events.dao.EventCommunicationElementDao;
import es.onebox.event.events.dao.TourDao;
import es.onebox.event.events.dao.record.EventRecord;
import es.onebox.event.events.dao.record.TourRecord;
import es.onebox.event.events.dto.BaseTourDTO;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.dto.TourDTO;
import es.onebox.event.events.dto.ToursDTO;
import es.onebox.event.events.enums.TourStatus;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.event.events.request.TourEventsFilter;
import es.onebox.event.events.request.ToursFilter;
import es.onebox.event.events.utils.EventCommunicationElementUtils;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelGiraRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static es.onebox.core.exception.CoreErrorCode.BAD_PARAMETER;
import static es.onebox.event.exception.MsEventErrorCode.INVALID_NAME_CONFLICT;

@Service
public class TourService {

    private final TourDao tourDao;
    private final EventCommunicationElementDao communicationElementDao;
    private final StaticDataContainer staticDataContainer;
    private final S3BinaryRepository s3OneboxRepository;
    private final RefreshDataService refreshDataService;

    @Autowired
    public TourService(TourDao tourDao, EventCommunicationElementDao communicationElementDao,
                       StaticDataContainer staticDataContainer, S3BinaryRepository s3OneboxRepository,
                       RefreshDataService refreshDataService) {
        this.tourDao = tourDao;
        this.communicationElementDao = communicationElementDao;
        this.staticDataContainer = staticDataContainer;
        this.s3OneboxRepository = s3OneboxRepository;
        this.refreshDataService = refreshDataService;
    }

    @MySQLRead
    public TourDTO getTour(Long tourId, TourEventsFilter filter) {
        Map.Entry<TourRecord, List<EventRecord>> tour = tourDao.findWithEvents(tourId.intValue(), filter);
        checkTour(tour != null ? tour.getKey() : null);
        return TourConverter.convert(tour);
    }

    @MySQLRead
    public ToursDTO findTours(ToursFilter filter) {
        if (filter.getOperatorId() == null || filter.getOperatorId() <= 0) {
            throw new OneboxRestException(BAD_PARAMETER,
                    "operator Id is mandatory and must be above 0", null);
        }

        ToursDTO toursDTO = new ToursDTO();
        toursDTO.setData(
                tourDao.find(filter).stream()
                        .map(TourConverter::convert)
                        .collect(Collectors.toList()));
        toursDTO.setMetadata(MetadataBuilder.build(filter, tourDao.countByFilter(filter)));
        return toursDTO;
    }

    @MySQLWrite
    public Integer createTour(BaseTourDTO tourDTO) {
        checkTourName(tourDTO.getName(), null, tourDTO.getEntity().getId().intValue());

        CpanelGiraRecord tour = new CpanelGiraRecord();
        tour.setNombre(tourDTO.getName());
        tour.setIdentidad(tourDTO.getEntity().getId().intValue());
        tour.setEstado(TourStatus.INACTIVE.getId());
        tour.setReferenciapromotor(tour.getNombre());

        CpanelGiraRecord insert = tourDao.insert(tour);

        return insert.getIdgira();
    }

    @MySQLWrite
    public void updateTour(Long tourId, BaseTourDTO tourDTO) {
        TourRecord tour = getAndCheckTour(tourId);

        checkTourName(tourDTO.getName(), tour.getNombre(), tour.getIdentidad());

        TourConverter.updateRecord(tour, tourDTO);
        if (tour.changed()) {
            tourDao.update(tour);
        }
    }

    @MySQLRead
    public List<EventCommunicationElementDTO> findCommunicationElements(Long tourId, EventCommunicationElementFilter filter) {
        TourRecord tour = getAndCheckTour(tourId);

        List<CpanelElementosComEventoRecord> records =
                communicationElementDao.findCommunicationElements(null, null, tourId, filter);

        return EventCommunicationElementConverter.fromRecords(records, tour, staticDataContainer);
    }

    @MySQLWrite
    public void updateCommunicationElements(Long tourId, List<EventCommunicationElementDTO> elements) {
        TourRecord tour = getAndCheckTour(tourId);

        List<CpanelElementosComEventoRecord> records =
                communicationElementDao.findCommunicationElements(null, null, tourId, null);

        for (EventCommunicationElementDTO element : elements) {
            Integer languageId = staticDataContainer.getLanguageByCode(element.getLanguage());
            CpanelElementosComEventoRecord record = EventCommunicationElementUtils.checkAndGetElement(element, languageId, records);

            if (record == null) {
                CpanelElementosComEventoRecord newRecord = new CpanelElementosComEventoRecord();
                newRecord.setIdgira(tourId.intValue());
                newRecord.setIdtag(element.getTagId());
                newRecord.setIdioma(languageId);
                newRecord.setDestino(1);
                newRecord.setValor(element.getValue());
                record = communicationElementDao.insert(newRecord);
            }

            EventTagType tagType = EventTagType.getTagTypeById(element.getTagId());
            if (tagType.isImage()) {
                String filename = EventCommunicationElementUtils.uploadImage(s3OneboxRepository, record, element,
                        S3URLResolver.S3ImageType.TOUR_IMAGE, tour.getOperatorId(), tour.getIdentidad(),
                        tourId, null, false);
                record.setValor(filename);
                record.setAlttext(element.getAltText());
            } else {
                record.setValor(element.getValue());
            }
            communicationElementDao.update(record);
        }
    }

    @MySQLRead
    public void postUpdateTourEvents(Long tourId, EventIndexationType refreshType) {
        List<Long> tourEvents = tourDao.findTourEvents(tourId.intValue());
        for (Long eventId : tourEvents) {
            refreshDataService.refreshEvent(eventId, "postUpdateTourEvents", refreshType);
        }
    }

    private TourRecord getAndCheckTour(Long tourId) {
        if (tourId == null || tourId <= 0) {
            throw new OneboxRestException(BAD_PARAMETER, "tourId must have a value and be greater than 0", null);
        }
        TourRecord tour = tourDao.find(tourId.intValue());
        checkTour(tour);
        return tour;
    }

    private void checkTourName(String name, String oldName, Integer entityId) {
        if (name != null && (oldName == null || !oldName.equals(name)) &&
                tourDao.countByNameAndEntity(name, entityId) > 0L) {
            throw new OneboxRestException(INVALID_NAME_CONFLICT);
        }
    }

    private void checkTour(CpanelGiraRecord tour) {
        if (tour == null || TourStatus.DELETED.getId().equals(tour.getEstado())) {
            throw new OneboxRestException(MsEventErrorCode.TOUR_NOT_FOUND);
        }
    }

}

package es.onebox.event.externalevents.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.externalevents.controller.dto.ExternalEventDTO;
import es.onebox.event.externalevents.controller.dto.ExternalEventRateDTO;
import es.onebox.event.externalevents.controller.dto.ExternalEventTypeDTO;
import es.onebox.event.externalevents.converter.ExternalEventsConverter;
import es.onebox.event.externalevents.dao.ExternalEventRatesDao;
import es.onebox.event.externalevents.dao.ExternalEventsDao;
import es.onebox.jooq.cpanel.tables.records.CpanelExternalEventRatesRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelExternalEventRecord;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ExternalEventsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalEventsService.class);

    private final ExternalEventsDao externalEventsDao;
    private final ExternalEventRatesDao externalEventRatesDao;

    @Autowired
    public ExternalEventsService(ExternalEventsDao externalEventsDao, ExternalEventRatesDao externalEventRatesDao) {
        this.externalEventsDao = externalEventsDao;
        this.externalEventRatesDao = externalEventRatesDao;
    }

    public List<ExternalEventDTO> getExternalEvents(List<Integer> entityId, ExternalEventTypeDTO eventType) {
        List<CpanelExternalEventRecord> externalEventsByEntityIds;
        if (entityId == null && eventType == null) {
            externalEventsByEntityIds = externalEventsDao.getAll();
        } else {
            externalEventsByEntityIds = externalEventsDao.getExternalEvents(entityId, eventType);
        }
        return externalEventsByEntityIds.stream()
                .map(ExternalEventsConverter::newExternalEventDTO)
                .collect(Collectors.toList());
    }

    public void upsertExternalEvents(ExternalEventDTO[] externalEvents) {
        List<Integer> entityIds = Arrays.stream(externalEvents)
                .map(ExternalEventDTO::getEntityId)
                .distinct()
                .collect(Collectors.toList());
        List<CpanelExternalEventRecord> allExistingExternalEvents = externalEventsDao
                .getExternalEvents(entityIds, null);
        createNewExternalEvents(externalEvents, allExistingExternalEvents);
        updateExistingExternalEvents(externalEvents, allExistingExternalEvents);
    }

    private void createNewExternalEvents(ExternalEventDTO[] externalEvents,
                                         List<CpanelExternalEventRecord> allExistingExternalEvents) {
        Set<CpanelExternalEventRecord> newExternalEvents = Arrays.stream(externalEvents)
                .filter(externalEventDTO -> doesExternalEventExistOnDatabase(allExistingExternalEvents,
                        externalEventDTO))
                .map(ExternalEventsConverter::newCpanelExternalEventRecord)
                .collect(Collectors.toSet());
        externalEventsDao.insertBatch(newExternalEvents);
    }

    private boolean doesExternalEventExistOnDatabase(List<CpanelExternalEventRecord> allExistingExternalEvents,
                                                     ExternalEventDTO externalEventDTO) {
        return allExistingExternalEvents.stream()
                .filter(existingExternalEvent -> doesExternalEventExistOnDatabase(existingExternalEvent,
                        externalEventDTO))
                .findFirst()
                .orElse(null) == null;
    }

    private boolean doesExternalEventExistOnDatabase(CpanelExternalEventRecord dbExternalEvent,
                                                     ExternalEventDTO paramExternalEvent) {
        return dbExternalEvent.getExternaleventid().equals(paramExternalEvent.getEventId()) &&
            dbExternalEvent.getEntityid().equals(paramExternalEvent.getEntityId());
    }

    private void updateExistingExternalEvents(ExternalEventDTO[] externalEvents,
                                              List<CpanelExternalEventRecord> allExistingExternalEvents) {
        Set<CpanelExternalEventRecord> existingExternalEvents = Arrays.stream(externalEvents)
                .filter(externalEventDTO -> !doesExternalEventExistOnDatabase(allExistingExternalEvents,
                        externalEventDTO))
                .map(ExternalEventsConverter::newCpanelExternalEventRecord)
                .collect(Collectors.toSet());
        existingExternalEvents.forEach(externalEventsDao::update);
    }

    public List<IdNameDTO> getRatesForExternalEvent(Long internalId) {
        // Verify that external event exists
        getExternalEvent(internalId);

        List<CpanelExternalEventRatesRecord> ratesRecords = externalEventRatesDao.getRatesForExternalEvents(Collections.singleton(internalId.intValue()));
        return ratesRecords.stream().map(ExternalEventsConverter::convertRateRecord).collect(Collectors.toList());
    }

    public void upsertExternalEventRates(ExternalEventRateDTO[] externalEventRates) {
        List<Integer> entityIds = Arrays.stream(externalEventRates)
                .map(ExternalEventRateDTO::getEntityId)
                .distinct()
                .collect(Collectors.toList());
        List<CpanelExternalEventRecord> allExistingExternalEvents = externalEventsDao
                .getExternalEvents(entityIds, null);

        Map<Pair<String, Integer>, Integer> mapOfExistingExternalEventsAndTheirInternalId =
                getMapOfExistingExternalEventsAndTheirInternalId(allExistingExternalEvents);

        Map<Integer, List<String>> mapOfExistingRatesFromExternalEvents =
                getMapOfExistingRatesFromExternalEvents(mapOfExistingExternalEventsAndTheirInternalId.values());

        Set<CpanelExternalEventRatesRecord> ratesRecords = Arrays.stream(externalEventRates).map(rate -> {
            Integer internalId = mapOfExistingExternalEventsAndTheirInternalId.get(new ImmutablePair<>(rate.getEventId(), rate.getEntityId()));
            if(internalId != null) {
                if(mapOfExistingRatesFromExternalEvents.getOrDefault(internalId, new ArrayList<>()).contains(rate.getRateName())) {
                    LOGGER.info("[EXTERNAL EVENTS] UPSERT: Rate {} exists for external event {}", rate.getRateName(), internalId);
                    return null;
                } else {
                    return ExternalEventsConverter.convertToRateRecord(rate, internalId);
                }
            } else {
                LOGGER.error("[EXTERNAL EVENTS] UPSERT: Not existing external event {} for entity {} ", rate.getEventId(), rate.getEntityId());
                return null;
            }
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());
        if(!ratesRecords.isEmpty()) {
            externalEventRatesDao.insertBatch(ratesRecords);
        }
    }

    private Map<Pair<String, Integer>, Integer> getMapOfExistingExternalEventsAndTheirInternalId(List<CpanelExternalEventRecord> externalEvents) {
        return externalEvents.stream()
                .collect(Collectors.toMap(
                        externalEvent -> new ImmutablePair<>(externalEvent.getExternaleventid(), externalEvent.getEntityid()),
                        CpanelExternalEventRecord::getInternalid
                ));
    }

    private Map<Integer, List<String>> getMapOfExistingRatesFromExternalEvents(Collection<Integer> internalIds) {
        List<CpanelExternalEventRatesRecord> ratesRecords = externalEventRatesDao.getRatesForExternalEvents(internalIds);
        return ratesRecords.stream()
                .collect(Collectors.groupingBy(
                        CpanelExternalEventRatesRecord::getExternaleventinternalid,
                        Collectors.mapping(CpanelExternalEventRatesRecord::getRatename, Collectors.toList())
                ));
    }

    public ExternalEventDTO getExternalEvent(Long internalId) {
        try {
            CpanelExternalEventRecord externalEventRecord = externalEventsDao.getById(internalId);
            return ExternalEventsConverter.newExternalEventDTO(externalEventRecord);
        } catch (EntityNotFoundException ex) {
            throw OneboxRestException
                    .builder(MsEventErrorCode.EXTERNAL_EVENT_NOT_FOUND)
                    .setMessage("External Event not found for id: " + internalId)
                    .build();
        }
    }
}

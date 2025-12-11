package es.onebox.event.events.service;

import java.util.HashMap;
import java.util.Map;

import es.onebox.event.datasources.integration.dispatcher.dto.ExternalEvent;
import es.onebox.event.datasources.integration.dispatcher.repository.IntDispatcherRepository;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.attendants.AttendantsConfigService;
import es.onebox.event.common.ExternalDataConstants;
import es.onebox.event.datasources.integration.avet.config.dto.Competition;
import es.onebox.event.datasources.integration.avet.config.repository.IntAvetConfigRepository;
import es.onebox.event.datasources.integration.dispatcher.dto.ConnectorRelation;
import es.onebox.event.datasources.integration.dispatcher.repository.ConnectorsRelationRepository;
import es.onebox.event.events.converter.ConnectorRelationConverter;
import es.onebox.event.events.dao.EventAvetConfigCouchDao;
import es.onebox.event.events.dao.IntegrationConnectorDao;
import es.onebox.event.events.domain.eventconfig.EventAvetConfig;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.dto.CreateEventRequestDTO;
import es.onebox.event.events.dto.EventDTO;
import es.onebox.event.events.enums.EventAvetConfigType;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import es.onebox.jooq.cpanel.tables.records.IntegrationConnectorRecord;

@Service
public class EventExternalService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EventExternalService.class);


    private final EventAvetConfigCouchDao eventAvetConfigCouchDao;
    private final IntAvetConfigRepository intAvetConfigRepository;
    private final ConnectorsRelationRepository connectorRelationsRepository;
    private final AttendantsConfigService attendantsConfigService;
    private final AttendantFieldsService attendantFieldsService;
    private final IntegrationConnectorDao integrationConnectorDao;
    private final IntDispatcherRepository intDispatcherRepository;

    public EventExternalService(EventAvetConfigCouchDao eventAvetConfigCouchDao, IntAvetConfigRepository intAvetConfigRepository,
                                ConnectorsRelationRepository connectorRelationsRepository, AttendantsConfigService attendantsConfigService,
                                AttendantFieldsService attendantFieldsService, IntegrationConnectorDao integrationConnectorDao, IntDispatcherRepository intDispatcherRepository) {
        this.eventAvetConfigCouchDao = eventAvetConfigCouchDao;
        this.intAvetConfigRepository = intAvetConfigRepository;
        this.connectorRelationsRepository = connectorRelationsRepository;
        this.attendantsConfigService = attendantsConfigService;
        this.attendantFieldsService = attendantFieldsService;
        this.integrationConnectorDao = integrationConnectorDao;
        this.intDispatcherRepository = intDispatcherRepository;
    }
    
    
    public IntegrationConnectorRecord getConnectorByName(String name) {
        return integrationConnectorDao.getByName(name);
    }

    public void createEventConnectorRelationship(Provider provider, Long eventId) {
        IntegrationConnectorRecord connector = this.getConnectorByName(provider.getConnector());
        if (connector != null) {
            ConnectorRelation cr = ConnectorRelationConverter.toIntegration(eventId.intValue(), connector.getConnectorid());
            this.connectorRelationsRepository.createConnectorsRelation(cr);
        }
    }
    
    public void createEventAvetConfig(CreateEventRequestDTO event, CpanelEventoRecord newEvent) {
        EventAvetConfig avetConfig = new EventAvetConfig();
        avetConfig.setEventId(newEvent.getIdevento());
        avetConfig.setIsSocket(EventAvetConfigType.SOCKET.equals(event.getAvetConfig()));
        eventAvetConfigCouchDao.upsert(newEvent.getIdevento().toString(), avetConfig);

        attendantsConfigService.createAvetDefaultEventAttendantsConfig(newEvent.getIdevento().longValue());
        attendantFieldsService.createAvetDefaultAttendantFields(newEvent.getIdevento());
    }
    
    public void checkAndfillExternalData(EventDTO dto, EventConfig eventConfig) {
        if (EventType.AVET.equals(dto.getType())) {
            dto.setAvetConfig(EventAvetConfigType.WS);
            if (eventAvetConfigCouchDao.get(dto.getId().toString()) == null  || !BooleanUtils.isTrue(eventAvetConfigCouchDao.get(dto.getId().toString()).getIsSocket())) {
                dto.setAvetConfig(EventAvetConfigType.WS);
            } else if (Boolean.TRUE.equals(eventAvetConfigCouchDao.get(dto.getId().toString()).getIsSocket())) {
                dto.setAvetConfig(EventAvetConfigType.SOCKET);
            }

            if (dto.getExternalId() != null) {
                Competition competition = getCompetition(dto.getExternalId());
                if (competition == null) {
                    return;
                }

                Map<String, Object> externalData = new HashMap<>();
                externalData.put(ExternalDataConstants.EVENT_COMPETITION_ID, competition.getCode());
                externalData.put(ExternalDataConstants.EVENT_COMPETITION_NAME, competition.getDescription());
                dto.setExternalData(externalData);
            }
        }
        if (eventConfig != null) {
            dto.setInventoryProvider(eventConfig.getInventoryProvider());
        }
        if (eventConfig != null && Provider.SGA.equals(eventConfig.getInventoryProvider()) && !EventType.ACTIVITY.equals(dto.getType())) {
            try {
                ExternalEvent externalEvent = intDispatcherRepository.getExternalEvent(dto.getEntityId(), dto.getId());
                Map<String, Object> externalData = new HashMap<>();
                externalData.put(ExternalDataConstants.EVENT_COMPETITION_ID, externalEvent.getId());
                externalData.put(ExternalDataConstants.EVENT_COMPETITION_NAME, externalEvent.getName());
                dto.setExternalData(externalData);
            } catch (Exception e) {
                LOGGER.warn("Can not retrieve SGA external event info", e);
            }
        }
    }

    private Competition getCompetition(Long externalId) {
        try {
            return intAvetConfigRepository.getCompetition(externalId);
        }catch (OneboxRestException e) {
            processException(e, externalId);
            return null;
        }
    }
    
    private static void processException(OneboxRestException e, Long externalId) {
        LOGGER.warn("[EVENT SERVICE] Error getting competition for event with external id: {}", externalId);

        if( !e.getErrorCode().equals( MsEventErrorCode.COMPETITION_NOT_FOUND.getErrorCode() )) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        }
    }

}

package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.datasources.integration.dispatcher.dto.ConnectorRelation;
import es.onebox.event.datasources.integration.dispatcher.repository.ConnectorsRelationRepository;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.events.converter.ConnectorRelationConverter;
import es.onebox.event.events.dao.EventExternalBarcodeConfigCouchDao;
import es.onebox.event.events.dao.IntegrationConnectorDao;
import es.onebox.event.events.dto.ExternalBarcodeEventConfigDTO;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.IntegrationConnectorRecord;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class EventExternalBarcodeConfigService {

    private static final String INTEGRATION_CONFIG_QUERY_PREFIX = "int-ifema-connector";
    private static final Logger LOGGER = LoggerFactory.getLogger(EventExternalBarcodeConfigService.class);

    private final EventExternalBarcodeConfigCouchDao eventExternalBarcodeConfigCouchDao;
    private final EventExternalService eventExternalService;
    private final SessionDao sessionDao;
    private final ConnectorsRelationRepository connectorsRelationRepository;
    private final OrdersRepository ordersRepository;

    @Autowired
    public EventExternalBarcodeConfigService (EventExternalBarcodeConfigCouchDao eventExternalBarcodeConfigCouchDao,
                                              SessionDao sessionDao, IntegrationConnectorDao integrationConnectorDao,
                                              ConnectorsRelationRepository connectorsRelationRepository,
                                              OrdersRepository ordersRepository,
                                              EventExternalService eventExternalService) {
        this.eventExternalBarcodeConfigCouchDao = eventExternalBarcodeConfigCouchDao;
        this.sessionDao = sessionDao;
        this.connectorsRelationRepository = connectorsRelationRepository;
        this.ordersRepository = ordersRepository;
        this.eventExternalService = eventExternalService;
    }

    public ExternalBarcodeEventConfigDTO getExternalBarcodeEventConfig (Long eventId) {
        return eventExternalBarcodeConfigCouchDao.get(eventId.toString());
    }

    public void upsertExternalBarcodeEventConfig (Long eventId, ExternalBarcodeEventConfigDTO externalBarcodeEventConfigDTO) {
        ExternalBarcodeEventConfigDTO oldConfig = getExternalBarcodeEventConfig(eventId);

        validateActiveSessions(eventId, oldConfig, externalBarcodeEventConfigDTO);

        Long eventSales = ordersRepository.countByEventAndChannel(eventId, null);
        if (eventSales > 0) {
            throw new OneboxRestException(MsEventErrorCode.UPDATE_EVENT_EXTERNAL_BARCODE_WITH_SALES);
        }

        eventExternalBarcodeConfigCouchDao.upsert(eventId.toString(), externalBarcodeEventConfigDTO);

        LOGGER.info("Event {} external use barcode config updated to {}", eventId, externalBarcodeEventConfigDTO.getAllow());

        propagateExternalStatus(eventId, oldConfig, externalBarcodeEventConfigDTO);
    }

    private void validateActiveSessions(Long eventId, ExternalBarcodeEventConfigDTO  oldConfig, ExternalBarcodeEventConfigDTO newConfig) {
        if ((oldConfig == null || BooleanUtils.isNotTrue(oldConfig.getAllow())) && BooleanUtils.isTrue(newConfig.getAllow())) {
            SessionSearchFilter filter = new SessionSearchFilter();
            filter.setStatus(Collections.singletonList(SessionStatus.READY));
            filter.setEventId(Collections.singletonList(eventId));
            if (sessionDao.countByFilter(filter) > 0) {
                throw new OneboxRestException(MsEventErrorCode.UPDATE_EVENT_EXTERNAL_BARCODE_CONFIG_WITH_ACTIVE_SESSIONS);
            }
        }
    }

    private void propagateExternalStatus(Long eventId, ExternalBarcodeEventConfigDTO  oldConfig, ExternalBarcodeEventConfigDTO newConfig) {
        if ((oldConfig == null && BooleanUtils.isTrue(newConfig.getAllow())
                || (oldConfig != null && !oldConfig.getAllow().equals(newConfig.getAllow())))) {
            updateSessionsExternalStatus(eventId, newConfig.getAllow());

            if (BooleanUtils.isTrue(newConfig.getAllow())) {
                IntegrationConnectorRecord connector = eventExternalService.getConnectorByName(Provider.IFEMA.getConnector());
                if (connector != null) {
                    ConnectorRelation connectorRelation = ConnectorRelationConverter.toIntegration(eventId.intValue(), connector.getConnectorid());
                    connectorsRelationRepository.createConnectorsRelation(connectorRelation);
                }
            }
        }
    }

    private void updateSessionsExternalStatus(Long eventId, Boolean allowed) {
        List<Integer> sessions = sessionDao.findSessionsByEventId(eventId.intValue()).stream().map(SessionRecord::getIdsesion).collect(Collectors.toList());
        LOGGER.info("Sessions {} for event {} external status updated to {}",
                sessions, eventId, allowed);
        sessionDao.updateFieldBySessions(sessions, Tables.CPANEL_SESION.ISEXTERNAL, BooleanUtils.isTrue(allowed));
    }
}

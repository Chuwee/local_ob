package es.onebox.event.secondarymarket.service;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.enums.ChannelEventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.dao.ChannelEventDao;
import es.onebox.event.secondarymarket.converter.EventSecondaryMarketConverter;
import es.onebox.event.secondarymarket.dao.EventSecondaryMarketConfigCouchDao;
import es.onebox.event.secondarymarket.domain.EnabledChannel;
import es.onebox.event.secondarymarket.domain.EventSecondaryMarketConfig;
import es.onebox.event.secondarymarket.dto.CreateEventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.utils.SecondaryMarketUtils;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventSecondaryMarketConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventSecondaryMarketConfigService.class);

    private final EventSecondaryMarketConfigCouchDao eventSecondaryMarketConfigCouchDao;
    private final SecondaryMarketService secondaryMarketService;
    private final SessionDao sessionDao;
    private final ChannelEventDao channelEventDao;
    private final RefreshDataService refreshDataService;
    private final EventDao eventDao;

    @Autowired
    public EventSecondaryMarketConfigService(EventSecondaryMarketConfigCouchDao eventSecondaryMarketConfigCouchDao,
                                             SessionDao sessionDao, ChannelEventDao channelEventDao,
                                             RefreshDataService refreshDataService, SecondaryMarketService secondaryMarketService, EventDao eventDao) {
        this.eventSecondaryMarketConfigCouchDao = eventSecondaryMarketConfigCouchDao;
        this.secondaryMarketService = secondaryMarketService;
        this.sessionDao = sessionDao;
        this.channelEventDao = channelEventDao;
        this.refreshDataService = refreshDataService;
        this.eventDao = eventDao;
    }

    public void createEventSecondaryMarketConfig(Long eventId,
                                                 CreateEventSecondaryMarketConfigDTO createEventSecondaryMarketConfigDTO) {
        Integer entityId = secondaryMarketService.isAllowedByEventEntityOrThrow(eventId);
        List<EnabledChannel> enabledChannels = getEnabledChannels(eventId);
        EventSecondaryMarketConfigDTO currentConfig = getEventSecondaryMarketConfig(eventId);

        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        SecondaryMarketUtils.validateSecondaryMarketPricesAndSaleTypesConfig(
                currentConfig, createEventSecondaryMarketConfigDTO, EventType.byId(event.getTipoevento())
        );

        if (createEventSecondaryMarketConfigDTO.getCustomerLimits() != null && BooleanUtils.isTrue(createEventSecondaryMarketConfigDTO.getCustomerLimitsEnabled())) {
            secondaryMarketService.validateCustomerLimits(createEventSecondaryMarketConfigDTO.getCustomerLimits(), entityId);
        } else if (createEventSecondaryMarketConfigDTO.getCustomerLimits() == null && BooleanUtils.isTrue(createEventSecondaryMarketConfigDTO.getCustomerLimitsEnabled())) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_CUSTOMER_LIMITS);
        }
        EventSecondaryMarketConfig eventSecondaryMarketConfig =
                EventSecondaryMarketConverter.toEntity(createEventSecondaryMarketConfigDTO, enabledChannels, currentConfig);
        eventSecondaryMarketConfigCouchDao.upsert(String.valueOf(eventId), eventSecondaryMarketConfig);
        refreshDataService.refreshEvent(eventId, "createEventSecondaryMarketConfig");
    }

    public EventSecondaryMarketConfigDTO getEventSecondaryMarketConfig(Long eventId) {
        secondaryMarketService.isAllowedByEventEntityOrThrow(eventId);

        EventSecondaryMarketConfig eventSecondaryMarketConfig =
                eventSecondaryMarketConfigCouchDao.get(String.valueOf(eventId));

        return EventSecondaryMarketConverter.toDTO(eventSecondaryMarketConfig);
    }

    public EventSecondaryMarketConfigDTO getEventSecondaryMarketConfigSafely(Long eventId) {
        if (secondaryMarketService.isAllowedByEventEntity(eventId)) {
            EventSecondaryMarketConfig eventSecondaryMarketConfig =
                    eventSecondaryMarketConfigCouchDao.get(String.valueOf(eventId));

            if (eventSecondaryMarketConfig != null) {
                return EventSecondaryMarketConverter.toDTO(eventSecondaryMarketConfig);
            }
        }
        return null;
    }

    public void updateEventSecondaryMarketConfig(Long eventId,
                                                 CreateEventSecondaryMarketConfigDTO createEventSecondaryMarketConfigDTO) {
        Integer entityId = secondaryMarketService.isAllowedByEventEntityOrThrow(eventId);

        if (createEventSecondaryMarketConfigDTO.getCustomerLimits() != null && BooleanUtils.isTrue(createEventSecondaryMarketConfigDTO.getCustomerLimitsEnabled())) {
            secondaryMarketService.validateCustomerLimits(createEventSecondaryMarketConfigDTO.getCustomerLimits(), entityId);
        }

        CpanelEventoRecord event = eventDao.getById(eventId.intValue());
        EventSecondaryMarketConfigDTO currentConfig = getEventSecondaryMarketConfig(eventId);
        SecondaryMarketUtils.validateSecondaryMarketPricesAndSaleTypesConfig(
                currentConfig, createEventSecondaryMarketConfigDTO, EventType.byId(event.getTipoevento())
        );

        List<EnabledChannel> enabledChannels = getEnabledChannels(eventId);
        EventSecondaryMarketConfig eventSecondaryMarketConfig =
                EventSecondaryMarketConverter.toEntity(createEventSecondaryMarketConfigDTO, enabledChannels, currentConfig);
        eventSecondaryMarketConfigCouchDao.upsert(String.valueOf(eventId), eventSecondaryMarketConfig);
        refreshDataService.refreshEvent(eventId, "updateEventSecondaryMarketConfig");
    }

    public void deleteEventSecondaryMarketConfig(Long eventId) {
        String key = String.valueOf(eventId);

        try {
            List<EnabledChannel> enabledChannels = getEnabledChannels(eventId);

            if (CollectionUtils.isNotEmpty(enabledChannels)) {
                eventSecondaryMarketConfigCouchDao.remove(key);
            } else {
                EventSecondaryMarketConfig cloneConfig = new EventSecondaryMarketConfig();
                cloneConfig.setEnabledChannels(enabledChannels);
                eventSecondaryMarketConfigCouchDao.upsert(key, cloneConfig);
            }

            refreshDataService.refreshEvent(eventId, "deleteEventSecondaryMarketConfig");
        } catch (Exception e) {
            LOGGER.error("Problem removing couchbase key: " + eventId, e);
        }
    }


    public EventSecondaryMarketConfigDTO existsChannelIdForEvent(Long sessionId, Long channelId) {
        Long eventId = sessionDao.getEventId(sessionId);
        Optional<CpanelCanalEventoRecord> optionalEventoCanalRecord =
                channelEventDao.getChannelEvent(channelId.intValue(), eventId.intValue());
        if (optionalEventoCanalRecord.isPresent()) {
            CpanelCanalEventoRecord eventoCanalRecord = optionalEventoCanalRecord.get();
            if (!ChannelEventStatus.ACCEPTED.getId().equals(eventoCanalRecord.getEstadorelacion())) {
                throw OneboxRestException.builder(MsEventErrorCode.CHANNEL_EVENT_NOT_FOUND).
                        setMessage("ChannelEvent not found for event: " + eventId + " - channel: " + channelId).build();
            }
        }
        EventSecondaryMarketConfigDTO eventSecondaryMarketConfigDTO = getEventSecondaryMarketConfig(eventId);
        boolean exists = eventSecondaryMarketConfigDTO.getEnabledChannels().stream().anyMatch(ec -> ec.getId().equals(channelId));
        if (!exists) {
            throw OneboxRestException.builder(CoreErrorCode.NOT_FOUND).
                    setMessage("Secondary market config for event: " + eventId + "  not found").build();
        }
        return eventSecondaryMarketConfigDTO;
    }


    private List<EnabledChannel> getEnabledChannels(Long eventId) {
        String key = String.valueOf(eventId);

        if (!eventSecondaryMarketConfigCouchDao.exists(key)) {
            return null;
        }

        EventSecondaryMarketConfig secondaryMarketConfig = eventSecondaryMarketConfigCouchDao.get(key);
        List<EnabledChannel> enabledChannels = secondaryMarketConfig.getEnabledChannels();

        return CollectionUtils.isNotEmpty(enabledChannels) ? enabledChannels : null;
    }
}


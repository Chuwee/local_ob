package es.onebox.event.sessions.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.sessions.converter.SessionSaleConstraintConverter;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.dto.SessionSaleConstraintDTO;
import es.onebox.event.sessions.dto.UpdatePriceTypeLimitDTO;
import es.onebox.event.sessions.dto.UpdateSaleConstraintDTO;
import es.onebox.event.sessions.utils.PurchaseLimitValidator;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SessionSaleConstraintService {

    private final SessionDao sessionDao;
    private final PriceTypeConfigDao priceZoneConfigDao;
    private final SessionConfigCouchDao sessionConfigCouchDao;
    private final RefreshDataService refreshDataService;
    private final EventConfigCouchDao eventConfigCouchDao;
    private final EntitiesRepository entitiesRepository;

	public SessionSaleConstraintService(SessionDao sessionDao, PriceTypeConfigDao priceZoneConfigDao,
			SessionConfigCouchDao sessionConfigCouchDao, RefreshDataService refreshDataService,
			EventConfigCouchDao eventConfigCouchDao, EntitiesRepository entitiesRepository) {
		this.sessionDao = sessionDao;
		this.priceZoneConfigDao = priceZoneConfigDao;
		this.sessionConfigCouchDao = sessionConfigCouchDao;
		this.refreshDataService = refreshDataService;
		this.eventConfigCouchDao = eventConfigCouchDao;
		this.entitiesRepository = entitiesRepository;
	}

    @MySQLRead
    public SessionSaleConstraintDTO getSaleConstraints(Long sessionId) {
        SessionRecord session = getSession(sessionId);
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        return SessionSaleConstraintConverter.convert(sessionConfig, session.getNummaxlocalidadescompra(), session.getIdsesion());
    }

    @MySQLWrite
    public void upsertSaleConstraints(Long eventId, Long sessionId, UpdateSaleConstraintDTO request) {
        SessionRecord session = getSession(sessionId);
        EventConfig eventConfig = eventConfigCouchDao.getOrInitEventConfig(eventId);
        validatePurchaseLimit(request, eventConfig);
        SessionConfig sessionConfig = sessionConfigCouchDao.getOrInitSessionConfig(sessionId);
        boolean refreshSession = false;
        if (request.getCartLimitsEnabled() != null) {
            if (BooleanUtils.isTrue(request.getCartLimitsEnabled())) {
                validateCartLimit(request.getCartLimit());
                session.setNummaxlocalidadescompra(request.getCartLimit());
            } else {
                session.setNummaxlocalidadescompra(null);
                sessionConfig.setPriceTypeLimits(null);
            }
            sessionDao.update(session);
            refreshSession = true;
        }
        if (request.getCartPriceTypeLimitsEnabled() != null) {
            if (BooleanUtils.isTrue(request.getCartPriceTypeLimitsEnabled())) {
                Integer maxPerSession = calculateMaxPerSession(session.getNummaxlocalidadescompra(), request.getCartLimit());
                validateLimits(session.getVenueTemplateId(), maxPerSession, request.getCartPriceTypeLimits());
                sessionConfig.setPriceTypeLimits(SessionSaleConstraintConverter.convert(request.getCartPriceTypeLimits()));
            } else {
                sessionConfig.setPriceTypeLimits(null);
            }
            sessionConfigCouchDao.upsert(sessionId.toString(), sessionConfig);
            refreshSession = true;
        }
        if (request.getCustomersLimitsEnabled() != null) {
            if (BooleanUtils.isTrue(request.getCustomersLimitsEnabled())) {
                validateCustomersLimits(session.getVenueTemplateId(), request);
                sessionConfig.setCustomersLimits(SessionSaleConstraintConverter.convertCustomersLimits(request.getCustomersLimits()));
            } else {
                sessionConfig.setCustomersLimits(null);
            }
            sessionConfigCouchDao.upsert(sessionId.toString(), sessionConfig);
            refreshSession = true;
        }
        if (refreshSession) {
            refreshDataService.refreshSession(sessionId, "upsertSaleConstraints");
        }
    }

    private void validatePurchaseLimit(UpdateSaleConstraintDTO request, EventConfig eventConfig) {
        Optional.ofNullable(eventConfig.getInventoryProvider())
                .map(entitiesRepository::getExternalLoginConfig)
                .ifPresent(config -> PurchaseLimitValidator.validateConfig(config, request));
    }

    private Integer calculateMaxPerSession(Integer prevCartLimit, Integer newCartLimit) {
        if (prevCartLimit == null) {
            return newCartLimit;
        }
        if (newCartLimit == null) {
            return prevCartLimit;
        }
        return Math.max(prevCartLimit, newCartLimit);
    }

    private void validateCartLimit(Integer cartLimit) {
        if (cartLimit != null && cartLimit < 1) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_CART_LIMIT);
        }
    }

    private void validateLimits(Integer venueTemplateId, Integer maxPerSession, List<UpdatePriceTypeLimitDTO> limits) {
        if (limits.isEmpty()) {
            return;
        }

        Set<Integer> allowed = Optional.ofNullable(priceZoneConfigDao.findByVenueTemplateId(venueTemplateId))
                .orElse(new ArrayList<>())
                .stream()
                .map(CpanelZonaPreciosConfigRecord::getIdzona)
                .collect(Collectors.toSet());

        Set<Long> priceTypesUpdated = new HashSet<>();

        for (UpdatePriceTypeLimitDTO limit : limits) {
            validatePriceTypeLimits(allowed, priceTypesUpdated, limit);
            if (limit.getMin() == null || limit.getMin() < 0) {
                throw new OneboxRestException(MsEventSessionErrorCode.INVALID_MIN_PRICE_TYPE_LIMIT);
            }
            if (limit.getMax() < limit.getMin()) {
                throw new OneboxRestException(MsEventSessionErrorCode.MAX_SMALLER_THAN_MIN);
            }
            if (maxPerSession != null && limit.getMax() > maxPerSession) {
                throw new OneboxRestException(MsEventSessionErrorCode.MAX_GREATER_THAN_SESSION_CART_LIMIT);
            }
        }
    }

    private void validateCustomersLimits(Integer venueTemplateId, UpdateSaleConstraintDTO updateSaleConstraintDTO) {

        if (CollectionUtils.isEmpty(updateSaleConstraintDTO.getCustomersLimits().getPriceTypeLimits()) && updateSaleConstraintDTO.getCustomersLimits().getMin() == null && updateSaleConstraintDTO.getCustomersLimits().getMax() == null) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_SESSION_CLIENT_CONFIGURATION);
        }
        if (updateSaleConstraintDTO.getCustomersLimits().getMax() != null && updateSaleConstraintDTO.getCustomersLimits().getMax() < 0) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_MAX_PRICE_TYPE_LIMIT);
        }
        if (CollectionUtils.isEmpty(updateSaleConstraintDTO.getCustomersLimits().getPriceTypeLimits())) {
            return;
        }

        Set<Integer> allowed = Optional.ofNullable(priceZoneConfigDao.findByVenueTemplateId(venueTemplateId))
                .orElse(new ArrayList<>())
                .stream()
                .map(CpanelZonaPreciosConfigRecord::getIdzona)
                .collect(Collectors.toSet());

        Set<Long> priceTypesUpdated = new HashSet<>();

        for (UpdatePriceTypeLimitDTO limit : updateSaleConstraintDTO.getCustomersLimits().getPriceTypeLimits()) {
            validatePriceTypeLimits(allowed, priceTypesUpdated, limit);
        }
    }

    private void validatePriceTypeLimits(Set<Integer> allowed, Set<Long> priceTypesUpdated, UpdatePriceTypeLimitDTO limit) {
        if (limit == null || limit.getId() == null || limit.getId() < 1L) {
            throw new OneboxRestException(MsEventErrorCode.INVALID_PRICE_TYPE);
        }
        if (!priceTypesUpdated.add(limit.getId())) {
            throw new OneboxRestException(MsEventSessionErrorCode.DUPLICATED_PRICE_TYPE_LIMIT);
        }
        if (!allowed.contains(limit.getId().intValue())) {
            throw new OneboxRestException(MsEventSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION);
        }
        if (limit.getMax() == null || limit.getMax() < 0) {
            throw new OneboxRestException(MsEventSessionErrorCode.INVALID_MAX_PRICE_TYPE_LIMIT);
        }
    }

    private SessionRecord getSession(Long sessionId) {
        final SessionRecord session = sessionDao.findSession(sessionId);
        if (session == null) {
            throw OneboxRestException.builder(MsEventSessionErrorCode.SESSION_NOT_FOUND).
                    setMessage("Session: " + sessionId + " not found").build();
        }
        return session;
    }

    public List<SessionSaleConstraintDTO> getEventSaleConstraints(Long eventId) {
        List<SessionRecord> sessions = sessionDao.findActiveSessionsByEventId(eventId.intValue());
        List<Long> sessionIds = sessions.stream().map(s -> s.getIdsesion().longValue()).toList();
        Map<Integer, SessionConfig> configs = sessionConfigCouchDao.bulkGet(sessionIds).stream()
                .collect(Collectors.toMap(SessionConfig::getSessionId, Function.identity()));
        List<SessionSaleConstraintDTO> result = new ArrayList<>();
        if (!CommonUtils.isEmpty(sessions)) {
            for (SessionRecord s : sessions) {
                SessionConfig sc = configs.get(s.getIdsesion());
                if (sc == null || CommonUtils.isEmpty(sc.getPriceTypeLimits()) ||
                        s.getNummaxlocalidadescompra() == null) {
                    continue;
                }
                SessionSaleConstraintDTO sessionSaleConstraint = SessionSaleConstraintConverter
                        .convert(sc, s.getNummaxlocalidadescompra(), s.getIdsesion());
                result.add(sessionSaleConstraint);
            }
        }
        return result;
    }
}

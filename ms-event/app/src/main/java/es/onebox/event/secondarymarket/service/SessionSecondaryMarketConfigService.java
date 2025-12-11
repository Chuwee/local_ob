package es.onebox.event.secondarymarket.service;

import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.secondarymarket.converter.SessionSecondaryMarketConverter;
import es.onebox.event.secondarymarket.dao.SessionSecondaryMarketConfigCouchDao;
import es.onebox.event.secondarymarket.domain.SessionSecondaryMarketConfig;
import es.onebox.event.secondarymarket.dto.CreateSessionSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigDTO;
import es.onebox.event.secondarymarket.dto.SessionSecondaryMarketConfigExtended;
import es.onebox.event.secondarymarket.utils.SecondaryMarketUtils;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionSecondaryMarketConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionSecondaryMarketConfigService.class);

    private final SessionSecondaryMarketConfigCouchDao sessionSecondaryMarketConfigCouchDao;
    private final EventSecondaryMarketConfigService eventSecondaryMarketConfigService;
    private final SecondaryMarketService secondaryMarketService;
    private final RefreshDataService refreshDataService;
    private final SessionConfigCouchDao sessionConfigCouchDao;

    @Autowired
    public SessionSecondaryMarketConfigService(SessionSecondaryMarketConfigCouchDao sessionSecondaryMarketConfigCouchDao,
                                               EventSecondaryMarketConfigService eventSecondaryMarketConfigService,
                                               SecondaryMarketService secondaryMarketService,
                                               RefreshDataService refreshDataService,
                                               SessionConfigCouchDao sessionConfigCouchDao) {
        this.sessionSecondaryMarketConfigCouchDao = sessionSecondaryMarketConfigCouchDao;
        this.eventSecondaryMarketConfigService = eventSecondaryMarketConfigService;
        this.secondaryMarketService = secondaryMarketService;
        this.refreshDataService = refreshDataService;
        this.sessionConfigCouchDao = sessionConfigCouchDao;
    }


    private static SessionSecondaryMarketConfigExtended buildSessionSecondaryMarketConfigExtended(SessionSecondaryMarketConfig sessionSecondaryMarketConfig, SessionConfig sessionConfig) {
        if ((sessionConfig == null || sessionConfig.getSecondaryMarketDates() == null) && sessionSecondaryMarketConfig == null) {
            return null;
        }
        SessionSecondaryMarketConfigExtended secondaryMarket = new SessionSecondaryMarketConfigExtended();

        if (sessionSecondaryMarketConfig != null) {
            secondaryMarket.setEnabled(sessionSecondaryMarketConfig.getEnabled());
            secondaryMarket.setPrice(sessionSecondaryMarketConfig.getPrice());
            secondaryMarket.setCommission(sessionSecondaryMarketConfig.getCommission());
        }

        if (sessionConfig != null) {
            secondaryMarket.setDates(sessionConfig.getSecondaryMarketDates());
        }

        return secondaryMarket;
    }

    public SessionSecondaryMarketConfigDTO getSessionSecondaryMarketConfig(Long sessionId) {
        SessionRecord sessionRecord = secondaryMarketService.getSessionAndCheckSecondaryMarket(sessionId);

        SessionConfig sessionConfig = sessionConfigCouchDao.get(sessionId.toString());
        SessionSecondaryMarketConfig sessionSecondaryMarketConfig = sessionSecondaryMarketConfigCouchDao.get(sessionId.toString());

        if (sessionSecondaryMarketConfig != null) {
            return SessionSecondaryMarketConverter.toDTO(sessionSecondaryMarketConfig, null, sessionConfig);
        }

        EventSecondaryMarketConfigDTO eventSecondaryMarketConfig = eventSecondaryMarketConfigService.getEventSecondaryMarketConfig(sessionRecord.getIdevento().longValue());

        return SessionSecondaryMarketConverter.toDTO(null, eventSecondaryMarketConfig, sessionConfig);
    }

    public SessionSecondaryMarketConfigExtended getSessionSecondaryMarketConfigSafely(Integer entityId, Long sessionId) {
        return Boolean.TRUE.equals(secondaryMarketService.getAllowSecondaryMarket(entityId)) ? getSessionSecondaryMarketConfigExtended(sessionId) : null;
    }

    public SessionSecondaryMarketConfigDTO getSessionSecondaryMarketConfigDTOSafely(Integer entityId, Long sessionId) {
        return Boolean.TRUE.equals(getAllowSecondaryMarket(entityId)) ? getSessionSecondaryMarketConfig(sessionId) : null;
    }

    public void createSessionSecondaryMarketConfig(Long sessionId, CreateSessionSecondaryMarketConfigDTO createSessionSecondaryMarketConfigDTO) {
        secondaryMarketService.checkSessionAndSecondaryMarketEnabled(sessionId);


        SecondaryMarketUtils.validateSecondaryMarketPricesAndSaleTypesConfig(
                getSessionSecondaryMarketConfig(sessionId), createSessionSecondaryMarketConfigDTO, null);
        sessionSecondaryMarketConfigCouchDao.upsert(sessionId.toString(), SessionSecondaryMarketConverter.toEntity(createSessionSecondaryMarketConfigDTO));
        refreshDataService.refreshSession(sessionId, "createSessionSecondaryMarketConfig");
    }

    public void deleteSessionSecondaryMarketConfig(Long sessionId) {
        secondaryMarketService.checkSessionAndSecondaryMarketEnabled(sessionId);

        try {
            String key = String.valueOf(sessionId);
            if (sessionSecondaryMarketConfigCouchDao.exists(key)) {
                sessionSecondaryMarketConfigCouchDao.remove(key);
                refreshDataService.refreshSession(sessionId, "deleteSessionSecondaryMarketConfig");
            }
        } catch (Exception e) {
            LOGGER.error("Problem removing couchbase key: " + sessionId, e);
        }
    }

    public Boolean getAllowSecondaryMarket(Integer entityId) {
        return secondaryMarketService.getAllowSecondaryMarket(entityId);
    }

    private SessionSecondaryMarketConfigExtended getSessionSecondaryMarketConfigExtended(Long sessionId) {
        secondaryMarketService.checkSessionAndSecondaryMarketEnabled(sessionId);
        SessionSecondaryMarketConfig sessionSecondaryMarketConfig = sessionSecondaryMarketConfigCouchDao.get(sessionId.toString());
        SessionConfig sessionConfig = sessionConfigCouchDao.get(sessionId.toString());

        return buildSessionSecondaryMarketConfigExtended(sessionSecondaryMarketConfig, sessionConfig);
    }
}




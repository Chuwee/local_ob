package es.onebox.event.loyaltypoints.seasontickets.service;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.loyaltypoints.seasontickets.converter.SeasonTicketLoyaltyPointsConverter;
import es.onebox.event.loyaltypoints.seasontickets.domain.SessionLoyaltyPoints;
import es.onebox.event.loyaltypoints.seasontickets.dto.SeasonTicketLoyaltyPointsConfigDTO;
import es.onebox.event.loyaltypoints.seasontickets.domain.SeasonTicketLoyaltyPointsConfig;
import es.onebox.event.loyaltypoints.seasontickets.dao.SeasonTicketLoyaltyPointsCouchDao;
import es.onebox.event.seasontickets.dao.SessionElasticDao;
import es.onebox.event.seasontickets.dto.SeasonTicketDTO;
import es.onebox.event.seasontickets.dto.SessionAssignationStatusDTO;
import es.onebox.event.seasontickets.dto.SessionResultDTO;
import es.onebox.event.seasontickets.request.SeasonTicketSessionsSearchFilter;
import es.onebox.event.seasontickets.service.SeasonTicketService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class SeasonTicketLoyaltyPointsService {

    private final SeasonTicketService seasonTicketService;
    private final EntitiesRepository entitiesRepository;
    private final SeasonTicketLoyaltyPointsCouchDao seasonTicketLoyaltyPointsCouchDao;
    private final SessionElasticDao sessionElasticDao;

    @Autowired
    public SeasonTicketLoyaltyPointsService(SeasonTicketService seasonTicketService, EntitiesRepository entitiesRepository,
                                            SeasonTicketLoyaltyPointsCouchDao seasonTicketLoyaltyPointsCouchDao, SessionElasticDao sessionElasticDao) {
        this.seasonTicketService = seasonTicketService;
        this.entitiesRepository = entitiesRepository;
        this.seasonTicketLoyaltyPointsCouchDao = seasonTicketLoyaltyPointsCouchDao;
        this.sessionElasticDao = sessionElasticDao;
    }

    public SeasonTicketLoyaltyPointsConfigDTO getSeasonTicketLoyaltyPoints(Long seasonTicketId) {
        SeasonTicketLoyaltyPointsConfig config = getConfig(seasonTicketId);
        return SeasonTicketLoyaltyPointsConverter.toDTO(config);
    }

    public void updateSeasonTicketLoyaltyPoints(Long seasonTicketId, SeasonTicketLoyaltyPointsConfigDTO seasonTicketLoyaltyPointsConfigDTO) {
        SeasonTicketLoyaltyPointsConfig config = getConfig(seasonTicketId);
        SeasonTicketDTO seasonTicketDTO = seasonTicketService.getSeasonTicket(seasonTicketId);
        SeasonTicketSessionsSearchFilter filter = new SeasonTicketSessionsSearchFilter();
        filter.setAssignationStatus(SessionAssignationStatusDTO.ASSIGNED);
        SearchResponse<SessionData> sessions = sessionElasticDao.getSessions(filter, seasonTicketDTO);
        List<SessionResultDTO> seasonTicketCandidateSessions = sessionElasticDao.getSeasonTicketCandidateSessionsDTO(sessions);
        SeasonTicketLoyaltyPointsConverter.updateSeasonTicketLoyaltyPointsConfig(seasonTicketCandidateSessions, config, seasonTicketLoyaltyPointsConfigDTO);
        seasonTicketLoyaltyPointsCouchDao.upsert(seasonTicketId.toString(), config);
    }

    public void onSessionLink(Long seasonTicketId, Long sessionId) {
        if (checkLoyaltyPoints(seasonTicketId)) {
            SeasonTicketLoyaltyPointsConfig config = getConfig(seasonTicketId);
            List<SessionLoyaltyPoints> sessions = config.getSessions() == null ? new ArrayList<>() : config.getSessions();
            sessions.add(new SessionLoyaltyPoints(sessionId));
            config.setSessions(sessions);
            seasonTicketLoyaltyPointsCouchDao.upsert(seasonTicketId.toString(), config);
        }
    }

    public void onSessionUnlink(Long seasonTicketId, Long sessionId) {
        if (checkLoyaltyPoints(seasonTicketId)) {
            SeasonTicketLoyaltyPointsConfig config = getConfig(seasonTicketId);
            if (CollectionUtils.isNotEmpty(config.getSessions())) {
                config.setSessions(config.getSessions().stream().filter(session -> !sessionId.equals(session.getSessionId())).toList());
                seasonTicketLoyaltyPointsCouchDao.upsert(seasonTicketId.toString(), config);
            }
        }
    }

    private boolean checkLoyaltyPoints(Long seasonTicketId) {
        SeasonTicketDTO seasonTicket = seasonTicketService.getSeasonTicket(seasonTicketId);
        EntityDTO entity = entitiesRepository.getEntity(seasonTicket.getEntityId().intValue());
        return BooleanUtils.isTrue(entity.getAllowLoyaltyPoints());
    }

    private SeasonTicketLoyaltyPointsConfig getConfig(Long seasonTicketId) {
        SeasonTicketLoyaltyPointsConfig config = seasonTicketLoyaltyPointsCouchDao.get(seasonTicketId.toString());
        if (config == null) {
            config = new SeasonTicketLoyaltyPointsConfig();
        }
        return config;
    }
}
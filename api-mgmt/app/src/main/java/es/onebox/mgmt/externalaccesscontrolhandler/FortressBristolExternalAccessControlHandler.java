package es.onebox.mgmt.externalaccesscontrolhandler;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.AddProductEventRequestDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.HandlePackageEventRequestDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.dto.ProductResponseDTO;
import es.onebox.mgmt.datasources.ms.accesscontrol.repository.AccessControlSystemsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.sessions.dto.CloneSessionRequestDTO;
import es.onebox.mgmt.sessions.dto.CreateSessionRequestDTO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;

@Service("fortress")
public class FortressBristolExternalAccessControlHandler implements ExternalAccessControlHandler {
    private final AccessControlSystemsRepository accessControlSystemsRepository;

    protected static final Logger LOGGER = LoggerFactory.getLogger(FortressBristolExternalAccessControlHandler.class);


    public FortressBristolExternalAccessControlHandler(AccessControlSystemsRepository accessControlSystemsRepository) {
        this.accessControlSystemsRepository = accessControlSystemsRepository;
    }


    @Override
    public void createSessions(Event event, List<Long> sessionIds) {
        if (CollectionUtils.isEmpty(sessionIds)) {
            LOGGER.warn("[FORTRESS] No sessions to add to fortress for eventId: {}", event.getId());
            return;
        }
        AddProductEventRequestDTO addProductEventRequestDTO = new AddProductEventRequestDTO();
        addProductEventRequestDTO.setSessionIds(sessionIds);
        try {
            accessControlSystemsRepository.addFortressSession(event.getEntityId(), event.getId(), addProductEventRequestDTO);
        } catch (Exception e) {
            LOGGER.error("[FORTRESS] Error adding sessions to fortress for eventId: {} and sessions: {}. Error: {}", event.getId(), sessionIds, e.getMessage());
        }
    }

    @Override
    public void validateCreateSession(Event event, CreateSessionRequestDTO request) {
        if (event.getCustomCategory() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ACCESS_CONTROL_REQUIRES_CUSTOM_CATEGORY);
        }

        if (request.getDates() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ACCESS_CONTROL_REQUIRES_DATES);
        }

        if (request.getDates().getEndDate() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ACCESS_CONTROL_REQUIRES_END_DATE);
        }

    }

    @Override
    public void validateCloneSession(Event event, CloneSessionRequestDTO request) {
        if (event.getCustomCategory() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ACCESS_CONTROL_REQUIRES_CUSTOM_CATEGORY);
        }

        if (request.getStartDate() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ACCESS_CONTROL_REQUIRES_DATES);
        }

        if (request.getEndDate() == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ACCESS_CONTROL_REQUIRES_END_DATE);
        }
    }


    @Override
    public void addOrUpdateEventRate(Long entityId, Long eventId, Long rateId) {
        try {
            accessControlSystemsRepository.addOrUpdateFortressRate(entityId, eventId, rateId);
        } catch (Exception e) {
            LOGGER.error("[FORTRESS] Error adding or updating Fortress rate for entityId: {}, eventId: {}, rateId: {}. Error: {}", entityId, eventId, rateId, e.getMessage());
        }
    }

    @Override
    public void assignSessionToSeasonTicket(SeasonTicket seasonTicket, Long sessionId) {

        ProductResponseDTO ftSeasonTicket;

        try{
            ftSeasonTicket = accessControlSystemsRepository.getFortressSeasonTicket(seasonTicket.getEntityId(), seasonTicket.getId());
        } catch (Exception e) {
            LOGGER.error("[FORTRESS] Error retrieving season ticket from Fortress for seasonTicketId: {} and entityId: {}. Error: {}", seasonTicket.getId(), seasonTicket.getEntityId(), e.getMessage());
            return;
        }

        if (ftSeasonTicket == null) {
            createSeasonTicket(seasonTicket.getEntityId(), seasonTicket.getId());
        }

        HandlePackageEventRequestDTO request = new HandlePackageEventRequestDTO();
        request.setSessionIds(List.of(sessionId));


        LOGGER.info("[FORTRESS] Assigning session {} to Season Ticket {}", sessionId, seasonTicket.getId());
        try {
            accessControlSystemsRepository.assignFortressSessionToSeasonTicket(seasonTicket.getEntityId(), seasonTicket.getId(), request);
        } catch (Exception e) {
            LOGGER.error("[FORTRESS] Error assigning session {} to Season Ticket {}", sessionId, seasonTicket.getId());
        }
    }

    @Override
    public void unassignSessionFromSeasonTicket(SeasonTicket seasonTicket, Long sessionId) {

        ProductResponseDTO ftSeasonTicket;
        try {
            ftSeasonTicket = accessControlSystemsRepository.getFortressSeasonTicket(seasonTicket.getEntityId(), seasonTicket.getId());
        } catch (Exception e) {
            LOGGER.error("[FORTRESS] Error retrieving season ticket from Fortress for seasonTicketId: {} and entityId: {}. Error: {}", seasonTicket.getId(), seasonTicket.getEntityId(), e.getMessage());
            return;
        }
        if (ftSeasonTicket == null) {
           createSeasonTicket(seasonTicket.getEntityId(), seasonTicket.getId());
        }

        HandlePackageEventRequestDTO request = new HandlePackageEventRequestDTO();
        request.setSessionIds(List.of(sessionId));
        try {
            LOGGER.info("[FORTRESS] Unassigning session from season ticket in Fortress for seasonTicketId: {} and entityId: {}.", seasonTicket.getId(), seasonTicket.getEntityId());
            accessControlSystemsRepository.unassignFortressSessionFromSeasonTicket(seasonTicket.getEntityId(), seasonTicket.getId(), request);
        } catch (Exception e) {
            LOGGER.error("[FORTRESS] Error unassigning session from season ticket in Fortress for seasonTicketId: {} and entityId: {}. Error: {}", seasonTicket.getId(), seasonTicket.getEntityId(), e.getMessage());
        }
    }

    @Override
    public void createSeasonTicket(Long entityId, Long seasonTicketId) {
        LOGGER.info("[FORTRESS] Creating season ticket in Fortress for seasonTicketId: {} and entityId: {}", seasonTicketId, entityId);
        try {
            accessControlSystemsRepository.createFortressSeasonTicket(entityId, seasonTicketId);
        } catch (Exception e) {
            LOGGER.error("[FORTRESS] Error creating season ticket in Fortress for seasonTicketId: {} and entityId: {}", seasonTicketId, entityId);
        }
    }

    @Override
    public void validateCreateSeasonTicket(Integer customCategoryId, ZonedDateTime startDate, ZonedDateTime endDate) {
        if (customCategoryId == null){
            throw new OneboxRestException(ApiMgmtErrorCode.ACCESS_CONTROL_REQUIRES_CUSTOM_CATEGORY);
        }

        if (startDate == null || endDate == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.ACCESS_CONTROL_REQUIRES_DATES);
        }
    }




    @Override
    public void addOrUpdateVenueElements(Long entityId, Long venueTemplateId) {
        try {
            accessControlSystemsRepository.addOrUpdateFortressVenueTemplate(entityId, venueTemplateId);
        } catch (Exception e) {
            LOGGER.error("[FORTRESS] Error adding or updating Fortress venue elements for entityId: {}, venueTemplateId: {}. Error: {}", entityId, venueTemplateId, e.getMessage());
        }
    }
}

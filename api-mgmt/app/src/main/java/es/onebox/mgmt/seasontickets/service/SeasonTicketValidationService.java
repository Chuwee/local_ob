package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventChannel;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.repository.EventChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateScope;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateStatus;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketOperativeDTO;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class SeasonTicketValidationService {

    private final ValidationService validationService;
    private final SecurityManager securityManager;
    private final SeasonTicketRepository seasonTicketRepository;
    private final EventChannelsRepository eventChannelsRepository;

    @Autowired
    public SeasonTicketValidationService(ValidationService validationService, SecurityManager securityManager,
                                         SeasonTicketRepository seasonTicketRepository, EventChannelsRepository eventChannelsRepository) {
        this.validationService = validationService;
        this.securityManager = securityManager;
        this.seasonTicketRepository = seasonTicketRepository;
        this.eventChannelsRepository = eventChannelsRepository;
    }

    public boolean validateSeasonTicketVenueTemplate(VenueTemplate venueTemplate, Long venueConfigId) {
        if(Objects.isNull(venueTemplate) || Objects.isNull(venueConfigId)) {
            return false;
        }
        try {
            securityManager.checkEntityAccessible(venueTemplate.getEntityId());
        } catch (AccessDeniedException e) {
            return false;
        }

        return venueConfigId.equals(venueTemplate.getId()) &&
                VenueTemplateType.DEFAULT.equals(venueTemplate.getTemplateType()) &&
                VenueTemplateScope.CAPACITIES.equals(venueTemplate.getScope()) &&
                VenueTemplateStatus.ACTIVE.equals(venueTemplate.getStatus());
    }

    public void checkCategory(Integer categoryId) {
        this.validationService.checkCategory(categoryId);
    }

    public void checkCustomCategory(Long entityId, Long categoryId) {
        this.validationService.checkCustomCategory(entityId, categoryId);
    }

    public EventChannel getAndCheckSeasonTicketChannel(Long seasonTicketId, Long channelId) {
        validateIds(seasonTicketId, channelId);
        getAndCheckSeasonTicket(seasonTicketId);
        return getAndCheckSeasonTicketChannelRelationExists(seasonTicketId, channelId);
    }

    public EventChannel getAndCheckSeasonTicketChannelRelationExists(Long seasonTicketId, Long channelId) {
        try {
            return eventChannelsRepository.getEventChannel(seasonTicketId, channelId);
        } catch (OneboxRestException e) {
            if (e.getErrorCode().equals(ApiMgmtErrorCode.EVENT_CHANNEL_NOT_FOUND.getErrorCode())) {
                throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_CHANNEL_NOT_FOUND);
            }
            throw e;
        }
    }

    public void validateIds(Long seasonTicketId, Long channelId) {
        if (seasonTicketId == null || seasonTicketId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_SEASON_TICKET_ID);
        }
        if (channelId == null || channelId <= 0) {
            throw new OneboxRestException(ApiMgmtErrorCode.CHANNEL_ID_INVALID);
        }
    }

    public SeasonTicket getAndCheckSeasonTicket(Long seasonTicketId) {
        SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        if (seasonTicket == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_FOUND);
        }
        securityManager.checkEntityAccessible(seasonTicket.getEntityId());
        return seasonTicket;
    }
}

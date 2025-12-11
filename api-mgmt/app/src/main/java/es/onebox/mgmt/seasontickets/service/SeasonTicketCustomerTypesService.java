package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketStatus;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketCustomerTypesConverter;
import es.onebox.mgmt.seasontickets.dto.customertypes.SeasonTicketCustomerTypeDTO;
import es.onebox.mgmt.seasontickets.dto.customertypes.UpdateSeasonTicketCustomerTypesDTO;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.stereotype.Service;

import java.util.List;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.NOT_FOUND;

@Service
public class SeasonTicketCustomerTypesService {

    private final EventsRepository eventsRepository;
    private final SecurityManager securityManager;
    private final SeasonTicketRepository seasonTicketRepository;

    public SeasonTicketCustomerTypesService(EventsRepository eventsRepository, SecurityManager securityManager, SeasonTicketRepository seasonTicketRepository) {
        this.eventsRepository = eventsRepository;
        this.securityManager = securityManager;
        this.seasonTicketRepository = seasonTicketRepository;
    }

   public List<SeasonTicketCustomerTypeDTO> getSeasonTicketCustomerTypes(Integer seasonTicketId) {
       checkSeasonTicketAccessibility(Long.valueOf(seasonTicketId));
        return SeasonTicketCustomerTypesConverter.toDTO(eventsRepository.getEventCustomerTypes(Long.valueOf(seasonTicketId)));
    }

    public void updateSeasonTicketCustomerTypes(Integer seasonTicketId, UpdateSeasonTicketCustomerTypesDTO seasonTicketCustomerTypes) {
        checkSeasonTicketAccessibility(Long.valueOf(seasonTicketId));
        eventsRepository.putEventCustomerTypes(Long.valueOf(seasonTicketId), SeasonTicketCustomerTypesConverter.toMs(seasonTicketCustomerTypes));
    }

    private void checkSeasonTicketAccessibility(Long seasonTicketId) {
        if (seasonTicketId == null || seasonTicketId <= 0) {
            throw OneboxRestException.builder(BAD_REQUEST_PARAMETER).setMessage("Season Ticket id is mandatory").build();
        }
        SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        if (seasonTicket == null || SeasonTicketStatus.DELETED.equals(seasonTicket.getStatus())) {
            throw OneboxRestException.builder(NOT_FOUND)
                    .setMessage("no season ticket found with id: " + seasonTicketId)
                    .build();
        }
        securityManager.checkEntityAccessible(seasonTicket.getEntityId());
    }
}

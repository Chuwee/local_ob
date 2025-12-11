package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.event.dto.event.VenueTemplatePrice;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketStatus;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.events.dto.VenueTemplatePriceDTO;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketPricesConverter;
import es.onebox.mgmt.seasontickets.dto.UpdateSeasonTicketPriceRequestDTO;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.NOT_FOUND;
import static es.onebox.mgmt.exception.ApiMgmtErrorCode.VENUE_TEMPLATE_PRICE_EVENT_STATUS;

@Service
public class SeasonTicketPricesService {

    private SeasonTicketRepository seasonTicketRepository;
    private SecurityManager securityManager;

    @Autowired
    public SeasonTicketPricesService(SeasonTicketRepository seasonTicketRepository, SecurityManager securityManager) {
        this.seasonTicketRepository = seasonTicketRepository;
        this.securityManager = securityManager;
    }

    public List<VenueTemplatePriceDTO> getPrices(Long seasonTicketId) {
        checkSeasonTicket(seasonTicketId);

        List<VenueTemplatePrice> venueTemplatePrices = seasonTicketRepository.getPrices(seasonTicketId);
        return SeasonTicketPricesConverter.fromMsVenueTemplatePrices(venueTemplatePrices);
    }

    public void updatePrices(Long seasonTicketId, List<UpdateSeasonTicketPriceRequestDTO> prices) {
        SeasonTicket seasonTicket = checkSeasonTicket(seasonTicketId);

        if (Objects.isNull(seasonTicket.getStatus()) || (seasonTicket.getStatus() != SeasonTicketStatus.SET_UP && seasonTicket.getStatus() != SeasonTicketStatus.PENDING_PUBLICATION)) {
            throw new OneboxRestException(VENUE_TEMPLATE_PRICE_EVENT_STATUS, "Prices can be updated only with season status SET_UP or PENDING_PUBLICATION", null);
        }

        seasonTicketRepository.updatePrices(seasonTicketId, SeasonTicketPricesConverter.toMsVenue(prices));
    }

    private SeasonTicket checkSeasonTicket(Long seasonTicketId) {
        SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        validateSeasonTicketAccessibility(seasonTicket, seasonTicketId);
        return seasonTicket;
    }

    private void validateSeasonTicketAccessibility(SeasonTicket seasonTicket, Long seasonTicketId) {
        if (seasonTicket == null) {
            throw new OneboxRestException(NOT_FOUND, "no season ticket found with id: " + seasonTicketId, null);
        }
        securityManager.checkEntityAccessible(seasonTicket.getEntityId());
    }
}

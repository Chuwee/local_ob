package es.onebox.mgmt.seasontickets.service;

import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketRedemption;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.UpdateSeasonTicketRedemption;
import es.onebox.mgmt.seasontickets.dto.redemption.UpdateSeasonTicketRedemptionDTO;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketRedemptionConverter;
import es.onebox.mgmt.seasontickets.dto.redemption.SeasonTicketRedemptionConfigDTO;
import org.springframework.stereotype.Service;

@Service
public class SeasonTicketRedemptionService {

    private final SeasonTicketRepository seasonTicketRepository;
    private final SeasonTicketService seasonTicketService;

    public SeasonTicketRedemptionService(SeasonTicketRepository seasonTicketRepository, SeasonTicketService seasonTicketService) {
        this.seasonTicketRepository = seasonTicketRepository;
        this.seasonTicketService = seasonTicketService;
    }

    public SeasonTicketRedemptionConfigDTO getSeasonTicketRedemption(Long seasonTicketId) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        SeasonTicketRedemption ms = seasonTicketRepository.getSeasonTicketRedemption(seasonTicketId);
        return SeasonTicketRedemptionConverter.fromMs(ms);
    }

    public void updateSeasonTicketRedemption(Long seasonTicketId, UpdateSeasonTicketRedemptionDTO request) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        UpdateSeasonTicketRedemption req = SeasonTicketRedemptionConverter.toMs(request);
        seasonTicketRepository.updateSeasonTicketRedemption(seasonTicketId, req);
    }
}

package es.onebox.mgmt.loyaltypoints.seasontickets.service;

import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketLoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.loyaltypoints.seasontickets.converter.SeasonTicketLoyaltyPointsConverter;
import es.onebox.mgmt.loyaltypoints.seasontickets.dto.SeasonTicketLoyaltyPointsConfigDTO;
import es.onebox.mgmt.seasontickets.service.SeasonTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class SeasonTicketLoyaltyPointsService {

    private final SeasonTicketService seasonTicketService;
    private final SeasonTicketRepository seasonTicketRepository;

    @Autowired
    public SeasonTicketLoyaltyPointsService(@Lazy SeasonTicketService seasonTicketService,
                                            SeasonTicketRepository seasonTicketRepository) {
        this.seasonTicketService = seasonTicketService;
        this.seasonTicketRepository = seasonTicketRepository;
    }

    public SeasonTicketLoyaltyPointsConfigDTO getSeasonTicketLoyaltyPoints(Long seasonTicketId) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        SeasonTicketLoyaltyPointsConfig seasonTicketLoyaltyPoints = seasonTicketRepository.getSeasonTicketLoyaltyPoints(seasonTicketId);
        return SeasonTicketLoyaltyPointsConverter.toDTO(seasonTicketLoyaltyPoints);
    }

    public void updateSeasonTicketLoyaltyPoints(Long seasonTicketId, SeasonTicketLoyaltyPointsConfigDTO updateSessionPointsDTOs) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        SeasonTicketLoyaltyPointsConfig updatedPoints = SeasonTicketLoyaltyPointsConverter.toMs(updateSessionPointsDTOs);
        seasonTicketRepository.updateSeasonTicketLoyaltyPoints(seasonTicketId, updatedPoints);
    }
}
package es.onebox.mgmt.secondarymarket.service;

import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.SeasonTicketSecondaryMarketConfig;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.secondarymarket.converter.SecondaryMarketConverter;
import es.onebox.mgmt.secondarymarket.dto.SeasonTicketSecondaryMarketConfigDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeasonTicketsSecondaryMarketService {

    private final EventsRepository eventsRepository;
    private final ValidationService validationService;

    @Autowired
    public SeasonTicketsSecondaryMarketService(EventsRepository eventsRepository, ValidationService validationService) {
        this.eventsRepository = eventsRepository;
        this.validationService = validationService;
    }

    public SeasonTicketSecondaryMarketConfigDTO getSeasonTicketSecondaryMarketConfig(Long id) {
        validationService.getAndCheckSeasonTicket(id);
        SeasonTicketSecondaryMarketConfig seasonTicketSecondaryMarketConfig = eventsRepository.getSeasonTicketSecondaryMarketConfig(id);
        return SecondaryMarketConverter.toDTO(seasonTicketSecondaryMarketConfig);

    }

    public void createSeasonTicketSecondaryMarketConfig(Long eventId, SeasonTicketSecondaryMarketConfigDTO seasonTicketSecondaryMarketConfig) {
        validationService.validateNumberOfSessions(seasonTicketSecondaryMarketConfig);
        validationService.getAndCheckSeasonTicket(eventId);
        eventsRepository.createSeasonTicketSecondaryMarketConfig(eventId, SecondaryMarketConverter.toMs(seasonTicketSecondaryMarketConfig));
    }
}

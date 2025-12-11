package es.onebox.mgmt.secondarymarket.service;

import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.EventSecondaryMarketConfig;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.secondarymarket.converter.SecondaryMarketConverter;
import es.onebox.mgmt.secondarymarket.dto.SecondaryMarketConfigDTO;
import es.onebox.mgmt.secondarymarket.dto.SecondaryMarketType;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventsSecondaryMarketService {

    private final EventsRepository eventsRepository;
    private final ValidationService validationService;

    @Autowired
    public EventsSecondaryMarketService(EventsRepository eventsRepository, ValidationService validationService) {
        this.eventsRepository = eventsRepository;
        this.validationService = validationService;
    }

    public SecondaryMarketConfigDTO getEventSecondaryMarketConfig(Long id) {
        validationService.getAndCheckEvent(id);
        EventSecondaryMarketConfig eventSecondaryMarketConfig = eventsRepository.getEventSecondaryMarketConfig(id);
        return SecondaryMarketConverter.toDTO(eventSecondaryMarketConfig, SecondaryMarketType.EVENT);

    }

    public void createEventSecondaryMarketConfig(Long eventId, SecondaryMarketConfigDTO secondaryMarketConfig) {
        validationService.validatePriceType(secondaryMarketConfig.getPrice().getType());
        validationService.getAndCheckEvent(eventId);
        eventsRepository.createEventSecondaryMarketConfig(eventId, SecondaryMarketConverter.toMs(secondaryMarketConfig, EventSecondaryMarketConfig.class));
    }
}

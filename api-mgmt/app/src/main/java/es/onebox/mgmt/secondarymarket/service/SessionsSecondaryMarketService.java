package es.onebox.mgmt.secondarymarket.service;

import es.onebox.mgmt.datasources.ms.event.dto.secondarymarket.SessionSecondaryMarketConfig;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.secondarymarket.converter.SecondaryMarketConverter;
import es.onebox.mgmt.secondarymarket.dto.SecondaryMarketConfigDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionsSecondaryMarketService {

    private final EventsRepository eventsRepository;
    private final ValidationService validationService;

    @Autowired
    public SessionsSecondaryMarketService(EventsRepository eventsRepository, ValidationService validationService) {
        this.eventsRepository = eventsRepository;
        this.validationService = validationService;
    }

    public SecondaryMarketConfigDTO getSessionSecondaryMarketConfig(Long id) {
        validationService.getAndCheckVisibilitySession(id);
        return SecondaryMarketConverter.toDTO(eventsRepository.getSessionSecondaryMarketConfig(id));

    }

    public void createSessionSecondaryMarketConfig(Long eventId, SecondaryMarketConfigDTO secondaryMarketConfig) {
        validationService.validatePriceType(secondaryMarketConfig.getPrice().getType());
        validationService.getAndCheckVisibilitySession(eventId);
        eventsRepository.createSessionSecondaryMarketConfig(eventId, SecondaryMarketConverter.toMs(secondaryMarketConfig, SessionSecondaryMarketConfig.class));
    }

    public void deleteSessionSecondaryMarketConfig(Long id) {
        validationService.getAndCheckVisibilitySession(id);
        eventsRepository.deleteSessionSecondaryMarketConfig(id);
    }
}

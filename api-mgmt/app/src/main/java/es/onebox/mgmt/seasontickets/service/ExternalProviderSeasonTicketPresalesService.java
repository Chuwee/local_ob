package es.onebox.mgmt.seasontickets.service;

import es.onebox.mgmt.datasources.integration.dispatcher.dto.ExternalPresaleBase;
import es.onebox.mgmt.datasources.integration.dispatcher.repository.DispatcherRepository;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.events.dto.ExternalPresaleBaseDTO;
import es.onebox.mgmt.sessions.converters.ExternalPresaleConverter;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExternalProviderSeasonTicketPresalesService {

    private final DispatcherRepository dispatcherRepository;
    private final ValidationService validationService;

    public ExternalProviderSeasonTicketPresalesService(DispatcherRepository dispatcherRepository, ValidationService validationService) {
        this.validationService = validationService;
        this.dispatcherRepository = dispatcherRepository;
    }

    public List<ExternalPresaleBaseDTO> getAllExternalPrivatePresales(Long seasonTicketId, boolean skipUsed) {
        SeasonTicket seasonTicket = validationService.getAndCheckSeasonTicket(seasonTicketId);
        Long entityId = seasonTicket.getEntityId();

        List<ExternalPresaleBase> externalPresales = dispatcherRepository.getExternalSeasonTicketPresales(entityId, seasonTicketId, skipUsed);

        return ExternalPresaleConverter.fromIntDispatcher(externalPresales);
    }
}

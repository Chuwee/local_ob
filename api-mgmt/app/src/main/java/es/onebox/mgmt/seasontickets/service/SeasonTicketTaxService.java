package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityTax;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketTax;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.seasontickets.dto.tax.SeasonTicketTaxDTO;
import org.springframework.stereotype.Service;

import java.util.List;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;

@Service
public class SeasonTicketTaxService {

    private final SessionsRepository sessionsRepository;
    private final EntitiesRepository entitiesRepository;
    private final SeasonTicketService seasonTicketService;

    public SeasonTicketTaxService(SessionsRepository sessionsRepository,
                                  EntitiesRepository entitiesRepository, SeasonTicketService seasonTicketService) {
        this.sessionsRepository = sessionsRepository;
        this.entitiesRepository = entitiesRepository;
        this.seasonTicketService = seasonTicketService;
    }

    public SeasonTicketTaxDTO getSeasonTicketTaxes(Long seasonTicketId) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        Session session = sessionsRepository.getSession(seasonTicket.getSessionId());
        
        SeasonTicketTaxDTO dto = new SeasonTicketTaxDTO();
        dto.setTaxId(session.getTicketTax().getId());
        dto.setChargesTaxId(session.getChargesTax().getId());
        return dto;
    }

    public void updateSeasonTicketTaxes(Long seasonTicketId, SeasonTicketTaxDTO updateTaxes) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        validateTaxes(seasonTicket.getEntityId(), updateTaxes.getTaxId(), updateTaxes.getChargesTaxId());

        SeasonTicketTax taxes = new SeasonTicketTax();
        taxes.setTaxId(updateTaxes.getTaxId());
        taxes.setChargesTaxId(updateTaxes.getChargesTaxId());
        
        sessionsRepository.updateSessionTaxes(seasonTicket.getSessionId(), taxes);
    }

    private void validateTaxes(Long entityId, Long taxId, Long chargesTaxId) {
        List<EntityTax> entityTaxes = entitiesRepository.getTaxes(entityId);

        boolean taxIdValid = entityTaxes.stream()
                .anyMatch(tax -> tax.getIdImpuesto().equals(taxId.intValue()));

        if (!taxIdValid) {
            throw OneboxRestException.builder(BAD_REQUEST_PARAMETER)
                    .setMessage("Invalid tax_id")
                    .build();
        }

        boolean chargesTaxIdValid = entityTaxes.stream()
                .anyMatch(tax -> tax.getIdImpuesto().equals(chargesTaxId.intValue()));

        if (!chargesTaxIdValid) {
            throw OneboxRestException.builder(BAD_REQUEST_PARAMETER)
                    .setMessage("Invalid charges_tax_id")
                    .build();
        }
    }
}


package es.onebox.mgmt.forms.service;

import es.onebox.mgmt.datasources.ms.entity.dto.Form;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypes;
import es.onebox.mgmt.datasources.ms.entity.repository.CustomerTypesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.forms.converter.FormsConverter;
import es.onebox.mgmt.forms.dto.FormFieldDTO;
import es.onebox.mgmt.forms.dto.UpdateFormDTO;
import es.onebox.mgmt.forms.enums.SeasonTicketFormType;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeasonTicketFormsService {

    private final SeasonTicketRepository seasonTicketRepository;
    private final CustomerTypesRepository customerTypesRepository;
    private final SecurityManager securityManager;

    @Autowired
    public SeasonTicketFormsService(SeasonTicketRepository seasonTicketRepository, CustomerTypesRepository customerTypesRepository, SecurityManager securityManager) {
        this.seasonTicketRepository = seasonTicketRepository;
        this.customerTypesRepository = customerTypesRepository;
        this.securityManager = securityManager;
    }

    public List<List<FormFieldDTO>> getSeasonTicketForm(Long seasonTicketId, SeasonTicketFormType type) {
        SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        securityManager.checkEntityAccessible(seasonTicket.getEntityId());
        
        String formTypeString = type.name();
        Form form = seasonTicketRepository.getSeasonTicketForm(seasonTicketId, formTypeString);
        CustomerTypes customerTypes = customerTypesRepository.getCustomerTypes(seasonTicket.getEntityId());
        return FormsConverter.toDTO(form, customerTypes);
    }

    public void updateSeasonTicketForm(Long seasonTicketId, UpdateFormDTO updateFormDTO, SeasonTicketFormType type) {
        SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        securityManager.checkEntityAccessible(seasonTicket.getEntityId());
        
        Form updateForm = FormsConverter.toMs(updateFormDTO);
        String formTypeString = type.name();
        seasonTicketRepository.updateSeasonTicketForm(seasonTicketId, formTypeString, updateForm);
    }

}

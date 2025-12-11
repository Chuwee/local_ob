package es.onebox.mgmt.forms.service;

import es.onebox.mgmt.datasources.ms.entity.dto.Form;
import es.onebox.mgmt.datasources.ms.entity.dto.customertypes.CustomerTypes;
import es.onebox.mgmt.datasources.ms.entity.repository.CustomerTypesRepository;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.forms.converter.FormsConverter;
import es.onebox.mgmt.forms.dto.FormFieldDTO;
import es.onebox.mgmt.forms.dto.UpdateFormDTO;
import es.onebox.mgmt.forms.enums.EntityFormType;
import es.onebox.mgmt.security.SecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntityFormsService {

    private final EntitiesRepository entitiesRepository;
    private final CustomerTypesRepository customerTypesRepository;

    private final SecurityManager securityManager;

    @Autowired
    public EntityFormsService(EntitiesRepository entitiesRepository, CustomerTypesRepository customerTypesRepository, SecurityManager securityManager) {
        this.entitiesRepository = entitiesRepository;
        this.customerTypesRepository = customerTypesRepository;
        this.securityManager = securityManager;
    }

    public List<List<FormFieldDTO>> getEntityForm(Long entityId, EntityFormType type) {
        securityManager.checkEntityAccessible(entityId);
        String formTypeString = type.name();
        Form form = entitiesRepository.getForm(entityId, formTypeString);
        CustomerTypes customerTypes = customerTypesRepository.getCustomerTypes(entityId);
        return FormsConverter.toDTO(form, customerTypes);
    }

    public void updateEntityForm(Long entityId, UpdateFormDTO updateFormDTO, EntityFormType type) {
        securityManager.checkEntityAccessible(entityId);
        Form updateForm = FormsConverter.toMs(updateFormDTO);
        String formTypeString = type.name();
        entitiesRepository.updateForm(entityId, updateForm, formTypeString);
    }

}
